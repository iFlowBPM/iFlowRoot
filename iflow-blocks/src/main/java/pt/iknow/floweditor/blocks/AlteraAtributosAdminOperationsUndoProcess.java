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