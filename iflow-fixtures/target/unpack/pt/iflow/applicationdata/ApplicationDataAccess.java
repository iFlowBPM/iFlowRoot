/*
 *
 * Created on May 20, 2005 by iKnow
 *
  */

package pt.iflow.applicationdata;

import java.util.Collection;

import pt.iflow.api.applicationdata.ApplicationData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface ApplicationDataAccess {

  public ApplicationData getApplication(String appId);
  public Collection<String> getApplicationProfiles(String appId);
  public Collection<String> getProfileApplications(String profileId);
  public Collection<ApplicationData> getApplications();
}
