package pt.iflow.servlets;

import java.util.Date;
import java.util.Hashtable;

public class RequestForApplet {
	
	private Hashtable<String,String> parameters;
	private Date created;

	public RequestForApplet(Hashtable<String, String> htp) {
		this.setCreated(new Date());
		this.setParameters(htp);
	}

	public Hashtable<String,String> getParameters() {
		return parameters;
	}

	public void setParameters(Hashtable<String,String> parameters) {
		this.parameters = parameters;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
