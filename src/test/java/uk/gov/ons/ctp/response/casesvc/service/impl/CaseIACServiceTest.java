package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

@RunWith(MockitoJUnitRunner.class)
public class CaseIACServiceTest {

  @Mock private InternetAccessCodeSvcClientService iacClient;
  @Mock private CaseIacAuditRepository caseIacAuditRepository;
  @Mock private CaseEventRepository caseEventRepository;
  @Mock private Clock clock;
  @InjectMocks private CaseIACService caseIACService;

  @Test
  public void shouldGenerateNewIacCode() {
    // Given
    Integer casePk = 1;
    given(iacClient.generateIACs(1)).willReturn(Collections.singletonList("new iac code"));
    Instant now = Instant.now();
    given(clock.instant()).willReturn(now);

    // When
    String iacCode = caseIACService.generateNewCaseIACCode(casePk);

    // Then
    assertEquals("new iac code", iacCode);
  }

  @Test
  public void shouldCreateGenerateEnrolmentCodeCaseEvent() {
    // Given
    Integer casePk = 1;
    given(iacClient.generateIACs(1)).willReturn(Collections.singletonList("new iac code"));
    Instant now = Instant.now();
    given(clock.instant()).willReturn(now);

    // When
    caseIACService.generateNewCaseIACCode(casePk);

    // Then
    CaseEvent caseEvent =
        CaseEvent.builder()
            .category(CategoryDTO.CategoryName.GENERATE_ENROLMENT_CODE)
            .createdDateTime(Timestamp.from(now))
            .caseFK(casePk)
            .createdBy(null) // TODO
            .build();
    verify(caseEventRepository).save(caseEvent);
  }

  @Test
  public void shouldSaveIACCodeAudit() {
    // Given
    Integer casePk = 1;
    given(iacClient.generateIACs(1)).willReturn(Collections.singletonList("new iac code"));
    Instant now = Instant.now();
    given(clock.instant()).willReturn(now);

    // When
    caseIACService.generateNewCaseIACCode(casePk);

    // Then
    CaseIacAudit audit =
        CaseIacAudit.builder()
            .caseFK(casePk)
            .iac("new iac code")
            .createdDateTime(Timestamp.from(now))
            .build();
    verify(caseIacAuditRepository).save(audit);
  }
}
