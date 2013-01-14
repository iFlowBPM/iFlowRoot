package pt.iflow.api.licensing;

import pt.iflow.api.utils.UserInfoInterface;

public interface LicenseService {

  void consume(UserInfoInterface userInfo, int fluxo, long custoDoBloco) throws LicenseServiceException;

  long getAvailable(UserInfoInterface userInfo);

  long getConsumed(UserInfoInterface userInfo, int fluxo);

  void load(UserInfoInterface userInfo, String licData);
  
  void load(UserInfoInterface userInfo, byte [] licData);

  
  String getLicenseType(UserInfoInterface userInfo);

  int getMaxFlows(UserInfoInterface userInfo);

  int getMaxBlocks(UserInfoInterface userInfo);

  int getMaxCPU(UserInfoInterface userInfo);

  String getSupportLevel(UserInfoInterface userInfo);
  
  boolean isLicenseOK();
  
  void canInstantiateFlows(UserInfoInterface userInfo, int flowCount) throws LicenseServiceException;
  
  void canInstantiateBlock(UserInfoInterface userInfo, String blockType) throws LicenseServiceException;
  
  void canInstantiateFlowBlocks(UserInfoInterface userInfo, int blockCount) throws LicenseServiceException;
  
  void instanceShutdown();

  void instanceStartup();
}
