package android.test;

import java.util.ArrayList;

public class FormField {
	

	private String type;
	private String text;
	private String cssClass;
	private String alignment;
	private boolean obligatory;
	private String variable;
	private String value;
	private int maxLength;
	private String suffix;
	private boolean disabled;
	private String onChangeSubmit;
	private ArrayList<ListOption> optionsList;
	private boolean evenField;
	private int size;
	private String onBlur;
	private String onFocus;
	
	
	
	//constructor
	public FormField() {
		
		super();
		
		this.type = "";
		this.text = "";
		this.cssClass = "";
		this.alignment = "";
		this.obligatory = false;
		this.variable = "";
		this.value = "";
		this.maxLength = -1;
		this.suffix = "";
		this.disabled = false;
		this.onChangeSubmit = "";
		optionsList = new ArrayList<ListOption>();
		this.evenField = false;
		this.size = -1;
		this.onBlur = "";
		this.onFocus = "";
		
		optionsList = new ArrayList<ListOption>();
		
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public void setObligatory(boolean obligatory) {
		this.obligatory = obligatory;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setOnChangeSubmit(String onChangeSubmit) {
		this.onChangeSubmit = onChangeSubmit;
	}

	public void setOptionsList(ArrayList<ListOption> lo){
		
		optionsList = lo;
	}
	
	public String getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public String getCssClass() {
		return cssClass;
	}

	public String getAlignment() {
		return alignment;
	}

	public boolean isObligatory() {
		return obligatory;
	}

	public String getVariable() {
		return variable;
	}

	public String getValue() {
		return value;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public String getOnChangeSubmit() {
		return onChangeSubmit;
	}

	public ArrayList<ListOption> getOptionsList() {
		return optionsList;
	}

	public boolean isEvenField() {
		return evenField;
	}

	public void setEvenField(boolean b){
		
		this.evenField = b;
		
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getOnBlur() {
		return onBlur;
	}

	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}

	public String getOnFocus() {
		return onFocus;
	}

	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}
	
	
	
}
