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
