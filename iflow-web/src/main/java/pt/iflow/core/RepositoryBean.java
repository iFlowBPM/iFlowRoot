package pt.iflow.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.core.ResourceModifiedListener;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.XslTransformerFactory;

import org.apache.commons.io.FilenameUtils;

/**
 * <p>
 * Title: RepositoryBean
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow - Consultoria em Tecnologias de Informacao, Lda
 * </p>
 * 
 * @author Luis Guerra e Silva
 * @version 1.0
 */

public class RepositoryBean implements Repository {

	// CACHE STUFF
	private static RepositoryBean instance = null;

	/*
	 * Template para nomes de ficheiros: {0} = ID da organização {1} = nome do
	 * ficheiro que foi requisitado {2} = identificador do locale para ficheiros
	 * localizados.
	 * 
	 * a template "{0}{2}/StyleSheets/{1}" na organização "3", ficheiro
	 * "welcome.xsl" e chave de locale "_pt_PT" será transformado em:
	 * "3_pt_PT/StyleSheets/welcome.xsl"
	 */

	// ISTO DEPOIS VAI PARA O CONST??
	private static final String sSTYLESHEETS_DIR_TEMPLATE = "{0}{2}/StyleSheets/{1}";
	private static final String sEMAILS_DIR_TEMPLATE = "{0}{2}/Mail/{1}";
	private static final String sPRINTS_DIR_TEMPLATE = "{0}{2}/Templates/{1}";
	private static final String sWEBFILES_DIR_TEMPLATE = "{0}{2}/WebFiles/{1}";
	private static final String sLOGO_TEMPLATE = "{0}{2}/logo.png";
	private static final String sCLASS_DIR_TEMPLATE = "{0}{2}/Classes/{1}";
	private static final String sCHARTS_DIR_TEMPLATE = "{0}{2}/Charts/{1}";
	private static String sLIBRARIES_DIR_TEMPLATE;
	private static final String sICONS_DIR_TEMPLATE = "{0}{2}/Icons/{1}";
	private static final String sANNOTATION_ICONS_DIR_TEMPLATE = "{0}{2}/Icons/annotations/{1}";
	private static final String sTHEMES_DIR_TEMPLATE = "{0}{2}/Themes/{1}";
	private static final String sWEBSERVICES_DIR_TEMPLATE = "{0}{2}/WSDL/{1}";
	private static final String sHELP_DIR_TEMPLATE = "{0}{2}/Help/{1}";
	private static final String sMESSAGES_DIR_TEMPLATE = "{0}{2}/Messages/{1}";
	private static final String sLOGFILES_DIR_TEMPLATE = "log/{1}";
	// private static final String sBLOCKS_DIR_TEMPLATE = "{0}/Blocks/{1}"; // The
	// future is Block!

	private static final File REPOSITORY_ROOT;

	static {
		REPOSITORY_ROOT = new File(Const.sIFLOW_HOME + "/repository_data");
	}

	protected RepositoryBean() {
		if (StringUtils.equals(Const.EDITOR_MODE_BPMN, Setup.getProperty(Const.EDITOR_MODE)))
			sLIBRARIES_DIR_TEMPLATE = "{0}{2}/Libraries_BPMN/{1}";
		else
			sLIBRARIES_DIR_TEMPLATE = "{0}{2}/Libraries/{1}";
	}

	/**
	 *
	 * Get a Repositoy instance
	 * 
	 * @return
	 */
	public static RepositoryBean getInstance() {
		if (null == instance)
			instance = new RepositoryBean();
		return instance;
	}

	// CACHE MANAGEMENT
	public synchronized void resetCache(UserInfoInterface userInfo) {
		classLoadersTable.clear();
	}
	// CACHE MANAGEMENT END

	public boolean checkConnection() {
		return true;
	}

	private static String getLocaleKey(String orgName) {
		Locale loc = BeanFactory.getSettingsBean().getOrganizationLocale(orgName);
		String chave = "";
		if (null != loc)
			chave = "_" + loc.getLanguage() + "_" + loc.getCountry();
		return chave;
	}

	// User validation
	/**
	 * Check if an user the org admin
	 * 
	 * @param userInfo
	 *            user to validate
	 * @return true if the user is the organization admin
	 */
	private boolean isOrgAdmin(UserInfoInterface userInfo) {
		return userInfo.isOrgAdmin();
	}

	/******* New Interface **********/
	private RepositoryFile getData(String name, String orgFileName, String locSysFileName, String sysFileName) {
		File orgFile = null;
		File locSysFile = null;
		File sysFile = null;

		if (null != orgFileName) {
			orgFile = new File(REPOSITORY_ROOT, PathNormalizer.cleanString( orgFileName ));
		}
		if (null != locSysFileName) {
			locSysFile = new File(REPOSITORY_ROOT, PathNormalizer.cleanString(locSysFileName));
		}
		if (null != sysFileName) {
			sysFile = new File(REPOSITORY_ROOT, PathNormalizer.cleanString(sysFileName));
			if (sysFile == null || !sysFile.exists() || !sysFile.isFile()) {
				sysFile = new File(Const.sIFLOW_HOME, sysFileName);
			}
		}

		boolean isOrg = (null != orgFile && orgFile.exists() && orgFile.isFile());
		boolean isLocSys = (null != locSysFile && locSysFile.exists() && locSysFile.isFile());
		boolean isSys = (null != sysFile && sysFile.exists() && sysFile.isFile());

		File theFile = null;
		if (isSys)
			theFile = sysFile;
		if (isLocSys)
			theFile = locSysFile;
		if (isOrg)
			theFile = orgFile;

		return new RepositoryFileImpl(name, theFile, (isOrg), (isSys || isLocSys));
	}

	private RepositoryFile getResourceData(String nameTpl, String org, String name) {
		if (null == name)
			return null;
		// validate file
		int lpos = name.lastIndexOf('/');
		if (lpos > -1)
			name = name.substring(lpos + 1);

		String localeKey = getLocaleKey(org);
		String orgname = MessageFormat.format(nameTpl, new Object[] { org, name, "" });
		String locSysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, name, localeKey });
		String sysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, name, "" });

		return getData(name, orgname, locSysname, sysname);
	}
	// getters

	public RepositoryFile getStyleSheet(UserInfoInterface userInfo, String xsl) {
		return getResourceData(sSTYLESHEETS_DIR_TEMPLATE, userInfo.getOrganization(), xsl);
	}

	public RepositoryFile getEmailTemplate(UserInfoInterface userInfo, String tpl) {
		return getResourceData(sEMAILS_DIR_TEMPLATE,
				null == userInfo ? Const.SYSTEM_ORGANIZATION : userInfo.getOrganization(), tpl);
	}

	public RepositoryFile getEmailTemplate(String tpl) {
		return getResourceData(sEMAILS_DIR_TEMPLATE, Const.SYSTEM_ORGANIZATION, tpl);
	}

	public RepositoryFile getPrintTemplate(UserInfoInterface userInfo, String tpl) {
		return getResourceData(sPRINTS_DIR_TEMPLATE, userInfo.getOrganization(), tpl);
	}

	public RepositoryFile getWebFile(UserInfoInterface userInfo, String file) {
		String org = Const.SYSTEM_ORGANIZATION;
		if (userInfo != null)
			org = userInfo.getOrganization();
		return getResourceData(sWEBFILES_DIR_TEMPLATE, org, file);
	}

	public RepositoryFile getLogFile(UserInfoInterface userInfo, String file) {
		RepositoryFile retObj = null;
		if (file != null && userInfo != null && userInfo.isSysAdmin()) {
			retObj = getData(file, null, null,
					MessageFormat.format(sLOGFILES_DIR_TEMPLATE, new Object[] { Const.SYSTEM_ORGANIZATION, file, "" }));
		}
		return retObj;
	}

	public RepositoryFile getLogo(UserInfoInterface userInfo) {
		return getResourceData(sLOGO_TEMPLATE, userInfo.getOrganization(), "");
	}

	@Deprecated
	public RepositoryFile getChartFile(String file) {
		return getResourceData(sCHARTS_DIR_TEMPLATE, Const.SYSTEM_ORGANIZATION, file);
	}

	public RepositoryFile getChartFile(UserInfoInterface userInfo, String file) {
		return getResourceData(sCHARTS_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getLibrary(UserInfoInterface userInfo, String file) {
		return getResourceData(sLIBRARIES_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getIcon(UserInfoInterface userInfo, String file) {
		return getResourceData(sICONS_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getAnnotationIcon(UserInfoInterface userInfo, String file) {
		return getResourceData(sANNOTATION_ICONS_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getWebService(UserInfoInterface userInfo, String file) {
		return getResourceData(sWEBSERVICES_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getClassFile(String org, String fileName) {
		String orgname = MessageFormat.format(sCLASS_DIR_TEMPLATE, new Object[] { org, fileName, "" });
		String sysname = MessageFormat.format(sCLASS_DIR_TEMPLATE,
				new Object[] { Const.SYSTEM_ORGANIZATION, fileName, "" });

		return getData(fileName, orgname, null, sysname);
	}

	public RepositoryFile getTheme(UserInfoInterface userInfo, String file) {
		return getResourceData(sTHEMES_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getHelp(UserInfoInterface userInfo, String file) {
		return getResourceData(sHELP_DIR_TEMPLATE, userInfo.getOrganization(), file);
	}

	public RepositoryFile getMessagesFile(UserInfoInterface userInfo, String fileName) {
		return getMessagesFile(userInfo.getOrganization(), fileName);
	}

	public RepositoryFile getMessagesFile(String org, String fileName) {
		String orgname = null;
		if (null != org) {
			orgname = MessageFormat.format(sMESSAGES_DIR_TEMPLATE, new Object[] { org, fileName, "" });
		}
		String sysname = MessageFormat.format(sMESSAGES_DIR_TEMPLATE,
				new Object[] { Const.SYSTEM_ORGANIZATION, fileName, "" });

		return getData(fileName, orgname, null, sysname);
	}

	// setters
	private synchronized boolean setResourceData(String nameTpl, UserInfoInterface user, String name, byte[] data,
			ResourceModifiedListener evt) {
		if (null == name)
			return false;
		if (null == data)
			return false;

		// validate file
		int lpos = name.lastIndexOf('/');
		if (lpos > -1)
			name = name.substring(lpos + 1);

		String fullname = MessageFormat.format(nameTpl, new Object[] { user.getOrganization(), name, "" });

		File file = new File(REPOSITORY_ROOT, fullname);
		File parentFile = file.getParentFile();
		boolean ok = true;
		if (!parentFile.exists()) {
			ok = parentFile.mkdirs();
		}

		if (ok) {			
			try (FileOutputStream fout = new FileOutputStream(file);){
				fout.write(data);
				ok = true;				
			} catch (IOException e) {
				e.printStackTrace();
				ok = false;
			}   
			
			// fire event
			if (null != evt)
				evt.resourceModified(user, name, fullname);
		}

		return ok;
	}

	public boolean setStyleSheet(UserInfoInterface userInfo, String xsl, byte[] data) {
		return setResourceData(sSTYLESHEETS_DIR_TEMPLATE, userInfo, xsl, data,
				XslTransformerFactory.getResourceModifiedListener());
	}

	public boolean setEmailTemplate(UserInfoInterface userInfo, String tpl, byte[] data) {
		return setResourceData(sEMAILS_DIR_TEMPLATE, userInfo, tpl, data, EmailManager.getResourceModifiedListener());
	}

	public boolean setPrintTemplate(UserInfoInterface userInfo, String tpl, byte[] data) {
		return setResourceData(sPRINTS_DIR_TEMPLATE, userInfo, tpl, data, null);
	}

	public boolean setWebFile(UserInfoInterface userInfo, String file, byte[] data) {
		return setResourceData(sWEBFILES_DIR_TEMPLATE, userInfo, file, data, null);
	}

	public boolean setLogo(UserInfoInterface userInfo, byte[] data) {
		return setResourceData(sLOGO_TEMPLATE, userInfo, "", data, null);
	}

	public boolean setChartFile(UserInfoInterface userInfo, String file, byte[] data) {
		return setResourceData(sCHARTS_DIR_TEMPLATE, userInfo, file, data, null);
	}

	public boolean setWebService(UserInfoInterface userInfo, String file, byte[] data) {
		return setResourceData(sWEBSERVICES_DIR_TEMPLATE, userInfo, file, data, null);
	}

	// file listing
	private synchronized RepositoryFile[] listResources(String nameTpl, String org) {
		String localeKey = getLocaleKey(org);
		String orgname = MessageFormat.format(nameTpl, new Object[] { org, "", "" });
		String locsysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, "", localeKey });
		String sysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, "", "" });

		File orgDir = new File(REPOSITORY_ROOT, orgname);
		File sysDir = new File(REPOSITORY_ROOT, sysname);
		File locsysDir = new File(REPOSITORY_ROOT, locsysname);

		File[] orgfiles = orgDir.listFiles(fileNameFilter);
		File[] sysfiles = sysDir.listFiles(fileNameFilter);
		File[] locsysfiles = locsysDir.listFiles(fileNameFilter);

		// List localized system files then "default" system files
		TreeSet<RepositoryFile> sysFileSet = new TreeSet<RepositoryFile>();
		for (int i = 0; null != locsysfiles && i < locsysfiles.length; i++) {
			sysFileSet.add(new RepositoryFileImpl(locsysfiles[i], false, true));
		}

		for (int i = 0; null != sysfiles && i < sysfiles.length; i++) {
			sysFileSet.add(new RepositoryFileImpl(sysfiles[i], false, true));
		}

		// List localized organization files then "default" organization files
		TreeSet<RepositoryFile> orgFileSet = new TreeSet<RepositoryFile>();
		for (int i = 0; null != orgfiles && i < orgfiles.length; i++) {
			RepositoryFileImpl f = new RepositoryFileImpl(orgfiles[i], true, false);
			if (sysFileSet.contains(f)) {
				f.system = true;
				sysFileSet.remove(f);
			}
			orgFileSet.add(f);
		}
		orgFileSet.addAll(sysFileSet);

		return orgFileSet.toArray(new RepositoryFile[orgFileSet.size()]);
	}

	public RepositoryFile[] listStyleSheets(UserInfoInterface userInfo) {
		return listResources(sSTYLESHEETS_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listEmailTemplates(UserInfoInterface userInfo) {
		return listResources(sEMAILS_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listPrintTemplates(UserInfoInterface userInfo) {
		return listResources(sPRINTS_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listWebFiles(UserInfoInterface userInfo) {
		return listResources(sWEBFILES_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listChartFiles(UserInfoInterface userInfo) {
		return listResources(sCHARTS_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listLibraries(UserInfoInterface userInfo) {
		return listResources(sLIBRARIES_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listWebServices(UserInfoInterface userInfo) {
		return listResources(sWEBSERVICES_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listIcons(UserInfoInterface userInfo) {
		return listResources(sICONS_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public RepositoryFile[] listMessages(UserInfoInterface userInfo) {
		return listResources(sMESSAGES_DIR_TEMPLATE, userInfo.getOrganization());
	}

	public List<Class<?>> listClasses(UserInfoInterface userInfo, String inPackage) {
		if (StringUtils.isNotEmpty(inPackage)) {
			inPackage = StringUtils.replace(inPackage, ".", "/");
			if (!inPackage.endsWith("/"))
				inPackage += "/";
		}
		String tpl = sCLASS_DIR_TEMPLATE + inPackage;
		RepositoryFile[] classFiles = listResources(tpl, userInfo.getOrganization());

		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (RepositoryFile classFile : classFiles) {
			String className = normalizeClassName(inPackage + classFile.getName());
			try {
				classes.add(loadClass(userInfo, className));
			} catch (ClassNotFoundException e) {
				Logger.warning(userInfo.getUtilizador(), this, "listClasses",
						"Could not load class " + className + "(ignoring)", e);
			}
		}
		return classes;
	}

	private static String normalizeClassName(String name) {
		if (StringUtils.isNotEmpty(name)) {
			name = StringUtils.replace(name, "/", ".");
			name = StringUtils.remove(name, ".class");
		}
		return name;
	}

	// Class loading stuff...

	public Class<?> loadClass(UserInfoInterface userInfo, String className) throws ClassNotFoundException {
		return loadClass(userInfo.getOrganization(), className);
	}

	public Class<?> loadClass(String organization, String className) throws ClassNotFoundException {
		Class<?> result = null;
		try {
			result = getClassLoader(organization).loadClass(className);// Class.forName(className, true,
																		// getClassLoader(organization));
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (Throwable t) {
			Logger.error(null, this, "loadClass", "Could not load class " + className, t);
			throw new ClassNotFoundException("Class not loaded", t);
		}
		return result;
	}

	private static final Hashtable<String, ClassLoader> classLoadersTable = new Hashtable<String, ClassLoader>();

	private ClassLoader getClassLoader(String organization) {
		ClassLoader cl = classLoadersTable.get(organization);
		if (cl == null) {
			cl = newClassLoader(organization);
			classLoadersTable.put(organization, cl);
		}

		return cl;
	}

	private ClassLoader newClassLoader(String org) {
		// return new RepositoryBeanClassLoader(org);
		String localeKey = getLocaleKey(org);
		String orgname = MessageFormat.format(sCLASS_DIR_TEMPLATE, new Object[] { org, "", "" });
		String locsysname = MessageFormat.format(sCLASS_DIR_TEMPLATE,
				new Object[] { Const.SYSTEM_ORGANIZATION, "", localeKey });
		String sysname = MessageFormat.format(sCLASS_DIR_TEMPLATE, new Object[] { Const.SYSTEM_ORGANIZATION, "", "" });

		File orgDir = new File(REPOSITORY_ROOT, orgname);
		File sysDir = new File(REPOSITORY_ROOT, sysname);
		File locsysDir = new File(REPOSITORY_ROOT, locsysname);

		// prepare URLs for URLClassLoader

		List<URL> urls = new ArrayList<URL>();

		// 1. Organization specific files
		if (orgDir.exists()) {
			try {
				urls.add(orgDir.toURL()); // XXX check if a trailing slash is needed
			} catch (MalformedURLException e) {
			}
			File[] orgfiles = orgDir.listFiles(jarFileFilter);

			for (int i = 0; null != orgfiles && i < orgfiles.length; i++) {
				try {
					urls.add(orgfiles[i].toURL());
				} catch (MalformedURLException e) {
				}
			}
		}

		// 2. System localized (Locale) specific files
		if (locsysDir.exists()) {
			try {
				urls.add(locsysDir.toURL()); // XXX check if a trailing slash is needed
			} catch (MalformedURLException e) {
			}
			File[] files = locsysDir.listFiles(jarFileFilter);

			for (int i = 0; null != files && i < files.length; i++) {
				try {
					urls.add(files[i].toURL());
				} catch (MalformedURLException e) {
				}
			}
		}

		// 3. System files
		if (sysDir.exists()) {
			try {
				urls.add(sysDir.toURL()); // XXX check if a trailing slash is needed
			} catch (MalformedURLException e) {
			}
			File[] files = sysDir.listFiles(jarFileFilter);

			for (int i = 0; null != files && i < files.length; i++) {
				try {
					urls.add(files[i].toURL());
				} catch (MalformedURLException e) {
				}
			}
		}

		// use current classloader as parent
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
	}

	public synchronized void reloadClassLoaders(UserInfoInterface userInfo) {
		if (!isOrgAdmin(userInfo)) {
			Logger.warning(userInfo.getUtilizador(), this, "reloadClassLoaders", "User not authorized.");
			return;
		}

		classLoadersTable.clear();
		// force classloaders to be garbage collected
		System.gc();
		System.gc();
	}

	public synchronized void reloadClassLoader(UserInfoInterface userInfo) {
		if (!isOrgAdmin(userInfo)) {
			Logger.warning(userInfo.getUtilizador(), this, "reloadClassLoader", "User not authorized.");
			return;
		}

		ClassLoader cl = (ClassLoader) classLoadersTable.remove(userInfo.getOrganization());
		if (null != cl && cl instanceof RepositoryBeanClassLoader)
			((RepositoryBeanClassLoader) cl).reset();
		cl = null;
		// force classloaders to be garbage collected
		System.gc();
		System.gc();
	}

	private static final class RepositoryFileImpl implements RepositoryFile, Comparable<RepositoryFile> {
		private File file;
		private boolean org;
		private boolean system;
		private String name;

		private RepositoryFileImpl(File file, boolean org, boolean system) {
			this(file.getName(), file, org, system);
		}

		/**
		 * Create a new Repository File view.
		 * 
		 * A repository file can be both organization and system.
		 * 
		 * @param name
		 *            File name
		 * @param file
		 *            The file that this instance refers to
		 * @param org
		 *            is organization file
		 * @param system
		 *            is system file
		 */
		private RepositoryFileImpl(String name, File file, boolean org, boolean system) {
			this.name = name;
			this.file = file;
			this.org = org;
			this.system = system;
		}

		/**
		 * Get the file name
		 * 
		 * @return File name
		 */
		public String getName() {
			return name;
		}

		public boolean isOrganization() {
			return org;
		}

		public boolean isSystem() {
			return system;
		}

		public String toString() {
			return getName();
		}

		public boolean equals(Object o) {
			if (o == null || !(o instanceof RepositoryFile))
				return false;
			return compareTo((RepositoryFileImpl) o) == 0;
		}

		public int compareTo(RepositoryFile f) {
			return getName().compareTo(f.getName());
		}

		public InputStream getResourceAsStream() {
			if (null == file)
				return null;
			InputStream inStream = null;
			try (FileInputStream fin = new FileInputStream(file)){
				inStream = new BufferedInputStream(fin);
			} catch (IOException e) {
				Logger.error(null, this, "getResourceData", "Error opening file " + file + ": " + e.getMessage(), e);
			}
			
			return inStream;
		}

		public byte[] getResouceData() {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			writeToStream(bout);
			return bout.toByteArray();
		}

		public void writeToStream(OutputStream outStream) {
			try (InputStream in = getResourceAsStream();) {
				if (null == in)
					return;
				byte[] b = new byte[2048];
				int r = -1;
				try {
					while ((r = in.read(b)) != -1)
						outStream.write(b, 0, r);
				} catch (IOException e) {
					Logger.error(null, this, "writeToStream", "Error reading file " + file + ": " + e.getMessage(), e);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						Logger.error(null, this, "writeToStream", "Error closing file " + file + ": " + e.getMessage(), e);
					}
				}
			} catch (IOException e1) {}

		}

		public int getSize() {
			if (null == file)
				return 0;
			return (int) file.length();
		}

		public boolean exists() {
			return file != null && file.exists();
		}

		public long getLastModified() {
			if (null == file)
				return 0L;
			return file.lastModified();
		}

		public URL getURL() {
			if (null == file)
				return null;
			URL url = null;
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException e) {
				Logger.error(null, this, "getURL", "Error creating URL for file " + file + ": " + e.getMessage(), e);
			}
			return url;
		}

	}

	// File removal methods
	private boolean removeResource(String nameTpl, UserInfoInterface user, String name, ResourceModifiedListener evt) {
		if (null == name)
			return false;
		// validate file
		int lpos = name.lastIndexOf('/');
		if (lpos > -1)
			name = name.substring(lpos + 1);

		String fullname = MessageFormat.format(nameTpl, new Object[] { user.getOrganization(), name, "" });

		File file = new File(REPOSITORY_ROOT, fullname);
		File parentFile = file.getParentFile();
		boolean ok = true;
		if (parentFile.exists()) {
			ok = file.delete();
			// notify listeners
			if (null != evt)
				evt.resourceModified(user, name, fullname);
		}

		return ok;

	}

	public boolean removeEmailTemplate(UserInfoInterface userInfo, String file) {
		return removeResource(sEMAILS_DIR_TEMPLATE, userInfo, file, EmailManager.getResourceModifiedListener());
	}

	public boolean removePrintTemplate(UserInfoInterface userInfo, String file) {
		return removeResource(sPRINTS_DIR_TEMPLATE, userInfo, file, null);
	}

	public boolean removeStyleSheet(UserInfoInterface userInfo, String file) {
		return removeResource(sSTYLESHEETS_DIR_TEMPLATE, userInfo, file,
				XslTransformerFactory.getResourceModifiedListener());
	}

	public boolean removeWebFile(UserInfoInterface userInfo, String file) {
		return removeResource(sWEBFILES_DIR_TEMPLATE, userInfo, file, null);
	}

	// File reset

	private boolean resetResource(String nameTpl, UserInfoInterface userInfo, String name,
			ResourceModifiedListener evt) {
		if (null == name)
			return false;
		String org = userInfo.getOrganization();
		if (Const.SYSTEM_ORGANIZATION.equals(org))
			return false; // Cannot reset a System File
		String localeKey = getLocaleKey(org);
		String orgname = MessageFormat.format(nameTpl, new Object[] { org, name, "" });
		String locysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, name, localeKey });
		String sysname = MessageFormat.format(nameTpl, new Object[] { Const.SYSTEM_ORGANIZATION, name, "" });

		File orgFile = new File(REPOSITORY_ROOT, orgname);
		File sysFile = new File(REPOSITORY_ROOT, locysname);

		if (!sysFile.exists() || !sysFile.isFile()) {
			sysFile = new File(REPOSITORY_ROOT, sysname);
			if (!sysFile.exists() || !sysFile.isFile())
				return false; // Cannot reset without sys file
		}

		orgFile.getParentFile().mkdirs(); // ensure path exist

		boolean ok = false;
		try (FileOutputStream fout = new FileOutputStream(orgFile);
				FileInputStream	fin = new FileInputStream(sysFile);){
			
			byte[] b = new byte[8092];
			int r = -1;
			while ((r = fin.read(b)) != -1)
				fout.write(b, 0, r);
			ok = true;

			if (null != evt)
				evt.resourceModified(userInfo, name, orgname);
		} catch (FileNotFoundException e) {
			Logger.error(userInfo.getUtilizador(), this, "resetResource",
					"Error resetting file " + name + ": " + e.getMessage(), e);
		} catch (IOException e) {
			Logger.error(userInfo.getUtilizador(), this, "resetResource",
					"Error resetting file " + name + ": " + e.getMessage(), e);
		}

		return ok;
	}

	public boolean resetEmailTemplate(UserInfoInterface userInfo, String file) {
		return resetResource(sEMAILS_DIR_TEMPLATE, userInfo, file, EmailManager.getResourceModifiedListener());
	}

	public boolean resetPrintTemplate(UserInfoInterface userInfo, String file) {
		return resetResource(sPRINTS_DIR_TEMPLATE, userInfo, file, null);
	}

	public boolean resetStyleSheet(UserInfoInterface userInfo, String file) {
		return resetResource(sSTYLESHEETS_DIR_TEMPLATE, userInfo, file,
				XslTransformerFactory.getResourceModifiedListener());
	}

	public boolean resetWebFile(UserInfoInterface userInfo, String file) {
		return resetResource(sWEBFILES_DIR_TEMPLATE, userInfo, file, null);
	}

	private static SVCFilter fileNameFilter = new SVCFilter();

	private static class SVCFilter implements FileFilter {

		public boolean accept(String name) {
			return !(name.equals(".svn") || name.equals("CVS"));
		}

		public boolean accept(File pathname) {
			if (pathname.isDirectory())
				return false;
			return accept(pathname.getName());
		}

	}

	private static JARFilter jarFileFilter = new JARFilter();

	private static class JARFilter implements FileFilter {

		public boolean accept(String name) {
			String extension = StringUtils.substringAfterLast(name, ".");
			return StringUtils.equalsIgnoreCase(extension, "jar") || StringUtils.equalsIgnoreCase(extension, "zip");
		}

		public boolean accept(File pathname) {
			if (pathname.isDirectory())
				return false;
			return accept(pathname.getName());
		}

	}

	private final Map<String, ResourceClassLoader> resourceClassloaders = new HashMap<String, ResourceClassLoader>();

	private ResourceClassLoader getResourceClassLoader(String org) {
		ResourceClassLoader rcl = resourceClassloaders.get(org);
		if (null == rcl) {
			rcl = new ResourceClassLoader(org);
			resourceClassloaders.put(org, rcl);
		}
		return rcl;
	}

	public ResourceBundle getBundle(final String bundleName, final Locale locale) {
		return getBundle(bundleName, locale, null);
	}

	public ResourceBundle getBundle(final String bundleName, final Locale locale, final String organization) {
		ResourceBundle b = null;
		try {
			b = ResourceBundle.getBundle(bundleName, locale, getResourceClassLoader(organization));
		} catch (MissingResourceException e) {
			// b = ResourceBundle.getBundle(bundleName);
		}
		return b;
	}

	private class ResourceClassLoader extends ClassLoader {
		private String organization;

		@SuppressWarnings("unused")
		public ResourceClassLoader() {
			this(null);
		}

		public ResourceClassLoader(String organization) {
			this.organization = organization;
		}

		public InputStream getResourceAsStream(String name) {
			RepositoryFile f = getMessagesFile(organization, name);
			if (null == f || !f.exists())
				return super.getResourceAsStream(name);
			return f.getResourceAsStream();
		}
	}
}
