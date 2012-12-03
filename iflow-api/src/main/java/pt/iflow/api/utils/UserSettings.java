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
