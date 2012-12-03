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

