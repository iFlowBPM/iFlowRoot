package pt.iflow.api.utils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple bean to share flow data between iFlow and Editor
 * @author oscar
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FlowInfo implements Serializable {
  private static final long serialVersionUID = 104L;
  
  private String flowName;
  private boolean online;
  private int id;
  private String flowFile;
  private String created;
  private String modified;
  private int maxBlockId;
  
  public FlowInfo () {
  }

  public FlowInfo (int id, String name, String fileName, boolean online, String created, String modified) {
    this.flowName = name;
    this.online = online;
    this.id = id;
    this.flowFile = fileName;
    this.created = created;
    this.modified = modified;
  }

  public FlowInfo (int id, String name, String fileName, boolean online, String created, String modified, int maxBlockId) {
      this.flowName = name;
      this.online = online;
      this.id = id;
      this.flowFile = fileName;
      this.created = created;
      this.modified = modified;
      this.maxBlockId = maxBlockId;
    }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public int getId() {
    return id;
  }

  public void setId(int revision) {
    this.id = revision;
  }

  public String getFlowFile() {
    return flowFile;
  }

  public void setFlowFile(String flowFile) {
    this.flowFile = flowFile;
  }
  
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public void setMaxBlockId(int maxBlockId) {
    this.maxBlockId = maxBlockId;
  }

  public int getMaxBlockId() {
    return maxBlockId;
 }
}
