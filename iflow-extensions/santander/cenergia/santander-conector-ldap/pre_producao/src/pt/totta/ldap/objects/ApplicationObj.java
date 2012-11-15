package pt.totta.ldap.objects;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap ApplicationObj object.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class ApplicationObj
{
  /**
   * This method will fill the ApplicationObj object info.
   * <p>
   * @param attributes the Attributes to scan.
   * @return An hashtable with the application information.<br>
   * Keys for the hashtable are: 
   *  "a"
   *  "dnvalue"
   *  "nomeaplicacao"
   *  "nivel"
   */
  public Hashtable fillInfo2(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // To store the revelant attributes.
    String a = null;
    String dnvalue = null;
    String nomeaplicacao = null;
    String nivel = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        
        if(attribute.getID().equals("a")) a = (String) attribute.get();
        if(attribute.getID().equals("dn")) dnvalue = (String) attribute.get();
        if(attribute.getID().equals("nomeaplicacao")) nomeaplicacao = (String) attribute.get();
        if(attribute.getID().equals("nivel")) nivel = (String) attribute.get();
        
        if((a != null) && 
           (dnvalue != null) && 
           (nomeaplicacao != null) && 
           (nivel != null))
          break;
      }
      
      if(a == null) a = "";
      if(dnvalue == null) dnvalue = "";
      if(nomeaplicacao == null) nomeaplicacao = "";
      if(nivel == null) nivel = "";
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ApplicationObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    // Place the relevant attributes on the resultsHashtable.
    resultsHashtable.put("a", a);
    resultsHashtable.put("dn", dnvalue);
    resultsHashtable.put("nomeaplicacao", nomeaplicacao);
    resultsHashtable.put("nivel", nivel);
    
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
      
      if (!resultsHashtable.containsKey("a")) resultsHashtable.put("a", "");
      if (!resultsHashtable.containsKey("nomeaplicacao")) resultsHashtable.put("nomeaplicacao", "");
      if (!resultsHashtable.containsKey("nivel")) resultsHashtable.put("nivel", "");
      
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ApplicationObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    return resultsHashtable;
  }
}