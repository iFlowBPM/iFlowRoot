/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
