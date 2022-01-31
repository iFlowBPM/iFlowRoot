package pt.iflow.api.processtype;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Const;

public class FloatDataType extends FormattedDataType implements ProcessDataType {

  protected static final RawNumberFormatter rawFormatter = new RawNumberFormatter();
  protected static final Locale numberLocale = new Locale(Const.sDEF_NUMBER_LOCALE);

  protected static final NumberFormat floatInstance = 
		  new DecimalFormat(Const.sDEF_FLOAT_FORMAT,
				  new DecimalFormatSymbols(numberLocale));

  
  public FloatDataType() {
    this(NumberFormat.getInstance());
  }

  public FloatDataType(NumberFormat numberFormat) {
    super(numberFormat, rawFormatter);
  }

  public FloatDataType(String format) {
    this(getFormat(format));
  }

  private static NumberFormat getFormat(String pattern) {
    if(StringUtils.isBlank(pattern)) {
      return floatInstance;
    }
    DecimalFormat fmt = (DecimalFormat)NumberFormat.getNumberInstance(numberLocale);
    fmt.applyPattern(pattern);

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
    Object result = "";
    try {
      if (rawvalue != null) {
        result = super.convertFrom(rawvalue);    
      }
    } catch (ParseException e) {
//      if (rawvalue.indexOf(",") >= 0) {
//        rawvalue = rawvalue.replaceAll("\\.", "");
//        rawvalue = rawvalue.replaceAll(",", ".");
//        rawvalue = rawFormatter.format(Float.valueOf(rawvalue));
//        result = super.convertFrom(rawvalue);
//      }
    }
    return result;
  }

}
