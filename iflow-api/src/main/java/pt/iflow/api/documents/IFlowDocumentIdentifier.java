package pt.iflow.api.documents;

public class IFlowDocumentIdentifier extends DocumentIdentifier {

  private int docid;
  
  public IFlowDocumentIdentifier(int docid) {
    this.docid = docid;
  }
  
  @Override
  public String getId() {
    return String.valueOf(docid);
  }

  public int getIntId() {
    return docid;
  }
}
