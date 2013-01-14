package android.test;

import java.util.ArrayList;

public class BaseForm {
	
	private String name;
	private String action;
	private ArrayList<FormField> blockDivision;
	private ArrayList<FormButton> formButtons;
	private ArrayList<Hidden> hiddenList;
	
	
	public BaseForm(String name, String action){
		
		super();
		
		this.name = name;
		this.action = action;
		
		blockDivision = new ArrayList<FormField>();
		formButtons = new ArrayList<FormButton>();
		hiddenList = new ArrayList<Hidden>();
	
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public ArrayList<Hidden> getHiddenList() {
		return hiddenList;
	}


	public void setHiddenList(ArrayList<Hidden> hiddenList) {
		this.hiddenList = hiddenList;
	}


	public ArrayList<FormField> getBlockDivision() {
		return blockDivision;
	}


	public ArrayList<FormButton> getFormButtons() {
		return formButtons;
	}


	public void setBlockDivision(ArrayList<FormField> a){
		
		blockDivision = a;
		
	}
	
	public void setFormButtons(ArrayList<FormButton> a){
		
		formButtons = a;
	}
	
	public void setHidden(ArrayList<Hidden> a){
		
		hiddenList = a;
		
	}
}
