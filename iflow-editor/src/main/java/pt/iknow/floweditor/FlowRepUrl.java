package pt.iknow.floweditor;

import java.util.HashMap;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
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
