package pt.iflow.delegations;

public class DelegationData {

	int _id, _parentid, _flowid, _slave;
	String _userid, _ownerid, _flowname;

	public DelegationData () { }

	public DelegationData (int id, int parentid, int flowid, int slave,
			String userid, String ownerid) {
		_id = id;
		_parentid = parentid;
		_flowid = flowid;
		_slave = slave;
		_userid = userid;
		_ownerid = ownerid;
	}

	public int getId()      { return _id; }
	public int getParentid()     { return _parentid; }
	public int getFlowid()     { return _flowid; }
    public String getFlowName()     { return _flowname; }
	public int getSlave() { return _slave; }
	public String getUserid()       { return _userid; }
	public String getOwnerid() { return _ownerid; }

	public void setId(int id)              { _id = id; }
	public void setParentid(int parentid)  { _parentid = parentid; }
	public void setFlowid(int flowid)      { _flowid = flowid; }
    public void setFlowName(String sFlowName)      { _flowname = sFlowName; }
	public void setSlave(int slave)        { _slave = slave; }
	public void setUserid(String userid)   { _userid = userid; }
	public void setOwnerid(String ownerid) { _ownerid = ownerid; }
}

