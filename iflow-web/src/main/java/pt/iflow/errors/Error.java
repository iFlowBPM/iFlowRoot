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
/*
 * <p>Title: Error.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Jun 20, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.errors;

import java.util.Date;

import pt.iflow.api.errors.IError;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.utils.Utils;

public class Error implements IError {

    private String _userid = null;
    private Date   _dtNow = null;
    private String _class = null;
    private String _method = null;
    private int    _flowid = -1;
    private int    _pid = -1;
    private int    _subpid = -1;
    private int    _type = IErrorManager.sGENERIC_ERROR;
    private String _description = null;
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getType()
	 */
    public int getType() {
        return _type;
    }
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getUserId()
	 */
    public String getUserId() {
        return _userid;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#setType(int)
	 */
    public void setType(int type) {
        _type = type;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#setDescription(java.lang.String)
	 */
    public void setDescription(String description) {
        _description = description;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getFlowId()
	 */
    public int getFlowId() {
      return _flowid;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getPid()
	 */
    public int getPid() {
      return _pid;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getSubPid()
	 */
    public int getSubPid() {
      return _subpid;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getDescription()
	 */
    public String getDescription() {
      return _description;
    }
    
    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#getNow()
	 */
    public Date getNow() {
      return _dtNow;
    }
    
    public Error(String userid, Date dtNow, String className, String method, 
            int flowid, int pid, int subpid) {
        _userid = userid;
        _dtNow = dtNow;
        _class = className;
        _method = method;
        _flowid = flowid;
        _pid = pid;
        _subpid = subpid;
    }

    /* (non-Javadoc)
	 * @see pt.iflow.web.errors.IError#toString()
	 */
    public String toString() {
        return "\nUSER: " + _userid + " in " + _method + "@" + _class + 
        "\nCREATED: " + Utils.date2string(_dtNow, "yyyy-MM-dd HH:mm:ss:SSS") +  
        "\nFLOWID: " + _flowid + 
        "\nPID: " + _pid + 
        "\nSUBPID: " + _subpid +
        "\nTYPE: " + _type + 
        "\nDESCRIPTION: " + _description;
    }

    
}
