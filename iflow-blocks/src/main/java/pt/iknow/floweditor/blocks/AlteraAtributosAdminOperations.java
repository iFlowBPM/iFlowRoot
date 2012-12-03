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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.blocks.dataProcessing.OperationField;
import pt.iknow.utils.swing.DecoratedBorder;

public class AlteraAtributosAdminOperations extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1L;

  private  static final int nPANEL_WIDTH = 450;
  private  static final int nPANEL_HEIGHT = 250;

  private JPanel jPanelMasterButtons = new JPanel();
  private JPanel jPanelDataPresentation = new JPanel();
  private JPanel jPanelMasterDataPresentation = new JPanel();
  private JPanel jPanelProcessDataPresentation = new JPanel();

  private JButton jButton_OK = new JButton();
  private JButton jButton_Cancel = new JButton();

  private JTextField jTextFieldDescription = null;
  private JTextField jTextFieldResult = null;

  private JTextField jTextField_Field_FlowId = null;
  private JTextField jTextField_Field_Pid = null;
  private JTextField jTextField_Field_SubPid = null;

  private int exitStatus = EXIT_STATUS_CANCEL;

  // BUTTON PREVIEW STUFF END
  protected final static String sPROP_DESCR = "block_description";  //  @jve:decl-index=0:
  protected final static String sPROP_RESULT = "block_result";  //  @jve:decl-index=0:
  public final static String sPROP_Operation = "operation";
  public final static String sPROP_PID = "pid"; // @jve:decl-index=0:
  public final static String sPROP_SUBPID = "subpid";
  public final static String sPROP_FLOWID = "flowid";


  private JPanel jContentPane_AlteraAtributosAdminOperations = null;

  public AlteraAtributosAdminOperations(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosAdminOperations.title"), true); //$NON-NLS-1$
  }

  public AlteraAtributosAdminOperations(FlowEditorAdapter adapter, String title, boolean modal) {
    super(adapter,title,modal);
  }

  protected void dealWithInformation(List<Atributo> atributos) {
    // fetch attributes
    for (Atributo atributo : atributos) {
      String atributName = atributo.getNome();
      if (sPROP_DESCR.equals(atributName)){
        jTextFieldDescription.setText(atributo.getValor());
      } else if (sPROP_RESULT.equals(atributName)){
        jTextFieldResult.setText(atributo.getValor());
      } else if (sPROP_FLOWID.equals(atributName)){
        jTextField_Field_FlowId.setText(atributo.getValor());
      } else if (sPROP_PID.equals(atributName)){
        jTextField_Field_Pid.setText(atributo.getValor());
      } else if (sPROP_SUBPID.equals(atributName)){
        jTextField_Field_SubPid.setText(atributo.getValor());
      }
    }
  }

  /**
   * getExitStatus
   * 
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * 
   * @return
   */
  public String[][] getNewAttributes() {
    List<String[]> elements = new ArrayList<String[]>();

    String[] element = getNewAttributeElement(AlteraAtributosAdminOperations.sPROP_DESCR, this.jTextFieldDescription.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperations.sPROP_RESULT, this.jTextFieldResult.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperations.sPROP_FLOWID, this.jTextField_Field_FlowId.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperations.sPROP_PID, this.jTextField_Field_Pid.getText(), "");
    elements.add(element);

    element = getNewAttributeElement(AlteraAtributosAdminOperations.sPROP_SUBPID, this.jTextField_Field_SubPid.getText(), "");
    elements.add(element);

    elements = processAditionalElements(elements);

    int numberOfVariables = elements.size();
    String[][] newAttributes = new String[numberOfVariables][3];
    
    for (int i=0; i<elements.size(); i++){
      newAttributes[i]=elements.get(i);
    }
    return newAttributes;
  }

  protected List<String[]> processAditionalElements(List<String[]> elements) {
    return elements;
  }

  protected String[] getNewAttributeElement(String fieldKey, String fieldValue, String filler) {
    String[] element = new String[3];
    element[0] = fieldKey;
    element[1] = fieldValue;
    element[2] = filler;
    return element;
  }

  public boolean fixedAttributes() {
    return true;
  }

  /**
   * setDataIn
   * 
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    this.setTitle(title);

    jbInit();

    dealWithInformation(atributos);

    this.repaint();
    setVisible(true);
  }


  /**
   * jbInit
   * 
   * @throws Exception
   */
  void jbInit() {
    BorderLayout borderLayout1 = new BorderLayout();
    borderLayout1.setVgap(10);
    GridLayout gridLayout1 = new GridLayout(0, 2);
    gridLayout1.setVgap(2);
    GridLayout gridLayout = new GridLayout(0, 2);
    gridLayout.setVgap(2);
    jPanelMasterDataPresentation.setLayout(gridLayout1);
    jPanelMasterDataPresentation.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    jPanelProcessDataPresentation.setLayout(gridLayout);
    jPanelProcessDataPresentation.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jPanelDataPresentation.setLayout(borderLayout1);
    jPanelDataPresentation.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    jPanelDataPresentation.add(jPanelMasterDataPresentation, BorderLayout.NORTH);
    jPanelDataPresentation.add(jPanelProcessDataPresentation, BorderLayout.CENTER);

    inicializeDataFields();
    JLabel fieldLabel = null;

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.description"), jTextFieldDescription);
    this.jPanelMasterDataPresentation.add(fieldLabel);
    this.jPanelMasterDataPresentation.add(jTextFieldDescription, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.result"), jTextFieldResult);
    this.jPanelMasterDataPresentation.add(fieldLabel);
    this.jPanelMasterDataPresentation.add(jTextFieldResult, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.flowid"), jTextField_Field_FlowId);
    this.jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_FlowId);
    this.jPanelProcessDataPresentation.add(jTextField_Field_FlowId, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.pid"), jTextField_Field_Pid);
    this.jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_Pid);
    this.jPanelProcessDataPresentation.add(jTextField_Field_Pid, null);

    fieldLabel = createLabelForField(adapter.getString("AlteraAtributosAdminOperations.subpid"), jTextField_Field_SubPid);
    this.jPanelProcessDataPresentation.add(fieldLabel, null);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, jPanelProcessDataPresentation.getBackground(), jTextField_Field_SubPid);
    this.jPanelProcessDataPresentation.add(jTextField_Field_SubPid, null);

    this.jPanelProcessDataPresentation = addAdicionalFields(this.jPanelProcessDataPresentation);

    this.jPanelMasterButtons.add(jButton_OK, null);
    this.jPanelMasterButtons.add(jButton_Cancel, null);

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });

    jButton_OK.setText(OK);
    jButton_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_OK_actionPerformed(e);
      }
    });

    jButton_Cancel.setText(Cancelar);
    jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_Cancel_actionPerformed(e);
      }
    });

    this.setSize(nPANEL_WIDTH,nPANEL_HEIGHT);
    this.setLocationRelativeTo(null);
    this.setModal(true);
    this.setContentPane(getJContentPane_AlteraAtributosAdminOperations());
    dialogComponentResized(null);

    repaint();
  }

  protected JPanel addAdicionalFields(JPanel jPanelProcessDataPresentation) {
    return jPanelProcessDataPresentation;
  }

  protected void inicializeDataFields() {
    this.jTextFieldDescription= new JTextField(30);
    this.jTextFieldResult = new JTextField(30);

    this.jTextField_Field_FlowId = new JTextField(30);
    this.jTextField_Field_Pid = new JTextField(30);
    this.jTextField_Field_SubPid = new JTextField(30);
  }

  protected JLabel createLabelForField(String labelText, Component fieldNeedingDescription) {
    JLabel jLabel = null;
    jLabel = new JLabel(labelText);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(fieldNeedingDescription);
    return jLabel;
  }

  /* OK */
  void jButton_OK_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton_Cancel_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

  private JPanel getJContentPane_AlteraAtributosAdminOperations() {
    if (jContentPane_AlteraAtributosAdminOperations == null) {
      BorderLayout borderLayout = new BorderLayout();
      borderLayout.setHgap(5);
      jContentPane_AlteraAtributosAdminOperations = new JPanel();
      jContentPane_AlteraAtributosAdminOperations.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
      jContentPane_AlteraAtributosAdminOperations.setLayout(borderLayout);

      jContentPane_AlteraAtributosAdminOperations.add(jPanelDataPresentation, BorderLayout.CENTER);
      jPanelMasterButtons.add(jButton_OK, null);
      jPanelMasterButtons.add(jButton_Cancel, null);
      jContentPane_AlteraAtributosAdminOperations.add(jPanelMasterButtons, BorderLayout.SOUTH);
    }
    return jContentPane_AlteraAtributosAdminOperations;
  }
}
