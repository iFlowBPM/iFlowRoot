/**
 * WS_ReqInternaCompra_Fields.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_ReqInternaCompra_Fields implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WS_ReqInternaCompra_Fields(java.lang.String value) {
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
    public static final java.lang.String _User_ID = "User_ID";
    public static final java.lang.String _Location_Code = "Location_Code";
    public static final java.lang.String _Shipment_Method_Code = "Shipment_Method_Code";
    public static final java.lang.String _Expected_Receipt_Date = "Expected_Receipt_Date";
    public static final java.lang.String _STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No = "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No";
    public static final java.lang.String _STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec = "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec";
    public static final WS_ReqInternaCompra_Fields No = new WS_ReqInternaCompra_Fields(_No);
    public static final WS_ReqInternaCompra_Fields Service = new WS_ReqInternaCompra_Fields(_Service);
    public static final WS_ReqInternaCompra_Fields Ship_to_Name = new WS_ReqInternaCompra_Fields(_Ship_to_Name);
    public static final WS_ReqInternaCompra_Fields Ship_to_Address = new WS_ReqInternaCompra_Fields(_Ship_to_Address);
    public static final WS_ReqInternaCompra_Fields Ship_to_Address_2 = new WS_ReqInternaCompra_Fields(_Ship_to_Address_2);
    public static final WS_ReqInternaCompra_Fields Ship_to_Post_Code = new WS_ReqInternaCompra_Fields(_Ship_to_Post_Code);
    public static final WS_ReqInternaCompra_Fields Ship_to_City = new WS_ReqInternaCompra_Fields(_Ship_to_City);
    public static final WS_ReqInternaCompra_Fields Ship_to_County = new WS_ReqInternaCompra_Fields(_Ship_to_County);
    public static final WS_ReqInternaCompra_Fields Ship_to_Contact = new WS_ReqInternaCompra_Fields(_Ship_to_Contact);
    public static final WS_ReqInternaCompra_Fields Posting_Date = new WS_ReqInternaCompra_Fields(_Posting_Date);
    public static final WS_ReqInternaCompra_Fields Document_Date = new WS_ReqInternaCompra_Fields(_Document_Date);
    public static final WS_ReqInternaCompra_Fields User_ID = new WS_ReqInternaCompra_Fields(_User_ID);
    public static final WS_ReqInternaCompra_Fields Location_Code = new WS_ReqInternaCompra_Fields(_Location_Code);
    public static final WS_ReqInternaCompra_Fields Shipment_Method_Code = new WS_ReqInternaCompra_Fields(_Shipment_Method_Code);
    public static final WS_ReqInternaCompra_Fields Expected_Receipt_Date = new WS_ReqInternaCompra_Fields(_Expected_Receipt_Date);
    public static final WS_ReqInternaCompra_Fields STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No = new WS_ReqInternaCompra_Fields(_STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No);
    public static final WS_ReqInternaCompra_Fields STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec = new WS_ReqInternaCompra_Fields(_STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec);
    public java.lang.String getValue() { return _value_;}
    public static WS_ReqInternaCompra_Fields fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WS_ReqInternaCompra_Fields enumeration = (WS_ReqInternaCompra_Fields)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WS_ReqInternaCompra_Fields fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WS_ReqInternaCompra_Fields.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "WS_ReqInternaCompra_Fields"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
