/**
 * Reordering_Policy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Reordering_Policy implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Reordering_Policy(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String __blank_ = "_blank_";
    public static final java.lang.String _Fixed_Reorder_Qty = "Fixed_Reorder_Qty";
    public static final java.lang.String _Maximum_Qty = "Maximum_Qty";
    public static final java.lang.String _Order = "Order";
    public static final java.lang.String _Lot_for_Lot = "Lot_for_Lot";
    public static final Reordering_Policy _blank_ = new Reordering_Policy(__blank_);
    public static final Reordering_Policy Fixed_Reorder_Qty = new Reordering_Policy(_Fixed_Reorder_Qty);
    public static final Reordering_Policy Maximum_Qty = new Reordering_Policy(_Maximum_Qty);
    public static final Reordering_Policy Order = new Reordering_Policy(_Order);
    public static final Reordering_Policy Lot_for_Lot = new Reordering_Policy(_Lot_for_Lot);
    public java.lang.String getValue() { return _value_;}
    public static Reordering_Policy fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Reordering_Policy enumeration = (Reordering_Policy)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Reordering_Policy fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Reordering_Policy.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reordering_Policy"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
