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
