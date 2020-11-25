package uk.gov.ons.ctp.response.lib.common.health;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import lombok.Data;

/** Simple abstract class for the scheduled tasks providing info to the health endpoint to use */
@Data
public abstract class ScheduledHealthInfo {
  private String lastRunTime;

  /** The constructor */
  public ScheduledHealthInfo() {
    DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
    lastRunTime = dateTimeInstance.format(Calendar.getInstance().getTime());
  }
}
