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
