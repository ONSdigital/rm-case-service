package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;

public abstract class CaseITBase {

  private static final Logger log = LoggerFactory.getLogger(CaseITBase.class);

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Autowired protected CaseCreator caseCreator;

  @LocalServerPort protected int port;

  protected void createIACStub() {
    stubFor(
        post(urlPathEqualTo("/iacs"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[\"{{randomValue length=12 type='ALPHANUMERIC' lowercase=true}}\"]")
                    .withTransformers("response-template")));
  }

  protected void disableIACStub() {
    stubFor(
        put(urlPathMatching("/iacs/(.*)"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
  }
}
