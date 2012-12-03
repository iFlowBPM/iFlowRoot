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

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosOpenProc
 *
 *  desc: dialogo para alterar atributos do bloco ForwardTo
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pt.iflow.blocks.interfaces.OpenProc;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;


public class AlteraAtributosOpenProc extends AbstractAlteraAtributos  implements AlteraAtributosInterface, OpenProc {
  private static final long serialVersionUID = 2831555702402683562L;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel2 = new JPanel();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JPanel jPanel3 = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private MyJTableX jTable1 = new MyJTableX();

  private JComboBox _jcbType = null;
  private JTextField _jtfFid = null;
  private JTextField _jtfPid = null;
  private JTextField _jtfSubPid = null;
  private JTextField _jtfBid = null;
  private JComboBox _jcbModes = null;
  private JComboBox _jcbUserMode = null;
  private JTextField _jtfUser = null;
  private JTextField _jtfDescription = null;
  private JTextField _jtf2Import = null;
  private JTextField _jtfCreatedPid = null;

  private final String[] AlteraAtributosColumnNames;


  private final String[] openProcessModesDesc;
  private final String[] openProcessTypesDesc;
  private final String[] userModesDesc;

  private final Map<String, String> openProcessModesMap = new HashMap<String, String>();
  private final Map<String, String> openProcessModesRevMap = new HashMap<String, String>();
  private final Map<String, String> userModesMap = new HashMap<String, String>();
  private final Map<String, String> userModesRevMap = new HashMap<String, String>();
  private final Map<String, String> openProcessTypesMap = new HashMap<String, String>();
  private final Map<String, String> openProcessTypesRevMap = new HashMap<String, String>();
  
  private final String sOPEN_PROC_TYPE;
  private final String sOPEN_PROC_FID;
  private final String sOPEN_PROC_PID;
  private final String sOPEN_PROC_SUBPID;
  private final String sOPEN_PROC_MODE;
  private final String sOPEN_PROC_BID;
  private final String sOPEN_PROC_USERMODE;
  private final String sOPEN_PROC_USER;
  private final String sOPEN_PROC_DESCRIPTION;
  private final String sOPEN_PROC_2IMPORT;
  private final String sOPEN_PROC_CREATED_PID;

  private final static String sOPEN_PROC_TYPE_ATTR = "TYPE";
  private final static String sOPEN_PROC_FID_ATTR = "FID";
  private final static String sOPEN_PROC_PID_ATTR = "PID";
  private final static String sOPEN_PROC_SUBPID_ATTR = "SUBPID";
  private final static String sOPEN_PROC_MODE_ATTR = "MODE";
  private final static String sOPEN_PROC_BID_ATTR = "BID";
  private final static String sOPEN_PROC_USERMODE_ATTR = "USERMODE"; 
  private final static String sOPEN_PROC_USER_ATTR = "USER";
  private final static String sOPEN_PROC_DESCRIPTION_ATTR = "DESCRIPTION"; 
  private final static String sOPEN_PROC_2IMPORT_ATTR = "2IMPORT";
  private final static String sOPEN_PROC_CREATED_PID_ATTR = "CREATED_PID";
  
  public final static int nOPEN_PROC_TYPE        = 0;
  public final static int nOPEN_PROC_FID         = 1;
  public final static int nOPEN_PROC_PID         = 2;
  public final static int nOPEN_PROC_SUBPID      = 3;
  public final static int nOPEN_PROC_MODE        = 4;
  public final static int nOPEN_PROC_BID         = 5;
  public final static int nOPEN_PROC_USERMODE    = 6;
  public final static int nOPEN_PROC_USER        = 7;
  public final static int nOPEN_PROC_DESCRIPTION = 8;
  public final static int nOPEN_PROC_2IMPORT     = 9;
  public final static int nOPEN_PROC_CREATED_PID = 10;


  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data ;

  public AlteraAtributosOpenProc(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$

    AlteraAtributosColumnNames = new String [] {
        adapter.getString("AlteraAtributosOpenProc.name.columnlabel"), //$NON-NLS-1$ 
        adapter.getString("AlteraAtributosOpenProc.value.columnlabel"), //$NON-NLS-1$ 
    };


    // check if match with ones in Uniflow's pt.iflow.blocks.BlockForwardTo
    openProcessModesDesc = new String [] {
        adapter.getString("AlteraAtributosOpenProc.openmode.passThru"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosOpenProc.openmode.jumpTo"), //$NON-NLS-1$
    };
    openProcessTypesDesc = new String [] {
        adapter.getString("AlteraAtributosOpenProc.import.existing"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosOpenProc.open.new"), //$NON-NLS-1$
    };
    userModesDesc = new String [] {
        adapter.getString("AlteraAtributosOpenProc.mode.creator"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosOpenProc.mode.profile"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosOpenProc.mode.profiletext"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosOpenProc.mode.user"), //$NON-NLS-1$
    };

    sOPEN_PROC_TYPE = adapter.getString("AlteraAtributosOpenProc.open.type"); //$NON-NLS-1$
    sOPEN_PROC_FID = adapter.getString("AlteraAtributosOpenProc.flowid"); //$NON-NLS-1$
    sOPEN_PROC_PID = adapter.getString("AlteraAtributosOpenProc.pid"); //$NON-NLS-1$
    sOPEN_PROC_SUBPID = adapter.getString("AlteraAtributosOpenProc.subpid"); //$NON-NLS-1$
    sOPEN_PROC_MODE = adapter.getString("AlteraAtributosOpenProc.open.mode"); //$NON-NLS-1$
    sOPEN_PROC_BID = adapter.getString("AlteraAtributosOpenProc.blockid"); //$NON-NLS-1$
    sOPEN_PROC_USERMODE = adapter.getString("AlteraAtributosOpenProc.openin"); //$NON-NLS-1$
    sOPEN_PROC_USER = adapter.getString("AlteraAtributosOpenProc.user_profile"); //$NON-NLS-1$
    sOPEN_PROC_DESCRIPTION = adapter.getString("AlteraAtributosOpenProc.open.description"); //$NON-NLS-1$
    sOPEN_PROC_2IMPORT = adapter.getString("AlteraAtributosOpenProc.import.variables"); //$NON-NLS-1$
    sOPEN_PROC_CREATED_PID = adapter.getString("AlteraAtributosOpenProc.createdPid"); //$NON-NLS-1$


    initMaps();
  }
  
  
  private void initMaps() {
    for(int i = 0; i < openProcessModes.length; i++) {
      openProcessModesMap.put(openProcessModes[i], openProcessModesDesc[i]);
      openProcessModesRevMap.put(openProcessModesDesc[i], openProcessModes[i]);
    }
      
    for(int i = 0; i < userModes.length; i++) {
      userModesMap.put(userModes[i], userModesDesc[i]);
      userModesRevMap.put(userModesDesc[i], userModes[i]);
    }
    
    for(int i = 0; i < openProcessTypes.length; i++) {
      openProcessTypesMap.put(openProcessTypes[i], openProcessTypesDesc[i]);
      openProcessTypesRevMap.put(openProcessTypesDesc[i], openProcessTypes[i]);
    }
  }
  

  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    data[nOPEN_PROC_TYPE][1] = openProcessTypesRevMap.get(data[nOPEN_PROC_TYPE][1]);
    data[nOPEN_PROC_MODE][1] = openProcessModesRevMap.get(data[nOPEN_PROC_MODE][1]);
    data[nOPEN_PROC_USERMODE][1] = userModesRevMap.get(data[nOPEN_PROC_USERMODE][1]);

    return data;
  }


  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    int size = atributos.size();
    if (size < 11) {
      size = 11;
    }
    data=new String[size][3];

    Atributo atr = null;
    String sName = null;
    String sVal = null;
    String mappedValue = null;

    HashMap<String, String> hmData = new HashMap<String, String>();

    for (int i=0; i < atributos.size(); i++) {

      atr = (Atributo)atributos.get(i);

      if (atr == null) continue;

      sName = atr.getNome();

      sVal = atr.getValor();
      if (sVal == null) sVal = ""; //$NON-NLS-1$

      hmData.put(sName, sVal);
    }

    // TYPE
    sVal = getAttr(hmData, sOPEN_PROC_TYPE_ATTR, sOPEN_PROC_TYPE);
    if (sVal.equals("")) { //$NON-NLS-1$
      sVal = openProcessTypes[0];
    }
    mappedValue = openProcessTypesMap.get(sVal);
    if(null == mappedValue)
      mappedValue = openProcessTypesDesc[0];

    data[nOPEN_PROC_TYPE][0] = AlteraAtributosOpenProc.sOPEN_PROC_TYPE_ATTR;
    data[nOPEN_PROC_TYPE][1] = mappedValue;
    data[nOPEN_PROC_TYPE][2] = sOPEN_PROC_TYPE;
    this._jcbType = new JComboBox(openProcessTypesDesc);
    try {
      this._jcbType.setSelectedItem(mappedValue);
    }
    catch (Exception ei) {
      adapter.log("error", ei);
    }
    this._jcbType.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
          String sType = (String)evt.getItem();
          toggleImportType(sType.equals(openProcessTypes[0]));
        }
      }
    });


    // FID
    sVal = getAttr(hmData, sOPEN_PROC_FID_ATTR, sOPEN_PROC_FID);
    data[nOPEN_PROC_FID][0] = AlteraAtributosOpenProc.sOPEN_PROC_FID_ATTR;
    data[nOPEN_PROC_FID][1] = sVal;
    data[nOPEN_PROC_FID][2] = sOPEN_PROC_FID;
    this._jtfFid = new JTextField(sVal);


    // PID
    sVal = getAttr(hmData, sOPEN_PROC_PID_ATTR, sOPEN_PROC_PID);
    data[nOPEN_PROC_PID][0] = AlteraAtributosOpenProc.sOPEN_PROC_PID_ATTR;
    data[nOPEN_PROC_PID][1] = sVal;
    data[nOPEN_PROC_PID][2] = sOPEN_PROC_PID;
    this._jtfPid = new JTextField(sVal);


    // SUBPID
    sVal = getAttr(hmData, sOPEN_PROC_SUBPID_ATTR, sOPEN_PROC_SUBPID);
    data[nOPEN_PROC_SUBPID][0] = AlteraAtributosOpenProc.sOPEN_PROC_SUBPID_ATTR;
    data[nOPEN_PROC_SUBPID][1] = sVal;
    data[nOPEN_PROC_SUBPID][2] = sOPEN_PROC_SUBPID;
    this._jtfSubPid = new JTextField(sVal);


    // MODE
    sVal = getAttr(hmData, sOPEN_PROC_MODE_ATTR, sOPEN_PROC_MODE);
    if (sVal.equals("")) { //$NON-NLS-1$
      sVal = openProcessModes[0];
    }
    mappedValue = openProcessModesMap.get(sVal);
    if(null == mappedValue)
      mappedValue = openProcessModesDesc[0];

    data[nOPEN_PROC_MODE][0] = AlteraAtributosOpenProc.sOPEN_PROC_MODE_ATTR;
    data[nOPEN_PROC_MODE][1] = mappedValue;
    data[nOPEN_PROC_MODE][2] = sOPEN_PROC_MODE;
    this._jcbModes = new JComboBox(openProcessModesDesc);
    try {
      this._jcbModes.setSelectedItem(mappedValue);
    }
    catch (Exception ei) {
      adapter.log("error", ei);
    }


    // BID
    sVal = getAttr(hmData, sOPEN_PROC_BID_ATTR, sOPEN_PROC_BID);
    data[nOPEN_PROC_BID][0] = AlteraAtributosOpenProc.sOPEN_PROC_BID_ATTR;
    data[nOPEN_PROC_BID][1] = sVal;
    data[nOPEN_PROC_BID][2] = sOPEN_PROC_BID;
    this._jtfBid = new JTextField(sVal);


    // USERMODE
    sVal = getAttr(hmData, sOPEN_PROC_USERMODE_ATTR, sOPEN_PROC_USERMODE);
    if (sVal.equals("")) { //$NON-NLS-1$
      sVal = userModes[3]; // utilizador
    }
    mappedValue = userModesMap.get(sVal);
    if(null == mappedValue)
      mappedValue = userModesDesc[3];
    
    data[nOPEN_PROC_USERMODE][0] = AlteraAtributosOpenProc.sOPEN_PROC_USERMODE_ATTR;
    data[nOPEN_PROC_USERMODE][1] = mappedValue;
    data[nOPEN_PROC_USERMODE][2] = sOPEN_PROC_USERMODE;
    this._jcbUserMode = new JComboBox(userModesDesc);
    try {
        this._jcbUserMode.setSelectedItem(mappedValue);
    }
    catch (Exception ei) {
      adapter.log("error", ei);
    }
    this._jcbUserMode.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
          String sMode = (String)evt.getItem();
          toggleUserMode(sMode.equals(userModes[0]));
        }
      }
    });



    // USER
    sVal = getAttr(hmData, sOPEN_PROC_USER_ATTR, sOPEN_PROC_USER);
    data[nOPEN_PROC_USER][0] = AlteraAtributosOpenProc.sOPEN_PROC_USER_ATTR;
    data[nOPEN_PROC_USER][1] = sVal;
    data[nOPEN_PROC_USER][2] =  sOPEN_PROC_USER;
    this._jtfUser = new JTextField(sVal);


    // DESCRIPTION
    sVal = getAttr(hmData, sOPEN_PROC_DESCRIPTION_ATTR, sOPEN_PROC_DESCRIPTION);
    data[nOPEN_PROC_DESCRIPTION][0] = AlteraAtributosOpenProc.sOPEN_PROC_DESCRIPTION_ATTR;
    data[nOPEN_PROC_DESCRIPTION][1] = sVal;
    data[nOPEN_PROC_DESCRIPTION][2] = sOPEN_PROC_DESCRIPTION;
    this._jtfDescription = new JTextField(sVal);


    // 2IMPORT
    sVal = getAttr(hmData, sOPEN_PROC_2IMPORT_ATTR, sOPEN_PROC_2IMPORT);
    data[nOPEN_PROC_2IMPORT][0] = AlteraAtributosOpenProc.sOPEN_PROC_2IMPORT_ATTR;
    data[nOPEN_PROC_2IMPORT][1] = sVal;
    data[nOPEN_PROC_2IMPORT][2] = sOPEN_PROC_2IMPORT;
    this._jtf2Import = new JTextField(sVal);

    // CREATED PID
    sVal = getAttr(hmData, sOPEN_PROC_CREATED_PID_ATTR, sOPEN_PROC_CREATED_PID);
    data[nOPEN_PROC_CREATED_PID][0] = AlteraAtributosOpenProc.sOPEN_PROC_CREATED_PID_ATTR;
    data[nOPEN_PROC_CREATED_PID][1] = sVal;
    data[nOPEN_PROC_CREATED_PID][2] = sOPEN_PROC_CREATED_PID;
    this._jtfCreatedPid = new JTextField(sVal);

    // DATA SET



    // ENABLE/DISABLE FIELDS
    toggleImportType(data[nOPEN_PROC_TYPE][1].equals(openProcessTypes[0]));
    toggleUserMode(data[nOPEN_PROC_USERMODE][1].equals(userModes[0]));


    Object[][] tabledata = new Object[data.length][2];
    for (int i=0; i < data.length; i++) {
      tabledata[i][0] = data[i][2]; // desc
      tabledata[i][1] = data[i][1]; // value
    }

    jTable1= new MyJTableX(tabledata, AlteraAtributosColumnNames);

    /* table model -> can not edit 1st collunn */
    MyTableModel model = new MyTableModel(AlteraAtributosColumnNames, tabledata);
    model.setColumnEditable(0, false);
    jTable1.setModel(model);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    DefaultCellEditor ed = null;
    ed = new DefaultCellEditor(this._jcbType);
    rm.addEditorForCell(nOPEN_PROC_TYPE, 1, ed);
    ed = new DefaultCellEditor(this._jtfFid);
    rm.addEditorForCell(nOPEN_PROC_FID, 1, ed);
    ed = new DefaultCellEditor(this._jtfPid);
    rm.addEditorForCell(nOPEN_PROC_PID, 1, ed);
    ed = new DefaultCellEditor(this._jtfSubPid);
    rm.addEditorForCell(nOPEN_PROC_SUBPID, 1, ed);
    ed = new DefaultCellEditor(this._jcbModes);
    rm.addEditorForCell(nOPEN_PROC_MODE, 1, ed);
    ed = new DefaultCellEditor(this._jtfBid);
    rm.addEditorForCell(nOPEN_PROC_BID, 1, ed); 
    ed = new DefaultCellEditor(this._jcbUserMode);
    rm.addEditorForCell(nOPEN_PROC_USERMODE, 1, ed);
    ed = new DefaultCellEditor(this._jtfUser);
    rm.addEditorForCell(nOPEN_PROC_USER, 1, ed);
    ed = new DefaultCellEditor(this._jtfDescription);
    rm.addEditorForCell(nOPEN_PROC_DESCRIPTION, 1, ed);
    ed = new DefaultCellEditor(this._jtf2Import);
    rm.addEditorForCell(nOPEN_PROC_2IMPORT, 1, ed);
    ed = new DefaultCellEditor(this._jtfCreatedPid);
    rm.addEditorForCell(nOPEN_PROC_CREATED_PID, 1, ed);


    /* criar botões e arranjar dialogo */
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(500,300);
    setVisible(true);
  }

  private void toggleImportType(boolean abImport) {
    this._jtfPid.setEnabled(abImport);     
    this._jtfSubPid.setEnabled(abImport);     
    this._jcbModes.setEnabled(abImport);     
  }

  private void toggleUserMode(boolean abCreator) {
    this._jtfUser.setEnabled(!abCreator);     
  }

  /**
   * Iniciar caixa de diálogo
   * @throws Exception
   */
  void jbInit() throws Exception {
    // configurar
    panel1.setLayout(borderLayout1);

    this.setSize(300,250);

    /* resize */
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });

    /* botão OK */
    jButton1.setText(adapter.getString("Common.ok")); //$NON-NLS-1$

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    /* botão cancelar */
    jButton2.setText(adapter.getString("Common.cancel")); //$NON-NLS-1$
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });

    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    /* paineis */
    JPanel aux1=new JPanel();
    aux1.setSize(100,1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2=new JPanel();
    aux2.setSize(100,1);
    getContentPane().add(aux2, BorderLayout.EAST);



    jScrollPane1.getViewport().add(jTable1, null);

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);

    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton1, null);
    jPanel2.add(jButton2, null);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);

    dialogComponentResized(null);
    repaint();
  }


  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    for (int i=0; i < data.length; i++) {
      data[i][1] = jTable1.getStringAt(i,1); 
    }

    exitStatus=EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }


  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

  private String getAttr(Map<String, String> hmData, String attr, String desc) {
    if (hmData.containsKey(attr)) {
      return hmData.get(attr);
    }
    else if (hmData.containsKey(desc)) {
      return hmData.get(desc);
    }
    return ""; 
  }

}

