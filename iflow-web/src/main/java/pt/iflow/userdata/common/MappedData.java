/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
