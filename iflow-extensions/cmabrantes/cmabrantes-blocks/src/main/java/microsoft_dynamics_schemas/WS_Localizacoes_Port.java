/**
 * WS_Localizacoes_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public interface WS_Localizacoes_Port extends java.rmi.Remote {
    public microsoft_dynamics_schemas.WS_Localizacoes read(java.lang.String code) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Localizacoes[] readMultiple(microsoft_dynamics_schemas.WS_Localizacoes_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException;
    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException;
}
