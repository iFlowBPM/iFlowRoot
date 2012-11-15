package pt.iflow.applet;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "pt.iflow.applet.applet"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE;

  static {
    reset();
  }
  
  private Messages() {
  }
  
  public static void reset() {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  /**
   * MessageFormat takes a set of objects, formats them, then returns the formatted strings.
   * 
   * @param asMessage
   *          The Message Name.
   * @param asAttributes
   *          The attributes.
   * @return The formatted strings.
   */
  public static String getString(String asMessage, Object... asAttributes) {
    String sMessage = null;

    if (asMessage != null) {
      if (asAttributes != null) {
        return MessageFormat.format(getString(asMessage), asAttributes);
      } else {
        return asMessage;
      }
    }

    return sMessage;
  }
}
