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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DBTable;
import pt.iflow.api.db.DBTableHelper;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.xml.DBTableMarshaller;
import pt.iflow.blocks.BlockSQL;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosSQL extends AlteraAtributos {
  private static final long serialVersionUID = 7353724963450325257L;

  private static final String sWIZARD = "wizard";
  private static final String sWIZARD_SEPARATOR = "@";
  private static final String advancedQuery = "advancedQuery";
  protected static final String sJNDIName = "JNDIName";
  protected static final String sUnitResult = "ResultadoUnitario";

  private JTabbedPane jTabbedPane = new JTabbedPane();
  private JComboBox jcbDataSources;
  private JTextArea jtfQuery = new JTextArea();
  private JTable descTable = new JTable();;
  private JScrollPane jWizardPanel = new JScrollPane();
  private boolean isWizard = false;

  public AlteraAtributosSQL(FlowEditorAdapter adapter) {
    super(adapter);
    initJNDIComboBox();
  }

  private static String [] fixNames(String [] array) {
    if(null == array) {
      return new String[] {};
    }
    for(int  i = 0; i < array.length; i++) {
      array[i] = "\""+array[i]+"\"";
    }
    return array;
  }

  void customizeTable(List<Atributo> atributos) {
    atributos = parseAtributos(atributos);
    super.customizeTable(atributos);
    // update editor for datasource
    MyColumnEditorModel cm = jTable1.getMyColumnEditorModel();

    //TODO
    String [] dataUnit = new String[2];
    dataUnit[0] = adapter.getString("AlteraAtributosSQL.resultadoUnitario.no");
    dataUnit[1] = adapter.getString("AlteraAtributosSQL.resultadoUnitario.yes");

    for (int x = 0; x < atributos.size(); x++) {
      Atributo at = atributos.get(x);
      //	  if (sJNDIName.equals(at.getNome())) {
      //	    cm.addEditorForCell(x, 1, new ComboCellEditor(dataSources, true));
      //	  }
      if (sUnitResult.equals(at.getNome())) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(dataUnit, true));
      }
    }
  }

  //TODO
  /**
   * jbInit
   */
  void jbInit() {

    super.jbInit();
    getContentPane().remove(jScrollPane1);

    JPanel jHeaderPanel = new JPanel(new BorderLayout());
    JLabel dsLabel = new JLabel(sJNDIName + ": ");
    dsLabel.setLabelFor(jcbDataSources);
    dsLabel.setHorizontalAlignment(JLabel.LEFT);
    jHeaderPanel.add(dsLabel, BorderLayout.WEST);
    jHeaderPanel.add(jcbDataSources, BorderLayout.CENTER);
    getContentPane().add(jHeaderPanel, BorderLayout.NORTH);

    if (isWizard) {
      getContentPane().add(getWizardPanel(), BorderLayout.CENTER);      
    }
    else {
      jTabbedPane.addTab(adapter.getString("AlteraAtributosSQL.flow.wizard.tab.label"), getWizardPanel()); 
      JPanel jPanelAvancado = new JPanel(new BorderLayout());
      JLabel jLabel = new JLabel(adapter.getString("AlteraAtributosSQL.avancado.query"));
      jLabel.setHorizontalAlignment(JLabel.LEFT);
      jLabel.setLabelFor(jtfQuery);
      jPanelAvancado.add(jLabel, BorderLayout.NORTH);
      // separator
      JPanel sizer = new JPanel();
      sizer.setSize(5, 1); 

      JScrollPane scroll = new JScrollPane(jtfQuery);
      jPanelAvancado.add(scroll, BorderLayout.CENTER);

      jTabbedPane.addTab(adapter.getString("AlteraAtributosSQL.flow.avancado.tab.label"), jPanelAvancado);
      getContentPane().add(jTabbedPane, BorderLayout.CENTER);
    }
  }
  
  public void setDataIn(String title, List<Atributo> atributos){
    atributos = parseAtributos(atributos);
    List<Atributo> myAttrs = new ArrayList<Atributo>();
    for (Atributo a : atributos) {
      myAttrs.add(a.cloneAtributo());
    }

    if(myAttrs != null){
      Atributo atributoQuery = null;
      for(Atributo a : myAttrs){
        if(a == null)
          continue;

        if(a.getNome().equals(advancedQuery)){
          jtfQuery.setText(a.getValor());
          atributoQuery = a;
          break;
        }
      }
      if(atributoQuery != null ) {
        myAttrs.remove(atributoQuery);
      }
    } 
    super.setDataIn(title, myAttrs);
  }

  public String[][] getNewAttributes() {
    String[][] atributos = super.getNewAttributes();
    List<String[]> extra = this.getExtra();

    String[][] newAtributos = new String[atributos.length + extra.size()][];
    System.arraycopy(atributos, 0, newAtributos, 0, atributos.length);
    
//    newAtributos[atributos.length] = newAtributos[atributos.length-1];
//    newAtributos[atributos.length-1] = newAtributos[atributos.length-2];
    for (int i = 0; i < extra.size(); i++) {
      String[] item = extra.get(i);
      newAtributos[atributos.length + i] = item;
    }
    
    return newAtributos;
  }
  
  private List<String[]> getExtra() {
    List<String[]> retObj = new ArrayList<String[]>();
    
    String[] aux = new String[2];
    aux[0] = advancedQuery;
    aux[1] = jtfQuery.getText();
    retObj.add(aux);
    
    String[] jndi = new String[2];
    jndi[0] = sJNDIName;
    jndi[1] = (String) jcbDataSources.getSelectedItem();
    retObj.add(jndi);

    String[] wizard = new String[2];
    wizard[0] = sWIZARD;
    wizard[1] = "" + (this.isWizard ? 1 : 0);
    retObj.add(wizard);
    
    if (this.isWizard && this.descTable != null) {
      for (int i = 0; i < this.descTable.getRowCount(); i++) {
        String field = (String) this.descTable.getValueAt(i, 0);
        String type = (String) this.descTable.getValueAt(i, 1);
        String value = (String) this.descTable.getValueAt(i, 2);
        String sql = (String) this.descTable.getValueAt(i, 3);
        String cond = (String) this.descTable.getValueAt(i, 4);

        String[] wizardField = new String[2];
        wizardField[0] = sWIZARD + sWIZARD_SEPARATOR + DBTable.FIELD + sWIZARD_SEPARATOR + i;
        wizardField[1] = field;
        retObj.add(wizardField);
        
        String[] wizardType = new String[2];
        wizardType[0] = sWIZARD + sWIZARD_SEPARATOR + DBTable.TYPE + sWIZARD_SEPARATOR + i;
        wizardType[1] = type;
        retObj.add(wizardType);
        
        String[] wizardValue = new String[2];
        wizardValue[0] = sWIZARD + sWIZARD_SEPARATOR + DBTable.VALUE + sWIZARD_SEPARATOR + i;
        wizardValue[1] = value;
        retObj.add(wizardValue);
        
        String[] wizardSQL = new String[2];
        wizardSQL[0] = sWIZARD + sWIZARD_SEPARATOR + DBTable.SQL + sWIZARD_SEPARATOR + i;
        wizardSQL[1] = sql;
        retObj.add(wizardSQL);
        
        String[] wizardCond = new String[2];
        wizardCond[0] = sWIZARD + sWIZARD_SEPARATOR + DBTable.COND + sWIZARD_SEPARATOR + i;
        wizardCond[1] = "" + (Utils.string2bool(cond) ? 1 : 0);
        retObj.add(wizardCond);
      }
    }
    
    return retObj;
  }
  
  private List<Atributo> parseAtributos(List<Atributo> atributos) {
    DBTable virtualDBTable = null;
    // remover sJNDIName
    List<Atributo> retObj = new ArrayList<Atributo>();
    DBTableHelper.clearCache();
    for (Atributo at : atributos) {
      if (StringUtils.equalsIgnoreCase(sJNDIName, at.getNome())) {
        String valor = at.getValor();
        boolean found = false;
        for (int i = 0; i < jcbDataSources.getItemCount(); i++) {
          String item = (String) jcbDataSources.getItemAt(i);
          if (StringUtils.equals(valor, item)) {
            jcbDataSources.setSelectedIndex(i);
            found = true;
            break;
          }
        }
        if (!found) {
          jcbDataSources.setModel(new DefaultComboBoxModel(prepareJNDIComboBox(valor)));
        }
      } else if (StringUtils.equalsIgnoreCase(sWIZARD, at.getNome())) {
        isWizard = Utils.string2bool(at.getValor());
      } else if (StringUtils.startsWithIgnoreCase(at.getNome(), sWIZARD + sWIZARD_SEPARATOR)) {
        // wizard props
        if (virtualDBTable == null) {
          virtualDBTable = new DBTable();
        }
        String name = at.getNome().split(sWIZARD_SEPARATOR)[1];
        String value = at.getValor();
        int pos = -1;
        try {
          pos = Integer.parseInt(at.getNome().split(sWIZARD_SEPARATOR)[2]);
        } catch (Exception ex) {
        }
        DBTableHelper.addItem(virtualDBTable, name, value, pos);
      } else {
        retObj.add(at);
      }
    }
    DBTableHelper.clearCache();
    if (virtualDBTable != null) {
      updateDBTable(virtualDBTable);
    }
    return retObj;
  }
  
  private Object[] prepareJNDIComboBox(String defVal) {
    Object[] newValues = null;
    String[] values = getDataSources();
    if (values != null) {
      newValues = new Object[values.length+1];
      System.arraycopy(values, 0, newValues, 1, values.length);
      newValues[0] = ((StringUtils.isBlank(defVal)) ? "" : defVal);
    }
    return newValues;
  }

  private String[] getDataSources() {
    String[] retObj;
    try {
      retObj = fixNames(adapter.getRepository().listDataSources());
    } catch (NullPointerException e) {
      retObj = new String[] {};
      adapter.log("Null DataSources");
    }
    return retObj;
  }

  private void initJNDIComboBox() {
    if (jcbDataSources == null) {
      jcbDataSources = new JComboBox(prepareJNDIComboBox(null));
      jcbDataSources.setEditable(true);
      jcbDataSources.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          int idx = jcbDataSources.getSelectedIndex();
          if(idx < 0) { // user typed this entry
            Object obj = jcbDataSources.getSelectedItem();
            final int pos = 0;
            jcbDataSources.removeItemAt(pos);
            jcbDataSources.insertItemAt(obj, pos);
            jcbDataSources.setSelectedIndex(pos);
          }
        }
      });
    }
  }
  
  private JComponent getWizardPanel() {
    JComponent retObj = null;
    if (isWizard) {
      final FlowEditorAdapter fAdapter = adapter;
      JButton descButton = new JButton();
      descButton.setToolTipText(adapter.getString("AlteraAtributosSQL.wizard.desc.label"));
      descButton.setText("Go!");
      descButton.addMouseListener(new MouseListener(){
        public void mouseReleased(MouseEvent e) {
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseClicked(MouseEvent e) {
          stopEditing(jTable1);
          stopEditing(descTable);
          if (descTable != null && descTable.getRowCount() > 0
              && !createDialog(true, fAdapter.getString("AlteraAtributosSQL.dialog.clear.title"), fAdapter.getString("AlteraAtributosSQL.dialog.clear.message"), JOptionPane.QUESTION_MESSAGE)) {
            return;
          }
          try {
            String jndiName = "";
            if (jcbDataSources.getSelectedItem() != null) {
              jndiName = (String) jcbDataSources.getSelectedItem();
            }
            String table = "";
            for (int i = 0; i < jTable1.getRowCount(); i++) {
              String name = (String) jTable1.getValueAt(i, 0);
              if (StringUtils.equals(name, BlockSQL.sTABLE) || StringUtils.equals(name, "Table")) {
                table = (String) jTable1.getValueAt(i, 1);
                break;
              }
            }
            if (StringUtils.isBlank(table)) {
              createDialog(false, fAdapter.getString("AlteraAtributosSQL.error.con.title"), fAdapter.getString("AlteraAtributosSQL.error.con.message"), JOptionPane.WARNING_MESSAGE);
            } else {
              String myJndi = jndiName;
              if (StringUtils.isNotEmpty(myJndi) && myJndi.startsWith("\"") && myJndi.endsWith("\"")) {
                myJndi = myJndi.substring(1, myJndi.length()-1);
              }              
              updateDBTable(DBTableMarshaller.unmarshal(fAdapter.getRepository().getTableDesc(myJndi, table)));
            }
          } catch (Exception ex) {
            String errorMsg = fAdapter.getString("AlteraAtributosSQL.error.title") + ": "
                + fAdapter.getString("AlteraAtributosSQL.error.message");
            fAdapter.log(errorMsg, ex);
            ex.printStackTrace();
          }
        }
      });
      JScrollPane descTablePanel = new JScrollPane();
      descTablePanel.getViewport().add(descTable.getTableHeader());
      descTablePanel.getViewport().add(descTable);
      
      JPanel descButtonPanel = new JPanel();
      descButtonPanel.add(descButton);
      
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(descButtonPanel, BorderLayout.NORTH);
      mainPanel.add(descTablePanel, BorderLayout.CENTER);
      
      JPanel jPanel = new JPanel(new BorderLayout());
      jPanel.add(jScrollPane1, BorderLayout.NORTH);
      jPanel.add(mainPanel, BorderLayout.CENTER);
      jWizardPanel.getViewport().add(jPanel);
      retObj = this.jWizardPanel;
    } else {
      retObj = this.jScrollPane1;
    }
    return retObj;
  }
  
  // FIXME show checkboxes
  private void updateDBTable(DBTable dbTable) {
    if (descTable == null) {
      descTable = new JTable();
    }
    Object[][] myData = new Object[0][];
    if (dbTable != null) {
      List<Object[]> list = new ArrayList<Object[]>();
      for (Map<String, String> column : DBTableHelper.getColumns(dbTable)) {
//        boolean isMandatory = !Utils.string2bool(column.get(DBTable.NULL)) && StringUtils.isBlank(column.get(DBTable.DEFAULT));
//        String field = column.get(DBTable.FIELD);
//        field = field + ((isMandatory && !field.contains(" *")) ? " *" : "");
        Object[] dt = new Object[5];
        dt[0] = column.get(DBTable.FIELD);
        dt[1] = column.get(DBTable.TYPE);
        dt[2] = column.get(DBTable.VALUE);
        dt[3] = "" + (Utils.string2bool(column.get(DBTable.SQL)) ? 1 : 0);
        dt[4] = "" + (Utils.string2bool(column.get(DBTable.COND)) ? 1 : 0);
        list.add(dt);
      }
      myData = new Object[list.size()][];
      for (int i = 0; i < list.size(); i++) {
        myData[i] = list.get(i);
      }
    }
    String[] colNames = new String[] { "Name", "Type", "Value", "SQL Value?", "Condition?" };
      MyTableModel model = new MyTableModel(colNames, myData);
      for (int row = 0; row < model.getRowCount(); row++) {
        model.setCellEditable(row, 0, false);
        model.setCellEditable(row, 1, false);
      }
      descTable.setModel(model);
    descTable.repaint();
  }
  
  private boolean createDialog(boolean isConfirmation, String title, String message, int messageType) {
    boolean ret = false;
    if (isConfirmation) {
      ret = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION));      
    } else {
      if (StringUtils.isBlank(title)) {
        JOptionPane.showMessageDialog(this, message);
      } else {
        JOptionPane.showMessageDialog(this, message, title, messageType);
      }
    }
    return ret;
  }
}
