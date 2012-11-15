package pt.iflow.applet;

public interface DynamicFormProvider {
  public static final int OK = 1;
  public static final int CANCEL = 0;
  
  DynamicForm getForm();
  String validateForm();
  
}
