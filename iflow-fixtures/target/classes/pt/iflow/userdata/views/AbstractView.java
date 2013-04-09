package pt.iflow.userdata.views;

import java.util.Map;

import pt.iflow.api.userdata.views.IView;


public abstract class AbstractView implements IView {

  private Map<String,String> _mapping;

  public AbstractView(Map<String,String> mapping) {
    this._mapping = mapping;
  }
  
  public String get(String fieldName) {
    if(_mapping == null) return "";
    String val = _mapping.get(fieldName);
    return (val == null?"":val);
  }

}
