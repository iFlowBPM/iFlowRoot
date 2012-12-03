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
package pt.iflow.api.processdata;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.Repository;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.processtype.TextDataType;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.BshUtils;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.DataSet;
import pt.iknow.utils.DataSetImpl;
import bsh.EvalError;
import bsh.Interpreter;

public class ProcessData {

  ProcessCatalogue _catalogue;

  ProcessHeader _header;
  Map<String, ProcessSimpleVariable> _simpleVars;
  Map<String, ProcessListVariable> _listVars;
  Map<String, ProcessSimpleVariable> _bindableVars;

  Map<String,String> _simpleVarsOrigRawVal;
  Map<String,ListVarOrigValues> _listVarsOrigRawVal;
  InternalValue _errorOrigRawVal;

  Map<String,String> _tempData;
  Map<String,String> _appData;

  boolean _readOnlyMode = false;
  boolean _inDB = false;
  boolean _appDataChanged = false;
  InternalValue _error = null;

  private boolean _hasChanged = false;
  
  private boolean isOnPopup = false;

  private boolean isPopupOpened = false;
  private boolean openPopup = false;
  private boolean closePopup = false;

  /*
   * Default constructor
   */
  public ProcessData(ProcessCatalogue catalogue) {
    this(catalogue, new ProcessHeader(-1, Const.nSESSION_PID, Const.nSESSION_SUBPID));
  }

  /*
   * Copy constructor
   */
  public ProcessData(ProcessData procData) {
    this(procData._catalogue, new ProcessHeader(procData._header));

    for (ProcessSimpleVariable svar : procData.getSimpleVariables()) {
      if (svar.isBindable()) {
        continue;
      }
      set(svar, false);
      setOrigRawValue(svar.getName(), procData.getOrigRawValue(svar.getName()));
    }

    for (ProcessListVariable lvar : procData.getListVariables()) {
      if (lvar.isBindable()) {
        continue;
      }
      setList(lvar, false);
      setListOrigRawValue(lvar.getName(), procData.getListOrigRawValues(lvar.getName()));
    }

    Iterator<String> itTemp = procData._tempData.keySet().iterator();
    while(itTemp.hasNext()) {
      String name = itTemp.next();      
      setTempData(name, procData.getTempData(name));
    }
    
    Iterator<String> itApp = procData._appData.keySet().iterator();
    while (itApp.hasNext()) {
      String name = itApp.next();
      setAppData(name, procData.getAppData(name), false);
    }
    
    setErrorOrigRawValue(procData.getErrorOrigRawValue());

    _readOnlyMode = procData.isReadOnly();
    _inDB = procData.isInDB();
    _appDataChanged = procData._appDataChanged;
    _hasChanged = procData.hasChanged();

    _hasChanged = procData.hasChanged();
    isPopupOpened =  procData.isPopupOpened;
    this.setOnPopup(procData.isOnPopup());
  }

  public ProcessData(ProcessCatalogue catalogue, int flowid, int pid, int subpid) {
    this(catalogue, new ProcessHeader(flowid, pid, subpid));
  }

  public ProcessData(ProcessCatalogue catalogue, ProcessHeader header) {		
    _catalogue = catalogue;
    _header = header;

    initData();

    _error = new InternalValue(new TextDataType());
    _errorOrigRawVal = null;
    
  }

  void initData() {
    _simpleVars = new TreeMap<String, ProcessSimpleVariable>();
    _listVars = new TreeMap<String, ProcessListVariable>();

    initOrigValues();

    // initialize variables from catalogue
    for (String var : _catalogue.getSimpleVariableNames())
      set(new ProcessSimpleVariable(_catalogue.getDataType(var), var), false);
    for (String var : _catalogue.getListVariableNames())
      setList(new ProcessListVariable(_catalogue.getDataType(var), var), false);

    BindDelegate bindDelegate = new DynamicBindDelegate(this);
    for(String var : _catalogue.getBindableVariableNames())
      setBindableVariable(new BindableProcessVariable(_catalogue.getDataType(var), var, bindDelegate));
//      setBindableVariable(new BindableProcessVariable(_catalogue.getDataType(var), var));

    _tempData = new Hashtable<String, String>();
    _appData = new Hashtable<String, String>();
  }

  void initOrigValues() {
    _simpleVarsOrigRawVal = new HashMap<String, String>();
    _listVarsOrigRawVal = new HashMap<String, ListVarOrigValues>();
  }

  String getOrigRawValue(String var) {
    return _simpleVarsOrigRawVal.containsKey(var) ? _simpleVarsOrigRawVal.get(var) : null;
  }

  void setOrigRawValue(String var, String origrawvalue) {
    if (!_simpleVarsOrigRawVal.containsKey(var))
      _simpleVarsOrigRawVal.put(var, origrawvalue);
  }

  Collection<String> getListOrigRawValues(String var) {
    ListVarOrigValues ov = _listVarsOrigRawVal.containsKey(var) ? _listVarsOrigRawVal.get(var) : null;
    return ov != null ? ov.values() : null;
  }

  void setListOrigRawValue(ProcessListVariable var) {
    if (!_listVarsOrigRawVal.containsKey(var.getName())) {
      Collection<String> values = new ArrayList<String>();
      for (int i=0; i < var.size(); i++) {
        ProcessListItem item = var.getItem(i);
        values.add(item != null ? item.getRawValue() : null);
      }
      setListOrigRawValue(var.getName(), values);
    }
  }

  void setListOrigRawValue(String var, Collection<String> origrawvalues) {
    if (!_listVarsOrigRawVal.containsKey(var)) {
      _listVarsOrigRawVal.put(var, new ListVarOrigValues(origrawvalues));
    }
  }

  void setErrorOrigRawValue(String origrawvalue) {
    if (_errorOrigRawVal == null) {
      _errorOrigRawVal = new InternalValue(new TextDataType());
      _errorOrigRawVal.setValue(origrawvalue);
    }
  }

  String getErrorOrigRawValue() {
    return _errorOrigRawVal != null ? _errorOrigRawVal.getRawValue() : null;
  }


  public ProcessHeader getProcessHeader() {
    return _header;
  }

  public String getSignature() {
    return _header.getSignature();
  }
  
  public boolean isReadOnly() {
    return _readOnlyMode;
  }

  public boolean isInDB() {
    return _inDB;
  }

  public void setInDB(boolean inDB) {
    _inDB = inDB;
  }
  
  public void setReadOnly(boolean readonly) {
    _readOnlyMode = readonly;
  }

  public int getFlowId() {
    return _header.getFlowId();
  }

  public int getPid() {
    return _header.getPid();
  }

  public int getSubPid() {
    return _header.getSubPid();
  }

  public String getPNumber() {
    return _header.getPNumber();
  }

  public int getMid() { return _header.getMid(); }
  public void setMid(int mid) {
    _header.setMid(mid);
  }
  public boolean hasMid() { return _header.hasMid(); }
  
  public void setFlowId(int flowid) {
    assertReadOnly();
    _header.setFlowId(flowid);
  }

  public void setPid(int pid) {
    assertReadOnly();
    _header.setPid(pid);
  }

  public void setPNumber(String pnumber) {
    assertReadOnly();
    _header.setPNumber(pnumber);
  }

  public void setSubPid(int subpid) {
    assertReadOnly();
    _header.setSubPid(subpid);
  }

  public String getCreator() {
    return _header.getCreator();
  }

  public void setCreator(String creator) {
    assertReadOnly();
    _header.setCreator(creator);
  }

  public Date getCreationDate() {
    return _header.getCreationDate();
  }

  public void setCreationDate(Date date) {
    assertReadOnly();
    _header.setCreationDate(date);
  }

  public String getCurrentUser() {
    return _header.getCurrentUser();
  }

  public void setCurrentUser(String user) {
    assertReadOnly();
    _header.setCurrentUser(user);
  }

  public Date getLastUpdate() {
    return _header.getLastUpdate();
  }

  public void setLastUpdate(Date date) {
    assertReadOnly();
    _header.setLastUpdate(date);
  }

  public boolean isClosed() {
    return _header.isClosed();
  }

  public void setClosed(boolean closed) {
    assertReadOnly();
    _header.setClosed(closed);
  }

  public ProcessCatalogue getCatalogue() {
    return _catalogue;
  }

  public ProcessDataType getVariableDataType(String name) {
    return _catalogue.getDataType(name);
  }
  
  /**
   * Store given report in tail position of cached map. The key taken for the
   * cached map is the report's code, which should be unique per process.
   * 
   * @see #getCachedReports()
   * @see #removeReport(ReportTO)
   * @see #removeReport(String)
   * @param report
   *          Report to be stored.
   */
  public void storeReport(ReportTO report) {
    this._header.storeReport(report);
    
  }

  /**
   * Removes given report from cache. The key taken for the cached map is the
   * report's code, which should be unique per process.
   * 
   * @see #removeReport(String)
   * @see #storeReport(ReportTO)
   * @see #getCachedReports()
   * @param report
   *          Report to be removed.
   * @return Removed report.
   */
  public ReportTO removeReport(ReportTO report) {
    return this.removeReport(report.getCodReporting());
  }

  /**
   * Removes report with the given code from cache.
   * 
   * @see #removeReport(ReportTO)
   * @see #storeReport(ReportTO)
   * @see #getCachedReports()
   * @param code
   *          Report's code.
   * @return Removed report.
   */
  public ReportTO removeReport(String code) {
    return this._header.removeReport(code);
  }
  
  /**
   * Retrieves list of cached reports.
   * 
   * @see #storeReport(ReportTO)
   * @see #removeReport(ReportTO)
   * @see #removeReport(String)
   * @return Cached reports.
   */
  public Map<String, ReportTO> getCachedReports() {
    return this._header.getCachedReports();
  }

  /**
   * 
   * @param variable
   * @return previous variable or null if no previous variable present
   */
  private ProcessSimpleVariable setBindableVariable(ProcessSimpleVariable variable) {
    // allways set and ignore
    ProcessSimpleVariable ret = _simpleVars.put(variable.getName(), variable);
    return ret;
  }

  /**
   * 
   * @param variable
   * @return previous variable or null if no previous variable present
   */
  public ProcessSimpleVariable set(ProcessSimpleVariable variable) {
    return set(variable, true);
  }
  
  public ProcessSimpleVariable set(ProcessSimpleVariable variable, boolean checkChanges) {
    if (variable != null && variable.getType() != null) {
      if (variable.getType().isBindable()) {
        throw new IllegalArgumentException("Cannot set bindable variables");
      }
      assertReadOnly();
      setOrigRawValue(variable.getName(), variable.getRawValue());
    }
    ProcessSimpleVariable ret = _simpleVars.put(variable.getName(), variable);
    if (checkChanges)
      _hasChanged = _hasChanged || (((variable!=null && variable.getValue()!=null) || (ret!=null && ret.getValue()!=null)) && 
                                    (variable==null || variable.getValue()==null || ret==null || ret.getValue()==null || !variable.equals(ret)));
    return ret;
  }

  /**
   * 
   * @param name variable name
   * @param value variable value
   * @return previous variable or null if no previous variable present
   */
  public ProcessSimpleVariable set(String name, Object value) {
    ProcessSimpleVariable var = get(name);
    if (null == var)
      return null;
    _hasChanged = _hasChanged || ((var.getValue()!=null || value!=null) && 
                                  (var.getValue()==null || value==null || !value.equals(var.getValue())));
    var.setValue(value);
    return set(var);
  }

  public ProcessSimpleVariable copyFrom(ProcessSimpleVariable variable) {
    ProcessSimpleVariable newVar = new ProcessSimpleVariable(variable);
    return set(newVar);
  }

  public ProcessListVariable copyFrom(ProcessListVariable variable) {
    ProcessListVariable newVar = new ProcessListVariable(variable);
    return setList(newVar);
  }

  /**
   * 
   * @param name variable name
   * @param value value to be parsed
   * @return previous variable or null if no previous variable present
   */
  public ProcessSimpleVariable parseAndSet(String name, String value) throws ParseException {
    return parseAndSet(name, value, null);		
  }

  /**
   * 
   * @param name variable name
   * @param value value to be parsed
   * @return previous variable or null if no previous variable present
   */
  public ProcessSimpleVariable parseAndSet(String name, String value, ProcessDataType formatter) throws ParseException {
    ProcessSimpleVariable var = get(name);
    if(var == null) {
      var = new ProcessSimpleVariable(getVariableDataType(name), name);
    }
    if(formatter == null) {
      var.parse(value);
    } else {
      var.parse(value, formatter);
    }
    return set(var);        
  }

  public ProcessListVariable setList(ProcessListVariable variable) {
    return setList(variable, true);
  }

  public ProcessListVariable setList(ProcessListVariable variable, boolean checkChanges) {
    assertReadOnly();
    setListOrigRawValue(variable);
    ProcessListVariable ret = _listVars.put(variable.getName(), variable);
    
    if(variable.getType().toString() == "Document"){
    	for (int i = 0; i < variable.size(); i++){
	    	try{
	    		int teste = Integer.parseInt(variable.getFormattedItem(i));
	    	}catch(Exception e){
	    		Logger.warning("", "ProcessData", "setList", "Erro ao converter ID do documento para inteiro! Id "+variable.getItem(0).getValue()+".");
	    		e.printStackTrace();
	    	}
    	}
    }
    
    if (checkChanges)
      _hasChanged = _hasChanged || variableListsChanged(variable, ret);
    return ret;
  }

  public ProcessSimpleVariable get(String name) {
    return _simpleVars.get(name);
  }

  public String getFormatted(String name) {
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    name = name.trim();    
    if (name.contains("[") && name.endsWith("]")) {
      try {
        int listBreak = name.indexOf("[");
        String list = name.substring(0, listBreak);
        String sIndex = name.substring(listBreak + 1, name.length() - 1);
        int index = -1;
        if (NumberUtils.isNumber(sIndex)) {
          index = Integer.parseInt(sIndex);
        }
        else {
          ProcessSimpleVariable row = _simpleVars.get(sIndex);
          if(row != null) {
            index = Integer.parseInt(row.getValue().toString());
          }
        }
        if (index >= 0) {
          return getListItemFormatted(list, index);
        }
        else {
          Logger.warning("", this, "getFormatted", "Found complex variable '" + name + 
              "' but was not able to get a valid index for it");
        }
      } catch (Exception e) {
        Logger.error("", this, "getFormatted", "Found complex variable '" + name + 
            "', but was not able to parse it.", e);
      }
    }
    ProcessSimpleVariable var = _simpleVars.get(name);
    return (var == null ? null : var.format());
  }

  public ProcessListVariable getList(String name) {
    return _listVars.get(name);
  }

  public ProcessListItem getListItem(String name, int position) {
    ProcessListVariable var = _listVars.get(name);
    return var == null ? null : var.getItem(position);
  }


  public String getListItemFormatted(String name, int position) {
    ProcessListItem item = getListItem(name, position);
    return item == null ? null : item.format();
  }

  public ProcessListVariable parseAndSetListItem(String name, String value, int position) throws ParseException {
    ProcessListVariable var = getList(name);
    if(null == var) {
      var = new ProcessListVariable(getVariableDataType(name),name);
    }
    var.parseAndSetItemValue(position, value);
    return setList(var);    
  }


  public Collection<String> getSimpleVariableNames() {
    return Collections.unmodifiableCollection(_simpleVars.keySet());
  }

  public Collection<String> getListVariableNames() {
    return Collections.unmodifiableCollection(_listVars.keySet());
  }

  public Collection<ProcessSimpleVariable> getSimpleVariables() {
    return Collections.unmodifiableCollection(_simpleVars.values());
  }

  public Collection<ProcessListVariable> getListVariables() {
    return Collections.unmodifiableCollection(_listVars.values());
  }

  public boolean isEmpty() {
    return _simpleVars.isEmpty() && _listVars.isEmpty();
  }


  boolean isVarModified(String var) {
    return isVarModified(get(var));
  }

  private boolean isVarModified(ProcessSimpleVariable var) {
    if (var == null)
      return false;

    if (!StringUtils.equals(var.getRawValue(), getOrigRawValue(var.getName()))) {
      return true;
    }
    return false;
  }

  boolean isListVarModified(String var) {
    return isListVarModified(getList(var));
  }

  private boolean isListVarModified(ProcessListVariable var) {
    Collection<String> origValues = getListOrigRawValues(var.getName());
    if(null == origValues) return true;
    if(var.size() != origValues.size()) return true;
    Iterator<String> iterOrigValues = origValues.iterator();
    Iterator<ProcessListItem> iterItems = var.getItemIterator();
    while(iterItems.hasNext() && iterOrigValues.hasNext()) {
      ProcessListItem li = iterItems.next();
      String rawValue = null;
      if(li!=null) rawValue = li.getRawValue();
      String origRawValue = iterOrigValues.next();
      if(!StringUtils.equals(rawValue, origRawValue)) return true;
    }
    return false;
  }

  public boolean isModified() {
    boolean ismod = isModifiedImpl();
    return ismod;
  }

  private boolean isModifiedImpl() {

    if (!_error.equals(_errorOrigRawVal))
      return true;

    if (_header.isModified())
      return true;

    Collection<ProcessSimpleVariable> simpleVars = getSimpleVariables();
    for (ProcessSimpleVariable var : simpleVars) {
      if (isVarModified(var)) {
        return true;
      }
    }

    Collection<ProcessListVariable> listVars = getListVariables();
    for (ProcessListVariable var : listVars) {
      if (isListVarModified(var))
        return true;
    }

    return _appDataChanged;
  }

  public void resetModified() {
    _hasChanged = false;
    _appDataChanged = false;
    _errorOrigRawVal = _error;
    _header.resetModified();

    initOrigValues();

    for (ProcessSimpleVariable svar : getSimpleVariables()) {
      setOrigRawValue(svar.getName(), svar.getRawValue());
    }

    for (ProcessListVariable lvar : getListVariables()) {
      setListOrigRawValue(lvar);
    }
  }

  public void clear(String name) {
    ProcessSimpleVariable sv = get(name);
    if (sv != null)
      sv.clear();
  }

  public void clearList(String name) {
    ProcessListVariable lv =getList(name);
    if (lv != null)
      lv.clear();
  }

  public void clearData() {
    initData();
  }

  public boolean isListVar(String name) {
    return _catalogue.isList(name);
  }

  public void setTempData(String name, String value) {
    if (value == null)
      _tempData.remove(name);
    else
      _tempData.put(name, value);
  }

  public String getTempData(String name) {
    return _tempData.get(name);
  }

  public void setAppData(String name, String value) {
    setAppData(name, value, true);
  }

  public void setAppData(String name, String value, boolean checkChanges) {
    String oldValue = null;
    if (value == null)
      oldValue = _appData.remove(name);
    else
      oldValue = _appData.put(name, value);

    _appDataChanged = _appDataChanged || StringUtils.equals(value, oldValue);
    if (checkChanges)
      _hasChanged = _hasChanged || !StringUtils.equals(value, oldValue);
  }

  public String getAppData(String name) {
    return _appData.get(name);
  }

  public boolean hasError() {
    return _error != null && _error.getValue() != null;
  }

  public String getError() {
    return _error.format();
  }

  public void clearError() {
    _error.setValue(null);
  }

  public void setError(String error) {
    _error.setValue(error);
  }

  void initError(String error) {
    _error = new InternalValue(new TextDataType(), error);
  }

  public String toString() {
    return toString(false,false);
  }

  public String toString(boolean showVarContent) {
    return toString(showVarContent, false);
  }

  public String toDebugString() {
    return toString(true, true);
  }

  private String toString(boolean showVarContent, boolean debug) {
    if (_header == null)
      return "";

    StringBuilder sb = new StringBuilder();  

    sb.append("Process: ").append(_header.toString()); 

    if (hasError())
      sb.append("(erro: ").append(getError()).append(")"); 

    if (showVarContent) {
      sb.append("\n");
      sb.append("Simple Vars\n");
      if (_simpleVars.isEmpty())
        sb.append("\tEmpty\n");
      else {
        Iterator<String> it = _simpleVars.keySet().iterator();
        while(it.hasNext()) {
          ProcessSimpleVariable psv = _simpleVars.get(it.next());
          sb.append("\t");
          if (debug)
            sb.append(psv.toDebugString());
          else
            sb.append(psv.toString());
          sb.append("\n");
        }
      }			

      sb.append("List Vars\n");
      if (_listVars.isEmpty())
        sb.append("\tEmpty\n");
      else {
        Iterator<String> it = _listVars.keySet().iterator();
        while(it.hasNext()) {
          ProcessListVariable plv = _listVars.get(it.next());
          if (debug)
            sb.append(plv.toDebugString());
          else
            sb.append(plv.toString());
          sb.append("\n");
        }
      }
    }
    else {
      sb.append(" [").append(_simpleVars.size()).append(" simpleVars]");			
      sb.append(" [").append(_listVars.size()).append(" listVars]");				
    }

    return sb.toString();
  }

  // TODO: se for para continuar a usar o dataset, verificar se é mais eficiente 
  // guardar uma instancia do dataset localmente para nao fazer a construcao
  // em cada chamada!!
  /**
   * Created for historical reasons...
   * 
   * @deprecated Dont use datasets! ProcessData is the way to go!
   */
  @Deprecated
	public DataSetImpl getDataSetx() {
		DataSetImpl dataset = new DataSetImpl();

		Collection<ProcessSimpleVariable> simplevars = getSimpleVariables();
		for (ProcessSimpleVariable var : simplevars) {
			dataset.setString(var.getName(), var.format());
		}

		Collection<ProcessListVariable> listvars = getListVariables();
		for (ProcessListVariable listvar : listvars) {
			for (int i=0; i < listvar.size(); i++) {
				dataset.setString(listvar.getName(), listvar.getItem(i).format(), i);
			}
		}

		for (String tempKey : _tempData.keySet()) {
			dataset.setTempData(tempKey, getTempData(tempKey));
		}

		dataset.setError(getError());

		return dataset;
	}

  /**
   * Created for historical reasons...
   * 
   * @deprecated Dont use datasets! ProcessData is the way to go!
   */
	@Deprecated
  public DataSet getDataSet() {
	  Logger.debug("admin", this, "getDataSet", "Dataset requested", new Exception());
	  new Exception().printStackTrace();
    return new ProcessDataSet(this);
  }

/**
   * Created for historical reasons...
   * 
   * @deprecated Dont use datasets! ProcessData is the way to go!
   */
 @Deprecated
	public void setDataSet(DataSet dataset) {

		  ProcessSimpleVariable var = null;
		  ProcessListVariable listvar = null;

		  SimpleDateFormat dateFormatter = new SimpleDateFormat(DataSetVariables.sPROCESS_DATE_FORMAT);
		  var = get(DataSetVariables.PROCESS_CREATION_DATE);
		  var.setValue(dateFormatter.format(getCreationDate()));
		  set(var);

		  Iterator<?> keys = dataset.getStringKeys();
		  while (keys != null && keys.hasNext()) {
			  String key = (String)keys.next();
			  if (DataSetVariables.PROCESS_CREATION_DATE.equals(key)) {
				  continue;
			  }

			  if (dataset.isArray(key)) {
				  String[] values = dataset.getStringArray(key);

				  String unlisted = Utils.unlistVarName(key);
				  listvar = getList(unlisted);

				  for (int i=0; i < values.length; i++) {
					  listvar.setItemValue(i, values[i]);
				  }

				  setList(listvar);				  
			  }
			  else {
				  var = get(key);
				  var.setValue(dataset.getString(key));
				  set(var);				  
			  }
		  }

		  keys = dataset.getDoubleKeys();
		  while (keys != null && keys.hasNext()) {
			  String key = (String)keys.next();

			  if (dataset.isArray(key)) {
				  double[] values = dataset.getDoubleArray(key);

				  String unlisted = Utils.unlistVarName(key);
				  listvar = getList(unlisted);

				  for (int i=0; i < values.length; i++) {
					  listvar.setItemValue(i, values[i]);
				  }

				  setList(listvar);

			  }
			  else {
				  var = get(key);
				  var.setValue(dataset.getDouble(key));
				  set(var);
			  }
		  }

		  setError(dataset.getError());

		  Iterator<?> tempKeys = dataset.getTempDataKeys();
		  while (tempKeys != null && tempKeys.hasNext()) {
			  String tempKey = (String)tempKeys.next();
			  setTempData(tempKey, dataset.getTempData(tempKey));
		  }

	}

  /**
   * Asserção para verificar se o processo está em modo readonly
   * 
   */
  private void assertReadOnly() {
    // TODO customize exceptions!!!
    if (isReadOnly())
      throw new AssertionError("Process is readonly.");
  }

  public void importDataFrom(ProcessData procData) {
    _catalogue = procData._catalogue;
    for (ProcessSimpleVariable svar : procData.getSimpleVariables()) 
      copyFrom(svar);

    for (ProcessListVariable lvar : procData.getListVariables()) 
      copyFrom(lvar);
  }

  private static class ListVarOrigValues {
    Collection<String> _origvalues;

    @SuppressWarnings("unused")
    ListVarOrigValues() {
      _origvalues = new ArrayList<String>();
    }

    ListVarOrigValues(Collection<String> values) {
      _origvalues = values;
    }

    int size() {
      return _origvalues.size();
    }

    Collection<String> values() {
      return _origvalues;
    }

    @SuppressWarnings("unused")
    void ensureSize(int position) {
      if (size() <= position) {
        for (int i=size(); i <= position; i++) {
          _origvalues.add(null);
        }
      }
    }
  }

  /**
   * Get an Interpreter instance. This will always be a new instance.
   * @return
   */
  public synchronized Interpreter getInterpreter(UserInfoInterface userInfo) {
    return getInterpreter(userInfo, 0);
  }

  /**
   * Get an Interpreter instance. This will always be a new instance.
   * @param mode Set NameSpace mode.
   * @return
   */
  public Interpreter getInterpreter(UserInfoInterface userInfo, int mode) {
    return internalGetInterpreter(userInfo, mode, false);
  }
  
    
  private Interpreter internalGetInterpreter(UserInfoInterface userInfo, int mode, boolean forDB) {
    Interpreter bsh = new Interpreter();
    bsh.getClassManager().cacheClassInfo("BshUtils", BshUtils.class);
    try {
      Repository rep = BeanFactory.getRepBean();
      String clazz = "pt.iflow.bsh.BshUtils";
      if (rep.getClassFile(userInfo.getOrganization(), clazz) != null) {
        Class<?> orgBshUtilsClass = rep.loadClass(userInfo, clazz);
        bsh.getClassManager().cacheClassInfo("MyBshUtils", orgBshUtilsClass);
      }
    } catch (Exception e) {
      // ignore
    } 
    ProcessDataNameSpace nameSpace = new ProcessDataNameSpace(userInfo, bsh, this, forDB);
    nameSpace.setProcessDataMode(mode);
    bsh.setNameSpace(nameSpace);

    // bind userInfo and procData
    try {
      bsh.set("userInfo", userInfo);
      bsh.set("procData", this);
    } catch (EvalError e) {
      Logger.error(userInfo.getUtilizador(), this, "internalGetInterpreter", 
          this.getSignature() + "unable to set userinfo and procdata", e);
    }

    return bsh;
  }

  /**
   * Return a possibly pooled Interpreter and finalize/update process data 
   * @param bsh
   */
  public void returnInterpreter(Interpreter bsh) {
    ProcessDataNameSpace nameSpace = (ProcessDataNameSpace) bsh.getNameSpace();
    if(nameSpace != null) {
      nameSpace.saveToProcess();
      nameSpace.setProcessDataMode(0);
    }
  }
  
  public synchronized Object eval(UserInfoInterface userInfo, String expression) throws EvalException {
    return internalEval(userInfo, expression, false);
  }
  
  /**
   * @deprecated
   */
  private synchronized Object evalForDB(UserInfoInterface userInfo, String expression) throws EvalException {
    return internalEval(userInfo, expression, true);
  }
  
  private synchronized Object internalEval(UserInfoInterface userInfo, String expression, boolean forDB) throws EvalException {
    Object result = null;
    if (StringUtils.isEmpty(expression))
      return result;
    
    Interpreter interpreter = null;
    try {
      interpreter = internalGetInterpreter(userInfo, 0, forDB);
      result = interpreter.eval(expression);
    } catch (Throwable t) {
      throw new EvalException(t);
    } finally {
      returnInterpreter(interpreter);
    }
    return result;
  }

  public synchronized Object evalAndUpdate(UserInfoInterface userInfo, String expression) throws EvalException {
    Object result = null;
    if (StringUtils.isEmpty(expression))
      return result;
    
    Interpreter interpreter = null;
    try {
      interpreter = getInterpreter(userInfo, 1);
      result = interpreter.eval(expression);
    } catch (Throwable t) {
      throw new EvalException(t);
    } finally {
      // unbind userInfo and procData
      if(null != interpreter) {
        try {
          interpreter.set("userInfo",null);
        } catch (EvalError e) {
        }
        try {
          interpreter.set("procData",null);
        } catch (EvalError e) {
        }
      }
      returnInterpreter(interpreter);
    }
    return result;
  }

  public boolean query(UserInfoInterface userInfo, String expression) throws EvalException {
    boolean result = false;
    if (StringUtils.isEmpty(expression))
      return result;
    
    try {
      result = ((Boolean)eval(userInfo, expression)).booleanValue();
    } catch (EvalException e) {
      throw e;
    } catch(Throwable t) {
      throw new EvalException(t);
    }
    return result;
  }

  public String transform(UserInfoInterface userInfo, String expression) throws EvalException {
    if (StringUtils.isEmpty(expression))
      return expression;
    
    String result = null;
    try {
      Object obj = eval(userInfo, expression);
      if (obj instanceof Date && obj!=null) {
        SimpleDateFormat format = new SimpleDateFormat("'\"'dd/MM/yyyy'\"'");
        result = format.format(obj);
      } else {
        result = obj.toString();
      }
    } catch (EvalException e) {
      throw e;
    } catch(Throwable t) {
      throw new EvalException(t);
    }
    return result;
  }

  public String transform(UserInfoInterface userInfo, String expression, boolean parsing) throws EvalException {
    if (StringUtils.isEmpty(expression))
      return expression;

    String result = null;
    try {
      // TODO tirar comentário quando o parse funcionar bem, ver a nova sintax de blocos SQL
      // if (parsing)
      // expression = parseString(expression);
      Object obj = eval(userInfo, expression);

      if (obj instanceof Date && obj != null) {
        SimpleDateFormat format = new SimpleDateFormat("'\"'dd/MM/yyyy'\"'");
        result = format.format(obj);
      } else {
        result = obj.toString();
      }
    } catch (EvalException e) {
      throw e;
    } catch (Throwable t) {
      throw new EvalException(t);
    }
    return result;
  }

  /**
   * @deprecated temporary function: implement correctly in callers
   */
  public String transformForDB(UserInfoInterface userInfo, String expression) throws EvalException {
    if (StringUtils.isEmpty(expression))
      return expression;
    
    String result = null;
    try {
      result = evalForDB(userInfo, expression).toString();
    } catch (EvalException e) {
      throw e;
    } catch(Throwable t) {
      throw new EvalException(t);
    }
    return result;
  }

  public boolean hasChanged() {
    return _hasChanged;
  }

  private boolean variableListsChanged(ProcessListVariable variable, ProcessListVariable variableAux) {
    if ((variable == null && variableAux != null) || (variable != null && variableAux == null))
      return true;
    if (variable != null) {
      if (variableAux.size() != variable.size())
        return true;
      else {
        for (int i = 0; i < variable.size(); i++) {
          //if (!variable.getItem(i).getValue().equals(variableAux.getItem(i).getValue()))
          if ((variable.getItem(i) == null && variableAux.getItem(i)!=null) ||
              (variable.getItem(i) != null && variableAux.getItem(i)==null) ||
              (variable.getItem(i) != null && !variable.getItem(i).getValue().equals(variableAux.getItem(i).getValue())))
            return true;
        }
      }
    }
    return false;
  }

  public Set<File> addDocuments(UserInfoInterface userInfo, String docsVarName, List<File> files) {
    Set<File> errorFiles = new HashSet<File>();
    if (getCatalogue().hasVar(docsVarName)) {
      ProcessListVariable docsVar = getList(docsVarName);
      Documents docBean = BeanFactory.getDocumentsBean();
      String user = userInfo.getUtilizador();
      for (File file : files) {
        try {
          Document doc = new DocumentData(file.getName(), FileUtils.readFileToByteArray(file));
          doc = docBean.addDocument(userInfo, this, doc);
          Logger.debug(user, this, "addDocuments", 
              getSignature() + "file ("
              + doc.getFileName() + ") for var " + docsVarName + " added.");

          // now update process
          docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
        } 
        catch (Exception e) {
          Logger.error(user, this, "addDocuments", getSignature() + "error adding file document for file " + file.getAbsolutePath(), e);
          errorFiles.add(file);
        }
      }
    }
    return errorFiles;
  }

  public boolean isOnPopup() {
    return isOnPopup;
  }

  public void setOnPopup(boolean isOnPopup) {
    this.openPopup = (!this.isPopupOpened && isOnPopup);
    this.closePopup = (this.isPopupOpened && !isOnPopup);
    this.isOnPopup = isOnPopup;
  }

  public boolean MustOpenPopup() {
    return this.openPopup;
  }

  public boolean MustClosePopup() {
    return this.closePopup;
  }

  public void setPopupOpened(boolean isPopupOpened) {
    this.isPopupOpened = isPopupOpened;
  }
  
  /**
   * Reset evaluator internals. Use this method to propagate process changes to internal evaluator.
   */
  /*
  public synchronized void resetEvaluator() {
    if(null != nameSpace) nameSpace.clear();
  }
  */

  private boolean isInProhibited(String stringTest) {
    String sProhibited = ""; // example "+,";
    // this chars cannot be into stringTest
    char[] cProhited = sProhibited.toCharArray();

    for (int i = 0; i < cProhited.length; i++) {

      if (stringTest.indexOf(cProhited[i]) != -1) {
        return true;
      }
    }
    return false;
  }

  private boolean oldVersion(String stringTest) {
    boolean oVersion = true;
    int pos = 0;
    // Counts spaces into stringTest
    int spaces = stringTest.length() - stringTest.replaceAll(" ", "").length();
    // Delete spaces , Trim on stringTest
    stringTest = stringTest.replaceAll(" ", ""); // stringTest.trim();
    // Counts double quotes (") into stringTest
    int d_quote = stringTest.length() - stringTest.replaceAll("\"", "").length();
    // double quotes (") must be pairs
    if (d_quote % 2 != 0)
      return false;
    // if d_quote is 0 ...
    if (d_quote == 0) {
      if (stringTest.indexOf("${") == -1) { // Hasn't "${"
        if ((spaces == 0) && (!getCatalogue().hasVar(stringTest))) {
          // It's a word alone (spaces == 0), that isn't into catalogue, so it isn't oldVersion
          return false;
        } else if (spaces != 0) {
          return false;
        }
      } else { // if d_quote is 0 and appears ${ is new version
        return false;
      }
    }

    // Store position of double quotes (")
    int[] pos_d_quote = new int[d_quote];
    for (int i = 0; i < pos_d_quote.length; i++) {
      pos_d_quote[i] = stringTest.indexOf('"', pos);
      pos = pos_d_quote[i] + 1;
    }
    // If index (i) is odd, (") must have (+) at right, except last
    // character
    // If index (i) is pair, (") must have (+) at left, except first
    // character
    for (int i = 0; i < pos_d_quote.length; i++) {
      if (i % 2 == 0) { // par
        if ((pos_d_quote[i] != 0) && (!stringTest.substring(pos_d_quote[i] - 1, pos_d_quote[i] + 1).equals("+\""))) {
          return false;
        }
      } else { // odd
        if ((pos_d_quote[i] != stringTest.length() - 1)
            && (!stringTest.substring(pos_d_quote[i], pos_d_quote[i] + 2).equals("\"+"))) {
          return false;
        }
      }
    }
    // Test (y), it is out pairs of double quotes: ("x") + y + ...
    // Or: y + ("x") + ...
    if (pos_d_quote.length == 0) {
      // has not double quotes
      // Only search prohibited characters
      if (isInProhibited(stringTest)) {
        return false;
      }
    }
    for (int i = 0; i < pos_d_quote.length; i++) {
      if (i == 0 && pos_d_quote[i] != 0) {
        if (pos_d_quote[i] == 1) {
          // Failure is assured
          return false;
        }
        // Before there is something
        // the first character its left must be ("+")
        if (stringTest.substring(pos_d_quote[i] - 1, pos_d_quote[i]).equals("+")) {
          // Search prohibited characters
          if (isInProhibited(stringTest.substring(0, pos_d_quote[i] - 1))) {
            return false;
          }
        } else {
          return false;
        }
      } else if (i % 2 != 0) {
        if ((i == pos_d_quote.length - 1) && (pos_d_quote[i] != stringTest.length() - 1)) {
          if (pos_d_quote[i] == stringTest.length() - 2) {
            // Failure is assured
            return false;
          }
          // After there is something
          // the first character its right must be ("+")
          if (stringTest.substring(pos_d_quote[i] + 1, pos_d_quote[i] + 2).equals("+")) {
            // Search prohibited characters
            if (isInProhibited(stringTest.substring(pos_d_quote[i] + 2))) {
              return false;
            }
          } else {
            return false;
          }
        } else if (i != pos_d_quote.length - 1) {
          // After there is something
          // the first character its right must be ("+") and the last
          // must be ("+")
          if ((stringTest.substring(pos_d_quote[i] + 1, pos_d_quote[i] + 2).equals("+"))
              && stringTest.substring(pos_d_quote[i + 1] - 1, pos_d_quote[i + 1]).equals("+")) {
            // Search prohibited characters
            if (isInProhibited(stringTest.substring(pos_d_quote[i] + 2, pos_d_quote[i + 1] - 1))) {
              return false;
            }
          } else {
            return false;
          }
        }
      }
    }
    return oVersion;
  }

  private String parseString(String stringTest) throws EvalException {

    StringBuffer sTest = new StringBuffer(stringTest);
    String[] myStrings1;

    if (oldVersion(stringTest)) {
      return stringTest;
    }

    int pos1 = sTest.indexOf("$");
    int pos2 = sTest.indexOf("{", pos1);
    int pos3 = sTest.indexOf("}", pos2);
    boolean continuar = false;

    if (pos1 != -1 && pos2 != -1 && pos3 != -1)
      continuar = true;

    // cerrar espaços entre "$" y "{"
    while (continuar) {
      continuar = false;
      if (sTest.substring(pos1, pos2 + 1).replaceAll(" ", "").equals("${")) {
        int nspaces = sTest.substring(pos1 + 1, pos2).length();
        sTest.replace(pos1 + 1, pos2, "");
        pos2 -= nspaces;
        pos3 -= nspaces;
      }

      if (sTest.indexOf("$", pos3) != -1)
        pos1 = sTest.indexOf("$", pos3);
      else
        break;
      if (sTest.indexOf("{", pos1) != -1)
        pos2 = sTest.indexOf("{", pos1);
      else
        break;
      if (sTest.indexOf("}", pos2) != -1)
        pos3 = sTest.indexOf("}", pos2);
      else
        break;
      if (pos1 != -1 && pos2 != -1 && pos3 != -1)
        continuar = true;
    }

    // detectar erros e tratar as linhas do array
    myStrings1 = sTest.toString().split("\\$\\{");
    stringTest = "";
    for (int i = 0; i < myStrings1.length; i++) {
      String store = myStrings1[i];
      // todas as linhas menos a primeira estão precedidas pelo "${",
      // a primeira não lhe precede "${", então não deveria aparecer "${"
      // em ninguma linha,
      // e "}" apenas una vez menos na primeira
      int pos = store.indexOf('}');
      if (store.indexOf('$') != -1 || store.indexOf('{') != -1) {
        // sobra "$" ou "{"
        String msg = "Was found more than one " + "variable locator \"$\" or " + "initial statement \"{\"";
        EvalException pe = new EvalException(msg);
        throw pe;

      } else {
        if (i == 0) {
          if (!store.equals("")) {
            if (pos != -1) {
              // sobra o encerramento "}"
              String msg = "Was found more than one " + "final statement \"}\"";
              EvalException pe = new EvalException(msg);
              throw pe;
            } else {
              myStrings1[0] = "\"" + myStrings1[0] + "\"";
            }
          }
        } else {

          if (pos == -1) {
            // falta o encerramento "}"
            String msg = "Was not found " + "final statement \"}\"";
            EvalException pe = new EvalException(msg);
            throw pe;
          } else {
            if (store.indexOf('}', pos + 1) != -1) {
              // sobra o encerramento "}"
              String msg = "Was found more than one " + "final statement \"}\"";
              EvalException pe = new EvalException(msg);
              throw pe;
            }
            myStrings1[i] = store.substring(0, pos);
            if (!(i == 1 && myStrings1[0].equals(""))) {
              myStrings1[i] = " + " + myStrings1[i];
            }
            if (!store.substring(pos + 1).equals("")) {
              myStrings1[i] += " + \"" + store.substring(pos + 1) + "\"";
            }
          }
        }
      }
      stringTest += myStrings1[i];
    }
    return stringTest;
  }

  public boolean isTransformable(String stringTest, boolean searchCataloge) {
    if (StringUtils.contains(stringTest, "\"") || StringUtils.contains(stringTest.replace(" ", ""), "${")) {
      return true;
    }
    if (searchCataloge && this.getCatalogue().hasVar(stringTest)) {
      return true;
    }
    return false;
  }
}

