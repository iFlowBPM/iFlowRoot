package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Atributo
 *
 *  desc: atributo de um bloco
 *
 ****************************************************/


public interface Atributo {
  public void setAtributo(String n, String v, String d, String[] vt);

  public void setAtributo( String v);

  public Atributo cloneAtributo();
  
  public String getNome();

  public void setNome(String nome);

  public String getValor();

  public void setValor(String valor);

  public String getDescricao();

  public void setDescricao(String descricao);

  public String[] getValoresTipo();

  public void setValoresTipo(String[] valoresTipo);

  public String getDataType();

  public void setDataType(String dataType);

  public String getInitValue();

  public void setInitValue(String initValue);

  public boolean isSearchable();

  public void setSearchable(boolean isSearchable);

  public String getPublicName();

  public void setPublicName(String publicName);

  public String getFormat();

  public void setFormat(String format);

}
