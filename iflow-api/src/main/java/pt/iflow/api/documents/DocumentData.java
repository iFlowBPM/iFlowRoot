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
package pt.iflow.api.documents;

import java.util.Date;

import pt.iflow.connector.document.Document;

/**
 * <p>Title: Attribute</p>
 * <p>Description: This class represents a document, with setters and getters for docid, filename, datadoc and updated </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow </p>
 * @author Campa
 * @version 1.0
 */

public class DocumentData implements Document {
  
  private int docId;
  private String sFileName;
  private byte[] content;
  private Date updated;
  private int flowid;
  private int pid;
  private int subpid;
  private int length;

  public DocumentData() {
    this(-1, null, null, null, 0, 0, 0);
  }

  public DocumentData(int anDocId) {
    this(anDocId, null, null, null, 0, 0, 0);
  }

  public DocumentData(int anDocId, String asFileName) {
    this(anDocId, asFileName, null, null, 0, 0, 0);
  }

  public DocumentData(int anDocId, String asFileName, Date aUpdated) {
    this(anDocId, asFileName, null, aUpdated, 0, 0, 0);
  }

  public DocumentData(String asFileName, byte[] aContent) {
    this(-1, asFileName, aContent, null, 0, 0, 0);
  }

  public DocumentData(int aDocId, String asFileName,byte[] aContent,Date aUpdated) {
    this(aDocId, asFileName, aContent, aUpdated, 0, 0, 0);
  }

  public DocumentData(int aDocId, String asFileName,byte[] aContent,Date aUpdated, int flowid, int pid, int subpid) {
    this.docId  = aDocId;
    this.sFileName = asFileName;
    this.content = aContent;
    this.updated = aUpdated;
    this.flowid = flowid;
    this.pid = pid;
    this.subpid = subpid;
    this.length = aContent!=null?aContent.length:-1;
  }

  /**
   * Sets the attribute docId.
   * @param aDocId the attribute docId.
   */
  public void setDocId(int aDocId) {
    docId = aDocId;
  }

  /**
   * Gets the attribute docId.
   * @return the attribute docId.
   */
  public int getDocId() {
    return docId;
  }

  /**
   * Sets the attribute sFileName.
   * @param asFileName the attribute sFileName.
   */
  public void setFileName(String asFileName) {
    sFileName = asFileName;
  }

  /**
   * Gets the attribute sFileName.
   * @return the attribute sFileName.
   */
  public String getFileName() {
    return sFileName;
  }

  /**
   * Gets the attribute content.
   * @return the attribute content.
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Sets the attribute content.
   * @param content the attribute content.
   */
  public void setContent(byte[] content) {
    this.content = content;
  }

  /**
   * Sets the attribute content.
   * @param content the attribute content.
   */
  public void setContent(String content) {
    this.content = content.getBytes();
  }

  /**
   * Sets the attribute updated.
   * @param aUpdated the attribute updated.
   */
  public void setUpdated(Date aUpdated) {
    updated = aUpdated;
  }

  /**
   * Gets the attribute sfilename.
   * @return the attribute sfilename.
   */
  public Date getUpdated() {
    return updated;
  }

  public int getFlowid() {
    return flowid;
  }

  public void setFlowid(int flowid) {
    this.flowid = flowid;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public int getSubpid() {
    return subpid;
  }

  public void setSubpid(int subpid) {
    this.subpid = subpid;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int size) {
    this.length = size;
  }

}
