
package com.kaizensundays.fusion.quickfix.fix44;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="field" type="{}fieldType"/>
 *         &lt;element name="group" type="{}groupType"/>
 *         &lt;element name="component" type="{}componentType"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="msgtype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="msgcat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageType", propOrder = {
    "fieldOrGroupOrComponent"
})
public class MessageType {

    @XmlElements({
        @XmlElement(name = "field", type = FieldType.class),
        @XmlElement(name = "group", type = GroupType.class),
        @XmlElement(name = "component", type = ComponentType.class)
    })
    protected List<Object> fieldOrGroupOrComponent;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "msgtype")
    protected String msgtype;
    @XmlAttribute(name = "msgcat")
    protected String msgcat;

    /**
     * Gets the value of the fieldOrGroupOrComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldOrGroupOrComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldOrGroupOrComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldType }
     * {@link GroupType }
     * {@link ComponentType }
     * 
     * 
     */
    public List<Object> getFieldOrGroupOrComponent() {
        if (fieldOrGroupOrComponent == null) {
            fieldOrGroupOrComponent = new ArrayList<Object>();
        }
        return this.fieldOrGroupOrComponent;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the msgtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgtype() {
        return msgtype;
    }

    /**
     * Sets the value of the msgtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgtype(String value) {
        this.msgtype = value;
    }

    /**
     * Gets the value of the msgcat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgcat() {
        return msgcat;
    }

    /**
     * Sets the value of the msgcat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgcat(String value) {
        this.msgcat = value;
    }

}
