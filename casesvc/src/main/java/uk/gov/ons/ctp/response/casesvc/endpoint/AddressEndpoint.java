package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Address;
import uk.gov.ons.ctp.response.casesvc.representation.AddressDTO;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;

/**
 * The REST endpoint controller for Addresses
 */
@Path("/addresses")
@Produces({ "application/json" })
@Slf4j
public final class AddressEndpoint implements CTPEndpoint {

  @Inject
  private AddressService addressService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to find addresses by UPRN
   * @param uprn the uprn to find by
   * @return the DTO representation
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{uprn}")
  public AddressDTO findAddressesByUprn(@PathParam("uprn") final Long uprn) throws CTPException {
    log.info("Entering findAddressesByUprn with {}", uprn);

    Address address = addressService.findByUprn(uprn);
    log.debug("address = {}", address);
    if (address == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "No addresses found for uprn %d", uprn);
    }

    return mapperFacade.map(address, AddressDTO.class);
  }

  /**
   * the GET endpoint to find addresses by postcode
   * @param postcode to find by
   * @return the addresses found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/postcode/{postcode}")
  public List<AddressDTO> findAddressesByPostcode(@PathParam("postcode") final String postcode) throws CTPException {
    log.info("Entering findAddressesByPostcode with {}", postcode);

    List<Address> addresses = addressService.findByPostcode(postcode);
    log.debug("addresses = {}", addresses);
    if (addresses == null || addresses.isEmpty()) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "No addresses found for postcode %s", postcode);
    }

    return mapperFacade.mapAsList(addresses, AddressDTO.class);
  }

}
