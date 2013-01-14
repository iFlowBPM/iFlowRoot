package pt.iknow.floweditor.blocks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosDocumentToSign extends AbstractAlteraAtributos implements AlteraAtributosInterface {
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

  private final String sDESC_DOCS = adapter.getString("AlteraAtributosDocumentToSign.docs");
  private final String sDESC_TOSIGN = adapter.getString("AlteraAtributosDocumentToSign.checkvals");
  private final String sATTR_DOCS = "DocsToSign";
  private final String sATTR_TOSIGN = "CheckBoxToSign";
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
    String[][] newAttributes = new String[2][2];
    newAttributes[0][0] = sATTR_DOCS;
    newAttributes[0][1] = (String) data[0][1];   
    newAttributes[1][0] = sATTR_TOSIGN;
    newAttributes[1][1] = (String) data[1][1];      
    return newAttributes;
  }

  public boolean fixedAttributes() {
    return true;
  }

  public AlteraAtributosDocumentToSign(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosDocumentToSign.title"), true); 
    columnNames = new String[]{ adapter.getString("AlteraAtributosDocumentToSign.vector"),adapter.getString("AlteraAtributosDocumentToSign.vars") };
  }



  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    String stmp = null;
    data = new String[2][2];

    Map<String,String> attr = new HashMap<String, String>();
    for(Atributo a : atributos)
      attr.put(a.getNome(), a.getValor());

    //GET ATTR DOCS
    data[0][0] = sDESC_DOCS;
    stmp = attr.get(sATTR_DOCS);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[0][1] = stmp;

    //GET ATTR TOSIGN
    data[1][0] = sDESC_TOSIGN;
    stmp = attr.get(sATTR_TOSIGN);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[1][1] = stmp;
    
     
    /* table model -> can not edit 1st collunn */
    MyTableModel model = new MyTableModel(columnNames, data);
    model.setColumnEditable(0, false);
    jTable1.setModel(model);
    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false); 
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
      
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    dialogComponentResized(null);
    repaint();
  }
  
  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    jTable1.stopEditing();
    //VALIDATION
      if (StringUtilities.isEmpty((String) data[0][1])) {
        adapter.showError(adapter.getString("AlteraAtributosDocumentToSign.error")); //$NON-NLS-1$
        return;
      }
      if (StringUtilities.isEmpty((String) data[1][1])) {
        adapter.showError(adapter.getString("AlteraAtributosDocumentToSign.error")); //$NON-NLS-1$
        return;
      }
      exitStatus = EXIT_STATUS_OK;
      dispose();
  }

  /* CANCEL */
  void jButton2_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) { }

}
