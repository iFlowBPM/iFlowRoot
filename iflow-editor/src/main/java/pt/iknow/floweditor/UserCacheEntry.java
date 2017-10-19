package pt.iknow.floweditor;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user-cache-entry")
public class UserCacheEntry 
{
  private static String defaultLang = "";
  static {
    Locale l = Locale.getDefault();
    defaultLang = l.getLanguage()+"_"+l.getCountry();
  }
  
  String iconsHash = "";
  String librariesHash = "";
  String language = defaultLang;

  public String getIconsHash() {
    return iconsHash;
  }

  public void setIconsHash(String iconsHash) {
    this.iconsHash = iconsHash;
  }

  public String getLibrariesHash() {
    return librariesHash;
  }

  public void setLibrariesHash(String librariesHash) {
    this.librariesHash = librariesHash;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String lang) {
    language = lang;
  }

}
