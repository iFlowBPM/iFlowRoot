package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.transition.FlowStateLogTOList;

public class ProcessStateLogMarshaller {

	public static byte[] marshall(List<FlowStateLogTO> flowStateLog) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(FlowStateLogTOList.class);
			Marshaller marshaller = context.createMarshaller();
			FlowStateLogTOList elements = new FlowStateLogTOList();
			elements.setElements( flowStateLog );
			marshaller.marshal(elements, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}

		return retObj.toByteArray();
	}

	public static List<FlowStateLogTO> unmarshal(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JAXBException(e);
		}
		
		JAXBContext context = JAXBContext.newInstance(FlowStateLogTOList.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return ((FlowStateLogTOList)unmarshaller.unmarshal(source)).getElements();

	}
}
