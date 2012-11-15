package pt.iflow.api.licensing;

import org.apache.commons.discovery.tools.DiscoverSingleton;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;

public abstract class LicenseServiceFactory {

  private static final class LicenseServiceFactoryError extends Error {
    private static final long serialVersionUID = -8666419168994261983L;

    public LicenseServiceFactoryError(String msg, Throwable cause) {
      super(msg,cause);
    }

  }

  private static LicenseServiceFactory instance = null;

  static LicenseServiceFactory getInstance() {
    if(null == instance) {
      try {
        instance = (LicenseServiceFactory)DiscoverSingleton.find(LicenseServiceFactory.class, Setup.getProperties());
      } catch (Exception e) {
        Logger.adminError("LicenseServiceFactory", "getInstance", "Error creating LicenseServiceFactory instance", e);
        // check if a runtime error should be thrown
        throw new LicenseServiceFactoryError("Could not instantiate bean factory", e);
      }
    }
    return instance;
  }

  protected abstract LicenseService doGetLicenseService();

  ///////////////////////////////////////////////////////////
  
  public static LicenseService getLicenseService() {
    return getInstance().doGetLicenseService();
  }

}
