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
/*
 *
 * Created on Apr 28, 2006 by mach
 *
 */

package pt.iflow.api.notification;

import java.util.Properties;

public abstract  class SMSGateway {
  private SMSData smsData = new SMSData();;

  public SMSData getSmsData() {
    return smsData;
  }

  public void setSmsData(SMSData smsData) {
    this.smsData = smsData;
  }

  public  abstract boolean init(Properties initParams);

  /**
   *
   * @param applicationUser 
   * @return
   */
  public abstract boolean send(String applicationUser);

}
