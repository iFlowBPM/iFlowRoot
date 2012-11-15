/**
 * Replenishment_System.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Replenishment_System implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Replenishment_System(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Purchase = "Purchase";
    public static final java.lang.String _Prod_Order = "Prod_Order";
    public static final java.lang.String __blank_ = "_blank_";
    public static final Replenishment_System Purchase = new Replenishment_System(_Purchase);
    public static final Replenishment_System Prod_Order = new Replenishment_System(_Prod_Order);
    public static final Replenishment_System _blank_ = new Replenishment_System(__blank_);
    public java.lang.String getValue() { return _value_;}
    public static Replenishment_System fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Replenishment_System enumeration = (Replenishment_System)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Replenishment_System fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Replenishment_System.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Replenishment_System"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
