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
 * Class XmlCatalogVarsType.
 * 
 * @version $Revision$ $Date$
 */
public class XmlCatalogVarsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _xmlAttributeList
     */
    private java.util.ArrayList _xmlAttributeList;

    /**
     * Field _xmlCatalogVarAttributeList
     */
    private java.util.ArrayList _xmlCatalogVarAttributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlCatalogVarsType() 
     {
        super();
        _xmlAttributeList = new ArrayList();
        _xmlCatalogVarAttributeList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType()


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
     * Method addXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param vXmlCatalogVarAttribute
     */
    public void addXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute vXmlCatalogVarAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlCatalogVarAttributeList.add(vXmlCatalogVarAttribute);
    } //-- void addXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) 

    /**
     * Method addXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param index
     * @param vXmlCatalogVarAttribute
     */
    public void addXmlCatalogVarAttribute(int index, pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute vXmlCatalogVarAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _xmlCatalogVarAttributeList.add(index, vXmlCatalogVarAttribute);
    } //-- void addXmlCatalogVarAttribute(int, pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) 

    /**
     * Method clearXmlAttribute
     * 
     */
    public void clearXmlAttribute()
    {
        _xmlAttributeList.clear();
    } //-- void clearXmlAttribute() 

    /**
     * Method clearXmlCatalogVarAttribute
     * 
     */
    public void clearXmlCatalogVarAttribute()
    {
        _xmlCatalogVarAttributeList.clear();
    } //-- void clearXmlCatalogVarAttribute() 

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
     * Method enumerateXmlCatalogVarAttribute
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateXmlCatalogVarAttribute()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_xmlCatalogVarAttributeList.iterator());
    } //-- java.util.Enumeration enumerateXmlCatalogVarAttribute() 

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
     * Method getXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param index
     * @return XmlCatalogVarAttribute
     */
    public pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute getXmlCatalogVarAttribute(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlCatalogVarAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) _xmlCatalogVarAttributeList.get(index);
    } //-- pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute getXmlCatalogVarAttribute(int) 

    /**
     * Method getXmlCatalogVarAttribute
     * 
     * 
     * 
     * @return XmlCatalogVarAttribute
     */
    public pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute[] getXmlCatalogVarAttribute()
    {
        int size = _xmlCatalogVarAttributeList.size();
        pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute[] mArray = new pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) _xmlCatalogVarAttributeList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute[] getXmlCatalogVarAttribute() 

    /**
     * Method getXmlCatalogVarAttributeCount
     * 
     * 
     * 
     * @return int
     */
    public int getXmlCatalogVarAttributeCount()
    {
        return _xmlCatalogVarAttributeList.size();
    } //-- int getXmlCatalogVarAttributeCount() 

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
     * Method removeXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param vXmlCatalogVarAttribute
     * @return boolean
     */
    public boolean removeXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute vXmlCatalogVarAttribute)
    {
        boolean removed = _xmlCatalogVarAttributeList.remove(vXmlCatalogVarAttribute);
        return removed;
    } //-- boolean removeXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) 

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
     * Method setXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param index
     * @param vXmlCatalogVarAttribute
     */
    public void setXmlCatalogVarAttribute(int index, pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute vXmlCatalogVarAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _xmlCatalogVarAttributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _xmlCatalogVarAttributeList.set(index, vXmlCatalogVarAttribute);
    } //-- void setXmlCatalogVarAttribute(int, pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) 

    /**
     * Method setXmlCatalogVarAttribute
     * 
     * 
     * 
     * @param xmlCatalogVarAttributeArray
     */
    public void setXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute[] xmlCatalogVarAttributeArray)
    {
        //-- copy array
        _xmlCatalogVarAttributeList.clear();
        for (int i = 0; i < xmlCatalogVarAttributeArray.length; i++) {
            _xmlCatalogVarAttributeList.add(xmlCatalogVarAttributeArray[i]);
        }
    } //-- void setXmlCatalogVarAttribute(pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute) 

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
        return (pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType.class, reader);
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
