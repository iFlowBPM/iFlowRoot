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

import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateHistoryTOList;

public class ProcessStateHistoryMarshaller {

	public static byte[] marshall(List<FlowStateHistoryTO> flowStateHistory) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(FlowStateHistoryTOList.class);
			Marshaller marshaller = context.createMarshaller();
			FlowStateHistoryTOList elements = new FlowStateHistoryTOList();
			elements.setElements( flowStateHistory );
			marshaller.marshal(elements, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}

		return retObj.toByteArray();
	}

	public static List<FlowStateHistoryTO> unmarshal(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JAXBException(e);
		}
		
		JAXBContext context = JAXBContext.newInstance(FlowStateHistoryTOList.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return ((FlowStateHistoryTOList) unmarshaller.unmarshal(source)).getElements();
		
	}
}
