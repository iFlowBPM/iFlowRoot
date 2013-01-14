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

