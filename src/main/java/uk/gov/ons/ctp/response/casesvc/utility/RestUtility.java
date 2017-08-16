package uk.gov.ons.ctp.response.casesvc.utility;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

@Component
public class RestUtility {

  private RestClientConfig config;

  /**
   * Construct with no details of the server - will use the default provided by RestClientConfig
   */
  public RestUtility() {
    this.config = new RestClientConfig();
  }

  /**
   * Construct with the core details of the server
   *
   * @param clientConfig the configuration
   */
  public RestUtility(RestClientConfig clientConfig) {
    this.config = clientConfig;
  }

  public UriComponents createUriComponents(String path, MultiValueMap<String, String> queryParams,
      Object... pathParams) {
    UriComponents uriComponentsWithOutQueryParams = UriComponentsBuilder.newInstance()
        .scheme(config.getScheme())
        .host(config.getHost())
        .port(config.getPort())
        .path(path)
        .buildAndExpand(pathParams);

    // Have to build UriComponents for query parameters separately as Expand interprets braces in JSON query string
    // values as URI template variables to be replaced.
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .uriComponents(uriComponentsWithOutQueryParams)
        .queryParams(queryParams)
        .build()
        .encode();

    return uriComponents;
  }

  public <H> HttpEntity<H> createHttpEntity(H entity) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<H> httpEntity = new HttpEntity<H>(entity, headers);
    return httpEntity;
  }
}
