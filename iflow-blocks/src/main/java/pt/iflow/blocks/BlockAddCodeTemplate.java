package pt.iflow.blocks;

import java.util.Hashtable;
import java.util.Iterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;
/**
 * <p>Title: BlockAddCodeTemplate</p>
 * <p>Description: This block adds a template to the internal template table</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Infosistema</p>
 * @author
 */

public class BlockAddCodeTemplate extends Block {
  public Port portIn, portSuccess, portError;

  private static final String TEMPLATE = "Template";
  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String CALLBACK = "Callback";  
  private static final String ADDED = "Added";

  public BlockAddCodeTemplate(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
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

    try {
      String sTemplateVar = this.getAttribute(TEMPLATE);
      String sNameVar = this.getAttribute(NAME);
      String sDescVar = this.getAttribute(DESCRIPTION);
      String sCallbackVar = this.getAttribute(CALLBACK);
      String sAdded = this.getAttribute(ADDED);
      boolean sAddedVar = false;

      if (StringUtilities.isEmpty(sTemplateVar)) {
    	  Logger.error(login, this, "after", 
    			  		procData.getSignature() + "empty value for Template attribute");
    	  outPort = portError;
      }
      else if (StringUtilities.isEmpty(sNameVar)) {
          Logger.error(login, this, "after", 
                  		procData.getSignature() + "empty value for Nome attribute");
          outPort = portError;
      }
      else {
        String sTemplate = procData.transform(userInfo, sTemplateVar);
        String sName = procData.transform(userInfo, sNameVar);
        String sDesc = procData.transform(userInfo, sDescVar);
        String sCallback = procData.transform(userInfo, sCallbackVar);

        Logger.debug(login, this, "after", "Template=" + sTemplate + ", Nome=" + sName + ", DescriÃ§Ã£o=" + sDesc + ", Callback=" + sCallback);

        if (sTemplate != null) {
        	sTemplate = sTemplate.trim();
        }
        if (sName != null) {
        	sName = sName.trim();
        }
        if (sDesc != null) {
        	sDesc = sDesc.trim();
        }
        if (sCallback != null) {
        	sCallback = sCallback.trim();
        }

        if (StringUtilities.isEmpty(sTemplate)) {
        	Logger.error(login, this, "after", 
        				procData.getSignature() + "empty value for parsed Template");
        	outPort = portError;
        } 
        else if (StringUtilities.isEmpty(sName)) {
            Logger.error(login, this, "after", 
                    	procData.getSignature() + "empty value for parsed Nome");
            outPort = portError;
        } 
        else {
        	Hashtable<String,String> fields = getFields();
        	if (fields.size() == 0) {
        		Logger.error(login,this,"after",
                procData.getSignature() + "no output variables configured");  
        		outPort = portError;
        	} 
        	else {
            CodeTemplateManager ap = BeanFactory.getCodeTemplateManagerBean();
            ;

        		CodeTemplate template = new CodeTemplate(sTemplate, sName, sDesc, sCallback, userInfo.getOrganization());
        		sAddedVar = ap.addCodeTemplate(userInfo, procData, template);                     
        	              
        		if (sAddedVar == false) {
        			sAdded = "false";

        			Logger.info(login,this,"after","template was not added ");  
        		} 
        		else {
        			sAdded = "true";
        			Logger.info(login,this,"after","Template " + sTemplate + " was added with success");
        		} 
            ProcessSimpleVariable addedValue = procData.get(this.getAttribute(ADDED));
            addedValue.setValue(sAdded);
        		logMsg.append("Created variable '" + sAdded + "';");
        		outPort = portSuccess;
        	}	
        }
       }
    } catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  private Hashtable<String,String> getFields() {
    Hashtable<String,String> fields = new Hashtable<String, String>(); 
    Iterator<String> itera = this.getAttributeMap().keySet().iterator();
    while (itera.hasNext()) {
      final String sAttrName = itera.next();

      if (StringUtilities.isEmpty(sAttrName) || sAttrName.equals(TEMPLATE) || sAttrName.equals(NAME) || sAttrName.equals(DESCRIPTION) ||
    		  sAttrName.equals(CALLBACK) || sAttrName.equals(sDESCRIPTION) || sAttrName.equals(sRESULT))
        continue;
      
      String sAttrValue = this.getAttribute(sAttrName);

      if (StringUtilities.isEmpty(sAttrValue))
        continue;
      else 
        sAttrValue = sAttrValue.trim();

      fields.put(sAttrName, sAttrValue);
    }

    return fields;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData process) {
    return this.getDesc(userInfo, process, true, "Add Code Template");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Added Code Template");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
