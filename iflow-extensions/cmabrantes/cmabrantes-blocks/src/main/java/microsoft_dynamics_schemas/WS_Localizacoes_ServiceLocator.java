/**
 * WS_Localizacoes_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Localizacoes_ServiceLocator extends org.apache.axis.client.Service implements microsoft_dynamics_schemas.WS_Localizacoes_Service {

    public WS_Localizacoes_ServiceLocator() {
    }


    public WS_Localizacoes_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WS_Localizacoes_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WS_Localizacoes_Port
    private java.lang.String WS_Localizacoes_Port_address = "http://192.168.1.19:7047/DynamicsNAV/WS/Cidadela/Page/WS_Localizacoes?wsdl";

    public java.lang.String getWS_Localizacoes_PortAddress() {
        return WS_Localizacoes_Port_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WS_Localizacoes_PortWSDDServiceName = "WS_Localizacoes_Port";

    public java.lang.String getWS_Localizacoes_PortWSDDServiceName() {
        return WS_Localizacoes_PortWSDDServiceName;
    }

    public void setWS_Localizacoes_PortWSDDServiceName(java.lang.String name) {
        WS_Localizacoes_PortWSDDServiceName = name;
    }

    public microsoft_dynamics_schemas.WS_Localizacoes_Port getWS_Localizacoes_Port() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WS_Localizacoes_Port_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWS_Localizacoes_Port(endpoint);
    }

    public microsoft_dynamics_schemas.WS_Localizacoes_Port getWS_Localizacoes_Port(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            microsoft_dynamics_schemas.WS_Localizacoes_BindingStub _stub = new microsoft_dynamics_schemas.WS_Localizacoes_BindingStub(portAddress, this);
            _stub.setPortName(getWS_Localizacoes_PortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWS_Localizacoes_PortEndpointAddress(java.lang.String address) {
        WS_Localizacoes_Port_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (microsoft_dynamics_schemas.WS_Localizacoes_Port.class.isAssignableFrom(serviceEndpointInterface)) {
                microsoft_dynamics_schemas.WS_Localizacoes_BindingStub _stub = new microsoft_dynamics_schemas.WS_Localizacoes_BindingStub(new java.net.URL(WS_Localizacoes_Port_address), this);
                _stub.setPortName(getWS_Localizacoes_PortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WS_Localizacoes_Port".equals(inputPortName)) {
            return getWS_Localizacoes_Port();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "WS_Localizacoes_Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_localizacoes", "WS_Localizacoes_Port"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WS_Localizacoes_Port".equals(portName)) {
            setWS_Localizacoes_PortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
