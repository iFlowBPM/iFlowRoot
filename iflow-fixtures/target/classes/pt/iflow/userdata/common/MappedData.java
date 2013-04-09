package pt.iflow.userdata.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import pt.iflow.api.userdata.IMappedData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Setup;

public abstract class MappedData implements IMappedData,Serializable {
  private static final long serialVersionUID = -1226524284657281461L;
  private Map<String,String> _data;
  private Map<String,String> _mapping;
  
  public MappedData(Map<String,String> data, Map<String,String> map) {
    this._data = new Hashtable<String, String>();
    Iterator<String> keys = data.keySet().iterator();
    while(keys.hasNext()) {
      String key = keys.next();
      String value = data.get(key);

      this._data.put(key.toLowerCase(),value);
    }
    this._mapping = map;

  }

  public String get(String paramName) {
    String retObj = "";
    String mappedName = "";
    
    if(this._mapping == null) {
      mappedName = paramName;
    } else {
      mappedName = _mapping.get(paramName);
      if(mappedName == null || mappedName.trim().equals("")) {
        mappedName = paramName;
      }
    }
    
    retObj = this._data.get(mappedName.toLowerCase());
    if(null == retObj) retObj = "";  // TODO mudar...
    
    return retObj;
  }

  public Map<String,String> getData() {
    return this._data;
  }

  public String toString() {
    final String lineSep = System.getProperty("line.separator");
    StringBuilder result = new StringBuilder(lineSep).append(getClass().getSimpleName()).append(lineSep);
    Set<String> keys = new TreeSet<String>();
    if(this._mapping == null) keys.addAll(this._data.keySet());
    else {
      keys.addAll(this._mapping.keySet());
      for(String k : this._data.keySet())
        keys.add(k);
    }

    Iterator<String> iter = keys.iterator();

    while(iter.hasNext()){
      boolean isMapped = true;
      String key = iter.next();
      String k=null;
      if(this._mapping != null)
        k = this._mapping.get(key);

      if(null == k) {
        k = key;
        isMapped = false;
      }
      String value = this._data.get(k.toLowerCase());
      result.append("'").append(key);
      if(isMapped) {
        result.append("' => '").append(k);
      }
      result.append("' = '").append(value).append("'").append(lineSep);
    }
    
    return result.toString();
  }

  public static Properties cleanExtras(Properties parameters) {
    if (parameters == null) return parameters;
    Properties newparameters = new Properties();
    for (Object key : parameters.keySet()) {
      String newKey = key.toString().startsWith(Const.sEXTRA_PROP) ? key.toString().replaceFirst(Const.sEXTRA_PROP, "") : key.toString();
      newparameters.put(newKey, parameters.get(key));
    }
    return newparameters;
  }

  public static Properties getExtras(Properties parameters) {
    if (parameters == null) return parameters;
    Properties extraparameters = null;
    for (Object key : parameters.keySet()) {
      if (extraparameters==null) 
        extraparameters = new Properties();
      if (key.toString().startsWith(Const.sEXTRA_PROP)) {
        String newKey=key.toString().replaceFirst(Const.sEXTRA_PROP, "");
        extraparameters.put(newKey, parameters.get(key));
      }
    }
    return extraparameters;
  }

}
