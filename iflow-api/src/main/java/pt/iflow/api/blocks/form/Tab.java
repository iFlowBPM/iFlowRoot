package pt.iflow.api.blocks.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Tab {
  @Value
  private String text;
  @Value(optional=true)
  private List<Field> fields;
  @Value(optional=true)
  private Map<String,String> properties;

  public Tab() {
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> tabs) {
    this.fields = tabs;
  }

  public Map<String,String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String,String> properties) {
    this.properties = properties;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public Tab duplicate() {
    Tab clone = new Tab();
    clone.text = text;
    clone.properties = new HashMap<String, String>(properties);

    if(fields != null) {
      clone.fields = new ArrayList<Field>();
      for(Field field : fields)
        clone.fields.add(field.duplicate());
    }

    return clone;
  }

}
