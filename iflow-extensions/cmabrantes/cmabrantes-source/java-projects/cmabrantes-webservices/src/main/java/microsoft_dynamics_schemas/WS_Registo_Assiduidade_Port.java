/**
 * WS_Registo_Assiduidade_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public interface WS_Registo_Assiduidade_Port extends java.rmi.Remote {
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade read(java.lang.String no) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] readMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException;
    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade create(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] createMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade update(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException;
    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] updateMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException;
    public boolean delete(java.lang.String key) throws java.rmi.RemoteException;
    public boolean delete_SubformLinhas(java.lang.String subformLinhas_Key) throws java.rmi.RemoteException;
    public void setUser(String username, String password); 
}
