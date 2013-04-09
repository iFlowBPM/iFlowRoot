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

package pt.iflow.api.xml.codegen.library.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class BoolType.
 * 
 * @version $Revision$ $Date$
 */
public class BoolType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The yes type
     */
    public static final int YES_TYPE = 0;

    /**
     * The instance of the yes type
     */
    public static final BoolType YES = new BoolType(YES_TYPE, "yes");

    /**
     * The no type
     */
    public static final int NO_TYPE = 1;

    /**
     * The instance of the no type
     */
    public static final BoolType NO = new BoolType(NO_TYPE, "no");

    /**
     * The true type
     */
    public static final int TRUE_TYPE = 2;

    /**
     * The instance of the true type
     */
    public static final BoolType TRUE = new BoolType(TRUE_TYPE, "true");

    /**
     * The false type
     */
    public static final int FALSE_TYPE = 3;

    /**
     * The instance of the false type
     */
    public static final BoolType FALSE = new BoolType(FALSE_TYPE, "false");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private BoolType(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- pt.iflow.api.xml.codegen.library.types.BoolType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of BoolType
     * 
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getType
     * 
     * Returns the type of this BoolType
     * 
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     * 
     * 
     * 
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("yes", YES);
        members.put("no", NO);
        members.put("true", TRUE);
        members.put("false", FALSE);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method readResolve
     * 
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     * 
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve() 

    /**
     * Method toString
     * 
     * Returns the String representation of this BoolType
     * 
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOf
     * 
     * Returns a new BoolType based on the given String value.
     * 
     * @param string
     * @return BoolType
     */
    public static pt.iflow.api.xml.codegen.library.types.BoolType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid BoolType";
            throw new IllegalArgumentException(err);
        }
        return (BoolType) obj;
    } //-- pt.iflow.api.xml.codegen.library.types.BoolType valueOf(java.lang.String) 

}
