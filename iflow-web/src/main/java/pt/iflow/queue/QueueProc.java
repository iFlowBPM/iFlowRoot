package pt.iflow.queue;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;


/**
 * <p>Title: QueueProc</p>
 * <p>Description: Contains the data of a queue process</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

class QueueProc {

  private int _nId;
  private int _nObject;
  private String _sGroup;
  private int _nFlowId;
  private int _nPid;
  private Properties _pProps;
  private Date _dtCreationDate;
  private Map<String,String> _dsData;

  // empty constructor
  public QueueProc () {
    this._nId = QueueManager.NAN;
    this._nObject = QueueManager.NAN;
    this._sGroup = null;
    this._nFlowId = QueueManager.NAN;
    this._nPid = QueueManager.NAN;
    this._pProps = null;
    this._dtCreationDate = null;
    this._dsData = null;
  }

  // constructor to be used for database queue proc fetching
  public QueueProc (int anId, 
		    int anObject, 
		    String asGroup, 
		    int anFlowId, 
		    int anPid, 
		    Properties apProps,
		    Date adtCreationDate,
		    Map<String,String> adsData) {
    this(anObject,asGroup,anFlowId,anPid,apProps,adsData);
    this._nId = anId;
    this._dtCreationDate = adtCreationDate;
  }

  // constructor to be used by application to set queue procs in database
  public QueueProc (int anObject, 
		    String asGroup, 
		    int anFlowId, 
		    int anPid, 
		    Properties apProps,
		    Map<String,String> adsData) {
    this();
    this._nObject = anObject;
    this._sGroup = asGroup;
    this._nFlowId = anFlowId;
    this._nPid = anPid;
    this._pProps = apProps;
    this._dtCreationDate = new Date();
    this._dsData = adsData;    
  }


  protected void setId(int anId) {
    this._nId = anId;
  }

  public int getId() {
    return this._nId;
  }

  public int getObject() {
    return this._nObject;
  }

  public String getGroup() {
    return new String(this._sGroup);
  }

  public int getFlowId() {
    return this._nFlowId;
  }

  public int getPid() {
    return this._nPid;
  }

  public Properties getProps() {
    return this._pProps;
  }

  public String getProperty(String asPropName) {
    if (this._pProps == null) return null;
    return this._pProps.getProperty(asPropName);
  }

  public void setProperty(String asPropName, String asPropValue) {
    if (this._pProps == null) this._pProps = new Properties();
    this._pProps.setProperty(asPropName,asPropValue);
  }

  public Date getCreationDate() {
    return this._dtCreationDate;
  }

  public Map<String,String> getData() {
    return this._dsData;
  }

  public String getData(String asName) {
    if (this._dsData == null) return null;
    String str = this._dsData.get(asName);
    if(null == str) str = "";
    return str;
  }

  protected void setData(Map<String,String> adsData) {
    this._dsData = adsData;
  }

  protected void setData(String asName, String asValue) {
    if (this._dsData == null) this._dsData = new Hashtable<String, String>();
    this._dsData.put(asName,asValue);
  }

}
