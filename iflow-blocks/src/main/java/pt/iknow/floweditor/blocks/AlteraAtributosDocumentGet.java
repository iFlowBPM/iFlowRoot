package pt.iknow.floweditor.blocks;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang.StringUtils;

import pt.iflow.connector.dms.ContentResult;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosDocumentGet extends AlteraAtributosDMS {
  private static final long serialVersionUID = -6600331903506247212L;
  
  private JTable dataTable;
  private JTable defValTable;
  private MyJTableX dynTable = new MyJTableX();
  private JCheckBox toggleLock;

  private static final String ID = "id";
  private static final String VARIABLE = "variable";

  private static final String OUTPUT_TITLE = "output.title";
  private static final String OUTPUT_DESCRIPTION = "output.description";
  private static final String OUTPUT_AUTHOR = "output.author";
  private static final String OUTPUT_ID = "output.id";
  private static final String OUTPUT_NAME = "output.name";
  private static final String OUTPUT_URL = "output.url";
  private static final String OUTPUT_PATH = "output.path";
  private static final String OUTPUT_CREATED = "output.created";
  private static final String OUTPUT_MODIFIED = "output.modified";

  private static final String[] DATA_VARS = new String[] { ID, VARIABLE };

  private static final String LOCK = "lock";
  private static final String[] EXTRA_VARS = new String[] { LOCK, OUTPUT_TITLE, OUTPUT_DESCRIPTION, OUTPUT_AUTHOR, OUTPUT_ID,
      OUTPUT_NAME, OUTPUT_URL, OUTPUT_PATH, OUTPUT_CREATED, OUTPUT_MODIFIED };

  private static final String PROP_NAME_PREFIX = "name_";
  private static final String PROP_VALUE_PREFIX = "value_";

  //AUTHENTICATION
  JCheckBox jcbAuth = null;
  JTextField jtfUser = null;
  JLabel jLabelUser = null;
  JPasswordField jtfPass = null;
  JLabel jLabelPass = null;  
  private static final String[][] dataAUTH = new String[3][2];// {{"AUTHENTICATION","false"},{"USER","tiagold"},{"PASSWORD","xpto456"}};
  
  private static final String AUTHENTICATION = "o_AUTH";
  private static final String USER = "o_USER";
  private static final String PASSWORD = "o_PASS";
  
  public AlteraAtributosDocumentGet(FlowEditorAdapter adapter) {
    super(adapter, "");
    toggleLock = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.label.lock"));

  }

  public void setDataIn(String title, List<Atributo> atributos) {
    try {
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
      setSize(400, 500);
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
      boolean add = true;
      if (i < DATA_VARS.length) {
        data[i][0] = DATA_VARS[i];
      } else if (i < (DATA_VARS.length + EXTRA_VARS.length)) {
        data[i][0] = EXTRA_VARS[i - DATA_VARS.length];
      } else {
        String[] itemName = new String[] { PROP_NAME_PREFIX + (i - (DATA_VARS.length + EXTRA_VARS.length)),asString(data[i][0])};
        String[] itemValue = new String[] { PROP_VALUE_PREFIX + (i - (DATA_VARS.length + EXTRA_VARS.length)),asString(data[i][1])};
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

  private void jbInit() throws Exception {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(prepareDefValTable(), BorderLayout.PAGE_START);
    bottomPanel.add(prepareDynTable(), BorderLayout.CENTER);
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(prepareMainTable(), BorderLayout.PAGE_START);
    tablePanel.add(bottomPanel, BorderLayout.CENTER);
    toggleLock.setSelected(Boolean.valueOf("" + data[DATA_VARS.length][1]));
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(tablePanel, BorderLayout.CENTER);
    mainPanel.add(toggleLock, BorderLayout.PAGE_END);
    
    //Criar Interface para AUTHENTICATION -----------------------------------------------------------------
    JPanel authPanel = new JPanel(new BorderLayout());     //checkBox AUTHENTICATION
    jcbAuth = new JCheckBox();
    jcbAuth.setText( adapter.getString("AlteraAtributosDocumentDMS.label.autenticacao"));
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

  private JPanel prepareDefValTable() {
    Object[][] tableData = new Object[EXTRA_VARS.length - 1][AlteraAtributosDMSDynColumns.length];
    for (int i = DATA_VARS.length + 1; i < DATA_VARS.length + EXTRA_VARS.length; i++) {
      tableData[i - (DATA_VARS.length + 1)][0] = adapter.getString("AlteraAtributosDocumentDMS.label."
          + EXTRA_VARS[i - (DATA_VARS.length)]);
      tableData[i - (DATA_VARS.length + 1)][1] = data[i][1];
    }
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosDMSDynColumns, tableData);
    tableModel.setColumnEditable(0, false);
    defValTable = new JTable(tableModel);
    defValTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    defValTable.getColumnModel().getColumn(0).setMaxWidth(70);
    defValTable.getColumnModel().getColumn(0).setMinWidth(70);
    defValTable.setRowSelectionAllowed(true);
    defValTable.setColumnSelectionAllowed(false);
    defValTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    defValTable.doLayout();
    JPanel container = new JPanel(new BorderLayout());
    container.add(defValTable.getTableHeader(), BorderLayout.CENTER);
    container.add(defValTable, BorderLayout.PAGE_END);
    return container;
  }

  private JPanel prepareDynTable() {
    Object[][] tableData = new Object[data.length - (DATA_VARS.length + EXTRA_VARS.length)][AlteraAtributosDMSDynColumns.length];
    for (int i = DATA_VARS.length + EXTRA_VARS.length, lim = data.length; i < lim; i++) {
      tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][0] = data[i][0];
      tableData[i - (DATA_VARS.length + EXTRA_VARS.length)][1] = data[i][1];
    }
    if (dynTable == null) {
      dynTable = new MyJTableX(tableData, AlteraAtributosDMSDynColumns);
    }
    dynTable.setModel(new MyTableModel(AlteraAtributosDMSDynColumns, tableData));
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
    JPanel dynButtonPanel = new JPanel();
    dynButtonPanel.add(addButton);
    dynButtonPanel.add(remButton);
    JPanel container = new JPanel(new BorderLayout());
    container.add(new JScrollPane(dynTable), BorderLayout.CENTER);
    container.add(dynButtonPanel, BorderLayout.PAGE_END);
    return container;
  }

  protected void stopEditing() {
    stopEditing(dataTable);
    stopEditing(defValTable);
    stopEditing(dynTable);
  }

  protected void okActionPerformed(ActionEvent e) {
    stopEditing();
    StringBuffer errorMsg = new StringBuffer();
    for (int i = 0; i < DATA_VARS.length; i++) {
      if (StringUtils.isBlank("" + dataTable.getValueAt(i, 1))) {
        errorMsg.append(adapter.getString("AlteraAtributosDocumentDMS.error.label", adapter
            .getString("AlteraAtributosDocumentDMS.label." + DATA_VARS[i])));
      }
    }
    if (StringUtils.isNotEmpty(errorMsg.toString())) {
      String msg = adapter.getString("AlteraAtributosDocumentDMS.error.message", errorMsg.toString());
      msg = msg.replace("\\n", System.getProperty("line.separator"));
      JOptionPane.showMessageDialog(this, msg, adapter.getString("AlteraAtributosDocumentDMS.error.title"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    for (int i = 0; i < data.length; i++) {
      if (i < DATA_VARS.length) {
        data[i][1] = dataTable.getValueAt(i, 1);
      } else if (i < (DATA_VARS.length + EXTRA_VARS.length)) {
        if (i == DATA_VARS.length) {
          data[i][1] = "" + toggleLock.isSelected();
        } else {
          data[i][0] = EXTRA_VARS[i - (DATA_VARS.length)];
          data[i][1] = defValTable.getValueAt(i - (DATA_VARS.length + 1), 1);
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
    Object[][] tmpData = new Object[DATA_VARS.length + EXTRA_VARS.length + tableData.length][AlteraAtributosDMSDynColumns.length];
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
      Object[][] tmpData = new Object[DATA_VARS.length + EXTRA_VARS.length + tableData.length][AlteraAtributosDMSDynColumns.length];
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

class MyTreeModel extends DefaultTreeModel {
  private static final long serialVersionUID = -2630367301248790715L;

  public MyTreeModel(TreeNode root) {
    super(root);
  }

  @Override
  public boolean isLeaf(Object node) {
    boolean retObj = super.isLeaf(node);
    if (retObj) {
      ContentResult content = (ContentResult) ((DefaultMutableTreeNode) (node)).getUserObject();
      retObj = content.isLeaf();
    }
    return retObj;
  }
}

class JTableComponentRenderer implements TableCellRenderer {
  private TableCellRenderer defaultRenderer;

  public JTableComponentRenderer(TableCellRenderer renderer) {
    defaultRenderer = renderer;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
      int column) {
    if (value instanceof Component)
      return (Component) value;
    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}