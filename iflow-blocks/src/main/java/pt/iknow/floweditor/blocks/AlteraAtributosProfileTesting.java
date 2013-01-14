package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar condicoes para perfis - Teste e atribuição num só bloco </p></p>
 * <p>  condição | perfil | variavel (bool)
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow </p>
 * @author Campa
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class AlteraAtributosProfileTesting extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 3892798958101731109L;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  public static String[] saProfiles = null;
  private static final String _sSELECT = "Escolha";

  static final String[] columnNames = { "Condition", "Profile", "VarName" };
  static final String[] varNames = { "cond", "prof" , "varname" };
  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data ;

  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  public AlteraAtributosProfileTesting(FlowEditorAdapter adapter) {
    super(adapter, "Validation Conditions", true);
    
    initMaps();
  }


  
  private void initMaps() {
    RepositoryClient rep = adapter.getRepository();
    String[] sa = null;

    if (rep != null) {
      sa = rep.listProfiles();
    }
    if (sa == null) {
      saProfiles = new String[0];
    }
    else {
      saProfiles = new String[sa.length + 1];
      saProfiles[0] = _sSELECT;
      for (int i=0; i < sa.length; i++) {
        saProfiles[i+1] = sa[i];
      }
    }
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
    String  cond = null;
    for (int i = 0; i < data.length; i++) {
      cond = (String) data[i][0];
      if (cond != null && !cond.equals("")) {
        condCount++;
      }
    }
    String[][] newAttributes = new String[condCount*3][3];

    for (int i = 0; i < data.length; i++) {
      cond = (String) data[i][0];
      if (cond != null && !cond.equals("")) {
        newAttributes[3*i][0] = new String(varNames[0] + i);
        newAttributes[3*i][1] = (String) data[i][0];
        newAttributes[3*i][2] = "";
        newAttributes[3*i+1][0] = new String(varNames[1] + i);
        newAttributes[3*i+1][1] = (String) ((JComboBox)data[i][1]).getSelectedItem();
        newAttributes[3*i+1][2] = "";
        newAttributes[3*i+2][0] = new String(varNames[2] + i);
        newAttributes[3*i+2][1] = (String) data[i][2];
        newAttributes[3*i+2][2] = "";
      }
    }
    return newAttributes;
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

    String name = null;
    String value = null;

    Atributo atributo = null;
    for (int i = 0; i < atributos.size(); i++) {
      atributo = (Atributo)atributos.get(i);
      if (atributo == null) continue;
      name = atributo.getNome();
      if (name != null &&
          name.length() >= varNames[0].length() && 
          name.substring(0,4).equals(varNames[0])) {
        condCount++;
      }
    }

    data = new Object[condCount][3];
    HashMap<String,String> hmCond = new HashMap<String, String>();
    HashMap<String,String> hmProf = new HashMap<String, String>();
    HashMap<String,String> hmMsgs = new HashMap<String,String>();

    for (int i = 0; i < atributos.size(); i++) {
      name = ((Atributo)atributos.get(i)).getNome();
      value = ((Atributo)atributos.get(i)).getValor();

      if (name.length() >= varNames[0].length() && 
          name.substring(0,varNames[0].length()).equals(varNames[0])) {
        hmCond.put(name,value);
      }
      else if (name.length() >= varNames[1].length() && 
          name.substring(0,varNames[1].length()).equals(varNames[1])) {
        if (value == null) value = "";
        hmProf.put(name,value);
      }
      else if (name.length() >= varNames[2].length() && 
          name.substring(0,varNames[2].length()).equals(varNames[2])) {
        if (value == null) value = "";
        hmMsgs.put(name,value);
      }
    }

    for (int i=0; i < condCount; i++) {
      data[i][0] = (String)hmCond.get(varNames[0] + i);
      data[i][1] = new JComboBox(saProfiles);
      ((JComboBox)data[i][1]).setSelectedItem(hmProf.get(varNames[1] + i));
      data[i][2] = (String)hmMsgs.get(varNames[2] + i);
    }


    jTable1= new MyJTableX(data, columnNames);

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
    rm.addEditorForColumn(0,cce);

    JComboBox jcb1 = new JComboBox(saProfiles);
    DefaultCellEditor ed = new DefaultCellEditor(jcb1);
    rm.addEditorForColumn(1,ed);

    JTextField jtf2 = new JTextField();
    jtf2.setSelectionColor(Color.red);
    jtf2.setSelectedTextColor(Color.white);
    DefaultCellEditor mce = new DefaultCellEditor(jtf2);
    mce.setClickCountToStart(2);
    rm.addEditorForColumn(2,mce);

    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(600,250);
    setVisible(true);
  }

  /**
   * jbInit
   * @throws Exception
   */
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText(OK);

    jButton3.setText("+");
    jButton4.setText("-");

    this.setSize(600,250);

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
