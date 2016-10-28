package uk.gov.ons.ctp.response.action.export.domain;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;

/**
 * Mongo repository domain entity representing an ActionRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Document(collection = "actionRequest")
public class ActionRequestDocument {

  @Id
  private BigInteger actionId;
  private boolean responseRequired;
  private String actionPlan;
  private String actionType;
  private ActionAddress address;
  private String contactName;
  private BigInteger caseId;
  private Priority priority;
  private String caseRef;
  private String iac;
  private ActionEvent events;
  private Date dateStored;
  private Date dateSent;
}
