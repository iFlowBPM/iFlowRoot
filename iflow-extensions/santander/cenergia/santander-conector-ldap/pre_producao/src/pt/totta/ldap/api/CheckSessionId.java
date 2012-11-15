package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API CheckSessionId.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class CheckSessionId
{
  /**
   * This method will validate a given session id.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param userId the user id.
   * @param sessionId the session id to validate.
   * @return True if sessionId is valid. False otherwise.
   */
  public boolean business(String userId, String sessionId)
  {  
    // Create the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckSessionId.business: userId is invalid.");
      return false;
    }
    
    // Check if sessionId is null or if it´s an empty string.
    if(sessionId == null || sessionId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckSessionId.business: sessionId is invalid.");
      return false;
    }
    
    // Set a dirContext.
    Ldap ldap = new Ldap();
    DirContext dirContext = ldap.setContext();
    if(dirContext == null)
      return false;
    
    // Setup the searchControls.
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningObjFlag(true);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // Setup the search string.
    String searchString = "";
    if(!Character.isDigit(userId.charAt(0)))
      searchString = "loginpublico=" + userId;
    else
      searchString = "numeroempregado=" + userId;
      
    searchString = "(&(objectclass=personbank)(" + searchString + "))";
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = setup.getUserRoot();
    
    // Search on the organizational branch of the ldap tree.
    NamingEnumeration results = null;
    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(results == null)
      return false;
    
    // Process the result(s).
    int resultsFound = 0;
    SearchResult result = null;
    Attributes attributes = null;
    try
    {
      // While there are results to process.
      while(results.hasMore())
      {
        // Count the number of results found.
        resultsFound++;
        
        // Get a result.
        result = (SearchResult) results.next();
        
        // Get attributes for the result.
        attributes = result.getAttributes();
        attributes.put(new BasicAttribute("dn", result.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(attributes == null)
        {
          System.out.println("pt.totta.ldap.api.CheckSessionId.business: result has no attributes.");
          return false;
        }
        
        Person person = new Person();
        resultsHashtable = person.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.CheckSessionId.business: unable to fill person info.");
          return false;
        }
      }
      
      results.close();
      dirContext.close();
      
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.CheckSessionId.business: no results match the search criteria.");
        return false;
      }
      
      // If more than one result found.
      if(resultsFound > 1)
      {
        System.out.println("pt.totta.ldap.api.CheckSessionId.business: primary key violation.");
        return false;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.CheckSessionId.business: " + e.getMessage() + ".");
      return false;
    }
    
    // Compare the local sessionId with the remote sessionId.
    if(!sessionId.equals((String) resultsHashtable.get("sessionidutilizador")))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckSessionId.business: sessionId mismatch.");
      return false;
    }
    
    return true;
  }

  public static void main(String args[])
  {
    // Variables.
    String userId = null;
    String sessionId = null;
    boolean result = false;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("CheckSessionId Test");
    System.out.println("-------------------");
    System.out.println("");
    
    // Get userId.
    Utils utils = new Utils();
    userId = utils.userInput("userId: ");
    
    // Get sessionId.
    sessionId = utils.userInput("sessionId: ");
    
    // CheckSessionId test.
    CheckSessionId checkSessionId = new CheckSessionId();
    result = checkSessionId.business(userId, sessionId);
    
    // Show the test result.
    System.out.println("");
    System.out.println("Test returns: " + result);
  }
}