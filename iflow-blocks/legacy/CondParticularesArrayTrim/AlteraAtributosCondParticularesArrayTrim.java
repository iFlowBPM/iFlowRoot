package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar validações </p></p>
 * <p>  condição | mensagem de erro
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

import javax.swing.JFrame;

import pt.iknow.floweditor.messages.Messages;

public class AlteraAtributosCondParticularesArrayTrim extends AlteraAtributosArrayTrim implements
AlteraAtributosInterface {

  private static final long serialVersionUID = 3725359039127895414L;

  //  private static final String[] columnNames = { "VarsTeste", "VarsTrim", "VarControlo" };
  //
  //  protected String[] getColumnNames() {
  //    return columnNames;
  //  }

  public AlteraAtributosCondParticularesArrayTrim(JFrame janela) {
    super(janela, Messages.getString("AlteraAtributosCondParticularesArrayTrim.title"), true);
  }
}
