package pt.iflow.connector.dms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class ContentResult {

  private String scheme;
  private String address;
  private String id;
  private String name;
  private String description;
  private String url;
  private String author;
  private String title;
  private String path;
  private Date createDate;
  private Date modifiedDate;
  private List<String> aspects;
  private List<ContentResult> children;
  private Properties comments;

  public ContentResult() {
    this.path = "";
    this.children = new ArrayList<ContentResult>();
    this.aspects = new ArrayList<String>();
    this.comments = new Properties();
  }

  public ContentResult(String id) {
    this();
    this.id = id;
  }

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<ContentResult> getChildren() {
    return children;
  }

  public void setChildren(List<ContentResult> children) {
    for (ContentResult child : children) {
      child.setPath(this.getPath() + "/" + child.getName());
    }
    this.children = children;
  }

  public void addChild(ContentResult child) {
    if (this.children == null) {
      this.children = new ArrayList<ContentResult>();
    }
    child.setPath(this.getPath() + "/" + child.getName());
    this.children.add(child);
  }

  public List<String> getAspects() {
    return aspects;
  }

  public void setAspects(List<String> aspects) {
    this.aspects = aspects;
  }

  public void addAspect(String aspect) {
    if (this.aspects == null) {
      this.aspects = new ArrayList<String>();
    }
    this.aspects.add(aspect);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void addToPath(String aPath) {
    if (StringUtils.isEmpty(this.path)) {
      this.path = "";
    }
    if (!this.path.endsWith("/") && !this.path.endsWith("\\") && !aPath.startsWith("/") && !aPath.startsWith("\\")) {
      this.path += "/";
    }
    this.path += (StringUtils.isBlank(aPath) ? "" : aPath);
  }

  public Properties getComments() {
    return comments;
  }

  public void setComments(Properties comments) {
    this.comments = comments;
  }

  public void addComment(Object key, Object value) {
    if (this.comments == null) {
      this.comments = new Properties();
    }
    value = (value == null) ? "" : value;
    this.comments.put(key, value);
  }

  public boolean isLeaf() {
    boolean retObj = false;
    if (StringUtils.isNotEmpty(this.url)) {
      retObj = true;
    }
    return retObj;
  }

  @Override
  public String toString() {
    return StringUtils.isEmpty(this.name) ? "/" : this.name;
  }
}