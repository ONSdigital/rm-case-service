package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.representation.CaseIACDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseIACService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

@RunWith(MockitoJUnitRunner.class)
@Component
public class CaseIACEndpointTest {

  private MockMvc mockMvc;

  @Mock private CaseService caseService;
  @Mock private CaseIACService caseIACService;
  @InjectMocks private CaseIACEndpoint caseIACEndpoint;

  private ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseIACEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();
  }

  @Test
  public void shouldReturnNotFoundForCreateIACWhenCaseDoesNotExist() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();
    given(caseService.findCaseById(caseId)).willReturn(null);

    // When
    ResultActions actions = mockMvc.perform(post("/cases/{caseId}/iac", caseId));

    // Then
    actions.andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnCreatedForCreateIACWhenCaseExists() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();
    String expected = "new iac code";
    Case aCase = Case.builder().casePK(1).build();
    given(caseService.findCaseById(caseId)).willReturn(aCase);
    given(caseIACService.generateNewCaseIACCode(aCase.getCasePK())).willReturn(expected);

    // When
    ResultActions actual = mockMvc.perform(post("/cases/{caseId}/iac", caseId));

    CaseIACDTO dto = new CaseIACDTO(expected);
    String expectedJson = this.mapper.writeValueAsString(dto);

    // Then
    actual.andExpect(status().isCreated()).andExpect(content().string(expectedJson));
  }

  @Test
  public void shouldReturnLocationToIacCodesForCreateIAC() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();
    given(caseService.findCaseById(caseId)).willReturn(new Case());

    // When
    ResultActions actual = mockMvc.perform(post("/cases/{caseId}/iac", caseId));

    // Then
    String location = String.format("http://localhost/cases/%s/iac", caseId);
    actual.andExpect(header().stringValues("Location", location));
  }

  @Test
  public void shouldReturnNotFoundGetIACWhenCaseDoesNotExist() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();
    given(caseService.findCaseById(caseId)).willReturn(null);

    // When
    ResultActions actions = mockMvc.perform(get("/cases/{caseId}/iac", caseId));

    // Then
    actions.andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnIACCodesForGetIACWhenCaseExists() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();
    given(caseService.findCaseById(caseId))
        .willReturn(
            Case.builder()
                .iacAudits(Collections.singletonList(CaseIacAudit.builder().iac("an iac").build()))
                .build());

    // When
    ResultActions actions = mockMvc.perform(get("/cases/{caseId}/iac", caseId));

    // Then
    actions.andExpect(status().isOk()).andExpect(content().string(containsString("an iac")));
  }
}
