package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.sourceforge.cobertura.CoverageIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.LockingException;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.EventPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * This is the 'service' class that distributes cases to the action service. It has a number of
 * injected beans, including a RestClient, Repositories and the InstructionPublisher.
 *
 * <p>This class is scheduled to wake and looks for Cases in SAMPLED_INIT & REPLACEMENT_INIT states
 * to send to the action service. On each wake cycle, it fetches the first n cases, by
 * createddatetime. It then fetches n IACs from the IAC service and loops through those n cases to
 * update each case with an IAC taken from the set of n codes and transitions the case state to
 * ACTIVE. It takes each case and constructs a notification message to send to the action service -
 * when it has x notifications it publishes them.
 */
@CoverageIgnore
@Component
public class CaseDistributor {
  private static final Logger log = LoggerFactory.getLogger(CaseDistributor.class);

  private static final String CASE_DISTRIBUTOR_LIST_ID = "case";

  // this is a bit of a kludge - jpa does not like having an IN clause with an empty list
  // it does not return results when you expect it to - so ... always have this in the list of
  // excluded case ids
  private static final int IMPOSSIBLE_CASE_ID = Integer.MAX_VALUE;

  private AppConfig appConfig;
  private CaseRepository caseRepo;
  private CaseService caseService;
  private InternetAccessCodeSvcClient internetAccessCodeSvcClient;
  private DistributedListManager<Integer> caseDistributionListManager;
  private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;
  private CaseNotificationPublisher notificationPublisher;
  private EventPublisher eventPublisher;

  /** Constructor for CaseDistributor */
  @Autowired
  public CaseDistributor(
      final AppConfig appConfig,
      final DistributedListManager<Integer> caseDistributionListManager,
      final CaseRepository caseRepo,
      final CaseService caseService,
      final InternetAccessCodeSvcClient internetAccessCodeSvcClient,
      final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager,
      final CaseNotificationPublisher notificationPublisher,
      final EventPublisher eventPublisher) {
    this.appConfig = appConfig;
    this.caseRepo = caseRepo;
    this.caseService = caseService;
    this.internetAccessCodeSvcClient = internetAccessCodeSvcClient;
    this.caseDistributionListManager = caseDistributionListManager;
    this.caseSvcStateTransitionManager = caseSvcStateTransitionManager;
    this.notificationPublisher = notificationPublisher;
    this.eventPublisher = eventPublisher;
  }

  /**
   * wake up on schedule and check for cases that are in INIT state - fetch IACs for them, adding
   * the IAC to the case questionnaire, and send a notificaiton of the activation to the action
   * service
   *
   * @return the info for the health endpoint regarding the distribution just performed
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
          List<String> codes = internetAccessCodeSvcClient.generateIACs(nbRetrievedCases);

          if (!CollectionUtils.isEmpty(codes)) {
            if (nbRetrievedCases == codes.size()) {
              for (int idx = 0; idx < nbRetrievedCases; idx++) {
                Case caze = cases.get(idx);
                try {
                  processCase(caze, codes.get(idx));
                  successes++;
                } catch (Exception e) {
                  log.with("case", caze)
                      .error("Exception thrown processing case. Processing postponed", e);
                  failures++;
                }
              }
            }
          }

          distInfo.setCasesSucceeded(successes);
          distInfo.setCasesFailed(failures);
        } catch (Exception e) {
          log.error("Failed to obtain IAC codes", e);
        }
      }
    } catch (Exception e) {
      log.error("Failed to process cases", e);
    } finally {
      try {
        caseDistributionListManager.deleteList(CASE_DISTRIBUTOR_LIST_ID, true);
      } catch (LockingException e) {
        log.error("Failed to release caseDistributionListManager", e);
      }
    }

    log.debug("CaseDistributor sleeping");
    return distInfo;
  }

  /**
   * Get the oldest page of SAMPLED_INIT & REPLACEMENT_INIT cases to activate - but do not retrieve
   * the same cases as other CaseSvc' in the cluster
   *
   * @throws LockingException locking exception thrown when caseDistributionListManager does
   * @return list of cases
   */
  private List<Case> retrieveCases() throws LockingException {
    List<Case> cases;

    List<Integer> excludedCases =
        caseDistributionListManager.findList(CASE_DISTRIBUTOR_LIST_ID, false);
    log.with("excluded_cases", excludedCases).debug("retrieve cases");

    // prepare and execute the query to find the oldest N cases that are in SAMPLED_INIT &
    // REPLACEMENT_INIT states and
    // not in the excluded list
    Pageable pageable =
        new PageRequest(
            0,
            appConfig.getCaseDistribution().getRetrievalMax(),
            new Sort(new Sort.Order(Direction.ASC, "createdDateTime")));
    excludedCases.add(IMPOSSIBLE_CASE_ID);
    cases =
        caseRepo.findByStateInAndCasePKNotIn(
            Arrays.asList(CaseState.SAMPLED_INIT, CaseState.REPLACEMENT_INIT),
            excludedCases,
            pageable);

    if (!CollectionUtils.isEmpty(cases)) {
      log.with(
              "case_ids",
              cases.stream().map(caze -> caze.getId().toString()).collect(Collectors.joining(",")))
          .debug("RETRIEVED case ids");
      caseDistributionListManager.saveList(
          CASE_DISTRIBUTOR_LIST_ID,
          cases.stream().map(caze -> caze.getCasePK()).collect(Collectors.toList()),
          true);
    } else {
      log.debug("RETRIEVED 0 case id");
      caseDistributionListManager.unlockContainer();
    }

    return cases;
  }

  /**
   * Deal with a single case.
   *
   * <p>The processing requires to write to our own case table. A CaseNotification is also produced
   * and added to the outbound CaseNotifications sent to the action service.
   *
   * @param caze the case to deal with
   * @param iac the IAC to assign to the Case
   * @throws CTPException when transitionCase does.
   */
  private void processCase(final Case caze, final String iac) throws CTPException {
    log.with("case_id", caze.getId()).debug("Processing case");

    CaseDTO.CaseEvent event = null;
    CaseState initialState = caze.getState();
    switch (caze.getState()) {
      case SAMPLED_INIT:
        event = CaseDTO.CaseEvent.ACTIVATED;
        eventPublisher.publishEvent("case Activated");
        break;
      case REPLACEMENT_INIT:
        event = CaseDTO.CaseEvent.REPLACED;
        break;
      default:
        log.with("initial_state", initialState).error("Unexpected state found");
    }

    Case updatedCase = transitionCase(caze, event);
    updatedCase.setIac(iac);
    caseRepo.saveAndFlush(updatedCase);

    caseService.saveCaseIacAudit(updatedCase);

    CaseNotification caseNotification = caseService.prepareCaseNotification(caze, event);
    log.debug("Publishing caseNotification...");
    notificationPublisher.sendNotification(caseNotification);
  }

  /**
   * Change the case status in db to indicate we have sent this case downstream, and clear previous
   * situation (in the scenario where the case has prev. failed)
   *
   * @param caze the case to change and persist
   * @param event the event to transition the case with
   * @return the transitioned case
   * @throws CTPException when case state transition error
   */
  private Case transitionCase(final Case caze, final CaseDTO.CaseEvent event) throws CTPException {
    CaseState nextState = caseSvcStateTransitionManager.transition(caze.getState(), event);
    caze.setState(nextState);
    return caze;
  }
}
