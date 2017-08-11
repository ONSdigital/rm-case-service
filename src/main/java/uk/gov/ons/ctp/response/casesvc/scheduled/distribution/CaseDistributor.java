package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.LockingException;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the 'service' class that distributes cases to the action service. It has a number of injected beans,
 * including a RestClient, Repositories and the InstructionPublisher.
 *
 * This class is scheduled to wake and looks for Cases in SAMPLED_INIT & REPLACEMENT_INIT states to send to the action
 * service. On each wake cycle, it fetches the first n cases, by createddatetime. It then fetches n IACs from the IAC
 * service and loops through those n cases to update each case with an IAC taken from the set of n codes and transitions
 * the case state to ACTIVE. It takes each case and constructs a notification message to send to the action service -
 * when it has x notifications it publishes them.
 *
 */
@CoverageIgnore
@Component
@Slf4j
public class CaseDistributor {

  private static final String CASE_DISTRIBUTOR_LIST_ID = "case";

  // this is a bit of a kludge - jpa does not like having an IN clause with an empty list
  // it does not return results when you expect it to - so ... always have this in the list of excluded case ids
  private static final int IMPOSSIBLE_CASE_ID = Integer.MAX_VALUE;

  private static final long MILLISECONDS = 1000L;

  @Autowired
  private DistributedListManager<Integer> caseDistributionListManager;

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @Autowired
  private CaseNotificationPublisher notificationPublisher;

  @Autowired
  private CaseRepository caseRepo;

  @Autowired
  private CaseService caseService;

  @Autowired
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  /**
   * wake up on schedule and check for cases that are in INIT state - fetch IACs
   * for them, adding the IAC to the case questionnaire, and send a notificaiton
   * of the activation to the action service
   *
   * @return the info for the health endpoint regarding the distribution just
   *         performed
   */
  public final CaseDistributionInfo distribute() {
    log.debug("CaseDistributor awoken...");

    CaseDistributionInfo distInfo = new CaseDistributionInfo();
    int successes = 0;
    int failures = 0;

    try {
      List<Case> cases = retrieveCases();

      if (!CollectionUtils.isEmpty(cases)) {
        int nbRetrievedCases = cases.size();

        try {
          List<String> codes = internetAccessCodeSvcClientService.generateIACs(nbRetrievedCases);

          if (!CollectionUtils.isEmpty(codes)) {
            if (nbRetrievedCases == codes.size()) {
              for (int idx = 0; idx < nbRetrievedCases; idx++) {
                Case caze = cases.get(idx);
                try {
                  processCase(caze, codes.get(idx));
                  successes++;
                } catch (Exception e) {
                  log.error("Exception msg {} thrown processing case with id {}. Processing postponed", e.getMessage(),
                          caze.getId());
                  failures++;
                }
              }
            }
          }

          distInfo.setCasesSucceeded(successes);
          distInfo.setCasesFailed(failures);
        } catch (Exception e) {
          log.error("Failed to obtain IAC codes");
        }
      }
    } catch (Exception e) {
      log.error("Failed to process cases because {}", e.getMessage());
    } finally {
      try {
        caseDistributionListManager.deleteList(CASE_DISTRIBUTOR_LIST_ID, true);
      } catch (LockingException e) {
        log.error("Failed to release caseDistributionListManager data - error msg is {}", e.getMessage());
      }
    }

    log.debug("CaseDistributor sleeping");
    return distInfo;
  }

  /**
   * Get the oldest page of SAMPLED_INIT & REPLACEMENT_INIT cases to activate - but do not retrieve the
   * same cases as other CaseSvc' in the cluster
   *
   * @throws LockingException locking exception thrown when caseDistributionListManager does
   *
   * @return list of cases
   */
  private List<Case> retrieveCases() throws LockingException {
    List<Case> cases;

    List<Integer> excludedCases = caseDistributionListManager.findList(CASE_DISTRIBUTOR_LIST_ID, false);
    log.debug("retrieve cases excluding {}", excludedCases);

    // prepare and execute the query to find the oldest N cases that are in SAMPLED_INIT & REPLACEMENT_INIT states and
    // not in the excluded list
    Pageable pageable = new PageRequest(0, appConfig.getCaseDistribution().getRetrievalMax(),
            new Sort(new Sort.Order(Direction.ASC, "createdDateTime")));
    excludedCases.add(Integer.valueOf(IMPOSSIBLE_CASE_ID));
    cases = caseRepo.findByStateInAndCasePKNotIn(Arrays.asList(CaseState.SAMPLED_INIT, CaseState.REPLACEMENT_INIT),
            excludedCases, pageable);

    if (!CollectionUtils.isEmpty(cases)) {
      log.debug("RETRIEVED case ids {}", cases.stream().map(caze -> caze.getId().toString()).collect(
              Collectors.joining(",")));
      caseDistributionListManager.saveList(CASE_DISTRIBUTOR_LIST_ID, cases.
              stream().map(caze -> caze.getCasePK()).collect(Collectors.toList()), true);
    } else {
      log.debug("RETRIEVED 0 case id");
      caseDistributionListManager.unlockContainer();
    }

    return cases;
  }

  /**
   * Deal with a single case - the transaction boundary is here.
   *
   * The processing requires to write to our own case table. A CaseNotification is also produced and added to the
   * outbound CaseNotifications sent to the action service.
   *
   * @param caze the case to deal with
   * @param iac the IAC to assign to the Case
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = Exception.class)
  private void processCase(final Case caze, final String iac) throws CTPException {
    log.info("processing caseid {}", caze.getId());

    CaseDTO.CaseEvent event = null;
    switch (caze.getState()) {
      case SAMPLED_INIT:
        event = CaseDTO.CaseEvent.ACTIVATED;
        break;
      case REPLACEMENT_INIT:
        event = CaseDTO.CaseEvent.REPLACED;
        break;
    }

    Case updatedCase = transitionCase(caze, event);
    updatedCase.setIac(iac);
    caseRepo.saveAndFlush(updatedCase);

    CaseNotification caseNotification = caseService.prepareCaseNotification(caze, event);
    log.debug("Publishing caseNotification...");
    notificationPublisher.sendNotification(caseNotification);
  }

  /**
   * Change the case status in db to indicate we have sent this case downstream,
   * and clear previous situation (in the scenario where the case has prev.
   * failed)
   *
   * @param caze the case to change and persist
   * @param event the event to transition the case with
   * @return the transitioned case
   * @throws CTPException when case state transition error
   */
  private Case transitionCase(final Case caze, final CaseDTO.CaseEvent event) throws CTPException {
    CaseDTO.CaseState nextState = caseSvcStateTransitionManager.transition(caze.getState(), event);
    caze.setState(nextState);
    return caze;
  }
}
