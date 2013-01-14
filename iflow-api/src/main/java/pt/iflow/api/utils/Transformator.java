package pt.iflow.api.utils;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class Transformator {

  /**
   * @param args
   */
  public static void main(String[] args) throws Throwable {
    // attach the XSLT processor to the CSVXMLReader
    XMLReader readr = new RandomXMLReader();
    InputSource is = new RandomStringInputSource();
    SAXSource ss = new SAXSource(readr, is);
    Result sr = new StreamResult(System.out);

    // create an instance of TransformerFactory
    TransformerFactory transFact = TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer();
    trans.transform(ss, sr);
  }

}
