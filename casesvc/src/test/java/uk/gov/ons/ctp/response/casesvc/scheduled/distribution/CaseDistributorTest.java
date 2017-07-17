package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.LockingException;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.CaseDistribution;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test the case distributor
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseDistributorTest {
  private static final int TWO = 2;
  private static final int TEN = 10;

  private static final long TEN_LONG = 10L;

  private static final String IAC_0 = "ABCD-EFGH-IJKL-MNOP";
  private static final String IAC_1 = "QRST-UVWX-YZAB-CDEF";
  private static final String IAC_2 = "GHIJ-KLMN-OPQR-STUV";

  private List<Case> cases;

  @Spy
  private AppConfig appConfig = new AppConfig();

  @Mock
  private PlatformTransactionManager transactionManager;  // required by transactionTemplate

  @Mock
  private TransactionTemplate transactionTemplate;

  @Mock
  private Tracer tracer;

  @Mock
  private DistributedListManager<Integer> caseDistributionListManager;

  @Mock
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  @Mock
  private CaseRepository caseRepo;

  @Mock
  private CaseService caseService;

  @Mock
  private CaseNotificationPublisher caseNotificationPublisher;

  @Mock
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @InjectMocks
  private CaseDistributor caseDistributor;

  /**
   * All of these tests require the mocked repos to respond with predictable data loaded from test fixture json files.
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    cases = FixtureHelper.loadClassFixtures(Case[].class);

    InternetAccessCodeSvc internetAccessCodeSvc = new InternetAccessCodeSvc();
    appConfig.setInternetAccessCodeSvc(internetAccessCodeSvc);

    CaseDistribution caseDistributionConfig = new CaseDistribution();
    caseDistributionConfig.setDelayMilliSeconds(TEN_LONG);
    caseDistributionConfig.setRetrySleepSeconds(TEN);
    caseDistributionConfig.setRetrievalMax(TEN);
    caseDistributionConfig.setDistributionMax(TWO);
    appConfig.setCaseDistribution(caseDistributionConfig);

    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test where we fail at the 1st hurdle, ie we can't retrieve any cases
   */
  @Test
  public void testFailRetrievingCases() throws LockingException {
    Mockito.when(caseRepo.findByStateInAndCasePKNotIn(any(List.class), any(List.class), any(Pageable.class)))
        .thenThrow(new RuntimeException("Database access failed"));

    caseDistributor.distribute();

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).generateIACs(any(Integer.class));
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(caseService, times(0)).prepareCaseNotification(any(Case.class),
            any(CaseDTO.CaseEvent.class));
    verify(caseNotificationPublisher, times(0)).sendNotifications(any(List.class));
    verify(caseDistributionListManager, times(0)).deleteList(any(String.class),
            any(Boolean.class));
    verify(caseDistributionListManager, times(0)).unlockContainer();
    verify(tracer, times(1)).close(any(Span.class));
  }

  /**
   * Test where we retrieve 0 case
   */
  @Test
  public void testRetrieveZeroCases() throws LockingException {
    List<Case> cases = new ArrayList<>();
    Mockito.when(caseRepo.findByStateInAndCasePKNotIn(any(List.class), any(List.class), any(Pageable.class)))
            .thenReturn(cases);

    caseDistributor.distribute();

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).generateIACs(any(Integer.class));
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(caseService, times(0)).prepareCaseNotification(any(Case.class),
            any(CaseDTO.CaseEvent.class));
    verify(caseNotificationPublisher, times(0)).sendNotifications(any(List.class));
    verify(caseDistributionListManager, times(0)).deleteList(any(String.class),
            any(Boolean.class));
    verify(caseDistributionListManager, times(1)).unlockContainer();
    verify(tracer, times(1)).close(any(Span.class));
  }

  /**
   * Test where we retrieve cases but IAC call fails
   */
  @Test
  public void testFailIAC() throws LockingException {
    Mockito.when(caseRepo.findByStateInAndCasePKNotIn(any(List.class), any(List.class), any(Pageable.class)))
            .thenReturn(cases);
    Mockito.when(internetAccessCodeSvcClientService.generateIACs(any(Integer.class))).thenThrow(
            new RuntimeException("IAC access failed"));

    caseDistributor.distribute();

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(internetAccessCodeSvcClientService, times(1)).generateIACs(any(Integer.class));
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(caseService, times(0)).prepareCaseNotification(any(Case.class),
            any(CaseDTO.CaseEvent.class));
    verify(caseNotificationPublisher, times(0)).sendNotifications(any(List.class));
    verify(caseDistributionListManager, times(1)).deleteList(any(String.class),
            any(Boolean.class));
    verify(caseDistributionListManager, times(1)).unlockContainer();
    verify(tracer, times(1)).close(any(Span.class));
  }

  /**
   * Test where we retrieve 3 cases and 3 IAC correctly. Cases also have a correct state.
   */
  @Test
  public void testHappyPath() throws CTPException, LockingException {
    Mockito.when(caseRepo.findByStateInAndCasePKNotIn(any(List.class), any(List.class), any(Pageable.class)))
            .thenReturn(cases);

    List<String> iacs = new ArrayList<>();
    iacs.add(IAC_0);
    iacs.add(IAC_1);
    iacs.add(IAC_2);
    Mockito.when(internetAccessCodeSvcClientService.generateIACs(any(Integer.class))).thenReturn(iacs);

    when(caseSvcStateTransitionManager.transition(any(CaseDTO.CaseState.class), any(CaseDTO.CaseEvent.class))).
            thenReturn(CaseDTO.CaseState.ACTIONABLE);

    CaseNotification caseNotification = new CaseNotification();
    when(caseService.prepareCaseNotification(any(Case.class), any(CaseDTO.CaseEvent.class))).
            thenReturn(caseNotification);

    // launching the test...
    caseDistributor.distribute();

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(internetAccessCodeSvcClientService, times(1)).generateIACs(any(Integer.class));
    verify(caseRepo, times(3)).saveAndFlush(any(Case.class));
    verify(caseService, times(3)).prepareCaseNotification(any(Case.class),
            any(CaseDTO.CaseEvent.class));
    // Only 2 below as we have 3 cases AND setDistributionMax is at 2 in setUp(). So, the first time around it is
    // invoked with a list of 2 cases and the second time around with a list of 1 case.
    verify(caseNotificationPublisher, times(2)).sendNotifications(any(List.class));
    verify(caseDistributionListManager, times(1)).deleteList(any(String.class),
            any(Boolean.class));
    verify(caseDistributionListManager, times(1)).unlockContainer();
    verify(tracer, times(1)).close(any(Span.class));
  }
}
