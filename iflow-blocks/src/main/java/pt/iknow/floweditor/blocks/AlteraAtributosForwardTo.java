package pt.iknow.floweditor.blocks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pt.iflow.api.blocks.MessageBlock;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

/**
 * Project <b>Flow Editor</b>. Attribute alteration dialog for "ForwardTo"
 * block.
 * 
 * @author iKnow
 */
public class AlteraAtributosForwardTo extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 7007497191627115141L;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel2 = new JPanel();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JPanel jPanel3 = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private MyJTableX jTable1 = new MyJTableX();

  private JList _jlProfiles = null;
  private JScrollPane _jspProfiles = null;
  private JComboBox _jcbTypes = null;
  private JTextField _jtfUser = null;
  private JTextField _jtfProfile = null;
  private JTextField _jtfOrganicUnit = null;
  private JTextField _jtfUserMessage = null;
  private JTextField jtfDescription = null;
  private JTextField jtfResult = null;

  private String[] saProfiles = null;

  // INI - task annotations label
  private JTextField _jtfUpdateTaskAnnotationsLabel = null;
  private JComboBox _jcbTaskAnnotationsLabel = null;

  private JTextField _jtfUpdateTaskFolderCondition = null;
  private JTextField _jtfUpdateTaskFolder = null;
  
  private String[] taskAnnotationsLabelArray = null;
  // END - task annotations label

  private String description = null;
  private String result = null;

  private final String[] AlteraAtributosColumnNames;

  // check if match with ones in Uniflow's pt.iflow.blocks.BlockForwardTo
  private final String sFORWARD_TO_NONE;
  private final String sFORWARD_TO_PROFILE;
  private final String sFORWARD_TO_USER;
  private final String sFORWARD_TO_PROFILE_TEXT;
  private final String sFORWARD_TO_USER_MESSAGE;
  private final String sFORWARD_TO_ORGANIC_UNIT;

  // DO NOT CHANGE! MUST MATCH BlockForwardTo
  private final static String sTYPE_CHOOSE = "Escolha"; //$NON-NLS-1$
  private final static String sTYPE_PROFILE = "Perfil"; //$NON-NLS-1$
  private final static String sTYPE_USER = "Utilizador"; //$NON-NLS-1$
  private final static String sTYPE_PROFILE_TEXT = "PerfilTexto"; //$NON-NLS-1$
  private final static String sTYPE_ORGANIC_UNIT = "UnidadeOrganica" ;
  private final static String sTYPE_USER_MESSAGE = MessageBlock.MESSAGE_USER;
  private static final String sPROFILE_DELIM = ","; //$NON-NLS-1$

  private final static String sATTR_FORWARD_TO_TYPE = "Tipo"; //$NON-NLS-1$
  private final static String sATTR_FORWARD_TO_PROFILE = "Perfil"; //$NON-NLS-1$
  private final static String sATTR_FORWARD_TO_USER = "Utilizador"; //$NON-NLS-1$
  private final static String sATTR_FORWARD_TO_PROFILE_TEXT = "PerfilTexto"; //$NON-NLS-1$
  private final static String sATTR_FORWARD_TO_ORGANIC_UNIT_TEXT = "UnidadeOrganica";
  private final static String sATTR_FORWARD_TO_USER_MESSAGE = MessageBlock.MESSAGE_USER;

  private static final String sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL_CONDITION = "task_annotation_update_label_condition";
  private static final String sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL = "task_annotation_update_label";

  private static final String sATTR_FORWARD_TO_TASK_FOLDER_UPDATE_CONDITION = "task_folder_update_condition";
  private static final String sATTR_FORWARD_TO_TASK_FOLDER_UPDATE = "task_folder_update";
  
  private final String sDESC_FORWARD_TO_TYPE;
  private final String sDESC_FORWARD_TO_USER;
  private final String sDESC_FORWARD_TO_PROFILE_TEXT;
  private final String sDESC_FORWARD_TO_ORGANIC_UNIT_TEXT;

  private final String sDESC_FORWARD_TO_USER_MESSAGE;

  private final String sDESC_FORWARD_TO_UPDATE_LABEL_COND;
  
  private final String sDESC_FORWARD_TO_UPDATE_LABEL;
  private final String sFORWARD_TO_UPDATE_LABEL_NONE;

  private final String sDESC_FORWARD_TO_UPDATE_FOLDER_CONDITION;
  private final String sDESC_FORWARD_TO_UPDATE_FOLDER;
  
  // Map between externalized string and property
  private final HashMap<String,String> propToTypeMapping = new HashMap<String, String>();
  private final HashMap<String,String> typeToPropMapping = new HashMap<String, String>();

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;

  
  public AlteraAtributosForwardTo(FlowEditorAdapter adapter) {
    super(adapter, "", true); //$NON-NLS-1$

    
    AlteraAtributosColumnNames = new String [] {
        adapter.getString("AlteraAtributosForwardTo.name"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosForwardTo.value"), //$NON-NLS-1$
        };

    // check if match with ones in Uniflow's pt.iflow.blocks.BlockForwardTo
    sFORWARD_TO_NONE = adapter.getString("AlteraAtributosForwardTo.choose"); //$NON-NLS-1$
    sFORWARD_TO_PROFILE = adapter.getString("AlteraAtributosForwardTo.profile"); //$NON-NLS-1$
    sFORWARD_TO_USER = adapter.getString("AlteraAtributosForwardTo.user"); //$NON-NLS-1$
    sFORWARD_TO_PROFILE_TEXT = adapter.getString("AlteraAtributosForwardTo.textProfile"); //$NON-NLS-1$
    sFORWARD_TO_USER_MESSAGE = adapter.getString("AlteraAtributosForwardTo.userMessage"); //$NON-NLS-1$
    sFORWARD_TO_ORGANIC_UNIT = adapter.getString("AlteraAtributosForwardTo.organicUnit");

    sDESC_FORWARD_TO_TYPE = adapter.getString("AlteraAtributosForwardTo.kindDesc"); //$NON-NLS-1$
    // sDESC_FORWARD_TO_PROFILE = adapter.getString("AlteraAtributosForwardTo.profileDesc");//$NON-NLS-1$
    sDESC_FORWARD_TO_USER = adapter.getString("AlteraAtributosForwardTo.userDesc"); //$NON-NLS-1$
    sDESC_FORWARD_TO_PROFILE_TEXT = adapter.getString("AlteraAtributosForwardTo.textProfileDesc"); //$NON-NLS-1$
    sDESC_FORWARD_TO_ORGANIC_UNIT_TEXT = adapter.getString("AlteraAtributosForwardTo.organicUnitDesc"); //$NON-NLS-1$
    
    sDESC_FORWARD_TO_USER_MESSAGE = adapter.getString("AlteraAtributosForwardTo.userMessageDesc");

    
    sDESC_FORWARD_TO_UPDATE_LABEL_COND = adapter.getString("AlteraAtributosForwardTo.updateTaskAnnotationLabelCondition");
    sDESC_FORWARD_TO_UPDATE_LABEL = adapter.getString("AlteraAtributosForwardTo.updateTaskAnnotationLabel");
    sFORWARD_TO_UPDATE_LABEL_NONE = adapter.getString("AlteraAtributosForwardTo.choose"); 

    sDESC_FORWARD_TO_UPDATE_FOLDER_CONDITION = adapter.getString("AlteraAtributosForwardTo.updateTaskFolderCondition");
    sDESC_FORWARD_TO_UPDATE_FOLDER = adapter.getString("AlteraAtributosForwardTo.updateTaskFolder");
    
    initMaps();
    
    RepositoryClient rep = adapter.getRepository();
    if (rep != null && (saProfiles == null || saProfiles.length == 0)) {
      saProfiles = rep.listProfiles();
    }
    if (saProfiles == null) {
      saProfiles = new String[0];
    }

    rep = adapter.getRepository();
    String[] taskAnnotationsLabelAuxiliarArray = null;
    if (rep != null && (taskAnnotationsLabelArray == null || taskAnnotationsLabelArray.length == 0)) {
      taskAnnotationsLabelAuxiliarArray = rep.listTaskAnnotationLabels();
    }
    if (taskAnnotationsLabelAuxiliarArray == null) {
      taskAnnotationsLabelArray = new String[0];
    } else {
      taskAnnotationsLabelArray = new String[taskAnnotationsLabelAuxiliarArray.length +1];
      taskAnnotationsLabelArray[0] = sFORWARD_TO_UPDATE_LABEL_NONE;
      for (int i=0; i<taskAnnotationsLabelAuxiliarArray.length; i++){
        taskAnnotationsLabelArray[i+1] = taskAnnotationsLabelAuxiliarArray[i];
      }
    }

  }


  private void initMaps() {
    propToTypeMapping.put(sFORWARD_TO_NONE, sTYPE_CHOOSE);
    propToTypeMapping.put(sFORWARD_TO_PROFILE, sTYPE_PROFILE);
    propToTypeMapping.put(sFORWARD_TO_USER, sTYPE_USER);
    propToTypeMapping.put(sFORWARD_TO_PROFILE_TEXT, sTYPE_PROFILE_TEXT);
    propToTypeMapping.put(sFORWARD_TO_USER_MESSAGE, sTYPE_USER_MESSAGE);
    propToTypeMapping.put(sFORWARD_TO_ORGANIC_UNIT, sTYPE_ORGANIC_UNIT);

    typeToPropMapping.put(sTYPE_CHOOSE,sFORWARD_TO_NONE);
    typeToPropMapping.put(sTYPE_PROFILE,sFORWARD_TO_PROFILE);
    typeToPropMapping.put(sTYPE_USER,sFORWARD_TO_USER);
    typeToPropMapping.put(sTYPE_PROFILE_TEXT,sFORWARD_TO_PROFILE_TEXT);
    typeToPropMapping.put(sTYPE_USER_MESSAGE, sFORWARD_TO_USER_MESSAGE);
    typeToPropMapping.put(sTYPE_ORGANIC_UNIT, sFORWARD_TO_ORGANIC_UNIT);
  }
  
  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    String selectedProfiles = ""; //$NON-NLS-1$
    String[][] newData = new String[12][2];

    // get selected values to a csv string
    Object[] soSelected = _jlProfiles.getSelectedValues();
    if (soSelected.length > 0) {
      selectedProfiles = (String) soSelected[0];
    }
    for (int i = 1; i < soSelected.length; i++) {
      selectedProfiles = selectedProfiles + sPROFILE_DELIM
      + soSelected[i];
    }

    newData[0][0] = sATTR_FORWARD_TO_TYPE; // Type
    newData[0][1] = propToTypeMapping.get(data[0][1]);

    newData[1][0] = sATTR_FORWARD_TO_PROFILE; // profile
    newData[1][1] = selectedProfiles;

    newData[2][0] = sATTR_FORWARD_TO_USER; // User
    newData[2][1] = (String)data[1][1];

    newData[3][0] = sATTR_FORWARD_TO_PROFILE_TEXT; // Profile Text
    newData[3][1] = (String)data[2][1];
    
    newData[4][0] = sATTR_FORWARD_TO_ORGANIC_UNIT_TEXT;
    newData[4][1] = (String)data[3][1];
    
    newData[5][0] = sATTR_FORWARD_TO_USER_MESSAGE; // User Msg
    newData[5][1] = (String)data[4][1];

    newData[6][0] = sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL_CONDITION; // task annnotation update label cond
    newData[6][1] = (String)data[5][1];

    newData[7][0] = sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL; // task annnotation update label
    newData[7][1] = (String)data[6][1];

    newData[8][0] = sATTR_FORWARD_TO_TASK_FOLDER_UPDATE_CONDITION; // task folder update condition
    newData[8][1] = (String)data[7][1];
    
    newData[9][0] = sATTR_FORWARD_TO_TASK_FOLDER_UPDATE; // task folder update
    newData[9][1] = (String)data[8][1];
    
    // this is common to all blocks, right?
    newData[10][0] = AlteraAtributos.sDESCRIPTION;
    newData[10][1] = description;// this.jtfDescription.getText();
    newData[11][0] = AlteraAtributos.sRESULT;
    newData[11][1] = result;// this.jtfResult.getText();

    return newData;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    data = new String[9][2];

    Map<String,String> attr = new HashMap<String, String>();
    for(Atributo a : atributos)
      attr.put(a.getNome(), a.getValor());

    // TYPE
    String type = null;
    data[0][0] = sDESC_FORWARD_TO_TYPE;
    type = attr.get(sATTR_FORWARD_TO_TYPE);
    if (StringUtilities.isEmpty(type)) {
      type = AlteraAtributosForwardTo.sTYPE_CHOOSE;
    }
    data[0][1] = typeToPropMapping.get(type);


    String stmp = null;
    // USER
    data[1][0] = sDESC_FORWARD_TO_USER;
    stmp = attr.get(sATTR_FORWARD_TO_USER);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = ""; //$NON-NLS-1$
    }
    data[1][1] = stmp;

    // PROFILE TEXT
    data[2][0] = sDESC_FORWARD_TO_PROFILE_TEXT;
    stmp = attr.get(sATTR_FORWARD_TO_PROFILE_TEXT);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = ""; //$NON-NLS-1$
    }
    data[2][1] = stmp;
    
    //OU Message    
    data[3][0] = sDESC_FORWARD_TO_ORGANIC_UNIT_TEXT;
    stmp = attr.get(sATTR_FORWARD_TO_ORGANIC_UNIT_TEXT);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = ""; //$NON-NLS-1$
    }
    data[3][1] = stmp;
    
    // USER MESSAGE
    data[4][0] = sDESC_FORWARD_TO_USER_MESSAGE;
    stmp = attr.get(sATTR_FORWARD_TO_USER_MESSAGE);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[4][1] = stmp;

    // TASK ANNOTATION LABEL UPDATE CONDITION
    data[5][0] = sDESC_FORWARD_TO_UPDATE_LABEL_COND;
    stmp = attr.get(sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL_CONDITION);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[5][1] = stmp;

    // TASK ANNOTATION LABEL
    data[6][0] = sDESC_FORWARD_TO_UPDATE_LABEL;
    stmp = attr.get(sATTR_FORWARD_TO_TASK_ANNOTATION_UPDATE_LABEL);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[6][1] = stmp;

    // TASK FOLDER CONDITION
    data[7][0] = sDESC_FORWARD_TO_UPDATE_FOLDER_CONDITION;
    stmp = attr.get(sATTR_FORWARD_TO_TASK_FOLDER_UPDATE_CONDITION);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[7][1] = stmp;
    
    // TASK FOLDER
    data[8][0] = sDESC_FORWARD_TO_UPDATE_FOLDER;
    stmp = attr.get(sATTR_FORWARD_TO_TASK_FOLDER_UPDATE);
    if (StringUtilities.isEmpty(stmp)) {
      stmp = "";
    }
    data[8][1] = stmp;
    
    this.description = attr.get(AlteraAtributos.sDESCRIPTION);
    if (null == this.description)
      this.description = "";
    this.result = attr.get(AlteraAtributos.sRESULT);
    if (null == this.result)
      this.result = "";

    jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);

    /* table model -> can not edit 1st collunn */
    MyTableModel model = new MyTableModel(AlteraAtributosColumnNames, data);
    model.setColumnEditable(0, false);
    jTable1.setModel(model);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel rm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(rm);

    /* Combo box types */
    String[] satmp = new String[5];
    satmp[0] = sFORWARD_TO_NONE;
    satmp[1] = sFORWARD_TO_USER;
    satmp[2] = sFORWARD_TO_PROFILE_TEXT;
    satmp[3] = sFORWARD_TO_PROFILE;
    satmp[4] = sFORWARD_TO_ORGANIC_UNIT;
    this._jcbTypes = new JComboBox(satmp);
    this._jcbTypes.setSelectedItem(data[0][1]);

    /* List Profiles */
    _jlProfiles = new JList(saProfiles);
    String prof = attr.get(sATTR_FORWARD_TO_PROFILE);
    if(null != prof) {
      StringTokenizer stProfiles = new StringTokenizer(prof,
          sPROFILE_DELIM);
      int[] selectedIndices = new int[stProfiles.countTokens()];
      for (int i = 0; i < selectedIndices.length; i++) {
        _jlProfiles.setSelectedValue(stProfiles.nextToken(), true);
        selectedIndices[i] = _jlProfiles.getSelectedIndex();
      }
      _jlProfiles.setSelectedIndices(selectedIndices);
    }
    _jspProfiles = new JScrollPane(_jlProfiles);
    if (saProfiles.length == 0) {
      this._jlProfiles.setEnabled(false);
    }

    /* User box */
    this._jtfUser = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[1][1])) { //$NON-NLS-1$
      this._jtfUser.setText((String) data[1][1]);
    }

    /* Profile Text box */
    this._jtfProfile = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[2][1])) { //$NON-NLS-1$
      this._jtfProfile.setText((String) data[2][1]);
    }
    
    /* OU Text box */
    this._jtfOrganicUnit = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[3][1])) { //$NON-NLS-1$
      this._jtfOrganicUnit.setText((String) data[3][1]);
    }
    /* User Message Text box */
    this._jtfUserMessage = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[4][1])) {
      this._jtfUserMessage.setText((String) data[4][1]);
    }

    /* task Annotations Label condition */
    this._jtfUpdateTaskAnnotationsLabel = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[5][1])) {
      this._jtfUpdateTaskAnnotationsLabel.setText((String) data[5][1]);
    }

    this._jcbTaskAnnotationsLabel = new JComboBox(taskAnnotationsLabelArray);
    
    String annotationsLabelSelectedItem = data[6][1];
    if (annotationsLabelSelectedItem == null || "".equals(annotationsLabelSelectedItem)){
      annotationsLabelSelectedItem = this.sFORWARD_TO_UPDATE_LABEL_NONE;
    }
    this._jcbTaskAnnotationsLabel.setSelectedItem(annotationsLabelSelectedItem);

    /* task folder Condition*/
    this._jtfUpdateTaskFolderCondition = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[7][1])) {
      this._jtfUpdateTaskFolderCondition.setText((String) data[6][1]);
    }    
    
    /* task folder */
    this._jtfUpdateTaskFolder = new JTextField();
    if (StringUtilities.isNotEmpty((String) data[8][1])) {
      this._jtfUpdateTaskFolder.setText((String) data[8][1]);
    }
    
    /* Enable/Disable stuff */
    DefaultCellEditor ed = new DefaultCellEditor(this._jcbTypes);
    rm.addEditorForCell(0, 1, ed);
    ed = new DefaultCellEditor(this._jtfUser);
    rm.addEditorForCell(1, 1, ed);
    ed = new DefaultCellEditor(this._jtfProfile);
    rm.addEditorForCell(2, 1, ed);    
    ed = new DefaultCellEditor(this._jtfOrganicUnit);
    rm.addEditorForCell(3, 1, ed);    
    ed = new DefaultCellEditor(this._jtfUserMessage);
    rm.addEditorForCell(4, 1, ed);
    ed = new DefaultCellEditor(this._jtfUpdateTaskAnnotationsLabel);
    rm.addEditorForCell(5, 1, ed);
    ed = new DefaultCellEditor(this._jcbTaskAnnotationsLabel);
    rm.addEditorForCell(6, 1, ed);
    ed = new DefaultCellEditor(this._jtfUpdateTaskFolderCondition);
    rm.addEditorForCell(7, 1, ed);
    ed = new DefaultCellEditor(this._jtfUpdateTaskFolder);
    rm.addEditorForCell(8, 1, ed);
    
    this._jcbTypes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jcbTypeAction();
      }
    });

    /* criar bot�es e arranjar dialogo */
    jbInit();

    toggleEnabledFields();
    
    // repaint();
    pack();
    this.setSize(500, 350);
    setVisible(true);

  }

  /**
   * Iniciar caixa de di�logo
   * 
   */
  void jbInit() {
    // configurar
    panel1.setLayout(borderLayout1);

    /* bot�o OK */
    jButton1.setText(adapter.getString("Button.OK")); //$NON-NLS-1$

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    /* bot�o cancelar */
    jButton2.setText(adapter.getString("Button.CANCEL")); //$NON-NLS-1$
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
    this.getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(100, 1);
    this.getContentPane().add(aux2, BorderLayout.EAST);

    jScrollPane1.getViewport().add(jTable1, null);

    // jPanel2.add(_jspProfiles);
    jPanel2.add(jButton1, null);
    jPanel2.add(jButton2, null);

    jPanel3.setLayout(new GridBagLayout());
    GridBagConstraints gc;
    JLabel lbl;
    gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 0;
    gc.insets=new Insets(2,2,0,2);
    gc.anchor=GridBagConstraints.LINE_START;
    jPanel3.add(lbl = new JLabel(adapter.getString("AlteraAtributos.descriptionLabel")), gc);
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.gridy = 0;
    gc.insets=new Insets(2,2,0,2);
    jPanel3.add(jtfDescription = new JTextField(20),gc);
    lbl.setLabelFor(jtfDescription);
    jtfDescription.setText(this.description);
    gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 1;
    gc.insets=new Insets(2,2,4,2);
    gc.anchor=GridBagConstraints.LINE_START;
    jPanel3.add(lbl = new JLabel(adapter.getString("AlteraAtributos.descriptionResultLabel")), gc);
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.gridy = 1;
    gc.insets=new Insets(2,2,4,2);
    jPanel3.add(jtfResult = new JTextField(20),gc);
    lbl.setLabelFor(jtfResult);
    jtfResult.setText(this.result);

    JPanel jp = new JPanel();
    jp.setLayout(new BorderLayout());

    jp.add(jScrollPane1, BorderLayout.CENTER);
    jp.add(_jspProfiles, BorderLayout.EAST);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);
    this.getContentPane().add(jp, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
  }

  private void jcbTypeAction() {
    toggleEnabledFields();
  }

  private void toggleEnabledFields() {
    DefaultCellEditor dceTF = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(1, 1);
    DefaultCellEditor dceTF2 = (DefaultCellEditor) jTable1.getMyColumnEditorModel().getEditor(2, 1);

    String selectedType = (String)_jcbTypes.getSelectedItem();
    if (selectedType.equals(sFORWARD_TO_PROFILE)) {
      _jlProfiles.setEnabled(true);
      editCell(_jtfUser, 1, 1, false, Color.gray);
      editCell(_jtfProfile, 2, 1, false, Color.gray);
      editCell(_jtfOrganicUnit, 3, 1, false, Color.gray);
    } else if (selectedType.equals(sFORWARD_TO_USER)) {
      _jlProfiles.setEnabled(false);
      editCell(_jtfUser, 1, 1, true, Color.white);
      editCell(_jtfProfile, 2, 1, false, Color.gray);
      editCell(_jtfOrganicUnit, 3, 1, false, Color.gray);
    } else if (selectedType.equals(sFORWARD_TO_PROFILE_TEXT)) {
      _jlProfiles.setEnabled(false);
      editCell(_jtfUser, 1, 1, false, Color.gray);
      editCell(_jtfProfile, 2, 1, true, Color.white);
      editCell(_jtfOrganicUnit, 3, 1, false, Color.gray);
    } else if (selectedType.equals(sFORWARD_TO_ORGANIC_UNIT)) {
      _jlProfiles.setEnabled(false);	
      editCell(_jtfUser, 1, 1, false, Color.gray);
      editCell(_jtfProfile, 2, 1, false, Color.gray);
      editCell(_jtfOrganicUnit, 3, 1, true, Color.white);
    } else {
      _jlProfiles.setEnabled(false);
      editCell(_jtfUser, 1, 1, false, Color.gray);
      editCell(_jtfProfile, 2, 1, false, Color.gray);
      editCell(_jtfOrganicUnit, 3, 1, false, Color.gray);
    }
    dceTF.stopCellEditing();
    dceTF2.stopCellEditing();    
  }
  
  /**
   * Edits given cell with input.
   * 
   * @param component
   *            Field to edit.
   * @param row
   *            Row of the component.
   * @param column
   *            Column of the component.
   * @param enabled
   *            Component enabled/disabled.
   * @param bg
   *            Background color of the component.
   */
  private void editCell(final JComponent component, int row, int column,
      boolean enabled, Color bg) {
    jTable1.editCellAt(row, column);
    component.setEnabled(enabled);
    component.setBackground(bg);
  }

  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    // Type
    data[0][1] = (String)jTable1.getValueAt(0, 1);

    // User
    data[1][1] = (String)jTable1.getValueAt(1, 1);

    // Profile Text
    data[2][1] = (String)jTable1.getValueAt(2, 1);

    // User Message
    data[3][1] = (String) jTable1.getValueAt(3, 1);

    // VALIDATION
    if (data[0][1] == null
        || data[0][1].equals(sFORWARD_TO_NONE)) {
      adapter.showError(adapter.getString("AlteraAtributosForwardTo.errorNoUserOrProfile")); //$NON-NLS-1$
      return;
    } else {
      if (data[0][1].equals(sFORWARD_TO_USER)
          && StringUtilities.isEmpty((String) data[1][1])) {
        adapter.showError(adapter.getString("AlteraAtributosForwardTo.errorNoUser")); //$NON-NLS-1$
        return;
      }
      if (data[0][1].equals(sFORWARD_TO_PROFILE_TEXT)
                  && StringUtilities.isEmpty((String) data[2][1])) {
        adapter.showError(adapter.getString("AlteraAtributosForwardTo.errorNoTextProfile")); //$NON-NLS-1$
        return;
      }
      if (data[0][1].equals(sFORWARD_TO_PROFILE)
          && (_jlProfiles.getSelectedIndex() == -1)) {
        adapter.showError(adapter.getString("AlteraAtributosForwardTo.errorNoProfile")); //$NON-NLS-1$
        return;
      }
    }

    String updateLabelCondition = data[4][1];
    if (!StringUtilities.isEmpty(updateLabelCondition)) {
      String updateLabel = data[5][1];
      if (updateLabel == null || sFORWARD_TO_UPDATE_LABEL_NONE.equals(updateLabel) || "".equals(updateLabel)){
        adapter.showError(adapter.getString("AlteraAtributosForwardTo.errorNoLabelSelect")); //$NON-NLS-1$
        return;
      }
    }

    this.description = this.jtfDescription.getText();
    this.result = this.jtfResult.getText();

    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  
}
