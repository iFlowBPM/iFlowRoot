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
 * <p>Description: Diálogo para editar e criar condicoes para perfis </p></p>
 * <p>  condição | perfil | mensagem de erro
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

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosIsInProfilesText extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = -7281406603161259196L;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  // public static String[] saProfiles = null;
  // private static final String _sSELECT = "Escolha";

  private final String[] columnNames;
  private static final String[] varNames = { "cond", "prof", "msg" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  public AlteraAtributosIsInProfilesText(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosIsInProfilesText.title"), true); //$NON-NLS-1$
    columnNames = new String [] {
      adapter.getString("AlteraAtributosIsInProfilesText.columnlabels.condition"),//$NON-NLS-1$
      adapter.getString("AlteraAtributosIsInProfilesText.columnlabels.profile"),//$NON-NLS-1$
      adapter.getString("AlteraAtributosIsInProfilesText.columnlabels.false_message")//$NON-NLS-1$
    };
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
    String cond = null;
    for (int i = 0; i < data.length; i++) {
      cond = (String) data[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        condCount++;
      }
    }
    String[][] newAttributes = new String[condCount * 3][3];

    for (int i = 0, j = 0; i < data.length; i++) {
      cond = (String) data[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        newAttributes[3 * j + 0][0] = varNames[0] + i;
        newAttributes[3 * j + 0][1] = (String)data[i][0];
        newAttributes[3 * j + 0][2] = ""; //$NON-NLS-1$
        newAttributes[3 * j + 1][0] = varNames[1] + i;
        newAttributes[3 * j + 1][1] = (String)data[i][1];
        newAttributes[3 * j + 1][2] = ""; //$NON-NLS-1$
        newAttributes[3 * j + 2][0] = varNames[2] + i;
        newAttributes[3 * j + 2][1] = (String)data[i][2];
        newAttributes[3 * j + 2][2] = ""; //$NON-NLS-1$
        ++j;
      }
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

    String name = null;
    String value = null;

    Atributo atributo = null;
    for (int i = 0; i < atributos.size(); i++) {
      atributo = (Atributo) atributos.get(i);
      if (atributo == null)
        continue;
      name = atributo.getNome();
      if (name != null && name.length() >= varNames[0].length() && name.substring(0, 4).equals(varNames[0])) {
        condCount++;
      }
    }

    data = new Object[condCount][3];
    HashMap<String, String> hmCond = new HashMap<String, String>();
    HashMap<String, String> hmProf = new HashMap<String, String>();
    HashMap<String, String> hmMsgs = new HashMap<String, String>();

    for (int i = 0; i < atributos.size(); i++) {
      name = atributos.get(i).getNome();
      value = atributos.get(i).getValor();

      if (name.startsWith(varNames[0])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmCond.put(name, value);
      } else if (name.startsWith(varNames[1])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmProf.put(name, value);
      } else if (name.startsWith(varNames[2])) {
        if (value == null)
          value = ""; //$NON-NLS-1$
        hmMsgs.put(name, value);
      }
    }

    for (int i = 0; i < condCount; i++) {
      data[i][0] = hmCond.get(varNames[0] + i);
      data[i][1] = hmProf.get(varNames[1] + i);
      data[i][2] = hmMsgs.get(varNames[2] + i);
    }

    jTable1 = new MyJTableX(data, columnNames);

    jTable1.setModel(new MyTableModel(columnNames, data));

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

    JTextField jtf3 = new JTextField();
    jtf3.setSelectionColor(Color.red);
    jtf3.setSelectedTextColor(Color.white);
    DefaultCellEditor ed = new DefaultCellEditor(jtf3);
    ed.setClickCountToStart(2);
    rm.addEditorForColumn(1, ed);

    JTextField jtf2 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor mce = new DefaultCellEditor(jtf2);
    mce.setClickCountToStart(2);
    rm.addEditorForColumn(2, mce);

    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(600, 250);
    setVisible(true);
  }

  /**
   * jbInit
   * 
   * @throws Exception
   */
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText(OK);

    jButton3.setText("+"); //$NON-NLS-1$
    jButton4.setText("-"); //$NON-NLS-1$

    this.setSize(600, 250);

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

    /*
     * JPanel aux3=new JPanel(); aux2.setSize(100,1); getContentPane().add(aux3,
     * BorderLayout.CENTER);
     */
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
