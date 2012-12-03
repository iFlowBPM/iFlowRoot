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
/**
 * 
 */
package pt.iflow.api.processtype;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

// TODO terminar a implementacao para dataype
public class BooleanFormatter implements VariableFormatter {
  
  private String sTrue;
  private String sFalse;
  private String sNull;

  public BooleanFormatter() {
    this("true", "false", null);
  }
  
  public BooleanFormatter(String sTrue, String sFalse) {
    this(sTrue, sFalse, null);
  }
  
  public BooleanFormatter(String sTrue, String sFalse, String sNull) {
    this.sTrue = sTrue;
    this.sFalse = sFalse;
    this.sNull = sNull;
  }
  
  public String format(Object obj) {
    if(obj == null) return sNull;
    if(obj instanceof Boolean) return ((Boolean)obj).booleanValue()?sTrue:sFalse;
    return null;
  }

  public Object parse(String source) throws ParseException {
    if(null == source || StringUtils.equals(this.sNull, source))
      return null;
    return new Boolean(StringUtils.equals(this.sTrue, source));
  }
}
