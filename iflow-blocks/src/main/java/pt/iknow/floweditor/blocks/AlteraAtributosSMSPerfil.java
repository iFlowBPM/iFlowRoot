package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosSMSPerfil
 *
 *  desc: dialogo para alterar atributos do bloco ForwardTo
 *
 ****************************************************/

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

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosSMSPerfil extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 2455045935000020823L;

  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  JTextField _jtfToPerfilTexto = null;
  JTextField _jtfMessage = null;
  JComboBox _jcbToTypeModes = null;
  JComboBox _jcbToPerfil = null;

  String[] saProfiles = null;

  private final String[] AlteraAtributosColumnNames;
  // Map this
  private static final String toTypePerfil = "Perfil"; //$NON-NLS-1$
  private static final String toTypePerfilTexto = "PerfilTexto"; //$NON-NLS-1$
  // Into this
  private final String toTypePerfilDesc;
  private final String toTypePerfilTextoDesc;

  private final HashMap<String, String> descToValMapping = new HashMap<String, String>();
  private final HashMap<String, String> valToDescMapping = new HashMap<String, String>();

  private final String[] toTypeModes;
  private final String _sSELECT;

  // check if math with ones in Uniflow's pt.iflow.blocks.BlockForwardTo
  public final static String sSMS_TO_TYPE = "Type of To"; //$NON-NLS-1$
  public final static String sSMS_TO_PERFILTEXTO = "To PerfilTexto"; //$NON-NLS-1$
  public final static String sSMS_TO_PERFIL = "To Perfil"; //$NON-NLS-1$
  public final static String sSMS_MESSAGE = "Message"; //$NON-NLS-1$

  public final String sSMS_TO_TYPE_DESC;
  public final String sSMS_TO_PERFILTEXTO_DESC;
  public final String sSMS_TO_PERFIL_DESC;
  public final String sSMS_MESSAGE_DESC;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;

  public AlteraAtributosSMSPerfil(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$

    AlteraAtributosColumnNames = new String []{
        adapter.getString("AlteraAtributosSMSPerfil.name"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosSMSPerfil.value"), //$NON-NLS-1$
    };

    // Into this
    toTypePerfilDesc = adapter.getString("AlteraAtributosSMSPerfil.profile"); //$NON-NLS-1$
    toTypePerfilTextoDesc = adapter.getString("AlteraAtributosSMSPerfil.textProfile"); //$NON-NLS-1$

    _sSELECT = adapter.getString("AlteraAtributosSMSPerfil.choose"); //$NON-NLS-1$

    sSMS_TO_TYPE_DESC = adapter.getString("AlteraAtributosSMSPerfil.toType"); //$NON-NLS-1$
    sSMS_TO_PERFILTEXTO_DESC = adapter.getString("AlteraAtributosSMSPerfil.toTextProfile"); //$NON-NLS-1$
    sSMS_TO_PERFIL_DESC = adapter.getString("AlteraAtributosSMSPerfil.toProfile"); //$NON-NLS-1$
    sSMS_MESSAGE_DESC = adapter.getString("AlteraAtributosSMSPerfil.message"); //$NON-NLS-1$

    toTypeModes = new String [] { toTypePerfilDesc, toTypePerfilTextoDesc };

    initMaps();
  }

  private void initMaps() {
    descToValMapping.put(toTypePerfilDesc, toTypePerfil);
    descToValMapping.put(toTypePerfilTextoDesc, toTypePerfilTexto);

    valToDescMapping.put(toTypePerfil, toTypePerfilDesc);
    valToDescMapping.put(toTypePerfilTexto, toTypePerfilTextoDesc);
  }

  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {

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

    int size = atributos.size();
    if (size < 4) {
      size = 4;
    }
    data = new String[size][2];

    String stmp = null;

    // TO_TYPE
    data[0][0] = sSMS_TO_TYPE_DESC;
    if (atributos.size() > 0 && atributos.get(0) != null)
      stmp = atributos.get(0).getValor();
    if (StringUtilities.isEmpty(stmp))
      stmp = toTypePerfil;
    data[0][1] = stmp;
    this._jcbToTypeModes = new JComboBox(toTypeModes);
    this._jcbToTypeModes.setSelectedItem(valToDescMapping.get(data[0][1]));

    // TO_PERFIL
    stmp = null;
    data[1][0] = sSMS_TO_PERFIL_DESC;
    if (atributos.size() > 1 && atributos.get(1) != null)
      stmp = atributos.get(1).getValor();
    if (StringUtilities.isEmpty(stmp))
      stmp = _sSELECT;
    String[] satmp = saProfiles;
    saProfiles = new String[satmp.length + 1];
    saProfiles[0] = _sSELECT;
    for (int p = 0; p < satmp.length; p++) {
      saProfiles[p + 1] = satmp[p];
    }
    data[1][1] = stmp;
    this._jcbToPerfil = new JComboBox(saProfiles);
    try {
      this._jcbToPerfil.setSelectedItem(stmp);
    } catch (Exception ei) {
      adapter.log("error", ei);
    }

    // TO_PERFILTEXTO
    stmp = null;
    data[2][0] = sSMS_TO_PERFILTEXTO_DESC;
    if (atributos.size() > 2 && atributos.get(2) != null)
      stmp = new String(((Atributo) atributos.get(2)).getValor());
    if (StringUtilities.isEmpty(stmp))
      stmp = ""; //$NON-NLS-1$
    data[2][1] = stmp;
    this._jtfToPerfilTexto = new JTextField();
    this._jtfToPerfilTexto.setText((String) data[2][1]);

    // MESSAGE
    stmp = null;
    data[3][0] = sSMS_MESSAGE_DESC;
    if (atributos.size() > 3 && atributos.get(3) != null)
      stmp = new String(((Atributo) atributos.get(3)).getValor());
    if (StringUtilities.isEmpty(stmp))
      stmp = ""; //$NON-NLS-1$
    data[3][1] = stmp;
    this._jtfMessage = new JTextField();
    this._jtfMessage.setText((String) data[3][1]);

    /* Enable/Disable stuff */
    stmp = (String) this._jcbToTypeModes.getSelectedItem();
    if (stmp.equals(toTypeModes[0])) {
      this._jtfToPerfilTexto.setEnabled(false);
      this._jtfToPerfilTexto.setBackground(Color.gray);
    } else {
      this._jcbToPerfil.setEnabled(false);
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
    ed = new DefaultCellEditor(this._jcbToTypeModes);
    rm.addEditorForCell(0, 1, ed);
    ed = new DefaultCellEditor(this._jcbToPerfil);
    rm.addEditorForCell(1, 1, ed);
    ed = new DefaultCellEditor(this._jtfToPerfilTexto);
    rm.addEditorForCell(2, 1, ed);
    ed = new DefaultCellEditor(this._jtfMessage);
    rm.addEditorForCell(3, 1, ed);

    this._jcbToTypeModes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultCellEditor dceT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(0, 1);
        JComboBox jcbToTypeModes = (JComboBox) dceT.getComponent();
        DefaultCellEditor dceP = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(1, 1);
        JComboBox jcbToPerfil = (JComboBox) dceP.getComponent();
        DefaultCellEditor dcePT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(2, 1);
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

    /* criar botoes e arranjar dialogo */
    jbInit();

    this.setSize(500, 250);
    setVisible(true);
  }

  /**
   * Iniciar caixa de dialogo
   * 
   * @throws Exception
   */
  void jbInit() {
    // configurar
    panel1.setLayout(borderLayout1);

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

    data[0][0] = sSMS_TO_TYPE;
    data[0][1] = descToValMapping.get(jTable1.getValueAt(0, 1));

    data[1][0] = sSMS_TO_PERFIL;
    data[1][1] = (String) jTable1.getValueAt(1, 1);

    data[2][0] = sSMS_TO_PERFILTEXTO;
    data[2][1] = (String) jTable1.getValueAt(2, 1);

    data[3][0] = sSMS_MESSAGE;
    data[3][1] = (String) jTable1.getValueAt(3, 1);

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
