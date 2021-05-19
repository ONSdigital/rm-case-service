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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for SampleUnit complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SampleUnit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sampleUnitRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sampleUnitType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="partyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="collectionInstrumentId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actionPlanId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "SampleUnit",
    propOrder = {
      "id",
      "sampleUnitRef",
      "sampleUnitType",
      "partyId",
      "collectionInstrumentId",
      "activeEnrolment",
      "actionPlanId"
    })
@XmlSeeAlso({SampleUnitParent.class})
public class SampleUnit {

  protected String id;

  @XmlElement(required = true)
  protected String sampleUnitRef;

  @XmlElement(required = true)
  protected String sampleUnitType;

  protected String partyId;

  @XmlElement(required = true)
  protected String collectionInstrumentId;

  protected boolean activeEnrolment;

  protected String actionPlanId;

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setId(String value) {
    this.id = value;
  }

  /**
   * Gets the value of the sampleUnitRef property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitRef() {
    return sampleUnitRef;
  }

  /**
   * Sets the value of the sampleUnitRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitRef(String value) {
    this.sampleUnitRef = value;
  }

  /**
   * Gets the value of the sampleUnitType property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitType() {
    return sampleUnitType;
  }

  /**
   * Sets the value of the sampleUnitType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitType(String value) {
    this.sampleUnitType = value;
  }

  /**
   * Gets the value of the partyId property.
   *
   * @return possible object is {@link String }
   */
  public String getPartyId() {
    return partyId;
  }

  /**
   * Sets the value of the partyId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPartyId(String value) {
    this.partyId = value;
  }

  /**
   * Gets the value of the collectionInstrumentId property.
   *
   * @return possible object is {@link String }
   */
  public String getCollectionInstrumentId() {
    return collectionInstrumentId;
  }

  /**
   * Sets the value of the collectionInstrumentId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCollectionInstrumentId(String value) {
    this.collectionInstrumentId = value;
  }

  /**
   * Gets the value of the actionPlanId property.
   *
   * @return possible object is {@link String }
   */
  public String getActionPlanId() {
    return actionPlanId;
  }

  /**
   * Sets the value of the actionPlanId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActionPlanId(String value) {
    this.actionPlanId = value;
  }

  public boolean isActiveEnrolment() {
    return activeEnrolment;
  }

  public void setActiveEnrolment(boolean activeEnrolment) {
    this.activeEnrolment = activeEnrolment;
  }
}
