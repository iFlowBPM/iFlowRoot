package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosGetLDAPInfo
 *
 *  desc: dialogo para alterar atributos de um bloco
 *        para obter informacao do LDAP
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosGetLDAPInfo extends AlteraAtributos {
  private static final long serialVersionUID = -8331326984195409476L;

  /* AlteraAtributos */
  private final String[] AlteraAtributosColumnNames;

  public final static String sINPUT_FIELD = "_inputfield_"; //$NON-NLS-1$
  public final static String sNOTDEF = ""; //$NON-NLS-1$
  public final static String sTRUE = "true"; //$NON-NLS-1$
  public final static String sFALSE = "false"; //$NON-NLS-1$

  private static final char cINPUT = 'I';
  private static final char cDEFAULT = 'D';
  private static final char cOUTPUT = 'O';

  private JTextField _jtfDescription = new JTextField(20);
  private JTextField _jtfResult = new JTextField(20);

  private HashSet<String> _hsInputFields = new HashSet<String>();

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;
  private String[] data2;

  List<String> extraProperties = new ArrayList<String>();

  MyJTableX jTable1 = new MyJTableX();

  public final static String[] saSTATES = { sTRUE, sFALSE };

  public AlteraAtributosGetLDAPInfo(FlowEditorAdapter adapter) {
    super(adapter);
    
    AlteraAtributosColumnNames = new String[] {
        adapter.getString("AlteraAtributosGetLDAPInfo.columnlabels.name"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosGetLDAPInfo.columnlabels.value"), //$NON-NLS-1$
        adapter.getString("AlteraAtributosGetLDAPInfo.columnlabels.input"), //$NON-NLS-1$
      };

  }


  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    if (data == null)
      return null;

    String stmp = null;
    String[][] newdata = new String[data.length + 3][2];

    for (int i = 0; i < data.length; i++) {
      newdata[i][0] = data2[i];
      newdata[i][1] = data[i][1];
    }

    // input field
    newdata[data.length][0] = sINPUT_FIELD;
    newdata[data.length][1] = ""; //$NON-NLS-1$

    for (int i = 0; i < data.length; i++) {
      stmp = data[i][2];

      if (stmp == null)
        continue;

      stmp = stmp.toLowerCase();

     // if (stmp.equals(sTRUE) || stmp.equals("1") || stmp.equals(adapter.getString("Common.yes"))) { //$NON-NLS-1$ //$NON-NLS-2$
       // newdata[data.length][1] = data2[i];
       // break;
     // }
    }

    // now description and result
    newdata[data.length + 1][0] = AlteraAtributos.sDESCRIPTION;
    newdata[data.length + 1][1] = this._jtfDescription.getText();
    newdata[data.length + 2][0] = AlteraAtributos.sRESULT;
    newdata[data.length + 2][1] = this._jtfResult.getText();

    return newdata;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    Atributo atr = null;
    Atributo atr2 = null;
    String stmp = null;
    String stmp2 = null;
    boolean bInput = false;
    boolean bDesc = false;
    boolean bRes = false;
    String sInput = null;
    int ntmp = 0;
    boolean bOldies = false;

    atr = (Atributo) atributos.get(atributos.size() - 3);
    if (atr != null) {
      stmp = atr.getNome();
      if (stmp != null && stmp.equals(sINPUT_FIELD)) {
        sInput = atr.getValor();
        if (sInput == null)
          sInput = ""; //$NON-NLS-1$
        bInput = true;
      }
    }

    // check if atributos already has description and result
    atr = (Atributo) atributos.get(atributos.size() - 2);
    if (atr != null) {
      stmp = atr.getDescricao();
      if (stmp != null && stmp.equals(AlteraAtributos.sDESCRIPTION)) {
        stmp2 = atr.getValor();
        if (stmp2 == null)
          stmp2 = ""; //$NON-NLS-1$
        this._jtfDescription.setText(new String(stmp2));
        bDesc = true;
      }
    }

    atr = (Atributo) atributos.get(atributos.size() - 1);
    if (atr != null) {
      stmp = atr.getDescricao();
      if (stmp != null && stmp.equals(AlteraAtributos.sRESULT)) {
        stmp2 = atr.getValor();
        if (stmp2 == null)
          stmp2 = ""; //$NON-NLS-1$
        this._jtfResult.setText(new String(stmp2));
        bRes = true;
      }
    }

    if (!bDesc && !bRes) {
      atr = adapter.newAtributo(AlteraAtributos.sDESCRIPTION, "", AlteraAtributos.sDESCRIPTION); //$NON-NLS-1$
      atributos.add(atr);
      atr = adapter.newAtributo(AlteraAtributos.sRESULT, "", AlteraAtributos.sRESULT); //$NON-NLS-1$
      atributos.add(atr);
    } else if (!bDesc) {
      atr = adapter.newAtributo(AlteraAtributos.sDESCRIPTION, "", AlteraAtributos.sDESCRIPTION); //$NON-NLS-1$
      atributos.add(atributos.size() - 1, atr);
    } else if (!bRes) {
      atr = adapter.newAtributo(AlteraAtributos.sRESULT, "", AlteraAtributos.sRESULT); //$NON-NLS-1$
      atributos.add(atr);
    }

    if (!bInput) {
      atr = adapter.newAtributo(sINPUT_FIELD, "", sINPUT_FIELD); //$NON-NLS-1$
      atributos.add(atributos.size() - 2, atr);
    }

    // add backward compatibility
    HashMap<String, Integer> hmAtt = new HashMap<String, Integer>();
    bOldies = false;
    for (int i = 0; i < atributos.size() - 3; i++) {
      atr = (Atributo) atributos.get(i);
      hmAtt.put(atr.getNome(), i);

      if (atr.getNome().charAt(0) != cINPUT && atr.getNome().charAt(0) != cOUTPUT) {
        bOldies = true;
      }
    }

    if (bOldies) {
      for (int i = 0; i < atributos.size() - 3; i++) {
        atr = (Atributo) atributos.get(i);

        if (atr == null)
          continue;

        stmp = null;

        if (atr.getNome().charAt(0) == cINPUT) {
          stmp = atr.getNome().substring(2);
        } else if (atr.getNome().charAt(0) == cOUTPUT) {
          stmp = atr.getNome().substring(1);
        } else {
          continue;
        }

        if (stmp == null)
          continue;

        if (hmAtt.containsKey(stmp)) {
          // old attribute... get value and reset
          ntmp = ((Integer) hmAtt.get(stmp)).intValue();
          atr2 = (Atributo) atributos.get(ntmp);

          if (atr2 == null)
            continue;

          atr.setValor(atr2.getValor());

          // update new attribute with old value
          atributos.set(i, atr);
          // reset old attribute
          atributos.set(ntmp, null);

        }
      }

      // clean/resize list
      for (int i = 0; true; i++) {
        if (i >= atributos.size())
          break;

        atr = (Atributo) atributos.get(i);

        if (atr == null) {
          atributos.remove(i);
          i--; // list size decremented; decrement counter
        }
      }
    }
    hmAtt = null;

    RepositoryClient rep = adapter.getRepository();
    if (rep != null && (extraProperties == null || extraProperties.size() == 0)) {
      String[] extrasList = rep.listExtraProperties();
      if (extrasList != null) {
        for (String atrName : extrasList) {
          boolean exists = false;
          for (Atributo atrib : atributos) {
            if ((cOUTPUT+atrName).equals(atrib.getNome()) || (cINPUT+atrName).equals(atrib.getNome())) {
              exists = true;
              break;
            }
          }
          if (!exists) extraProperties.add(cOUTPUT+atrName);
        }
      }
    }
    
    int nAtributos = atributos.size() - 3;
    int nExtras = extraProperties.size();

    data = new String[nAtributos + nExtras][3];
    data2 = new String[nAtributos + nExtras];

    for (int i = 0; i < nAtributos; i++) {
      atr = (Atributo) atributos.get(i);

      if (atr.getNome().charAt(0) == cINPUT) {
        stmp = atr.getNome().substring(2);
        _hsInputFields.add(stmp);
      } else if (atr.getNome().charAt(0) == cOUTPUT) {
        stmp = atr.getNome().substring(1);
      } else {
        continue;
      }

      data2[i] = new String(atr.getNome());

      data[i][0] = new String(stmp);
      data[i][1] = new String(atr.getValor());

      if (_hsInputFields.contains(stmp)) {
        data[i][2] = sFALSE;
        if (sInput != null) {
          if (sInput.equals(atr.getNome())) {
            data[i][2] = sTRUE;
          }
        } else {
          if (atr.getNome().charAt(1) == cDEFAULT) {
            data[i][2] = sTRUE;
          }
        }
      } else {
        data[i][2] = null;
      }

    }

    for (int i = 0; i < nExtras; i++) {
      stmp = extraProperties.get(i).substring(1);
      data2[i+nAtributos] = new String(extraProperties.get(i));
      data[i+nAtributos][0] = stmp;
      data[i+nAtributos][1] = "";
      data[i+nAtributos][2] = null;
    }

    jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);
    MyTableModel tableModel = new MyTableModel(AlteraAtributosColumnNames, data);
    tableModel.setColumnEditable(0, false);
    jTable1.setModel(tableModel);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel cm = new MyColumnEditorModel();

    stmp = null;
    for (int x = 0; x < data.length; x++) {
      if (data[x][2] != null) {
        cm.addEditorForCell(x, 2, new ComboCellEditor(saSTATES));
      }
    }

    jTable1.setMyColumnEditorModel(cm);


    /* criar botões e arranjar dialogo */
    try {
      jbInit();
      pack();

    } catch (Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(510, 300);
    setVisible(true);

  }

  /**
   * Iniciar caixa de diálogo
   * 
   * @throws Exception
   */
  void jbInit() {
    // configurar
    panel1.setLayout(borderLayout1);

    this.setSize(510, 300);

    /* botão OK */
    okButton.setText(OK);

    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });

    /* botão cancelar */
    cancelButton.setText(Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });

    // DESCRIPTION
    GridBagLayout sGridbag = new GridBagLayout();
    GridBagConstraints sC = new GridBagConstraints();
    sC.fill = GridBagConstraints.HORIZONTAL;
    JPanel jPanel = new JPanel();
    jPanel.setLayout(sGridbag);
    JLabel jLabel = null;
    String stmp = adapter.getString("AlteraAtributosGetLDAPInfo.description"); //$NON-NLS-1$
    jLabel = new JLabel(stmp);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(this._jtfDescription);
    sGridbag.setConstraints(jLabel, sC);
    jPanel.add(jLabel);
    // separator
    JPanel sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(this._jtfDescription, sC);
    jPanel.add(this._jtfDescription);
    sC.gridwidth = 1;

    // RESULT
    jLabel = null;
    stmp = adapter.getString("AlteraAtributosGetLDAPInfo.result.description"); //$NON-NLS-1$
    jLabel = new JLabel(stmp);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(this._jtfResult);
    sGridbag.setConstraints(jLabel, sC);
    jPanel.add(jLabel);
    // separator
    sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(this._jtfResult, sC);
    jPanel.add(this._jtfResult);
    sC.gridwidth = 1;

    /* table */
    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    JPanel jtmp = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    jtmp.setLayout(gridbag);

    // DESCRIPTION, RESULT
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(jPanel, c);
    jtmp.add(jPanel);

    sizer = new JPanel();
    sizer.setSize(1, 10);
    gridbag.setConstraints(sizer, c);
    jtmp.add(sizer);

    JTableHeader jth = jTable1.getTableHeader();
    gridbag.setConstraints(jth, c);
    jtmp.add(jth);
    gridbag.setConstraints(jTable1, c);
    jtmp.add(jTable1);

    JPanel fullPanel = new JPanel();
    fullPanel.add(jtmp, BorderLayout.NORTH);

    /* paineis */
    JPanel aux1 = new JPanel();
    aux1.setSize(100, 1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(100, 1);
    getContentPane().add(aux2, BorderLayout.EAST);

    jScrollPane1.getViewport().add(fullPanel, null);

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);

    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(okButton, null);
    jPanel2.add(cancelButton, null);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);

    repaint();
  }

  /* OK */
  void okButtonActionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void cancelButtonActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

}
