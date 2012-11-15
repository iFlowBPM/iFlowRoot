/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlColorType.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlColor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public abstract class XmlColorType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _item;

    private Rgb _rgb;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlColorType() {
        super();
    } //-- pt.iknow.XmlColor.XmlColorType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getItem()
    {
        return this._item;
    } //-- java.lang.String getItem() 

    /**
    **/
    public Rgb getRgb()
    {
        return this._rgb;
    } //-- Rgb getRgb() 

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
     * @param item
    **/
    public void setItem(java.lang.String item)
    {
        this._item = item;
    } //-- void setItem(java.lang.String) 

    /**
     * 
     * @param rgb
    **/
    public void setRgb(Rgb rgb)
    {
        this._rgb = rgb;
    } //-- void setRgb(Rgb) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
