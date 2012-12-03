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
package pt.iflow.api.filters;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class FlowFilter {
  private Date dateAfter;
  private Date dateBefore;
  private String pnumber;
  private int startIndex;
  private int numElements;
  private FlowIgnorable ignoreFlow;
  private boolean isIntervenient = false;
  private String orderBy = "";
  private String orderType = "asc";
  private String folderid = "0";
  private String labelid = "0";
  private String deadline = "0";
  
  public boolean isValid() {
    return StringUtils.isNotEmpty(pnumber) || dateAfter != null || dateBefore != null || hasSizeLimit();
  }

  public boolean hasSizeLimit() {
    return startIndex > -1 && numElements > -1;
  }

  public FlowFilter() {
    this(null, null, null, -1, -1);
  }

  public FlowFilter(String pnumber, Date dateAfter, Date dateBefore, int startIndex, int numElements) {
    this.dateAfter = dateAfter;
    this.dateBefore = dateBefore;
    this.pnumber = pnumber;
    this.startIndex = startIndex;
    this.numElements = numElements;
  }

  public FlowFilter(String pnumber, Date dateAfter, Date dateBefore, int startIndex, int numElements, 
      boolean isIntervenient, String orderBy, String orderType) {
    this.dateAfter = dateAfter;
    this.dateBefore = dateBefore;
    this.pnumber = pnumber;
    this.startIndex = startIndex;
    this.numElements = numElements;
    this.isIntervenient = isIntervenient;
    this.orderBy = orderBy;
    this.orderType = orderType;
  }

  public Date getDateAfter() {
    return dateAfter;
  }

  public void setDateAfter(Date dateAfter) {
    this.dateAfter = dateAfter;
  }

  public Date getDateBefore() {
    return dateBefore;
  }

  public void setDateBefore(Date dateBefore) {
    this.dateBefore = dateBefore;
  }

  public String getPnumber() {
    return pnumber;
  }

  public void setPnumber(String pnumber) {
    this.pnumber = pnumber;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getNumElements() {
    return numElements;
  }

  public void setNumElements(int numElements) {
    this.numElements = numElements;
  }
  
  public FlowIgnorable getIgnoreFlow() {
    return ignoreFlow;
  }

  public void setIgnoreFlow(FlowIgnorable ignoreFlow) {
    this.ignoreFlow = ignoreFlow;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
  
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }
  
  public void setFolderid(String folderid) {
	    this.folderid = folderid;
	  }
  
  public String getFolderid() {
	    return folderid;
	  }

  public void setLabelid(String labelid) {
    this.labelid = labelid;
  }

  public String getLabelid() {
    return labelid;
  }
  
  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  public String getDeadline() {
    return deadline;
  }
  
  public boolean isIntervenient() {
    return isIntervenient;
  }

  public void setIsIntervenient(boolean isIntervenient) {
    this.isIntervenient = isIntervenient;
  }
  
  public boolean ignoreFlow(int flowid) {
    return this.getIgnoreFlow() == null ? false : this.getIgnoreFlow().ignoreFlow(flowid);
  }
}
