/**
 * WS_ReqInternaMaterial_Fields.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_ReqInternaMaterial_Fields implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WS_ReqInternaMaterial_Fields(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _No = "No";
    public static final java.lang.String _Service = "Service";
    public static final java.lang.String _Ship_to_Name = "Ship_to_Name";
    public static final java.lang.String _Ship_to_Address = "Ship_to_Address";
    public static final java.lang.String _Ship_to_Address_2 = "Ship_to_Address_2";
    public static final java.lang.String _Ship_to_Post_Code = "Ship_to_Post_Code";
    public static final java.lang.String _Ship_to_City = "Ship_to_City";
    public static final java.lang.String _Ship_to_County = "Ship_to_County";
    public static final java.lang.String _Ship_to_Contact = "Ship_to_Contact";
    public static final java.lang.String _Posting_Date = "Posting_Date";
    public static final java.lang.String _Document_Date = "Document_Date";
    public static final java.lang.String _User = "User";
    public static final java.lang.String _Location_Code = "Location_Code";
    public static final java.lang.String _Shipment_Method_Code = "Shipment_Method_Code";
    public static final java.lang.String _Shipment_Date = "Shipment_Date";
    public static final java.lang.String _STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No = "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No";
    public static final java.lang.String _STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec = "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec";
    public static final WS_ReqInternaMaterial_Fields No = new WS_ReqInternaMaterial_Fields(_No);
    public static final WS_ReqInternaMaterial_Fields Service = new WS_ReqInternaMaterial_Fields(_Service);
    public static final WS_ReqInternaMaterial_Fields Ship_to_Name = new WS_ReqInternaMaterial_Fields(_Ship_to_Name);
    public static final WS_ReqInternaMaterial_Fields Ship_to_Address = new WS_ReqInternaMaterial_Fields(_Ship_to_Address);
    public static final WS_ReqInternaMaterial_Fields Ship_to_Address_2 = new WS_ReqInternaMaterial_Fields(_Ship_to_Address_2);
    public static final WS_ReqInternaMaterial_Fields Ship_to_Post_Code = new WS_ReqInternaMaterial_Fields(_Ship_to_Post_Code);
    public static final WS_ReqInternaMaterial_Fields Ship_to_City = new WS_ReqInternaMaterial_Fields(_Ship_to_City);
    public static final WS_ReqInternaMaterial_Fields Ship_to_County = new WS_ReqInternaMaterial_Fields(_Ship_to_County);
    public static final WS_ReqInternaMaterial_Fields Ship_to_Contact = new WS_ReqInternaMaterial_Fields(_Ship_to_Contact);
    public static final WS_ReqInternaMaterial_Fields Posting_Date = new WS_ReqInternaMaterial_Fields(_Posting_Date);
    public static final WS_ReqInternaMaterial_Fields Document_Date = new WS_ReqInternaMaterial_Fields(_Document_Date);
    public static final WS_ReqInternaMaterial_Fields User = new WS_ReqInternaMaterial_Fields(_User);
    public static final WS_ReqInternaMaterial_Fields Location_Code = new WS_ReqInternaMaterial_Fields(_Location_Code);
    public static final WS_ReqInternaMaterial_Fields Shipment_Method_Code = new WS_ReqInternaMaterial_Fields(_Shipment_Method_Code);
    public static final WS_ReqInternaMaterial_Fields Shipment_Date = new WS_ReqInternaMaterial_Fields(_Shipment_Date);
    public static final WS_ReqInternaMaterial_Fields STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No = new WS_ReqInternaMaterial_Fields(_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No);
    public static final WS_ReqInternaMaterial_Fields STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec = new WS_ReqInternaMaterial_Fields(_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec);
    public java.lang.String getValue() { return _value_;}
    public static WS_ReqInternaMaterial_Fields fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WS_ReqInternaMaterial_Fields enumeration = (WS_ReqInternaMaterial_Fields)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WS_ReqInternaMaterial_Fields fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WS_ReqInternaMaterial_Fields.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "WS_ReqInternaMaterial_Fields"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
