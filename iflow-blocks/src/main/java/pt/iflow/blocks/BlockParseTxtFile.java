package pt.iflow.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.StringUtilities;

/**
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockParseTxtFile extends Block {
  public Port portIn, portSuccess, portError;

  private static final String PATH = "Path";
  private static final String DOCUMENT = "Document";
  private static final String VARIABLES = "Variables";
  private static final String START = "Start";
  private static final String END = "End";
  private static final String SEPARATOR = "Separator";

  public BlockParseTxtFile(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
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

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    Documents docBean = BeanFactory.getDocumentsBean();

    String sDocumentVar = this.getAttribute(DOCUMENT);
    String sVariablesVar = this.getAttribute(VARIABLES);
    String sStartVar = this.getAttribute(START);
    String sEndVar = this.getAttribute(END);
    String sSeparatorVar = this.getAttribute(SEPARATOR);

    if (StringUtilities.isEmpty(sDocumentVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for Document attribute");
      outPort = portError;
    } else if (StringUtilities.isEmpty(sVariablesVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for Variables attribute");
      outPort = portError;
    } else if ((StringUtilities.isEmpty(sStartVar) && StringUtilities.isEmpty(sEndVar)) && StringUtilities.isEmpty(sSeparatorVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for file definition attributes");
      outPort = portError;
    } else
      try {
        Map<String, String> inputArgs = new HashMap<String, String>();
        ParseTxtUtil ptu = new ParseTxtUtil();
        ProcessListVariable docsVar = procData.getList(sDocumentVar);
        Document doc = docBean.getDocument(userInfo, procData, ((Long) docsVar.getItem(0).getValue()).intValue());

        String[] listNames = procData.transform(userInfo, sVariablesVar).split(",");

        inputArgs.put(ParseTxtUtil.PARAM_DYNAMIC_SEPARATOR, procData.transform(userInfo, sSeparatorVar));
        inputArgs.put(ParseTxtUtil.PARAM_FIXED_INIT_FIELD, procData.transform(userInfo, sStartVar));
        inputArgs.put(ParseTxtUtil.PARAM_FIXED_END_FIELD, procData.transform(userInfo, sEndVar));
        inputArgs.put(ParseTxtUtil.PARAM_NUMBER_COLLUMNS, "" + listNames.length);
        inputArgs.put(ParseTxtUtil.PARAM_DOCUMENT, new String(doc.getContent()));
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) ptu.execute(inputArgs);

        for (int i = 0; i < listNames.length; i++) {
          String varName = listNames[i];
          ProcessListVariable listVar = procData.getList(varName);
          for (int j = 0; j < result.get(i).size(); j++)
            listVar.parseAndSetItemValue(j, result.get(i).get(j));
        }

        outPort = portSuccess;

      } catch (Exception e) {
        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
        outPort = portError;
      }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  class ParseTxtUtil {

    public static final String PARAM_NUMBER_COLLUMNS = "_number_collumns";
    public static final String PARAM_DOCUMENT = "_document";
    public static final String PARAM_DYNAMIC_SEPARATOR = "_separator";
    public static final String PARAM_FIXED_INIT_FIELD = "_fixed_init_field";
    public static final String PARAM_FIXED_END_FIELD = "_fixed_end_field";
    public static final String FIXED_LIMITER = ",";

    protected List execute(Map<String, String> inputArgs) throws Exception {

      String inputSeparator = (String) inputArgs.get(PARAM_DYNAMIC_SEPARATOR);

      if (inputSeparator == null || inputSeparator.equals("")) {
        return executeFixed(inputArgs);
      } else {
        return executeDynamic(inputArgs);
      }
    }

    /**
     * Method that parse a file with Dynamic field length
     * 
     * @param inputArgs
     * @return
     * @throws Exception
     */
    private List executeDynamic(Map<String, String> inputArgs) throws Exception {

      String separator = inputArgs.get(PARAM_DYNAMIC_SEPARATOR);

      // field PARAM_DYNAMIC_SEPARATOR must have some value
      if (separator == null || "".equals(separator)) {
        throw new Exception("PARAM_DYNAMIC_SEPARATOR value is invalid [" + separator + "]");
      }

      Integer n_cols = Integer.parseInt(inputArgs.get(PARAM_NUMBER_COLLUMNS));

      Scanner scanner = new Scanner((String) inputArgs.get(PARAM_DOCUMENT));
      ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>(n_cols);
      for (int i = 0; i < n_cols; i++)
        outputList.add(new ArrayList<String>());

      // parsing the line
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] lineSplit = line.split(separator);

        // for each field the value is stored on the fields list
        for (int i = 0; i < n_cols; i++) {
          // first time data creation
          // Atention: the first line cannot have less fields than the
          // rest of the document
          if (outputList.get(i) == null)
            outputList.set(i, new ArrayList<String>());

          String token = "";
          try {
            token = lineSplit[i];
          } catch (Exception e) {
            token = "";
          }

          outputList.get(i).add(token);

        }
      }
      return outputList;
    }

    private List executeFixed(Map<String, String> inputArgs) throws Exception {

      String initPositionsStr = inputArgs.get(PARAM_FIXED_INIT_FIELD);
      String[] initPositions = initPositionsStr.split(FIXED_LIMITER);

      String endPositionsStr = inputArgs.get(PARAM_FIXED_END_FIELD);
      String[] endPositions = endPositionsStr.split(FIXED_LIMITER);

      // Get the last position to read.
      int lineMinSize = Integer.parseInt(endPositions[endPositions.length - 1]) + 1;

      if (initPositions.length != endPositions.length) {
        throw new Exception("The PARAM_FIXED_INIT_FIELD's size is diferent " + "from PARAM_FIXED_END_FIELD");
      }

      List<List> outputList = new ArrayList<List>(initPositions.length);
      for (int i = 0; i < initPositions.length; i++) {
        outputList.add(new ArrayList<String>());
      }

      Scanner scanner = new Scanner((String) inputArgs.get(PARAM_DOCUMENT));

      // parsing the line
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.length() < lineMinSize) {
          throw new Exception("Line [" + line + "] is smaller than [" + lineMinSize + "]");
        }

        for (int i = 0; i < initPositions.length; i++) {
          List<String> fieldList = outputList.get(i);

          int begin = Integer.parseInt(initPositions[i]);
          int end = Integer.parseInt(endPositions[i]) + 1;

          fieldList.add(line.substring(begin, end));
        }
      }
      return outputList;
    }
  }
}
