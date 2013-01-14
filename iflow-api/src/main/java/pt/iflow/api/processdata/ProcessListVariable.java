package pt.iflow.api.processdata;

import java.text.ParseException;
import java.util.ListIterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.iflow.api.processtype.ProcessDataType;


public class ProcessListVariable extends ProcessVariable {

	ProcessListItemList _items;
	
	public ProcessListVariable(ProcessDataType type, String name) {
		super(type, name);
		_items = new ProcessListItemList();
	}

  public ProcessListVariable(ProcessListVariable variable) {
    this(variable._type, variable._name);
    for(ProcessListItem item : variable._items) {
      if(null == item) continue;
      InternalValue internalValue = new InternalValue(_type,item.getValue());
      ProcessListItem newItem = new ProcessListItem(internalValue);
      setItem(newItem);
    }
  }

	ProcessListVariable(ProcessDataType type, Element xmlElement) throws ParseException {
		super(type, xmlElement);

		int size = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_SIZE));
		
		_items = new ProcessListItemList(size);
		
		NodeList itemNodes = xmlElement.getChildNodes();
		
		for (int i=0; i < itemNodes.getLength(); i++) {
			Node node = itemNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element itemElement = (Element)node;
				ProcessListItem item = new ProcessListItem(_type, itemElement);
				_items.set(item);
			}
		}		
	}
	
	public int size() {
		return _items.size();
	}
	
	public ListIterator<ProcessListItem> getItemIterator() {
		return _items.listIterator();
	}
	
	public ProcessListItemList getItems() {
		return _items;
	}
	
	public ProcessListItem getItem(int position) {
	  try {
	    return _items.get(position);
	  } catch (IndexOutOfBoundsException e) {
	    
	  }
	  return null;
	}
	
	public Object getItemValue(int position) {
		ProcessListItem item = getItem(position);
		if (item == null)
			return null;
		
		return item.getValue();
	}

	public String getFormattedItem(int position) {
		ProcessListItem item = getItem(position);
		if (item == null)
			return null;
		
		return item.format();
	}
	
	public ProcessListItem parseAndAddNewItem(String source) throws ParseException {
		if (source == null)
			return addItem(null);
		
		InternalValue inValue = new InternalValue(_type);
		inValue.parse(source);
		return addItem(inValue);
	}
	
	public ProcessListItem addNewItem(Object value) {
		if (value == null)
			return addItem(null);
		
		InternalValue inValue = new InternalValue(_type);
		inValue.setValue(value);
		return addItem(inValue);
	}
	
	private  ProcessListItem addItem(InternalValue value) {
		if (value == null) {
			_items.add(null);
			return null;
		}		
		
		ProcessListItem item = new ProcessListItem(value);
		item = _items.addItem(item);
		return item;		
	}
	
	public void parseAndSetItemValue(int position, String source) throws ParseException {
		if (source == null) {
			setItem(position, null);
			return;
		}
		ProcessListItem item = getItem(position);
		if (item == null) {
			InternalValue inValue = new InternalValue(_type);
			inValue.parse(source);
			item = new ProcessListItem(position, inValue);
		}
		else {
			item.parse(source);
		}
		
		if (item.getValue() == null) {
		  item = null;
		}
		
		setItem(position, item);
	}
	
	public void setItemValue(int position, Object value) {
		if (value == null) {
			setItem(position, null);
			return;
		}

		ProcessListItem item = getItem(position);
		if (item == null) {
			InternalValue inValue = new InternalValue(_type);
			inValue.setValue(value);
			item = new ProcessListItem(position, inValue);
		}
		else {
			item.setValue(value);
		}
		setItem(position, item);
	}

	public void setItem(ProcessListItem item) {
		_items.set(item.getPosition(), item);
	}
	
	private void setItem(int position, ProcessListItem item) {
	  _items.set(position, item);
	}
	
	public void setItems(ProcessListItemList items) {
		_items = items;
	}
	
	public ProcessListItem removeItemAt(int pos) {
	  return _items.remove(pos);
	}
	
    public boolean removeItem(ProcessListItem item) {
      return _items.remove(item);
    }
    
	@Override
	public void clear() {
		_items = new ProcessListItemList();
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toDebugString() {
		return toString(true);
	}

	private String toString(boolean debug) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append(getName()).append("[\n");
		
		for(ProcessListItem item : _items) {
			if (item != null) {
				sb.append("\t\t");
				if (debug)
					sb.append(item.toDebugString());
				else
					sb.append(item.toString());
				sb.append("\n");
			}
		}
		
		sb.append("\t]");
		return sb.toString();		
	}
	
	public boolean equals(ProcessListVariable var) {
		return _items.equals(var._items);
	}
}
