package uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification package.
 *
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema
 * derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification
   */
  public ObjectFactory() {}

  /** Create an instance of {@link SampleUnitParent } */
  public SampleUnitParent createSampleUnitParent() {
    return new SampleUnitParent();
  }

  /** Create an instance of {@link SampleUnit } */
  public SampleUnit createSampleUnit() {
    return new SampleUnit();
  }

  /** Create an instance of {@link SampleUnitChildren } */
  public SampleUnitChildren createSampleUnitChildren() {
    return new SampleUnitChildren();
  }
}
