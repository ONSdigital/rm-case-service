
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteBooking complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteBooking">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deleteBooking" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}xmbDeleteBooking" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteBooking", propOrder = {
    "deleteBooking"
})
public class DeleteBooking {

    protected XmbDeleteBooking deleteBooking;

    /**
     * Gets the value of the deleteBooking property.
     * 
     * @return
     *     possible object is
     *     {@link XmbDeleteBooking }
     *     
     */
    public XmbDeleteBooking getDeleteBooking() {
        return deleteBooking;
    }

    /**
     * Sets the value of the deleteBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmbDeleteBooking }
     *     
     */
    public void setDeleteBooking(XmbDeleteBooking value) {
        this.deleteBooking = value;
    }

}
