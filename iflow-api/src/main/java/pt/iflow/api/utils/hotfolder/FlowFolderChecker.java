/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.api.utils.hotfolder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBConnectionWrapper;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserInfoManagerInterface;
import pt.iflow.api.utils.Utils;

public class FlowFolderChecker implements Runnable {

  private HotFolderConfig hfconfig;
  private boolean stop = false;
  private Thread worker;
  private UserInfoManagerInterface managerUser; 

  private static final String signature = "FlowFolderChecker"; 
  
  /**
   * Constructor
   * @param checkInterval check interval in seconds
   * @param client the mail client to use
   * @param messageParser the message parser to parse new mail messages
   */
  public FlowFolderChecker(HotFolderConfig hfconfig) {
    this.hfconfig = hfconfig;
    managerUser = (UserInfoManagerInterface)BeanFactory.getUserInfoFactory().newClassManager(this.getClass().getName());
    
    IFlowData fd = BeanFactory.getFlowBean().getFlow(managerUser, hfconfig.getFlowId());
    managerUser.setOrganizationId(fd.getOrganizationId());
  }

  public void stop() {
    Logger.adminInfo(signature, "stop", getId() + "signaling thread to stop.");
    stop = true;
    if (worker != null) {
      worker.interrupt();
      worker = null;
    }
  }
  
  public void start() {
    Logger.adminInfo(signature, "start", getId() + "starting thread");
    
    if (worker != null) {
      Logger.adminInfo(signature, "start", getId() + "thread is already running");
      return;
    }
    
    worker = new Thread(this, getId());
    stop=false;
    worker.start();    
  }
  
  public String getId() {
    return "[" + this.hfconfig.getFlowId() + "] ";
  }
  
  public void run() {

    Logger.adminInfo(signature, "run", getId() + "starting folder checker");

    while (!stop) {
      try {      
        try {
          processFolders(hfconfig.getSubsFolders());
        } 
        catch (Exception e) {
          Logger.adminError(signature, "run", getId() + "caught exception", e);
        }

        try {
          Logger.adminInfo(signature, "run", 
              getId() + "sleeping for " + Const.HOT_FOLDER_SEARCH_INTERVAL + "ms");
          Thread.sleep(Const.HOT_FOLDER_SEARCH_INTERVAL);
        }
        catch (InterruptedException e) {        
        }
      }
      catch (Exception master) {
        Logger.adminError(signature, "run", getId() + "caught unexpected exception", master);
      }
    }
    Logger.adminInfo(signature, "run", getId() + "done checking folders");
  }

  private void processFolders(List<String> folders) {
    
    List<String> depthFolders = new ArrayList<String>();
    for (String folder : folders) {
      depthFolders.add(folder);
    }    
    for (int depth=0; hfconfig.getDepth() == -1 || depth <= hfconfig.getDepth(); depth++ ) {

      Logger.adminDebug(signature, "processFolders", 
          getId() + "processing folders for depth " + 
          depth + ": " + depthFolders.size() + " total folders to check...");

      List<String> nextDepthFolders = new ArrayList<String>();  

      for (String folder : depthFolders) {
        nextDepthFolders.addAll(processFolder(folder));
      }   
      
      if (nextDepthFolders.size() == 0) 
        break;
      
      depthFolders = nextDepthFolders;
    }
  }

  private List<String> processFolder(String folder) {
    
    try {
      File ff = new File(folder);
      if (ff.isDirectory()) {
        File[] files = ff.listFiles();
        if (files != null) {
          List<String> ret = new ArrayList<String>();
          for (File f : files) {
            if (f.isDirectory()) {
              ret.add(f.getAbsolutePath());
            }
            else {
              processFile(f);
            }
          }
          return ret;
        }
      }
      else {
        Logger.adminWarning(signature, "processFolder", 
            getId() + "folder " + folder + " is not a directory");
      }      
    }
    catch (Exception e) {      
    }
    return new ArrayList<String>();
  }
  
  private void processFile(File file) {
    Logger.adminDebug(signature, "processFolder", 
        getId() + "processing file " + file.getAbsolutePath());

    Connection conn = null;
    PreparedStatement pst = null;
    String transactionId = null;
    
    // create process
    // insert entry in db
    // move orig file to processed folder
    // commit

    UserInfoInterface ui = managerUser;
    if (StringUtils.isNotEmpty(hfconfig.getUser())) {
      ui = BeanFactory.getUserInfoFactory().newUserInfoDelegate(managerUser, hfconfig.getUser());
    }
    
    try {
      conn = Utils.getDataSource().getConnection();
      conn.setAutoCommit(false);
      transactionId = ui.registerTransaction(new DBConnectionWrapper(conn));
      pst = conn.prepareStatement("insert into hotfolder_files " +
      		"(path,flowid,in_user,entry_date,processed_path,created_pid) " +
      		"values (?,?,?,?,?,?)");
      
      Date now = Calendar.getInstance().getTime();
      
      
      ProcessData procData = BeanFactory.getProcessManagerBean().createProcess(ui, hfconfig.getFlowId()); 

      String docVarName = hfconfig.getDocVar();
      Set<File> errors = procData.addDocuments(ui, docVarName, Arrays.asList(file));
      if (errors.contains(file)) {
        Logger.adminWarning(signature, "processFile", getId() + "add document returned error");
        throw new Exception("document not added to process var " + docVarName);
      }

      BeanFactory.getFlowBean().nextBlock(ui, procData, true);
      Logger.adminInfo(signature, "processFile", 
          getId() + "Process " + procData.getPid() + " started in flow " + procData.getFlowId());

      
      if (!procData.isInDB() && StringUtils.isNotEmpty(hfconfig.getUser())) {
        BeanFactory.getFlowBean().storeProcess(ui, procData, false);
        Logger.adminDebug(signature, "processFile", 
            getId() + "Stored process " + procData.getSignature());
      }
      
      String processedFileName = genHash(file, now);
      String fileDestPath = FilenameUtils.concat(Const.HOT_FOLDER_PROCESSED_FOLDER, processedFileName);

      pst.setString(1, file.getAbsolutePath());
      pst.setInt(2, hfconfig.getFlowId());
      pst.setString(3, hfconfig.getUser());
      pst.setTimestamp(4, new Timestamp(now.getTime()));
      pst.setString(5, fileDestPath);
      pst.setInt(6, procData.getPid());

      if (pst.executeUpdate() != 1) {
        throw new Exception("Failed to insert file in DB");
      }
      
      File destFile = new File(fileDestPath);
      FileUtils.moveFile(file, destFile);
      
      conn.commit();
    }
    catch (Exception e) {
      Logger.adminError(signature, "processFolder", 
          getId() + "ERROR processing file " + file.getAbsolutePath(), e);
      try {
        if (conn != null) {
          conn.rollback();
        }
      }
      catch (Exception ee) {
        Logger.adminError(signature, "processFolder", 
            getId() + "ERROR rolling back file " + file.getAbsolutePath());        
      }
    }
    finally {
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            ui.unregisterTransaction(transactionId);
          }
          catch (IllegalAccessException e) {
            Logger.adminError(signature, "processFile", 
                getId() + "unregistering transaction in userinfo", e); 
          }
        }
      }
      finally {
        DatabaseInterface.closeResources(conn,pst);
      }
    }
  }
  
  private String genHash(File f, Date dt) {
    return MessageFormat.format("{0}-{1}-{2}", 
        String.valueOf(hfconfig.getFlowId()), 
        String.valueOf(dt.getTime()), 
        String.valueOf(f.getAbsolutePath().hashCode()));
  }
}
