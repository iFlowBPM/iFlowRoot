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
