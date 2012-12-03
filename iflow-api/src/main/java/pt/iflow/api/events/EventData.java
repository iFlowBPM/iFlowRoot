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
package pt.iflow.api.events;

public class EventData {

  int _id, _fid, _pid, _subpid, _blockid;
  long _starttime;
  String _type, _properties, _userId;

  public EventData() {
  }

  public EventData(String userId, int id, int fid, int pid, int subpid, int blockid, long starttime, String type, String properties) {
    _id = id;
    _fid = fid;
    _pid = pid;
    _subpid = subpid;
    _blockid = blockid;
    _starttime = starttime;
    _type = type;
    _properties = properties;
    _userId = userId;
  }

  public int getId() {
    return _id;
  }

  public int getFid() {
    return _fid;
  }

  public int getPid() {
    return _pid;
  }

  public int getSubPid() {
    return _subpid;
  }

  public int getBlockid() {
    return _blockid;
  }

  public long getStarttime() {
    return _starttime;
  }

  public String getType() {
    return _type;
  }

  public String getProperties() {
    return _properties;
  }

  public String getUserId() {
    return _userId;
  }

  public void setId(int id) {
    _id = id;
  }

  public void setPid(int pid) {
    _pid = pid;
  }

  public void setSubPid(int subpid) {
    _subpid = subpid;
  }

  public void setFid(int fid) {
    _fid = fid;
  }

  public void setBlockId(int blockid) {
    _blockid = blockid;
  }

  public void setStarttime(long starttime) {
    _starttime = starttime;
  }

  public void setType(String type) {
    _type = type;
  }

  public void setProperties(String properties) {
    _properties = properties;
  }

  public void setUserId(String userId) {
    _userId = userId;
  }
}

