package uk.gov.ons.ctp.response.casesvc.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

@RunWith(MockitoJUnitRunner.class)
public class CaseIACEndpointTest {

    private MockMvc mockMvc;

    @Mock
    private CaseService caseService;

    @InjectMocks
    private CaseIACEndpoint caseIACEndpoint;

    @Before
    public void setUp() {
        this.mockMvc =
                MockMvcBuilders.standaloneSetup(caseIACEndpoint)
                        .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
                        .build();
    }

    @Test
    public void shouldReturnNotFoundWhenCaseDoesNotExist() throws Exception {

        Mockito.when(caseService.findCaseById(Mockito.any())).thenReturn(null);

        // Given
        UUID caseId = UUID.randomUUID();

        // When
        ResultActions actions = mockMvc.perform(post("/cases/{caseId}/iac", caseId));

        // Then
        actions.andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnCaseCreatedWhenCaseExists() throws Exception {

        String expected = "new iac code";

        Mockito.when(caseService.findCaseById(Mockito.any())).thenReturn(new Case());
        Mockito.when(caseService.generateNewCaseIACCode(Mockito.any())).thenReturn(expected);

        // Given
        UUID caseId = UUID.randomUUID();

        // When
        ResultActions actual = mockMvc.perform(post("/cases/{caseId}/iac", caseId));

        // Then
        actual.andExpect(status().isCreated())
                .andExpect(content().string(expected));
    }
}