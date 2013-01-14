package pt.iknow.floweditor;

public enum WebFileType {
  HTML ("text/html"),
  XML ("text/xml"),
  JAVASCRIPT ("text/javascript"),
  JSON ("application/json");

  private final String mimeType;
  WebFileType(String mimeType){
    this.mimeType = mimeType;
  }
  public String getMimeType() {
    return this.mimeType;
  }
}
