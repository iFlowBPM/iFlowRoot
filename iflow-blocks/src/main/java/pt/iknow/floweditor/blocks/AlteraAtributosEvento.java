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
 * <p>Description: Dialogo para criar e editar eventos </p></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow </p>
 * @author Pedro Monteiro
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosEvento extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = -1104016248727183359L;

  private static final String DISABLE_IF = "disableIf";

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
  private Object[][] data ;

  private Properties htProperties = new Properties();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  // Condição de Disable
  JLabel disableIfLbl = new JLabel();
  JTextField disableIfTxt = new JTextField();	

  private final String[] columnNames;
  private final String _sSELECT;

  public AlteraAtributosEvento(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosEvento.title"), true); //$NON-NLS-1$
    columnNames = new String[]{ adapter.getString("AlteraAtributosEvento.type"), adapter.getString("AlteraAtributosEvento.properties") }; //$NON-NLS-1$ //$NON-NLS-2$
    _sSELECT = adapter.getString("AlteraAtributosEvento.choose"); //$NON-NLS-1$
  }


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
        newAttributes[2*i][2] = ""; //$NON-NLS-1$
      }
    }
    String[][] auxAtt = new String[newAttributes.length+1][3];
	System.arraycopy(newAttributes, 0, auxAtt, 0, newAttributes.length);
	auxAtt[newAttributes.length][0] = DISABLE_IF;
	auxAtt[newAttributes.length][1] = disableIfTxt.getText();
	auxAtt[newAttributes.length][2] = "";
	return auxAtt;
  }

  public boolean fixedAttributes() {
    return true;
  }


  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    int condCount = 0;

    String stmp = null;

    Atributo atributo = null;
    for (int i = 0; i < atributos.size(); i++) {
      atributo = (Atributo)atributos.get(i);
      if (atributo == null) continue;
      stmp = atributo.getNome();
      if (stmp != null && stmp.length() >= 4 && stmp.substring(0,4).equals("dest")) { //$NON-NLS-1$
        condCount++;
	  }
	  else if(atributo.getDescricao().equals(DISABLE_IF)) {
	  	disableIfTxt.setText(atributo.getValor());
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

    jTable1= new MyJTableX(data, columnNames);
    TableModel1 model = new TableModel1();
    model.setDefaultValuesForRow(new String []{_sSELECT, ""}); //$NON-NLS-1$

    jTable1.setModel(model);

    jTable1.setRowSelectionAllowed(true);
    jTable1.setColumnSelectionAllowed(false);

    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    String[] saEvents = null;
    RepositoryClient rep = adapter.getRepository();

    if (rep != null && (saEvents == null || saEvents.length == 0)) {
      saEvents = rep.listEvents();
    }
    String[] satmp = saEvents;
    if (satmp == null) {
      saEvents = new String[1];
    }
    else {
      saEvents = new String[satmp.length + 1];
      for (int e=0; e < satmp.length; e++) {
        saEvents[e+1] = satmp[e];
        String stmptmp = rep.getEvent(satmp[e]);
        htProperties.setProperty(satmp[e], stmptmp);
      }
    }
    saEvents[0] = _sSELECT;

    JComboBox jcb = new JComboBox(saEvents);
    jcb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jcb_actionPerformed(e);
      }
    });
    jcb.setSelectedItem(saEvents[0]);
    DefaultCellEditor mce = new DefaultCellEditor(jcb);
    rm.addEditorForColumn(0,mce);

    JTextField jtf = new JTextField();
    jtf.setSelectionColor(Color.red);
    jtf.setSelectedTextColor(Color.white);
    DefaultCellEditor cce = new DefaultCellEditor(jtf);
    cce.setClickCountToStart(2);
    rm.addEditorForColumn(1,cce);

    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    // Mouse positions
//    int xCoordinate = (MouseInfo.getPointerInfo().getLocation().x) - (MouseInfo.getPointerInfo().getLocation().x / 3);
//    int yCoordinate = (MouseInfo.getPointerInfo().getLocation().y) - (MouseInfo.getPointerInfo().getLocation().y / 3);
    
    this.setSize(400,250);
//    setLocation(xCoordinate, yCoordinate);
    setLocationRelativeTo(getParent());
    setVisible(true);
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

	disableIfLbl.setText(adapter.getString("AlteraAtributosEvento.disableIf"));
	disableIfTxt.setColumns(20);
	disableIfTxt.setSelectionColor(Color.red);
	disableIfTxt.setSelectedTextColor(Color.white);

	jPanel1.add(disableIfLbl, null);
	jPanel1.add(disableIfTxt, null);
    jPanel2.add(jButton1, null);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton3, null);
    jPanel2.add(jButton4, null);

		this.getContentPane().add(jPanel1, BorderLayout.NORTH);
//		this.getContentPane().add(jPanel3, BorderLayout.NORTH);
		this.getContentPane().add(jPanel2, BorderLayout.SOUTH);

    dialogComponentResized(null);

    repaint();
  }

  /* Escolha de eventos */
  void jcb_actionPerformed(ActionEvent e) {
    int rowSelected = jTable1.getSelectedRow();

    if (rowSelected != -1) {
      TableModel1 tm = (TableModel1)jTable1.getModel();
      tm.alterProperties(rowSelected);
    }
  }

  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    jTable1.stopEditing();
    exitStatus=EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    dispose();
  }

  /* + */
  void jButton3_actionPerformed(ActionEvent e) {
    //Add a row to the table
    TableModel1 tm = (TableModel1)jTable1.getModel();
    data = tm.insertRow();
  }

  /* - */
  void jButton4_actionPerformed(ActionEvent e) {
    int rowSelected = jTable1.getSelectedRow();

    if (rowSelected != -1) {
      TableModel1 tm = (TableModel1)jTable1.getModel();
      data = tm.removeRow(rowSelected);
    }
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) { }

  private class TableModel1 extends MyTableModel {
    private static final long serialVersionUID = -3081028190323293208L;

    public TableModel1() {
      super(columnNames, AlteraAtributosEvento.this.data);
    }

    public void alterProperties(int rowSelected) {
      Object [][] data = getData();
      data[rowSelected][1] = htProperties.getProperty((String)data[rowSelected][0]);
      fireTableCellUpdated(rowSelected, 1); // notify table
    }
  }
}
