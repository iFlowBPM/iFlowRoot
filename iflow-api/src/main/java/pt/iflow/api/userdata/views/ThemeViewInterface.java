package pt.iflow.api.userdata.views;

public interface ThemeViewInterface {

	public static final String ORGANIZATIONID = "ORGANIZATIONID";
	public static final String THEME = "THEME";
	public static final String STYLE_URL = "STYLE_URL";
	public static final String LOGO_URL = "LOGO_URL";

	public abstract String getOrganizationId();

	public abstract String getTheme();

	public abstract String getStyle();

	public abstract String getLogo();

}