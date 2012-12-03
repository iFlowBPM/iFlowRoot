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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockDate</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockDate extends Block {
  public Port portIn, portOut;

  // Esta é a versão corrigida
  protected static final String DATA = "data";
  protected static final String DIA = "dia";
  protected static final String MES = "mes";
  protected static final String ANO = "ano";
  protected static final String SEMANA = "semana";
  protected static final String INICIO_SEMANA = "inicio_semana";
  protected static final String FIM_SEMANA = "fim_semana";
  protected static final String NOME_MES = "nome_mes";

  public BlockDate(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[]{portIn};
      return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[]{portOut};
    return retObj;
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

  private static void print (String asString) {
    Logger.debug("ADMIN", "BlockDate", "print", asString);
  }

  protected static void getDateFields(UserInfoInterface userInfo, HashMap<String,String> attrMap, ProcessData procData) {
    String dataVar = (String)attrMap.get(DATA);
    String data = procData.getFormatted(dataVar);
    Locale loc = BeanFactory.getSettingsBean().getOrganizationLocale(userInfo);
    Calendar cal = Calendar.getInstance(loc);

    if(StringUtils.isNotEmpty(data)) {
      try {
        cal.setTime(DateUtility.parseFormDate(userInfo, data));
      }catch (Throwable t) {
      }
    }
    
    // JOAO
    int semana = -1;
    int inicio = 0;
    int fim = 0;
    int mes = cal.get(Calendar.MONTH);
    int ano = cal.get(Calendar.YEAR);
    int dia = cal.get(Calendar.DAY_OF_MONTH);
    String nomeMes = DateUtility.getMonthName(mes, loc);

    Calendar cal1 = (Calendar)cal.clone();
    //Calendar cal2 = (Calendar)cal.clone();

    cal1.set(ano, mes, 1);
    
    // Ultimo dia do mês
    Calendar cal2 = (Calendar) cal1.clone();
    cal2.add(Calendar.MONTH, 1);
    cal2.add(Calendar.DAY_OF_MONTH, -1);
    int lastdayofmonth = cal2.get(Calendar.DAY_OF_MONTH)+1;
//    int lastdayofmonth = 31;

    int[] starts = new int[4];
    int[] ends = new int[4];

    starts[0] = 1;
    switch (cal1.get(Calendar.DAY_OF_WEEK)) {
    case Calendar.SUNDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 6);
        break;
    case Calendar.MONDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 5);
        break;
    case Calendar.TUESDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 11);
        break;
    case Calendar.WEDNESDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 10);
        break;
    case Calendar.THURSDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 9);
        break;
    case Calendar.FRIDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 8);
        break;
    case Calendar.SATURDAY:
        cal1.add(Calendar.DAY_OF_MONTH, 7);
        break;

    }

    ends[0] = cal1.get(Calendar.DAY_OF_MONTH);
    starts[1] = ends[0] + 1;
    ends[1] = starts[1] + 6;
    starts[2] = ends[1] + 1;
    ends[2] = starts[2] + 6;
    if (lastdayofmonth - ends[2] < 7) {
        ends[2] = lastdayofmonth;
        starts[3] = -1;
        ends[3] = -1;
    }
    else {
        starts[3] = ends[2] + 1;
        ends[3] = starts[3] + 6;
        if (lastdayofmonth - ends[3] < 7) {
            ends[3] = lastdayofmonth;
        }           
    }


    for (int i = 0; i < starts.length && i < ends.length ; i++) {
        if (dia < ends[i]) { 
            semana = i+1;
            inicio = starts[i];
            fim = ends[i];
            break;
        }
    }       

    print("starts[0] = " + starts[0] + " : ends[0] = " + ends[0]);
    print("starts[1] = " + starts[1] + " : ends[1] = " + ends[1]);
    print("starts[2] = " + starts[2] + " : ends[2] = " + ends[2]);
    print("starts[3] = " + starts[3] + " : ends[3] = " + ends[3]);

    print("inicio: " + inicio);
    print("fim: " + fim);
    print("semana: " + semana);
    print("mes: " + (mes+1));
    print("ano: " + ano);

    
    // Guardar os resultados da avaliacao da data
    String diaVar = (String)attrMap.get(DIA);
    print("diaVar: " + diaVar);
    String mesVar = (String)attrMap.get(MES);
    print("mesVar: " + mesVar);
    String anoVar = (String)attrMap.get(ANO);
    print("anoVar: " + anoVar);
    String semanaVar = (String)attrMap.get(SEMANA);
    print("semanaVar: " + semanaVar);
    String inico_semanaVar = (String)attrMap.get(INICIO_SEMANA);
    print("inico_semanaVar: " + inico_semanaVar);
    String fim_semanaVar = (String)attrMap.get(FIM_SEMANA);
    print("fim_semanaVar: " + fim_semanaVar);
    String nome_mesVar = (String)attrMap.get(NOME_MES);
    print("nome_mesVar: " + nome_mesVar);
    
    // Obter o nome do mês
    
    
    try { if(null != diaVar && !"".equals(diaVar)) procData.parseAndSet(diaVar, String.valueOf(dia)); } catch (Exception e) {}
    try { if(null != mesVar && !"".equals(mesVar)) procData.parseAndSet(mesVar, String.valueOf(mes+1)); } catch (Exception e) {}
    try { if(null != anoVar && !"".equals(anoVar)) procData.parseAndSet(anoVar, String.valueOf(ano)); } catch (Exception e) {}
    try { if(null != semanaVar && !"".equals(semanaVar)) procData.parseAndSet(semanaVar, String.valueOf(semana)); } catch (Exception e) {}
    try { if(null != inico_semanaVar && !"".equals(inico_semanaVar)) procData.parseAndSet(inico_semanaVar, String.valueOf(inicio)); } catch (Exception e) {}
    try { if(null != fim_semanaVar && !"".equals(fim_semanaVar)) procData.parseAndSet(fim_semanaVar, String.valueOf(fim)); } catch (Exception e) {}
    try { if(null != nome_mesVar && !"".equals(nome_mesVar)) procData.parseAndSet(nome_mesVar, nomeMes); } catch (Exception e) {}

  }
  
  
  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    String login = userInfo.getUtilizador();
    Logger.debug(login,this,"after", "Searching for week of month...");
    
    HashMap<String,String> attrs = getAttributeMap();
    getDateFields(userInfo, attrs, procData);

    return portOut;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Datas");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Datas efectuadas");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
