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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Const;

public class DateDataType extends FormattedDataType implements ProcessDataType {

  private static final DateFormat RAW_FORMAT = new SimpleDateFormat("ddMMyyyykkmmssSSS");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat(Const.sDEF_DATE_FORMAT);

  public DateDataType() {
    this(getFormat(null));
  }

  public DateDataType(DateFormat format) {
    super(format, RAW_FORMAT);
  }
  
  public DateDataType(String format) {
    this(getFormat(format));
  }

  private static DateFormat getFormat(String format) {
    if(StringUtils.isBlank(format))
      return DATE_FORMAT;
    DateFormat fmt = new SimpleDateFormat(format);
    return fmt;
  }

  @Override
  public String toString() {
    return "Date";
  }

  @Override
  public Class<?> getSupportingClass() {
    return Date.class;
  }
}
