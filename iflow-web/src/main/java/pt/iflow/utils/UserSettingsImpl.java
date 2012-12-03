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
