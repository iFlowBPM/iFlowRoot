package pt.iknow.iflow;

import java.util.Collection;

import org.apache.commons.httpclient.Cookie;

import pt.iflow.api.utils.FlowInfo;

public interface RepositoryClient {

  public abstract String getURL();

  public abstract boolean checkConnection();

  public abstract boolean login(String asLogin, String asPassword);

  public abstract boolean login(String asLogin, String asPassword, boolean abPassEncrypted);

  public abstract String[] listProfiles();

  public abstract String[] listExtraProperties();
  
  public abstract String[] listStyleSheets();

  public abstract String[] listMailTemplates();

  public abstract String[] listPrintTemplates();

  public abstract String[] listChartTemplates();

  /**
   * List existing flows.
   * @return
   */
  public abstract String[] listFlows();

  public abstract String[] listSubFlows();

  public abstract String[] listLibraries();

  public abstract String[] listWebServices();

  public abstract String[] listEvents();

  public abstract byte[] getFlow(String name);

  public abstract byte[] getSubFlow(String name);

  public abstract byte[] getLibrary(String name);

  public abstract byte[] getWebService(String name);

  public abstract String getEvent(String name);

  public abstract byte[] getIcon(String name);

  public abstract byte[] getClassFile(String name);

  public abstract byte[] getProcessStateHistory(int flowid, String pnumber);

  public abstract byte[] getProcessStateLog(int flowid, String pnumber, int state);

  public abstract int deployFlow(String flowName, String description, byte[] data);

  public abstract int deploySubFlow(String flowName, String description, byte[] data);

  public abstract boolean setWebService(String name, byte[] wsdl);

  public abstract Class<?> loadClass(String name) throws ClassNotFoundException;

  /**
   * Load a class
   * 
   * @param path
   * @param name
   * @return
   * @throws ClassNotFoundException
   * @deprecated Use loadClass(String name) instead
   */
  public abstract Class<?> loadClass(String path, String name) throws ClassNotFoundException;

  public abstract void deleteFile(String name);

  public abstract boolean hasExtendedAPI();

  public abstract Collection<FlowInfo> listFlowsExtended();

  public abstract FlowInfo getFlowInfo(String name);

  public abstract void logout();

  public abstract byte[] getZippedIcons();

  public abstract byte[] getZippedLibraries();

  public abstract long getLastModifiedIcons();

  public abstract long getLastModifiedLibraries();

  public abstract String getFlowHash(String name);

  public abstract String getSubFlowHash(String name);

  public abstract String getLibraryHash(String name);

  public abstract String getWebServiceHash(String name);

  public abstract String getIconHash(String name);

  public abstract String getClassHash(String name);

  public abstract String getIconsHash();

  public abstract String getLibrariesHash();

  public abstract byte[] getModifiedClasses(byte[] checksums);
  
  public abstract byte[] getModifiedMessages(byte[] messages);

  public abstract String getUserLocale();

  // versioning
  public abstract String[] listFlowVersions(String flow);

  public abstract String[] listSubFlowVersions(String flow);

  public abstract String getFlowVersionComment(String flow, int version);

  public abstract String getSubFlowVersionComment(String flow, int version);

  public abstract byte[] getFlowVersion(String flow, int version);

  public abstract byte[] getSubFlowVersion(String flow, int version);

  public abstract int deployFlowVersion(String flowName, String description, byte[] data, String comment);

  public abstract int deploySubFlowVersion(String flowName, String description, byte[] data, String comment);

  public abstract String[] listDataSources();

  public abstract byte[] getConnectors();

  // Flow Docs
  public abstract byte[] flowDocsGetFiles(String searchValue, String path);

  public abstract String[] listSignatureTypes();

  // BD Synch
  public byte[] getTableDesc(String jndiName, String table);
  
  // Other
  public abstract void setStatusListener(RepositoryStatusListener listener);

  public abstract void reloadClassLoader();

  public abstract String getBaseURL();

  public abstract String getCookie();
  
  public Cookie[] getCookies();

  public byte [] getMessages(String file);

  /**
   * Returns array of Strings with format "<label_name> - <label_description>"
   * @return 
   */
  public abstract String[] listTaskAnnotationLabels();

  public String runFlow(String flowName);

  public String undeployFlow(String flowname);

  public String setFlowReady2Run(String flowname);

}