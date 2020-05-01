package uk.gov.ons.ctp.response.casesvc.message.feedback;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for InboundChannel.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="InboundChannel"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OFFLINE"/&gt;
 *     &lt;enumeration value="ONLINE"/&gt;
 *     &lt;enumeration value="PAPER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "InboundChannel")
@XmlEnum
public enum InboundChannel {
  OFFLINE,
  ONLINE,
  PAPER;

  public String value() {
    return name();
  }

  public static InboundChannel fromValue(String v) {
    return valueOf(v);
  }
}
