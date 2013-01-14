package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosNotification
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
import javax.swing.JCheckBox;
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

public class AlteraAtributosNotification extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 2455045935000020823L;
  
  // Map this
  protected static final String toTypeUser = "user"; //$NON-NLS-1$
  protected static final String toTypeProfile = "profile"; //$NON-NLS-1$
  protected static final String toTypeTextProfile = "textProfile"; //$NON-NLS-1$
  protected static final String toTypeIntervenients = "intervenient"; //$NON-NLS-1$
  
  protected static final String slinkDetalhe = "linkDetalhe"; //$NON-NLS-1$
  // Into this
  private final String toTypePerfilDesc;
  private final String toTypePerfilTextoDesc;
  private final String toTypeUserDesc;
  private final String toTypeIntervenientesDesc;

  private final String _sSELECT;

  private final HashMap<String, String> descToValMapping = new HashMap<String, String>();
  private final HashMap<String, String> valToDescMapping = new HashMap<String, String>();
  // check if math with ones in Uniflow's pt.iflow.blocks.BlockForwardTo
  protected final static String sNOTIFICATION_TYPE = "type"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_TO = "to"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_TO_PROFILE = "profile"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_MESSAGE = "message"; //$NON-NLS-1$

  public final String sNOTIFICATION_TO_TYPE_DESC;
  public final String sNOTIFICATION_TO_PERFIL_DESC;
  public final String sNOTIFICATION_TO_PERFILTEXTO_DESC;
  public final String sNOTIFICATION_TO_USER_DESC;
  public final String sNOTIFICATION_MESSAGE_DESC;
  public final String sNOTIFICATION_NA_DESC;

  private final String[] toTypeModes;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;
  
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton jButtonOk = new JButton();
  JButton jButtonCancel = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  MyJTableX jTable1 = new MyJTableX();

  JTextField _jtfToPerfilTexto = null;
  JTextField _jtfMessage = null;
  JComboBox _jcbToTypeModes = null;
  JComboBox _jcbToPerfil = null;

  JCheckBox link = new JCheckBox();
  
  String[] saProfiles = null;

  private final String[] AlteraAtributosColumnNames;

  public AlteraAtributosNotification(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$
    // Into this
    toTypePerfilDesc = adapter.getString("AlteraAtributosNotification.profile"); //$NON-NLS-1$
    toTypePerfilTextoDesc = adapter.getString("AlteraAtributosNotification.textProfile"); //$NON-NLS-1$
    toTypeUserDesc = adapter.getString("AlteraAtributosNotification.user"); //$NON-NLS-1$
    toTypeIntervenientesDesc = adapter.getString("AlteraAtributosNotification.intervenient"); //$NON-NLS-1$

    _sSELECT = adapter.getString("AlteraAtributosNotification.choose"); //$NON-NLS-1$

    sNOTIFICATION_TO_TYPE_DESC = adapter.getString("AlteraAtributosNotification.toType"); //$NON-NLS-1$
    sNOTIFICATION_TO_PERFIL_DESC = adapter.getString("AlteraAtributosNotification.toProfile"); //$NON-NLS-1$
    sNOTIFICATION_TO_PERFILTEXTO_DESC = adapter.getString("AlteraAtributosNotification.toTextProfile"); //$NON-NLS-1$
    sNOTIFICATION_TO_USER_DESC = adapter.getString("AlteraAtributosNotification.toUser"); //$NON-NLS-1$
    sNOTIFICATION_MESSAGE_DESC = adapter.getString("AlteraAtributosNotification.message"); //$NON-NLS-1$
    sNOTIFICATION_NA_DESC = adapter.getString("AlteraAtributosNotification.na"); //$NON-NLS-1$

    toTypeModes = new String[]{ _sSELECT, toTypeUserDesc, toTypeIntervenientesDesc, toTypePerfilDesc, toTypePerfilTextoDesc };
    AlteraAtributosColumnNames = new String[]{ adapter.getString("AlteraAtributosNotification.name"), adapter.getString("AlteraAtributosNotification.value") }; //$NON-NLS-1$ //$NON-NLS-2$

    initMaps();

  }

  private void initMaps() {
    descToValMapping.put(toTypePerfilDesc, toTypeProfile);
    descToValMapping.put(toTypePerfilTextoDesc, toTypeTextProfile);
    descToValMapping.put(toTypeUserDesc, toTypeUser);
    descToValMapping.put(toTypeIntervenientesDesc, toTypeIntervenients);

    valToDescMapping.put(toTypeProfile, toTypePerfilDesc);
    valToDescMapping.put(toTypeTextProfile, toTypePerfilTextoDesc);
    valToDescMapping.put(toTypeUser, toTypeUserDesc);
    valToDescMapping.put(toTypeIntervenients, toTypeIntervenientesDesc);
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
     
    if(size == 4) size = 5;
    
    data = new String[size][2];

    String stmp = null;

    // TO_TYPE
    data[0][0] = sNOTIFICATION_TO_TYPE_DESC;
    if (atributos.size() > 0 && atributos.get(0) != null)
      stmp = atributos.get(0).getValor();
    if (StringUtilities.isEmpty(stmp))
      stmp = _sSELECT;
    this._jcbToTypeModes = new JComboBox(toTypeModes);
    stmp = valToDescMapping.get(stmp);
    if(StringUtilities.isEmpty(stmp)) stmp = _sSELECT;
    data[0][1] = stmp;
    this._jcbToTypeModes.setSelectedItem(stmp);

    // TO_PERFIL
    stmp = null;
    data[1][0] = sNOTIFICATION_TO_PERFIL_DESC;
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
    data[2][0] = sNOTIFICATION_NA_DESC;
    if (atributos.size() > 2 && atributos.get(2) != null)
      stmp = new String(((Atributo) atributos.get(2)).getValor());
    if (StringUtilities.isEmpty(stmp))
      stmp = ""; //$NON-NLS-1$
    data[2][1] = stmp;
    this._jtfToPerfilTexto = new JTextField();
    this._jtfToPerfilTexto.setText((String) data[2][1]);

    // MESSAGE
    stmp = null;
    data[3][0] = sNOTIFICATION_MESSAGE_DESC;
    if (atributos.size() > 3 && atributos.get(3) != null)
      stmp = new String(((Atributo) atributos.get(3)).getValor());
    if (StringUtilities.isEmpty(stmp))
      stmp = ""; //$NON-NLS-1$
    data[3][1] = stmp;
    this._jtfMessage = new JTextField();
    this._jtfMessage.setText((String) data[3][1]);

    String[][] dataAux = new  String[4][2];
    
for(int i= 0; i < 4 ; i++){
    dataAux[i][0] = data[i][0];
    dataAux[i][1] = data[i][1];
}
	
    jTable1 = new MyJTableX(dataAux, AlteraAtributosColumnNames);

    /* table model -> can not edit 1st collunn */
    MyTableModel model = new MyTableModel(AlteraAtributosColumnNames, dataAux);
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
        checkComboStatus();
      }
    });

    //CHECK BOX LINK GET FROM XML

    for (Atributo atributo : atributos) {
  	  if(atributo.getNome().equals(slinkDetalhe)){
  	    data[4][0] = slinkDetalhe;
  	    data[4][1] = atributo.getValor();
  	  }
    }
    
    /* criar botoes e arranjar dialogo */
    jbInit();

    this.setSize(500, 250);
    setVisible(true);
  }

  private void checkComboStatus() {
    DefaultCellEditor dceT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(0, 1);
    JComboBox jcbToTypeModes = (JComboBox) dceT.getComponent();
    DefaultCellEditor dceP = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(1, 1);
    JComboBox jcbToPerfil = (JComboBox) dceP.getComponent();
    DefaultCellEditor dcePT = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(2, 1);
    JTextField jtfToPerfilTexto = (JTextField) dcePT.getComponent();
    DefaultCellEditor dceMessage = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(3, 1);
    JTextField jtfMessage = (JTextField) dceMessage.getComponent();

    String sType = (String) jcbToTypeModes.getSelectedItem();
    if(_sSELECT.equals(sType)) {
      jTable1.setValueAt(sNOTIFICATION_NA_DESC, 2, 0);
      jcbToPerfil.setEnabled(false);
      jtfToPerfilTexto.setEnabled(false);
      jtfMessage.setEditable(false);
      jButtonOk.setEnabled(false);
      jtfMessage.setBackground(Color.gray);
      jtfToPerfilTexto.setBackground(Color.gray);
    } else {
      jtfMessage.setEditable(true);
      jButtonOk.setEnabled(true);
      jtfMessage.setBackground(Color.white);
      if (toTypePerfilDesc.equals(sType)) {
        jcbToPerfil.setEnabled(true);
        jtfToPerfilTexto.setEnabled(false);
        jtfToPerfilTexto.setBackground(Color.gray);
      }
      else if(toTypePerfilTextoDesc.equals(sType)) {
        jTable1.setValueAt(sNOTIFICATION_TO_PERFILTEXTO_DESC, 2, 0);
        jcbToPerfil.setEnabled(false);
        jtfToPerfilTexto.setEnabled(true);
        jtfToPerfilTexto.setBackground(Color.white);
      }
      else if(toTypeIntervenientesDesc.equals(sType)) {
        jTable1.setValueAt(sNOTIFICATION_NA_DESC, 2, 0);
        jcbToPerfil.setEnabled(false);
        jtfToPerfilTexto.setEnabled(false);
        jtfToPerfilTexto.setBackground(Color.gray);
      }
      else {
        jTable1.setValueAt(sNOTIFICATION_TO_USER_DESC, 2, 0);
        jcbToPerfil.setEnabled(false);
        jtfToPerfilTexto.setEnabled(true);
        jtfToPerfilTexto.setBackground(Color.white);
      }
      dceT.stopCellEditing();
      dceP.stopCellEditing();
      dcePT.stopCellEditing();
    }
    repaint();
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
    jButtonOk.setText(OK); //$NON-NLS-1$

    jButtonOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonOkActionPerformed(e);
      }
    });

    /* botao cancelar */
    jButtonCancel.setText(Cancelar); //$NON-NLS-1$
    jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonCancelActionPerformed(e);
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

    JPanel buttonPanel = new JPanel();
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(jButtonOk, null);
    buttonPanel.add(jButtonCancel, null);
    JPanel fillerPanel = new JPanel();
//    this.getContentPane().add(fillerPanel, BorderLayout.NORTH);

    //CHECKBOX NOTIFICAÇÃO
    JPanel checkPanel = new JPanel();
    link.setText("Incluir link para detalhes do processo");
    link.setSelected(Boolean.valueOf("" + data[4][1]));
    checkPanel.add(link , BorderLayout.SOUTH );
    
    JPanel teste = new JPanel();
    teste.add(fillerPanel, BorderLayout.NORTH);
    teste.add(checkPanel, BorderLayout.SOUTH);
    
    
    this.getContentPane().add(teste, BorderLayout.NORTH);
    
    dialogComponentResized(null);
    checkComboStatus();
  }

  /* OK */
  void jButtonOkActionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    data[0][0] = sNOTIFICATION_TYPE;
    data[0][1] = descToValMapping.get(jTable1.getValueAt(0, 1));

    data[1][0] = sNOTIFICATION_TO_PROFILE;
    data[1][1] = jTable1.getStringAt(1, 1);

    data[2][0] = sNOTIFICATION_TO;
    data[2][1] = jTable1.getStringAt(2, 1);

    data[3][0] = sNOTIFICATION_MESSAGE;
    data[3][1] = jTable1.getStringAt(3, 1);

    data[4][0] = slinkDetalhe;
    data[4][1] = ""+link.isSelected();
    
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButtonCancelActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }

}
