/**
 * WS_ReqInternaCompra.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_ReqInternaCompra  implements java.io.Serializable {
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

    private java.lang.String user_ID;

    private java.lang.String location_Code;

    private java.lang.String shipment_Method_Code;

    private java.util.Date expected_Receipt_Date;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec;

    private microsoft_dynamics_schemas.Purchase_Internal_Req_Line[] purchLines;

    public WS_ReqInternaCompra() {
    }

    public WS_ReqInternaCompra(
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
           java.lang.String user_ID,
           java.lang.String location_Code,
           java.lang.String shipment_Method_Code,
           java.util.Date expected_Receipt_Date,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec,
           microsoft_dynamics_schemas.Purchase_Internal_Req_Line[] purchLines) {
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
           this.user_ID = user_ID;
           this.location_Code = location_Code;
           this.shipment_Method_Code = shipment_Method_Code;
           this.expected_Receipt_Date = expected_Receipt_Date;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec;
           this.purchLines = purchLines;
    }


    /**
     * Gets the key value for this WS_ReqInternaCompra.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this WS_ReqInternaCompra.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this WS_ReqInternaCompra.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this WS_ReqInternaCompra.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the service value for this WS_ReqInternaCompra.
     * 
     * @return service
     */
    public java.lang.String getService() {
        return service;
    }


    /**
     * Sets the service value for this WS_ReqInternaCompra.
     * 
     * @param service
     */
    public void setService(java.lang.String service) {
        this.service = service;
    }


    /**
     * Gets the ship_to_Name value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_Name
     */
    public java.lang.String getShip_to_Name() {
        return ship_to_Name;
    }


    /**
     * Sets the ship_to_Name value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_Name
     */
    public void setShip_to_Name(java.lang.String ship_to_Name) {
        this.ship_to_Name = ship_to_Name;
    }


    /**
     * Gets the ship_to_Address value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_Address
     */
    public java.lang.String getShip_to_Address() {
        return ship_to_Address;
    }


    /**
     * Sets the ship_to_Address value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_Address
     */
    public void setShip_to_Address(java.lang.String ship_to_Address) {
        this.ship_to_Address = ship_to_Address;
    }


    /**
     * Gets the ship_to_Address_2 value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_Address_2
     */
    public java.lang.String getShip_to_Address_2() {
        return ship_to_Address_2;
    }


    /**
     * Sets the ship_to_Address_2 value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_Address_2
     */
    public void setShip_to_Address_2(java.lang.String ship_to_Address_2) {
        this.ship_to_Address_2 = ship_to_Address_2;
    }


    /**
     * Gets the ship_to_Post_Code value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_Post_Code
     */
    public java.lang.String getShip_to_Post_Code() {
        return ship_to_Post_Code;
    }


    /**
     * Sets the ship_to_Post_Code value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_Post_Code
     */
    public void setShip_to_Post_Code(java.lang.String ship_to_Post_Code) {
        this.ship_to_Post_Code = ship_to_Post_Code;
    }


    /**
     * Gets the ship_to_City value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_City
     */
    public java.lang.String getShip_to_City() {
        return ship_to_City;
    }


    /**
     * Sets the ship_to_City value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_City
     */
    public void setShip_to_City(java.lang.String ship_to_City) {
        this.ship_to_City = ship_to_City;
    }


    /**
     * Gets the ship_to_County value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_County
     */
    public java.lang.String getShip_to_County() {
        return ship_to_County;
    }


    /**
     * Sets the ship_to_County value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_County
     */
    public void setShip_to_County(java.lang.String ship_to_County) {
        this.ship_to_County = ship_to_County;
    }


    /**
     * Gets the ship_to_Contact value for this WS_ReqInternaCompra.
     * 
     * @return ship_to_Contact
     */
    public java.lang.String getShip_to_Contact() {
        return ship_to_Contact;
    }


    /**
     * Sets the ship_to_Contact value for this WS_ReqInternaCompra.
     * 
     * @param ship_to_Contact
     */
    public void setShip_to_Contact(java.lang.String ship_to_Contact) {
        this.ship_to_Contact = ship_to_Contact;
    }


    /**
     * Gets the posting_Date value for this WS_ReqInternaCompra.
     * 
     * @return posting_Date
     */
    public java.util.Date getPosting_Date() {
        return posting_Date;
    }


    /**
     * Sets the posting_Date value for this WS_ReqInternaCompra.
     * 
     * @param posting_Date
     */
    public void setPosting_Date(java.util.Date posting_Date) {
        this.posting_Date = posting_Date;
    }


    /**
     * Gets the document_Date value for this WS_ReqInternaCompra.
     * 
     * @return document_Date
     */
    public java.util.Date getDocument_Date() {
        return document_Date;
    }


    /**
     * Sets the document_Date value for this WS_ReqInternaCompra.
     * 
     * @param document_Date
     */
    public void setDocument_Date(java.util.Date document_Date) {
        this.document_Date = document_Date;
    }


    /**
     * Gets the user_ID value for this WS_ReqInternaCompra.
     * 
     * @return user_ID
     */
    public java.lang.String getUser_ID() {
        return user_ID;
    }


    /**
     * Sets the user_ID value for this WS_ReqInternaCompra.
     * 
     * @param user_ID
     */
    public void setUser_ID(java.lang.String user_ID) {
        this.user_ID = user_ID;
    }


    /**
     * Gets the location_Code value for this WS_ReqInternaCompra.
     * 
     * @return location_Code
     */
    public java.lang.String getLocation_Code() {
        return location_Code;
    }


    /**
     * Sets the location_Code value for this WS_ReqInternaCompra.
     * 
     * @param location_Code
     */
    public void setLocation_Code(java.lang.String location_Code) {
        this.location_Code = location_Code;
    }


    /**
     * Gets the shipment_Method_Code value for this WS_ReqInternaCompra.
     * 
     * @return shipment_Method_Code
     */
    public java.lang.String getShipment_Method_Code() {
        return shipment_Method_Code;
    }


    /**
     * Sets the shipment_Method_Code value for this WS_ReqInternaCompra.
     * 
     * @param shipment_Method_Code
     */
    public void setShipment_Method_Code(java.lang.String shipment_Method_Code) {
        this.shipment_Method_Code = shipment_Method_Code;
    }


    /**
     * Gets the expected_Receipt_Date value for this WS_ReqInternaCompra.
     * 
     * @return expected_Receipt_Date
     */
    public java.util.Date getExpected_Receipt_Date() {
        return expected_Receipt_Date;
    }


    /**
     * Sets the expected_Receipt_Date value for this WS_ReqInternaCompra.
     * 
     * @param expected_Receipt_Date
     */
    public void setExpected_Receipt_Date(java.util.Date expected_Receipt_Date) {
        this.expected_Receipt_Date = expected_Receipt_Date;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No value for this WS_ReqInternaCompra.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No value for this WS_ReqInternaCompra.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec value for this WS_ReqInternaCompra.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec value for this WS_ReqInternaCompra.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec;
    }


    /**
     * Gets the purchLines value for this WS_ReqInternaCompra.
     * 
     * @return purchLines
     */
    public microsoft_dynamics_schemas.Purchase_Internal_Req_Line[] getPurchLines() {
        return purchLines;
    }


    /**
     * Sets the purchLines value for this WS_ReqInternaCompra.
     * 
     * @param purchLines
     */
    public void setPurchLines(microsoft_dynamics_schemas.Purchase_Internal_Req_Line[] purchLines) {
        this.purchLines = purchLines;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_ReqInternaCompra)) return false;
        WS_ReqInternaCompra other = (WS_ReqInternaCompra) obj;
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
            ((this.user_ID==null && other.getUser_ID()==null) || 
             (this.user_ID!=null &&
              this.user_ID.equals(other.getUser_ID()))) &&
            ((this.location_Code==null && other.getLocation_Code()==null) || 
             (this.location_Code!=null &&
              this.location_Code.equals(other.getLocation_Code()))) &&
            ((this.shipment_Method_Code==null && other.getShipment_Method_Code()==null) || 
             (this.shipment_Method_Code!=null &&
              this.shipment_Method_Code.equals(other.getShipment_Method_Code()))) &&
            ((this.expected_Receipt_Date==null && other.getExpected_Receipt_Date()==null) || 
             (this.expected_Receipt_Date!=null &&
              this.expected_Receipt_Date.equals(other.getExpected_Receipt_Date()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec()))) &&
            ((this.purchLines==null && other.getPurchLines()==null) || 
             (this.purchLines!=null &&
              java.util.Arrays.equals(this.purchLines, other.getPurchLines())));
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
        if (getUser_ID() != null) {
            _hashCode += getUser_ID().hashCode();
        }
        if (getLocation_Code() != null) {
            _hashCode += getLocation_Code().hashCode();
        }
        if (getShipment_Method_Code() != null) {
            _hashCode += getShipment_Method_Code().hashCode();
        }
        if (getExpected_Receipt_Date() != null) {
            _hashCode += getExpected_Receipt_Date().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec().hashCode();
        }
        if (getPurchLines() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPurchLines());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPurchLines(), i);
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
        new org.apache.axis.description.TypeDesc(WS_ReqInternaCompra.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "WS_ReqInternaCompra"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("service");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Service"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Address");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Address_2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_Address_2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Post_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_Post_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_City");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_City"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_County");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_County"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ship_to_Contact");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Ship_to_Contact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posting_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Posting_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Document_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user_ID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "User_ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Location_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipment_Method_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Shipment_Method_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expected_Receipt_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Expected_Receipt_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfOrderAddr_Buy_from_Vendor_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgmt_CalcNoOfContacts_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchLines");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "PurchLines"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Purchase_Internal_Req_Line"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Purchase_Internal_Req_Line"));
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
