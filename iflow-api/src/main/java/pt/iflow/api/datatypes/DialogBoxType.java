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
package pt.iflow.api.datatypes;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.UserInfoInterface;

public enum DialogBoxType {
  INFO("/images/box_info.png", "DialogBoxType.text.info"), 
  WARN("/images/box_warn.png", "DialogBoxType.text.warn"), 
  ERROR("/images/box_error.png", "DialogBoxType.text.error");
  
  private String icon;
  private String textKey;
  
  private DialogBoxType(String icon, String textKey) {
    this.icon = icon;
    this.textKey = textKey;
  }
  
  public String getIcon() {
    return this.icon;
  }
  
  public String getTextKey() {
    return this.textKey;
  }
  
  public String getText(UserInfoInterface userInfo) {
    if (userInfo != null) {
      return userInfo.getMessages().getString(getTextKey());
    }    
    return "";
  }
  
  public static DialogBoxType parse(String type) {
    for (DialogBoxType t : values()) {
      if (StringUtils.equals(type, t.name())) {
        return t;
      }
    }
    return INFO;
  }

  @Override
  public String toString() {
    return name();
  }
  
}
