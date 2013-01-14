package pt.iflow.api.documents;

public class NullDocumentIdentifier extends DocumentIdentifier {

  public NullDocumentIdentifier() {
  }
  
  @Override
  public String getId() {
    return "";
  }
}
