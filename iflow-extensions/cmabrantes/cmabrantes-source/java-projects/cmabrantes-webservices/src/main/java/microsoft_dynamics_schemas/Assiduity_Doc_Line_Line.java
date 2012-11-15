/**
 * Assiduity_Doc_Line_Line.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Assiduity_Doc_Line_Line  implements java.io.Serializable {
    private java.lang.String key;

    private microsoft_dynamics_schemas.Type type;

    private java.lang.String no;

    private java.lang.String description;

    private java.lang.String unit_Of_Measure_Code;

    private java.lang.String employee_No;

    private java.lang.String employee_Name;

    private java.util.Date begin_Date;

    private org.apache.axis.types.Time begin_Hour;

    private java.util.Date end_Date;

    private org.apache.axis.types.Time end_Hour;

    private java.math.BigDecimal quantity;

    private java.math.BigDecimal qty_Util_Days;

    private java.lang.String observations;

    private java.lang.String txtDisponibilidade;

    public Assiduity_Doc_Line_Line() {
    }

    public Assiduity_Doc_Line_Line(
           java.lang.String key,
           microsoft_dynamics_schemas.Type type,
           java.lang.String no,
           java.lang.String description,
           java.lang.String unit_Of_Measure_Code,
           java.lang.String employee_No,
           java.lang.String employee_Name,
           java.util.Date begin_Date,
           org.apache.axis.types.Time begin_Hour,
           java.util.Date end_Date,
           org.apache.axis.types.Time end_Hour,
           java.math.BigDecimal quantity,
           java.math.BigDecimal qty_Util_Days,
           java.lang.String observations,
           java.lang.String txtDisponibilidade) {
           this.key = key;
           this.type = type;
           this.no = no;
           this.description = description;
           this.unit_Of_Measure_Code = unit_Of_Measure_Code;
           this.employee_No = employee_No;
           this.employee_Name = employee_Name;
           this.begin_Date = begin_Date;
           this.begin_Hour = begin_Hour;
           this.end_Date = end_Date;
           this.end_Hour = end_Hour;
           this.quantity = quantity;
           this.qty_Util_Days = qty_Util_Days;
           this.observations = observations;
           this.txtDisponibilidade = txtDisponibilidade;
    }


    /**
     * Gets the key value for this Assiduity_Doc_Line_Line.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this Assiduity_Doc_Line_Line.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the type value for this Assiduity_Doc_Line_Line.
     * 
     * @return type
     */
    public microsoft_dynamics_schemas.Type getType() {
        return type;
    }


    /**
     * Sets the type value for this Assiduity_Doc_Line_Line.
     * 
     * @param type
     */
    public void setType(microsoft_dynamics_schemas.Type type) {
        this.type = type;
    }


    /**
     * Gets the no value for this Assiduity_Doc_Line_Line.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this Assiduity_Doc_Line_Line.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the description value for this Assiduity_Doc_Line_Line.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Assiduity_Doc_Line_Line.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the unit_Of_Measure_Code value for this Assiduity_Doc_Line_Line.
     * 
     * @return unit_Of_Measure_Code
     */
    public java.lang.String getUnit_Of_Measure_Code() {
        return unit_Of_Measure_Code;
    }


    /**
     * Sets the unit_Of_Measure_Code value for this Assiduity_Doc_Line_Line.
     * 
     * @param unit_Of_Measure_Code
     */
    public void setUnit_Of_Measure_Code(java.lang.String unit_Of_Measure_Code) {
        this.unit_Of_Measure_Code = unit_Of_Measure_Code;
    }


    /**
     * Gets the employee_No value for this Assiduity_Doc_Line_Line.
     * 
     * @return employee_No
     */
    public java.lang.String getEmployee_No() {
        return employee_No;
    }


    /**
     * Sets the employee_No value for this Assiduity_Doc_Line_Line.
     * 
     * @param employee_No
     */
    public void setEmployee_No(java.lang.String employee_No) {
        this.employee_No = employee_No;
    }


    /**
     * Gets the employee_Name value for this Assiduity_Doc_Line_Line.
     * 
     * @return employee_Name
     */
    public java.lang.String getEmployee_Name() {
        return employee_Name;
    }


    /**
     * Sets the employee_Name value for this Assiduity_Doc_Line_Line.
     * 
     * @param employee_Name
     */
    public void setEmployee_Name(java.lang.String employee_Name) {
        this.employee_Name = employee_Name;
    }


    /**
     * Gets the begin_Date value for this Assiduity_Doc_Line_Line.
     * 
     * @return begin_Date
     */
    public java.util.Date getBegin_Date() {
        return begin_Date;
    }


    /**
     * Sets the begin_Date value for this Assiduity_Doc_Line_Line.
     * 
     * @param begin_Date
     */
    public void setBegin_Date(java.util.Date begin_Date) {
        this.begin_Date = begin_Date;
    }


    /**
     * Gets the begin_Hour value for this Assiduity_Doc_Line_Line.
     * 
     * @return begin_Hour
     */
    public org.apache.axis.types.Time getBegin_Hour() {
        return begin_Hour;
    }


    /**
     * Sets the begin_Hour value for this Assiduity_Doc_Line_Line.
     * 
     * @param begin_Hour
     */
    public void setBegin_Hour(org.apache.axis.types.Time begin_Hour) {
        this.begin_Hour = begin_Hour;
    }


    /**
     * Gets the end_Date value for this Assiduity_Doc_Line_Line.
     * 
     * @return end_Date
     */
    public java.util.Date getEnd_Date() {
        return end_Date;
    }


    /**
     * Sets the end_Date value for this Assiduity_Doc_Line_Line.
     * 
     * @param end_Date
     */
    public void setEnd_Date(java.util.Date end_Date) {
        this.end_Date = end_Date;
    }


    /**
     * Gets the end_Hour value for this Assiduity_Doc_Line_Line.
     * 
     * @return end_Hour
     */
    public org.apache.axis.types.Time getEnd_Hour() {
        return end_Hour;
    }


    /**
     * Sets the end_Hour value for this Assiduity_Doc_Line_Line.
     * 
     * @param end_Hour
     */
    public void setEnd_Hour(org.apache.axis.types.Time end_Hour) {
        this.end_Hour = end_Hour;
    }


    /**
     * Gets the quantity value for this Assiduity_Doc_Line_Line.
     * 
     * @return quantity
     */
    public java.math.BigDecimal getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this Assiduity_Doc_Line_Line.
     * 
     * @param quantity
     */
    public void setQuantity(java.math.BigDecimal quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the qty_Util_Days value for this Assiduity_Doc_Line_Line.
     * 
     * @return qty_Util_Days
     */
    public java.math.BigDecimal getQty_Util_Days() {
        return qty_Util_Days;
    }


    /**
     * Sets the qty_Util_Days value for this Assiduity_Doc_Line_Line.
     * 
     * @param qty_Util_Days
     */
    public void setQty_Util_Days(java.math.BigDecimal qty_Util_Days) {
        this.qty_Util_Days = qty_Util_Days;
    }


    /**
     * Gets the observations value for this Assiduity_Doc_Line_Line.
     * 
     * @return observations
     */
    public java.lang.String getObservations() {
        return observations;
    }


    /**
     * Sets the observations value for this Assiduity_Doc_Line_Line.
     * 
     * @param observations
     */
    public void setObservations(java.lang.String observations) {
        this.observations = observations;
    }


    /**
     * Gets the txtDisponibilidade value for this Assiduity_Doc_Line_Line.
     * 
     * @return txtDisponibilidade
     */
    public java.lang.String getTxtDisponibilidade() {
        return txtDisponibilidade;
    }


    /**
     * Sets the txtDisponibilidade value for this Assiduity_Doc_Line_Line.
     * 
     * @param txtDisponibilidade
     */
    public void setTxtDisponibilidade(java.lang.String txtDisponibilidade) {
        this.txtDisponibilidade = txtDisponibilidade;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Assiduity_Doc_Line_Line)) return false;
        Assiduity_Doc_Line_Line other = (Assiduity_Doc_Line_Line) obj;
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
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.no==null && other.getNo()==null) || 
             (this.no!=null &&
              this.no.equals(other.getNo()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.unit_Of_Measure_Code==null && other.getUnit_Of_Measure_Code()==null) || 
             (this.unit_Of_Measure_Code!=null &&
              this.unit_Of_Measure_Code.equals(other.getUnit_Of_Measure_Code()))) &&
            ((this.employee_No==null && other.getEmployee_No()==null) || 
             (this.employee_No!=null &&
              this.employee_No.equals(other.getEmployee_No()))) &&
            ((this.employee_Name==null && other.getEmployee_Name()==null) || 
             (this.employee_Name!=null &&
              this.employee_Name.equals(other.getEmployee_Name()))) &&
            ((this.begin_Date==null && other.getBegin_Date()==null) || 
             (this.begin_Date!=null &&
              this.begin_Date.equals(other.getBegin_Date()))) &&
            ((this.begin_Hour==null && other.getBegin_Hour()==null) || 
             (this.begin_Hour!=null &&
              this.begin_Hour.equals(other.getBegin_Hour()))) &&
            ((this.end_Date==null && other.getEnd_Date()==null) || 
             (this.end_Date!=null &&
              this.end_Date.equals(other.getEnd_Date()))) &&
            ((this.end_Hour==null && other.getEnd_Hour()==null) || 
             (this.end_Hour!=null &&
              this.end_Hour.equals(other.getEnd_Hour()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.qty_Util_Days==null && other.getQty_Util_Days()==null) || 
             (this.qty_Util_Days!=null &&
              this.qty_Util_Days.equals(other.getQty_Util_Days()))) &&
            ((this.observations==null && other.getObservations()==null) || 
             (this.observations!=null &&
              this.observations.equals(other.getObservations()))) &&
            ((this.txtDisponibilidade==null && other.getTxtDisponibilidade()==null) || 
             (this.txtDisponibilidade!=null &&
              this.txtDisponibilidade.equals(other.getTxtDisponibilidade())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getNo() != null) {
            _hashCode += getNo().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getUnit_Of_Measure_Code() != null) {
            _hashCode += getUnit_Of_Measure_Code().hashCode();
        }
        if (getEmployee_No() != null) {
            _hashCode += getEmployee_No().hashCode();
        }
        if (getEmployee_Name() != null) {
            _hashCode += getEmployee_Name().hashCode();
        }
        if (getBegin_Date() != null) {
            _hashCode += getBegin_Date().hashCode();
        }
        if (getBegin_Hour() != null) {
            _hashCode += getBegin_Hour().hashCode();
        }
        if (getEnd_Date() != null) {
            _hashCode += getEnd_Date().hashCode();
        }
        if (getEnd_Hour() != null) {
            _hashCode += getEnd_Hour().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getQty_Util_Days() != null) {
            _hashCode += getQty_Util_Days().hashCode();
        }
        if (getObservations() != null) {
            _hashCode += getObservations().hashCode();
        }
        if (getTxtDisponibilidade() != null) {
            _hashCode += getTxtDisponibilidade().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Assiduity_Doc_Line_Line.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Type"));
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
        elemField.setFieldName("unit_Of_Measure_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Unit_Of_Measure_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employee_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Employee_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employee_Name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Employee_Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("begin_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Begin_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("begin_Hour");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Begin_Hour"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "time"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "End_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end_Hour");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "End_Hour"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "time"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_Util_Days");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Qty_Util_Days"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("observations");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Observations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txtDisponibilidade");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "txtDisponibilidade"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
