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


public class AtributoImpl implements Atributo {

  private String nome=""; //$NON-NLS-1$
  private String valor=""; //$NON-NLS-1$
  private String descricao=""; //$NON-NLS-1$
  private String[] valoresTipo=null;

  private String dataType;
  private String initValue;
  private boolean isSearchable;
  private String publicName;
  private String format;

  public AtributoImpl() {
  }


  public AtributoImpl(String n, String v, String d, String[] vt) {
    setAtributo(n,v,d,vt);
  }

  public AtributoImpl(Atributo a) {
    nome=a.getNome();
    valor=a.getValor();
    descricao=a.getDescricao();
    dataType = a.getDataType();
    initValue = a.getInitValue();
    isSearchable = a.isSearchable();
    publicName = a.getPublicName();
    format = a.getFormat();

    valoresTipo=a.getValoresTipo();
  }

  public AtributoImpl(String n, String v) {
    nome  = n;
    valor = v;
  }

  public void setAtributo(String n, String v, String d, String[] vt) {
    nome=n;
    valor=v;
    descricao=d;
    valoresTipo=vt;
  }

  public void setAtributo( String v) {
    valor=v;
  }

  public AtributoImpl(String n, String v, String d) {
    nome=n;
    valor=v;
    descricao=d;
  }

  public Atributo cloneAtributo() {
    String[] values = null;

    if(this.valoresTipo != null && this.valoresTipo.length > 0) {
      values = new String[this.valoresTipo.length];
      for(int i=0;i<this.valoresTipo.length;i++)
        values[i] = this.valoresTipo[i];
    }

    Atributo a = new AtributoImpl(this.nome,this.valor,this.descricao,values);
    a.setDataType(getDataType());
    a.setInitValue(getInitValue());
    a.setSearchable(isSearchable());
    a.setPublicName(getPublicName());
    return a;

  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getValor() {
    return valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String[] getValoresTipo() {
    return valoresTipo;
  }

  public void setValoresTipo(String[] valoresTipo) {
    this.valoresTipo = valoresTipo;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getInitValue() {
    return initValue;
  }

  public void setInitValue(String initValue) {
    this.initValue = initValue;
  }

  public boolean isSearchable() {
    return isSearchable;
  }

  public void setSearchable(boolean isSearchable) {
    this.isSearchable = isSearchable;
  }

  public String getPublicName() {
    return publicName;
  }

  public void setPublicName(String publicName) {
    this.publicName = publicName;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Atributo nome=").append(nome).append(" descricao=").append(descricao).append(" valor=").append(valor);
    return sb.toString();
  }

}
