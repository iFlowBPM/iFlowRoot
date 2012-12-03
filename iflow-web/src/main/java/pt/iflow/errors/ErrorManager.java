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
package pt.iflow.errors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;

import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.errors.IError;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.notification.Email;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class ErrorManager implements IErrorManager {

    Hashtable<String,Error> _htErrors = null;
    private static IErrorManager _em = null;
    
    private ErrorManager() {
        _htErrors = new Hashtable<String, Error>();
    }
    public static IErrorManager getInstance() {
        if (_em == null) {
            _em = new ErrorManager();
        }
        return _em;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorManager#init(pt.iflow.api.utils.UserInfoInterface, java.lang.Object, java.lang.String)
	 */
    public String init(UserInfoInterface userInfo, Object objClass, String method) {
        return this.init(userInfo, null, objClass, method);
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorManager#init(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.core.ProcessData, java.lang.Object, java.lang.String)
	 */
    public String init(UserInfoInterface userInfo, ProcessData procData, Object objClass, String method) {
        Date dtNow = new Date();
        
        int flowid   = -1;
        int pid      = -1;
        int subpid   = -1;
        if (procData != null) {
            flowid = procData.getFlowId();
            pid    = procData.getPid();
            subpid = procData.getSubPid();
        }
 
        Error error = new Error(userInfo.getUtilizador(), dtNow, 
                objClass.getClass().getName(), method, flowid, pid, subpid);
        
        String key = 
            method + "@" + objClass.getClass().getName() + 
            "@" + Utils.date2string(dtNow, "yyyy-MM-dd HH:mm:ss:SSS");
        
        _htErrors.put(key, error);
        
        return key;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorManager#register(java.lang.String, int, java.lang.String)
	 */
    public void register(String key, int type, String description) {
        IError error = (IError)_htErrors.get(key);
        error.setType(type);
        error.setDescription(description);
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorManager#fire(java.lang.String)
	 */
    public void fire(String key) {
        
        IError error = (IError)_htErrors.get(key);
        if (error == null) return;
                
        DataSource ds = null;
        Connection db = null;
        PreparedStatement st = null;
        
        // insert in DB
        try {
            ds = Utils.getDataSource();
            db = ds.getConnection();
            db.setAutoCommit(true);
            String query = DBQueryManager.getQuery("ErrorManager.INSERT_ERROR");
            
            st = db.prepareStatement(query);
            st.setString(1, error.getUserId());
            st.setTimestamp(2, new Timestamp(error.getNow().getTime()));
            st.setInt(3, error.getFlowId());
            st.setInt(4, error.getPid());
            st.setInt(5, error.getSubPid());
            st.setInt(6, error.getType());
            st.setString(7, error.getDescription());

            st.executeUpdate();
                        
        } catch (SQLException sqle) {
          Logger.error(error.getUserId(), this, "fire", "Error registering error", sqle);
        } finally {
          DatabaseInterface.closeResources(db,st);
        }
        
        // notify iFlow/Adm of the error
        if(Const.bUSE_EMAIL) {
          try {
            String sFrom = Const.sAPP_EMAIL;
            String sTo   = Const.sMAIL_ADM_ERROR_NOTIFY;
            String sSubject = "iFlow Error: type[" + error.getType() + "]";

            Email email = new Email();

            email.setHtml(false);
            email.sendMsg(sFrom, sTo, sSubject, error.toString());
          }
          catch (Exception e) {
            Logger.error(error.getUserId(), this, "fire", "Error sending email notification to ADMIN", e);
          }
        }
        
        // output to log
        Logger.error(error.getUserId(), "pt.iknow.errors.ErrorManager", "register",
                "\n---- BEGIN - IFLOW ERROR LOG ----" +
                error.toString() + 
                "\n---- END - IFLOW ERROR LOG ----");
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorManager#close(java.lang.String)
	 */
    public void close(String key) {
        _htErrors.remove(key);
    }
}
