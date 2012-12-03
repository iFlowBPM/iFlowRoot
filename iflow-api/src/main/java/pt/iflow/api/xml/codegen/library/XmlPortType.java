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
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;
import pt.iflow.api.xml.codegen.library.types.InOutType;

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
     * Nome do porto. Tem que existir um porto com o mesmo nome no
     * bloco do iFlow.
     */
    private java.lang.String _name;

    /**
     * Descrição do porto
     */
    private java.lang.String _description;

    /**
     * Tipo de bloco. Pode ser entrada (in) ou saída (out).
     */
    private pt.iflow.api.xml.codegen.library.types.InOutType _inOut;

    /**
     * Posição relativa do porto em relação ao bloco.
     */
    private pt.iflow.api.xml.codegen.library.Position _position;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlPortType() 
     {
        super();
    } //-- pt.iflow.api.xml.codegen.library.XmlPortType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Descrição do
     * porto
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'inOut'. The field 'inOut' has
     * the following description: Tipo de bloco. Pode ser entrada
     * (in) ou saída (out).
     * 
     * @return InOutType
     * @return the value of field 'inOut'.
     */
    public pt.iflow.api.xml.codegen.library.types.InOutType getInOut()
    {
        return this._inOut;
    } //-- pt.iflow.api.xml.codegen.library.types.InOutType getInOut() 

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: Nome do porto. Tem que existir um
     * porto com o mesmo nome no bloco do iFlow.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'position'. The field 'position'
     * has the following description: Posição relativa do porto
     * em relação ao bloco.
     * 
     * @return Position
     * @return the value of field 'position'.
     */
    public pt.iflow.api.xml.codegen.library.Position getPosition()
    {
        return this._position;
    } //-- pt.iflow.api.xml.codegen.library.Position getPosition() 

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
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Descrição do
     * porto
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'inOut'. The field 'inOut' has the
     * following description: Tipo de bloco. Pode ser entrada (in)
     * ou saída (out).
     * 
     * @param inOut the value of field 'inOut'.
     */
    public void setInOut(pt.iflow.api.xml.codegen.library.types.InOutType inOut)
    {
        this._inOut = inOut;
    } //-- void setInOut(pt.iflow.api.xml.codegen.library.types.InOutType) 

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: Nome do porto. Tem que existir um
     * porto com o mesmo nome no bloco do iFlow.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'position'. The field 'position' has
     * the following description: Posição relativa do porto em
     * relação ao bloco.
     * 
     * @param position the value of field 'position'.
     */
    public void setPosition(pt.iflow.api.xml.codegen.library.Position position)
    {
        this._position = position;
    } //-- void setPosition(pt.iflow.api.xml.codegen.library.Position) 

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
        return (pt.iflow.api.xml.codegen.library.XmlPortType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.library.XmlPortType.class, reader);
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
