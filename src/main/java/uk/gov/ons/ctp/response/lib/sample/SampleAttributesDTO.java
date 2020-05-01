package uk.gov.ons.ctp.response.lib.sample;

import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleAttributesDTO {

  private UUID id;

  private Map<String, String> attributes;
}
