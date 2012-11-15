package pt.iflow.api.services.types;

import java.util.HashMap;
//import java.util.LinkedHashMap;
//import org.apache.axis.utils.LinkedHashMap;

/*To solve:
 org.apache.axis.wsdl.fromJava.Types 
 The class org.apache.commons.collections15.map.LinkedMap 
 extends non-bean class org.apache.commons.collections15.map.AbstractLinkedMap.  
 An xml schema anyType will be used to define org.apache.commons.collections15.map.LinkedMap 
 in the wsdl file.*/

public class MailStartSettingsConvert {
	private String fromEmailVar;
	private String fromNameVar;
	private String subjectVar;
	private String sentDateVar;
	private String filesVar;

	private HashMap<String, String> customProps;

	public MailStartSettingsConvert() {
		super();
	}

	public String getFromEmailVar() {
		return fromEmailVar;
	}

	public void setFromEmailVar(String fromEmailVar) {
		this.fromEmailVar = fromEmailVar;
	}

	public String getFromNameVar() {
		return fromNameVar;
	}

	public void setFromNameVar(String fromNameVar) {
		this.fromNameVar = fromNameVar;
	}

	public String getSubjectVar() {
		return subjectVar;
	}

	public void setSubjectVar(String subjectVar) {
		this.subjectVar = subjectVar;
	}

	public String getSentDateVar() {
		return sentDateVar;
	}

	public void setSentDateVar(String sentDateVar) {
		this.sentDateVar = sentDateVar;
	}

	public String getFilesVar() {
		return filesVar;
	}

	public void setFilesVar(String filesVar) {
		this.filesVar = filesVar;
	}

	public HashMap<String, String> getCustomProps() {
		return customProps;
	}

	public void setCustomProps(HashMap<String, String> customProps) {
		this.customProps = customProps;
	}
}
