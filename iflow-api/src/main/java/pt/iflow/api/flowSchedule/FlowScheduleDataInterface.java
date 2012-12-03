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
package pt.iflow.api.flowSchedule;

import java.sql.Timestamp;

public interface FlowScheduleDataInterface {
  public String getJobName();

  public void setJobName(String jobName);

  public Timestamp getStartTime();

  public void setStartTime(Timestamp startTime);

  public int getFlowId();

  public void setFlowId(int flowId);

  public String getFlowName();

  public void setFlowName(String flowName);

  public String getUserAssigned();

  public void setUserAssigned(String userAssigned);

  public String getUserAssignedProfile();

  public void setUserAssignedProfile(String userAssignedProfile);

  public String getExtra();

  public void setExtra(String extra);

  public boolean isSimpleEvent();

  public void setSimpleEvent(boolean isSimpleEvent);

  public Timestamp getNextFireDate();

  public void setNextFireDate(Timestamp nextFireDate);

  public String getNextFireDateJsp();

  public long getTimeBetweenFires();

  public void setTimeBetweenFires(long timeBetweenFires);

  public String getTimeBetweenFiresInHoursJsp();
  
  public String getFormatedTimeBetweenExecutions();

  public void setFormatedTimeBetweenExecutions(String formatedTimeBetweenExecutions);
}
