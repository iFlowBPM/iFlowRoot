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


public class UserErrorCode extends ErrorCode {

	public static final ErrorCode FAILURE_DUPLICATE_USER = new UserErrorCode(
			10001);
	public static final ErrorCode FAILURE_DUPLICATE_EMAIL = new UserErrorCode(
			10002);
	public static final ErrorCode FAILURE_NOT_AUTHORIZED = new UserErrorCode(
			10003);
	public static final ErrorCode FAILURE_USER_NOT_FOUND = new UserErrorCode(
			10004);
	public static final ErrorCode PENDING_ORG_ADM_EMAIL = new UserErrorCode(
			10005);

	protected UserErrorCode(int code) {
		super(code);
	}

  protected UserErrorCode(int code, String errorKey) {
    super(code, errorKey);
  }
}
