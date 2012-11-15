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
    
    private static final String IFLOW_HOME = System.getProperty("iflow.home");
    
    private static final String MAIN_PROP_FILE = "iflow.properties";
    private static final String AUTH_PROP_FILE = "authentication.properties";
    private static final String FEED_PROP_FILE = "feed.properties";
    private static final String DMS_PROP_FILE = "dms.properties";
    private static final String CERT_PROP_FILE = "certificates.properties";
    private static final String SIGN_PROP_FILE = "signatures.properties";
    
    // Hashtable with properties keys,values
    private static Properties _pMainProperties = new Properties();
    private static Properties _pExtraProperties = new Properties();
    
    private static String _configHome = "";
    
    static {
        loadProperties();
    }
    
    public static Properties readPropertiesFile(String fileName) {
        String sFile = FilenameUtils.concat(_configHome, fileName);
        Properties properties = new Properties();
        
        // Open properties file and get contents
        FileInputStream propertiesFile = null;
        try {
            propertiesFile = new FileInputStream(sFile);
            properties.load(propertiesFile);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.warning(null, "Setup", "readPropertiesFile",
                    "Error reading file " + sFile, e);
        } finally {
            try {
                if (null != propertiesFile)
                    propertiesFile.close();
            } catch (IOException e) {
            }
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
        
        FileInputStream propertiesFile = null;
        _configHome = ((IFLOW_HOME == null) ? "" : IFLOW_HOME);
        _configHome = FilenameUtils.concat(_configHome, "config");
        
        try {
            sFile = FilenameUtils.concat(_configHome, MAIN_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newMainProperties.load(propertiesFile);
        } catch (Exception e) {
            Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
            System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
        }
        
        try {
            sFile = FilenameUtils.concat(_configHome, AUTH_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file ("+ sFile + ").", e);
          System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
        }
        
        try {
            sFile = FilenameUtils.concat(_configHome, FEED_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file ("
                    + sFile + ").");
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
        }
        
        try {
            sFile = FilenameUtils.concat(_configHome, DMS_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
        }
        
        try {
            sFile = FilenameUtils.concat(_configHome, CERT_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
        }
        
        try {
            sFile = FilenameUtils.concat(_configHome, SIGN_PROP_FILE);
            // Open properties file and get contents
            propertiesFile = new FileInputStream(sFile);
            newExtraProperties.load(propertiesFile);
        } catch (Exception e) {
          Logger.error("", "Setup", "loadProperties", "Setup: unable to load properties file (" + sFile + ").", e);
          System.exit(1);
        } finally {
            if (null != propertiesFile) {
                try {
                    propertiesFile.close();
                } catch (IOException e) {
                }
            }
            propertiesFile = null;
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
        
        try {
            String sFile = stmp + MAIN_PROP_FILE;
            
            FileOutputStream propertiesFile = new FileOutputStream(sFile);
            
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
    try {
      return new FileInputStream(f);
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public static void storeResource(String name, InputStream data) {
    FileOutputStream fout = null;
    File f = fixFile(name);
    File fBak = fixFile(name+".bak");
    try {
      if(fBak.exists()) fBak.delete();
      if(f.exists()) f.renameTo(fBak);
      fout= new FileOutputStream(f);
      byte [] b = new byte[4096];
      int r;

      while((r = data.read(b))>=0)
        fout.write(b, 0, r);

    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    } finally {
      if(null != fout) {
        try {
          fout.close();
        } catch (IOException e) {
        }
      }
    }

  }
}
