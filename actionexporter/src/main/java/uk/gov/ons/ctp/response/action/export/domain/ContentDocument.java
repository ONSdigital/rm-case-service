package uk.gov.ons.ctp.response.action.export.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Abstract Mongo repository domain entity representing any Content
 */
@Data
public abstract class ContentDocument {
  @Id
  private String name;
  private String content;
  private Date dateModified;
}
