package pt.iflow.api.userdata.views;

public interface OrganizationViewInterface {

	public static final String ORGANIZATIONID = "ORGANIZATIONID";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String LOCKED = "LOCKED";

	public abstract String getOrganizationId();

	public abstract String getName();

	public abstract String getDescription();

	public abstract boolean isLocked();

}