package pt.iflow.api.utils;

public interface UserSettings {
  public static final String TUTORIAL_DEFAULT = "none";
  public static final String TUTORIAL_USERS = "users";
  public static final String TUTORIAL_ORG_UNITS = "org_units";
  public static final String TUTORIAL_PROFILES = "profiles";
  public static final String TUTORIAL_FLOWS = "flows";
  public static final String TUTORIAL_PERMISSIONS = "permissions";
  public static final String TUTORIAL_MENUS = "menus";

  public abstract java.util.TimeZone getTimeZone();
  
  public abstract java.util.Locale getLocale();
  
  public abstract boolean isDefault();
  
  public abstract String getLangString();
  
  public abstract String getTimeZoneID();

  public abstract String getTutorial();

  public abstract void setTutorial(String tutorial);

  public abstract boolean isHelpMode();

  public abstract void setHelpMode(boolean helpMode);

  public abstract boolean isTutorialMode();

  public abstract void setTutorialMode(boolean helpMode);
}
