package pt.iflow.api.presentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

public class FlowMenu {

	OrderedMap<Integer, FlowAppMenu> appMenus;
	
	public FlowMenu() {
		appMenus = new ListOrderedMap<Integer,FlowAppMenu>();
	}

	public void addAppMenu(FlowAppMenu appMenu) {
		appMenus.put(appMenu.getAppID(), appMenu);
	}

	public FlowAppMenu getAppMenu(int asID) {
		FlowAppMenu appMenu = null;
		
		try {
			appMenu = appMenus.get(asID);
		}
		catch (Exception e) {
		}
		return appMenu;
	}

	public Collection<FlowAppMenu> getAppMenuList() {
		return appMenus.values();
	}

}
