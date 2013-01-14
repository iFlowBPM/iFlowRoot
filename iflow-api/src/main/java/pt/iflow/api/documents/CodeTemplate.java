package pt.iflow.api.documents;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DBQueryManager;

public class CodeTemplate {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String TEMPLATE = "template";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String NAME = "name";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String DESCRIPTION = "description";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String CALLBACK = "callback";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String FLAG = "flag";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String ORGANIZATION = "organization";

    private String template;
  private String name;
  private String description;
  private String callback;
  private String flag;
  private String organization;

  public static String TABLE_NAME = "serial_code_templates";

  public static String[] TABLE_COLUMNS = { TEMPLATE, NAME, DESCRIPTION, CALLBACK, FLAG, ORGANIZATION };

  public CodeTemplate(String atemplate, String aname, String adescription, String acallback, String aorganization) {
    this.template = atemplate;
    this.name = aname;
    this.description = adescription;
    this.callback = acallback;
    this.organization = aorganization;
  }

  public CodeTemplate(String atemplate, String aname, String adescription, String acallback, String aflag, String aorganization) {
    this.template = atemplate;
    this.name = aname;
    this.description = adescription;
    this.callback = acallback;
    this.flag = aflag;
    this.organization = aorganization;
  }

  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, TEMPLATE)) {
      retObj = DBQueryManager.toQueryValue(getTemplate());
    } else if (StringUtils.equals(columnName, NAME)) {
      retObj = DBQueryManager.toQueryValue(getName());
    } else if (StringUtils.equals(columnName, DESCRIPTION)) {
      retObj = DBQueryManager.toQueryValue(getDescription());
    } else if (StringUtils.equals(columnName, CALLBACK)) {
      retObj = DBQueryManager.toQueryValue(getCallback());
    } else if (StringUtils.equals(columnName, FLAG)) {
      retObj = DBQueryManager.toQueryValue(getFlag());
    } else if (StringUtils.equals(columnName, ORGANIZATION)) {
      retObj = DBQueryManager.toQueryValue(getOrganization());
    }
    return retObj;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

}
