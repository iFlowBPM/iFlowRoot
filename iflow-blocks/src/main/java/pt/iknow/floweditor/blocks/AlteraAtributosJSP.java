package pt.iknow.floweditor.blocks;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;

public class AlteraAtributosJSP extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1L;

  private static final int LABEL_MAX_LENGTH = 50;
  
  protected static final int nPANEL_WIDTH = 800;
  protected static final int nPANEL_HEIGHT = 500;
  protected static final int nSPACER_SIZE = 100;

  JPanel jPanelMain = new JPanel(); // main window container

  JButton jButtonAddButton = new JButton(); // add button button
  JButton jButtonAddField = new JButton(); // add field button
  JButton jButtonClose = new JButton(); // close button
  JButton jButtonCancel = new JButton(); // cancel button
  JTableJSP jTable = null; // table
  JScrollPane jScrollPane = null; // table container
  JPreviewFormButtons jpButtons = null; // button panel
  JScrollPane jScrollPaneButtons = null; // button container

  //public final static Color cBG_COLOR = Color.WHITE;
  public final static Color cBG_COLOR = new Color(255,255,255);
  public final static String sROW_EDIT_PREFIX = "ROW_EDIT_"; //$NON-NLS-1$
  public final static String sROW_MOVE_UP = "ROW_UP_"; //$NON-NLS-1$
  public final static String sROW_MOVE_DOWN = "ROW_DOWN_"; //$NON-NLS-1$
  public final static String sROW_REMOVE = "ROW_REMOVE_"; //$NON-NLS-1$
  public final static String sROW_ADD_AT = "ROW_ADD_AT_"; //$NON-NLS-1$

  private final static String sAPPEND_DOCS_VAR = "append_docs";
  private final static String sFO_EXT = ".fo";

  public final String sCHOOSE;

  public final String sNO_PRINT;
  
  public final String[] saColNames;

  protected final ArrayList<Integer> alTABLE_PROPS = new ArrayList<Integer>();
  protected final HashSet<Integer> hsEDITABLE_TABLE_COL = new HashSet<Integer>();

  // BUTTON PREVIEW STUFF
  public static final Dimension FORM_BUTTON_DIMENSION = new Dimension(100,49);
  public final static String sBUTTON_EDIT_PREFIX = "BUTTON_EDIT_"; //$NON-NLS-1$
  public final static String sBUTTON_MOVE_LEFT = "BUTTON_LEFT_"; //$NON-NLS-1$
  public final static String sBUTTON_MOVE_RIGHT = "BUTTON_RIGHT_"; //$NON-NLS-1$
  public final static String sBUTTON_REMOVE = "BUTTON_REMOVE_"; //$NON-NLS-1$
  public final static String sBUTTON_ADD_AT = "BUTTON_ADD_AT_"; //$NON-NLS-1$

  protected static final String sCANCEL_TYPE = "_cancelar"; //$NON-NLS-1$
  protected static final String sRESET_TYPE = "_repor"; //$NON-NLS-1$
  protected static final String sSAVE_TYPE = "_guardar"; //$NON-NLS-1$
  protected static final String sPRINT_TYPE = "_imprimir"; //$NON-NLS-1$
  protected static final String sNEXT_TYPE = "_avancar"; //$NON-NLS-1$
  protected static final String sCUSTOM_TYPE = "_custom"; //$NON-NLS-1$
  protected static final String sRETURN_PARENT_TYPE = "_retornar_parent"; //$NON-NLS-1$

  private final static String[] saDEF_BUTTONS = { sCANCEL_TYPE, sRESET_TYPE, sSAVE_TYPE, 
    sPRINT_TYPE,  sNEXT_TYPE};
  protected final HashSet<String> hsREQ_BUTTONS = new HashSet<String>();
  protected final HashMap<String,String> hmBUTTON_TYPES = new HashMap<String,String>();
  protected final HashMap<String,String> hmBUTTON_TYPES_REV = new HashMap<String,String>(); // hmBUTTON_TYPES reverse
  protected final HashMap<String,String> hmBUTTON_INFO = new HashMap<String,String>();

  // BUTTON PREVIEW STUFF END

  private int exitStatus = EXIT_STATUS_CANCEL;

  private int CURR_BUTTON_ID = -1;
  
  public AlteraAtributosJSP(FlowEditorAdapter adapter) {
    super(adapter, true);

    sCHOOSE = adapter.getString("AlteraAtributosJSP.choose"); //$NON-NLS-1$

    sNO_PRINT = adapter.getString("AlteraAtributosJSP.no_print"); //$NON-NLS-1$

    saColNames = new String [] {
        adapter.getString("AlteraAtributosJSP.colnames.text"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosJSP.colnames.field_type"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosJSP.colnames.data_type"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosJSP.colnames.var"), //$NON-NLS-1$
    };

    initMaps();
  }


  private void initMaps() {
    alTABLE_PROPS.add(JSPFieldData.nPROP_TEXT);
    alTABLE_PROPS.add(JSPFieldData.nPROP_FIELD_TYPE);
    alTABLE_PROPS.add(JSPFieldData.nPROP_DATA_TYPE);
    alTABLE_PROPS.add(JSPFieldData.nPROP_VAR_NAME);

    // hsREQ_BUTTONS.add(sPRINT_TYPE);

    hmBUTTON_TYPES.put(sCANCEL_TYPE,adapter.getString("AlteraAtributosJSP.button.type.cancel")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sRESET_TYPE,adapter.getString("AlteraAtributosJSP.button.type.reset")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sSAVE_TYPE,adapter.getString("AlteraAtributosJSP.button.type.save")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sPRINT_TYPE,adapter.getString("AlteraAtributosJSP.button.type.print")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sNEXT_TYPE,adapter.getString("AlteraAtributosJSP.button.type.forward")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sCUSTOM_TYPE,adapter.getString("AlteraAtributosJSP.button.type.custom")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sRETURN_PARENT_TYPE,adapter.getString("AlteraAtributosJSP.button.type.return_to_parent")); //$NON-NLS-1$

    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.cancel"),sCANCEL_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.reset"),sRESET_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.save"),sSAVE_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.print"),sPRINT_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.forward"),sNEXT_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.custom"),sCUSTOM_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.return_to_parent"),sRETURN_PARENT_TYPE); //$NON-NLS-1$

    hmBUTTON_INFO.put(sCANCEL_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.cancel")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sRESET_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.reset")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sSAVE_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.save")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sPRINT_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.print")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sNEXT_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.forward")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sCUSTOM_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.custom")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sRETURN_PARENT_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.return_to_parent")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sCHOOSE,adapter.getString("AlteraAtributosJSP.button.choose")); //$NON-NLS-1$
  }

  
  @SuppressWarnings("unchecked")
  private static ArrayList<String>[] newArray(int n) {
    return (ArrayList<String>[])new ArrayList[n];
  }

  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    String[][] retObj = null;

    String[][] tmp = this.jTable.exportData();
    String[][] tmp2 = this.jpButtons.exportData();

    retObj = new String[tmp.length + tmp2.length][2];

    for (int i=0; i < tmp.length; i++) {
      retObj[i][0] = tmp[i][0];
      retObj[i][1] = tmp[i][1];
    }

    for (int i=0, j=tmp.length; i < tmp2.length; i++, j++) {
      retObj[j][0] = tmp2[i][0];
      retObj[j][1] = tmp2[i][1];
    }



    return retObj;
  }


  public void setDataIn(String title, List<Atributo> atributos) {
    this.setTitle(title);

    this.setSize(nPANEL_WIDTH,nPANEL_HEIGHT);

    try {
      this.jbInit(atributos);
      this.pack();

    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(nPANEL_WIDTH,nPANEL_HEIGHT);
    this.setModal(true);
    this.repaint();
    this.setLocationRelativeTo(getParent());
    this.setVisible(true);
  }

  private void updateButtonId(int id) {
    if (id > CURR_BUTTON_ID)
      CURR_BUTTON_ID = id;
  }

  private int getNextButtonId() {
    CURR_BUTTON_ID++;
    return CURR_BUTTON_ID;
  }
  
  void jbInit(List<Atributo> atributos) throws Exception {

    // MAIN WINDOW

    this.jPanelMain.setLayout(new BorderLayout());


    // borders
    JPanel aux=new JPanel();
    aux.setSize(nSPACER_SIZE,1);
    this.jPanelMain.add(aux, BorderLayout.WEST);
    aux=new JPanel();
    aux.setSize(nSPACER_SIZE,1);
    this.jPanelMain.add(aux, BorderLayout.EAST);
    aux=new JPanel();
    aux.setSize(1,nSPACER_SIZE);
    this.jPanelMain.add(aux, BorderLayout.NORTH);



    // table
    jTable = new JTableJSP(saColNames,atributos);
    jScrollPane = new JScrollPane(jTable);
    jScrollPane.getVerticalScrollBar().setUnitIncrement(10); // skip pixels per scroll
    jTable.setParent(this);
    this.jPanelMain.add(jScrollPane, BorderLayout.CENTER);

    // buttons
    jButtonAddButton.setText(adapter.getString("AlteraAtributosJSP.button.add.button")); //$NON-NLS-1$
    jButtonAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jpButtons.addFormButton();
      }
    });

    jButtonAddField.setText(adapter.getString("AlteraAtributosJSP.button.add.field")); //$NON-NLS-1$
    jButtonAddField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTable.addRow();
      }
    });

    jButtonClose.setText(adapter.getString("AlteraAtributosJSP.button.close")); //$NON-NLS-1$
    jButtonClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitStatus = EXIT_STATUS_OK;
        dispose();
      }
    });

    jButtonCancel.setText(adapter.getString("AlteraAtributosJSP.button.cancel")); //$NON-NLS-1$
    jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitStatus = EXIT_STATUS_CANCEL;
        dispose();
      }
    });

    aux = new JPanel();
    aux.add(jButtonAddButton, null);
    aux.add(jButtonAddField, null);
    aux.add(jButtonClose, null);
    aux.add(jButtonCancel, null);

    // button preview
    jpButtons = new JPreviewFormButtons(this, atributos);
    jScrollPaneButtons = new JScrollPane(jpButtons);

    JPanel aux2 = new JPanel();
    aux2.setLayout(new BorderLayout());
    JPanel aux3=new JPanel();
    aux3.setSize(nSPACER_SIZE,1);
    aux2.add(aux3, BorderLayout.WEST);
    aux3=new JPanel();
    aux3.setSize(nSPACER_SIZE,1);
    aux2.add(aux3, BorderLayout.EAST);
    aux3=new JPanel();
    aux3.setSize(1,nSPACER_SIZE);
    aux2.add(aux3, BorderLayout.SOUTH);

    //  aux3 = new JPanel();
    //  aux3.setBackground(AlteraAtributosJSP.cBG_COLOR);
    //  aux3.setBorder(BorderFactory.createLoweredBevelBorder());
    //  aux3.setLayout(new BorderLayout());
    //  aux3.add(new JLabel("Botões"),BorderLayout.NORTH);
    //  aux3.add(jpButtons,BorderLayout.CENTER);

    aux2.add(jScrollPaneButtons, BorderLayout.CENTER);

    JPanel jp = new JPanel();
    jp.setLayout(new BorderLayout());
    jp.add(aux2, BorderLayout.NORTH);
    jp.add(aux, BorderLayout.SOUTH);

    aux = null;
    aux2 = null;
    aux3 = null;

    this.jPanelMain.add(jp, BorderLayout.SOUTH);

    this.getContentPane().add(this.jPanelMain);

  }

  public JSPFieldData cloneField(JSPFieldData fd) {
    if(null == fd) return null;

    JSPFieldData field = null;

    String sClassName = null;
    Class<?> cClass = null;
    Object[] args = new Object[0];
    Constructor<?> cConstructor = null;

    args = new Object[1];
    args[0] = fd;

    sClassName = fd.getFieldTypeClassName();

    if (sClassName == null) {
      getAdapter().log("AlteraAtributosJSP: cloneField: unable to get class name for field type " + fd.getFieldType()); //$NON-NLS-1$
      return null;
    }

    try {
      FlowEditorAdapter adapter = getAdapter();

      cClass = adapter.getRepository().loadClass(sClassName);
      cConstructor = cClass.getConstructor(JSPFieldData.class);
      field = (JSPFieldData)cConstructor.newInstance(args);
    }
    catch (Exception e) {
      getAdapter().log("AlteraAtributosJSP: cloneField: unable to load class " + sClassName, e); //$NON-NLS-1$
      return null;
    }
    
    return field;
  }



  public void revalidate() {
    this.repaint();
    this.jPanelMain.revalidate();
  }

  /************************
   *
   * JTABLE JSP
   *
   ***********************/

  private class JTableJSP extends JPanel {
    private static final long serialVersionUID = 1L;

    String[] _saColNames;

    AlteraAtributosJSP _parent = null;

    // ordered list with JSPFieldData objects
    private ArrayList<JSPFieldData> _alRows = new ArrayList<JSPFieldData>();

    private JPanel _fullPanel = null;
    private JPanel _mainPanel = null;
    private GridBagLayout _gridbag;
    private GridBagConstraints _c;

    private JTextField _jtfDescription = null;
    private JTextField _jtfResult = null;
    private JComboBox _jcbStyleSheet = null;
    private JComboBox _jcbPrintStyleSheet = null;
    private JCheckBox _jcbForwardOnSubmit = null;
    private JTextField _jtfReadOnlyExp = null;

	private JTextField _jtfAppendDocs = null;

    public JTableJSP(String[] asaColNames, List<Atributo> alAttributes) {
      super(new BorderLayout());

      this._jtfDescription = new JTextField(30);
      this._jtfResult = new JTextField(30);
      this._jtfReadOnlyExp = new JTextField(30);
      this._jtfAppendDocs = new JTextField(30);
      
      RepositoryClient rep = getAdapter().getRepository();
      String[] saStyleSheets = null;
      String[] saTemplates = null;
      if (rep != null) {
        saStyleSheets = rep.listStyleSheets();
        saTemplates = rep.listPrintTemplates();
      }
      if(null == saTemplates) saTemplates = new String[0];
      if(null == saStyleSheets) saStyleSheets = new String[0];


      String [] saXSL = new String [saStyleSheets.length+1];
      int npos = 0;
      saXSL[npos++] = "";
      for (String xsl : saStyleSheets) {
        saXSL[npos++] = xsl;
      }

      String [] saPrint = new String[saStyleSheets.length + saTemplates.length + 1];
      npos = 0;
      saPrint[npos++] = sNO_PRINT;
      for (String xsl : saStyleSheets) {
        saPrint[npos++] = xsl;
      }
      for (String fo : saTemplates) {
        saPrint[npos++] = fo;
      }

      // Read property and setup checkBox
      this._jcbStyleSheet = new JComboBox(saXSL);
      this._jcbStyleSheet.setEnabled(saXSL.length>1);

      this._jcbPrintStyleSheet = new JComboBox(saPrint);
      this._jcbPrintStyleSheet.setEnabled(saPrint.length>1);
      this._jcbPrintStyleSheet.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String value = (String) _jcbPrintStyleSheet.getSelectedItem();
            _jtfAppendDocs.setEditable(value != null && value.toLowerCase().endsWith(sFO_EXT));
          }
        });
      
      this._jcbForwardOnSubmit = new JCheckBox();
      

      this._saColNames = asaColNames;

      this.setBackground(AlteraAtributosJSP.cBG_COLOR);

      this.importData(alAttributes);

      this.makeTable();
    }


    public String[][] exportData() {

      String[][] ret = new String[0][0];

      ArrayList<String> alNames = new ArrayList<String>();
      ArrayList<String> alValues = new ArrayList<String>();

      JSPFieldData fd = null;
      Map<Integer,ArrayList<String>> alArray = null;
      for (int i=0; i < this.getRowCount(); i++) {
        fd = this.getRow(i);
        alArray = fd.exportData();
        alNames.addAll(alArray.get(JSPFieldData.nNAMES_INDEX));
        alValues.addAll(alArray.get(JSPFieldData.nVALUES_INDEX));
      }

      String sDescription = ""; //$NON-NLS-1$
      if (this._jtfDescription != null) {
        sDescription = this._jtfDescription.getText();
        if (sDescription == null || sDescription.equals("")) { //$NON-NLS-1$
          sDescription = ""; //$NON-NLS-1$
        }
        else {
          sDescription = sDescription.trim();
        }
      }

      String sResult = ""; //$NON-NLS-1$
      if (this._jtfResult != null) {
        sResult = this._jtfResult.getText();
        if (sResult == null || sResult.equals("")) { //$NON-NLS-1$
          sResult = ""; //$NON-NLS-1$
        }
        else {
          sResult = sResult.trim();
        }
      }

      String sStyleSheet = ""; //$NON-NLS-1$
      if (this._jcbStyleSheet != null) {
        sStyleSheet = (String)this._jcbStyleSheet.getSelectedItem();
        if (sStyleSheet == null || sStyleSheet.equals("")) { //$NON-NLS-1$
          sStyleSheet = ""; //$NON-NLS-1$
        }
        else {
          sStyleSheet = sStyleSheet.trim();
        }
      }

      String sPrintStyleSheet = ""; //$NON-NLS-1$
      if (this._jcbPrintStyleSheet != null) {
        sPrintStyleSheet = (String)this._jcbPrintStyleSheet.getSelectedItem();
        if (sPrintStyleSheet == null || sPrintStyleSheet.equals("") || //$NON-NLS-1$
            sPrintStyleSheet.equals(sNO_PRINT)) {
          sPrintStyleSheet = ""; //$NON-NLS-1$
        }
        else {
          sPrintStyleSheet = sPrintStyleSheet.trim();
        }
      }

      String forwardOnSubmit = _jcbForwardOnSubmit.isSelected()?"true":"false";// or something else....
      
      String appendDocsVar = "";
      if (this._jtfAppendDocs != null) {
        appendDocsVar = this._jtfAppendDocs.getText();
        if (appendDocsVar == null || appendDocsVar.equals("")) { //$NON-NLS-1$
          appendDocsVar = ""; //$NON-NLS-1$
        }
        else {
          appendDocsVar = appendDocsVar.trim();
        }
      }      
      
      String sReadOnly = "";
      if (this._jtfReadOnlyExp != null) {
        sReadOnly = this._jtfReadOnlyExp.getText();
        if (StringUtils.isBlank(sReadOnly)) {
          sReadOnly = "";
        } else {
          sReadOnly = sReadOnly.trim();
        }
      }
      
      ret = new String[alNames.size() + 7][2];

      // put description, result and stylesheet prop at list end
      ret[alNames.size()][0] = AlteraAtributos.sDESCRIPTION;
      ret[alNames.size()][1] = sDescription;
      ret[alNames.size() + 1][0] = AlteraAtributos.sRESULT;
      ret[alNames.size() + 1][1] = sResult;
      ret[alNames.size() + 2][0] = JSPFieldData.getPropCodeName(JSPFieldData.nPROP_STYLESHEET);
      ret[alNames.size() + 2][1] = sStyleSheet;
      ret[alNames.size() + 3][0] = FormProps.PRINT_STYLESHEET;
      ret[alNames.size() + 3][1] = sPrintStyleSheet;
      ret[alNames.size() + 4][0] = FormProps.FORWARD_ON_SUBMIT;
      ret[alNames.size() + 4][1] = forwardOnSubmit;
      ret[alNames.size() + 5][0] = FormProps.READ_ONLY;
      ret[alNames.size() + 5][1] = sReadOnly;
      ret[alNames.size() + 6][0] = AlteraAtributosJSP.sAPPEND_DOCS_VAR;
      ret[alNames.size() + 6][1] = appendDocsVar;
      
      for (int i=0; i < alNames.size(); i++) {
        ret[i][0] = (String)alNames.get(i);
        ret[i][1] = (String)alValues.get(i);
      }

      return ret;
    }


    public void importData(List<Atributo> lAttributes) {

      // key: integer with obj position
      // value: ArrayList[2] with name arraylist and values arraylist
      HashMap<Integer,List<String>[]> hmList = new HashMap<Integer,List<String>[]>();

      List<String>[] alList = null;
      List<String> alNames = null;
      List<String> alValues = null;
      String sName = null;
      String sVal = null;
      String stmp = null;
      Integer itmp = null;
      int idx = -1;

      for (int i=0;i<lAttributes.size(); i++) {
        sName = lAttributes.get(i).getDescricao();
        sVal = lAttributes.get(i).getValor();

        if (StringUtils.isEmpty(sName))
          continue;
        
        if (sName.equals(AlteraAtributos.sDESCRIPTION)) {
          this._jtfDescription.setText(sVal);
          continue;
        } else if (sName.equals(AlteraAtributos.sRESULT)) {
          this._jtfResult.setText(sVal);
          continue;
        } else if (sName.equals(JSPFieldData.getPropCodeName(JSPFieldData.nPROP_STYLESHEET))) {
          this._jcbStyleSheet.setSelectedItem(sVal);
          continue;
        } else if (sName.equals(FormProps.PRINT_STYLESHEET)) {
          this._jcbPrintStyleSheet.setSelectedItem(sVal);
          continue;
        } else if(sName.equals(FormProps.FORWARD_ON_SUBMIT)) {
          this._jcbForwardOnSubmit.setSelected("true".equals(sVal));
          continue;
        } else if(sName.equals(FormProps.READ_ONLY)) {
          this._jtfReadOnlyExp.setText(sVal);
          continue;
        } else if (sName.startsWith(FormProps.sBUTTON_ATTR_PREFIX)) {
          continue;
        } else if (sName.equals(AlteraAtributosJSP.sAPPEND_DOCS_VAR)) {
              this._jtfAppendDocs.setText(sVal);
              continue;
        }

        stmp = JSPFieldData.sNAME_PREFIX + JSPFieldData.sJUNCTION;
        idx = sName.indexOf(stmp);

        if (idx == -1) {
          getAdapter().log("AlteraAtributosJSP: importData: " + "undefined object"); //$NON-NLS-1$ $NON-NLS-2$
          continue;
        }

        idx += stmp.length();
        stmp = sName.substring(idx, sName.indexOf(JSPFieldData.sJUNCTION, idx));
        itmp = new Integer(stmp);

        sName = sName.substring(sName.indexOf(JSPFieldData.sJUNCTION, idx) + JSPFieldData.sJUNCTION.length());

        alList = hmList.get(itmp);
        if (alList == null) {
          alList = newArray(2);
          alNames = new ArrayList<String>();
          alValues = new ArrayList<String>();
        }
        else {
          alNames = alList[0];
          alValues = alList[1];
        }

        alNames.add(sName);
        alValues.add(sVal);

        alList[0] = alNames;
        alList[1] = alValues;

        hmList.put(itmp, alList);
      }

      // build fields

      this._alRows = new ArrayList<JSPFieldData>();

      JSPFieldData fd1 = null;
      JSPFieldData fd2 = null;

      for (int row=0; row < hmList.size(); row++) {
        itmp = new Integer(row);
        alList = (ArrayList<String>[])hmList.get(itmp);

        alNames = alList[0];
        alValues = alList[1];
        
        fd1 = new JSPFieldData(getAdapter(), row, alNames, alValues, this.getTableProps());

        fd2 = cloneField(fd1);
        if (fd2 == null) {
          getAdapter().log("AlteraAtributosJSP: importData: unable to get class name for field type " + fd1.getFieldType()); //$NON-NLS-1$ $NON-NLS-2$
          continue;
        }
        
        this.appendTableRow(fd2);
      }

    }



    public ArrayList<Integer> getTableProps() {
      return alTABLE_PROPS;
    }

    // intended to be used at start to create table from imported rows
    private void appendTableRow(JSPFieldData afdData) {
      this._alRows.add(afdData.getPosition(),afdData);
    }


    // updates or adds a row in the table
    private void setTableRow(JSPFieldData afdData) {
      // add row at right position
      // if position not defined or data is new, add it at table's end
      // else, need to remove the element at previous position to insert it
      // afterwards at that position
      int nIDAtPos = (this.getRow(afdData.getPosition())).getID();
      boolean bNew = afdData.isNew();
      int nPos = afdData.getPosition();
      int nMoveToPos = nPos;

      if (!bNew && nIDAtPos == afdData.getID()) {
        // same field data: remove old one
        this._alRows.remove(afdData.getPosition());
      }


      if (bNew) {
        if (afdData.getPosition() >= 0) {
          // new row with position defined
          afdData.setID(this.getRowCount());
        }
        else {
          // new row with position undefined...
          int pos = this.getRowCount();
          afdData.setPosition(pos);
          afdData.setID(pos);
        }
        // set add position last row
        nPos = this.getRowCount();
        // set desired position to field's position
        nMoveToPos = afdData.getPosition();
        afdData.setPosition(nPos);
      }

      // add field data at nPos
      this._alRows.add(nPos, afdData);

      if (nPos != nMoveToPos) {
        // change field's positions
        this.changeRowPositions(nPos,nMoveToPos);
      }

      // remake table // TODO: improve to avoid all table regeneration
      this.makeTable();
    }


    private void makeTable() {

      // DESCRIPTION
      GridBagLayout sGridbag = new GridBagLayout();
      GridBagConstraints sC = new GridBagConstraints();
      sC.fill = GridBagConstraints.HORIZONTAL;
      JPanel jPanel = new JPanel();
      jPanel.setLayout(sGridbag);
      jPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);
      JLabel jLabel = null;
      String stmp = getAdapter().getString("AlteraAtributosJSP.description"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfDescription);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      JPanel sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfDescription,sC);
      jPanel.add(this._jtfDescription);
      sC.gridwidth = 1;


      // RESULT
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP.result.description"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfResult);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfResult,sC);
      jPanel.add(this._jtfResult);
      sC.gridwidth = 1;


      // STYLESHEET
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP.stylesheet"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jcbStyleSheet);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jcbStyleSheet,sC);
      jPanel.add(this._jcbStyleSheet);
      sC.gridwidth = 1;


      // PRINT STYLESHEET
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP.print.stylesheet"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jcbPrintStyleSheet);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jcbPrintStyleSheet,sC);
      jPanel.add(this._jcbPrintStyleSheet);
      sC.gridwidth = 1;
      
      // Append document
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP.appendDoc.description"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfAppendDocs);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfAppendDocs,sC);
      jPanel.add(this._jtfAppendDocs);
      sC.gridwidth = 1;
      
      // FORWARD ON SUBMIT
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP." + FormProps.FORWARD_ON_SUBMIT); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jcbForwardOnSubmit);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jcbForwardOnSubmit,sC);
      jPanel.add(this._jcbForwardOnSubmit);
      _jcbForwardOnSubmit.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sC.gridwidth = 1;

      // READ ONLY
      jLabel = null;
      stmp = getAdapter().getString("AlteraAtributosJSP." + FormProps.READ_ONLY); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfReadOnlyExp);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfReadOnlyExp, sC);
      jPanel.add(this._jtfReadOnlyExp);
      sC.gridwidth = 1;

      // now table panel
      if (this._fullPanel != null) {
        this.remove(this._fullPanel);
      }

      this._mainPanel = new JPanel();
      this._mainPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);

      this._gridbag = new GridBagLayout();
      this._c = new GridBagConstraints();
      this._c.fill = GridBagConstraints.HORIZONTAL;

      this._mainPanel.setLayout(this._gridbag);


      // set font to plain
      Font fMainFont = this.getFont();


      Font fTitlefont = new Font(fMainFont.getName(),
          Font.BOLD,
          fMainFont.getSize()+2);


      stmp = null;

      // make columns for TITLE ROW
      this._c.weightx = 1.0;
      for (int col=0; col < this.getColumnCount(); col++) {
        makeLabel(this._saColNames[col],fTitlefont);
      }
      // now the last column (the control column) TODO!!!
      this._c.gridwidth = GridBagConstraints.REMAINDER;
      makeLabel(""); //$NON-NLS-1$


      // separator row
      makeSeparator(1,nSPACER_SIZE);


      // stuff
      for (int row=0; row < this.getRowCount(); row++) {
        this.makeTableRow(row);
      }

      // now generate panel
      JPanel jtmp = new JPanel();
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      jtmp.setLayout(gridbag);

      // DESCRIPTION, RESULT, STYLESHEET AND PRINT_STYLESHEET
      c.gridwidth = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(jPanel,c);
      jtmp.add(jPanel);

      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
      sizer.setSize(1,10);
      gridbag.setConstraints(sizer,c);
      jtmp.add(sizer);

      gridbag.setConstraints(this._mainPanel,c);
      jtmp.add(this._mainPanel);


      this._fullPanel = new JPanel();
      this._fullPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);
      this._fullPanel.add(jtmp, BorderLayout.NORTH);

      this.add(this._fullPanel, BorderLayout.NORTH);      

      this.revalidateParent();
    }


    // I dont quite understand this code, but it is necessary and will not work if removed.
    private void makeTableRow (int anPosition) {
      String stmp;

      JSPFieldData fd = this.getRow(anPosition);
      String[] values = fd.exportToTableRow();
      JPanel aux = null;

      this._c.gridwidth = 1;
      for (int col=0; col < this.getColumnCount(); col++) {
        stmp = values[col];

        if (stmp == null) {
          // no property... make a label
          this.makeLabel(""); //$NON-NLS-1$
        }
        else {
          // property ok..

          if (hsEDITABLE_TABLE_COL.contains(col)) {
            // make a text field
            //          jTextField = this.makeTextField(stmp,
            //          AlteraAtributosJSP.sROW_EDIT_PREFIX
            //          + anPosition);
          }
          else {
            // make a button
            this.makeButton(formatLabel(stmp),
                AlteraAtributosJSP.sROW_EDIT_PREFIX
                + anPosition);
          }
        }
      } // for

      // add last column (the control column) TODO !!!!
      this._c.gridwidth = GridBagConstraints.REMAINDER;
      aux = this.makeControlPanel(anPosition);
      this._gridbag.setConstraints(aux, this._c);
      this._mainPanel.add(aux);
      // now prepare for new row
      this._c.gridwidth = 1;

    } // makeTableRow


    private void makeSeparator(int anWidth,
        int anHeight) {
      JPanel panel = new JPanel();
      panel.setSize(anWidth,anHeight);
      panel.setBackground(AlteraAtributosJSP.cBG_COLOR);
      this._gridbag.setConstraints(panel, this._c);
      this._mainPanel.add(panel);
    } // makeSeparator


    private JLabel makeLabel(String asName) {
      return this.makeLabel(asName,null);
    }

    private JLabel makeLabel(String asName,
        Font afFont) {
      JLabel label = new JLabel (asName,JLabel.CENTER);
      label.setBackground(AlteraAtributosJSP.cBG_COLOR);
      if (afFont != null) {
        label.setFont(afFont);
      }
      this._gridbag.setConstraints(label, this._c);
      this._mainPanel.add(label);
      return label;
    } // makeLabel

    private JButton makeButton(String asName,
        String asActionCommand) {
      JButton button = new JButton(asName);
      button.setBackground(AlteraAtributosJSP.cBG_COLOR);
      button.setBorderPainted(false);
      button.setActionCommand(asActionCommand);
      button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String sActionCom = e.getActionCommand();
          String sRowPos =
            sActionCom
            .substring(AlteraAtributosJSP.sROW_EDIT_PREFIX.length());
          editRow(Integer.parseInt(sRowPos),false);
        }
      });

      this._gridbag.setConstraints(button, this._c);
      this._mainPanel.add(button);
      return button;
    } // makeButton


    private JPanel makeControlPanel(int anPosition) {
      JPanel ret = new JPanel();

      Dimension dimButton = new Dimension(22,20);
      int nNumButtons = 5;
      int nPanelWidth = (int)(dimButton.getWidth())*nNumButtons
      + (nNumButtons - 1);
      int nPanelHeight = (int)dimButton.getHeight() + 2;

      ret.setBackground(AlteraAtributosJSP.cBG_COLOR);
      ret.setSize(nPanelWidth,nPanelHeight);


      JButton jbUp =
        this.makeControlButton("up.gif", //$NON-NLS-1$
            getAdapter().getString("AlteraAtributosJSP.button.move_line_up"), //$NON-NLS-1$
            AlteraAtributosJSP.sROW_MOVE_UP,
            anPosition,
            dimButton);
      if (anPosition == 0) {
        jbUp.setEnabled(false);
      }


      JButton jbDown =
        this.makeControlButton("down.gif", //$NON-NLS-1$
            getAdapter().getString("AlteraAtributosJSP.button.move_line_down"), //$NON-NLS-1$
            AlteraAtributosJSP.sROW_MOVE_DOWN,
            anPosition,
            dimButton);
      if (anPosition == (this.getRowCount()-1)) {
        jbDown.setEnabled(false);
      }

      JButton jbRemove =
        this.makeControlButton("remove.gif", //$NON-NLS-1$
            getAdapter().getString("AlteraAtributosJSP.button.remove_line"), //$NON-NLS-1$
            AlteraAtributosJSP.sROW_REMOVE,
            anPosition,
            dimButton);


      JButton jbAdd =
        this.makeControlButton("add.gif", //$NON-NLS-1$
            getAdapter().getString("AlteraAtributosJSP.button.add_line"), //$NON-NLS-1$
            AlteraAtributosJSP.sROW_ADD_AT,
            anPosition,
            dimButton);

      JButton jbEdit =
        this.makeControlButton("edit.gif", //$NON-NLS-1$
            getAdapter().getString("AlteraAtributosJSP.button.edit_line"), //$NON-NLS-1$
            AlteraAtributosJSP.sROW_EDIT_PREFIX,
            anPosition,
            dimButton);

      ret.add(jbUp);
      ret.add(jbDown);
      ret.add(jbRemove);
      ret.add(jbAdd);
      ret.add(jbEdit);

      return ret;
    }


    private JButton makeControlButton(String asImageIconName,
        String asToolTipText,
        final String asActionCommandPrefix,
        int anPosition,
        Dimension adButtonDimension) {

      ImageIcon ic = new ImageIcon(getAdapter().getJanela().createImage(asImageIconName, false));
      JButton ret = new JButton(ic);
      ret.setMaximumSize(adButtonDimension);
      ret.setMinimumSize(adButtonDimension);
      ret.setPreferredSize(adButtonDimension);
      ret.setToolTipText(asToolTipText);
      ret.setActionCommand(asActionCommandPrefix + anPosition);
      ret.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String sActionCom = e.getActionCommand();
          String sRowPos = null;

          if (asActionCommandPrefix.equals(AlteraAtributosJSP.sROW_MOVE_UP)) {
            sRowPos = sActionCom.substring(AlteraAtributosJSP.sROW_MOVE_UP.length());
            upRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix.equals(AlteraAtributosJSP.sROW_MOVE_DOWN)) {
            sRowPos = sActionCom.substring(AlteraAtributosJSP.sROW_MOVE_DOWN.length());
            downRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix.equals(AlteraAtributosJSP.sROW_REMOVE)) {
            sRowPos = sActionCom.substring(AlteraAtributosJSP.sROW_REMOVE.length());
            removeRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix.equals(AlteraAtributosJSP.sROW_ADD_AT)) {
            sRowPos = sActionCom.substring(AlteraAtributosJSP.sROW_ADD_AT.length());
            addRowAt(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix.equals(AlteraAtributosJSP.sROW_EDIT_PREFIX)) {
            sRowPos = sActionCom.substring(AlteraAtributosJSP.sROW_EDIT_PREFIX.length());
            editRow(Integer.parseInt(sRowPos),false);
          }
        }
      });

      return ret;
    }

    private void upRow(int anPosition) {
      this.changeRowPositions(anPosition, (anPosition - 1));
      this.makeTable();
    }

    private void downRow(int anPosition) {
      this.changeRowPositions(anPosition, (anPosition + 1));
      this.makeTable();
    }

    private void removeRow(int anPosition) {
      // first change row to list's end
      this.changeRowPositions(anPosition,(this.getRowCount()-1));
      this._alRows.remove((this.getRowCount()-1));
      this.makeTable();
    }



    private void changeRowPositions(int anOldPosition,
        int anNewPosition) {

      if (anOldPosition == anNewPosition) {
        return;
      }

      JSPFieldData fd1 = null;
      JSPFieldData fd2 = null;

      if (anOldPosition < anNewPosition) {
        for (int i=anOldPosition; i < anNewPosition; i++) {
          fd1 = (JSPFieldData)this._alRows.remove(i);

          fd2 = this.getRow(i);
          fd2.setPosition(i);

          fd1.setPosition(i+1);

          this._alRows.add(i+1,fd1);
        }
      }
      else {

        for (int i=anOldPosition; i > anNewPosition; i--) {

          fd1 = (JSPFieldData)this._alRows.remove(i);

          fd2 = this.getRow(i-1);
          fd2.setPosition(i);

          fd1.setPosition(i-1);

          this._alRows.add(i-1,fd1);
        }
      }
    }

    private void revalidateParent() {
      if (this._parent != null) {
        this._parent.revalidate();
      }
    }

    public void editRow(int anPosition, boolean abNewRow) {
      JSPFieldData fd = null;
      if (abNewRow) {
        fd = new JSPFieldData(getAdapter());
        fd.setPosition(anPosition);
      }
      else {
        fd = this.getRow(anPosition);
      }
      new RowEditor(this, cloneField(fd));
    }


    public void addRowAt(int anPosition) {
      this.editRow(anPosition,true);
    }

    public void addRow() {
      this.addRowAt(-1);
    }


    public int getColumnCount() {
      return this._saColNames.length;
    }

    public int getRowCount() {
      return (this._alRows.size());
    }

    public JSPFieldData getRow(int anPosition) {
      JSPFieldData fd = null;
      if (anPosition >= 0) {
        // valid/existing row
        fd = (JSPFieldData)this._alRows.get(anPosition);
      }
      else {
        fd = new JSPFieldData(getAdapter());
      }

      return fd;
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }
    public Dimension getPreferredSize() {
      return this._fullPanel.getPreferredSize();
    }
    public Dimension getMaximumSize() {
      return this._fullPanel.getMaximumSize();
    }

    public void setParent (AlteraAtributosJSP aParent) {
      this._parent = aParent;
    }

  } // class JTableJSP



  /**
   * ROW EDITOR: class to edit/create a table's rows
   * @see javax.swing.JDialog
   */
  private class RowEditor extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTableJSP _parent;

    private JPanel jPanelMain;
    private JPanel jPanelEdit;

    private JComboBox jComboFields;

    private JButton jButtonOk;
    private JButton jButtonCancel;

    // KEY: String with field type text
    // VALUE: JSPFieldData with field data
    private HashMap<JSPFieldTypeEnum, JSPFieldData> _hmFields;

    private JSPFieldTypeEnum _sFieldSelected;

    private boolean bInitialized = false;

    public RowEditor(JTableJSP aparent, JSPFieldData afdData) {
      super((Frame)null,true);
      this._parent = aparent;
      this._hmFields = new HashMap<JSPFieldTypeEnum, JSPFieldData>();
      JSPFieldData fd = afdData;
      if (fd == null) {
        fd = new JSPFieldData(getAdapter());
      }
      this._sFieldSelected = fd.getFieldType();

      this._hmFields.put(this._sFieldSelected,fd);

      this.init();
      this.setSize(nPANEL_WIDTH,nPANEL_HEIGHT);
      this.setModal(true);
      this.bInitialized = true;
      this.setLocationRelativeTo(getParent());
      this.setVisible(true);
    }

    public void init() {

      JSPFieldData fdSel = this.getSelectedFieldData();
      JPanel sizer = null;
      JPanel aux = null;
      String sClassName = null;
      Class<?> cClass = null;


      if (fdSel.isNew()) {
        this.setTitle(getAdapter().getString("AlteraAtributosJSP.button.add")); //$NON-NLS-1$
      }
      else {
        this.setTitle(getAdapter().getString("AlteraAtributosJSP.button.edit")); //$NON-NLS-1$
      }

      this.jPanelMain = new JPanel(new BorderLayout());


      // BORDERS
      sizer = new JPanel();
      sizer.setSize(nSPACER_SIZE,1);
      this.jPanelMain.add(sizer, BorderLayout.WEST);
      sizer=new JPanel();
      sizer.setSize(nSPACER_SIZE,1);
      this.jPanelMain.add(sizer, BorderLayout.EAST);
      sizer=new JPanel();
      sizer.setSize(1,nSPACER_SIZE);
      this.jPanelMain.add(sizer, BorderLayout.NORTH);

      // BUTTONS
      this.jButtonOk = new JButton();
      this.jButtonOk.setText(getAdapter().getString("Common.ok")); //$NON-NLS-1$
      this.jButtonOk.setEnabled(false);  // only enable after field type select
      this.jButtonOk.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          saveData();
        }
      });

      this.jButtonCancel = new JButton();
      this.jButtonCancel.setText(getAdapter().getString("Common.cancel")); //$NON-NLS-1$
      this.jButtonCancel.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          dispose();
        }
      });

      aux = new JPanel();
      aux.add(this.jButtonOk);
      aux.add(this.jButtonCancel);
      this.jPanelMain.add(aux,BorderLayout.SOUTH);



      // STUFF
      this.jPanelEdit = new JPanel();
      this.jPanelEdit.setLayout(new CardLayout());
      JSPFieldTypeEnum [] types = JSPFieldData.getFieldTypesIDs();
      for (int i=0; i < types.length; i++) {
        JSPFieldData fd = null;

        JSPFieldTypeEnum iFieldType = types[i];

        if (iFieldType.equals(fdSel.getFieldType())) {
          fd = fdSel;
        } else {
          sClassName = JSPFieldData.getFieldTypeClassName(iFieldType);

          if (sClassName == null) {
            getAdapter().log("AlteraAtributosJSP: RowEditor: " //$NON-NLS-1$
                + "unable to get class name for field type " //$NON-NLS-1$
                + iFieldType.toString());
            continue;
          }

          try {
            FlowEditorAdapter adapter = getAdapter();
            cClass = adapter.getRepository().loadClass(sClassName);
            fd = (JSPFieldData)cClass.getConstructor(FlowEditorAdapter.class).newInstance(adapter);
            if (fd == null) throw new Exception();
            if (!fdSel.isNew()) {
              // migrate/clone/copy fdSel basic stuff to fd
              fd.init(fdSel);
            }
            else {
              // don't forget to set necessary table props
              fd.setTableProps(this._parent.getTableProps());
              // copy position
              fd.setPosition(fdSel.getPosition());
            }
          }
          catch (Exception e) {
            getAdapter().log("AlteraAtributosJSP: RowEditor: unable to load class " //$NON-NLS-1$
                + sClassName, e);
            continue;
          }

        }

        fd.setParent(this);

        this._hmFields.put(fd.getFieldType(), fd);

        aux = fd.makeEditPanel();

        aux.setToolTipText(JSPFieldData.getFieldTypeTooltip(iFieldType));

        this.jPanelEdit.add(aux, fd.getEngineClass(), fd.getFieldType().getCode());

      } // for

      this.jComboFields = new JComboBox(JSPFieldTypeEnum.values());
      this.jComboFields.setRenderer(new JSPFieldComboBoxRenderer());
      this.jComboFields.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent evt) {
          if (evt.getStateChange() == ItemEvent.SELECTED) {
            processFieldTypeChange((JSPFieldTypeEnum)evt.getItem());
          }
        }
      });
      this.jComboFields.setSelectedItem(this._sFieldSelected);

      aux = new JPanel(new BorderLayout());
      aux.add(this.jComboFields,BorderLayout.NORTH);
      aux.add(this.jPanelEdit,BorderLayout.CENTER);

      this.jPanelMain.add(aux,BorderLayout.CENTER);

      // MAIN PANEL
      this.getContentPane().add(this.jPanelMain);

    }

    private JSPFieldData getSelectedFieldData() {
      return (JSPFieldData)this._hmFields.get(this._sFieldSelected);
    }

    private void processFieldTypeChange(JSPFieldTypeEnum asFieldType) {
      // remove NONE selection from combo when it becomes de-selected
      if (!JSPFieldTypeEnum.FIELD_TYPE_NONE.equals(asFieldType)) {
        JSPFieldTypeEnum noneFld = (JSPFieldTypeEnum)this.jComboFields.getItemAt(0);
        if (JSPFieldTypeEnum.FIELD_TYPE_NONE.equals(noneFld)) {
          this.jComboFields.removeItem(noneFld);
        }
        // enable data saving
        this.jButtonOk.setEnabled(true);
      }

      showEditPanel(this._sFieldSelected = asFieldType);
    }

    private void showEditPanel(JSPFieldTypeEnum asFieldType) {
      CardLayout cl = (CardLayout)(this.jPanelEdit.getLayout());
      cl.show(this.jPanelEdit, asFieldType.getEngineClass());
    }


    private void saveData() {
      JSPFieldData fd = this.getSelectedFieldData();
      try {
        fd.saveData();

        // if we get here, then fd is ok! save it and close editor window
        this._parent.setTableRow(fd);
        dispose();
      }
      catch (FieldDataException fde) {
        // show error message
        getAdapter().showError(fde.getMessage());
      }
    }


    public void validate() {

      if (this.bInitialized) {
        JSPFieldData fd = this.getSelectedFieldData();
        this.jPanelEdit.remove(fd.getFieldType().getCode());
        this.jPanelEdit.add(fd.makeEditPanel(),
            fd.getEngineClass(),
            fd.getFieldType().getCode());

        CardLayout cl = (CardLayout)(this.jPanelEdit.getLayout());
        cl.show(this.jPanelEdit, fd.getEngineClass());
      }

      super.validate();
    }
  } // class RowEditor


  /*
   * 
   * Eu sou o BOB Construtor!
   * 
   */


  private class JPreviewFormButtons extends JPanel {
    private static final long serialVersionUID = 1L;

    AlteraAtributosJSP _parent = null;

    // ordered list with FormButton objects
    private List<FormButton> _alButtons = new ArrayList<FormButton>();
    // button hashset 
    private HashSet<String> _hsButtons = new HashSet<String>();

    private JPanel _mainPanel = null;
    private GridBagLayout _gridbag;
    private GridBagConstraints _c;

    public JPreviewFormButtons(AlteraAtributosJSP aParent, List<Atributo> alAttributes) {
      super(new BorderLayout());

      this.setParent(aParent);

      this.setBackground(AlteraAtributosJSP.cBG_COLOR);

      this.importData(alAttributes);

      this.makePreview();
    }


    public String[][] exportData() {
      int nNUM_ATTR = 13;
      int pos;
      String[][] retObj = new String[this.getButtonCount()*nNUM_ATTR][2];

      FormButton fb = null;
      String stmp = null;
      String sPrefix = null;
      for (int i=0, j=0; i < this.getButtonCount(); i++,j+=nNUM_ATTR) {
        fb = this.getFormButton(i);

        sPrefix = FormProps.sBUTTON_ATTR_PREFIX + fb.getPosition() + "_"; //$NON-NLS-1$

        pos = 0;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_ID;
        retObj[j + pos][1] = String.valueOf(fb.getID());

        pos = 1;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_POSITION;
        retObj[j + pos][1] = String.valueOf(fb.getPosition());

        pos = 2;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_TYPE;
        retObj[j + pos][1] = fb.getType();

        stmp = fb.getText();
        if (stmp == null) {
          stmp = ""; //$NON-NLS-1$
        }
        else if (stmp.equals(sCHOOSE)) {
          stmp = (String)hmBUTTON_TYPES.get((String)retObj[j+2][1]);
        }

        pos = 3;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_TEXT;
        retObj[j + pos][1] = stmp;

        pos = 4;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_TOOLTIP;
        retObj[j + pos][1] = fb.getToolTip();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 5;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_IMAGE;
        retObj[j + pos][1] = fb.getImage();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 6;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_CUSTOM_VAR;
        retObj[j + pos][1] = fb.getCustomVar();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 7;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_CUSTOM_VALUE;
        retObj[j + pos][1] = fb.getCustomValue();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 8;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_SHOW_COND;
        retObj[j + pos][1] = fb.getShowCond();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 9;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_IGNORE_FORM_PROCESSING;
        retObj[j + pos][1] = fb.getIgnoreFormProcessing();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 10;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_CONFIRM_ACTION;
        retObj[j + pos][1] = fb.getConfirmAction();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 11;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_CONFIRM_ACTION_MESSAGE;
        retObj[j + pos][1] = fb.getConfirmActionMessage();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

        pos = 12;
        retObj[j + pos][0] = sPrefix + FormProps.sBUTTON_ATTR_IGNORE_FORM_VALIDATION;
        retObj[j + pos][1] = fb.getIgnoreFormValidation();
        if (retObj[j + pos][1] == null) retObj[j + pos][1] = ""; //$NON-NLS-1$

      }
      
      return retObj;
    }


    public void importData(List<Atributo> lAttributes) {

      FormButton fb = null;
      String sPos = null;
      String sName = null;
      String sVal = null;
      int ntmp = 0;
      Map<String,FormButton> hm = new TreeMap<String, FormButton>(); // order buttons by position
      HashSet<Integer> ids = new HashSet<Integer>();
      
      for (int i=0;i<lAttributes.size(); i++) {
        sName = lAttributes.get(i).getDescricao();
        sVal = lAttributes.get(i).getValor();
        
        if (sName == null || !sName.startsWith(FormProps.sBUTTON_ATTR_PREFIX)) {
          continue;
        }

        sPos = sName.substring(FormProps.sBUTTON_ATTR_PREFIX.length());
        ntmp = sPos.indexOf("_"); //$NON-NLS-1$
        sName = sPos.substring(ntmp+1);
        sPos = sPos.substring(0,ntmp);

        if (hm.containsKey(sPos)) {
          fb = hm.get(sPos);
        }
        else {
          fb = new FormButton(this);
        }

        if (sName.equals(FormProps.sBUTTON_ATTR_ID)) {
          try {
            int id = fb.setID(Integer.parseInt(sVal));
            updateButtonId(id);              
            if (ids.contains(id)) {
              // duplicated id...
              id = getNextButtonId(); 
              fb.setID(id);
            }
            else {
              ids.add(id);
            }
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_POSITION)) {
          try {
            fb.setPosition(Integer.parseInt(sVal));
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_TYPE)) {
          fb.setType(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_TEXT)) {
          fb.setText(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_TOOLTIP)) {
          fb.setToolTip(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_IMAGE)) {
          fb.setImage(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_CUSTOM_VAR)) {
          fb.setCustomVar(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_CUSTOM_VALUE)) {
          fb.setCustomValue(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_SHOW_COND)) {
          fb.setShowCond(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_IGNORE_FORM_PROCESSING)) {
          fb.setIgnoreFormProcessing(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_CONFIRM_ACTION)) {
          fb.setConfirmAction(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_CONFIRM_ACTION_MESSAGE)) {
          fb.setConfirmActionMessage(sVal);
        }
        else if (sName.equals(FormProps.sBUTTON_ATTR_IGNORE_FORM_VALIDATION)) {
          fb.setIgnoreFormValidation(sVal);
        }


        hm.put(sPos, fb);
      }
      
      if (hm.size() == 0) {
        for (int i=0; i < saDEF_BUTTONS.length; i++) {
          fb = new FormButton(this, saDEF_BUTTONS[i]);
          this.setFormButton(fb);	
        }
      }
      else {
        int pos = 0;
        for(String sId : hm.keySet()) {
          fb = hm.get(sId);
          fb.setPosition(pos);
          this.appendFormButton(fb);	
          pos++;
        }
      }
    }

    // intended to be used at start to create panel from imported buttons
    private void appendFormButton(FormButton afbButton) {
      this._alButtons.add(afbButton.getPosition(),afbButton);
      String sType = afbButton.getType();
      if (sType != null && !sType.equals(sCUSTOM_TYPE)) {
        this._hsButtons.add(sType);
      }
    }


    // updates or adds a form button
    private void setFormButton(FormButton afbButton) {
      // add form button at right position
      // if position not defined or data is new, add it at list's end
      // else, need to remove the element at previous position to insert it
      // afterwards at that position
      int nPos = afbButton.getPosition();
      int nIDAtPos = (this.getFormButton(nPos)).getID();
      boolean bNew = afbButton.isNew();
      int nMoveToPos = nPos;
      String sType = afbButton.getType(); 

      if (!bNew && nIDAtPos == afbButton.getID()) {
        // same field data: remove old one
        this._alButtons.remove(afbButton.getPosition());
        if (this._hsButtons.contains(sType)) {
          this._hsButtons.remove(sType);
        }
      }


      if (bNew) {
        afbButton.setID(getNextButtonId());
        if (nPos < 0) {
          // new button with position undefined...
          int pos = this.getButtonCount();
          afbButton.setPosition(pos);
        }
        // set add position last row
        nPos = this.getButtonCount();
        // set desired position to field's position
        nMoveToPos = afbButton.getPosition();
        afbButton.setPosition(nPos);
      }

      // add field data at nPos
      this._alButtons.add(nPos, afbButton);
      if (sType != null && !sType.equals(sCUSTOM_TYPE)) {
        this._hsButtons.add(sType);
      }

      if (nPos != nMoveToPos) {
        // change field's positions
        this.changeButtonPositions(nPos,nMoveToPos);
      }

      // remake panel // TODO: improve to avoid all table regeneration
      this.makePreview();
    }


    private void makePreview() {

      int nPad = 15;

      if (this._mainPanel != null) {
        this.remove(this._mainPanel);
      }

      this._mainPanel = new JPanel();
      this._mainPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);

      this._gridbag = new GridBagLayout();
      this._c = new GridBagConstraints();
      this._c.fill = GridBagConstraints.HORIZONTAL;
      this._c.weightx = 1.0;
      this._c.gridwidth = 1;
      this._c.ipady = nPad;

      this._mainPanel.setLayout(this._gridbag);
      this._mainPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);

      int nNumButtons = this.getButtonCount();
      int nPanelWidth = nPANEL_WIDTH - (2 * nSPACER_SIZE);
      int nPanelHeight = (int)(FORM_BUTTON_DIMENSION.getHeight() + 30);
      int nButtonWidth = (int)(FORM_BUTTON_DIMENSION.getWidth() + 2);
      int nTotalButtonWidth = nButtonWidth * nNumButtons;

      int nSpacerWidth = 15;
      int nTotalSpacerWidth = nPanelWidth - nTotalButtonWidth;
      if (nTotalSpacerWidth < 0) {
        nTotalSpacerWidth = 0;
        int nButtonSpacerWidth = nTotalSpacerWidth / nNumButtons;
        nSpacerWidth = (int)((nButtonSpacerWidth / 2)*0.8);
      }

      Dimension dim = new Dimension(nPanelWidth,nPanelHeight);
      this.setMaximumSize(dim);
      this.setMinimumSize(dim);
      this.setPreferredSize(dim);

      this._mainPanel.setMaximumSize(dim);
      this._mainPanel.setMinimumSize(dim);
      this._mainPanel.setPreferredSize(dim);

      FormButton fb = null;

      boolean bFirst = false;
      boolean bLast = false;
      int nLast = this.getButtonCount() - 1;
      JPanel sizer = null;

      // stuff
      for (int button=0; button < this.getButtonCount(); button++) {
        bFirst = false;
        bLast = false;

        if (button == 0) bFirst = true;
        if (button == nLast) bLast = true;

        fb = this.getFormButton(button);

        fb.make(bFirst, bLast);

        sizer = new JPanel();
        sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
        sizer.setSize(nSpacerWidth,nPanelHeight);
        this._gridbag.setConstraints(sizer,this._c);
        this._mainPanel.add(sizer);

        this._gridbag.setConstraints(fb,this._c);
        this._mainPanel.add(fb);

        sizer = new JPanel();
        sizer.setBackground(AlteraAtributosJSP.cBG_COLOR);
        sizer.setSize(nSpacerWidth,nPanelHeight);
        this._gridbag.setConstraints(sizer,this._c);
        this._mainPanel.add(sizer);
      }

      this.add(this._mainPanel, BorderLayout.CENTER);      

      this.revalidateParent();
    }


    private void revalidateParent() {
      if (this._parent != null) {
        this._parent.revalidate();
      }
    }

    private void leftFormButton(int anPosition) {
      this.changeButtonPositions(anPosition, (anPosition - 1));
      this.makePreview();
    }

    private void rightFormButton(int anPosition) {
      this.changeButtonPositions(anPosition, (anPosition + 1));
      this.makePreview();
    }

    private void removeFormButton(int anPosition) {
      FormButton fb = this.getFormButton(anPosition);
      if (this._hsButtons.contains(fb.getType())) {
        this._hsButtons.remove(fb.getType());
      }

      // first change button to list's end
      this.changeButtonPositions(anPosition,(this.getButtonCount()-1));
      this._alButtons.remove((this.getButtonCount()-1));
      this.makePreview();
    }



    private void changeButtonPositions(int anOldPosition,
        int anNewPosition) {

      if (anOldPosition == anNewPosition) {
        return;
      }

      FormButton fb1 = null;
      FormButton fb2 = null;

      if (anOldPosition < anNewPosition) {
        for (int i=anOldPosition; i < anNewPosition; i++) {
          fb1 = (FormButton)this._alButtons.remove(i);

          fb2 = this.getFormButton(i);
          fb2.setPosition(i);

          fb1.setPosition(i+1);

          this._alButtons.add(i+1,fb1);
        }
      }
      else {

        for (int i=anOldPosition; i > anNewPosition; i--) {

          fb1 = (FormButton)this._alButtons.remove(i);

          fb2 = this.getFormButton(i-1);
          fb2.setPosition(i);

          fb1.setPosition(i-1);

          this._alButtons.add(i-1,fb1);
        }
      }
    }

    public void editFormButton(int anPosition, boolean abNewRow) {
      FormButton fb = null;
      if (abNewRow) {
        fb = new FormButton(this);
        fb.setPosition(anPosition);
      }
      else {
        fb = this.getFormButton(anPosition);
      }
      new FormButtonEditor(this, new FormButton(fb));
    }


    public void addFormButtonAt(int anPosition) {
      this.editFormButton(anPosition,true);
    }

    public void addFormButton() {
      this.addFormButtonAt(-1);
    }

    public int getButtonCount() {
      return (this._alButtons.size());
    }

    public FormButton getFormButton(int anPosition) {
      FormButton fb = null;
      if (anPosition >= 0) {
        // valid/existing button
        fb = (FormButton)this._alButtons.get(anPosition);
      }
      else {
        fb = new FormButton(this);
      }

      return fb;
    }

    public String[] getAvailableTypes (String asType) {
      String[] retObj = new String[0];

      ArrayList<String> altmp = new ArrayList<String>();

      if (asType == null || asType.equals(sCHOOSE)) altmp.add(sCHOOSE);

      Iterator<String> iter = hmBUTTON_TYPES.keySet().iterator();

      String stmp = null;

      while (iter != null && iter.hasNext()) {
        stmp = iter.next();
        if (_hsButtons.contains(stmp)) {
          if (asType == null || !stmp.equals(asType)) {
            continue;
          }
        }
        altmp.add(hmBUTTON_TYPES.get(stmp));
      }

      retObj = new String[altmp.size()];
      for (int i=0; i < altmp.size(); i++) {
        retObj[i] = (String)altmp.get(i);
      }

      return retObj;
    }

    public String getType(String asTypeText) {
      if (asTypeText == null || asTypeText.equals(sCHOOSE)) return null;
      String retObj = (String)hmBUTTON_TYPES_REV.get(asTypeText);
      return retObj;
    }

    public String getTypeInfo(String asType) {
      if (asType == null || asType.equals("")) return getTypeInfo(sCHOOSE); //$NON-NLS-1$
      String retObj = (String)hmBUTTON_INFO.get(asType);
      return retObj;
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }
    public Dimension getPreferredSize() {
      return this._mainPanel.getPreferredSize();
    }
    public Dimension getMaximumSize() {
      return this._mainPanel.getMaximumSize();
    }

    public void setParent (AlteraAtributosJSP aParent) {
      this._parent = aParent;
    }

  } // class JPreviewFormButtons


  private class FormButton extends JPanel {
    private static final long serialVersionUID = 1L;

    private JPreviewFormButtons _parent = null;

    private JPanel _mainPanel = null;
    private JButton _jbMoveLeft = null;
    private JButton _jbAdd = null;
    private JButton _jbRemove = null;
    private JButton _jbMoveRight = null;
    private JButton _jbText = null;

    private String _sType = null;
    private boolean _bNew;
    private int _nID = -1;
    private int _nPosition = -1;
    private String _sText = null;
    private String _sToolTip = null;
    private String _sImage = null;
    private String _sCustomVar = null;
    private String _sCustomValue = null;
    private String _sShowCond = null;
    private String _ignoreFormProcessing = null;
    private String _confirmAction = null;
    private String _confirmActionMessage = null;
    private String _ignoreFormValidation = null;


    // default constructor
    public FormButton(JPreviewFormButtons aParent) {
      super(new BorderLayout());
      this.setBackground(AlteraAtributosJSP.cBG_COLOR);

      this._parent = aParent;
      this.init();
    }

    public FormButton(JPreviewFormButtons aParent, String asType) {
      this(aParent);
      this.setType(asType);
      this.init();
    }


    public FormButton(FormButton afb) {
      this(afb.getParentPanel(),afb.getType());

      this.setID(afb.getID());
      this.setPosition(afb.getPosition());
      this.setText(afb.getText());
      this.setToolTip(afb.getToolTip());
      this.setImage(afb.getImage());
      this.setCustomVar(afb.getCustomVar());
      this.setCustomValue(afb.getCustomValue());
      this.setShowCond(afb.getShowCond());
      this.setIgnoreFormProcessing(afb.getIgnoreFormProcessing());
      this.setConfirmAction(afb.getConfirmAction());
      this.setConfirmActionMessage(afb.getConfirmActionMessage());
      this.setIgnoreFormValidation(afb.getIgnoreFormValidation());

      this.init();
    }

    private void init() {
      if (this._nID >= 0) {
        this._bNew = false;
      }
      else {
        this._bNew = true;
      }      
    }

    public int setID(int anID) {
      if (anID >= 0) {
        this._nID = anID;
        this._bNew = false;
      }
      return this.getID();
    }

    public int getID() {
      return this._nID;
    }

    public int setPosition(int anPosition) {
      this._nPosition = anPosition;
      return this.getPosition();
    }

    public int getPosition() {
      return this._nPosition;
    }

    public boolean isNew() {
      return this._bNew;
    }

    public void setType(String asType) {
      this._sType = asType;

      if (this._sType != null) {
        if (this._sText == null || this._sText.equals("")) { //$NON-NLS-1$
          this.setText((String)hmBUTTON_TYPES.get(this._sType));
        }
      }
    }

    public String getType() {
      if (this._sType == null) return null;
      return new String(this._sType);
    }

    public void setText(String asText) {
      this._sText = asText;
    }

    public String getText() {
      if (this._sText == null) return null;
      return new String(this._sText);
    }

    public void setToolTip(String asToolTip) {
      this._sToolTip = asToolTip;
    }

    public String getToolTip() {
      if (this._sToolTip == null) return null;
      return new String(this._sToolTip);
    }

    public void setImage(String asImage) {
      this._sImage = asImage;
    }

    public String getImage() {
      if (this._sImage == null) return null;
      return new String(this._sImage);
    }

    public void setCustomVar(String asCustomVar) {
      this._sCustomVar = asCustomVar;
    }

    public String getCustomVar() {
      if (this._sCustomVar == null) return null;
      return new String(this._sCustomVar);
    }

    public void setCustomValue(String asCustomValue) {
      this._sCustomValue = asCustomValue;
    }

    public String getCustomValue() {
      if (this._sCustomValue == null) return null;
      return new String(this._sCustomValue);
    }

    public void setShowCond(String asShowCond) {
      this._sShowCond = asShowCond;
    }

    public String getShowCond() {
      if (this._sShowCond == null) return null;
      return new String(this._sShowCond);
    }

    public void setIgnoreFormProcessing(String asIgnoreFormProcessing) {
      this._ignoreFormProcessing = asIgnoreFormProcessing;
    }

    public String getIgnoreFormProcessing() {
      if (this._ignoreFormProcessing == null) return null;
      return new String(this._ignoreFormProcessing);
    }

    public String getConfirmAction() {
      if (this._confirmAction == null) return null;
      return new String(this._confirmAction);
    }

    public void setConfirmAction(String asConfirmAction) {
      this._confirmAction = asConfirmAction;
    }

    public String getConfirmActionMessage() {
      if (this._confirmActionMessage == null) return null;
      return new String(this._confirmActionMessage);
    }

    public void setConfirmActionMessage(String asConfirmActionMessage) {
      this._confirmActionMessage = asConfirmActionMessage;
    }

    public void setIgnoreFormValidation(String asIgnoreFormValidation) {
      this._ignoreFormValidation = asIgnoreFormValidation;
    }

    public String getIgnoreFormValidation() {
      if (this._ignoreFormValidation == null) return null;
      return new String(this._ignoreFormValidation);
    }

    public boolean isRequired() {
      if (this._sType == null || this._sType.equals("")) return false; //$NON-NLS-1$
      return hsREQ_BUTTONS.contains(this._sType);
    }

    public JPreviewFormButtons getParentPanel() {
      return this._parent;
    }

    public void make(boolean abFirst, boolean abLast) {
      if (this._mainPanel != null) {
        this.remove(this._mainPanel);
      }

      int nButX = 10;
      int nButY = 10;
      Dimension dSmall = new Dimension(nButX,nButY);
      Dimension dBig = new Dimension((2*nButX),(2*nButY));

      this._mainPanel = new JPanel();
      this._mainPanel.setBackground(AlteraAtributosJSP.cBG_COLOR);

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;

      this._mainPanel.setLayout(gridbag);

      // reset to default
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridx = GridBagConstraints.RELATIVE;
      c.gridy = GridBagConstraints.RELATIVE;
      c.gridwidth = 1;
      c.gridheight = 1;

      // LEFT BUTTON
      c.gridwidth = 1;
      c.gridheight = 2;
      c.weighty = 1.0;
      c.weightx = 0.5;
      this._jbMoveLeft = this.makeButton("left.gif", null, getAdapter().getString("AlteraAtributosJSP.button.move_button_left"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosJSP.sBUTTON_MOVE_LEFT, dBig);
      if (abFirst) {
        this._jbMoveLeft.setEnabled(false);
      }
      gridbag.setConstraints(this._jbMoveLeft, c);
      this._mainPanel.add(this._jbMoveLeft);

      // reset to default
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridx = GridBagConstraints.RELATIVE;
      c.gridy = GridBagConstraints.RELATIVE;
      c.gridwidth = 1;
      c.gridheight = 1;

      // ADD BUTTON
      c.gridwidth = GridBagConstraints.RELATIVE;
      c.weighty = 0.75;
      c.weightx = 1;
      this._jbAdd = this.makeButton("addButton.gif", null, getAdapter().getString("AlteraAtributosJSP.button.add_button"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosJSP.sBUTTON_ADD_AT, dSmall);
      gridbag.setConstraints(this._jbAdd, c);
      this._mainPanel.add(this._jbAdd);

      // reset to default
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridx = GridBagConstraints.RELATIVE;
      c.gridy = GridBagConstraints.RELATIVE;
      c.gridwidth = 1;
      c.gridheight = 1;

      // RIGHT BUTTON
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.gridheight = 2;
      c.weighty = 1.0;
      c.weightx = 0.5;
      this._jbMoveRight = this.makeButton("right.gif", null, getAdapter().getString("AlteraAtributosJSP.button.move_button_right"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosJSP.sBUTTON_MOVE_RIGHT, dBig);
      if (abLast) {
        this._jbMoveRight.setEnabled(false);
      }
      gridbag.setConstraints(this._jbMoveRight, c);
      this._mainPanel.add(this._jbMoveRight);

      // reset to default
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridx = GridBagConstraints.RELATIVE;
      c.gridy = GridBagConstraints.RELATIVE;
      c.gridwidth = 1;
      c.gridheight = 1;

      // REMOVE BUTTON
      c.gridx = 1;
      c.gridy = 1;
      c.weighty = 0.75;
      c.weightx = 1;
      c.gridwidth = GridBagConstraints.RELATIVE;
      this._jbRemove = this.makeButton("removeButton.gif", null, getAdapter().getString("AlteraAtributosJSP.button.remove_button"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosJSP.sBUTTON_REMOVE, dSmall);
      if (this.isRequired()) {
        this._jbRemove.setEnabled(false);
      }
      gridbag.setConstraints(this._jbRemove, c);
      this._mainPanel.add(this._jbRemove);

      // reset to default
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridx = GridBagConstraints.RELATIVE;
      c.gridy = GridBagConstraints.RELATIVE;
      c.gridwidth = 1;
      c.gridheight = 1;

      // TEXT BUTTON
      c.gridy = 2;
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.gridheight = GridBagConstraints.REMAINDER;

      this._jbText = this.makeButton(null, this.getText(), getAdapter().getString("AlteraAtributosJSP.button.edit_button"), //$NON-NLS-1$
          AlteraAtributosJSP.sBUTTON_EDIT_PREFIX,null);
      gridbag.setConstraints(this._jbText, c);
      this._mainPanel.add(this._jbText);


      this.add(this._mainPanel, BorderLayout.CENTER);      

      this.setMinimumSize(FORM_BUTTON_DIMENSION);

      this.revalidateParent();
    }

    private void revalidateParent() {
      if (this._parent != null) {
        this._parent.revalidateParent();
      }
    }


    private JButton makeButton(String asImageIconName,
        String asText,
        String asToolTipText,
        final String asActionCommandPrefix,
        Dimension adButtonDimension) {

      ImageIcon ic = null;
      JButton ret = null;
      if (asText != null && !asText.equals("")) { //$NON-NLS-1$
        ret = new JButton(asText);
      }
      else if (asImageIconName != null && !asImageIconName.equals("")) { //$NON-NLS-1$
        ic = new ImageIcon(getAdapter().getJanela().createImage(asImageIconName, false));
        ret = new JButton(ic);
      }
      else {
        ret = new JButton("<empty>"); //$NON-NLS-1$
      }

      if (ret == null) {
        // oops
        return null;
      }

      if (adButtonDimension != null) {
        ret.setMaximumSize(adButtonDimension);
        ret.setMinimumSize(adButtonDimension);
        ret.setPreferredSize(adButtonDimension);
      }
      ret.setToolTipText(asToolTipText);
      ret.setActionCommand(asActionCommandPrefix + this.getPosition());

      ret.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String sActionCom = e.getActionCommand();
          String sButtonPos = null;

          if (asActionCommandPrefix
              .equals(AlteraAtributosJSP.sBUTTON_MOVE_RIGHT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosJSP.sBUTTON_MOVE_RIGHT.length());
            _parent.rightFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosJSP.sBUTTON_MOVE_LEFT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosJSP.sBUTTON_MOVE_LEFT.length());
            _parent.leftFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosJSP.sBUTTON_REMOVE)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosJSP.sBUTTON_REMOVE.length());
            _parent.removeFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosJSP.sBUTTON_ADD_AT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosJSP.sBUTTON_ADD_AT.length());
            _parent.addFormButtonAt(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosJSP.sBUTTON_EDIT_PREFIX)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosJSP.sBUTTON_EDIT_PREFIX.length());
            _parent.editFormButton(Integer.parseInt(sButtonPos),false);
          }
        }
      });

      return ret;
    }

  } // class FormButton



  /**
   * FORM BUTTON EDITOR: class to edit/create a form button
   * @see javax.swing.JDialog
   */
  private class FormButtonEditor extends JDialog {
    private static final long serialVersionUID = 1L;

    private JPreviewFormButtons _parent;

    private JPanel jPanelMain;
    private JPanel jPanelEdit;

    private JComboBox jComboFields;
    private JTextArea jtaInfo;

    // KEY:type(String) VALUE:hashmap(key:fieldname(String) value:Field(JComponent)
    private HashMap<String,HashMap<String,JComponent>> hmComponents;

    private JButton jButtonOk;
    private JButton jButtonCancel;

    private FormButton _fb;

    public FormButtonEditor(JPreviewFormButtons aparent,
        FormButton afbButton) {
      super((Frame)null,true);
      this._parent = aparent;
      this._fb = afbButton;
      if (this._fb == null) {
        this._fb = new FormButton(this._parent);
      }
      this.hmComponents = new HashMap<String, HashMap<String,JComponent>>();

      this.init();
      this.setSize((int)(nPANEL_WIDTH/1.5),(int)(nPANEL_HEIGHT/1.4));
      this.setModal(true);
      this.setLocationRelativeTo(getParent());
      this.setVisible(true);
    }

    public void init() {

      JPanel sizer = null;
      JPanel aux = null;

      String sType = this._fb.getType();
      String sTypeText = null;
      if (sType != null) sTypeText = (String)hmBUTTON_TYPES.get(sType);

      if (this._fb.isNew()) {
        this.setTitle(getAdapter().getString("Common.add")); //$NON-NLS-1$
      }
      else {
        this.setTitle(getAdapter().getString("Common.edit")); //$NON-NLS-1$
      }

      this.jPanelMain = new JPanel(new BorderLayout());


      // BORDERS
      sizer = new JPanel();
      sizer.setSize(nSPACER_SIZE,1);
      this.jPanelMain.add(sizer, BorderLayout.WEST);
      sizer=new JPanel();
      sizer.setSize(nSPACER_SIZE,1);
      this.jPanelMain.add(sizer, BorderLayout.EAST);
      sizer=new JPanel();
      sizer.setSize(1,nSPACER_SIZE);
      this.jPanelMain.add(sizer, BorderLayout.NORTH);

      // BUTTONS
      this.jButtonOk = new JButton();
      this.jButtonOk.setText(getAdapter().getString("Common.ok")); //$NON-NLS-1$
      this.jButtonOk.setEnabled(false);  // only enable after field type select
      this.jButtonOk.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          saveData();
        }
      });

      this.jButtonCancel = new JButton();
      this.jButtonCancel.setText(getAdapter().getString("Common.cancel")); //$NON-NLS-1$
      this.jButtonCancel.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          dispose();
        }
      });

      aux = new JPanel();
      aux.add(this.jButtonOk);
      aux.add(this.jButtonCancel);
      this.jPanelMain.add(aux,BorderLayout.SOUTH);


      // STUFF
      String[] saTypesText = this._parent.getAvailableTypes(sType);


      JPanel jPanelEditMain = new JPanel();

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;

      jPanelEditMain.setLayout(gridbag);

      this.jtaInfo = new JTextArea(2,25);
      this.jtaInfo.setEnabled(false);
      this.jtaInfo.setLineWrap(true);
      this.jtaInfo.setWrapStyleWord(true);
      this.jtaInfo.setBorder(BorderFactory.createLoweredBevelBorder());
      c.gridwidth = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(this.jtaInfo,c);
      jPanelEditMain.add(this.jtaInfo);

      sizer = new JPanel();
      sizer.setSize(1,2*nSPACER_SIZE);
      gridbag.setConstraints(sizer,c);
      jPanelEditMain.add(sizer);
      c.gridwidth = 1;


      this.jPanelEdit = new JPanel();
      this.jPanelEdit.setLayout(new CardLayout());

      // now make each type panel      
      for (int i=0; i < saTypesText.length; i++) {
        if (!this._fb.isNew() &&
            sTypeText != null &&
            sTypeText.equals(saTypesText[i])) {
          aux = this.makeTypeCard(saTypesText[i],true);
        }
        else {
          aux = this.makeTypeCard(saTypesText[i],false);
        }
        this.jPanelEdit.add(aux, saTypesText[i], -1);
      }

      c.gridwidth = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(this.jPanelEdit,c);
      jPanelEditMain.add(this.jPanelEdit);


      // TYPE COMBO
      this.jComboFields = new JComboBox(saTypesText);
      this.jComboFields.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent evt) {
          if (evt.getStateChange() == ItemEvent.SELECTED) {
            processTypeChange((String)evt.getItem());
          }
        }
      });
      if (sTypeText != null) {
        this.jComboFields.setSelectedItem(sTypeText);
        processTypeChange(sTypeText);
      }
      else {
        this.jtaInfo.setText(this._parent.getTypeInfo(sCHOOSE));
      }


      JPanel aux2 = new JPanel(new BorderLayout());
      sizer = new JPanel();
      sizer.setSize(1,2*nSPACER_SIZE);
      aux2.add(sizer, BorderLayout.NORTH);
      aux2.add(jPanelEditMain,BorderLayout.CENTER);

      aux = new JPanel(new BorderLayout());
      aux.add(this.jComboFields,BorderLayout.NORTH);
      aux.add(aux2,BorderLayout.CENTER);

      aux2 = new JPanel(new BorderLayout());
      aux2.add(aux,BorderLayout.NORTH);

      this.jPanelMain.add(aux2,BorderLayout.CENTER);

//      this.jPanelMain
      
      // MAIN PANEL
      this.getContentPane().add(this.jPanelMain);

    }

    private void processTypeChange(String asTypeText) {
      String sNone = sCHOOSE;

      // remove NONE selection from combo when it becomes de-selected
      if (!asTypeText.equals(sNone)) {
        String stmp = (String)this.jComboFields.getItemAt(0);
        if (stmp.equals(sNone)) {
          this.jComboFields.removeItem(sNone);
        }
        // enable data saving
        this.jButtonOk.setEnabled(true);
      }

      changeType(asTypeText);
    }

    private void changeType(String asTypeText) {
      String sType = this._parent.getType(asTypeText);

      this._fb.setType(sType);

      // now change associated texts, ...
      String sInfo = this._parent.getTypeInfo(sType);
      if (sInfo == null) sInfo = sCHOOSE;
      this.jtaInfo.setText(sInfo);

      CardLayout cl = (CardLayout)(this.jPanelEdit.getLayout());
      cl.show(this.jPanelEdit, asTypeText);
    }


    private void saveData() {
      String sType = this._fb.getType();

      if (sType == null || sType.equals("") || sType.equals(sCHOOSE)) { //$NON-NLS-1$
        dispose();
        return;
      }

      String stmp = null;
      String stmp2 = null;

      // save text
      HashMap<String,JComponent> hmtmp = (HashMap<String,JComponent>)this.hmComponents.get(sType);
      JTextField jtf = (JTextField)hmtmp.get("jtfText");       //$NON-NLS-1$
      stmp = jtf.getText();
      this._fb.setText(stmp);

      // save tooltip
      if (hmtmp.containsKey("jtfToolTip")) { //$NON-NLS-1$
        jtf = (JTextField)hmtmp.get("jtfToolTip"); //$NON-NLS-1$
        stmp = jtf.getText();
        this._fb.setToolTip(stmp);
      }

      // save image
      if (hmtmp.containsKey("jtfImage")) { //$NON-NLS-1$
        jtf = (JTextField)hmtmp.get("jtfImage"); //$NON-NLS-1$
        stmp = jtf.getText();
        this._fb.setImage(stmp);
      }

      stmp = this._fb.getText();
      stmp2 = this._fb.getImage();
      if ((stmp == null || stmp.equals("")) && //$NON-NLS-1$
          (stmp2 == null || stmp2.equals(""))) { //$NON-NLS-1$
        getAdapter().showError(getAdapter().getString("AlteraAtributosJSP.error.missing_button_text"));//$NON-NLS-1$
        return;
      }


      if (sType.equals(sCUSTOM_TYPE)) {
        // save custom vars

        if (hmtmp.containsKey("jtfCustomVar")) { //$NON-NLS-1$
          jtf = (JTextField)hmtmp.get("jtfCustomVar"); //$NON-NLS-1$
          stmp = jtf.getText();
          if (stmp == null || stmp.equals("")) { //$NON-NLS-1$
            getAdapter().showError(getAdapter().getString("AlteraAtributosJSP.error.missing_button_var"));//$NON-NLS-1$
            return;
          }
          this._fb.setCustomVar(stmp);
        }

        if (hmtmp.containsKey("jtfCustomValue")) { //$NON-NLS-1$
          jtf = (JTextField)hmtmp.get("jtfCustomValue"); //$NON-NLS-1$
          stmp = jtf.getText();
          if (stmp == null || stmp.equals("")) { //$NON-NLS-1$
            getAdapter().showError(getAdapter().getString("AlteraAtributosJSP.error.missing_button_var_value")); //$NON-NLS-1$
            return;
          }
          this._fb.setCustomValue(stmp);
        }
      }

      if (hmtmp.containsKey("jtfShowCond")) { //$NON-NLS-1$
        jtf = (JTextField)hmtmp.get("jtfShowCond"); //$NON-NLS-1$
        stmp = jtf.getText();
        if (stmp == null) {
          stmp = ""; //$NON-NLS-1$
        }
        this._fb.setShowCond(stmp);
      }

      // ignore processing
      if (hmtmp.containsKey("jcbIgnorePro")) { //$NON-NLS-1$ 
        JCheckBox jcbiv = (JCheckBox)hmtmp.get("jcbIgnorePro"); //$NON-NLS-1$
        this._fb.setIgnoreFormProcessing(jcbiv.isSelected() ? "true" : "false");
      }

      // ignore validation
      if (hmtmp.containsKey("jcbIgnoreVal")) { //$NON-NLS-1$ 
        JCheckBox jcbiv = (JCheckBox)hmtmp.get("jcbIgnoreVal"); //$NON-NLS-1$
        this._fb.setIgnoreFormValidation(jcbiv.isSelected() ? "true" : "false");
      }

      // on click confirmation
      if (hmtmp.containsKey("jcbConfirm")) { //$NON-NLS-1$ 
        JCheckBox jcbiv = (JCheckBox)hmtmp.get("jcbConfirm"); //$NON-NLS-1$
        this._fb.setConfirmAction(jcbiv.isSelected() ? "true" : "false");
      }
      if (hmtmp.containsKey("jtfConfirmMsg")) { //$NON-NLS-1$
        jtf = (JTextField)hmtmp.get("jtfConfirmMsg"); //$NON-NLS-1$
        stmp = jtf.getText();
        if (stmp == null) {
          stmp = ""; //$NON-NLS-1$
        }
        this._fb.setConfirmActionMessage(stmp);
      }
      
      this._parent.setFormButton(this._fb);
      dispose();
    }

    private JPanel makeTypeCard(String asTypeText, boolean abSelected) {

      JPanel retObj = new JPanel();

      String sType = this._parent.getType(asTypeText);

      if (sType == null || sType.equals(sCHOOSE)) {
        // default panel (blank panel)
      }
      else {

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        retObj.setLayout(gbl);

        JTextField jtfText = new JTextField(20);
        jtfText.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.text.tooltip")); //$NON-NLS-1$
        JLabel jlTextLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.text") + asTypeText); //$NON-NLS-1$
        jlTextLabel.setHorizontalAlignment(JLabel.LEFT);
        jlTextLabel.setLabelFor(jtfText);
        gbl.setConstraints(jlTextLabel,gbc);
        retObj.add(jlTextLabel);

        JPanel sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        if (abSelected) {
          if (this._fb.getText() != null) {
            jtfText.setText(this._fb.getText());
          }
          else {
            jtfText.setText(""); //$NON-NLS-1$
          }
        }
        else {
          jtfText.setText(asTypeText);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jtfText,gbc);
        retObj.add(jtfText);

        gbc.gridwidth = 1;

        JTextField jtfToolTip = new JTextField(20);
        jtfToolTip.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.tooltip")); //$NON-NLS-1$
        JLabel jlToolTipLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.tooltip") + asTypeText); //$NON-NLS-1$
        jlToolTipLabel.setHorizontalAlignment(JLabel.LEFT);
        jlToolTipLabel.setLabelFor(jtfToolTip);
        gbl.setConstraints(jlToolTipLabel,gbc);
        retObj.add(jlToolTipLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        if (abSelected) {
          if (this._fb.getToolTip() != null) {
            jtfToolTip.setText(this._fb.getToolTip());
          }
          else {
            jtfToolTip.setText(""); //$NON-NLS-1$
          }
        }
        else {
          jtfToolTip.setText(asTypeText);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jtfToolTip,gbc);
        retObj.add(jtfToolTip);

        gbc.gridwidth = 1;


        JTextField jtfImage = new JTextField(20);
        jtfImage.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.image.tooltip")); //$NON-NLS-1$
        JLabel jlImageLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.image") + asTypeText); //$NON-NLS-1$
        jlImageLabel.setHorizontalAlignment(JLabel.LEFT);
        jlImageLabel.setLabelFor(jtfImage);
        gbl.setConstraints(jlImageLabel,gbc);
        retObj.add(jlImageLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        jtfImage.setText(""); //$NON-NLS-1$
        if (abSelected) {
          if (this._fb.getImage() != null) {
            jtfImage.setText(this._fb.getImage());
          }
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jtfImage,gbc);
        retObj.add(jtfImage);

        gbc.gridwidth = 1;

        // show cond
        JTextField jtfShowCond = null;
        JLabel jlShowCondLabel = null;
        jtfShowCond = new JTextField(20);
        jtfShowCond.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.visible.cond.tooltip")); //$NON-NLS-1$
        jlShowCondLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.visible.cond") + asTypeText); //$NON-NLS-1$
        jlShowCondLabel.setHorizontalAlignment(JLabel.LEFT);
        jlShowCondLabel.setLabelFor(jtfShowCond);
        gbl.setConstraints(jlShowCondLabel,gbc);
        retObj.add(jlShowCondLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        jtfShowCond.setText(""); //$NON-NLS-1$
        if (abSelected) {
          if (this._fb.getShowCond() != null) {
            jtfShowCond.setText(this._fb.getShowCond());
          }
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jtfShowCond,gbc);
        retObj.add(jtfShowCond);

        gbc.gridwidth = 1;

        JTextField jtfCustomVar = null;
        JLabel jlCustomVarLabel = null;
        JTextField jtfCustomValue = null;
        JLabel jlCustomValueLabel = null;

        if (sType.equals(sCUSTOM_TYPE)) {
          // custom props
          jtfCustomVar = new JTextField(20);
          jtfCustomVar.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.var.tooltip")); //$NON-NLS-1$
          jlCustomVarLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.var") + asTypeText); //$NON-NLS-1$
          jlCustomVarLabel.setHorizontalAlignment(JLabel.LEFT);
          jlCustomVarLabel.setLabelFor(jtfCustomVar);
          gbl.setConstraints(jlCustomVarLabel,gbc);
          retObj.add(jlCustomVarLabel);

          sizer = new JPanel();
          sizer.setSize(nSPACER_SIZE,1);
          gbl.setConstraints(sizer,gbc);
          retObj.add(sizer);

          jtfCustomVar.setText(""); //$NON-NLS-1$
          if (abSelected) {
            if (this._fb.getCustomVar() != null) {
              jtfCustomVar.setText(this._fb.getCustomVar());
            }
          }

          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbl.setConstraints(jtfCustomVar,gbc);
          retObj.add(jtfCustomVar);

          gbc.gridwidth = 1;


          jtfCustomValue = new JTextField(20);
          jtfCustomValue.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.var.value.tooltip")); //$NON-NLS-1$
          jlCustomValueLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.value") + asTypeText); //$NON-NLS-1$
          jlCustomValueLabel.setHorizontalAlignment(JLabel.LEFT);
          jlCustomValueLabel.setLabelFor(jtfCustomValue);
          gbl.setConstraints(jlCustomValueLabel,gbc);
          retObj.add(jlCustomValueLabel);

          sizer = new JPanel();
          sizer.setSize(nSPACER_SIZE,1);
          gbl.setConstraints(sizer,gbc);
          retObj.add(sizer);

          jtfCustomValue.setText(""); //$NON-NLS-1$
          if (abSelected) {
            if (this._fb.getCustomValue() != null) {
              jtfCustomValue.setText(this._fb.getCustomValue());
            }
          }

          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbl.setConstraints(jtfCustomValue,gbc);
          retObj.add(jtfCustomValue);

          gbc.gridwidth = 1;
        }

        gbc.gridwidth = 1;

        JCheckBox jcbIgnorePro = new JCheckBox();
        jcbIgnorePro.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.ignoreformprocessing")); //$NON-NLS-1$
        JLabel jlIgnoreProLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.ignoreformprocessing") + " " + asTypeText); //$NON-NLS-1$
        jlIgnoreProLabel.setHorizontalAlignment(JLabel.LEFT);
        jlIgnoreProLabel.setLabelFor(jcbIgnorePro);
        gbl.setConstraints(jlIgnoreProLabel,gbc);
        retObj.add(jlIgnoreProLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        if (abSelected) {
          jcbIgnorePro.setSelected("true".equals(this._fb.getIgnoreFormProcessing()));
        }
        else {
          jcbIgnorePro.setSelected(false);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jcbIgnorePro,gbc);
        retObj.add(jcbIgnorePro);

        gbc.gridwidth = 1;

        JCheckBox jcbIgnoreVal = new JCheckBox();
        jcbIgnoreVal.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.ignoreformvalidation")); //$NON-NLS-1$
        JLabel jlIgnoreValLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.ignoreformvalidation") + " " + asTypeText); //$NON-NLS-1$
        jlIgnoreValLabel.setHorizontalAlignment(JLabel.LEFT);
        jlIgnoreValLabel.setLabelFor(jcbIgnoreVal);
        gbl.setConstraints(jlIgnoreValLabel,gbc);
        retObj.add(jlIgnoreValLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        if (abSelected) {
          jcbIgnoreVal.setSelected("true".equals(this._fb.getIgnoreFormValidation()));
        }
        else {
          jcbIgnoreVal.setSelected(false);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jcbIgnoreVal,gbc);
        retObj.add(jcbIgnoreVal);

        gbc.gridwidth = 1;

        JCheckBox jcbConfirm = new JCheckBox();
        jcbConfirm.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.confirmAction"));
        JLabel jlConfirmLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.confirmAction") + " " + asTypeText); //$NON-NLS-1$
        jlConfirmLabel.setHorizontalAlignment(JLabel.LEFT);
        jlConfirmLabel.setLabelFor(jcbConfirm);
        gbl.setConstraints(jlConfirmLabel, gbc);
        retObj.add(jlConfirmLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer,gbc);
        retObj.add(sizer);

        if (abSelected) {
          jcbConfirm.setSelected("true".equals(this._fb.getConfirmAction()));
        } else {
          jcbConfirm.setSelected(false);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jcbConfirm, gbc);
        retObj.add(jcbConfirm);

        gbc.gridwidth = 1;
        
        JTextField jtfConfirmMsg = new JTextField(20);
        jtfConfirmMsg.setToolTipText(getAdapter().getString("AlteraAtributosJSP.button.confirmActionMessage")); //$NON-NLS-1$
        JLabel jlConfirmMsgLabel = new JLabel(getAdapter().getString("AlteraAtributosJSP.confirmActionMessage") + " " + asTypeText); //$NON-NLS-1$
        jlConfirmMsgLabel.setHorizontalAlignment(JLabel.LEFT);
        jlConfirmMsgLabel.setLabelFor(jtfConfirmMsg);
        gbl.setConstraints(jlConfirmMsgLabel,gbc);
        retObj.add(jlConfirmMsgLabel);

        sizer = new JPanel();
        sizer.setSize(nSPACER_SIZE,1);
        gbl.setConstraints(sizer, gbc);
        retObj.add(sizer);

        jtfConfirmMsg.setText(""); //$NON-NLS-1$
        if (abSelected) {
          if (this._fb.getConfirmActionMessage() != null) {
            jtfConfirmMsg.setText(this._fb.getConfirmActionMessage());
          }
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(jtfConfirmMsg,gbc);
        retObj.add(jtfConfirmMsg);

        gbc.gridwidth = 1;
        

        HashMap<String,JComponent> hmtmp = null;

        if (this.hmComponents.containsKey(sType)) {
          hmtmp = (HashMap<String,JComponent>)this.hmComponents.get(sType);
        }
        else {
          hmtmp = new HashMap<String,JComponent>();
        }

        hmtmp.put("jtfText", jtfText); //$NON-NLS-1$
        hmtmp.put("jlTextLabel", jlTextLabel); //$NON-NLS-1$
        hmtmp.put("jtfToolTip", jtfToolTip); //$NON-NLS-1$
        hmtmp.put("jlToolTipLabel", jlToolTipLabel); //$NON-NLS-1$
        hmtmp.put("jtfImage", jtfImage); //$NON-NLS-1$
        hmtmp.put("jlImageLabel", jlImageLabel); //$NON-NLS-1$
        hmtmp.put("jtfShowCond", jtfShowCond); //$NON-NLS-1$
        hmtmp.put("jlShowCondLabel", jlShowCondLabel);       //$NON-NLS-1$
        if (sType.equals(sCUSTOM_TYPE)) {
          hmtmp.put("jlCustomVarLabel", jlCustomVarLabel); //$NON-NLS-1$
          hmtmp.put("jtfCustomVar", jtfCustomVar); //$NON-NLS-1$
          hmtmp.put("jtfCustomValue", jtfCustomValue); //$NON-NLS-1$
          hmtmp.put("jlCustomValueLabel", jlCustomValueLabel);	   //$NON-NLS-1$
        }
        hmtmp.put("jcbIgnorePro", jcbIgnorePro); //$NON-NLS-1$
        hmtmp.put("jcbIgnoreVal", jcbIgnoreVal); //$NON-NLS-1$
        hmtmp.put("jcbConfirm", jcbConfirm); //$NON-NLS-1$
        hmtmp.put("jtfConfirmMsg", jtfConfirmMsg); //$NON-NLS-1$
        this.hmComponents.put(sType,hmtmp);
      }

      return retObj;
    }

  } // class FormButtonEditor

  
  private class JSPFieldComboBoxRenderer extends JLabel implements ListCellRenderer {
    private static final long serialVersionUID = -1820591430838239472L;

    public JSPFieldComboBoxRenderer() {
      setOpaque(true);
//      setHorizontalAlignment(LEFT);
//      setVerticalAlignment(CENTER);
    }

    /*
     * This method finds the image and text corresponding to the selected value and returns the label, set up to display the text
     * and image.
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JSPFieldTypeEnum selectedValue = (JSPFieldTypeEnum) value;

      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      String msg = getAdapter().getString(selectedValue.getDescrKey());
      
      setText(msg);

      return this;
    }

  }

  private static String formatLabel(String label) {
    if (label == null || label.length() <= LABEL_MAX_LENGTH)
      return label;
    
    return label.substring(0, LABEL_MAX_LENGTH) + "(...)";
  }
}

