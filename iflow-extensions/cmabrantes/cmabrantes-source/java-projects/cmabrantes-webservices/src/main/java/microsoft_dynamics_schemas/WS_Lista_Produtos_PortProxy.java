package microsoft_dynamics_schemas;

public class WS_Lista_Produtos_PortProxy implements microsoft_dynamics_schemas.WS_Lista_Produtos_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_Lista_Produtos_Port wS_Lista_Produtos_Port = null;
  
  public WS_Lista_Produtos_PortProxy() {
    _initWS_Lista_Produtos_PortProxy();
  }
  
  public WS_Lista_Produtos_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_Lista_Produtos_PortProxy();
  }
  
  private void _initWS_Lista_Produtos_PortProxy() {
    try {
      wS_Lista_Produtos_Port = (new microsoft_dynamics_schemas.WS_Lista_Produtos_ServiceLocator()).getWS_Lista_Produtos_Port();
      if (wS_Lista_Produtos_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_Lista_Produtos_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_Lista_Produtos_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_Lista_Produtos_Port != null)
      ((javax.xml.rpc.Stub)wS_Lista_Produtos_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_Lista_Produtos_Port getWS_Lista_Produtos_Port() {
    if (wS_Lista_Produtos_Port == null)
      _initWS_Lista_Produtos_PortProxy();
    return wS_Lista_Produtos_Port;
  }
  
  public microsoft_dynamics_schemas.WS_Lista_Produtos read(java.lang.String no) throws java.rmi.RemoteException{
    if (wS_Lista_Produtos_Port == null)
      _initWS_Lista_Produtos_PortProxy();
    return wS_Lista_Produtos_Port.read(no);
  }
  
  public microsoft_dynamics_schemas.WS_Lista_Produtos[] readMultiple(microsoft_dynamics_schemas.WS_Lista_Produtos_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_Lista_Produtos_Port == null)
      _initWS_Lista_Produtos_PortProxy();
    return wS_Lista_Produtos_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Lista_Produtos_Port == null)
      _initWS_Lista_Produtos_PortProxy();
    return wS_Lista_Produtos_Port.isUpdated(key);
  }
  
  
}