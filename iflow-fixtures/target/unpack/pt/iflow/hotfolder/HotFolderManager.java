package pt.iflow.hotfolder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowDeployListener;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowSettingsListener;
import pt.iflow.api.flows.FlowVersionListener;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.flows.NewFlowListener;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.hotfolder.FlowFolderChecker;
import pt.iflow.api.utils.hotfolder.HotFolderConfig;


public class HotFolderManager extends Thread {

  private enum FlowStatus { CHECK_FLOW, ONLINE };
  
  private static HotFolderManager _hotfolderManager = null;
  private UserInfoInterface userInfo;
  private Map<Integer, FlowFolderChecker> checkers = Collections.synchronizedMap(new HashMap<Integer, FlowFolderChecker>());
  
  private HotFolderManager() {
    userInfo = BeanFactory.getUserInfoFactory().newClassManager(this.getClass().getName());
  }

  public static HotFolderManager get() {
    if(null == _hotfolderManager) _hotfolderManager = new HotFolderManager();
    return _hotfolderManager;
  }

  public static void startManager() {
    Logger.adminInfo("HotFolderManager", "startManager", "Starting...");
    get().start();
  }

  public static void stopManager() {
    Logger.adminInfo("HotFolderManager", "stopManager", "Stopping...");
    get().shutdown();
  }

  private void prepareAndStartChecker(int flowid) throws Exception {
    prepareChecker(flowid);
    startChecker(flowid, FlowStatus.CHECK_FLOW);
  }
  
  public void run() {
    FlowHolder fholder = BeanFactory.getFlowHolderBean();
    Collection<Integer> flowids = fholder.listFlowIds(userInfo);
    for (int flowid : flowids) {
      try {
        prepareAndStartChecker(flowid);
        Logger.adminInfo("HotFolderManager", "run", "Flow " + flowid + " processed.");
      } 
      catch (Exception e) {
        Logger.adminError("HotFolderManager", "run", "Error preparing flow " + flowid, e);
      }
    }

    // add settings listener
    BeanFactory.getFlowSettingsBean().addFlowSettingsListener(this.getClass().getName(), new FlowSettingsListener() {
      public void settingsChanged(int flowid) {
        Logger.adminTrace("HotFolderManager", "settingsChanged", "entered for flow " + flowid);
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("HotFolderManager", "settingsChanged", 
              "Flow " + flowid + " re-processed.");
        } 
        catch (Exception e) {
          Logger.adminError("HotFolderManager", "settingsChanged", 
              "error preparing checker for flow " + flowid, e);
        }
      }
    });

    fholder.addNewFlowListener(this.getClass().getName(), new NewFlowListener() {
      public void flowAdded(int flowid) {
        Logger.adminTrace("HotFolderManager", "flowAdded", "entered for flow " + flowid);
        
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("HotFolderManager", "flowAdded", 
              "Flow " + flowid + " initialized.");
        } 
        catch (Exception e) {
          Logger.adminError("HotFolderManager", "flowAdded", 
              "error initializing checker for flow " + flowid, e);
        }        
      }
    });

    fholder.addFlowDeployListener(this.getClass().getName(), new FlowDeployListener() {
      public void goOffline(int flowid) {
        Logger.adminTrace("HotFolderManager", "goOffline", "entered for flow " + flowid);
        stopChecker(flowid);
      }

      public void goOnline(int flowid) {
        Logger.adminTrace("HotFolderManager", "goOnline", "entered for flow " + flowid);
        try {
          startChecker(flowid, FlowStatus.ONLINE);
        } catch (Exception e) {
          Logger.adminError("HotFolderManager", "goOnline", 
              "error starting checker for flow " + flowid, e);
        }
      }
    });

    fholder.addFlowVersionListener(this.getClass().getName(), new FlowVersionListener() {
      public void newVersion(int flowid) {
        Logger.adminTrace("HotFolderManager", "newVersion", "entered for flow " + flowid);
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("HotFolderManager", "newVersion", 
              "Flow " + flowid + " re-processed.");
        } 
        catch (Exception e) {
          Logger.adminError("HotFolderManager", "newVersion", 
              "error preparing checker for flow " + flowid, e);
        }        
      }
    });
  }

  private void prepareChecker(int flowid) throws Exception {

    stopChecker(flowid);

    FlowSettings settingsbean = BeanFactory.getFlowSettingsBean();
    HotFolderConfig hfconfig = HotFolderConfig.parseAndValidate(flowid, settingsbean.getFlowSettings(flowid));

    if (hfconfig == null) {
      checkers.put(flowid, null);
      Logger.adminInfo("HotFolderManager", "prepareChecker", 
          "no hotfolder configuration for flow " + flowid);
      return;
    }
    
    try {
      FlowFolderChecker fchecker = new FlowFolderChecker(hfconfig);
      checkers.put(flowid, fchecker);
      Logger.adminInfo("HotFolderManager", "prepareChecker", 
          "hotfolder checker prepared for flow " + flowid);
    }
    catch (Exception e) {
      checkers.remove(flowid);
      Logger.adminError("HotFolderManager", "prepareChecker", 
          "hotfolder checker for flow " + flowid + " throw exception:", e);      
    }
  }

  private void stopChecker(int flowid) {
    synchronized (checkers) {
      FlowFolderChecker ffc = checkers.get(flowid);
      if (ffc != null) {
        ffc.stop();
        Logger.adminInfo("HotFolderManager", "stopChecker", "checker for flow " + flowid + " stopped.");
      }
    }
  }

  private void startChecker(final int flowid, FlowStatus status) throws Exception {

    if (!checkers.containsKey(flowid)) {
      prepareChecker(flowid);
    }

    FlowFolderChecker ffc = checkers.get(flowid);
    if (ffc == null) {
      return;
    }

    boolean start = status == FlowStatus.ONLINE;
    if (status == FlowStatus.CHECK_FLOW) {
      IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid);
      start = flow != null && flow.isOnline();
    }
    if (start) {   
      ffc.start();
    }

    Logger.adminInfo("HotFolderManager", "startChecker", 
        "checker for flow " + flowid + " started? " + start);
  }

  private void shutdown() {
    BeanFactory.getFlowSettingsBean().removeFlowSettingsListener(this.getClass().getName());
    FlowHolder fholder = BeanFactory.getFlowHolderBean();
    fholder.removeNewFlowListener(this.getClass().getName());
    fholder.removeFlowDeployListener(this.getClass().getName());
    fholder.removeFlowVersionListener(this.getClass().getName());

    Iterator<Integer> flowids = checkers.keySet().iterator();
    while (flowids.hasNext()) {
      int flowid = flowids.next();
      stopChecker(flowid);      
    }
  }
}
