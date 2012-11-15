package pt.iflow.blocks;

import java.util.Map;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Parametrização do formulário de detalhe de processo no backoffice do iFlow. 
 * 
 * @author oscar
 *
 */
public class BlockDetailForm extends BlockFormulario {

  public BlockDetailForm(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    nTYPE = nTYPE_DETALHE;
    sJSP = "detail.jsp";// force form JSP
  }
  
  
  public Object execute(int op, Object[]aoa) {
    Object result = null;
    if(op == 2) {
      // override default "generateForm"
      // String generateForm(UserInfo,ProcessData,HashMap)
      result = generateForm((BlockFormulario)this,
          (UserInfoInterface)aoa[0],
          (ProcessData)aoa[1],
          (Map<String,String>)aoa[2], 
          true, // force disabled
          (ServletUtils) aoa[3]);
    } else {
      result = super.execute(op, aoa);
    }
    return result;
  }

}
