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

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar condicoes para Importador Excel </p>
 * <p>  nome | variavel | modo importação
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosDataImport extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = -3746197440515802996L;

  // values shared beteween iFlow
  private static final String _sSheetNum = "sheet";  //$NON-NLS-1$ 
  private static final String _sInitLine = "initLine";  //$NON-NLS-1$ 
  private static final String _sColType = "colType";  //$NON-NLS-1$ 
  private static final String _sHasHeader = "hasHeader";  //$NON-NLS-1$ 

  private static final String _sColName = "name";  //$NON-NLS-1$ 
  private static final String _sVarName = "var";  //$NON-NLS-1$ 
  private static final String _sModeName = "mode";  //$NON-NLS-1$ 
  private static final String _sFormatName = "format";  //$NON-NLS-1$ 

  private static final String _sColNumberVal = "position";  //$NON-NLS-1$ 
  private static final String _sColNameVal = "name";  //$NON-NLS-1$ 

  private static final String _sModeWriteAllways = "write_always";  //$NON-NLS-1$ 
  private static final String _sModeWriteOnce = "write_once";  //$NON-NLS-1$ 

  // Form stuff
  private static final String _sHeader = "header";  //$NON-NLS-1$ 
  private static final String _sSubheader = "subheader";  //$NON-NLS-1$ 
  private static final String _sMessage = "message";  //$NON-NLS-1$ 
  private static final String _sFileLabel = "file";  //$NON-NLS-1$ 
  private static final String _sFileVar = "file_var";  //$NON-NLS-1$ 

  // Tipos de formatos
  private static final String INT_FMT  ="INT";
  private static final String NUM_FMT  ="NUM";
  private static final String DATE_FMT  ="DATE";

  // Externalized descriptions
  private final String _sColNumberValDesc;
  private final String _sColNameValDesc;


  private final String _sHeaderDesc; 
  private final String _sSubheaderDesc; 
  private final String _sMessageDesc; 
  private final String _sFileLabelDesc; 
  private final String _sFileVarDesc; 
  
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private MyJTableX jTable1 = new MyJTableX();
  private JTextField jtfSheet = new JTextField();
  private JTextField jtfInitialLine = new JTextField();
  private JTextField jtfHeader = new JTextField();
  private JTextField jtfSubheader = new JTextField();
  private JTextField jtfMessage = new JTextField();
  private JTextField jtfFileLabel = new JTextField();
  private JTextField jtfFileVar = new JTextField();

  // default is always
  private final String[] saIMPORT_MODES;
  private final String[] saIMPORT_MODES_DESC;

  private final String[] columnNames;
  
  private final String[] saFORMAT_NAMES;  //$NON-NLS-1$ 
  private final String[] saFORMAT_NAMES_DESC;

  private final String[] varNames; 
  private final String[] defaultRowValue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

  private final String[] columnTypes;
  private final String[] columnTypesDesc;

  private JComboBox jcbColLabelType;
  private JCheckBox jcboxHasHeader;

  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data;

  private JButton addButton = new JButton();
  private JButton removeButton = new JButton();

  private final HashMap<String,String> colMapping = new HashMap<String, String>(2);
  private final HashMap<String,String> colReverseMapping = new HashMap<String, String>(2);
  private final HashMap<String,String> importModeMapping = new HashMap<String, String>(2);
  private final HashMap<String,String> importReverseModeMapping = new HashMap<String, String>(2);
  private final HashMap<String,String> formatNameMapping;
  private final HashMap<String,String> formatNameReverseMapping;


  private void initMaps() {
    // initialize mappings
    for(int i = 0; i < columnTypes.length; i++) {
      colMapping.put(columnTypes[i], columnTypesDesc[i]);
      colReverseMapping.put(columnTypesDesc[i], columnTypes[i]);
    }

    // initialize mappings
    for(int i = 0; i < saIMPORT_MODES.length; i++) {
      importModeMapping.put(saIMPORT_MODES[i], saIMPORT_MODES_DESC[i]);
      importReverseModeMapping.put(saIMPORT_MODES_DESC[i], saIMPORT_MODES[i]);
    }

    // initialize mappings
    for(int i = 0; i < saFORMAT_NAMES.length; i++) {
      formatNameMapping.put(saFORMAT_NAMES[i], saFORMAT_NAMES_DESC[i]);
      formatNameReverseMapping.put(saFORMAT_NAMES_DESC[i], saFORMAT_NAMES[i]);
    }

  }

  public AlteraAtributosDataImport(FlowEditorAdapter adapter) {
    super(adapter, adapter.getBlockMessages().getString("AlteraAtributosDataImport.title"), true);  //$NON-NLS-1$
    
    // Externalized descriptions
    _sColNumberValDesc = adapter.getString("AlteraAtributosDataImport.label.column_number"); //$NON-NLS-1$
    _sColNameValDesc = adapter.getString("AlteraAtributosDataImport.label.column_name"); //$NON-NLS-1$


    _sHeaderDesc = adapter.getString("AlteraAtributosDataImport.label.form_header");  //$NON-NLS-1$ 
    _sSubheaderDesc = adapter.getString("AlteraAtributosDataImport.label.form_subheader");  //$NON-NLS-1$ 
    _sMessageDesc = adapter.getString("AlteraAtributosDataImport.label.form_message");  //$NON-NLS-1$ 
    _sFileLabelDesc = adapter.getString("AlteraAtributosDataImport.label.form_file_label");  //$NON-NLS-1$ 
    _sFileVarDesc = adapter.getString("AlteraAtributosDataImport.label.form_file_var");  //$NON-NLS-1$ 

    
    // default is always
    saIMPORT_MODES = new String[] { _sModeWriteAllways, _sModeWriteOnce };
    saIMPORT_MODES_DESC = new String[] { 
      adapter.getString("AlteraAtributosDataImport.label.write_always"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.label.write_once"),  //$NON-NLS-1$
    };

    columnNames = new String[] { 
      adapter.getString("AlteraAtributosDataImport.label.column"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.label.variable"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.label.import_mode"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.label.format_name"),  //$NON-NLS-1$ 
    };
    
    saFORMAT_NAMES = new String[] { "", INT_FMT, NUM_FMT, DATE_FMT };  //$NON-NLS-1$ 
    saFORMAT_NAMES_DESC = new String[] { 
      adapter.getString("AlteraAtributosDataImport.format.none"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.format.int"),  //$NON-NLS-1$
      adapter.getString("AlteraAtributosDataImport.format.num"),  //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosDataImport.format.date"),  //$NON-NLS-1$ 
    };

    varNames = new String[] { _sColName, _sVarName, _sModeName, _sFormatName }; 
    defaultRowValue = new String[] {"","",saIMPORT_MODES_DESC[0], ""}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

    columnTypes = new String[] { _sColNameVal, _sColNumberVal };
    columnTypesDesc = new String[] { _sColNameValDesc, _sColNumberValDesc };

    jcbColLabelType = new JComboBox(columnTypesDesc);
    jcboxHasHeader = new JCheckBox("",true);//$NON-NLS-1$
    formatNameMapping = new HashMap<String, String>(saFORMAT_NAMES.length);
    formatNameReverseMapping = new HashMap<String, String>(saFORMAT_NAMES.length);

    initMaps();
  }


  /**
   * getExitStatus
   * 
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * 
   * @return
   */
  public String[][] getNewAttributes() {
    final int tableCols = varNames.length;
    final int formProps = 6;
    final int singleProps = 3+formProps;


    int condCount = 0;
    String cond = null;
    for (int i = 0; i < data.length; i++) {
      cond = (String) jTable1.getValueAt(i, 0);
      if (StringUtilities.isNotEmpty(cond)) {
        condCount++;
      }
    }
    int propIdx = condCount * tableCols;
    String[][] newAttributes = new String[singleProps + propIdx][3];

    for (int i = 0, j = 0; i < data.length; i++) {
      cond = (String) data[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        int row = 0;
        newAttributes[tableCols * j + row][0] = varNames[row] + j;                 // column
        newAttributes[tableCols * j + row][1] = (String) data[i][row];
        newAttributes[tableCols * j + row][2] = "";  //$NON-NLS-1$
        row++;
        newAttributes[tableCols * j + row][0] = varNames[row] + j;             // var name
        newAttributes[tableCols * j + row][1] = (String) data[i][row];
        newAttributes[tableCols * j + row][2] = "";  //$NON-NLS-1$
        row++;
        newAttributes[tableCols * j + row][0] = varNames[row] + j;             // mode
        newAttributes[tableCols * j + row][1] = importReverseModeMapping.get(data[i][row]);
        newAttributes[tableCols * j + row][2] = "";  //$NON-NLS-1$
        row++;
        newAttributes[tableCols * j + row][0] = varNames[row] + j;             // mode
        newAttributes[tableCols * j + row][1] = formatNameReverseMapping.get(data[i][row]);
        newAttributes[tableCols * j + row][2] = "";  //$NON-NLS-1$
        row++;
        j++;
      }
    }

    // now 'Initial Line' and 'Column Label Type'
    int pos = 0;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sSheetNum;               // sheet
    newAttributes[propIdx + pos][1] = jtfSheet.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sSheetNum;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sInitLine;               // first line
    newAttributes[propIdx + pos][1] = jtfInitialLine.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sInitLine;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sColType;            // Column name/position
    newAttributes[propIdx + pos][1] = colReverseMapping.get(jcbColLabelType.getSelectedItem());
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sColType;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sHasHeader;          // Include header
    newAttributes[propIdx + pos][1] = String.valueOf(jcboxHasHeader.isSelected());
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sHasHeader;
    pos++;

    // finally, form props
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sHeader;               // first line
    newAttributes[propIdx + pos][1] = jtfHeader.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sHeader;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sSubheader;               // first line
    newAttributes[propIdx + pos][1] = jtfSubheader.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sSubheader;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sMessage;               // first line
    newAttributes[propIdx + pos][1] = jtfMessage.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sMessage;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sFileLabel;               // first line
    newAttributes[propIdx + pos][1] = jtfFileLabel.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sFileLabel;
    pos++;
    newAttributes[propIdx + pos][0] = AlteraAtributosDataImport._sFileVar;               // first line
    newAttributes[propIdx + pos][1] = jtfFileVar.getText();
    newAttributes[propIdx + pos][2] = AlteraAtributosDataImport._sFileVar;
    pos++;

    return newAttributes;
  }

  /**
   * setDataIn
   * 
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    int condCount = 0;

    boolean bInitLine = false;
    boolean bColType = false;
    boolean bHasHeader = false;

    // count attributes to rebuild table
    HashMap<String, Integer> hmPositions = new HashMap<String, Integer>();
    for (int i = 0; i < atributos.size(); i++) {
      Atributo atributo = atributos.get(i);
      if (atributo == null)
        continue;
      String name = atributo.getNome();
      if (name != null && name.startsWith(varNames[0])) {
        String pos = name.substring(varNames[0].length());
        hmPositions.put(pos, condCount);
        condCount++;
      }
    }

    data = new Object[condCount][varNames.length];
    Map<String, String>[] hmData = extracted();
    for(int j = 0; j < varNames.length; j++)
      hmData[j] = new HashMap<String, String>(condCount);

      for (int i = 0; i < atributos.size(); i++) {
        Atributo atributo = atributos.get(i);
        if (atributo == null)
          continue;

        String name = atributo.getNome();
        String value = atributo.getValor();

        if(null == name) continue;

        else if(name.equals(_sSheetNum)) {
          if (StringUtilities.isEmpty(value)) value="0";
          jtfSheet.setText(value);
        } 
        else if(name.equals(_sInitLine)) {
          if (StringUtilities.isEmpty(value)) continue;
          jtfInitialLine.setText(value);
          bInitLine = true;
        } 
        else if(name.equals(_sColType)) {
          if (StringUtilities.isEmpty(value)) continue;
          jcbColLabelType.setSelectedItem(colMapping.get(value));
          bColType = true;
        } 
        // form stuff
        else if(name.equals(_sHasHeader)) {
          if (StringUtilities.isEmpty(value)) continue;
          jcboxHasHeader.setSelected(new Boolean(value).booleanValue());
          bHasHeader = true;
        } 
        else if(name.equals(_sHeader)) {
          if (StringUtilities.isEmpty(value)) value=""; //$NON-NLS-1$
          jtfHeader.setText(value);
        } 
        else if(name.equals(_sSubheader)) {
          if (StringUtilities.isEmpty(value)) value=""; //$NON-NLS-1$
          jtfSubheader.setText(value);
        } 
        else if(name.equals(_sMessage)) {
          if (StringUtilities.isEmpty(value)) value=""; //$NON-NLS-1$
          jtfMessage.setText(value);
        } 
        else if(name.equals(_sFileLabel)) {
          if (StringUtilities.isEmpty(value)) value=""; //$NON-NLS-1$
          jtfFileLabel.setText(value);
        }
        else if(name.equals(_sFileVar)) {
            if (StringUtilities.isEmpty(value)) value="file"; //$NON-NLS-1$
            jtfFileVar.setText(value);
        }
        
        // table stuff
        else {
          for(int j = 0; j < varNames.length; j++) {
            if(name.startsWith(varNames[j])){ 
              if (value == null)
                value = defaultRowValue[j];
              String pos = name.substring(varNames[j].length());
              hmData[j].put(varNames[j]+hmPositions.get(pos), value);
              break;
            }
          }
        }
      }

      for (int i = 0; i < condCount; i++) {
        for(int j = 0; j < varNames.length; j++) {
          data[i][j] = hmData[j].get(varNames[j] + i);
          // mapping stuff...
          if(j == 2) data[i][j] = importModeMapping.get(hmData[j].get(varNames[j] + i));
          if(j == 3) data[i][j] = formatNameMapping.get(hmData[j].get(varNames[j] + i));
        }
      }

      if (!bInitLine) {
        jtfInitialLine.setText("1");  //$NON-NLS-1$
      } 
      if (!bColType) {
        jcbColLabelType.setSelectedItem(_sColNameValDesc);
      }
      if (!bHasHeader) {
        jcboxHasHeader.setSelected(true);
      }


      jTable1 = new MyJTableX(data, columnNames);
      MyTableModel tableModel = new MyTableModel(columnNames, data);
      tableModel.setDefaultValuesForRow(defaultRowValue);
      jTable1.setModel(tableModel);

      jTable1.setRowSelectionAllowed(true);
      jTable1.setColumnSelectionAllowed(false);

      jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      MyColumnEditorModel rm = new MyColumnEditorModel();
      jTable1.setMyColumnEditorModel(rm);

      JTextField jtf1 = new JTextField();
      jtf1.setSelectionColor(Color.red);
      jtf1.setSelectedTextColor(Color.white);
      DefaultCellEditor cce = new DefaultCellEditor(jtf1);
      cce.setClickCountToStart(2);
      rm.addEditorForColumn(0, cce);

      JTextField jtf2 = new JTextField();
      jtf2.setSelectionColor(Color.red);
      jtf2.setSelectedTextColor(Color.white);
      DefaultCellEditor mce = new DefaultCellEditor(jtf2);
      mce.setClickCountToStart(2);
      rm.addEditorForColumn(1, mce);

      JComboBox jcb1 = new JComboBox(saIMPORT_MODES_DESC);
      DefaultCellEditor ed1 = new DefaultCellEditor(jcb1);
      rm.addEditorForColumn(2, ed1);

      JComboBox jcb2 = new JComboBox(saFORMAT_NAMES_DESC);
      DefaultCellEditor ed2 = new DefaultCellEditor(jcb2);
      rm.addEditorForColumn(3, ed2);

      jbInit();
      
      // repack and resize
      pack();
      setSize(600, 400);
      setVisible(true);
  }

  @SuppressWarnings("unchecked")
  private Map<String, String>[] extracted() {
    return new HashMap[varNames.length];
  }

  private void jbInit() {
    okButton.setText(OK);

    addButton.setText("+");  //$NON-NLS-1$
    removeButton.setText("-");  //$NON-NLS-1$

    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });

    cancelButton.setText(Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });

    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButtonActionPerformed(e);
      }
    });

    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeButtonActionPerformed(e);
      }
    });

    // Layout panel
    JPanel formLayout = new JPanel();
    // Layout tem form e file

    java.awt.GridBagConstraints gridBagConstraints;

    javax.swing.JLabel headerLbl = new javax.swing.JLabel();
    javax.swing.JLabel subheaderLbl = new javax.swing.JLabel();
    javax.swing.JLabel messageLbl = new javax.swing.JLabel();
    javax.swing.JLabel fileLabelLbl = new javax.swing.JLabel();
    javax.swing.JLabel fileVarLbl = new javax.swing.JLabel();

    formLayout.setLayout(new java.awt.GridBagLayout());

    formLayout.setBorder(BorderFactory.createTitledBorder(adapter.getString("AlteraAtributosDataImport.label.form"))); //$NON-NLS-1$
    headerLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    headerLbl.setText(_sHeaderDesc);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    formLayout.add(headerLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    headerLbl.setLabelFor(jtfHeader);
    formLayout.add(jtfHeader, gridBagConstraints);

    subheaderLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    subheaderLbl.setText(_sSubheaderDesc);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    formLayout.add(subheaderLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    subheaderLbl.setLabelFor(jtfSubheader);
    formLayout.add(jtfSubheader, gridBagConstraints);

    messageLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    messageLbl.setText(_sMessageDesc);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    formLayout.add(messageLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    messageLbl.setLabelFor(jtfMessage);
    formLayout.add(jtfMessage, gridBagConstraints);

    fileLabelLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    fileLabelLbl.setText(_sFileLabelDesc);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    formLayout.add(fileLabelLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    fileLabelLbl.setLabelFor(jtfFileLabel);
    formLayout.add(jtfFileLabel, gridBagConstraints);


    fileVarLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    fileVarLbl.setText(_sFileVarDesc);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    formLayout.add(fileVarLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    fileLabelLbl.setLabelFor(jtfFileVar);
    formLayout.add(jtfFileVar, gridBagConstraints);

    JPanel importPanel = new JPanel();
    importPanel.setBorder(BorderFactory.createTitledBorder(adapter.getString("AlteraAtributosDataImport.label.importer"))); //$NON-NLS-1$
    importPanel.setLayout(new GridBagLayout());
    GridBagConstraints sC = new GridBagConstraints();

    // Sheet number
    jtfSheet.setHorizontalAlignment(JTextField.RIGHT);
    sC.fill = GridBagConstraints.HORIZONTAL;
    sC.insets = new java.awt.Insets(2, 2, 2, 2);
    JLabel jLabel = null;
    jLabel = new JLabel(adapter.getString("AlteraAtributosDataImport.label.sheet"));  //$NON-NLS-1$
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(jtfSheet);
    importPanel.add(jLabel, sC);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sC.weightx = 1.0;
    importPanel.add(jtfSheet, sC);
    sC.gridwidth = 1;
    
    // 'Initial Line'
    jtfInitialLine.setHorizontalAlignment(JTextField.RIGHT);
    sC.fill = GridBagConstraints.HORIZONTAL;
    sC.insets = new java.awt.Insets(2, 2, 2, 2);
    jLabel = null;
    jLabel = new JLabel(adapter.getString("AlteraAtributosDataImport.label.first_line"));  //$NON-NLS-1$
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(jtfInitialLine);
    importPanel.add(jLabel, sC);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    importPanel.add(jtfInitialLine, sC);
    sC.gridwidth = 1;

    // 'Column Label Type'
    jLabel = null;
    jLabel = new JLabel(adapter.getString("AlteraAtributosDataImport.label.column_designation"));  //$NON-NLS-1$
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(jcbColLabelType);
    importPanel.add(jLabel, sC);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    importPanel.add(jcbColLabelType, sC);
    sC.gridwidth = 1;

    // 'Has Header'
    jLabel = null;
    jLabel = new JLabel(adapter.getString("AlteraAtributosDataImport.label.ignore_header"));  //$NON-NLS-1$
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setLabelFor(jcboxHasHeader);
    importPanel.add(jLabel, sC);
    sC.gridwidth = GridBagConstraints.REMAINDER;
    importPanel.add(jcboxHasHeader, sC);
    sC.gridwidth = 1;

    /* table */
    jTable1.setRowSelectionAllowed(false);
    this.setModal(true);

    JPanel topPanel = new JPanel();
    GridBagConstraints c = new GridBagConstraints();
    topPanel.setLayout(new GridBagLayout());

    c.fill = GridBagConstraints.BOTH;
    c.gridx=0;
    c.gridy=0;
    c.weightx=1.0;
    c.insets = new java.awt.Insets(2, 30, 2, 2);
    topPanel.add(formLayout, c);
    c.fill = GridBagConstraints.BOTH;
    c.gridx=1;
    c.gridy=0;
    c.weightx=1.0;
    c.insets = new java.awt.Insets(2, 2, 2, 30);
    topPanel.add(importPanel, c);


    JPanel tablePanel = new JPanel(new BorderLayout());

    JScrollPane jScrollPane1 = new JScrollPane();

    JTableHeader jth = jTable1.getTableHeader();
    jScrollPane1.add(jth);
    jScrollPane1.getViewport().add(jTable1, null);

    tablePanel.add(jScrollPane1, BorderLayout.CENTER);

    /* spacer pannels */
    Dimension dim = new Dimension(30, 10);
    JPanel spacer = new JPanel();
    spacer.setSize(dim);
    spacer.setPreferredSize(dim);
    spacer.setMaximumSize(dim);
    spacer.setMinimumSize(dim);
    tablePanel.add(spacer, BorderLayout.WEST);
    spacer = new JPanel();
    spacer.setSize(dim);
    spacer.setPreferredSize(dim);
    spacer.setMaximumSize(dim);
    spacer.setMinimumSize(dim);
    tablePanel.add(spacer, BorderLayout.EAST);
    spacer = new JPanel();
    spacer.setSize(dim);
    spacer.setPreferredSize(dim);
    spacer.setMaximumSize(dim);
    spacer.setMinimumSize(dim);
    tablePanel.add(spacer, BorderLayout.NORTH);
    spacer = new JPanel();
    spacer.setSize(dim);
    spacer.setPreferredSize(dim);
    spacer.setMaximumSize(dim);
    spacer.setMinimumSize(dim);
    tablePanel.add(spacer, BorderLayout.SOUTH);

    JPanel buttonsPanel = new JPanel();
    buttonsPanel.add(okButton);
    buttonsPanel.add(cancelButton);
    buttonsPanel.add(addButton);
    buttonsPanel.add(removeButton);
    getContentPane().add(topPanel, BorderLayout.NORTH);
    getContentPane().add(tablePanel, BorderLayout.CENTER);
    getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

    repaint();
  }

  /* OK */
  private void okButtonActionPerformed(ActionEvent e) {
    jTable1.stopEditing();

    // Verify attribute values
    // InitLine > 1
    // Column Numbered Label > 1
    if (jtfInitialLine.getText() != null && jtfInitialLine.getText().length() > 0) {
      boolean res = true;
      try {
        if (Integer.parseInt(jtfInitialLine.getText()) < 1)
          res = false;
      } catch (NumberFormatException nfe) {
        res = false;
      }

      if (!res) {
        adapter.showError(adapter.getString("AlteraAtributosDataImport.error.invalid_first_line"));  //$NON-NLS-1$
        return;
      }
    }

    if (jcbColLabelType.getSelectedItem().equals(_sColNumberValDesc)) {

      boolean res = true;
      for (int i = 0; i < data.length; i++) {
        String v = (String) data[i][0];
        if(StringUtilities.isEmpty(v)) continue;
        int n = 0;

        try {
          n = Integer.parseInt(v);
          res = (n >= 0);
        } catch (NumberFormatException nfe) {
          res = false;
        }

        if (!res) {
          adapter.showError(adapter.getString("AlteraAtributosDataImport.error.invalid_column"));  //$NON-NLS-1$
          return;
        }
      }
    }

    exitStatus = EXIT_STATUS_OK;
    setVisible(false);
    dispose();
  }

  /* Cancelar */
  private void cancelButtonActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    setVisible(false);
    dispose();
  }

  /* + */
  private void addButtonActionPerformed(ActionEvent e) {
    // Add a row to the table
    MyTableModel tm = (MyTableModel) jTable1.getModel();
    data = tm.insertRow();
  }

  /* - */
  private void removeButtonActionPerformed(ActionEvent e) {
    int rowSelected = jTable1.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel) jTable1.getModel();
      data = tm.removeRow(rowSelected);
    }
  }

}
