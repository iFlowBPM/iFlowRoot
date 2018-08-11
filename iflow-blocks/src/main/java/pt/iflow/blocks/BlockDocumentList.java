package pt.iflow.blocks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.BlockAttributes;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;

public class BlockDocumentList extends Block {
  public Port portIn, portSuccess, portError;

  private static final String AUTHENTICATION = "l_AUTH";
  private static final String USER = "l_USER";
  private static final String CHAVE = "l_PASS";
  
  public BlockDocumentList(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    saveFlowState = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getInPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getEventPort()
   */
  public Port getEventPort() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getOutPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#before(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#after(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
	  
    Port retObj = portError;
    String login = userInfo.getUtilizador();
    if (Logger.isDebugEnabled()) {
      Logger.debug(login, this, "after", procData.getSignature() + "Entered method!");
    }
    try {
      StringBuffer filter = new StringBuffer();
      String text = this.getParsedAttribute(userInfo, BlockAttributes.DOC_FILTER, procData);
      if (StringUtils.isNotBlank(text)) {
        filter.append("+TEXT:\"" + text + "\" ");
      }
      String title = this.getParsedAttribute(userInfo, BlockAttributes.DOC_TITLE, procData);
      String description = this.getParsedAttribute(userInfo, BlockAttributes.DOC_DESCRIPTION, procData);
      String author = this.getParsedAttribute(userInfo, BlockAttributes.DOC_AUTHOR, procData);
      SimpleDateFormat format = new SimpleDateFormat("'\"'dd/MM/yyyy'\"'");
      Date createdFrom = null;
      try {
        createdFrom = format.parse(this.getParsedAttribute(userInfo, BlockAttributes.DOC_CREATED_FROM, procData));
      } catch (ParseException e) {
      }
      Date createdTo = null;
      try {
        createdTo = format.parse(this.getParsedAttribute(userInfo, BlockAttributes.DOC_CREATED_TO, procData));
      } catch (ParseException e) {
      }
      Date modifiedFrom = null;
      try {
        modifiedFrom = format.parse(this.getParsedAttribute(userInfo, BlockAttributes.DOC_MODIFIED_FROM, procData));
      } catch (ParseException e) {
      }
      Date modifiedTo = null;
      try {
        modifiedTo = format.parse(this.getParsedAttribute(userInfo, BlockAttributes.DOC_MODIFIED_TO, procData));
      } catch (ParseException e) {
      }
      
     
      DMSCredential credentialAux = null;
      if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
    	  credentialAux =  DMSConnectorUtils.createCredentialAuth(this.getAttribute(USER), this.getAttribute(CHAVE));
      }else{
    	  credentialAux =  DMSConnectorUtils.createCredential(userInfo, procData);  
      }
      
      ContentResult content = DMSUtils.getInstance().getAllDescendents(
    		  credentialAux , filter.toString(),
          this.getParsedAttribute(userInfo, BlockAttributes.DOC_PATH, procData));
      Criteria criteria = new Criteria(title, description, author, createdFrom, createdTo, modifiedFrom, modifiedTo);
      List<String> titles = new ArrayList<String>();
      List<String> descriptions = new ArrayList<String>();
      List<String> authors = new ArrayList<String>();
      List<String> ids = new ArrayList<String>();
      List<String> names = new ArrayList<String>();
      List<String> urls = new ArrayList<String>();
      List<String> paths = new ArrayList<String>();
      List<String> creates = new ArrayList<String>();
      List<String> mods = new ArrayList<String>();
      List<List<String>> dynProps = new ArrayList<List<String>>();
      SimpleDateFormat formatter = new SimpleDateFormat(Const.sDEF_DATE_FORMAT);
      for (ContentResult leaf : getAllLeafs(content)) {
        if (criteria.validate(leaf)) {
          titles.add(StringUtils.isEmpty(leaf.getTitle()) ? "" : leaf.getTitle());
          descriptions.add(StringUtils.isEmpty(leaf.getDescription()) ? "" : leaf.getDescription());
          authors.add(StringUtils.isEmpty(leaf.getAuthor()) ? "" : leaf.getAuthor());
          ids.add(StringUtils.isEmpty(leaf.getId()) ? "" : leaf.getId());
          names.add(StringUtils.isEmpty(leaf.getName()) ? "" : leaf.getName());
          urls.add(StringUtils.isEmpty(leaf.getUrl()) ? "" : leaf.getUrl());
          paths.add(StringUtils.isEmpty(leaf.getPath()) ? "" : leaf.getPath());
          creates.add(leaf.getCreateDate() == null ? "" : formatter.format(leaf.getCreateDate()));
          mods.add(leaf.getModifiedDate() == null ? "" : formatter.format(leaf.getModifiedDate()));
          List<String> horizontal = extractProperties(userInfo, procData, leaf.getComments());
          if (dynProps.isEmpty()) {
            for (int i = 0, lim = horizontal.size(); i < lim; i++) {
              dynProps.add(new ArrayList<String>());
            }
          }
          for (int i = 0, lim = dynProps.size(); i < lim; i++) {
            dynProps.get(i).add(horizontal.get(i));
          }
        }
      }
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_IDS), ids);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_TITLES), titles);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_DESCRIPTIONS), descriptions);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_AUTHORS), authors);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_NAME), names);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_URL), urls);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_PATH), paths);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_CREATED), creates);
      this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_OUTPUT_MODIFIED), mods);
      for (int i = 0, lim = dynProps.size(); i < lim; i++) {
        this.outputValues(userInfo, procData, this.getAttribute(BlockAttributes.DOC_PROP_VALUE_PREFIX + i), dynProps.get(i));
      }
      retObj = portSuccess;
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }
    this.addToLog("Using '" + retObj.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    if (Logger.isDebugEnabled()) {
      Logger.debug(login, this, "after", procData.getSignature() + "Leaving method!");
    }
    return retObj;
  }

  private void outputValues(UserInfoInterface userInfo, ProcessData procData, String variable, List<String> list) {
    if (StringUtils.isNotBlank(variable)) {
      ProcessListVariable varList = procData.getList(variable);
      if (varList != null) {
        varList.clear();
        for (String value : list) {
          ProcessListItem item = varList.addNewItem(value);
          this.addToLog("Set '" + variable + "[" + item.getPosition() + "]" + "' as '" + item.getValue() + "';");
          if (Logger.isDebugEnabled()) {
            Logger.debug(userInfo.getUtilizador(), this, "outputValues", procData.getSignature() + "Set '" + variable + "["
                + item.getPosition() + "]" + "' as '" + item.getValue());
          }
        }
      }
    }
  }

  private List<ContentResult> getAllLeafs(ContentResult current) {
    List<ContentResult> retObj = new ArrayList<ContentResult>();
    if (current.isLeaf()) {
      retObj.add(current);
    } else {
      for (ContentResult child : current.getChildren()) {
        retObj.addAll(getAllLeafs(child));
      }
    }
    return retObj;
  }

  private List<String> extractProperties(UserInfoInterface userInfo, ProcessData procData, Properties properties) {
    List<String> retObj = new ArrayList<String>();
    String name = null;
    for (int i = 0; StringUtils.isNotEmpty(name = this.getParsedAttribute(userInfo, BlockAttributes.DOC_PROP_NAME_PREFIX + i, procData)); i++) {
      if (!name.startsWith("{")) {
        name = "{" + DMSUtils.DEFAULT_NAMESPACE + "}" + name;
      }
      retObj.add(properties.containsKey(name) ? properties.get(name).toString() : "");
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#canProceed(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getDescription(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Document List");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document List Efectuado");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getUrl(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}

class Criteria {
  private String title;
  private String description;
  private String author;
  private Date createdFrom;
  private Date createdTo;
  private Date modifiedFrom;
  private Date modifiedTo;

  public Criteria(String title, String description, String author, Date createdFrom, Date createdTo, Date modifiedFrom,
      Date modifiedTo) {
    super();
    this.title = title;
    this.description = description;
    this.author = author;
    this.createdFrom = createdFrom;
    this.createdTo = createdTo;
    this.modifiedFrom = modifiedFrom;
    this.modifiedTo = modifiedTo;
  }

  public boolean validate(ContentResult content) {
    boolean result = isValid(content.getTitle(), title);
    result = result && isValid(content.getDescription(), description);
    result = result && isValid(content.getAuthor(), author);
    result = result && validateCreatedFrom(content);
    result = result && validateCreatedTo(content);
    result = result && validateModifiedFrom(content);
    result = result && validateModifiedTo(content);
    return result;
  }
  
  private boolean isValid(String str, String searchStr) {
    return (StringUtils.isEmpty(searchStr) || StringUtils.containsIgnoreCase(str, searchStr));
  }

  private boolean validateCreatedFrom(ContentResult content) {
    boolean isValid = true;
    if (createdFrom != null && content.getCreateDate().before(createdFrom)) {
      isValid = false;
    }
    return isValid;
  }

  private boolean validateCreatedTo(ContentResult content) {
    boolean isValid = true;
    if (createdTo != null && content.getCreateDate().after(createdTo)) {
      isValid = false;
    }
    return isValid;
  }

  private boolean validateModifiedFrom(ContentResult content) {
    boolean isValid = true;
    if (modifiedFrom != null && content.getModifiedDate().before(modifiedFrom)) {
      isValid = false;
    }
    return isValid;
  }

  private boolean validateModifiedTo(ContentResult content) {
    boolean isValid = true;
    if (modifiedTo != null && content.getModifiedDate().after(modifiedTo)) {
      isValid = false;
    }
    return isValid;
  }
}
