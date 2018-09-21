package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

@RunWith(MockitoJUnitRunner.class)
public class CaseIACServiceTest {

  @Mock private InternetAccessCodeSvcClientService iacClient;
  @Mock private Case mockedCase;
  @Mock private CaseIacAuditRepository caseIacAuditRepository;
  @Mock private CaseEventRepository caseEventRepository;
  @Mock private Clock clock;
  @InjectMocks private CaseIACServiceImpl caseIACService;

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

  @Test
  public void shouldDisableAllIACsForCaseWhenMultipleIACsExist() {
    // Given
    List<CaseIacAudit> listOf3IACAudits = new ArrayList<>();

    listOf3IACAudits.add(createCaseIacAudit("IAC1"));
    listOf3IACAudits.add(createCaseIacAudit("IAC2"));
    listOf3IACAudits.add(createCaseIacAudit("IAC3"));

    when(mockedCase.getIacAudits()).thenReturn(listOf3IACAudits);

    // When
    caseIACService.disableAllIACsForCase(mockedCase);

    // Then
    verify(iacClient).disableIAC("IAC1");
    verify(iacClient).disableIAC("IAC2");
    verify(iacClient).disableIAC("IAC3");
  }

  @Test
  public void shouldDisableOneIACForCaseWhenOneIACExists() {
    // Given
    List<CaseIacAudit> listOfSingleIAC = new ArrayList<>();

    listOfSingleIAC.add(createCaseIacAudit("IAC1"));

    when(mockedCase.getIacAudits()).thenReturn(listOfSingleIAC);

    // When
    caseIACService.disableAllIACsForCase(mockedCase);

    // Then
    verify(iacClient).disableIAC("IAC1");
  }

  @Test
  public void shouldNotTryToDisableIACForCaseWhenNoneExist() {
    // Given
    List<CaseIacAudit> emptyIACList = new ArrayList<>();

    when(mockedCase.getIacAudits()).thenReturn(emptyIACList);

    // When
    caseIACService.disableAllIACsForCase(mockedCase);

    // Then
    verify(iacClient, times(0)).disableIAC(any(String.class));
  }

  /** mock iac audit * */
  private CaseIacAudit createCaseIacAudit(String IAC) {
    return new CaseIacAudit(1, 1, IAC, new Timestamp(System.currentTimeMillis()));
  }
}
