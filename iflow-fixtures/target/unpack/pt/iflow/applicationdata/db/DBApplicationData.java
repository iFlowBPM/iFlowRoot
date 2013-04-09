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
