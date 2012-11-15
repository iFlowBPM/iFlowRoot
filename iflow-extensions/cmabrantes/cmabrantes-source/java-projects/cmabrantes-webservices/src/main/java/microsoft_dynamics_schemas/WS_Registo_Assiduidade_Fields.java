/**
 * WS_Registo_Assiduidade_Fields.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Registo_Assiduidade_Fields implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WS_Registo_Assiduidade_Fields(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _No = "No";
    public static final java.lang.String _Description = "Description";
    public static final java.lang.String _External_Document_No = "External_Document_No";
    public static final java.lang.String _Responsibility_Center = "Responsibility_Center";
    public static final java.lang.String _Post_Type = "Post_Type";
    public static final java.lang.String _Posting_Date = "Posting_Date";
    public static final java.lang.String _Document_Date = "Document_Date";
    public static final java.lang.String _Creation_DateTime = "Creation_DateTime";
    public static final java.lang.String _Status = "Status";
    public static final java.lang.String _User_ID = "User_ID";
    public static final java.lang.String _Shortcut_Dimension_1_Code = "Shortcut_Dimension_1_Code";
    public static final java.lang.String _Shortcut_Dimension_2_Code = "Shortcut_Dimension_2_Code";
    public static final WS_Registo_Assiduidade_Fields No = new WS_Registo_Assiduidade_Fields(_No);
    public static final WS_Registo_Assiduidade_Fields Description = new WS_Registo_Assiduidade_Fields(_Description);
    public static final WS_Registo_Assiduidade_Fields External_Document_No = new WS_Registo_Assiduidade_Fields(_External_Document_No);
    public static final WS_Registo_Assiduidade_Fields Responsibility_Center = new WS_Registo_Assiduidade_Fields(_Responsibility_Center);
    public static final WS_Registo_Assiduidade_Fields Post_Type = new WS_Registo_Assiduidade_Fields(_Post_Type);
    public static final WS_Registo_Assiduidade_Fields Posting_Date = new WS_Registo_Assiduidade_Fields(_Posting_Date);
    public static final WS_Registo_Assiduidade_Fields Document_Date = new WS_Registo_Assiduidade_Fields(_Document_Date);
    public static final WS_Registo_Assiduidade_Fields Creation_DateTime = new WS_Registo_Assiduidade_Fields(_Creation_DateTime);
    public static final WS_Registo_Assiduidade_Fields Status = new WS_Registo_Assiduidade_Fields(_Status);
    public static final WS_Registo_Assiduidade_Fields User_ID = new WS_Registo_Assiduidade_Fields(_User_ID);
    public static final WS_Registo_Assiduidade_Fields Shortcut_Dimension_1_Code = new WS_Registo_Assiduidade_Fields(_Shortcut_Dimension_1_Code);
    public static final WS_Registo_Assiduidade_Fields Shortcut_Dimension_2_Code = new WS_Registo_Assiduidade_Fields(_Shortcut_Dimension_2_Code);
    public java.lang.String getValue() { return _value_;}
    public static WS_Registo_Assiduidade_Fields fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WS_Registo_Assiduidade_Fields enumeration = (WS_Registo_Assiduidade_Fields)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WS_Registo_Assiduidade_Fields fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WS_Registo_Assiduidade_Fields.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_Fields"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
