package uk.gov.ons.ctp.response.casesvc.representation;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ContactDTO {

  public static final String EMAIL_RE = "^$|[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
  public static final String TELEPHONE_RE = "[\\d]{0,11}";
  public static final int TITLE_MAX_LEN = 20;
  public static final int FORENAME_MAX_LEN = 35;
  public static final int SURNAME_MAX_LEN = 35;

  @Size(min = 0, max = TITLE_MAX_LEN)
  private String title;

  @Size(min = 0, max = FORENAME_MAX_LEN)
  private String forename;
  
  @Size(min = 0, max = SURNAME_MAX_LEN)
  private String surname;
   
  @Pattern(regexp = TELEPHONE_RE)
  private String phoneNumber;

  @Pattern(regexp = EMAIL_RE)
  private String emailAddress;

}
