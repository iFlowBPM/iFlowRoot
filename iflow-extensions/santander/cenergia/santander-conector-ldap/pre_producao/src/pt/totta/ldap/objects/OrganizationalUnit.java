package pt.totta.ldap.objects;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap OrganizationalUnit object.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class OrganizationalUnit
{
  /**
   * This method will fill the OrganizationalUnit object info.
   * <p>
   * @param attributes the Attributes to scan.
   * @return An hashtable with the organical unit information.<br>
   * Keys for the hashtable are: 
   *  "centrocusto"
   *  "nome"
   *  "marcaorgunit"
   *  "moradarua"
   *  "moradalocalidade"
   *  "moradacodigopostal"
   *  "moradacodzona"
   *  "moradadesczona"
   *  "telefoneextensao"
   *  "telefonefixo"
   *  "faxinterno"
   *  "faxexterno"
   *  "emailexterno"
   *  "emailinterno"
   *  "loginpublicoresponsavel"
   *  "perfilsintra"
   *  "perfilpartenon"
   *  "idpai"
   *  "sit"
   */
  public Hashtable fillInfo2(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // To store the revelant attributes.
    String centrocusto = null;
    String nome = null;
    String marcaorgunit = null;
    String moradarua = null;
    String moradalocalidade = null;
    String moradacodigopostal = null;
    String moradacodzona = null;
    String moradadesczona = null;
    String telefoneextensao = null;
    String telefonefixo = null;
    String faxinterno = null;
    String faxexterno = null;
    String emailexterno = null;
    String emailinterno = null;
    String loginpublicoresponsavel = null;
    String perfilsintra = null;
    String perfilpartenon = null;
    String idpai = null;
    String sit = null;
    String dnvalue = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        
        if(attribute.getID().equals("centrocusto")) centrocusto = (String) attribute.get();
        if(attribute.getID().equals("nome")) nome = (String) attribute.get();
        if(attribute.getID().equals("marcaorgunit")) marcaorgunit = (String) attribute.get();
        if(attribute.getID().equals("moradarua")) moradarua = (String) attribute.get();
        if(attribute.getID().equals("moradalocalidade")) moradalocalidade = (String) attribute.get();
        if(attribute.getID().equals("moradacodigopostal")) moradacodigopostal = (String) attribute.get();
        if(attribute.getID().equals("moradacodzona")) moradacodzona = (String) attribute.get();
        if(attribute.getID().equals("moradadesczona")) moradadesczona = (String) attribute.get();
        if(attribute.getID().equals("telefoneextensao")) telefoneextensao = (String) attribute.get();
        if(attribute.getID().equals("telefonefixo")) telefonefixo = (String) attribute.get();
        if(attribute.getID().equals("faxinterno")) faxinterno = (String) attribute.get();
        if(attribute.getID().equals("faxexterno")) faxexterno = (String) attribute.get();
        if(attribute.getID().equals("emailexterno")) emailexterno = (String) attribute.get();
        if(attribute.getID().equals("emailinterno")) emailinterno = (String) attribute.get();
        if(attribute.getID().equals("loginpublicoresponsavel")) loginpublicoresponsavel = (String) attribute.get();
        if(attribute.getID().equals("perfilsintra")) perfilsintra = (String) attribute.get();
        if(attribute.getID().equals("perfilpartenon")) perfilpartenon = (String) attribute.get();
        if(attribute.getID().equals("sit")) sit = (String) attribute.get();
        if(attribute.getID().equals("idpai")) idpai = (String) attribute.get();
        if(attribute.getID().equals("dn")) dnvalue = (String) attribute.get();
        
        if((centrocusto != null) && 
           (nome != null) && 
           (marcaorgunit != null) && 
           (moradarua != null) && 
           (moradalocalidade != null) && 
           (moradacodigopostal != null) && 
           (moradacodzona != null) && 
           (moradadesczona != null) && 
           (telefoneextensao != null) && 
           (telefonefixo != null) && 
           (faxinterno != null) && 
           (faxexterno != null) && 
           (emailexterno != null) && 
           (emailinterno != null) && 
           (loginpublicoresponsavel != null) && 
           (perfilsintra != null) && 
           (perfilpartenon != null) && 
           (sit != null) && 
           (idpai != null) &&
           (dnvalue != null))
          break;
      }
      
      if(centrocusto == null) centrocusto = "";
      if(nome == null) nome = "";
      if(marcaorgunit == null) marcaorgunit = "";
      if(moradarua == null) moradarua = "";
      if(moradalocalidade == null) moradalocalidade = "";
      if(moradacodigopostal == null) moradacodigopostal = "";
      if(moradacodzona == null) moradacodzona = "";
      if(moradadesczona == null) moradadesczona = "";
      if(telefoneextensao == null) telefoneextensao = "";
      if(telefonefixo == null) telefonefixo = "";
      if(faxinterno == null) faxinterno = "";
      if(faxexterno == null) faxexterno = "";
      if(emailexterno == null) emailexterno = "";
      if(emailinterno == null) emailinterno = "";
      if(loginpublicoresponsavel == null) loginpublicoresponsavel = "";
      if(perfilsintra == null) perfilsintra = "";
      if(perfilpartenon == null) perfilpartenon = "";
      if(sit == null) sit = "";
      if(idpai == null) idpai = "";
      if(dnvalue == null) dnvalue = "";
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.OrganizationalUnit.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    // Place the relevant attributes on the resultsHashtable.
    resultsHashtable.put("centrocusto", centrocusto);
    resultsHashtable.put("nome", nome);
    resultsHashtable.put("marcaorgunit", marcaorgunit);
    resultsHashtable.put("moradarua", moradarua);
    resultsHashtable.put("moradalocalidade", moradalocalidade);
    resultsHashtable.put("moradacodigopostal", moradacodigopostal);
    resultsHashtable.put("moradacodzona", moradacodzona);
    resultsHashtable.put("moradadesczona", moradadesczona);
    resultsHashtable.put("telefoneextensao", telefoneextensao);
    resultsHashtable.put("telefonefixo", telefonefixo);
    resultsHashtable.put("faxinterno", faxinterno);
    resultsHashtable.put("faxexterno", faxexterno);
    resultsHashtable.put("emailexterno", emailexterno);
    resultsHashtable.put("emailinterno", emailinterno);
    resultsHashtable.put("loginpublicoresponsavel", loginpublicoresponsavel);
    resultsHashtable.put("perfilsintra", perfilsintra);
    resultsHashtable.put("perfilpartenon", perfilpartenon);
    resultsHashtable.put("sit", sit);
    resultsHashtable.put("idpai", idpai);
    resultsHashtable.put("dn", dnvalue);
    
    return resultsHashtable;
  }
  
  public Hashtable fillInfo(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        resultsHashtable.put(attribute.getID(), attribute.get());
        
      }
      
      if (!resultsHashtable.containsKey("centrocusto")) resultsHashtable.put("centrocusto", "");
      if (!resultsHashtable.containsKey("nome")) resultsHashtable.put("nome", "");
      if (!resultsHashtable.containsKey("marcaorgunit")) resultsHashtable.put("marcaorgunit", "");
      if (!resultsHashtable.containsKey("moradarua")) resultsHashtable.put("moradarua", "");
      if (!resultsHashtable.containsKey("moradalocalidade")) resultsHashtable.put("moradalocalidade", "");
      if (!resultsHashtable.containsKey("moradacodigopostal")) resultsHashtable.put("moradacodigopostal", "");
      if (!resultsHashtable.containsKey("moradacodzona")) resultsHashtable.put("moradacodzona", "");
      if (!resultsHashtable.containsKey("moradadesczona")) resultsHashtable.put("moradadesczona", "");
      if (!resultsHashtable.containsKey("telefoneextensao")) resultsHashtable.put("telefoneextensao", "");
      if (!resultsHashtable.containsKey("telefonefixo")) resultsHashtable.put("telefonefixo", "");
      if (!resultsHashtable.containsKey("faxinterno")) resultsHashtable.put("faxinterno", "");
      if (!resultsHashtable.containsKey("faxexterno")) resultsHashtable.put("faxexterno", "");
      if (!resultsHashtable.containsKey("emailexterno")) resultsHashtable.put("emailexterno", "");
      if (!resultsHashtable.containsKey("emailinterno")) resultsHashtable.put("emailinterno", "");
      if (!resultsHashtable.containsKey("loginpublicoresponsavel")) resultsHashtable.put("loginpublicoresponsavel", "");
      if (!resultsHashtable.containsKey("perfilsintra")) resultsHashtable.put("perfilsintra", "");
      if (!resultsHashtable.containsKey("perfilpartenon")) resultsHashtable.put("perfilpartenon", "");
      if (!resultsHashtable.containsKey("sit")) resultsHashtable.put("sit", "");
      if (!resultsHashtable.containsKey("idpai")) resultsHashtable.put("idpai", "");
      
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.OrganizationalUnit.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    return resultsHashtable;
  }
}