package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;

/**
 * Created by Martin.Humphrey on 17/2/16.
 */
public interface QuestionSetService extends CTPService {

  List<QuestionSet> findQuestionSets();

  QuestionSet findQuestionSetByQuestionSet(String questionSet);
}
