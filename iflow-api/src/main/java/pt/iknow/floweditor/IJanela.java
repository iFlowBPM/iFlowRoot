package pt.iknow.floweditor;

import java.awt.Image;

public interface IJanela {

  /*****************************************************************************
   * Cria uma nova imagem, recebendo o nome do ficheiro
   */
  public Image createImage(String nomeficheiro, boolean isBiblioteca);

}
