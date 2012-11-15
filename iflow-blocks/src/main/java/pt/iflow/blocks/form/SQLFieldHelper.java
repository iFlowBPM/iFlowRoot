package pt.iflow.blocks.form;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

class SQLFieldHelper {

  public static String getSQLData(UserInfoInterface userInfo, ProcessData procData, Properties props) {
    String login = userInfo.getUtilizador();

    String value = null;
    
    // process query
    String query = props.getProperty(FormProps.sQUERY);
    if (StringUtils.isNotEmpty(query)) {

      String dsName = props.getProperty(FormProps.sDATASOURCE);

      DataSource ds = null;
      Connection db = null;
      Statement st = null;
      ResultSet rs = null;

      try {
        if (StringUtils.isNotEmpty(dsName)) {
          dsName = procData.transform(userInfo, dsName, true);
        }
        if (StringUtils.isEmpty(dsName)) {
          dsName = null;
        }

        String transfQuery = procData.transform(userInfo, query, true);

        if (StringUtils.isEmpty(transfQuery)) {
          // undo transformation
          transfQuery = query;
        }

        ds = Utils.getUserDataSource(dsName);
        db = ds.getConnection();
        st = db.createStatement();

        Logger.debug(login,"SQLFieldHelper","setup",
            procData.getSignature() +
            "importing SQL attributes for query: "
            + transfQuery);

        rs = st.executeQuery(transfQuery);
        
        
        if (rs != null && rs.next()) {
          value = rs.getString(1);
        }
      }
      catch (Exception ei) {
        Logger.error(login,"SQLFieldHelper","setup",
            procData.getSignature() +
            "importing SQL attributes: "
            + ei.getMessage(), ei);
      }
      finally {
        DatabaseInterface.closeResources(db,st,rs);
      }
    }

    if (StringUtils.isEmpty(value)) {
      value = "";  // avoid null pointer in props set
    }

    return value;
  }
  
}
