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
 * Created on May 20, 2005 by iKnow
 *
  */

package pt.iflow.applicationdata.db;

import java.util.Hashtable;
import java.util.Map;

import pt.iflow.api.applicationdata.ApplicationData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class DBApplicationData implements ApplicationData {

  private Hashtable<String,String> _data;
  
  public DBApplicationData(Map<String,String> data) {
    this._data = new Hashtable<String,String>();
    if(null != data) this._data.putAll(data);
  }

  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationData#getId()
   */
  public String getId() {
    return _data.get("APPID");
  }

  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationData#getName()
   */
  public String getName() {
    return _data.get("NAME");
   }

  /* (non-Javadoc)
   * @see pt.iknow.applicationdata.ApplicationData#getDescription()
   */
  public String getDescription() {
    return _data.get("DESCRIPTION");
    }

}
