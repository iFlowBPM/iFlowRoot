package pt.iflow.api.utils;

public enum ServletUtilsRoutesEnum {
	
	LOGIN("login.jsp"),
	MAIN("main.jsp"),
	FORM("Form/form.jsp"),
	LICENSE("Admin/licenseValidation.jsp"),
	SETUP("setupUser"),
	MAINTENANCE("maintenance.jsp"),
	PERSONALACCOUNT("GoTo?goto=personal_account.jsp"),
	LICENSESERVLET("Admin/Organization/license.jsp"),
	LOGOUT("logout.jsp"),
	PROCUSERSTS("proc_users.jsp?ts=1508325725924"),
	UNAUTHORIZED("Admin/unauthorized.jsp"),
	DATASOURCES("datasources.jsp"),
	FLOWSTATES("flow_states.jsp"),
	SERIES("series.jsp"),
	ORGANIZATIONADM("organizationadm.jsp"),
	PROFILEADM("profileadm.jsp"),
	UNITADM("UNITADM.jsp"),
	USERADM("useradm.jsp"),
	FLOWSETTINGS("flow_settings.jsp"),
	FLOWERROR("flow_error.jsp"),
	NOPRIV("nopriv.jsp"),
	NODEPLOYEDFLOW("nodeployedflow.jsp"),
	NOINITPROC("noinitproc.jsp"),
	ADMINLOGIN("Admin/login.jsp"),
	
	;

	private String url;
	
	private ServletUtilsRoutesEnum(String url) 
	{
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}

