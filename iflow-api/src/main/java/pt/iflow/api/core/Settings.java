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
