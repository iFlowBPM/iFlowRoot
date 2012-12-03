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

import java.util.Map;
import java.util.Set;
import pt.iflow.api.processtype.ProcessDataType;

/*To solve:
org.apache.axis.wsdl.fromJava.Types 
The class pt.iflow.api.core.ProcessCatalogue does not contain a default constructor, 
which is a requirement for a bean class.  The class cannot be converted into an xml schema type.  
An xml schema anyType will be used to define this class in the wsdl file.*/

public class ProcessCatalogueConvert {
	private Map<String, ProcessDataType> _map;
	private Set<String> _lists;
	private Set<String> _bindables;
	private Map<String, String> _publicNames;
	private Map<String, String> _defaultValueExpressions;
	private Map<String, Integer> _searchables;
	private int indexPosition = 0;
	
	public ProcessCatalogueConvert() {
		super();
	}
	public Map<String, ProcessDataType> get_map() {
		return _map;
	}
	public void set_map(Map<String, ProcessDataType> map) {
		_map = map;
	}
	public Set<String> get_lists() {
		return _lists;
	}
	public void set_lists(Set<String> lists) {
		_lists = lists;
	}
	public Set<String> get_bindables() {
		return _bindables;
	}
	public void set_bindables(Set<String> bindables) {
		_bindables = bindables;
	}
	public Map<String, String> get_publicNames() {
		return _publicNames;
	}
	public void set_publicNames(Map<String, String> publicNames) {
		_publicNames = publicNames;
	}
	public Map<String, String> get_defaultValueExpressions() {
		return _defaultValueExpressions;
	}
	public void set_defaultValueExpressions(
			Map<String, String> defaultValueExpressions) {
		_defaultValueExpressions = defaultValueExpressions;
	}
	public Map<String, Integer> get_searchables() {
		return _searchables;
	}
	public void set_searchables(Map<String, Integer> searchables) {
		_searchables = searchables;
	}
	public int getIndexPosition() {
		return indexPosition;
	}
	public void setIndexPosition(int indexPosition) {
		this.indexPosition = indexPosition;
	}
	
}
