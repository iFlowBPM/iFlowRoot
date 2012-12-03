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
 * Created on May 20, 2005 by iKnow
 *
  */

package pt.iflow.applicationdata.db;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import pt.iflow.api.applicationdata.ApplicationData;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.applicationdata.ApplicationDataAccess;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class DBApplicationDataAccess implements ApplicationDataAccess {

  private static final String SQL_GET_APPLICATIONS = 
    "select * from applications";
  
  private static final String SQL_GET_APPLICATION = 
    "select * from applications where appid={0}";
 
  private static final String SQL_GET_APPLICATION_PROFILES =
    "select a.NAME as PROFILE from profiles a,applicationprofiles b, applications c where " +
    "a.profileid=b.profileid and b.appid=c.appid and c.name=''{0}'' and a.organizationid=''{1}''";

  private static final String SQL_GET_ALL_PROFILES =
	    "select NAME as PROFILE from profiles where organizationid=''{1}''";

//  private static final String SQL_GET_APPLICATION_PROFILES = 
//    "select a.NAME as PROFILE from profiles a,applicationprofiles b where " +
//    " a.profileid=b.profileid and b.appid={0}";

    private static final String SQL_GET_PROFILE_APPLICATIONS = 
      "select a.name as APPLICATION from applications a,applicationprofiles b,profiles c where " +
      "a.appid=b.appid and b.profileid=c.profileid and c.name=''{0}'' and c.organizationid=''{1}''";
    
  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationDataAccess#getApplication(java.lang.String)
   */
  public ApplicationData getApplication(String appId) {
    DBApplicationData retObj = null;
    
    Collection<Map<String,String>> units = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_APPLICATION,new Object[]{appId}));
    
    if(units.isEmpty()) {
      Logger.error(null,this,"getApplication","No application with id " + appId);
    } else if (units.size() > 1) {
      Logger.error(null,this,"getApplication","More than one application with id " + appId);
    } else {
      Map<String,String> appData = units.iterator().next();
      retObj = new DBApplicationData(appData);
  }
    return retObj;
  }

  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationDataAccess#getApplicationProfiles(java.lang.String)
   */
  public Collection<String> getApplicationProfiles(String appId) {
    Vector<String> retObj = new Vector<String>();
    
    Collection<Map<String,String>> fullProfiles = null;
    
    if(appId == null || appId.trim().length() == 0) {
    	fullProfiles = DatabaseInterface.executeQuery(SQL_GET_ALL_PROFILES);
    } else {
    	fullProfiles = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_APPLICATION_PROFILES,new Object[]{appId}));
    }
    
    Iterator<Map<String,String>> iter = fullProfiles.iterator();
    while(iter.hasNext()) {
      String val = iter.next().get("PROFILE");
      retObj.add(val);
    }
    
    return  retObj;
  }

  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationDataAccess#getProfileApplications(java.lang.String)
   */
  public Collection<String> getProfileApplications(String profileId) {
    Vector<String> retObj = new Vector<String>();
    
    Collection<Map<String,String>> fullProfiles = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_PROFILE_APPLICATIONS,new Object[]{profileId}));
    
    Iterator<Map<String,String>> iter = fullProfiles.iterator();
    while(iter.hasNext()) {
      String val = iter.next().get("APPLICATION");
      retObj.add(val);
    }
    
    return  retObj;
  }

  public Collection<ApplicationData> getApplications() {
    Vector<ApplicationData> retObj = new Vector<ApplicationData>();
    
    Collection<Map<String,String>> fullApplications = 
      DatabaseInterface.executeQuery(SQL_GET_APPLICATIONS);
    
    Iterator<Map<String,String>> iter = fullApplications.iterator();
    while(iter.hasNext()) {
      DBApplicationData val = new DBApplicationData(iter.next());
      retObj.add(val);
    }
    
    return  retObj;
  }

}
