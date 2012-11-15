package pt.iflow.api.utils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;

public abstract class IFlowMessages implements Serializable, IFlowMessagesInterface {
  private static final long serialVersionUID = 8476091446771206166L;

  private final ResourceBundle RESOURCE_BUNDLE;
  private final ResourceBundle SYS_BUNDLE;

  private static final Hashtable<String,ResourceBundle> bundleCache = new Hashtable<String, ResourceBundle>();
  private static final Hashtable<String, Locale> localeCache = new Hashtable<String, Locale>();
  
  private static String getCacheKey(String bundleName, Locale locale, String organization) {
    StringBuilder sb = new StringBuilder(bundleName).append("-").append(locale);
    if(null != organization){
      sb.append("-").append(organization).toString();
    }
    return sb.toString();
  }
  
  private static synchronized Locale getLocale(String location) {
    if (null == location)
      return null;
    Locale locale = localeCache.get(location);
    if (null == locale) {
      String[] parts = location.split("_");
      if(parts.length == 1)
        locale = new Locale(parts[0]);
      else if(parts.length == 2)
        locale = new Locale(parts[0], parts[1]);
      else if(parts.length == 3)
        locale = new Locale(parts[0], parts[1], parts[2]);
      else 
        return null;
      localeCache.put(location, locale);
    }

    return locale;
  }

  private static synchronized ResourceBundle getBundle(String bundleName, Locale locale, String organization) {
    String key = getCacheKey(bundleName, locale, organization);
    ResourceBundle b = bundleCache.get(key);
    if(null == b) {
      b = BeanFactory.getRepBean().getBundle(bundleName, locale, organization);
      bundleCache.put(key, b);
    }
    return b;
  }

  protected IFlowMessages(String bundleName, String locale, String organization) {
    this(bundleName, getLocale(locale), organization);
  }
  
  protected IFlowMessages(String bundleName, Locale locale, String organization) {
    RESOURCE_BUNDLE = getBundle(bundleName, locale, organization);
    if (!StringUtils.equals(Const.SYSTEM_ORGANIZATION, organization)) {
      SYS_BUNDLE = getBundle(bundleName, locale, Const.SYSTEM_ORGANIZATION);
    } else {
      SYS_BUNDLE = null;
    }
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.utils.IFlowMessagesInterface#getString(java.lang.String, java.lang.String[])
   */
  public String getString(String asMessage, String... asAttributes) {
    return getString(asMessage, (Object[])asAttributes);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.web.utils.IFlowMessagesInterface#getString(java.lang.String, java.lang.Object[])
   */
  public String getString(String asMessage, Object... asAttributes) {
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
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.utils.IFlowMessagesInterface#getString(java.lang.String, java.util.List)
   */
  public String getString(String asMessage, List<String> asAttributes) {
    return getString(asMessage, asAttributes.toArray(new String[asAttributes.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.web.utils.IFlowMessagesInterface#getString(java.lang.String, java.lang.String)
   */
  public String getString(String asMessage, String asAttribute) {
    String[] saTmp = { asAttribute };
    return getString(asMessage, saTmp);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.utils.IFlowMessagesInterface#getString(java.lang.String, java.lang.String, java.lang.String)
 */
  public String getString(String asMessage, String asAttribute0, String asAttribute1) {

    String[] saTmp = { asAttribute0, asAttribute1 };
    return getString(asMessage, saTmp);

  }

  /* (non-Javadoc)
 * @see pt.iflow.web.utils.IFlowMessagesInterface#getString(java.lang.String)
 */
  public String getString(String key) {
    if (orgHasKey(key)) {
      return RESOURCE_BUNDLE.getString(key);
    } else if (sysHasKey(key)){
      return SYS_BUNDLE.getString(key);
    } else {
      return '!' + key + '!';
    }
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.utils.IFlowMessagesInterface#getMessages()
   */
  public Hashtable<String, String> getMessages() {
    Hashtable<String, String> messages = new Hashtable<String, String>();
    Enumeration<String> iter = RESOURCE_BUNDLE.getKeys();
    while(iter.hasMoreElements()) {
      String key = iter.nextElement();
      messages.put(key, RESOURCE_BUNDLE.getString(key));
    }
    return messages;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.utils.IFlowMessagesInterface#hasKey(java.lang.String)
   */
  public boolean hasKey(String key) {
    return orgHasKey(key) || sysHasKey(key);
  }

  private boolean orgHasKey(String key) {
    try {
      RESOURCE_BUNDLE.getString(key);
      return true;
    } catch (MissingResourceException e) {
      return false;
    }
  }

  private boolean sysHasKey(String key) {
    if (SYS_BUNDLE == null) {
      return false;
    }
    try {
      SYS_BUNDLE.getString(key);
      return true;
    } catch (MissingResourceException e) {
      return false;
    }
  }
}
