package uk.gov.ons.ctp.response.casesvc.service.impl;

import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

/** The impl of the service which calls the IAC service via REST */
@Slf4j
@Service
public class InternetAccessCodeSvcClientServiceImpl implements InternetAccessCodeSvcClientService {

  private AppConfig appConfig;
  private RestTemplate restTemplate;
  private RestUtility restUtility;

  @Autowired
  public InternetAccessCodeSvcClientServiceImpl(
      AppConfig appConfig,
      RestTemplate restTemplate,
      @Qualifier("iacServiceRestUtility") RestUtility restUtility) {
    this.appConfig = appConfig;
    this.restTemplate = restTemplate;
    this.restUtility = restUtility;
  }

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public List<String> generateIACs(int count) {
    log.debug("Generating iac codes, count: {}", count);
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getInternetAccessCodeSvc().getIacPostPath(), null);

    CreateInternetAccessCodeDTO createCodesDTO = new CreateInternetAccessCodeDTO(count, SYSTEM);
    HttpEntity<CreateInternetAccessCodeDTO> httpEntity =
        restUtility.createHttpEntity(createCodesDTO);

    ResponseEntity<String[]> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity, String[].class);
    log.debug("Successfully generated iac codes, count: {}", count);
    return Arrays.asList(responseEntity.getBody());
  }

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public void disableIAC(String iac) {
    log.debug("Disabling iac code");
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getInternetAccessCodeSvc().getIacPutPath(), null, iac);
    HttpEntity<UpdateInternetAccessCodeDTO> httpEntity =
        restUtility.createHttpEntity(new UpdateInternetAccessCodeDTO(SYSTEM));

    restTemplate.exchange(
        uriComponents.toUri(), HttpMethod.PUT, httpEntity, InternetAccessCodeDTO.class);
    log.debug("Successfully disabled iac code");
  }

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public Boolean isIacActive(String iac) {
    log.debug("Checking if iac code is active");
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getInternetAccessCodeSvc().getIacPutPath(), null, iac);
    HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    ResponseEntity<InternetAccessCodeDTO> responseEntity =
        restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.GET, httpEntity, InternetAccessCodeDTO.class);
    return responseEntity.getBody().getActive();
  }
}
