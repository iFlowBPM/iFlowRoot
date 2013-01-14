package android.test;

public class ListOption {

	private String text;
	private String value;
	private boolean selected;
	
	public ListOption() {
	
		super();
		
		this.selected = false;
		this.text = "";
		this.value = "";
	
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
