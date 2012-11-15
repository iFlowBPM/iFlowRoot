package pt.iflow.applet;

public class DynamicField {

  public static enum Type {
    TEXTFIELD,
    TEXTAREA,
    DATE,
    SIGNATURE_CERTIFICATE,
    CHECKBOX,
    FILE,
    IMAGE,
  }
  
  private final Type type;
  private final String label;
  private Object value;
  
  public DynamicField(Type type, String label) {
    this.type = type;
    this.label = label;
    this.value = null;
  }
  
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public String getLabel() {
    return label;
  }

}
