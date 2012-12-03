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
