package pt.iflow.api.processtype;

import java.text.DecimalFormat;

import pt.iflow.connector.document.Document;

public class DocumentDataType extends IntegerDataType implements ProcessDataType {
  public DocumentDataType() {
    super(new DecimalFormat("#0"),new DecimalFormat("#0"));
  }
  
  @Override
	public String toString() {
		return "Document";
	}
	
  @Override
	public Class<?> getSupportingClass() {
//      return pt.iflow.api.documents.DocumentData.class;  
      return Document.class;
	}
}
