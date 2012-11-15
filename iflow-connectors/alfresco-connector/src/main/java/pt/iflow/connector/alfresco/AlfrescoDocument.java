package pt.iflow.connector.alfresco;

import java.io.Serializable;

import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import pt.iflow.connector.document.DMSDocument;

public class AlfrescoDocument implements DMSDocument, Serializable {
  private static final long serialVersionUID = 3697595768424561587L;

  Reference reference;
  NamedValue[] comments;

  private int docId;
  private String fileName;
  private byte[] content;

  /**
   * Empty DMSDocument. This document carries no information, so make sure to fill it out accordingly.
   */
  public AlfrescoDocument() {
    this(null);
  }

  /**
   * Minimal DMSDocument consistency info, use {@link #AlfrescoDocument(String, String, String, String, String, OrderedMap)}
   * instead.
   * 
   * @param content
   *          Document content.
   */
  public AlfrescoDocument(byte[] content) {
    this.content = (content == null ? new byte[] {} : content);
    this.fileName = null;
    this.comments = new NamedValue[] {};
    this.reference = new Reference();
  }

  /**
   * Prepares the document with Store and Reference information, but without any content.
   * 
   * @param scheme
   *          Document's store scheme.
   * @param address
   *          Document's store address.
   * @param uuid
   *          Document's reference id.
   * @param path
   *          Document's reference path.
   */
  public AlfrescoDocument(String scheme, String address, String uuid, String path) {
    this();
    this.reference = new Reference(new Store(scheme, address), uuid, path);
  }

  /**
   * Main c'tor.
   * 
   * @param content
   *          Document content.
   * @param scheme
   *          Document's store scheme.
   * @param address
   *          Document's store address.
   * @param uuid
   *          Document's reference id.
   * @param path
   *          Document's reference path.
   * @param comments
   */
  public AlfrescoDocument(String content, String scheme, String address, String uuid, String path,
      OrderedMap<String, String> comments) {
    this(content.getBytes());
    this.reference = new Reference(new Store(scheme, address), uuid, path);
    for (String key : comments.keySet()) {
      addComment(key, comments.get(key));
    }
  }

  public OrderedMap<String, String> getComments() {
    OrderedMap<String, String> properties = new ListOrderedMap<String, String>();
    for (NamedValue comment : comments) {
      properties.put(comment.getName(), comment.getValue());
    }
    return properties;
  }

  public void addComments(OrderedMap<String, String> comments) {
    for (String key : comments.keySet()) {
      addComment(key, comments.get(key));
    }
  }

  public void addComment(String name, String value) {
    OrderedMap<String, String> properties = new ListOrderedMap<String, String>();
    if (name != null) {
      for (NamedValue comment : comments) {
        properties.put(comment.getName(), comment.getValue());
      }
      properties.put(name, value);
      comments = new NamedValue[properties.size()];
      int index = 0;
      for (String key : properties.keySet()) {
        if (index < comments.length) {
          comments[index] = Utils.createNamedValue(key, properties.get(key));
          index++;
        } else {
          break;
        }
      }
    }
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public void setContent(String content) {
    setContent(content.getBytes());
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public int getDocId() {
    return docId;
  }

  public void setDocId(int docId) {
    this.docId = docId;
  }

  public String getAddress() {
    return (this.reference == null || this.reference.getStore() == null) ? null : this.reference.getStore().getAddress();
  }

  public void setAddress(String address) {
    if (this.reference != null && this.reference.getStore() != null) {
      this.reference.getStore().setAddress(address);
    }
  }

  public String getScheme() {
    return (this.reference == null || this.reference.getStore() == null) ? null : this.reference.getStore().getScheme();
  }

  public void setScheme(String Scheme) {
    if (this.reference != null && this.reference.getStore() != null) {
      this.reference.getStore().setScheme(Scheme);
    }
  }

  public String getUuid() {
    return this.reference == null ? null : this.reference.getUuid();
  }

  public void setUuid(String uuid) {
    if (this.reference != null && this.reference.getStore() != null) {
      this.reference.setUuid(uuid);
    }
  }

  public String getPath() {
    return this.reference == null ? null : this.reference.getPath();
  }

  public void setPath(String path) {
    if (this.reference != null && this.reference.getStore() != null) {
      this.reference.setPath(path);
    }
  }
}
