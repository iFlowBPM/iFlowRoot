package pt.iflow.api.transition;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FlowStateLogTOList 
{
	private List<FlowStateLogTO> elements;

	public List<FlowStateLogTO> getElements() {
		return elements;
	}

	public void setElements(List<FlowStateLogTO> elements) {
		this.elements = elements;
	}
	
	
	
}
