package uk.gov.ons.ctp.response.casesvc.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseTypeDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

/**
 * The REST endpoint controller for CaseType
 */
@RestController
@RequestMapping(value = "/casetypes", produces = "application/json")
@Slf4j
public final class CaseTypeEndpoint implements CTPEndpoint {

  @Autowired
  private CaseTypeService caseTypeService;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to find a casetype by id
   * @param caseTypeId to find by
   * @return the casetype or null if not found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{casetypeid}", method = RequestMethod.GET)
  public CaseTypeDTO findCaseTypeByCaseTypeId(@PathVariable("casetypeid") final Integer caseTypeId) throws CTPException {
    log.info("Entering findCaseTypeByCaseTypeId with {}", caseTypeId);
    CaseType caseType = caseTypeService.findCaseTypeByCaseTypeId(caseTypeId);
    if (caseType == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "CaseType not found for id %s", caseTypeId);
    }
    return mapperFacade.map(caseType, CaseTypeDTO.class);
  }

}
