package uk.gov.ons.ctp.response.casesvc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseReportRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseReportService;

@Service
@Slf4j
public class CaseReportServiceImpl implements CaseReportService {

    @Autowired
    private CaseReportRepository caseReportRepository;

    @Override
    public void createReport() {
        log.debug("Entering createReport...");
// TODO CTPA-1409        caseReportRepository.plus1inoutStoredProcedure();
    }
}
