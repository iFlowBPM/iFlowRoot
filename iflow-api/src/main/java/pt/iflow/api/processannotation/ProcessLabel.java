package pt.iflow.api.processannotation;


public class ProcessLabel {

	private int id;
	private String name;
	private String description;
	private boolean check = false;
	
  public ProcessLabel(int id, String name, String description){
	  this.id = id;
	  this.name = name;
	  this.description = description;
  }	
  
  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setCheck(boolean check){
	  this.check = check;
  }
  
  public boolean getCheck() {
    return check;
  }
  
}
