package pt.iflow.api.flows;

import org.apache.commons.collections15.map.LinkedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;

public class MailStartSettings {
  private String fromEmailVar;
  private String fromNameVar;
  private String subjectVar;
  private String sentDateVar;
  private String filesVar;
  private String textVar;

  private LinkedMap<String, String> customProps;
  
  private MailStartSettings() {    
  }
  
  public String getFromEmailVar() {
    return fromEmailVar;
  }

  public String getFromNameVar() {
    return fromNameVar;
  }

  public String getSubjectVar() {
    return subjectVar;
  }

  public String getSentDateVar() {
    return sentDateVar;
  }

  public String getFilesVar() {
    return filesVar;
  }

  public LinkedMap<String, String> getCustomProps() {
    return customProps;
  }

  public String getCustomPropVar(String mailprop) {
    return customProps != null ? customProps.get(mailprop) : null;
  }

  public static MailStartSettings parse(Block startBlock) {
    
    if (!startBlock.isStartBlock())
      return null;
    
    MailStartSettings ret = new MailStartSettings();
    
    ret.fromEmailVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_FROM_EMAIL_PROP); 
    ret.fromNameVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_FROM_NAME_PROP);     
    ret.subjectVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_SUBJECT_PROP); 
    ret.sentDateVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_SENT_DATE_PROP);
    ret.filesVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_DOCS_PROP);
    ret.textVar = startBlock.getAttribute(IFlowData.sMAIL_START_INFO_PREFIX + IFlowData.MAILSTART_TEXT_PROP);
    
    for (int i=0; true; i++) {
      String mailprop = startBlock.getAttribute(IFlowData.sMAIL_START_VARS_PREFIX + IFlowData.MAILSTART_MAIL_PROP + i); 
      String var = startBlock.getAttribute(IFlowData.sMAIL_START_VARS_PREFIX + IFlowData.MAILSTART_FLOW_VAR + i); 

      if (StringUtils.isEmpty(mailprop) || StringUtils.isEmpty(var)) {
        break;
      }
      
      if (ret.customProps == null) {
        ret.customProps = new LinkedMap<String, String>();
      }
      
      ret.customProps.put(mailprop, var);
    }
  
    if (ret.isSet())
      return ret;
    else
      return null;
  }
  
  private boolean isSet() {
    return StringUtils.isNotEmpty(fromEmailVar) ||
      StringUtils.isNotEmpty(fromNameVar) ||
      StringUtils.isNotEmpty(subjectVar) ||
      StringUtils.isNotEmpty(sentDateVar) ||
      StringUtils.isNotEmpty(textVar) ||
      StringUtils.isNotEmpty(filesVar) ||
      (customProps != null && !customProps.isEmpty());
  }

public String getTextVar() {
	return textVar;
}

public void setTextVar(String textVar) {
	this.textVar = textVar;
}
  
}
