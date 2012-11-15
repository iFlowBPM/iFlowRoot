package pt.iflow.blocks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>
 * Title: BlockValidacoes
 * </p>
 * <p>
 * Agora a explicação de como isto funciona:<br>
 * <br>
 * No editor existe uma tabela com 3 colunas:<br>
 *  - alVars: Variaveis que vão ser limpas<br>
 *  - alTestVars: Variaveis de controlo/teste<br>
 *  - sControlo: Valor de controlo<br>
 * <br>
 * Cada linha da tabela corresponde a uma operação de limpeza de listas.<br>
 * <br>
 * Uma operação de limpeza processa-se da seguinte forma:<br>
 *  - Percorrem-se todas as variáveis configuradas em alTestVars e determinam-se 
 *  que posições se devem manter, de acordo com o valor de controlo (actualmente 
 *  apenas verifica se as posições não estão vazias)<br>
 *  - Percorrem-se todas as variáveis em alVars e para cada uma delas eliminam-se
 *  as posições que não são para manter.<br>
 *  <br>
 *  Aplicações práticas:<br>
 *  Remover todas as posições vazias de um array:<br>
 *  - Configurar alVars e alTestVars apenas com o nome do array a limpar.<br>
 *  <br>
 *  Filtrar dados:<br>
 *  - Criar um array em que as posições a remover estão vazias;<br>
 *  - Configurar alVars com as variaveis a serem filtradas<br>
 *  - Configurar alTestVars com o array "filtro"
 * 
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class BlockArrayTrim extends Block {

  public Port portIn, portOut, portError;

  private static final String VARS_TEST = "dest";

  private static final String VARS_TRIM = "orig";

  private static final String VAR_CONTROLO = "ctrl";

  public BlockArrayTrim(int anFlowId, int id,
      int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portOut;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portOut;

    String login = userInfo.getUtilizador();

    Logger.debug(login, this, "after", "Entrou no after");

    try {
      String sTest = null;
      String sVars = null;
      String sControlo = null;

      for (int i = 0; (sTest = this.getAttribute(VARS_TEST + i)) != null; i++) {
        sVars = this.getAttribute(VARS_TRIM + i);
        sControlo = this.getAttribute(VAR_CONTROLO + i);
        
        List<String> testTokens = Utils.tokenize(sTest.trim(), ",");
        List<String> varTokens = Utils.tokenize(sVars.trim(), ",");
        
        Set<String> alTestVars = new HashSet<String>();
        if(null != testTokens) alTestVars.addAll(testTokens);
        Set<String> alVars = new HashSet<String>();
        if(null != varTokens) alVars.addAll(varTokens);

        if (alTestVars == null || alTestVars.size() == 0) {
          Logger.debug(login, this, "after", "Vai apagar tudo");
          destroyAllVars(login, procData, alVars);
        } else {
          Logger.debug(login, this, "after", "Apagar de acordo com o teste");
          removeIndexes(login, procData, alTestVars, alVars, sControlo);
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    return outPort;
  }
  
  protected Set<Integer> getIndicesToKeep(String login, ProcessData process, Set<String> testVars, String control) {

    // The algorithm bellow requires a sorted map!
    Set<Integer> result = new HashSet<Integer>();

    // add all positions with content for vars to test
    Iterator<String> iter = testVars.iterator();
    while(iter.hasNext()) {
      String sTestVar = iter.next();
      
      ProcessListVariable var = process.getList(sTestVar);
      if(null == var) continue; // does not exist, so there is nothing to trim...
      
      for(int i = 0; i < var.size(); i++) {
        ProcessListItem item = var.getItem(i);
        // TODO test item
        if(item != null) result.add(i);
      }
    }

    return result;
  }

  protected void destroyIndices(String login, ProcessData process, Set<String> alVars, Set<Integer> indicesToKeep) {

    // now trim all vars
    Iterator<String> varIter = alVars.iterator();
    while(varIter.hasNext()) {
      String sVar = varIter.next();

      ProcessListVariable var = process.getList(sVar);
      if (var == null) {
        Logger.warning(login, this, "removeIndexes", "List '" + sVar + "' does not exist");
        continue;
      }

      ProcessListVariable newVar = new ProcessListVariable(var.getType(), var.getName());
      
      for(int i = 0; i < var.size(); i++) {
        if(!indicesToKeep.contains(i)) continue;
        ProcessListItem item = var.getItem(i);
        if(item == null)
          newVar.addNewItem(null);
        else
          newVar.addNewItem(item.getValue());
      }
    }
  }

  protected void removeIndexes(String login, ProcessData process, Set<String> alTestVars, Set<String> alVars, String control) {
    Set<Integer> indicesToKeep = getIndicesToKeep(login, process, alTestVars, control);
    destroyIndices(login, process, alVars, indicesToKeep);
  }

  protected void destroyAllVars(String login, ProcessData process, Set<String> alVars) {
    Logger.debug(login, this, "destroyAllVars", "Chegou ao destroyAllVars");
    if (alVars == null)
      return;

    Iterator<String> iter = alVars.iterator();
    while (iter.hasNext()) {
      String var = iter.next();
      process.setList(new ProcessListVariable(process.getVariableDataType(var), var));
      Logger.debug(login, this, "destroyAllVars", "Var " + var + " removed");
    }

    Logger.debug(login, this, "destroyAllVars", "Saiu do destroyAllVars");
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "ArrayTrim");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "ArrayTrim efectuado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
