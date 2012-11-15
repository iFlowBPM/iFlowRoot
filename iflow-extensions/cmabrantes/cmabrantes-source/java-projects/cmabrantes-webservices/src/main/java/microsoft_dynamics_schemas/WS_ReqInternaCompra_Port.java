/**
 * WS_ReqInternaCompra_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public interface WS_ReqInternaCompra_Port extends java.rmi.Remote {
    public microsoft_dynamics_schemas.WS_ReqInternaCompra read(java.lang.String no) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaCompra[] readMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException;
    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaCompra create(microsoft_dynamics_schemas.WS_ReqInternaCompra WS_ReqInternaCompra) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaCompra[] createMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra[] WS_ReqInternaCompra_List) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaCompra update(microsoft_dynamics_schemas.WS_ReqInternaCompra WS_ReqInternaCompra) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaCompra[] updateMultiple(microsoft_dynamics_schemas.WS_ReqInternaCompra[] WS_ReqInternaCompra_List) throws java.rmi.RemoteException;
    public boolean delete(java.lang.String key) throws java.rmi.RemoteException;
    public boolean delete_PurchLines(java.lang.String purchLines_Key) throws java.rmi.RemoteException;
}
