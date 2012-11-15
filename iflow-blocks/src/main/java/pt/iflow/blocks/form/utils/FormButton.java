package pt.iflow.blocks.form.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.utils.UserInfoInterface;

public class FormButton {

  public static final String ATTR_PREFIX = FormProps.sBUTTON_ATTR_PREFIX;
  public static final String ATTR_ID = FormProps.sBUTTON_ATTR_ID;
  public static final String ATTR_POSITION = FormProps.sBUTTON_ATTR_POSITION;
  public static final String ATTR_TYPE = FormProps.sBUTTON_ATTR_TYPE;
  public static final String ATTR_TEXT = FormProps.sBUTTON_ATTR_TEXT;
  public static final String ATTR_TOOLTIP = FormProps.sBUTTON_ATTR_TOOLTIP;
  public static final String ATTR_IMAGE = FormProps.sBUTTON_ATTR_IMAGE;
  public static final String ATTR_CUSTOM_VAR = FormProps.sBUTTON_ATTR_CUSTOM_VAR;
  public static final String ATTR_CUSTOM_VALUE = FormProps.sBUTTON_ATTR_CUSTOM_VALUE;
  public static final String ATTR_SHOW_COND = FormProps.sBUTTON_ATTR_SHOW_COND;
  public final static String ATTR_IGNORE_FORM_PROCESSING = FormProps.sBUTTON_ATTR_IGNORE_FORM_PROCESSING;
  public final static String ATTR_CONFIRM_ACTION = FormProps.sBUTTON_ATTR_CONFIRM_ACTION;
  public final static String ATTR_CONFIRM_ACTION_MESSAGE = FormProps.sBUTTON_ATTR_CONFIRM_ACTION_MESSAGE;
  public final static String ATTR_IGNORE_FORM_VALIDATION = FormProps.sBUTTON_ATTR_IGNORE_FORM_VALIDATION;

  private Map<String, String> attrs;

  public FormButton(int id, int position, FormButtonType type) {
    attrs = new HashMap<String, String>();
    attrs.put(ATTR_ID, String.valueOf(id));
    attrs.put(ATTR_POSITION, String.valueOf(position));
    setAttribute(ATTR_TYPE, type.getCode());
  }

  public FormButton(Map<String, String> attrs) {
    this.attrs = attrs;
  }

  public int getId() {
    return Integer.parseInt(getAttribute(ATTR_ID));
  }

  public FormButtonType getType() {
    return FormButtonType.parse(getAttribute(ATTR_TYPE));
  }

  public String getText(UserInfoInterface userInfo) {
    String text = getAttribute(ATTR_TEXT);
    if (StringUtils.isEmpty(text)) {
      text = getType().getText(userInfo);
    }
    return text;
  }

  public void setAttribute(String attr, String value) {
    attrs.put(attr, value);
  }

  public String getAttribute(String attr) {
    return attrs.get(attr);
  }

  public boolean ignoreFormProcessing() {
    return StringUtils.equals("true", getAttribute(ATTR_IGNORE_FORM_PROCESSING));
  }

  public boolean confirmMessage() {
    return StringUtils.equals("true", getAttribute(ATTR_CONFIRM_ACTION));
  }

  public boolean ignoreFormValidation() {
    return StringUtils.equals("true", getAttribute(ATTR_IGNORE_FORM_VALIDATION));
  }
}
