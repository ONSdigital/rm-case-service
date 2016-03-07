package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;
import uk.gov.ons.ctp.response.caseframe.representation.LocalAuthorityDTO;
import uk.gov.ons.ctp.response.caseframe.representation.RegionDTO;
import uk.gov.ons.ctp.response.caseframe.service.RegionService;

/**
 * The REST endpoint controller for CaseFrame Regions
 */
@Path("/regions")
@Produces({ "application/json" })
@Slf4j
public final class RegionEndpoint implements CTPEndpoint {

  @Inject
  private RegionService regionService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/")
  public List<RegionDTO> findAllRegions() {
    log.debug("Entering findAllRegions...");
    List<Region> regions = regionService.findAll();
    List<RegionDTO> regionDTOs = mapperFacade.mapAsList(regions, RegionDTO.class);
    return CollectionUtils.isEmpty(regionDTOs) ? null : regionDTOs;
  }

  @GET
  @Path("/{regionid}")
  public RegionDTO findRegionById(@PathParam("regionid") String regionId) throws CTPException {
    log.debug("Entering findRegionById with {}", regionId);
    Region region = regionService.findById(regionId);
    if (region == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Region not found for id %s", regionId);
    }
    return mapperFacade.map(region, RegionDTO.class);
  }

  @GET
  @Path("/{regionid}/lads")
  public List<LocalAuthorityDTO> findAllLadsForRegionId(@PathParam("regionid") String regionId) {
    log.debug("Entering findAllLadsByRegionid with {}", regionId);
    List<LocalAuthority> localAuthorities = regionService.findAllLadsByRegionid(regionId);
    List<LocalAuthorityDTO> localAuthorityDTOs = mapperFacade.mapAsList(localAuthorities, LocalAuthorityDTO.class);
    return CollectionUtils.isEmpty(localAuthorityDTOs) ? null : localAuthorityDTOs;
  }

}
