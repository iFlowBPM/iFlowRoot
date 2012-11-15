package pt.iflow.api.userdata.views;

public interface OrganizationalUnitViewInterface {

	public static final String UNITID = "UNITID";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String PARENTID = "PARENT_ID";
	public static final String ORGANIZATIONID = "ORGANIZATIONID";
	public static final String MANAGERID = "MANAGERID";
	public static final String LEVEL = "LEVEL";

	public abstract String getUnitId();

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getOrganizationId();

	public abstract String getParentId();

	public abstract String getManagerId();

	public abstract String getLevel();

}