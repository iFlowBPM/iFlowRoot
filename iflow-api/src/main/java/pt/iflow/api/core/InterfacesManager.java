/**
 * 
 */
package pt.iflow.api.core;

import java.util.ArrayList;
import java.util.List;

import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.utils.InterfaceInfo;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * @author helder
 *
 */
public interface InterfacesManager{
  
  public InterfaceInfo[] getAllInterfaces();
  public String[] getProfilesForInterface(UserInfoInterface userInfo, String idInterface);

  public boolean addProfileToInterface(UserInfoInterface userInfo, String interfaceId, String profileId);
  public boolean removeProfileFromInterface(UserInfoInterface userInfo, String interfaceId, String profileId);
  public boolean isInterfaceDisabledByDefault(UserInfoInterface userInfo,String idInterface);
  
  
  public int[] tabsRejeitadas(UserInfoInterface userInfo, String perfil);
}
