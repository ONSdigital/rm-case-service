//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2020.04.30 at 05:48:38 PM BST
//

package uk.gov.ons.ctp.response.lib.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for SampleUnitParent complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SampleUnitParent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ons.gov.uk/ctp/response/casesvc/message/sampleunitnotification}SampleUnit">
 *       &lt;sequence>
 *         &lt;element name="collectionExerciseId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sampleUnitChildren" type="{http://ons.gov.uk/ctp/response/casesvc/message/sampleunitnotification}SampleUnitChildren" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "SampleUnitParent",
    propOrder = {"collectionExerciseId", "sampleUnitChildren"})
public class SampleUnitParent extends SampleUnit {

  @XmlElement(required = true)
  protected String collectionExerciseId;

  protected SampleUnitChildren sampleUnitChildren;

  /**
   * Gets the value of the collectionExerciseId property.
   *
   * @return possible object is {@link String }
   */
  public String getCollectionExerciseId() {
    return collectionExerciseId;
  }

  /**
   * Sets the value of the collectionExerciseId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCollectionExerciseId(String value) {
    this.collectionExerciseId = value;
  }

  /**
   * Gets the value of the sampleUnitChildren property.
   *
   * @return possible object is {@link SampleUnitChildren }
   */
  public SampleUnitChildren getSampleUnitChildren() {
    return sampleUnitChildren;
  }

  /**
   * Sets the value of the sampleUnitChildren property.
   *
   * @param value allowed object is {@link SampleUnitChildren }
   */
  public void setSampleUnitChildren(SampleUnitChildren value) {
    this.sampleUnitChildren = value;
  }
}