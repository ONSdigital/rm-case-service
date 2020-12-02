package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.common.UnirestInitialiser;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ActionTemplateEndpointIT {
    private static final Logger log = LoggerFactory.getLogger(ActionTemplateEndpointIT.class);

    @Autowired
    private ActionTemplateRepository actionTemplateRepo;
    @Autowired private ObjectMapper mapper;
    @Autowired private AppConfig appConfig;
    @ClassRule
    public static WireMockRule wireMockRule =
            new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

    @LocalServerPort private int port;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        ObjectMapper value = new ObjectMapper();
        UnirestInitialiser.initialise(value);
        Thread.sleep(2000);
    }

    @Before
    public void testSetup() {
        actionTemplateRepo.deleteAll();
    }

    @Test
    public void testCreateActionTemplateSuccess() throws Exception {

        // Given
        ActionTemplateDTO request = new ActionTemplateDTO("BSNL",
                "something",
                "mps", ActionTemplateDTO.Handler.LETTER,
                null
                );

        // When
        HttpResponse response =
                Unirest.post("http://localhost:" + port + "/cases/action-template/")
                        .basicAuth("admin", "secret")
                        .header("Content-Type", "application/json")
                        .body(request)
                        .asObject(ActionTemplateDTO.class);

        // Then
        assertThat(response.getStatus()).isEqualTo(201);
    }
}
