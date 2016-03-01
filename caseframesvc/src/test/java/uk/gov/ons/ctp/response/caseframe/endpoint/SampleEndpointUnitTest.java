package uk.gov.ons.ctp.response.caseframe.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPExceptionMapper;
import uk.gov.ons.ctp.common.jaxrs.GeneralExceptionMapper;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.SampleService;
import uk.gov.ons.ctp.response.caseframe.utility.MockSampleServiceFactory;
import uk.gov.ons.ctp.response.caseframe.utility.MockSurveyServiceFactory;

/**
 * Created by Martin.Humphrey on 23/2/2016.
 */
public class SampleEndpointUnitTest extends JerseyTest  {
  @Override
  public Application configure() {
    ResourceConfig config = new ResourceConfig(SampleEndpoint.class);

    AbstractBinder binder = new AbstractBinder() {
      @Override
      protected void configure() {
        bindFactory(MockSampleServiceFactory.class).to(SampleService.class);
        bind(new CaseFrameBeanMapper()).to(MapperFacade.class);
      }
    };
    config.register(binder);

    config.register(CTPExceptionMapper.class);
    config.register(GeneralExceptionMapper.class);
    return config;
  }

  @Test
  public void findSamplesFound() {

    Client client = ClientBuilder.newClient();
    Response response = client.target("http://localhost:9998/samples").request().get();

    Assert.assertEquals(200, response.getStatus());
    String json = response.readEntity(String.class);
 
    assertEquals(new Integer(3), JsonPath.parse(json).read("$.length()", Integer.class));
 
    List<String> nameList = JsonPath.parse(json).read("$..sampleName");
    assertThat(nameList, containsInAnyOrder(MockSampleServiceFactory.SAMPLE1_NAME, MockSampleServiceFactory.SAMPLE2_NAME,
        MockSampleServiceFactory.SAMPLE3_NAME));
 
    List<String> descList = JsonPath.parse(json).read("$..description");
    assertThat(descList, containsInAnyOrder(MockSampleServiceFactory.SAMPLE1_DESC, MockSampleServiceFactory.SAMPLE2_DESC,
        MockSampleServiceFactory.SAMPLE3_DESC));

    List<String> criteriaList = JsonPath.parse(json).read("$..addressCriteria");
    assertThat(criteriaList, containsInAnyOrder(MockSampleServiceFactory.SAMPLE1_CRITERIA, MockSampleServiceFactory.SAMPLE2_CRITERIA,
        MockSampleServiceFactory.SAMPLE3_CRITERIA));

    List<Integer> caseTypeList = JsonPath.parse(json).read("$..caseTypeId");
    assertThat(caseTypeList, containsInAnyOrder(MockSampleServiceFactory.SAMPLE1_CASETYPEID, MockSampleServiceFactory.SAMPLE2_CASETYPEID,
        MockSampleServiceFactory.SAMPLE3_CASETYPEID));

    List<Integer> surveyList = JsonPath.parse(json).read("$..surveyId");
    assertThat(surveyList, everyItem(equalTo(MockSampleServiceFactory.SURVEYID)));

    response.close();
    client.close();
  }
  
  @Test
  public void findSampleBySampleIdFound() {

    Client client = ClientBuilder.newClient();
    Response response = client.target(String.format("http://localhost:9998/samples/%s",MockSampleServiceFactory.SAMPLEID)).request().get();

    Assert.assertEquals(200, response.getStatus());
    String json = response.readEntity(String.class);

    assertEquals(new Integer(3), JsonPath.parse(json).read("$.sampleId", Integer.class));
    assertEquals(MockSampleServiceFactory.SAMPLE3_NAME,JsonPath.parse(json).read("$.sampleName", String.class));
    assertEquals(MockSampleServiceFactory.SAMPLE3_DESC,JsonPath.parse(json).read("$.description", String.class));
    assertEquals(MockSampleServiceFactory.SAMPLE3_CRITERIA,JsonPath.parse(json).read("$.addressCriteria", String.class));
    assertEquals(MockSampleServiceFactory.SAMPLE3_CASETYPEID,JsonPath.parse(json).read("$.caseTypeId", Integer.class));
    assertEquals(MockSampleServiceFactory.SURVEYID,JsonPath.parse(json).read("$.surveyId", Integer.class));

    response.close();
    client.close();
  }
  
  @Test
  public void findSampleBySampleIdNotFound() {

    Client client = ClientBuilder.newClient();
    Response response = client.target(String.format("http://localhost:9998/samples/%s",MockSampleServiceFactory.NON_EXISTING_SAMPLEID)).request().get();
    
    Assert.assertEquals(404, response.getStatus());
    String responseStrg = response.readEntity(String.class);
    Assert.assertEquals(CTPException.Fault.RESOURCE_NOT_FOUND.toString(), JsonPath.read(responseStrg, "$.error.code"));
    Assert.assertNotNull(JsonPath.read(responseStrg, "$.error.timestamp"));
    Assert.assertEquals(String.format("Sample not found for id %s", MockSampleServiceFactory.NON_EXISTING_SAMPLEID), JsonPath.read(responseStrg, "$.error.message"));

    response.close();
    client.close();
  }

  @Test
  public void findSampleBysampleIdUnCheckedException() {

    Client client = ClientBuilder.newClient();
    Response response = client.target(String.format("http://localhost:9998/samples/%s", MockSampleServiceFactory.UNCHECKED_EXCEPTION)).request().get();

    Assert.assertEquals(500, response.getStatus());
    String responseStrg = response.readEntity(String.class);
    
    Assert.assertEquals(CTPException.Fault.SYSTEM_ERROR.toString(), JsonPath.read(responseStrg, "$.error.code"));
    Assert.assertNotNull(JsonPath.read(responseStrg, "$.error.timestamp"));
    Assert.assertEquals(MockSurveyServiceFactory.OUR_EXCEPTION_MESSAGE, JsonPath.read(responseStrg, "$.error.message"));

    response.close();
    client.close();
  }
}
