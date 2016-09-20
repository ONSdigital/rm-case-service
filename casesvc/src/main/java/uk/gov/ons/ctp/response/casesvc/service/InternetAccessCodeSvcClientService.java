package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

public interface InternetAccessCodeSvcClientService {

  List<String> generateIACs(int count);

}