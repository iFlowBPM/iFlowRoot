/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
