package pt.totta.ldap.objects;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

//import org.apache.log4j.Logger;

/**
 * This class implements the Ldap ApplicationProfileObj object.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class ApplicationProfileObj
{
	
  //private Logger logger = Logger.getLogger("TP.ApplicationProfileObj");
  /**
   * This method will fill the ApplicationProfileObj object info.
   * <p>
   * @param attributes the Attributes to scan.
   * @return An hashtable with the application profile information.<br>
   * Keys for the hashtable are: 
   *  "pf"
   *  "nivelperfil"
   *  "nivel"
   *  "dnvalue"
   */
  public Hashtable fillInfo2(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // To store the revelant attributes.
    String pf = null;
    String nivelperfil = null;
    String nivel = null;
    String dnvalue = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        
        if(attribute.getID().equals("pf")) pf = (String) attribute.get();
        if(attribute.getID().equals("nivelperfil")) nivelperfil = (String) attribute.get();
        if(attribute.getID().equals("nivel")) nivel = (String) attribute.get();
        if(attribute.getID().equals("dn")) dnvalue = (String) attribute.get();
        
        if((pf != null) && 
           (nivelperfil != null) && 
           (nivel != null) && 
           (dnvalue != null))
          break;
      }
      
      if(pf == null) pf = "";
      if(nivelperfil == null) nivelperfil = "";
      if(nivel == null) nivel = "";
      if(dnvalue == null) dnvalue = "";
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ApplicationProfileObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    // Place the relevant attributes on the resultsHashtable.
    resultsHashtable.put("pf", pf);
    resultsHashtable.put("nivelperfil", nivelperfil);
    resultsHashtable.put("nivel", nivel);
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
      
      
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ApplicationProfileObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    return resultsHashtable;
  }
}