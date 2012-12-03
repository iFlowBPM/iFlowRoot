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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.IDesenho;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosSubFlow extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = -7311692444232634764L;

  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JButton assignButton = new JButton();
  private JScrollPane jScrollPane0 = new JScrollPane();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JScrollPane jScrollPane2 = new JScrollPane();

  private MyJTableX jtableChooseSubFlow = new MyJTableX();
  private MyJTableX jtableInput = new MyJTableX();
  private MyJTableX jtableOutput = new MyJTableX();

  private static final String sCHOOSE = "Escolha"; //$NON-NLS-1$
  private final String sCHOOSE_DESC;

  private final IDesenho desenho;

  // map this
  private static final String[] subflowRowNames = { "Nome do SubFluxo" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  // into this
  private final String[] subflowRowNamesDesc;
  private final String[] fieldsColumnNames;
  private static final String[] subflowColumnNames = { " ", " " }; //$NON-NLS-1$ //$NON-NLS-2$

  private static String[] _subFlows = null;

  // map this
  private static final String[] _activities = { "Fluxo Original", "Sub Fluxo" }; //$NON-NLS-1$ //$NON-NLS-2$
  // into this
  private final String[] _activitiesDesc;

  // map this
  private static final String[] _SimNao = { "Nao", "Sim" }; //$NON-NLS-1$ //$NON-NLS-2$
  // into this
  private final String[] _SimNaoDesc;


  private final HashMap<String, String> actToDescMapping = new HashMap<String, String>();
  private final HashMap<String, String> descToActMapping = new HashMap<String, String>();
  private final HashMap<String, String> snToDescMapping = new HashMap<String, String>();
  private final HashMap<String, String> descToSnMapping = new HashMap<String, String>();
  private final HashMap<String, String> rowToDescMapping = new HashMap<String, String>();
  private final HashMap<String, String> descToRowMapping = new HashMap<String, String>();
  
  
  public AlteraAtributosSubFlow(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosSubFlow.title"), true); //$NON-NLS-1$
    desenho = adapter.getDesenho();
    if (adapter.getRepository() != null) {
      _subFlows = adapter.getRepository().listSubFlows();
    }

    sCHOOSE_DESC = adapter.getString("AlteraAtributosSubFlow.choose"); //$NON-NLS-1$

    subflowRowNamesDesc = new String []{
        adapter.getString("AlteraAtributosSubFlow.subflowName"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.keepActivities"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.fork"), //$NON-NLS-1$ 
    };
    fieldsColumnNames = new String[] {
        adapter.getString("AlteraAtributosSubFlow.flowVars"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.subFlowVars"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.varType"), //$NON-NLS-1$ 
    };

    _activitiesDesc = new String[] {
        adapter.getString("AlteraAtributosSubFlow.activity.originalFlow"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.activity.subFlow"), //$NON-NLS-1$ 
    };
    _SimNaoDesc = new String[] {
        adapter.getString("AlteraAtributosSubFlow.no"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosSubFlow.yes"), //$NON-NLS-1$ 
    };

    initMaps();
  }

  private void initMaps() {
    for(int i = 0; i < subflowRowNames.length; i++) {
      rowToDescMapping.put(subflowRowNames[i], subflowRowNamesDesc[i]);
      descToRowMapping.put(subflowRowNamesDesc[i], subflowRowNames[i]);
    }
    rowToDescMapping.put(sCHOOSE, sCHOOSE_DESC);
    descToRowMapping.put(sCHOOSE_DESC, sCHOOSE);

    for(int i = 0; i < _SimNao.length; i++) {
      snToDescMapping.put(_SimNao[i], _SimNaoDesc[i]);
      descToSnMapping.put(_SimNaoDesc[i], _SimNao[i]);
    }

    for(int i = 0; i < _activities.length; i++) {
      actToDescMapping.put(_activities[i], _activitiesDesc[i]);
      descToActMapping.put(_activitiesDesc[i], _activities[i]);
    }
  }


  private pt.iflow.api.xml.codegen.flow.XmlFlow _xmlflow = null;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] chooseSubFlow, inputFields, outputFields;

  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    String[][] newAttributes = new String[chooseSubFlow.length + 2 + 2 * inputFields.length + 2 * outputFields.length
                                          + inputFields.length][3];
    int n = 0;

    newAttributes[n][0] = "M" + descToRowMapping.get(chooseSubFlow[0][0]); //$NON-NLS-1$
    newAttributes[n][1] = chooseSubFlow[0][1];
    newAttributes[n][2] = chooseSubFlow[0][1];
    n++;
    newAttributes[n][0] = "A" + "foobarfoobar"; //$NON-NLS-1$
    newAttributes[n][1] = _SimNaoDesc[1];
    newAttributes[n][2] = _SimNaoDesc[1];
    n++;
    newAttributes[n][0] = "B" + "foobarfoobar"; //$NON-NLS-1$
    newAttributes[n][1] = _activitiesDesc[1];
    newAttributes[n][2] = _activitiesDesc[1];
    n++;

    for (int i = 0; i < inputFields.length; i++) {
      newAttributes[n][0] = "Isubflow" + i; //$NON-NLS-1$
      newAttributes[n][1] = inputFields[i][1];
      n++;
      newAttributes[n][0] = "Ibigflow" + i; //$NON-NLS-1$
      if (inputFields[i][0] == null)
        newAttributes[n][1] = ""; //$NON-NLS-1$
      else
        newAttributes[n][1] = inputFields[i][0];
      n++;
    }
    for (int i = 0; i < outputFields.length; i++) {
      newAttributes[n][0] = "Osubflow" + i; //$NON-NLS-1$
      newAttributes[n][1] = outputFields[i][1];
      n++;
      newAttributes[n][0] = "Obigflow" + i; //$NON-NLS-1$
      if (outputFields[i][0] == null)
        newAttributes[n][1] = ""; //$NON-NLS-1$
      else
        newAttributes[n][1] = outputFields[i][0];
      n++;
    }

    // var types
    for (int i = 0; i < inputFields.length; i++) {
      newAttributes[n][0] = "Tvartype" + i; //$NON-NLS-1$
      newAttributes[n][1] = inputFields[i][2];
      n++;
    }
    return newAttributes;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    if(null == adapter.getRepository()) {
      String wMsg = adapter.getString("Block.offline.msg");
      String wTitle = adapter.getString("Block.offline.title");
      JOptionPane.showMessageDialog(getParent(), wMsg, wTitle, JOptionPane.ERROR_MESSAGE);
      exitStatus = EXIT_STATUS_CANCEL;
      dispose();
      return;
    }

    setTitle(adapter.getString("AlteraAtributosSubFlow.title")); //$NON-NLS-1$

    int ifCount = 0, ofCount = 0, tfCount = 0;
    for (int i = 0; i < atributos.size(); i++) {
      Atributo a = (Atributo) atributos.get(i);
      if (a.getNome().charAt(0) == 'I')
        ifCount++;
      else if (a.getNome().charAt(0) == 'O')
        ofCount++;
      else if (a.getNome().charAt(0) == 'T')
        tfCount++;
    }
    ifCount /= 2;
    ofCount /= 2;

    inputFields = new String[ifCount][fieldsColumnNames.length];
    outputFields = new String[ofCount][fieldsColumnNames.length];
    chooseSubFlow = new String[subflowRowNames.length][subflowColumnNames.length];
    jtableChooseSubFlow = new MyJTableX(chooseSubFlow, subflowColumnNames);
    jtableInput = new MyJTableX(inputFields, fieldsColumnNames);
    jtableOutput = new MyJTableX(outputFields, fieldsColumnNames);

    MyTableModel modelChooseSubFlow = new MyTableModel(subflowColumnNames, chooseSubFlow);
    for(int i = 0; i < subflowColumnNames.length; i++)
      modelChooseSubFlow.setColumnEditable(i, false);
    modelChooseSubFlow.setColumnEditable(1, true);

    jtableChooseSubFlow.setModel(modelChooseSubFlow);

    MyTableModel modelInput = new MyTableModel(fieldsColumnNames, inputFields);
    for(int i = 0; i < fieldsColumnNames.length; i++)
      modelInput.setColumnEditable(i, false);
    modelInput.setColumnEditable(0, true);

    jtableInput.setModel(modelInput);

    MyTableModel modelOutput = new MyTableModel(fieldsColumnNames, outputFields);
    for(int i = 0; i < fieldsColumnNames.length; i++)
      modelOutput.setColumnEditable(i, false);
    modelOutput.setColumnEditable(0, true);

    jtableOutput.setModel(modelOutput);

    jtableChooseSubFlow.setRowSelectionAllowed(false);
    jtableChooseSubFlow.setColumnSelectionAllowed(false);

    jtableInput.setRowSelectionAllowed(false);
    jtableInput.setColumnSelectionAllowed(false);

    jtableOutput.setRowSelectionAllowed(false);
    jtableOutput.setColumnSelectionAllowed(false);

    MyColumnEditorModel rmSubFlow = new MyColumnEditorModel();
    MyColumnEditorModel rmInput = new MyColumnEditorModel();
    MyColumnEditorModel rmOutput = new MyColumnEditorModel();

    jtableChooseSubFlow.setMyColumnEditorModel(rmSubFlow);
    jtableInput.setMyColumnEditorModel(rmInput);
    jtableOutput.setMyColumnEditorModel(rmOutput);

    jtableChooseSubFlow.setValueAt(subflowRowNamesDesc[0], 0, 0);
    jtableChooseSubFlow.setValueAt(sCHOOSE, 0, 1);
    // jtableChooseSubFlow.setValueAt(subflowRowNamesDesc[1], 1, 0);
    // jtableChooseSubFlow.setValueAt(_activities[0], 1, 1);
    // jtableChooseSubFlow.setValueAt(subflowRowNamesDesc[2], 2, 0);
    // jtableChooseSubFlow.setValueAt(_SimNao[0], 2, 1);

    // TODO: add support for new subflow catalogue vars!!!!! now only accounts
    // for existing/configured vars
    for (int i = 0; i < atributos.size(); i++) {
      Atributo a = (Atributo) atributos.get(i);
      if (a.getNome().charAt(0) == 'I') {
        int pos = -1;
        try {
          pos = Integer.parseInt(a.getNome().substring(8));
        } catch (NumberFormatException nfe) {
        }

        if (a.getNome().substring(1, 8).equals("bigflow")) { //$NON-NLS-1$
          jtableInput.setValueAt(a.getValor(), pos, 0);
        } else {
          jtableInput.setValueAt(a.getValor(), pos, 1);
        }
      } else if (a.getNome().charAt(0) == 'O') {
        int pos = -1;
        try {
          pos = Integer.parseInt(a.getNome().substring(8));
        } catch (NumberFormatException nfe) {
        }

        if (a.getNome().substring(1, 8).equals("bigflow")) { //$NON-NLS-1$
          jtableOutput.setValueAt(a.getValor(), pos, 0);
        } else {
          jtableOutput.setValueAt(a.getValor(), pos, 1);
        }
      } else if (a.getNome().charAt(0) == 'M' && null != a.getValor()) {
        //jtableChooseSubFlow.setValueAt(new String(a.nome.substring(1)), 0, 0);
        jtableChooseSubFlow.setValueAt(a.getValor(), 0, 1);
        // } else if (a.getNome().charAt(0) == 'A' && null != a.getValor()) {
        // //jtableChooseSubFlow.setValueAt(new String(a.nome.substring(1)), 1, 0);
        // jtableChooseSubFlow.setValueAt(a.getValor(), 1, 1);
        // } else if (a.getNome().charAt(0) == 'B' && null != a.getValor()) {
        // //jtableChooseSubFlow.setValueAt(new String(a.nome.substring(1)), 2, 0);
        // jtableChooseSubFlow.setValueAt(a.getValor(), 2, 1);
      } else if (a.getNome().charAt(0) == 'T' && null != a.getValor()) {
        try {
          int pos = -1;
          pos = Integer.parseInt(a.getNome().substring(8));
          jtableInput.setValueAt(a.getValor(), pos, 2);
          jtableOutput.setValueAt(a.getValor(), pos, 2);
        } catch (NumberFormatException nfe) {
        }
      }
    }

    // subflow
    String subflow = (String) jtableChooseSubFlow.getValueAt(0, 1);
    JComboBox _jcbSubFlow = new JComboBox();
    _jcbSubFlow.addItem(sCHOOSE_DESC);
    int typeChoice = 0;
    for (int i = 0; _subFlows != null && i < _subFlows.length; i++) {
      _jcbSubFlow.addItem(_subFlows[i]);
      if (_subFlows[i].equals(subflow)) {
        typeChoice = i + 1;
      }
    }
    _jcbSubFlow.setSelectedIndex(typeChoice);
    jtableChooseSubFlow.setValueAt(_jcbSubFlow.getSelectedItem(), 0, 1);
    _jcbSubFlow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jcbSubFlowActionPerformed(e);
      }
    });
    okButton.setEnabled(typeChoice!=0);
    assignButton.setEnabled(typeChoice != 0);

    // activities
    // Object activity = jtableChooseSubFlow.getValueAt(1, 1);
    // if(null == activity) activity = _activitiesDesc[0];
    // JComboBox _jcbActivity = new JComboBox();
    // typeChoice = 0;
    // for (int i = 0; _activities != null && i < _activities.length; i++) {
    // _jcbActivity.addItem(_activitiesDesc[i]);
    // if (_activities[i].equals(activity)) {
    // typeChoice = i;
    // }
    // }
    // _jcbActivity.setSelectedIndex(typeChoice);
    // jtableChooseSubFlow.setValueAt(_jcbActivity.getSelectedItem(), 1, 1);
    //
    // // bifurcar
    // String fork = (String) jtableChooseSubFlow.getValueAt(2, 1);
    // JComboBox _jcbFork = new JComboBox();
    // typeChoice = 0;
    // for (int i = 0; _SimNao != null && i < _SimNao.length; i++) {
    // _jcbFork.addItem(_SimNaoDesc[i]);
    // if (_SimNao[i].equals(fork)) {
    // typeChoice = i;
    // }
    // }
    // _jcbFork.setSelectedIndex(typeChoice);
    // jtableChooseSubFlow.setValueAt(_jcbFork.getSelectedItem(), 2, 1);

    DefaultCellEditor dce0 = new DefaultCellEditor(_jcbSubFlow);
    rmSubFlow.addEditorForCell(0, 1, dce0);

    // DefaultCellEditor dce1 = new DefaultCellEditor(_jcbActivity);
    // rmSubFlow.addEditorForCell(1, 1, dce1);
    //
    // DefaultCellEditor dce2 = new DefaultCellEditor(_jcbFork);
    // rmSubFlow.addEditorForCell(2, 1, dce2);

    // input
    for (int i = 0; i < inputFields.length; i++) {
      JTextField jtf1 = new JTextField((String) inputFields[i][0]);
      jtf1.setSelectionColor(Color.red);
      jtf1.setSelectedTextColor(Color.white);
      DefaultCellEditor cce = new DefaultCellEditor(jtf1);
      cce.setClickCountToStart(2);
      rmInput.addEditorForColumn(0, cce);

      JTextField jtf2 = new JTextField((String) inputFields[i][1]);
      jtf2.setSelectionColor(Color.red);
      jtf2.setSelectedTextColor(Color.white);
      DefaultCellEditor mce = new DefaultCellEditor(jtf2);
      mce.setClickCountToStart(2);
      rmInput.addEditorForColumn(1, mce);
    }

    // output
    for (int i = 0; i < outputFields.length; i++) {
      JTextField jtf1 = new JTextField((String) outputFields[i][0]);
      jtf1.setSelectionColor(Color.red);
      jtf1.setSelectedTextColor(Color.white);
      DefaultCellEditor cce = new DefaultCellEditor(jtf1);
      cce.setClickCountToStart(2);
      rmOutput.addEditorForColumn(0, cce);

      JTextField jtf2 = new JTextField((String) outputFields[i][1]);
      jtf2.setSelectionColor(Color.red);
      jtf2.setSelectedTextColor(Color.white);
      DefaultCellEditor mce = new DefaultCellEditor(jtf2);
      mce.setClickCountToStart(2);
      rmOutput.addEditorForColumn(1, mce);
    }

    /* criar botoes e arranjar dialogo */
    jbInit();
    pack();

    this.setSize(500, 350);
    setVisible(true);
  }

  /**
   * Iniciar caixa de dialogo
   * 
   * @throws Exception
   */
  private void jbInit() {
    JPanel panel1 = new JPanel();
    JPanel jPanel3 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JTabbedPane jtp = new JTabbedPane();
    BorderLayout borderLayout1 = new BorderLayout();

    // configurar
    panel1.setLayout(borderLayout1);

    /* botao OK */
    okButton.setText(OK);

    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });

    /* botao cancelar */
    cancelButton.setText(Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });

    /* botao auto assignar */
    assignButton.setText("Assignar Auto");

    assignButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        assignButtonActionPerformed(e);
      }
    });

    jtableInput.setRowSelectionAllowed(false);
    this.setModal(true);

    /* paineis */
    JPanel aux1 = new JPanel();
    aux1.setSize(300, 1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(300, 1);
    getContentPane().add(aux2, BorderLayout.EAST);

    jScrollPane0.getViewport().add(jtableChooseSubFlow, null);
    jScrollPane1.getViewport().add(jtableInput, null);
    jScrollPane2.getViewport().add(jtableOutput, null);

    getContentPane().add(jScrollPane0);
    getContentPane().add(jScrollPane1);
    getContentPane().add(jScrollPane2);

    jtp.addTab(adapter.getString("AlteraAtributosSubFlow.tab.subflow"), jScrollPane0); //$NON-NLS-1$
    jtp.addTab(adapter.getString("AlteraAtributosSubFlow.tab.input"), jScrollPane1); //$NON-NLS-1$
    //jtp.addTab(adapter.getString("AlteraAtributosSubFlow.tab.output"), jScrollPane2); //$NON-NLS-1$
    getContentPane().add(jtp, BorderLayout.CENTER);

    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(okButton, null);
    jPanel2.add(cancelButton, null);
    jPanel2.add(assignButton, null);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);

    repaint();
  }

  private void jcbSubFlowActionPerformed(ActionEvent e) {

    DefaultCellEditor dceM = (DefaultCellEditor) jtableChooseSubFlow.getMyColumnEditorModel().getEditor(0, 1);
    JComboBox jcbM = (JComboBox) dceM.getComponent();

    int pos = jcbM.getSelectedIndex();
    if(pos == 0) { // sCHOOSE selected
      okButton.setEnabled(false);
      assignButton.setEnabled(false);
      return;
    }
    okButton.setEnabled(true);
    assignButton.setEnabled(true);

    // if selected host doesn't change the type and code remains the same
    String subflow = (String) jcbM.getSelectedItem();

    if (subflow.equals(sCHOOSE) || adapter.getRepository() == null) {

      inputFields = new String[0][fieldsColumnNames.length];
      outputFields = new String[0][fieldsColumnNames.length];

    } else {
      byte[] bXml = adapter.getRepository().getSubFlow(subflow);
      try {
        _xmlflow = FlowMarshaller.unmarshal(bXml);
      } catch (ValidationException ve) {
      } catch (MarshalException me) {
      }

      XmlCatalogVars xmlcv = _xmlflow.getXmlCatalogVars();
      if(xmlcv.getXmlAttributeCount() > 0 && xmlcv.getXmlCatalogVarAttributeCount() == 0) {
        inputFields = new String[xmlcv.getXmlAttributeCount()][fieldsColumnNames.length];
        outputFields = new String[xmlcv.getXmlAttributeCount()][fieldsColumnNames.length];
        for (int i = 0; i < xmlcv.getXmlAttributeCount(); i++) {
          XmlAttribute attr = xmlcv.getXmlAttribute(i);
          inputFields[i][1] = attr.getName();
          inputFields[i][2] = attr.getDescription();
          outputFields[i][1] = attr.getName();
          outputFields[i][2] = attr.getDescription();
        }
      } else {
        String[][] inputFieldsTmp = new String[xmlcv.getXmlCatalogVarAttributeCount()][fieldsColumnNames.length];
        String[][] outputFieldsTmp = new String[xmlcv.getXmlCatalogVarAttributeCount()][fieldsColumnNames.length];

        for (int i = 0; i < xmlcv.getXmlCatalogVarAttributeCount(); i++) {
          XmlCatalogVarAttribute attr = xmlcv.getXmlCatalogVarAttribute(i);
          inputFieldsTmp[i][0] = (inputFields.length == 0 || i >= inputFields.length) ? "" : inputFields[i][0];
          inputFieldsTmp[i][1] = attr.getName();
          inputFieldsTmp[i][2] = attr.getDataType();

          outputFieldsTmp[i][0] = (outputFields.length == 0 || i >= outputFields.length) ? "" : outputFields[i][0];
          outputFieldsTmp[i][1] = attr.getName();
          outputFieldsTmp[i][2] = attr.getDataType();
        }
        inputFields = inputFieldsTmp;
        outputFields = outputFieldsTmp;
      }
    }

    jtableInput = new MyJTableX(inputFields, fieldsColumnNames);
    MyColumnEditorModel rmInput = new MyColumnEditorModel();
    jtableInput.setMyColumnEditorModel(rmInput);
    jtableOutput = new MyJTableX(outputFields, fieldsColumnNames);
    MyColumnEditorModel rmOutput = new MyColumnEditorModel();
    jtableOutput.setMyColumnEditorModel(rmOutput);

    MyTableModel modelInput = new MyTableModel(fieldsColumnNames, inputFields);
    for(int i = 0; i < fieldsColumnNames.length; i++)
      modelInput.setColumnEditable(i, false);
    modelInput.setColumnEditable(0, true);

    jtableInput.setModel(modelInput);

    MyTableModel modelOutput = new MyTableModel(fieldsColumnNames, outputFields);
    for(int i = 0; i < fieldsColumnNames.length; i++)
      modelOutput.setColumnEditable(i, false);
    modelOutput.setColumnEditable(0, true);

    jtableOutput.setModel(modelOutput);


    jScrollPane1.getViewport().add(jtableInput, null);
    jScrollPane2.getViewport().add(jtableOutput, null);

    for (int i = 0; i < inputFields.length; i++) {
      JTextField jtf1 = new JTextField((String) inputFields[i][0]);
      jtf1.setSelectionColor(Color.red);
      jtf1.setSelectedTextColor(Color.white);
      DefaultCellEditor cce = new DefaultCellEditor(jtf1);
      cce.setClickCountToStart(2);
      rmInput.addEditorForColumn(0, cce);

      JTextField jtf2 = new JTextField((String) inputFields[i][1]);
      jtf2.setSelectionColor(Color.red);
      jtf2.setSelectedTextColor(Color.white);
      DefaultCellEditor mce = new DefaultCellEditor(jtf2);
      mce.setClickCountToStart(2);
      rmInput.addEditorForColumn(1, mce);
    }
    for (int i = 0; i < outputFields.length; i++) {
      JTextField jtf1 = new JTextField((String) outputFields[i][0]);
      jtf1.setSelectionColor(Color.red);
      jtf1.setSelectedTextColor(Color.white);
      DefaultCellEditor cce = new DefaultCellEditor(jtf1);
      cce.setClickCountToStart(2);
      rmOutput.addEditorForColumn(0, cce);

      JTextField jtf2 = new JTextField((String) outputFields[i][1]);
      jtf2.setSelectionColor(Color.red);
      jtf2.setSelectedTextColor(Color.white);
      DefaultCellEditor mce = new DefaultCellEditor(jtf2);
      mce.setClickCountToStart(2);
      rmOutput.addEditorForColumn(1, mce);
    }
  }

  /* OK */
  private void okButtonActionPerformed(ActionEvent e) {
    jtableChooseSubFlow.stopEditing();
    jtableInput.stopEditing();
    jtableOutput.stopEditing();

    // choose SubFlow
    chooseSubFlow[0][1] = (String)jtableChooseSubFlow.getValueAt(0,1);

    // chooseSubFlow = new String[3][1];
    // chooseSubFlow[0][0] = subflowRowNames[0];
    // chooseSubFlow[0][1] = (String)jtableChooseSubFlow.getValueAt(0,1);
    // chooseSubFlow[1][0] = subflowRowNames[1];
    // chooseSubFlow[1][1] = "Original Flow";
    // chooseSubFlow[2][0] = subflowRowNames[2];
    // chooseSubFlow[2][1] = "No";

    exitStatus = EXIT_STATUS_OK;
    setVisible(false);
    dispose();
  }

  /* Cancelar */
  private void cancelButtonActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    setVisible(false);
    dispose();
  }

  /* Auto assignar */
  private void assignButtonActionPerformed(ActionEvent e) {
    Collection<Atributo> catalogo = desenho.getCatalogue();

    for (int i = 0; i < inputFields.length; i++)
      for (Atributo variavel : catalogo) {
        if (variavel.getNome().equals(inputFields[i][1]) && variavel.getDataType().equals(inputFields[i][2])
            && "".equals(inputFields[i][0]))
          inputFields[i][0] = variavel.getNome();
        if (variavel.getNome().equals(inputFields[i][1]) && variavel.getDataType().equals(inputFields[i][2])
            && "".equals(inputFields[i][0]))
          inputFields[i][0] = variavel.getNome();
      }
    repaint();
  }
}
