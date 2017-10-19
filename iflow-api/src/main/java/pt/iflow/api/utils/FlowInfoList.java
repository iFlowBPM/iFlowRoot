package pt.iflow.api.utils;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FlowInfoList 
{
	private List<FlowInfo> elements;
	
	public List<FlowInfo> getElements() {
		return elements;
	}

	public void setElements(List<FlowInfo> elements) {
		this.elements = elements;
	}
	
	
	
}
