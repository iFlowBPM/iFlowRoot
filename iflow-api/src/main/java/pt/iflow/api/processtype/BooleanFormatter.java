/**
 * 
 */
package pt.iflow.api.processtype;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

// TODO terminar a implementacao para dataype
public class BooleanFormatter implements VariableFormatter {
  
  private String sTrue;
  private String sFalse;
  private String sNull;

  public BooleanFormatter() {
    this("true", "false", null);
  }
  
  public BooleanFormatter(String sTrue, String sFalse) {
    this(sTrue, sFalse, null);
  }
  
  public BooleanFormatter(String sTrue, String sFalse, String sNull) {
    this.sTrue = sTrue;
    this.sFalse = sFalse;
    this.sNull = sNull;
  }
  
  public String format(Object obj) {
    if(obj == null) return sNull;
    if(obj instanceof Boolean) return ((Boolean)obj).booleanValue()?sTrue:sFalse;
    return null;
  }

  public Object parse(String source) throws ParseException {
    if(null == source || StringUtils.equals(this.sNull, source))
      return null;
    return new Boolean(StringUtils.equals(this.sTrue, source));
  }
}