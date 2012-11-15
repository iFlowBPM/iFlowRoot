package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetOrganicalUnitParent.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetOrganicalUnitParent
{
  /**
   * This method returns the parent organical unit (for a given organical unit).
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param organicalUnit the organicalUnit id.
   * @return An hashtable with the parent organical unit information.<br>
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
    // Create the results hashtables.
    Hashtable resultsHashtable = new Hashtable();
    Hashtable parentResultsHashtable = new Hashtable();
    
    // Check if organicalUnit is null or if it´s an empty string.
    if(organicalUnit == null || organicalUnit.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: organicalUnit is invalid.");
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
    /*else
      searchString = "(&(objectclass=organizationalunitbank)(codigoestrutura=" + organicalUnit + "))";*/
    
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
        
        // Get attributes for the result.
        attributes = result.getAttributes();
        attributes.put(new BasicAttribute("dn", result.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(attributes == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: result has no attributes.");
          return null;
        }
        
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        resultsHashtable = organizationalUnit.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: unable to fill organical unit info.");
          return null;
        }
      }
      
      results.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: " + e.getMessage() + ".");
      return null;
    }
    
    //System.out.println("HASHTABLE --> " + resultsHashtable + " <--");
    // Get the dnValue.
    //String dnValue = (String) resultsHashtable.get("dnvalue");
    String idpai = (String) resultsHashtable.get("idpai");
    
    // Process parentDnValue.
    //String parentDnValue = dnValue.substring(dnValue.indexOf(",") + 1).trim();
    
    // Check if parentDnValue is null or if it´s an empty string.
    /*if(parentDnValue == null || parentDnValue.equals(""))
    {
      System.out.println("LdapQuery:GetOrganicalUnitParent: parentDn is invalid.");
      return null;
    }*/
    
    if(idpai == null || idpai.equals(""))
    {
      System.out.println("LdapQuery:GetOrganicalUnitParent: parentId is invalid.");
      return null;
    }
    
    // Setup the search string.
    searchString = "(&(objectclass=organizationalunitbank)(centrocusto=*" + idpai + "*))";
    //System.out.println("Parent Search --> " + searchString + " <--");
    
    // Search on the organizational branch of the ldap tree.
    NamingEnumeration parentResults = null;
    parentResults = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(parentResults == null)
      return null;
    
    // Process the parent result(s).
    int parentResultsFound = 0;
    SearchResult parentResult = null;
    Attributes parentAttributes = null;
    try
    {
      // While there are parent results to process.
      while(parentResults.hasMore())
      {
        // Count the number of parent results found.
        parentResultsFound++;
        
        // Get a parent.
        parentResult = (SearchResult) parentResults.next();
        
        // Get attributes for the parent result.
        parentAttributes = parentResult.getAttributes();
        parentAttributes.put(new BasicAttribute("dn", parentResult.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(parentAttributes == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: parent result has no attributes.");
          return null;
        }
        
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        parentResultsHashtable = organizationalUnit.fillInfo(parentAttributes);
        if(parentResultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: unable to fill parent organical unit info.");
          return null;
        }
      }
      
      parentResults.close();
      dirContext.close();
	  // If no parent results were found.
      if(parentResultsFound == 0)
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: no parent results match the search criteria.");
      return parentResultsHashtable;
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitParent.business: " + e.getMessage() + ".");
      return null;
    }
  }

  public static void main(String args[])
  {
    // Variables.
    String organicalUnit= null;
    Hashtable resultsHashtable =  new Hashtable();
    int hashtableSize = 0;
    Enumeration elements = null;
    Enumeration keys = null;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetOrganicalUnitParent Test");
    System.out.println("---------------------------");
    System.out.println("");
    
    // Instantiate GetOrganicalUnitParent.
    GetOrganicalUnitParent getOrganicalUnitParent = new GetOrganicalUnitParent();
    
    // Get organizationalUnit.
    Utils utils = new Utils();
    organicalUnit = utils.userInput("organizationalUnit: ");
    
    // GetOrganicalUnitParent test.
    resultsHashtable = getOrganicalUnitParent.business(organicalUnit);
    
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