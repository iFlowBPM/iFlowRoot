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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditor;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosArrayTrim extends JDialog implements
AlteraAtributosInterface {

  private static final String VARS_TESTE = "dest"; //$NON-NLS-1$

  private static final String VARS_TRIM = "orig"; //$NON-NLS-1$

  private static final String VAR_CONTROLO = "ctrl"; //$NON-NLS-1$

  /**
   * 
   */
  private static final long serialVersionUID = -5366590910242497041L;
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  protected static String Cancelar = Messages.getString("Button.CANCEL"); //$NON-NLS-1$
  protected static String OK = Messages.getString("Button.OK"); //$NON-NLS-1$
  private static final String[] columnNames = { Messages.getString("AlteraAtributosArrayTrim.label.varTeste"), Messages.getString("AlteraAtributosArrayTrim.label.varTrim"), Messages.getString("AlteraAtributosArrayTrim.label.varControl")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  private static final String[] atributeNames = { VARS_TESTE, VARS_TRIM, VAR_CONTROLO };
  protected int exitStatus = EXIT_STATUS_CANCEL;
  protected Object[][] data;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();


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
  public Object[][] getNewAttributes() {
    int condCount = 0;
    for (int i = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if (cond != null && !cond.equals("")) { //$NON-NLS-1$
        condCount++;
      }
    }
    Object[][] newAttributes = new String[condCount * 3][3];

    for (int i = 0, pos = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if ((cond != null && !cond.equals(""))) { //$NON-NLS-1$
        for(int j = 0; j < atributeNames.length; j++, pos++) {
          newAttributes[pos][0] = atributeNames[j] + i;
          newAttributes[pos][1] = data[i][j];
          newAttributes[pos][2] = ""; //$NON-NLS-1$
        }
      }
    }

    return newAttributes;
  }

  public boolean fixedAttributes() {
    return true;
  }

  public AlteraAtributosArrayTrim(JFrame janela) {
    this(janela, Messages.getString("AlteraAtributosArrayTrim.title"), true); //$NON-NLS-1$
  }

  protected AlteraAtributosArrayTrim(JFrame janela, String msg, boolean modal) {
    super(janela, msg, modal);
  }


  protected void loadAttributes(List<Atributo> atributos) {
    int condCount = 0;
    String stmp = null;
    Atributo atributo = null;
    for (int i = 0; i < atributos.size(); i++) {
      atributo = atributos.get(i);
      if (atributo == null)
        continue;
      stmp = atributo.nome;
      if (stmp != null && stmp.length() >= 4
          && stmp.substring(0, 4).equals(VARS_TESTE)) {
        condCount++;
      }
    }

    data = new String[condCount][3];
    for(int i = 0; i < data.length; i++) {
      data[i][0] = ""; //$NON-NLS-1$
      data[i][1] = ""; //$NON-NLS-1$
      data[i][2] = ""; //$NON-NLS-1$
    }
    String name = null;
    String value = null;
    for (int i = 0; i < atributos.size(); i++) {
      name = atributos.get(i).nome;
      value = atributos.get(i).valor;
      if (name.length() >= 4 && name.substring(0, 4).equals(VARS_TESTE)) {
        data[Integer.parseInt(name.substring(4))][0] = value;
      } else if (name.length() >= 4 && name.substring(0, 4).equals(VARS_TRIM)) {
        int pos = Integer.parseInt(name.substring(4));
        if(pos < data.length) {
          data[pos][1] = value;
        }
      } else if (name.length() >= 4 && name.substring(0, 4).equals(VAR_CONTROLO)) {
        int pos = Integer.parseInt(name.substring(4));
        if(pos < data.length) {
          data[pos][2] = value;
        }
      }
    }

  }






  /**
   * setDataIn
   * 
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    loadAttributes(atributos);

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
    DefaultCellEditor mce = new DefaultCellEditor(jtf1);
    mce.setClickCountToStart(2);
    rm.addEditorForColumn(0, mce);

    JTextField jtf2 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor cce = new DefaultCellEditor(jtf2);
    cce.setClickCountToStart(2);
    rm.addEditorForColumn(1, cce);

    JTextField jtf3 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor vce = new DefaultCellEditor(jtf3);
    vce.setClickCountToStart(2);
    rm.addEditorForColumn(2, vce);

    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      FlowEditor.log("error", ex);
    }

    this.setSize(600, 250);
    this.setVisible(true);
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
