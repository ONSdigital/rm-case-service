package uk.gov.ons.ctp.response.caseframe.utility;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.springframework.http.HttpStatus;

import com.jayway.jsonpath.JsonPath;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jackson.JacksonConfigurator;
import uk.gov.ons.ctp.common.jaxrs.CTPExceptionMapper;
import uk.gov.ons.ctp.common.jaxrs.GeneralExceptionMapper;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;

/**
 * An abstract base class for CTP Unit Tests. This class attempts to distill into util methods the repetetetetetive drudgery
 * of cookie cutter, mechanical jersey unit test code.
 * It provides a DSL or fluent API. Start with(etc).assertThis(etc).assertOther(etc etc).assertEtc
 * 
 */
public abstract class CTPJerseyTest extends JerseyTest {
  
  private static final String ERROR_CODE = "$.error.code";
  private static final String ERROR_TIMESTAMP = "$.error.timestamp";
  private static final String ERROR_MESSAGE = "$.error.message";

  
  @SuppressWarnings("rawtypes")
  public Application init(final Class endpointClass, final Class serviceClass, final Class factoryClass) {
    ResourceConfig config = new ResourceConfig(endpointClass);

    AbstractBinder binder = new AbstractBinder() {
      @SuppressWarnings("unchecked")
      @Override
      protected void configure() {
        bindFactory(factoryClass).to(serviceClass);
        bind(new CaseFrameBeanMapper()).to(MapperFacade.class);
      }
    };
    config.register(binder);

    config.register(CTPExceptionMapper.class);
    config.register(GeneralExceptionMapper.class);
    config.register(JacksonConfigurator.class);
    return config;
  }

  protected TestableResponse with(String url, Object... args) {
    return new TestableResponse(String.format(url, args));
  }

  @RequiredArgsConstructor
  protected static class TestableResponse {
    @NonNull
    private String url;
    
    private Client client;
    private Response response;
    private String responseStr;
    private String bodyStr;
    private Operation operation = Operation.GET;
    private enum Operation {
      GET, PUT, POST;
    }

    public TestableResponse put(String body) {
      bodyStr = body;
      operation = Operation.PUT;
      return this;
    }
    
    public TestableResponse post(String body) {
      bodyStr = body;
      operation = Operation.POST;
      return this;
    }

    public TestableResponse assertResponseCodeIs(HttpStatus expectedStatus) {
      Assert.assertEquals(expectedStatus.value(), getResponse().getStatus());
      return this;
    }

    public TestableResponse assertResponseLengthIs(int value) {
      Assert.assertEquals(value, getResponse().getLength());
      return this;
    }

    public TestableResponse assertFaultIs(CTPException.Fault fault) {
      Assert.assertEquals(fault.toString(), JsonPath.read(getResponseString(), ERROR_CODE));
      return this;
    }

    public TestableResponse assertTimestampExists() {
      Assert.assertNotNull(JsonPath.read(getResponseString(), ERROR_TIMESTAMP));
      return this;
    }

    public TestableResponse assertMessageEquals(String message, Object... args) {
      Assert.assertEquals(String.format(message, args), JsonPath.read(getResponseString(), ERROR_MESSAGE));
      return this;
    }

    public TestableResponse assertArrayLengthInBodyIs(int value) {
      Assert.assertEquals(new Integer(value), JsonPath.parse(getResponseString()).read("$.length()", Integer.class));
      return this;
    }
    
    public TestableResponse assertIntegerInBody(String path, int value) {
      Assert.assertEquals(new Integer(value), JsonPath.parse(getResponseString()).read(path, Integer.class));
      return this;
    }

    public TestableResponse assertIntegerListInBody(String path, Integer... integers) {
      List<Integer> integersList = JsonPath.parse(getResponseString()).read(path);
      Assert.assertThat(integersList, containsInAnyOrder(integers));
      return this;
    }

    public TestableResponse assertDoubleInBody(String path, double value) {
      Assert.assertEquals(new Double(value), JsonPath.parse(getResponseString()).read(path, Double.class));
      return this;
    }

    public TestableResponse assertDoubleListInBody(String path, Double... doubles) {
      List<Double> doublesList = JsonPath.parse(getResponseString()).read(path);
      Assert.assertThat(doublesList, containsInAnyOrder(doubles));
      return this;
    }
    
    public TestableResponse assertStringInBody(String path, String value) {
      Assert.assertEquals(value, JsonPath.parse(getResponseString()).read(path, String.class));
      return this;
    }
    
    public TestableResponse assertStringListInBody(String path, String... strs) {
      List<String> strList = JsonPath.parse(getResponseString()).read(path);
      Assert.assertThat(strList, containsInAnyOrder(strs));
      return this;
    }
    
    public TestableResponse assertIntegerOccursThroughoutListInBody(String path, int value) {
      List<Integer> integerList = JsonPath.parse(getResponseString()).read(path);
      Assert.assertThat(integerList, everyItem(equalTo(value)));
      return this;
    }

    public TestableResponse assertStringOccursThroughoutListInBody(String path, String value) {
      List<String> stringList = JsonPath.parse(getResponseString()).read(path);
      Assert.assertThat(stringList, everyItem(equalTo(value)));
      return this;
    }

    public void andClose() {
      response.close();
      client.close();
    }
    
    /**
     * Client, Response and ResponseStr are chickens and eggs,
     * The TestableResponse is constructed with url only.
     * Before we can get ResponseStr,
     * we need to get Response,
     * Before we can get Response,
     * we need to get Client.
     * Nested lazy loading!
     * @return
     */
    private Client getClient() {
        if (client == null) {
          client = ClientBuilder.newClient();
        }
        return client;
    }

    private Response getResponse() {
      if (response == null) {
        Builder builder = getClient().target(url).request();
        switch (operation) {
          case GET:
            response = builder.get();
            break;
          case PUT:
            response = builder.put(Entity.json(bodyStr));
            break;
          case POST:
            response = builder.post(Entity.json(bodyStr));
            break;
        }
      }
      return response;
    }

    private String getResponseString() {
      if (responseStr == null) {
        responseStr = getResponse().readEntity(String.class);
      }
      return responseStr;
    }
  }
}
