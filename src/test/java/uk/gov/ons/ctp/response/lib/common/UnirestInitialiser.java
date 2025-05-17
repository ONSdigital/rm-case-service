package uk.gov.ons.ctp.response.lib.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import kong.unirest.core.Unirest;

public class UnirestInitialiser {
  public static void initialise() {

    ObjectMapper objectMapper = new ObjectMapper();

    Unirest.config()
        .connectTimeout(1000)
        .setDefaultHeader("Accept", "application/json")
        .followRedirects(false)
        .enableCookieManagement(false)
        .setObjectMapper(
            new kong.unirest.core.ObjectMapper() {
              public <T> T readValue(final String value, final Class<T> valueType) {
                try {
                  return objectMapper.readValue(value, valueType);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }

              public String writeValue(final Object value) {
                try {
                  return objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }
            });
  }
}
