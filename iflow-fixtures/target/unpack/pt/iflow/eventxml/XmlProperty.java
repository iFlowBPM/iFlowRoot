/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlProperty.java 2 2006-01-17 11:04:42Z jcosta $
 */

package pt.iflow.eventxml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * @version $Revision: 2 $ $Date: 2006-01-17 11:04:42 +0000 (ter, 17 Jan 2006) $
**/
public class XmlProperty extends pt.iflow.eventxml.XmlPropertyType 
implements java.io.Serializable
{


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlProperty() {
        super();
    } //-- pt.iknow.taskEventXML.XmlProperty()


      //-----------/
     //- Methods -/
    //-----------/

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * 
     * @param reader
    **/
    public static pt.iflow.eventxml.XmlProperty unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iflow.eventxml.XmlProperty) Unmarshaller.unmarshal(pt.iflow.eventxml.XmlProperty.class, reader);
    } //-- pt.iknow.taskEventXML.XmlProperty unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
