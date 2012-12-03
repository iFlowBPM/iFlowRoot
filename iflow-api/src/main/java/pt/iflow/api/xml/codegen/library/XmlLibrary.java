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
 * Class XmlLibrary.
 * 
 * @version $Revision$ $Date$
 */
public class XmlLibrary implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Nome (identificador) da biblioteca de blocos
     */
    private java.lang.String _name;

    /**
     * Versão da biblioteca de blocos
     */
    private java.lang.String _version;

    /**
     * Autor da biblioteca de blocos
     */
    private java.lang.String _author;

    /**
     * Descrição da biblioteca
     */
    private java.lang.String _description;

    /**
     * Chave para o nome da biblioteca (internacionalização)
     */
    private java.lang.String _i18nName;

    /**
     * Chave para a descrição da biblioteca (internacionalização
     */
    private java.lang.String _i18nDescription;

    /**
     * Lista de blocos
     */
    private java.util.ArrayList _xmlBlockList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlLibrary() 
     {
        super();
        _xmlBlockList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.library.XmlLibrary()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addXmlBlock
     * 
     * 
     * 
     * @param vXmlBlock
     */
    public void addXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlBlockList.add(vXmlBlock);
    } //-- void addXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock) 

    /**
     * Method addXmlBlock
     * 
     * 
     * 
     * @param index
     * @param vXmlBlock
     */
    public void addXmlBlock(int index, pt.iflow.api.xml.codegen.library.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlBlockList.add(index, vXmlBlock);
    } //-- void addXmlBlock(int, pt.iflow.api.xml.codegen.library.XmlBlock) 

    /**
     * Method clearXmlBlock
     * 
     */
    public void clearXmlBlock()
    {
        _xmlBlockList.clear();
    } //-- void clearXmlBlock() 

    /**
     * Method enumerateXmlBlock
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateXmlBlock()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_xmlBlockList.iterator());
    } //-- java.util.Enumeration enumerateXmlBlock() 

    /**
     * Returns the value of field 'author'. The field 'author' has
     * the following description: Autor da biblioteca de blocos
     * 
     * @return String
     * @return the value of field 'author'.
     */
    public java.lang.String getAuthor()
    {
        return this._author;
    } //-- java.lang.String getAuthor() 

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Descrição da
     * biblioteca
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'i18nDescription'. The field
     * 'i18nDescription' has the following description: Chave para
     * a descrição da biblioteca (internacionalização)
     * 
     * @return String
     * @return the value of field 'i18nDescription'.
     */
    public java.lang.String getI18nDescription()
    {
        return this._i18nDescription;
    } //-- java.lang.String getI18nDescription() 

    /**
     * Returns the value of field 'i18nName'. The field 'i18nName'
     * has the following description: Chave para o nome da
     * biblioteca (internacionalização)
     * 
     * @return String
     * @return the value of field 'i18nName'.
     */
    public java.lang.String getI18nName()
    {
        return this._i18nName;
    } //-- java.lang.String getI18nName() 

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: Nome (identificador) da biblioteca de
     * blocos
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'version'. The field 'version'
     * has the following description: Versão da biblioteca de
     * blocos
     * 
     * @return String
     * @return the value of field 'version'.
     */
    public java.lang.String getVersion()
    {
        return this._version;
    } //-- java.lang.String getVersion() 

    /**
     * Method getXmlBlock
     * 
     * 
     * 
     * @param index
     * @return XmlBlock
     */
    public pt.iflow.api.xml.codegen.library.XmlBlock getXmlBlock(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlBlockList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.library.XmlBlock) _xmlBlockList.get(index);
    } //-- pt.iflow.api.xml.codegen.library.XmlBlock getXmlBlock(int) 

    /**
     * Method getXmlBlock
     * 
     * 
     * 
     * @return XmlBlock
     */
    public pt.iflow.api.xml.codegen.library.XmlBlock[] getXmlBlock()
    {
        int size = _xmlBlockList.size();
        pt.iflow.api.xml.codegen.library.XmlBlock[] mArray = new pt.iflow.api.xml.codegen.library.XmlBlock[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.library.XmlBlock) _xmlBlockList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.library.XmlBlock[] getXmlBlock() 

    /**
     * Method getXmlBlockCount
     * 
     * 
     * 
     * @return int
     */
    public int getXmlBlockCount()
    {
        return _xmlBlockList.size();
    } //-- int getXmlBlockCount() 

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
     * Method removeXmlBlock
     * 
     * 
     * 
     * @param vXmlBlock
     * @return boolean
     */
    public boolean removeXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock vXmlBlock)
    {
        boolean removed = _xmlBlockList.remove(vXmlBlock);
        return removed;
    } //-- boolean removeXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock) 

    /**
     * Sets the value of field 'author'. The field 'author' has the
     * following description: Autor da biblioteca de blocos
     * 
     * @param author the value of field 'author'.
     */
    public void setAuthor(java.lang.String author)
    {
        this._author = author;
    } //-- void setAuthor(java.lang.String) 

    /**
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Descrição da
     * biblioteca
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'i18nDescription'. The field
     * 'i18nDescription' has the following description: Chave para
     * a descrição da biblioteca (internacionalização)
     * 
     * @param i18nDescription the value of field 'i18nDescription'.
     */
    public void setI18nDescription(java.lang.String i18nDescription)
    {
        this._i18nDescription = i18nDescription;
    } //-- void setI18nDescription(java.lang.String) 

    /**
     * Sets the value of field 'i18nName'. The field 'i18nName' has
     * the following description: Chave para o nome da biblioteca
     * (internacionalização)
     * 
     * @param i18nName the value of field 'i18nName'.
     */
    public void setI18nName(java.lang.String i18nName)
    {
        this._i18nName = i18nName;
    } //-- void setI18nName(java.lang.String) 

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: Nome (identificador) da biblioteca de
     * blocos
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'version'. The field 'version' has
     * the following description: Versão da biblioteca de blocos
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(java.lang.String version)
    {
        this._version = version;
    } //-- void setVersion(java.lang.String) 

    /**
     * Method setXmlBlock
     * 
     * 
     * 
     * @param index
     * @param vXmlBlock
     */
    public void setXmlBlock(int index, pt.iflow.api.xml.codegen.library.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlBlockList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlBlockList.set(index, vXmlBlock);
    } //-- void setXmlBlock(int, pt.iflow.api.xml.codegen.library.XmlBlock) 

    /**
     * Method setXmlBlock
     * 
     * 
     * 
     * @param xmlBlockArray
     */
    public void setXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock[] xmlBlockArray)
    {
        //-- copy array
        _xmlBlockList.clear();
        for (int i = 0; i < xmlBlockArray.length; i++) {
            _xmlBlockList.add(xmlBlockArray[i]);
        }
    } //-- void setXmlBlock(pt.iflow.api.xml.codegen.library.XmlBlock) 

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
        return (pt.iflow.api.xml.codegen.library.XmlLibrary) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.library.XmlLibrary.class, reader);
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
