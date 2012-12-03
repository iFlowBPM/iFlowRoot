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

import java.util.Calendar;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.security.LicenseProperties;
import pt.iknow.utils.security.SecurityWrapper;

class NoLicenseService implements LicenseService {

  private static Calendar dtLimit;
  
  static {
    dtLimit = Calendar.getInstance();
    dtLimit.set(2014, Calendar.SEPTEMBER, 27);
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
