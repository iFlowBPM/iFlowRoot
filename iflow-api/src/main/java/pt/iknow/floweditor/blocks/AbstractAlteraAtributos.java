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
package pt.iknow.floweditor.blocks;

import javax.swing.JDialog;

import pt.iknow.floweditor.FlowEditorAdapter;

public abstract class AbstractAlteraAtributos extends JDialog implements AlteraAtributosInterface {
  private static final long serialVersionUID = -9153864796786247370L;
  
  protected final transient FlowEditorAdapter adapter;
  /* OK CANCEL */
  protected final String Cancelar;
  protected final String OK;

  public AbstractAlteraAtributos(FlowEditorAdapter adapter) {
    super(adapter.getParentFrame());
    this.adapter = adapter;
    /* OK CANCEL */
    Cancelar = adapter.getBlockMessages().getString("Common.cancel"); //$NON-NLS-1$
    OK = adapter.getBlockMessages().getString("Common.ok"); //$NON-NLS-1$
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, String title) {
    this(adapter);
    setTitle(title);
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, boolean modal) {
    this(adapter);
    setModal(modal);
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, String title, boolean modal) {
    this(adapter);
    setTitle(title);
    setModal(modal);
  }
  
  public FlowEditorAdapter getAdapter() {
    return adapter;
  }


  public static String asString(Object obj) {
    if(null == obj) return null;
    return String.valueOf(obj);
  }
  
  public static String [] asString(Object [] obj) {
    if(null == obj) return null;
    String [] res = new String[obj.length];
    for(int i = 0; i < res.length; i++)
      res[i] = asString(obj[i]);
    return res;
  }
  
  public static String [][] asString(Object [][] obj) {
    if(null == obj) return null;
    String [][] res = new String[obj.length][];
    for(int i = 0; i < res.length; i++)
      res[i] = asString(obj[i]);
    
    return res;
  }
  
}
