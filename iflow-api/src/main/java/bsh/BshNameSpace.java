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
package bsh;

import java.util.Vector;

/**
 * Extension to NameSpace to avoid some "limitations" by package access methods.
 * @author ombl
 *
 */
public abstract class BshNameSpace extends NameSpace {

  private static final long serialVersionUID = -2758372117272579760L;
  
  public BshNameSpace(NameSpace parent, String name) {
    super(parent, name);
    if(null == parent) throw new IllegalArgumentException("Parent Cant be null");
  }


  public abstract String[] getProcessVariableNames();
  
  /**
   * Synchronize environment with process
   * @deprecated Use saveToProcess()
   */
  @Deprecated
  public void saveToDataSet() {
    saveToProcess();
  }
  
  public abstract void saveToProcess();
  
  // Give visibility to package methods
  protected abstract void setVariableImpl(String name, Object value, boolean strictJava, boolean recurse ) throws UtilEvalError;
  
  @Override
  public void setVariable(String name, Object value, boolean strictJava, boolean recurse) throws UtilEvalError {
    setVariableImpl(name, value, strictJava, recurse);
  }

  protected abstract Variable getVariableImpl( String name, boolean recurse ) throws UtilEvalError;
  
  protected Variable getSuperVariable(String name, boolean recurse) throws UtilEvalError {
    return super.getVariableImpl( name, recurse );
  }
  protected void setSuperVariable(String name, Object value, boolean strictJava, boolean recurse ) throws UtilEvalError {
    super.setVariable( name, value, strictJava, recurse );
  }

  // wrapper methods
  public String[] getVariableNames() {
    String [] processVars = getProcessVariableNames();
    String [] localVars = super.getVariableNames();
    if(processVars.length == 0) return processVars;
    if(localVars.length == 0) return processVars;
    int size = processVars.length + localVars.length;

    String [] result = new String[size];
    System.arraycopy(processVars, 0, result, 0, processVars.length);
    System.arraycopy(localVars, 0, result, processVars.length, localVars.length);
    return result;
  }
  
  @SuppressWarnings("unchecked")
  protected void getAllNamesAux( Vector vec ) {
    String [] processVars = getProcessVariableNames();
    for(String var : processVars)
      vec.add(var);
    super.getAllNamesAux(vec);
  }

}
