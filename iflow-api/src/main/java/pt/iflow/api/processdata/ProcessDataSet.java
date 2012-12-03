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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iknow.utils.DataSet;
import pt.iknow.utils.IDataSet;

/**
 * Created for historical reasons...
 * 
 * @author ombl
 * @deprecated Dont use datasets! ProcessData is the way to go!
 */
class ProcessDataSet extends DataSet implements IDataSet {
  private static final long serialVersionUID = -8293805183156124718L;
  private final ProcessData process;
  
  private static class VarIterator implements Iterator<String> {
    private final Iterator<? extends ProcessVariable> items;
    private VarIterator(final List<? extends ProcessVariable> vars) {
      items = vars.iterator();
    }
    public boolean hasNext() {
      return items.hasNext();
    }
    public String next() {
      ProcessVariable item = items.next();
      return item.getName();
    }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class EmptyIterator implements Iterator<String> {
    public boolean hasNext() {
      return false;
    }
    public String next() {
      return null;
    }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  ProcessDataSet(final ProcessData process) {
    if(null == process) throw new IllegalArgumentException("Process can't be null");
    this.process = process;
  }

  @Override
  public boolean appendError(String e) {
    if(StringUtils.isEmpty(e)) return false;
    StringBuilder sb = new StringBuilder();
    if(this.process.hasError())
      sb.append(this.process.getError());
    sb.append("<br>").append(e);
    this.process.setError(e.toString());
    return true;
  }

  @Override
  public boolean fieldChanged(String asKey) {
    return this.process.isVarModified(asKey) || this.process.isListVarModified(asKey);
  }

  @Override
  public Iterator getArrayKeys() {
    return process.getListVariableNames().iterator();
  }

  @Override
  public String getCatalogueVarDescr(String var) {
    return this.process.get(var).getName();
  }

  @Override
  public Map getCatalogueVars() {
    return new HashMap();
  }

  @Override
  public HashSet getConstList() {
    return new HashSet();
  }

  @Override
  public double getCurrencyValue(String key) {
    return getDouble(key);
  }

  @Override
  public double getCurrencyValue(String key, int index) {
    return getDouble(key, index);
  }

  @Override
  public Date getDate(String var) {
    return null;
  }

  @Override
  public double getDouble(String key) {
    double d = Double.NaN;
    try {
      String str = getString(key);
      d = Double.parseDouble(str);
    } catch (Throwable t) {
    }
    return d;
  }

  @Override
  public double getDouble(String key, int index) {
    double d = Double.NaN;
    try {
      String str = getString(key,index);
      d = Double.parseDouble(str);
    } catch (Throwable t) {
    }
    return d;
  }

  @Override
  public double[] getDoubleArray(String key) {
    double [] dlst = null;
    try {
      String [] lst = getStringArray(key);
      dlst = new double[lst.length];
      for(int i = 0; i < lst.length; i++) {
        try {
          dlst[i] = Double.parseDouble(lst[i]);
        } catch (NumberFormatException e){
          dlst[i] = Double.NaN;
        }
      }
        
    } catch (Throwable t){}
    return dlst;
  }

  @Override
  public ArrayList getDoubleChangedKeys() {
    return new ArrayList();
  }

  @Override
  public Iterator getDoubleKeys() {
    return new EmptyIterator();
  }

  @Override
  public String getError() {
    return this.process.getError();
  }

  @Override
  public Iterator getKeys() {
    Collection<String> simple = process.getSimpleVariableNames();
    Collection<String> list = process.getListVariableNames();
    
    List<String> vars = new ArrayList<String>(simple.size()+list.size());
    vars.addAll(simple);
    vars.addAll(list);
    return Collections.unmodifiableList(vars).iterator();
  }

  @Override
  public int getLength(String asKey) {
    int size = -1;
    try {
      size = this.process.getList(asKey).size();
    } catch (Throwable t){}
    return size;
  }

  @Override
  public String getString(String key) {
    String str = "";
    try {
      str = this.process.get(key).format();
    } catch (Throwable t) {
    }
    return str;
  }

  @Override
  public String getString(String key, int index) {
    String str = "";
    try {
      str = this.process.getListItem(key,index).format();
    } catch (Throwable t) {
    }
    return str;
  }

  @Override
  public String[] getStringArray(String key) {
    String [] strs = null;
    try {
      ProcessListVariable lst = this.process.getList(key);
      strs = new String[lst.size()];
      for(int i = 0; i < lst.size(); i++) {
        try {
          strs[i] = lst.getItem(i).format();
        } catch(NullPointerException e) {}
      }
    } catch (Throwable t) {
    }
    return strs;
  }

  @Override
  public ArrayList getStringChangedKeys() {
    return new ArrayList();
  }

  @Override
  public Iterator getStringKeys() {
    return new EmptyIterator();
  }

  @Override
  public String getTempData(String asName) {
    return this.process.getTempData(asName);
  }

  @Override
  public Iterator getTempDataKeys() {
    return Collections.unmodifiableSet(this.process._tempData.keySet()).iterator();
  }

  @Override
  public String getType(String name) {
    return IDataSet.sTYPE_STRING;
  }

  @Override
  public String getType(String name, int index) {
    return IDataSet.sTYPE_STRING;
  }

  @Override
  public boolean hasError() {
    return this.process.hasError();
  }

  @Override
  public boolean hasVariable(String asVar) {
    return this.process.getCatalogue().hasVar(asVar);
  }

  @Override
  public boolean isArray(String asKey) {
    return this.process.getList(asKey)!=null;
  }

  @Override
  public String isChecked(String key, double value) {
    double d = this.getDouble(key);
    String s = "";

    if (d == value)
      s = "checked";

    return s;
  }

  @Override
  public boolean isConst(String name) {
    return false;
  }

  @Override
  public String isSelected(String key, String value) {
    String s = this.getString(key);

    if (s != null && value != null && s.equals(value))
      return "selected";
    else
      return "";
  }

  @Override
  public String isSelected(String key, double value) {
    double d = this.getDouble(key);
    String s = "";

    if (d == value)
      s = "selected";

    return s;
  }

  @Override
  public void markChangedFields(boolean keepConsts) {
  }

  @Override
  public void removeCurrencyValue(String key) {
    removeString(key);
  }

  @Override
  public void removeCurrencyValue(String key, int index) {
    removeString(key, index);
  }

  @Override
  public void removeDouble(String key) {
    removeString(key);
  }

  @Override
  public void removeDouble(String key, int index) {
    removeString(key, index);
  }

  @Override
  public void removeList() {
  }

  @Override
  public void removeListItem(int anListPosition) {
  }

  @Override
  public void removeListVar(String asListVar) {
    try {
      this.process.getList(asListVar).clear();
    } catch(Throwable t){}
  }

  @Override
  public void removeListVar(String asListVar, boolean abForce) {
    removeListVar(asListVar);
  }

  @Override
  public void removeString(String key) {
    try {
      this.process.get(key).clear();
    } catch(Throwable t){}
  }

  @Override
  public void removeString(String key, int index) {
    try {
      this.process.getList(key).getItem(index).clear();
    } catch(Throwable t){}
  }

  @Override
  public void setCatalogueVars(Map hmCatVars, Map hmCatValues, Map hmCatDesc) {
  }

  @Override
  public void setConst(String asName, String asValue) {
  }

  @Override
  public void setConst(String asName, String asValue, int anIndex) {
  }

  @Override
  public void setCurrencyType(int ct) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean setCurrencyValue(String key, double value) {
    this.process.set(key, new Double(value));
    return true;
  }

  @Override
  public boolean setCurrencyValue(String key, double value, int index) {
    ProcessListVariable listVar = this.process.getList(key);
    if(null == listVar) return false;

    listVar.setItemValue(index, new Double(value));
    
    return true;
  }

  @Override
  public boolean setDate(String var, Date date) {
    this.process.set(var, date);
    return true;
  }

  @Override
  public boolean setDouble(String key, double value) {
    this.process.set(key, new Double(value));
    return true;
  }

  @Override
  public boolean setDouble(String key, double value, int index) {
    ProcessListVariable listVar = this.process.getList(key);
    if(null == listVar) return false;

    listVar.setItemValue(index, new Double(value));
    
    return true;
  }

  @Override
  public boolean setDoubleArray(String key, double[] data) {
    ProcessListVariable listVar = this.process.getList(key);
    if(null == listVar) return false;
    int size = 0;
    if(null != data) size = data.length;
    listVar.setItems(new ProcessListItemList(size));
    for(int i = 0; i < size; i++)
      listVar.setItemValue(i, new Double(data[i]));
    return true;
  }

  @Override
  public boolean setError(String e) {
    this.process.setError(e);
    return true;
  }

  @Override
  public void setListItem(IDataSet adsData, int anListPosition) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean setString(String key, String value) {
    try {
      this.process.get(key).parse(value);
    } catch (Throwable t) {
      return false;
    }
    return true;
  }

  @Override
  public boolean setString(String key, String value, int index) {
    ProcessListVariable listVar = this.process.getList(key);
    if(null == listVar) return false;

    listVar.setItemValue(index, value);
    
    return true;
  }

  @Override
  public boolean setStringArray(String key, String[] data) {
    ProcessListVariable listVar = this.process.getList(key);
    if(null == listVar) return false;
    int size = 0;
    if(null != data) size = data.length;
    listVar.setItems(new ProcessListItemList(size));
    for(int i = 0; i < size; i++)
      listVar.setItemValue(i, data[i]);
    return true;
  }

  @Override
  public void setTempData(String asName, String asValue) {
    this.process.setTempData(asName, asValue);
  }

  @Override
  public void unMarkChangeField(String asKey) {
    // this.process.resetModified();
  }

  @Override
  public void unMarkChangeFields() {
    this.process.resetModified();
  }

  @Override
  public void update(IDataSet dataSet) {
    // TODO Auto-generated method stub
  }

  @Override
  public void zap() {
    this.process.clearData();
  }

}
