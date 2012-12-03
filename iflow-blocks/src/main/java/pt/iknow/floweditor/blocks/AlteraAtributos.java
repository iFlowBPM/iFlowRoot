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
 *  class: AlteraAtributos
 *
 *  desc: dialogo para alterar atributos de um bloco
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.msg.IMessages;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributos extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 4245719994156479953L;

  /* AlteraAtributos */
  protected final String[] AlteraAtributosColumnNames;

  protected JPanel panel1 = new JPanel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JButton okButton = new JButton();
  protected JButton cancelButton = new JButton();
  protected JPanel jPanel3 = new JPanel();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected MyJTableX jTable1 = new MyJTableX();

  protected JTextField _jtfDescription = new JTextField(20);
  protected JTextField _jtfResult = new JTextField(20);

  // check if match with ones in Uniflow's pt.iflow.blocks.Block
  public final static String sDESCRIPTION = "block_description"; //$NON-NLS-1$
  public final static String sRESULT = "block_result"; //$NON-NLS-1$

  protected int exitStatus = EXIT_STATUS_CANCEL;
  protected String[][] data;

  public AlteraAtributos(FlowEditorAdapter adapter) {
    super(adapter, true);
    
    /* AlteraAtributos */
    AlteraAtributosColumnNames = new String[]{ adapter.getString("AlteraAtributos.column.name"), adapter.getString("AlteraAtributos.column.value") };
    
    
  }


  public int getExitStatus() {
    return exitStatus;
  }

  public String[][] getNewAttributes() {
    if (data == null)
      return null;

    String[][] newdata = new String[data.length + 2][2];
    for (int i = 0; i < data.length; i++) {
      newdata[i][0] = data[i][0];
      newdata[i][1] = data[i][1];
    }

    // now description and result
    newdata[data.length][0] = AlteraAtributos.sDESCRIPTION;
    newdata[data.length][1] = this._jtfDescription.getText();
    newdata[data.length + 1][0] = AlteraAtributos.sRESULT;
    newdata[data.length + 1][1] = this._jtfResult.getText();

    return newdata;
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);

    Atributo atr = null;
    String stmp = null;
    String stmp2 = null;
    boolean bDesc = false;
    boolean bRes = false;

    // check if atributos already has description and result
    if (atributos != null && atributos.size() > 1)
      atr = (Atributo) atributos.get(atributos.size() - 2);
    if (atr != null) {
      stmp = atr.getDescricao();
      if (stmp != null && stmp.equals(AlteraAtributos.sDESCRIPTION)) {
        stmp2 = atr.getValor();
        if (stmp2 == null)
          stmp2 = "";
        this._jtfDescription.setText(stmp2);
        bDesc = true;
      }
    }

    if (atributos != null && atributos.size() > 0)
      atr = (Atributo) atributos.get(atributos.size() - 1);
    if (atr != null) {
      stmp = atr.getDescricao();
      if (stmp != null && stmp.equals(AlteraAtributos.sRESULT)) {
        stmp2 = atr.getValor();
        if (stmp2 == null)
          stmp2 = "";
        this._jtfResult.setText(stmp2);
        bRes = true;
      }
    }

    if (!bDesc && !bRes) {
      atr = adapter.newAtributo(AlteraAtributos.sDESCRIPTION, "", AlteraAtributos.sDESCRIPTION);
      atributos.add(atr);
      atr = adapter.newAtributo(AlteraAtributos.sRESULT, "", AlteraAtributos.sRESULT);
      atributos.add(atr);
    } else if (!bDesc) {
      atr = adapter.newAtributo(AlteraAtributos.sDESCRIPTION, "", AlteraAtributos.sDESCRIPTION);
      atributos.set(atributos.size() - 1, atr);
    } else if (!bRes) {
      atr = adapter.newAtributo(AlteraAtributos.sRESULT, "", AlteraAtributos.sRESULT);
      atributos.add(atr);
    }

    data = new String[atributos.size() - 2][3];

    IMessages msg = adapter.getBlockMessages();
    
    for (int i = 0; i < atributos.size() - 2; i++) {
      data[i][0] = atributos.get(i).getNome();
      data[i][1] = atributos.get(i).getValor();
      data[i][2] = atributos.get(i).getDescricao();
      if(StringUtils.isNotBlank(adapter.getBlockKey())) {
        String i18nKey = adapter.getBlockKey()+".attribute."+atributos.get(i).getNome();
        if(msg.hasKey(i18nKey))
          data[i][2] = msg.getString(i18nKey);
      }
    }

    jTable1 = new MyJTableX(parseData(), AlteraAtributosColumnNames);

    customizeTable(atributos);

    /* criar botões e arranjar dialogo */
    jbInit();

    this.setSize(500, 680);
    setVisible(true);

  }
  
  String[][] parseData() {
    String[][] parsedData = new String[data.length][2];
    for (int i = 0, lim = data.length; i < lim; i++) {
      parsedData[i][0] = data[i][2];
      parsedData[i][1] = data[i][1];
    }
    return parsedData;
  }
  
  void customizeTable(List<Atributo> atributos) {
    /* table model -> can not edit 1st collunn */
    MyTableModel tableModel = new MyTableModel(AlteraAtributosColumnNames, parseData());
    tableModel.setColumnEditable(0, false);
    jTable1.setModel(tableModel);

    jTable1.setRowSelectionAllowed(false);
    jTable1.setColumnSelectionAllowed(false);

    MyColumnEditorModel cm = new MyColumnEditorModel();
    jTable1.setMyColumnEditorModel(cm);

    /* Combo boxes e text fields */
    for (int x = 0; x < atributos.size(); x++) {
      Atributo at = atributos.get(x);
      if (at.getValoresTipo() != null && at.getValoresTipo().length > 0) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(at.getValoresTipo()));
      }
    }
  }

  /**
   * Iniciar caixa de diálogo
   * 
   * @throws Exception
   */
  void jbInit() {
    // configurar
    panel1.setLayout(borderLayout1);

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
    String stmp = adapter.getString("AlteraAtributos.descriptionLabel");//"Descrição";
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
    stmp = adapter.getString("AlteraAtributos.descriptionResultLabel");//"Descrição do Resultado";
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
    for (int i = 0; i < jTable1.getRowCount(); i++) {
      data[i][1] = jTable1.getStringAt(i, 1);
    }

    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void cancelButtonActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  protected void stopEditing(JTable table) {
    if (table != null) {
      CellEditor currentEditor = table.getCellEditor();
      if (currentEditor != null) {
        currentEditor.stopCellEditing();
      }
    }
  }
}
