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
import java.util.Date;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Processdata.
 * 
 * @version $Revision$ $Date$
 */
public class Processdata implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _fid
     */
    private int _fid;

    /**
     * keeps track of state for field: _fid
     */
    private boolean _has_fid;

    /**
     * Field _pid
     */
    private int _pid;

    /**
     * keeps track of state for field: _pid
     */
    private boolean _has_pid;

    /**
     * Field _spid
     */
    private int _spid;

    /**
     * keeps track of state for field: _spid
     */
    private boolean _has_spid;

    /**
     * Field _pnumber
     */
    private java.lang.String _pnumber;

    /**
     * Field _creator
     */
    private java.lang.String _creator;

    /**
     * Field _creationDate
     */
    private java.util.Date _creationDate;

    /**
     * Field _currentUser
     */
    private java.lang.String _currentUser;

    /**
     * Field _lastUpdate
     */
    private java.util.Date _lastUpdate;

    /**
     * Field _closed
     */
    private boolean _closed;

    /**
     * keeps track of state for field: _closed
     */
    private boolean _has_closed;

    /**
     * Field _e
     */
    private java.lang.String _e;

    /**
     * Field _aList
     */
    private java.util.ArrayList _aList;

    /**
     * Field _lList
     */
    private java.util.ArrayList _lList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Processdata() 
     {
        super();
        _aList = new ArrayList();
        _lList = new ArrayList();
    } //-- pt.iflow.api.xml.codegen.processdata.Processdata()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addA
     * 
     * 
     * 
     * @param vA
     */
    public void addA(pt.iflow.api.xml.codegen.processdata.A vA)
        throws java.lang.IndexOutOfBoundsException
    {
        _aList.add(vA);
    } //-- void addA(pt.iflow.api.xml.codegen.processdata.A) 

    /**
     * Method addA
     * 
     * 
     * 
     * @param index
     * @param vA
     */
    public void addA(int index, pt.iflow.api.xml.codegen.processdata.A vA)
        throws java.lang.IndexOutOfBoundsException
    {
        _aList.add(index, vA);
    } //-- void addA(int, pt.iflow.api.xml.codegen.processdata.A) 

    /**
     * Method addL
     * 
     * 
     * 
     * @param vL
     */
    public void addL(pt.iflow.api.xml.codegen.processdata.L vL)
        throws java.lang.IndexOutOfBoundsException
    {
        _lList.add(vL);
    } //-- void addL(pt.iflow.api.xml.codegen.processdata.L) 

    /**
     * Method addL
     * 
     * 
     * 
     * @param index
     * @param vL
     */
    public void addL(int index, pt.iflow.api.xml.codegen.processdata.L vL)
        throws java.lang.IndexOutOfBoundsException
    {
        _lList.add(index, vL);
    } //-- void addL(int, pt.iflow.api.xml.codegen.processdata.L) 

    /**
     * Method clearA
     * 
     */
    public void clearA()
    {
        _aList.clear();
    } //-- void clearA() 

    /**
     * Method clearL
     * 
     */
    public void clearL()
    {
        _lList.clear();
    } //-- void clearL() 

    /**
     * Method deleteClosed
     * 
     */
    public void deleteClosed()
    {
        this._has_closed= false;
    } //-- void deleteClosed() 

    /**
     * Method deleteFid
     * 
     */
    public void deleteFid()
    {
        this._has_fid= false;
    } //-- void deleteFid() 

    /**
     * Method deletePid
     * 
     */
    public void deletePid()
    {
        this._has_pid= false;
    } //-- void deletePid() 

    /**
     * Method deleteSpid
     * 
     */
    public void deleteSpid()
    {
        this._has_spid= false;
    } //-- void deleteSpid() 

    /**
     * Method enumerateA
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateA()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_aList.iterator());
    } //-- java.util.Enumeration enumerateA() 

    /**
     * Method enumerateL
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateL()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_lList.iterator());
    } //-- java.util.Enumeration enumerateL() 

    /**
     * Method getA
     * 
     * 
     * 
     * @param index
     * @return A
     */
    public pt.iflow.api.xml.codegen.processdata.A getA(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _aList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.processdata.A) _aList.get(index);
    } //-- pt.iflow.api.xml.codegen.processdata.A getA(int) 

    /**
     * Method getA
     * 
     * 
     * 
     * @return A
     */
    public pt.iflow.api.xml.codegen.processdata.A[] getA()
    {
        int size = _aList.size();
        pt.iflow.api.xml.codegen.processdata.A[] mArray = new pt.iflow.api.xml.codegen.processdata.A[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.processdata.A) _aList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.processdata.A[] getA() 

    /**
     * Method getACount
     * 
     * 
     * 
     * @return int
     */
    public int getACount()
    {
        return _aList.size();
    } //-- int getACount() 

    /**
     * Returns the value of field 'closed'.
     * 
     * @return boolean
     * @return the value of field 'closed'.
     */
    public boolean getClosed()
    {
        return this._closed;
    } //-- boolean getClosed() 

    /**
     * Returns the value of field 'creationDate'.
     * 
     * @return Date
     * @return the value of field 'creationDate'.
     */
    public java.util.Date getCreationDate()
    {
        return this._creationDate;
    } //-- java.util.Date getCreationDate() 

    /**
     * Returns the value of field 'creator'.
     * 
     * @return String
     * @return the value of field 'creator'.
     */
    public java.lang.String getCreator()
    {
        return this._creator;
    } //-- java.lang.String getCreator() 

    /**
     * Returns the value of field 'currentUser'.
     * 
     * @return String
     * @return the value of field 'currentUser'.
     */
    public java.lang.String getCurrentUser()
    {
        return this._currentUser;
    } //-- java.lang.String getCurrentUser() 

    /**
     * Returns the value of field 'e'.
     * 
     * @return String
     * @return the value of field 'e'.
     */
    public java.lang.String getE()
    {
        return this._e;
    } //-- java.lang.String getE() 

    /**
     * Returns the value of field 'fid'.
     * 
     * @return int
     * @return the value of field 'fid'.
     */
    public int getFid()
    {
        return this._fid;
    } //-- int getFid() 

    /**
     * Method getL
     * 
     * 
     * 
     * @param index
     * @return L
     */
    public pt.iflow.api.xml.codegen.processdata.L getL(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _lList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (pt.iflow.api.xml.codegen.processdata.L) _lList.get(index);
    } //-- pt.iflow.api.xml.codegen.processdata.L getL(int) 

    /**
     * Method getL
     * 
     * 
     * 
     * @return L
     */
    public pt.iflow.api.xml.codegen.processdata.L[] getL()
    {
        int size = _lList.size();
        pt.iflow.api.xml.codegen.processdata.L[] mArray = new pt.iflow.api.xml.codegen.processdata.L[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (pt.iflow.api.xml.codegen.processdata.L) _lList.get(index);
        }
        return mArray;
    } //-- pt.iflow.api.xml.codegen.processdata.L[] getL() 

    /**
     * Method getLCount
     * 
     * 
     * 
     * @return int
     */
    public int getLCount()
    {
        return _lList.size();
    } //-- int getLCount() 

    /**
     * Returns the value of field 'lastUpdate'.
     * 
     * @return Date
     * @return the value of field 'lastUpdate'.
     */
    public java.util.Date getLastUpdate()
    {
        return this._lastUpdate;
    } //-- java.util.Date getLastUpdate() 

    /**
     * Returns the value of field 'pid'.
     * 
     * @return int
     * @return the value of field 'pid'.
     */
    public int getPid()
    {
        return this._pid;
    } //-- int getPid() 

    /**
     * Returns the value of field 'pnumber'.
     * 
     * @return String
     * @return the value of field 'pnumber'.
     */
    public java.lang.String getPnumber()
    {
        return this._pnumber;
    } //-- java.lang.String getPnumber() 

    /**
     * Returns the value of field 'spid'.
     * 
     * @return int
     * @return the value of field 'spid'.
     */
    public int getSpid()
    {
        return this._spid;
    } //-- int getSpid() 

    /**
     * Method hasClosed
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasClosed()
    {
        return this._has_closed;
    } //-- boolean hasClosed() 

    /**
     * Method hasFid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasFid()
    {
        return this._has_fid;
    } //-- boolean hasFid() 

    /**
     * Method hasPid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasPid()
    {
        return this._has_pid;
    } //-- boolean hasPid() 

    /**
     * Method hasSpid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasSpid()
    {
        return this._has_spid;
    } //-- boolean hasSpid() 

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
     * Method removeA
     * 
     * 
     * 
     * @param vA
     * @return boolean
     */
    public boolean removeA(pt.iflow.api.xml.codegen.processdata.A vA)
    {
        boolean removed = _aList.remove(vA);
        return removed;
    } //-- boolean removeA(pt.iflow.api.xml.codegen.processdata.A) 

    /**
     * Method removeL
     * 
     * 
     * 
     * @param vL
     * @return boolean
     */
    public boolean removeL(pt.iflow.api.xml.codegen.processdata.L vL)
    {
        boolean removed = _lList.remove(vL);
        return removed;
    } //-- boolean removeL(pt.iflow.api.xml.codegen.processdata.L) 

    /**
     * Method setA
     * 
     * 
     * 
     * @param index
     * @param vA
     */
    public void setA(int index, pt.iflow.api.xml.codegen.processdata.A vA)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _aList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _aList.set(index, vA);
    } //-- void setA(int, pt.iflow.api.xml.codegen.processdata.A) 

    /**
     * Method setA
     * 
     * 
     * 
     * @param aArray
     */
    public void setA(pt.iflow.api.xml.codegen.processdata.A[] aArray)
    {
        //-- copy array
        _aList.clear();
        for (int i = 0; i < aArray.length; i++) {
            _aList.add(aArray[i]);
        }
    } //-- void setA(pt.iflow.api.xml.codegen.processdata.A) 

    /**
     * Sets the value of field 'closed'.
     * 
     * @param closed the value of field 'closed'.
     */
    public void setClosed(boolean closed)
    {
        this._closed = closed;
        this._has_closed = true;
    } //-- void setClosed(boolean) 

    /**
     * Sets the value of field 'creationDate'.
     * 
     * @param creationDate the value of field 'creationDate'.
     */
    public void setCreationDate(java.util.Date creationDate)
    {
        this._creationDate = creationDate;
    } //-- void setCreationDate(java.util.Date) 

    /**
     * Sets the value of field 'creator'.
     * 
     * @param creator the value of field 'creator'.
     */
    public void setCreator(java.lang.String creator)
    {
        this._creator = creator;
    } //-- void setCreator(java.lang.String) 

    /**
     * Sets the value of field 'currentUser'.
     * 
     * @param currentUser the value of field 'currentUser'.
     */
    public void setCurrentUser(java.lang.String currentUser)
    {
        this._currentUser = currentUser;
    } //-- void setCurrentUser(java.lang.String) 

    /**
     * Sets the value of field 'e'.
     * 
     * @param e the value of field 'e'.
     */
    public void setE(java.lang.String e)
    {
        this._e = e;
    } //-- void setE(java.lang.String) 

    /**
     * Sets the value of field 'fid'.
     * 
     * @param fid the value of field 'fid'.
     */
    public void setFid(int fid)
    {
        this._fid = fid;
        this._has_fid = true;
    } //-- void setFid(int) 

    /**
     * Method setL
     * 
     * 
     * 
     * @param index
     * @param vL
     */
    public void setL(int index, pt.iflow.api.xml.codegen.processdata.L vL)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _lList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _lList.set(index, vL);
    } //-- void setL(int, pt.iflow.api.xml.codegen.processdata.L) 

    /**
     * Method setL
     * 
     * 
     * 
     * @param lArray
     */
    public void setL(pt.iflow.api.xml.codegen.processdata.L[] lArray)
    {
        //-- copy array
        _lList.clear();
        for (int i = 0; i < lArray.length; i++) {
            _lList.add(lArray[i]);
        }
    } //-- void setL(pt.iflow.api.xml.codegen.processdata.L) 

    /**
     * Sets the value of field 'lastUpdate'.
     * 
     * @param lastUpdate the value of field 'lastUpdate'.
     */
    public void setLastUpdate(java.util.Date lastUpdate)
    {
        this._lastUpdate = lastUpdate;
    } //-- void setLastUpdate(java.util.Date) 

    /**
     * Sets the value of field 'pid'.
     * 
     * @param pid the value of field 'pid'.
     */
    public void setPid(int pid)
    {
        this._pid = pid;
        this._has_pid = true;
    } //-- void setPid(int) 

    /**
     * Sets the value of field 'pnumber'.
     * 
     * @param pnumber the value of field 'pnumber'.
     */
    public void setPnumber(java.lang.String pnumber)
    {
        this._pnumber = pnumber;
    } //-- void setPnumber(java.lang.String) 

    /**
     * Sets the value of field 'spid'.
     * 
     * @param spid the value of field 'spid'.
     */
    public void setSpid(int spid)
    {
        this._spid = spid;
        this._has_spid = true;
    } //-- void setSpid(int) 

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
        return (pt.iflow.api.xml.codegen.processdata.Processdata) Unmarshaller.unmarshal(pt.iflow.api.xml.codegen.processdata.Processdata.class, reader);
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
