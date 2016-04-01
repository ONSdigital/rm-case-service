
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmReservation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmReservation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confirmReservation" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}xmbConfirmReservation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmReservation", propOrder = {
    "confirmReservation"
})
public class ConfirmReservation {

    protected XmbConfirmReservation confirmReservation;

    /**
     * Gets the value of the confirmReservation property.
     * 
     * @return
     *     possible object is
     *     {@link XmbConfirmReservation }
     *     
     */
    public XmbConfirmReservation getConfirmReservation() {
        return confirmReservation;
    }

    /**
     * Sets the value of the confirmReservation property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmbConfirmReservation }
     *     
     */
    public void setConfirmReservation(XmbConfirmReservation value) {
        this.confirmReservation = value;
    }

}
