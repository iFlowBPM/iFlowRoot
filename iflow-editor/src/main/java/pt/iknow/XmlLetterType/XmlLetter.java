/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id: XmlLetter.java 40 2007-08-17 18:48:03Z uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iknow.XmlLetterType;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * @version $Revision: 40 $ $Date: 2007-08-17 19:48:03 +0100 (Fri, 17 Aug 2007) $
**/
public abstract class XmlLetter implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _nome;

    private int _tamanho;

    /**
     * keeps track of state for field: _tamanho
    **/
    private boolean _has_tamanho;

    private java.lang.String _tipe;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlLetter() {
        super();
    } //-- pt.iknow.XmlLetterType.XmlLetter()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getNome()
    {
        return this._nome;
    } //-- java.lang.String getNome() 

    /**
    **/
    public int getTamanho()
    {
        return this._tamanho;
    } //-- int getTamanho() 

    /**
    **/
    public java.lang.String getTipe()
    {
        return this._tipe;
    } //-- java.lang.String getTipe() 

    /**
    **/
    public boolean hasTamanho()
    {
        return this._has_tamanho;
    } //-- boolean hasTamanho() 

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
     * @param nome
    **/
    public void setNome(java.lang.String nome)
    {
        this._nome = nome;
    } //-- void setNome(java.lang.String) 

    /**
     * 
     * @param tamanho
    **/
    public void setTamanho(int tamanho)
    {
        this._tamanho = tamanho;
        this._has_tamanho = true;
    } //-- void setTamanho(int) 

    /**
     * 
     * @param tipe
    **/
    public void setTipe(java.lang.String tipe)
    {
        this._tipe = tipe;
    } //-- void setTipe(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
