package microsoft_dynamics_schemas;

public class WS_Ficha_Produto_PortProxy implements microsoft_dynamics_schemas.WS_Ficha_Produto_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_Ficha_Produto_Port wS_Ficha_Produto_Port = null;
  
  public WS_Ficha_Produto_PortProxy() {
    _initWS_Ficha_Produto_PortProxy();
  }
  
  public WS_Ficha_Produto_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_Ficha_Produto_PortProxy();
  }
  
  private void _initWS_Ficha_Produto_PortProxy() {
    try {
      wS_Ficha_Produto_Port = (new microsoft_dynamics_schemas.WS_Ficha_Produto_ServiceLocator()).getWS_Ficha_Produto_Port();
      if (wS_Ficha_Produto_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_Ficha_Produto_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_Ficha_Produto_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_Ficha_Produto_Port != null)
      ((javax.xml.rpc.Stub)wS_Ficha_Produto_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto_Port getWS_Ficha_Produto_Port() {
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port;
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto read(java.lang.String no) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.read(no);
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto[] readMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.isUpdated(key);
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto create(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.create(WS_Ficha_Produto);
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto[] createMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.createMultiple(WS_Ficha_Produto_List);
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto update(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.update(WS_Ficha_Produto);
  }
  
  public microsoft_dynamics_schemas.WS_Ficha_Produto[] updateMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.updateMultiple(WS_Ficha_Produto_List);
  }
  
  public boolean delete(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Ficha_Produto_Port == null)
      _initWS_Ficha_Produto_PortProxy();
    return wS_Ficha_Produto_Port.delete(key);
  }
  
  
}