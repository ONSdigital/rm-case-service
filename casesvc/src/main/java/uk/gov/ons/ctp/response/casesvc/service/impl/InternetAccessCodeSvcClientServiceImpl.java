package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.iac.representation.CreateInternetAccessCodeDTO;

/**
 * The impl of the service which calls the IAC service via REST
 *
 */
@Slf4j
@Named
public class InternetAccessCodeSvcClientServiceImpl implements InternetAccessCodeSvcClientService {

  // TODO centralize this!
  public static final String SYSTEM = "System";
  
  @Inject
  private AppConfig appConfig;

  @Inject
  @Qualifier("internetAccessCodeServiceClient")
  private RestClient internetAccessClientServiceClient;

  /* (non-Javadoc)
   * @see uk.gov.ons.ctp.response.casesvc.service.impl.InternetAccessCodeSvcClient#generateIACs(int)
   */
  @Override
  public List<String> generateIACs(int count) {
      CreateInternetAccessCodeDTO createCodesDTO = new CreateInternetAccessCodeDTO(count, SYSTEM);
      log.debug("about to post to the IAC SVC with {}", createCodesDTO);
      String[] codes = internetAccessClientServiceClient.postResource(appConfig.getInternetAccessCodeSvc().getIacPostPath(), createCodesDTO, String[].class);
      return Arrays.asList(codes);
  }
}
