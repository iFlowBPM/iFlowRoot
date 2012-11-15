/**
 * Flushing_Method.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Flushing_Method implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Flushing_Method(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Manual = "Manual";
    public static final java.lang.String _Forward = "Forward";
    public static final java.lang.String _Backward = "Backward";
    public static final java.lang.String _Pick__x002B__Forward = "Pick__x002B__Forward";
    public static final java.lang.String _Pick__x002B__Backward = "Pick__x002B__Backward";
    public static final Flushing_Method Manual = new Flushing_Method(_Manual);
    public static final Flushing_Method Forward = new Flushing_Method(_Forward);
    public static final Flushing_Method Backward = new Flushing_Method(_Backward);
    public static final Flushing_Method Pick__x002B__Forward = new Flushing_Method(_Pick__x002B__Forward);
    public static final Flushing_Method Pick__x002B__Backward = new Flushing_Method(_Pick__x002B__Backward);
    public java.lang.String getValue() { return _value_;}
    public static Flushing_Method fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Flushing_Method enumeration = (Flushing_Method)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Flushing_Method fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Flushing_Method.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Flushing_Method"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
