/**
 * WS_ReqInternaMaterial.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_ReqInternaMaterial  implements java.io.Serializable {
    private java.lang.String key;

    private java.lang.String no;

    private java.lang.String service;

    private java.lang.String ship_to_Name;

    private java.lang.String ship_to_Address;

    private java.lang.String ship_to_Address_2;

    private java.lang.String ship_to_Post_Code;

    private java.lang.String ship_to_City;

    private java.lang.String ship_to_County;

    private java.lang.String ship_to_Contact;

    private java.util.Date posting_Date;

    private java.util.Date document_Date;

    private java.lang.String user;

    private java.lang.String location_Code;

    private java.lang.String shipment_Method_Code;

    private java.util.Date shipment_Date;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec;

    private microsoft_dynamics_schemas.Sales_Internal_Req_subform[] salesLines;

    public WS_ReqInternaMaterial() {
    }

    public WS_ReqInternaMaterial(
           java.lang.String key,
           java.lang.String no,
           java.lang.String service,
           java.lang.String ship_to_Name,
           java.lang.String ship_to_Address,
           java.lang.String ship_to_Address_2,
           java.lang.String ship_to_Post_Code,
           java.lang.String ship_to_City,
           java.lang.String ship_to_County,
           java.lang.String ship_to_Contact,
           java.util.Date posting_Date,
           java.util.Date document_Date,
           java.lang.String user,
           java.lang.String location_Code,
           java.lang.String shipment_Method_Code,
           java.util.Date shipment_Date,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec,
           microsoft_dynamics_schemas.Sales_Internal_Req_subform[] salesLines) {
           this.key = key;
           this.no = no;
           this.service = service;
           this.ship_to_Name = ship_to_Name;
           this.ship_to_Address = ship_to_Address;
           this.ship_to_Address_2 = ship_to_Address_2;
           this.ship_to_Post_Code = ship_to_Post_Code;
           this.ship_to_City = ship_to_City;
           this.ship_to_County = ship_to_County;
           this.ship_to_Contact = ship_to_Contact;
           this.posting_Date = posting_Date;
           this.document_Date = document_Date;
           this.user = user;
           this.location_Code = location_Code;
           this.shipment_Method_Code = shipment_Method_Code;
           this.shipment_Date = shipment_Date;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec;
           this.salesLines = salesLines;
    }


    /**
     * Gets the key value for this WS_ReqInternaMaterial.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this WS_ReqInternaMaterial.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this WS_ReqInternaMaterial.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this WS_ReqInternaMaterial.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the service value for this WS_ReqInternaMaterial.
     * 
     * @return service
     */
    public java.lang.String getService() {
        return service;
    }


    /**
     * Sets the service value for this WS_ReqInternaMaterial.
     * 
     * @param service
     */
    public void setService(java.lang.String service) {
        this.service = service;
    }


    /**
     * Gets the ship_to_Name value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_Name
     */
    public java.lang.String getShip_to_Name() {
        return ship_to_Name;
    }


    /**
     * Sets the ship_to_Name value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_Name
     */
    public void setShip_to_Name(java.lang.String ship_to_Name) {
        this.ship_to_Name = ship_to_Name;
    }


    /**
     * Gets the ship_to_Address value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_Address
     */
    public java.lang.String getShip_to_Address() {
        return ship_to_Address;
    }


    /**
     * Sets the ship_to_Address value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_Address
     */
    public void setShip_to_Address(java.lang.String ship_to_Address) {
        this.ship_to_Address = ship_to_Address;
    }


    /**
     * Gets the ship_to_Address_2 value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_Address_2
     */
    public java.lang.String getShip_to_Address_2() {
        return ship_to_Address_2;
    }


    /**
     * Sets the ship_to_Address_2 value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_Address_2
     */
    public void setShip_to_Address_2(java.lang.String ship_to_Address_2) {
        this.ship_to_Address_2 = ship_to_Address_2;
    }


    /**
     * Gets the ship_to_Post_Code value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_Post_Code
     */
    public java.lang.String getShip_to_Post_Code() {
        return ship_to_Post_Code;
    }


    /**
     * Sets the ship_to_Post_Code value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_Post_Code
     */
    public void setShip_to_Post_Code(java.lang.String ship_to_Post_Code) {
        this.ship_to_Post_Code = ship_to_Post_Code;
    }


    /**
     * Gets the ship_to_City value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_City
     */
    public java.lang.String getShip_to_City() {
        return ship_to_City;
    }


    /**
     * Sets the ship_to_City value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_City
     */
    public void setShip_to_City(java.lang.String ship_to_City) {
        this.ship_to_City = ship_to_City;
    }


    /**
     * Gets the ship_to_County value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_County
     */
    public java.lang.String getShip_to_County() {
        return ship_to_County;
    }


    /**
     * Sets the ship_to_County value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_County
     */
    public void setShip_to_County(java.lang.String ship_to_County) {
        this.ship_to_County = ship_to_County;
    }


    /**
     * Gets the ship_to_Contact value for this WS_ReqInternaMaterial.
     * 
     * @return ship_to_Contact
     */
    public java.lang.String getShip_to_Contact() {
        return ship_to_Contact;
    }


    /**
     * Sets the ship_to_Contact value for this WS_ReqInternaMaterial.
     * 
     * @param ship_to_Contact
     */
    public void setShip_to_Contact(java.lang.String ship_to_Contact) {
        this.ship_to_Contact = ship_to_Contact;
    }


    /**
     * Gets the posting_Date value for this WS_ReqInternaMaterial.
     * 
     * @return posting_Date
     */
    public java.util.Date getPosting_Date() {
        return posting_Date;
    }


    /**
     * Sets the posting_Date value for this WS_ReqInternaMaterial.
     * 
     * @param posting_Date
     */
    public void setPosting_Date(java.util.Date posting_Date) {
        this.posting_Date = posting_Date;
    }


    /**
     * Gets the document_Date value for this WS_ReqInternaMaterial.
     * 
     * @return document_Date
     */
    public java.util.Date getDocument_Date() {
        return document_Date;
    }


    /**
     * Sets the document_Date value for this WS_ReqInternaMaterial.
     * 
     * @param document_Date
     */
    public void setDocument_Date(java.util.Date document_Date) {
        this.document_Date = document_Date;
    }


    /**
     * Gets the user value for this WS_ReqInternaMaterial.
     * 
     * @return user
     */
    public java.lang.String getUser() {
        return user;
    }


    /**
     * Sets the user value for this WS_ReqInternaMaterial.
     * 
     * @param user
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }


    /**
     * Gets the location_Code value for this WS_ReqInternaMaterial.
     * 
     * @return location_Code
     */
    public java.lang.String getLocation_Code() {
        return location_Code;
    }


    /**
     * Sets the location_Code value for this WS_ReqInternaMaterial.
     * 
     * @param location_Code
     */
    public void setLocation_Code(java.lang.String location_Code) {
        this.location_Code = location_Code;
    }


    /**
     * Gets the shipment_Method_Code value for this WS_ReqInternaMaterial.
     * 
     * @return shipment_Method_Code
     */
    public java.lang.String getShipment_Method_Code() {
        return shipment_Method_Code;
    }


    /**
     * Sets the shipment_Method_Code value for this WS_ReqInternaMaterial.
     * 
     * @param shipment_Method_Code
     */
    public void setShipment_Method_Code(java.lang.String shipment_Method_Code) {
        this.shipment_Method_Code = shipment_Method_Code;
    }


    /**
     * Gets the shipment_Date value for this WS_ReqInternaMaterial.
     * 
     * @return shipment_Date
     */
    public java.util.Date getShipment_Date() {
        return shipment_Date;
    }


    /**
     * Sets the shipment_Date value for this WS_ReqInternaMaterial.
     * 
     * @param shipment_Date
     */
    public void setShipment_Date(java.util.Date shipment_Date) {
        this.shipment_Date = shipment_Date;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No value for this WS_ReqInternaMaterial.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No value for this WS_ReqInternaMaterial.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec value for this WS_ReqInternaMaterial.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec value for this WS_ReqInternaMaterial.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec;
    }


    /**
     * Gets the salesLines value for this WS_ReqInternaMaterial.
     * 
     * @return salesLines
     */
    public microsoft_dynamics_schemas.Sales_Internal_Req_subform[] getSalesLines() {
        return salesLines;
    }


    /**
     * Sets the salesLines value for this WS_ReqInternaMaterial.
     * 
     * @param salesLines
     */
    public void setSalesLines(microsoft_dynamics_schemas.Sales_Internal_Req_subform[] salesLines) {
        this.salesLines = salesLines;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_ReqInternaMaterial)) return false;
        WS_ReqInternaMaterial other = (WS_ReqInternaMaterial) obj;
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
            ((this.service==null && other.getService()==null) || 
             (this.service!=null &&
              this.service.equals(other.getService()))) &&
            ((this.ship_to_Name==null && other.getShip_to_Name()==null) || 
             (this.ship_to_Name!=null &&
              this.ship_to_Name.equals(other.getShip_to_Name()))) &&
            ((this.ship_to_Address==null && other.getShip_to_Address()==null) || 
             (this.ship_to_Address!=null &&
              this.ship_to_Address.equals(other.getShip_to_Address()))) &&
            ((this.ship_to_Address_2==null && other.getShip_to_Address_2()==null) || 
             (this.ship_to_Address_2!=null &&
              this.ship_to_Address_2.equals(other.getShip_to_Address_2()))) &&
            ((this.ship_to_Post_Code==null && other.getShip_to_Post_Code()==null) || 
             (this.ship_to_Post_Code!=null &&
              this.ship_to_Post_Code.equals(other.getShip_to_Post_Code()))) &&
            ((this.ship_to_City==null && other.getShip_to_City()==null) || 
             (this.ship_to_City!=null &&
              this.ship_to_City.equals(other.getShip_to_City()))) &&
            ((this.ship_to_County==null && other.getShip_to_County()==null) || 
             (this.ship_to_County!=null &&
              this.ship_to_County.equals(other.getShip_to_County()))) &&
            ((this.ship_to_Contact==null && other.getShip_to_Contact()==null) || 
             (this.ship_to_Contact!=null &&
              this.ship_to_Contact.equals(other.getShip_to_Contact()))) &&
            ((this.posting_Date==null && other.getPosting_Date()==null) || 
             (this.posting_Date!=null &&
              this.posting_Date.equals(other.getPosting_Date()))) &&
            ((this.document_Date==null && other.getDocument_Date()==null) || 
             (this.document_Date!=null &&
              this.document_Date.equals(other.getDocument_Date()))) &&
            ((this.user==null && other.getUser()==null) || 
             (this.user!=null &&
              this.user.equals(other.getUser()))) &&
            ((this.location_Code==null && other.getLocation_Code()==null) || 
             (this.location_Code!=null &&
              this.location_Code.equals(other.getLocation_Code()))) &&
            ((this.shipment_Method_Code==null && other.getShipment_Method_Code()==null) || 
             (this.shipment_Method_Code!=null &&
              this.shipment_Method_Code.equals(other.getShipment_Method_Code()))) &&
            ((this.shipment_Date==null && other.getShipment_Date()==null) || 
             (this.shipment_Date!=null &&
              this.shipment_Date.equals(other.getShipment_Date()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec()))) &&
            ((this.salesLines==null && other.getSalesLines()==null) || 
             (this.salesLines!=null &&
              java.util.Arrays.equals(this.salesLines, other.getSalesLines())));
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
        if (getService() != null) {
            _hashCode += getService().hashCode();
        }
        if (getShip_to_Name() != null) {
            _hashCode += getShip_to_Name().hashCode();
        }
        if (getShip_to_Address() != null) {
            _hashCode += getShip_to_Address().hashCode();
        }
        if (getShip_to_Address_2() != null) {
            _hashCode += getShip_to_Address_2().hashCode();
        }
        if (getShip_to_Post_Code() != null) {
            _hashCode += getShip_to_Post_Code().hashCode();
        }
        if (getShip_to_City() != null) {
            _hashCode += getShip_to_City().hashCode();
        }
        if (getShip_to_County() != null) {
            _hashCode += getShip_to_County().hashCode();
        }
        if (getShip_to_Contact() != null) {
            _hashCode += getShip_to_Contact().hashCode();
        }
        if (getPosting_Date() != null) {
            _hashCode += getPosting_Date().hashCode();
        }
        if (getDocument_Date() != null) {
            _hashCode += getDocument_Date().hashCode();
        }
        if (getUser() != null) {
            _hashCode += getUser().hashCode();
        }
        if (getLocation_Code() != null) {
            _hashCode += getLocation_Code().hashCode();
        }
        if (getShipment_Method_Code() != null) {
            _hashCode += getShipment_Method_Code().hashCode();
        }
        if (getShipment_Date() != null) {
            _hashCode += getShipment_Date().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec().hashCode();
        }
        if (getSalesLines() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSalesLines());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSalesLines(), i);
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
        new org.apache.axis.description.TypeDesc(WS_ReqInternaMaterial.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "WS_ReqInternaMaterial"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("service");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Service"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Address");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Address_2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_Address_2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Post_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_Post_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_City");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_City"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_County");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_County"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Contact");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Ship_to_Contact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posting_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Posting_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Document_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "User"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Location_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipment_Method_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Shipment_Method_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipment_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Shipment_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salesLines");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "SalesLines"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sales_Internal_Req_subform"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sales_Internal_Req_subform"));
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
