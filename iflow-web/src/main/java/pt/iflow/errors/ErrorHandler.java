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

import java.util.ArrayList;
import java.util.Locale;

import pt.iflow.api.errors.ErrorCode;
import pt.iflow.api.errors.IErrorHandler;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.errors.msg.Messages;

public class ErrorHandler implements IErrorHandler {
	
	private boolean success;
	private ErrorCode errorCode;
	private Object[] errorExtra;

	public ErrorHandler() {
		this(ErrorCode.UNDEFINED, null);
	}

	public ErrorHandler(ErrorCode err) {
		this(err, null);
	}

  public ErrorHandler(ErrorCode err, Object[] errorExtra) {
    this.success = false;
    this.errorCode = err;
    this.errorExtra = buildExtraArray(err,errorExtra);
  }
  
  private static Object[] buildExtraArray(ErrorCode err, Object[] errorExtra) {
    if(null == errorExtra && null == err) return new Object[]{-1};
    if(null == errorExtra) return new Object[]{err.getCode()};
    ArrayList<Object> items = new ArrayList<Object>();
    items.add(err.getCode());
    for (Object object : errorExtra) {
      items.add(object);
    }
    return items.toArray();
  }

	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorHandler#isSuccess()
	 */
	public boolean isSuccess() {
		return success;
	}

	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorHandler#setSuccess(boolean)
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorHandler#getErrorCode()
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorHandler#setErrorCode(pt.iflow.web.errors.ErrorCode)
	 */
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorHandler#getLogMessage(pt.iflow.api.utils.UserInfoInterface)
	 */
	public String getLogMessage(UserInfoInterface ui) {
	  String org = null;
	  Locale loc = null;
    if(null != ui) {
      loc = ui.getUserSettings().getLocale();
      org = ui.getOrganization();
    }

	  return Messages.getInstance(loc, org).getString("log."+errorCode.getErrorKey(), this.errorExtra);
	}
	
  /* (non-Javadoc)
 * @see pt.iflow.web.errors.IErrorHandler#getPageMessage(pt.iflow.api.utils.UserInfoInterface)
 */
public String getPageMessage(UserInfoInterface ui) {
    String org = null;
    Locale loc = null;
    if(null != ui) {
      loc = ui.getUserSettings().getLocale();
      org = ui.getOrganization();
    }

    return Messages.getInstance(loc, org).getString("page."+errorCode.getErrorKey(), this.errorExtra);
  }
  
	
}
