package pt.iflow.api.blocks.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Field {
  @Value
  private String id;
  @Value
  private String type;
  @Value(name="values")
  private Map<String,String> properties;
  @Value(optional=true)
  private List<Field> fields;
  @Value(optional=true)
  private String variable;

  public Field() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getVariable() {
    return variable;
  }

  public void setVariable(String name) {
    this.variable = name;
  }

  public Field duplicate() {
    Field clone = new Field();
    clone.id = id;
    clone.type = type;
    clone.variable = variable;
    clone.properties = new HashMap<String, String>(properties);
    if(fields != null) {
      clone.fields = new ArrayList<Field>();
      for(Field field : fields)
        clone.fields.add(field.duplicate());
    }
    return clone;
  }


}
