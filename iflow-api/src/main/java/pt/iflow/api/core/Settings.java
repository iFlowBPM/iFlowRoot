package pt.iflow.api.core;

import java.util.Locale;
import java.util.TimeZone;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserSettings;

public interface Settings {

  static final Locale ENGLISH = new Locale("en", "US");
  static final Locale PORTUGUES = new Locale("pt", "PT");
  static final Locale ESPANHOL = new Locale("es", "ES");
  public static final Locale[] localeKeys = { ENGLISH, PORTUGUES, ESPANHOL };
  
  public abstract UserSettings getUserSettings(UserInfoInterface userInfo);
  public abstract UserSettings getUserSettings(UserInfoInterface userInfo, String userId);
  public abstract void updateUserSettings(UserInfoInterface userInfo, String lang, String region, String timezone);
  public abstract void updateUserSettings(UserInfoInterface userInfo, String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode);
  public abstract void updateUserSettings(UserInfoInterface userInfo, String userId, String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode);
  public abstract void updateUserSettings(UserInfoInterface userInfo, UserSettings settings);

  public abstract Locale getOrganizationLocale(String orgId);
  public abstract Locale getOrganizationLocale(UserInfoInterface userInfo);
  public abstract TimeZone getOrganizationTimeZone(String orgId);
  public abstract TimeZone getOrganizationTimeZone(UserInfoInterface userInfo);
  public abstract void updateOrganizationSettings(UserInfoInterface userInfo, String lang, String region, String timezone);
  public abstract void updateOrganizationSettings(UserInfoInterface userInfo, String orgId, String lang, String region, String timezone);

}
