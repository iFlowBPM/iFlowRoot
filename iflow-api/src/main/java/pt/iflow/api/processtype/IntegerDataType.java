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
    this(getFormat(null),getFormat(null));
  }

  public IntegerDataType(NumberFormat numberFormat, NumberFormat inputNumberFormat) {
    super(numberFormat, inputNumberFormat, rawFormatter);
  }

  public IntegerDataType(String format, String inputFormat) {
    this(getFormat(format), getFormat(inputFormat));
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
