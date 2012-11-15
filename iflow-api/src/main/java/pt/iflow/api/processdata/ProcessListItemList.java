package pt.iflow.api.processdata;

import java.util.ArrayList;
import java.util.List;

public class ProcessListItemList extends ArrayList<ProcessListItem> implements List<ProcessListItem> {
  private static final long serialVersionUID = -7445860468279270860L;
  
  int _initialSize;

	public ProcessListItemList() {
		this(0);
	}
	
	public ProcessListItemList(int initialSize) {
		_initialSize = initialSize;
		initItems();
	}

	private void initItems() {
	  ensureListSize(_initialSize);
	}
	
	public void ensureListSize(int minCapacity) {
	  super.ensureCapacity(minCapacity);
	  for(int i = size(); i < minCapacity; i++) // add to increase size...
	    super.add(null);
	}
	
	
	public ProcessListItem set(ProcessListItem element) {
		return set(element.getPosition(), element);
	}

	public ProcessListItem addItem(ProcessListItem item) {
		int index = -1;
		synchronized (this) {
			index = size();
			add(item);
		}
		return get(index);
	}
	

	// ARRAYLIST EXTENSION
	
	public boolean add(ProcessListItem e) {
		if (e == null)
			return super.add(e);
		
		synchronized (this) {		
			e.setPosition(size());
			return super.add(e);
		}
	}

	public void add(int index, ProcessListItem element) {
		if (element != null) 
			element.setPosition(index);
		ensureListSize(index+1);
		super.add(index, element);
	}

	public ProcessListItem set(int index, ProcessListItem element) {
    if (element != null) 
      element.setPosition(index);
		ensureListSize(index+1);
		return super.set(index, element);
	}

	public boolean equals(ProcessListItemList list) {
		if (list == null)
			return false;
		if (size() != list.size())
			return false;
		
		for (int i=0; i < size(); i++) {
			if (!get(i).equals(list.get(i)))
				return false;
		}
		return true;
	}
}
