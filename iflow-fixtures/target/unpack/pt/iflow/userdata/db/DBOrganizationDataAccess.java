/*
 *
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.userdata.db;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
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
@SuppressWarnings("unchecked")
public class DBOrganizationDataAccess implements OrganizationDataAccess {
  
  private Map<String,String> _mapping;
  
  private static final String SQL_GET_ORGS = "select ORGANIZATIONID,NAME,DESCRIPTION from organizations";
  
  private static final String SQL_GET_ORG = "select ORGANIZATIONID,NAME,DESCRIPTION from organizations " +
  "where ORGANIZATIONID={0}";

  private static final String SQL_GET_ORG_UNIT = 
    "select a.unitid,a.ORGANIZATIONID,a.NAME,a.DESCRIPTION,b.NAME as ORG_NAME,c.USERNAME as MANAGER_USERNAME from " + 
    "organizational_units a, organizations b, users c, unitmanagers d where " +
    "a.ORGANIZATIONID=b.ORGANIZATIONID and c.userid=d.userid and d.unitid=a.unitid and a.unitid={0}";
 
  private static final String SQL_GET_ORG_UNIT_PARENT = 
    "select a.unitid,a.ORGANIZATIONID,a.NAME,a.DESCRIPTION,b.NAME as ORG_NAME,c.USERNAME as MANAGER_USERNAME from " +
    "organizational_units a, organizations b, users c,organizational_units z, unitmanagers d where " + 
    "a.ORGANIZATIONID=b.ORGANIZATIONID and c.userid=d.userid and d.unitid=a.unitid and a.unitid=z.parent_id and z.unitid={0}";
  
  private static final String SQL_GET_ORG_ID_FROM_UNIT = "select ORGANIZATIONID from organizational_units where UNITID={0}";
  
  /* (non-Javadoc)
   * @see pt.iknow.userdata.OrganizationDataAccess#getOrganizationalUnit(java.lang.String)
   */
  public OrganizationalUnitData getOrganizationalUnit(String unitId) {
    MappedOrganizationalUnitData retObj = null;
    
    Collection units = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ORG_UNIT,new Object[]{unitId}));
    
    if(units.isEmpty()) {
      Logger.error(null,this,"getOrganizationalUnit","No unit with id " + unitId);
    } else if (units.size() > 1) {
      Logger.error(null,this,"getOrganizationalUnit","More than one unit with id " + unitId);
    } else {
      Hashtable unitData = (Hashtable)units.iterator().next();
      retObj = new MappedOrganizationalUnitData(unitData,this._mapping);
      Logger.trace(this,"getOrganizationalUnit","Got UNIT DATA : " + retObj.toString());
  }
    return retObj;
  }
  
  public OrganizationalUnitData getOrganizationalUnitParent(String unitId) {
    MappedOrganizationalUnitData retObj = null;
    
    Collection units = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ORG_UNIT_PARENT,new Object[]{unitId}));
    
    if(units.isEmpty()) {
      Logger.error(null,this,"getOrganizationalUnitParent","No parent for unit with id " + unitId);
    } else if (units.size() > 1) {
      Logger.error(null,this,"getOrganizationalUnitParent","More than one parent for unit with id " + unitId);
    } else {
      Hashtable unitData = (Hashtable)units.iterator().next();
      retObj = new MappedOrganizationalUnitData(unitData,this._mapping);
      Logger.trace(this,"getOrganizationalUnitParent","Got UNIT DATA : " + retObj.toString());
  }
    return retObj;
  }

  public OrganizationData getOrganization(String orgId) {
    MappedOrganizationData retObj = null;
    
    Collection orgs = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ORG,new Object[]{orgId}));
    
    if(orgs.isEmpty()) {
      Logger.error(null,this,"getOrganization","No organization with id " + orgId);
    } else if (orgs.size() > 1) {
      Logger.error(null,this,"getOrganization","More than one organization with id " + orgId);
    } else {
      Hashtable orgData = (Hashtable)orgs.iterator().next();
      retObj = new MappedOrganizationData(orgData,this._mapping);
      Logger.trace(this,"getOrganization","Got ORG DATA : " + retObj.toString());
  }
    return retObj;
  }

  public void init(Properties params) {
    this._mapping = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(params));
  }

  public OrganizationData[] getOrganizations() {
    MappedOrganizationData[] retObj = null;
   
    Collection orgs = DatabaseInterface.executeQuery(SQL_GET_ORGS);
    
    Object[] oa = orgs.toArray();
    
    retObj = new MappedOrganizationData[oa.length];
    
    for (int i=0; i < oa.length; i++) {
      Hashtable orgData = (Hashtable)oa[i];
      retObj[i] = new MappedOrganizationData(orgData,this._mapping);
    }

    Logger.trace(this,"getOrganizations","Got  " + retObj.length + " organizations");
   
    return retObj;
  }

  public boolean canModifyOrganization() {
    return true;
  }

  public OrganizationData getUnitOrganization(String unitId) {
    OrganizationData retObj = null;
    
    Collection<Map<String,String>> units = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ORG_ID_FROM_UNIT,new Object[]{unitId}));
    if(units.isEmpty()) {
      Logger.error(null,this,"getUnitOrganization","No organization with unit id " + unitId);
      return null;
    } else if (units.size() > 1) {
      Logger.error(null,this,"getUnitOrganization","More than one organization with unit id " + unitId);
      return null;
    } else {
      String orgId = units.iterator().next().get("ORGANIZATIONID");

      retObj = getOrganization(orgId);
    }
    return retObj;
  }

}
