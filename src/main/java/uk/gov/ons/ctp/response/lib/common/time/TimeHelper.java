package uk.gov.ons.ctp.response.lib.common.time;

import java.sql.Timestamp;

/**
 * This interface is to provide a layer of separation
 * between the service classes and DateTimeUtil.
 *
 * This will aid us in incrementally removing the
 * XMLGregorianCalendar from the services.
 */
public interface TimeHelper {

    /**
     * Get the current time.
     * @return Timestamp
     */
    Timestamp getNowUTC();
}
