package pt.iflow.api.datatypes.msg;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "pt.iflow.api.datatypes.msg.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public String getString(String asMessage, Object... asAttributes) {
    String sMessage = getString(asMessage);
    if (asAttributes != null) {
      sMessage = MessageFormat.format(sMessage, asAttributes);
    }
    return sMessage;
  }
}
