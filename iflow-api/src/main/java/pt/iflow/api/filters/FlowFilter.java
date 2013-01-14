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