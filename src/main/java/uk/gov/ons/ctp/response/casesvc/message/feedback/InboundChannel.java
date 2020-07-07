package uk.gov.ons.ctp.response.casesvc.message.feedback;

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
