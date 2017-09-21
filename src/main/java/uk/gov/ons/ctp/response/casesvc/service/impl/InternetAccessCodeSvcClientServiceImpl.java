package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
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

  @Autowired
  private RestTemplate restTemplate;

  @Qualifier("iacServiceRestUtility")
  @Autowired
  private RestUtility restUtility;

  @Retryable(value = {RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public List<String> generateIACs(int count) {
    UriComponents uriComponents = restUtility.createUriComponents(appConfig.getInternetAccessCodeSvc().getIacPostPath(),
        null);

    CreateInternetAccessCodeDTO createCodesDTO = new CreateInternetAccessCodeDTO(count, SYSTEM);
    HttpEntity<CreateInternetAccessCodeDTO> httpEntity = restUtility.createHttpEntity(createCodesDTO);

    log.debug("about to post to the IAC SVC with {}", createCodesDTO);
    ResponseEntity<String[]> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity,
        String[].class);
    return Arrays.asList(responseEntity.getBody());
  }

  @Retryable(value = {RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public void disableIAC(String iac) {
    log.debug("about to put to the IAC SVC with {}", iac);
    UriComponents uriComponents = restUtility.createUriComponents(appConfig.getInternetAccessCodeSvc().getIacPutPath(),
        null, iac);
    HttpEntity<UpdateInternetAccessCodeDTO> httpEntity = restUtility.createHttpEntity(
        new UpdateInternetAccessCodeDTO(SYSTEM));

    restTemplate.exchange(uriComponents.toUri(), HttpMethod.PUT, httpEntity, InternetAccessCodeDTO.class);
    log.debug("gone past the call to the IAC Svc...");
  }
}
