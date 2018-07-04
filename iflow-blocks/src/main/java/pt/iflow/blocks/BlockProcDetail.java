package pt.iflow.blocks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.presentation.PresentationManager;
import pt.iflow.api.presentation.ProcessPresentation;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.form.utils.FormButton;
import pt.iflow.blocks.form.utils.FormCache;

public class BlockProcDetail extends BlockFormulario {
  //Ports
  public Port portIn, portOut;
  
  private static final String sJSP = "proc_detail.jsp";
  private List<FormButton> buttonsOverride = null;

  public BlockProcDetail(int anFlowId, int anId, int subFlowId, String subFlowName) {
    super(anFlowId,anId, subFlowId, subFlowName);
    hasInteraction = true;
  }

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    // remove error.
    procData.clearError();
    procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, null);
    return portOut;
  }

  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // Variables
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    String description = getDescription(userInfo, procData);
    String url         = getUrl(userInfo, procData);

    Logger.trace(this,"before",login + " call with subpid="+subpid+",pid="+pid+",flowid="+flowid);

    String nextPage = url;

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Activity activity = null;

    try {
      activity = new Activity(login,flowid,pid,subpid,0,0,description,Block.getDefaultUrl(userInfo, procData),1);
      activity.mid = procData.getMid();
      activity.setRead();
      pm.updateActivity(userInfo,activity);
    }
    catch (Throwable e) {
      Logger.error(login, this, "before",
          procData.getSignature() + "Caught an unexpected exception updating activities: ", e);
    }

    String procNotInCreator = procData.getAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR);
    if (StringUtils.isEmpty(procNotInCreator)) {
      String processCreator = procData.getCreator();
      if (!StringUtils.equals(login, processCreator)) {
        try {
    	  procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
        }
        catch (Exception e) {}
      }
      else {
        try {
          Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
          while(it != null && it.hasNext()) {
            activity = it.next();
            if (!login.equals(activity.userid)) {
              // Another user has activity scheduled
              procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
              break;
            }
          }
        }
        catch (Throwable e) {
          Logger.warning(login, this, "before", 
              procData.getSignature() + "Caught exception checking activities: ", e);
        }
      }
    }
    return nextPage;
  }

  
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    return new Port[]{portOut,};
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    return new Port[]{portIn,};
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "ProcDetail");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "ProcDetail Processado");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    int flowid  = procData.getFlowId();
    int pid     = procData.getPid();
    int subpid  = procData.getSubPid();

    return sJSP+"?flowid="+ flowid+"&pid="+ pid + "&subpid="+subpid;
  }

  protected ProcessData getProcess(UserInfoInterface userInfo, ProcessData procData) {
    String sflowid = getAttribute("flowid");
    String spid = getAttribute("pid");
    String ssubpid = getAttribute("subpid");
    String spnumber = getAttribute("pnumber");

    int flowid = -1;
    try {
      sflowid = procData.transform(userInfo, sflowid);
      flowid = new Double(sflowid).intValue();
    } catch (Throwable t) {}

    int pid = -1;
    try {
      spid = procData.transform(userInfo, spid);
      pid = new Double(spid).intValue();
    } catch (Throwable t) {}

    int subpid = -1;
    try {
      ssubpid = procData.transform(userInfo, ssubpid);
      subpid = new Double(ssubpid).intValue();
    } catch (Throwable t) {}
    String pnumber = null;
    try {
      pnumber = procData.transform(userInfo, spnumber);
      if(StringUtils.isEmpty(pnumber)) pnumber = spnumber;
    } catch (Throwable t) {}

    if(flowid < 1) flowid = -1;
    if(pid < 1) pid = -1;
    if(subpid < 1) subpid = -1;
    
    ProcessHeader ph = new ProcessHeader(flowid, pid, subpid);
    ph.setPNumber(pnumber);
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    
    ph = pm.findProcess(userInfo, ph); // find the process
    
    if(null == ph) {
      Logger.error(userInfo.getUtilizador(), this, "getProcess", "Process not found: flowid="+flowid+" pid="+pid+" subpid="+subpid+" pnumber="+pnumber);
      return null;
    }
    
    IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, ph.getFlowId());
    if(null == fd) {
      Logger.error(userInfo.getUtilizador(), this, "getProcess", "Could not fetch flow: flowid="+ph.getFlowId());
      return null;
    }
    
    ProcessData processo = pm.getProcessData(userInfo, ph, Const.nALL_PROCS); // load process data
    
    if(null == processo) {
      Logger.error(userInfo.getUtilizador(), this, "getProcess", "Process not found: flowid="+flowid+" pid="+pid+" subpid="+subpid+" pnumber="+pnumber);
      return null;
    }
    // alteracao de requisitos, agora temos acesso sem restrições ao processo
    /*boolean canViewProcess = (processo != null && pm.canViewProcess(userInfo, processo));
    if(!canViewProcess) {
      Logger.error(userInfo.getUtilizador(), this, "getProcess", "User cannot view process: "+processo);
      processo = null;
    }*/
    return processo;
  }
  
  
  public String getForm(UserInfoInterface userInfo, ProcessData procData, ServletUtils response) {
    return getForm(userInfo, procData, response, false, null);
  }
  
  public String getPrintForm(UserInfoInterface userInfo, ProcessData procData, ServletUtils response, String field) {
    return getForm(userInfo, procData, response, true, field);
  }

  protected String getForm(UserInfoInterface userInfo, ProcessData procData, ServletUtils response, boolean print, String sField) {
    
    if(null == buttonsOverride) {
      buttonsOverride = FormCache.buildButtonList(this); // this will strip button prefixes
    }
    
    HashMap<String,String> hmHidden = new HashMap<String,String>();
    hmHidden.put("subpid",String.valueOf(procData.getSubPid()));
    hmHidden.put("pid",String.valueOf(procData.getPid()));
    hmHidden.put("flowid",String.valueOf(procData.getFlowId()));
    hmHidden.put("_serv_field_","-1");
    hmHidden.put("op","1");
    
    IFlowData flow = null;
    ProcessData processo = getProcess(userInfo, procData);
    
    if(processo != null) {
      flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, processo.getFlowId());

      Block block = flow.getDetailForm();
      if(null != block) {
        String obj = null;
        BlockFormulario blockForm = (BlockFormulario) block;
        if(print) { 
          obj = blockForm.print(userInfo, processo, sField, response);
        } else {
          obj = BlockFormulario.generateForm(
              blockForm, 
              userInfo, // current user
              processo, // process to check
              hmHidden, // hidden vars
              true,     // force read only
              response, // ServletResponse wrapper
              sJSP,     // override form.jsp with this one
              buttonsOverride); // override buttons with these
        }
        return obj;
      }
    }
    // another thing must be done....
    Map<String,String> processDetail = null;
    if(processo != null && flow.hasDetail())
      processDetail = ProcessPresentation.getProcessDetail(userInfo, processo, response);
    if(null == processDetail) processDetail = new HashMap<String, String>();
    Hashtable<String,Object> htSubst = new Hashtable<String, Object>();
    htSubst.put("processDetail", processDetail);
    htSubst.put("make_head",true);
    htSubst.put("url_prefix", Const.APP_URL_PREFIX);
    htSubst.put("sJSP", sJSP);
    htSubst.put("hmHidden", hmHidden);
    int procFlowid = 0;
    int procPid = 0;
    int procSubpid = 0;
    if(processo != null) {
      procFlowid = processo.getFlowId();
      procPid = processo.getPid();
      procSubpid = processo.getSubPid();
    }
    htSubst.put("procFlowid", procFlowid);
    htSubst.put("procPid", procPid);
    htSubst.put("procSubpid", procSubpid);
    
    //  messages.....
    htSubst.put("noDetail",userInfo.getMessages().getString("user_proc_detail.msg.noProcessDetail"));
    htSubst.put("variableLabel",userInfo.getMessages().getString("user_proc_detail.field.variable"));
    htSubst.put("valueLabel",userInfo.getMessages().getString("user_proc_detail.field.value"));

    // buttons
    if(print)
      htSubst.put("buttonList", new ArrayList<Map<String,String>>());
    else
      htSubst.put("buttonList", buttonsOverride);
    
    return PresentationManager.buildPage(response, userInfo, htSubst, "proc_detail");
  }

  protected String getPdfURL(UserInfoInterface userInfo, ProcessData procData, ServletUtils response) {
    ProcessData processo = getProcess(userInfo, procData);
    if(null == processo) return null;
    IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, processo.getFlowId());

    String sURL = null;
    Block block = flow.getDetailForm();
    if(null != block) {
      final String sTEMPLATE_SUFFIX = ".fo";
      
      // 10: print stylesheet attribute
      String sPrintStyleSheet = (String)block.execute(10,null);
      if (StringUtils.isNotEmpty(sPrintStyleSheet)) {
        if (sPrintStyleSheet.toLowerCase(Locale.ENGLISH).endsWith(sTEMPLATE_SUFFIX)) {
          int nDSFlowId = processo.getFlowId();
          int nDSPid = processo.getPid();
          int nDSSubPid = processo.getSubPid();

          sURL = Const.APP_URL_PREFIX+"Services/pdf.jsp?flowid=" + nDSFlowId 
            + "&pid=" + nDSPid 
            + "&subpid=" + nDSSubPid
            + "&template=" + response.encodeURL(sPrintStyleSheet)
            + "&ts=" + System.currentTimeMillis();

        }
      }

    }
    return sURL;
  }
  
  protected Object[] exportSpreadsheet(UserInfoInterface userInfo, ProcessData procData, ServletUtils response, String field) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    Object[] result = new Object[4];
    result[0] = "Erro";
    result[1] = null;
    result[2] = null;
    result[3] = null;
    
    ProcessData processo = getProcess(userInfo, procData);
    if(processo != null) {
      IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, processo.getFlowId());

      Block block = flow.getDetailForm();
      if(null != block) {
        BlockFormulario blockForm = (BlockFormulario) block;
        String exportResult = blockForm.exportFieldToSpreadSheet(userInfo, processo, field, out, response);
        result[0] = exportResult;
        if(StringUtils.isEmpty(exportResult)) {
          if (Const.nEXPORT_MODE == Const.nEXPORT_MODE_CSV) {
            result[1] = "text/csv";
            result[2] = "export.csv";
          } else {
            result[1] = "application/vnd.ms-excel";
            result[2] = "export.xsl";
          }
          result[3] = out.toByteArray();
        }
      }
    }
    
    return result;
  }
  
  public Object execute(int opCode, Object[] params) {
    Object result = null;
    switch(opCode) {
    case 2:
      result = getPrintForm(
          (UserInfoInterface)params[0],
          (ProcessData)params[1],
          (ServletUtils)params[2],
          (String) params[3]);
      break;
    case 3:
      result = getPdfURL(
          (UserInfoInterface)params[0],
          (ProcessData)params[1],
          (ServletUtils)params[2]);
      break;
    case 4:
      result = getPrintForm(
          (UserInfoInterface)params[0],
          (ProcessData)params[1],
          (ServletUtils)params[2],
          (String) params[3]);
      break;
    case 1:
    default:
      result = getForm(
          (UserInfoInterface)params[0],
          (ProcessData)params[1],
          (ServletUtils)params[2]);
    break;
    }

    return result;
  }
}
