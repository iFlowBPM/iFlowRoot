package pt.totta.ldap.api;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import pt.totta.ldap.lowlevel.Ldap;
import pt.totta.ldap.objects.Person;
import pt.totta.ldap.setup.Setup;
import pt.totta.ldap.utils.Utils;

/**
 * This class implements the Ldap API CheckUserPassword.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class CheckUserPassword
{
  /**
   * This method will validate a given user password.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param userId the user id.
   * @param userPassword the user password to validate.
   * @return True if userPassword is valid. False otherwise.
   */
  public boolean business(String userId, String userPassword)
  {  
    // Create the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: userId is invalid.");
      return false;
    }
    
    // Check if userPassword is null or if it´s an empty string.
    if(userPassword == null || userPassword.equals(""))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: userPassword is invalid.");
      return false;
    }
    
    // Set a dirContext.
    Ldap ldap = new Ldap();
    DirContext dirContext = ldap.setContext();
    if(dirContext == null) {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: cannot access LDAP Context.");
      return false;
    }
    
    // Setup the searchControls.
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningObjFlag(true);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // Setup the search string.
    String searchString = "";
    if(Character.isDigit(userId.charAt(0)))
      searchString = "numeroempregado=" + userId;
    else
      searchString = "loginpublico=" + userId;
      
    searchString = "(&(objectclass=personbank)(" + searchString + "))";
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = setup.getUserRoot();
    
    // Search on the organizational branch of the ldap tree.
    NamingEnumeration results = null;
    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(results == null) {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: user not found.");
      return false;
    }
    
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
        
        Person person = new Person();
        resultsHashtable = person.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.CheckUserPassword.business: unable to fill person info.");
          return false;
        }
      }
      
      results.close();
      dirContext.close();
      
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.CheckUserPassword.business: no results match the search criteria.");
        return false;
      }
      
      // If more than one result found.
      if(resultsFound > 1)
      {
        System.out.println("pt.totta.ldap.api.CheckUserPassword.business: primary key violation.");
        return false;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.CheckUserPassword.business: " + e.getMessage() + ".");
      return false;
    }
    
    Hashtable dirContextVars = new Hashtable();
    System.out.println("DN --> " + resultsHashtable.get("dn"));
    dirContextVars.put(Context.PROVIDER_URL, setup.getProviderUrl());
    dirContextVars.put(Context.SECURITY_PRINCIPAL, resultsHashtable.get("dn"));
    dirContextVars.put(Context.SECURITY_AUTHENTICATION, "simple");
    dirContextVars.put(Context.SECURITY_CREDENTIALS, userPassword);
    //System.out.println("CONTEXT --> " + dirContextVars);
    
    Ldap ldapLogin = new Ldap();
    DirContext dirContextLogin = ldapLogin.setContext(dirContextVars);
    if(dirContextLogin == null) {
    	System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: userPassword mismatch.");
      	//return false;
    } else {
    	System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: userPassword correct.");
    	return true;
    }
    
    // Encript the user password.
    Utils utils = new Utils();
    userPassword = utils.encrypt(userPassword);
    if(userPassword == null)
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: encrypt error.");
      return false;
    }
    
    // Compare the encrypted userPassword with the remote encrypted userPassword.
    if(!userPassword.equals( resultsHashtable.get("loginprivado")))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: loginPrivado mismatch.");
      return false;
    } else {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: loginPrivado correct.");
    }
    
    return true;
  }

  public static void main(String args[])
  {
    // Variables.
    String userId = null;
    String userPassword = null;
    boolean result = false;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("CheckUserPassword Test");
    System.out.println("----------------------");
    System.out.println("");
    
    // Get userId.
    Utils utils = new Utils();
    userId = utils.userInput("userId: ");
    
    // Get userPassword.
    userPassword = utils.userInput("userPassword: ");
    
    // CheckSessionId test.
    CheckUserPassword checkUserPassword = new CheckUserPassword();
    result = checkUserPassword.business(userId, userPassword);
    
    // Show the test result.
    System.out.println("");
    System.out.println("Test returns: " + result);
  }
}