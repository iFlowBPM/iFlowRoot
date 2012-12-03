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
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlColorDescription.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlColor2;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public abstract class XmlColorDescription implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _reg;

    /**
     * keeps track of state for field: _reg
    **/
    private boolean _has_reg;

    private int _green;

    /**
     * keeps track of state for field: _green
    **/
    private boolean _has_green;

    private int _blue;

    /**
     * keeps track of state for field: _blue
    **/
    private boolean _has_blue;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlColorDescription() {
        super();
    } //-- pt.iknow.XmlColor2.XmlColorDescription()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public int getBlue()
    {
        return this._blue;
    } //-- int getBlue() 

    /**
    **/
    public int getGreen()
    {
        return this._green;
    } //-- int getGreen() 

    /**
    **/
    public int getReg()
    {
        return this._reg;
    } //-- int getReg() 

    /**
    **/
    public boolean hasBlue()
    {
        return this._has_blue;
    } //-- boolean hasBlue() 

    /**
    **/
    public boolean hasGreen()
    {
        return this._has_green;
    } //-- boolean hasGreen() 

    /**
    **/
    public boolean hasReg()
    {
        return this._has_reg;
    } //-- boolean hasReg() 

    /**
    **/
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
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param blue
    **/
    public void setBlue(int blue)
    {
        this._blue = blue;
        this._has_blue = true;
    } //-- void setBlue(int) 

    /**
     * 
     * @param green
    **/
    public void setGreen(int green)
    {
        this._green = green;
        this._has_green = true;
    } //-- void setGreen(int) 

    /**
     * 
     * @param reg
    **/
    public void setReg(int reg)
    {
        this._reg = reg;
        this._has_reg = true;
    } //-- void setReg(int) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
