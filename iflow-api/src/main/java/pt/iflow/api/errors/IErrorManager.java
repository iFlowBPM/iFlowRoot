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
package pt.iflow.api.errors;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface IErrorManager {

	public static final int sGENERIC_ERROR = 1;
	public static final int sBEAN_ERROR = 2;
	public static final int sDB_ERROR = 3;
	public static final int sMAIL_ERROR = 4;
	public static final int sIO_ERROR = 5;
	public static final int sJVM_ERROR = 6;

	public abstract String init(UserInfoInterface userInfo, Object objClass,
			String method);

	public abstract String init(UserInfoInterface userInfo,
			ProcessData procData, Object objClass, String method);

	public abstract void register(String key, int type, String description);

	public abstract void fire(String key);

	public abstract void close(String key);

}
