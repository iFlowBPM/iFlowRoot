package pt.totta.ldap.objects;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap ProfilePersonObj object.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class ProfilePersonObj
{
  /**
   * This method will fill the ProfilePersonObj object info.
   * <p>
   * @param attributes the Attributes to scan.
   * @return An hashtable with the profile person information.<br>
   * Keys for the hashtable are: 
   *  "pp"
   *  "profileuserid"
   *  "profileuntildate"
   *  "nivel"
   *  "dnvalue"
   *  "creationdate"
   *  "creationtype"
   *  "profileusername"
   *  "profileuserfullname"
   *  "profileuseridandname"
   */
  public Hashtable fillInfo2(Attributes attributes)
  {
    // To store the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // To store the attributes.
    NamingEnumeration attributesEnumeration = null;
    
    // To store the revelant attributes.
    String pp = null;
    String profileuserid = null;
    String profileuntildate = null;
    String nivel = null;
    String dnvalue = null;
    String creationdate = null;
    String creationtype = null;
    String profileusername = null;
    String profileuserfullname = null;
    String profileuseridandname = null;
    
    // Scan all the attributes looking in particular for the relevant ones.
    try
    {
      for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
      {
        Attribute attribute = (Attribute) attributesEnumeration.next();
        
        if(attribute.getID().equals("pp")) pp = (String) attribute.get();
        if(attribute.getID().equals("profileuserid")) profileuserid = (String) attribute.get();
        if(attribute.getID().equals("profileuntildate")) profileuntildate = (String) attribute.get();
        if(attribute.getID().equals("nivel")) nivel = (String) attribute.get();
        if(attribute.getID().equals("dn")) dnvalue = (String) attribute.get();
        if(attribute.getID().equals("creationdate")) creationdate = (String) attribute.get();
        if(attribute.getID().equals("creationtype")) creationtype = (String) attribute.get();
        
        /**/
        if(attribute.getID().equals("profileusername")) {
        	profileusername = (String) attribute.get();
        }
        
        if(attribute.getID().equals("profileuserfullname")) {
        	profileuserfullname = (String) attribute.get();
        }
        
        if(attribute.getID().equals("profileuseridandname")) {
        	profileuseridandname = (String) attribute.get();
        }
        
        if((pp != null) && 
           (profileuserid != null) && 
           (profileuntildate != null) && 
           (nivel != null) && 
           (dnvalue != null) && 
           (creationdate != null) && 
           (creationtype != null) &&
           (profileusername != null) &&
           (profileuserfullname != null) &&
           (profileuseridandname != null))
          break;
      }
      
      if(pp == null) pp = "";
      if(profileuserid == null) profileuserid = "";
      if(profileuntildate == null) profileuntildate = "";
      if(nivel == null) nivel = "";
      if(dnvalue == null) dnvalue = "";
      if(creationdate == null) creationdate = "";
      if(creationtype == null) creationtype = "";
      
      if(profileusername == null) {
    	  profileusername = "";
      }
      
      if(profileuserfullname == null) {
    	  profileuserfullname = "";
      }
      
      if(profileuseridandname == null) {
    	  profileuseridandname = "";
      }
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ProfilePersonObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    // Place the relevant attributes on the resultsHashtable.
    resultsHashtable.put("pp", pp);
    resultsHashtable.put("profileuserid", profileuserid);
    resultsHashtable.put("profileuntildate", profileuntildate);
    resultsHashtable.put("nivel", nivel);
    resultsHashtable.put("dn", dnvalue);
    resultsHashtable.put("creationdate", creationdate);
    resultsHashtable.put("creationtype", creationtype);
    resultsHashtable.put("creationtype", creationtype);
    
    resultsHashtable.put("profileusername", profileusername);
    resultsHashtable.put("profileuserfullname", profileuserfullname);
    resultsHashtable.put("profileuseridandname", profileuseridandname);
    
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
      
      if (!resultsHashtable.containsKey("pp")) resultsHashtable.put("pp", "");
      if (!resultsHashtable.containsKey("profileuserid")) resultsHashtable.put("profileuserid", "");
      if (!resultsHashtable.containsKey("profileuntildate")) resultsHashtable.put("profileuntildate", "");
      if (!resultsHashtable.containsKey("nivel")) resultsHashtable.put("nivel", "");
      if (!resultsHashtable.containsKey("creationdate")) resultsHashtable.put("creationdate", "");
      if (!resultsHashtable.containsKey("creationtype")) resultsHashtable.put("creationtype", "");
      
      if (!resultsHashtable.containsKey("profileusername")) resultsHashtable.put("profileusername", "");
      if (!resultsHashtable.containsKey("profileuserfullname")) resultsHashtable.put("profileuserfullname", "");
      if (!resultsHashtable.containsKey("profileuseridandname")) resultsHashtable.put("profileuseridandname", "");
      
      
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.objects.ProfilePersonObj.fillInfo: " + e.getExplanation() + ".");
      return null;
    }
    
    return resultsHashtable;
  }
}