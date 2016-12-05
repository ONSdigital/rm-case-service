package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.response.iac.representation.InternetAccessCodeDTO;

public interface InternetAccessCodeSvcClientService {

  List<String> generateIACs(int count);

  InternetAccessCodeDTO disableIAC(String iac);
  
  //TODO: add method call

}