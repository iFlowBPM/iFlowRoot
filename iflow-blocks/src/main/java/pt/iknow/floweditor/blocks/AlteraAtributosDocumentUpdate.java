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
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosDocumentUpdate extends AlteraAtributosDMS {
  private static final long serialVersionUID = 9068546779821461222L;

  private JTable dataTable;
  private MyJTableX dynTable = new MyJTableX();
  private JCheckBox toggleLock;
  private JCheckBox toggleVersionable;

  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String AUTHOR = "author";
  private static final String VARIABLE = "variable";
  private static final String[] DATA_VARS = new String[] { TITLE, DESCRIPTION, AUTHOR, VARIABLE };

  private static final String LOCK = "lock";
  private static final String VERSIONABLE = "versionable";
  private static String[] EXTRA_VARS = new String[] { LOCK, VERSIONABLE, PATH };

  private static final String PROP_NAME_PREFIX = "name_";
  private static final String PROP_VALUE_PREFIX = "value_";

  private int exitStatus = EXIT_STATUS_CANCEL;

  //AUTHENTICATION
  JCheckBox jcbAuth = null;
  JTextField jtfUser = null;
  JLabel jLabelUser = null;
  JPasswordField jtfPass = null;
  JLabel jLabelPass = null;  
  private static final String[][] dataAUTH = new String[3][2];
  
  private static final String AUTHENTICATION = "u_AUTH";
  private static final String USER = "u_USER";
  private static final String PASSWORD = "u_PASS";
  
  public AlteraAtributosDocumentUpdate(FlowEditorAdapter adapter) {
    super(adapter, "");
    toggleLock = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.label.lock"));
    toggleVersionable = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.label.versionable"));
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);
    int condCount = 0;
    if (atributos != null) {
      for (Atributo atributo : atributos) {
        if (atributo != null && atributo.getNome().startsWith(PROP_NAME_PREFIX)) {
          condCount++;
        }
      }
    }
    if (data == null) {
      data = new Object[DATA_VARS.length + EXTRA_VARS.length + condCount][2];
    }
    if (atributos != null) {
      for (Atributo atributo : atributos) {
        boolean found = false;
        for (int i = 0, lim = DATA_VARS.length; i < lim; i++) {
          if (StringUtils.equalsIgnoreCase(atributo.getNome(), DATA_VARS[i])) {
            data[i][0] = atributo.getNome();
            data[i][1] = atributo.getValor();
            found = true;
            break;
          }
        }
        if (found) {
          continue;
        }
        for (int i = 0, lim = EXTRA_VARS.length; i < lim; i++) {
          if (StringUtils.equalsIgnoreCase(atributo.getNome(), EXTRA_VARS[i])) {
            data[DATA_VARS.length + i][0] = atributo.getNome();
            data[DATA_VARS.length + i][1] = atributo.getValor();
            found = true;
            break;
          }
        }
        if (!found && (atributo.getNome().startsWith(PROP_NAME_PREFIX) || atributo.getNome().startsWith(PROP_VALUE_PREFIX))) {
          int index;
          try {
            index = DATA_VARS.length + EXTRA_VARS.length;
            index += Integer.parseInt(atributo.getNome().split("_")[1]);
          } catch (NumberFormatException ex) {
            index = DATA_VARS.length + EXTRA_VARS.length;
          }
          if (atributo.getNome().startsWith(PROP_NAME_PREFIX)) {
            data[index][0] = atributo.getValor();
          } else if (atributo.getNome().startsWith(PROP_VALUE_PREFIX)) {
            data[index][1] = atributo.getValor();
          }
        }
      }
    }
    
    //AUTHENTICATION GET
    for (Atributo atributo : atributos) {
  	  if(atributo.getNome().equals(AUTHENTICATION)){
  		  dataAUTH[0][0] = AUTHENTICATION;
  		  dataAUTH[0][1] = atributo.getValor();
  	  }else if(atributo.getNome().equals(USER)){
  		  dataAUTH[1][0] = USER;
      	  dataAUTH[1][1] = atributo.getValor();   		  
  	  }else if (atributo.getNome().equals(PASSWORD)){
  		  dataAUTH[2][0] = PASSWORD;
      	  dataAUTH[2][1] = atributo.getValor();
  	  }
    }
    
    jbInit();
    pack();
    setSize(600, 400);
    setVisible(true);
  }

  public String[][] getNewAttributes() {
    List<String[]> retObj = new ArrayList<String[]>();
    for (int i = 0; i < data.length; i++) {
      boolean add = true;
      if (i < DATA_VARS.length) {
        data[i][0] = DATA_VARS[i];
      } else if (i < (DATA_VARS.length + EXTRA_VARS.length)) {
        data[i][0] = EXTRA_VARS[i - DATA_VARS.length];
      } else {
        String[] itemName = new String[] { PROP_NAME_PREFIX + (i - (DATA_VARS.length + EXTRA_VARS.length)), asString(data[i][0])};
        String[] itemValue = new String[] { PROP_VALUE_PREFIX + (i - (DATA_VARS.length + EXTRA_VARS.length)), asString(data[i][1])};
        if (StringUtils.isNotBlank("" + itemName[1]) && StringUtils.isNotBlank("" + itemValue[1])) {
          retObj.add(itemName);
          retObj.add(itemValue);
          add = false;
        }
      }
      if (StringUtils.equals("" + data[i][1], "null")) {
        data[i][1] = "";
      }
      if (add && StringUtils.isNotBlank("" + data[i][0]) && StringUtils.isNotBlank("" + data[i][1])) {
        retObj.add(asString(data[i]));
      }
    }
    
    //AUTHENTICATION SAVE
    retObj.add(new String [] {AUTHENTICATION,""+jcbAuth.isSelected()});
    retObj.add(new String [] {USER,jtfUser.getText()});
    retObj.add(new String [] {PASSWORD,jtfPass.getText()});
    
    return retObj.toArray(new String[retObj.size()][2]);
  }

  public int getExitStatus() {
    return exitStatus;
  }

  private void jbInit() {
    JPanel left = getTreePanel();
    JPanel right = prepareRightPanel();
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right), BorderLayout.CENTER);
    
    //Criar Interface para AUTHENTICATION -----------------------------------------------------------------
    JPanel authPanel = new JPanel(new BorderLayout());     //checkBox AUTHENTICATION
    jcbAuth = new JCheckBox();
    jcbAuth.setText(  adapter.getString("AlteraAtributosDocumentDMS.label.autenticacao"));
    authPanel.add(jcbAuth, BorderLayout.NORTH);
    jcbAuth.setSelected(Boolean.valueOf("" + dataAUTH[0][1]));
    addListenerAuth();
    
    JPanel aux = new JPanel(new BorderLayout());  			//USER AUTHENTICATION
    jtfUser = new JTextField(10);
    jLabelUser = new JLabel(  adapter.getString("AlteraAtributosDocumentDMS.label.user"));
	jLabelUser.setLabelFor(jtfUser);
	if(!Boolean.valueOf("" + dataAUTH[0][1])){
		jtfUser.setVisible(false);
		jLabelUser.setVisible(false);
	}
	jtfUser.setText(dataAUTH[1][1]);
	aux.add(jLabelUser, BorderLayout.WEST);
    aux.add(jtfUser, BorderLayout.EAST);
    authPanel.add(aux, BorderLayout.CENTER);
 
    aux = new JPanel(new BorderLayout()); 					//PASS AUTHENTICATION
    jtfPass = new JPasswordField(10);
    jLabelPass = new JLabel(  adapter.getString("AlteraAtributosDocumentDMS.label.password"));
    jLabelPass.setLabelFor(jtfPass);
	if(!Boolean.valueOf("" + dataAUTH[0][1])){
		jtfPass.setVisible(false);
		jLabelPass.setVisible(false);
	}
	jtfPass.setText(dataAUTH[2][1]);
    aux.add(jLabelPass, BorderLayout.WEST);
    aux.add(jtfPass, BorderLayout.EAST);
    authPanel.add(aux, BorderLayout.SOUTH);
    
    //Juntar os 2 para adicionar ------------------------------------------------------------------------------
    JPanel global = new JPanel(new BorderLayout());
    global.add(mainPanel, BorderLayout.CENTER);
    global.add(authPanel, BorderLayout.PAGE_END);
    
    getContentPane().add(global, BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.PAGE_END);
    repaint();
  }

 
// AUTHENTICATION --------------------------------------------------------------
	private void addListenerAuth() {
		jcbAuth.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {	
					jtfUser.setVisible(true);
					jtfPass.setVisible(true);
					jLabelUser.setVisible(true);
					jLabelPass.setVisible(true);
				} else {
					jtfUser.setVisible(false);
					jtfPass.setVisible(false);
					jLabelUser.setVisible(false);
					jLabelPass.setVisible(false);
				}
			}
		});
	}
//---------------------------------------------------------------------------------
  
  
  private JPanel prepareRightPanel() {
    toggleLock.setSelected(Boolean.valueOf("" + data[DATA_VARS.length][1]));
    toggleVersionable.setSelected(Boolean.valueOf("" + data[DATA_VARS.length + 1][1]));
    drawMainTable();
    drawDynTable();
    JButton addButton = new JButton("+");
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButtonActionPerformed(e);
      }
    });
    JButton remButton = new JButton("-");
    remButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        remButtonActionPerformed(e);
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(remButton);
    JPanel dynPanel = new JPanel(new BorderLayout());
    dynPanel.add(new JScrollPane(dynTable), BorderLayout.CENTER);
    dynPanel.add(buttonPanel, BorderLayout.PAGE_END);
    JPanel dataPanel = new JPanel(new BorderLayout());
    dataPanel.add(dataTable.getTableHeader(), BorderLayout.PAGE_START);
    dataPanel.add(dataTable, BorderLayout.CENTER);
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(dataPanel, BorderLayout.PAGE_START);
    tablePanel.add(dynPanel, BorderLayout.CENTER);
    JPanel togglePanel = new JPanel(new BorderLayout());
    togglePanel.add(toggleVersionable, BorderLayout.PAGE_START);
    togglePanel.add(toggleLock, BorderLayout.CENTER);
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(tablePanel, BorderLayout.CENTER);
    rightPanel.add(togglePanel, BorderLayout.PAGE_END);
    return rightPanel;
  }

  private void drawMainTable() {
    // prepare table data
    Object[][] tableData = new Object[DATA_VARS.length][AlteraAtributosColumnNames.length];
    for (int i = 0; i < DATA_VARS.length; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.label." + DATA_VARS[i]);
      tableData[i][1] = data[i][1];
    }
    // set table data
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    // table init
    if (dataTable == null) {
      dataTable = new JTable(tableModel);
    }
    // final table preparations
    dataTable.setModel(tableModel);
    dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    dataTable.setRowSelectionAllowed(true);
    dataTable.setColumnSelectionAllowed(false);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
        // do nothing
      }

      public void focusGained(FocusEvent e) {
        if (dynTable != null) {
          stopEditing(dynTable);
          dynTable.clearSelection();
        }
      }
    });
    dataTable.doLayout();
  }

  private void drawDynTable() {
    Object[][] tableData = new Object[data.length - (DATA_VARS.length + EXTRA_VARS.length)][AlteraAtributosColumnNames.length];
    for (int i = DATA_VARS.length + EXTRA_VARS.length, lim = data.length; i < lim; i++) {
      tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][0] = data[i][0];
      tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][1] = data[i][1];
    }
    if (dynTable == null) {
      dynTable = new MyJTableX(tableData, AlteraAtributosColumnNames);
    }
    dynTable.setModel(new MyTableModel(AlteraAtributosColumnNames, tableData));
    dynTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    dynTable.setRowSelectionAllowed(true);
    dynTable.setColumnSelectionAllowed(false);
    dynTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dynTable.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
        // do nothing
      }

      public void focusGained(FocusEvent e) {
        if (dataTable != null) {
          stopEditing(dataTable);
          dataTable.clearSelection();
        }
      }
    });
    dynTable.doLayout();
  }

  protected void stopEditing() {
    stopEditing(dataTable);
    stopEditing(dynTable);
  }

  protected void okActionPerformed(ActionEvent e) {
    stopEditing();
    String variable = "" + dataTable.getValueAt(DATA_VARS.length - 1, 1);
    String path = pathField.getText();
    StringBuffer errorMsg = new StringBuffer();
    if (StringUtils.isBlank(variable)) {
      errorMsg.append(adapter.getString("AlteraAtributosDocumentDMS.error.label", 
          adapter.getString("AlteraAtributosDocumentDMS.label." + VARIABLE)));
    }
    if (StringUtils.isBlank(path)) {
      errorMsg.append(adapter.getString("AlteraAtributosDocumentDMS.error.label", 
          adapter.getString("AlteraAtributosDocumentDMS.label.address")));
    }
    if (StringUtils.isNotEmpty(errorMsg.toString())) {
      String msg = adapter.getString("AlteraAtributosDocumentDMS.error.message", errorMsg.toString());
      msg = msg.replace("\\n", System.getProperty("line.separator"));
      JOptionPane.showMessageDialog(this, 
          msg, 
          adapter.getString("AlteraAtributosDocumentDMS.error.title"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    int count = 0;
    for (int i = 0; i < data.length; i++) {
      if (i < DATA_VARS.length) {
        data[i][1] = dataTable.getValueAt(i, 1);
      } else if (i < (DATA_VARS.length + EXTRA_VARS.length)) {
        if (count == 0) {
          data[i][1] = "" + toggleLock.isSelected();
          count++;
        } else if (count == 1) {
          data[i][1] = "" + toggleVersionable.isSelected();
          count++;
        } else if (count == 2) {
          data[i][1] = pathField.getText();
        }
      } else {
        data[i][0] = dynTable.getValueAt(i - (DATA_VARS.length + EXTRA_VARS.length), 0);
        data[i][1] = dynTable.getValueAt(i - (DATA_VARS.length + EXTRA_VARS.length), 1);
      }
    }
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  void addButtonActionPerformed(ActionEvent e) {
    MyTableModel tm = (MyTableModel) dynTable.getModel();
    Object[][] tableData = tm.insertRow();
    Object[][] tmpData = new Object[DATA_VARS.length + EXTRA_VARS.length + tableData.length][AlteraAtributosColumnNames.length];
    for (int i = 0, lim = tmpData.length; i < lim; i++) {
      if (i < DATA_VARS.length + EXTRA_VARS.length) {
        tmpData[i][0] = data[i][0];
        tmpData[i][1] = data[i][1];
      } else {
        tmpData[i][0] = tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][0];
        tmpData[i][1] = tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][1];
      }
    }
    data = tmpData;
  }

  void remButtonActionPerformed(ActionEvent e) {
    int rowSelected = dynTable.getSelectedRow();
    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel) dynTable.getModel();
      Object[][] tableData = tm.removeRow(rowSelected);
      Object[][] tmpData = new Object[DATA_VARS.length + EXTRA_VARS.length + tableData.length][AlteraAtributosColumnNames.length];
      for (int i = 0, lim = tmpData.length; i < lim; i++) {
        if (i < DATA_VARS.length + EXTRA_VARS.length) {
          tmpData[i][0] = data[i][0];
          tmpData[i][1] = data[i][1];
        } else {
          tmpData[i][0] = tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][0];
          tmpData[i][1] = tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][1];
        }
      }
      data = tmpData;
    }
  }
}
