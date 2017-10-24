package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

import pt.iflow.api.xml.codegen.flow.XmlAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

public class FlowMarshaller {

	public static byte[] marshall(XmlFlow flow) throws JAXBException {
		flow = validate(flow);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(bout, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(XmlFlow.class);
			javax.xml.bind.Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(flow, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}

		return bout.toByteArray();
	}

	public static XmlFlow unmarshal(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JAXBException(e);
		}
		
		JAXBContext context = JAXBContext.newInstance(XmlFlow.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		XmlFlow retFlow = (XmlFlow) unmarshaller.unmarshal(source);
		return validate(retFlow);
	}

	private static XmlFlow validate(XmlFlow xmlFlow) {
		if (null == xmlFlow.getXmlCatalogVars())
			xmlFlow.setXmlCatalogVars(new XmlCatalogVarsType());
		if (xmlFlow.getXmlCatalogVars().getXmlAttribute().size() > 0) 
		{
			XmlCatalogVarsType oldCatalogVars = xmlFlow.getXmlCatalogVars();
			for (XmlAttributeType oldAttribute : oldCatalogVars.getXmlAttribute()) {
				XmlCatalogVarAttributeType newAttribute = new XmlCatalogVarAttributeType();

				newAttribute.setDataType(oldAttribute.getDescription());
				newAttribute.setInitVal(oldAttribute.getValue());
				newAttribute.setName(oldAttribute.getName());

				newAttribute.setPublicName("");
				newAttribute.setIsSearchable(false);

				xmlFlow.getXmlCatalogVars().withXmlCatalogVarAttribute( newAttribute );
			}
			xmlFlow.getXmlCatalogVars().getXmlCatalogVarAttribute().clear();
		}

		return xmlFlow;
	}

}
