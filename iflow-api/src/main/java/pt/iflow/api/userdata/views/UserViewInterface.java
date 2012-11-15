package pt.iflow.api.userdata.views;

public interface UserViewInterface {

	public static final String USERID = "USERID";
	public static final String USERNAME = "USERNAME";
	public static final String FIRST_NAME = "FIRST_NAME";
	public static final String LAST_NAME = "LAST_NAME";
	public static final String EMAIL = "EMAIL_ADDRESS";
	public static final String UNITID = "UNITID";
	public static final String MOBILE = "MOBILE_NUMBER";
	public static final String FAX = "FAX_NUMBER";
	public static final String COMPANY_PHONE = "COMPANY_PHONE";
	public static final String PHONE = "PHONE_NUMBER";
	public static final String GENDER = "GENDER";
	public static final String ACTIVATED = "ACTIVATED";
	public static final String ORGADM = "ORGADM";

	public abstract String getUserId();

	public abstract String getEmail();

	public abstract String getUsername();

	public abstract String getFirstName();

	public abstract String getLastName();

	public abstract String getUnitId();

	public abstract String getMobileNumber();

	public abstract String getFax();

	public abstract String getGender();

	public abstract String getCompanyPhone();

	public abstract String getPhoneNumber();

	public abstract String getActivated();

	public abstract String getOrgAdm();

    public abstract String get(String fieldName);
}