package pt.iflow.api.blocks.form;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Resultado {
  @Value
  private String action;
  @Value(optional=true)
  private Form values;
  
  public String getAction() {
    return action;
  }
  public void setAction(String action) {
    this.action = action;
  }
  public Form getValues() {
    return values;
  }
  public void setValues(Form values) {
    this.values = values;
  }
}
