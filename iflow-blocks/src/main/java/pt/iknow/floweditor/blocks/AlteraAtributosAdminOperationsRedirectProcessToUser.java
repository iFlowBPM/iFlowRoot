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

public class AlteraAtributosAdminOperationsRedirectProcessToUser extends AlteraAtributosAdminOperations{
  private static final long serialVersionUID = 1L;

  public static String sPROP_PROCESS_CURRENT_USER = "process_current_user";
  public static String sPROP_PROCESS_NEW_USER = "process_new_user";

  private JTextField jTextField_Field_CurrentUser = null;
  private JTextField jTextField_Field_NewUser = null;

  public AlteraAtributosAdminOperationsRedirectProcessToUser(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosAdminOperations.title"), true); //$NON-NLS-1$
  }

  protected void inicializeDataFields() {
    super.inicializeDataFields();

    this.jTextField_Field_CurrentUser = new JTextField(30);
    this.jTextField_Field_NewUser = new JTextField(30);
  }
  
  protected void dealWithInformation(List<Atributo> atributos) {
    super.dealWithInformation(atributos);

    for (Atributo atributo : atributos) {
      String atributName = atributo.getNome();
      if (sPROP_PROCESS_CURRENT_USER.equals(atributName)){
        jTextField_Field_CurrentUser.setText(atributo.getValor());
      } else if (sPROP_PROCESS_NEW_USER.equals(atributName)){
        jTextField_Field_NewUser.setText(atributo.getValor());
      }
    }
  }

  protected JPanel addAdicionalFields(JPanel jPanelProcessDataPresentation) {
    if (jPanelProcessDataPresentation == null){
      jPanelProcessDataPresentation = new JPanel();
    }

    JLabel fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.redirect_process_to_user.current_user"), jTextField_Field_CurrentUser);
    jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_CurrentUser);
    jPanelProcessDataPresentation.add(jTextField_Field_CurrentUser, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.redirect_process_to_user.new_user"), jTextField_Field_NewUser);
    jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_NewUser);
    jPanelProcessDataPresentation.add(jTextField_Field_NewUser, null);

    return jPanelProcessDataPresentation;
  }

  protected List<String[]> processAditionalElements(List<String[]> elements) {
    if (elements == null){
      elements = new ArrayList<String[]>();
    }

    String[] element = getNewAttributeElement(AlteraAtributosAdminOperationsRedirectProcessToUser.sPROP_PROCESS_CURRENT_USER, this.jTextField_Field_CurrentUser.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperationsRedirectProcessToUser.sPROP_PROCESS_NEW_USER, this.jTextField_Field_NewUser.getText(), "");
    elements.add(element);

    return elements;
  }
}
