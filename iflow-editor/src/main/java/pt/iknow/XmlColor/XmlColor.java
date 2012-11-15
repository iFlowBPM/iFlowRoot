/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlColor.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlColor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public class XmlColor extends pt.iknow.XmlColor.XmlColorType 
implements java.io.Serializable
{


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlColor() {
        super();
    } //-- pt.iknow.XmlColor.XmlColor()


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
    public static pt.iknow.XmlColor.XmlColor unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iknow.XmlColor.XmlColor) Unmarshaller.unmarshal(pt.iknow.XmlColor.XmlColor.class, reader);
    } //-- pt.iknow.XmlColor.XmlColor unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
