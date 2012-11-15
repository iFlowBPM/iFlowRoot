package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetApplicationProfiles.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetApplicationProfiles
{
  /**
   * This method returns the profile(s) for a given application.
   * <p>
   * This method uses the applicational branch of the LDAP tree.
   * <p>
   * @param applicationName the application name.
   * @return A vector of hashtables. Keys for the hashtable(s) are:
   *  "pf"
   *  "nivelperfil"
   *  "nivel"
   *  "dnvalue"
   */
  public Vector business(String applicationName)
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
    // Check if applicationName is null or if it´s an empty string.
    if(applicationName == null || applicationName.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetApplicationProfiles.business: applicationName is invalid.");
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
    String searchString = "pf=*";
    
    // Setup the main search base.
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
          System.out.println("pt.totta.ldap.api.GetApplicationProfiles.business: result has no attributes.");
          return null;
        }
        
        // Fill ApplicationProfileObj info.
        Hashtable resultsHashtable = new Hashtable();
        ApplicationProfileObj applicationProfileObj = new ApplicationProfileObj();
        resultsHashtable = applicationProfileObj.fillInfo(attributes);
        if(resultsHashtable == null)
        {
         System.out.println("pt.totta.ldap.api.GetApplicationProfiles.business: unable to fill applicationProfileObj info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector.add(resultsHashtable);
      }
      
      results.close();
      dirContext.close();
      
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetApplicationProfiles.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetApplicationProfiles.business: " + e.getMessage() + ".");
      return null;
    }
    
    return resultsVector;
  }

  public static void main(String args[])
  {
    // Variables.
    String applicationName = null;
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetApplicationProfiles Test");
    System.out.println("---------------------------");
    System.out.println("");
    
    // Get applicationName.
    Utils utils = new Utils();
    applicationName = utils.userInput("applicationName: ");
    
    // GetApplicationProfiles test.
    GetApplicationProfiles getApplicationProfiles = new GetApplicationProfiles();
    resultsVector = getApplicationProfiles.business(applicationName);
    
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