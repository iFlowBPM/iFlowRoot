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

import java.util.Map;

import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Double extends NumericDataType implements DataTypeInterface {

  public Double() {
    this.setLocale(null);
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    this.fmt.setMaximumFractionDigits(Const.MAX_DOUBLE_DECIMAL_DIGITS);
  }

  public String getDescription() {
    return Messages.getString("Double.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Double.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 2;
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
    return ""; //$NON-NLS-1$
  }

  public int getFormSize() {
    return 10;
  }

  protected String validateErrorMsg() {
    return "Double.validation_error"; //$NON-NLS-1$
  }

//  protected String formatNumber (Object input, Object[] aoaArgs) {
//    java.lang.Double d = new java.lang.Double(java.lang.Double.NaN);
//    try {
//      String var = (String)input;
//      var = var.replace(",", "");
//      d = new java.lang.Double(var);
//    }
//    catch (Exception e) {
//      Logger.error("", this, "formatNumber", "Unable to format number '" + input + "'.");
//    }
//
//    String s = ""; //$NON-NLS-1$
//
//    if (!d.isNaN()) {
//      if (java.lang.Double.compare(d.doubleValue(), 0) == 0) { //$NON-NLS-1$
//        s = "0"; //$NON-NLS-1$
//      }
//      else {
//        s = fmt.format(d);
//      }
//    }
//    return s;
//  }

}
