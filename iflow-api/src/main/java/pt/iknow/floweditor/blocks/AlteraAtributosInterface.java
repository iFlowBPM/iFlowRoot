package pt.iknow.floweditor.blocks;

import java.util.List;

import pt.iknow.floweditor.Atributo;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: AlteraAtributoInterface
 * 
 * desc: interface para dialogo para alterar atributos de blocos
 * 
 ******************************************************************************/

public interface AlteraAtributosInterface {

  public static final int EXIT_STATUS_OK = 0;
  public static final int EXIT_STATUS_CANCEL = 1;

  public void setDataIn(String title, List<Atributo> atributos);

  public int getExitStatus();

  public String[][] getNewAttributes();

}
