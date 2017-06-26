package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the 'service' class that distributes cases to the action service. It
 * has a number of injected beans, including a RestClient, Repositories and the
 * InstructionPublisher
 *
 * It cannot use the normal serviceimpl @Transaction pattern, as that will
 * rollback on a runtime exception (desired) but will then rethrow that
 * exception all the way up the stack. If we try and catch that exception, the
 * rollback does not happen. So - see the TransactionTemplate usage - that
 * allows both rollback and for us to catch the runtime exception and handle it.
 *
 * This class is scheduled to wake and looks for Cases in INIT state to send to
 * the action service. On each wake cycle, it fetches the first n cases, by
 * createddatetime. It loops through those n cases and fetches m IACs from the
 * IAC service. It then updates each case with an IAC taken from
 * the set of m codes and transitions the case state to ACTIVE. It takes each
 * case and constructs a notification message to send to the action service -
 * when it has x notifications it publishes them.
 *
 */
@Component
@Slf4j
public class CaseDistributor {

  private static final String CASE_DISTRIBUTOR_SPAN = "caseDistributor";
  private static final String CASE_DISTRIBUTOR_LIST_ID = "case";

  // this is a bit of a kludge - jpa does not like having an IN clause with an empty list
  // it does not return results when you expect it to - so ... always have this in the list of excluded case ids
  private static final int IMPOSSIBLE_CASE_ID = Integer.MAX_VALUE;

  private static final long MILLISECONDS = 1000L;

  @Autowired
  private DistributedListManager<Integer> caseDistributionListManager;

  @Autowired
  private Tracer tracer;

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent>
          caseSvcStateTransitionManager;

  @Autowired
  private CaseNotificationPublisher notificationPublisher;

  @Autowired
  private CaseRepository caseRepo;

  @Autowired
  private CaseService caseService;

  @Autowired
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  // single TransactionTemplate shared amongst all methods in this instance
  private final TransactionTemplate transactionTemplate;

  /**
   * Constructor into which the Spring PlatformTransactionManager is injected
   *
   * @param transactionManager provided by Spring
   */
  @Autowired
  public CaseDistributor(final PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  /**
   * wake up on schedule and check for cases that are in INIT state - fetch IACs
   * for them, adding the IAC to the case questionnaire, and send a notificaiton
   * of the activation to the action service
   *
   * @return the info for the health endpoint regarding the distribution just
   *         performed
   */
  public final CaseDistributionInfo distribute() {
    log.info("CaseDistributor awoken");
    Span distribSpan = tracer.createSpan(CASE_DISTRIBUTOR_SPAN);
    CaseDistributionInfo distInfo = new CaseDistributionInfo();

    int successes = 0;
    int failures = 0;
    try {
      List<CaseNotification> caseNotifications = new ArrayList<>();
      List<Case> cases = retrieveCases();

      if (cases.size() > 0) {
        int iacPageSize = appConfig.getCaseDistribution().getIacMax();
        List<String> codes = null;
        for (int idx = 0; idx < cases.size(); idx++) {
          Case caze = cases.get(idx);
          if (idx % iacPageSize == 0) {
            int codesToRequest = (idx < cases.size()
                    / iacPageSize * iacPageSize) ? iacPageSize
                : (cases.size() % iacPageSize);
            try {
              codes = internetAccessCodeSvcClientService.generateIACs(codesToRequest);
            } catch (Exception e) {
              log.error("Failed to obtain IAC block");
              // exit case loop and send notifications of cases activated so far to action svc
              break;
            }
          }

          try {
            caseNotifications.add(processCase(caze, codes.get(idx % iacPageSize)));
            if (caseNotifications.size() == appConfig.getCaseDistribution().getDistributionMax()) {
              publishCases(caseNotifications);
            }
            successes++;
          } catch (Exception e) {
            // single case/questionnaire db changes rolled back
            log.error("Exception {} thrown processing case {}. Processing postponed", e.getMessage(), caze.getCasePK());
            failures++;
          }
        }

        distInfo.setCasesSucceeded(successes);
        distInfo.setCasesFailed(failures);

        publishCases(caseNotifications);

        caseDistributionListManager.deleteList(CASE_DISTRIBUTOR_LIST_ID, true);
      }

      try {
        caseDistributionListManager.unlockContainer();
      } catch (LockingException le) {
        // oh well - will time out or we never had the lock
      }

      tracer.close(distribSpan);
    } catch (Exception e) {
      // something went wrong retrieving case types or cases
      log.error("Failed to process cases because {}", e.getMessage());
    }

    log.info("CaseDistributor sleeping");
    return distInfo;
  }

  /**
   * Get the oldest page of SAMPLED_INIT & REPLACEMENT_INIT cases to activate - but do not retrieve the
   * same cases as other CaseSvc' in the cluster
   *
   * @throws LockingException locking exception thrown
   * @return list of cases
   */
  private List<Case> retrieveCases() throws LockingException {
    List<Case> cases = new ArrayList<>();

    List<Integer> excludedCases = caseDistributionListManager.findList(CASE_DISTRIBUTOR_LIST_ID, false);

    // using the distributed map of lists of cases that other nodes are processing
    // flatten them into a list of case ids to exclude from our query
    log.debug("retrieve cases excluding {}", excludedCases);

    // prepare and execute the query to find the oldest N cases that are in
    // INIT states and not in the excluded list
    Pageable pageable = new PageRequest(0, appConfig.getCaseDistribution().getRetrievalMax(),
            new Sort(new Sort.Order(Direction.ASC, "createdDateTime")));
    excludedCases.add(Integer.valueOf(IMPOSSIBLE_CASE_ID));
    cases = caseRepo.findByStateInAndCasePKNotIn(Arrays.asList(CaseState.SAMPLED_INIT, CaseState.REPLACEMENT_INIT),
            excludedCases, pageable);

    log.debug("RETRIEVED case ids {}", cases.stream().map(a -> a.getCasePK().toString()).collect(
            Collectors.joining(",")));

    // try and save our list to the distributed store
    if (cases.size() > 0) {
      caseDistributionListManager.saveList(CASE_DISTRIBUTOR_LIST_ID, cases.
              stream().map(caze -> caze.getCasePK()).collect(Collectors.toList()), true);
    }

    return cases;
  }

  /**
   * Deal with a single case - the transaction boundary is here. The processing
   * requires a call to the IAC service and to write to our own case
   * table. The rollback is most likely to be triggered by either
   * failing to find the IAC service, or if it sends back an http error status
   * code.
   *
   * @param caze the case to deal with
   * @param iac the newly minted IAC to assign to the Case
   * @return The resulting CaseNotification that will be added to the outbound
   *         CaseNotifications sent to the action service
   */
  private CaseNotification processCase(final Case caze, String iac) {
    log.info("processing caseid {}", caze.getId());
    return transactionTemplate.execute(
            new TransactionCallback<CaseNotification>() {
              // the code in this method executes in a transactional context
              public CaseNotification doInTransaction(final TransactionStatus status) {
                CaseNotification caseNotification = null;
                // update our cases state in db
                CaseDTO.CaseEvent event = null;
                switch (caze.getState()) {
                  case SAMPLED_INIT:
                    event = CaseDTO.CaseEvent.ACTIVATED;
                    break;
                  case REPLACEMENT_INIT:
                    event = CaseDTO.CaseEvent.REPLACED;
                    break;
                  default:
                    String msg = String.format("Case id %s has incorrect state %s", caze.getId(), caze.getState());
                    log.error(msg);
                    throw new RuntimeException(msg);  // Recommended way by Spring to come out of a TransactionCallback
                }

                try {
                  Case updatedCase = transitionCase(caze, event);
                  updatedCase.setIac(iac);
                  caseRepo.saveAndFlush(updatedCase);

                  // create the request, filling in details by GETs from casesvc
                  caseNotification = caseService.prepareCaseNotification(caze, event);
                  return caseNotification;
                } catch (CTPException e) {
                  String msg = String.format("Transition error - cause = %s - message = %s", e.getCause(),
                          e.getMessage());
                  log.error(msg);
                  throw new RuntimeException(msg);  // Recommended way by Spring to come out of a TransactionCallback
                }
              }
            });
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

  /**
   * publish cases using the inject publisher - try and try and try ... side
   * effect will be the list of notifications will be cleared once sent
   *
   * @param caseNotifications the case messages to publish - cleared afterwards
   * @throws InterruptedException our pause was interrupted
   */
  private void publishCases(List<CaseNotification> caseNotifications) {
    boolean published = false;
    if (caseNotifications.size() > 0 || caseNotifications.size() > 0) {
      do {
        try {
          // send the list of requests for this case type to the
          // handler
          log.debug("Publishing instruction");
          notificationPublisher.sendNotifications(caseNotifications);
          caseNotifications.clear();
          published = true;
        } catch (Exception e) {
          // broker not there ? sleep then retry
          log.warn("Failed to send notifications {} because {}",
              caseNotifications.stream().map(a -> a.getCaseId().toString())
                  .collect(Collectors.joining(",")),
              e.getMessage());
          log.warn("CaseDistribution will sleep and retry publish");
          try {
            Thread.sleep(appConfig.getCaseDistribution().
                    getRetrySleepSeconds() * MILLISECONDS);
          } catch (InterruptedException ie) {
            log.warn("Retry sleep was interrupted");
          }
        }
      } while (!published);
    }
  }

}
