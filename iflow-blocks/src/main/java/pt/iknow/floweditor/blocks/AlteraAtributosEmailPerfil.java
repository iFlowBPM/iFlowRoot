package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosEmailPerfil
 *
 *  desc: dialogo para alterar atributos do bloco ForwardTo
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
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

public class AlteraAtributosEmailPerfil extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = -641363100024758635L;

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
  JTextField _jtfToPerfilTexto = null;
  JTextField _jtfSubject = null;
  JTextField _jtfMessage = null;
  JComboBox _jcbToTypeModes = null;
  JComboBox _jcbToPerfil = null;
  JComboBox _jcbTemplate = null;

  String[] saProfiles = null;
  String[] saTemplates = null;

  public final static String[] toTypeModes = { "Perfil", "PerfilTexto" }; //$NON-NLS-1$ //$NON-NLS-2$
  public final static String sEMAIL_FROM = "From"; //$NON-NLS-1$
  public final static String sEMAIL_TO_TYPE = "Type of To"; //$NON-NLS-1$
  public final static String sEMAIL_TO_PERFILTEXTO = "To PerfilTexto"; //$NON-NLS-1$
  public final static String sEMAIL_TO_PERFIL = "To Perfil"; //$NON-NLS-1$
  public final static String sEMAIL_SUBJECT = "Subject"; //$NON-NLS-1$
  public final static String sEMAIL_MESSAGE = "Message"; //$NON-NLS-1$
  public final static String sEMAIL_TEMPLATE = "Template"; //$NON-NLS-1$

  final String[] AlteraAtributosColumnNames;

  // check if math with ones in Uniflow's pt.iflow.blocks.BlockEmailPerfil
  private final String _sSELECT;
  private final String sDESC_EMAIL_FROM;
  private final String sDESC_EMAIL_TO_TYPE;
  private final String sDESC_EMAIL_TO_PERFILTEXTO;
  private final String sDESC_EMAIL_TO_PERFIL;
  private final String sDESC_EMAIL_SUBJECT;
  private final String sDESC_EMAIL_MESSAGE;
  private final String sDESC_EMAIL_TEMPLATE;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;
  

  public AlteraAtributosEmailPerfil(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$
    AlteraAtributosColumnNames = new String [] {
        adapter.getString("AlteraAtributosEmailPerfil.name"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosEmailPerfil.value"), //$NON-NLS-1$
        };

    // check if math with ones in Uniflow's pt.iflow.blocks.BlockEmailPerfil
    _sSELECT = adapter.getString("AlteraAtributosEmailPerfil.choose"); //$NON-NLS-1$
    sDESC_EMAIL_FROM = adapter.getString("AlteraAtributosEmailPerfil.from"); //$NON-NLS-1$
    sDESC_EMAIL_TO_TYPE = adapter.getString("AlteraAtributosEmailPerfil.toType"); //$NON-NLS-1$
    sDESC_EMAIL_TO_PERFILTEXTO = adapter.getString("AlteraAtributosEmailPerfil.textProfile"); //$NON-NLS-1$
    sDESC_EMAIL_TO_PERFIL = adapter.getString("AlteraAtributosEmailPerfil.profile"); //$NON-NLS-1$
    sDESC_EMAIL_SUBJECT = adapter.getString("AlteraAtributosEmailPerfil.subject"); //$NON-NLS-1$
    sDESC_EMAIL_MESSAGE = adapter.getString("AlteraAtributosEmailPerfil.message"); //$NON-NLS-1$
    sDESC_EMAIL_TEMPLATE = adapter.getString("AlteraAtributosEmailPerfil.template"); //$NON-NLS-1$

  }


  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    // overide attribute names for backward compability!!
    data[0][0] = AlteraAtributosEmailPerfil.sEMAIL_FROM;
    // TO_TYPE
    data[1][0] = AlteraAtributosEmailPerfil.sEMAIL_TO_TYPE;
    // TO_PERFIL
    data[2][0] = AlteraAtributosEmailPerfil.sEMAIL_TO_PERFIL;
    // TO_PERFILTEXTO
    data[3][0] = AlteraAtributosEmailPerfil.sEMAIL_TO_PERFILTEXTO;
    // SUBJECT
    data[4][0] = AlteraAtributosEmailPerfil.sEMAIL_SUBJECT;
    // MESSAGE
    data[5][0] = AlteraAtributosEmailPerfil.sEMAIL_MESSAGE;
    // TEMPLATE
    data[6][0] = AlteraAtributosEmailPerfil.sEMAIL_TEMPLATE;

    return data;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    RepositoryClient rep = adapter.getRepository();
    if (rep != null && (saProfiles == null || saProfiles.length == 0)) {
      saProfiles = rep.listProfiles();
    }
    if (saProfiles == null) {
      saProfiles = new String[0];
    }

    if (rep != null && (saTemplates == null || saTemplates.length == 0)) {
      saTemplates = rep.listMailTemplates();
    }
    if (saTemplates == null) {
      saTemplates = new String[0];
    }

    int size = atributos.size();
    if (size < 7) {
      size = 7;
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

    // TO_TYPE
    data[1][0] = sDESC_EMAIL_TO_TYPE;
    if (atributos != null && atributos.size() > 1 && atributos.get(1) != null)
      stmp = new String(((Atributo) atributos.get(1)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = toTypeModes[0];
    data[1][1] = stmp;
    this._jcbToTypeModes = new JComboBox(toTypeModes);
    try {
      this._jcbToTypeModes.setSelectedItem(data[1][1]);
    } catch (Exception ei) {
      adapter.log("error", ei);
    }

    // TO_PERFIL
    data[2][0] = sDESC_EMAIL_TO_PERFIL;
    if (atributos != null && atributos.size() > 2 && atributos.get(2) != null)
      stmp = new String(((Atributo) atributos.get(2)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = _sSELECT;
    String[] satmp = saProfiles;
    saProfiles = new String[satmp.length + 1];
    saProfiles[0] = _sSELECT;
    for (int p = 0; p < satmp.length; p++) {
      saProfiles[p + 1] = satmp[p];
    }
    data[2][1] = stmp;
    this._jcbToPerfil = new JComboBox(saProfiles);
    try {
      this._jcbToPerfil.setSelectedItem(stmp);
    } catch (Exception ei) {
      adapter.log("error", ei);
    }

    // TO_PERFILTEXTO
    data[3][0] = sDESC_EMAIL_TO_PERFILTEXTO;
    if (atributos != null && atributos.size() > 3 && atributos.get(3) != null)
      stmp = new String(((Atributo) atributos.get(3)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[3][1] = stmp;
    this._jtfToPerfilTexto = new JTextField();
    this._jtfToPerfilTexto.setText((String) data[3][1]);

    // SUBJECT
    data[4][0] = sDESC_EMAIL_SUBJECT;
    if (atributos != null && atributos.size() > 4 && atributos.get(4) != null)
      stmp = new String(((Atributo) atributos.get(4)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[4][1] = stmp;
    this._jtfSubject = new JTextField();
    this._jtfSubject.setText((String) data[4][1]);

    // MESSAGE
    data[5][0] = sDESC_EMAIL_MESSAGE;
    if (atributos != null && atributos.size() > 5 && atributos.get(5) != null)
      stmp = new String(((Atributo) atributos.get(5)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = ""; //$NON-NLS-1$
    data[5][1] = stmp;
    this._jtfMessage = new JTextField();
    this._jtfMessage.setText((String) data[5][1]);

    // TEMPLATE
    data[6][0] = sDESC_EMAIL_TEMPLATE;
    if (atributos != null && atributos.size() > 6 && atributos.get(6) != null)
      stmp = new String(((Atributo) atributos.get(6)).getValor());
    else
      stmp = null;
    if (stmp == null || stmp.equals("")) //$NON-NLS-1$
      stmp = _sSELECT;
    satmp = saTemplates;
    saTemplates = new String[satmp.length + 1];
    saTemplates[0] = _sSELECT;
    for (int p = 0; p < satmp.length; p++) {
      saTemplates[p + 1] = satmp[p];
    }
    data[6][1] = stmp;
    this._jcbTemplate = new JComboBox(saTemplates);
    try {
      this._jcbTemplate.setSelectedItem(stmp);
    } catch (Exception ei) {
      adapter.log("error", ei);
    }

    /* Enable/Disable stuff */
    stmp = (String) this._jcbToTypeModes.getSelectedItem();
    if (stmp.equals(toTypeModes[0])) {
      this._jtfToPerfilTexto.setEnabled(false);
      this._jtfToPerfilTexto.setBackground(Color.gray);
    } else {
      this._jcbToPerfil.setEnabled(false);
      this._jcbToPerfil.setBackground(Color.gray);
    }

    jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);

    /* table model -> can not edit 1st collunn */
    MyTableModel model = new MyTableModel(AlteraAtributosColumnNames, data);
    model.setColumnEditable(0, false);
    jTable1.setModel(model);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    DefaultCellEditor ed = null;
    ed = new DefaultCellEditor(this._jtfFrom);
    rm.addEditorForCell(0, 1, ed);
    ed = new DefaultCellEditor(this._jcbToTypeModes);
    rm.addEditorForCell(1, 1, ed);
    ed = new DefaultCellEditor(this._jcbToPerfil);
    rm.addEditorForCell(2, 1, ed);
    ed = new DefaultCellEditor(this._jtfToPerfilTexto);
    rm.addEditorForCell(3, 1, ed);
    ed = new DefaultCellEditor(this._jtfSubject);
    rm.addEditorForCell(4, 1, ed);
    ed = new DefaultCellEditor(this._jtfMessage);
    rm.addEditorForCell(5, 1, ed);
    ed = new DefaultCellEditor(this._jcbTemplate);
    rm.addEditorForCell(6, 1, ed);

    this._jcbToTypeModes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultCellEditor dceT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(1, 1);
        JComboBox jcbToTypeModes = (JComboBox) dceT.getComponent();
        DefaultCellEditor dceP = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(2, 1);
        JComboBox jcbToPerfil = (JComboBox) dceP.getComponent();
        DefaultCellEditor dcePT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(3, 1);
        JTextField jtfToPerfilTexto = (JTextField) dcePT.getComponent();

        String stmp = (String) jcbToTypeModes.getSelectedItem();
        if (stmp.equals(toTypeModes[0])) {
          jcbToPerfil.setEnabled(true);
          jtfToPerfilTexto.setEnabled(false);
          jtfToPerfilTexto.setBackground(Color.gray);
        } else {
          jcbToPerfil.setEnabled(false);
          jtfToPerfilTexto.setEnabled(true);
          jtfToPerfilTexto.setBackground(Color.white);
        }
        dceT.stopCellEditing();
        dceP.stopCellEditing();
        dcePT.stopCellEditing();
      }
    });

    /* criar bot?es e arranjar dialogo */
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

    /* bot?o OK */
    jButton1.setText(OK); //$NON-NLS-1$

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    /* bot?o cancelar */
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

    data[0][1] = jTable1.getStringAt(0, 1);

    data[1][1] = jTable1.getStringAt(1, 1);

    data[2][1] = jTable1.getStringAt(2, 1);

    data[3][1] = jTable1.getStringAt(3, 1);

    data[4][1] = jTable1.getStringAt(4, 1);

    data[5][1] = jTable1.getStringAt(5, 1);

    // template
    data[6][1] = jTable1.getStringAt(6, 1);

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
