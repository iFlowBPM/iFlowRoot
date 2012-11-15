package pt.iflow.api.blocks.form;

import java.util.ArrayList;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Form {
  @Value
  private List<Tab> tabs;
  @Value
  private FormProperties properties;

  public Form() {
  }

  public List<Tab> getTabs() {
    return tabs;
  }

  public void setTabs(List<Tab> tabs) {
    this.tabs = tabs;
  }

  public FormProperties getProperties() {
    return properties;
  }

  public void setProperties(FormProperties properties) {
    this.properties = properties;
  }

  public Form duplicate() throws CloneNotSupportedException {
    Form clone = new Form();
    clone.properties = properties.duplicate();
    clone.tabs = new ArrayList<Tab>();
    for(Tab tab : tabs)
      clone.tabs.add(tab.duplicate());
    return clone;
  }

}
