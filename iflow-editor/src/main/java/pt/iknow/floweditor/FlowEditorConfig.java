package pt.iknow.floweditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import pt.iflow.api.core.Settings;
import pt.iknow.XmlColor.XmlColorLibrary;
import pt.iknow.XmlLetterType.XmlLetterType;
import pt.iknow.utils.StringUtilities;

public class FlowEditorConfig {

  transient public final static String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".floweditor"; //$NON-NLS-1$ //$NON-NLS-2$
  transient public final static String CONFIG_FILE = "config.xml"; //$NON-NLS-1$
  transient public final static String CONFIG_FILE_ENCODING = "UTF-8"; //$NON-NLS-1$
  transient public final static String LETTERS_FILE = "letters.xml"; //$NON-NLS-1$
  transient public final static String COLORS_FILE = "color.xml"; //$NON-NLS-1$

  transient public static final String LOCALE_USE_DEFAULT = "DEFAULT";
  transient public static final String LOCALE_USE_IFLOW = "IFLOW";
  transient public static final String LOCALE_USE_SELECTED = "SELECTED";

  transient public static final Locale[] AVAILABLE_LANGS = Settings.localeKeys;

  Vector<FlowRepUrl> listFlowURL = null;
  Integer defaultUrlIndex = new Integer(-1);

  boolean useProxy = false;
  String proxyHost = null;
  String proxyPort = null;
  boolean useProxyAuth = false;
  String proxyUser = null;
  String proxyPass = null;
  boolean useNTAuth = false;
  String proxyDomain = null;
  boolean confirmExit = true;

  // Lang stuff
  String useLocale = LOCALE_USE_IFLOW;
  String selectedLocale = null;


  public FlowEditorConfig() {
    super();
  }

  public FlowEditorConfig(Vector<FlowRepUrl> listFlowURL) {
    this();
    this.listFlowURL = listFlowURL;
  }

  public Vector<FlowRepUrl> getListFlowURL() {
    return listFlowURL;
  }

  public void setListFlowURL(Vector<FlowRepUrl> listFlowURL) {
    this.listFlowURL = listFlowURL;
  }

  public Integer getDefaultUrlIndex() {
    return defaultUrlIndex;
  }

  public void setDefaultUrlIndex(Integer defaultUrlIndex) {
    this.defaultUrlIndex = defaultUrlIndex;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public String getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getProxyPass() {
    return proxyPass;
  }

  public void setProxyPass(String proxyPass) {
    this.proxyPass = proxyPass;
  }

  public String getProxyDomain() {
    return proxyDomain;
  }

  public void setProxyDomain(String proxyDomain) {
    this.proxyDomain = proxyDomain;
  }

  public void saveConfig() {
    File dir = new File (FlowEditorConfig.CONFIG_DIR); 
    dir.mkdirs();
    File configFile = new File (dir, FlowEditorConfig.CONFIG_FILE);

    try {

      // if config file dows not exist create it and stuff it
      FileOutputStream fos = new FileOutputStream(configFile);

      OutputStreamWriter osw = new OutputStreamWriter(fos, FlowEditorConfig.CONFIG_FILE_ENCODING);
      Marshaller marshaller = new Marshaller(osw);
      marshaller.setEncoding(FlowEditorConfig.CONFIG_FILE_ENCODING);
      marshaller.marshal(this);
      osw.close();
      fos.close();
    }
    catch (Exception e) {
      FlowEditor.log("error", e);
    }
  }

  public static FlowEditorConfig loadConfig() {
    FlowEditorConfig feConfig = null;

    File dir = new File (FlowEditorConfig.CONFIG_DIR); 
    dir.mkdirs();
    File configFile = new File (dir, FlowEditorConfig.CONFIG_FILE);

    // if config file dows not exist create it and stuff it
    if (!configFile.exists()) {

      Vector<FlowRepUrl> flowRepList = new Vector<FlowRepUrl>();
      //flowRepList.add(new FlowRepUrl("http://localhost:8080/iFlow", ""));
      //flowRepList.add(new FlowRepUrl("http://localhost:8081/iFlow", ""));
      //flowRepList.add(new FlowRepUrl("http://www.iflow.pt/iFlow", ""));
      flowRepList.add(new FlowRepUrl("", ""));
      feConfig = new FlowEditorConfig(flowRepList);
      feConfig.saveConfig();
    }

    try {
      FileInputStream fis = new FileInputStream(configFile);
      InputSource isr = new InputSource(fis);  // Automatically retrieve encoding from XML header
      feConfig = (FlowEditorConfig)Unmarshaller.unmarshal(FlowEditorConfig.class,isr);
      fis.close();
    }
    catch (Exception e) {
      FlowEditor.log("error", e);
    }

    // some extra hacking (ie, defaults, etc)
    if(StringUtilities.isEmpty(feConfig.useLocale)) feConfig.useLocale = LOCALE_USE_IFLOW;

    return feConfig;
  }

  public boolean isUseProxy() {
    return useProxy;
  }

  public void setUseProxy(boolean useProxy) {
    this.useProxy = useProxy;
  }

  public boolean isUseProxyAuth() {
    return useProxyAuth;
  }

  public void setUseProxyAuth(boolean useProxyAuth) {
    this.useProxyAuth = useProxyAuth;
  }

  public boolean isUseNTAuth() {
    return useNTAuth;
  }

  public void setUseNTAuth(boolean useNTAuth) {
    this.useNTAuth = useNTAuth;
  }

  public static XmlLetterType getLetters() {
    XmlLetterType letters = null;

    InputStream is = null;
    FileOutputStream out = null;
    File theFile = null;

    try {
      File pwdLetter = new File(LETTERS_FILE);
      if(pwdLetter.exists()) {
        theFile = pwdLetter;
      } else {
        File configLetter = new File(CONFIG_DIR, LETTERS_FILE);
        theFile = configLetter;
        if(!configLetter.exists()) {
          configLetter.getParentFile().mkdirs();
          out = new FileOutputStream(configLetter);
          is  = FlowEditorConfig.class.getResourceAsStream("/"+LETTERS_FILE); 
          int r = -1;
          byte [] b = new byte[8092];

          while((r = is.read(b))!=-1) 
            out.write(b, 0, r);

          is.close();
          out.close();
          is = null;
          out = null;
        }
      }

      is = new FileInputStream(theFile);
      InputSource iSrc = new InputSource(is);
      letters = (XmlLetterType) Unmarshaller.unmarshal(XmlLetterType.class, iSrc);
      is.close();
      is = null;
    } catch(Exception e) {
      FlowEditor.log("error", e);
    } finally {
      if(null != is) {
        try {
          is.close();
        } catch (IOException e) {
          FlowEditor.log("error", e);
        }
      }
      if(null != out) {
        try {
          out.close();
        } catch (IOException e) {
          FlowEditor.log("error", e);
        }
      }
    }

    return letters;
  }


  public static XmlColorLibrary getColors() {
    XmlColorLibrary colors = null;

    InputStream is = null;
    FileOutputStream out = null;
    File theFile = null;

    try {
      File pwdLetter = new File(COLORS_FILE);
      if(pwdLetter.exists()) {
        theFile = pwdLetter;
      } else {
        File configLetter = new File(CONFIG_DIR, COLORS_FILE);
        theFile = configLetter;
        if(!configLetter.exists()) {
          configLetter.getParentFile().mkdirs();
          out = new FileOutputStream(configLetter);
          is  = FlowEditorConfig.class.getResourceAsStream("/"+COLORS_FILE); 
          int r = -1;
          byte [] b = new byte[8092];

          while((r = is.read(b))!=-1) 
            out.write(b, 0, r);

          is.close();
          out.close();
          is = null;
          out = null;
        }
      }

      is = new FileInputStream(theFile);
      InputSource iSrc = new InputSource(is);
      colors = (XmlColorLibrary) Unmarshaller.unmarshal(XmlColorLibrary.class, iSrc);
      is.close();
      is = null;
    } catch(Exception e) {
      FlowEditor.log("error", e);
    } finally {
      if(null != is) {
        try {
          is.close();
        } catch (IOException e) {
          FlowEditor.log("error", e);
        }
      }
      if(null != out) {
        try {
          out.close();
        } catch (IOException e) {
          FlowEditor.log("error", e);
        }
      }
    }

    return colors;
  }


  public static void saveColors(XmlColorLibrary colors) {
    if(null == colors) return;
    FileWriter out = null;
    File theFile = null;

    try {
      File pwdLetter = new File(COLORS_FILE);
      if(pwdLetter.exists()) {
        theFile = pwdLetter;
      } else {
        theFile = new File(CONFIG_DIR, COLORS_FILE);
        theFile.getParentFile().mkdirs();
      }

      out = new FileWriter(theFile);
      Marshaller m = new Marshaller(out);
      m.setEncoding("UTF-8");
      m.marshal(colors);
      out.close();
      out = null;
    } catch(Exception e) {
      FlowEditor.log("error", e);
    } finally {
      if(null != out) {
        try {
          out.close();
        } catch (IOException e) {
          FlowEditor.log("error", e);
        }
      }
    }
  }

  public String getUseLocale() {
    return useLocale;
  }

  public void setUseLocale(String useLocale) {
    this.useLocale = useLocale;
  }

  public String getSelectedLocale() {
    return selectedLocale;
  }

  public void setSelectedLocale(String locale) {
    this.selectedLocale = locale;
  }

  public boolean isConfirmExit() {
    return confirmExit;
  }

  public void setConfirmExit(boolean confirmExit) {
    this.confirmExit = confirmExit;
  }
}
