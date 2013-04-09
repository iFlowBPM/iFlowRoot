/*
 *
 * Created on Oct 12, 2005 by mach
 *
  */

package pt.iflow.api.presentation;

import java.io.Serializable;

public class OrganizationThemeData implements Serializable{

  private static final long serialVersionUID = 5810142207530370526L;
  
  private String _orgID;
  private String _orgName;
  private String _themeName;
  private String _cssURL;
  private String _logoURL;
  private String _menuLocation;
  private String _menuStyle;
  private boolean _procMenuVisible = true;
  
  
  public OrganizationThemeData(String orgID, String orgName, String themeName, String cssURL, String logoURL, String menuLocation, String menuStyle, boolean procMenuVisible){
    this._orgID = orgID;
    this._themeName = themeName;
    this._cssURL = cssURL;
    this._logoURL = logoURL;
    this._orgName = orgName;
    this._menuLocation = menuLocation;
    this._menuStyle = menuStyle;
    this._procMenuVisible = procMenuVisible;
  }
  
  /**
   * 
   * Getter method for _cssURL
   * 
   * @return Returns the _cssURL.
   */
  public String getCssURL() {
    return this._cssURL;
  }

  /**
   * 
   * Getter method for _logoURL
   * 
   * @return Returns the _logoURL.
   */
  public String getLogoURL() {
    return this._logoURL;
  }

  /**
   * 
   * Getter method for _orgID
   * 
   * @return Returns the _orgID.
   */
  public String getOrgID() {
    return this._orgID;
  }

  /**
   * 
   * Getter method for _themeName
   * 
   * @return Returns the _themeName.
   */
  public String getThemeName() {
    return this._themeName;
  }

  public String getOrgName() {
    return _orgName;
  }

  public String getMenuLocation() {
	  return _menuLocation;
  }	

  public void setMenuLocation(String menuLocation) {
	  _menuLocation = menuLocation;
  }

  public String getMenuStyle() {
	  return _menuStyle;
  }

  public void setMenuStyle(String menuStyle) {
	  _menuStyle = menuStyle;
  }

  public boolean getProcMenuVisible() {
    return _procMenuVisible;
  }

  public void setProcMenuVisible(boolean procMenuVisible) {
    _procMenuVisible = procMenuVisible;
  }

}
