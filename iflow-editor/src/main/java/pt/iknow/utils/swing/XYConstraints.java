package pt.iknow.utils.swing;

import java.awt.GridBagConstraints;

/**
 * 
 * XYConstraints implemented as GridBagConstraints. Further information, read:
 * http://forum.java.sun.com/thread.jspa?threadID=647308&messageID=3811056
 * 
 * @author oscar
 *
 */
public class XYConstraints extends GridBagConstraints {

	private static final long serialVersionUID = 713924721826583067L;

	
	public XYConstraints(int x, int y, int w, int h) {
		super();
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = w;
		this.gridheight = h;
	}
	
}
