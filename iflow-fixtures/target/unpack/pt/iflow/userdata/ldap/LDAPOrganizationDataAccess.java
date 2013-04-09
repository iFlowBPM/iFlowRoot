/*
 *
 * Created on May 31, 2005 by iKnow
 *
 */

package pt.iflow.userdata.ldap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.ldap.LdapName;

import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.ldap.LDAPInterface;
import pt.iflow.ldap.LDAPMapping;
import pt.iflow.userdata.common.MappedOrganizationData;
import pt.iflow.userdata.common.MappedOrganizationalUnitData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class LDAPOrganizationDataAccess implements OrganizationDataAccess {

  private String _GET_UNIT = "";
  private String _GET_UNIT_PARENT ="";
  private String _GET_ORG ="";
  private String _LIST_ORGS = "";

  private Map<String,String> _mapping = null;
  private List<LDAPMapping>   _postProc = null;
  
  @SuppressWarnings("unused")
  private LdapName baseName = null;


  public void init(Properties parameters) {
    LDAPInterface.init(parameters);

    this._GET_UNIT = parameters.getProperty("GET_UNIT");
    this._GET_UNIT_PARENT = parameters.getProperty("GET_UNIT_PARENT");
    this._GET_ORG = parameters.getProperty("GET_ORG");
    this._LIST_ORGS = parameters.getProperty("LIST_ORGS");

    try {
      baseName = new LdapName(LDAPInterface.getBaseDN());
    } catch (Throwable t) {
      Logger.error(null, this, "init", "Could not parse Base Search DN", t);
    }
    
    Properties mappingFile = Setup.readPropertiesFile(parameters.getProperty("ORG_MAPPING_FILE"));
    Map<String,String> map = new Hashtable<String,String>();
    List<LDAPMapping> postProcs = new ArrayList<LDAPMapping>(mappingFile.size());
    for(Object k : mappingFile.keySet()) {
      String key = (String) k;
      String val = mappingFile.getProperty(key);
      LDAPMapping m = LDAPMapping.getMapping(key, val);
      m.setupMapping(parameters, map);
      postProcs.add(m);
    }
    
    this._mapping = Collections.unmodifiableMap(map);
    this._postProc = Collections.unmodifiableList(postProcs);
  }

  public OrganizationalUnitData getOrganizationalUnit(String unitId) {
    OrganizationalUnitData retObj = null;

    try {
      if (unitId != null && !unitId.equals("") ) {

        String search = MessageFormat.format(this._GET_UNIT,new Object[] {unitId});
        Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
        Collection<Map<String,String>> units = LDAPInterface.searchDeep(search);
        if (units.isEmpty() || units.size() == 0 || units.size() > 1) {
          Logger.debug(null,this,"checkUser","EMPTY ORGANIZATION LIST");
          retObj = null;
        }
        else {
          Map<String,String> unit = units.iterator().next();
          for (LDAPMapping map : _postProc)
            map.updateAttributes(unit);
          retObj = new MappedOrganizationalUnitData(unit,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public OrganizationalUnitData getOrganizationalUnitParent(String unitId) {
    OrganizationalUnitData retObj = null;

    try {
      if (unitId != null && !unitId.equals("") ) {

        String search = MessageFormat.format(this._GET_UNIT_PARENT,new Object[] {unitId});
        Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
        Collection<Map<String,String>> units = LDAPInterface.searchDeep(search);
        if (units.isEmpty() || units.size() == 0 || units.size() > 1) {
          Logger.debug(null,this,"checkUser","EMPTY ORGANIZATION LIST");
          retObj = null;
        }
        else {
          Map<String,String> unit = units.iterator().next();
          retObj = new MappedOrganizationalUnitData(unit,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public OrganizationData getOrganization(String orgId) {
    OrganizationData retObj = null;

    try {
      if (orgId != null && !orgId.equals("") ) {

        String search = MessageFormat.format(this._GET_ORG,new Object[] {orgId});
        Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
        Collection<Map<String,String>> orgs = LDAPInterface.searchDeep(search);
        if (orgs.isEmpty() || orgs.size() == 0 || orgs.size() > 1) {
          Logger.debug(null,this,"checkUser","EMPTY ORGANIZATION LIST");
          retObj = null;
        }
        else {
          Map<String,String> org = orgs.iterator().next();
          for (LDAPMapping map : _postProc)
            map.updateAttributes(org);
          retObj = new MappedOrganizationData(org,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public OrganizationData[] getOrganizations() {
    OrganizationData [] retObj = null;

    try {
      String search = this._LIST_ORGS;
      Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
      Collection<Map<String,String>> orgs = LDAPInterface.searchDeep(search);
      if (orgs.isEmpty() || orgs.size() == 0) {
        Logger.debug(null,this,"checkUser","EMPTY ORGANIZATION LIST");
        retObj = new OrganizationData[0];
      }
      else {
        retObj = new OrganizationData[orgs.size()];
        Iterator<Map<String,String>> iter = orgs.iterator();
        for(int i = 0; i < retObj.length; i++) {
          Map<String,String> org = iter.next();
          for (LDAPMapping map : _postProc)
            map.updateAttributes(org);
          retObj[i] = new MappedOrganizationData(org,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }


  public boolean canModifyOrganization() {
    return false;
  }

  public OrganizationData getUnitOrganization(String unitId) {
    // TODO Auto-generated method stub
    return null;
  }


}
