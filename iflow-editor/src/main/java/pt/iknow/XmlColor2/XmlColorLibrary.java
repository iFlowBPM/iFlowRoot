/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlColorLibrary.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlColor2;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public class XmlColorLibrary implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _xmlColorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlColorLibrary() {
        super();
        _xmlColorList = new Vector();
    } //-- pt.iknow.XmlColor2.XmlColorLibrary()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vXmlColor
    **/
    public void addXmlColor(XmlColor vXmlColor)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlColorList.addElement(vXmlColor);
    } //-- void addXmlColor(XmlColor) 

    /**
    **/
    public java.util.Enumeration enumerateXmlColor()
    {
        return _xmlColorList.elements();
    } //-- java.util.Enumeration enumerateXmlColor() 

    /**
     * 
     * @param index
    **/
    public XmlColor getXmlColor(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlColorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (XmlColor) _xmlColorList.elementAt(index);
    } //-- XmlColor getXmlColor(int) 

    /**
    **/
    public XmlColor[] getXmlColor()
    {
        int size = _xmlColorList.size();
        XmlColor[] mArray = new XmlColor[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (XmlColor) _xmlColorList.elementAt(index);
        }
        return mArray;
    } //-- XmlColor[] getXmlColor() 

    /**
    **/
    public int getXmlColorCount()
    {
        return _xmlColorList.size();
    } //-- int getXmlColorCount() 

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
    **/
    public void removeAllXmlColor()
    {
        _xmlColorList.removeAllElements();
    } //-- void removeAllXmlColor() 

    /**
     * 
     * @param index
    **/
    public XmlColor removeXmlColor(int index)
    {
        Object obj = _xmlColorList.elementAt(index);
        _xmlColorList.removeElementAt(index);
        return (XmlColor) obj;
    } //-- XmlColor removeXmlColor(int) 

    /**
     * 
     * @param index
     * @param vXmlColor
    **/
    public void setXmlColor(int index, XmlColor vXmlColor)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlColorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlColorList.setElementAt(vXmlColor, index);
    } //-- void setXmlColor(int, XmlColor) 

    /**
     * 
     * @param xmlColorArray
    **/
    public void setXmlColor(XmlColor[] xmlColorArray)
    {
        //-- copy array
        _xmlColorList.removeAllElements();
        for (int i = 0; i < xmlColorArray.length; i++) {
            _xmlColorList.addElement(xmlColorArray[i]);
        }
    } //-- void setXmlColor(XmlColor) 

    /**
     * 
     * @param reader
    **/
    public static pt.iknow.XmlColor2.XmlColorLibrary unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iknow.XmlColor2.XmlColorLibrary) Unmarshaller.unmarshal(pt.iknow.XmlColor2.XmlColorLibrary.class, reader);
    } //-- pt.iknow.XmlColor2.XmlColorLibrary unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
