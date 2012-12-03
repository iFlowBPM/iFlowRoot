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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import bsh.BshNameSpace;
import bsh.BshVariable;
import bsh.Interpreter;
import bsh.InterpreterError;
import bsh.Primitive;
import bsh.UtilEvalError;
import bsh.Variable;

/**
 * Classe de mapeamento e interacção entre beanshell e ProcessData
 * @author ombl
 *
 */
public class ProcessDataNameSpace extends BshNameSpace {

  private static final long serialVersionUID = -2758372117272579761L;
  private Map<String,Variable> variableCache;
  private UserInfoInterface userInfo;
  private ProcessData process;
  private int processDataMode;
  
  /**
   * @deprecated
   */
  private boolean forDB = false;

  protected ProcessDataNameSpace(UserInfoInterface userInfo, Interpreter bsh, ProcessData process, boolean forDB) {
    this(userInfo, bsh, process);
    this.forDB = forDB;
  }
  
  public ProcessDataNameSpace(UserInfoInterface userInfo, Interpreter bsh, ProcessData process) {
    super(bsh.getNameSpace(), "ProcessData NameSpace");
    this.userInfo = userInfo;
    this.process = process;
    setProcessDataMode(0);
    this.variableCache = new Hashtable<String, Variable>();
  }

  public void setProcessDataMode (int mode) {
    this.processDataMode = mode;
  }

  public int getProcessDataMode () {
    return this.processDataMode;
  }


// TODO definir um modo "Lista" e um modo "Array" para interagir com  
  
  public String[] getProcessVariableNames() {
    return this.variableCache.keySet().toArray(new String[this.variableCache.size()]);
  }

  /**
   * Actualiza o processo com os arrays modificados/criados na beanshell.
   * As variaveis simples são actualizadas automaticamente.
   */
  public void saveToProcess() {
    if(process == null) return;
    if(processDataMode == 0) return; // so actualiza em modo "1"
    
    String[] names = this.getVariableNames();
    int i;
    for (i = 0; i < names.length; i++) {
      String name = names[i];
      if (StringUtils.isBlank(name)) continue;

      String varname = getProcessVariableName(name);

      // as variaveis simples são actualizadas automaticamente.
      ProcessListVariable listVariable = process.getList(varname);

      if (null == listVariable) continue; // do not exist.

      Object value = null;
      try {
        value = this.getVariable(name,false); // procura apenas neste namespace
      } 
      catch (bsh.UtilEvalError uee) {
        uee.printStackTrace();
        // TODO handle error
        continue;
      }

      if(null == value) {
        // clear variable...
        listVariable.setItems(new ProcessListItemList(0)); // kill previous list
      } else if(value.getClass().isArray()) {
        int size = Array.getLength(value);
        listVariable.setItems(new ProcessListItemList(size)); // kill previous list
        for(int position = 0; position < size; position++) {
          listVariable.setItemValue(position, parseItem(Array.get(value, position)));
        }
      } else if(value instanceof Iterable<?>) { // permitir aceder como lista
        Iterator<?> iter = ((Iterable<?>) value).iterator();
        listVariable.setItems(new ProcessListItemList()); // kill previous list
        while(iter.hasNext())
          listVariable.addNewItem(parseItem(iter.next()));
      }
    }
  }

  private Object parseItem(Object item) {
    Object retObj = item;
    if (item != null && item instanceof pt.iflow.connector.document.Document) {
      Document doc = (Document)item;
      retObj = doc.getDocId();
    }          
    return retObj;
  }
  
  /**
  Set the value of a the variable 'name' through this namespace.
  The variable may be an existing or non-existing variable.
  It may live in this namespace or in a parent namespace if recurse is 
  true.
  <p>
  Note: This method is not public and does *not* know about LOCALSCOPING.
  Its caller methods must set recurse intelligently in all situations 
  (perhaps based on LOCALSCOPING).

  <p>
  Note: this method is primarily intended for use internally.  If you use
  this method outside of the bsh package and wish to set variables with
  primitive values you will have to wrap them using bsh.Primitive.
  @see bsh.Primitive
  <p>
  Setting a new variable (which didn't exist before) or removing
  a variable causes a namespace change.

  @param strictJava specifies whether strict java rules are applied.
  @param recurse determines whether we will search for the variable in
    our parent's scope before assigning locally.
   */
  @Override
  protected void setVariableImpl(String name, Object value, boolean strictJava, boolean recurse ) throws UtilEvalError {
    // TODO se a variavel nao foi minha, delega para o pai


    // primitives should have been wrapped
    if ( value == null )
      throw new InterpreterError("null variable value");

    // Locate the variable definition if it exists.
    Variable existing = getVariableImpl( name, recurse );
    if(null == existing) {
      // try and recurse
      setSuperVariable(name, value, strictJava, recurse);
      return;//throw new UtilEvalError("oops");
    }

    existing.setValue(value, BshVariable.V_ASSIGNMENT);
    
    if(isReadOnly()) return; // ignore if datasetmode not 1
    String varname = getProcessVariableName(name);

    // TODO "arrebentar" se não existr...
    if(null == process) return;
    if(!isReadOnly() && name.charAt(0)!='_') return;
    ProcessSimpleVariable procVar = process.get(varname);
    if(procVar!=null) {
      
      ProcessDataType dataType = process.getVariableDataType(varname);
      Class<?> clazz = dataType.getSupportingClass();
      
      if (value instanceof java.lang.String) {
        if (clazz.isAssignableFrom(java.lang.String.class)) {
          procVar.setValue(value);
        }
        else {
          // AVOID NULL VALUES HACK
          String strValue = (String)value;
          if (StringUtils.isEmpty(strValue)) {
            procVar.setValue(null);
          }
          else {
            try {
              process.parseAndSet(varname, strValue);
            } catch (ParseException e) {
              // ignore
              return;
            }            
          }
        }
      } 
      else if (value instanceof bsh.Primitive) {
        bsh.Primitive primValue = (bsh.Primitive)value;
        Object finalValue = null;
        if (primValue != null && primValue.getValue() != null) {
          String typeName = primValue.getType().getName();
          finalValue = primValue.getValue();
          if (typeName.equals("boolean")) {
            // TODO implement support for iflow boolean datatype
            finalValue = new Integer(((Boolean)finalValue).booleanValue() ? 1 : 0);
          }
        }
        procVar.setValue(finalValue);
      }
      else if (value instanceof java.util.Date) {
        procVar.setValue(value);
      }
      else {
        // TODO: fazer o set na mesma?? ou ignorar?
        System.err.println(this.getClass().getName() + "->unsupported value " + value.getClass().getName() + " for variable " + varname);
      }
    }
    else if(process.getList(varname)==null) {
      Class<?> varClass = value.getClass();
      if(varClass.isArray()){
        // TODO mete lá o array
        System.err.println(this.getClass().getName() + "->TODO1");
      } else if(Iterable.class.isAssignableFrom(varClass)) {
        // TODO mete lá a colecao
        System.err.println(this.getClass().getName() + "->TODO2");
      }
    }
  }


  protected Variable getVariableImpl( String name, boolean recurse ) throws UtilEvalError {
    Variable var = null;

    if(variableCache.containsKey(name)) return variableCache.get(name);

    String varname = getProcessVariableName(name);
    Object obj = null;
    Class<?> clazz = null;


    ProcessVariableValue processVar = process.get(varname);
    ProcessDataType dataType = process.getVariableDataType(varname);

    if(processVar != null) {
      clazz = dataType.getSupportingClass();
      obj = processVar.getValue();
      if(obj == null) {
        if(clazz.isPrimitive()) {
          try {
            Class<?> boxClass = Primitive.boxType(clazz);
            Constructor<?> constructor = boxClass.getConstructor(String.class);
            obj = constructor.newInstance("0"); // works for all: Boolean -> FALSE, Number -> 0
          } catch(Exception e) {
            throw new UtilEvalError("Could not wrap value for var "+varname);
          }
          obj = new Primitive(obj);
        }
        else if (clazz == java.util.Date.class) {
          obj = null;
        }
        else {
          // AVOID NULL VALUES HACK: override null var values with empty strings
          if (!(clazz.isAssignableFrom(java.lang.String.class))) {
            clazz = java.lang.String.class;
          }
          obj = "";
        }
      }
      else {
        if (forDB && clazz == java.lang.String.class) {
          obj = StringEscapeUtils.escapeSql((String)obj);
        }
      }

      var = new BshVariable(name, clazz, obj, null);
    }

    ProcessListVariable listVar = process.getList(varname);
    if(var == null && null != listVar) {
      clazz = dataType.getSupportingClass();
      int length = listVar.size();

      boolean isDocument = clazz.isAssignableFrom(pt.iflow.connector.document.Document.class);
      Documents docBean = null;
      if (isDocument) {
        // document... needs fetching
        docBean = BeanFactory.getDocumentsBean();
      }
      
      obj = Array.newInstance(clazz, length);

      for(int i = 0; i < length; i++) {
        Object item = listVar.getItemValue(i);
        if(null != item) {
          
          if (isDocument) {
            item = getDocument(docBean, item);
          }
          else if (clazz == java.lang.String.class && item instanceof java.lang.Integer) {
            // XXX TextArray Document HACK!! legacy file vars where defined as text arrays. Now document id is an integer
            try {
              item = ((Integer)item).toString(); 
            }
            catch (NumberFormatException nfe) {
              // ignore.... back to the way it was
            }
          }
          else if (clazz == int.class && item instanceof java.lang.Long) {
            item = ((Long)item).intValue();
          }
          else {
            if (forDB && clazz == java.lang.String.class) {
              item = StringEscapeUtils.escapeSql((String)item);
            }
          }
          
          Array.set(obj, i, item); // esperemos que funcione...
        }
      }

      var = new BshVariable(name, obj.getClass(), obj, null);
    }

    if(var != null) {
      variableCache.put(name, var);
    } else {
      var = getSuperVariable(name, recurse);
    }
    return var;
  }
  
  private Document getDocument(Documents docBean, Object item) {
    if (item != null && docBean != null) {
      int docId = -1;
      if (item instanceof Number) {
        docId = ((Number)item).intValue();
      }
      else {
        try {
          docId = Integer.parseInt(String.valueOf(item));
        }
        catch (Exception e) {
        }
      }
      if (docId > -1) {
        return docBean.getDocumentInfo(this.userInfo, this.process, docId);
      }
    }
    
    return null;
  }
  
  /**
   * Reset current NameSpace internals. Keep local variables.
   */
  public void reset() {
    this.variableCache.clear();
  }

  /**
   * Reset current NameSpace internals
   */
  public void clear() {
    this.reset();
    super.clear();
  }

  protected boolean isReadOnly() {
    return (processDataMode == 0);
  }
  
  protected boolean isVariableReadOnly(String name) {
    return (processDataMode == 0) || (processDataMode == 1 && name.charAt(0) != '_');
  }
  
  protected String getProcessVariableName(String name) {
    if(isVariableReadOnly(name)) return name;
    return name.substring(1);
  }
  

  
}
