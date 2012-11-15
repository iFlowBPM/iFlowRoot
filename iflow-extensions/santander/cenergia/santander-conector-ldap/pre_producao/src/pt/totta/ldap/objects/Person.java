package pt.totta.ldap.objects;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap Person object.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class Person
{
  /**
   * This method will fill the Person object info.
   * <p>
   * @param attributes the Attributes to scan.
   * @return An hashtable with the user information.<br>
   * Keys for the hashtable are: 
   *  "loginpublico"
   *  "loginprivado"
   *  "numeroempregado"
   *  "nomeutilizador"
   *  "sexoutilizador"
   *  "marcautilizador"
   *  "marcautilizadorprestacaoservico"
   *  "centrocusto"
   *  "centrocustoprestacaoservico"
   *  "unidadeorganizativa"
   *  "codigoposto"
   *  "posto"
   *  "tipoestrutura"
   *  "emailexterno"
   *  "emailinterno"
   *  "telefoneextensao"
   *  "telefonefixo"
   *  "telefonemovel"
   *  "faxexterno"
   *  "faxinterno"
   *  "sessionidutilizador"
   *  "propriedades"
   *  "perfilexecucao"
   *  "indacessostaff"
   *  "indgestorcliente"
   *  "indgestorconta"
   *  "codigogestor"
   */
  public Hashtable fillInfo2(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // To store the revelant attributes.
    String loginpublico = null;
    String loginprivado = null;
    String numeroempregado = null;
    String nomeutilizador = null;
    String sexoutilizador = null;
    String marcautilizador = null;
    String marcautilizadorprestacaoservico = null;
    String centrocusto = null;
    String centrocustoprestacaoservico = null;
    String unidadeorganizativa = null;
    String codigoposto = null;
    String posto = null;
    String tipoestrutura = null;
    String emailexterno = null;
    String emailinterno = null;
    String telefoneextensao = null;
    String telefonefixo = null;
    String telefonemovel = null;
    String faxexterno = null;
    String faxinterno = null;
    String sessionidutilizador = null;
    String propriedades = null;
    String dnvalue = null;
    String perfilexecucao = null;
    String indacessostaff = null;
    String indgestorcliente = null;
    String indgestorconta = null;
    String codigogestor = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        
        if(attribute.getID().equals("loginpublico")) loginpublico = (String) attribute.get();
        if(attribute.getID().equals("loginprivado")) loginprivado = (String) attribute.get();
        if(attribute.getID().equals("numeroempregado")) numeroempregado = (String) attribute.get();
        if(attribute.getID().equals("nomeutilizador")) nomeutilizador = (String) attribute.get();
        if(attribute.getID().equals("sexoutilizador")) sexoutilizador = (String) attribute.get();
        if(attribute.getID().equals("marcautilizador")) marcautilizador = (String) attribute.get();
        if(attribute.getID().equals("marcautilizadorprestacaoservico")) marcautilizadorprestacaoservico = (String) attribute.get();
        if(attribute.getID().equals("centrocusto")) centrocusto = (String) attribute.get();
        if(attribute.getID().equals("centrocustoprestacaoservico")) centrocustoprestacaoservico = (String) attribute.get();
        if(attribute.getID().equals("unidadeorganizativa")) unidadeorganizativa = (String) attribute.get();
        if(attribute.getID().equals("codigoposto")) codigoposto = (String) attribute.get();
        if(attribute.getID().equals("posto")) posto = (String) attribute.get();
        if(attribute.getID().equals("tipoestrutura")) tipoestrutura = (String) attribute.get();
        if(attribute.getID().equals("emailexterno")) emailexterno = (String) attribute.get();
        if(attribute.getID().equals("emailinterno")) emailinterno = (String) attribute.get();
        if(attribute.getID().equals("telefoneextensao")) telefoneextensao = (String) attribute.get();
        if(attribute.getID().equals("telefonefixo")) telefonefixo = (String) attribute.get();
        if(attribute.getID().equals("telefonemovel")) telefonemovel = (String) attribute.get();
        if(attribute.getID().equals("faxexterno")) faxexterno = (String) attribute.get();
        if(attribute.getID().equals("faxinterno")) faxinterno = (String) attribute.get();
        if(attribute.getID().equals("sessionidutilizador")) sessionidutilizador = (String) attribute.get();
        if(attribute.getID().equals("propriedades")) propriedades = (String) attribute.get();
        if(attribute.getID().equals("dn")) dnvalue = (String) attribute.get();
        if(attribute.getID().equals("perfilexecucao")) perfilexecucao = (String) attribute.get();
        if(attribute.getID().equals("indacessostaff")) indacessostaff = (String) attribute.get();
        if(attribute.getID().equals("indgestorcliente")) indgestorcliente = (String) attribute.get();
        if(attribute.getID().equals("indgestorconta")) indgestorconta = (String) attribute.get();
        if(attribute.getID().equals("codigogestor")) codigogestor = (String) attribute.get();
        
        if((loginpublico != null) && 
           (loginprivado != null) && 
           (numeroempregado != null) && 
           (nomeutilizador != null) && 
           (sexoutilizador != null) && 
           (marcautilizador != null) && 
           (marcautilizadorprestacaoservico != null) && 
           (centrocusto != null) && 
           (centrocustoprestacaoservico != null) && 
           (unidadeorganizativa != null) && 
           (codigoposto != null) && 
           (posto != null) && 
           (tipoestrutura != null) && 
           (emailexterno != null) && 
           (emailinterno != null) && 
           (telefoneextensao != null) && 
           (telefonefixo != null) && 
           (telefonemovel != null) && 
           (faxexterno != null) && 
           (faxinterno != null) && 
           (sessionidutilizador != null) && 
           (propriedades != null) && 
           (perfilexecucao != null) && 
           (indacessostaff != null) && 
           (indgestorcliente != null) && 
           (indgestorconta != null) &&
           (codigogestor != null))
          break;
      }
      
      if(loginpublico == null) loginpublico = "";
      if(loginprivado == null) loginprivado = "";
      if(numeroempregado == null) numeroempregado = "";
      if(nomeutilizador == null) nomeutilizador = "";
      if(sexoutilizador == null) sexoutilizador = "";
      if(marcautilizador == null) marcautilizador = "";
      if(marcautilizadorprestacaoservico == null) marcautilizadorprestacaoservico = "";
      if(centrocusto == null) centrocusto = "";
      if(centrocustoprestacaoservico == null) centrocustoprestacaoservico = "";
      if(unidadeorganizativa == null) unidadeorganizativa = "";
      if(codigoposto == null) codigoposto = "";
      if(posto == null) posto = "";
      if(tipoestrutura == null) tipoestrutura = "";
      if(emailexterno == null) emailexterno = "";
      if(emailinterno == null) emailinterno = "";
      if(telefoneextensao == null) telefoneextensao = "";
      if(telefonefixo == null) telefonefixo = "";
      if(telefonemovel == null) telefonemovel = "";
      if(faxexterno == null) faxexterno = "";
      if(faxinterno == null) faxinterno = "";
      if(sessionidutilizador == null) sessionidutilizador = "";
      if(propriedades == null) propriedades = "";
      if(dnvalue == null) dnvalue = "";
      if(perfilexecucao == null) perfilexecucao = "";
      if(indacessostaff == null) indacessostaff = "";
      if(indgestorcliente == null) indgestorcliente = "";
      if(indgestorconta == null) indgestorconta = "";
      if(codigogestor == null) codigogestor = "";
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.Person.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    // Place the relevant attributes on the resultsHashtable.
    resultsHashtable.put("loginpublico", loginpublico);
    resultsHashtable.put("loginprivado", loginprivado);
    resultsHashtable.put("numeroempregado", numeroempregado);
    resultsHashtable.put("nomeutilizador", nomeutilizador);
    resultsHashtable.put("sexoutilizador", sexoutilizador);
    resultsHashtable.put("marcautilizador", marcautilizador);
    resultsHashtable.put("marcautilizadorprestacaoservico", marcautilizadorprestacaoservico);
    resultsHashtable.put("centrocusto", centrocusto);
    resultsHashtable.put("centrocustoprestacaoservico", centrocustoprestacaoservico);
    resultsHashtable.put("unidadeorganizativa", unidadeorganizativa);
    resultsHashtable.put("codigoposto", codigoposto);
    resultsHashtable.put("posto", posto);
    resultsHashtable.put("tipoestrutura", tipoestrutura);
    resultsHashtable.put("emailexterno", emailexterno);
    resultsHashtable.put("emailinterno", emailinterno);
    resultsHashtable.put("telefoneextensao", telefoneextensao);
    resultsHashtable.put("telefonefixo", telefonefixo);
    resultsHashtable.put("telefonemovel", telefonemovel);
    resultsHashtable.put("faxexterno", faxexterno);
    resultsHashtable.put("faxinterno", faxinterno);
    resultsHashtable.put("sessionidutilizador", sessionidutilizador);
    resultsHashtable.put("propriedades", propriedades);
    resultsHashtable.put("dn", dnvalue);
    resultsHashtable.put("perfilexecucao", perfilexecucao);
    resultsHashtable.put("indacessostaff", indacessostaff);
    resultsHashtable.put("indgestorcliente", indgestorcliente);
    resultsHashtable.put("indgestorconta", indgestorconta);
    resultsHashtable.put("codigogestor", codigogestor);
    
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
      
      if (!resultsHashtable.containsKey("loginpublico")) resultsHashtable.put("loginpublico", "");
      if (!resultsHashtable.containsKey("loginprivado")) resultsHashtable.put("loginprivado", "");
      if (!resultsHashtable.containsKey("numeroempregado")) resultsHashtable.put("numeroempregado", "");
      if (!resultsHashtable.containsKey("nomeutilizador")) resultsHashtable.put("nomeutilizador", "");
      if (!resultsHashtable.containsKey("sexoutilizador")) resultsHashtable.put("sexoutilizador", "");
      if (!resultsHashtable.containsKey("marcautilizador")) resultsHashtable.put("marcautilizador", "");
      if (!resultsHashtable.containsKey("marcautilizadorprestacaoservico")) resultsHashtable.put("marcautilizadorprestacaoservico", "");
      if (!resultsHashtable.containsKey("centrocusto")) resultsHashtable.put("centrocusto", "");
      if (!resultsHashtable.containsKey("centrocustoprestacaoservico")) resultsHashtable.put("centrocustoprestacaoservico", "");
      if (!resultsHashtable.containsKey("unidadeorganizativa")) resultsHashtable.put("unidadeorganizativa", "");
      if (!resultsHashtable.containsKey("codigoposto")) resultsHashtable.put("codigoposto", "");
      if (!resultsHashtable.containsKey("posto")) resultsHashtable.put("posto", "");
      if (!resultsHashtable.containsKey("tipoestrutura")) resultsHashtable.put("tipoestrutura", "");
      if (!resultsHashtable.containsKey("emailexterno")) resultsHashtable.put("emailexterno", "");
      if (!resultsHashtable.containsKey("emailinterno")) resultsHashtable.put("emailinterno", "");
      if (!resultsHashtable.containsKey("telefoneextensao")) resultsHashtable.put("telefoneextensao", "");
      if (!resultsHashtable.containsKey("telefonefixo")) resultsHashtable.put("telefonefixo", "");
      if (!resultsHashtable.containsKey("telefonemovel")) resultsHashtable.put("telefonemovel", "");
      if (!resultsHashtable.containsKey("faxexterno")) resultsHashtable.put("faxexterno", "");
      if (!resultsHashtable.containsKey("faxinterno")) resultsHashtable.put("faxinterno", "");
      if (!resultsHashtable.containsKey("sessionidutilizador")) resultsHashtable.put("sessionidutilizador", "");
      if (!resultsHashtable.containsKey("propriedades")) resultsHashtable.put("propriedades", "");
      if (!resultsHashtable.containsKey("perfilexecucao")) resultsHashtable.put("perfilexecucao", "");
      if (!resultsHashtable.containsKey("indacessostaff")) resultsHashtable.put("indacessostaff", "");
      if (!resultsHashtable.containsKey("indgestorcliente")) resultsHashtable.put("indgestorcliente", "");
      if (!resultsHashtable.containsKey("indgestorconta")) resultsHashtable.put("indgestorconta", "");
      if (!resultsHashtable.containsKey("codigogestor")) resultsHashtable.put("codigogestor", "");
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.Person.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    return resultsHashtable;
  }
}