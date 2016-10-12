package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;

public interface CaseFeedbackReceiver {
  void process(CaseFeedback caseFeedback);
}
