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
package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import pt.iflow.api.xml.codegen.library.XmlLibrary;

public class LibraryMarshaller {
  
  public static byte[] marshall(XmlLibrary library) throws MarshalException, ValidationException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      Writer w = new OutputStreamWriter(bout, "UTF-8");
      Marshaller.marshal(library, w);
      w.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return bout.toByteArray();
  }
  
  public static XmlLibrary unmarshal(byte [] data)
  throws MarshalException, ValidationException
  {
    return unmarshal(new ByteArrayInputStream(data));
  }
  
  public static XmlLibrary unmarshal(InputStream inStream)
  throws MarshalException, ValidationException
  {
    InputSource source = null;
    source = new InputSource(inStream); // guess encoding from file
    return (XmlLibrary) Unmarshaller.unmarshal(XmlLibrary.class, source);
  }
  

}
