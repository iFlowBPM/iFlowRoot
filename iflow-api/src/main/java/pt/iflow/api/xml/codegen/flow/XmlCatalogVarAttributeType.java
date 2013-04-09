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
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class XmlCatalogVarAttributeType.
 * 
 * @version $Revision$ $Date$
 */
public class XmlCatalogVarAttributeType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _initVal
     */
    private java.lang.String _initVal;

    /**
     * Field _dataType
     */
    private java.lang.String _dataType;

    /**
     * Field _isSearchable
     */
    private boolean _isSearchable;

    /**
     * keeps track of state for field: _isSearchable
     */
    private boolean _has_isSearchable;

    /**
     * Field _publicName
     */
    private java.lang.String _publicName;

    /**
     * Field _format
     */
    private java.lang.String _format;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlCatalogVarAttributeType() 
     {
        super();
    } //-- pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIsSearchable
     * 
     */
    public void deleteIsSearchable()
    {
        this._has_isSearchable= false;
    } //-- void deleteIsSearchable() 

    /**
     * Returns the value of field 'dataType'.
     * 
     * @return String
     * @return the value of field 'dataType'.
     */
    public java.lang.String getDataType()
    {
        return this._dataType;
    } //-- java.lang.String getDataType() 

    /**
     * Returns the value of field 'format'.
     * 
     * @return String
     * @return the value of field 'format'.
     */
    public java.lang.String getFormat()
    {
        return this._format;
    } //-- java.lang.String getFormat() 

    /**
     * Returns the value of field 'initVal'.
     * 
     * @return String
     * @return the value of field 'initVal'.
     */
    public java.lang.String getInitVal()
    {
        return this._initVal;
    } //-- java.lang.String getInitVal() 

    /**
     * Returns the value of field 'isSearchable'.
     * 
     * @return boolean
     * @return the value of field 'isSearchable'.
     */
    public boolean getIsSearchable()
    {
        return this._isSearchable;
    } //-- boolean getIsSearchable() 

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
     * Returns the value of field 'publicName'.
     * 
     * @return String
     * @return the value of field 'publicName'.
     */
    public java.lang.String getPublicName()
    {
        return this._publicName;
    } //-- java.lang.String getPublicName() 

    /**
     * Method hasIsSearchable
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasIsSearchable()
    {
        return this._has_isSearchable;
    } //-- boolean hasIsSearchable() 

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
     * Sets the value of field 'dataType'.
     * 
     * @param dataType the value of field 'dataType'.
     */
    public void setDataType(java.lang.String dataType)
    {
        this._dataType = dataType;
    } //-- void setDataType(java.lang.String) 

    /**
     * Sets the value of field 'format'.
     * 
     * @param format the value of field 'format'.
     */
    public void setFormat(java.lang.String format)
    {
        this._format = format;
    } //-- void setFormat(java.lang.String) 

    /**
     * Sets the value of field 'initVal'.
     * 
     * @param initVal the value of field 'initVal'.
     */
    public void setInitVal(java.lang.String initVal)
    {
        this._initVal = initVal;
    } //-- void setInitVal(java.lang.String) 

    /**
     * Sets the value of field 'isSearchable'.
     * 
     * @param isSearchable the value of field 'isSearchable'.
     */
    public void setIsSearchable(boolean isSearchable)
    {
        this._isSearchable = isSearchable;
        this._has_isSearchable = true;
    } //-- void setIsSearchable(boolean) 

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
     * Sets the value of field 'publicName'.
     * 
     * @param publicName the value of field 'publicName'.
     */
    public void setPublicName(java.lang.String publicName)
    {
        this._publicName = publicName;
    } //-- void setPublicName(java.lang.String) 

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
        return (pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType.class, reader);
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
