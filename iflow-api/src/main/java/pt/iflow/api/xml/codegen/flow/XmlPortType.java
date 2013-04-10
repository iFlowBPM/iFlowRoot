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
 * Class XmlPortType.
 * 
 * @version $Revision$ $Date$
 */
public class XmlPortType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _connectedBlockId
     */
    private int _connectedBlockId;

    /**
     * keeps track of state for field: _connectedBlockId
     */
    private boolean _has_connectedBlockId;

    /**
     * Field _connectedPortName
     */
    private java.lang.String _connectedPortName;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlPortType() 
     {
        super();
    } //-- pt.iflow.api.xml.codegen.flow.XmlPortType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteConnectedBlockId
     * 
     */
    public void deleteConnectedBlockId()
    {
        this._has_connectedBlockId= false;
    } //-- void deleteConnectedBlockId() 

    /**
     * Returns the value of field 'connectedBlockId'.
     * 
     * @return int
     * @return the value of field 'connectedBlockId'.
     */
    public int getConnectedBlockId()
    {
        return this._connectedBlockId;
    } //-- int getConnectedBlockId() 

    /**
     * Returns the value of field 'connectedPortName'.
     * 
     * @return String
     * @return the value of field 'connectedPortName'.
     */
    public java.lang.String getConnectedPortName()
    {
        return this._connectedPortName;
    } //-- java.lang.String getConnectedPortName() 

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
     * Method hasConnectedBlockId
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasConnectedBlockId()
    {
        return this._has_connectedBlockId;
    } //-- boolean hasConnectedBlockId() 

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
     * Sets the value of field 'connectedBlockId'.
     * 
     * @param connectedBlockId the value of field 'connectedBlockId'
     */
    public void setConnectedBlockId(int connectedBlockId)
    {
        this._connectedBlockId = connectedBlockId;
        this._has_connectedBlockId = true;
    } //-- void setConnectedBlockId(int) 

    /**
     * Sets the value of field 'connectedPortName'.
     * 
     * @param connectedPortName the value of field
     * 'connectedPortName'.
     */
    public void setConnectedPortName(java.lang.String connectedPortName)
    {
        this._connectedPortName = connectedPortName;
    } //-- void setConnectedPortName(java.lang.String) 

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
        return (pt.iflow.api.xml.codegen.flow.XmlPortType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.flow.XmlPortType.class, reader);
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