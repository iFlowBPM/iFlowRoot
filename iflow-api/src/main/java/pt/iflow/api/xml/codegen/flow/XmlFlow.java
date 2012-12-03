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
 * Class XmlFlow.
 * 
 * @version $Revision$ $Date$
 */
public class XmlFlow implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _iFlowVersion
     */
    private java.lang.String _iFlowVersion;

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _version
     */
    private java.lang.String _version;

    /**
     * Field _author
     */
    private java.lang.String _author;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _xmlBlockList
     */
    private java.util.ArrayList _xmlBlockList;

    /**
     * Field _xmlCatalogVars
     */
    private pt.iflow.api.xml.codegen.flow.XmlCatalogVars _xmlCatalogVars;

    /**
     * Field _xmlFormTemplateList
     */
    private java.util.ArrayList _xmlFormTemplateList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlFlow() 
     {
        super();
        _xmlBlockList = new ArrayList();
        _xmlFormTemplateList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.flow.XmlFlow()


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
    public void addXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlBlockList.add(vXmlBlock);
    } //-- void addXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock) 

    /**
     * Method addXmlBlock
     * 
     * 
     * 
     * @param index
     * @param vXmlBlock
     */
    public void addXmlBlock(int index, pt.iflow.api.xml.codegen.flow.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlBlockList.add(index, vXmlBlock);
    } //-- void addXmlBlock(int, pt.iflow.api.xml.codegen.flow.XmlBlock) 

    /**
     * Method addXmlFormTemplate
     * 
     * 
     * 
     * @param vXmlFormTemplate
     */
    public void addXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate vXmlFormTemplate)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlFormTemplateList.add(vXmlFormTemplate);
    } //-- void addXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate) 

    /**
     * Method addXmlFormTemplate
     * 
     * 
     * 
     * @param index
     * @param vXmlFormTemplate
     */
    public void addXmlFormTemplate(int index, pt.iflow.api.xml.codegen.flow.XmlFormTemplate vXmlFormTemplate)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlFormTemplateList.add(index, vXmlFormTemplate);
    } //-- void addXmlFormTemplate(int, pt.iflow.api.xml.codegen.flow.XmlFormTemplate) 

    /**
     * Method clearXmlBlock
     * 
     */
    public void clearXmlBlock()
    {
        _xmlBlockList.clear();
    } //-- void clearXmlBlock() 

    /**
     * Method clearXmlFormTemplate
     * 
     */
    public void clearXmlFormTemplate()
    {
        _xmlFormTemplateList.clear();
    } //-- void clearXmlFormTemplate() 

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
     * Method enumerateXmlFormTemplate
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateXmlFormTemplate()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_xmlFormTemplateList.iterator());
    } //-- java.util.Enumeration enumerateXmlFormTemplate() 

    /**
     * Returns the value of field 'author'.
     * 
     * @return String
     * @return the value of field 'author'.
     */
    public java.lang.String getAuthor()
    {
        return this._author;
    } //-- java.lang.String getAuthor() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'iFlowVersion'.
     * 
     * @return String
     * @return the value of field 'iFlowVersion'.
     */
    public java.lang.String getIFlowVersion()
    {
        return this._iFlowVersion;
    } //-- java.lang.String getIFlowVersion() 

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
     * Returns the value of field 'version'.
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
    public pt.iflow.api.xml.codegen.flow.XmlBlock getXmlBlock(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlBlockList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.flow.XmlBlock) _xmlBlockList.get(index);
    } //-- pt.iflow.api.xml.codegen.flow.XmlBlock getXmlBlock(int) 

    /**
     * Method getXmlBlock
     * 
     * 
     * 
     * @return XmlBlock
     */
    public pt.iflow.api.xml.codegen.flow.XmlBlock[] getXmlBlock()
    {
        int size = _xmlBlockList.size();
        pt.iflow.api.xml.codegen.flow.XmlBlock[] mArray = new pt.iflow.api.xml.codegen.flow.XmlBlock[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.flow.XmlBlock) _xmlBlockList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.flow.XmlBlock[] getXmlBlock() 

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
     * Returns the value of field 'xmlCatalogVars'.
     * 
     * @return XmlCatalogVars
     * @return the value of field 'xmlCatalogVars'.
     */
    public pt.iflow.api.xml.codegen.flow.XmlCatalogVars getXmlCatalogVars()
    {
        return this._xmlCatalogVars;
    } //-- pt.iflow.api.xml.codegen.flow.XmlCatalogVars getXmlCatalogVars() 

    /**
     * Method getXmlFormTemplate
     * 
     * 
     * 
     * @param index
     * @return XmlFormTemplate
     */
    public pt.iflow.api.xml.codegen.flow.XmlFormTemplate getXmlFormTemplate(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlFormTemplateList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.flow.XmlFormTemplate) _xmlFormTemplateList.get(index);
    } //-- pt.iflow.api.xml.codegen.flow.XmlFormTemplate getXmlFormTemplate(int) 

    /**
     * Method getXmlFormTemplate
     * 
     * 
     * 
     * @return XmlFormTemplate
     */
    public pt.iflow.api.xml.codegen.flow.XmlFormTemplate[] getXmlFormTemplate()
    {
        int size = _xmlFormTemplateList.size();
        pt.iflow.api.xml.codegen.flow.XmlFormTemplate[] mArray = new pt.iflow.api.xml.codegen.flow.XmlFormTemplate[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.flow.XmlFormTemplate) _xmlFormTemplateList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.flow.XmlFormTemplate[] getXmlFormTemplate() 

    /**
     * Method getXmlFormTemplateCount
     * 
     * 
     * 
     * @return int
     */
    public int getXmlFormTemplateCount()
    {
        return _xmlFormTemplateList.size();
    } //-- int getXmlFormTemplateCount() 

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
    public boolean removeXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock vXmlBlock)
    {
        boolean removed = _xmlBlockList.remove(vXmlBlock);
        return removed;
    } //-- boolean removeXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock) 

    /**
     * Method removeXmlFormTemplate
     * 
     * 
     * 
     * @param vXmlFormTemplate
     * @return boolean
     */
    public boolean removeXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate vXmlFormTemplate)
    {
        boolean removed = _xmlFormTemplateList.remove(vXmlFormTemplate);
        return removed;
    } //-- boolean removeXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate) 

    /**
     * Sets the value of field 'author'.
     * 
     * @param author the value of field 'author'.
     */
    public void setAuthor(java.lang.String author)
    {
        this._author = author;
    } //-- void setAuthor(java.lang.String) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'iFlowVersion'.
     * 
     * @param iFlowVersion the value of field 'iFlowVersion'.
     */
    public void setIFlowVersion(java.lang.String iFlowVersion)
    {
        this._iFlowVersion = iFlowVersion;
    } //-- void setIFlowVersion(java.lang.String) 

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
     * Sets the value of field 'version'.
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
    public void setXmlBlock(int index, pt.iflow.api.xml.codegen.flow.XmlBlock vXmlBlock)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlBlockList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlBlockList.set(index, vXmlBlock);
    } //-- void setXmlBlock(int, pt.iflow.api.xml.codegen.flow.XmlBlock) 

    /**
     * Method setXmlBlock
     * 
     * 
     * 
     * @param xmlBlockArray
     */
    public void setXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock[] xmlBlockArray)
    {
        //-- copy array
        _xmlBlockList.clear();
        for (int i = 0; i < xmlBlockArray.length; i++) {
            _xmlBlockList.add(xmlBlockArray[i]);
        }
    } //-- void setXmlBlock(pt.iflow.api.xml.codegen.flow.XmlBlock) 

    /**
     * Sets the value of field 'xmlCatalogVars'.
     * 
     * @param xmlCatalogVars the value of field 'xmlCatalogVars'.
     */
    public void setXmlCatalogVars(pt.iflow.api.xml.codegen.flow.XmlCatalogVars xmlCatalogVars)
    {
        this._xmlCatalogVars = xmlCatalogVars;
    } //-- void setXmlCatalogVars(pt.iflow.api.xml.codegen.flow.XmlCatalogVars) 

    /**
     * Method setXmlFormTemplate
     * 
     * 
     * 
     * @param index
     * @param vXmlFormTemplate
     */
    public void setXmlFormTemplate(int index, pt.iflow.api.xml.codegen.flow.XmlFormTemplate vXmlFormTemplate)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlFormTemplateList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlFormTemplateList.set(index, vXmlFormTemplate);
    } //-- void setXmlFormTemplate(int, pt.iflow.api.xml.codegen.flow.XmlFormTemplate) 

    /**
     * Method setXmlFormTemplate
     * 
     * 
     * 
     * @param xmlFormTemplateArray
     */
    public void setXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate[] xmlFormTemplateArray)
    {
        //-- copy array
        _xmlFormTemplateList.clear();
        for (int i = 0; i < xmlFormTemplateArray.length; i++) {
            _xmlFormTemplateList.add(xmlFormTemplateArray[i]);
        }
    } //-- void setXmlFormTemplate(pt.iflow.api.xml.codegen.flow.XmlFormTemplate) 

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
        return (pt.iflow.api.xml.codegen.flow.XmlFlow) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.flow.XmlFlow.class, reader);
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
