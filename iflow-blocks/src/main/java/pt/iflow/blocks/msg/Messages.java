package pt.iflow.blocks.msg;

import java.util.Locale;

import pt.iflow.api.utils.IFlowMessages;

/**
 * @author jcosta
 * 
 */
public class Messages extends IFlowMessages {
  private static final long serialVersionUID = 377171027191210222L;

  private static final String BUNDLE_NAME = "blocks"; //$NON-NLS-1$

  private Messages(Locale loc, String organization) {
    super(BUNDLE_NAME, loc, organization);
  }

  private Messages(String loc, String organization) {
    super(BUNDLE_NAME, loc, organization);
  }

  public static synchronized Messages getInstance() {
    return getInstance(Locale.getDefault(), null);
  }

  public static synchronized Messages getInstance(Locale locale) {
    return getInstance(locale, null);
  }

  public static synchronized Messages getInstance(String locale) {
    return getInstance(locale, null);
  }

  public static synchronized Messages getInstance(Locale locale, String organization) {
    return new Messages(locale, organization);
  }

  public static synchronized Messages getInstance(String location, String organization) {
    return new Messages(location, organization);
  }
}
