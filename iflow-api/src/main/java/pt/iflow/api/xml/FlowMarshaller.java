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
