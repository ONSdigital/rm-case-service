package uk.gov.ons.ctp.response.casesvc.message.notification;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for NotificationType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="NotificationType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ACTIVATED"/&gt;
 *     &lt;enumeration value="REPLACED"/&gt;
 *     &lt;enumeration value="DEACTIVATED"/&gt;
 *     &lt;enumeration value="DISABLED"/&gt;
 *     &lt;enumeration value="ACTIONPLAN_CHANGED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "NotificationType")
@XmlEnum
public enum NotificationType {
  ACTIVATED,
  REPLACED,
  DEACTIVATED,
  DISABLED,
  ACTIONPLAN_CHANGED;

  public String value() {
    return name();
  }

  public static NotificationType fromValue(String v) {
    return valueOf(v);
  }
}
