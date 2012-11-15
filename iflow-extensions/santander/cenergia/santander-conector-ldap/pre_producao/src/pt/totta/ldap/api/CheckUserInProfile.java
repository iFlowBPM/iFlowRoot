package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API CheckUserInProfile.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class CheckUserInProfile
{
  /**
   * This method checks if a user belongs to a profile (for a given application).
   * <p>
   * This method uses the applicational branch of the LDAP tree.
   * <p>
   * @param applicationName the application name.
   * @param profile the profile.
   * @param userId the user id.
   * @return True if the user belongs to the profile. False otherwise.
   */
  public boolean business(String applicationName, String profile, String userId)
  {
    // Create the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // Check if applicationName is null or if it´s an empty string.
    if(applicationName == null || applicationName.equals(""))
    {
      System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: applicationName is invalid.");
      return false;
    }
    
    // Check if profile is null or if it´s an empty string.
    if(profile == null || profile.equals(""))
    {
      System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: profile is invalid.");
      return false;
    }
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: userId is invalid.");
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
    String searchString = "profileuserid=" + userId;
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = "pf=" + profile + ",a=" + applicationName + "," + setup.getApplicationRoot();
    
    // Search on the applicational branch of the ldap tree.
    NamingEnumeration results = null;
    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(results == null)
      return false;
    
    // Process the result.
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
          System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: result has no attributes.");
          return false;
        }
        
        ProfilePersonObj profilePersonObj = new ProfilePersonObj();
        resultsHashtable = profilePersonObj.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: unable to fill profilepersonobj info.");
          return false;
        }
      }
      
      results.close();
      dirContext.close();
      
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: no results match the search criteria.");
        return false;
      }
      
      // If more than one result found.
      if(resultsFound > 1)
      {
        System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: primary key violation.");
        return false;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.CheckUserInProfile.business: " + e.getMessage() + ".");
      return false;
    }
    
    return true;
  }

  public static void main(String args[])
  {
    // Variables.
    String applicationName = null;
    String profile = null;
    String userId = null;
    boolean checkUserInProfileResult = false;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("CheckUserInProfile Test");
    System.out.println("-----------------------");
    System.out.println("");
    
    // Get applicationName.
    Utils utils = new Utils();
    applicationName = utils.userInput("applicationName: ");
    
    // Get Profile.
    profile = utils.userInput("profile: ");
    
    // Get UserId.
    userId = utils.userInput("userId: ");
    
    // CheckUserInProfile test.
    CheckUserInProfile checkUserInProfile = new CheckUserInProfile();
    checkUserInProfileResult = checkUserInProfile.business(applicationName,profile,userId);
    
    // Show the test result.
    System.out.println("");
    System.out.println("Test returns: " + checkUserInProfileResult);
  }
}