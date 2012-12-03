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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.utils.Logger;
import pt.iflow.ldap.LDAPInterface;
import pt.iflow.ldap.LDAPMapping;
import pt.iflow.userdata.common.MappedOrganizationData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class ADMultiOrganizationDataAccess extends ADSingleOrganizationDataAccess implements OrganizationDataAccess {

  private String _GET_ORG ="";
  private String _LIST_ORGS = "";
  
  public void init(Properties parameters) {
    super.init(parameters);
    this._GET_ORG = parameters.getProperty("GET_ORG");
    this._LIST_ORGS = parameters.getProperty("LIST_ORGS");
  }
  
  public OrganizationData getOrganization(String orgId) {
    OrganizationData retObj = null;
    
    try {
      if (StringUtils.isNotEmpty(orgId)) {
        
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


}
