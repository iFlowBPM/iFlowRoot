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

public class FlowEditorAdapterImpl implements FlowEditorAdapter {

  private final Desenho desenho;
  private final Janela janela;
  private final IMessages messages;
  private final String i18nKey;
  
  public FlowEditorAdapterImpl(Janela janela, Desenho desenho, String key) {
    this.janela = janela;
    this.desenho = desenho;
    this.messages = this.janela.getBlockMessages();
    this.i18nKey = key;
  }
  
  public void asyncExec(Runnable runnable) {
    FlowEditor.getRootDisplay().asyncExec(runnable);
  }

  public IMessages getBlockMessages() {
    return messages;
  }

  public String getString(String key, Object... objects) {
    return messages.getString(key, objects);
  }
  
  public IDesenho getDesenho() {
    return desenho;
  }

  public IJanela getJanela() {
    return janela;
  }

  public JFrame getParentFrame() {
    return janela;
  }

  public RepositoryClient getRepository() {
    return janela.getRepository();
  }

  public boolean isRepOn() {
    return getRepository().checkConnection();
  }

  public void log(String msg) {
    FlowEditor.log(msg);
  }

  public void log(String msg, Throwable t) {
    FlowEditor.log(msg, t);
  }
  
  public Object getRootShell() {
    return FlowEditor.getRootShell();
  }

  public void showError(String msg) {
    new Erro(msg, this.janela);  //$NON-NLS-1$
  }
  
  public Atributo newAtributo(String nome, String valor, String descricao) {
    return new AtributoImpl(nome, valor, descricao);
  }
  
  public String getBlockKey() {
    return i18nKey;
  }
}
