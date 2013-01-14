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
