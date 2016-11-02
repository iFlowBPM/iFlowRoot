package pt.iflow.blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

public class BlockCriarXlsInSheet extends Block {
  public Port portIn, portOutOk, portOutError;

  private static final String FILENAME = "filename";
  private static final String SHEETNAME = "sheetname";
  private static final String OVERWRITE = "overwrite";
  private static final String VARIABLE = "variable";
  private static final String COLUMN_NAMES = "columnNames";
  private static final String COLUMN_VALUES = "columnValues";

  public BlockCriarXlsInSheet(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
  }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portOutOk;
    retObj[1] = portOutError;
    return retObj;
  }

  @Override
  public Port getEventPort() {
    return null;
  }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = this.portOutError;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    String filename = getAttribute(FILENAME);
    String sheetname = getAttribute(SHEETNAME);
    String overwrite = getAttribute(OVERWRITE);
    String var = getAttribute(VARIABLE);
    String sColNames = getAttribute(COLUMN_NAMES);
    String sColValues = getAttribute(COLUMN_VALUES);
    ProcessListVariable variable = null;
    if (StringUtils.isBlank(filename) || StringUtils.isBlank(sheetname) || StringUtils.isBlank(var) || StringUtils.isBlank(sColNames)
        || StringUtils.isBlank(sColValues)) {
      Logger.error(login, this, "after", "Unable to process data into file: must set variables (found: filename="
          + filename + "; sheetname="+ sheetname + "; variable=" + var + "; colNames=" + sColNames + "; colValues=" + sColValues + ")");
    } else {
      variable = procData.getList(var);
      if (variable == null) {
        Logger.error(login, this, "after", "Unable to process data into file: unknown variable (found: variable=" + var
            + ")");
      } else {
        try {
          filename = procData.transform(userInfo, filename);
          sheetname = procData.transform(userInfo, sheetname);
          sColNames = procData.transform(userInfo, sColNames);
          String[] colNames = sColNames.split(",");
          String[] colValues = sColValues.split(",");
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          String sError = BlockData.exportToSpreadSheet(this, userInfo, filename, parseToDataTable(userInfo, procData,
              colNames, colValues), out);
          if (StringUtils.isNotBlank(sError)) {
            logMsg.append("Unable to export: " + sError);
            Logger.warning(login, this, "after", "Unable to export: " + sError);
          } else {
        	//get newly created sheet
        	InputStream is = new ByteArrayInputStream(out.toByteArray());
        	HSSFWorkbook wb = new HSSFWorkbook(is);  
        	HSSFSheet createdSheet = wb.getSheetAt(0);          	
        	//open document as workbook
        	HSSFWorkbook oldWb=null;
        	try{
	        	Integer docid = Integer.parseInt( "" + variable.getItemValue(variable.size()-1));
	        	Document oldDocument = BeanFactory.getDocumentsBean().getDocument(userInfo, procData, docid);
	        	InputStream oldIs = new ByteArrayInputStream(oldDocument.getContent());
	        	oldWb = new HSSFWorkbook(oldIs);    
        	} catch (Exception e) {
        		oldWb = new HSSFWorkbook();
        	}
        	
        	//add new sheet content
        	HSSFSheet tempSheet = oldWb.createSheet(sheetname);
        	for (int row=0; row<= createdSheet.getLastRowNum(); row++){
        		HSSFRow auxRow = tempSheet.createRow(row);
        		for(short cell=0; cell <= createdSheet.getRow(row).getLastCellNum(); cell++){
        			HSSFCell auxCell = auxRow.createCell(cell);
        			if(createdSheet.getRow(row).getCell(cell)==null)
        				auxCell.setCellValue( "" );
        			else
        				auxCell.setCellValue( createdSheet.getRow(row).getCell(cell).toString() );
        		}
        	}
        	
        	//get new workbook byte content	
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	oldWb.write(bos);
        	bos.close();
        	  
            DocumentData newDocument = new DocumentData();
            newDocument.setFileName(filename);
            newDocument.setContent(bos.toByteArray());
            newDocument.setUpdated(Calendar.getInstance().getTime());
            Document savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, newDocument);
            try {
        	  if (StringUtils.equalsIgnoreCase("true", procData.transform(userInfo, overwrite)))
        		  variable.clear();	
              variable.parseAndAddNewItem(String.valueOf(savedDocument.getDocId()));
              outPort = this.portOutOk;
              logMsg.append("Added '" + savedDocument.getDocId() + "' to '" + var + "';");
            } catch (Exception e) {
              Logger.error(userInfo.getUtilizador(), this, "after", "error parsing document "
                  + savedDocument.getDocId(), e);
            }
          }
        } catch (Exception e) {
          Logger.error(login, this, "after",
              "Unable to process data into file: error processing file (found: filename=" + filename + "; variable="
                  + var + "; colNames=" + sColNames + "; colValues=" + sColValues + ")", e);
        }
      }
    }
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Criar Documento XLS");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Criar Documento XLS Efectuado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private List<List<String>> parseToDataTable(UserInfoInterface userInfo, ProcessData procData, String[] colNames,
      String[] colValues) {
    List<List<String>> retObj = new ArrayList<List<String>>();
    List<String> lColNames = new ArrayList<String>();
    for (String colName : colNames) {
      lColNames.add(colName.trim());
    }
    retObj.add(lColNames);
    int maxSize = getMaxSize(procData, colValues);
    int pos = 0;
    while (pos < maxSize) {
      List<String> lRowValues = new ArrayList<String>();
      for (String colValue : colValues) {
        colValue = colValue.trim();
        ProcessListItem item = procData.getListItem(colValue, pos);
        lRowValues.add(item == null ? "" : "" + item.getValue());
      }
      retObj.add(lRowValues);
      pos++;
    }
    return retObj;
  }

  private int getMaxSize(ProcessData procData, String[] colValues) {
    int maxSize = 0;
    for (String colValue : colValues) {
      ProcessListVariable list = procData.getList(colValue.trim());
      if (list != null && list.size() > maxSize) {
        maxSize = list.size();
      }
    }
    return maxSize;
  }
}