package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosEmail
 *
 *  desc: dialogo para alterar atributos do bloco Email
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosEmail extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 7856715939266621714L;

  // check if math with ones in Uniflow's pt.iflow.blocks.BlockEmail
  public final static String sEMAIL_FROM = "from"; //$NON-NLS-1$
  public final static String sEMAIL_TO = "to"; //$NON-NLS-1$
  public final static String sEMAIL_SUBJECT = "subject"; //$NON-NLS-1$
  public final static String sEMAIL_MESSAGE = "message"; //$NON-NLS-1$
  public final static String sEMAIL_TEMPLATE = "template"; //$NON-NLS-1$

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  JTextField _jtfFrom = null;
  JTextField _jtfTo = null;
  JTextField _jtfSubject = null;
  JTextField _jtfMessage = null;
  JComboBox _jcbTemplate = null;

  String[] saTemplates = null;

  private final String[] AlteraAtributosColumnNames;
  private final String _sSELECT;

  public final String sDESC_EMAIL_FROM;
  public final String sDESC_EMAIL_TO;
  public final String sDESC_EMAIL_SUBJECT;
  public final String sDESC_EMAIL_MESSAGE;
  public final String sDESC_EMAIL_TEMPLATE;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;

  
  public AlteraAtributosEmail(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$
    
    AlteraAtributosColumnNames = new String[]{
        adapter.getString("AlteraAtributosEmail.name"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosEmail.value"), //$NON-NLS-1$
        };
    _sSELECT = adapter.getString("AlteraAtributosEmail.choose"); //$NON-NLS-1$

    sDESC_EMAIL_FROM = adapter.getString("AlteraAtributosEmail.from"); //$NON-NLS-1$
    sDESC_EMAIL_TO = adapter.getString("AlteraAtributosEmail.to"); //$NON-NLS-1$
    sDESC_EMAIL_SUBJECT = adapter.getString("AlteraAtributosEmail.subject"); //$NON-NLS-1$
    sDESC_EMAIL_MESSAGE = adapter.getString("AlteraAtributosEmail.message"); //$NON-NLS-1$
    sDESC_EMAIL_TEMPLATE = adapter.getString("AlteraAtributosEmail.template"); //$NON-NLS-1$

  }


  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    // overide attribute names for backward compability!!

    data[0][0] = AlteraAtributosEmail.sEMAIL_FROM;
    data[1][0] = AlteraAtributosEmail.sEMAIL_TO;
    data[2][0] = AlteraAtributosEmail.sEMAIL_SUBJECT;
    data[3][0] = AlteraAtributosEmail.sEMAIL_MESSAGE;
    data[4][0] = AlteraAtributosEmail.sEMAIL_TEMPLATE;

    return data;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    RepositoryClient rep = adapter.getRepository();
    if (rep != null && (saTemplates == null || saTemplates.length == 0)) {
      saTemplates = rep.listMailTemplates();
    }
    if (saTemplates == null) {
      saTemplates = new String[0];
    }

    int size = atributos.size();
    if (size < 5) {
      size = 5;
    }
    data = new String[size][2];

    String stmp = null;

    // FROM
    data[0][0] = sDESC_EMAIL_FROM;
    if (atributos != null && atributos.size() > 0 && atributos.get(0) != null)
      stmp = new String(((Atributo) atributos.get(0)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[0][1] = stmp;
    this._jtfFrom = new JTextField();
    this._jtfFrom.setText((String) data[0][1]);

    // TO
    data[1][0] = sDESC_EMAIL_TO;
    if (atributos != null && atributos.size() > 1 && atributos.get(1) != null)
      stmp = new String(((Atributo) atributos.get(1)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[1][1] = stmp;
    this._jtfTo = new JTextField();
    this._jtfTo.setText((String) data[1][1]);

    // SUBJECT
    data[2][0] = sDESC_EMAIL_SUBJECT;
    if (atributos != null && atributos.size() > 2 && atributos.get(2) != null)
      stmp = new String(((Atributo) atributos.get(2)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[2][1] = stmp;
    this._jtfSubject = new JTextField();
    this._jtfSubject.setText((String) data[2][1]);

    // MESSAGE
    data[3][0] = sDESC_EMAIL_MESSAGE;
    if (atributos != null && atributos.size() > 3 && atributos.get(3) != null)
      stmp = new String(((Atributo) atributos.get(3)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[3][1] = stmp;
    this._jtfMessage = new JTextField();
    this._jtfMessage.setText((String) data[3][1]);

    // TEMPLATE
    data[4][0] = sDESC_EMAIL_TEMPLATE;
    if (atributos != null && atributos.size() > 4 && atributos.get(4) != null)
      stmp = new String(((Atributo) atributos.get(4)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = _sSELECT;
    String[] satmp = saTemplates;
    saTemplates = new String[satmp.length + 1];
    saTemplates[0] = _sSELECT;
    for (int p = 0; p < satmp.length; p++) {
      saTemplates[p + 1] = satmp[p];
    }
    data[4][1] = stmp;
    this._jcbTemplate = new JComboBox(saTemplates);
    try {
      this._jcbTemplate.setSelectedItem(stmp);
    } catch (Exception ei) {
      adapter.log("error", ei);
    }

    jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);

    MyTableModel tableModel = new MyTableModel(AlteraAtributosColumnNames, data);
    tableModel.setColumnEditable(0, false);

    /* table model -> can not edit 1st collunn */
    jTable1.setModel(tableModel);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel cm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(cm);

    DefaultCellEditor ed = null;
    ed = new DefaultCellEditor(this._jtfFrom);
    cm.addEditorForCell(0, 1, ed);
    ed = new DefaultCellEditor(this._jtfTo);
    cm.addEditorForCell(1, 1, ed);
    ed = new DefaultCellEditor(this._jtfSubject);
    cm.addEditorForCell(2, 1, ed);
    ed = new DefaultCellEditor(this._jtfMessage);
    cm.addEditorForCell(3, 1, ed);
    ed = new DefaultCellEditor(this._jcbTemplate);
    cm.addEditorForCell(4, 1, ed);

    /* criar botoes e arranjar dialogo */
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(500, 250);
    setVisible(true);
  }

  /**
   * Iniciar caixa de di?logo
   * 
   * @throws Exception
   */
  void jbInit() throws Exception {
    // configurar
    panel1.setLayout(borderLayout1);

    this.setSize(300, 250);

    /* resize */
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });

    /* botao OK */
    jButton1.setText(OK); //$NON-NLS-1$

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    /* botao cancelar */
    jButton2.setText(Cancelar); //$NON-NLS-1$
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });

    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    /* paineis */
    JPanel aux1 = new JPanel();
    aux1.setSize(100, 1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(100, 1);
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

    // from
    data[0][1] = jTable1.getStringAt(0, 1);

    // to
    data[1][1] = jTable1.getStringAt(1, 1);

    // subject
    data[2][1] = jTable1.getStringAt(2, 1);

    // message
    data[3][1] = jTable1.getStringAt(3, 1);

    // template
    data[4][1] = jTable1.getStringAt(4, 1);

    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

}
