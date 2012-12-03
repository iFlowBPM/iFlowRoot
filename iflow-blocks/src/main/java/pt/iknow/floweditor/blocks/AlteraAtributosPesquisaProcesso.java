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

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar condicoes para Pesquisa de Processos </p>
 * <p>  variavel | nome | pesquisar
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import pt.iflow.blocks.interfaces.PesquisaProcesso;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosPesquisaProcesso extends AbstractAlteraAtributos implements AlteraAtributosInterface, PesquisaProcesso {
  private static final long serialVersionUID = 2403856429536958449L;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel2 = new JPanel();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JPanel jPanel3 = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private MyJTableX jTable1 = new MyJTableX();

  private final String[] saTYPESDesc;
  private final String[] saSEARCH_MODESDesc;
  private final String[] saFETCH_MODESDesc;
  private final String[] saCASE_MODESDesc;
  
  private final Map<String, String> saTypesMap = new HashMap<String, String>();
  private final Map<String, String> saTypesRev = new HashMap<String, String>();
  
  private final Map<String, String> saSearchModeMap = new HashMap<String, String>();
  private final Map<String, String> saSearchModeRev = new HashMap<String, String>();
  
  private final Map<String, String> saFetchModeMap = new HashMap<String, String>();
  private final Map<String, String> saFetchModeRev = new HashMap<String, String>();
  
  private final Map<String, String> saCaseModeMap = new HashMap<String, String>();
  private final Map<String, String> saCaseModeRev = new HashMap<String, String>();
  
  

  private final String[] columnNames;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  public AlteraAtributosPesquisaProcesso(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosPesquisaProcesso.title"), true); //$NON-NLS-1$
    saTYPESDesc = new String[saTYPES.length];
    for(int i = 0; i < saTYPES.length; i++)
      saTYPESDesc[i] = adapter.getString("AlteraAtributosPesquisaProcesso.type."+saTYPES[i]); //$NON-NLS-1$

    saSEARCH_MODESDesc = new String[] {
        adapter.getString("AlteraAtributosPesquisaProcesso.search_mode.yes_open"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.search_mode.yes_closed"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.search_mode.yes_all"), //$NON-NLS-1$
        adapter.getString("Common.no"),
    };
    saFETCH_MODESDesc = new String[] {
        adapter.getString("Common.yes"), //$NON-NLS-1$
        adapter.getString("Common.no"), //$NON-NLS-1$
    };
    saCASE_MODESDesc = new String[] {
        adapter.getString("Common.no"), //$NON-NLS-1$
        adapter.getString("Common.yes"), //$NON-NLS-1$
    };

    columnNames = new String[] {
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.type"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.varname"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.attrname"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.fetch"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.search"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.search.operator"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.search.value"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosPesquisaProcesso.column.names.case.sensitive"), //$NON-NLS-1$
    };

    initMaps();
  }

  private void initMaps() {
    for(int i = 0; i < saTYPES.length; i++) {
      saTypesMap.put(saTYPES[i], saTYPESDesc[i]);
      saTypesRev.put(saTYPESDesc[i], saTYPES[i]);
    }

    for(int i = 0; i < saSEARCH_MODES.length; i++) {
      saSearchModeMap.put(saSEARCH_MODES[i], saSEARCH_MODESDesc[i]);
      saSearchModeRev.put(saSEARCH_MODESDesc[i], saSEARCH_MODES[i]);
    }

    for(int i = 0; i < saFETCH_MODES.length; i++) {
      saFetchModeMap.put(saFETCH_MODES[i], saFETCH_MODESDesc[i]);
      saFetchModeRev.put(saFETCH_MODESDesc[i], saFETCH_MODES[i]);
    }
    
    for(int i = 0; i < saCASE_MODES.length; i++) {
      saCaseModeMap.put(saCASE_MODES[i], saCASE_MODESDesc[i]);
      saCaseModeRev.put(saCASE_MODESDesc[i], saCASE_MODES[i]);
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
    int condCount = 0;
    String var = null;
    for (int i = 0; i < data.length; i++) {
      var = (String) data[i][1];
      if (var != null && !var.equals("")) { //$NON-NLS-1$
        condCount++;
      }
    }
    int nNumCols = columnNames.length;

    String[][] newAttributes = new String[condCount * nNumCols][3];

    for (int i = 0; i < data.length; i++) {
      newAttributes[nNumCols * i + 0][0] = varNames[0] + i;
      newAttributes[nNumCols * i + 0][1] = (String) data[i][0];
      newAttributes[nNumCols * i + 0][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 1][0] = varNames[1] + i;
      newAttributes[nNumCols * i + 1][1] = (String) data[i][1];
      newAttributes[nNumCols * i + 1][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 2][0] = varNames[2] + i;
      newAttributes[nNumCols * i + 2][1] = (String) data[i][2];
      newAttributes[nNumCols * i + 2][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 3][0] = varNames[3] + i;
      newAttributes[nNumCols * i + 3][1] = (String) data[i][3];
      newAttributes[nNumCols * i + 3][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 4][0] = varNames[4] + i;
      newAttributes[nNumCols * i + 4][1] = (String) data[i][4];
      newAttributes[nNumCols * i + 4][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 5][0] = varNames[5] + i;
      newAttributes[nNumCols * i + 5][1] = (String) data[i][5];
      newAttributes[nNumCols * i + 5][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 6][0] = varNames[6] + i;
      newAttributes[nNumCols * i + 6][1] = (String) data[i][6];
      newAttributes[nNumCols * i + 6][2] = ""; //$NON-NLS-1$
      newAttributes[nNumCols * i + 7][0] = varNames[7] + i;
      newAttributes[nNumCols * i + 7][1] = (String) data[i][7];
      newAttributes[nNumCols * i + 7][2] = ""; //$NON-NLS-1$
      
      // remap properties
      newAttributes[nNumCols * i + 0][1] = saTypesRev.get(data[i][0]);
      newAttributes[nNumCols * i + 3][1] = saFetchModeRev.get(data[i][3]);
      newAttributes[nNumCols * i + 4][1] = saSearchModeRev.get(data[i][4]);
      newAttributes[nNumCols * i + 7][1] = saCaseModeRev.get(data[i][7]);
      
    }
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

    String stmp = null;
    String name = null;
    String value = null;

    Atributo atributo = null;
    for (int i = 0; i < atributos.size(); i++) {
      atributo = (Atributo) atributos.get(i);
      if (atributo == null)
        continue;
      name = atributo.getNome();
      if (name != null && name.length() >= varNames[1].length() && name.substring(0, varNames[1].length()).equals(varNames[1])) {
        condCount++;
      }
    }

    int nNumCols = columnNames.length;

    data = new Object[condCount][nNumCols];
    HashMap<String,String> hmTypes = new HashMap<String, String>();
    HashMap<String,String> hmCond = new HashMap<String, String>();
    HashMap<String,String> hmDestVar = new HashMap<String, String>();
    HashMap<String,String> hmProf = new HashMap<String, String>();
    HashMap<String,String> hmMsgs = new HashMap<String, String>();
    HashMap<String,String> hmOper = new HashMap<String, String>();
    HashMap<String,String> hmVals = new HashMap<String, String>();
    HashMap<String,String> hmCase = new HashMap<String, String>();

    for (int i = 0; i < atributos.size(); i++) {
      name = ((Atributo) atributos.get(i)).getNome();
      value = ((Atributo) atributos.get(i)).getValor();

      if (name.length() >= varNames[0].length() && name.substring(0, varNames[0].length()).equals(varNames[0])) {
        hmTypes.put(name, value);
      } else if (name.length() >= varNames[1].length() && name.substring(0, varNames[1].length()).equals(varNames[1])) {
        hmCond.put(name, value);
      } else if (name.length() >= varNames[2].length() && name.substring(0, varNames[2].length()).equals(varNames[2])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmDestVar.put(name, value);
      } else if (name.length() >= varNames[3].length() && name.substring(0, varNames[3].length()).equals(varNames[3])) {
        if (value == null)
          value = saFETCH_MODES[0]; //$NON-NLS-1$
        hmProf.put(name, value);
      } else if (name.length() >= varNames[4].length() && name.substring(0, varNames[4].length()).equals(varNames[4])) {
        if (value == null)
          value = saSEARCH_MODES[0]; //$NON-NLS-1$
        hmMsgs.put(name, value);
      } else if (name.length() >= varNames[5].length() && name.substring(0, varNames[5].length()).equals(varNames[5])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmOper.put(name, value);
      } else if (name.length() >= varNames[6].length() && name.substring(0, varNames[6].length()).equals(varNames[6])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmVals.put(name, value);
      } else if (name.length() >= varNames[7].length() && name.substring(0, varNames[7].length()).equals(varNames[7])) {
        if (value == null)
          value = saCASE_MODES[0]; //$NON-NLS-1$
        hmCase.put(name, value);
      }
    }

    for (int i = 0; i < condCount; i++) {
      stmp = (String) hmTypes.get(varNames[0] + i);
      if (stmp == null || stmp.equals("")) { //$NON-NLS-1$
        // default type value
        hmTypes.put(varNames[0] + i, saTYPES[0]);
      }
      data[i][0] = hmTypes.get(varNames[0] + i);
      data[i][1] = hmCond.get(varNames[1] + i);
      data[i][2] = hmDestVar.get(varNames[2] + i);
      data[i][3] = hmProf.get(varNames[3] + i);
      data[i][4] = hmMsgs.get(varNames[4] + i);
      data[i][5] = hmOper.get(varNames[5] + i);
      data[i][6] = hmVals.get(varNames[6] + i);
      data[i][7] = hmCase.get(varNames[7] + i);

      if (data[i][2] == null)
        data[i][2] = ""; //$NON-NLS-1$
      if (data[i][5] == null)
        data[i][5] = ""; //$NON-NLS-1$
      if (data[i][6] == null)
        data[i][6] = ""; //$NON-NLS-1$
      
      // remap properties
      data[i][0] = saTypesMap.get(data[i][0]);
      data[i][3] = saFetchModeMap.get(data[i][3]);
      data[i][4] = saSearchModeMap.get(data[i][4]);
      data[i][7] = saCaseModeMap.get(data[i][7]);
    }

    jTable1 = new MyJTableX(data, columnNames);

    Object [] defaultValues = new Object[]{
        saTYPESDesc[0],
        "",
        "",
        saFETCH_MODESDesc[0],
        saSEARCH_MODESDesc[0],
        "",
        "",
        saCASE_MODESDesc[0],
    };

    MyTableModel model = new MyTableModel(columnNames, data);
    model.setDefaultValuesForRow(defaultValues);
    jTable1.setModel(model);

    jTable1.setRowSelectionAllowed(true);
    jTable1.setColumnSelectionAllowed(false);

    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    JComboBox jcb0 = new JComboBox(saTYPESDesc);
    DefaultCellEditor ed0 = new DefaultCellEditor(jcb0);
    rm.addEditorForColumn(0, ed0);

    JTextField jtf1 = new JTextField();
    jtf1.setSelectionColor(Color.red);
    jtf1.setSelectedTextColor(Color.white);
    DefaultCellEditor cce = new DefaultCellEditor(jtf1);
    cce.setClickCountToStart(2);
    rm.addEditorForColumn(1, cce);

    JTextField jtf1_2 = new JTextField();
    jtf1_2.setSelectionColor(Color.red);
    jtf1_2.setSelectedTextColor(Color.white);
    DefaultCellEditor mce1_2 = new DefaultCellEditor(jtf1_2);
    mce1_2.setClickCountToStart(2);
    rm.addEditorForColumn(2, mce1_2);

    JComboBox jcb1 = new JComboBox(saFETCH_MODESDesc);
    DefaultCellEditor ed = new DefaultCellEditor(jcb1);
    rm.addEditorForColumn(3, ed);

    JComboBox jcb2 = new JComboBox(saSEARCH_MODESDesc);
    DefaultCellEditor ed2 = new DefaultCellEditor(jcb2);
    rm.addEditorForColumn(4, ed2);

    JTextField jtf3 = new JTextField();
    jtf3.setSelectionColor(Color.red);
    jtf3.setSelectedTextColor(Color.white);
    DefaultCellEditor mce3 = new DefaultCellEditor(jtf3);
    mce3.setClickCountToStart(2);
    rm.addEditorForColumn(5, mce3);

    JTextField jtf4 = new JTextField();
    jtf4.setSelectionColor(Color.red);
    jtf4.setSelectedTextColor(Color.white);
    DefaultCellEditor mce4 = new DefaultCellEditor(jtf4);
    mce4.setClickCountToStart(2);
    rm.addEditorForColumn(6, mce4);

    JComboBox jcb3 = new JComboBox(saCASE_MODESDesc);
    DefaultCellEditor ed3 = new DefaultCellEditor(jcb3);
    rm.addEditorForColumn(7, ed3);

    jbInit();

    this.setSize(800, 250);
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

    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    JPanel aux1 = new JPanel();
    aux1.setSize(100, 1);

    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(100, 1);
    getContentPane().add(aux2, BorderLayout.EAST);

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTable1, null);

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
    int rowSelected = jTable1.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel) jTable1.getModel();
      data = tm.removeRow(rowSelected);
    }
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

}
