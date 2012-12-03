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

/**
 * Class XmlBlockColor.
 * 
 * @version $Revision$ $Date$
 */
public class XmlBlockColor implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _r
     */
    private int _r;

    /**
     * keeps track of state for field: _r
     */
    private boolean _has_r;

    /**
     * Field _g
     */
    private int _g;

    /**
     * keeps track of state for field: _g
     */
    private boolean _has_g;

    /**
     * Field _b
     */
    private int _b;

    /**
     * keeps track of state for field: _b
     */
    private boolean _has_b;

    /**
     * Field _a
     */
    private int _a;

    /**
     * keeps track of state for field: _a
     */
    private boolean _has_a;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlBlockColor() 
     {
        super();
    } //-- pt.iflow.api.xml.codegen.library.XmlBlockColor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteA
     * 
     */
    public void deleteA()
    {
        this._has_a= false;
    } //-- void deleteA() 

    /**
     * Method deleteB
     * 
     */
    public void deleteB()
    {
        this._has_b= false;
    } //-- void deleteB() 

    /**
     * Method deleteG
     * 
     */
    public void deleteG()
    {
        this._has_g= false;
    } //-- void deleteG() 

    /**
     * Method deleteR
     * 
     */
    public void deleteR()
    {
        this._has_r= false;
    } //-- void deleteR() 

    /**
     * Returns the value of field 'a'.
     * 
     * @return int
     * @return the value of field 'a'.
     */
    public int getA()
    {
        return this._a;
    } //-- int getA() 

    /**
     * Returns the value of field 'b'.
     * 
     * @return int
     * @return the value of field 'b'.
     */
    public int getB()
    {
        return this._b;
    } //-- int getB() 

    /**
     * Returns the value of field 'g'.
     * 
     * @return int
     * @return the value of field 'g'.
     */
    public int getG()
    {
        return this._g;
    } //-- int getG() 

    /**
     * Returns the value of field 'r'.
     * 
     * @return int
     * @return the value of field 'r'.
     */
    public int getR()
    {
        return this._r;
    } //-- int getR() 

    /**
     * Method hasA
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasA()
    {
        return this._has_a;
    } //-- boolean hasA() 

    /**
     * Method hasB
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasB()
    {
        return this._has_b;
    } //-- boolean hasB() 

    /**
     * Method hasG
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasG()
    {
        return this._has_g;
    } //-- boolean hasG() 

    /**
     * Method hasR
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasR()
    {
        return this._has_r;
    } //-- boolean hasR() 

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
     * Sets the value of field 'a'.
     * 
     * @param a the value of field 'a'.
     */
    public void setA(int a)
    {
        this._a = a;
        this._has_a = true;
    } //-- void setA(int) 

    /**
     * Sets the value of field 'b'.
     * 
     * @param b the value of field 'b'.
     */
    public void setB(int b)
    {
        this._b = b;
        this._has_b = true;
    } //-- void setB(int) 

    /**
     * Sets the value of field 'g'.
     * 
     * @param g the value of field 'g'.
     */
    public void setG(int g)
    {
        this._g = g;
        this._has_g = true;
    } //-- void setG(int) 

    /**
     * Sets the value of field 'r'.
     * 
     * @param r the value of field 'r'.
     */
    public void setR(int r)
    {
        this._r = r;
        this._has_r = true;
    } //-- void setR(int) 

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
        return (pt.iflow.api.xml.codegen.library.XmlBlockColor) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.library.XmlBlockColor.class, reader);
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
