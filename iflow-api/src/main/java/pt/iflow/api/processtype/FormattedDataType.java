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

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import org.apache.commons.lang.StringUtils;

public abstract class FormattedDataType implements ProcessDataType {
  
  protected Format formatter;
  protected Format rawFormatter;
  
  public FormattedDataType() {
    this(null, null);
  }

  public FormattedDataType(Format formatter, Format rawFormatter) {
    this.formatter = formatter;
    this.rawFormatter = rawFormatter;
  }

  public Object convertFrom(String rawvalue) throws ParseException {
    if (rawvalue == null) // Retornava null. Mudei porque estragava os formularios
      return "";
    if(null == rawFormatter) throw new ParseException("Raw Parser/Formatter not set", -1);
    Object result = rawFormatter.parseObject(rawvalue);
    return result;
  }

  public String convertTo(Object value) {
    if (value == null)
      return "";
    if(null == rawFormatter) return String.valueOf(value);
    String result = rawFormatter.format(value);
    return result;
  }


  public String format(Object obj) {
    if (obj == null) // Retornava null. Mudei porque estragava os formularios
      return "";
    if(null == formatter) return String.valueOf(obj);
    String result = formatter.format(obj);
    return result;
  }

  public Object parse(String source) throws ParseException {
    if (StringUtils.isEmpty(source))
      return null;
    
    if(null == formatter) throw new ParseException("Parser/Formatter not set", -1);
    ParsePosition parsePosition = new ParsePosition(0);
    Object result = formatter.parseObject(source, parsePosition);
    if (parsePosition.getIndex() == 0) {
      throw new ParseException("Error parsing String: \""+source+"\"", parsePosition.getErrorIndex());
    }
    return result;
  }

  public boolean isBindable() {
    return false;
  }
  
  public abstract Class<?> getSupportingClass();

}
