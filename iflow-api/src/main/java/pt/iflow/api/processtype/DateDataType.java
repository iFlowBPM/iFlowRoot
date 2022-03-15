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
    this(getFormat(null), getFormat(null));
  }

  public DateDataType(DateFormat format, DateFormat inputFormat) {
    super(format, inputFormat, RAW_FORMAT);
  }
  
  public DateDataType(String format, String inputFormat) {
    this(getFormat(format), getFormat(inputFormat));
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
