package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetUserProfiles.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetUserProfiles
{
  /**
   * This method returns the user profile(s) for a given application.
   * <p>
   * This method uses the applicational branch of the LDAP tree.
   * <p>
   * @param applicationName the application name.
   * @param userId the user id.
   * @return A vector of hashtables. Keys for the hashtable(s) are:
   *  "pf"
   *  "nivelperfil"
   *  "nivel"
   *  "dnvalue"
   */
  public Vector business(String applicationName, String userId)
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
    // Check if applicationName is null or if it´s an empty string.
    if(applicationName == null || applicationName.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetUserProfiles.business: applicationName is invalid.");
      return null;
    }
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetUserProfiles.business: userId is invalid.");
      return null;
    }
    
    // Set a dirContext.
    Ldap ldap = new Ldap();
    DirContext dirContext = ldap.setContext();
    if(dirContext == null)
      return null;
    
    // Setup the searchControls.
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningObjFlag(true);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // Setup the search string.
    String searchString = "profileuserid=" + userId;
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = "a=" + applicationName + "," + setup.getApplicationRoot();
    
    // Search on the applicational branch of the ldap tree.
    NamingEnumeration results = null;
    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(results == null)
      return null;
    
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
          System.out.println("pt.totta.ldap.api.GetUserProfiles.business: result has no attributes.");
          return null;
        }
        
        // Fill ProfilePersonObj info.
        Hashtable resultsHashtable = new Hashtable();
        ProfilePersonObj profilePersonObj = new ProfilePersonObj();
        resultsHashtable = profilePersonObj.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetUserProfiles.business: unable to fill profilePersonObj info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector.add(resultsHashtable);
      }
      
      results.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetUserProfiles.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetUserProfiles.business: " + e.getMessage() + ".");
      return null;
    }
    
    // Create a vector to hold the results.
    Vector resultsVector_2 = new Vector();
    
    // Setup the second search string.
    String searchString_2 = "(|(";
    try
    {
      for(int index = 0; index < resultsVector.size(); index++)
      {
        Hashtable resultsHashtable = new Hashtable();
        resultsHashtable = (Hashtable) resultsVector.get(index);
        String dnValue = (String) resultsHashtable.get("dn");
        int loLimit = dnValue.indexOf("pf=");
        int hiLimit = dnValue.indexOf(",", loLimit);
        String profile = dnValue.substring(loLimit + 3, hiLimit).trim();
        searchString_2 = searchString_2 + "(pf=" + profile + ")";
      }
      searchString_2 = searchString_2 + "))";
    }
    catch(Exception e)
    {
      System.out.println("pt.totta.ldap.api.GetUserProfiles.business: unable to setup the second search string.");
      return null;
    }
    
    // Search on the applicational branch of the ldap tree.
    NamingEnumeration results_2 = null;
    results_2 = ldap.searchDirectory(dirContext, searchControls, searchString_2, searchBase);
    if(results_2 == null)
      return null;
    
    // Process the result(s).
    int resultsFound_2 = 0;
    SearchResult result_2 = null;
    Attributes attributes_2 = null;
    try
    {
      // While there are results to process.
      while(results_2.hasMore())
      {
        // Count the number of results found.
        resultsFound_2++;
        
        // Get a result.
        result_2 = (SearchResult) results_2.next();
        
         // Get attributes for the result.
        attributes_2 = result_2.getAttributes();
        attributes_2.put(new BasicAttribute("dn", result_2.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(attributes_2 == null)
        {
          System.out.println("pt.totta.ldap.api.GetUserProfiles.business: result has no attributes.");
          return null;
        }
        
        // Fill ApplicationProfileObj info.
        Hashtable resultsHashtable_2 = new Hashtable();
        ApplicationProfileObj applicationProfileObj = new ApplicationProfileObj();
        resultsHashtable_2 = applicationProfileObj.fillInfo(attributes_2);
        if(resultsHashtable_2 == null)
        {
          System.out.println("pt.totta.ldap.api.GetUserProfiles.business: unable to fill applicationProfileObj info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector_2.add(resultsHashtable_2);
      }
      
      results_2.close();
      dirContext.close();
      // If no results were found.
      if(resultsFound_2 == 0)
      {
        System.out.println("pt.totta.ldap.api.GetUserProfiles.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetUserProfiles.business: " + e.getMessage() + ".");
      return null;
    }
    
    return resultsVector_2;
  }

  public static void main(String args[])
  {
    // Variables.
    String applicationName = null;
    String userId = null;
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetUserProfiles Test");
    System.out.println("--------------------");
    System.out.println("");
    
    // Get applicationName and userId.
    Utils utils = new Utils();
    applicationName = utils.userInput("applicationName: ");
    userId = utils.userInput("userId: ");
    
    // GetUserProfiles test.
    GetUserProfiles getUserProfiles = new GetUserProfiles();
    resultsVector = getUserProfiles.business(applicationName, userId);
    
    // Show the test results.
    System.out.println("");
    System.out.println("Test returns:");
    System.out.println("");
    if(resultsVector == null)
      System.out.println("Invalid search.");
    else
    {
      vectorSize = resultsVector.size();
      if(vectorSize == 0)
        System.out.println("Empty vector.");
      else
        System.out.println(resultsVector);
    }
  }
}