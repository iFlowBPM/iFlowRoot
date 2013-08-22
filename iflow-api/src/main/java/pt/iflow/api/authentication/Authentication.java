/*
 *
 * Created on May 10, 2005 by iKnow
 *
  */

package pt.iflow.api.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.utils.Const;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface Authentication {

  public boolean checkUser(String username, String password);
  public boolean checkUserSession(String username, String sessionID);
  public AuthenticationInfo loginUser(String username, String password);
  public Collection<Map<String,String>> getProfileUsers(String profileID);
  public List<Map<String, String>> getUsersForSync();
  public void init(Properties parameters);
  public boolean hasMoreUserToProcess();
  public boolean doSyncronizeUsers();
  public String getSyncOrgId();
  public String getSyncUnitId();
  public int getSyncThreadCicle();
  public boolean shouldUpdateUser();
  public boolean shouldUpdateThisColumn(String prop);
 
}
