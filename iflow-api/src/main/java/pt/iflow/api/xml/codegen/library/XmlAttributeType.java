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

package pt.iflow.api.xml.codegen.library;

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
 * Class XmlAttributeType.
 * 
 * @version $Revision$ $Date$
 */
public class XmlAttributeType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Nome do atributo
     */
    private java.lang.String _name;

    /**
     * Descrição do atributo
     */
    private java.lang.String _description;

    /**
     * Valor por omissão a atribuir ao atributo
     */
    private java.lang.String _value;

    /**
     * Tipo de dados
     */
    private java.util.ArrayList _valueTypeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlAttributeType() 
     {
        super();
        _valueTypeList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.library.XmlAttributeType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addValueType
     * 
     * 
     * 
     * @param vValueType
     */
    public void addValueType(java.lang.String vValueType)
        throws java.lang.IndexOutOfBoundsException
    {
        _valueTypeList.add(vValueType);
    } //-- void addValueType(java.lang.String) 

    /**
     * Method addValueType
     * 
     * 
     * 
     * @param index
     * @param vValueType
     */
    public void addValueType(int index, java.lang.String vValueType)
        throws java.lang.IndexOutOfBoundsException
    {
        _valueTypeList.add(index, vValueType);
    } //-- void addValueType(int, java.lang.String) 

    /**
     * Method clearValueType
     * 
     */
    public void clearValueType()
    {
        _valueTypeList.clear();
    } //-- void clearValueType() 

    /**
     * Method enumerateValueType
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateValueType()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_valueTypeList.iterator());
    } //-- java.util.Enumeration enumerateValueType() 

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Descrição do
     * atributo
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: Nome do atributo
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'value'. The field 'value' has
     * the following description: Valor por omissão a atribuir ao
     * atributo
     * 
     * @return String
     * @return the value of field 'value'.
     */
    public java.lang.String getValue()
    {
        return this._value;
    } //-- java.lang.String getValue() 

    /**
     * Method getValueType
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getValueType(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _valueTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_valueTypeList.get(index);
    } //-- java.lang.String getValueType(int) 

    /**
     * Method getValueType
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getValueType()
    {
        int size = _valueTypeList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_valueTypeList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getValueType() 

    /**
     * Method getValueTypeCount
     * 
     * 
     * 
     * @return int
     */
    public int getValueTypeCount()
    {
        return _valueTypeList.size();
    } //-- int getValueTypeCount() 

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
     * Method removeValueType
     * 
     * 
     * 
     * @param vValueType
     * @return boolean
     */
    public boolean removeValueType(java.lang.String vValueType)
    {
        boolean removed = _valueTypeList.remove(vValueType);
        return removed;
    } //-- boolean removeValueType(java.lang.String) 

    /**
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Descrição do
     * atributo
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: Nome do atributo
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'value'. The field 'value' has the
     * following description: Valor por omissão a atribuir ao
     * atributo
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(java.lang.String value)
    {
        this._value = value;
    } //-- void setValue(java.lang.String) 

    /**
     * Method setValueType
     * 
     * 
     * 
     * @param index
     * @param vValueType
     */
    public void setValueType(int index, java.lang.String vValueType)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _valueTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _valueTypeList.set(index, vValueType);
    } //-- void setValueType(int, java.lang.String) 

    /**
     * Method setValueType
     * 
     * 
     * 
     * @param valueTypeArray
     */
    public void setValueType(java.lang.String[] valueTypeArray)
    {
        //-- copy array
        _valueTypeList.clear();
        for (int i = 0; i < valueTypeArray.length; i++) {
            _valueTypeList.add(valueTypeArray[i]);
        }
    } //-- void setValueType(java.lang.String) 

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
        return (pt.iflow.api.xml.codegen.library.XmlAttributeType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.library.XmlAttributeType.class, reader);
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
