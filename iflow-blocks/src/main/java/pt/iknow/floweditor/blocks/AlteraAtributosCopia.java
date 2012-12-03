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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.DynamicBindDelegate;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosCopia extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1245748103290376946L;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  private final String[] columnNames;
  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data ;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  /**
   * getExitStatus
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * @return
   */
  public String[][] getNewAttributes() {
    checkDynVars(true);
    int condCount = 0;
    for (int i = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if (cond != null && !cond.equals("")) { //$NON-NLS-1$
        condCount++;
      }
    }
    String[][] newAttributes = new String[condCount*2][3];

    for (int i = 0; i < data.length; i++) {
      String cond = (String) data[i][0];
      if (cond != null && !cond.equals("")) { //$NON-NLS-1$
        newAttributes[2*i][0] = new String("dest" + i); //$NON-NLS-1$
        newAttributes[2*i][1] = (String) data[i][0];
        newAttributes[2*i][2] = ""; //$NON-NLS-1$
        newAttributes[2*i+1][0] = new String("orig" + i); //$NON-NLS-1$
        newAttributes[2*i+1][1] = (String) data[i][1];
        newAttributes[2*i+1][2] = ""; //$NON-NLS-1$
      }
    }
    return newAttributes;
  }

  public boolean fixedAttributes() {
    return true;
  }

  public AlteraAtributosCopia(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosCopia.title"), true); //$NON-NLS-1$
    columnNames = new String[]{ adapter.getString("AlteraAtributosCopia.to"), adapter.getString("AlteraAtributosCopia.from") }; //$NON-NLS-1$ //$NON-NLS-2$

  }

  /**
   * Checks for the existence of attribute names that are reserved in the data
   * array.
   * 
   * @param remove
   *          Flag true if all found dynamic variables should be removed.
   * @return True if any variables were removed.
   */
  private boolean checkDynVars(boolean remove) {
    boolean hasDynVars = false;
    List<Integer> dynVarIndexes = new ArrayList<Integer>();
    for (int i = 0, lim = data.length; i < lim; i++) {
      if (data[i].length > 0) {
        for (String dynVar : DynamicBindDelegate.getDynamicVariables()) {
          if (StringUtils.equals(dynVar, "" + data[i][0])) {
            dynVarIndexes.add(i);
            break;
          }
        }
      }
    }
    if (!dynVarIndexes.isEmpty()) {
      hasDynVars = true;
      if(remove) {
        int dataSize = data.length - dynVarIndexes.size();
        if (dataSize >= 0) {
          Object[][] tmpData = new Object[dataSize][];
          int index = 0;
          for (int i = 0, lim = data.length; i < lim; i++) {
            boolean keepResult = true;
            for (int item : dynVarIndexes) {
              if (i == item) {
                keepResult = false;
                break;
              }
            }
            if (keepResult) {
              tmpData[index] = data[i];
              index++;
            }
          }
          data = tmpData;
        }
      }
    }
    return hasDynVars;
  }

  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    int condCount = 0;
    for (Atributo atributo : atributos) {
      if (atributo != null) {
        String stmp = atributo.getNome();
        if (stmp != null && stmp.length() >= 4 && stmp.substring(0,4).equals("dest")) { //$NON-NLS-1$
          condCount++;
        }
      }
    }

    data = new String[condCount][2];
    String name = null;
    String value = null;
    for (int i = 0; i < atributos.size(); i++) {
      name = ((Atributo)atributos.get(i)).getNome();
      value = ((Atributo)atributos.get(i)).getValor();
      if (name.length() >= 4 && name.substring(0,4).equals("dest")) //$NON-NLS-1$
        data[Integer.parseInt(name.substring(4))][0] = new String(value);
      else if (name.length() >= 4 && name.substring(0,4).equals("orig")) //$NON-NLS-1$
        data[Integer.parseInt(name.substring(4))][1] = new String(value);
    }
    checkDynVars(true);
    jTable1 = new MyJTableX(data, columnNames);

    jTable1.setModel(new MyTableModel(columnNames, data));

    jTable1.setRowSelectionAllowed(true);
    jTable1.setColumnSelectionAllowed(false);

    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MyColumnEditorModel cm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(cm);

    JTextField jtf1 = new JTextField();
    jtf1.setSelectionColor(Color.red);
    jtf1.setSelectedTextColor(Color.white);
    DefaultCellEditor mce = new DefaultCellEditor(jtf1);
    mce.setClickCountToStart(2);
    cm.addEditorForColumn(0,mce);
    JTextField jtf2 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor cce = new DefaultCellEditor(jtf2);
    cce.setClickCountToStart(2);
    cm.addEditorForColumn(1,cce);

    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(300,250);
    this.setLocationRelativeTo(getParent());
    this.setVisible(true);
  }

  /**
   * jbInit
   * @throws Exception
   */
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText(OK);

    jButton3.setText("+"); //$NON-NLS-1$
    jButton4.setText("-"); //$NON-NLS-1$

    this.setSize(300,250);

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

    JPanel aux1=new JPanel();
    aux1.setSize(100,1);

    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2=new JPanel();
    aux2.setSize(100,1);
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
  
  private String[] invalidVariables() {
    List<String> retObj = new ArrayList<String>();
    for (int i = 0, lim = data.length; i < lim; i++) {
      if (data[i].length > 0) {
        for (String dynVar : DynamicBindDelegate.getDynamicVariables()) {
          if (StringUtils.equals(dynVar, ("" + data[i][0]).trim())) {
            retObj.add(dynVar);
            break;
          }
        }
      }
    }
    return retObj.toArray(new String[retObj.size()]);
  }
  
  private boolean validateData() {
    boolean dataIsValid = !checkDynVars(false);
    if(!dataIsValid) {
      String title = adapter.getString("DynamicBindDelegate.dialog.error.title");
      StringBuffer message = new StringBuffer();
      message.append(adapter.getString("DynamicBindDelegate.dialog.error.message"));
      message.append(System.getProperty("line.separator"));
      for(String var : invalidVariables()) {
        message.append(var).append(" ");
        message.append(adapter.getString("DynamicBindDelegate.dialog.error.reason.reserved"));
        message.append(" :: ").append(adapter.getString("DynamicBindDelegate.catalog." + var + ".publicname"));
        message.append(System.getProperty("line.separator"));
      }
      JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    return dataIsValid;
  }
  
  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    if(validateData()) {
      jTable1.stopEditing();
      // convertHashMapToArray();
      exitStatus = EXIT_STATUS_OK;
      dispose();
    }
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  /* + */
  void jButton3_actionPerformed(ActionEvent e) {
    //Add a row to the table
    MyTableModel tm = (MyTableModel)jTable1.getModel();
    data = tm.insertRow();
  }

  /* - */
  void jButton4_actionPerformed(ActionEvent e) {
    int rowSelected = jTable1.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel)jTable1.getModel();
      data = tm.removeRow(rowSelected);
    }
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) { }

}
