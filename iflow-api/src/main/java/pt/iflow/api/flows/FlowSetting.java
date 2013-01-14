package pt.iflow.api.flows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author iKnow
 * @version 1.0
 */

public class FlowSetting {
    
    private int _nFlowId;
    private String _sName;
    private String _sDesc;
    private String _sValue;
    private Date _dtDate;
    private boolean _bIsQuery;
    
    private ArrayList<String> _alValues;
    private boolean _bListSetting;
    private HashSet<Integer> _hsSQLValuesIndex;
    
    public FlowSetting(int anFlowId, String asName, String asDesc) {
        this._nFlowId = anFlowId;
        this._sName = asName;
        this._sDesc = asDesc;
        this._bIsQuery = false;
    }
    
    public FlowSetting(int anFlowId, String asName, String asDesc,
            String asValue, boolean abIsQuery, Date adtDate) {
        this(anFlowId, asName, asDesc);
        this._sValue = asValue;
        this._bIsQuery = abIsQuery;
        this._dtDate = adtDate;
        this._bListSetting = false;
    }
    
    public int getFlowId() {
        return this._nFlowId;
    }
    
    public String getName() {
        return this._sName;
    }
    
    public String getDescription() {
        return this._sDesc;
    }
    
    public String getValue() {
        return this._sValue;
    }
    
    public void setValue(String asValue) {
        this._sValue = asValue;
    }
    
    public String getValue(int anIndex) {
        String retObj = null;
        
        if (this._alValues != null && anIndex < this._alValues.size()) {
            retObj = this._alValues.get(anIndex);
        }
        
        return retObj;
    }
    
    /**
     * Get values without fetching additional values from db. Used mainly to get
     * values to store them in db
     * 
     */
    public String[] getValuesToSave() {
        String[] retObj = new String[0];
        
        if (this._alValues != null) {
            retObj = new String[this._alValues.size()];
            retObj = this._alValues.toArray(retObj);
        }
        
        return retObj;
    }
    
    /**
     * Get values, fetching additional values from db.
     * 
     */
    public String[] getValues(UserInfoInterface userInfo) {
        return this.getValues(userInfo, -1);
    }
    
    /**
     * Get values, fetching additional values from db. Uses process dataset to
     * try to transform query (if applicable)
     * 
     */
    public String[] getValues(UserInfoInterface userInfo, ProcessData adsDataSet) {
        return this.getValues(userInfo, -1, adsDataSet);
    }
    
    /**
     * Get values (db values, since index is given and value list is expected)
     * 
     */
    public String[] getValues(UserInfoInterface userInfo, int anIndex) {
        return this.getValues(userInfo, anIndex, null);
    }
    
    /**
     * Get values (db values, since index is given and value list is expected)
     * Uses process dataset to try to transform query (if applicable)
     * 
     */
    public String[] getValues(UserInfoInterface userInfo, int anIndex, ProcessData adsDataSet) {
        String[] retObj = new String[0];
        
        if (this._alValues != null) {
            ArrayList<String> alAllValues = new ArrayList<String>();
            
            ArrayList<String> alCurrentValues = new ArrayList<String>();
            if (anIndex < 0) {
                // get all
                alCurrentValues = this._alValues;
            } else {
                // get for given index
                alCurrentValues.add(this.getValue(anIndex));
            }
            
            for (int i = 0; i < alCurrentValues.size(); i++) {
                String sValue = alCurrentValues.get(i);
                if (this.isQueryValue(i)) {
                    // fetch values from db
                    alAllValues.addAll(FlowSetting.getQueryValues(userInfo, sValue,
                            adsDataSet));
                } else {
                    alAllValues.add(sValue);
                }
            }
            
            retObj = new String[alAllValues.size()];
            retObj = alAllValues.toArray(retObj);
        }
        
        return retObj;
    }
    
    public Date getDate() {
        return this._dtDate;
    }
    
    public void setValues(ArrayList<String> aalValues) {
        this.setValues(aalValues, null);
    }
    
    public void setValues(ArrayList<String> aalValues,
            ArrayList<Integer> aalSQLValuesIndices) {
        this._alValues = aalValues;
        this._bListSetting = true;
        if (aalSQLValuesIndices != null) {
            this._hsSQLValuesIndex = new HashSet<Integer>(aalSQLValuesIndices);
        }
    }
    
    public boolean isListSetting() {
        return this._bListSetting;
    }
    
    public boolean isQueryValue() {
        return this._bIsQuery;
    }
    
    public boolean isQueryValue(int anIndex) {
        if (this._hsSQLValuesIndex != null) {
            if (this._hsSQLValuesIndex.contains(new Integer(anIndex))) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<String> getQueryValues(UserInfoInterface userInfo, 
        String asQuery,
        ProcessData process) {
      ArrayList<String> retObj = new ArrayList<String>();
        
        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        
        String sQuery = asQuery;
        String sDS = null;
        int ntmp = 0;
        
        try {
            try {
                if (process != null) {
                    sQuery = process.transform(userInfo, sQuery);
                    if (StringUtils.isEmpty(sQuery)) {
                        sQuery = asQuery;
                    }
                }
            } catch (Exception e) {
                sQuery = asQuery;
            }
            
            try {
                if (sQuery.startsWith("{DS=")) {
                    ntmp = sQuery.indexOf("}");
                    sDS = sQuery.substring(4, ntmp);
                    sQuery = sQuery.substring(ntmp + 1);
                }
            } catch (Exception e) {
                sDS = null;
            }
            
            ds = Utils.getUserDataSource(sDS);
            db = ds.getConnection();
            st = db.createStatement();
            rs = null;
            
            rs = st.executeQuery(sQuery);
            while (rs.next()) {
                retObj.add(rs.getString(1));
            }
            rs.close();
            rs = null;
        } catch (Exception ei) {
            Logger.error(null, "FlowSetting", "getQueryValues",
                    "caugh exception for query(" + sQuery + "): "
                            + ei.getMessage());
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }
}
