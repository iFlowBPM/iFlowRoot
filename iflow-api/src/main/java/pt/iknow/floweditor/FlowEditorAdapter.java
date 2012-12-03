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
package pt.iknow.floweditor;

import javax.swing.JFrame;

import pt.iflow.api.msg.IMessages;
import pt.iknow.iflow.RepositoryClient;

public interface FlowEditorAdapter {

  public JFrame getParentFrame();
  
  public IDesenho getDesenho();
  
  public IJanela getJanela();
  
  public void log(String msg);

  public void log(String msg, Throwable t);
  
  public RepositoryClient getRepository();
  
  public IMessages getBlockMessages();
  
  public String getString(String key, Object ... objects);
  
  public void asyncExec(Runnable runnable);
  
  public boolean isRepOn();
  
  public Object getRootShell();
  
  public void showError(String msg);
  
  public Atributo newAtributo(String nome, String valor, String descricao);
  
  public String getBlockKey();
}
