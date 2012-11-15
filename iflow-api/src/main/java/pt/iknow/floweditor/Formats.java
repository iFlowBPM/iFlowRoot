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
