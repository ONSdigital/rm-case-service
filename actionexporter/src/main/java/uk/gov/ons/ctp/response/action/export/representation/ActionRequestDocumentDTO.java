package uk.gov.ons.ctp.response.action.export.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;

import java.math.BigInteger;
import java.util.Date;

/**
 * Representation of ActionRequestDocument
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionRequestDocumentDTO {
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
