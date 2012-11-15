package pt.iflow.api.documents;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;


public abstract class DocumentIdentifier {

  public static DocumentIdentifier getInstance(int docid) {
    return new IFlowDocumentIdentifier(docid);
  }
  
  public static DocumentIdentifier getInstance(String docid) {
    if (StringUtils.isEmpty(docid)) {
      return new NullDocumentIdentifier();
    }
    try {
      return new IFlowDocumentIdentifier(Integer.parseInt(docid));
    } catch (NumberFormatException nfe) {
      return new DMSDocumentIdentifier(UUID.fromString(docid));
    }
  }
  
  public abstract String getId();

  @Override
  public String toString() {
    return getId();
  }

  DocumentIdentifier() {}
}
