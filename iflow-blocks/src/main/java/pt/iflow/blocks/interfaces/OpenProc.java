package pt.iflow.blocks.interfaces;


/**
 * Constants shared between BlockOpenProc and AlteraAtributosOpenProc
 * 
 * @author ombl
 *
 */
public interface OpenProc {

  public static final String PASS_THRU = "passThru"; //$NON-NLS-1$
  
  public static final String JUMP_TO = "jumpTo"; //$NON-NLS-1$
  
  public static final String[] openProcessModes = {PASS_THRU, JUMP_TO};

  
  // check with editor pt.iknow.iflow.iflow.editor.blocks.AlteraAtributosOpenProc
  public static final String[] openProcessTypes = {"Importar Existente", "Abrir Novo"};
  public static final String[] userModes = {"Criador","Perfil","PerfilTexto","Utilizador"};
  public static final String VAR_SEPARATOR=",";

}
