/**
 * Price_Profit_Calculation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Price_Profit_Calculation implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Price_Profit_Calculation(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Profit_x003D_Price_Cost = "Profit_x003D_Price_Cost";
    public static final java.lang.String _Price_x003D_Cost_x002B_Profit = "Price_x003D_Cost_x002B_Profit";
    public static final java.lang.String _No_Relationship = "No_Relationship";
    public static final Price_Profit_Calculation Profit_x003D_Price_Cost = new Price_Profit_Calculation(_Profit_x003D_Price_Cost);
    public static final Price_Profit_Calculation Price_x003D_Cost_x002B_Profit = new Price_Profit_Calculation(_Price_x003D_Cost_x002B_Profit);
    public static final Price_Profit_Calculation No_Relationship = new Price_Profit_Calculation(_No_Relationship);
    public java.lang.String getValue() { return _value_;}
    public static Price_Profit_Calculation fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Price_Profit_Calculation enumeration = (Price_Profit_Calculation)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Price_Profit_Calculation fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Price_Profit_Calculation.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Price_Profit_Calculation"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
