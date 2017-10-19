package pt.iknow.iflow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.xml.sax.InputSource;

import pt.iflow.api.utils.FlowInfo;
import pt.iflow.api.utils.FlowInfoList;
import pt.iflow.api.utils.RepositoryWebOpCodes;
import pt.iknow.floweditor.FlowEditor;
import pt.iknow.floweditor.FlowEditorConfig;
import pt.iknow.floweditor.FlowRepUrl;

public class RepositoryWebClient implements RepositoryClient {

	private static final String URL_TEMPLATE = "{0}/dispatcher"; //$NON-NLS-1$

	protected static final String sDEF_USER = "guest"; //$NON-NLS-1$
	protected static final String sDEF_PASS = "guest"; //$NON-NLS-1$

	private String _url = null;
	private String baseURL = null;

	private HttpClient client = null;
	private String _login = sDEF_USER;
	private String _password = sDEF_PASS;
	private RepositoryClassLoader _classLoader = null;
	private ClassLoader _parentLoader = null;
	private Boolean extendedAPI = null;
	private File classCacheFolder = null;
	private FlowRepUrl iFlowURL = null;
	private final boolean offline;

	public RepositoryWebClient(String url, String login, String password, ClassLoader parent, FlowEditorConfig cfg,
			boolean offline) {
		this.offline = offline;

		if (null == cfg)
			cfg = new FlowEditorConfig();

		if (url != null && !url.equals("")) { //$NON-NLS-1$
			baseURL = this._url = url.trim();
		}

		iFlowURL = new FlowRepUrl(url, login);

		_url = MessageFormat.format(URL_TEMPLATE, new Object[] { _url });
		client = new HttpClient();
		client.getParams().setAuthenticationPreemptive(true);
		Credentials defaultcreds = new UsernamePasswordCredentials(login, password);
		client.getState().setCredentials(AuthScope.ANY, defaultcreds);

		if (cfg.isUseProxy()) {
			String host = cfg.getProxyHost();
			int port = Integer.parseInt(cfg.getProxyPort());
			FlowEditor.log("Setting proxy host=" + host + " port=" + port); //$NON-NLS-1$ //$NON-NLS-2$
			client.getHostConfiguration().setProxy(host, port);

			if (cfg.isUseProxyAuth()) {
				Credentials credentials = null;
				String user = cfg.getProxyUser();
				String pass = cfg.getProxyPass();
				FlowEditor.log("User=" + user + " Password=xxxxx"); //$NON-NLS-1$ //$NON-NLS-2$
				if (cfg.isUseNTAuth()) {
					String hostName = "localhost"; //$NON-NLS-1$
					try {
						hostName = InetAddress.getLocalHost().getHostName();
					} catch (UnknownHostException e) {
						FlowEditor.log("error", e);
					}
					String domain = cfg.getProxyDomain();
					FlowEditor.log("NT auth host=" + hostName + " domain=" + domain); //$NON-NLS-1$ //$NON-NLS-2$
					credentials = new NTCredentials(user, pass, hostName, domain);
				} else {
					credentials = new UsernamePasswordCredentials(user, pass);
				}
				client.getState().setProxyCredentials(new AuthScope(host, port), credentials);
			}
		}

		if (login != null && !login.equals("") && password != null && !password.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			this.login(login, password, false);
		}

		File cacheFolder = new File(FlowEditorConfig.CONFIG_DIR, iFlowURL.getUserKey());
		if (!cacheFolder.exists()) {
			cacheFolder.mkdirs();
		}

		File classesCacheFolder = new File(cacheFolder, "classes");
		if (!classesCacheFolder.exists()) {
			classesCacheFolder.mkdirs();
		}

		classCacheFolder = classesCacheFolder;
		if (parent != null) {
			this._classLoader = new RepositoryClassLoader(classCacheFolder, this, parent); // $NON-NLS-1$
			this._parentLoader = parent;
		} else {
			this._classLoader = new RepositoryClassLoader(classCacheFolder, this); // $NON-NLS-1$
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getURL()
	 */
	public String getURL() {
		return _url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#checkConnection()
	 */
	public boolean checkConnection() {
		if (offline)
			return false;
		return doCheck(RepositoryWebOpCodes.CHECK_CONNECTION, _login, _password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#login(java.lang.String,
	 * java.lang.String)
	 */
	public boolean login(String asLogin, String asPassword) {
		return this.login(asLogin, asPassword, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#login(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public boolean login(String asLogin, String asPassword, boolean abPassEncrypted) {
		if (offline)
			return false;
		String pass = asPassword;
		if (!abPassEncrypted) {
			pass = RepositoryWebOpCodes._crypt.encrypt(asPassword);
		}
		boolean retObj = doCheck(RepositoryWebOpCodes.AUTHENTICATE_USER, asLogin, pass);

		if (retObj) {
			// set local vars
			this._login = asLogin;
			this._password = pass;
		}

		return retObj;
	}

	protected boolean doCheck(int op, String login, String password) {
		String response = "false"; //$NON-NLS-1$

		try {
			PostMethod method = new PostMethod(_url);
			Part[] parts = { new StringPart("login", login, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("password", password, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("op", String.valueOf(op), RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
			};
			method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));

			client.executeMethod(method);
			response = new String(method.getResponseBody());
			method.releaseConnection();
		} catch (FileNotFoundException fnfe) {
			FlowEditor.log("error", fnfe);
		} catch (IOException e) {
			FlowEditor.log("error", e);
		}

		return (Boolean.valueOf(response)).booleanValue();
	}

	public RepositoryClassLoader getClassLoader() {
		return _classLoader;
	}

	public File getClassCacheFolder() {
		return this.classCacheFolder;
	}

	// interface methods

	protected String[] getListFromString(String data) {

		if (null == data)
			return null;

		StringTokenizer stok = new StringTokenizer(data, "\n"); //$NON-NLS-1$
		if (stok.countTokens() != 0) {
			String[] fileList = new String[stok.countTokens()];
			for (int i = 0; stok.hasMoreTokens(); i++) {
				fileList[i] = stok.nextToken();
			}
			return fileList;
		} else {
			return null;
		}
	}

	protected byte[] getBytes(int op, String name) {
		if (offline)
			return null;
		byte[] buffer = null;

		try {
			name = null == name ? "NONE" : name; //$NON-NLS-1$
			PostMethod method = new PostMethod(_url);
			Part[] parts = { new StringPart("login", _login, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("password", _password, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("op", String.valueOf(op), RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("name", name, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new FilePart("file", name, (File) null) //$NON-NLS-1$
			};
			method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
			client.executeMethod(method);
			buffer = method.getResponseBody();
			method.releaseConnection();
		} catch (FileNotFoundException fnfe) {
			FlowEditor.log("error", fnfe);
		} catch (IOException e) {
			FlowEditor.log("error", e);
		}

		return buffer;
	}

	protected String getString(int op, String name) {
		String result = null;
		try {
			byte[] data = getBytes(op, name);
			if (null != data)
				result = new String(data, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			FlowEditor.log("error", e);
		}
		return result;
	}

	protected String[] getList(int op) {
		return getListFromString(getString(op, null));
	}

	protected String[] getList(int op, String param) {
		return getListFromString(getString(op, param));
	}

	protected byte[] sendBytes(int op, String name, String desc, byte[] data, String comment) {
		if (offline)
			return null;
		byte[] buffer = null;
		try {
			PostMethod method = new PostMethod(_url);
			name = null == name ? "NONE" : name; //$NON-NLS-1$
			desc = null == desc ? name : desc;
			comment = null == comment ? "" : comment;
			Part[] parts = { new StringPart("login", _login, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("password", _password, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("op", String.valueOf(op), RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("name", name, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("desc", desc, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new StringPart("comment", comment, RepositoryWebOpCodes.DEFAULT_ENCODING), //$NON-NLS-1$
					new RepositoryFilePart("file", name, data) //$NON-NLS-1$ //$NON-NLS-2$
			};
			method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
			client.executeMethod(method);
			buffer = method.getResponseBody();
			method.releaseConnection();
		} catch (FileNotFoundException fnfe) {
			FlowEditor.log("error", fnfe);
		} catch (IOException e) {
			FlowEditor.log("error", e);
		}
		return buffer;
	}

	protected byte[] sendString(int op, String name, String data) {
		if (offline)
			return null;
		byte[] ok = null;
		try {
			// Convert the string to UTF-8 data
			ok = sendBytes(op, name, null, data.getBytes("UTF-8"), null); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			FlowEditor.log("error", e);
		}
		return ok;
	}

	// Repository interface methods

	// 1. Listers

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listProfiles()
	 */
	public String[] listProfiles() {
		return getList(RepositoryWebOpCodes.LIST_PROFILES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listExtraProperties()
	 */
	public String[] listExtraProperties() {
		return getList(RepositoryWebOpCodes.LIST_EXTRA_PROPERTIES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listStyleSheets()
	 */
	public String[] listStyleSheets() {
		return getList(RepositoryWebOpCodes.LIST_STYLESHEETS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listMailTemplates()
	 */
	public String[] listMailTemplates() {
		return getList(RepositoryWebOpCodes.LIST_EMAILS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listPrintTemplates()
	 */
	public String[] listPrintTemplates() {
		return getList(RepositoryWebOpCodes.LIST_PRINTS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listChartTemplates()
	 */
	public String[] listChartTemplates() {
		return getList(RepositoryWebOpCodes.LIST_CHARTS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listFlows()
	 */
	public String[] listFlows() {
		return getList(RepositoryWebOpCodes.LIST_FLOWS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listSubFlows()
	 */
	public String[] listSubFlows() {
		return getList(RepositoryWebOpCodes.LIST_SUB_FLOWS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listLibraries()
	 */
	public String[] listLibraries() {
		return getList(RepositoryWebOpCodes.LIST_LIBRARIES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listWebServices()
	 */
	public String[] listWebServices() {
		return getList(RepositoryWebOpCodes.LIST_WEBSERVICES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listEvents()
	 */
	public String[] listEvents() {
		return getList(RepositoryWebOpCodes.LIST_EVENTS);
	}

	// 2. Getters

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getFlow(java.lang.String)
	 */
	public byte[] getFlow(String name) {
		return getBytes(RepositoryWebOpCodes.GET_FLOW, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getSubFlow(java.lang.String)
	 */
	public byte[] getSubFlow(String name) {
		return getBytes(RepositoryWebOpCodes.GET_SUBFLOW, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getLibrary(java.lang.String)
	 */
	public byte[] getLibrary(String name) {
		return getBytes(RepositoryWebOpCodes.GET_LIBRARY, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getWebService(java.lang.String)
	 */
	public byte[] getWebService(String name) {
		return getBytes(RepositoryWebOpCodes.GET_WEBSERVICE, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getEvent(java.lang.String)
	 */
	public String getEvent(String name) {
		return getString(RepositoryWebOpCodes.GET_EVENT, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getIcon(java.lang.String)
	 */
	public byte[] getIcon(String name) {
		return getBytes(RepositoryWebOpCodes.GET_ICON, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getClassFile(java.lang.String)
	 */
	public byte[] getClassFile(String name) {
		FlowEditor.log("Requesting class resource: " + name);
		return getBytes(RepositoryWebOpCodes.GET_CLASS, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getProcessStateHistory(int,
	 * java.lang.String)
	 */
	public byte[] getProcessStateHistory(int flowid, String pnumber) {
		return getBytes(RepositoryWebOpCodes.GET_FLOW_STATE_HISTORY, flowid + ";" + pnumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getProcessStateLog(int,
	 * java.lang.String, int)
	 */
	public byte[] getProcessStateLog(int flowid, String pnumber, int state) {
		int subpid = -1;
		return getBytes(RepositoryWebOpCodes.GET_FLOW_STATE_LOGS, flowid + ";" + pnumber + ";" + subpid + ";" + state);
	}

	// 3. Setters

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#deployFlow(java.lang.String,
	 * java.lang.String, byte[])
	 */
	public int deployFlow(String flowName, String description, byte[] data) {
		byte[] result = sendBytes(RepositoryWebOpCodes.SET_FLOW, flowName, description, data, null);
		if (null == result || result.length == 0) {
			return 0;
		}

		int n = -1;
		try {
			n = Integer.parseInt(new String(result, "UTF-8"));
		} catch (NumberFormatException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#deploySubFlow(java.lang.String,
	 * java.lang.String, byte[])
	 */
	public int deploySubFlow(String flowName, String description, byte[] data) {
		byte[] result = sendBytes(RepositoryWebOpCodes.SET_SUBFLOW, flowName, description, data, null);

		int n = -1;
		try {
			n = Integer.parseInt(new String(result, "UTF-8"));
		} catch (NumberFormatException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#setWebService(java.lang.String, byte[])
	 */
	public boolean setWebService(String name, byte[] wsdl) {
		return sendBytes(RepositoryWebOpCodes.SET_WEBSERVICE, name, null, wsdl, null) != null;
	}

	// 4. Class loading

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#loadClass(java.lang.String)
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return Class.forName(name, true, _classLoader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#loadClass(java.lang.String,
	 * java.lang.String)
	 */
	public Class<?> loadClass(String path, String name) throws ClassNotFoundException {
		return loadClass(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#deleteFile(java.lang.String)
	 */
	public void deleteFile(String name) {
	}

	// 5. Extended info

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#hasExtendedAPI()
	 */
	public boolean hasExtendedAPI() {
		if (null == extendedAPI) {
			String str = getString(RepositoryWebOpCodes.HAS_EXTENDED_API, null);
			if (null == str)
				str = "false"; //$NON-NLS-1$
			extendedAPI = new Boolean(str);
		}

		return extendedAPI.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listFlowsExtended()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Collection<FlowInfo> listFlowsExtended() {
		if (!hasExtendedAPI())
			return null;

		byte[] data = getBytes(RepositoryWebOpCodes.LIST_FLOWS_EXTENDED, "flow"); //$NON-NLS-1$
		Collection<FlowInfo> result = new ArrayList<FlowInfo>();
		if (null != data && data.length > 0) {
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			InputSource src = new InputSource(input);// Automatically retrieve encoding from inputstream

			try {
				JAXBContext context = JAXBContext.newInstance(FlowInfoList.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				result = ((FlowInfoList)unmarshaller.unmarshal(src)).getElements();
			} catch (Exception e) {
				FlowEditor.log("error", e);
			}
			src = null;
			input = null;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getFlowInfo(java.lang.String)
	 */
	public FlowInfo getFlowInfo(String name) {
		if (!hasExtendedAPI())
			return null;
		byte[] data = getBytes(RepositoryWebOpCodes.GET_FLOW_INFO, name);
		FlowInfo result = null;
		if (null != data && data.length > 0) {
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			InputSource src = new InputSource(input);// Automatically retrieve encoding from inputstream

			try {
				JAXBContext context = JAXBContext.newInstance(FlowInfo.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				result = (FlowInfo) unmarshaller.unmarshal(src);
			} catch (Exception e) {
				FlowEditor.log("error", e);
			}
			src = null;
			input = null;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#logout()
	 */
	public void logout() {
		sendString(RepositoryWebOpCodes.LOGOUT, null, "logout");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getZippedIcons()
	 */
	public byte[] getZippedIcons() {
		return getBytes(RepositoryWebOpCodes.GET_ZIPPED_ICONS, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getZippedLibraries()
	 */
	public byte[] getZippedLibraries() {
		return getBytes(RepositoryWebOpCodes.GET_ZIPPED_LIBRARIES, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getLastModifiedIcons()
	 */
	public long getLastModifiedIcons() {
		String slmod = getString(RepositoryWebOpCodes.GET_LAST_MODIFIED_ICONS, null);
		long lmod = 0L;
		try {
			lmod = Long.parseLong(slmod);
		} catch (Throwable t) {
			lmod = 0L;
		}
		return lmod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getLastModifiedLibraries()
	 */
	public long getLastModifiedLibraries() {
		String slmod = getString(RepositoryWebOpCodes.GET_LAST_MODIFIED_LIBRARIES, null);
		long lmod = 0L;
		try {
			lmod = Long.parseLong(slmod);
		} catch (Throwable t) {
			lmod = 0L;
		}
		return lmod;
	}

	// Hashing

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getFlowHash(java.lang.String)
	 */
	public String getFlowHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_FLOW, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getSubFlowHash(java.lang.String)
	 */
	public String getSubFlowHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_SUBFLOW, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getLibraryHash(java.lang.String)
	 */
	public String getLibraryHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_LIBRARY, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getWebServiceHash(java.lang.String)
	 */
	public String getWebServiceHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_WEBSERVICE, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getIconHash(java.lang.String)
	 */
	public String getIconHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_ICON, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getClassHash(java.lang.String)
	 */
	public String getClassHash(String name) {
		return getString(RepositoryWebOpCodes.HASH_CLASS, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getIconsHash()
	 */
	public String getIconsHash() {
		return getString(RepositoryWebOpCodes.GET_ICONS_HASH, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getLibrariesHash()
	 */
	public String getLibrariesHash() {
		return getString(RepositoryWebOpCodes.GET_LIBRARIES_HASH, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getModifiedClasses(byte[])
	 */
	public byte[] getModifiedClasses(byte[] checksums) {
		return sendBytes(RepositoryWebOpCodes.GET_MODIFIED_CLASS_HASH, null, null, checksums, null);
	}

	// Others

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getUserLocale()
	 */
	public String getUserLocale() {
		return getString(RepositoryWebOpCodes.GET_USER_LOCALE, null);
	}

	// versioning
	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listFlowVersions(java.lang.String)
	 */
	public String[] listFlowVersions(String flow) {
		return getList(RepositoryWebOpCodes.LIST_FLOW_VERSIONS, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listSubFlowVersions(java.lang.String)
	 */
	public String[] listSubFlowVersions(String flow) {
		return getList(RepositoryWebOpCodes.LIST_SUB_FLOW_VERSIONS, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getFlowVersionComment(java.lang.String,
	 * int)
	 */
	public String getFlowVersionComment(String flow, int version) {
		return getString(RepositoryWebOpCodes.GET_FLOW_VERSION_COMMENT, version + ";" + flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.iknow.iflow.RepositoryClient#getSubFlowVersionComment(java.lang.String,
	 * int)
	 */
	public String getSubFlowVersionComment(String flow, int version) {
		return getString(RepositoryWebOpCodes.GET_SUB_FLOW_VERSION_COMMENT, version + ";" + flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getFlowVersion(java.lang.String, int)
	 */
	public byte[] getFlowVersion(String flow, int version) {
		return getBytes(RepositoryWebOpCodes.GET_FLOW_VERSION, version + ";" + flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getSubFlowVersion(java.lang.String, int)
	 */
	public byte[] getSubFlowVersion(String flow, int version) {
		return getBytes(RepositoryWebOpCodes.GET_SUB_FLOW_VERSION, version + ";" + flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#deployFlowVersion(java.lang.String,
	 * java.lang.String, byte[], java.lang.String)
	 */
	public int deployFlowVersion(String flowName, String description, byte[] data, String comment) {
		byte[] result = sendBytes(RepositoryWebOpCodes.SET_FLOW_VERSION, flowName, description, data, comment);
		if (null == result || result.length == 0) {
			return 0;
		}

		int n = -1;
		try {
			n = Integer.parseInt(new String(result, "UTF-8"));
		} catch (NumberFormatException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#deploySubFlowVersion(java.lang.String,
	 * java.lang.String, byte[], java.lang.String)
	 */
	public int deploySubFlowVersion(String flowName, String description, byte[] data, String comment) {
		byte[] result = sendBytes(RepositoryWebOpCodes.SET_SUB_FLOW_VERSION, flowName, description, data, comment);

		int n = -1;
		try {
			n = Integer.parseInt(new String(result, "UTF-8"));
		} catch (NumberFormatException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return n;
	}

	// New features

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listDataSources()
	 */
	public String[] listDataSources() {
		return getList(RepositoryWebOpCodes.LIST_DATA_SOURCES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getConnectors()
	 */
	public byte[] getConnectors() {
		return getBytes(RepositoryWebOpCodes.GET_CONNECTORS, "");
	}

	// Flow Docs
	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#flowDocsGetFiles(java.lang.String,
	 * java.lang.String)
	 */
	public byte[] flowDocsGetFiles(String searchValue, String path) {
		return getBytes(RepositoryWebOpCodes.FLOW_DOC_GET_FILES, searchValue + ";" + path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#listSignatureTypes()
	 */
	public String[] listSignatureTypes() {
		return getList(RepositoryWebOpCodes.LIST_SIGNATURE_TYPES);
	}

	// BD Synch
	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getTableDesc()
	 */
	public byte[] getTableDesc(String jndiName, String table) {
		return getBytes(RepositoryWebOpCodes.DB_TABLE_DESC, jndiName + ";" + table);
	}

	private RepositoryStatusListener statusListener = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#setStatusListener(pt.iknow.iflow.
	 * RepositoryStatusListener)
	 */
	public void setStatusListener(RepositoryStatusListener listener) {
		this.statusListener = listener;
	}

	// upload status notification
	protected void notifyStart(long start, long total) {
		if (null != statusListener) {
			statusListener.start(start, total);
		}
	}

	protected void notifyDone(long done) {
		if (null != statusListener) {
			statusListener.done(done);
		}
	}

	protected void notifyComplete() {
		if (null != statusListener) {
			statusListener.finish();
		}
	}

	private class RepositoryFilePart extends FilePart {
		public RepositoryFilePart(final String param, final String name, final byte[] data) {
			super(param, new ByteArrayPartSource(null == name ? "NONE" : name, data));
		}

		/**
		 * Write the data in "source" to the specified stream.
		 * 
		 * @param out
		 *            The output stream.
		 * @throws IOException
		 *             if an IO problem occurs.
		 * @see org.apache.commons.httpclient.methods.multipart.Part#sendData(OutputStream)
		 */
		protected void sendData(OutputStream out) throws IOException {
			PartSource source = getSource();
			long size = lengthOfData();
			notifyStart(0, size);
			if (size > 0) {
				byte[] tmp = new byte[1024];
				InputStream instream = source.createInputStream();
				try {
					int len;
					while ((len = instream.read(tmp)) >= 0) {
						out.write(tmp, 0, len);
						notifyDone(len);
					}
				} finally {
					// we're done with the stream, close it
					instream.close();
				}
			}
			notifyComplete();
		}

	}

	/*
	 * public void deleteProperty(String name) { }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#reloadClassLoader()
	 */
	public synchronized void reloadClassLoader() {
		this._classLoader = null;
		this._classLoader = new RepositoryClassLoader(classCacheFolder, this, this._parentLoader); // $NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getBaseURL()
	 */
	public String getBaseURL() {
		return baseURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getCookie()
	 */
	public String getCookie() {
		return new CookieSpecBase().formatCookieHeader(getCookies()).getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.iknow.iflow.RepositoryClient#getCookies()
	 */
	public Cookie[] getCookies() {
		return client.getState().getCookies();
	}

	public FlowRepUrl getIFlowURL() {
		return this.iFlowURL;
	}

	public byte[] getMessages(String file) {
		FlowEditor.log("Requesting messages resource: " + file);
		return getBytes(RepositoryWebOpCodes.GET_I18N_MESSAGES, file);
	}

	public byte[] getModifiedMessages(byte[] checksums) {
		return sendBytes(RepositoryWebOpCodes.GET_MODIFIED_I18N_HASH, null, null, checksums, null);
	}

	public String[] listTaskAnnotationLabels() {
		return getList(RepositoryWebOpCodes.LIST_PROCESS_TASK_ANNOTATION_LABELS);
	}

	public String runFlow(String flowname) {
		String result = getString(RepositoryWebOpCodes.RUN_FLOW, flowname);
		return result;
	}

	public String undeployFlow(String flowname) {
		String result = getString(RepositoryWebOpCodes.UNDEPLOY_FLOW, flowname);
		return result;
	}

	public String setFlowReady2Run(String flowname) {
		String result = getString(RepositoryWebOpCodes.SET_R2R_FLOW, flowname);
		return result;
	}
}
