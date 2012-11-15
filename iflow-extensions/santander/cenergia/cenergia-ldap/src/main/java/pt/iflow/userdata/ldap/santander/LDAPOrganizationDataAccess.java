package pt.iflow.userdata.ldap.santander;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.common.properties.santander.Constants;
import pt.iflow.userdata.common.santander.PropOrganizationData;
import pt.iflow.userdata.common.santander.MappedOrganizationalUnitData;
import pt.totta.ldap.api.GetOrganicalUnitInfo;
import pt.totta.ldap.api.GetOrganicalUnitParent;

public class LDAPOrganizationDataAccess implements OrganizationDataAccess {

  private Map<String,String> _mapping = null;
  

  public void init(Properties parameters) {
    this._mapping = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(parameters));
  }

  public OrganizationalUnitData getOrganizationalUnit(String unitId) {
    OrganizationalUnitData retObj = null;

    try {
      if (unitId != null && !unitId.equals("") ) {
        GetOrganicalUnitInfo goui = new GetOrganicalUnitInfo();
        Map<String,String> unit = goui.business(unitId);

        if (unit==null || unit.isEmpty() || unit.size() == 0) {
          Logger.debug(null, this, "getOrganizationalUnit", "EMPTY ORGANIZATION LIST");
          retObj = null;
        }
        else {
          unit.put(Constants.getMap_OrganizationId(), Constants.getOrganizationId());
          retObj = new MappedOrganizationalUnitData(unit, this._mapping);
        }
      }
    }
    catch (Exception e) {
      Logger.error("", this, "getOrganizationalUnit", e.getMessage());
    }

    return retObj;
  }

  public OrganizationalUnitData getOrganizationalUnitParent(String unitId) {
    OrganizationalUnitData retObj = null;

    try {
      if (unitId != null && !unitId.equals("") ) {
        GetOrganicalUnitParent goup = new GetOrganicalUnitParent();
        Map<String,String> unit = goup.business(unitId);

        if (unit==null || unit.isEmpty() || unit.size() == 0) {
          Logger.debug(null,this,"checkUser","EMPTY ORGANIZATION LIST");
          retObj = null;
        }
        else {
          unit.put(Constants.getMap_OrganizationId(), Constants.getOrganizationId());
          retObj = new MappedOrganizationalUnitData(unit, this._mapping);
        }
      }
    }
    catch (Exception e) {
      Logger.error("###", this, "getOrganizationalUnitParent", e.getMessage());
    }

    return retObj;
  }

  public OrganizationData getOrganization(String orgId) {
    OrganizationData retObj = null;

    retObj = new PropOrganizationData();

    return retObj;
  }

  public OrganizationData[] getOrganizations() {
    OrganizationData[] retObj = new PropOrganizationData[1];

    retObj[0] = new PropOrganizationData();

    return retObj;
  }


  public boolean canModifyOrganization() {
    return false;
  }

  public OrganizationData getUnitOrganization(String unitId) {
    OrganizationData retObj = null;

    retObj = new PropOrganizationData();

    return retObj;
  }


}
