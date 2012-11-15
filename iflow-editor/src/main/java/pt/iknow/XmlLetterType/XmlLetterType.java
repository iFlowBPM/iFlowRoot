/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlLetterType.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlLetterType;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public class XmlLetterType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private TipoPequeno _tipoPequeno;

    private TipoGrande _tipoGrande;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlLetterType() {
        super();
    } //-- pt.iknow.XmlLetterType.XmlLetterType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public TipoGrande getTipoGrande()
    {
        return this._tipoGrande;
    } //-- TipoGrande getTipoGrande() 

    /**
    **/
    public TipoPequeno getTipoPequeno()
    {
        return this._tipoPequeno;
    } //-- TipoPequeno getTipoPequeno() 

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * 
     * @param tipoGrande
    **/
    public void setTipoGrande(TipoGrande tipoGrande)
    {
        this._tipoGrande = tipoGrande;
    } //-- void setTipoGrande(TipoGrande) 

    /**
     * 
     * @param tipoPequeno
    **/
    public void setTipoPequeno(TipoPequeno tipoPequeno)
    {
        this._tipoPequeno = tipoPequeno;
    } //-- void setTipoPequeno(TipoPequeno) 

    /**
     * 
     * @param reader
    **/
    public static pt.iknow.XmlLetterType.XmlLetterType unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (pt.iknow.XmlLetterType.XmlLetterType) Unmarshaller.unmarshal(pt.iknow.XmlLetterType.XmlLetterType.class, reader);
    } //-- pt.iknow.XmlLetterType.XmlLetterType unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
