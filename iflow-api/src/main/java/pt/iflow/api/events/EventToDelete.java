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
