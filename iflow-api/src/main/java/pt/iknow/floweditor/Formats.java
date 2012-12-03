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
package pt.iknow.floweditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.iflow.api.processtype.DataTypeEnum;

public class Formats {

  private static final Map<DataTypeEnum,Formats> formatMapping = new HashMap<DataTypeEnum, Formats>();
  
  static {
    Formats numberFormats = new Formats();
    Formats dateFormats = new Formats();
    
    numberFormats.addFormat("#,##0.00", "Formats.numeric.desc"); //$NON-NLS-1$ //$NON-NLS-2$
    numberFormats.addFormat("#,##0.00 \u00A4", "Formats.numeric.descEur"); //$NON-NLS-1$ //$NON-NLS-2$
    numberFormats.addFormat("#0.000 '%'", "Formats.numeric.descPercent"); //$NON-NLS-1$ //$NON-NLS-2$

    dateFormats.addFormat("dd/MM/yyyy", "Formats.date.dmy"); //$NON-NLS-1$ //$NON-NLS-2$
    dateFormats.addFormat("yyyy-MM-dd", "Formats.date.ymd"); //$NON-NLS-1$ //$NON-NLS-2$
    
    formatMapping.put(DataTypeEnum.Date, dateFormats);
    formatMapping.put(DataTypeEnum.DateArray, dateFormats);
    formatMapping.put(DataTypeEnum.Integer, numberFormats);
    formatMapping.put(DataTypeEnum.IntegerArray, numberFormats);
    formatMapping.put(DataTypeEnum.Float, numberFormats);
    formatMapping.put(DataTypeEnum.FloatArray, numberFormats);
  }
  
  public static Formats getFormats(DataTypeEnum dataType) {
    return formatMapping.get(dataType);
  }
  
  List<Format> formats = new ArrayList<Format>();
  
  void addFormat(String fmt, String descId) {
    Format format = new Format();
    format.format = fmt;
    format.nameId = descId;
    formats.add(format);
  }
  
  public String getFormatDesc(String fmt) {
    for(Format format : formats) {
      if(format.format.equals(fmt)) return format.nameId;
    }
    return null;
  }
  
  public List<Format> getFormats() {
    return Collections.unmodifiableList(formats);
  }
  
  public static class Format {
    public String format;
    public String nameId;
    
    public boolean equals(Object obj) {
      if(null == obj) return false;
      if(obj instanceof Format)
        obj = ((Format)obj).format;
      return format.equals(obj);
    }
    
    public String toString() {
      return nameId;
    }
  }
  
}
