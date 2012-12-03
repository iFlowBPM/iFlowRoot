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
package pt.iflow.api.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.DynamicBindDelegate;
import pt.iflow.api.processtype.BindableDataType;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.processtype.TextDataType;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;

public class ProcessCatalogueImpl implements ProcessCatalogue {

  Map<String,ProcessDataType> _map;
  Set<String> _lists;
  Set<String> _bindables;
  Map<String,String> _publicNames;
  Map<String,String> _defaultValueExpressions;
  Map<String, Integer> _searchables;
  int indexPosition = 0;

  public ProcessCatalogueImpl() {
    _map = new TreeMap<String, ProcessDataType>();
    _lists = new TreeSet<String>();
    _publicNames = new HashMap<String, String>();
    _defaultValueExpressions = new HashMap<String, String>();
    _bindables = new TreeSet<String>();
    _searchables = new HashMap<String, Integer>();

    setupDefaultVars();
  }

  private void setupDefaultVars() {

    String [] dynamicVars = DynamicBindDelegate.getDynamicVariables();

    for(String varName : dynamicVars)
      registerBindableDataType(new BindableDataType(varName));

    // Other vars...
    setDataType(DataSetVariables.PROCESS_STATE, new TextDataType());
    setDataType(DataSetVariables.PROCESS_STATE_DESC, new TextDataType());

    setDataType(DataSetVariables.PROCESS_SAVED, new TextDataType());

  }


  public boolean isList(String var){
    return _lists.contains(var);
  }

  public boolean isBindable(String var){
    return _bindables.contains(var);
  }

  public boolean hasVar(String var) {
    return _map.containsKey(var);
  }

  public ProcessDataType getDataType(String var) {
    return _map.get(var);
  }

  public void registerBindableDataType(BindableDataType type) {
    _bindables.add(type.getVariableName());
    _map.put(type.getVariableName(), type);
  }

  public void setDataType(String var, ProcessDataType type) {
    _map.put(var, type);
  }

  public void setDataType(String var, ProcessDataType type, String publicName, String defaultValue) {
    setDataType(var, type);
    if (StringUtils.isNotEmpty(publicName))
      _publicNames.put(var, publicName);

    // Set default value...
    try {
      _defaultValueExpressions.put(var, defaultValue);
    } catch (NullPointerException e) {
    }

  }


  public void setListDataType(String var, ProcessDataType type) {
    _map.put(var, type);
    _lists.add(var);
  }

  public void setListDataType(String var, ProcessDataType type, String publicName) {
    setListDataType(var, type);
    if (StringUtils.isNotEmpty(publicName))
      _publicNames.put(var, publicName);
  }

  public List<String> getSimpleVariableNames() {
    List<String> ret = new ArrayList<String>();

    Iterator<String> vars = _map.keySet().iterator();
    while(vars.hasNext()) {
      String varname = vars.next();
      boolean ignore = isList(varname) || isBindable(varname); // manter bindables?
      if (ignore)
        continue;
      ret.add(varname);
    }
    return ret;
  }


  public List<String> getListVariableNames() {
    List<String> ret = new ArrayList<String>(_lists);
    return ret;
  }

  public List<String> getBindableVariableNames() {
    List<String> ret = new ArrayList<String>(_bindables);
    return ret;
  }

  public boolean hasPublicName(String var) {
    return _publicNames.containsKey(var);
  }

  public String getPublicName(String var) {
    return hasPublicName(var) ? _publicNames.get(var) : null;
  }

  public String getDisplayName(String var) {
    String pn = getPublicName(var); 
    return pn != null ? pn : var;
  }

  public boolean isSearchable(String var) {
    return _searchables.containsKey(var);
  }

  public Map<String,Integer> getSearchables()  {
    return _searchables;
  }

  public String[] getOrderedSearchableNames() {
    String [] vars = new String [Const.INDEX_COLUMN_COUNT];
    for (String name : _searchables.keySet()) {
      int pos = _searchables.get(name);
      vars[pos]=name;
    }
    return vars;
  }

  public String getDefaultValueExpression(String var) {
    return _defaultValueExpressions.get(var);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    final String lineSep = System.getProperty("line.separator");

    sb.append("Catalogue Variables:").append(lineSep);
    List<String> simpleVars = getSimpleVariableNames();
    for(String varname : simpleVars) {
      ProcessDataType type = _map.get(varname);
      sb.append("\t").append(varname).append(" of type ").append(type).append(lineSep);			
    }
    List<String> listVars = getListVariableNames();
    for(String varname : listVars) {
      ProcessDataType type = _map.get(varname);
      sb.append("\t").append(varname).append(" list of type ").append(type).append(lineSep);			
    }

    return sb.toString();
  }

  public void importDataType(String var, String defaultValue, String type, boolean isSearchable, String publicName) {
    importDataType(var, defaultValue, DataTypeEnum.getDataType(type), isSearchable, publicName, type);
  }

  // import functions... Used as a special mechanism to something...
  public void importDataType(String var, String defaultValue, DataTypeEnum dataTypeEnum, boolean isSearchable, String publicName, String format) {
    ProcessDataType pdt = null;
    try {
      pdt = dataTypeEnum.getTypeClass().newInstance();
    } catch (Exception e) {
      pdt = dataTypeEnum.newDataTypeInstance(format);
    }
    if(dataTypeEnum.isList()) {
      setListDataType(var, pdt, publicName);
    } else {
      setDataType(var, pdt, publicName, defaultValue);
    }

    if (isSearchable && indexPosition < Const.INDEX_COLUMN_COUNT)
      _searchables.put(var, indexPosition++);

  }

  // import functions... Used as a special mechanism to something...
  public void importFlowSetting(int flowid, String name, String description) {
    // Do type mapping and....
    // TODO Verificar se é uma lista ou não
    registerBindableDataType(new BindableDataType(name));
  }
}
