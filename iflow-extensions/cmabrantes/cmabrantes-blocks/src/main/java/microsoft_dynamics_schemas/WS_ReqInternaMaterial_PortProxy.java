package microsoft_dynamics_schemas;

public class WS_ReqInternaMaterial_PortProxy implements microsoft_dynamics_schemas.WS_ReqInternaMaterial_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_ReqInternaMaterial_Port wS_ReqInternaMaterial_Port = null;
  
  public WS_ReqInternaMaterial_PortProxy() {
    _initWS_ReqInternaMaterial_PortProxy();
  }
  
  public WS_ReqInternaMaterial_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_ReqInternaMaterial_PortProxy();
  }
  
  private void _initWS_ReqInternaMaterial_PortProxy() {
    try {
      wS_ReqInternaMaterial_Port = (new microsoft_dynamics_schemas.WS_ReqInternaMaterial_ServiceLocator()).getWS_ReqInternaMaterial_Port();
      if (wS_ReqInternaMaterial_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_ReqInternaMaterial_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_ReqInternaMaterial_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_ReqInternaMaterial_Port != null)
      ((javax.xml.rpc.Stub)wS_ReqInternaMaterial_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial_Port getWS_ReqInternaMaterial_Port() {
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port;
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial read(java.lang.String no) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.read(no);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] readMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.isUpdated(key);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial create(microsoft_dynamics_schemas.WS_ReqInternaMaterial WS_ReqInternaMaterial) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.create(WS_ReqInternaMaterial);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] createMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial[] WS_ReqInternaMaterial_List) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.createMultiple(WS_ReqInternaMaterial_List);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial update(microsoft_dynamics_schemas.WS_ReqInternaMaterial WS_ReqInternaMaterial) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.update(WS_ReqInternaMaterial);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] updateMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial[] WS_ReqInternaMaterial_List) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.updateMultiple(WS_ReqInternaMaterial_List);
  }
  
  public boolean delete(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.delete(key);
  }
  
  public boolean delete_SalesLines(java.lang.String salesLines_Key) throws java.rmi.RemoteException{
    if (wS_ReqInternaMaterial_Port == null)
      _initWS_ReqInternaMaterial_PortProxy();
    return wS_ReqInternaMaterial_Port.delete_SalesLines(salesLines_Key);
  }
  
  
}