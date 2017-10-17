package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processdata.ProcessVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iknow.parsers.SQLExpressionParser;
import pt.iknow.parsers.Selection;
import pt.iknow.parsers.SelectionElement;


/**
 * <p>Title: BlockSQLSelect</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSQLSelect extends BlockSQL {

  private static final String advancedQuery = "advancedQuery";	
	
  public BlockSQLSelect(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
  }

  /**
   * Column's Metadata
   */
  class ColumnData {
    private int index, type;
    private String name, label;
       
    /**
     * Column Metadata
     * @param index The column's index
     * @param name The designated column's name
     * @param label The designated column's suggested title 
     * @param type The designated column's SQL type
     */
    public ColumnData(int index, String name, String label, int type) {
      this.index = index;
      this.name = name;
      this.label = label;
      this.type = type;
    }
    
    public int getIndex() { return this.index; }            
    public String getName() { return this.name; }            
    public String getLabel() { return this.label; }            
    public int getType() { return this.type; }

    public String getVarName() { 
      return StringUtils.isNotEmpty(label) ? label : name; 
    }
  }
  
  // Verifica se foi escolhido resultado unitário
  private boolean isSingle(UserInfoInterface userInfo, ProcessData procData) {
    String singleAttr = this.getAttribute(BlockSQL.sSINGLE).trim();
    try {
      singleAttr = procData.transform(userInfo, singleAttr, true);
    } catch (EvalException e) {
      return false;
    }
    return ArrayUtils.contains(new String[]{"true", "yes", "sim", "s", "y", "1"},
        singleAttr == null ? null : singleAttr.toLowerCase());
  }
  
  
  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;

    String sDataSource = null;
    boolean bSingle = false;
    String sNumResult = null;
    int numResultLines = -1;
    String sQuery = null;

    try{
      sQuery = this.getAttribute(advancedQuery);
      if (StringUtils.isNotEmpty(sQuery)) {
        sQuery = procData.transform(userInfo, sQuery, true);
      }
      if (StringUtils.isEmpty(sQuery)) sQuery = null;
    }
    catch (Exception e) {
      sQuery = null;
    }    
    try {
      sDataSource = this.getAttribute(BlockSQL.sDATASOURCE);
      if (StringUtils.isNotEmpty(sDataSource)) {
        sDataSource = procData.transform(userInfo, sDataSource, true);
      }
      if (StringUtils.isEmpty(sDataSource)) sDataSource = null;
    }
    catch (Exception e) {
      sDataSource = null;
    }

    boolean advanced = StringUtils.isNotEmpty(sQuery); 
    if (!advanced) {
      StringBuffer sbQuery = null;
      String sSel = null;
      String sFrom = null;
      String sWhere = null;
      String sGroup = null;
      String sOrder = null;

      try {
        sSel = this.getAttribute(BlockSQL.sSEL);
        if (StringUtils.isNotEmpty(sSel)) {
          sSel = procData.transform(userInfo, sSel, true);
        }
        if (sSel.equals("")) sSel = null;
      }
      catch (Exception e) {
        sSel = null;
      }
      try {
        sFrom = this.getAttribute(BlockSQL.sFROM);
        if (StringUtils.isNotEmpty(sFrom)) {
          sFrom = procData.transform(userInfo, sFrom, true);
        }
        if (sFrom.equals("")) sFrom = null;
      }
      catch (Exception e) {
        sFrom = null;
      }
      try {
        sWhere = this.getAttribute(BlockSQL.sWHERE);
        if (StringUtils.isNotEmpty(sWhere)) {
          sWhere = procData.transform(userInfo, sWhere, true);
        }
        if (sWhere.equals("")) sWhere = null;
      }
      catch (Exception e) {
        Logger.error(login, this, "after", procData.getSignature() + "Error in where: '"+ sWhere +"'");
        outPort = portError;
        sWhere = null;
      }
      try {
        sGroup = this.getAttribute(BlockSQL.sGROUP);
        if (StringUtils.isNotEmpty(sGroup)) {
          sGroup = procData.transform(userInfo, sGroup, true);
        }
        if (sGroup.equals("")) sGroup = null;
      }
      catch (Exception e) {
        sGroup = null;
        Logger.error(login, this, "after", procData.getSignature() + "Error in group: '"+ sGroup +"'");
        outPort = portError;
      }
      try {
        sOrder = this.getAttribute(BlockSQL.sORDER);
        if (StringUtils.isNotEmpty(sOrder)) {
          sOrder = procData.transform(userInfo, sOrder, true);
        }
        if (sOrder.equals("")) sOrder = null;
      }
      catch (Exception e) {
        sOrder = null;
        Logger.error(login, this, "after", procData.getSignature() + "Error in order: '"+ sOrder +"'");
        outPort = portError;
      }
      
      bSingle = isSingle(userInfo, procData) ? true : false;
      
      try {
        sNumResult = this.getAttribute(BlockSQL.sNumResult);
        if (StringUtils.isNotEmpty(sNumResult)) {
          sNumResult = procData.transform(userInfo, sNumResult, true);
          numResultLines = Integer.parseInt(sNumResult);
        }
        if (sNumResult.equals("")) sNumResult = null;
      }
      catch (Exception e) {
        sNumResult = null;
      }

      Logger.debug(login,this,"after","ARGS: DS: " + sDataSource 
          + "\n **** sel: " + sSel 
          + "\n **** from: " + sFrom
          + "\n **** where: " + sWhere
          + "\n **** group: " + sGroup
          + "\n **** order: " + sOrder
          + "\n **** single: " + bSingle
          + "\n **** numResult: " + sNumResult);


      if (sSel == null || sFrom == null) {
        Logger.error(login, this, "after", procData.getSignature() + "Empty select or from");
        outPort = portError;
      } else {
        try {

          Selection selection = null;
          
          try  {
            selection = SQLExpressionParser.parseSelection(sSel);
          }
          catch (Throwable t) {
            throw new Exception("Unable to parse selection", t);
          }
          Logger.debug(login,this,"after","Parsed selection: " + selection); 

          List<SelectionElement> elems = selection.getElements();
          String aggregator = selection.getAggregator();

          if(null == elems) {
            Logger.error(login, this, "after", procData.getSignature() + "No elements for parsed selection");            
            outPort = portError;
          } else {
            String sDateFormat = procData.getFormatted(Const.sFLOW_DATE_FORMAT);
            if (StringUtils.isEmpty(sDateFormat)) {
              sDateFormat = Const.sDEF_DATE_FORMAT;
            }


            sbQuery = new StringBuffer("select ");

            if(StringUtils.isNotEmpty(aggregator))
              sbQuery.append(aggregator).append(" ");

            for (int i=0; i < elems.size(); i++) {
              if (i > 0) {
                sbQuery.append(",");
              }
              SelectionElement elem = elems.get(i);
              sbQuery.append(elem.getSQLSelect()); // evitar o f_to_char()
            }
            sbQuery.append(" from ").append(sFrom);
            if (sWhere != null) {
              sbQuery.append(" where ").append(sWhere);
            }
            if (sGroup != null) {
              sbQuery.append(" group by ").append(sGroup);
            }
            if (sOrder != null) {
              sbQuery.append(" order by ").append(sOrder);
            }
            sQuery = sbQuery.toString();
          }
        }
        catch (Exception e) {
          Logger.error(login,this,"after",
              procData.getSignature() + "caught exception: " + e.getMessage(), e);
          outPort = portError;
        }
      }
    }

    if (outPort != portError && StringUtils.isEmpty(sQuery)) {
      Logger.error(login, this, "after", procData.getSignature() + "Empty query string");
      outPort = portError;
    }
    
    if (outPort != portError) {
      
      try {        
        ds = Utils.getUserDataSource(sDataSource);
        if (null == ds) {
          outPort = portError;
          Logger.error(login, this, "after", procData.getSignature() + "Unable to get user datasource " + sDataSource);
        } else {
          db = ds.getConnection();
          st = db.createStatement();

          // fetch
          Logger.debug(login, this, "after", "Going to execute query: " + sQuery);
          rs = st.executeQuery(sQuery);
          rsmd = rs.getMetaData();

          List<ColumnData> columns = new ArrayList<ColumnData>();

          for(int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (Logger.isDebugEnabled()) {
              String metaData = "index:" + i + "|Name:" + rsmd.getColumnName(i) + "|Label:" + rsmd.getColumnLabel(i) + "|Type:"
                                + rsmd.getColumnTypeName(i) + "," + rsmd.getColumnType(i) + "|ColumnClassName:" + rsmd.getColumnClassName(i);
              Logger.debug(login, this, "after", "Metadata: " + metaData);
//              Logger.debug(login, this, "after", "===== " + rsmd.getColumnName(i) + " ISNUMERIC: " + (rsmd.getColumnType(i) == Types.NUMERIC));
            }
            ColumnData cData = new ColumnData(i, rsmd.getColumnName(i), rsmd.getColumnLabel(i), rsmd.getColumnType(i));
            columns.add(cData);
          }

          bSingle = isSingle(userInfo, procData) ? true : false;
          
          // clean up vars...
          for(ColumnData content : columns) {
            if (bSingle) {        
              Logger.debug(login, this, "after", "Cleaning var: " + content.getVarName());
              logMsg.append("Cleaned var: " + content.getVarName() + ";");
              procData.clear(content.getVarName());
              Logger.debug(login, this, "after", "Var " + content.getVarName() + " cleaned");
            }
            else {
              Logger.debug(login, this, "after", "Cleaning list var: " + content.getVarName());
              logMsg.append("Cleaned list var: " + content.getVarName() + ";");
              procData.clearList(content.getVarName());
              Logger.debug(login, this, "after", "List Var " + content.getVarName() + " cleaned");
            }
          }
          Logger.debug(login, this, "after", "All vars cleaned");

          int counter = -1;
          while(rs.next()) {
            counter++;

            if (numResultLines > -1 && counter > numResultLines) {
              Logger.info(login, this, "after", "Select limited to " + numResultLines + " results. breaking...");
              break;
            }

            for(ColumnData content : columns) {

              ProcessSimpleVariable psv = bSingle ? procData.get(content.getVarName()) : null;
              ProcessListVariable plv = bSingle ? null : procData.getList(content.getVarName()); 
              ProcessVariable pv = bSingle ? psv : plv;

              if (pv == null) {
                Logger.warning(login, this, "after", "LIST VAR " + content.getVarName() + " IS NULL! Continuing to next one");
                continue;
              }
              if (pv.isBindable()) {
                Logger.warning(login, this, "after", "LIST VAR " + content.getVarName() + " IS BINDABLE! Continuing to next one");
                continue;
              }                

              Object value = null;
              if (pv.getType().getSupportingClass() == java.util.Date.class) {
                if(content.getType() == Types.DATE) {
                  Timestamp ts = rs.getTimestamp(content.getIndex());
                  if (ts != null) {
                    value = new Date(ts.getTime());
                    if (value != null) {
                      if (bSingle) {
                        psv.setValue(value);
                      }
                      else {
                        plv.setItemValue(counter, value);
                      }
                    }
                  }
                }
                else {                  
                  value = rs.getString(content.getIndex());
                  if (value != null) {
                    if (bSingle) {
                      procData.parseAndSet(content.getVarName(), (String)value); 
                    }                  
                    else {
                      plv.parseAndSetItemValue(counter, (String)value);
                    }
                  }
                }                
              }
              else if (pv.getType().getSupportingClass() == int.class 
                  || pv.getType().getSupportingClass() == double.class) {

                Object n = rs.getObject(content.getIndex());
                if (n != null) {
                  if (n instanceof Number) {
                    if (pv.getType().getSupportingClass() == int.class) {
                      value = new Integer(((Number)n).intValue());
                    }
                    else {
                      value = new Float(((Number)n).floatValue());                    
                    }

                    if (bSingle) {
                      psv.setValue(value);
                    }
                    else {
                      plv.setItemValue(counter, value);
                    }
                  }
                }
                else {                  
                  value = rs.getString(content.getIndex());
                  if (value != null) {
                    if (bSingle) {
                      psv.setValue(value);
                    }
                    else {
                      plv.setItemValue(counter, value);
                    }
                  }
                }
              }
              else {
                value = rs.getString(content.getIndex());
                if (value != null) {
                  if (pv.getType().getSupportingClass() == java.lang.String.class) {
                    if (bSingle) {
                      psv.setValue(value);
                    }
                    else {
                      plv.setItemValue(counter, value);
                    }
                  }
                  else {
                    if (bSingle) {
                      procData.parseAndSet(content.getVarName(), (String)value); 
                    }                  
                    else {
                      plv.parseAndSetItemValue(counter, (String)value);
                    }
                  }
                }
              }

              Logger.debug(login, this, "after", "setting " + content.getName() + "[" + counter + "]=" + value);
              logMsg.append("Set '" + content.getName() + "[" + counter + "]' as '" + value + "';");

            }
          }

          rs.close();
          rs = null;

          if (counter == -1) {
            outPort = portEmpty;
          }
        }
      } 
      catch (SQLException sqle) {
        Logger.error(login,this,"after",
            procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
        outPort = portError;
      }
      catch (Exception e) {
        Logger.error(login,this,"after",
            procData.getSignature() + "caught exception: " + e.getMessage(), e);
        outPort = portError;
      }
      finally {
        DatabaseInterface.closeResources(db,st,rs);
      }

      logMsg.append("Using '" + outPort.getName() + "';");
      Logger.logFlowState(userInfo, procData, this, logMsg.toString());
      return outPort;
    }
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Select");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Select Efectuado");
  }

}
