package uk.gov.ons.ctp.response.action.export.templating.freemarker.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Document
public class FreeMarkerTemplate {
  @Id
  private String name;
  private String content;
  private Date dateModified;
}
