package pt.iflow.api.xml.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "connector-transfer-object")
public class ConnectorTransferObject {

	private List<String> description;
	private List<String> clazz;

	public ConnectorTransferObject() {
	}

	public ConnectorTransferObject(List<String> description, List<String> clazz) {
		super();
		this.description = description;
		this.clazz = clazz;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public List<String> getClazz() {
		return clazz;
	}

	public void setClazz(List<String> clazz) {
		this.clazz = clazz;
	}
}