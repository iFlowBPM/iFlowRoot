package pt.iknow.floweditor.blocks;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

import pt.iflow.api.blocks.BlockAttributes;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosDocumentList extends AlteraAtributosDMS {
  private static final long serialVersionUID = 7231725058132442612L;

  private JTable dataTable;
  private MyJXTable modifiedTable;
  private MyJXTable createdTable;
  private MyJTableX dynTable = new MyJTableX();
  private JTable outputTable;

  
  //AUTHENTICATION
  JCheckBox jcbAuth = null;
  JTextField jtfUser = null;
  JLabel jLabelUser = null;
  JPasswordField jtfPass = null;
  JLabel jLabelPass = null;  
  private static final String[][] dataAUTH = new String[3][2];
  
  private static final String AUTHENTICATION = "l_AUTH";
  private static final String USER = "l_USER";
  private static final String PASSWORD = "l_PASS";
  
  
  
  private static final String[] INPUT_VARS = new String[] { 
    BlockAttributes.DOC_FILTER, BlockAttributes.DOC_TITLE, BlockAttributes.DOC_DESCRIPTION, BlockAttributes.DOC_AUTHOR, 
    BlockAttributes.DOC_CREATED_FROM, BlockAttributes.DOC_CREATED_TO,
    BlockAttributes.DOC_MODIFIED_FROM, BlockAttributes.DOC_MODIFIED_TO };

  private static String[] EXTRA_VARS = new String[] { PATH };

  private static String[] OUTPUT_VARS = new String[] { 
    BlockAttributes.DOC_OUTPUT_IDS, BlockAttributes.DOC_OUTPUT_NAME, 
    BlockAttributes.DOC_OUTPUT_TITLES, BlockAttributes.DOC_OUTPUT_DESCRIPTIONS, 
    BlockAttributes.DOC_OUTPUT_AUTHORS, BlockAttributes.DOC_OUTPUT_URL, 
    BlockAttributes.DOC_OUTPUT_PATH, BlockAttributes.DOC_OUTPUT_CREATED, 
    BlockAttributes.DOC_OUTPUT_MODIFIED };


  public AlteraAtributosDocumentList(FlowEditorAdapter adapter) {
    super(adapter, "");
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    try {
      setTitle(title);
      int condCount = 0;
      if (atributos != null) {
        for (Atributo atributo : atributos) {
          if (atributo != null && atributo.getNome().startsWith(BlockAttributes.DOC_PROP_NAME_PREFIX)) {
            condCount++;
          }
        }
      }
      if (data == null) {
        data = new Object[INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length + condCount][2];
      }
      if (atributos != null) {
        for (Atributo atributo : atributos) {
          boolean found = false;
          for (int i = 0, lim = INPUT_VARS.length; i < lim; i++) {
            if (StringUtils.equalsIgnoreCase(atributo.getNome(), INPUT_VARS[i])) {
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
              data[INPUT_VARS.length + i][0] = atributo.getNome();
              data[INPUT_VARS.length + i][1] = atributo.getValor();
              found = true;
              break;
            }
          }
          if (found) {
            continue;
          }
          for (int i = 0, lim = OUTPUT_VARS.length; i < lim; i++) {
            if (StringUtils.equalsIgnoreCase(atributo.getNome(), OUTPUT_VARS[i])) {
              data[INPUT_VARS.length + EXTRA_VARS.length + i][0] = atributo.getNome();
              data[INPUT_VARS.length + EXTRA_VARS.length + i][1] = atributo.getValor();
              found = true;
              break;
            }
          }
          if (!found && (atributo.getNome().startsWith(BlockAttributes.DOC_PROP_NAME_PREFIX) || 
              atributo.getNome().startsWith(BlockAttributes.DOC_PROP_VALUE_PREFIX))) {
            int index;
            try {
              index = INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length;
              index += Integer.parseInt(atributo.getNome().split("_")[1]);
            } catch (NumberFormatException ex) {
              index = INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length;
            }
            if (atributo.getNome().startsWith(BlockAttributes.DOC_PROP_NAME_PREFIX)) {
              data[index][0] = atributo.getValor();
            } else if (atributo.getNome().startsWith(BlockAttributes.DOC_PROP_VALUE_PREFIX)) {
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
    } catch (JAXBException e) {
      adapter.log(adapter.getString("AlteraAtributosDocumentDMS.error.main.message"), e);
      JOptionPane.showMessageDialog(this, adapter.getString("AlteraAtributosDocumentDMS.error.main.message"), adapter
          .getString("AlteraAtributosDocumentDMS.error.main.title"), JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
      adapter.log("Error!", e);
    }
  }

  public String[][] getNewAttributes() {
    List<String[]> retObj = new ArrayList<String[]>();
    for (int i = 0; i < data.length; i++) {
      boolean add = true;
      if (i < INPUT_VARS.length) {
        data[i][0] = INPUT_VARS[i];
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length) {
        data[i][0] = EXTRA_VARS[i - INPUT_VARS.length];
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length) {
        data[i][0] = OUTPUT_VARS[i - (INPUT_VARS.length + EXTRA_VARS.length)];
      } else {
        String[] itemName = new String[] { BlockAttributes.DOC_PROP_NAME_PREFIX + (i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)),
            asString(data[i][0]) };
        String[] itemValue = new String[] { BlockAttributes.DOC_PROP_VALUE_PREFIX + (i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)),
            asString(data[i][1]) };
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

  private void jbInit() throws JAXBException {
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
    
    
    getContentPane().add(global, BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.PAGE_END);
  }

//AUTHENTICATION --------------------------------------------------------------
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

  private JPanel prepareOutputPanel() {
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
    JPanel dynPanel = new JPanel(new BorderLayout());
    dynPanel.add(prepareDynTable(), BorderLayout.CENTER);
    dynPanel.add(dynButtonPanel, BorderLayout.PAGE_END);
    JPanel outputPanel = new JPanel(new BorderLayout());
    outputPanel.add(prepareOutputTable(), BorderLayout.PAGE_START);
    outputPanel.add(dynPanel, BorderLayout.CENTER);
    return outputPanel;
  }

  private JPanel prepareInputRightPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(prepareModifiedTable(), BorderLayout.PAGE_START);
    JPanel centralPanel = new JPanel(new BorderLayout());
    centralPanel.add(prepareCreatedTable(), BorderLayout.PAGE_START);
    centralPanel.add(bottomPanel, BorderLayout.CENTER);
    JPanel content = new JPanel(new BorderLayout());
    content.add(prepareDataTable(), BorderLayout.PAGE_START);
    content.add(centralPanel, BorderLayout.CENTER);
    return content;
  }

  private JPanel prepareOutputTable() {
    Object[][] tableData = new Object[OUTPUT_VARS.length][AlteraAtributosColumnNames.length];
    for (int i = 0; i < OUTPUT_VARS.length; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.label." + OUTPUT_VARS[i]);
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
    container.add(outputTable.getTableHeader(), BorderLayout.CENTER);
    container.add(outputTable, BorderLayout.PAGE_END);
    return container;
  }

  private JPanel prepareDataTable() {
    Object[][] tableData = new Object[INPUT_VARS.length - 4][AlteraAtributosColumnNames.length];
    for (int i = 0; i < INPUT_VARS.length - 4; i++) {
      tableData[i][0] = adapter.getString("AlteraAtributosDocumentDMS.label." + INPUT_VARS[i]);
      tableData[i][1] = data[i][1];
    }
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    if (dataTable == null) {
      dataTable = new JTable();
    }
    dataTable.setModel(tableModel);
    dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    dataTable.setRowSelectionAllowed(true);
    dataTable.setColumnSelectionAllowed(false);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.doLayout();
    dataTable.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
        // do nothing
      }

      public void focusGained(FocusEvent e) {
        createdTable.clearSelection();
        modifiedTable.clearSelection();
        dynTable.clearSelection();
        stopEditing(createdTable);
        stopEditing(modifiedTable);
        stopEditing(dynTable);
      }
    });
    JPanel container = new JPanel(new BorderLayout());
    container.add(dataTable.getTableHeader(), BorderLayout.CENTER);
    container.add(dataTable, BorderLayout.PAGE_END);
    return container;
  }

  private JPanel prepareCreatedTable() {
    JXDatePicker fromDatePicker = prepareDatePicker((String) data[INPUT_VARS.length - 4][1]);
    JXDatePicker toDatePicker = prepareDatePicker((String) data[INPUT_VARS.length - 3][1]);

    Object[][] tableData = new Object[2][AlteraAtributosColumnNames.length];
    tableData[0][0] = adapter.getString("AlteraAtributosDocumentDMS.label.date.from");
    tableData[0][1] = fromDatePicker;
    tableData[1][0] = adapter.getString("AlteraAtributosDocumentDMS.label.date.to");
    tableData[1][1] = toDatePicker;

    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    if (createdTable == null) {
      createdTable = new MyJXTable(tableModel);
    }
    createdTable.setDateCellEditors(new DateCellEditor(fromDatePicker), new DateCellEditor(toDatePicker));
    createdTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    createdTable.setRowSelectionAllowed(true);
    createdTable.setColumnSelectionAllowed(false);
    createdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    createdTable.doLayout();
    createdTable.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
        // do nothing
      }

      public void focusGained(FocusEvent e) {
        modifiedTable.clearSelection();
        dataTable.clearSelection();
        dynTable.clearSelection();
        stopEditing(modifiedTable);
        stopEditing(dataTable);
        stopEditing(dynTable);
      }
    });
    JLabel label = new JLabel(adapter.getString("AlteraAtributosDocumentDMS.label.created"), JLabel.CENTER);
    label.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
    JPanel container = new JPanel(new BorderLayout());
    container.add(label, BorderLayout.PAGE_START);
    container.add(createdTable, BorderLayout.CENTER);
    return container;
  }

  private JPanel prepareModifiedTable() {
    final JXDatePicker fromDatePicker = prepareDatePicker((String) data[INPUT_VARS.length - 2][1]);
    final JXDatePicker toDatePicker = prepareDatePicker((String) data[INPUT_VARS.length - 1][1]);
    Object[][] tableData = new Object[2][AlteraAtributosColumnNames.length];
    tableData[0][0] = adapter.getString("AlteraAtributosDocumentDMS.label.date.from");
    tableData[0][1] = fromDatePicker;
    tableData[1][0] = adapter.getString("AlteraAtributosDocumentDMS.label.date.to");
    tableData[1][1] = toDatePicker;
    MyTableModel tableModel = new SortFilterModel(AlteraAtributosColumnNames, tableData);
    tableModel.setColumnEditable(0, false);
    if (modifiedTable == null) {
      modifiedTable = new MyJXTable(tableModel);
    }
    modifiedTable.setDateCellEditors(new DateCellEditor(fromDatePicker), new DateCellEditor(toDatePicker));
    modifiedTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    modifiedTable.setRowSelectionAllowed(true);
    modifiedTable.setColumnSelectionAllowed(false);
    modifiedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    modifiedTable.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
        // do nothing
      }

      public void focusGained(FocusEvent e) {
        createdTable.clearSelection();
        dataTable.clearSelection();
        dynTable.clearSelection();
        stopEditing(createdTable);
        stopEditing(dataTable);
        stopEditing(dynTable);
      }
    });
    JLabel label = new JLabel(adapter.getString("AlteraAtributosDocumentDMS.label.modified"), JLabel.CENTER);
    label.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
    JPanel container = new JPanel(new BorderLayout());
    container.add(label, BorderLayout.PAGE_START);
    container.add(modifiedTable, BorderLayout.CENTER);
    return container;
  }

  private JPanel prepareDynTable() {
    Object[][] tableData = new Object[data.length - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][AlteraAtributosDMSDynColumns.length];
    for (int i = INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length, lim = data.length; i < lim; i++) {
      tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][0] = data[i][0];
      tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][1] = data[i][1];
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
          dataTable.clearSelection();
          createdTable.clearSelection();
          modifiedTable.clearSelection();
          stopEditing(dataTable);
          stopEditing(createdTable);
          stopEditing(modifiedTable);
        }
      }
    });
    dynTable.doLayout();
    JPanel container = new JPanel(new BorderLayout());
    container.add(new JScrollPane(dynTable), BorderLayout.CENTER);
    return container;
  }

  private JXDatePicker prepareDatePicker(String text) {
    String[] userLocale = adapter.getRepository().getUserLocale().split("_");
    Locale locale = new Locale(userLocale[0], userLocale[1]);
    JXDatePicker datePicker = new JXDatePicker(locale) {
      private static final long serialVersionUID = -7810518798486133419L;

      @Override
      public String toString() {
        String text = this.getEditor().getText();
        if (StringUtils.isEmpty(text)) {
          text = "";
        }
        return text;
      }
    };
    datePicker.setFormats(new SimpleDateFormat("'\"'dd/MM/yyyy'\"'"));
    datePicker.getEditor().setFocusLostBehavior(JFormattedTextField.PERSIST);
    datePicker.setLinkDay(new Date(System.currentTimeMillis()), adapter.getString("AlteraAtributosDocumentDMS.label.date",
        "{0, date, dd/MM/yyyy}"));
    datePicker.getEditor().setText(text == null ? "" : "" + text);
    return datePicker;
  }

  protected void stopEditing() {
    stopEditing(dataTable);
    stopEditing(createdTable);
    stopEditing(modifiedTable);
    stopEditing(dynTable);
    stopEditing(outputTable);
  }

  protected void okActionPerformed(ActionEvent e) {
    stopEditing();
    for (int i = 0; i < data.length; i++) {
      if (i < INPUT_VARS.length - 4) {
        data[i][1] = dataTable.getValueAt(i, 1);
      } else if (i < INPUT_VARS.length - 2) {
        data[i][1] = createdTable.getValueAt(i - (INPUT_VARS.length - 4), 1);
      } else if (i < INPUT_VARS.length) {
        data[i][1] = modifiedTable.getValueAt(i - (INPUT_VARS.length - 4) - 2, 1);
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length) {
        data[i][1] = pathField.getText();
      } else if (i < INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length) {
        data[i][1] = outputTable.getValueAt(i - (INPUT_VARS.length + EXTRA_VARS.length), 1);
      } else {
        data[i][0] = dynTable.getValueAt(i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length), 0);
        data[i][1] = dynTable.getValueAt(i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length), 1);
      }
    }
    exitStatus = EXIT_STATUS_OK;
    dispose();
  }

  void addButtonActionPerformed(ActionEvent e) {
    MyTableModel tm = (MyTableModel) dynTable.getModel();
    Object[][] tableData = tm.insertRow();
    Object[][] tmpData = new Object[INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length + tableData.length][AlteraAtributosDMSDynColumns.length];
    for (int i = 0, lim = tmpData.length; i < lim; i++) {
      if (i < INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length) {
        tmpData[i][0] = data[i][0];
        tmpData[i][1] = data[i][1];
      } else {
        tmpData[i][0] = tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][0];
        tmpData[i][1] = tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][1];
      }
    }
    data = tmpData;
  }

  void remButtonActionPerformed(ActionEvent e) {
    int rowSelected = dynTable.getSelectedRow();
    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel) dynTable.getModel();
      Object[][] tableData = tm.removeRow(rowSelected);
      Object[][] tmpData = new Object[INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length + tableData.length][AlteraAtributosDMSDynColumns.length];
      for (int i = 0, lim = tmpData.length; i < lim; i++) {
        if (i < INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length) {
          tmpData[i][0] = data[i][0];
          tmpData[i][1] = data[i][1];
        } else {
          tmpData[i][0] = tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][0];
          tmpData[i][1] = tableData[i - (INPUT_VARS.length + EXTRA_VARS.length + OUTPUT_VARS.length)][1];
        }
      }
      data = tmpData;
    }
  }
}

class MyJXTable extends JXTable {
  private static final long serialVersionUID = 922378938798423338L;
  private TableCellEditor dateFromCellEditor;
  private TableCellEditor dateToCellEditor;

  public MyJXTable(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
  }

  public MyJXTable(TableModel dm) {
    super(dm);
  }

  public void setDateCellEditors(TableCellEditor dateFromCellEditor, TableCellEditor dateToCellEditor) {
    setDateFromCellEditor(dateFromCellEditor);
    setDateToCellEditor(dateToCellEditor);
  }

  public void setDateFromCellEditor(TableCellEditor dateFromCellEditor) {
    this.dateFromCellEditor = dateFromCellEditor;
  }

  public void setDateToCellEditor(TableCellEditor dateToCellEditor) {
    this.dateToCellEditor = dateToCellEditor;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    if (convertColumnIndexToModel(column) > 0) {
      return true;
    }
    return false;
  }

  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    if (convertColumnIndexToModel(column) > 0 && convertRowIndexToModel(row) == 0) {
      return dateFromCellEditor;
    } else if (convertColumnIndexToModel(column) > 0 && convertRowIndexToModel(row) == 1) {
      return dateToCellEditor;
    }
    return super.getCellEditor(row, column);
  }
}

class DateCellEditor extends AbstractCellEditor implements TableCellEditor {
  private static final long serialVersionUID = 1L;
  private final JXDatePicker datePicker;

  DateCellEditor(JXDatePicker datePicker) {
    super();
    this.datePicker = datePicker;
  }

  public Object getCellEditorValue() {
    return this.datePicker.getEditor().getText();
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    if (value instanceof Date) {
      datePicker.setDate((Date) value);
    }
    return datePicker;
  }
}