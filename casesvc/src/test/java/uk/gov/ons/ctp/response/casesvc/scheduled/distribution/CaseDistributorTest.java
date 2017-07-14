package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.support.TransactionTemplate;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.LockingException;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.CaseDistribution;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test the case distributor
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseDistributorTest {
  private static final int TWO = 2;
  private static final int TEN = 10;

  private static final long TEN_LONG = 10L;

  private static final UUID CASE_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");

  @Spy
  private AppConfig appConfig = new AppConfig();

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

  @InjectMocks
  private CaseDistributor caseDistributor;

  /**
   * A Test
   */
  @Before
  public void setUp() {
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
    List<Case> cases = new ArrayList<>();
    Case caze = new Case();
    caze.setId(CASE_ID);
    cases.add(caze);
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
}
