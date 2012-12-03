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
