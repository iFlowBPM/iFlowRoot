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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.operations.DataOperation;
import pt.iflow.blocks.operations.OperationArrayTrim;
import pt.iflow.blocks.operations.OperationDiv;
import pt.iflow.blocks.operations.OperationInit;
import pt.iflow.blocks.operations.OperationMul;
import pt.iflow.blocks.operations.OperationSub;
import pt.iflow.blocks.operations.OperationSum;

public class BlockDataProcessing extends Block {
  // {0}=pos, {1}=code, {2}=fieldname
  private static final String PROP_RE = "(\\d+)-(\\w+)-(\\w+)";
  private static final Map<String,Class<? extends DataOperation>> OPERATION_MAPPING = new HashMap<String,Class<? extends DataOperation>>();
  static {
    // TODO indicar onde se encontra a classe de forma mais elegante
    OPERATION_MAPPING.put("add", OperationSum.class);
    OPERATION_MAPPING.put("sub", OperationSub.class);
    OPERATION_MAPPING.put("mul", OperationMul.class);
    OPERATION_MAPPING.put("div", OperationDiv.class);
    OPERATION_MAPPING.put("trim", OperationArrayTrim.class);
    OPERATION_MAPPING.put("init", OperationInit.class);
  }
  
  public Port portIn, portOut, portError;
  private List<DataOperation> operationList;
  
  public BlockDataProcessing(int a, int b, int c, java.lang.String d) {
    super(a,b,c,d);
    this.hasInteraction = false;
    this.hasEvent = false;
    this.isCodeGenerator = true;
    this.operationList = null;
  }

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port output = portOut;
    init(userInfo);
    if(null == operationList || operationList.isEmpty()) return output;
    try {
      Iterator<DataOperation> opIter = operationList.iterator();
      while(opIter.hasNext()) {
        DataOperation op = opIter.next();
        op.execute(userInfo, procData);
      }
    } catch (Throwable t) {
      // capture exception...
      Logger.error(userInfo.getUtilizador(), this, "after", "Error evaluating operations: "+t.getMessage(), t);
      output = portError;
    }

    return output;
  }

  private synchronized void init(UserInfoInterface userInfo) {
    if(null != operationList) return;
    // agrupar por tipo de dados
    Pattern p = Pattern.compile(PROP_RE);
    Map<Integer, OpAux> data = new TreeMap<Integer, OpAux>(); // auto sort :-)
    Map<String,String> atributos = getAttributeMap();
    Iterator<String> attrIter = atributos.keySet().iterator();
    while(attrIter.hasNext()) {
      String nome = attrIter.next();
      if(nome == null) continue;
      Matcher m = p.matcher(nome);
      if(!m.matches()) continue;
      String sPos = m.group(1);
      String sCode = m.group(2);
      String sField = m.group(3);

      Integer ii = new Integer(sPos);
      OpAux op = data.get(ii);
      if(null == op) {
        op = new OpAux();
        op.code = sCode;
        data.put(ii,op);
      }
      op.params.put(sField, atributos.get(nome));
    }

    ArrayList<DataOperation> opList = new ArrayList<DataOperation>(data.size());
    Iterator<Integer> opIter = data.keySet().iterator();
    while(opIter.hasNext()) {
      OpAux opAux = data.get(opIter.next());
      Class<? extends DataOperation> cc = OPERATION_MAPPING.get(opAux.code);
      if(null == cc) continue;
      try {
        DataOperation op = cc.newInstance();
        op.setup(userInfo, opAux.params);
        opList.add(op);
      } catch (Exception e) {
      }
    }
    this.operationList = Collections.unmodifiableList(opList);
    
    data = null; // invite GC
  }
  
  
  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "DataProcessing");
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    return new Port[]{portIn};
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    return new Port[]{portOut,portError};
  }


  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "DataProcessing Done");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private static final class OpAux {
    String code;
    Map<String,String> params = new HashMap<String,String>();
  }
  
}
