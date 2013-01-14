package pt.iflow.connector.document;

public interface Document {

  public int getDocId();

  public void setDocId(int aDocId);

  public String getFileName();

  public void setFileName(String fileName);

  byte[] getContent();

  void setContent(byte[] content);

  public void setContent(String content);

  public void setSerials(String serials);

  public String getSerials();
}
