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
package pt.iflow.chart;

import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.ChartData;
import pt.iknow.chart.ChartItem;

public class DataConverter {
  public static final String PROPERTIES = "PROPERTIES";
  
  
  protected static ChartData convertPieDataSource(ChartCtx ctx, Object dataSource) {
    if(null == dataSource) return null;
    UserInfoInterface userInfo = (UserInfoInterface) ctx.get(Const.USER_INFO);
    ProcessData procData = (ProcessData) ctx.get(Const.SESSION_PROCESS);
    Properties request = (Properties) ctx.get(PROPERTIES);
    
    String labelArray = request.getProperty("dsl");
    String valueArray = request.getProperty("dsv");
    
    ChartData data = new ChartData();
    if(null == procData) return null;
    ProcessListVariable labelList = procData.getList(labelArray);
    ProcessListVariable valueList = procData.getList(valueArray);
    
    if(labelList == null || valueList==null) return null;
    
    int labelCount = labelList.size();
    int valueCount = valueList.size();
    
    if(labelCount != valueCount) return null;
    
    for(int i = 0; i < labelCount; i++) {
      String lbl = labelList.getFormattedItem(i);
      Number val = (Number) valueList.getItemValue(i);
      if(null == lbl) lbl = "";
      if(null == val) val = new Double(Double.NaN);
      
      data.addChartItem(new ChartItem(lbl, val.doubleValue()));
    }
    
    return data;
  }
  
  protected static ChartData convertBarDataSource(ChartCtx ctx, Object dataSource) {
    UserInfoInterface userInfo = (UserInfoInterface) ctx.get(Const.USER_INFO);
    ProcessData procData = (ProcessData) ctx.get(Const.SESSION_PROCESS);
    Properties request = (Properties) ctx.get(PROPERTIES);
    
    String dsl = request.getProperty("dsl");
    String [] adsl = dsl.split(",");
    String seriesDesc = adsl[0];
    String seriesLabelName = adsl[1];
    String seriesValueName = request.getProperty("dsv");
    Logger.debug("chartGenerator", "uf", "convertDataSource", "partes: seriesDesc="+seriesDesc+"; seriesLabelName="+seriesLabelName+"; seriesValueName="+seriesValueName);
    
    ChartData data = new ChartData();
    ProcessListVariable labelVar, valueVar, serieVar, valVar, labVar;
    labelVar = procData.getList(seriesLabelName);
    valueVar = procData.getList(seriesValueName);
    serieVar = procData.getList(seriesDesc);
    
    int labelCount = labelVar.size();
    int valueCount = valueVar.size();
    int serieCount = serieVar.size();
    Logger.debug("chartGenerator", "uf", "convertDataSource", "partes: seriesDesc="+serieCount+"; seriesLabelName="+labelCount+"; seriesValueName="+valueCount);
    
    if(labelCount != valueCount && labelCount != serieCount) return null;
    
    for(int i = 0; i < serieCount; i++) {
      String serie = serieVar.getFormattedItem(i);
      String labels = labelVar.getFormattedItem(i);
      String values = valueVar.getFormattedItem(i);
      Logger.debug("chartGenerator", "uf", "convertDataSource", "Series: "+serie+"; labels="+labels+"; values="+values);
      
      valVar = procData.getList(values);
      labVar = procData.getList(labels);
      
      int valCount = 0;
      if(valVar != null)
        valCount = valVar.size();
      int labCount = 0;
      if(labVar != null)
        labCount = labVar.size();
      
      Logger.debug("chartGenerator", "uf", "convertDataSource", "valCount: "+valCount + "; labCount: "+labCount);

      if(valCount != labCount) continue;
      
      for(int j = 0; j < valCount; j++) {
        String l = labVar.getFormattedItem(j);
        Number v = (Number) valVar.getItemValue(j);
        if(null == l) l = "";
        if(null == v) v = new Double(Double.NaN);
        
        Logger.debug("chartGenerator", "uf", "convertDataSource", "Label: "+l+"; Value: "+v);
        data.addChartItem(new ChartItem(l, serie, v.doubleValue()));
      }
    }
    
    
    return data;
  }


}
