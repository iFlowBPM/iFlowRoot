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
package pt.iflow.api.processtype;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Const;

public class FloatDataType extends FormattedDataType implements ProcessDataType {

  protected static final RawNumberFormatter rawFormatter = new RawNumberFormatter();
  protected static final NumberFormat floatInstance = new DecimalFormat(Const.sDEF_FLOAT_FORMAT);

  public FloatDataType() {
    this(NumberFormat.getInstance());
  }

  public FloatDataType(NumberFormat numberFormat) {
    super(numberFormat, rawFormatter);
  }

  public FloatDataType(String format) {
    this(getFormat(format));
  }

  private static NumberFormat getFormat(String format) {
    if(StringUtils.isBlank(format)) {
      return floatInstance;
    }
    NumberFormat fmt = new DecimalFormat(format);
    // TODO symbol and separator support: $1,234.56 vs 1.234,56 €
    return fmt;
  }
  
  @Override
  public String toString() {
    return "Float";
  }

  @Override
  public Class<?> getSupportingClass() {
    return double.class;
  }

  public Object convertFrom(String rawvalue) throws ParseException {
    //MARTELADA, teve que ser
    Object result = "";
    try {
      if (rawvalue != null) {
        result = super.convertFrom(rawvalue);    
      }
    } catch (ParseException e) {
      if (rawvalue.indexOf(",") >= 0) {
        rawvalue = rawvalue.replaceAll("\\.", "");
        rawvalue = rawvalue.replaceAll(",", ".");
        rawvalue = rawFormatter.format(Float.valueOf(rawvalue));
        result = super.convertFrom(rawvalue);
      }
    }
    return result;
  }

}
