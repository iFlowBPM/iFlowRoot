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



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.blocks.dataProcessing.OperationField;
import pt.iknow.utils.swing.DecoratedBorder;

public class AlteraAtributosProcDetail extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1L;

  private  static final int nPANEL_WIDTH = 800;
  private  static final int nPANEL_HEIGHT = 400;
  private  static final int nSPACER_SIZE = 100;

  private JPanel jPanelMain = new JPanel(); // main window container

  private JButton jButtonAddButton = new JButton(); // add button button
  private JButton jButtonClose = new JButton(); // close button
  private JButton jButtonCancel = new JButton(); // cancel button
  private JPreviewFormButtons jpButtons = null; // button panel
  private JScrollPane jScrollPaneButtons = null; // button container
  private JTextField descriptionField = new JTextField(30);
  private JTextField descriptionResultField = new JTextField(30);
  private JTextField pidField = new JTextField(30);
  private JTextField subpidField = new JTextField(30);
  private JTextField pnumberField = new JTextField(30);
  private JTextField flowidField = new JTextField(30);

  //public final static Color cBG_COLOR = Color.WHITE;
  private  final static Color cBG_COLOR = new Color(255,255,255);

  // BUTTON PREVIEW STUFF
  private  static final Dimension FORM_BUTTON_DIMENSION = new Dimension(100,49);
  private  final static String sBUTTON_EDIT_PREFIX = "BUTTON_EDIT_"; //$NON-NLS-1$
  private  final static String sBUTTON_MOVE_LEFT = "BUTTON_LEFT_"; //$NON-NLS-1$
  private  final static String sBUTTON_MOVE_RIGHT = "BUTTON_RIGHT_"; //$NON-NLS-1$
  private  final static String sBUTTON_REMOVE = "BUTTON_REMOVE_"; //$NON-NLS-1$
  private  final static String sBUTTON_ADD_AT = "BUTTON_ADD_AT_"; //$NON-NLS-1$

  //  private  static final String sCANCEL_TYPE = "_cancelar"; //$NON-NLS-1$
  //  private  static final String sRESET_TYPE = "_repor"; //$NON-NLS-1$
  //  private  static final String sSAVE_TYPE = "_guardar"; //$NON-NLS-1$
  private  static final String sPRINT_TYPE = "_imprimir"; //$NON-NLS-1$
  private  static final String sNEXT_TYPE = "_avancar"; //$NON-NLS-1$

  private  final static String[] saDEF_BUTTONS = {sPRINT_TYPE,  sNEXT_TYPE};
  private  final HashSet<String> hsREQ_BUTTONS = new HashSet<String>();
  private  final HashMap<String,String> hmBUTTON_TYPES = new HashMap<String,String>();
  private  final HashMap<String,String> hmBUTTON_TYPES_REV = new HashMap<String,String>(); // hmBUTTON_TYPES reverse
  private  final HashMap<String,String> hmBUTTON_INFO = new HashMap<String,String>();
  private  final String sCHOOSE;

  private  final static String sBUTTON_ATTR_PREFIX = "button_"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_ID = "id"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_POSITION = "position"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_TYPE = "type"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_TEXT = "text"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_TOOLTIP = "tooltip"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_IMAGE = "image"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_CUSTOM_VAR = "cvar"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_CUSTOM_VALUE = "cvalue"; //$NON-NLS-1$
  private  final static String sBUTTON_ATTR_SHOW_COND = "showcond"; //$NON-NLS-1$
  // BUTTON PREVIEW STUFF END
  private  final static String sPROP_DESCR = "block_description"; //$NON-NLS-1$
  private  final static String sPROP_RESULT = "block_result"; //$NON-NLS-1$
  private  final static String sPROP_PID = "pid"; //$NON-NLS-1$
  private  final static String sPROP_SUBPID = "subpid"; //$NON-NLS-1$
  private  final static String sPROP_PNUMBER = "pnumber"; //$NON-NLS-1$
  private  final static String sPROP_FLOWID = "flowid"; //$NON-NLS-1$

  private final static String[] fixedProps = new String[] {
    sPROP_DESCR,
    sPROP_RESULT,
    sPROP_FLOWID,
    sPROP_PID,
    sPROP_SUBPID,
    sPROP_PNUMBER,
  };

  private final JTextField[] textFields = new JTextField[] {
      descriptionField,
      descriptionResultField,
      flowidField,
      pidField,
      subpidField,
      pnumberField,
  };

  private int exitStatus = EXIT_STATUS_CANCEL;

  public AlteraAtributosProcDetail(FlowEditorAdapter adapter) {
    super(adapter, true);
    
    sCHOOSE = adapter.getString("AlteraAtributosJSP.choose"); //$NON-NLS-1$

    initMaps();
  }


  private void initMaps() {
    hmBUTTON_TYPES.put(sPRINT_TYPE,adapter.getString("AlteraAtributosJSP.button.type.print")); //$NON-NLS-1$
    hmBUTTON_TYPES.put(sNEXT_TYPE,adapter.getString("AlteraAtributosJSP.button.type.forward")); //$NON-NLS-1$

    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.print"),sPRINT_TYPE); //$NON-NLS-1$
    hmBUTTON_TYPES_REV.put(adapter.getString("AlteraAtributosJSP.button.type.forward"),sNEXT_TYPE); //$NON-NLS-1$

    hmBUTTON_INFO.put(sPRINT_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.print")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sNEXT_TYPE,adapter.getString("AlteraAtributosJSP.button.tooltip.forward")); //$NON-NLS-1$
    hmBUTTON_INFO.put(sCHOOSE,adapter.getString("AlteraAtributosJSP.button.choose")); //$NON-NLS-1$
  }

  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    String[][] retObj = null;

    String[][] buttons = this.jpButtons.exportData();

    retObj = new String[buttons.length+fixedProps.length][3];

    for (int i=0, j=0; i < fixedProps.length; i++, j++) {
      retObj[j][0] = fixedProps[i];
      retObj[j][1] = textFields[i].getText();
      retObj[j][2] = "";
    }
    for (int i=0, j=fixedProps.length; i < buttons.length; i++, j++) {
      retObj[j][0] = buttons[i][0];
      retObj[j][1] = buttons[i][1];
      retObj[j][2] = "";
    }

    return retObj;
  }


  public AlteraAtributosProcDetail() {
    this(null);
  }


  public void setDataIn(String title, List<Atributo> atributos) {
    this.setTitle(title);
    this.jbInit(atributos);
    this.pack();
    this.setSize(nPANEL_WIDTH,nPANEL_HEIGHT);
    this.setModal(true);
    this.repaint();
    this.setVisible(true);
  }


  void jbInit(List<Atributo> atributos) {

    // fetch attributes
    for (Atributo atributo : atributos) {
      if(null == atributo || null == atributo.getNome()) continue;
      String v = atributo.getValor();
      if(null == v) v = "";
      for(int i = 0; i < fixedProps.length; i++) {
        if(fixedProps[i].equals(atributo.getNome())) {
          textFields[i].setText(v);
          break;
        }
      }
    }


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

    JPanel infoPanel = new JPanel(new GridBagLayout());
    infoPanel.setBackground(AlteraAtributosProcDetail.cBG_COLOR);
    GridBagConstraints gc = new GridBagConstraints();
    JLabel lbl = null;
    gc.fill=GridBagConstraints.HORIZONTAL;
    gc.insets=new Insets(10,3,1,3);// top elem is higher
    gc.gridx=0;
    gc.gridy=0;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.description"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=0;
    lbl.setLabelFor(descriptionField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), descriptionField);
    infoPanel.add(descriptionField, gc);
    gc.insets=new Insets(1,3,1,3);
    gc.gridx=0;
    gc.gridy=1;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.result"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=1;
    lbl.setLabelFor(descriptionResultField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), descriptionResultField);
    infoPanel.add(descriptionResultField, gc);
    gc.gridx=0;
    gc.gridy=2;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.flowid"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=2;
    lbl.setLabelFor(flowidField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), flowidField);
    infoPanel.add(flowidField, gc);
    gc.gridx=0;
    gc.gridy=3;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.pid"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=3;
    lbl.setLabelFor(pidField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), pidField);
    infoPanel.add(pidField, gc);
    gc.gridx=0;
    gc.gridy=4;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.subpid"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=4;
    lbl.setLabelFor(subpidField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), subpidField);
    infoPanel.add(subpidField, gc);
    gc.gridx=0;
    gc.gridy=5;
    infoPanel.add(lbl = new JLabel(adapter.getString("AlteraAtributosProcDetail.pnumber"), JLabel.LEFT), gc);
    gc.gridx=1;
    gc.gridy=5;
    lbl.setLabelFor(pnumberField);
    new DecoratedBorder(OperationField.TYPE_EXPRESSION, infoPanel.getBackground(), pnumberField);
    infoPanel.add(pnumberField, gc);

    // bottom panel
    gc.gridx=0;
    gc.gridy++;//last row...
    gc.fill=GridBagConstraints.VERTICAL;
    gc.gridwidth=GridBagConstraints.REMAINDER;
    gc.weighty=1.0; // consume space, alignin to top
    JPanel filler = new JPanel();
    filler.setBackground(infoPanel.getBackground());
    infoPanel.add(filler, gc);


    this.jPanelMain.add(new JScrollPane(infoPanel), BorderLayout.CENTER);

    // buttons
    jButtonAddButton.setText(adapter.getString("AlteraAtributosJSP.button.add.button")); //$NON-NLS-1$
    jButtonAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jpButtons.addFormButton();
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

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(jButtonAddButton);
    buttonPanel.add(jButtonClose);
    buttonPanel.add(jButtonCancel);

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

    aux2.add(jScrollPaneButtons, BorderLayout.CENTER);

    JPanel jp = new JPanel();
    jp.setLayout(new BorderLayout());
    jp.add(aux2, BorderLayout.NORTH);
    jp.add(buttonPanel, BorderLayout.SOUTH);

    aux = null;
    aux2 = null;
    aux3 = null;

    this.jPanelMain.add(jp, BorderLayout.SOUTH);

    this.getContentPane().add(this.jPanelMain);

  }

  public void revalidate() {
    this.repaint();
    this.jPanelMain.revalidate();
  }


  /*
   * 
   * Eu sou o BOB Construtor!
   * 
   */

  class JPreviewFormButtons extends JPanel {
    private static final long serialVersionUID = 1L;

    AlteraAtributosProcDetail _parent = null;

    // ordered list with FormButton objects
    private ArrayList<FormButton> _alButtons = new ArrayList<FormButton>();
    // button hashset 
    private HashSet<String> _hsButtons = new HashSet<String>();

    private JPanel _mainPanel = null;
    private GridBagLayout _gridbag;
    private GridBagConstraints _c;

    public JPreviewFormButtons(AlteraAtributosProcDetail aParent, List<Atributo> alAttributes) {
      super(new BorderLayout());

      this.setParent(aParent);

      this.setBackground(AlteraAtributosProcDetail.cBG_COLOR);

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

        sPrefix = AlteraAtributosProcDetail.sBUTTON_ATTR_PREFIX + fb.getPosition() + "_"; //$NON-NLS-1$

        retObj[j][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_ID;
        retObj[j][1] = String.valueOf(fb.getID());

        retObj[j+1][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_POSITION;
        retObj[j+1][1] = String.valueOf(fb.getPosition());

        retObj[j+2][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_TYPE;
        retObj[j+2][1] = fb.getType();

        stmp = fb.getText();
        if (stmp == null) {
          stmp = ""; //$NON-NLS-1$
        }
        else if (stmp.equals(sCHOOSE)) {
          stmp = (String)hmBUTTON_TYPES.get((String)retObj[j+2][1]);
        }

        retObj[j+3][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_TEXT;
        retObj[j+3][1] = stmp;

        retObj[j+4][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_TOOLTIP;
        retObj[j+4][1] = fb.getToolTip();
        if (retObj[j+4][1] == null) retObj[j+4][1] = ""; //$NON-NLS-1$

        retObj[j+5][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_IMAGE;
        retObj[j+5][1] = fb.getImage();
        if (retObj[j+5][1] == null) retObj[j+5][1] = ""; //$NON-NLS-1$

        retObj[j+6][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_CUSTOM_VAR;
        retObj[j+6][1] = fb.getCustomVar();
        if (retObj[j+6][1] == null) retObj[j+6][1] = ""; //$NON-NLS-1$

        retObj[j+7][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_CUSTOM_VALUE;
        retObj[j+7][1] = fb.getCustomValue();
        if (retObj[j+7][1] == null) retObj[j+7][1] = ""; //$NON-NLS-1$

        retObj[j+8][0] = sPrefix + AlteraAtributosProcDetail.sBUTTON_ATTR_SHOW_COND;
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
      Map<String,FormButton> hm = new TreeMap<String, FormButton>(); // order buttuns by position

      for (int i=0;i<lAttributes.size(); i++) {
        sName = lAttributes.get(i).getDescricao();
        sVal = lAttributes.get(i).getValor();

        if (!sName.startsWith(AlteraAtributosProcDetail.sBUTTON_ATTR_PREFIX)) {
          continue;
        }

        sPos = sName.substring(AlteraAtributosProcDetail.sBUTTON_ATTR_PREFIX.length());
        ntmp = sPos.indexOf("_"); //$NON-NLS-1$
        sName = sPos.substring(ntmp+1);
        sPos = sPos.substring(0,ntmp);

        if (hm.containsKey(sPos)) {
          fb = hm.get(sPos);
        }
        else {
          fb = new FormButton(this);
        }

        if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_ID)) {
          try {
            fb.setID(Integer.parseInt(sVal));
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_POSITION)) {
          try {
            fb.setPosition(Integer.parseInt(sVal));
          }
          catch (Exception e) {
          }
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_TYPE)) {
          fb.setType(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_TEXT)) {
          fb.setText(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_TOOLTIP)) {
          fb.setToolTip(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_IMAGE)) {
          fb.setImage(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_CUSTOM_VAR)) {
          fb.setCustomVar(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_CUSTOM_VALUE)) {
          fb.setCustomValue(sVal);
        }
        else if (sName.equals(AlteraAtributosProcDetail.sBUTTON_ATTR_SHOW_COND)) {
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
      if (sType != null) {
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
      if (sType != null) {
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
      this._mainPanel.setBackground(AlteraAtributosProcDetail.cBG_COLOR);

      this._gridbag = new GridBagLayout();
      this._c = new GridBagConstraints();
      this._c.fill = GridBagConstraints.HORIZONTAL;
      this._c.weightx = 1.0;
      this._c.gridwidth = 1;
      this._c.ipady = nPad;

      this._mainPanel.setLayout(this._gridbag);
      this._mainPanel.setBackground(AlteraAtributosProcDetail.cBG_COLOR);

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
        sizer.setBackground(AlteraAtributosProcDetail.cBG_COLOR);
        sizer.setSize(nSpacerWidth,nPanelHeight);
        this._gridbag.setConstraints(sizer,this._c);
        this._mainPanel.add(sizer);

        this._gridbag.setConstraints(fb,this._c);
        this._mainPanel.add(fb);

        sizer = new JPanel();
        sizer.setBackground(AlteraAtributosProcDetail.cBG_COLOR);
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

    public void setParent (AlteraAtributosProcDetail aParent) {
      this._parent = aParent;
    }

  } // class JPreviewFormButtons

  private String adapterGetString(String msg) {
    return adapter.getString(msg);
  }
  
  private void adapterShowError(String msg) {
    adapter.showError(msg);
  }
  
  private JButton makeButton(String asImageIconName,
      String asText,
      String asToolTipText,
      final String asActionCommandPrefix,
      Dimension adButtonDimension,
      int position,
      final JPreviewFormButtons _parent
  ) {

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
    if(null != asToolTipText)
      ret.setToolTipText(adapter.getString(asToolTipText));
    ret.setActionCommand(asActionCommandPrefix + position);

    ret.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String sActionCom = e.getActionCommand();
        String sButtonPos = null;

        if (asActionCommandPrefix
            .equals(AlteraAtributosProcDetail.sBUTTON_MOVE_RIGHT)) {
          sButtonPos = sActionCom
          .substring(AlteraAtributosProcDetail.sBUTTON_MOVE_RIGHT.length());
          _parent.rightFormButton(Integer.parseInt(sButtonPos));
        }
        else if (asActionCommandPrefix
            .equals(AlteraAtributosProcDetail.sBUTTON_MOVE_LEFT)) {
          sButtonPos = sActionCom
          .substring(AlteraAtributosProcDetail.sBUTTON_MOVE_LEFT.length());
          _parent.leftFormButton(Integer.parseInt(sButtonPos));
        }
        else if (asActionCommandPrefix
            .equals(AlteraAtributosProcDetail.sBUTTON_REMOVE)) {
          sButtonPos = sActionCom
          .substring(AlteraAtributosProcDetail.sBUTTON_REMOVE.length());
          _parent.removeFormButton(Integer.parseInt(sButtonPos));
        }
        else if (asActionCommandPrefix
            .equals(AlteraAtributosProcDetail.sBUTTON_ADD_AT)) {
          sButtonPos = sActionCom
          .substring(AlteraAtributosProcDetail.sBUTTON_ADD_AT.length());
          _parent.addFormButtonAt(Integer.parseInt(sButtonPos));
        }
        else if (asActionCommandPrefix
            .equals(AlteraAtributosProcDetail.sBUTTON_EDIT_PREFIX)) {
          sButtonPos = sActionCom
          .substring(AlteraAtributosProcDetail.sBUTTON_EDIT_PREFIX.length());
          _parent.editFormButton(Integer.parseInt(sButtonPos),false);
        }
      }
    });

    return ret;
  }



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


    // default constructor
    public FormButton(JPreviewFormButtons aParent) {
      super(new BorderLayout());
      this.setBackground(AlteraAtributosProcDetail.cBG_COLOR);

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
      this._mainPanel.setBackground(AlteraAtributosProcDetail.cBG_COLOR);

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
      this._jbMoveLeft = makeButton("left.gif", null, "AlteraAtributosJSP.button.move_button_left", //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosProcDetail.sBUTTON_MOVE_LEFT, dBig, this.getPosition(), _parent);
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
      this._jbAdd = makeButton("addButton.gif", null, "AlteraAtributosJSP.button.add_button", //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosProcDetail.sBUTTON_ADD_AT, dSmall, getPosition(), _parent);
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
      this._jbMoveRight = makeButton("right.gif", null, "AlteraAtributosJSP.button.move_button_right", //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosProcDetail.sBUTTON_MOVE_RIGHT, dBig, getPosition(), _parent);
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
      this._jbRemove = makeButton("removeButton.gif", null, "AlteraAtributosJSP.button.remove_button", //$NON-NLS-1$ //$NON-NLS-2$
          AlteraAtributosProcDetail.sBUTTON_REMOVE, dSmall, getPosition(), _parent);
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

      this._jbText = makeButton(null, this.getText(), "AlteraAtributosJSP.button.edit_button", //$NON-NLS-1$
          AlteraAtributosProcDetail.sBUTTON_EDIT_PREFIX,null, getPosition(), _parent);
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
        this.setTitle(adapterGetString("Common.add")); //$NON-NLS-1$
      }
      else {
        this.setTitle(adapterGetString("Common.edit")); //$NON-NLS-1$
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
      this.jButtonOk.setText(adapterGetString("Common.ok")); //$NON-NLS-1$
      this.jButtonOk.setEnabled(false);  // only enable after field type select
      this.jButtonOk.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e) {
          saveData();
        }
      });

      this.jButtonCancel = new JButton();
      this.jButtonCancel.setText(adapterGetString("Common.cancel")); //$NON-NLS-1$
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
        adapterShowError(adapterGetString("AlteraAtributosJSP.error.missing_button_text"));//$NON-NLS-1$
        return;
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
        jtfText.setToolTipText(adapterGetString("AlteraAtributosJSP.button.text.tooltip")); //$NON-NLS-1$
        JLabel jlTextLabel = new JLabel(adapterGetString("AlteraAtributosJSP.text") + asTypeText); //$NON-NLS-1$
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
        jtfToolTip.setToolTipText(adapterGetString("AlteraAtributosJSP.button.tooltip")); //$NON-NLS-1$
        JLabel jlToolTipLabel = new JLabel(adapterGetString("AlteraAtributosJSP.tooltip") + asTypeText); //$NON-NLS-1$
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
        jtfImage.setToolTipText(adapterGetString("AlteraAtributosJSP.button.image.tooltip")); //$NON-NLS-1$
        JLabel jlImageLabel = new JLabel(adapterGetString("AlteraAtributosJSP.image") + asTypeText); //$NON-NLS-1$
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
        this.hmComponents.put(sType,hmtmp);
      }

      return retObj;
    }

  } // class FormButtonEditor

}


