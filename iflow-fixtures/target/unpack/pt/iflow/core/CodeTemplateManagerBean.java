package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.vfs.connector.RestInterface;

import com.infosistema.fs.entity.FileStructure;
import com.infosistema.fs.entity.KeyValueStructure;
import com.infosistema.fs.entity.KeyValueStructureList;

/**
 * @author Ekaterina Hilário
 * @version 28.11.2012
 */
public class CodeTemplateManagerBean implements CodeTemplateManager {

  private static final String VFS_URL = "VFS_URL";
  private static CodeTemplateManagerBean instance = null;

  /**
   * This is a singleton.
   */
  private CodeTemplateManagerBean() {
  }

  /**
   * Retrieve this Singleton's instance, or create one if it is the first invocation.
   * 
   * @return An instance of this class.
   */
  public static CodeTemplateManagerBean getInstance() {
    if (instance == null) {
      instance = new CodeTemplateManagerBean();
    }
    return instance;
  }

  /**
   * @see pt.iflow.api.core.CodeTemplateManager#public boolean addCodeTemplate(UserInfoInterface userInfo, ProcessData procData,
   *      String codeTemplate)
   */
  public boolean addCodeTemplate(UserInfoInterface userInfo, ProcessData procData, CodeTemplate codeTemplate) {

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      List<CodeTemplate> existingCodeTemplates = this.listCodeTemplates(userInfo, procData);
      for (CodeTemplate ct : existingCodeTemplates)
        if (StringUtils.equalsIgnoreCase(ct.getName(), codeTemplate.getName())
            || StringUtils.equalsIgnoreCase(ct.getTemplate(), codeTemplate.getTemplate()))
          return false;

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      StringBuffer sql = new StringBuffer();
      if (contains(listCodeTemplates(userInfo, procData), codeTemplate)) {
        return false;
      } else {
        sql = buildQuerySqlInsert(codeTemplate);
      }

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "addCodeTemplate", "QUERY=" + sql);
      }

      if (StringUtils.isNotBlank(sql.toString())) {
        st.executeUpdate(sql.toString());
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addCodeTemplate", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      //jcosta: DatabaseInterface.closeResources(db, st, rs);
      if (db != null) DatabaseInterface.safeClose(db);
      if (st != null) DatabaseInterface.safeClose(st);      
      if (rs != null) DatabaseInterface.safeClose(rs);
    }
    return true;
  }

  /**
   * @see pt.iflow.api.core.CodeTemplateManager#listCodeTemplates(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   */
  public List<CodeTemplate> listCodeTemplates(UserInfoInterface userInfo, ProcessData procData) {
    List<CodeTemplate> templates = new ArrayList<CodeTemplate>();
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT * FROM " + CodeTemplate.TABLE_NAME);
      sql.append(" WHERE " + CodeTemplate.ORGANIZATION + "=?");
      sql.append(" ORDER BY " + CodeTemplate.NAME);

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "listCodeTemplates", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      pst.setString(1, userInfo.getCompanyID());
      rs = pst.executeQuery();
      while (rs.next()) {
        String template = rs.getString(CodeTemplate.TEMPLATE);
        String name = rs.getString(CodeTemplate.NAME);
        String description = rs.getString(CodeTemplate.DESCRIPTION);
        String callback = rs.getString(CodeTemplate.CALLBACK);
        String flag = rs.getString(CodeTemplate.FLAG);
        String organization = rs.getString(CodeTemplate.ORGANIZATION);
        templates.add(new CodeTemplate(template, name, description, callback, flag, organization));
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "listCodeTemplates", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return templates;
  }

  /**
   * @see pt.iflow.api.core.CodeTemplateManager#removeCodeTemplate(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessDataString)
   */
  public void removeCodeTemplate(UserInfoInterface userInfo, ProcessData procData, String name) {

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      StringBuffer sql = new StringBuffer();
      sql = buildQuerySqlRemove(name, userInfo.getOrganization());

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "removeCodeTemplate", "QUERY=" + sql);
      }

      if (StringUtils.isNotBlank(sql.toString())) {
        st.executeUpdate(sql.toString());
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeCodeTemplate", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
  }

  public StringBuffer buildQuerySqlRemove(String name, String organization) {
    StringBuffer sql = new StringBuffer("DELETE FROM " + CodeTemplate.TABLE_NAME);
    sql.append(" WHERE " + CodeTemplate.NAME + "='" + name + "' AND ORGANIZATION =" + organization);
    return sql;
  }

  private boolean contains(List<CodeTemplate> codeTemplates, CodeTemplate template) {
    for (CodeTemplate item : codeTemplates) {
      if (StringUtils.equals(item.getName(), template.getName()) && StringUtils.equals(item.getTemplate(), template.getTemplate())) {
        return true;
      }
    }
    return false;
  }

  private StringBuffer buildQuerySqlInsert(CodeTemplate template) {
    StringBuffer sql = new StringBuffer("INSERT INTO " + CodeTemplate.TABLE_NAME + " (");
    for (String column : CodeTemplate.TABLE_COLUMNS) {
      if (StringUtils.equals(column, CodeTemplate.TABLE_COLUMNS[0])) {
        sql.append(column);
        continue;
      }
      sql.append("," + column);
    }
    sql.append(") VALUES (");
    for (String column : CodeTemplate.TABLE_COLUMNS) {
      if (StringUtils.equals(column, CodeTemplate.TABLE_COLUMNS[0])) {
        sql.append(template.getValueOf(column));
        continue;
      }
      sql.append("," + template.getValueOf(column));
    }
    sql.append(")");
    return sql;
  }

  public String generateSerialCode(UserInfoInterface userInfo, String templateName) {
    for (CodeTemplate cTemplate : listCodeTemplates(userInfo, null))
      if (StringUtils.equals(cTemplate.getName(), templateName)) {
        String result = "";
        for (char c : cTemplate.getTemplate().toCharArray())
          if (c == '0')
            result += (char) randomNumber(48, 57);
          else if (c == 'x' || c == 'X')
            result += (char) randomNumber(65, 90);
        return result;
      }
    return "";
  }

  private int randomNumber(int min, int max) {
    return min + (new Random()).nextInt(max - min);
  }

  public String createNewSerialCode(UserInfoInterface userInfo, String templateName) {
    int limit = 0;
    while (true) {
      limit++;
      String result = generateSerialCode(userInfo, templateName);      
      RestInterface ri = new RestInterface(Setup.getProperty(VFS_URL));
      FileStructure f = ri.getFile(result);
      if (f == null || StringUtils.isBlank(f.getSerial()))
        return result;
      if (limit > 1000)
        return "";
    }
  }

  public Boolean markAsTag(UserInfoInterface userInfo, String templateName, Boolean mark) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      String sql = "UPDATE serial_code_templates set flag = '" + mark + "' WHERE name ='" + templateName + "' AND organization = "
          + userInfo.getOrganization();

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "markAsTag", "QUERY=" + sql);
      }

      if (StringUtils.isNotBlank(sql.toString())) {
        int ret = st.executeUpdate(sql.toString());
        return (ret > 0);
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeCodeTemplate", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return false;
  }

  public Boolean checkMetaTag(UserInfoInterface userInfo, Boolean mark) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      RestInterface ri = new RestInterface(Setup.getProperty(VFS_URL));
      KeyValueStructureList keyValues = ri.getKeyValue();
      String keyValueNames = "(";
      for (KeyValueStructure keyValueStructure : keyValues.getKeyValueStructure())
        keyValueNames += "'" + keyValueStructure.getValue() + "',";

      keyValueNames = keyValueNames.substring(0, keyValueNames.length() - 1) + ")";

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      String sql = "UPDATE serial_code_templates set flag = '" + mark + "' WHERE name IN " + keyValueNames + " AND organization = "
          + userInfo.getOrganization();

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "markAsTag", "QUERY=" + sql);
      }

      if (StringUtils.isNotBlank(sql.toString())) {
        int ret = st.executeUpdate(sql.toString());
        return (ret > 0);
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeCodeTemplate", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return false;
  }
}
