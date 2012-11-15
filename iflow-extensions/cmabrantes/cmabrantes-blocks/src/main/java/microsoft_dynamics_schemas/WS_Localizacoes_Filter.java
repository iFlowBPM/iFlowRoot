/**
 * WS_Localizacoes_Filter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Localizacoes_Filter  implements java.io.Serializable {
    private microsoft_dynamics_schemas.WS_Localizacoes_Fields field;

    private java.lang.String criteria;

    public WS_Localizacoes_Filter() {
    }

    public WS_Localizacoes_Filter(
           microsoft_dynamics_schemas.WS_Localizacoes_Fields field,
           java.lang.String criteria) {
           this.field = field;
           this.criteria = criteria;
    }


    /**
     * Gets the field value for this WS_Localizacoes_Filter.
     * 
     * @return field
     */
    public microsoft_dynamics_schemas.WS_Localizacoes_Fields getField() {
        return field;
    }


    /**
     * Sets the field value for this WS_Localizacoes_Filter.
     * 
     * @param field
     */
    public void setField(microsoft_dynamics_schemas.WS_Localizacoes_Fields field) {
        this.field = field;
    }


    /**
     * Gets the criteria value for this WS_Localizacoes_Filter.
     * 
     * @return criteria
     */
    public java.lang.String getCriteria() {
        return criteria;
    }


    /**
     * Sets the criteria value for this WS_Localizacoes_Filter.
     * 
     * @param criteria
     */
    public void setCriteria(java.lang.String criteria) {
        this.criteria = criteria;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_Localizacoes_Filter)) return false;
        WS_Localizacoes_Filter other = (WS_Localizacoes_Filter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.field==null && other.getField()==null) || 
             (this.field!=null &&
              this.field.equals(other.getField()))) &&
            ((this.criteria==null && other.getCriteria()==null) || 
             (this.criteria!=null &&
              this.criteria.equals(other.getCriteria())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getField() != null) {
            _hashCode += getField().hashCode();
        }
        if (getCriteria() != null) {
            _hashCode += getCriteria().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WS_Localizacoes_Filter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "WS_Localizacoes_Filter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("field");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "Field"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "WS_Localizacoes_Fields"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("criteria");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "Criteria"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
