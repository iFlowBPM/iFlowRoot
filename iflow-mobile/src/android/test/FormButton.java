package android.test;

public class FormButton {

	private String id;
	private String name;
	private String text;
	private String operation;
	private String tooltip;

	
	public FormButton() {
		
		super();
		
		this.id = "";
		this.name = "";
		this.text = "";
		this.operation = "";
		this.tooltip ="";
		
	
	}


	public void setId(String id) {
		this.id = id;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setText(String text) {
		this.text = text;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getText() {
		return text;
	}


	public String getOperation() {
		return operation;
	}


	public String getTooltip() {
		return tooltip;
	}
	
}
