/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
