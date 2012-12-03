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

import java.io.File;
import java.util.List;

import javax.swing.JScrollPane;

import pt.iflow.api.transition.FlowStateHistoryTO;

public class DesenhoScrollPane extends JScrollPane {
  private static final long serialVersionUID = 2956791840433440274L;
  
  private Desenho desenho;

  public DesenhoScrollPane(Janela j, String flowId, String flowName) {
    super();
    this.desenho = new Desenho(j, this, flowId, flowName);
    init();
  }
  
  public DesenhoScrollPane(Janela j, File flowFile) {
    super();
    this.desenho = new Desenho(j, this, flowFile);
    init();
  }
  
  public DesenhoScrollPane(Janela j, byte [] flowData, String altName, int flowVersion) {
    this(j, flowData, altName, flowVersion, null);
  }

  public DesenhoScrollPane(Janela j, byte [] flowData, String altName, int flowVersion, List<FlowStateHistoryTO> flowStateHistory) {
    super();
    if (flowStateHistory != null) {
      this.desenho = new DesenhoEstadoProcesso(j, this, flowData, altName, flowVersion, flowStateHistory);
    } else {
      this.desenho = new Desenho(j, this, flowData, altName, flowVersion);
    }
    init();
  }
  
  private void init() {
    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    setViewportView(desenho);
  }
  
  public Desenho getDesenho() {
    return desenho;
  }
  
  public String getName() {
    return desenho.getName();
  }
}
