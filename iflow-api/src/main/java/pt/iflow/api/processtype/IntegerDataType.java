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


public class IntegerDataType extends FormattedDataType implements ProcessDataType {
  protected static final RawNumberFormatter rawFormatter = new RawNumberFormatter();
  protected static final NumberFormat integerInstance = new DecimalFormat(Const.sDEF_INT_FORMAT);

  public IntegerDataType() {
    this(getFormat(null));
  }

  public IntegerDataType(NumberFormat numberFormat) {
    super(numberFormat, rawFormatter);
  }

  public IntegerDataType(String format) {
    this(getFormat(format));
  }

  private static NumberFormat getFormat(String format) {
    if(StringUtils.isBlank(format)) format = Const.sDEF_INT_FORMAT;
    NumberFormat fmt = new DecimalFormat(format);
    // TODO symbol and separator support: $1,234.56 vs 1.234,56 €
    fmt.setParseIntegerOnly(true);
    return fmt;
  }
  
  @Override
  public Object convertFrom(String rawvalue) throws ParseException {
    Object obj = super.convertFrom(rawvalue);
    if(null == obj) return null;
    if(obj instanceof Number) // Just in case... return a Long instance
      return new Long(((Number) obj).longValue());
    return null;
  }

  @Override
  public String toString() {
    return "Integer";
  }

  @Override
  public Class<?> getSupportingClass() {
    return int.class;
  }
}
