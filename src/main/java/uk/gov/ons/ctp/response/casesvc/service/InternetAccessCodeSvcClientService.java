package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

/** The Service to generate/disable IACs */
public interface InternetAccessCodeSvcClientService {

  /**
   * To generate IACs
   *
   * @param count the number of IACs to generate
   * @return a list of IACs
   */
  List<String> generateIACs(int count);

  /**
   * To disable an IAC
   *
   * @param iac the one to disable
   */
  void disableIAC(String iac);

  /**
   * To determine if a given iac is active
   *
   * @param iac code to check active status of
   * @return boolean true if iac is active, false if not
   */
  Boolean isIacActive(String iac);
}
