/**
 * 
 */
package pt.iflow.api.licensing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.security.LibraryLoader;
import pt.iknow.utils.security.LicenseProperties;
import pt.iknow.utils.security.SecurityWrapper;

class FileBasedLicenseService implements LicenseService {

  // sync every minute or so
  private static final long SYNC_DELAY = 1000*60;
  
  private final LicenseProperties properties = SecurityWrapper.getLicenseProperties();

  private final boolean isVoucherBased = StringUtils.equals("1", properties.getValue(SecurityWrapper.PROP_VB));

  private Map<String, LicenseEntry> licenseEntries = new HashMap<String, LicenseEntry>();
  
  private int instanceCount = 0;

  // daemon timer
  private Timer syncTimer = new Timer(true);
  private TimerTask syncTask = new TimerTask() {
    public void run() {
      try {
		saveState();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  };
  
  FileBasedLicenseService() {
    loadState();
    syncTimer.schedule(syncTask, SYNC_DELAY, SYNC_DELAY);
  }
  
  public void instanceStartup() {
    instanceCount++;
  }
  
  public void instanceShutdown() {
    if(--instanceCount == 0) {
      syncTimer.cancel();
      try {
		saveState();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  }
  
  public String getLicenseType(UserInfoInterface userInfo) {
    return properties.getValue(SecurityWrapper.PROP_DESCRIPTION);
  }

  public long getConsumed(UserInfoInterface userInfo, int flowid) {
    LicenseEntry entry = licenseEntries.get(userInfo.getOrganization());
    if (null == entry)
      return 0;
    if (flowid > 0) {
      Long cnt = entry.flowBased.get(flowid);
      if (null == cnt)
        return 0;
      return cnt;
    }

    return entry.consumed;
  }

  public synchronized void consume(UserInfoInterface userInfo, int flowid, long blockCost) throws LicenseServiceException {
    LicenseEntry entry = licenseEntries.get(userInfo.getOrganization());
    if (null == entry) {
      entry = new LicenseEntry();
      entry.available = 0;
      entry.consumed = 0;
      licenseEntries.put(userInfo.getOrganization(), entry);
    }
    if (isVoucherBased) {
      if (entry.available <= 0) {
        entry.available = 0;
        throw new LicenseServiceException("No more block units available");
      } else {
        entry.available -= blockCost;
        if (entry.available < 0)
          entry.available = 0;
      }
    }

    // increment stats
    entry.consumed += blockCost;
    if (flowid > 0) {
      long flowConsumed = 0L;
      if (entry.flowBased.containsKey(flowid))
        flowConsumed = entry.flowBased.get(flowid);
      flowConsumed += blockCost;
      entry.flowBased.put(flowid, flowConsumed);
    }
  }

  public long getAvailable(UserInfoInterface userInfo) {
    if (!isVoucherBased)
      return -1L;
    LicenseEntry entry = licenseEntries.get(userInfo.getOrganization());
    if (null == entry)
      return 0;
    return entry.available;
  }

  public synchronized void load(UserInfoInterface userInfo, String strMagica) {
    if (isVoucherBased) {
      LicenseEntry entry = licenseEntries.get(userInfo.getOrganization());
      if (entry == null) {
        entry = new LicenseEntry();
        licenseEntries.put(userInfo.getOrganization(), entry);
      }

      try {
        entry.available += Long.parseLong(strMagica);
      } catch (Exception e) {
      }
    } else {
      load(userInfo, Base64.decode(strMagica));
    }
  }

  public synchronized void load(UserInfoInterface userInfo, byte[] licData) {
    if (isVoucherBased) {
      load(userInfo, new String(licData));
      return;
    }
    if (licData != null) {
      Logger.warning(userInfo.getUtilizador(), this, "service", "Setting new license file...");
      SecurityWrapper.setLicenseData(licData);
    }

    Logger.warning(userInfo.getUtilizador(), this, "service", "Resetting license...");
    SecurityWrapper.resetLicense();
    int status = SecurityWrapper.getLicenseStatus();
    Logger.warning(userInfo.getUtilizador(), this, "service", "License reset. New status is: " + status);
  }

  public int getMaxFlows(UserInfoInterface userInfo) {
    int max = -1;

    try {
      max = Integer.parseInt(properties.getValue(SecurityWrapper.PROP_MAX_FLOWS));
    } catch (Exception e) {
    }

    return max;
  }

  public int getMaxBlocks(UserInfoInterface userInfo) {
    int max = -1;

    try {
      max = Integer.parseInt(properties.getValue(SecurityWrapper.PROP_MAX_BLOCKS));
    } catch (Exception e) {
    }

    return max;
  }

  public int getMaxCPU(UserInfoInterface userInfo) {
    int max = -1;

    try {
      max = Integer.parseInt(properties.getValue(SecurityWrapper.PROP_MAX_CPU));
    } catch (Exception e) {
    }

    return max;
  }

  public String getSupportLevel(UserInfoInterface userInfo) {
    return properties.getValue(SecurityWrapper.PROP_SUPPORT);
  }

  public boolean isLicenseOK() {
    return (LibraryLoader.isLoaded() && SecurityWrapper.getLicenseStatus() == SecurityWrapper.LIC_LOADED);
  }

  public void canInstantiateBlock(UserInfoInterface userInfo, String blockType) throws LicenseServiceException {
    if (SecurityWrapper.contains(SecurityWrapper.PROP_FORBIDDEN_BLOCKS, blockType)) {
      SecurityWrapper.profilaticReset();
      if (SecurityWrapper.contains(SecurityWrapper.PROP_FORBIDDEN_BLOCKS, blockType)) {
        throw new LicenseServiceException("Block forbidden: " + blockType);
      }
    }
  }

  public void canInstantiateFlowBlocks(UserInfoInterface userInfo, int blockCount) throws LicenseServiceException {
    if (!SecurityWrapper.great(SecurityWrapper.PROP_MAX_BLOCKS, blockCount)) {
      SecurityWrapper.profilaticReset();
      if (!SecurityWrapper.great(SecurityWrapper.PROP_MAX_BLOCKS, blockCount))
        throw new LicenseServiceException("Maximum blocks per flow reached");
    }
  }

  public void canInstantiateFlows(UserInfoInterface userInfo, int flowCount) throws LicenseServiceException {
    if (!SecurityWrapper.great(SecurityWrapper.PROP_MAX_FLOWS, flowCount)) {
      SecurityWrapper.profilaticReset();
      if (!SecurityWrapper.great(SecurityWrapper.PROP_MAX_FLOWS, flowCount))
        throw new LicenseServiceException("Maximum flows reached");
    }
  }
  

  // 256 "random" bit key
  private static byte[] KEY_256_BIT = new byte[] { 0x00, 0x51, 0x32, 0x03, 0x4f, 0x75, 0x06, 0x07, 0x08, 0x09,
    0x0a, 0x3b, 0x06, 0x45, 0x32, 0x3f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x14, 0x15, 0x16, 0x17, 0x14, 0x15, 0x16, 0x17 };

  // key size in bits
  private static final int KEY_SIZE = 128;
  // key size in bytes
  private static final int KEY_LENGTH = KEY_SIZE/8;
  
  private static byte [] encryptSnapShot(byte [] data) throws GeneralSecurityException {
    SecretKeySpec key = new SecretKeySpec(KEY_256_BIT, 0, KEY_LENGTH, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  private static byte [] decryptSnapShot(byte [] data) throws GeneralSecurityException {
    SecretKeySpec key = new SecretKeySpec(KEY_256_BIT, 0, KEY_LENGTH, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(data);
  }

  private void saveState() throws IOException {
    String xml = createXMLSnapshot();
    File licenseState = new File(Const.sIFLOW_HOME, "licenseState.dat");
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(licenseState);
      
      if(fout != null){
    	  fout.write(encryptSnapShot(xml.getBytes("UTF-8")));
    	  
      }     
      
    } catch(Exception e) {
      Logger.error(null, this, "saveState", "Error saving license state.", e);
    }
    finally{
    	fout.close();
    }
  }
  
  private synchronized void loadState() {
    
    FileInputStream fin = null;
    try {
      File licenseState = new File(Const.sIFLOW_HOME, "licenseState.dat");
      int size = (int)licenseState.length();
      if(size == 0) return;
      byte [] licData = new byte[size];
      fin = new FileInputStream(licenseState);
      fin.read(licData);
      licData = decryptSnapShot(licData);
      parseXMLSnapshot(licData);
    } catch(FileNotFoundException e) {
      Logger.error(null, this, "loadState", "License state does not exist. Ignoring...");
    } catch(Exception e) {
      Logger.error(null, this, "loadState", "Error loading license state.", e);
    } finally {
      if(null != fin) {
        try {
          fin.close();
        } catch (IOException e) {
        }
      }
    }
  }
  
  private synchronized String createXMLSnapshot() {
    StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    sb.append("<licenseUsage>");
    for (Map.Entry<String, LicenseEntry> licenseEntry : licenseEntries.entrySet()) {
      LicenseEntry entry = licenseEntry.getValue();
      // save org stats
      sb.append("<org id=\"").append(StringEscapeUtils.escapeXml(licenseEntry.getKey())).append("\" ");
      sb.append("available=\"").append(entry.available).append("\" ");
      sb.append("consumed=\"").append(entry.consumed).append("\">");
      // append per flow stats
      for (Map.Entry<Integer, Long> flowEntry : entry.flowBased.entrySet())
        sb.append("<flow id=\"").append(flowEntry.getKey()).append("\" consumed=\"").append(flowEntry.getValue()).append("\"/>");
      sb.append("</org>");
    }
    sb.append("</licenseUsage>");
    return sb.toString();
  }

  // TODO parser...
  private void parseXMLSnapshot(byte [] xml) throws SAXException, IOException, ParserConfigurationException {
    DefaultHandler handler = new DefaultHandler() {
      LicenseEntry currOrg;
      public void startDocument() throws SAXException {
        licenseEntries.clear();
      }
      
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("org".equals(qName)) {
          currOrg = new LicenseEntry();
          currOrg.available = Long.parseLong(attributes.getValue("available"));
          currOrg.consumed = Long.parseLong(attributes.getValue("consumed"));
          licenseEntries.put(attributes.getValue("id"), currOrg);
        } else if("flow".equals(qName)) {
          currOrg.flowBased.put(new Integer(attributes.getValue("id")), new Long(attributes.getValue("consumed")));
        }
      }
      
    };
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(false);
    factory.setValidating(false);
    SAXParser parser = factory.newSAXParser();
    parser.parse(new ByteArrayInputStream(xml), handler);
    
  }
  
  private static class LicenseEntry implements Serializable, Cloneable {
    private static final long serialVersionUID = 7930502407230862956L;

    private long available;
    private long consumed;
    private Map<Integer, Long> flowBased = new HashMap<Integer, Long>();
  }

}