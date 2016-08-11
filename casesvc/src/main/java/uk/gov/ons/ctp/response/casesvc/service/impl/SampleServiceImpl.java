package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.GeneratedCase;
import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseLifeCycleRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.SampleRepository;
import uk.gov.ons.ctp.response.casesvc.message.NotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotifications;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * A SampleService implementation which encapsulates all business logic
 * operating on the Sample entity model.
 */
@Named
@Slf4j
public final class SampleServiceImpl implements SampleService {

  private static final int TRANSACTION_TIMEOUT = 120;

  @Inject
  private AppConfig appConfig;

  @Inject
  private SampleRepository sampleRepo;

  @Inject
  private CaseLifeCycleRepository caseLifeCycleRepo;

  @Inject
  private NotificationPublisher notificationPubl;

  @Inject
  private MapperFacade mapperFacade;

  @Override
  public List<Sample> findSamples() {
    log.debug("Entering findSamples");
    return sampleRepo.findAll();
  }

  @Override
  public Sample findSampleBySampleId(final Integer sampleId) {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    return sampleRepo.findOne(sampleId);
  }

  /**
   * Generate new cases for given sample ID, geography type and geography code
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public void generateCases(final Integer sampleId, final String geographyType, final String geographyCode) {
    log.debug("Entering generateCases with sampleId {} - geographyType {} - geographyCode {}", sampleId, geographyType,
        geographyCode);
    CaseNotifications caseNotifications = new CaseNotifications();
    List<GeneratedCase> casesGenerated = caseLifeCycleRepo.generateCases(sampleId, geographyType, geographyCode);
    List<CaseNotification> caseNotification = mapperFacade.mapAsList(casesGenerated, CaseNotification.class);
    caseNotification.forEach((caseMessage) -> {
      caseMessage.setNotificationType(NotificationType.CREATED);
      caseNotifications.getCaseNotifications().add(caseMessage);
      if (caseNotifications.getCaseNotifications().size() >= appConfig.getNotificationPubl().getNotificationMax()) {
        notificationPubl.sendNotifications(caseNotifications);
        caseNotifications.getCaseNotifications().clear();
      }
    });
    if (!caseNotifications.getCaseNotifications().isEmpty()) {
      notificationPubl.sendNotifications(caseNotifications);
    }
    return;
  }

}
