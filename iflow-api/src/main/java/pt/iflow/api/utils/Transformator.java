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
