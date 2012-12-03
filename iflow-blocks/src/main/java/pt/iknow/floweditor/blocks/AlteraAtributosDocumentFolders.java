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
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosDocumentFolders extends AlteraAtributosDMS {
  private static final long serialVersionUID = 5041355004440665313L;

  private JTable inputTable;
  private JTable outputTable;
  private JCheckBox toggleDepth;

  private String depthBackup = "";
  private String DEPTH_DEFAULT = "\"1\"";

  private static final String PATTERN = "pattern";
  private static final String DEPTH = "depth";
  private static final String[] INPUT_VARS = new String[] { PATTERN, DEPTH };

  private static String[] EXTRA_VARS = new String[] { PATH };

  private static final String PATHS = "paths";
  private static String[] OUTPUT_VARS = new String[] { PATHS };

  
  //AUTHENTICATION
  JCheckBox jcbAuth = null;
  JTextField jtfUser = null;
  JLabel jLabelUser = null;
  JPasswordField jtfPass = null;
  JLabel jLabelPass = null;  
  private static final String[][] dataAUTH = new String[3][2];// {{"AUTHENTICATION","false"},{"USER","tiagold"},{"PASSWORD","xpto456"}};
  
  private static final String AUTHENTICATION = "f_AUTH";
  private static final String USER = "f_USER";
  private static final String PASSWORD = "f_PASS";
  
  public AlteraAtributosDocumentFolders(FlowEditorAdapter adapter) {
    super(adapter, "");
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    setTitle(title);
    if (data == null) {
      data = new Object[INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length][2];
    }
    if (toggleDepth == null) {
      toggleDepth = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.folders.toggle"));
    }
    if (atributos != null) {
      for (Atributo atributo : atributos) {
        for (int i = 0, lim = INPUT_VARS.length; i < lim; i++) {
          if (StringUtils.equalsIgnoreCase(atributo.getNome(), INPUT_VARS[i])) {
            data[i][0] = atributo.getNome();
            data[i][1] = atributo.getValor();
            break;
          }
        }
        for (int i = 0, lim = EXTRA_VARS.length; i < lim; i++) {
          if (StringUtils.equalsIgnoreCase(atributo.getNome(), EXTRA_VARS[i])) {
            data[INPUT_VARS.length + i][0] = atributo.getNome();
            data[INPUT_VARS.length + i][1] = atributo.getValor();
            break;
          }
        }
        for (int i = 0, lim = OUTPUT_VARS.length; i < lim; i++) {
          if (StringUtils.equalsIgnoreCase(atributo.getNome(), OUTPUT_VARS[i])) {
            data[INPUT_VARS.length + EXTRA_VARS.length + i][0] = atributo.getNome();
            data[INPUT_VARS.length + EXTRA_VARS.length + i][1] = atributo.getValor();
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
      if (i < INPUT_VARS.length) {
        data[i][0] = INPUT_VARS[i];
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length) {
        data[i][0] = EXTRA_VARS[i - INPUT_VARS.length];
      } else {
        data[i][0] = OUTPUT_VARS[i - (INPUT_VARS.length + EXTRA_VARS.length)];
      }
      if (StringUtils.equals("" + data[i][1], "null")) {
        data[i][1] = "";
      }
    }
    for (Object[] item : data) {
      if (StringUtils.isNotBlank("" + item[0]) && StringUtils.isNotBlank("" + item[1])) {
        retObj.add(asString(item));
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
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab(adapter.getString("AlteraAtributosDocumentDMS.tab.input"), prepareInputPanel());
    tabbedPane.addTab(adapter.getString("AlteraAtributosDocumentDMS.tab.output"), prepareOutputPanel());
    
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
    global.add(tabbedPane, BorderLayout.CENTER);
    global.add(authPanel, BorderLayout.PAGE_END);
    
    getContentPane().add(global, BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.PAGE_END);
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
  
  private JPanel prepareInputPanel() {
    JPanel left = getTreePanel();
    JPanel right = prepareInputRightPanel();
    JPanel content = new JPanel(new BorderLayout());
    content.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right), BorderLayout.CENTER);
    return content;
  }

  private JPanel prepareInputRightPanel() {
    Object[][] tableData = new Object[INPUT_VARS.length][AlteraAtributosColumnNames.length];
    for (int i = 0; i < INPUT_VARS.length; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.folders." + INPUT_VARS[i]);
      tableData[i][1] = data[i][1];
      if (StringUtils.equals(INPUT_VARS[i], DEPTH)) {
        if (StringUtils.isBlank((String) tableData[i][1])) {
          tableData[i][1] = DEPTH_DEFAULT;
        }
        toggleDepth.setSelected(!StringUtils.equals((String) tableData[i][1], DEPTH_DEFAULT));
      }
    }
    toggleDepth.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MyTableModel tableModel = (MyTableModel) inputTable.getModel();
        tableModel.setCellEditable(INPUT_VARS.length - 1, 1, toggleDepth.isSelected());
        if (toggleDepth.isSelected()) {
          inputTable.setValueAt(depthBackup, INPUT_VARS.length - 1, 1);
        } else {
          depthBackup = "" + inputTable.getValueAt(INPUT_VARS.length - 1, 1);
          inputTable.setValueAt(DEPTH_DEFAULT, INPUT_VARS.length - 1, 1);
        }
        inputTable.repaint();
      }
    });
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    tableModel.setCellEditable(INPUT_VARS.length - 1, 1, toggleDepth.isSelected());
    if (inputTable == null) {
      inputTable = new JTable();
    }
    inputTable.setModel(tableModel);
    inputTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    inputTable.setRowSelectionAllowed(true);
    inputTable.setColumnSelectionAllowed(false);
    inputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    inputTable.doLayout();
    JPanel containerAssist = new JPanel(new BorderLayout());
    containerAssist.add(toggleDepth, BorderLayout.PAGE_START);
    JPanel container = new JPanel(new BorderLayout());
    container.add(inputTable, BorderLayout.PAGE_START);
    container.add(containerAssist, BorderLayout.CENTER);
    return container;
  }

  private JPanel prepareOutputPanel() {
    Object[][] tableData = new Object[OUTPUT_VARS.length][AlteraAtributosColumnNames.length];
    for (int i = 0; i < OUTPUT_VARS.length; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.folders.label." + OUTPUT_VARS[i]);
      tableData[i][1] = data[i + INPUT_VARS.length + EXTRA_VARS.length][1];
    }
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    if (outputTable == null) {
      outputTable = new JTable();
    }
    outputTable.setModel(tableModel);
    outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    outputTable.setRowSelectionAllowed(true);
    outputTable.setColumnSelectionAllowed(false);
    outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    outputTable.doLayout();
    JPanel container = new JPanel(new BorderLayout());
    container.add(new JScrollPane(outputTable), BorderLayout.CENTER);
    return container;
  }

  protected void stopEditing() {
    stopEditing(inputTable);
    stopEditing(outputTable);
  }

  protected void okActionPerformed(ActionEvent e) {
    stopEditing();
    for (int i = 0; i < data.length; i++) {
      if (i < INPUT_VARS.length) {
        data[i][1] = inputTable.getValueAt(i, 1);
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length) {
        data[i][1] = pathField.getText();
      } else {
        data[i][1] = outputTable.getValueAt(i - (INPUT_VARS.length + EXTRA_VARS.length), 1);
      }
    }
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }
}
