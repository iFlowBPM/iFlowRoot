package pt.iflow.api.presentation;

import pt.iflow.api.presentation.FlowMenuItems;

public class FlowAppMenu {
	int appID;
	String appDesc;
	FlowMenuItems menuItems;
	
	public FlowAppMenu(int appID, String appDesc) {
		super();
		this.appID = appID;
		this.appDesc = appDesc;
		this.menuItems = new FlowMenuItems();
	}

	public int getAppID() {
		return appID;
	}

	public void setAppID(int appID) {
		this.appID = appID;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}

	public FlowMenuItems getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(FlowMenuItems menuItems) {
		this.menuItems = menuItems;
	}
	
	public boolean isEmpty() {
		
		if (this.menuItems == null) return true;
		
		return this.menuItems.isEmpty();
	}
	
}
