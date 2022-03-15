package pt.iflow.api.processtype;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Const;

public class FloatDataType extends FormattedDataType implements ProcessDataType {

  protected static final RawNumberFormatter rawFormatter = new RawNumberFormatter();
  protected static final Locale numberLocale = new Locale(Const.sDEF_NUMBER_LOCALE);
  protected static final Locale inputNumberLocale = new Locale(Const.sDEF_INPUT_NUMBER_LOCALE);

  protected static final NumberFormat floatInstance = 
		  new DecimalFormat(Const.sDEF_FLOAT_FORMAT,
				  new DecimalFormatSymbols(numberLocale));

  protected static final NumberFormat floatInstanceInput = 
		  new DecimalFormat(Const.sDEF_FLOAT_FORMAT,
				  new DecimalFormatSymbols(inputNumberLocale));
  
  
  public FloatDataType() {
    this(floatInstance, floatInstanceInput);
  }

  public FloatDataType(NumberFormat numberFormat, NumberFormat inputNumberFormat) {
    super(numberFormat,inputNumberFormat, rawFormatter);
  }

  public FloatDataType(String format, String inputFormat) {
    this(getFormat(format),getInputFormat(inputFormat));
  }

  private static NumberFormat getFormat(String pattern) {
    if(StringUtils.isBlank(pattern)) {
      return floatInstance;
    }
    DecimalFormat fmt = (DecimalFormat)NumberFormat.getNumberInstance(numberLocale);
    fmt.applyPattern(pattern);

    return fmt;
  }
  
  private static NumberFormat getInputFormat(String pattern) {
	    if(StringUtils.isBlank(pattern)) {
	      return floatInstanceInput;
	    }
	    DecimalFormat fmt = (DecimalFormat)NumberFormat.getNumberInstance(inputNumberLocale);
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
    }
    return result;
  }
  
  public Object parse(String source) throws ParseException {
    if (StringUtils.isEmpty(source))
      return null;
    
    return super.parse(source);
  }

}
