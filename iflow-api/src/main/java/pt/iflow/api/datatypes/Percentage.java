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
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Percentage extends NumericDataType implements DataTypeInterface {

  public Percentage() {
    this.setLocale(null);
  }
  
  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
  }

  public String getDescription() {
    return Messages.getString("Percentage.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Percentage.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 3;
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
    return "%"; //$NON-NLS-1$
  }

  protected String validateErrorMsg() {
    return "Percentage.validation_error";
  }
  
  public String formatToHtml (Object input, Object[] aoaArgs) {
    return formatNumber(input, aoaArgs)+getFormSuffix();
  }

}
