package pt.iflow.applet;

import java.util.ArrayList;
import java.util.List;

public class DynamicForm {

  private final String title;
  private final List<DynamicField> fields;
  
  public DynamicForm(String title) {
    this.title = title;
    this.fields = new ArrayList<DynamicField>();
  }
  
  public void addField(DynamicField field) {
    this.fields.add(field);
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public List<DynamicField> getFields() {
    return this.fields;
  }
  
  public boolean isEmpty() {
    return this.fields.isEmpty();
  }
}
