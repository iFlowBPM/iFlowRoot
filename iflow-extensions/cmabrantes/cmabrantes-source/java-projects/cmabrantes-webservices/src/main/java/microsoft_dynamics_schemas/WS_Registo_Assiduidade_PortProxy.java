package microsoft_dynamics_schemas;

public class WS_Registo_Assiduidade_PortProxy implements microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port {
  private String _endpoint = null;
  private microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port wS_Registo_Assiduidade_Port = null;
  
  public WS_Registo_Assiduidade_PortProxy() {
    _initWS_Registo_Assiduidade_PortProxy();
  }
  
  public WS_Registo_Assiduidade_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initWS_Registo_Assiduidade_PortProxy();
  }
  
  private void _initWS_Registo_Assiduidade_PortProxy() {
    try {
      wS_Registo_Assiduidade_Port = (new microsoft_dynamics_schemas.WS_Registo_Assiduidade_ServiceLocator()).getWS_Registo_Assiduidade_Port();
      if (wS_Registo_Assiduidade_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wS_Registo_Assiduidade_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wS_Registo_Assiduidade_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wS_Registo_Assiduidade_Port != null)
      ((javax.xml.rpc.Stub)wS_Registo_Assiduidade_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port getWS_Registo_Assiduidade_Port() {
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port;
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade read(java.lang.String no) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.read(no);
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] readMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.readMultiple(filter, bookmarkKey, setSize);
  }
  
  public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.isUpdated(key);
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade create(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.create(WS_Registo_Assiduidade);
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] createMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.createMultiple(WS_Registo_Assiduidade_List);
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade update(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.update(WS_Registo_Assiduidade);
  }
  
  public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] updateMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.updateMultiple(WS_Registo_Assiduidade_List);
  }
  
  public boolean delete(java.lang.String key) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.delete(key);
  }
  
  public boolean delete_SubformLinhas(java.lang.String subformLinhas_Key) throws java.rmi.RemoteException{
    if (wS_Registo_Assiduidade_Port == null)
      _initWS_Registo_Assiduidade_PortProxy();
    return wS_Registo_Assiduidade_Port.delete_SubformLinhas(subformLinhas_Key);
  }
  
  public void setUser(String username, String password){
    if (wS_Registo_Assiduidade_Port == null)
        _initWS_Registo_Assiduidade_PortProxy();
    wS_Registo_Assiduidade_Port.setUser(username, password);
    }
}