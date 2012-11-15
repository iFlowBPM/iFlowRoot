package pt.iflow.api.blocks.form;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.iflow.api.utils.AbstractXMLReader;

public class FormDebugXMLReader extends AbstractXMLReader {

  private final FormXMLReader formReader;
  private Result result;
  
  public FormDebugXMLReader(FormXMLReader formReader, Result result) {
    this.formReader = formReader;
    this.result = result;
  }
  
  public void setResult(Result result) {
    this.result = result;
  }

  public Result getResult() {
    return result;
  }

  @Override
  public void parse(InputSource input) throws IOException, SAXException {

    formReader.setDebugContentHandler(getContentHandler());
    try {
      // chain througt auxiliary identity transformer
      Transformer trf = TransformerFactory.newInstance().newTransformer();
      trf.transform(new SAXSource(formReader, input), getResult());
    } catch (Exception e) {
      throw new SAXException(e);
    }
  
  }

}
