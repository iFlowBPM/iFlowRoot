/**
 * WS_Ficha_Produto_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public interface WS_Ficha_Produto_Port extends java.rmi.Remote {
    public microsoft_dynamics_schemas.WS_Ficha_Produto read(java.lang.String no) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Ficha_Produto[] readMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException;
    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Ficha_Produto create(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Ficha_Produto[] createMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Ficha_Produto update(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Ficha_Produto[] updateMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException;
    public boolean delete(java.lang.String key) throws java.rmi.RemoteException;
}
