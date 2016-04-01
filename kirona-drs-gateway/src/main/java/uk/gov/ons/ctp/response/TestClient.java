package uk.gov.ons.ctp.response;

import uk.gov.ons.ctp.response.kirona.drs.SOAP;
import uk.gov.ons.ctp.response.kirona.drs.SOAP_Service;
import uk.gov.ons.ctp.response.kirona.drs.XmbCreateOrder;
import uk.gov.ons.ctp.response.kirona.drs.XmbCreateOrderResponse;

/**
 * This is a basic client to verify that we can create an order on the Kirona's side
 */
public class TestClient {
  public static void main(String[] args) {
    SOAP_Service soapService = new SOAP_Service();
    SOAP soap = soapService.getSOAPImplPort();
    XmbCreateOrder order = new XmbCreateOrder();
    XmbCreateOrderResponse response = soap.createOrder(order);
    System.out.println("response = " + response);
  }
}
