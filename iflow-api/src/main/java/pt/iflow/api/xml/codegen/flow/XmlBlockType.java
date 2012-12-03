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
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package pt.iflow.api.xml.codegen.flow;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class XmlBlockType.
 * 
 * @version $Revision$ $Date$
 */
public class XmlBlockType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type
     */
    private java.lang.String _type;

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _id
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * Field _xmlPortList
     */
    private java.util.ArrayList _xmlPortList;

    /**
     * Field _xmlAttributeList
     */
    private java.util.ArrayList _xmlAttributeList;

    /**
     * Field _xmlPosition
     */
    private pt.iflow.api.xml.codegen.flow.XmlPosition _xmlPosition;

    /**
     * Field _popupReturnBlockId
     */
    private int _popupReturnBlockId = -1;

    /**
     * keeps track of state for field: _popupReturnBlockId
     */
    private boolean _has_popupReturnBlockId;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlBlockType() 
     {
        super();
        _xmlPortList = new ArrayList();
        _xmlAttributeList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.flow.XmlBlockType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addXmlAttribute
     * 
     * 
     * 
     * @param vXmlAttribute
     */
    public void addXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlAttributeList.add(vXmlAttribute);
    } //-- void addXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute) 

    /**
     * Method addXmlAttribute
     * 
     * 
     * 
     * @param index
     * @param vXmlAttribute
     */
    public void addXmlAttribute(int index, pt.iflow.api.xml.codegen.flow.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlAttributeList.add(index, vXmlAttribute);
    } //-- void addXmlAttribute(int, pt.iflow.api.xml.codegen.flow.XmlAttribute) 

    /**
     * Method addXmlPort
     * 
     * 
     * 
     * @param vXmlPort
     */
    public void addXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlPortList.add(vXmlPort);
    } //-- void addXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort) 

    /**
     * Method addXmlPort
     * 
     * 
     * 
     * @param index
     * @param vXmlPort
     */
    public void addXmlPort(int index, pt.iflow.api.xml.codegen.flow.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlPortList.add(index, vXmlPort);
    } //-- void addXmlPort(int, pt.iflow.api.xml.codegen.flow.XmlPort) 

    /**
     * Method clearXmlAttribute
     * 
     */
    public void clearXmlAttribute()
    {
        _xmlAttributeList.clear();
    } //-- void clearXmlAttribute() 

    /**
     * Method clearXmlPort
     * 
     */
    public void clearXmlPort()
    {
        _xmlPortList.clear();
    } //-- void clearXmlPort() 

    /**
     * Method deleteId
     * 
     */
    public void deleteId()
    {
        this._has_id= false;
    } //-- void deleteId() 

    /**
     * Method deletePopupReturnBlockId
     * 
     */
    public void deletePopupReturnBlockId()
    {
        this._has_popupReturnBlockId= false;
    } //-- void deletePopupReturnBlockId() 

    /**
     * Method enumerateXmlAttribute
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateXmlAttribute()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_xmlAttributeList.iterator());
    } //-- java.util.Enumeration enumerateXmlAttribute() 

    /**
     * Method enumerateXmlPort
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateXmlPort()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_xmlPortList.iterator());
    } //-- java.util.Enumeration enumerateXmlPort() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return int
     * @return the value of field 'id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'popupReturnBlockId'.
     * 
     * @return int
     * @return the value of field 'popupReturnBlockId'.
     */
    public int getPopupReturnBlockId()
    {
        return this._popupReturnBlockId;
    } //-- int getPopupReturnBlockId() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return String
     * @return the value of field 'type'.
     */
    public java.lang.String getType()
    {
        return this._type;
    } //-- java.lang.String getType() 

    /**
     * Method getXmlAttribute
     * 
     * 
     * 
     * @param index
     * @return XmlAttribute
     */
    public pt.iflow.api.xml.codegen.flow.XmlAttribute getXmlAttribute(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.flow.XmlAttribute) _xmlAttributeList.get(index);
    } //-- pt.iflow.api.xml.codegen.flow.XmlAttribute getXmlAttribute(int) 

    /**
     * Method getXmlAttribute
     * 
     * 
     * 
     * @return XmlAttribute
     */
    public pt.iflow.api.xml.codegen.flow.XmlAttribute[] getXmlAttribute()
    {
        int size = _xmlAttributeList.size();
        pt.iflow.api.xml.codegen.flow.XmlAttribute[] mArray = new pt.iflow.api.xml.codegen.flow.XmlAttribute[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.flow.XmlAttribute) _xmlAttributeList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.flow.XmlAttribute[] getXmlAttribute() 

    /**
     * Method getXmlAttributeCount
     * 
     * 
     * 
     * @return int
     */
    public int getXmlAttributeCount()
    {
        return _xmlAttributeList.size();
    } //-- int getXmlAttributeCount() 

    /**
     * Method getXmlPort
     * 
     * 
     * 
     * @param index
     * @return XmlPort
     */
    public pt.iflow.api.xml.codegen.flow.XmlPort getXmlPort(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPortList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.flow.XmlPort) _xmlPortList.get(index);
    } //-- pt.iflow.api.xml.codegen.flow.XmlPort getXmlPort(int) 

    /**
     * Method getXmlPort
     * 
     * 
     * 
     * @return XmlPort
     */
    public pt.iflow.api.xml.codegen.flow.XmlPort[] getXmlPort()
    {
        int size = _xmlPortList.size();
        pt.iflow.api.xml.codegen.flow.XmlPort[] mArray = new pt.iflow.api.xml.codegen.flow.XmlPort[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.flow.XmlPort) _xmlPortList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.flow.XmlPort[] getXmlPort() 

    /**
     * Method getXmlPortCount
     * 
     * 
     * 
     * @return int
     */
    public int getXmlPortCount()
    {
        return _xmlPortList.size();
    } //-- int getXmlPortCount() 

    /**
     * Returns the value of field 'xmlPosition'.
     * 
     * @return XmlPosition
     * @return the value of field 'xmlPosition'.
     */
    public pt.iflow.api.xml.codegen.flow.XmlPosition getXmlPosition()
    {
        return this._xmlPosition;
    } //-- pt.iflow.api.xml.codegen.flow.XmlPosition getXmlPosition() 

    /**
     * Method hasId
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasId()
    {
        return this._has_id;
    } //-- boolean hasId() 

    /**
     * Method hasPopupReturnBlockId
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasPopupReturnBlockId()
    {
        return this._has_popupReturnBlockId;
    } //-- boolean hasPopupReturnBlockId() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
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
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeXmlAttribute
     * 
     * 
     * 
     * @param vXmlAttribute
     * @return boolean
     */
    public boolean removeXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute vXmlAttribute)
    {
        boolean removed = _xmlAttributeList.remove(vXmlAttribute);
        return removed;
    } //-- boolean removeXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute) 

    /**
     * Method removeXmlPort
     * 
     * 
     * 
     * @param vXmlPort
     * @return boolean
     */
    public boolean removeXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort vXmlPort)
    {
        boolean removed = _xmlPortList.remove(vXmlPort);
        return removed;
    } //-- boolean removeXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(int id)
    {
        this._id = id;
        this._has_id = true;
    } //-- void setId(int) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'popupReturnBlockId'.
     * 
     * @param popupReturnBlockId the value of field
     * 'popupReturnBlockId'.
     */
    public void setPopupReturnBlockId(int popupReturnBlockId)
    {
        this._popupReturnBlockId = popupReturnBlockId;
        this._has_popupReturnBlockId = true;
    } //-- void setPopupReturnBlockId(int) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(java.lang.String type)
    {
        this._type = type;
    } //-- void setType(java.lang.String) 

    /**
     * Method setXmlAttribute
     * 
     * 
     * 
     * @param index
     * @param vXmlAttribute
     */
    public void setXmlAttribute(int index, pt.iflow.api.xml.codegen.flow.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlAttributeList.set(index, vXmlAttribute);
    } //-- void setXmlAttribute(int, pt.iflow.api.xml.codegen.flow.XmlAttribute) 

    /**
     * Method setXmlAttribute
     * 
     * 
     * 
     * @param xmlAttributeArray
     */
    public void setXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute[] xmlAttributeArray)
    {
        //-- copy array
        _xmlAttributeList.clear();
        for (int i = 0; i < xmlAttributeArray.length; i++) {
            _xmlAttributeList.add(xmlAttributeArray[i]);
        }
    } //-- void setXmlAttribute(pt.iflow.api.xml.codegen.flow.XmlAttribute) 

    /**
     * Method setXmlPort
     * 
     * 
     * 
     * @param index
     * @param vXmlPort
     */
    public void setXmlPort(int index, pt.iflow.api.xml.codegen.flow.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPortList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlPortList.set(index, vXmlPort);
    } //-- void setXmlPort(int, pt.iflow.api.xml.codegen.flow.XmlPort) 

    /**
     * Method setXmlPort
     * 
     * 
     * 
     * @param xmlPortArray
     */
    public void setXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort[] xmlPortArray)
    {
        //-- copy array
        _xmlPortList.clear();
        for (int i = 0; i < xmlPortArray.length; i++) {
            _xmlPortList.add(xmlPortArray[i]);
        }
    } //-- void setXmlPort(pt.iflow.api.xml.codegen.flow.XmlPort) 

    /**
     * Sets the value of field 'xmlPosition'.
     * 
     * @param xmlPosition the value of field 'xmlPosition'.
     */
    public void setXmlPosition(pt.iflow.api.xml.codegen.flow.XmlPosition xmlPosition)
    {
        this._xmlPosition = xmlPosition;
    } //-- void setXmlPosition(pt.iflow.api.xml.codegen.flow.XmlPosition) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iflow.api.xml.codegen.flow.XmlBlockType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.flow.XmlBlockType.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
