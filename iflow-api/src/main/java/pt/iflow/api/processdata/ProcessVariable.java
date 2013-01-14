package pt.iflow.api.processdata;

import org.w3c.dom.Element;

import pt.iflow.api.processtype.ProcessDataType;


public abstract class ProcessVariable {
	String _name;

	ProcessDataType _type;

	public ProcessVariable(ProcessDataType type, String name) {
		_type = type;
		_name = name;
	}
	
	ProcessVariable(ProcessDataType type, Element xmlElement) {
		this(type, xmlElement.getAttribute("n"));
	}
	
	public String getName() {
		return _name;
	}
	
  public ProcessDataType getType() {
    return _type;
  }
  
	public abstract void clear();
	
	public abstract String toString();
	public abstract String toDebugString();

	public boolean isBindable() {
	  return _type.isBindable();
	}
	
	static String getNameFromElement(Element xmlElement) {
		return xmlElement.getAttribute("n");
	}
}
