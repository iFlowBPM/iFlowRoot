package pt.iflow.utils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DocFields {
  public String codigo;
  public String descricao;
  public String data_entrega;
  public String obrigatorio;
  public String fase;
  public boolean entregue;

  public DocFields() {
    codigo        = "";
    descricao     = "";
    data_entrega  = "";
    obrigatorio   = "";
    fase          = "";
    entregue      = false;
  }

  public void entregue() {
    entregue = true;
  }
}