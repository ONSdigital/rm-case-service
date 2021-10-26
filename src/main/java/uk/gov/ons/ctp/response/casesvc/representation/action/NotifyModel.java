package uk.gov.ons.ctp.response.casesvc.representation.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotifyModel {

  private Notify notify;

  @Getter
  @Builder
  @Setter
  public static class Notify {

    @JsonProperty("email_address")
    private String emailAddress;

    private Classifiers classifiers;

    private Personalisation personalisation;

    @JsonProperty("reference")
    private String reference;

    @Getter
    @Setter
    @Builder
    public static class Personalisation {

      @JsonProperty("reporting unit reference")
      private String reportingUnitReference;

      @JsonProperty("survey id")
      private String surveyId;

      @JsonProperty("survey name")
      private String surveyName;

      private String firstname;
      private String lastname;

      @JsonProperty("return by date")
      private String returnByDate;

      @JsonProperty("RU name")
      private String ruName;

      @JsonProperty("trading style")
      private String tradingSyle;

      @JsonProperty("respondent period")
      private String respondentPeriod;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Classifiers {

      @JsonProperty("communication_type")
      @JsonInclude(Include.NON_NULL)
      private String actionType;

      @JsonProperty("survey")
      @JsonInclude(Include.NON_NULL)
      private String surveyRef;

      @JsonProperty("region")
      @JsonInclude(Include.NON_NULL)
      private String region;

      @JsonProperty("legal_basis")
      @JsonInclude(Include.NON_NULL)
      private String legalBasis;

      public static ClassifiersBuilder builder() {
        return new ClassifiersBuilder();
      }

      public static class ClassifiersBuilder {

        private static final String REMINDER_EMAIL = "BSRE";
        private static final String NUDGE_EMAIL = "BSNUE";
        private static final String NUDGE = "NUDGE";
        private static final String NOTIFICATION_EMAIL = "BSNE";
        private static final String REMINDER = "REMINDER";
        private static final String NOTIFICATION = "NOTIFICATION";
        private static final String COVID_SURVEY_ID = "283";
        // a region of YY appears to be any region that isnt england
        private static final String NOT_ENGLAND = "YY";

        private String actionType;
        private String legalBasis;
        private String region;
        private String surveyRef;

        /*
         * This is lifted directly from the old notify-gateway and comms-template.
         */
        public Classifiers build() {
          if (NUDGE_EMAIL.equals(actionType)) {
            actionType = NUDGE;
            return new Classifiers(actionType, "", "", "");
          }

          // if not not england is england. welcome to ras-rm.
          if (!NOT_ENGLAND.equals(region)) {
            region = "";
          }

          if (!COVID_SURVEY_ID.equals(surveyRef)) {
            surveyRef = "";
          }

          if (NOTIFICATION_EMAIL.equalsIgnoreCase(actionType)) {
            actionType = NOTIFICATION;
          } else if (REMINDER_EMAIL.equalsIgnoreCase(actionType)) {
            actionType = REMINDER;
          }
          return new Classifiers(actionType, surveyRef, region, legalBasis);
        }

        public ClassifiersBuilder actionType(String actionType) {
          this.actionType = actionType;
          return this;
        }

        public ClassifiersBuilder legalBasis(String legalBasis) {
          this.legalBasis = legalBasis;
          return this;
        }

        public ClassifiersBuilder surveyRef(String surveyRef) {
          this.surveyRef = surveyRef;
          return this;
        }

        public ClassifiersBuilder region(String region) {
          this.region = region;
          return this;
        }
      }
    }
  }
}
