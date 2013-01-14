package pt.iflow.api.datatypes;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class DialogBoxConfig {

  public static final String TYPE = "type";
  public static final String TITLE = "title";
  public static final String MESSAGE = "message";
  public static final String DISABLED = "disabled";
  
  public DialogBoxType type = DialogBoxType.INFO;
  public String title = null;
  public String message = "";
  public boolean disabled = false;
  
  @Override
  public String toString() {
    JSONObject obj = new JSONObject();
    try {
      obj = obj.put(TYPE, type.toString());      
      obj = obj.put(TITLE, title);
      obj = obj.put(MESSAGE, message);
      obj = obj.put(DISABLED, disabled);      
      
    } catch (JSONException e) {
      Logger.error(null, DialogBoxConfig.class.getSimpleName(),
          "toString", "JSONException caught!", e);
    }
    return obj.toString();
  }

  public static DialogBoxConfig parse(String jsonData) {
    DialogBoxConfig dbc = new DialogBoxConfig();
    try {
      JSONObject jsonObj = new JSONObject(jsonData);
      dbc.type = DialogBoxType.parse(jsonObj.getString(TYPE));
      dbc.title = jsonObj.getString(TITLE);
      dbc.message = jsonObj.getString(MESSAGE);
      if (StringUtils.isEmpty(dbc.message)) {
        dbc.message = "";
      }
      dbc.disabled = jsonObj.getBoolean(DISABLED);
    } catch (JSONException e) {
      // ignore, defaults to using value as message
      dbc.message = jsonData;
    }
    return dbc;
  }

  public DialogBoxType getType() {
    return type;
  }

  public String getTypeText(UserInfoInterface userInfo) {
    return type.getText(userInfo);
  }

  public String getTitle(UserInfoInterface userInfo) {
    if (userInfo != null && title == null) {
      return type.getText(userInfo);
    }    
    return title;
  }

  public String getMessage() {
    return message;
  }

  public boolean isDisabled() {
    return disabled;
  }
  
}
