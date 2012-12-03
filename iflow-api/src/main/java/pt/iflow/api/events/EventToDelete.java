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
 * EventToDelete.java
 *
 * Created on November 23, 2004, 10:06 AM
 */

package pt.iflow.api.events;

/**
 *
 * @author  ptgm
 */
public class EventToDelete {
   
    int _fid, _pid, _blockid;
    
    /** Creates a new instance of EventToDelete */
    public EventToDelete(int fid, int pid, int blockid) {
        _fid = fid;
        _pid = pid;
        _blockid = blockid;
    }
    
	public int getFid()     { return _fid; }
	public int getPid()     { return _pid; }
	public int getBlockid() { return _blockid; }

	public void setPid(int pid)         { _pid = pid; }
	public void setFid(int fid)         { _fid = fid; }
	public void setBlockId(int blockid) { _blockid = blockid; }
   
   public String toSQL() {
      return " FID = " + _fid + " AND PID = " + _pid + " AND BLOCKID = " + _blockid;
   }
}
