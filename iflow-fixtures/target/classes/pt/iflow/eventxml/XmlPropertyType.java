/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlPropertyType.java 2 2006-01-17 11:04:42Z jcosta $
 */

package pt.iflow.eventxml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * @version $Revision: 2 $ $Date: 2006-01-17 11:04:42 +0000 (ter, 17 Jan 2006) $
**/
public abstract class XmlPropertyType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _key;

    private java.lang.String _value;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlPropertyType() {
        super();
    } //-- pt.iknow.taskEventXML.XmlPropertyType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getKey()
    {
        return this._key;
    } //-- java.lang.String getKey() 

    /**
    **/
    public java.lang.String getValue()
    {
        return this._value;
    } //-- java.lang.String getValue() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param key
    **/
    public void setKey(java.lang.String key)
    {
        this._key = key;
    } //-- void setKey(java.lang.String) 

    /**
     * 
     * @param value
    **/
    public void setValue(java.lang.String value)
    {
        this._value = value;
    } //-- void setValue(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
