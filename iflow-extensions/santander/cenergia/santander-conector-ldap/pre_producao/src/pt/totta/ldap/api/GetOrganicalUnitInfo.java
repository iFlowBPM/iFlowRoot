package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetOrganicalUnitInfo.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetOrganicalUnitInfo
{
  /**
   * This method returns information about a given organical unit.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param organicalUnit the organicalUnit id.
   * @return An hashtable with the organical unit information.<br>
   * Keys for the hashtable are:
   *  "codigoestrutura"
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
   *  "loginpublicoresponsavel"
   *  "caixaeventual"
   *  "dnvalue"
   */
  public Hashtable business(String organicalUnit)
  {
    // Create the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // Check if organicalUnit is null or if it´s an empty string.
    if(organicalUnit == null || organicalUnit.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: organicalUnit is invalid.");
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
    String searchString = "";
    if(organicalUnit.length() == 4)
      searchString = "(&(objectclass=organizationalunitbank)(centrocusto=*" + organicalUnit + "*))";
      //searchString = "(&(objectclass=top)(centrocusto=*" + organicalUnit + "*))";
      //searchString = "(centrocusto=*" + organicalUnit + "*)";
    //else
      //searchString = "(&(objectclass=organizationalunitbank)(codigoestrutura=" + organicalUnit + "))";
      
    System.out.println("search string --> " + searchString);
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = setup.getOrganicalUnitRoot();
    
    // Search on the organizational branch of the ldap tree.
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
        
        //System.out.println("Name --> " + result.getName());
        
        // Get attributes for the result.
        attributes = result.getAttributes();
        attributes.put(new BasicAttribute("dn", result.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(attributes == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: result has no attributes.");
          return null;
        }
        
        // Fill OrganizationalUnit info.
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        resultsHashtable = organizationalUnit.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: unable to fill organical unit info.");
          return null;
        }
      }
      
      results.close();
      dirContext.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: " + e.getMessage() + ".");
      return null;
    }
    
    return resultsHashtable;
  }

  public static void main(String args[])
  {
    // Variables.
    String organicalUnit = null;
    Hashtable resultsHashtable =  new Hashtable();
    int hashtableSize = 0;
    Enumeration elements = null;
    Enumeration keys = null;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetOrganicalUnitInfo Test");
    System.out.println("-------------------------");
    System.out.println("");
    
    // Get organicalUnit.
    Utils utils = new Utils();
    organicalUnit = utils.userInput("organicalUnit: ");
    
    // GetOrganicalUnitInfo test.
    GetOrganicalUnitInfo getOrganicalUnitInfo = new GetOrganicalUnitInfo();
    resultsHashtable = getOrganicalUnitInfo.business(organicalUnit);
    
    // Show the test results.
    System.out.println("");
    System.out.println("Test returns:");
    System.out.println("");
    if(resultsHashtable == null)
      System.out.println("Invalid search.");
    else
    {
      hashtableSize = resultsHashtable.size();
      if(hashtableSize== 0)
        System.out.println("Empty hashtable.");
      else
        System.out.println(resultsHashtable);
    }
  }
}