package microsoft_dynamics_schemas;

public class WS_ReqInternaCompra_PortProxy implements microsoft_dynamics_schemas.WS_ReqInternaCompra_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_ReqInternaCompra_Port wS_ReqInternaCompra_Port = null;
  
  public WS_ReqInternaCompra_PortProxy() {
    _initWS_ReqInternaCompra_PortProxy();
  }
  
  public WS_ReqInternaCompra_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_ReqInternaCompra_PortProxy();
  }
  
  private void _initWS_ReqInternaCompra_PortProxy() {
    try {
      wS_ReqInternaCompra_Port = (new microsoft_dynamics_schemas.WS_ReqInternaCompra_ServiceLocator()).getWS_ReqInternaCompra_Port();
      if (wS_ReqInternaCompra_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_ReqInternaCompra_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_ReqInternaCompra_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_ReqInternaCompra_Port != null)
      ((javax.xml.rpc.Stub)wS_ReqInternaCompra_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra_Port getWS_ReqInternaCompra_Port() {
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port;
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra read(java.lang.String no) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.read(no);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra[] readMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.isUpdated(key);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra create(microsoft_dynamics_schemas.WS_ReqInternaCompra WS_ReqInternaCompra) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.create(WS_ReqInternaCompra);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra[] createMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra[] WS_ReqInternaCompra_List) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.createMultiple(WS_ReqInternaCompra_List);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra update(microsoft_dynamics_schemas.WS_ReqInternaCompra WS_ReqInternaCompra) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.update(WS_ReqInternaCompra);
  }
  
  public microsoft_dynamics_schemas.WS_ReqInternaCompra[] updateMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra[] WS_ReqInternaCompra_List) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.updateMultiple(WS_ReqInternaCompra_List);
  }
  
  public boolean delete(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.delete(key);
  }
  
  public boolean delete_PurchLines(java.lang.String purchLines_Key) throws java.rmi.RemoteException{
    if (wS_ReqInternaCompra_Port == null)
      _initWS_ReqInternaCompra_PortProxy();
    return wS_ReqInternaCompra_Port.delete_PurchLines(purchLines_Key);
  }
  
  
}