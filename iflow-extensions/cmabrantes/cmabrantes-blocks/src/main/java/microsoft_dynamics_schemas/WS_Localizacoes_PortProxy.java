package microsoft_dynamics_schemas;

public class WS_Localizacoes_PortProxy implements microsoft_dynamics_schemas.WS_Localizacoes_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_Localizacoes_Port wS_Localizacoes_Port = null;
  
  public WS_Localizacoes_PortProxy() {
    _initWS_Localizacoes_PortProxy();
  }
  
  public WS_Localizacoes_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_Localizacoes_PortProxy();
  }
  
  private void _initWS_Localizacoes_PortProxy() {
    try {
      wS_Localizacoes_Port = (new microsoft_dynamics_schemas.WS_Localizacoes_ServiceLocator()).getWS_Localizacoes_Port();
      if (wS_Localizacoes_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_Localizacoes_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_Localizacoes_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_Localizacoes_Port != null)
      ((javax.xml.rpc.Stub)wS_Localizacoes_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_Localizacoes_Port getWS_Localizacoes_Port() {
    if (wS_Localizacoes_Port == null)
      _initWS_Localizacoes_PortProxy();
    return wS_Localizacoes_Port;
  }
  
  public microsoft_dynamics_schemas.WS_Localizacoes read(java.lang.String code) throws java.rmi.RemoteException{
    if (wS_Localizacoes_Port == null)
      _initWS_Localizacoes_PortProxy();
    return wS_Localizacoes_Port.read(code);
  }
  
  public microsoft_dynamics_schemas.WS_Localizacoes[] readMultiple(microsoft_dynamics_schemas.WS_Localizacoes_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_Localizacoes_Port == null)
      _initWS_Localizacoes_PortProxy();
    return wS_Localizacoes_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Localizacoes_Port == null)
      _initWS_Localizacoes_PortProxy();
    return wS_Localizacoes_Port.isUpdated(key);
  }
  
  
}