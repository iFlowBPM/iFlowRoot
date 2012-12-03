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
package pt.iflow.api.folder;



public class Folder {

	int folderid;
	String name;
	String color;
	String userid;
	
	public Folder(int folderid, String name, String color ) {
		  this.folderid = folderid;
		  this.name = name;
		  this.color = color;
	}
	
	public Folder(int folderid, String name, String color, String userid ) {
		  this.folderid = folderid;
		  this.name = name;
		  this.color = color;
		  this.userid = userid;
	}
	
	//SET
	public void setFolderid(int folderid){
		this.folderid = folderid;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setColor(String color){
		this.color = color;
	}
	
	public void setUserid(String userid){
		this.userid = userid;
	}
	
	//GET
	public int getFolderid(){
		return folderid;
	}
	
	public String getName(){
		return name;
	}
	
	public String getColor(){
		return color;
	}
	
	public String getUserid(){
		return userid;
	}
	
}
