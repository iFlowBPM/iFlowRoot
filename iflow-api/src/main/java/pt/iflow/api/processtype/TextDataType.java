package pt.iflow.api.processtype;


public class TextDataType implements ProcessDataType {

  public Object convertFrom(String rawvalue) {
    return rawvalue;
  }

  public String convertTo(Object value) {
    return value == null ? "" : String.valueOf(value);
  }

  public String format(Object obj) {
    return obj == null ? null : String.valueOf(obj);
  }

  public Object parse(String source) {
    return source;
  }

  public String toString() {
    return "Text";
  }

  public Class<?> getSupportingClass() {
    return String.class;
  }

  public boolean isBindable() {
    return false;
  }
  
}
