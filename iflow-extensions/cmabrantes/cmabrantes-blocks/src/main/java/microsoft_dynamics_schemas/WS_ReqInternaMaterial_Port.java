/**
 * WS_ReqInternaMaterial_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public interface WS_ReqInternaMaterial_Port extends java.rmi.Remote {
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial read(java.lang.String no) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] readMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException;
    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial create(microsoft_dynamics_schemas.WS_ReqInternaMaterial WS_ReqInternaMaterial) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] createMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial[] WS_ReqInternaMaterial_List) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial update(microsoft_dynamics_schemas.WS_ReqInternaMaterial WS_ReqInternaMaterial) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_ReqInternaMaterial[] updateMultiple(microsoft_dynamics_schemas.WS_ReqInternaMaterial[] WS_ReqInternaMaterial_List) throws java.rmi.RemoteException;
    public boolean delete(java.lang.String key) throws java.rmi.RemoteException;
    public boolean delete_SalesLines(java.lang.String salesLines_Key) throws java.rmi.RemoteException;
}
