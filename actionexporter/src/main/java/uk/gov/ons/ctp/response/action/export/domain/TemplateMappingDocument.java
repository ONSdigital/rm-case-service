package uk.gov.ons.ctp.response.action.export.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Mongo repository domain entity representing a Template Mapping.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Document
public class TemplateMappingDocument extends ContentDocument {
  private TemplateEngine templateEngine;
}
