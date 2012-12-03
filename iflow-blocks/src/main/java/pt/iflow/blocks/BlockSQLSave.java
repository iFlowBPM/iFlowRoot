/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Attribute;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DBTable;
import pt.iflow.api.db.DBTableHelper;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.processtype.FloatDataType;
import pt.iflow.api.processtype.IntegerDataType;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class BlockSQLSave extends BlockSQL {

  private static final String sWIZARD = "wizard";
  private static final String sWIZARD_SEPARATOR = "@";

  public BlockSQLSave(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
  }

  @Override
  // FIXME on insert: store conditionals in procData vars (store generated keys)
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sbQuery = null;

    DBTable dbTable = null;
    String sDataSource = null;
    String sQuery = null;
    String sTable = null;
    StringBuilder sbSet = null;
    boolean isUpdate = false;

    try {
      sDataSource = this.getAttribute(BlockSQL.sDATASOURCE);
      if (StringUtils.isNotEmpty(sDataSource)) {
        sDataSource = procData.transform(userInfo, sDataSource, true);
      }
      if (StringUtils.isEmpty(sDataSource))
        sDataSource = null;
    } catch (Exception e) {
      sDataSource = null;
    }

    if (StringUtils.isEmpty(sDataSource)) {
      Logger.warning(login, this, "after", procData.getSignature() + "NO DATASOURCE SPECIFIED !!!!!!!!!!!");
    }

    try {
      sTable = this.getAttribute(BlockSQL.sTABLE);
      if (StringUtils.isNotEmpty(sTable)) {
        sTable = procData.transform(userInfo, sTable, true);
      }
      if (sTable.equals("")) {
        sTable = null;
      }
    } catch (Exception e) {
    }

    if (StringUtils.isBlank(sTable)) {
      Logger.error(login, this, "after", procData.getSignature() + "Empty table");
      outPort = portError;
    } else {
      if (this.isSystemTable(sDataSource, sTable)) {
        Logger.error(login, this, "after", procData.getSignature() + "Table '" + sTable + "'is a system table");
        outPort = portError;
      } else {
        dbTable = new DBTable(sTable);
        HashMap<String, String> attrs = this.getAttributeMap();
        Iterator<String> iter = attrs.keySet().iterator();

        // fetch and prepare data
        DBTableHelper.clearCache();
        while (iter.hasNext()) {
          String name = iter.next();
          String value = attrs.get(name);
          if (StringUtils.startsWithIgnoreCase(name, sWIZARD + sWIZARD_SEPARATOR)) {
            DBTableHelper.addItem(dbTable, name.split(sWIZARD_SEPARATOR)[1], value, Integer.parseInt(name
                .split(sWIZARD_SEPARATOR)[2]));
          }
        }
        DBTableHelper.clearCache();

        boolean hasConditions2Check = false;
        StringBuffer sql = new StringBuffer("SELECT count(*) FROM ");
        sql.append(sTable);
        String sWhere = "";
        List<Map<String, String>> colMap = DBTableHelper.getColumns(dbTable);
        if (colMap != null) {
          for (Map<String, String> col : colMap) {
            if (Utils.string2bool(col.get(DBTable.COND))
                && StringUtils.isNotBlank(col.get(DBTable.VALUE))) {

              boolean isSql = Utils.string2bool(col.get(DBTable.SQL));
              if (StringUtils.isNotBlank(sWhere)) {
                sWhere += " AND ";
              }
              if (isSql)
                sWhere += col.get(DBTable.FIELD) + "=" + col.get(DBTable.VALUE);
              else                    
                sWhere += col.get(DBTable.FIELD) + "=?";
            }
          }
          if (StringUtils.isNotBlank(sWhere)) {
            sql.append(" WHERE ");
            sql.append(sWhere);
            hasConditions2Check = true;
          }

          if (Logger.isDebugEnabled()) {
            Logger.debug(login, this, "after", procData.getSignature() + "QUERY=" + sql);
          }

          if (hasConditions2Check) {
            try {
              ds = Utils.getUserDataSource(sDataSource);
              if (null == ds) {
                Logger.error(login, this, "after", procData.getSignature() + "null datasource for " + sDataSource);
                outPort = portError;
              } else {
                db = ds.getConnection();
                pst = prepareStatement(userInfo, procData, db, sql.toString(), dbTable, false, true);
                rs = pst.executeQuery();
                if (rs.next()) {
                  int count = rs.getInt(1);
                  if (count > 0) {
                    isUpdate = true;
                    Logger.debug(login, this, "after", procData.getSignature() + "Updating " + count + " rows!");
                  } else {
                    Logger.debug(login, this, "after", procData.getSignature() + "Inserting row!");
                  }
                }
              }
            } catch (SQLException sqle) {
              Logger.error(login, this, "after", "caught sql exception: " + sqle.getMessage(), sqle);
              outPort = portError;
            } catch (Exception e) {
              Logger.error(login, this, "after", "caught exception: " + e.getMessage(), e);
              outPort = portError;
            } finally {
              DatabaseInterface.closeResources(db, pst, rs);
            }
          }
          else {
            Logger.info(login, this, "after", "No conditions to check: assuming insert");
            isUpdate = false;
          }
          
          if (isUpdate) {
            sbQuery = new StringBuffer("UPDATE ");
            sbQuery.append(sTable);
            sbQuery.append(" SET ");
            sbSet = new StringBuilder();
            for (int i = 0; i < dbTable.getFields().size(); i++) {
              String sField = dbTable.getFields().get(i);
              String sValue = DBTableHelper.getListValue(dbTable.getValues(), i);

              if (StringUtils.isNotBlank(sField) && StringUtils.isNotBlank(sValue)) {
                boolean isSql = Utils.string2bool(DBTableHelper.getListValue(dbTable.getSql(), i));
                if (sbSet.length() > 0)
                  sbSet.append(",");

                if (isSql) {
                  sbSet.append(sField).append("=").append(sValue);
                } 
                else {
                  sbSet.append(sField).append("=?");
                }                  
              }
            }
            sbQuery.append(sbSet.toString());
            if (StringUtils.isNotBlank(sWhere)) {
              sbQuery.append(" WHERE ").append(sWhere);
            }
          } 
          else {
            sbQuery = new StringBuffer("INSERT INTO ");
            sbQuery.append(sTable);
            sbQuery.append(" (");
            sbSet = new StringBuilder();
            for (int i = 0; i < dbTable.getFields().size(); i++) {
              String sValue = DBTableHelper.getListValue(dbTable.getValues(), i);
              if (StringUtils.isNotBlank(sValue)) {
                if (sbSet.length() > 0)
                  sbSet.append(",");

                sbSet.append(dbTable.getFields().get(i));
              }
            }
            sbQuery.append(sbSet.toString());
            sbQuery.append(") VALUES (");
            StringBuilder sbValues = new StringBuilder();
            for (int i = 0; i < dbTable.getValues().size(); i++) {
              String sValue = DBTableHelper.getListValue(dbTable.getValues(), i);
              if (StringUtils.isNotBlank(sValue)) {
                if (sbValues.length() > 0)
                  sbValues.append(",");

                boolean isSql = Utils.string2bool(DBTableHelper.getListValue(dbTable.getSql(), i));

                if (isSql) {
                  sbValues.append(sValue);
                } 
                else {
                  sbValues.append("?");
                }
              }
            }
            sbQuery.append(sbValues.toString());
            sbQuery.append(")");
          }
          sQuery = sbQuery.toString();
        }
      }
    }
    if (sbSet == null || sbSet.length() == 0) {
      Logger.error(login, this, "after", procData.getSignature() + "Empty set");
      outPort = portError;
    }
    if (outPort != portError) {
      if (StringUtils.isEmpty(sQuery) || dbTable == null) {
        if (StringUtils.isEmpty(sQuery)) {
          Logger.error(login, this, "after", procData.getSignature() + "Empty query");
        } else if (dbTable == null) {
          Logger.error(login, this, "after", procData.getSignature() + "Unable to parse data");
        }
        outPort = portError;
      } else {
        try {
          ds = Utils.getUserDataSource(sDataSource);
          if (null == ds) {
            Logger.error(login, this, "after", procData.getSignature() + "null datasource for " + sDataSource);
            outPort = portError;
          } else {
            db = ds.getConnection();
            pst = prepareStatement(userInfo, procData, db, sQuery, dbTable, isUpdate, false);
            // save
            Logger.debug(login, this, "after", "Going to execute save: " + sQuery);
            int nCols = pst.executeUpdate();
            Logger.debug(login, this, "after", "Number of saved columns = " + nCols);

            if (nCols == 0) {
              outPort = portEmpty;
            }
          }
        } catch (SQLException sqle) {
          Logger.error(login, this, "after", "caught sql exception: " + sqle.getMessage(), sqle);
          outPort = portError;
        } catch (Exception e) {
          Logger.error(login, this, "after", "caught exception: " + e.getMessage(), e);
          outPort = portError;
        } finally {
          DatabaseInterface.closeResources(db, pst, rs);
        }
      }
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  private PreparedStatement prepareStatement(UserInfoInterface userInfo, ProcessData procData, Connection db,
      String query, DBTable dbTable, boolean isUpdate, boolean isCond)
  throws SQLException, ParseException {
    List<Attribute> values = new ArrayList<Attribute>();
    if (dbTable.getValues() != null) {
      for (int i = 0; i < dbTable.getValues().size(); i++) {
        String value = DBTableHelper.getListValue(dbTable.getValues(), i);
        String type = DBTableHelper.getListValue(dbTable.getTypes(), i);
        boolean isSql = Utils.string2bool(DBTableHelper.getListValue(dbTable.getSql(), i));
        if (isSql || StringUtils.isBlank(value))
          continue;
        
        if (!isCond || (isCond && Utils.string2bool(DBTableHelper.getListValue(dbTable.getConds(), i)))) {
          values.add(new Attribute(value, type)); 
        }
      }
      if (isUpdate && !isCond) {
        // add WHERE statement values
        for (int i = 0; i < dbTable.getValues().size(); i++) {
          String value = DBTableHelper.getListValue(dbTable.getValues(), i);
          String type = DBTableHelper.getListValue(dbTable.getTypes(), i);
          boolean isSql = Utils.string2bool(DBTableHelper.getListValue(dbTable.getSql(), i));
          if (isSql || StringUtils.isBlank(value))
            continue;

          if (Utils.string2bool(DBTableHelper.getListValue(dbTable.getConds(), i))) {
            values.add(new Attribute(value, type)); 
          }
        }
      }
    }
    return prepareStatement(userInfo, procData, db, query, values);
  }

  // FIXME add support for lists
  private PreparedStatement prepareStatement(UserInfoInterface userInfo, ProcessData procData, Connection db, String query, List<Attribute> values) throws SQLException, ParseException {
    PreparedStatement pst = db.prepareStatement(query);
    if (values != null) {
      int pos = 0;
      for (Attribute item : values) {
        String value = item.getName();
        String type = item.getValue();
        try {
          pos++;
          ProcessDataType pdt = procData.getVariableDataType(value);
          if (pdt != null) {
            if (pdt instanceof IntegerDataType) {
              Object varval = procData.get(value).getValue();
              if (varval instanceof Number) {
                long lval = ((Number)varval).longValue();
                if (containsIgnoreCaseOneOf(type, "time", "date")) {
                  pst.setTimestamp(pos, new Timestamp(lval));
                } 
                else {
                  pst.setLong(pos, lval);
                }                
              }
              else {
                pst.setString(pos, procData.getFormatted(value));
              }
            }
            else if (pdt instanceof FloatDataType) {
              double dval = ((Double)procData.get(value).getValue());
              if (containsIgnoreCaseOneOf(type, "time", "date")) {
                pst.setTimestamp(pos, new Timestamp((long)dval));
              } 
              else {
                pst.setDouble(pos, dval);
              }
            }
            else if (pdt instanceof DateDataType) {
              Date dt = ((Date)procData.get(value).getValue());
              if (containsIgnoreCaseOneOf(type, "time", "date")) {
                pst.setTimestamp(pos, new Timestamp(dt.getTime()));
              } 
              else if (containsIgnoreCaseOneOf(type, "int", "long", "number", "double")) {
                pst.setLong(pos, dt.getTime());
              }
              else {
                pst.setString(pos, procData.getFormatted(value));
              }
            }
            else {
              // as text
              pst.setString(pos, procData.getFormatted(value));              
            }
          } else {
            // defaults to string
            pst.setString(pos, procData.getFormatted(value));
          }
        } catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "prepareStatement", "Exception caught: ", e);
        }
      }
    }
    return pst;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Save");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Save Efectuado");
  }
  
  private boolean containsIgnoreCaseOneOf(String str, String... searchStrs) {
    if (str != null && searchStrs != null) {
      for (String searchStr : searchStrs) {
        if (StringUtils.containsIgnoreCase(str, searchStr)) {
          return true;
        }
      }
    }
    return false;
  }
}
