/**
 * WS_Registo_Assiduidade.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Registo_Assiduidade  implements java.io.Serializable {
    private java.lang.String key;

    private java.lang.String no;

    private java.lang.String description;

    private java.lang.String external_Document_No;

    private java.lang.String responsibility_Center;

    private microsoft_dynamics_schemas.Post_Type post_Type;

    private java.util.Date posting_Date;

    private java.util.Date document_Date;

    private java.util.Calendar creation_DateTime;

    private microsoft_dynamics_schemas.Status status;

    private java.lang.String user_ID;

    private java.lang.String shortcut_Dimension_1_Code;

    private java.lang.String shortcut_Dimension_2_Code;

    private microsoft_dynamics_schemas.Assiduity_Doc_Line_Line[] subformLinhas;

    public WS_Registo_Assiduidade() {
    }

    public WS_Registo_Assiduidade(
           java.lang.String key,
           java.lang.String no,
           java.lang.String description,
           java.lang.String external_Document_No,
           java.lang.String responsibility_Center,
           microsoft_dynamics_schemas.Post_Type post_Type,
           java.util.Date posting_Date,
           java.util.Date document_Date,
           java.util.Calendar creation_DateTime,
           microsoft_dynamics_schemas.Status status,
           java.lang.String user_ID,
           java.lang.String shortcut_Dimension_1_Code,
           java.lang.String shortcut_Dimension_2_Code,
           microsoft_dynamics_schemas.Assiduity_Doc_Line_Line[] subformLinhas) {
           this.key = key;
           this.no = no;
           this.description = description;
           this.external_Document_No = external_Document_No;
           this.responsibility_Center = responsibility_Center;
           this.post_Type = post_Type;
           this.posting_Date = posting_Date;
           this.document_Date = document_Date;
           this.creation_DateTime = creation_DateTime;
           this.status = status;
           this.user_ID = user_ID;
           this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
           this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
           this.subformLinhas = subformLinhas;
    }


    /**
     * Gets the key value for this WS_Registo_Assiduidade.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this WS_Registo_Assiduidade.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this WS_Registo_Assiduidade.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this WS_Registo_Assiduidade.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the description value for this WS_Registo_Assiduidade.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this WS_Registo_Assiduidade.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the external_Document_No value for this WS_Registo_Assiduidade.
     * 
     * @return external_Document_No
     */
    public java.lang.String getExternal_Document_No() {
        return external_Document_No;
    }


    /**
     * Sets the external_Document_No value for this WS_Registo_Assiduidade.
     * 
     * @param external_Document_No
     */
    public void setExternal_Document_No(java.lang.String external_Document_No) {
        this.external_Document_No = external_Document_No;
    }


    /**
     * Gets the responsibility_Center value for this WS_Registo_Assiduidade.
     * 
     * @return responsibility_Center
     */
    public java.lang.String getResponsibility_Center() {
        return responsibility_Center;
    }


    /**
     * Sets the responsibility_Center value for this WS_Registo_Assiduidade.
     * 
     * @param responsibility_Center
     */
    public void setResponsibility_Center(java.lang.String responsibility_Center) {
        this.responsibility_Center = responsibility_Center;
    }


    /**
     * Gets the post_Type value for this WS_Registo_Assiduidade.
     * 
     * @return post_Type
     */
    public microsoft_dynamics_schemas.Post_Type getPost_Type() {
        return post_Type;
    }


    /**
     * Sets the post_Type value for this WS_Registo_Assiduidade.
     * 
     * @param post_Type
     */
    public void setPost_Type(microsoft_dynamics_schemas.Post_Type post_Type) {
        this.post_Type = post_Type;
    }


    /**
     * Gets the posting_Date value for this WS_Registo_Assiduidade.
     * 
     * @return posting_Date
     */
    public java.util.Date getPosting_Date() {
        return posting_Date;
    }


    /**
     * Sets the posting_Date value for this WS_Registo_Assiduidade.
     * 
     * @param posting_Date
     */
    public void setPosting_Date(java.util.Date posting_Date) {
        this.posting_Date = posting_Date;
    }


    /**
     * Gets the document_Date value for this WS_Registo_Assiduidade.
     * 
     * @return document_Date
     */
    public java.util.Date getDocument_Date() {
        return document_Date;
    }


    /**
     * Sets the document_Date value for this WS_Registo_Assiduidade.
     * 
     * @param document_Date
     */
    public void setDocument_Date(java.util.Date document_Date) {
        this.document_Date = document_Date;
    }


    /**
     * Gets the creation_DateTime value for this WS_Registo_Assiduidade.
     * 
     * @return creation_DateTime
     */
    public java.util.Calendar getCreation_DateTime() {
        return creation_DateTime;
    }


    /**
     * Sets the creation_DateTime value for this WS_Registo_Assiduidade.
     * 
     * @param creation_DateTime
     */
    public void setCreation_DateTime(java.util.Calendar creation_DateTime) {
        this.creation_DateTime = creation_DateTime;
    }


    /**
     * Gets the status value for this WS_Registo_Assiduidade.
     * 
     * @return status
     */
    public microsoft_dynamics_schemas.Status getStatus() {
        return status;
    }


    /**
     * Sets the status value for this WS_Registo_Assiduidade.
     * 
     * @param status
     */
    public void setStatus(microsoft_dynamics_schemas.Status status) {
        this.status = status;
    }


    /**
     * Gets the user_ID value for this WS_Registo_Assiduidade.
     * 
     * @return user_ID
     */
    public java.lang.String getUser_ID() {
        return user_ID;
    }


    /**
     * Sets the user_ID value for this WS_Registo_Assiduidade.
     * 
     * @param user_ID
     */
    public void setUser_ID(java.lang.String user_ID) {
        this.user_ID = user_ID;
    }


    /**
     * Gets the shortcut_Dimension_1_Code value for this WS_Registo_Assiduidade.
     * 
     * @return shortcut_Dimension_1_Code
     */
    public java.lang.String getShortcut_Dimension_1_Code() {
        return shortcut_Dimension_1_Code;
    }


    /**
     * Sets the shortcut_Dimension_1_Code value for this WS_Registo_Assiduidade.
     * 
     * @param shortcut_Dimension_1_Code
     */
    public void setShortcut_Dimension_1_Code(java.lang.String shortcut_Dimension_1_Code) {
        this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
    }


    /**
     * Gets the shortcut_Dimension_2_Code value for this WS_Registo_Assiduidade.
     * 
     * @return shortcut_Dimension_2_Code
     */
    public java.lang.String getShortcut_Dimension_2_Code() {
        return shortcut_Dimension_2_Code;
    }


    /**
     * Sets the shortcut_Dimension_2_Code value for this WS_Registo_Assiduidade.
     * 
     * @param shortcut_Dimension_2_Code
     */
    public void setShortcut_Dimension_2_Code(java.lang.String shortcut_Dimension_2_Code) {
        this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
    }


    /**
     * Gets the subformLinhas value for this WS_Registo_Assiduidade.
     * 
     * @return subformLinhas
     */
    public microsoft_dynamics_schemas.Assiduity_Doc_Line_Line[] getSubformLinhas() {
        return subformLinhas;
    }


    /**
     * Sets the subformLinhas value for this WS_Registo_Assiduidade.
     * 
     * @param subformLinhas
     */
    public void setSubformLinhas(microsoft_dynamics_schemas.Assiduity_Doc_Line_Line[] subformLinhas) {
        this.subformLinhas = subformLinhas;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_Registo_Assiduidade)) return false;
        WS_Registo_Assiduidade other = (WS_Registo_Assiduidade) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.key==null && other.getKey()==null) || 
             (this.key!=null &&
              this.key.equals(other.getKey()))) &&
            ((this.no==null && other.getNo()==null) || 
             (this.no!=null &&
              this.no.equals(other.getNo()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.external_Document_No==null && other.getExternal_Document_No()==null) || 
             (this.external_Document_No!=null &&
              this.external_Document_No.equals(other.getExternal_Document_No()))) &&
            ((this.responsibility_Center==null && other.getResponsibility_Center()==null) || 
             (this.responsibility_Center!=null &&
              this.responsibility_Center.equals(other.getResponsibility_Center()))) &&
            ((this.post_Type==null && other.getPost_Type()==null) || 
             (this.post_Type!=null &&
              this.post_Type.equals(other.getPost_Type()))) &&
            ((this.posting_Date==null && other.getPosting_Date()==null) || 
             (this.posting_Date!=null &&
              this.posting_Date.equals(other.getPosting_Date()))) &&
            ((this.document_Date==null && other.getDocument_Date()==null) || 
             (this.document_Date!=null &&
              this.document_Date.equals(other.getDocument_Date()))) &&
            ((this.creation_DateTime==null && other.getCreation_DateTime()==null) || 
             (this.creation_DateTime!=null &&
              this.creation_DateTime.equals(other.getCreation_DateTime()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.user_ID==null && other.getUser_ID()==null) || 
             (this.user_ID!=null &&
              this.user_ID.equals(other.getUser_ID()))) &&
            ((this.shortcut_Dimension_1_Code==null && other.getShortcut_Dimension_1_Code()==null) || 
             (this.shortcut_Dimension_1_Code!=null &&
              this.shortcut_Dimension_1_Code.equals(other.getShortcut_Dimension_1_Code()))) &&
            ((this.shortcut_Dimension_2_Code==null && other.getShortcut_Dimension_2_Code()==null) || 
             (this.shortcut_Dimension_2_Code!=null &&
              this.shortcut_Dimension_2_Code.equals(other.getShortcut_Dimension_2_Code()))) &&
            ((this.subformLinhas==null && other.getSubformLinhas()==null) || 
             (this.subformLinhas!=null &&
              java.util.Arrays.equals(this.subformLinhas, other.getSubformLinhas())));
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
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        if (getNo() != null) {
            _hashCode += getNo().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getExternal_Document_No() != null) {
            _hashCode += getExternal_Document_No().hashCode();
        }
        if (getResponsibility_Center() != null) {
            _hashCode += getResponsibility_Center().hashCode();
        }
        if (getPost_Type() != null) {
            _hashCode += getPost_Type().hashCode();
        }
        if (getPosting_Date() != null) {
            _hashCode += getPosting_Date().hashCode();
        }
        if (getDocument_Date() != null) {
            _hashCode += getDocument_Date().hashCode();
        }
        if (getCreation_DateTime() != null) {
            _hashCode += getCreation_DateTime().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getUser_ID() != null) {
            _hashCode += getUser_ID().hashCode();
        }
        if (getShortcut_Dimension_1_Code() != null) {
            _hashCode += getShortcut_Dimension_1_Code().hashCode();
        }
        if (getShortcut_Dimension_2_Code() != null) {
            _hashCode += getShortcut_Dimension_2_Code().hashCode();
        }
        if (getSubformLinhas() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubformLinhas());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubformLinhas(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WS_Registo_Assiduidade.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("external_Document_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "External_Document_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responsibility_Center");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Responsibility_Center"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("post_Type");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Post_Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Post_Type"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posting_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Posting_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Document_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creation_DateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Creation_DateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Status"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user_ID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "User_ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_1_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Shortcut_Dimension_1_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_2_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Shortcut_Dimension_2_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subformLinhas");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "SubformLinhas"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line"));
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
