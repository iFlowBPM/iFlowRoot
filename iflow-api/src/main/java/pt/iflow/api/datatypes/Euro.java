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
package pt.iflow.api.datatypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Euro extends NumericDataType implements DataTypeInterface {

  private boolean hideSufix = false;

  public Euro() {
    this.setLocale(null);
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    hideSufix = DataTypeUtils.hideSufix(extraProps);
  }

  public String getDescription() {
    return Messages.getString("Euro.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Euro.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 5;
  }

  public int getFormSize() {
    return 14;
  }

  public String getFormPrefix() {
    return getFormPrefix(null);
  }
  public String getFormPrefix(Object[] aoaArgs) {
    return ""; //$NON-NLS-1$
  }

  public String getFormSuffix() {
    return getFormSuffix(null);
  }
  public String getFormSuffix(Object[] aoaArgs) {
    return hideSufix ? "" : " €"; //$NON-NLS-1$
  }

  protected String validateErrorMsg() {
    return "Euro.validation_error"; //$NON-NLS-1$
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {
    Object myinput = input;
    if (input != null && input instanceof String) {
      try {
        myinput = java.lang.Double.parseDouble((String)input);
      }
      catch (Exception e) {}
    }
    return formatNumber(myinput, aoaArgs)+getFormSuffix();
  }

  
  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
    this.fmt = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(this.locale));
  }

}
