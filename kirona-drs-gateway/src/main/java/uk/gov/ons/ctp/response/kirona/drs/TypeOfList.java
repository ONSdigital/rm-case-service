
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeOfList.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeOfList">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="notaList"/>
 *     &lt;enumeration value="closedList"/>
 *     &lt;enumeration value="openList"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeOfList")
@XmlEnum
public enum TypeOfList {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("notaList")
    NOTA_LIST("notaList"),
    @XmlEnumValue("closedList")
    CLOSED_LIST("closedList"),
    @XmlEnumValue("openList")
    OPEN_LIST("openList");
    private final String value;

    TypeOfList(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeOfList fromValue(String v) {
        for (TypeOfList c: TypeOfList.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
