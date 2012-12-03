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
 *
 * Created on May 10, 2005 by iKnow
 *
  */

package pt.iflow.api.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface Authentication {

  public boolean checkUser(String username, String password);
  public boolean checkUserSession(String username, String sessionID);
  public AuthenticationInfo loginUser(String username, String password);
  public Collection<Map<String,String>> getProfileUsers(String profileID);
  public List<String> getAllUsersForSync(String orgId);
  public void init(Properties parameters);
 
}
