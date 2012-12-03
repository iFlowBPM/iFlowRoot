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
package pt.iflow.api.userdata.views;

public interface UserViewInterface {

	public static final String USERID = "USERID";
	public static final String USERNAME = "USERNAME";
	public static final String FIRST_NAME = "FIRST_NAME";
	public static final String LAST_NAME = "LAST_NAME";
	public static final String EMAIL = "EMAIL_ADDRESS";
	public static final String UNITID = "UNITID";
	public static final String MOBILE = "MOBILE_NUMBER";
	public static final String FAX = "FAX_NUMBER";
	public static final String COMPANY_PHONE = "COMPANY_PHONE";
	public static final String PHONE = "PHONE_NUMBER";
	public static final String GENDER = "GENDER";
	public static final String ACTIVATED = "ACTIVATED";
	public static final String ORGADM = "ORGADM";

	public abstract String getUserId();

	public abstract String getEmail();

	public abstract String getUsername();

	public abstract String getFirstName();

	public abstract String getLastName();

	public abstract String getUnitId();

	public abstract String getMobileNumber();

	public abstract String getFax();

	public abstract String getGender();

	public abstract String getCompanyPhone();

	public abstract String getPhoneNumber();

	public abstract String getActivated();

	public abstract String getOrgAdm();

    public abstract String get(String fieldName);
}
