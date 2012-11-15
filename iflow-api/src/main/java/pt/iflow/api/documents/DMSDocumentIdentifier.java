package pt.iflow.api.documents;

import java.util.UUID;

public class DMSDocumentIdentifier extends DocumentIdentifier {

  private UUID docid;
  
  public DMSDocumentIdentifier(UUID docid) {
    this.docid = docid;
  }
  
  @Override
  public String getId() {
    return docid.toString();
  }

  public UUID getUUID() {
    return docid;
  }
}
