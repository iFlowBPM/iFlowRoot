/**
 * 
 */
package pt.iflow.api.core;

import pt.iflow.api.utils.UserInfoInterface;

public abstract class ResourceModifiedListener {
  public abstract void resourceModified(UserInfoInterface userInfo, String resourceName, String fullname);
}