package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para criar propriedades do flow </p></p>
 * <p> propriedade | valor
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.DynamicBindDelegate;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.Formats;
import pt.iknow.floweditor.IDesenho;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.CheckBoxCellEditor;
import pt.iknow.utils.swing.CheckBoxCellRenderer;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.FormatsCellEditor;
import pt.iknow.utils.swing.FormatsCellRenderer;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyColumnRendererModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableCellRenderer;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.swing.SortFilterModel;

public class AlteraAtributosStart extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 1L;

  // check if math with ones in Uniflow's pt.iknow.flows.FlowData
  // public final static String sSETTING_PREFIX = "##_"; // not supported yet
  public static final String sSETTING = "setting_"; //$NON-NLS-1$
  public static final String sSETTING_DESC = "settingdesc_"; //$NON-NLS-1$
  public static final String sWSVAR = "wsvar_"; //$NON-NLS-1$
  public static final String sDETAIL = "detail_"; //$NON-NLS-1$
  public static final String sMAIL_START_INFO_PREFIX = "msinfo_"; //$NON-NLS-1$
  public static final String sMAIL_START_VARS_PREFIX = "msvars_"; //$NON-NLS-1$

  public static final String sDETAIL_CLASS = "detailClass"; //$NON-NLS-1$
  public static final String sDETAIL_CLASS_BLOCK_NAME = "BlockDetailForm"; //$NON-NLS-1$

  public static final String sDETAIL_FORM = "detailForm"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_NODETAIL = "no"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_DEFAULT = "default"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_BLOCKFORM = "form"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_EXISTINGBLOCKFORM = "existingform"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_BID = "detailFormBID"; //$NON-NLS-1$
  
  private final String[] columnNamesCatalog;

  private final String[] columnNamesProps;

  private final String[] columnNamesVars;

  
  // Mail start
  private static int MAILSTART_INFO_PROP_COUNT = 5;
  private static String MAILSTART_FROM_EMAIL_PROP = "MS_FROMEMAIL"; //$NON-NLS-1$
  private static String MAILSTART_FROM_NAME_PROP = "MS_FROMNAME"; //$NON-NLS-1$
  private static String MAILSTART_SUBJECT_PROP = "MS_SUBJ"; //$NON-NLS-1$
  private static String MAILSTART_SENT_DATE_PROP = "MS_SD"; //$NON-NLS-1$
  private static String MAILSTART_DOCS_PROP = "MS_DOCS"; //$NON-NLS-1$
  private static final String MAILSTART_MAIL_PROP = "MS_MP"; //$NON-NLS-1$
  private static final String MAILSTART_FLOW_VAR = "MS_VAR"; //$NON-NLS-1$
  
  private final String[] columnNamesMailStart;


  private JPanel jButtonPanel = new JPanel();
  private JTabbedPane jTabbedPane = new JTabbedPane();

  private JButton jButtonOK = new JButton();
  private JButton jButtonCancel = new JButton();
  private JButton jButtonDetail = new JButton();
  private JTextField jTextBID = new JTextField();

  // Catalogo
  private JPanel jPanelCatalog = new JPanel();
  private JScrollPane jScrollCatalog = new JScrollPane();
  private MyJTableX jTableCatalog = new MyJTableX();
  private JButton jButtonCatalogAdd = new JButton();
  private JButton jButtonCatalogDel = new JButton();

  private JPanel jPanelWSVars = new JPanel();
  private JScrollPane jScrollWSVars = new JScrollPane();
  private MyJTableX jTableWSVars = new MyJTableX();
  private JButton jButtonWSVarAdd = new JButton();
  private JButton jButtonWSVarDel = new JButton();

  private JPanel jPanelFlowProps = new JPanel();
  private JScrollPane jScrollFlowProps = new JScrollPane();
  private MyJTableX jTableFlowProps = new MyJTableX();
  private JButton jButtonPropsAdd = new JButton();
  private JButton jButtonPropsDel = new JButton();
  private JPanel jPanelFlowDetail = new JPanel();


  // mail start
  private JPanel jPanelMS = new JPanel();
  private JTextField jtfMSFromName = new JTextField(20);
  private JTextField jtfMSFromEmail = new JTextField(20);
  private JTextField jtfMSSubject = new JTextField(20);
  private JTextField jtfMSSentDate = new JTextField(20);
  private JTextField jtfMSDocs = new JTextField(20);
//  private JScrollPane jScrollMSVars = new JScrollPane();
  private MyJTableX jTableMSVars = new MyJTableX();
  private JButton jButtonMSVarAdd = new JButton();
  private JButton jButtonMSVarDel = new JButton();

  
  
  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] dataProps, dataWSVars, dataCatalog, dataMSVars;
  private String[][] dataDetail;

  private String detailType = sDETAIL_FORM_DEFAULT;
  private String detailFormBID = "";

  // Reserved Data Size
  private int reservedDataSize;
  
  private final IDesenho desenho;

  public AlteraAtributosStart(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosStart.title"), true); //$NON-NLS-1$
    desenho = adapter.getDesenho();
    adapter.log("Good constructor");
    
    columnNamesCatalog = new String[]{ 
      adapter.getString("AlteraAtributosCatalogVars.vars.columnlabel"), //$NON-NLS-1$ 
      adapter.getString("AlteraAtributosCatalogVars.initval.columnlabel"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosCatalogVars.datatype.columnlabel"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosCatalogVars.issearchable.columnlabel"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosCatalogVars.publicname.columnlabel"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosCatalogVars.format.columnlabel"), //$NON-NLS-1$
    };

    columnNamesProps = new String[]{ 
      adapter.getString("AlteraAtributosStart.property.column.name"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosStart.description.column.name"), //$NON-NLS-1$
    };

    columnNamesVars  = new String[]{
      adapter.getString("AlteraAtributosStart.ws.vars.tab.label"), //$NON-NLS-1$
    };

    columnNamesMailStart  = new String[]{
      adapter.getString("AlteraAtributosStart.mailstart.mapping.prop"), //$NON-NLS-1$
      adapter.getString("AlteraAtributosStart.mailstart.mapping.var") //$NON-NLS-1$
    };


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
    if(null != desenho) {
      // Take care of catalog
      // ArrayList<Atributo> catalog = desenho.getVariableCatalog();
      // catalog.clear();
      desenho.newCatalog();
      for (Object[] catElem : dataCatalog) {
        String nome = (String) catElem[0];
        String valor = (String) catElem[1];
        String tipo = catElem[2].toString();
        boolean isSearchable = (Boolean) catElem[3];
        String publicName = (String) catElem[4];
        String format = "";
        if(catElem[5] instanceof Formats.Format)
          format = ((Formats.Format)catElem[5]).format;
        else
          format = (String) catElem[5];

        if(StringUtilities.isEmpty(nome) || StringUtilities.isEmpty(tipo)) {
          continue;
        }

        boolean isDynVar = false;
        for(Object[] elem : getDynamicVariables()) {
          if(elem.length > 0 && StringUtils.equals(elem[0].toString(), nome)) {
            isDynVar = true;
            break;
          }
        }
        if(!isDynVar) {
          desenho.addCatalogVariable(nome, valor, isSearchable, publicName, tipo, format);
        }
      }
    }

    int condCountProps = 0, condCountVars = 0;
    for (int i = 0; i < dataProps.length; i++) {
      String cond = (String) dataProps[i][0];
      String msg = (String) dataProps[i][1];
      if (StringUtilities.isNotEmpty(cond) && StringUtilities.isNotEmpty(msg)) {
        condCountProps++;
      }
    }
    for (int i = 0; i < dataWSVars.length; i++) {
      String cond = (String) dataWSVars[i][0];
      if (StringUtilities.isNotEmpty(cond)) {
        condCountVars++;
      }
    }

    int msvarscount = 0;
    for (int i = 0; i < dataMSVars.length; i++) {
      String msprop = (String)dataMSVars[i][0];
      String msvar = (String)dataMSVars[i][1];
      if (StringUtilities.isNotEmpty(msprop) &&
          StringUtilities.isNotEmpty(msvar) &&
          !StringUtils.equals(msprop, sMAIL_START_VARS_PREFIX + i) &&
          !StringUtils.equals(msvar, sMAIL_START_VARS_PREFIX + i)) {
        msvarscount+=2;
      }
    }
    
    int attrSize = (condCountProps * 2) + 
      condCountVars + dataDetail.length + MAILSTART_INFO_PROP_COUNT + msvarscount + 3;
    String[][] newAttributes = new String[attrSize][3];

    for (int i = 0, j = 0; i < dataProps.length; i++) {
      String cond = (String) dataProps[i][0];
      String msg = (String) dataProps[i][1];

      if (StringUtilities.isNotEmpty(cond) && StringUtilities.isNotEmpty(msg)) {
        newAttributes[2*j][0] = AlteraAtributosStart.sSETTING + j;
        newAttributes[2*j][1] = cond;
        newAttributes[2*j][2] = ""; //$NON-NLS-1$
        newAttributes[2*j+1][0] = AlteraAtributosStart.sSETTING_DESC + j;
        newAttributes[2*j+1][1] = msg;
        newAttributes[2*j+1][2] = ""; //$NON-NLS-1$
        j++;
      }
    }
    int beginIndex = 2 * condCountProps;
    for (int i = 0, j = 0; i < dataWSVars.length; i++) {
      String cond = (String) dataWSVars[i][0];

      if (StringUtilities.isNotEmpty(cond)) {
        newAttributes[beginIndex + j][0] = AlteraAtributosStart.sWSVAR + j;
        newAttributes[beginIndex + j][1] = cond;
        newAttributes[beginIndex + j][2] = ""; //$NON-NLS-1$
        j++;
      }
    }
    beginIndex += condCountVars;
    int j = 0;
    for(String [] elem : dataDetail) {
      newAttributes[beginIndex + j][0] = AlteraAtributosStart.sDETAIL + elem[0];
      newAttributes[beginIndex + j][1] = elem[1];
      newAttributes[beginIndex + j][2] = elem[2];
      j++;
    }

    beginIndex += dataDetail.length;
    for(int i=0, k=0; i < dataMSVars.length; i++) {
      String msprop = (String) dataMSVars[i][0];
      String msvar = (String) dataMSVars[i][1];
      if (StringUtilities.isNotEmpty(msprop) &&
          StringUtilities.isNotEmpty(msvar) &&
          !StringUtils.equals(msprop, sMAIL_START_VARS_PREFIX + k) &&
          !StringUtils.equals(msvar, sMAIL_START_VARS_PREFIX + k)) {
        newAttributes[beginIndex + k][0] = 
          AlteraAtributosStart.sMAIL_START_VARS_PREFIX + AlteraAtributosStart.MAILSTART_MAIL_PROP + i;
        newAttributes[beginIndex + k][1] = msprop;
        newAttributes[beginIndex + k][2] = AlteraAtributosStart.MAILSTART_MAIL_PROP;
        k++;
        newAttributes[beginIndex + k][0] = 
          AlteraAtributosStart.sMAIL_START_VARS_PREFIX + AlteraAtributosStart.MAILSTART_FLOW_VAR + i;
        newAttributes[beginIndex + k][1] = msvar;
        newAttributes[beginIndex + k][2] = AlteraAtributosStart.MAILSTART_FLOW_VAR;
        k++;
      }
    }
    
    beginIndex += msvarscount;
    newAttributes[beginIndex][0] = sMAIL_START_INFO_PREFIX + MAILSTART_FROM_EMAIL_PROP;
    newAttributes[beginIndex][1] = jtfMSFromEmail.getText();
    newAttributes[beginIndex][2] = MAILSTART_FROM_EMAIL_PROP;      
    beginIndex++;
    newAttributes[beginIndex][0] = sMAIL_START_INFO_PREFIX + MAILSTART_FROM_NAME_PROP;
    newAttributes[beginIndex][1] = jtfMSFromName.getText();
    newAttributes[beginIndex][2] = MAILSTART_FROM_NAME_PROP;      
    beginIndex++;
    newAttributes[beginIndex][0] = sMAIL_START_INFO_PREFIX + MAILSTART_SUBJECT_PROP;
    newAttributes[beginIndex][1] = jtfMSSubject.getText();
    newAttributes[beginIndex][2] = MAILSTART_SUBJECT_PROP;      
    beginIndex++;
    newAttributes[beginIndex][0] = sMAIL_START_INFO_PREFIX + MAILSTART_SENT_DATE_PROP;
    newAttributes[beginIndex][1] = jtfMSSentDate.getText();
    newAttributes[beginIndex][2] = MAILSTART_SENT_DATE_PROP;      
    beginIndex++;
    newAttributes[beginIndex][0] = sMAIL_START_INFO_PREFIX + MAILSTART_DOCS_PROP;
    newAttributes[beginIndex][1] = jtfMSDocs.getText();
    newAttributes[beginIndex][2] = MAILSTART_DOCS_PROP;      
    
    newAttributes[newAttributes.length-3][0]=sDETAIL_FORM_BID;
    newAttributes[newAttributes.length-3][1]=detailFormBID;
    newAttributes[newAttributes.length-3][2]=sDETAIL_FORM_BID;

    newAttributes[newAttributes.length-2][0]=sDETAIL_CLASS;
    newAttributes[newAttributes.length-2][1]=sDETAIL_CLASS_BLOCK_NAME;
    newAttributes[newAttributes.length-2][2]=sDETAIL_CLASS;

    newAttributes[newAttributes.length-1][0]=sDETAIL_FORM;
    newAttributes[newAttributes.length-1][1]=detailType;
    newAttributes[newAttributes.length-1][2]=sDETAIL_FORM;

    return newAttributes;
  }

  private List<Object[]> getDynamicVariables() {
    List<Object[]> retObj = new ArrayList<Object[]>();
    for(String var : DynamicBindDelegate.getDynamicVariables()) {
      retObj.add(new Object[]{
          var,
          null,
          DynamicBindDelegate.getDynamicVariableDataType(var),
          Boolean.FALSE,
          adapter.getString("DynamicBindDelegate.catalog." + var + ".publicname"),
          null });
    }
    return retObj;
  }
  
  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    int condCountProps = 0, condCountVars = 0, detailVars = 0, msVars = 0;
    Atributo a = null;

    for (int i = 0; atributos != null && i < atributos.size(); i++) {
      a = atributos.get(i);
      if (a == null || a.getNome() == null) continue;
      if (a.getNome().startsWith(AlteraAtributosStart.sSETTING) &&
          !a.getNome().startsWith(AlteraAtributosStart.sSETTING_DESC)) {
        condCountProps++;
      } else if (a.getNome().startsWith(AlteraAtributosStart.sWSVAR)) {
        condCountVars++;
      } else if (a.getNome().startsWith(AlteraAtributosStart.sDETAIL)) {
        detailVars++;
      } else if(a.getNome().equals(AlteraAtributosStart.sDETAIL_FORM)) {
        detailType = a.getValor();
      } else if(a.getNome().equals(AlteraAtributosStart.sDETAIL_FORM_BID)) {
        detailFormBID = a.getValor();
      } else if(a.getNome().startsWith(sMAIL_START_VARS_PREFIX + MAILSTART_MAIL_PROP)) {
        msVars++;
      }
    }
    
    dataProps  = new String[condCountProps][columnNamesProps.length];
    dataWSVars = new String[condCountVars][columnNamesVars.length];
    dataDetail = new String[detailVars][3]; // just like others
    dataMSVars = new Object[msVars][columnNamesMailStart.length];
    int detailPos = 0;
    for (int i = 0; atributos != null && i < atributos.size(); i++) {
      a = atributos.get(i);
      if (a == null || a.getNome() == null) continue;
      String name = a.getNome();
      String value = a.getValor();
      if (name.startsWith(sSETTING_DESC)) {
        dataProps[Integer.parseInt(name.substring(sSETTING_DESC.length()))][1] = new String(value);        
      }
      else if (name.startsWith(sSETTING)) {
        dataProps[Integer.parseInt(name.substring(sSETTING.length()))][0] = new String(value);        
      }
      else if (name.startsWith(sWSVAR)) {
        dataWSVars[Integer.parseInt(name.substring(sWSVAR.length()))][0] = new String(value);        
      } 
      else if (name.startsWith(sDETAIL)) {
        dataDetail[detailPos][0] = name.substring(sDETAIL.length());
        dataDetail[detailPos][1] = value;
        dataDetail[detailPos][2] = a.getDescricao();
        detailPos++;
      }
      else if (name.startsWith(sMAIL_START_INFO_PREFIX) && StringUtils.isNotEmpty(value)) {        
        if (StringUtils.equals(name, sMAIL_START_INFO_PREFIX + MAILSTART_FROM_EMAIL_PROP)) {
          jtfMSFromEmail.setText(value);
        }
        else if (StringUtils.equals(name, sMAIL_START_INFO_PREFIX + MAILSTART_FROM_NAME_PROP)) {
          jtfMSFromName.setText(value);
        }
        else if (StringUtils.equals(name, sMAIL_START_INFO_PREFIX + MAILSTART_SUBJECT_PROP)) {
          jtfMSSubject.setText(value);
        }
        else if (StringUtils.equals(name, sMAIL_START_INFO_PREFIX + MAILSTART_SENT_DATE_PROP)) {
          jtfMSSentDate.setText(value);
        }
        else if (StringUtils.equals(name, sMAIL_START_INFO_PREFIX + MAILSTART_DOCS_PROP)) {
          jtfMSDocs.setText(value);
        }
      }
      else if (name.startsWith(sMAIL_START_VARS_PREFIX)) {

        if (name.indexOf(MAILSTART_MAIL_PROP) > -1) {
          int length = (sMAIL_START_VARS_PREFIX + MAILSTART_MAIL_PROP).length();
          int idx = Integer.parseInt(name.substring(length));
          dataMSVars[idx][0] = new String(value);
        }
        else if (name.indexOf(MAILSTART_FLOW_VAR) > -1) {
          int length = (sMAIL_START_VARS_PREFIX + MAILSTART_FLOW_VAR).length();
          int idx = Integer.parseInt(name.substring(length));
          dataMSVars[idx][1] = new String(value);
        }
      }
    }
    if(desenho != null) {
      Collection<Atributo> catalogo = desenho.getCatalogue();
      List<Object[]> reservedData = getDynamicVariables();
      ArrayList<Object[]> dataAux = new ArrayList<Object[]>(catalogo.size());
      HashMap<Object,Object[]> removeAux = new HashMap<Object,Object[]>();
      for (Atributo attr : catalogo) {
        if(StringUtilities.isEmpty(attr.getNome())) continue;
        String sType = attr.getDataType();
        DataTypeEnum type = DataTypeEnum.getDataType(sType);
        Object[] elem = new Object[]{
            attr.getNome(), 
            attr.getInitValue(), 
            type, 
            attr.isSearchable(), 
            attr.getPublicName(),
            attr.getFormat(),
            };
        dataAux.add(elem);
        removeAux.put(elem[0], elem);
      }
      for (Object[] elem : reservedData) {
          if (elem.length > 0 && removeAux.containsKey(elem[0])) {
            dataAux.remove(removeAux.remove(elem[0]));
          }
      }
      this.reservedDataSize = reservedData.size();
      reservedData.addAll(dataAux);
      dataCatalog = reservedData.toArray(new Object[reservedData.size()][]);
      
      jTableCatalog = new MyJTableX(dataCatalog, columnNamesCatalog);
      jTableCatalog.setRowSelectionAllowed(true);
      jTableCatalog.setColumnSelectionAllowed(false);
      jTableCatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      MyColumnEditorModel catProps = new MyColumnEditorModel();
      catProps.addEditorForColumn(2, new ComboCellEditor(DataTypeEnum.values()));
      CheckBoxCellEditor checkSearchable = new  CheckBoxCellEditor();
      catProps.addEditorForColumn(3, checkSearchable);
      catProps.addEditorForColumn(5, new FormatsCellEditor(2));
      jTableCatalog.setMyColumnEditorModel(catProps);

      Color color = new Color(245,245,245);
      MyTableModel catalogModel = new SortFilterModel(columnNamesCatalog, dataCatalog, reservedDataSize);
      MyColumnRendererModel renderModel = new MyColumnRendererModel();
      TableCellRenderer renderCheck = new CheckBoxCellRenderer();
      renderModel.addRendererForColumn(3, renderCheck);
      renderModel.addRendererForColumn(5, new FormatsCellRenderer(2));
      // default values
      catalogModel.setDefaultValuesForRow(new Object[] {"","",DataTypeEnum.Text,Boolean.FALSE,"",""} );
      TableCellRenderer greyCellRenderer = new MyTableCellRenderer(color);
      TableCellRenderer greyCheckBoxRenderer = new CheckBoxCellRenderer(color, false);
      for(int row = 0; row < reservedDataSize; row++) {
        for(int col = 0; col < columnNamesCatalog.length; col++) {
          catalogModel.setCellEditable(row, col, false);
          if(col == 3) {
            renderModel.addRendererForCell(row, col, greyCheckBoxRenderer);
//            catalogModel.setCellEditable(row, col, true); TODO Implementar pesquisavel nas variáveis Bindable
          } else {
            renderModel.addRendererForCell(row, col, greyCellRenderer);            
          }
        }
      }
      jTableCatalog.setMyColumnRendererModel(renderModel);
      jTableCatalog.setModel(catalogModel);
      
      // Column Width
      for(int i = 0; i < 6; i++) {
        TableColumn column = jTableCatalog.getColumnModel().getColumn(i);
        switch(i) {
        case 0:  column.setPreferredWidth(100); break;
        case 1:  column.setPreferredWidth(5); break;
        case 2:  column.setPreferredWidth(17); break;
        case 3:  column.setPreferredWidth(5); break;
        case 4:  column.setPreferredWidth(5); break;
        case 5:  column.setPreferredWidth(5); break;
        default: column.setPreferredWidth(5);
        }
      }
      
      // ensure dataCatalog only carries pertinent data
      dataCatalog = dataAux.toArray(new Object[dataAux.size()][]);
    }

    jTableFlowProps = new MyJTableX(dataProps, columnNamesProps);
    jTableFlowProps.setModel(new MyTableModel(columnNamesProps, dataProps));
    jTableFlowProps.setRowSelectionAllowed(true);
    jTableFlowProps.setColumnSelectionAllowed(false);
    jTableFlowProps.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    MyColumnEditorModel rmProps = new MyColumnEditorModel();
    jTableFlowProps.setMyColumnEditorModel(rmProps);

    jTableWSVars= new MyJTableX(dataWSVars, columnNamesVars);
    jTableWSVars.setModel(new MyTableModel(columnNamesVars, dataWSVars));
    jTableWSVars.setRowSelectionAllowed(true);
    jTableWSVars.setColumnSelectionAllowed(false);
    jTableWSVars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    MyColumnEditorModel rmWSVars = new MyColumnEditorModel();
    jTableWSVars.setMyColumnEditorModel(rmWSVars);


    jTableMSVars= new MyJTableX(dataMSVars,  columnNamesMailStart);
    jTableMSVars.setModel(new MyTableModel(columnNamesMailStart, dataMSVars));
    jTableMSVars.setRowSelectionAllowed(true);
    jTableMSVars.setColumnSelectionAllowed(false);
    jTableMSVars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    MyColumnEditorModel rmMSVars = new MyColumnEditorModel();
    jTableMSVars.setMyColumnEditorModel(rmMSVars);

    
    jbInit();

    //this.setSize(480, 640);
    this.setSize(630, 640);
    setLocationRelativeTo(getParent());
    setVisible(true);
  }

  /**
   * jbInit
   */
  private void jbInit() {
    /* paineis */
    JPanel aux1 = new JPanel();
    this.getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    this.getContentPane().add(aux2, BorderLayout.EAST);
    JPanel aux3 = new JPanel();
    this.getContentPane().add(aux3, BorderLayout.NORTH);

    {
      if(desenho != null) {
        BorderLayout jPanelFlowPropsLayout = new BorderLayout();
        jPanelCatalog.setLayout(jPanelFlowPropsLayout);
        jPanelCatalog.add(jScrollCatalog, BorderLayout.CENTER);
        jScrollCatalog.getViewport().add(jTableCatalog);
        JPanel auxProps = new JPanel();

        jButtonCatalogAdd.setText("+"); //$NON-NLS-1$
        jButtonCatalogAdd.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonCatalogAddActionPerformed(e);
          }
        });
        auxProps.add(jButtonCatalogAdd);

        jButtonCatalogDel.setText("-"); //$NON-NLS-1$
        jButtonCatalogDel.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonCatalogDelActionPerformed(e);
          }
        });
        auxProps.add(jButtonCatalogDel);
        jPanelCatalog.add(auxProps, BorderLayout.SOUTH);
        jTabbedPane.addTab(adapter.getString("AlteraAtributosStart.catalog.tab.label"), jPanelCatalog); //$NON-NLS-1$
      }

      {
        BorderLayout jPanelFlowPropsLayout = new BorderLayout();
        jPanelFlowProps.setLayout(jPanelFlowPropsLayout);
        jPanelFlowProps.add(jScrollFlowProps, BorderLayout.CENTER);
        jScrollFlowProps.getViewport().add(jTableFlowProps);
        JPanel auxProps = new JPanel();

        jButtonPropsAdd.setText("+"); //$NON-NLS-1$
        jButtonPropsAdd.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonPropsAddActionPerformed(e);
          }
        });
        auxProps.add(jButtonPropsAdd);

        jButtonPropsDel.setText("-"); //$NON-NLS-1$
        jButtonPropsDel.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonPropsDelActionPerformed(e);
          }
        });
        auxProps.add(jButtonPropsDel);
        jPanelFlowProps.add(auxProps, BorderLayout.SOUTH);
      }
      jTabbedPane.addTab(adapter.getString("AlteraAtributosStart.flow.properties.tab.label"), jPanelFlowProps); //$NON-NLS-1$

      {
        BorderLayout jPanelWSVarsLayout = new BorderLayout();
        jPanelWSVars.setLayout(jPanelWSVarsLayout);
        jScrollWSVars.setPreferredSize(new java.awt.Dimension(174, 163));
        jScrollWSVars.getViewport().add(jTableWSVars);
        JPanel auxWS = new JPanel();
        FlowLayout auxWSLayout = new FlowLayout();
        auxWS.setLayout(auxWSLayout);

        jButtonWSVarAdd.setText("+"); //$NON-NLS-1$
        jButtonWSVarAdd.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonWSVarAddActionPerformed(e);
          }
        });
        auxWS.add(jButtonWSVarAdd);

        jButtonWSVarDel.setText("-"); //$NON-NLS-1$
        jButtonWSVarDel.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonWSVarDelActionPerformed(e);
          }
        });
        auxWS.add(jButtonWSVarDel);
        jPanelWSVars.add(auxWS, BorderLayout.SOUTH);
        jPanelWSVars.add(jScrollWSVars, BorderLayout.CENTER);
      }
      jTabbedPane.addTab(adapter.getString("AlteraAtributosStart.ws.vars.tab.label"), jPanelWSVars); //$NON-NLS-1$

      
      // MAIL START
      {


        JPanel jPanelMSInfo = new JPanel(new GridBagLayout());

        GridBagConstraints sC = new GridBagConstraints();
        sC.fill = GridBagConstraints.HORIZONTAL;
        sC.gridwidth = 1;
        
        // from email
        JLabel jLabel = new JLabel(adapter.getString("AlteraAtributosStart.mailstart.fromemail"));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        jLabel.setLabelFor(jtfMSFromEmail);
        jPanelMSInfo.add(jLabel, sC);
        // separator
        JPanel sizer = new JPanel();
        sizer.setSize(5, 1);
        jPanelMSInfo.add(sizer, sC);
        sC.gridwidth = GridBagConstraints.REMAINDER;
        jPanelMSInfo.add(jtfMSFromEmail, sC);
        sC.gridwidth = 1;
        
        // from name
        jLabel = new JLabel(adapter.getString("AlteraAtributosStart.mailstart.fromname"));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        jLabel.setLabelFor(jtfMSFromName);
        jPanelMSInfo.add(jLabel, sC);
        // separator
        sizer = new JPanel();
        sizer.setSize(5, 1);
        jPanelMSInfo.add(sizer, sC);
        sC.gridwidth = GridBagConstraints.REMAINDER;
        jPanelMSInfo.add(jtfMSFromName, sC);
        sC.gridwidth = 1;

        // subject
        jLabel = new JLabel(adapter.getString("AlteraAtributosStart.mailstart.subject"));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        jLabel.setLabelFor(jtfMSSubject);
        jPanelMSInfo.add(jLabel, sC);
        // separator
        sizer = new JPanel();
        sizer.setSize(5, 1);
        jPanelMSInfo.add(sizer, sC);
        sC.gridwidth = GridBagConstraints.REMAINDER;
        jPanelMSInfo.add(jtfMSSubject, sC);
        sC.gridwidth = 1;

        // Sent date
        jLabel = new JLabel(adapter.getString("AlteraAtributosStart.mailstart.sentdate"));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        jLabel.setLabelFor(jtfMSSentDate);
        jPanelMSInfo.add(jLabel, sC);
        // separator
        sizer = new JPanel();
        sizer.setSize(5, 1);
        jPanelMSInfo.add(sizer, sC);
        sC.gridwidth = GridBagConstraints.REMAINDER;
        jPanelMSInfo.add(jtfMSSentDate, sC);
        sC.gridwidth = 1;
        
        // Docs var
        jLabel = new JLabel(adapter.getString("AlteraAtributosStart.mailstart.docs"));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        jLabel.setLabelFor(jtfMSDocs);
        jPanelMSInfo.add(jLabel, sC);
        // separator
        sizer = new JPanel();
        sizer.setSize(5, 1);
        jPanelMSInfo.add(sizer, sC);
        sC.gridwidth = GridBagConstraints.REMAINDER;
        jPanelMSInfo.add(jtfMSDocs, sC);
        sC.gridwidth = 1;
        
        
        // prop mapping
        BorderLayout jPanelMSVarsLayout = new BorderLayout();
        JPanel jPanelMSVars = new JPanel();
        jPanelMSVars.setLayout(jPanelMSVarsLayout);
        JScrollPane jScrollMSVars = new JScrollPane();
        jScrollMSVars.setPreferredSize(new java.awt.Dimension(174, 163));
        jScrollMSVars.getViewport().add(jTableMSVars);
        JPanel auxMS = new JPanel();
        FlowLayout auxMSLayout = new FlowLayout();
        auxMS.setLayout(auxMSLayout);

        jButtonMSVarAdd.setText("+"); //$NON-NLS-1$
        jButtonMSVarAdd.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonMSVarAddActionPerformed(e);
          }
        });
        auxMS.add(jButtonMSVarAdd);

        jButtonMSVarDel.setText("-"); //$NON-NLS-1$
        jButtonMSVarDel.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonMSVarDelActionPerformed(e);
          }
        });
        auxMS.add(jButtonWSVarDel);
        jPanelMSVars.add(auxMS, BorderLayout.SOUTH);
        jPanelMSVars.add(jScrollMSVars, BorderLayout.CENTER);
        
        
        
        jPanelMS.setLayout(new BorderLayout());
        jPanelMS.add(jPanelMSInfo, BorderLayout.NORTH);        
        jPanelMS.add(jPanelMSVars, BorderLayout.CENTER);
        
      }
      jTabbedPane.addTab(adapter.getString("AlteraAtributosStart.mailstart.tab"), jPanelMS); //$NON-NLS-1$
      // MAIL START END

      
      
      
      {
        jPanelFlowDetail.setLayout(new GridBagLayout());
        ButtonGroup group = new ButtonGroup();
        // NO DETAIL
        JRadioButton noDetail = new JRadioButton(adapter.getString("AlteraAtributosStart.detail.radio.no"));
        noDetail.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            detailType = sDETAIL_FORM_NODETAIL;
            jButtonDetail.setEnabled(false);
            disableDetailFormBIDField();
          }
        });
        group.add(noDetail);
        // DEFAULT DETAIL
        JRadioButton defaultDetail = new JRadioButton(adapter.getString("AlteraAtributosStart.detail.radio.default"));
        defaultDetail.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            detailType = sDETAIL_FORM_DEFAULT;
            jButtonDetail.setEnabled(false);
            disableDetailFormBIDField();
          }
        });
        group.add(defaultDetail);
        // FORM DETAIL-CONFIGURE
        JRadioButton formDetail = new JRadioButton(adapter.getString("AlteraAtributosStart.detail.radio.form"));
        formDetail.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            detailType = sDETAIL_FORM_BLOCKFORM;
            jButtonDetail.setEnabled(true);
            disableDetailFormBIDField();
          }
        });
        group.add(formDetail);
        jButtonDetail.setText(adapter.getString("AlteraAtributosStart.detail.configureForm"));
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jButtonDetailActionPerformed(e);
            disableDetailFormBIDField();
          }
        });
        jButtonDetail.setEnabled(false);
        // FORM DETAIL-BLOCKID
        JRadioButton existFormDetail = new JRadioButton(adapter.getString("AlteraAtributosStart.detail.radio.existingform"));
        existFormDetail.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            detailType = sDETAIL_FORM_EXISTINGBLOCKFORM;
            jButtonDetail.setEnabled(false);
            jTextBID.setEnabled(true);
          }
        });
        group.add(existFormDetail);
        disableDetailFormBIDField();
        
        // END DETAIL SETUP        
        
        String s = detailType;
        if(null == s) s = sDETAIL_FORM_DEFAULT;
        if(StringUtilities.isEqualIgnoreCase(s, sDETAIL_FORM_BLOCKFORM)) {
          formDetail.setSelected(true);
          jButtonDetail.setEnabled(true);
        } else if (StringUtilities.isEqualIgnoreCase(s, sDETAIL_FORM_NODETAIL)) {
          noDetail.setSelected(true);
        } else if (StringUtilities.isEqualIgnoreCase(s, sDETAIL_FORM_EXISTINGBLOCKFORM)) {
          existFormDetail.setSelected(true);
          jTextBID.setText(detailFormBID);
          jTextBID.setEnabled(true);
        } else { 
          defaultDetail.setSelected(true);
        }

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2,2,2,2);
        gc.gridx = 0;
        gc.gridy = 0;
        jPanelFlowDetail.add(new JLabel(adapter.getString("AlteraAtributosStart.detail.label")), gc);
        gc.gridwidth = 1;
        gc.gridy = 1;
        jPanelFlowDetail.add(noDetail, gc);
        gc.gridy = 2;
        jPanelFlowDetail.add(defaultDetail, gc);
        gc.gridy = 3;
        jPanelFlowDetail.add(formDetail, gc);
        gc.gridx = 1;
        jPanelFlowDetail.add(jButtonDetail, gc);
        gc.gridy = 4;
        gc.gridx = 0;
        jPanelFlowDetail.add(existFormDetail, gc);
        gc.gridx = 1;
        jPanelFlowDetail.add(jTextBID, gc);
      }
      jTabbedPane.addTab(adapter.getString("AlteraAtributosStart.detail.tab"), jPanelFlowDetail); //$NON-NLS-1$

      
      
    }

    this.getContentPane().add(jTabbedPane, BorderLayout.CENTER);

    {
      jButtonOK.setText(OK);
      jButtonOK.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButtonOKActionPerformed(e);
        }
      });
      FlowLayout jButtonPanelLayout = new FlowLayout();
      jButtonPanel.setLayout(jButtonPanelLayout);
      jButtonPanel.add(jButtonOK);
      jButtonPanel.add(jButtonCancel);

      jButtonCancel.setText(Cancelar);
      jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButtonCancelActionPerformed(e);
        }
      });
    }
    getContentPane().add(jButtonPanel, BorderLayout.SOUTH);
    repaint();
  }
  
  private void disableDetailFormBIDField() {
    jTextBID.setText("");
    jTextBID.setEnabled(false);
  }
  
  private Object[][] getDataCatalog() {
    List<Object[]> retObj = new ArrayList<Object[]>();
    String[] dynVars = DynamicBindDelegate.getDynamicVariables();
    if(dataCatalog.length > dynVars.length) {
      for(int i = dynVars.length, lim = dataCatalog.length; i < lim; i++) {
        retObj.add(dataCatalog[i]);
      }
    }
    return retObj.toArray(new Object[retObj.size()][]);
  }
  
  private String[] getCatalogueDynamicVariables() {
    List<String> retObj = new ArrayList<String>();
    for(Object[] item : getDataCatalog()) {
      if (item.length > 0) {
        for(String var : DynamicBindDelegate.getDynamicVariables()) {
          if(StringUtils.equals(var, ("" + item[0]).trim())) {
            retObj.add(var);
            break;
          }
        }
      }
    }
    return retObj.toArray(new String[retObj.size()]);
  }
  
  private String[] getCatalogueRepeatedVariables() {
    List<String> retObj = new ArrayList<String>();
    List<String> tmpData = new ArrayList<String>();
    
    for(Object[] item : getDataCatalog()) {
      if (item.length > 0) {
        String var = ("" + item[0]).trim();
        if(StringUtils.isNotBlank(var)) {
          if(tmpData.contains(var)) {
            if(!retObj.contains(var)) {
              retObj.add(var);
            }
          } else {
            tmpData.add(var);
          }
        }
      }
    }
    return retObj.toArray(new String[retObj.size()]);
  }
  
  private boolean validateData() {
    StringBuffer errMsg = new StringBuffer();
    String lineBreak = System.getProperty("line.separator");
    
    String[] catalogueDynamicVariables = getCatalogueDynamicVariables();
    boolean dataIsValid = (catalogueDynamicVariables.length == 0);
    if(!dataIsValid) {
      for(String var : catalogueDynamicVariables) {
        errMsg.append(var).append(" ");
        errMsg.append(adapter.getString("DynamicBindDelegate.dialog.error.reason.reserved"));
        errMsg.append(" :: ").append(adapter.getString("DynamicBindDelegate.catalog." + var + ".publicname"));
        errMsg.append(lineBreak);
      }
    }
    
    String[] catalogueRepeatedVariables = getCatalogueRepeatedVariables();
    dataIsValid = (dataIsValid && (catalogueRepeatedVariables.length == 0));
    if(!dataIsValid) {
      for(String var : catalogueRepeatedVariables) {
        errMsg.append(var).append(" ");
        errMsg.append(adapter.getString("DynamicBindDelegate.dialog.error.reason.duplicated"));
        errMsg.append(lineBreak);
      }
    }


    if(!dataIsValid) {
      String title = adapter.getString("DynamicBindDelegate.dialog.error.title");
      StringBuffer message = new StringBuffer();
      message.append(adapter.getString("DynamicBindDelegate.dialog.error.message"));
      message.append(lineBreak);
      message.append(errMsg);
      JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    else {
      detailFormBID = jTextBID.getText();
      boolean detailOk = !(detailType == sDETAIL_FORM_EXISTINGBLOCKFORM && (StringUtils.isEmpty(detailFormBID) || !StringUtils.isNumeric(detailFormBID)));
      dataIsValid = (dataIsValid && detailOk);
      if(!dataIsValid) {
        // FIXME - mensagens!!
        String title = adapter.getString("AlteraAtributosStart.detail.error.title");
        StringBuffer message = new StringBuffer();
        message.append(adapter.getString("AlteraAtributosStart.detail.error.invalidExistingForm"));
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
      }
    }
    
    return dataIsValid;
  }

  /* OK */
  private void jButtonOKActionPerformed(ActionEvent e) {
    if(validateData()) {
      if(null != desenho) {
        jTableCatalog.stopEditing();
      }
      jTableFlowProps.stopEditing();
      jTableWSVars.stopEditing();
      exitStatus=EXIT_STATUS_OK;
      setVisible(false);
      dispose();
    }
  }

  /* Cancelar */
  private void jButtonCancelActionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }

  /* Detalhe */
  private void jButtonDetailActionPerformed(ActionEvent e) {
    ArrayList<Atributo> attrs = new ArrayList<Atributo>(dataDetail.length);
    for(Object[] elem : dataDetail) {
      attrs.add(adapter.newAtributo((String)elem[0],(String)elem[1],(String)elem[2]));
    }

    AlteraAtributosInterface alteraAttrs = new AlteraAtributoDetail(adapter);
    alteraAttrs.setDataIn("Detalhe de Processo", attrs);
    if (alteraAttrs.getExitStatus() == AlteraAtributosInterface.EXIT_STATUS_OK) {
      String [][] detail = alteraAttrs.getNewAttributes();
      dataDetail = new String[detail.length][3];
      for(int i = 0; i < detail.length;i++) {
        String [] elem = detail[i];
        String desc = elem[0];
        if(elem.length > 2)
          desc = elem[2];
        dataDetail[i][0] = elem[0];
        dataDetail[i][1] = elem[1];
        dataDetail[i][2] = desc;
      }
    }

  }

  /* + */
  private void jButtonCatalogAddActionPerformed(ActionEvent e) {
    //Add a row to the table
    MyTableModel tm = (MyTableModel)jTableCatalog.getModel();
    dataCatalog = tm.insertRow();
  }

  /* - */
  private void jButtonCatalogDelActionPerformed(ActionEvent e) {
    int rowSelected = jTableCatalog.getSelectedRow();

    if (rowSelected != -1 && rowSelected >= this.reservedDataSize) {
      MyTableModel tm = (MyTableModel)jTableCatalog.getModel();
      dataCatalog = tm.removeRow(rowSelected);
    }
  }

  /* + */
  private void jButtonPropsAddActionPerformed(ActionEvent e) {
    //Add a row to the table
    MyTableModel tm = (MyTableModel)jTableFlowProps.getModel();
    dataProps = tm.insertRow();
  }

  /* - */
  private void jButtonPropsDelActionPerformed(ActionEvent e) {
    int rowSelected = jTableFlowProps.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel)jTableFlowProps.getModel();
      dataProps = tm.removeRow(rowSelected);
    }
  }

  /* + */
  private void jButtonWSVarAddActionPerformed(ActionEvent e) {
    //Add a row to the table
    MyTableModel tm = (MyTableModel)jTableWSVars.getModel();
    dataWSVars = tm.insertRow();
  }

  /* - */
  private void jButtonWSVarDelActionPerformed(ActionEvent e) {
    int rowSelected = jTableWSVars.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel)jTableWSVars.getModel();
      dataWSVars = tm.removeRow(rowSelected);
    }
  }

  /* mailstart + */
  private void jButtonMSVarAddActionPerformed(ActionEvent e) {
    //Add a row to the table
    MyTableModel tm = (MyTableModel)jTableMSVars.getModel();
    dataMSVars = tm.insertRow();
  }

  /* mailstart - */
  private void jButtonMSVarDelActionPerformed(ActionEvent e) {
    int rowSelected = jTableMSVars.getSelectedRow();

    if (rowSelected != -1) {
      MyTableModel tm = (MyTableModel)jTableMSVars.getModel();
      dataMSVars = tm.removeRow(rowSelected);
    }
  }

}