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
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosDocumentDelete extends AlteraAtributosDMS {
  private static final long serialVersionUID = 6094090546260568228L;

  private JTable dataTable;
  private JCheckBox toggleRemote;

  private static final String FILES = "files";
  private static final String REMOTE = "remote";

  private static final String[] DATA_VARS = new String[] { FILES };
  private static final String[] EXTRA_VARS = new String[] { REMOTE };

  
  //AUTHENTICATION
  JCheckBox jcbAuth = null;
  JTextField jtfUser = null;
  JLabel jLabelUser = null;
  JPasswordField jtfPass = null;
  JLabel jLabelPass = null;  
  private static final String[][] dataAUTH = new String[3][2];// {{"AUTHENTICATION","false"},{"USER","tiagold"},{"PASSWORD","xpto456"}};
  
  private static final String AUTHENTICATION = "d_AUTH";
  private static final String USER = "d_USER";
  private static final String PASSWORD = "d_PASS";
  
  
  public AlteraAtributosDocumentDelete(FlowEditorAdapter adapter) {
    super(adapter, "");
    toggleRemote = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.label.delete.remote"));
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    try {
      setTitle(title);
      if (data == null) {
        data = new Object[DATA_VARS.length + EXTRA_VARS.length][2];
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
          if (!found) {
            for (int i = 0, lim = EXTRA_VARS.length; i < lim; i++) {
              if (StringUtils.equalsIgnoreCase(atributo.getNome(), EXTRA_VARS[i])) {
                data[DATA_VARS.length + i][0] = atributo.getNome();
                data[DATA_VARS.length + i][1] = atributo.getValor();
                found = true;
                break;
              }
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
      setSize(300, 150);
      setVisible(true);
    } catch (Exception e) {
      adapter.log(adapter.getString("AlteraAtributosDocumentDMS.error.main.message"), e);
      JOptionPane.showMessageDialog(this, 
          adapter.getString("AlteraAtributosDocumentDMS.error.main.message"), 
          adapter.getString("AlteraAtributosDocumentDMS.error.main.title"),
          JOptionPane.ERROR_MESSAGE);
    }

  }

  public String[][] getNewAttributes() {
    List<String[]> retObj = new ArrayList<String[]>();
    for (int i = 0; i < data.length; i++) {
      if (i < DATA_VARS.length) {
        data[i][0] = DATA_VARS[i];
      } else {
        data[i][0] = EXTRA_VARS[i - DATA_VARS.length];
      }
      if (StringUtils.equals("" + data[i][1], "null")) {
        data[i][1] = "";
      }
      if (StringUtils.isNotBlank("" + data[i][0]) && StringUtils.isNotBlank("" + data[i][1])) {
        retObj.add(asString(data[i]));
      }
    }
    
    //AUTHENTICATION SAVE
    retObj.add(new String [] {AUTHENTICATION,""+jcbAuth.isSelected()});
    retObj.add(new String [] {USER,jtfUser.getText()});
    retObj.add(new String [] {PASSWORD,jtfPass.getText()});
    
    return retObj.toArray(new String[retObj.size()][2]);
  }

  @Override
  protected void okActionPerformed(ActionEvent e) {
    stopEditing();
    for (int i = 0; i < DATA_VARS.length + EXTRA_VARS.length; i++) {
      if (i < DATA_VARS.length) {
        data[i][1] = dataTable.getValueAt(i, 1);
      } else {
        data[i][1] = "" + toggleRemote.isSelected();
      }
    }
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  @Override
  protected void stopEditing() {
    stopEditing(dataTable);
  }

  public int getExitStatus() {
    return exitStatus;
  }

  private void jbInit() throws Exception {
    toggleRemote.setSelected(Boolean.valueOf("" + data[DATA_VARS.length][1]));
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(prepareMainTable(), BorderLayout.CENTER);
    mainPanel.add(toggleRemote, BorderLayout.PAGE_END);
    
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
  private JPanel prepareMainTable() {
    Object[][] tableData = new Object[DATA_VARS.length][AlteraAtributosColumnNames.length];
    for (int i = 0; i < DATA_VARS.length; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.label." + DATA_VARS[i]);
      tableData[i][1] = data[i][1];
    }
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    dataTable = new JTable(tableModel);
    dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    dataTable.getColumnModel().getColumn(0).setMaxWidth(70);
    dataTable.getColumnModel().getColumn(0).setMinWidth(70);
    dataTable.setRowSelectionAllowed(true);
    dataTable.setColumnSelectionAllowed(false);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.doLayout();
    JPanel container = new JPanel(new BorderLayout());
    container.add(dataTable.getTableHeader(), BorderLayout.PAGE_START);
    container.add(dataTable, BorderLayout.CENTER);
    return container;
  }
}
