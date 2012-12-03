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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosSincronizacao extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 6220908361190831246L;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  private final String _dataSetLabel;
  private final String _scheduleLabel;

  // property name to share between editor and iflow
  public final static String _dataSet = "DataSet"; //$NON-NLS-1$
  public final static String _schedule = "Schedule"; //$NON-NLS-1$

  // map this
  static String _sOnFirst = "Utilizadores SubProc 1"; //$NON-NLS-1$
  static String _sOnSecond = "Utilizadores SubProc 2"; //$NON-NLS-1$
  static String _sOnBoth = "Utilizadores SubProc 1 e 2"; //$NON-NLS-1$
  // into this
  private final String _sOnFirstDesc;
  private final String _sOnSecondDesc;
  private final String _sOnBothDesc;
  static final String[] columnSchedule = { _sOnFirst, _sOnSecond, _sOnBoth };
  JComboBox _jcbSchedule = new JComboBox(columnSchedule);

  private final String[] columnTitles;
  // map this
  static String _sDataSet1 = "DataSet 1 (entrada superior)"; //$NON-NLS-1$
  static String _sDataSet2 = "DataSet 2 (entrada inferior)"; //$NON-NLS-1$
  // Into this
  private final String _sDataSet1Desc;
  private final String _sDataSet2Desc;

  static final String[] columnDataSets = { _sDataSet1, _sDataSet2 };
  JComboBox _jcbDataSets = new JComboBox(columnDataSets);

  static final String varName = "dataset_"; //$NON-NLS-1$

  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  private final HashMap<String,String> dsDescToIFlowMap = new HashMap<String, String>();
  private final HashMap<String,String> iFlowToDsDescMap = new HashMap<String, String>();
  private final HashMap<String,String> typeDescToIFlowMap = new HashMap<String, String>();
  private final HashMap<String,String> iFlowToTypeDescMap = new HashMap<String, String>();


  public AlteraAtributosSincronizacao(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosSincronizacao.title"), true); //$NON-NLS-1$
    
    _dataSetLabel = adapter.getString("AlteraAtributosSincronizacao.mainDataSet"); //$NON-NLS-1$
    _scheduleLabel = adapter.getString("AlteraAtributosSincronizacao.scheduleType"); //$NON-NLS-1$

    _sOnFirstDesc = adapter.getString("AlteraAtributosSincronizacao.subprocUsers1"); //$NON-NLS-1$
    _sOnSecondDesc = adapter.getString("AlteraAtributosSincronizacao.subprocUsers2"); //$NON-NLS-1$
    _sOnBothDesc = adapter.getString("AlteraAtributosSincronizacao.subprocUsers12"); //$NON-NLS-1$

    columnTitles = new String[]{ adapter.getString("AlteraAtributosSincronizacao.varsToImport") }; //$NON-NLS-1$

    _sDataSet1Desc = adapter.getString("AlteraAtributosSincronizacao.dataSet1"); //$NON-NLS-1$
    _sDataSet2Desc = adapter.getString("AlteraAtributosSincronizacao.dataSet2"); //$NON-NLS-1$

    initMaps();
  }


  private void initMaps() {
    dsDescToIFlowMap.put(_sDataSet1Desc, _sDataSet1);
    dsDescToIFlowMap.put(_sDataSet2Desc, _sDataSet2);

    iFlowToDsDescMap.put(_sDataSet1, _sDataSet1Desc);
    iFlowToDsDescMap.put(_sDataSet2, _sDataSet2Desc);

    typeDescToIFlowMap.put(_sOnFirstDesc, _sOnFirst);
    typeDescToIFlowMap.put(_sOnSecondDesc, _sOnSecond);
    typeDescToIFlowMap.put(_sOnBothDesc, _sOnBoth);

    iFlowToTypeDescMap.put(_sOnFirst, _sOnFirstDesc);
    iFlowToTypeDescMap.put(_sOnSecond, _sOnSecondDesc);
    iFlowToTypeDescMap.put(_sOnBoth, _sOnBothDesc);
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
    int condCount = 0, c = 0;
    String stmp = null;

    // count them
    for (int i = 0; i < data.length; i++) {
      stmp = (String) data[i][0];
      if (stmp != null && !stmp.equals("")) { //$NON-NLS-1$
        condCount++;
      }
    }
    // add 2 more for the type and dataSet
    String[][] newAttributes = new String[2 + condCount][2];

    for (int i = 0; i < data.length; i++) {
      stmp = (String) data[i][0];
      if (stmp != null && !stmp.equals("")) { //$NON-NLS-1$
        newAttributes[c][0] = new String(varName + c);
        newAttributes[c][1] = stmp;
        c++;
      }
    }

    // now 'Initial Line' and 'Column Label Type'
    newAttributes[c][0] = AlteraAtributosSincronizacao._dataSet;
    newAttributes[c][1] = dsDescToIFlowMap.get(_jcbDataSets.getSelectedItem());
    newAttributes[c + 1][0] = AlteraAtributosSincronizacao._schedule;
    newAttributes[c + 1][1] = typeDescToIFlowMap.get(_jcbSchedule.getSelectedItem());

    return newAttributes;
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
    int condCount = 0;

    String name = null;
    String value = null;
    Atributo atributo = null;
    String stmp = null;
    String stmp2 = null;
    boolean bColDataSet = false;
    boolean bColSchedule = false;

    if (atributos.size() > 1) {
      atributo = (Atributo) atributos.get(atributos.size() - 2);
    }
    if (atributo != null) {
      stmp = atributo.getNome();
      if (stmp != null && stmp.equals(AlteraAtributosSincronizacao._dataSet)) {
        stmp2 = atributo.getValor();
        if (stmp2 == null)
          stmp2 = ""; //$NON-NLS-1$
        _jcbDataSets.setSelectedItem(iFlowToDsDescMap.get(stmp2));
        bColDataSet = true;
      } else {
        _jcbDataSets.setSelectedItem(_sDataSet1Desc);
        bColDataSet = false;
      }
    }

    if (atributos.size() > 0) {
      atributo = (Atributo) atributos.get(atributos.size() - 1);
    } else
      atributo = null;
    if (atributo != null) {
      stmp = atributo.getNome();
      if (stmp != null && stmp.equals(AlteraAtributosSincronizacao._schedule)) {
        stmp2 = atributo.getValor();
        if (stmp2 == null)
          stmp2 = ""; //$NON-NLS-1$
        _jcbSchedule.setSelectedItem(iFlowToTypeDescMap.get(stmp2));
        bColSchedule = true;
      } else {
        _jcbSchedule.setSelectedItem(_sOnFirstDesc);
        bColSchedule = false;
      }
    }

    if (!bColDataSet && !bColSchedule) {
      atributo = adapter.newAtributo(AlteraAtributosSincronizacao._dataSet, AlteraAtributosSincronizacao._sDataSet1,
          _dataSetLabel);
      atributos.add(atributo);
      atributo = adapter.newAtributo(AlteraAtributosSincronizacao._schedule, AlteraAtributosSincronizacao._sOnFirst,
          _scheduleLabel);
      atributos.add(atributo);
    } else if (!bColDataSet) {
      atributo = adapter.newAtributo(AlteraAtributosSincronizacao._dataSet, AlteraAtributosSincronizacao._sDataSet1,
          _dataSetLabel);
      atributos.add(atributo);
    } else if (!bColSchedule) {
      atributo = adapter.newAtributo(AlteraAtributosSincronizacao._schedule, AlteraAtributosSincronizacao._sOnFirst,
          _scheduleLabel);
      atributos.add(atributo);
    }

    for (int i = 0; i < atributos.size(); i++) {
      atributo = atributos.get(i);
      if (atributo == null)
        continue;
      name = atributo.getNome();
      if ((name != null) && (name.length() > varName.length()) && name.substring(0, varName.length()).equals(varName)) {
        condCount++;
      }
    }

    data = new Object[condCount][1];
    HashMap<String, String> hmDataSet = new HashMap<String, String>();

    for (int i = 0; i < atributos.size(); i++) {
      atributo = (Atributo) atributos.get(i);
      if (atributo == null)
        continue;
      name = atributo.getNome();
      value = atributo.getValor();
      if (value == null)
        value = ""; //$NON-NLS-1$

      if (name.length() > varName.length() && name.substring(0, varName.length()).equals(varName)) {
        hmDataSet.put(name, value);
      }
    }

    for (int i = 0; i < condCount; i++) {
      data[i][0] = hmDataSet.get(varName + i);
    }

    jTable1 = new MyJTableX(data, columnTitles);

    jTable1.setModel(new MyTableModel(columnTitles, data));

    jTable1.setRowSelectionAllowed(true);
    jTable1.setColumnSelectionAllowed(false);

    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    JTextField jtf1 = new JTextField();
    jtf1.setSelectionColor(Color.red);
    jtf1.setSelectedTextColor(Color.white);
    DefaultCellEditor cce = new DefaultCellEditor(jtf1);
    cce.setClickCountToStart(2);
    rm.addEditorForColumn(0, cce);

    JTextField jtf2 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor mce = new DefaultCellEditor(jtf2);
    mce.setClickCountToStart(2);
    rm.addEditorForColumn(1, mce);

    jbInit();

    this.setSize(600, 600);
    setVisible(true);
  }

  /**
   * jbInit
   * 
   * @throws Exception
   */
  void jbInit() {
    panel1.setLayout(borderLayout1);
    jButton1.setText(OK);

    jButton3.setText("+"); //$NON-NLS-1$
    jButton4.setText("-"); //$NON-NLS-1$

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    jButton2.setText(Cancelar);
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });

    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });

    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton4_actionPerformed(e);
      }
    });

    // 'Initial Line'
    GridBagLayout sGridbag = new GridBagLayout();
    GridBagConstraints sC = new GridBagConstraints();
    sC.fill = GridBagConstraints.HORIZONTAL;
    JPanel jPanel = new JPanel();
    jPanel.setLayout(sGridbag);

    // 'DataSet'
    JLabel jLabel = null;
    jLabel = new JLabel(_dataSetLabel);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(_jcbDataSets);
    sGridbag.setConstraints(jLabel, sC);
    jPanel.add(jLabel);
    // separator
    JPanel sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(_jcbDataSets, sC);
    jPanel.add(_jcbDataSets);
    sC.gridwidth = 1;

    // 'Schedule'
    jLabel = null;
    jLabel = new JLabel(_scheduleLabel);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(_jcbSchedule);
    sGridbag.setConstraints(jLabel, sC);
    jPanel.add(jLabel);
    // separator
    sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(_jcbSchedule, sC);
    jPanel.add(_jcbSchedule);
    sC.gridwidth = 1;

    /* table */
    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    JPanel jtmp = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    jtmp.setLayout(gridbag);

    // 'Initial Line', 'Column label type'
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(jPanel, c);
    jtmp.add(jPanel);

    sizer = new JPanel();
    sizer.setSize(1, 10);
    gridbag.setConstraints(sizer, c);
    jtmp.add(sizer);

    JScrollPane jScrollPane2 = new JScrollPane();

    JTableHeader jth = jTable1.getTableHeader();
    gridbag.setConstraints(jth, c);
    jScrollPane2.add(jth);
    gridbag.setConstraints(jTable1, c);
    // jScrollPane2.setSize(jScrollPane1.getSize());
    jScrollPane2.getViewport().add(jTable1, null);
    // jScrollPane2.add(jTable1);

    jtmp.add(jScrollPane2);

    JPanel fullPanel = new JPanel();
    fullPanel.add(jtmp, BorderLayout.NORTH);

    /* paineis */
    JPanel aux1 = new JPanel();
    aux1.setSize(100, 1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(100, 1);
    getContentPane().add(aux2, BorderLayout.EAST);

    jScrollPane1.getViewport().add(fullPanel, null);

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);

    jPanel2.add(jButton1, null);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton3, null);
    jPanel2.add(jButton4, null);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);

    dialogComponentResized(null);
    repaint();
  }

  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    dispose();
  }

  /* + */
  void jButton3_actionPerformed(ActionEvent e) {
    // Add a row to the table
    MyTableModel tm = (MyTableModel) jTable1.getModel();
    data = tm.insertRow();
  }

  /* - */
  void jButton4_actionPerformed(ActionEvent e) {
    // Remove a row from the table
    int rowSelected = jTable1.getSelectedRow();
    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel) jTable1.getModel();
      data = tm.removeRow(rowSelected);
    }
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

}
