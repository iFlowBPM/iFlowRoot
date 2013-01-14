package pt.iflow.utils;

import java.util.Locale;
import java.util.TimeZone;

import pt.iflow.api.userdata.Tutorial;
import pt.iflow.api.utils.UserSettings;

public class UserSettingsImpl implements UserSettings,java.io.Serializable {
  private static final long serialVersionUID = 5391419150800182567L;

  private Locale locale;
  private TimeZone timezone;
  private boolean isDefault;
  private String langString;
  private String tutorial;
  private boolean helpMode;
  private boolean tutorialMode;

  public UserSettingsImpl() {
    this(Locale.getDefault());
    this.tutorial = Tutorial.TUTORIAL_DEFAULT;
  }

  public UserSettingsImpl(Locale orgLocale) {
    this.locale = orgLocale;
    this.timezone = TimeZone.getDefault();
    this.langString = this.locale.getLanguage()+"_"+this.locale.getCountry();
    this.isDefault = true;
    this.tutorial = Tutorial.TUTORIAL_DEFAULT;
    this.helpMode = false;
    this.tutorialMode = false;
  }

  public UserSettingsImpl(String lang, String region, String timezone) {
    this(new Locale(lang, region), TimeZone.getTimeZone(timezone), Tutorial.TUTORIAL_DEFAULT, false, false);
  }

  public UserSettingsImpl(Locale locale, TimeZone timezone, String tutorial, boolean helpMode, boolean tutorialMode) {
    this.locale = locale;
    this.timezone = timezone;
    this.langString = this.locale.getLanguage()+"_"+this.locale.getCountry();
    this.isDefault = false;
    this.tutorial = tutorial;
    this.helpMode = helpMode;
    this.tutorialMode = tutorialMode;
  }

  public UserSettingsImpl(String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode) {
    this(new Locale(lang, region), TimeZone.getTimeZone(timezone), tutorial, helpMode, tutorialMode);
  }

  public Locale getLocale() {
    return locale;
  }

  public TimeZone getTimeZone() {
    return timezone;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public String getLangString() {
    return langString;
  }

  public String getTimeZoneID() {
    return timezone.getID();
  }

  public String getTutorial() {
    return tutorial;
  }

  public void setTutorial(String tutorial) {
    this.tutorial = tutorial;
  }

  public boolean isHelpMode() {
    return helpMode;
  }

  public void setHelpMode(boolean helpMode) {
    this.helpMode = helpMode;
  }

  public boolean isTutorialMode() {
    return tutorialMode;
  }

  public void setTutorialMode(boolean tutorialMode) {
    this.tutorialMode = tutorialMode;
  }

}
