package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

import pt.iflow.api.xml.codegen.library.XmlLibrary;

public class LibraryMarshaller {

	public static byte[] marshall(XmlLibrary library) throws JAXBException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(bout, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(XmlLibrary.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(library, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}

		return bout.toByteArray();
	}

	public static XmlLibrary unmarshal(byte[] data) throws JAXBException {
		return unmarshal(new ByteArrayInputStream(data));
	}

	public static XmlLibrary unmarshal(InputStream inStream) throws JAXBException 
	{
		InputSource source = null;
		source = new InputSource(inStream); // guess encoding from file		
		JAXBContext context = JAXBContext.newInstance(XmlLibrary.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (XmlLibrary) unmarshaller.unmarshal(source);	
	}

}
