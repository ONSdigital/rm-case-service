package uk.gov.ons.ctp.response.casesvc.message.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.definition.CaseCreation;
import uk.gov.ons.ctp.response.casesvc.message.CaseCreationReceiver;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * The implementation of the SampleService
 *
 */
@MessageEndpoint
@Slf4j
public class CaseCreationReceiverImpl implements CaseCreationReceiver
{
	@Autowired
	private CaseService caseService;
	
	@Override
	@ServiceActivator(inputChannel = "caseTransformed", adviceChain = "caseRetryAdvice")
	public void process(CaseCreation caseCreation) 
	{
		log.info("received from queue" + caseCreation.getCaseId());
		caseService.createInitialCase(caseCreation);
		log.info("done");
	}
}
