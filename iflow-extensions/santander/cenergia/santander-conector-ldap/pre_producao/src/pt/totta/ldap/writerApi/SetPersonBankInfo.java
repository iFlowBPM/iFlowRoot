/**
 * @(#)SetPersonBankInfo.java Oct 11, 2007
 *
 * Created by Infosistema, Sistemas de Informação S. A.
 * Av. José Gomes Ferreira, 11, Sala 42, Miraflores, 1495-139 Algés
 *
 * To ISBAN
 */
package pt.totta.ldap.writerApi;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pt.totta.ldap.api.CheckUserPassword;
import pt.totta.ldap.api.GetUserInfo;
import pt.totta.ldap.lowlevel.Ldap;
import pt.totta.ldap.objects.Person;
import pt.totta.ldap.setup.Setup;
import pt.totta.ldap.utils.Utils;

/**
 * @author Luis Sardinha - Infosistema
 *
 */
public class SetPersonBankInfo {

    /** GENERAL_ERROR */
    private static final String GENERAL_ERROR = "Error processing request. See log for details!";
    private static final String INVALID_ATTRIBUTE = "Invalid Attribute";
    public static final String SEARCH_USER_LDAP = "uid=";
    
    /** The Class log attribute */
    private static Logger log;
    /*
     * This reads the log4j config file
     */
    static {
        PropertyConfigurator.configure(Setup.getProperties("pt.totta.ldap.writerApi.log4j"));

        log = Logger.getLogger(SetPersonBankInfo.class);
    }

    public SetPersonBankInfo () {
        
    }
    /**
     * @param remedyNumber
     * @param userName
     * @param userPass
     * @param attributeList
     * @return
     */
    public String business(String remedyNumber, String userName,
            String userPass, String targetUser, String attributeName, String attributeValue) {
        String ret = null;

        log.info("New request received:");
        log.info("RemedyNumber: " + remedyNumber + " User name: " + userName);

        try {
            CheckUserPassword checkUserPassword = new CheckUserPassword();
            boolean passwdOK = checkUserPassword.business(userName, userPass);

            if (passwdOK == true) {
                ret = changeUserAttributes(targetUser, attributeName, attributeValue);
            } else {
                ret = "Request with invalid password.";
            }
            
            if(ret != null) {
                log.warn(ret);
            }
        } catch (Exception e) {
            log.error("Error in request", e);
            ret = GENERAL_ERROR;
        }

        return ret;
    }

    /**
     * @param targetUser
     * @param attributes
     * @throws NamingException 
     */
    private String changeUserAttributes(String targetUser, String attributeName, String attributeValuie) throws NamingException {
        String ret = null;
        
        /* verify if all attributes are valid
         * The method fillInfo2 only returns the correct attributes
         * for each attribute we verify its existence in the 
         * hashtable personInfo
         */
        Attributes attributes = new BasicAttributes();
        attributes.put(new BasicAttribute(attributeName, attributeValuie));
        
        
        Hashtable personInfo = new Person().fillInfo2(attributes);
        
        NamingEnumeration attributesEnumeration;
        for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
        {
          Attribute attribute = (Attribute) attributesEnumeration.next();
          
          if(!personInfo.containsKey(attribute.getID())) {
              return INVALID_ATTRIBUTE + ": " + attribute.getID();
          }
        }
        Hashtable userInfo = new GetUserInfo().business(targetUser);

        // Logging the operation
        for(attributesEnumeration = attributes.getAll(); attributesEnumeration.hasMore();)
        {
          Attribute attribute = (Attribute) attributesEnumeration.next();
          
          log.info("[" + userInfo.get("dn") + "] " + attribute.getID() + ": " 
                  + userInfo.get(attribute.getID()) + " -> " + attribute.get());

        }
        
        // All attributes are valid, lets store it in the LDAP
        Ldap ldap = new Ldap();
        DirContext dirContext = ldap.setContext(new WriterSetup());
        dirContext.modifyAttributes((String)userInfo.get("dn"), DirContext.REPLACE_ATTRIBUTE, attributes);
        
        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Variables.
        String remedyNumber = null;
        String userName = null;
        String userPass = null;
        String targetUser = null;
        
        // Small initial message saying we are starting the test.
        System.out.println("");
        System.out.println("SetPersoBankInfo Test");
        System.out.println("----------------------------");
        System.out.println("");
        
        // Get organicalUnit and subTreeScope.
        Utils utils = new Utils();
        remedyNumber = utils.userInput("remedyNumber: ");
        userName = utils.userInput("User name: ");
        userPass = utils.userInput("User pass: ");
        targetUser = utils.userInput("Target User: ");
        
        String name;
        do {
            name = utils.userInput("Attribute name (press ENTER to finish): ");
            
            if (!"".equals(name)) {
                String value = utils.userInput("New Value: ");

                SetPersonBankInfo setPersonBankInfo = new SetPersonBankInfo();
                String result = setPersonBankInfo.business(remedyNumber, userName, userPass, targetUser, name, value);
                
                // Show the test results.
                System.out.println("");
                System.out.println("Test returns: " + result);
                System.out.println("");
            }
            
        } while (!"".equals(name)); 
        
      }

}

