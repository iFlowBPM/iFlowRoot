package pt.iflow.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class FlowJob implements Job {

  public static final String PROP_USERNAME="username";
  public static final String PROP_USERAUTH="userauth";
  public static final String PROP_FLOWID="flowid";
  public static final String PROP_EXTRA_PARAMS="extraParams";
  
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    Logger.info("internal", this, "execute", "Starting a job...");
    try {
      UserInfoInterface userInfo = null;
      int flowid = -1;
      JobDataMap dataMap = ctx.getMergedJobDataMap();
      String userName = dataMap.getString(PROP_USERNAME);
      String userProfile = dataMap.getString(PROP_USERAUTH);
      String sFlowId = dataMap.getString(PROP_FLOWID);
      String extraParams = dataMap.getString(PROP_EXTRA_PARAMS);
      flowid = Integer.parseInt(sFlowId);
      Logger.info("internal", this, "execute", "flowid: "+flowid+" - "+sFlowId);
      
      Logger.info("internal", this, "execute", "username: "+userName+" profile: "+userProfile);
      userInfo = BeanFactory.getUserInfoFactory().newUserInfo();
      userInfo.profileLogin(userName, userProfile);
      if (!userInfo.isLogged()) {
        Logger.info("internal", this, "execute", "Error user is not logged on");
        throw new Exception(userInfo.getError());
      }
      Logger.info("internal", this, "execute", "Login OK");
      
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      Flow flow = BeanFactory.getFlowBean();
      
      if (!userInfo.isOrgAdmin() &&
          !flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.CREATE_PRIV)) {
        throw new Exception("Não autorizado");
      }
      if (!flow.checkFlowEnabled(userInfo, flowid)) {
        throw new Exception("Fluxo não disponível");
      }

      //CreateProcess
      ProcessData procData = pm.createProcess(userInfo, flowid);
      if (procData == null) {
        throw new Exception("Processo não inicializado");
      }

      Logger.info("internal", this, "execute", procData.getSignature() + "Reached this point... Now parse extra props: "+extraParams);

      // Setup extra properties
      String [] props = new String[0];
      if(null != extraParams) {
        props = extraParams.split(",");
      }
      
      for(int i = 0; i < props.length; i++) {
        String [] pair = props[i].split("=");
        if(pair.length != 2) continue;
        procData.parseAndSet(pair[0], pair[1]);
      }
      
      Logger.info("internal", this, "execute", procData.getSignature() + "DataSet update complete....");
      // Start this boy
      String nextURL = flow.nextBlock(userInfo, procData);
      Logger.info("internal", this, "execute", procData.getSignature() + "nextBlock returned "+nextURL);

      
      if(null != nextURL) {
        // save process or create activity??
      }
      
    } catch(Throwable t) {
      Logger.error("internal", this, "execute", "Ocorreu um erro ao iniciar o fluxo." + t.getClass().getName() + ": " +t.getMessage());
      throw new JobExecutionException("Ocorreu um erro ao iniciar o fluxo.", t);
    }
  }

}
