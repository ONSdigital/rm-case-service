package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.iac.representation.CreateInternetAccessCodeDTO;
import uk.gov.ons.ctp.response.iac.representation.InternetAccessCodeDTO;
import uk.gov.ons.ctp.response.iac.representation.UpdateInternetAccessCodeDTO;

import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * The impl of the service which calls the IAC service via REST
 *
 */
@Slf4j
@Service
public class InternetAccessCodeSvcClientServiceImpl implements InternetAccessCodeSvcClientService {

  @Autowired
  private AppConfig appConfig;

  // TODO remove
  @Autowired
  @Qualifier("internetAccessCodeServiceClient")
  private RestClient internetAccessClientServiceClient;

  @Override
  public List<String> generateIACs(int count) {
    CreateInternetAccessCodeDTO createCodesDTO = new CreateInternetAccessCodeDTO(count, SYSTEM);
    log.debug("about to post to the IAC SVC with {}", createCodesDTO);
    String[] codes = internetAccessClientServiceClient
        .postResource(appConfig.getInternetAccessCodeSvc().getIacPostPath(), createCodesDTO, String[].class);
    return Arrays.asList(codes);
  }

  @Override
  public void disableIAC(String iac) {
    log.error("about to put to the IAC SVC with {}", iac);
    UriComponents uriComponents = createUriComponents("/iacs/{iac}", null, iac);
    HttpEntity<UpdateInternetAccessCodeDTO> httpEntity = createHttpEntity(
        new UpdateInternetAccessCodeDTO("SYSTEM"));

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.exchange(uriComponents.toUri(), HttpMethod.PUT, httpEntity, InternetAccessCodeDTO.class);
    log.error("gone past the call to the IAC Svc...");
  }

  private UriComponents createUriComponents(String path, MultiValueMap<String, String> queryParams,
      Object... pathParams) {
    UriComponents uriComponentsWithOutQueryParams = UriComponentsBuilder.newInstance()
        .scheme("http")
        .host("iacsvc.apps.devtest.onsclofo.uk")
        .port(80)
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

  private <H> HttpEntity<H> createHttpEntity(H entity) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<H> httpEntity = new HttpEntity<H>(entity, headers);
    return httpEntity;
  }
}
