package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitBase;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Slf4j
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseEndpointIT {

  @Autowired private ResourceLoader resourceLoader;

  @LocalServerPort private int port;

  @Autowired private ObjectMapper mapper;

  @Autowired private AppConfig appConfig;

  @Rule public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

  //  @Rule
  //  public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @Test
  public void ensureSampleAttributesReceived() throws Exception {
    createIACStub();

    SimpleMessageSender sender = getMessageSender();

    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(UUID.randomUUID().toString());
    sampleUnit.setId(UUID.randomUUID().toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef("LMS0001");
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType("H");

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml =
        convertObjectToXml(
            jaxbContext, sampleUnit, "casesvc/xsd/inbound/SampleUnitNotification.xsd");

    sender.sendMessage("collection-inbound-exchange", "Case.CaseDelivery.binding", xml);

    Thread.sleep(10_000_000);
  }

  /**
   * Convert an object into its XML equivalent based on the provided schema
   *
   * @param context JAXBContext
   * @param o Object to convert to XML
   * @param cpSchemaLocation Location of *.xsd as a classpath location (don't prepend location with
   *     classpath)
   * @return xml of the object
   * @throws Exception
   */
  String convertObjectToXml(JAXBContext context, Object o, String cpSchemaLocation)
      throws Exception {
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    URL xsd = resourceLoader.getResource(String.format("classpath:%s", cpSchemaLocation)).getURL();
    Schema schema = sf.newSchema(xsd);
    Marshaller mars = context.createMarshaller();
    StringWriter buffer = new StringWriter();

    mars.setSchema(schema);
    mars.marshal(o, buffer);

    return buffer.toString();
  }

  /**
   * Creates a new SimpleMessageSender based on the config in AppConfig
   *
   * @return a new SimpleMessageSender
   */
  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

  private void createIACStub() throws IOException {
    this.wireMockRule.stubFor(
        post(urlPathEqualTo("/iacs"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[\"grtt7x2nhygg\"]")));
  }
}
