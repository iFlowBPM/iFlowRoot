package pt.iflow.api.transition;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FlowStateHistoryTOList 
{
	private List<FlowStateHistoryTO> elements;

	public List<FlowStateHistoryTO> getElements() {
		return elements;
	}

	public void setElements(List<FlowStateHistoryTO> elements) {
		this.elements = elements;
	}
	
	
	
}
