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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.blocks.dataProcessing.OperationField;
import pt.iknow.utils.swing.DecoratedBorder;

public class AlteraAtributosAdminOperationsUndoProcess extends AlteraAtributosAdminOperations {
  private static final long serialVersionUID = 1L;

  public static String sPROP_PROCESS_FLOW_STATE = "process_flow_state";
  public static String sPROP_PROCESS_MID = "process_mid";

  private JTextField jTextField_Field_FlowState = null;
  private JTextField jTextField_Field_MID = null;

  public AlteraAtributosAdminOperationsUndoProcess(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosAdminOperations.title"), true); //$NON-NLS-1$
  }

  protected void inicializeDataFields() {
    super.inicializeDataFields();

    this.jTextField_Field_FlowState = new JTextField(30);
    this.jTextField_Field_MID = new JTextField(30);
  }
  
  protected void dealWithInformation(List<Atributo> atributos) {
    super.dealWithInformation(atributos);

    for (Atributo atributo : atributos) {
      String atributName = atributo.getNome();
      if (sPROP_PROCESS_FLOW_STATE.equals(atributName)) {
        jTextField_Field_FlowState.setText(atributo.getValor());
      } else if (sPROP_PROCESS_MID.equals(atributName)) {
        jTextField_Field_MID.setText(atributo.getValor());
      }
    }
  }

  protected JPanel addAdicionalFields(JPanel jPanelProcessDataPresentation) {
    if (jPanelProcessDataPresentation == null){
      jPanelProcessDataPresentation = new JPanel();
    }

    JLabel fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.undo_process.flow_state"),
        jTextField_Field_FlowState);
    jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_FlowState);
    jPanelProcessDataPresentation.add(jTextField_Field_FlowState, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.undo_process.mid"), jTextField_Field_MID);
    jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_MID);
    jPanelProcessDataPresentation.add(jTextField_Field_MID, null);

    return jPanelProcessDataPresentation;
  }

  protected List<String[]> processAditionalElements(List<String[]> elements) {
    if (elements == null){
      elements = new ArrayList<String[]>();
    }

    String[] element = getNewAttributeElement(AlteraAtributosAdminOperationsUndoProcess.sPROP_PROCESS_FLOW_STATE,
        jTextField_Field_FlowState.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperationsUndoProcess.sPROP_PROCESS_MID, jTextField_Field_MID.getText(),
        "");
    elements.add(element);

    return elements;
  }
}
