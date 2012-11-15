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
import java.awt.event.ItemEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import pt.iflow.api.blocks.FormProps;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;

public class AlteraAtributosDetalheProcesso extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 475547789890009273L;

  private static final int nPANEL_WIDTH = AlteraAtributosJSP.nPANEL_WIDTH;
  private static final int nPANEL_HEIGHT = AlteraAtributosJSP.nPANEL_HEIGHT;
  private static final int nSPACER_SIZE = AlteraAtributosJSP.nSPACER_SIZE;

  //   public final static Color cBG_COLOR = Color.WHITE;
  public final static Color cBG_COLOR = AlteraAtributosJSP.cBG_COLOR;
  public final static String sROW_EDIT_PREFIX = AlteraAtributosJSP.sROW_EDIT_PREFIX;
  public final static String sROW_MOVE_UP = AlteraAtributosJSP.sROW_MOVE_UP;
  public final static String sROW_MOVE_DOWN = AlteraAtributosJSP.sROW_MOVE_DOWN;
  public final static String sROW_REMOVE = AlteraAtributosJSP.sROW_REMOVE;
  public final static String sROW_ADD_AT = AlteraAtributosJSP.sROW_ADD_AT;

  public final String sCHOOSE;

  public final String sNO_PRINT;

  public final String[] saColNames;


  // BUTTON PREVIEW STUFF
  public static final Dimension FORM_BUTTON_DIMENSION = AlteraAtributosJSP.FORM_BUTTON_DIMENSION;
  public final static String sBUTTON_EDIT_PREFIX = AlteraAtributosJSP.sBUTTON_EDIT_PREFIX;
  public final static String sBUTTON_MOVE_LEFT = AlteraAtributosJSP.sBUTTON_MOVE_LEFT;
  public final static String sBUTTON_MOVE_RIGHT = AlteraAtributosJSP.sBUTTON_MOVE_RIGHT;
  public final static String sBUTTON_REMOVE = AlteraAtributosJSP.sBUTTON_REMOVE;
  public final static String sBUTTON_ADD_AT = AlteraAtributosJSP.sBUTTON_ADD_AT;

  protected static final String sCANCEL_TYPE = AlteraAtributosJSP.sCANCEL_TYPE;
  protected static final String sRESET_TYPE = AlteraAtributosJSP.sRESET_TYPE;
  protected static final String sSAVE_TYPE = AlteraAtributosJSP.sSAVE_TYPE;
  protected static final String sPRINT_TYPE = AlteraAtributosJSP.sPRINT_TYPE;
  protected static final String sNEXT_TYPE = AlteraAtributosJSP.sNEXT_TYPE;
  protected static final String sCUSTOM_TYPE = AlteraAtributosJSP.sCUSTOM_TYPE;

  private final static String[] saDEF_BUTTONS = { sPRINT_TYPE,  sNEXT_TYPE};
  private final HashSet<String> hsREQ_BUTTONS = new HashSet<String>();
  private final HashMap<String,String> hmBUTTON_TYPES = new HashMap<String, String>();
  private final HashMap<String,String> hmBUTTON_TYPES_REV = new HashMap<String, String>(); // hmBUTTON_TYPES reverse
  private final HashMap<String,String> hmBUTTON_INFO = new HashMap<String, String>();

  public final static String sBUTTON_ATTR_PREFIX = FormProps.sBUTTON_ATTR_PREFIX;
  public final static String sBUTTON_ATTR_ID = FormProps.sBUTTON_ATTR_ID;
  public final static String sBUTTON_ATTR_POSITION = FormProps.sBUTTON_ATTR_POSITION;
  public final static String sBUTTON_ATTR_TYPE = FormProps.sBUTTON_ATTR_TYPE;
  public final static String sBUTTON_ATTR_TEXT = FormProps.sBUTTON_ATTR_TEXT;
  public final static String sBUTTON_ATTR_TOOLTIP = FormProps.sBUTTON_ATTR_TOOLTIP;
  public final static String sBUTTON_ATTR_IMAGE = FormProps.sBUTTON_ATTR_IMAGE;
  public final static String sBUTTON_ATTR_CUSTOM_VAR = FormProps.sBUTTON_ATTR_CUSTOM_VAR;
  public final static String sBUTTON_ATTR_CUSTOM_VALUE = FormProps.sBUTTON_ATTR_CUSTOM_VALUE;
  public final static String sBUTTON_ATTR_SHOW_COND = FormProps.sBUTTON_ATTR_SHOW_COND;
  // BUTTON PREVIEW STUFF END

  private int exitStatus = EXIT_STATUS_CANCEL;
  
  JPanel jPanelMain = new JPanel(); // main window container

  JButton jButtonAddButton = new JButton(); // add button button
  JButton jButtonAddField = new JButton(); // add row button
  JButton jButtonClose = new JButton(); // close button
  JButton jButtonCancel = new JButton(); // cancel button
  JTableJSP jTable = null; // table
  JScrollPane jScrollPane = null; // table container
  JPreviewFormButtons jpButtons = null; // button panel
  JScrollPane jScrollPaneButtons = null; // button container



  private void initMaps() {
    hsREQ_BUTTONS.add(sPRINT_TYPE);
    hsREQ_BUTTONS.add(sNEXT_TYPE);

    hmBUTTON_TYPES.put(sPRINT_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.print")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sNEXT_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.forward")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sCUSTOM_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.custom")); //$NON-NLS-1$

    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosDetalheProcesso.button.print"),sPRINT_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosDetalheProcesso.button.forward"),sNEXT_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosDetalheProcesso.button.custom"),sCUSTOM_TYPE); //$NON-NLS-1$

    hmBUTTON_INFO.put(sPRINT_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.print.tooltip")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sNEXT_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.forward.tooltip")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sCHOOSE,adapter.getString("AlteraAtributosDetalheProcesso.button.choose")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sCUSTOM_TYPE,adapter.getString("AlteraAtributosDetalheProcesso.button.custom.tooltip")); //$NON-NLS-1$
  }

  
  public AlteraAtributosDetalheProcesso(FlowEditorAdapter adapter) {
    super(adapter, true);
    
    // copy some attributes from block jsp
    AlteraAtributosJSP jsp = new AlteraAtributosJSP(adapter);
    sCHOOSE = jsp.sCHOOSE;
    sNO_PRINT = jsp.sNO_PRINT;
    saColNames = jsp.saColNames;


    initMaps();
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
    this.setVisible(true);
  }


  void jbInit(List<Atributo> atributos)
  throws Exception {

    // MAIN WINDOW

    this.jPanelMain.setLayout(new BorderLayout());


    // borders
    JPanel aux=new JPanel();
    aux.setSize(100,1);
    this.jPanelMain.add(aux, BorderLayout.WEST);
    aux=new JPanel();
    aux.setSize(100,1);
    this.jPanelMain.add(aux, BorderLayout.EAST);
    aux=new JPanel();
    aux.setSize(1,100);
    this.jPanelMain.add(aux, BorderLayout.NORTH);



    // table
    jTable = new JTableJSP(saColNames, atributos);
    jScrollPane = new JScrollPane(jTable);
    jScrollPane.getVerticalScrollBar().setUnitIncrement(10); // skip pixels per scroll
    jTable.setParent(this);
    this.jPanelMain.add(jScrollPane, BorderLayout.CENTER);


    // buttons
    jButtonAddButton.setText(adapter.getString("AlteraAtributosDetalheProcesso.button.addbutton")); //$NON-NLS-1$
    jButtonAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jpButtons.addFormButton();
      }
    });

    jButtonAddField.setText(adapter.getString("AlteraAtributosDetalheProcesso.button.addfield")); // TODO: put message in Mesg class //$NON-NLS-1$
    jButtonAddField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTable.addRow();
      }
    });

    jButtonClose.setText(adapter.getString("AlteraAtributosDetalheProcesso.close")); // TODO: put message in Mesg class //$NON-NLS-1$
    jButtonClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitStatus = EXIT_STATUS_OK;
        dispose();
      }
    });

    jButtonCancel.setText(adapter.getString("Common.cancel")); // TODO: put message in Mesg class //$NON-NLS-1$
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

    //     aux3 = new JPanel();
    //     aux3.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
    //     aux3.setBorder(BorderFactory.createLoweredBevelBorder());
    //     aux3.setLayout(new BorderLayout());
    //     aux3.add(new JLabel("Botões"),BorderLayout.NORTH);
    //     aux3.add(jpButtons,BorderLayout.CENTER);

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
    private static final long serialVersionUID = 4268232795220552814L;

    String[] _saColNames;

    AlteraAtributosDetalheProcesso _parent = null;

    // ordered list with JSPFieldData objects
    private ArrayList<JSPFieldData> _alRows = new ArrayList<JSPFieldData>();

    private ArrayList<Integer> _alTableProps = new ArrayList<Integer>();

    private JPanel _fullPanel = null;
    private JPanel _mainPanel = null;
    private GridBagLayout _gridbag;
    private GridBagConstraints _c;

    private JTextField _jtfDescription = null;
    private JTextField _jtfResult = null;
    private JTextField _jtfSearchFlowId = null;
    private JTextField _jtfSearchPid = null;
    private JTextField _jtfSearchSubPid = null;
    private JComboBox _jcbStyleSheet = null;
    private JComboBox _jcbPrintStyleSheet = null;

    public JTableJSP(String[] asaColNames, List<Atributo> alAttributes) {
      super(new BorderLayout());

      this._jtfDescription = new JTextField(30);
      this._jtfResult = new JTextField(30);
      this._jtfSearchFlowId = new JTextField(30);
      this._jtfSearchPid = new JTextField(30);
      this._jtfSearchSubPid = new JTextField(30);

      RepositoryClient rep = adapter.getRepository();
      String[] saStyleSheets = null;
      String[] saTemplates = null;

      if (rep != null) {
        saStyleSheets = rep.listStyleSheets(); // rep.listFiles("StyleSheets"); //$NON-NLS-1$
        saTemplates = rep.listPrintTemplates(); //$NON-NLS-1$
      }

      if (saStyleSheets != null && saStyleSheets.length > 0) {
        this._jcbStyleSheet = new JComboBox(saStyleSheets);
      }
      else {
        this._jcbStyleSheet = new JComboBox();
        this._jcbStyleSheet.setEnabled(false);
      }

      String[] sa = null;
      if ((saStyleSheets != null && saStyleSheets.length > 0) ||
          (saTemplates != null && saTemplates.length > 0)) {
        if (saStyleSheets != null && saTemplates != null) {
          sa = new String[saStyleSheets.length + saTemplates.length + 1];
          int index = 0;
          sa[index++] = sNO_PRINT;
          for (int i=0; i < saStyleSheets.length; i++) {
            sa[index++] = "StyleSheets/" + saStyleSheets[i]; //$NON-NLS-1$
          }
          for (int i=0; i < saTemplates.length; i++) {
            sa[index++] = "Templates/" + saTemplates[i]; //$NON-NLS-1$
          }
        }
        else if (saStyleSheets != null) {
          sa = new String[saStyleSheets.length + 1];
          int index = 0;
          sa[index++] = sNO_PRINT;
          for (int i=0; i < saStyleSheets.length; i++) {
            sa[index++] = "StyleSheets/" + saStyleSheets[i]; //$NON-NLS-1$
          }
        }
        else if (saTemplates != null) {
          sa = new String[saTemplates.length + 1];
          int index = 0;
          sa[index++] = sNO_PRINT;
          for (int i=0; i < saTemplates.length; i++) {
            sa[index++] = "Templates/" + saTemplates[i]; //$NON-NLS-1$
          }
        }
        if (sa != null) {
          this._jcbPrintStyleSheet = new JComboBox(sa);
        }
      }
      if (sa == null) {
        this._jcbPrintStyleSheet = new JComboBox();
        this._jcbPrintStyleSheet.setEnabled(false);
      }

      this._saColNames = asaColNames;

      this._alTableProps.add(new Integer(JSPFieldData.nPROP_TEXT));
      this._alTableProps.add(new Integer(JSPFieldData.nPROP_FIELD_TYPE));
      this._alTableProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
      this._alTableProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));

      this.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

      this.importData(alAttributes);

      this.makeTable();
    }


    public String[][] exportData() {

      String[][] ret = new String[0][0];

      ArrayList<String> alNames = new ArrayList<String>();
      ArrayList<String> alValues = new ArrayList<String>();

      JSPFieldData fd = null;
      Map<Integer, ArrayList<String>> alArray = null;
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

      String sSearchFlowId = ""; //$NON-NLS-1$
      if (this._jtfSearchFlowId != null) {
        sSearchFlowId = this._jtfSearchFlowId.getText();
        if (sSearchFlowId == null || sSearchFlowId.equals("")) { //$NON-NLS-1$
          sSearchFlowId = ""; //$NON-NLS-1$
        }
        else {
          sSearchFlowId = sSearchFlowId.trim();
        }
      }

      String sSearchPid = ""; //$NON-NLS-1$
      if (this._jtfSearchPid != null) {
        sSearchPid = this._jtfSearchPid.getText();
        if (sSearchPid == null || sSearchPid.equals("")) { //$NON-NLS-1$
          sSearchPid = ""; //$NON-NLS-1$
        }
        else {
          sSearchPid = sSearchPid.trim();
        }
      }

      String sSearchSubPid = ""; //$NON-NLS-1$
      if (this._jtfSearchSubPid != null) {
        sSearchSubPid = this._jtfSearchSubPid.getText();
        if (sSearchSubPid == null || sSearchSubPid.equals("")) { //$NON-NLS-1$
          sSearchSubPid = ""; //$NON-NLS-1$
        }
        else {
          sSearchSubPid = sSearchSubPid.trim();
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

      ret = new String[alNames.size() + 7][2];

      // put description, result and stylesheet prop at list end
      ret[alNames.size()][0] = AlteraAtributos.sDESCRIPTION;
      ret[alNames.size()][1] = sDescription;
      ret[alNames.size() + 1][0] = AlteraAtributos.sRESULT;
      ret[alNames.size() + 1][1] = sResult;
      ret[alNames.size() + 2][0] = JSPFieldData.getPropCodeName(JSPFieldData.nPROP_STYLESHEET);
      ret[alNames.size() + 2][1] = sStyleSheet;
      ret[alNames.size() + 3][0] = FormProps.PROC_DETAIL_SEARCH_SUBPID;
      ret[alNames.size() + 3][1] = sSearchSubPid;
      ret[alNames.size() + 4][0] = FormProps.PROC_DETAIL_SEARCH_PID;
      ret[alNames.size() + 4][1] = sSearchPid;
      ret[alNames.size() + 5][0] = FormProps.PROC_DETAIL_SEARCH_FLOWID;
      ret[alNames.size() + 5][1] = sSearchFlowId;
      ret[alNames.size() + 6][0] = FormProps.PRINT_STYLESHEET;
      ret[alNames.size() + 6][1] = sPrintStyleSheet;

      for (int i=0; i < alNames.size(); i++) {
        ret[i][0] = (String)alNames.get(i);
        ret[i][1] = (String)alValues.get(i);
      }

      return ret;
    }


    public void importData(List<Atributo> lAttributes) {

      // key: integer with obj position
      // value: ArrayList[2] with name arraylist and values arraylist
      HashMap<Integer,ArrayList<String>[]> hmList = new HashMap<Integer, ArrayList<String>[]>();

      ArrayList<String>[] alList = null;
      ArrayList<String> alNames = null;
      ArrayList<String> alValues = null;
      String sName = null;
      String sVal = null;
      String stmp = null;
      Integer itmp = null;
      int idx = -1;

      for (int i=0;i<lAttributes.size(); i++) {
        sName = lAttributes.get(i).getDescricao();
        sVal = lAttributes.get(i).getValor();


        if (sName.equals(AlteraAtributos.sDESCRIPTION)) {
          this._jtfDescription.setText(sVal);
          continue;
        } else if (sName.equals(AlteraAtributos.sRESULT)) {
          this._jtfResult.setText(sVal);
          continue;
        } else if (sName.equals(FormProps.PROC_DETAIL_SEARCH_FLOWID)) {
          this._jtfSearchFlowId.setText(sVal);
          continue;
        } else if (sName.equals(FormProps.PROC_DETAIL_SEARCH_PID)) {
          this._jtfSearchPid.setText(sVal);
          continue;
        } else if (sName.equals(FormProps.PROC_DETAIL_SEARCH_SUBPID)) {
          this._jtfSearchSubPid.setText(sVal);
          continue;
        } else if (sName.equals(JSPFieldData.getPropCodeName(JSPFieldData.nPROP_STYLESHEET))) {
          this._jcbStyleSheet.setSelectedItem(sVal);
          continue;
        } else if (sName.equals(FormProps.PRINT_STYLESHEET)) {
          this._jcbPrintStyleSheet.setSelectedItem(sVal);
          continue;
        } else if (sName.startsWith(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_PREFIX)) {
          continue;
        }

        stmp = JSPFieldData.sNAME_PREFIX + JSPFieldData.sJUNCTION;
        idx = sName.indexOf(stmp);

        if (idx == -1) {
          adapter.log("AlteraAtributosDetalheProcesso: importData: undefined object"); //$NON-NLS-1$
          continue;
        }

        idx += stmp.length();
        stmp = sName.substring(idx, sName.indexOf(JSPFieldData.sJUNCTION, idx));
        itmp = new Integer(stmp);

        sName = sName.substring(sName.indexOf(JSPFieldData.sJUNCTION,
            idx) + JSPFieldData.sJUNCTION.length());

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
        alList = hmList.get(itmp);

        alNames = alList[0];
        alValues = alList[1];

        fd1 = new JSPFieldData(adapter, row, alNames, alValues, this.getTableProps());

        fd2 = cloneField(fd1);
        if (fd2 == null) {
          adapter.log("AlteraAtributosDetalheProcesso: importData: unable to get class name for field type "+ fd1.getFieldType()); //$NON-NLS-1$
          continue;
        }

        this.appendTableRow(fd2);
      }

    }


    public ArrayList<Integer> getTableProps() {
      return this._alTableProps;
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
      jPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      JLabel jLabel = null;
      String stmp = adapter.getString("AlteraAtributosDetalheProcesso.description"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfDescription);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      JPanel sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfDescription,sC);
      jPanel.add(this._jtfDescription);
      sC.gridwidth = 1;


      // RESULT
      jLabel = null;
      stmp = adapter.getString("AlteraAtributosDetalheProcesso.result.description"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfResult);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfResult,sC);
      jPanel.add(this._jtfResult);
      sC.gridwidth = 1;


      // SEARCHFLOWID
      jLabel = null;
      stmp = adapter.getString("AlteraAtributosDetalheProcesso.flowid.var"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfSearchFlowId);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfSearchFlowId,sC);
      jPanel.add(this._jtfSearchFlowId);
      sC.gridwidth = 1;


      // SEARCHPID
      jLabel = null;
      stmp = adapter.getString("AlteraAtributosDetalheProcesso.pid.var"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfSearchPid);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfSearchPid,sC);
      jPanel.add(this._jtfSearchPid);
      sC.gridwidth = 1;


      // SEARCHSUBPID //ptgm
      jLabel = null;
      stmp = adapter.getString("AlteraAtributosDetalheProcesso.subpid.var"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jtfSearchSubPid);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jtfSearchSubPid,sC);
      jPanel.add(this._jtfSearchSubPid);
      sC.gridwidth = 1;


      // STYLESHEET
      jLabel = null;
      stmp = JSPFieldData.getPropLabel(JSPFieldData.nPROP_STYLESHEET);
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jcbStyleSheet);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jcbStyleSheet,sC);
      jPanel.add(this._jcbStyleSheet);
      sC.gridwidth = 1;


      // PRINT STYLESHEET
      jLabel = null;
      stmp = adapter.getString("AlteraAtributosDetalheProcesso.stylesheet.var"); //$NON-NLS-1$
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(this._jcbPrintStyleSheet);
      sGridbag.setConstraints(jLabel,sC);
      jPanel.add(jLabel);
      // separator
      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      jPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(this._jcbPrintStyleSheet,sC);
      jPanel.add(this._jcbPrintStyleSheet);
      sC.gridwidth = 1;


      // now table panel
      if (this._fullPanel != null) {
        this.remove(this._fullPanel);
      }

      this._mainPanel = new JPanel();
      this._mainPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

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
      makeSeparator(1,100);


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

      // DESCRIPTION, RESULT, SEARCHFLOWID, SEARCHPID, STYLESHEET AND PRINT_STYLESHEET
      c.gridwidth = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(jPanel,c);
      jtmp.add(jPanel);

      sizer = new JPanel();
      sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      sizer.setSize(1,10);
      gridbag.setConstraints(sizer,c);
      jtmp.add(sizer);

      gridbag.setConstraints(this._mainPanel,c);
      jtmp.add(this._mainPanel);


      this._fullPanel = new JPanel();
      this._fullPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      this._fullPanel.add(jtmp, BorderLayout.NORTH);

      this.add(this._fullPanel, BorderLayout.NORTH);

      this.revalidateParent();
    }


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
          // property ok.. make a button
          this.makeButton(stmp,
              AlteraAtributosDetalheProcesso.sROW_EDIT_PREFIX
              + anPosition);
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
      panel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      this._gridbag.setConstraints(panel, this._c);
      this._mainPanel.add(panel);
    } // makeSeparator


    private JLabel makeLabel(String asName) {
      return this.makeLabel(asName,null);
    }

    private JLabel makeLabel(String asName,
        Font afFont) {
      JLabel label = new JLabel (asName,JLabel.CENTER);
      label.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
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
      button.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      button.setBorderPainted(false);
      button.setActionCommand(asActionCommand);
      button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String sActionCom = e.getActionCommand();
          String sRowPos =
            sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_EDIT_PREFIX.length());
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

      ret.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
      ret.setSize(nPanelWidth,nPanelHeight);


      JButton jbUp =
        this.makeControlButton("up.gif", //$NON-NLS-1$
            adapter.getString("AlteraAtributosDetalheProcesso.line.up"), //$NON-NLS-1$
            AlteraAtributosDetalheProcesso.sROW_MOVE_UP,
            anPosition,
            dimButton);
      if (anPosition == 0) {
        jbUp.setEnabled(false);
      }


      JButton jbDown =
        this.makeControlButton("down.gif", //$NON-NLS-1$
            adapter.getString("AlteraAtributosDetalheProcesso.line.down"), //$NON-NLS-1$
            AlteraAtributosDetalheProcesso.sROW_MOVE_DOWN,
            anPosition,
            dimButton);
      if (anPosition == (this.getRowCount()-1)) {
        jbDown.setEnabled(false);
      }

      JButton jbRemove =
        this.makeControlButton("remove.gif", //$NON-NLS-1$
            adapter.getString("AlteraAtributosDetalheProcesso.line.remove"), //$NON-NLS-1$
            AlteraAtributosDetalheProcesso.sROW_REMOVE,
            anPosition,
            dimButton);


      JButton jbAdd =
        this.makeControlButton("add.gif", //$NON-NLS-1$
            adapter.getString("AlteraAtributosDetalheProcesso.line.add"), //$NON-NLS-1$
            AlteraAtributosDetalheProcesso.sROW_ADD_AT,
            anPosition,
            dimButton);

      JButton jbEdit =
        this.makeControlButton("edit.gif", //$NON-NLS-1$
            adapter.getString("AlteraAtributosDetalheProcesso.line.edit"), //$NON-NLS-1$
            AlteraAtributosDetalheProcesso.sROW_EDIT_PREFIX,
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

      ImageIcon ic = new ImageIcon(adapter.getJanela().createImage(asImageIconName, false));
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

          if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sROW_MOVE_UP)) {
            sRowPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_MOVE_UP.length());
            upRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sROW_MOVE_DOWN)) {
            sRowPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_MOVE_DOWN.length());
            downRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sROW_REMOVE)) {
            sRowPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_REMOVE.length());
            removeRow(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sROW_ADD_AT)) {
            sRowPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_ADD_AT.length());
            addRowAt(Integer.parseInt(sRowPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sROW_EDIT_PREFIX)) {
            sRowPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sROW_EDIT_PREFIX.length());
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
        fd = new JSPFieldData(adapter);
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
        fd = new JSPFieldData(adapter);
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

    public void setParent (AlteraAtributosDetalheProcesso aParent) {
      this._parent = aParent;
    }

  } // class JTableJSP



  /**
   * ROW EDITOR: class to edit/create a table's rows
   * @see javax.swing.JDialog
   */
  private class RowEditor extends JDialog {
    private static final long serialVersionUID = 6212882193191382551L;

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
        fd = new JSPFieldData(adapter);
      }
      this._sFieldSelected = fd.getFieldType();

      this._hmFields.put(this._sFieldSelected,fd);

      this.init();
      this.setSize(800,400);
      this.setModal(true);
      this.bInitialized = true;
      this.setVisible(true);
    }

    public void init() {

      JSPFieldData fdSel = this.getSelectedFieldData();
      JPanel sizer = null;
      JPanel aux = null;
      String sClassName = null;
      Class<?> cClass = null;


      if (fdSel.isNew()) {
        this.setTitle(adapter.getString("Common.add")); //$NON-NLS-1$
      }
      else {
        this.setTitle(adapter.getString("Common.edit")); //$NON-NLS-1$
      }

      this.jPanelMain = new JPanel(new BorderLayout());


      // BORDERS
      sizer = new JPanel();
      sizer.setSize(100,1);
      this.jPanelMain.add(sizer, BorderLayout.WEST);
      sizer=new JPanel();
      sizer.setSize(100,1);
      this.jPanelMain.add(sizer, BorderLayout.EAST);
      sizer=new JPanel();
      sizer.setSize(1,100);
      this.jPanelMain.add(sizer, BorderLayout.NORTH);

      // BUTTONS
      this.jButtonOk = new JButton();
      this.jButtonOk.setText(adapter.getString("Common.ok")); //$NON-NLS-1$
      this.jButtonOk.setEnabled(false);  // only enable after field type select
      this.jButtonOk.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          saveData();
        }
      });

      this.jButtonCancel = new JButton();
      this.jButtonCancel.setText(adapter.getString("Common.cancel")); //$NON-NLS-1$
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
            getAdapter().log("AlteraAtributosDetalheProcesso: RowEditor: " //$NON-NLS-1$
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
            adapter.log("AlteraAtributosDetalheProcesso: RowEditor: " //$NON-NLS-1$
                + "unable to load class " //$NON-NLS-1$
                + sClassName);
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
        adapter.showError(fde.getMessage());
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



  class JPreviewFormButtons extends JPanel {
    private static final long serialVersionUID = 4208967245193286717L;

    AlteraAtributosDetalheProcesso _parent = null;

    // ordered list with FormButton objects
    private List<FormButton> _alButtons = new ArrayList<FormButton>();
    // button hashset 
    private HashSet<String> _hsButtons = new HashSet<String>();

    private JPanel _mainPanel = null;
    private GridBagLayout _gridbag;
    private GridBagConstraints _c;

    public JPreviewFormButtons(AlteraAtributosDetalheProcesso aParent, List<Atributo> alAttributes) {
      super(new BorderLayout());

      this.setParent(aParent);

      this.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

      this.importData(alAttributes);

      this.makePreview();
    }


    public String[][] exportData() {
      int nNUM_ATTR = 9;
      String[][] retObj = new String[this.getButtonCount()*nNUM_ATTR][2];

      FormButton fb = null;
      String stmp = null;
      String sPrefix = null;
      for (int i=0, j=0; i < this.getButtonCount(); i++,j+=nNUM_ATTR) {
        fb = this.getFormButton(i);

        sPrefix = AlteraAtributosDetalheProcesso.sBUTTON_ATTR_PREFIX + fb.getPosition() + "_"; //$NON-NLS-1$

        retObj[j][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_ID;
        retObj[j][1] = String.valueOf(fb.getID());

        retObj[j+1][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_POSITION;
        retObj[j+1][1] = String.valueOf(fb.getPosition());

        retObj[j+2][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TYPE;
        retObj[j+2][1] = fb.getType();

        stmp = fb.getText();
        if (stmp == null) {
          stmp = ""; //$NON-NLS-1$
        }
        else if (stmp.equals(sCHOOSE)) {
          stmp = (String)hmBUTTON_TYPES.get((String)retObj[j+2][1]);
        }

        retObj[j+3][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TEXT;
        retObj[j+3][1] = stmp;

        retObj[j+4][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TOOLTIP;
        retObj[j+4][1] = fb.getToolTip();
        if (retObj[j+4][1] == null) retObj[j+4][1] = ""; //$NON-NLS-1$

        retObj[j+5][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_IMAGE;
        retObj[j+5][1] = fb.getImage();
        if (retObj[j+5][1] == null) retObj[j+5][1] = ""; //$NON-NLS-1$

        retObj[j+6][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_CUSTOM_VAR;
        retObj[j+6][1] = fb.getCustomVar();
        if (retObj[j+6][1] == null) retObj[j+6][1] = ""; //$NON-NLS-1$

        retObj[j+7][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_CUSTOM_VALUE;
        retObj[j+7][1] = fb.getCustomValue();
        if (retObj[j+7][1] == null) retObj[j+7][1] = ""; //$NON-NLS-1$

        retObj[j+8][0] = sPrefix + AlteraAtributosDetalheProcesso.sBUTTON_ATTR_SHOW_COND;
        retObj[j+8][1] = fb.getShowCond();
        if (retObj[j+8][1] == null) retObj[j+8][1] = ""; //$NON-NLS-1$
      }

      return retObj;
    }


    public void importData(List<Atributo> lAttributes) {

      FormButton fb = null;
      String sPos = null;
      String sName = null;
      String sVal = null;
      int ntmp = 0;
      HashMap<String, FormButton> hm = new HashMap<String, FormButton>();

      for (int i=0;i<lAttributes.size(); i++) {
        sName = lAttributes.get(i).getDescricao();
        sVal = lAttributes.get(i).getValor();

        if (!sName.startsWith(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_PREFIX)) {
          continue;
        }

        sPos = sName.substring(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_PREFIX.length());
        ntmp = sPos.indexOf("_"); //$NON-NLS-1$
        sName = sPos.substring(ntmp+1);
        sPos = sPos.substring(0,ntmp);

        if (hm.containsKey(sPos)) {
          fb = hm.get(sPos);
        }
        else {
          fb = new FormButton(this);
        }

        if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_ID)) {
          try {
            fb.setID(Integer.parseInt(sVal));
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_POSITION)) {
          try {
            fb.setPosition(Integer.parseInt(sVal));
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TYPE)) {
          fb.setType(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TEXT)) {
          fb.setText(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_TOOLTIP)) {
          fb.setToolTip(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_IMAGE)) {
          fb.setImage(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_CUSTOM_VAR)) {
          fb.setCustomVar(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_CUSTOM_VALUE)) {
          fb.setCustomValue(sVal);
        }
        else if (sName.equals(AlteraAtributosDetalheProcesso.sBUTTON_ATTR_SHOW_COND)) {
          fb.setShowCond(sVal);
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
        for (int i=0; i < hm.size(); i++) {
          sPos = String.valueOf(i);
          fb = (FormButton)hm.get(sPos);
          this.appendFormButton(fb);	
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
        if (nPos >= 0) {
          // new button with position defined
          afbButton.setID(this.getButtonCount());
        }
        else {
          // new button with position undefined...
          int pos = this.getButtonCount();
          afbButton.setPosition(pos);
          afbButton.setID(pos);
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
      this._mainPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

      this._gridbag = new GridBagLayout();
      this._c = new GridBagConstraints();
      this._c.fill = GridBagConstraints.HORIZONTAL;
      this._c.weightx = 1.0;
      this._c.gridwidth = 1;
      this._c.ipady = nPad;

      this._mainPanel.setLayout(this._gridbag);
      this._mainPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

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
        sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
        sizer.setSize(nSpacerWidth,nPanelHeight);
        this._gridbag.setConstraints(sizer,this._c);
        this._mainPanel.add(sizer);

        this._gridbag.setConstraints(fb,this._c);
        this._mainPanel.add(fb);

        sizer = new JPanel();
        sizer.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);
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
        stmp = (String)iter.next();
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

    public void setParent (AlteraAtributosDetalheProcesso aParent) {
      this._parent = aParent;
    }

  } // class JPreviewFormButtons


  class FormButton extends JPanel {
    private static final long serialVersionUID = -2939457242721204070L;

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


    // default constructor
    public FormButton(JPreviewFormButtons aParent) {
      super(new BorderLayout());
      this.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

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
      this._mainPanel.setBackground(AlteraAtributosDetalheProcesso.cBG_COLOR);

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
      this._jbMoveLeft = this.makeButton("left.gif", null, adapter.getString("AlteraAtributosDetalheProcesso.button.move.left"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosDetalheProcesso.sBUTTON_MOVE_LEFT, dBig);
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
      this._jbAdd = this.makeButton("addButton.gif", null, adapter.getString("AlteraAtributosDetalheProcesso.button.add"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosDetalheProcesso.sBUTTON_ADD_AT, dSmall);
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
      this._jbMoveRight = this.makeButton("right.gif", null, adapter.getString("AlteraAtributosDetalheProcesso.button.move.right"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosDetalheProcesso.sBUTTON_MOVE_RIGHT, dBig);
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
      this._jbRemove = this.makeButton("removeButton.gif", null, adapter.getString("AlteraAtributosDetalheProcesso.button.remove"), //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosDetalheProcesso.sBUTTON_REMOVE, dSmall);
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

      this._jbText = this.makeButton(null, this.getText(), adapter.getString("AlteraAtributosDetalheProcesso.button.eddit"), //$NON-NLS-1$
          AlteraAtributosDetalheProcesso.sBUTTON_EDIT_PREFIX,null);
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
        ic = new ImageIcon(adapter.getJanela().createImage(asImageIconName, false));
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
              .equals(AlteraAtributosDetalheProcesso.sBUTTON_MOVE_RIGHT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sBUTTON_MOVE_RIGHT.length());
            _parent.rightFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sBUTTON_MOVE_LEFT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sBUTTON_MOVE_LEFT.length());
            _parent.leftFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sBUTTON_REMOVE)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sBUTTON_REMOVE.length());
            _parent.removeFormButton(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sBUTTON_ADD_AT)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sBUTTON_ADD_AT.length());
            _parent.addFormButtonAt(Integer.parseInt(sButtonPos));
          }
          else if (asActionCommandPrefix
              .equals(AlteraAtributosDetalheProcesso.sBUTTON_EDIT_PREFIX)) {
            sButtonPos = sActionCom
            .substring(AlteraAtributosDetalheProcesso.sBUTTON_EDIT_PREFIX.length());
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
    private static final long serialVersionUID = 8891432346326957954L;

    private JPreviewFormButtons _parent;

    private JPanel jPanelMain;
    private JPanel jPanelEdit;

    private JComboBox jComboFields;
    private JTextArea jtaInfo;

    // KEY:type(String) VALUE:hashmap(key:fieldname(String) value:Field(JComponent)
    private HashMap<String,HashMap<String, Component>> hmComponents;

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
      this.hmComponents = new HashMap<String, HashMap<String,Component>>();

      this.init();
      this.setSize((int)(nPANEL_WIDTH/1.5),(int)(nPANEL_HEIGHT/1.5));
      this.setModal(true);
      this.setVisible(true);
    }

    public void init() {

      JPanel sizer = null;
      JPanel aux = null;

      String sType = this._fb.getType();
      String sTypeText = null;
      if (sType != null) sTypeText = (String)hmBUTTON_TYPES.get(sType);

      if (this._fb.isNew()) {
        this.setTitle(adapter.getString("Common.add")); //$NON-NLS-1$
      }
      else {
        this.setTitle(adapter.getString("Common.edit")); //$NON-NLS-1$
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
      this.jButtonOk.setText(adapter.getString("Common.ok")); //$NON-NLS-1$
      this.jButtonOk.setEnabled(false);  // only enable after field type select
      this.jButtonOk.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          saveData();
        }
      });

      this.jButtonCancel = new JButton();
      this.jButtonCancel.setText(adapter.getString("Common.cancel")); //$NON-NLS-1$
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
      HashMap<String, Component> hmtmp = this.hmComponents.get(sType);
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
        adapter.showError(adapter.getString("AlteraAtributosDetalheProcesso.error.missing.button.text")); //$NON-NLS-1$
        return;
      }


      if (sType.equals(sCUSTOM_TYPE)) {
        // save custom vars

        if (hmtmp.containsKey("jtfCustomVar")) { //$NON-NLS-1$
          jtf = (JTextField)hmtmp.get("jtfCustomVar"); //$NON-NLS-1$
          stmp = jtf.getText();
          if (stmp == null || stmp.equals("")) { //$NON-NLS-1$
            adapter.showError(adapter.getString("AlteraAtributosDetalheProcesso.error.missing.button.var"));//$NON-NLS-1$
            return;
          }
          this._fb.setCustomVar(stmp);
        }

        if (hmtmp.containsKey("jtfCustomValue")) { //$NON-NLS-1$
          jtf = (JTextField)hmtmp.get("jtfCustomValue"); //$NON-NLS-1$
          stmp = jtf.getText();
          if (stmp == null || stmp.equals("")) { //$NON-NLS-1$
            adapter.showError(adapter.getString("AlteraAtributosDetalheProcesso.error.missing.button.var.value"));//$NON-NLS-1$
            return;
          }
          this._fb.setCustomValue(stmp);
        }

        if (hmtmp.containsKey("jtfShowCond")) { //$NON-NLS-1$
          jtf = (JTextField)hmtmp.get("jtfShowCond"); //$NON-NLS-1$
          stmp = jtf.getText();
          if (stmp == null) {
            stmp = ""; //$NON-NLS-1$
          }
          this._fb.setShowCond(stmp);
        }
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
        jtfText.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.text.tooltip")); //$NON-NLS-1$
        JLabel jlTextLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.text") + asTypeText); //$NON-NLS-1$
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
        jtfToolTip.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.button.tooltip")); //$NON-NLS-1$
        JLabel jlToolTipLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.tooltip") + asTypeText); //$NON-NLS-1$
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
        jtfImage.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.image.tooltip")); //$NON-NLS-1$
        JLabel jlImageLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.image") + asTypeText); //$NON-NLS-1$
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



        JTextField jtfCustomVar = null;
        JLabel jlCustomVarLabel = null;
        JTextField jtfCustomValue = null;
        JLabel jlCustomValueLabel = null;
        JTextField jtfShowCond = null;
        JLabel jlShowCondLabel = null;

        if (sType.equals(sCUSTOM_TYPE)) {
          // custom props

          jtfCustomVar = new JTextField(20);
          jtfCustomVar.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.var.tooltip")); //$NON-NLS-1$
          jlCustomVarLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.var") + asTypeText); //$NON-NLS-1$
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
          jtfCustomValue.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.var.value.tooltip")); //$NON-NLS-1$
          jlCustomValueLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.value") + asTypeText); //$NON-NLS-1$
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

          // show cond
          jtfShowCond = new JTextField(20);
          jtfShowCond.setToolTipText(adapter.getString("AlteraAtributosDetalheProcesso.button.condition.tooltip")); //$NON-NLS-1$
          jlShowCondLabel = new JLabel(adapter.getString("AlteraAtributosDetalheProcesso.condition") + asTypeText); //$NON-NLS-1$
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

        }


        HashMap<String, Component> hmtmp = null;

        if (this.hmComponents.containsKey(sType)) {
          hmtmp = this.hmComponents.get(sType);
        }
        else {
          hmtmp = new HashMap<String, Component>();
        }

        hmtmp.put("jtfText", jtfText); //$NON-NLS-1$
        hmtmp.put("jlTextLabel", jlTextLabel); //$NON-NLS-1$
        hmtmp.put("jtfToolTip", jtfToolTip); //$NON-NLS-1$
        hmtmp.put("jlToolTipLabel", jlToolTipLabel); //$NON-NLS-1$
        hmtmp.put("jtfImage", jtfImage); //$NON-NLS-1$
        hmtmp.put("jlImageLabel", jlImageLabel); //$NON-NLS-1$
        if (sType.equals(sCUSTOM_TYPE)) {
          hmtmp.put("jtfCustomVar", jtfCustomVar); //$NON-NLS-1$
          hmtmp.put("jlCustomVarLabel", jlCustomVarLabel); //$NON-NLS-1$
          hmtmp.put("jtfCustomValue", jtfCustomValue); //$NON-NLS-1$
          hmtmp.put("jlCustomValueLabel", jlCustomValueLabel);	   //$NON-NLS-1$
          hmtmp.put("jtfShowCond", jtfShowCond); //$NON-NLS-1$
          hmtmp.put("jlShowCondLabel", jlShowCondLabel);	   //$NON-NLS-1$
        }
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
}


