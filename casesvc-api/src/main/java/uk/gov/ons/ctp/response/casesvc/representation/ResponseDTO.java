package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.DATE_FORMAT_IN_JSON;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ResponseDTO {

  private String inboundChannel;

  @JsonFormat(pattern = DATE_FORMAT_IN_JSON)
  private Date dateTime;

}
