package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetOrganicalUnitLeafs.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetOrganicalUnitLeafs
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
  public Vector business(String organicalUnit)
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
    // Check if organicalUnit is null or if it´s an empty string.
    if(organicalUnit == null || organicalUnit.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: organicalUnit is invalid.");
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
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: result has no attributes.");
          return null;
        }
        
        // Fill OrganizationalUnit info.
        Hashtable resultsHashtable = new Hashtable();
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        resultsHashtable = organizationalUnit.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: unable to fill organizationalUnit info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector.add(resultsHashtable);
      }
      
      results.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: " + e.getMessage() + ".");
      return null;
    }
    
    //return resultsVector;
    // Create a vector to hold the results.
    Vector resultsVector_2 = new Vector();
    
    // Setup the second search string.
    //String searchString_2 = "(&(objectclass=organizationalunitbank)";
    String searchString_2 = new String();
    
    // Cicle first search results.
    for(int ciclo = 0; ciclo < resultsVector.size(); ciclo++)
    {
      // Setup the search base.
      Hashtable ldapOrganicalUnit = new Hashtable();
      ldapOrganicalUnit = (Hashtable) resultsVector.get(ciclo);
      resultsVector_2.add(ldapOrganicalUnit);
      //String searchBase_2 = (String) ldapOrganicalUnit.get("dnvalue");
      String searchBase_2 = setup.getOrganicalUnitRoot();
      searchString_2 = "(&(objectclass=organizationalunitbank)(idpai=" + ldapOrganicalUnit.get("centrocusto") + "))";
      
      // Search on the organizational branch of the ldap tree.
      NamingEnumeration results_2 = null;
      results_2 = ldap.searchDirectory(dirContext, searchControls, searchString_2, searchBase_2);
      //if(results_2 == null)
      //  return null;
      
      // Process the result(s).
      
      /*System.out.println("SB1 --> " + searchBase_2);
      System.out.println("SS1 --> " + searchString_2);*/
      int resultsFound_2 = 1;
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
          attributes_2.put(new BasicAttribute("dn", result_2.getName() + "," + searchBase_2));
          
          // Check if this result has any attributes.
          if(attributes_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: result has no attributes.");
            return null;
          }
          
          // Fill OrganizationalUnit info.
          Hashtable resultsHashtable_2 = new Hashtable();
          OrganizationalUnit organizationalUnit = new OrganizationalUnit();
          resultsHashtable_2 = organizationalUnit.fillInfo(attributes_2);
          if(resultsHashtable_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: unable to fill organizationalUnit info.");
            return null;
          }
          
          // Place the resultsHashtable on the resultsVector.
          resultsVector_2.add(resultsHashtable_2);
          Vector tempVector = business((String)resultsHashtable_2.get("id"));
          for (int j=1; j<tempVector.size(); j++) {
          	resultsVector_2.add(tempVector.elementAt(j));
          }
        }
        
        results_2.close();
        dirContext.close();
        // If no results were found.
        if(resultsFound_2 == 0)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: no results match the search criteria.");
          return null;
        }
      }
      catch(NamingException e)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitLeafs.business: " + e.getMessage() + ".");
        return null;
      }
    
    }
    
    return resultsVector_2;
  }

  public static void main(String args[])
  {
    // Variables.
    String organicalUnit = null;
    String subTreeScope = null;
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetOrganicalUnitLeafs Test");
    System.out.println("--------------------------");
    System.out.println("");
    
    // Get organicalUnit.
    Utils utils = new Utils();
    organicalUnit = utils.userInput("organicalUnit: ");
    
    // GetOrganicalUnitLeafs test.
    GetOrganicalUnitLeafs getOrganicalUnitLeafs = new GetOrganicalUnitLeafs();
    resultsVector = getOrganicalUnitLeafs.business(organicalUnit);
    
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