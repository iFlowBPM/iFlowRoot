package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar validações </p></p>
 * <p>  condição | mensagem de erro
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.IDesenho;
import pt.iknow.floweditor.blocks.dataProcessing.Operation;
import pt.iknow.floweditor.blocks.dataProcessing.OperationField;
import pt.iknow.utils.swing.DecoratedBorder;

public class AlteraAtributosDataProcessing extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1245748103290376946L;

  private static final int PROP_SIZE = 2;
  private static final int PROP_NAME_POS = 0;
  private static final int PROP_VALUE_POS = 1;

  // {0}=pos, {1}=code, {2}=fieldname
  private static final String PROP_FMT = "{0}-{1}-{2}";  //$NON-NLS-1$
  private static final String PROP_RE = "(\\d+)-(\\w+)-(\\w+)"; //$NON-NLS-1$

  private final JPanel buttonPanel = new JPanel();
  private final JButton buttonOk = new JButton();
  private final JButton buttonCancel = new JButton();
  private final JButton buttonAdd = new JButton();
  private final JButton buttonRemove = new JButton();
  private final JPanel dataPanel = new JPanel();
  private final GridBagLayout dataPanelLayout = new GridBagLayout();
  private final JScrollPane jScrollPane1 = new JScrollPane(dataPanel);

  private final static Color BG_COLOR = new Color(0xe7,0xe3,0xda);

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String[][] data;
  
  private final IDesenho desenho;

  private final Collection<String> catalogVars;
  private final Collection<String> arrayVars;
  private final Collection<String> scalarVars;
  private final Collection<Operation> operations;
  private final Map<String,Integer> operationIndex;

  public AlteraAtributosDataProcessing(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosDataProcessing.title"), true); //$NON-NLS-1$
    this.desenho = adapter.getDesenho();
    this.operations = Operation.getOperations(adapter);
    this.operationIndex = new HashMap<String, Integer>();
    processOperations();

    Collection<Atributo> vars = desenho.getCatalogue();
    this.catalogVars = new ArrayList<String>(vars.size());
    this.arrayVars = new ArrayList<String>(vars.size());
    this.scalarVars = new ArrayList<String>(vars.size());
    processCatalogVars(vars);
  }



  /**
   * getExitStatus
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * @return
   */
  public String[][] getNewAttributes() {
    return data;
  }

  /**
   * getNewAttributes
   * @return
   */
  private void buildNewAttributes() {
    int condCount = 0;

    final int elemCount = dataPanel.getComponentCount();
    for (int n = 0; n < elemCount; n++) {
      OperationSelectionPanel panel = (OperationSelectionPanel) dataPanel.getComponent(n);
      Operation op = panel.getSelectedOperation();
      if(op.isIgnorable()) continue; // skip this one
      condCount += op.getFields().size();
    }
    String[][] newAttributes = new String[condCount][3];

    for (int pos = 0, line = 0, n = 0; n < elemCount; n++) {
      OperationSelectionPanel panel = (OperationSelectionPanel) dataPanel.getComponent(n);
      Operation op = panel.getSelectedOperation();
      if(op.isIgnorable()) continue; // skip this one
      String [][] values = panel.getProperties();
      for(int i = 0; i < values.length; i++) {
        String propName = MessageFormat.format(PROP_FMT, line, op.getCode(), values[i][PROP_NAME_POS]);
        String propValue = values[i][PROP_VALUE_POS];
        newAttributes[pos][0] = propName;
        newAttributes[pos][1] = propValue;
        newAttributes[pos][2] = "";
        pos++;
      }
      ++line;

    }
    data = newAttributes;
  }

  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    // agrupar por tipo de dados
    Pattern p = Pattern.compile(PROP_RE);
    Map<Integer, OpAux> data = new TreeMap<Integer, OpAux>();
    Iterator<Atributo> iter = atributos.iterator();
    while(iter.hasNext()) {
      Atributo a = iter.next();
      if(a.getNome() == null) continue;
      Matcher m = p.matcher(a.getNome());
      if(!m.matches()) continue;
      String sPos = m.group(1);
      String sCode = m.group(2);
      String sField = m.group(3);

      Integer ii = new Integer(sPos);
      OpAux op = data.get(ii);
      if(null == op) {
        op = new OpAux();
        op.code = sCode;
        data.put(ii,op);
      }
      op.params.put(sField, a.getValor());
    }

    jbInit(data);
    pack();
    this.setSize(500,350);
    this.setVisible(true);
  }

  private void jbInit(Map<Integer, OpAux> data) {
    buttonOk.setText(OK);

    buttonAdd.setText("+"); //$NON-NLS-1$
    buttonRemove.setText("-"); //$NON-NLS-1$

    buttonOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonOkActionPerformed(e);
      }
    });

    buttonCancel.setText(Cancelar);
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonCancelActionPerformed(e);
      }
    });

    buttonAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonAddActionPerformed(e);
      }
    });


    buttonRemove.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonRemoveActionPerformed(e);
      }
    });


    this.setModal(true);

    getContentPane().add(new JPanel(), BorderLayout.NORTH);
    getContentPane().add(new JPanel(), BorderLayout.WEST);
    getContentPane().add(new JPanel(), BorderLayout.EAST);


    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    // control the speed effect of the wheelmouse
    jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.getVerticalScrollBar().setUnitIncrement( 16 );

    dataPanel.setLayout(dataPanelLayout);
    // dataPanel.setBorder(BorderFactory.createLineBorder(Color.RED));


    buttonPanel.add(buttonOk);
    buttonPanel.add(buttonCancel);
    buttonPanel.add(buttonAdd);
    buttonPanel.add(buttonRemove);

    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    // add existing attributes

    Iterator<Integer> ops = data.keySet().iterator();
    while(ops.hasNext()) {
      OpAux opAux = data.get(ops.next());

      // append new line
      OperationSelectionPanel pane = new OperationSelectionPanel();
      pane.showSeparator();
      GridBagConstraints gc = new GridBagConstraints();
      gc.fill = GridBagConstraints.HORIZONTAL;
      gc.insets = new Insets(3,3,3,3);
      gc.gridx = 0;
      gc.gridy = dataPanel.getComponentCount();
      gc.anchor = GridBagConstraints.NORTH;
      gc.weightx=1.0;
      gc.weighty=0.0;
      gc.ipadx=2;
      gc.ipady=2;
      dataPanel.add(pane, gc);
      pane.setOperation(getOperationIndex(opAux.code), opAux.params);
    }

    int count = dataPanel.getComponentCount();
    if(count > 0) {
      OperationSelectionPanel op = (OperationSelectionPanel)dataPanel.getComponent(count-1);
      op.hideSeparator();
      GridBagConstraints gc = dataPanelLayout.getConstraints(op);
      gc.weighty=1.0;
      dataPanelLayout.setConstraints(op, gc);
    }
  }

  /* OK */
  private void buttonOkActionPerformed(ActionEvent e) {
    buildNewAttributes();
    exitStatus=EXIT_STATUS_OK;
    setVisible(false);
    dispose();
  }

  /* Cancelar */
  private void buttonCancelActionPerformed(ActionEvent e) {
    exitStatus=EXIT_STATUS_CANCEL;
    setVisible(false);
    dispose();
  }

  /* + */
  private void buttonAddActionPerformed(ActionEvent e) {
    // show last component separator
    int count = dataPanel.getComponentCount();
    if(count > 0) {
      OperationSelectionPanel op = (OperationSelectionPanel)dataPanel.getComponent(count-1);
      op.showSeparator();
      GridBagConstraints gc = dataPanelLayout.getConstraints(op);
      gc.weighty=0.0;
      dataPanelLayout.setConstraints(op, gc);
    }

    // append new line
    OperationSelectionPanel pane = new OperationSelectionPanel();
    pane.hideSeparator();
    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(3,3,3,3);
    gc.gridx = 0;
    gc.gridy = dataPanel.getComponentCount();
    gc.anchor = GridBagConstraints.NORTH;
    gc.weightx=1.0;
    gc.weighty=1.0;
    gc.ipadx=2;
    gc.ipady=2;
    dataPanel.add(pane, gc);
    pane.setVisible(true);
    updateDataPanel();
  }

  /* - */
  private void buttonRemoveActionPerformed(ActionEvent e) {
    int count = dataPanel.getComponentCount();
    if(count > 0) {
      dataPanel.remove(count-1);
    }

    // hide last component separator
    count = dataPanel.getComponentCount();
    if(count > 0) {
      OperationSelectionPanel op = (OperationSelectionPanel)dataPanel.getComponent(count-1);
      op.hideSeparator();
      GridBagConstraints gc = dataPanelLayout.getConstraints(op);
      gc.weighty=1.0;
      dataPanelLayout.setConstraints(op, gc);
    }

    updateDataPanel();
  }

  private void updateDataPanel() { // será que existe outra forma eficiente de fazer isto?
    jScrollPane1.revalidate();
  }

  private void processCatalogVars(Collection<Atributo> vars) {
    if(vars.size() > 0) {
      catalogVars.add("");
      arrayVars.add("");
      scalarVars.add("");
      for(Atributo a : vars) {
        catalogVars.add(a.getNome());
        String dataType = a.getDataType();
        if(dataType.startsWith("class [")) {
          arrayVars.add(a.getNome());
        } else {
          scalarVars.add(a.getNome());
        }
      }
    }
  }

  private void processOperations() {
    Iterator<Operation> ops = operations.iterator();
    int pos = 0;
    while(ops.hasNext()) {
      Operation op = ops.next();
      operationIndex.put(op.getCode(), pos);
      pos++;
    }
  }

  private int getOperationIndex(String opCode) {
    Integer position = operationIndex.get(opCode);
    if(null == position) return -1;
    return position.intValue();
  }

  private Vector<?> getOperations(OperationSelectionPanel parent) {
    return new Vector<Operation>(operations);
  }

  protected class OperationSelectionPanel extends JPanel {
    private static final long serialVersionUID = 4288246534611689984L;

    private JPanel contentPanel;
    private JComboBox operationCombo;
    private boolean ignoreEvent = false;
    // private JSeparator separator;

    public OperationSelectionPanel() {
      super();
      setLayout(new GridBagLayout());
      JLabel titleLabel = new JLabel(adapter.getString("AlteraAtributosDataProcessing.operator"));
      GridBagConstraints gc = new GridBagConstraints();
      gc.gridx = 0;
      gc.gridy = 0;
      gc.insets = new Insets(1,1,1,1);
      add(titleLabel, gc);
      gc.gridy = 1;

      operationCombo = new JComboBox(getOperations(this));
      operationCombo.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Operation op = getSelectedOperation();
          updateFields(op, null);
        }
      });

      add(operationCombo, gc);
      contentPanel = new JPanel(new GridBagLayout());
      gc.gridx = 1;
      gc.gridy = 0;
      gc.gridheight = 2;
      gc.fill = GridBagConstraints.BOTH;
      gc.weightx = 1.0;
      gc.weighty = 1.0;
      add(contentPanel, gc);
      setBorder(BorderFactory.createLineBorder(Color.BLACK));
      contentPanel.setBackground(BG_COLOR);
      setBackground(BG_COLOR);
    }

    public void hideSeparator() {
      //    separator.setVisible(false);
    }

    public void showSeparator() {
      //    separator.setVisible(true);
    }

    public String [][] getProperties() {
      Operation op = getSelectedOperation();
      List<OperationField> fields = op.getFields();
      String [][] result = new String [fields.size()][PROP_SIZE];
      final int elemCount = contentPanel.getComponentCount();
      for(int pos = 0, n = 0; n < elemCount; n++ ) {
        Component component = contentPanel.getComponent(n);
        String value = "";
        if(component instanceof JLabel) continue;
        if(component instanceof JTextField) {
          value = ((JTextField)component).getText();
        } else if(component instanceof JComboBox) {
          value = (String) ((JComboBox)component).getSelectedItem();
        }
        result[pos][PROP_NAME_POS] = fields.get(pos).getName();
        result[pos][PROP_VALUE_POS] = value;
        pos++;
      }

      return result;
    }

    public Operation getSelectedOperation() {
      return (Operation) operationCombo.getSelectedItem();
    }

    public synchronized void setOperation(int pos, Map<String,String> params) {
      ignoreEvent = true;
      operationCombo.setSelectedIndex(pos);
      ignoreEvent = false;
      updateFields(getSelectedOperation(), params);
    }

    public void updateFields(Operation op, Map<String,String> params) {
      if(ignoreEvent) return;
      contentPanel.removeAll();
      List<OperationField> fields = op.getFields();
      Iterator<OperationField> iter = fields.iterator();
      GridBagConstraints gc = new GridBagConstraints();
      gc.gridx = 0;
      gc.gridy = 0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      gc.weightx = 1.0;
      gc.insets = new Insets(0,1,0,1);

      Color bg = contentPanel.getBackground();

      while(iter.hasNext()) {
        OperationField field = iter.next();
        JLabel descLabel = new JLabel(field.getDesc());
        JComponent jc = null;
        JComboBox jcb = null;
        JTextField jtf = null;
        String value = null;
        if(null != params) value = params.get(field.getName());

        switch(field.getType()) {
        case OperationField.TYPE_ANY:
          jc = jcb = new JComboBox(new Vector<String>(catalogVars));
          break;
        case OperationField.TYPE_ARRAY:
          jc = jcb = new JComboBox(new Vector<String>(arrayVars));
          break;
        case OperationField.TYPE_SCALAR:
          jc = jcb = new JComboBox(new Vector<String>(scalarVars));
          break;
        case OperationField.TYPE_EXPRESSION:
        default:
          jc = jtf = new JTextField();
        break;
        }
        if(null != jcb && null != value) jcb.setSelectedItem(value);
        if(null != jtf && null != value) jtf.setText(value);

        jc.setBorder(new DecoratedBorder(field.getType(), bg, jc.getBorder()));
        gc.gridy = 0;
        contentPanel.add(descLabel, gc);
        gc.gridy = 1;
        contentPanel.add(jc, gc);
        gc.gridx++;
      }
      updateDataPanel();
    }

  }

  private static final class OpAux {
    String code;
    Map<String,String> params = new HashMap<String,String>();
  }
}
