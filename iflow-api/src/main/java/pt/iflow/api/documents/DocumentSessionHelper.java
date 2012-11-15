package pt.iflow.api.documents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class DocumentSessionHelper implements HttpSessionBindingListener, Serializable {
  private static final long serialVersionUID = 5519391353619280798L;
  
  public static final String SESSION_ATTRIBUTE = "SessinDocumentHelper";
  
  private Map<String,List<Integer>> documents;
  private int flowid;
  private int pid;
  private int subpid;
  
  public DocumentSessionHelper(int flowid, int pid, int subpid) {
    documents = new HashMap<String, List<Integer>>();
    this.flowid = flowid;
    this.pid = pid;
    this.subpid = subpid;
  }

  public int getFlowid() {
    return flowid;
  }

  public void setFlowid(int flowid) {
    this.flowid = flowid;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public int getSubpid() {
    return subpid;
  }

  public void setSubpid(int subpid) {
    this.subpid = subpid;
  }
  
  public synchronized void cleanupDocuments(UserInfoInterface userInfo, HttpSession session) {
    if(null == userInfo) {
      Logger.error(null, this, "cleanupDocuments", "Userinfo not provided.");
      return;
    }
    ProcessData procData = null;
    if (pid == Const.nSESSION_PID) {
      String flowExecType = "";
      try {
        FlowType flowType = BeanFactory.getFlowBean().getFlowType(userInfo, this.flowid);
        if (FlowType.SEARCH.equals(flowType)) {
          flowExecType = "SEARCH";
        } else if (FlowType.REPORTS.equals(flowType)) {
          flowExecType = "REPORT";
        }
      } catch (Exception e){
      }
      if(session != null)
        procData = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
    } else {
      procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo, flowid, pid, subpid, session);
    }
    
    if(null == procData) {
      // create a dummy process
      IFlowData fd = BeanFactory.getFlowBean().getFlow(userInfo, flowid);
      procData = new ProcessData(fd.getCatalogue(), flowid, pid, subpid);
    }

    
    Documents docBean = BeanFactory.getDocumentsBean();
    
    for(Map.Entry<String, List<Integer>> entry : documents.entrySet()) {
      List<Integer> ids = entry.getValue();
      if(null == ids) continue;
      for(Integer docId : ids) {
        if(docId == null) continue;
        docBean.removeDocument(userInfo, procData, docId);
      }
      ids.clear();
    }
  }
  
  public synchronized void updateProcessData(UserInfoInterface userInfo, ProcessData procData) {
    if(null == userInfo) {
      Logger.error(null, this, "updateProcessData", "Userinfo not provided.");
      return;
    }
    
    if(null == procData) {
      Logger.error(userInfo.getUtilizador(), this, "updateProcessData", "Invalid process.");
      return;
    }
    
    for(Map.Entry<String, List<Integer>> entry : documents.entrySet()) {
      List<Integer> ids = entry.getValue();
      if(null == ids) continue;
      String varName = entry.getKey();
      if(null == varName) continue;
      
      // check if list exists
      ProcessListVariable list = procData.getList(varName);
      if(list == null) {
        Logger.error(userInfo.getUtilizador(), this, "updateProcessData", "Process variable not found: "+varName);
        continue;
      }
      
      List<Integer> added = new ArrayList<Integer>();
      for(Integer docId : ids) {
        if(docId == null) continue;
        try {
          list.addNewItem(docId);
          added.add(docId);
        } catch(Throwable t) {
          Logger.warning(userInfo.getUtilizador(), this, "updateProcessData", "Could not add document "+docId+" to var: "+varName);
        }
      }
      ids.removeAll(added);
    }
    
  }
  
  
  public void valueBound(HttpSessionBindingEvent arg0) {
    // called when added to session
  }

  public void valueUnbound(HttpSessionBindingEvent arg0) {
    // perform some cleanup
    HttpSession session = arg0.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    cleanupDocuments(userInfo, session);
  }

  public synchronized void addDocument(String varName, Integer docId) {
    if(null == varName || null == docId) {
      Logger.warning(null, this, "addDocument", "Null varName or document. Ignoring");
      return;
    }
    List<Integer> docs = documents.get(varName);
    if(null == docs) {
      docs = new ArrayList<Integer>();
      documents.put(varName, docs);
    }
    
    if(docs.contains(docId)) {
      Logger.info(null, this, "addDocument", "Document already listed. Ignoring");
    } else {
      docs.add(docId);
    }
  }

  public boolean hasDocument(String varName, Integer docId) {
    boolean retValue = false;
    if (varName != null && docId != null) {
      List<Integer> docs = documents.get(varName);
      if (docs != null) {
        for (Integer doc : docs) {
          if (doc.equals(docId)) {
        	  docs.remove(doc);
            retValue = true;
            break;
          }
        }
      }
    }
    return retValue;
  }
}
