package pt.iknow.utils.swing;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class CheckBoxCellEditor extends DefaultCellEditor {

	public CheckBoxCellEditor() {
		super(new JCheckBox());
		((JCheckBox)getComponent()).setHorizontalAlignment(JLabel.CENTER);
    // setClickCountToStart(2);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
