/**
 * Sub_Type_Item.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Sub_Type_Item implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Sub_Type_Item(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String __blank_ = "_blank_";
    public static final java.lang.String _Item = "Item";
    public static final java.lang.String _Tax = "Tax";
    public static final java.lang.String _Seale = "Seale";
    public static final java.lang.String _Service = "Service";
    public static final Sub_Type_Item _blank_ = new Sub_Type_Item(__blank_);
    public static final Sub_Type_Item Item = new Sub_Type_Item(_Item);
    public static final Sub_Type_Item Tax = new Sub_Type_Item(_Tax);
    public static final Sub_Type_Item Seale = new Sub_Type_Item(_Seale);
    public static final Sub_Type_Item Service = new Sub_Type_Item(_Service);
    public java.lang.String getValue() { return _value_;}
    public static Sub_Type_Item fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Sub_Type_Item enumeration = (Sub_Type_Item)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Sub_Type_Item fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Sub_Type_Item.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sub_Type_Item"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
