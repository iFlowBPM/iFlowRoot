package pt.iflow.blocks;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockData</p>
 * <p>Description: Bloco "biblioteca" para manipulação de dados</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockData extends Block {

  final static String FMT_TEXT = "TEXT";
  final static String FMT_INTEGER = "INT";
  final static String FMT_NUMBER = "NUMBER";
  final static String FMT_DATE = "DATE";
  
  
  private static class UnclosableInputStream extends BufferedInputStream {

    public UnclosableInputStream(InputStream in) {
      super(in);
    }

    public UnclosableInputStream(InputStream in, int i) {
      super(in, i);
    }

    public void close() {
    }

  }

  static interface DataAdapter {

    public abstract boolean loadData(byte [] data, Properties properties);

    public abstract int sheetCount();
    public abstract int rowCount(int sheet);
    public abstract int colCount(int sheet);

    public abstract Object getValue(int sheet, int row, int col);

  }

  // DataAdapter and SAX parser handler for Open Document Spreadsheet
  static class ODSAdapter extends DefaultHandler implements ContentHandler, DataAdapter {

    private List<List<String>> data = null;
    private List<String> line = null;
    private int cols = -1;
    private int lineNum = -1;
    private int colNum = -1;
    private boolean allDone = true;
    private int getText = 0;
    private int repeat = 0;
    private StringBuffer curText = null;

    private final static String tableTag = "table:table";
    private final static String rowTag = "table:table-row";
    private final static String cellTag = "table:table-cell";
    private final static String dataTag = "text:p";
    private final static String ATTR_VALUE = "office:value";
    private final static String ATTR_REPEAT = "table:number-columns-repeated";

    public void startDocument () throws SAXException {
      data = new ArrayList<List<String>>();
      colNum = 0;
      lineNum = 0;
      cols = 0;
      allDone = false;
      getText = 0;
      repeat=-1;
    }

    public void endDocument () throws SAXException {
      Logger.debug("", this, "", "DATA: " + data);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if(allDone) return;
      if(StringUtils.equalsIgnoreCase(tableTag, qName)) {
        // begining table
        lineNum=-1;
        cols = -1;
      } else if(StringUtils.equalsIgnoreCase(rowTag, qName)) {
        lineNum++;
        colNum=-1;
        line = new ArrayList<String>();
        data.add(line);
      } else if(StringUtils.equalsIgnoreCase(cellTag, qName)) {
        colNum++;
        getText = 0;
        curText = null;
        repeat = 1;
        
        String attrRep = atts.getValue(ATTR_REPEAT);
        if (NumberUtils.isNumber(attrRep)) {
          try {
            repeat = Integer.parseInt(attrRep);
          }
          catch (Exception e) {}
        }
        
        String attrValue = atts.getValue(ATTR_VALUE);
        if (StringUtils.isNotEmpty(attrValue)) {
          for (int i=0; i < repeat; i++) {
            line.add(attrValue);          
          }
        }
        else {
          curText = new StringBuffer();
        }
        
      } else if(StringUtils.equalsIgnoreCase(dataTag, qName)) {
        getText++;
      }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
      if(allDone) return;

      if(StringUtils.equalsIgnoreCase(tableTag, qName)) {
        allDone = true; 
      } else if(StringUtils.equalsIgnoreCase(rowTag, qName)) {
        if(cols < line.size()) cols = line.size();
      } else if(StringUtils.equalsIgnoreCase(cellTag, qName)) {
        getText = 0;
        if (curText != null) {
          for (int i=0; i < repeat; i++) {
            line.add(curText.toString());
          }
        }
        else {
          // already processed in startElement
        }
      } else if(StringUtils.equalsIgnoreCase(dataTag, qName)) {
        getText--;
      }
    }

    public void characters (char buf [], int start, int length) throws SAXException {
      if(allDone || getText <= 0 || curText == null) return;
      curText.append(buf, start, length);
    }

    public int colCount(int sheet) {
      return cols;
    }

    public Object getValue(int sheet, int row, int col) {
      String result = null;
      try {
        result = data.get(row).get(col);
      } catch (Throwable t) {} // catch nullpointer and array out of bounds
      return result;
    }

    public int sheetCount() {
      return 1;
    }
    
    public int rowCount(int sheet) {
      int size = 0;
      if(null != data) size = data.size();
      return size;
    }


    // ODS - OpenDocument Spreadsheet parser/importer
    private void parseODS(byte [] odsData) throws Exception {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      // if ods is xml...
      if ('P' == odsData[0] && 'K' == odsData[1]) { // zip file "magic" bytes
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(odsData));
        ZipEntry e = null;
        boolean found = false;
        while((e = zin.getNextEntry()) != null) {
          if (e.getName().equalsIgnoreCase("content.xml")) {
            // found content
            Logger.debug("", this, "", "found content tag. Parsing...");
            parser.parse(new UnclosableInputStream(zin), this);
            found = true;
            break;
          }
        }
        zin.close();
        if(!found) {
          Logger.debug("", this, "", "Content not found...");
        }
      } else {
        throw new Exception("Unparsable file type");
      }
    }

    public boolean loadData(byte[] data, Properties props) {
      boolean result = false;
      try {
        parseODS(data);
        result = true;
      } catch (Throwable t) {
        t.printStackTrace();
        result = false;
      }

      return result;
    }

  }


  // DataAdapter and SAX parser handler for ooxml
  static class XLSXAdapter extends DefaultHandler implements ContentHandler, DataAdapter {

    private List<List<String>> data = new ArrayList<List<String>>();
    private List<String> line = null;
    private int cols = -1;
    private int lineNum = -1;
    private int colNum = -1;
    private boolean allDone = true;
    private int getText = 0;
    private StringBuffer curText = null;

    private final String tableTag = "sheetData";
    private final String rowTag = "row";
    private final String cellTag = "c";
    private final String dataTag = "v";
    private final String txtAttr = "t";
    private Map<String,String> sharedStrings = new HashMap<String, String>();
    private Set<String> isShared = new HashSet<String>();
    private int stringId = 0;

    private boolean sharedStringMode = false;


    public void startDocument () throws SAXException {
      colNum = 0;
      lineNum = 0;
      cols = 0;
      allDone = false;
      getText = 0;
      stringId = 0;
    }

    public void endDocument () throws SAXException {
      Logger.debug("", this, "", "DATA: " + data);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if(allDone) return;

      if(sharedStringMode) {
        if(StringUtils.equalsIgnoreCase("si", qName)) {
          curText = new StringBuffer();
          getText = 0;
        } else if(StringUtils.equalsIgnoreCase("t", qName)) {
          getText++;
        }
      } else {
        if(StringUtils.equalsIgnoreCase(tableTag, qName)) {
          // begining table
          lineNum=-1;
          cols = -1;
        } else if(StringUtils.equalsIgnoreCase(rowTag, qName)) {
          lineNum++;
          colNum=-1;
          line = new ArrayList<String>();
        } else if(StringUtils.equalsIgnoreCase(cellTag, qName)) {
          colNum++;
          String txt = atts.getValue(uri, txtAttr);  // in ooxml text element are stored in attributes :(
          curText = new StringBuffer();
          if(StringUtils.equalsIgnoreCase(txt, "s")) {
            String shared = data.size()+";"+line.size();
            isShared.add(shared);
            Logger.debug("", this, "", "This one is shared: "+shared);
          }
          getText = 0;
        } else if(StringUtils.equalsIgnoreCase(dataTag, qName)) {
          getText++;
        }
      }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
      if(allDone) return;

      if(sharedStringMode) {
        if(StringUtils.equalsIgnoreCase("si", qName)) {
          getText = 0;
          sharedStrings.put(String.valueOf(stringId), curText.toString());
          Logger.debug("", this, "", "String id: "+stringId+" = "+curText);
          stringId++;
        } else if(StringUtils.equalsIgnoreCase("t", qName)) {
          getText--;
        }
      } else {
        if(StringUtils.equalsIgnoreCase(tableTag, qName)) {
          allDone = true; 
        } else if(StringUtils.equalsIgnoreCase(rowTag, qName)) {
          data.add(line);
          if(cols < line.size()) cols = line.size();
          Logger.debug("", this, "", "Added row: "+line);
        } else if(StringUtils.equalsIgnoreCase(cellTag, qName)) {
          getText=0;
          Logger.debug("", this, "", "Text is: "+curText);
          line.add(curText.toString());
        } else if(StringUtils.equalsIgnoreCase(dataTag, qName)) {
          getText--;
        }
      }
    }

    public void characters (char buf [], int start, int length) throws SAXException {
      if(allDone || getText <= 0) return;
      curText.append(buf, start, length);
    }

    public int sheetCount() {
      return 1;
    }
    
    public int colCount(int sheet) {
      return cols;
    }

    public Object getValue(int sheet, int row, int col) {
      String result = null;
      try {
        result = data.get(row).get(col);
        if(isShared.contains(row+";"+col))
          result = sharedStrings.get(result);
      } catch (Throwable t) {} // catch nullpointer and array out of bounds
      return result;
    }

    public int rowCount(int sheet) {
      int size = 0;
      if(null != data) size = data.size();
      return size;
    }

    
    private void parsePOI(byte [] odsData) throws IOException, InvalidFormatException{
    	ByteArrayInputStream bai=new ByteArrayInputStream(odsData);
    	org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(bai);
        Sheet worksheet = workbook.getSheetAt(0);			     
        SimpleDateFormat sdf = new SimpleDateFormat(Const.sDEF_DATE_FORMAT);
        for(int i=0; i<worksheet.getPhysicalNumberOfRows(); i++){
        	Row row1 = worksheet.getRow(i);
        	ArrayList<String> tempRowData = new ArrayList<String>();
        	for(int j=0; j < row1.getLastCellNum(); j++){
        		Cell cellA1 = row1.getCell((short) j);
        		if (cellA1==null)
        			tempRowData.add("");
        		else{   
        			if (cellA1.getCellType()==Cell.CELL_TYPE_STRING)
        				tempRowData.add(cellA1.getStringCellValue());
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cellA1))
        				tempRowData.add(sdf.format(DateUtil.getJavaDate(cellA1.getNumericCellValue())));
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_NUMERIC)
        				tempRowData.add("" + new BigDecimal( cellA1.getNumericCellValue()));
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_BLANK)
        				tempRowData.add("");
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_BOOLEAN)
        				tempRowData.add("" + cellA1.getBooleanCellValue());
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_FORMULA)
        				tempRowData.add("" + cellA1.getCellFormula());
        			else if (cellA1.getCellType()==Cell.CELL_TYPE_ERROR)
        				tempRowData.add("");
        			}
        	}
        	data.add(tempRowData);
        }             
    }
    // XSLX - Excel 2007 Spreadsheet parser/importer
    private void parseXSLX(byte [] odsData) throws Exception {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      // if ods is xml...
      if ('P' == odsData[0] && 'K' == odsData[1]) { // zip file "magic" bytes
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(odsData));
        ZipEntry e = null;
        boolean found = false;
        while((e = zin.getNextEntry()) != null) {
          if (e.getName().equalsIgnoreCase("xl/worksheets/sheet1.xml")) {
            // found content
            Logger.debug("", this, "", "found content tag. Parsing...");
            sharedStringMode=false;
            //parser.parse(new UnclosableInputStream(zin), this);
            parsePOI(odsData);            
            found = true;
          } else if (false && e.getName().equalsIgnoreCase("xl/sharedStrings.xml")) {
            // found content
            Logger.debug("", this, "", "shares strings content tag. Parsing...");
            sharedStringMode=true;
            //parser.parse(new UnclosableInputStream(zin), this);
            parsePOI(odsData);
          }
        }
        zin.close();
        if(!found) {
          Logger.debug("", this, "", "Content not found...");
        }
      } else {
        throw new Exception("Unparsable file type");
      }
    }

    public boolean loadData(byte[] data, Properties props) {
      boolean result = false;
      try {
        parseXSLX(data);
        result = true;
      } catch (Throwable t) {
        t.printStackTrace();
        result = false;
      }

      return result;
    }
  }

  static class HSSFAdapter implements DataAdapter {

    private List<List<List<Object>>> data = null;
    private Map<Integer,Integer> rowcount = new HashMap<Integer, Integer>();
    private Map<Integer,Integer> colcount = new HashMap<Integer, Integer>();

    public boolean loadData(byte[] data, Properties props) {
      boolean result = false;

      try {
        POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(data));
        HSSFWorkbook workBook = new HSSFWorkbook(fs);
        
        int sheets = workBook.getNumberOfSheets();
        this.data = new ArrayList<List<List<Object>>>(sheets);
        for(int sheetNum = 0; sheetNum < sheets; sheetNum++) {
          HSSFSheet sheet = workBook.getSheetAt(sheetNum);
          int colCount = -1;
          int rowCount = sheet.getPhysicalNumberOfRows();
          List<List<Object>> sheetList = new ArrayList<List<Object>>(rowCount);

          for(int i=0; i<sheet.getPhysicalNumberOfRows(); i++){
          	Row row1 = sheet.getRow(i);
          	List<Object> line = new ArrayList<Object>();
          	if(colCount<row1.getLastCellNum())
          		colCount=row1.getLastCellNum();
          	for(int j=0; j < row1.getLastCellNum(); j++){
          		Cell cell = row1.getCell((short) j);
          		if (cell==null)
          			line.add("");
          		else{
          			int type = cell.getCellType();
                    Object value = "";

                    switch (type) {
                    case HSSFCell.CELL_TYPE_STRING:
                      value = cell.getRichStringCellValue().getString();
                      break;
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                      value = new Boolean(cell.getBooleanCellValue());
                      break;
                    case HSSFCell.CELL_TYPE_NUMERIC:
                      if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue();
                        
                      } else if(cell.getCellStyle().getDataFormatString().contains("%")){
                    	 value = (new BigDecimal( cell.getNumericCellValue())).multiply(new BigDecimal(100)) + "%" ;
                      } else {
//                        Number val = cell.getNumericCellValue();
//                        value = val;
                    	 value = new BigDecimal( cell.getNumericCellValue());
                      }
                      break;
                    case HSSFCell.CELL_TYPE_ERROR:
                      value = "#ERR" + cell.getErrorCellValue();
                      break;
                    case HSSFCell.CELL_TYPE_FORMULA:
                      value = cell.getCellFormula();
                      break;
                    case HSSFCell.CELL_TYPE_BLANK:
                    default:
                      value = ""; // NOT SUPPORTED
                    }
                    
                    line.add(value);
          		}
          			
          	}
          	sheetList.add(line);
          }
          
          
//          for (Iterator<?> rit = sheet.rowIterator(); rit.hasNext(); ) {
//            HSSFRow row = (HSSFRow)rit.next();
//            List<Object> line = new ArrayList<Object>();
//            for (Iterator<?> cit = row.cellIterator(); cit.hasNext(); ) {
//              HSSFCell cell = (HSSFCell)cit.next();
//              int type = cell.getCellType();
//              Object value = "";
//
//              switch (type) {
//              case HSSFCell.CELL_TYPE_STRING:
//                value = cell.getRichStringCellValue().getString();
//                break;
//              case HSSFCell.CELL_TYPE_BOOLEAN:
//                value = new Boolean(cell.getBooleanCellValue());
//                break;
//              case HSSFCell.CELL_TYPE_NUMERIC:
//                if(HSSFDateUtil.isCellDateFormatted(cell)) {
//                  value = cell.getDateCellValue();
//                } else {
//                  Number val = cell.getNumericCellValue();
//                  value = val;
//                }
//                break;
//              case HSSFCell.CELL_TYPE_ERROR:
//                value = "#ERR" + cell.getErrorCellValue();
//                break;
//              case HSSFCell.CELL_TYPE_FORMULA:
//                // value = cell.getCellFormula();
//                // break;
//              case HSSFCell.CELL_TYPE_BLANK:
//              default:
//                value = ""; // NOT SUPPORTED
//              }
//
//              line.add(value);
//            }
//            sheetList.add(line);
//            if(colCount < line.size()) colCount = line.size();
//          }
          this.rowcount.put(sheetNum, rowCount);
          this.colcount.put(sheetNum, colCount);
          this.data.add(sheetList);
        }

        result = true;
      } catch(Throwable t) {
        t.printStackTrace();
        result = false;
      }

      return result;
    }

    public Object getValue(int sheet, int row, int col) {
      Object result = null;
      try {
        result = data.get(sheet).get(row).get(col);
      } catch (Throwable t) {} // catch nullpointer and array out of bounds
      return result;
    }

    public int sheetCount() {
      return data.size();
    }
    
    public int colCount(int sheet) {
      return colcount.get(sheet);
    }


    public int rowCount(int sheet) {
      return rowcount.get(sheet);
    }

  }


  static class CSVAdapter implements DataAdapter {

    private int colCount;
    private List<List<String>> data = null;

    /** The rather involved pattern used to match CSV's consists of three
     * alternations: the first matches aquoted field, the second unquoted,
     * the third a null field.
     * The separator char replaces {0} and the quote char replaces {1}
     */
    public static final String CSV_PATTERN = "{1}([^{1}]+?){1}{0}?|([^{0}]+){0}?|{0}";


    public boolean loadData(byte[] data, Properties props) {
      boolean result = false;

      char cDelim = ';';
      char cQuote = '"';
      
      String delim = ";";
      String quote = "\\\"" ; // this is \" to escape " in Regular Expression
      String encoding = "Windows-1252";

      try {
        BufferedReader isr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), encoding));
        this.data = new ArrayList<List<String>>();

        Pattern p = Pattern.compile(MessageFormat.format(CSV_PATTERN, new Object[]{delim,quote}));


        String sLine = null;
        while((sLine = isr.readLine())!=null) {
          List<String> line = new ArrayList<String>();
          Matcher m = p.matcher(sLine);

          while(m.find()) {
            String val = m.group();
            int l = val.length();
            if(l > 0) {
              if(val.charAt(l-1)==cDelim) {
                val = val.substring(0, l-1);
                l--;
              }
            }
            if(l > 1) {
              if(val.charAt(0)==cQuote && val.charAt(l-1) == cQuote) {
                val = val.substring(1, l-1);
              }
            }
            line.add(val);
          }

          if(colCount < line.size()) colCount = line.size();

          this.data.add(line);
        }

        isr = null;
        result = true;
      } catch(Throwable t) {
        t.printStackTrace();
        result = false;
      }

      return result;
    }

    public Object getValue(int sheet, int row, int col) {
      String result = null;
      try {
        result = data.get(row).get(col);
      } catch (Throwable t) {} // catch nullpointer and array out of bounds
      return result;
    }

    public int sheetCount() {
      return 1;
    }
    
    public int colCount(int sheet) {
      return colCount;
    }

    public int rowCount(int sheet) {
      if(null == data) return 0;
      return data.size();
    }

  }


  public static DataAdapter getDataAdapter(String fmtName) {
    if(StringUtils.equalsIgnoreCase(fmtName, "ODS"))
      return new ODSAdapter();
    else if(StringUtils.equalsIgnoreCase(fmtName, "XLS"))
      return new HSSFAdapter();
    else if(StringUtils.equalsIgnoreCase(fmtName, "XLSX"))
      return new XLSXAdapter();
    else if(StringUtils.equalsIgnoreCase(fmtName, "CSV"))
      return new CSVAdapter();
    return null;
  }


  public Port portIn, portSuccess, portError;

  private static final String sCSV_SEPARATOR = ";";

  protected static final int nEXPORT = 0;
  protected static final int nIMPORT = 1;

  protected static final int nIMPORT_MODE_WRITE_ALWAYS = 0;
  protected static final int nIMPORT_MODE_WRITE_ONCE = 1;

  protected static final int nCOLUMN_LABEL = 0;
  protected static final int nCOLUMN_NUMBER = 1;

  protected static final int nVARS_IDX = 0;
  protected static final int nNAMES_IDX = 1;

  protected static final String sSEPARATOR = ",";

  protected static final String sDIR = "Data";
  protected static final String sJSP_EXPORT = "export.jsp";
  protected static final String sJSP_IMPORT = "import.jsp";

  private int _nMode = -1;

  protected BlockData(int anMode, int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = true;
    this._nMode = anMode;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    String description   = this.getDescription(userInfo, procData);
    String url           = this.getUrl(userInfo, procData);

    Logger.trace(this,"before", login + " call with subpid="+subpid+",pid="+pid+",flowid="+flowid);

    String nextPage = url;

    try {
      // Get the ProcessManager EJB
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      Activity activity = new Activity(login,flowid,pid,subpid,0,0,description,Block.getDefaultUrl(userInfo, procData),1);
      activity.mid = procData.getMid();
      pm.updateActivity(userInfo,activity);

    }
    catch (Exception e) {
      Logger.error(login, this, "before", 
          procData.getSignature() + "Caught an unexpected exception scheduling activities: " + e.getMessage(), e);
    }

    return nextPage;
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    return outPort;
  }


  /**
   * Exports a given content to Excel to a given output stream
   *
   * @param userInfo the calling user info
   * @param asSheetName the excel sheet name
   * @param asXmlRowIdentifier the xml row tag identifier
   * @param asXmlColumnIdentifier the xml column tag identifier
   * @param asXmlValueIdentifier the xml value tag identifier
   * @param asXml the content in the form of xml
   * @param psOut the output stream to write to
   * 
   * @return error if it exists or null
   */
  public String exportToSpreadSheet(UserInfoInterface userInfo, String asSheetName, String asXmlRowIdentifier, String asXmlColumnIdentifier, String asXmlValueIdentifier, String asXml, OutputStream psOut) {
    return exportToSpreadSheet(this, userInfo, asSheetName, asXmlRowIdentifier, asXmlColumnIdentifier, asXmlValueIdentifier, asXml, psOut);
  }

  /**
   * Exports a given content to Excel to a given output stream
   *
   * @param abBlock the calling object
   * @param userInfo the calling user info
   * @param asSheetName the excel sheet name
   * @param asXmlRowIdentifier the xml row tag identifier
   * @param asXmlColumnIdentifier the xml column tag identifier
   * @param asXmlValueIdentifier the xml value tag identifier
   * @param asXml the content in the form of xml
   * @param psOut the output stream to write to
   * 
   * @return error if it exists or null
   */
  public static String exportToSpreadSheet(Block abBlock, UserInfoInterface userInfo, String asSheetName, String asXmlRowIdentifier, String asXmlColumnIdentifier, String asXmlValueIdentifier, String asXml, OutputStream psOut) {

    String retObj = null;

    ArrayList<List<String>> alData = new ArrayList<List<String>>();
    ArrayList<String> altmp = null;

    try {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      //Obtain an instance of a DocumentBuilder from the factory.
      DocumentBuilder db = dbf.newDocumentBuilder();
      //Parse the document.
      // InputStream isInStream = new ByteArrayInputStream(asXml.getBytes());
      StringReader sr = new StringReader(asXml);
      Document doc = db.parse(new InputSource(sr));

      NodeList nl = doc.getElementsByTagName(asXmlRowIdentifier);

      NodeList nl2 = null;
      NodeList nl3 = null;
      NodeList nl4 = null;
      Node n = null;
      Node n2 = null;
      Node n3 = null;
      Node n4 = null;
      for (int row=0; row < nl.getLength(); row++) {
        // process rows
        n = nl.item(row);

        altmp = new ArrayList<String>();

        nl2 = n.getChildNodes();

        for (int col=0; col < nl2.getLength(); col++) {
          n2 = nl2.item(col);

          if (n2.getNodeName().equals(asXmlColumnIdentifier)) {
            nl3 = n2.getChildNodes();

            for (int col2=0; col2 < nl3.getLength(); col2++) {
              n3 = nl3.item(col2);

              if (n3.getNodeName().equals(asXmlValueIdentifier)) {
                nl4 = n3.getChildNodes();
                n4 = nl4.item(0);

                if (n4 == null || nl4.getLength() != 1 || n4.getChildNodes().getLength() > 0) {
                  // process only "single" value fields
                  altmp.add("");
                }
                else {
                  altmp.add(n4.getNodeValue());
                }
              }
            }
          }
        }
        alData.add(altmp);
      }

      retObj = exportToSpreadSheet(abBlock,userInfo,asSheetName,alData,psOut);
    }
    catch (Exception e) {
      retObj = "Erro ao processar o xml do campo: " + e.getMessage();
    }

    return retObj;
  }

  /**
   * Exports a given content to Excel to a given output stream (generic method)
   *
   * @param userInfo the calling user info
   * @param asSheetName the excel's sheet name
   * @param aalValues the row list (each row is another arraylist with values for each column)
   * @param psOut the output stream to write to
   * 
   * @return error if it exists or null
   */
  public String exportToSpreadSheet(UserInfoInterface userInfo, String asSheetName, List<List<String>> aalValues, OutputStream psOut) {
    return exportToSpreadSheet(this,userInfo,asSheetName,aalValues,psOut);    
  }


  /**
   * Exports a given content to Excel to a given output stream (generic method)
   *
   * @param userInfo the calling user info
   * @param asSheetName the excel's sheet name
   * @param aalValues the row list (each row is another arraylist with values for each column)
   * @param psOut the output stream to write to
   * 
   * @return error if it exists or null
   */
  @SuppressWarnings("unchecked")
  public static String exportToSpreadSheet(Block abBlock, UserInfoInterface userInfo, String asSheetName, List<List<String>> aalValues, OutputStream psOut) {
    String[] saSheets = new String[1];
    saSheets[0] = asSheetName;
    List<List<String>>[] aaValues = new List[1];
    aaValues[0] = aalValues;

    return BlockData.exportToSpreadSheet(abBlock, userInfo, saSheets, aaValues, psOut);    

  }    

  public static String exportToSpreadSheet(Block abBlock, UserInfoInterface userInfo, String[] asaSheetName, List<List<String>>[] aalaValues, OutputStream psOut) {


    if (Const.nEXPORT_MODE == Const.nEXPORT_MODE_CSV) {
      return exportToCSV(abBlock, userInfo, asaSheetName, aalaValues, psOut);
    }
    else if (Const.nEXPORT_MODE == Const.nEXPORT_MODE_EXCEL) {
      if (Const.nEXCEL_LIBRARY == Const.nEXCEL_LIBRARY_JXL) {
        return exportToSpreadSheetJXL(abBlock, userInfo, asaSheetName, aalaValues, psOut);
      }
      else if (Const.nEXCEL_LIBRARY == Const.nEXCEL_LIBRARY_POI) {
        return exportToSpreadSheetPOI(abBlock, userInfo, asaSheetName, aalaValues, psOut);
      }
    }
    return null;
  }

  private static String exportToCSV(Block abBlock, UserInfoInterface userInfo, String[] asaSheetName, List<List<String>>[] aalaValues, OutputStream psOut) {
    String retObj = null;

    PrintWriter aWriter = new PrintWriter(psOut);
    try {
      List<List<String>> alValues = null;
      List<String> altmp = null;
      String stmp = null;
      StringBuffer sbtmp = null;

      for (int sheet=0; sheet < aalaValues.length; sheet++) {

        alValues = aalaValues[sheet];

        // rows
        for (int row=0; row < alValues.size(); row++) {
          altmp = alValues.get(row);
          sbtmp = new StringBuffer();
          for (int col=0; altmp != null && col < altmp.size(); col++) {
            stmp = (String)altmp.get(col);
            if (stmp == null) stmp = "";

            if (stmp.indexOf(sCSV_SEPARATOR) > -1 ||
                stmp.indexOf("\"") > -1) {

              // replace " by ""
              Utils.replaceString(stmp, "\"", "\"\"");

              // append " at start and end
              stmp = "\"" + stmp + "\"";
            }

            sbtmp.append(stmp).append(sCSV_SEPARATOR);
          }
          aWriter.println(sbtmp.toString());
        }      
      }

      aWriter.close();

    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), abBlock, "exportToCSV", "caught exception: ", e);
      retObj = "Ocorreu um erro ao exportar: " + e.getMessage();
    }

    return retObj;
  }

  private static String exportToSpreadSheetJXL(Block abBlock, UserInfoInterface userInfo, String[] asaSheetName, List<List<String>>[] aalaValues, OutputStream psOut) {

    String retObj = null;

    try {
      WritableWorkbook workbook = Workbook.createWorkbook(psOut); 
      WritableSheet wsSheet = null;
      Label label = null;
      List<List<String>> alValues = null;
      List<String> altmp = null;
      String stmp = null;      


      for (int sheet=0; sheet < asaSheetName.length; sheet++) {

        wsSheet = workbook.createSheet(asaSheetName[sheet],0);
        alValues = aalaValues[sheet];

        // rows
        for (int row=0; row < alValues.size(); row++) {
          altmp = alValues.get(row);
          for (int col=0; altmp != null && col < altmp.size(); col++) {
            stmp = altmp.get(col);
            if (stmp == null) stmp = "";
            label = new Label(col, row, StringEscapeUtils.unescapeHtml(stmp));
            wsSheet.addCell(label);
          }
        }  

        workbook.write();
      }

      workbook.close();

      psOut.close();

    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), abBlock, "exportToExcelJXL", 
          "caught exception: " + e.getMessage(), e);
      retObj = "Ocorreu um erro ao exportar: " + e.getMessage();
    }

    return retObj;
  }


  @SuppressWarnings("deprecation")
  private static String exportToSpreadSheetPOI(Block abBlock,
      UserInfoInterface userInfo, 
      String[] asaSheetName,
      List<List<String>>[] aalaValues, 
      OutputStream psOut) {
    String retObj = null;

    try {
      List<List<String>> alValues = null;
      List<String> alRow = null;
      String stmp = null;      

      HSSFWorkbook wb = new HSSFWorkbook();

      HSSFSheet hSheet = null;
      HSSFRow hRow = null;
      HSSFCell hCell = null;

      for (int sheet=0; sheet < asaSheetName.length; sheet++) {

        String sheetName = validatePOISheetName(asaSheetName[sheet]);
        
        hSheet = wb.createSheet(sheetName);
        alValues = aalaValues[sheet];

        for (int row=0; row < alValues.size(); row++) {
          hRow = hSheet.createRow((short)row);
          alRow = alValues.get(row);
          for (int col=0; alRow != null && col < alRow.size(); col++) {
            stmp = alRow.get(col);
            if (stmp == null) stmp = "";

            hCell = hRow.createCell((short)col);
            hCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            hCell.setCellValue(stmp);
          }
        }      
      }

      wb.write(psOut);

      psOut.close();

    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), abBlock, "exportToExcelPOI", 
          "caught exception: " + e.getMessage(), e);
      retObj = "Ocorreu um erro ao exportar: " + e.getMessage();
    }

    return retObj;
  }

  
  private static String validatePOISheetName(String origName) {
    // Sheet name cannot be blank, greater than 31 chars, or contain any of /\*?[]
    if (StringUtils.isEmpty(origName))
      return "Sheet";
    
    String ret = origName;
    if (ret.length() > 31) {
      ret = ret.substring(0,30);
    }
    
    ret = StringUtils.replaceChars(ret, '/', '_');
    ret = StringUtils.remove(ret, "\\");
    ret = StringUtils.remove(ret, "*");
    ret = StringUtils.remove(ret, "?");
    ret = StringUtils.remove(ret, "[");
    ret = StringUtils.remove(ret, "]");
    
    return ret;
  }

  /**
   * // ahmVars:  key=colname (String); value=varname (String)
   * // ahmModes: key=varname (String); value=import mode (Integer)
   * // returns String with error or, if no errors occur, null
   * 
   * @param abBlock
   * @param userInfo
   * @param procData
   * @param ahmVars
   * @param ahmModes
   * @param dataAdapter
   * @param iStartLine
   * @param iColLabelType
   * @param bHasHeader
   * @return
   */ 
  protected static String importFromAdapter(Block abBlock,
      UserInfoInterface userInfo,
      ProcessData procData,
      Map<String,String> ahmVars,
      Map<String,Integer> ahmModes,
      DataAdapter dataAdapter,
      int sheet,
      int iStartLine,
      int iColLabelType,
      boolean bHasHeader,
      Map<String,Format> formatters
  ) {

    String retObj = null;

    try {

      HashSet<String> hsToProcess = new HashSet<String>(ahmVars.keySet());
      int nLastRow = dataAdapter.rowCount(sheet);

      HashMap<String, Integer> hmColInfo = new HashMap<String, Integer>();

      if(iColLabelType == nCOLUMN_LABEL) {
        bHasHeader = true;
        for (int i=0; i < dataAdapter.colCount(sheet); i++) {
          String colname = String.valueOf(dataAdapter.getValue(sheet, 0, i));
          if (hsToProcess.contains(colname)) {
            hsToProcess.remove(colname);
            hmColInfo.put(ahmVars.get(colname), i);
          }
        }
      }
      else {
        Iterator<String> iter = ahmVars.keySet().iterator();
        while (iter != null && iter.hasNext()) {
          String sColnum = iter.next();
          String var = (String)ahmVars.get(sColnum);
          hmColInfo.put(var, Integer.parseInt(sColnum) - 1); // index at 0
        }
      }
      // column name row processed

      iStartLine--; // 0 indexed
      int curRow = 0;
      if(curRow<iStartLine)
        curRow = iStartLine;

      if(bHasHeader)
        curRow++;

      if (hmColInfo.size() > 0) {
        // sheet has columns with desired content

        for (int irow=0; curRow < nLastRow; irow++, curRow++) {
          Iterator<String> iter = hmColInfo.keySet().iterator();
          while (iter != null && iter.hasNext()) {
            String var = iter.next();
            int col = hmColInfo.get(var);
            Format fmt = formatters.get(var);

            Object oVal = dataAdapter.getValue(sheet, curRow, col);
            String value = null;
            if(null == fmt) {
              value = oVal == null?"":String.valueOf(oVal);
            } else {
              try {
                value = fmt.format(oVal);
              } catch(Throwable t) {
                if(null != oVal)
                  value = String.valueOf(oVal);
              }
            }

            int mode = BlockData.nIMPORT_MODE_WRITE_ALWAYS;
            if (ahmModes.containsKey(var)) {
              mode = ahmModes.get(var);
            }

            boolean updateDS = (BlockData.nIMPORT_MODE_WRITE_ALWAYS==mode);
            if (mode == BlockData.nIMPORT_MODE_WRITE_ONCE) {
              updateDS = (procData.getListItem(var, irow) == null);  // vou acreditar que isto funciona...
            }


            if (updateDS) {
            	// write always or first time
            	procData.getList(var).parseAndSetItemValue(irow, value); 
            }
          } // while
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      Logger.error(userInfo.getUtilizador(), abBlock, "importFromExcelPOI", 
          "caught exception: " + e.getMessage(),e);
      retObj = "Ocorreu um erro ao importar: " + e.getMessage();
    }

    return retObj;
  }


  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    if (this._nMode == BlockData.nEXPORT) {
      return this.getDesc(userInfo, procData, true, "Exportar Dados");
    }
    else {
      return this.getDesc(userInfo, procData, true, "Importar Dados");
    }
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    if (this._nMode == BlockData.nEXPORT) {
      return this.getDesc(userInfo, procData, false, "Exportação de Dados efectuada");
    }
    else {
      return this.getDesc(userInfo, procData, false, "Importação de Dados efectuada");
    }
  }


  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    int flowid   = procData.getFlowId();
    int pid      = procData.getPid();
    int subpid   = procData.getSubPid();

    String sJSP = null;
    if (this._nMode == BlockData.nEXPORT) {
      sJSP = BlockData.sJSP_EXPORT;
    }
    else {
      sJSP = BlockData.sJSP_IMPORT;
    }

    return sDIR + "/" + sJSP + "?flowid="+ flowid+"&pid="+ pid+"&subpid="+subpid;
  }


  public Object execute (int op, Object[] aoa) {
    return null;
  }


  public static void main (String[] args) throws Throwable {
    try {
      ByteArrayOutputStream bous = new ByteArrayOutputStream();

      FileInputStream fin = new FileInputStream("/iKnow/work/projects/iFlowRoot/iFlowHome/content.xml");
      byte [] d = new byte[4096];
      int r = 0;
      while((r = fin.read(d)) != -1)
        bous.write(d, 0, r);
      fin.close();

      new ODSAdapter().loadData(bous.toByteArray(), new Properties());

    } catch (Throwable t) {
      t.printStackTrace();
    }

  }

}
