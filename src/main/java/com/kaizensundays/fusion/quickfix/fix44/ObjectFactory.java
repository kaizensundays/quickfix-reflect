
package com.kaizensundays.fusion.quickfix.fix44;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.kaizensundays.fusion.quickfix.fix44 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Fix_QNAME = new QName("", "fix");
    private final static QName _FieldTypeComponent_QNAME = new QName("", "component");
    private final static QName _FieldTypeField_QNAME = new QName("", "field");
    private final static QName _FieldTypeValue_QNAME = new QName("", "value");
    private final static QName _FieldTypeGroup_QNAME = new QName("", "group");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.kaizensundays.fusion.quickfix.fix44
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FixType }
     * 
     */
    public FixType createFixType() {
        return new FixType();
    }

    /**
     * Create an instance of {@link ComponentType }
     * 
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link GroupType }
     * 
     */
    public GroupType createGroupType() {
        return new GroupType();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link MessagesType }
     * 
     */
    public MessagesType createMessagesType() {
        return new MessagesType();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link HeaderType }
     * 
     */
    public HeaderType createHeaderType() {
        return new HeaderType();
    }

    /**
     * Create an instance of {@link ComponentsType }
     * 
     */
    public ComponentsType createComponentsType() {
        return new ComponentsType();
    }

    /**
     * Create an instance of {@link TrailerType }
     * 
     */
    public TrailerType createTrailerType() {
        return new TrailerType();
    }

    /**
     * Create an instance of {@link FieldType }
     * 
     */
    public FieldType createFieldType() {
        return new FieldType();
    }

    /**
     * Create an instance of {@link FieldsType }
     * 
     */
    public FieldsType createFieldsType() {
        return new FieldsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FixType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fix")
    public JAXBElement<FixType> createFix(FixType value) {
        return new JAXBElement<FixType>(_Fix_QNAME, FixType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "component", scope = FieldType.class)
    public JAXBElement<ComponentType> createFieldTypeComponent(ComponentType value) {
        return new JAXBElement<ComponentType>(_FieldTypeComponent_QNAME, ComponentType.class, FieldType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "field", scope = FieldType.class)
    public JAXBElement<FieldType> createFieldTypeField(FieldType value) {
        return new JAXBElement<FieldType>(_FieldTypeField_QNAME, FieldType.class, FieldType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "value", scope = FieldType.class)
    public JAXBElement<ValueType> createFieldTypeValue(ValueType value) {
        return new JAXBElement<ValueType>(_FieldTypeValue_QNAME, ValueType.class, FieldType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "group", scope = FieldType.class)
    public JAXBElement<GroupType> createFieldTypeGroup(GroupType value) {
        return new JAXBElement<GroupType>(_FieldTypeGroup_QNAME, GroupType.class, FieldType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "component", scope = ComponentType.class)
    public JAXBElement<ComponentType> createComponentTypeComponent(ComponentType value) {
        return new JAXBElement<ComponentType>(_FieldTypeComponent_QNAME, ComponentType.class, ComponentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "field", scope = ComponentType.class)
    public JAXBElement<FieldType> createComponentTypeField(FieldType value) {
        return new JAXBElement<FieldType>(_FieldTypeField_QNAME, FieldType.class, ComponentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "group", scope = ComponentType.class)
    public JAXBElement<GroupType> createComponentTypeGroup(GroupType value) {
        return new JAXBElement<GroupType>(_FieldTypeGroup_QNAME, GroupType.class, ComponentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "component", scope = GroupType.class)
    public JAXBElement<ComponentType> createGroupTypeComponent(ComponentType value) {
        return new JAXBElement<ComponentType>(_FieldTypeComponent_QNAME, ComponentType.class, GroupType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "field", scope = GroupType.class)
    public JAXBElement<FieldType> createGroupTypeField(FieldType value) {
        return new JAXBElement<FieldType>(_FieldTypeField_QNAME, FieldType.class, GroupType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "group", scope = GroupType.class)
    public JAXBElement<GroupType> createGroupTypeGroup(GroupType value) {
        return new JAXBElement<GroupType>(_FieldTypeGroup_QNAME, GroupType.class, GroupType.class, value);
    }

}
