/**
 * WS_Lista_Produtos.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Lista_Produtos  implements java.io.Serializable {
    private java.lang.String key;

    private java.lang.String no;

    private java.lang.String name;

    private java.lang.String responsibility_Center;

    private java.lang.String location_Code;

    private java.lang.String post_Code;

    private java.lang.String country_Region_Code;

    private java.lang.String phone_No;

    private java.lang.String fax_No;

    private java.lang.String IC_Partner_Code;

    private java.lang.String contact;

    private java.lang.String salesperson_Code;

    private java.lang.String customer_Posting_Group;

    private java.lang.String gen_Bus_Posting_Group;

    private java.lang.String VAT_Bus_Posting_Group;

    private java.lang.String customer_Price_Group;

    private java.lang.String customer_Disc_Group;

    private java.lang.String payment_Terms_Code;

    private java.lang.String reminder_Terms_Code;

    private java.lang.String fin_Charge_Terms_Code;

    private java.lang.String currency_Code;

    private java.lang.String language_Code;

    private java.lang.String search_Name;

    private java.math.BigDecimal credit_Limit_LCY;

    private microsoft_dynamics_schemas.Blocked blocked;

    private java.util.Date last_Date_Modified;

    private microsoft_dynamics_schemas.Application_Method application_Method;

    private java.lang.Boolean combine_Shipments;

    private microsoft_dynamics_schemas.Reserve reserve;

    private microsoft_dynamics_schemas.Shipping_Advice shipping_Advice;

    private java.lang.String shipping_Agent_Code;

    private java.lang.String base_Calendar_Code;

    public WS_Lista_Produtos() {
    }

    public WS_Lista_Produtos(
           java.lang.String key,
           java.lang.String no,
           java.lang.String name,
           java.lang.String responsibility_Center,
           java.lang.String location_Code,
           java.lang.String post_Code,
           java.lang.String country_Region_Code,
           java.lang.String phone_No,
           java.lang.String fax_No,
           java.lang.String IC_Partner_Code,
           java.lang.String contact,
           java.lang.String salesperson_Code,
           java.lang.String customer_Posting_Group,
           java.lang.String gen_Bus_Posting_Group,
           java.lang.String VAT_Bus_Posting_Group,
           java.lang.String customer_Price_Group,
           java.lang.String customer_Disc_Group,
           java.lang.String payment_Terms_Code,
           java.lang.String reminder_Terms_Code,
           java.lang.String fin_Charge_Terms_Code,
           java.lang.String currency_Code,
           java.lang.String language_Code,
           java.lang.String search_Name,
           java.math.BigDecimal credit_Limit_LCY,
           microsoft_dynamics_schemas.Blocked blocked,
           java.util.Date last_Date_Modified,
           microsoft_dynamics_schemas.Application_Method application_Method,
           java.lang.Boolean combine_Shipments,
           microsoft_dynamics_schemas.Reserve reserve,
           microsoft_dynamics_schemas.Shipping_Advice shipping_Advice,
           java.lang.String shipping_Agent_Code,
           java.lang.String base_Calendar_Code) {
           this.key = key;
           this.no = no;
           this.name = name;
           this.responsibility_Center = responsibility_Center;
           this.location_Code = location_Code;
           this.post_Code = post_Code;
           this.country_Region_Code = country_Region_Code;
           this.phone_No = phone_No;
           this.fax_No = fax_No;
           this.IC_Partner_Code = IC_Partner_Code;
           this.contact = contact;
           this.salesperson_Code = salesperson_Code;
           this.customer_Posting_Group = customer_Posting_Group;
           this.gen_Bus_Posting_Group = gen_Bus_Posting_Group;
           this.VAT_Bus_Posting_Group = VAT_Bus_Posting_Group;
           this.customer_Price_Group = customer_Price_Group;
           this.customer_Disc_Group = customer_Disc_Group;
           this.payment_Terms_Code = payment_Terms_Code;
           this.reminder_Terms_Code = reminder_Terms_Code;
           this.fin_Charge_Terms_Code = fin_Charge_Terms_Code;
           this.currency_Code = currency_Code;
           this.language_Code = language_Code;
           this.search_Name = search_Name;
           this.credit_Limit_LCY = credit_Limit_LCY;
           this.blocked = blocked;
           this.last_Date_Modified = last_Date_Modified;
           this.application_Method = application_Method;
           this.combine_Shipments = combine_Shipments;
           this.reserve = reserve;
           this.shipping_Advice = shipping_Advice;
           this.shipping_Agent_Code = shipping_Agent_Code;
           this.base_Calendar_Code = base_Calendar_Code;
    }


    /**
     * Gets the key value for this WS_Lista_Produtos.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this WS_Lista_Produtos.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this WS_Lista_Produtos.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this WS_Lista_Produtos.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the name value for this WS_Lista_Produtos.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this WS_Lista_Produtos.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the responsibility_Center value for this WS_Lista_Produtos.
     * 
     * @return responsibility_Center
     */
    public java.lang.String getResponsibility_Center() {
        return responsibility_Center;
    }


    /**
     * Sets the responsibility_Center value for this WS_Lista_Produtos.
     * 
     * @param responsibility_Center
     */
    public void setResponsibility_Center(java.lang.String responsibility_Center) {
        this.responsibility_Center = responsibility_Center;
    }


    /**
     * Gets the location_Code value for this WS_Lista_Produtos.
     * 
     * @return location_Code
     */
    public java.lang.String getLocation_Code() {
        return location_Code;
    }


    /**
     * Sets the location_Code value for this WS_Lista_Produtos.
     * 
     * @param location_Code
     */
    public void setLocation_Code(java.lang.String location_Code) {
        this.location_Code = location_Code;
    }


    /**
     * Gets the post_Code value for this WS_Lista_Produtos.
     * 
     * @return post_Code
     */
    public java.lang.String getPost_Code() {
        return post_Code;
    }


    /**
     * Sets the post_Code value for this WS_Lista_Produtos.
     * 
     * @param post_Code
     */
    public void setPost_Code(java.lang.String post_Code) {
        this.post_Code = post_Code;
    }


    /**
     * Gets the country_Region_Code value for this WS_Lista_Produtos.
     * 
     * @return country_Region_Code
     */
    public java.lang.String getCountry_Region_Code() {
        return country_Region_Code;
    }


    /**
     * Sets the country_Region_Code value for this WS_Lista_Produtos.
     * 
     * @param country_Region_Code
     */
    public void setCountry_Region_Code(java.lang.String country_Region_Code) {
        this.country_Region_Code = country_Region_Code;
    }


    /**
     * Gets the phone_No value for this WS_Lista_Produtos.
     * 
     * @return phone_No
     */
    public java.lang.String getPhone_No() {
        return phone_No;
    }


    /**
     * Sets the phone_No value for this WS_Lista_Produtos.
     * 
     * @param phone_No
     */
    public void setPhone_No(java.lang.String phone_No) {
        this.phone_No = phone_No;
    }


    /**
     * Gets the fax_No value for this WS_Lista_Produtos.
     * 
     * @return fax_No
     */
    public java.lang.String getFax_No() {
        return fax_No;
    }


    /**
     * Sets the fax_No value for this WS_Lista_Produtos.
     * 
     * @param fax_No
     */
    public void setFax_No(java.lang.String fax_No) {
        this.fax_No = fax_No;
    }


    /**
     * Gets the IC_Partner_Code value for this WS_Lista_Produtos.
     * 
     * @return IC_Partner_Code
     */
    public java.lang.String getIC_Partner_Code() {
        return IC_Partner_Code;
    }


    /**
     * Sets the IC_Partner_Code value for this WS_Lista_Produtos.
     * 
     * @param IC_Partner_Code
     */
    public void setIC_Partner_Code(java.lang.String IC_Partner_Code) {
        this.IC_Partner_Code = IC_Partner_Code;
    }


    /**
     * Gets the contact value for this WS_Lista_Produtos.
     * 
     * @return contact
     */
    public java.lang.String getContact() {
        return contact;
    }


    /**
     * Sets the contact value for this WS_Lista_Produtos.
     * 
     * @param contact
     */
    public void setContact(java.lang.String contact) {
        this.contact = contact;
    }


    /**
     * Gets the salesperson_Code value for this WS_Lista_Produtos.
     * 
     * @return salesperson_Code
     */
    public java.lang.String getSalesperson_Code() {
        return salesperson_Code;
    }


    /**
     * Sets the salesperson_Code value for this WS_Lista_Produtos.
     * 
     * @param salesperson_Code
     */
    public void setSalesperson_Code(java.lang.String salesperson_Code) {
        this.salesperson_Code = salesperson_Code;
    }


    /**
     * Gets the customer_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @return customer_Posting_Group
     */
    public java.lang.String getCustomer_Posting_Group() {
        return customer_Posting_Group;
    }


    /**
     * Sets the customer_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @param customer_Posting_Group
     */
    public void setCustomer_Posting_Group(java.lang.String customer_Posting_Group) {
        this.customer_Posting_Group = customer_Posting_Group;
    }


    /**
     * Gets the gen_Bus_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @return gen_Bus_Posting_Group
     */
    public java.lang.String getGen_Bus_Posting_Group() {
        return gen_Bus_Posting_Group;
    }


    /**
     * Sets the gen_Bus_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @param gen_Bus_Posting_Group
     */
    public void setGen_Bus_Posting_Group(java.lang.String gen_Bus_Posting_Group) {
        this.gen_Bus_Posting_Group = gen_Bus_Posting_Group;
    }


    /**
     * Gets the VAT_Bus_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @return VAT_Bus_Posting_Group
     */
    public java.lang.String getVAT_Bus_Posting_Group() {
        return VAT_Bus_Posting_Group;
    }


    /**
     * Sets the VAT_Bus_Posting_Group value for this WS_Lista_Produtos.
     * 
     * @param VAT_Bus_Posting_Group
     */
    public void setVAT_Bus_Posting_Group(java.lang.String VAT_Bus_Posting_Group) {
        this.VAT_Bus_Posting_Group = VAT_Bus_Posting_Group;
    }


    /**
     * Gets the customer_Price_Group value for this WS_Lista_Produtos.
     * 
     * @return customer_Price_Group
     */
    public java.lang.String getCustomer_Price_Group() {
        return customer_Price_Group;
    }


    /**
     * Sets the customer_Price_Group value for this WS_Lista_Produtos.
     * 
     * @param customer_Price_Group
     */
    public void setCustomer_Price_Group(java.lang.String customer_Price_Group) {
        this.customer_Price_Group = customer_Price_Group;
    }


    /**
     * Gets the customer_Disc_Group value for this WS_Lista_Produtos.
     * 
     * @return customer_Disc_Group
     */
    public java.lang.String getCustomer_Disc_Group() {
        return customer_Disc_Group;
    }


    /**
     * Sets the customer_Disc_Group value for this WS_Lista_Produtos.
     * 
     * @param customer_Disc_Group
     */
    public void setCustomer_Disc_Group(java.lang.String customer_Disc_Group) {
        this.customer_Disc_Group = customer_Disc_Group;
    }


    /**
     * Gets the payment_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @return payment_Terms_Code
     */
    public java.lang.String getPayment_Terms_Code() {
        return payment_Terms_Code;
    }


    /**
     * Sets the payment_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @param payment_Terms_Code
     */
    public void setPayment_Terms_Code(java.lang.String payment_Terms_Code) {
        this.payment_Terms_Code = payment_Terms_Code;
    }


    /**
     * Gets the reminder_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @return reminder_Terms_Code
     */
    public java.lang.String getReminder_Terms_Code() {
        return reminder_Terms_Code;
    }


    /**
     * Sets the reminder_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @param reminder_Terms_Code
     */
    public void setReminder_Terms_Code(java.lang.String reminder_Terms_Code) {
        this.reminder_Terms_Code = reminder_Terms_Code;
    }


    /**
     * Gets the fin_Charge_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @return fin_Charge_Terms_Code
     */
    public java.lang.String getFin_Charge_Terms_Code() {
        return fin_Charge_Terms_Code;
    }


    /**
     * Sets the fin_Charge_Terms_Code value for this WS_Lista_Produtos.
     * 
     * @param fin_Charge_Terms_Code
     */
    public void setFin_Charge_Terms_Code(java.lang.String fin_Charge_Terms_Code) {
        this.fin_Charge_Terms_Code = fin_Charge_Terms_Code;
    }


    /**
     * Gets the currency_Code value for this WS_Lista_Produtos.
     * 
     * @return currency_Code
     */
    public java.lang.String getCurrency_Code() {
        return currency_Code;
    }


    /**
     * Sets the currency_Code value for this WS_Lista_Produtos.
     * 
     * @param currency_Code
     */
    public void setCurrency_Code(java.lang.String currency_Code) {
        this.currency_Code = currency_Code;
    }


    /**
     * Gets the language_Code value for this WS_Lista_Produtos.
     * 
     * @return language_Code
     */
    public java.lang.String getLanguage_Code() {
        return language_Code;
    }


    /**
     * Sets the language_Code value for this WS_Lista_Produtos.
     * 
     * @param language_Code
     */
    public void setLanguage_Code(java.lang.String language_Code) {
        this.language_Code = language_Code;
    }


    /**
     * Gets the search_Name value for this WS_Lista_Produtos.
     * 
     * @return search_Name
     */
    public java.lang.String getSearch_Name() {
        return search_Name;
    }


    /**
     * Sets the search_Name value for this WS_Lista_Produtos.
     * 
     * @param search_Name
     */
    public void setSearch_Name(java.lang.String search_Name) {
        this.search_Name = search_Name;
    }


    /**
     * Gets the credit_Limit_LCY value for this WS_Lista_Produtos.
     * 
     * @return credit_Limit_LCY
     */
    public java.math.BigDecimal getCredit_Limit_LCY() {
        return credit_Limit_LCY;
    }


    /**
     * Sets the credit_Limit_LCY value for this WS_Lista_Produtos.
     * 
     * @param credit_Limit_LCY
     */
    public void setCredit_Limit_LCY(java.math.BigDecimal credit_Limit_LCY) {
        this.credit_Limit_LCY = credit_Limit_LCY;
    }


    /**
     * Gets the blocked value for this WS_Lista_Produtos.
     * 
     * @return blocked
     */
    public microsoft_dynamics_schemas.Blocked getBlocked() {
        return blocked;
    }


    /**
     * Sets the blocked value for this WS_Lista_Produtos.
     * 
     * @param blocked
     */
    public void setBlocked(microsoft_dynamics_schemas.Blocked blocked) {
        this.blocked = blocked;
    }


    /**
     * Gets the last_Date_Modified value for this WS_Lista_Produtos.
     * 
     * @return last_Date_Modified
     */
    public java.util.Date getLast_Date_Modified() {
        return last_Date_Modified;
    }


    /**
     * Sets the last_Date_Modified value for this WS_Lista_Produtos.
     * 
     * @param last_Date_Modified
     */
    public void setLast_Date_Modified(java.util.Date last_Date_Modified) {
        this.last_Date_Modified = last_Date_Modified;
    }


    /**
     * Gets the application_Method value for this WS_Lista_Produtos.
     * 
     * @return application_Method
     */
    public microsoft_dynamics_schemas.Application_Method getApplication_Method() {
        return application_Method;
    }


    /**
     * Sets the application_Method value for this WS_Lista_Produtos.
     * 
     * @param application_Method
     */
    public void setApplication_Method(microsoft_dynamics_schemas.Application_Method application_Method) {
        this.application_Method = application_Method;
    }


    /**
     * Gets the combine_Shipments value for this WS_Lista_Produtos.
     * 
     * @return combine_Shipments
     */
    public java.lang.Boolean getCombine_Shipments() {
        return combine_Shipments;
    }


    /**
     * Sets the combine_Shipments value for this WS_Lista_Produtos.
     * 
     * @param combine_Shipments
     */
    public void setCombine_Shipments(java.lang.Boolean combine_Shipments) {
        this.combine_Shipments = combine_Shipments;
    }


    /**
     * Gets the reserve value for this WS_Lista_Produtos.
     * 
     * @return reserve
     */
    public microsoft_dynamics_schemas.Reserve getReserve() {
        return reserve;
    }


    /**
     * Sets the reserve value for this WS_Lista_Produtos.
     * 
     * @param reserve
     */
    public void setReserve(microsoft_dynamics_schemas.Reserve reserve) {
        this.reserve = reserve;
    }


    /**
     * Gets the shipping_Advice value for this WS_Lista_Produtos.
     * 
     * @return shipping_Advice
     */
    public microsoft_dynamics_schemas.Shipping_Advice getShipping_Advice() {
        return shipping_Advice;
    }


    /**
     * Sets the shipping_Advice value for this WS_Lista_Produtos.
     * 
     * @param shipping_Advice
     */
    public void setShipping_Advice(microsoft_dynamics_schemas.Shipping_Advice shipping_Advice) {
        this.shipping_Advice = shipping_Advice;
    }


    /**
     * Gets the shipping_Agent_Code value for this WS_Lista_Produtos.
     * 
     * @return shipping_Agent_Code
     */
    public java.lang.String getShipping_Agent_Code() {
        return shipping_Agent_Code;
    }


    /**
     * Sets the shipping_Agent_Code value for this WS_Lista_Produtos.
     * 
     * @param shipping_Agent_Code
     */
    public void setShipping_Agent_Code(java.lang.String shipping_Agent_Code) {
        this.shipping_Agent_Code = shipping_Agent_Code;
    }


    /**
     * Gets the base_Calendar_Code value for this WS_Lista_Produtos.
     * 
     * @return base_Calendar_Code
     */
    public java.lang.String getBase_Calendar_Code() {
        return base_Calendar_Code;
    }


    /**
     * Sets the base_Calendar_Code value for this WS_Lista_Produtos.
     * 
     * @param base_Calendar_Code
     */
    public void setBase_Calendar_Code(java.lang.String base_Calendar_Code) {
        this.base_Calendar_Code = base_Calendar_Code;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_Lista_Produtos)) return false;
        WS_Lista_Produtos other = (WS_Lista_Produtos) obj;
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
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.responsibility_Center==null && other.getResponsibility_Center()==null) || 
             (this.responsibility_Center!=null &&
              this.responsibility_Center.equals(other.getResponsibility_Center()))) &&
            ((this.location_Code==null && other.getLocation_Code()==null) || 
             (this.location_Code!=null &&
              this.location_Code.equals(other.getLocation_Code()))) &&
            ((this.post_Code==null && other.getPost_Code()==null) || 
             (this.post_Code!=null &&
              this.post_Code.equals(other.getPost_Code()))) &&
            ((this.country_Region_Code==null && other.getCountry_Region_Code()==null) || 
             (this.country_Region_Code!=null &&
              this.country_Region_Code.equals(other.getCountry_Region_Code()))) &&
            ((this.phone_No==null && other.getPhone_No()==null) || 
             (this.phone_No!=null &&
              this.phone_No.equals(other.getPhone_No()))) &&
            ((this.fax_No==null && other.getFax_No()==null) || 
             (this.fax_No!=null &&
              this.fax_No.equals(other.getFax_No()))) &&
            ((this.IC_Partner_Code==null && other.getIC_Partner_Code()==null) || 
             (this.IC_Partner_Code!=null &&
              this.IC_Partner_Code.equals(other.getIC_Partner_Code()))) &&
            ((this.contact==null && other.getContact()==null) || 
             (this.contact!=null &&
              this.contact.equals(other.getContact()))) &&
            ((this.salesperson_Code==null && other.getSalesperson_Code()==null) || 
             (this.salesperson_Code!=null &&
              this.salesperson_Code.equals(other.getSalesperson_Code()))) &&
            ((this.customer_Posting_Group==null && other.getCustomer_Posting_Group()==null) || 
             (this.customer_Posting_Group!=null &&
              this.customer_Posting_Group.equals(other.getCustomer_Posting_Group()))) &&
            ((this.gen_Bus_Posting_Group==null && other.getGen_Bus_Posting_Group()==null) || 
             (this.gen_Bus_Posting_Group!=null &&
              this.gen_Bus_Posting_Group.equals(other.getGen_Bus_Posting_Group()))) &&
            ((this.VAT_Bus_Posting_Group==null && other.getVAT_Bus_Posting_Group()==null) || 
             (this.VAT_Bus_Posting_Group!=null &&
              this.VAT_Bus_Posting_Group.equals(other.getVAT_Bus_Posting_Group()))) &&
            ((this.customer_Price_Group==null && other.getCustomer_Price_Group()==null) || 
             (this.customer_Price_Group!=null &&
              this.customer_Price_Group.equals(other.getCustomer_Price_Group()))) &&
            ((this.customer_Disc_Group==null && other.getCustomer_Disc_Group()==null) || 
             (this.customer_Disc_Group!=null &&
              this.customer_Disc_Group.equals(other.getCustomer_Disc_Group()))) &&
            ((this.payment_Terms_Code==null && other.getPayment_Terms_Code()==null) || 
             (this.payment_Terms_Code!=null &&
              this.payment_Terms_Code.equals(other.getPayment_Terms_Code()))) &&
            ((this.reminder_Terms_Code==null && other.getReminder_Terms_Code()==null) || 
             (this.reminder_Terms_Code!=null &&
              this.reminder_Terms_Code.equals(other.getReminder_Terms_Code()))) &&
            ((this.fin_Charge_Terms_Code==null && other.getFin_Charge_Terms_Code()==null) || 
             (this.fin_Charge_Terms_Code!=null &&
              this.fin_Charge_Terms_Code.equals(other.getFin_Charge_Terms_Code()))) &&
            ((this.currency_Code==null && other.getCurrency_Code()==null) || 
             (this.currency_Code!=null &&
              this.currency_Code.equals(other.getCurrency_Code()))) &&
            ((this.language_Code==null && other.getLanguage_Code()==null) || 
             (this.language_Code!=null &&
              this.language_Code.equals(other.getLanguage_Code()))) &&
            ((this.search_Name==null && other.getSearch_Name()==null) || 
             (this.search_Name!=null &&
              this.search_Name.equals(other.getSearch_Name()))) &&
            ((this.credit_Limit_LCY==null && other.getCredit_Limit_LCY()==null) || 
             (this.credit_Limit_LCY!=null &&
              this.credit_Limit_LCY.equals(other.getCredit_Limit_LCY()))) &&
            ((this.blocked==null && other.getBlocked()==null) || 
             (this.blocked!=null &&
              this.blocked.equals(other.getBlocked()))) &&
            ((this.last_Date_Modified==null && other.getLast_Date_Modified()==null) || 
             (this.last_Date_Modified!=null &&
              this.last_Date_Modified.equals(other.getLast_Date_Modified()))) &&
            ((this.application_Method==null && other.getApplication_Method()==null) || 
             (this.application_Method!=null &&
              this.application_Method.equals(other.getApplication_Method()))) &&
            ((this.combine_Shipments==null && other.getCombine_Shipments()==null) || 
             (this.combine_Shipments!=null &&
              this.combine_Shipments.equals(other.getCombine_Shipments()))) &&
            ((this.reserve==null && other.getReserve()==null) || 
             (this.reserve!=null &&
              this.reserve.equals(other.getReserve()))) &&
            ((this.shipping_Advice==null && other.getShipping_Advice()==null) || 
             (this.shipping_Advice!=null &&
              this.shipping_Advice.equals(other.getShipping_Advice()))) &&
            ((this.shipping_Agent_Code==null && other.getShipping_Agent_Code()==null) || 
             (this.shipping_Agent_Code!=null &&
              this.shipping_Agent_Code.equals(other.getShipping_Agent_Code()))) &&
            ((this.base_Calendar_Code==null && other.getBase_Calendar_Code()==null) || 
             (this.base_Calendar_Code!=null &&
              this.base_Calendar_Code.equals(other.getBase_Calendar_Code())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getResponsibility_Center() != null) {
            _hashCode += getResponsibility_Center().hashCode();
        }
        if (getLocation_Code() != null) {
            _hashCode += getLocation_Code().hashCode();
        }
        if (getPost_Code() != null) {
            _hashCode += getPost_Code().hashCode();
        }
        if (getCountry_Region_Code() != null) {
            _hashCode += getCountry_Region_Code().hashCode();
        }
        if (getPhone_No() != null) {
            _hashCode += getPhone_No().hashCode();
        }
        if (getFax_No() != null) {
            _hashCode += getFax_No().hashCode();
        }
        if (getIC_Partner_Code() != null) {
            _hashCode += getIC_Partner_Code().hashCode();
        }
        if (getContact() != null) {
            _hashCode += getContact().hashCode();
        }
        if (getSalesperson_Code() != null) {
            _hashCode += getSalesperson_Code().hashCode();
        }
        if (getCustomer_Posting_Group() != null) {
            _hashCode += getCustomer_Posting_Group().hashCode();
        }
        if (getGen_Bus_Posting_Group() != null) {
            _hashCode += getGen_Bus_Posting_Group().hashCode();
        }
        if (getVAT_Bus_Posting_Group() != null) {
            _hashCode += getVAT_Bus_Posting_Group().hashCode();
        }
        if (getCustomer_Price_Group() != null) {
            _hashCode += getCustomer_Price_Group().hashCode();
        }
        if (getCustomer_Disc_Group() != null) {
            _hashCode += getCustomer_Disc_Group().hashCode();
        }
        if (getPayment_Terms_Code() != null) {
            _hashCode += getPayment_Terms_Code().hashCode();
        }
        if (getReminder_Terms_Code() != null) {
            _hashCode += getReminder_Terms_Code().hashCode();
        }
        if (getFin_Charge_Terms_Code() != null) {
            _hashCode += getFin_Charge_Terms_Code().hashCode();
        }
        if (getCurrency_Code() != null) {
            _hashCode += getCurrency_Code().hashCode();
        }
        if (getLanguage_Code() != null) {
            _hashCode += getLanguage_Code().hashCode();
        }
        if (getSearch_Name() != null) {
            _hashCode += getSearch_Name().hashCode();
        }
        if (getCredit_Limit_LCY() != null) {
            _hashCode += getCredit_Limit_LCY().hashCode();
        }
        if (getBlocked() != null) {
            _hashCode += getBlocked().hashCode();
        }
        if (getLast_Date_Modified() != null) {
            _hashCode += getLast_Date_Modified().hashCode();
        }
        if (getApplication_Method() != null) {
            _hashCode += getApplication_Method().hashCode();
        }
        if (getCombine_Shipments() != null) {
            _hashCode += getCombine_Shipments().hashCode();
        }
        if (getReserve() != null) {
            _hashCode += getReserve().hashCode();
        }
        if (getShipping_Advice() != null) {
            _hashCode += getShipping_Advice().hashCode();
        }
        if (getShipping_Agent_Code() != null) {
            _hashCode += getShipping_Agent_Code().hashCode();
        }
        if (getBase_Calendar_Code() != null) {
            _hashCode += getBase_Calendar_Code().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WS_Lista_Produtos.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "WS_Lista_Produtos"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responsibility_Center");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Responsibility_Center"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Location_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("post_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Post_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country_Region_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Country_Region_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Phone_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fax_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Fax_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IC_Partner_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "IC_Partner_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contact");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Contact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salesperson_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Salesperson_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Customer_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gen_Bus_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Gen_Bus_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VAT_Bus_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "VAT_Bus_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_Price_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Customer_Price_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_Disc_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Customer_Disc_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payment_Terms_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Payment_Terms_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reminder_Terms_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Reminder_Terms_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fin_Charge_Terms_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Fin_Charge_Terms_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Currency_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("language_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Language_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("search_Name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Search_Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("credit_Limit_LCY");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Credit_Limit_LCY"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blocked");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Blocked"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Blocked"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last_Date_Modified");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Last_Date_Modified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("application_Method");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Application_Method"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Application_Method"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("combine_Shipments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Combine_Shipments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Reserve"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Reserve"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipping_Advice");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Shipping_Advice"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Shipping_Advice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipping_Agent_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Shipping_Agent_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("base_Calendar_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_lista_produtos", "Base_Calendar_Code"));
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
