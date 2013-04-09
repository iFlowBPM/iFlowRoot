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

package pt.iflow.api.xml.codegen.processdata;

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
 * Class Listvar.
 * 
 * @version $Revision$ $Date$
 */
public class Listvar implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _n
     */
    private java.lang.String _n;

    /**
     * Field _s
     */
    private int _s;

    /**
     * keeps track of state for field: _s
     */
    private boolean _has_s;

    /**
     * Field _iList
     */
    private java.util.ArrayList _iList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Listvar() 
     {
        super();
        _iList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.processdata.Listvar()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addI
     * 
     * 
     * 
     * @param vI
     */
    public void addI(pt.iflow.api.xml.codegen.processdata.I vI)
        throws java.lang.IndexOutOfBoundsException
    {
        _iList.add(vI);
    } //-- void addI(pt.iflow.api.xml.codegen.processdata.I) 

    /**
     * Method addI
     * 
     * 
     * 
     * @param index
     * @param vI
     */
    public void addI(int index, pt.iflow.api.xml.codegen.processdata.I vI)
        throws java.lang.IndexOutOfBoundsException
    {
        _iList.add(index, vI);
    } //-- void addI(int, pt.iflow.api.xml.codegen.processdata.I) 

    /**
     * Method clearI
     * 
     */
    public void clearI()
    {
        _iList.clear();
    } //-- void clearI() 

    /**
     * Method deleteS
     * 
     */
    public void deleteS()
    {
        this._has_s= false;
    } //-- void deleteS() 

    /**
     * Method enumerateI
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateI()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_iList.iterator());
    } //-- java.util.Enumeration enumerateI() 

    /**
     * Method getI
     * 
     * 
     * 
     * @param index
     * @return I
     */
    public pt.iflow.api.xml.codegen.processdata.I getI(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _iList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.processdata.I) _iList.get(index);
    } //-- pt.iflow.api.xml.codegen.processdata.I getI(int) 

    /**
     * Method getI
     * 
     * 
     * 
     * @return I
     */
    public pt.iflow.api.xml.codegen.processdata.I[] getI()
    {
        int size = _iList.size();
        pt.iflow.api.xml.codegen.processdata.I[] mArray = new pt.iflow.api.xml.codegen.processdata.I[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.processdata.I) _iList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.processdata.I[] getI() 

    /**
     * Method getICount
     * 
     * 
     * 
     * @return int
     */
    public int getICount()
    {
        return _iList.size();
    } //-- int getICount() 

    /**
     * Returns the value of field 'n'.
     * 
     * @return String
     * @return the value of field 'n'.
     */
    public java.lang.String getN()
    {
        return this._n;
    } //-- java.lang.String getN() 

    /**
     * Returns the value of field 's'.
     * 
     * @return int
     * @return the value of field 's'.
     */
    public int getS()
    {
        return this._s;
    } //-- int getS() 

    /**
     * Method hasS
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasS()
    {
        return this._has_s;
    } //-- boolean hasS() 

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
     * Method removeI
     * 
     * 
     * 
     * @param vI
     * @return boolean
     */
    public boolean removeI(pt.iflow.api.xml.codegen.processdata.I vI)
    {
        boolean removed = _iList.remove(vI);
        return removed;
    } //-- boolean removeI(pt.iflow.api.xml.codegen.processdata.I) 

    /**
     * Method setI
     * 
     * 
     * 
     * @param index
     * @param vI
     */
    public void setI(int index, pt.iflow.api.xml.codegen.processdata.I vI)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _iList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _iList.set(index, vI);
    } //-- void setI(int, pt.iflow.api.xml.codegen.processdata.I) 

    /**
     * Method setI
     * 
     * 
     * 
     * @param iArray
     */
    public void setI(pt.iflow.api.xml.codegen.processdata.I[] iArray)
    {
        //-- copy array
        _iList.clear();
        for (int i = 0; i < iArray.length; i++) {
            _iList.add(iArray[i]);
        }
    } //-- void setI(pt.iflow.api.xml.codegen.processdata.I) 

    /**
     * Sets the value of field 'n'.
     * 
     * @param n the value of field 'n'.
     */
    public void setN(java.lang.String n)
    {
        this._n = n;
    } //-- void setN(java.lang.String) 

    /**
     * Sets the value of field 's'.
     * 
     * @param s the value of field 's'.
     */
    public void setS(int s)
    {
        this._s = s;
        this._has_s = true;
    } //-- void setS(int) 

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
        return (pt.iflow.api.xml.codegen.processdata.Listvar) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.processdata.Listvar.class, reader);
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
