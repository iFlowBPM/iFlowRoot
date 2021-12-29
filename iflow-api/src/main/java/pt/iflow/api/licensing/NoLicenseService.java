package pt.iflow.api.licensing;

import java.util.Calendar;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.security.LicenseProperties;
import pt.iknow.utils.security.SecurityWrapper;

class NoLicenseService implements LicenseService {

  private static Calendar dtLimit;
  
  static {
    dtLimit = Calendar.getInstance();
    dtLimit.set(2030, Calendar.SEPTEMBER, 27);
  }
  
  NoLicenseService() {
  }
  
  public void instanceStartup() {
  }
  
  public void instanceShutdown() {
  }
  
  public String getLicenseType(UserInfoInterface userInfo) {
    return "None";
  }

  public long getConsumed(UserInfoInterface userInfo, int flowid) {
    return 0l;
  }

  public synchronized void consume(UserInfoInterface userInfo, int flowid, long blockCost) throws LicenseServiceException {
  }

  public long getAvailable(UserInfoInterface userInfo) {
    return -1L;
  }

  public synchronized void load(UserInfoInterface userInfo, String strMagica) {
  }

  public synchronized void load(UserInfoInterface userInfo, byte[] licData) {
  }

  public int getMaxFlows(UserInfoInterface userInfo) {
    return -1;
  }

  public int getMaxBlocks(UserInfoInterface userInfo) {
    return -1;
  }

  public int getMaxCPU(UserInfoInterface userInfo) {
    return -1;
  }

  public String getSupportLevel(UserInfoInterface userInfo) {
    return "None";
  }

  public boolean isLicenseOK() {
    Calendar dt = Calendar.getInstance();
    return (dt.before(dtLimit));
  }

  public void canInstantiateBlock(UserInfoInterface userInfo, String blockType) throws LicenseServiceException {
  }

  public void canInstantiateFlowBlocks(UserInfoInterface userInfo, int blockCount) throws LicenseServiceException {
  }

  public void canInstantiateFlows(UserInfoInterface userInfo, int flowCount) throws LicenseServiceException {
  }
  
}