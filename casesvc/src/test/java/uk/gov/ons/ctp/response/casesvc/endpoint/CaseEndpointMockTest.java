package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

@RunWith(MockitoJUnitRunner.class)
public class CaseEndpointMockTest {

	@InjectMocks
	CaseEndpoint caseEndpoint;

	@Mock
	CaseService caseService;

	@Mock
	CategoryService categoryService;

	@Mock
	private MapperFacade mapperFacade;

	@Test(expected = CTPException.class)
	public void whenACaseObjectIsNull_confirmThatACTPExceptionIsThrown() throws CTPException {
		Mockito.when(caseService.findCaseByIac(any())).thenReturn(null);

		caseEndpoint.findCaseByIac("dsdysdrsd");
	}

	@Test
	public void whenAValidIacIsUsed_ConfirmThatACaseEventIsCreated() throws CTPException {

		Case newCase = new Case();
		newCase.setCaseId(1234);

		Category newCategory = new Category();
		newCategory.setShortDescription("desc");

		CaseEvent caseEvent = new CaseEvent();

		Mockito.when(caseService.findCaseByIac(any())).thenReturn(newCase);
		Mockito.when(categoryService.findCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED)).thenReturn(newCategory);
		Mockito.when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class))).thenReturn(caseEvent);
		Mockito.when(mapperFacade.map(any(), any())).thenReturn(new CaseDTO());

		caseEndpoint.findCaseByIac("1234");

		verify(caseService).createCaseEvent(any(CaseEvent.class), any(Case.class));

	}

}
