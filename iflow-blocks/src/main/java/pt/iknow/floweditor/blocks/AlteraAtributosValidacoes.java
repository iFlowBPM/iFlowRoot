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
 * <p>Description: Diálogo para editar e criar validações </p></p>
 * <p>  condição | mensagem de erro
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
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

public class AlteraAtributosValidacoes extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 2498624149122653655L;

  /* AlteraAtributosValidacoes */
  private final String[] AlteraAtributosValidacoesColumnNames;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  public AlteraAtributosValidacoes(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosValidacoes.title"), true); //$NON-NLS-1$

    /* AlteraAtributosValidacoes */
    AlteraAtributosValidacoesColumnNames = new String [] {
        adapter.getString("AlteraAtributosValidacoes.condition"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosValidacoes.errorMessage"), //$NON-NLS-1$
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
    for (int i = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        condCount++;
      }
    }
    String[][] newAttributes = new String[condCount * 2][3];

    for (int i = 0, j = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        newAttributes[2 * j + 0][0] = "cond" + j; //$NON-NLS-1$
        newAttributes[2 * j + 0][1] = (String) data[i][0];
        newAttributes[2 * j + 0][2] = ""; //$NON-NLS-1$
        newAttributes[2 * j + 1][0] = "msg" + j; //$NON-NLS-1$
        newAttributes[2 * j + 1][1] = (String) data[i][1];
        newAttributes[2 * j + 1][2] = ""; //$NON-NLS-1$
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

    for (int i = 0; i < atributos.size(); i++) {
      name = atributos.get(i).getNome();
      if (name.length() >= 4 && name.substring(0, 4).equals("cond")) { //$NON-NLS-1$
        condCount++;
      }
    }

    data = new String[condCount][2];
    for (int i = 0; i < atributos.size(); i++) {
      name = atributos.get(i).getNome();
      value = atributos.get(i).getValor();
      if (name.length() >= 4 && name.substring(0, 4).equals("cond")) //$NON-NLS-1$
        data[Integer.parseInt(name.substring(4))][0] = value;
      else if (name.length() >= 3 && name.substring(0, 3).equals("msg")) //$NON-NLS-1$
        data[Integer.parseInt(name.substring(3))][1] = value;
    }

    jTable1 = new MyJTableX(data, AlteraAtributosValidacoesColumnNames);

    jTable1.setModel(new MyTableModel(AlteraAtributosValidacoesColumnNames, data));

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

    this.setSize(300, 250);
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
