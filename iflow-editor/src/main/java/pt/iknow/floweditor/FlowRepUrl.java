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
package pt.iknow.floweditor;

import java.util.HashMap;
import java.util.Locale;

public class FlowRepUrl {
  String url = null;
  String user = null;
  HashMap<String, Long> iconsLastModifiedMap = new HashMap<String, Long>();
  HashMap<String, Long> librariesLastModifiedMap = new HashMap<String, Long>();
  HashMap<String, String> languageMap = new HashMap<String, String>();

  private static String defaultLang = "";
  static {
    Locale l = Locale.getDefault();
    defaultLang = l.getLanguage()+"_"+l.getCountry();
  }
  
  public FlowRepUrl() {
    super();
  }
  
  public FlowRepUrl(String url, String user) {
    super();
    this.url = url;
    this.user = user;
    iconsLastModifiedMap.put(user, -1L);
    librariesLastModifiedMap.put(user, -1L);
    languageMap.put(getUserKey(), defaultLang);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
  
  public String toString() {
    return getUrl();
  }

  @Deprecated
  public Long getIconsLastModified() {
    Long lmod = iconsLastModifiedMap.get(getUserKey());
    return lmod==null?0L:lmod;
  }

  @Deprecated
  public void setIconsLastModified(Long iconsLastModified) {
    this.iconsLastModifiedMap.put(getUserKey(), iconsLastModified);
  }

  @Deprecated
  public Long getLibrariesLastModified() {
    Long lmod = librariesLastModifiedMap.get(getUserKey());
    return lmod==null?0L:lmod;
  }

  @Deprecated
  public void setLibrariesLastModified(Long librariesLastModified) {
    this.librariesLastModifiedMap.put(getUserKey(),librariesLastModified);
  }

  @Deprecated
  public HashMap<String, Long> getIconsLastModifiedMap() {
    return iconsLastModifiedMap;
  }

  @Deprecated
  public void setIconsLastModifiedMap(HashMap<String, Long> iconsLastModifiedMap) {
    this.iconsLastModifiedMap = iconsLastModifiedMap;
  }

  @Deprecated
  public HashMap<String, Long> getLibrariesLastModifiedMap() {
    return librariesLastModifiedMap;
  }

  @Deprecated
  public void setLibrariesLastModifiedMap(HashMap<String, Long> librariesLastModifiedMap) {
    this.librariesLastModifiedMap = librariesLastModifiedMap;
  }
  
  public String getLanguage() {
    String lang = languageMap.get(getUserKey());
    return lang==null?defaultLang:lang;
  }

  public void setLanguage(String lang) {
    this.languageMap.put(getUserKey(),lang);
  }

  public HashMap<String, String> getLanguageMap() {
    return languageMap;
  }

  public void setLanguageMap(HashMap<String, String> languageMap) {
    this.languageMap = languageMap;
  }
  
  public String getUserKey() {
    return (getUser() + "@" + getUrl()).replaceAll("[/:]","_");
  }
  
  public void setUserKey() {
    // do nothing. just to prevent some complaints.
  }
}
