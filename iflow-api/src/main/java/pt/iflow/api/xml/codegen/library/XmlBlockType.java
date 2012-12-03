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
import pt.iflow.api.xml.codegen.library.types.BoolType;

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
     * Nome da classe que implementa o bloco no iFlow. O "package"
     * correcto será acrescentado.
     */
    private java.lang.String _type;

    /**
     * Permite definir um porto de ligação entre blocos.
     */
    private java.util.ArrayList _xmlPortList;

    /**
     * Define um atributo do bloco
     */
    private java.util.ArrayList _xmlAttributeList;

    /**
     * Imagem/icon que representa o bloco
     */
    private java.lang.String _image;

    /**
     * Cor da borda do bloco
     */
    private pt.iflow.api.xml.codegen.library.Color _color;

    /**
     * Nome (completo) da classe que permite alterar os atributos
     * do bloco no Editor.
     */
    private java.lang.String _className;

    /**
     * Não definir!
     */
    private pt.iflow.api.xml.codegen.library.Size _size;

    /**
     * Não definir!
     */
    private java.lang.String _fileName;

    /**
     * Nome a utilizar na apresentação do bloco. Se não for
     * especificado, usa o valor de class.
     */
    private java.lang.String _description;

    /**
     * O bloco é automático ou não.
     *  Se o bloco for automático é SEMPRE adicionado ao fluxo e
     * NÃO pode ser REMOVIDO.
     *  Um bloco automático não está disponível na biblioteca
     * para ser adicionado ao fluxo.
     *  Aceita os seguintes valores: yes, no, true, false.
     *  
     */
    private pt.iflow.api.xml.codegen.library.types.BoolType _automatic;

    /**
     * Chave para a descrição do bloco (internacionalização)
     */
    private java.lang.String _i18n;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlBlockType() 
     {
        super();
        _xmlPortList = new ArrayList();
        _xmlAttributeList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.library.XmlBlockType()


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
    public void addXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlAttributeList.add(vXmlAttribute);
    } //-- void addXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute) 

    /**
     * Method addXmlAttribute
     * 
     * 
     * 
     * @param index
     * @param vXmlAttribute
     */
    public void addXmlAttribute(int index, pt.iflow.api.xml.codegen.library.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlAttributeList.add(index, vXmlAttribute);
    } //-- void addXmlAttribute(int, pt.iflow.api.xml.codegen.library.XmlAttribute) 

    /**
     * Method addXmlPort
     * 
     * 
     * 
     * @param vXmlPort
     */
    public void addXmlPort(pt.iflow.api.xml.codegen.library.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlPortList.add(vXmlPort);
    } //-- void addXmlPort(pt.iflow.api.xml.codegen.library.XmlPort) 

    /**
     * Method addXmlPort
     * 
     * 
     * 
     * @param index
     * @param vXmlPort
     */
    public void addXmlPort(int index, pt.iflow.api.xml.codegen.library.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlPortList.add(index, vXmlPort);
    } //-- void addXmlPort(int, pt.iflow.api.xml.codegen.library.XmlPort) 

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
     * Returns the value of field 'automatic'. The field
     * 'automatic' has the following description: O bloco é
     * automático ou não.
     *  Se o bloco for automático é SEMPRE adicionado ao fluxo e
     * NÃO pode ser REMOVIDO.
     *  Um bloco automático não está disponível na biblioteca
     * para ser adicionado ao fluxo.
     *  Aceita os seguintes valores: yes, no, true, false.
     *  
     * 
     * @return BoolType
     * @return the value of field 'automatic'.
     */
    public pt.iflow.api.xml.codegen.library.types.BoolType getAutomatic()
    {
        return this._automatic;
    } //-- pt.iflow.api.xml.codegen.library.types.BoolType getAutomatic() 

    /**
     * Returns the value of field 'className'. The field
     * 'className' has the following description: Nome (completo)
     * da classe que permite alterar os atributos do bloco no
     * Editor.
     * 
     * @return String
     * @return the value of field 'className'.
     */
    public java.lang.String getClassName()
    {
        return this._className;
    } //-- java.lang.String getClassName() 

    /**
     * Returns the value of field 'color'. The field 'color' has
     * the following description: Cor da borda do bloco
     * 
     * @return Color
     * @return the value of field 'color'.
     */
    public pt.iflow.api.xml.codegen.library.Color getColor()
    {
        return this._color;
    } //-- pt.iflow.api.xml.codegen.library.Color getColor() 

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Nome a utilizar
     * na apresentação do bloco. Se não for especificado, usa o
     * valor de class.
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'fileName'. The field 'fileName'
     * has the following description: Não definir!
     * 
     * @return String
     * @return the value of field 'fileName'.
     */
    public java.lang.String getFileName()
    {
        return this._fileName;
    } //-- java.lang.String getFileName() 

    /**
     * Returns the value of field 'i18n'. The field 'i18n' has the
     * following description: Chave para a descrição do bloco
     * (internacionalização)
     * 
     * @return String
     * @return the value of field 'i18n'.
     */
    public java.lang.String getI18n()
    {
        return this._i18n;
    } //-- java.lang.String getI18n() 

    /**
     * Returns the value of field 'image'. The field 'image' has
     * the following description: Imagem/icon que representa o
     * bloco
     * 
     * @return String
     * @return the value of field 'image'.
     */
    public java.lang.String getImage()
    {
        return this._image;
    } //-- java.lang.String getImage() 

    /**
     * Returns the value of field 'size'. The field 'size' has the
     * following description: Não definir!
     * 
     * @return Size
     * @return the value of field 'size'.
     */
    public pt.iflow.api.xml.codegen.library.Size getSize()
    {
        return this._size;
    } //-- pt.iflow.api.xml.codegen.library.Size getSize() 

    /**
     * Returns the value of field 'type'. The field 'type' has the
     * following description: Nome da classe que implementa o bloco
     * no iFlow. O "package" correcto será acrescentado.
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
    public pt.iflow.api.xml.codegen.library.XmlAttribute getXmlAttribute(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.library.XmlAttribute) _xmlAttributeList.get(index);
    } //-- pt.iflow.api.xml.codegen.library.XmlAttribute getXmlAttribute(int) 

    /**
     * Method getXmlAttribute
     * 
     * 
     * 
     * @return XmlAttribute
     */
    public pt.iflow.api.xml.codegen.library.XmlAttribute[] getXmlAttribute()
    {
        int size = _xmlAttributeList.size();
        pt.iflow.api.xml.codegen.library.XmlAttribute[] mArray = new pt.iflow.api.xml.codegen.library.XmlAttribute[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.library.XmlAttribute) _xmlAttributeList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.library.XmlAttribute[] getXmlAttribute() 

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
    public pt.iflow.api.xml.codegen.library.XmlPort getXmlPort(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPortList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.library.XmlPort) _xmlPortList.get(index);
    } //-- pt.iflow.api.xml.codegen.library.XmlPort getXmlPort(int) 

    /**
     * Method getXmlPort
     * 
     * 
     * 
     * @return XmlPort
     */
    public pt.iflow.api.xml.codegen.library.XmlPort[] getXmlPort()
    {
        int size = _xmlPortList.size();
        pt.iflow.api.xml.codegen.library.XmlPort[] mArray = new pt.iflow.api.xml.codegen.library.XmlPort[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.library.XmlPort) _xmlPortList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.library.XmlPort[] getXmlPort() 

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
    public boolean removeXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute vXmlAttribute)
    {
        boolean removed = _xmlAttributeList.remove(vXmlAttribute);
        return removed;
    } //-- boolean removeXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute) 

    /**
     * Method removeXmlPort
     * 
     * 
     * 
     * @param vXmlPort
     * @return boolean
     */
    public boolean removeXmlPort(pt.iflow.api.xml.codegen.library.XmlPort vXmlPort)
    {
        boolean removed = _xmlPortList.remove(vXmlPort);
        return removed;
    } //-- boolean removeXmlPort(pt.iflow.api.xml.codegen.library.XmlPort) 

    /**
     * Sets the value of field 'automatic'. The field 'automatic'
     * has the following description: O bloco é automático ou
     * não.
     *  Se o bloco for automático é SEMPRE adicionado ao fluxo e
     * NÃO pode ser REMOVIDO.
     *  Um bloco automático não está disponível na biblioteca
     * para ser adicionado ao fluxo.
     *  Aceita os seguintes valores: yes, no, true, false.
     *  
     * 
     * @param automatic the value of field 'automatic'.
     */
    public void setAutomatic(pt.iflow.api.xml.codegen.library.types.BoolType automatic)
    {
        this._automatic = automatic;
    } //-- void setAutomatic(pt.iflow.api.xml.codegen.library.types.BoolType) 

    /**
     * Sets the value of field 'className'. The field 'className'
     * has the following description: Nome (completo) da classe que
     * permite alterar os atributos do bloco no Editor.
     * 
     * @param className the value of field 'className'.
     */
    public void setClassName(java.lang.String className)
    {
        this._className = className;
    } //-- void setClassName(java.lang.String) 

    /**
     * Sets the value of field 'color'. The field 'color' has the
     * following description: Cor da borda do bloco
     * 
     * @param color the value of field 'color'.
     */
    public void setColor(pt.iflow.api.xml.codegen.library.Color color)
    {
        this._color = color;
    } //-- void setColor(pt.iflow.api.xml.codegen.library.Color) 

    /**
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Nome a utilizar
     * na apresentação do bloco. Se não for especificado, usa o
     * valor de class.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'fileName'. The field 'fileName' has
     * the following description: Não definir!
     * 
     * @param fileName the value of field 'fileName'.
     */
    public void setFileName(java.lang.String fileName)
    {
        this._fileName = fileName;
    } //-- void setFileName(java.lang.String) 

    /**
     * Sets the value of field 'i18n'. The field 'i18n' has the
     * following description: Chave para a descrição do bloco
     * (internacionalização)
     * 
     * @param i18n the value of field 'i18n'.
     */
    public void setI18n(java.lang.String i18n)
    {
        this._i18n = i18n;
    } //-- void setI18n(java.lang.String) 

    /**
     * Sets the value of field 'image'. The field 'image' has the
     * following description: Imagem/icon que representa o bloco
     * 
     * @param image the value of field 'image'.
     */
    public void setImage(java.lang.String image)
    {
        this._image = image;
    } //-- void setImage(java.lang.String) 

    /**
     * Sets the value of field 'size'. The field 'size' has the
     * following description: Não definir!
     * 
     * @param size the value of field 'size'.
     */
    public void setSize(pt.iflow.api.xml.codegen.library.Size size)
    {
        this._size = size;
    } //-- void setSize(pt.iflow.api.xml.codegen.library.Size) 

    /**
     * Sets the value of field 'type'. The field 'type' has the
     * following description: Nome da classe que implementa o bloco
     * no iFlow. O "package" correcto será acrescentado.
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
    public void setXmlAttribute(int index, pt.iflow.api.xml.codegen.library.XmlAttribute vXmlAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlAttributeList.set(index, vXmlAttribute);
    } //-- void setXmlAttribute(int, pt.iflow.api.xml.codegen.library.XmlAttribute) 

    /**
     * Method setXmlAttribute
     * 
     * 
     * 
     * @param xmlAttributeArray
     */
    public void setXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute[] xmlAttributeArray)
    {
        //-- copy array
        _xmlAttributeList.clear();
        for (int i = 0; i < xmlAttributeArray.length; i++) {
            _xmlAttributeList.add(xmlAttributeArray[i]);
        }
    } //-- void setXmlAttribute(pt.iflow.api.xml.codegen.library.XmlAttribute) 

    /**
     * Method setXmlPort
     * 
     * 
     * 
     * @param index
     * @param vXmlPort
     */
    public void setXmlPort(int index, pt.iflow.api.xml.codegen.library.XmlPort vXmlPort)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlPortList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlPortList.set(index, vXmlPort);
    } //-- void setXmlPort(int, pt.iflow.api.xml.codegen.library.XmlPort) 

    /**
     * Method setXmlPort
     * 
     * 
     * 
     * @param xmlPortArray
     */
    public void setXmlPort(pt.iflow.api.xml.codegen.library.XmlPort[] xmlPortArray)
    {
        //-- copy array
        _xmlPortList.clear();
        for (int i = 0; i < xmlPortArray.length; i++) {
            _xmlPortList.add(xmlPortArray[i]);
        }
    } //-- void setXmlPort(pt.iflow.api.xml.codegen.library.XmlPort) 

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
        return (pt.iflow.api.xml.codegen.library.XmlBlockType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.library.XmlBlockType.class, reader);
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
