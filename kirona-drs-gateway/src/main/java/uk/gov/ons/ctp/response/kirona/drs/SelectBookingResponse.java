
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for selectBookingResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="selectBookingResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}xmbSelectBookingResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "selectBookingResponse", propOrder = {
    "_return"
})
public class SelectBookingResponse {

    @XmlElement(name = "return")
    protected XmbSelectBookingResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link XmbSelectBookingResponse }
     *     
     */
    public XmbSelectBookingResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmbSelectBookingResponse }
     *     
     */
    public void setReturn(XmbSelectBookingResponse value) {
        this._return = value;
    }

}
