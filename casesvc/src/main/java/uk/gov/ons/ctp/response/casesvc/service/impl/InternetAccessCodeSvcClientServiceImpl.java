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
import uk.gov.ons.ctp.response.iac.representation.InternetAccessCodeDTO;
import uk.gov.ons.ctp.response.iac.representation.UpdateInternetAccessCodeDTO;

import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * The impl of the service which calls the IAC service via REST
 *
 */
@Slf4j
@Named
public class InternetAccessCodeSvcClientServiceImpl implements InternetAccessCodeSvcClientService {

  @Inject
  private AppConfig appConfig;

  @Inject
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
  public InternetAccessCodeDTO disableIAC(String iac) {
    log.debug("about to put to the IAC SVC with {}", iac);
    UpdateInternetAccessCodeDTO updateInternetAccessCodeDTO = new UpdateInternetAccessCodeDTO("SYSTEM");
    InternetAccessCodeDTO internetAccessCodeDTO = internetAccessClientServiceClient
        .putResource(appConfig.getInternetAccessCodeSvc().getIacPutPath(), updateInternetAccessCodeDTO, InternetAccessCodeDTO.class, iac);
    return internetAccessCodeDTO;
  }

}
