package uk.gov.ons.ctp.response.action.export.domain;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of a message being sent.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ExportMessage {

  private Map<String, List<String>>  actionRequestIds;
  private Map<String, ByteArrayOutputStream> outputStreams;

  /**
   * Return actionIds for actionRequests sent in filename.
   * @param filename for which to get actionIds.
   * @return List of actionIds sent in filename.
   */
  public List<String> getActionRequestIds(String filename) {
    return actionRequestIds.get(filename);
  }

}
