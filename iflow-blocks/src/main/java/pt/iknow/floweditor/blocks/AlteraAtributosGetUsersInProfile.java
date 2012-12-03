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

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributosGetUsersInProfile
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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosGetUsersInProfile extends AlteraAtributos {

  private static final long serialVersionUID = 8318502608835333229L;

  /* AlteraAtributos */
  private final String[] AlteraAtributosColumnNames;

  public final static String sPROFILE_FIELD = "_profilefield_"; //$NON-NLS-1$

  private JTextField _jtfDescription = new JTextField(20);
  private JTextField _jtfResult = new JTextField(20);
  private JTextField _jtfProfile = new JTextField(20);

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;

  List<String> extraProperties = new ArrayList<String>();

  MyJTableX jTable1 = new MyJTableX();

  public AlteraAtributosGetUsersInProfile(FlowEditorAdapter janela) {
    super(janela);

    AlteraAtributosColumnNames = new String[] {
        adapter.getString("AlteraAtributosGetUsersInProfile.columnlabels.name"),  //$NON-NLS-1$
        adapter.getString("AlteraAtributosGetUsersInProfile.columnlabels.var"), //$NON-NLS-1$
    };

  }


  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    if (data == null)
      return null;

    String[][] newdata = new String[data.length + 3][2];

    for (int i = 0; i < data.length; i++) {
      newdata[i][0] = data[i][0];
      newdata[i][1] = data[i][1];
    }

    // Profile
    newdata[data.length][0] = sPROFILE_FIELD;
    newdata[data.length][1] = this._jtfProfile.getText();

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
    String stmp = null;
    String stmp2 = null;
    boolean bProfile = false;
    boolean bDesc = false;
    boolean bRes = false;
    String sProfile = null;

    atr = (Atributo) atributos.get(atributos.size() - 3);
    if (atr != null) {
      stmp = atr.getNome();
      if (atr.getNome() != null && atr.getNome().equals(sPROFILE_FIELD)) {
        sProfile = atr.getValor();
        if (sProfile == null)
          sProfile = ""; //$NON-NLS-1$
        this._jtfProfile.setText(new String(sProfile));
        bProfile = true;
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

    if (!bProfile) {
      atr = adapter.newAtributo(sPROFILE_FIELD, "", sPROFILE_FIELD); //$NON-NLS-1$
      atributos.add(atributos.size() - 2, atr);
    }

    RepositoryClient rep = adapter.getRepository();
    if (rep != null && (extraProperties == null || extraProperties.size() == 0)) {
      String[] extrasList = rep.listExtraProperties();
      if (extrasList != null) {
        for (String atrName : extrasList) {
          boolean exists = false;
          for (Atributo atrib : atributos) {
            if (atrName.equals(atrib.getNome())) {
              exists = true;
              break;
            }
          }
          if (!exists) extraProperties.add(atrName);
        }
      }
    }
    
    int nAtributos = atributos.size() - 3;
    int nExtras = extraProperties.size();

    data = new String[nAtributos + nExtras][2];

    for (int i = 0; i < nAtributos; i++) {
      atr = (Atributo) atributos.get(i);
      data[i][0] = new String(atr.getNome());
      data[i][1] = new String(atr.getValor());
    }

    for (int i = 0; i < nExtras; i++) {
      data[i+nAtributos][0] = extraProperties.get(i);
      data[i+nAtributos][1] = "";
    }

    jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);
    MyTableModel tableModel = new MyTableModel(AlteraAtributosColumnNames, data);
    tableModel.setColumnEditable(0, false);
    jTable1.setModel(tableModel);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);


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
    String stmp = adapter.getString("AlteraAtributosGetUsersInProfile.description"); //$NON-NLS-1$
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
    stmp = adapter.getString("AlteraAtributosGetUsersInProfile.result.description"); //$NON-NLS-1$
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

    // PROFILE
    jLabel = null;
    stmp = adapter.getString("AlteraAtributosGetUsersInProfile.profile"); //$NON-NLS-1$
    jLabel = new JLabel(stmp);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(this._jtfProfile);
    sGridbag.setConstraints(jLabel, sC);
    jPanel.add(jLabel);
    // separator
    sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(this._jtfProfile, sC);
    jPanel.add(this._jtfProfile);
    sC.gridwidth = 1;


    /* table */
    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    JPanel jtmp = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    jtmp.setLayout(gridbag);

    // DESCRIPTION, RESULT, PROFILE
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
