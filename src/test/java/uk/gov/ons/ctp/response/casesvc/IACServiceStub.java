package uk.gov.ons.ctp.response.casesvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IACServiceStub {

  private static final Logger log = LoggerFactory.getLogger(IACServiceStub.class);

  public void createIACStub() {
    log.info("Stubbing IAC Service POST /iacs endpoint");
    stubFor(
        post(urlPathEqualTo("/iacs"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[\"{{randomValue length=12 type='ALPHANUMERIC' lowercase=true}}\"]")
                    .withTransformers("response-template")));
  }

  public void disableIACStub() {
    log.info("Stubbing IAC Service PUT /iacs/{iac} endpoint");
    stubFor(
        put(urlPathMatching("/iacs/[a-z0-9]{12}$"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
  }
}
