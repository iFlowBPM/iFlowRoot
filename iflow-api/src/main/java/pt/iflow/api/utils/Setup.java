package pt.iflow.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * This class is a bridge between a properties file and the application.
 * 
 * @author rfn
 */
public class Setup {
    
    //private static final String IFLOW_HOME = System.getProperty("iflow.home");
	
	
	private static String IFLOW_HOME = "/userdata/iFlowHome";
	
	
    private static final String MAIN_PROP_FILE = "iflow.properties";
    private static final String AUTH_PROP_FILE = "authentication.properties";
    private static final String FEED_PROP_FILE = "feed.properties";
    private static final String DMS_PROP_FILE = "dms.properties";
    private static final String CERT_PROP_FILE = "certificates.properties";
    private static final String SIGN_PROP_FILE = "signatures.properties";
    private static final String SSO_PROP_FILE = "sso.properties";
    
    
    // Hashtable with properties keys,values
    private static Properties _pMainProperties = new Properties();
    private static Properties _pExtraProperties = new Properties();
    
    private static String _configHome = "";
    
    static {
        loadProperties();
     /*   File file = new File(Setup.loadIflowHome());
        if(file != null && file.isDirectory()){
        	IFLOW_HOME = file.getAbsolutePath();
        }*/
    }
    
    public static Properties readPropertiesFile(String fileName) {
    	
    	
        String sFile = FilenameUtils.concat(_configHome, fileName);
        Properties properties = new Properties();
        
        // Open properties file and get contents
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){            
            properties.load(propertiesFile);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.warning(null, "Setup", "readPropertiesFile",
                    "Error reading file " + sFile, e);
        } 
        
        return properties;
    }
    
    public static Map<String, String> getPropertiesFileAsStringMap(
            final String fileName) {
        return getPropertiesAsStringMap(readPropertiesFile(fileName));
    }
    
    public static Map<String, String> getPropertiesAsStringMap(
            final Properties properties) {
        if (null == properties)
            return null;
        Map<String, String> result = new Hashtable<String, String>(properties
                .size());
        for (Object o : properties.keySet()) {
            if (o instanceof String)
                result.put((String) o, properties.getProperty((String) o));
        }
        
        return result;
    }
    
    /**
     * This method loads the necessary setup parameters.
     */
    public static synchronized void loadProperties() {
        
        String sFile = null;
        
        Properties newMainProperties = new Properties();
        Properties newExtraProperties = new Properties();
        
        _configHome = ((IFLOW_HOME == null) ? "" : IFLOW_HOME);
        _configHome = FilenameUtils.concat(_configHome, "config");
        
        sFile = FilenameUtils.concat(_configHome, MAIN_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newMainProperties.load(propertiesFile);
        } catch (Exception e) {
            Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
            System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, AUTH_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file ("+ sFile + ").", e);
          System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, FEED_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file ("
                    + sFile + ").");
            e.printStackTrace();
            System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, DMS_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, CERT_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, SIGN_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } 
        
        sFile = FilenameUtils.concat(_configHome, SSO_PROP_FILE);
        try (FileInputStream propertiesFile = new FileInputStream(sFile);){
            // Open properties file and get contents
            
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } 
        
        _pMainProperties = newMainProperties;
        _pExtraProperties = newExtraProperties;
    }
    
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
    
    public static String getProperty(String key, String defVal) {
        if (_pMainProperties.containsKey(key))
            return _pMainProperties.getProperty(key);
        else if (_pExtraProperties.containsKey(key))
            return _pExtraProperties.getProperty(key);
        
        return defVal;
    }
    
    public static int getPropertyInt(String key) {
        String stmp = Setup.getProperty(key);
        if (StringUtils.isNotEmpty(stmp)) {
          try {
            return Integer.parseInt(stmp);
          } catch (Exception e) {
          }
        }
        return -1;
    }
    
    public static Properties getProperties() {
        return cloneProperties();
    }
    
    public static void setProperties(Properties apProperties) {
        setProperties(apProperties, false);
    }
    
    public static synchronized void setProperties(Properties apProperties,
            boolean abWriteToFile) {
        _pMainProperties = cloneProperties(apProperties);
        
        if (abWriteToFile) {
            writeProperties();
        }
    }
    
    public static synchronized void writeProperties() {
        
        String stmp = "";
        if (IFLOW_HOME == null) {
            stmp = "config/";
        } else {
            if (IFLOW_HOME.endsWith("/") || IFLOW_HOME.endsWith("\\")) {
                stmp = IFLOW_HOME + "config/";
            } else {
                stmp = IFLOW_HOME + "/config/";
            }
        }
        
        String sFile = stmp + MAIN_PROP_FILE;           
        try (FileOutputStream propertiesFile = new FileOutputStream(sFile);){                                     
            stmp = "iFlow Properties modified in " + new java.util.Date();            
            _pMainProperties.store(propertiesFile, stmp);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Unable to write properties: " + e.getMessage(), e);
        }
    }
    
    private static Properties cloneProperties() {
        return cloneProperties(null);
    }
    
    private static Properties cloneProperties(final Properties apToClone) {
        Properties retObj = null;
        
        Properties pSource = apToClone;
        if (pSource == null)
            pSource = _pMainProperties;
        
        retObj = (Properties) pSource.clone();
        
        try {
            Enumeration<Object> propertiesKeys = pSource.keys();
            String stmp = null;
            while (propertiesKeys.hasMoreElements()) {
                stmp = (String) propertiesKeys.nextElement();
                retObj.setProperty(stmp, pSource.getProperty(stmp));
            }
        } catch (Exception e) {
            retObj = null;
        }
        
        return retObj;
    }
    
  private static File fixFile(String name) {
    File f = new File(name);
    return new File(_configHome, f.getName());
  }

  public static InputStream getResource(String name) {
    File f = fixFile(name);
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(f);
      return fis;
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public static void storeResource(String name, InputStream data) {
    
    File f = fixFile(name);
    File fBak = fixFile(name+".bak");
    if(fBak.exists()) fBak.delete();
    if(f.exists()) f.renameTo(fBak);
    try (FileOutputStream fout= new FileOutputStream(f);){
      byte [] b = new byte[4096];
      int r;

      while((r = data.read(b))>=0)
        fout.write(b, 0, r);

    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    } 

  }
  
	public static String loadIflowHome() 
	{
		String path = "";
		java.io.InputStream is = null;
		try {

			String filename = "path.properties";
			is = Setup.class.getClassLoader().getResourceAsStream(filename);

			Properties p = new Properties();
			p.load(is);
			path = p.getProperty("iflow.home");
			Logger.info("System", "Setup.java", "loadIflowHome()", "Path iFlow-home: " + path);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("", "Setup", "loadProperties", "Path: " + path, e);
		} finally {
			if (is != null)
				try { is.close(); } catch (IOException e) {}
		}

		return path;

	}
}
