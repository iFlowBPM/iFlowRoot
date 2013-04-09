package pt.iflow.search;

public class PesquisaProcessoRow {

  private String [] values;
  private String link;
  
  public PesquisaProcessoRow(String link, String [] values) {
    this.values = values;
    this.link = link;
  }
  
  public String [] getValues() {
    return values;
  }
  
  public String getLink() {
    return link;
  }
  
}
