package pt.iflow.api.presentation;

public class ApplicationItem {
  private int linkid;
  private String text;
  private String url;

  public ApplicationItem(int linkid, String text) {
    this(linkid, text, null);
  }
  
  public ApplicationItem(int linkid, String text, String url) {
    this.linkid = linkid;
    this.text = text;
    this.url = url;
  }

  public int getLinkid() {
    return linkid;
  }

  public String getText() {
    return text;
  }

  public String getUrl() {
    return url;
  }

}
