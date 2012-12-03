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
package pt.iflow.blocks.form.utils;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.msg.Messages;

public enum FormButtonType {
  CANCEL("_cancelar", "BlockForm.button.default.text.cancel"), 
  RESET("_repor", "BlockForm.button.default.text.reset"), 
  SAVE("_guardar", "BlockForm.button.default.text.save"), 
  PRINT("_imprimir", "BlockForm.button.default.text.print"), 
  NEXT("_avancar", "BlockForm.button.default.text.next"), 
  CUSTOM("_custom", "BlockForm.button.default.text.custom"), 
  RETURN_PARENT("_retornar_parent", "BlockForm.button.default.text.returnParent"), 
  NONE("_none", "BlockForm.button.default.text.none");

  String code;
  String messageKey;

  private FormButtonType(String code, String messageKey) {
    this.code = code;
    this.messageKey = messageKey;
  }

  public String getCode() {
    return code;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public String getText(UserInfoInterface userInfo) {
    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    return msg.getString(messageKey);
  }

  public static FormButtonType parse(String code) {
    for (FormButtonType type : FormButtonType.values()) {
      if (StringUtils.equals(code, type.getCode())) {
        return type;
      }
    }
    return FormButtonType.NONE;
  }
}
