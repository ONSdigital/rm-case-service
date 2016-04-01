
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkAvailTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="checkAvailTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="standard"/>
 *     &lt;enumeration value="longPeriod"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "checkAvailTypeType")
@XmlEnum
public enum CheckAvailTypeType {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("standard")
    STANDARD("standard"),
    @XmlEnumValue("longPeriod")
    LONG_PERIOD("longPeriod");
    private final String value;

    CheckAvailTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CheckAvailTypeType fromValue(String v) {
        for (CheckAvailTypeType c: CheckAvailTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
