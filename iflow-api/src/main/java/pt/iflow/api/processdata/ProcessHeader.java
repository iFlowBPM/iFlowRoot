package pt.iflow.api.processdata;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.Const;

public class ProcessHeader {

    private static final String SIGNATURE_FORMAT = "[{0}:{1}:{2}] ";
  
    private int _flowid = -1;
	private int _pid = -1;
	private int _subpid = -1;
	private String _pnumber;
	private String _creator;
	private Date _creationDate;
	private String _currentUser;
	private Date _lastUpdate;
	private boolean _closed;
	
	private OrderedMap<String,ReportTO> reports;
	
	private int _origflowid = -1;
	private int _origpid = -1;
	private int _origsubpid = -1;
	private String _origpnumber = null;
	private String _origcreator = null;
	private Date _origcreationDate = null;
	private String _origcurrentUser = null;
	private Date _origlastUpdate = null;
	private boolean _origclosed = false;

	transient private int _mid = Const.NO_MID;
	  

	
	ProcessHeader(Element xmlElement) throws ParseException {
		_flowid = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_FLOWID));
		_pid = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_PID));
		_subpid = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_SUBPID));
		_pnumber = xmlElement.getAttribute(ProcessXml.ATTR_PNUMBER);
		_creator = xmlElement.getAttribute(ProcessXml.ATTR_CREATOR);
		_creationDate = ProcessXml.DateFormatter.parse(xmlElement.getAttribute(ProcessXml.ATTR_CREATION_DATE));
		_currentUser = xmlElement.getAttribute(ProcessXml.ATTR_CURRENT_USER);
		_lastUpdate = ProcessXml.DateFormatter.parse(xmlElement.getAttribute(ProcessXml.ATTR_LAST_UPDATE));
		_closed = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_CLOSED)) == 1;

    	reports = new ListOrderedMap<String, ReportTO>();
		saveOriginals();
	}

	public ProcessHeader(int flowid, int pid, int subpid) { 
		_flowid = flowid;
		_pid = pid;
		_subpid = subpid;
		
    	reports = new ListOrderedMap<String, ReportTO>();
	}

	/**
	 * Copy constructor
	 */
	public ProcessHeader(ProcessHeader header) { 
	  this(header.getFlowId(), header.getPid(), header.getSubPid());

	  _pnumber = header._pnumber;
	  _creator = header._creator;
	  _creationDate = header._creationDate;
	  _currentUser = header._currentUser;
	  _lastUpdate = header._lastUpdate;
	  _closed = header._closed;

	  Iterator<String> itRpts = reports.keySet().iterator();
	  while (itRpts != null && itRpts.hasNext()) {
	    String key = itRpts.next();
	    reports.put(key, reports.get(key));
	  }
      _mid = header._mid;
	}
	
	private void saveOriginals() {
		_origflowid = _flowid;
		_origpid = _pid;
		_origsubpid = _subpid;
		_origpnumber = _pnumber;
		_origcreator = _creator;
		_origcreationDate = _creationDate;
		_origcurrentUser = _currentUser;
		_origlastUpdate = _lastUpdate;
		_origclosed = _closed;
	}
	
	public boolean isModified() {
		return _flowid != _origflowid ||
		_pid != _origpid ||
		_subpid != _origsubpid ||
		!StringUtils.equals(_origpnumber, _pnumber) ||
		!StringUtils.equals(_creator, _origcreator) ||
		!equalDates(_creationDate, _origcreationDate) ||
		!StringUtils.equals(_currentUser, _origcurrentUser) ||
		!equalDates(_lastUpdate, _origlastUpdate) ||
		_closed != _origclosed;
	}

	public void resetModified() {
		saveOriginals();
	}
	
	private boolean equalDates(Date dt1, Date dt2) {
		if (dt1 == null && dt2 == null)
			return true;
		
		if (dt1 != null && dt2 != null) {
			return dt1.compareTo(dt2) == 0;
		}
		
		return false;	
	}
	
	public int getFlowId() {
		return _flowid;
	}

	public int getPid() {
		return _pid;
	}

	public int getSubPid() {
		return _subpid;
	}

	public String getSignature() {
	    return MessageFormat.format(SIGNATURE_FORMAT, 
	        String.valueOf(_flowid), String.valueOf(_pid), String.valueOf(_subpid));
	}	  
	
	public String getPNumber() {
		return _pnumber;
	}

	public int getMid() { return _mid; }
	public void setMid(int mid) {
	  _mid = mid;
	}
	public boolean hasMid() { return _mid != Const.NO_MID; }


	public void setFlowId(int flowid) {
		_flowid = flowid;
	}

	public void setPid(int pid) {
		_pid = pid;
	}

	public void setSubPid(int subpid) {
		_subpid = subpid;
	}

	public void setPNumber(String pnumber) {
		_pnumber = pnumber;
	}

	public String getCreator() {
		return _creator;
	}

	public void setCreator(String creator) {
		_creator = creator;
	}

	public Date getCreationDate() {
		return _creationDate;
	}

	public void setCreationDate(Date date) {
		_creationDate = date;
	}

	public String getCurrentUser() {
		return _currentUser;
	}

	public void setCurrentUser(String user) {
		_currentUser = user;
	}

	public Date getLastUpdate() {
		return _lastUpdate;
	}

	public void setLastUpdate(Date date) {
		_lastUpdate = date;
	}
	
	public boolean isClosed() {
		return _closed;
	}
	
	public void setClosed(boolean closed) { 
		_closed = closed;
	}

	@Override
	public String toString() {
		return "Header: FLOWID=" + _flowid + "; PID=" + _pid + "; SUBPID=" + _subpid;
	}
	
  public void storeReport(ReportTO report) {
    OrderedMap<String, ReportTO> map = new ListOrderedMap<String, ReportTO>();
    map.put(report.getCodReporting(), report);
    for(String key : reports.keySet()) {
      ReportTO value = reports.get(key);
      map.put(key, value);
    }
    reports.putAll(map);
  }

  public ReportTO removeReport(String code) {
    return reports.remove(code);
  }

  public Map<String, ReportTO> getCachedReports() {
    return this.reports;
  }
}
