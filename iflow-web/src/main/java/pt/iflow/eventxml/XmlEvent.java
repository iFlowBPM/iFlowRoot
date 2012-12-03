/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlEvent.java 2 2006-01-17 11:04:42Z jcosta $
 */

package pt.iflow.eventxml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * @version $Revision: 2 $ $Date: 2006-01-17 11:04:42 +0000 (ter, 17 Jan 2006) $
**/
public class XmlEvent implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _xmlPropertyList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlEvent() {
        super();
        _xmlPropertyList = new Vector();
    } //-- pt.iknow.taskEventXML.XmlEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vXmlProperty
    **/
    public void addXmlProperty(XmlProperty vXmlProperty)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlPropertyList.addElement(vXmlProperty);
    } //-- void addXmlProperty(XmlProperty) 

    /**
    **/
    public java.util.Enumeration enumerateXmlProperty()
    {
        return _xmlPropertyList.elements();
    } //-- java.util.Enumeration enumerateXmlProperty() 

    /**
     * 
     * @param index
    **/
    public XmlProperty getXmlProperty(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPropertyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (XmlProperty) _xmlPropertyList.elementAt(index);
    } //-- XmlProperty getXmlProperty(int) 

    /**
    **/
    public XmlProperty[] getXmlProperty()
    {
        int size = _xmlPropertyList.size();
        XmlProperty[] mArray = new XmlProperty[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (XmlProperty) _xmlPropertyList.elementAt(index);
        }
        return mArray;
    } //-- XmlProperty[] getXmlProperty() 

    /**
    **/
    public int getXmlPropertyCount()
    {
        return _xmlPropertyList.size();
    } //-- int getXmlPropertyCount() 

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
    public void removeAllXmlProperty()
    {
        _xmlPropertyList.removeAllElements();
    } //-- void removeAllXmlProperty() 

    /**
     * 
     * @param index
    **/
    public XmlProperty removeXmlProperty(int index)
    {
        Object obj = _xmlPropertyList.elementAt(index);
        _xmlPropertyList.removeElementAt(index);
        return (XmlProperty) obj;
    } //-- XmlProperty removeXmlProperty(int) 

    /**
     * 
     * @param index
     * @param vXmlProperty
    **/
    public void setXmlProperty(int index, XmlProperty vXmlProperty)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPropertyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlPropertyList.setElementAt(vXmlProperty, index);
    } //-- void setXmlProperty(int, XmlProperty) 

    /**
     * 
     * @param xmlPropertyArray
    **/
    public void setXmlProperty(XmlProperty[] xmlPropertyArray)
    {
        //-- copy array
        _xmlPropertyList.removeAllElements();
        for (int i = 0; i < xmlPropertyArray.length; i++) {
            _xmlPropertyList.addElement(xmlPropertyArray[i]);
        }
    } //-- void setXmlProperty(XmlProperty) 

    /**
     * 
     * @param reader
    **/
    public static pt.iflow.eventxml.XmlEvent unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iflow.eventxml.XmlEvent) Unmarshaller.unmarshal(pt.iflow.eventxml.XmlEvent.class, reader);
    } //-- pt.iknow.taskEventXML.XmlEvent unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
