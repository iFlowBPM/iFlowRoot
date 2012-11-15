/*
 *
 * Created on Feb 24, 2006 by mach
 *
  */

package pt.iflow.startup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.events.EventManager;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.utils.Logger;
import pt.iflow.core.NotificationManagerBean;
import pt.iflow.datasources.DSLoader;
import pt.iflow.delegations.DelegationManager;
import pt.iflow.hotfolder.HotFolderManager;
import pt.iflow.maillistener.MailListenerManager;
import pt.iflow.profilessync.ProfilesSyncManager;
import pt.iflow.scheduler.CronManager;
import pt.iflow.update.UpdateManager;
import pt.iflow.usersync.UsersSyncManager;

/**
* 
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright (c) 2006 mach</p>
* 
* @author mach
* 
* @web.servlet
* name="StartupServlet"
* load-on-startup="1"
* 
* @web.servlet-mapping
* url-pattern="/StartupServlet"
*/
public class StartupServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor of the object.
   */
  public StartupServlet() {
    super();
  }

  /**
   * Destruction of the servlet. <br>
   */
  public void destroy() {
    super.destroy(); // Just puts "destroy" string in log
    
    // Stop started services
    Logger.warning("", this, "", "StartupServlet: Stopping launched");
    DelegationManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: Delegations stopped");

    Logger.warning("", this, "", "StartupServlet: Stopping EventManager");
    EventManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: EventManager stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping NotificationManager");
    NotificationManagerBean.stopManager();
    Logger.warning("", this, "", "StartupServlet: NotificationManager stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping ChronoManager");
    CronManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: ChronoManager stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping MailListener");
    MailListenerManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: MailListener stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping HotFolder");
    HotFolderManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: HotFolder stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping DataSources");
    DSLoader.getInstance().stop();
    Logger.warning("", this, "", "StartupServlet: DataSources stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping LicenseService");
    LicenseServiceFactory.getLicenseService().instanceShutdown();
    Logger.warning("", this, "", "StartupServlet: LicenseService stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping LicenseService");
    LicenseServiceFactory.getLicenseService().instanceShutdown();
    Logger.warning("", this, "", "StartupServlet: LicenseService stopped");
    
    Logger.warning("", this, "", "StartupServlet: Stopping ProfilesSync");
    ProfilesSyncManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: ProfilesSync stopped");

    Logger.warning("", this, "", "StartupServlet: Stopping UsersSync");
    UsersSyncManager.stopManager();
    Logger.warning("", this, "", "StartupServlet: UsersSync stopped");

  }


  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Operation not permitted.");
  }

  /**
   * Initialization of the servlet. <br>
   *
   * @throws ServletException if an error occure
   */
  public void init() throws ServletException {
	// perform updates prior to init
    Logger.warning("", this, "", "StartupServlet: Starting UpdateManager!!");
    if ( UpdateManager.SYS_UPDATE != null && getServletContext().getRealPath(UpdateManager.SYS_UPDATE) != null) {
    	UpdateManager.run(getServletContext().getRealPath(UpdateManager.SYS_UPDATE));
    	Logger.warning("", this, "", "StartupServlet: UpdateManager Complete!!");
    }
    else {
      Logger.warning("", this, "", "StartupServlet: UpdateManager Path " + UpdateManager.SYS_UPDATE + " no found!!");
    }
    
    // Load DLL
    Logger.warning("", this, "", "StartupServlet: Starting LicenseService!!");
    LicenseServiceFactory.getLicenseService().instanceStartup();
    Logger.debug("", this, "", "StartupServlet: LicenseService launched!!");
    
    String licStatus = LicenseServiceFactory.getLicenseService().isLicenseOK()?"OK":"Error";
    Logger.warning("", this, "", "StartupServlet: License status: "+licStatus);

    Logger.warning("", this, "", "StartupServlet: Starting DataSources");
    DSLoader.getInstance().start();
    Logger.warning("", this, "", "StartupServlet: DataSources launched");
    
    Logger.warning("", this, "", "StartupServlet: Starting Delegations!!");
    DelegationManager.startManager();
    Logger.warning("", this, "", "StartupServlet: Delegations launched!!");

    Logger.warning("", this, "", "StartupServlet: Starting EventManager!!");
    EventManager.startManager();
    Logger.warning("", this, "", "StartupServlet: EventManager launched!!");

    Logger.warning("", this, "", "StartupServlet: Starting NotificationManager");
    NotificationManagerBean.startManager();
    Logger.warning("", this, "", "StartupServlet: NotificationManager launched!!");
    
    Logger.warning("", this, "", "StartupServlet: Starting ChronoManager");
    CronManager.startManager();
    Logger.warning("", this, "", "StartupServlet: ChronoManager launched!!");

    Logger.warning("", this, "", "StartupServlet: Starting MailListener");
    MailListenerManager.startManager();
    Logger.warning("", this, "", "StartupServlet: MailListener started");

    Logger.warning("", this, "", "StartupServlet: Starting HotFolder");
    HotFolderManager.startManager();
    Logger.warning("", this, "", "StartupServlet: HotFolder started");
    
    Logger.warning("", this, "", "StartupServlet: Starting ProfilesSync");
    ProfilesSyncManager.startManager();
    Logger.warning("", this, "", "StartupServlet: ProfilesSync started");

    Logger.warning("", this, "", "StartupServlet: Starting UsersSync");
    UsersSyncManager.startManager();
    Logger.warning("", this, "", "StartupServlet: UsersSync started");

  }

}
