/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
/*
 *
 * Created on May 31, 2005 by iKnow
 *
  */

package pt.iflow.userdata.ad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.apache.commons.lang.StringUtils;

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

public class ADSingleOrganizationDataAccess implements OrganizationDataAccess {

  protected String _GET_UNIT = "";
  protected String _GET_UNIT_PARENT ="";
  protected String _ORG_ID ="";
  protected String _ORG_NAME = "";
  protected String _ORG_DESC = "";
  
  protected Map<String,String> _mapping = null;
  protected List<LDAPMapping>   _postProc = null;
  protected LdapName baseName = null;

  public void init(Properties parameters) {
    LDAPInterface.init(parameters);
    
    this._GET_UNIT = parameters.getProperty("GET_UNIT");
    this._GET_UNIT_PARENT = parameters.getProperty("GET_UNIT_PARENT");
    this._ORG_ID = parameters.getProperty("ORGID");
    this._ORG_NAME = parameters.getProperty("ORGNAME");
    this._ORG_DESC = parameters.getProperty("ORGDESCRIPTION");

    try {
      baseName = new LdapName(LDAPInterface.getBaseDN());
    } catch (Throwable t) {
      Logger.error(null, this, "init", "Could not parse Base Search DN", t);
    }
    
    Properties mappingFile = Setup.readPropertiesFile(parameters.getProperty("ORG_MAPPING_FILE"));
    Map<String,String> map = new Hashtable<String,String>();
    List<LDAPMapping> postProcs = new ArrayList<LDAPMapping>();

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
      if (StringUtils.isNotEmpty(unitId)) {
        Map<String,String> unit = LDAPInterface.getByDN(unitId);
        
        if (null == unit) {
          Logger.debug(null,this,"getOrganizationalUnit","EMPTY ORGANIZATION UNIT");
          retObj = null;
        }
        else {
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
      if (StringUtils.isNotEmpty(unitId)) {
        String parentDN = unitId;
        // build parent DN without base DN
        Name name = new LdapName(unitId);
        if(name.size()>0) { 
          // remove first
          name = name.getPrefix(name.size()-1);  // 0 based size 
          parentDN = name.toString();
        }


        Map<String,String> unit = LDAPInterface.getByDN(parentDN);

        for (LDAPMapping map : _postProc)
          map.updateAttributes(unit);
        retObj = new MappedOrganizationalUnitData(unit,this._mapping);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public OrganizationData getOrganization(String orgId) {
    OrganizationData retObj = null;
    
    if(null == orgId || !orgId.equals(_ORG_ID)) return null;
    
    Map<String,String> org = new HashMap<String, String>();
    org.put(OrganizationData.ORGANIZATIONID, _ORG_ID);
    org.put(OrganizationData.NAME, _ORG_NAME);
    org.put(OrganizationData.DESCRIPTION, _ORG_DESC);
    
    retObj = new MappedOrganizationData(org, null);
    
    return retObj;
  }

  public OrganizationData[] getOrganizations() {
    OrganizationData [] retObj = new OrganizationData[]{getOrganization(_ORG_ID)};
    return retObj;
  }


  public boolean canModifyOrganization() {
    return false;
  }


  public static void main(String [] args) {
    // define just in case
    System.setProperty("iflow.home", "/iKnow/work/projects/iFlowRoot/iFlowHome");
    Setup.loadProperties();
    new ADSingleOrganizationDataAccess().init(Setup.readPropertiesFile("ad.properties"));
  }

  public OrganizationData getUnitOrganization(String unitId) {
    return getOrganization(_ORG_ID);
  }

}
