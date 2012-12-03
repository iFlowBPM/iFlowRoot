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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

public class FlowMarshaller {

  public static byte [] marshall(XmlFlow flow) throws MarshalException, ValidationException {
    flow = validate(flow);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      Writer w = new OutputStreamWriter(bout, "UTF-8");
      Marshaller.marshal(flow, w);
      w.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return bout.toByteArray();
  }

  public static XmlFlow unmarshal(byte [] data)
  throws MarshalException, ValidationException
  {
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    XmlFlow retFlow = (XmlFlow) Unmarshaller.unmarshal(XmlFlow.class, source);

    return validate(retFlow);
  }

  private static XmlFlow validate(XmlFlow xmlFlow) {
    if(null == xmlFlow.getXmlCatalogVars()) xmlFlow.setXmlCatalogVars(new XmlCatalogVars());
    if(xmlFlow.getXmlCatalogVars().getXmlAttributeCount() > 0) {
      XmlCatalogVars oldCatalogVars = xmlFlow.getXmlCatalogVars();
      for (XmlAttribute oldAttribute : oldCatalogVars.getXmlAttribute()) {
        XmlCatalogVarAttribute newAttribute = new XmlCatalogVarAttribute();

        newAttribute.setDataType(oldAttribute.getDescription());
        newAttribute.setInitVal(oldAttribute.getValue());
        newAttribute.setName(oldAttribute.getName());

        newAttribute.setPublicName("");
        newAttribute.setIsSearchable(false);

        xmlFlow.getXmlCatalogVars().addXmlCatalogVarAttribute(newAttribute);
      }
      xmlFlow.getXmlCatalogVars().clearXmlAttribute();
    }

    for (XmlCatalogVarAttribute varType : xmlFlow.getXmlCatalogVars().getXmlCatalogVarAttribute()) {
      try {
        varType.validate();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return xmlFlow;
  }




}
