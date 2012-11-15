package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AcessoFicheiroCores
 *
 *  desc: acesso de leitura e escrita ao ficheiro com
 *        informação de cores
 *
 ****************************************************/


import pt.iknow.XmlColor.XmlColor;
import pt.iknow.XmlColor.XmlColorLibrary;



class AcessoFicheiroCores {
    
/**************************************************
 * Ler descricao de cores a serem utilizados
 */
  public void LerFicheiroCores() {
    XmlColorLibrary colors = FlowEditorConfig.getColors();
    XmlColor[] items=colors.getXmlColor();

    for (int i=0; i<items.length; i++) {
      XmlColor cor=items[i];
      pt.iknow.XmlColor.Rgb xcd=cor.getRgb();

      /* colocar cor na class de cores */
      Cor.getInstance().setCor(cor.getItem().toLowerCase(), new java.awt.Color(xcd.getRed(), xcd.getGreen(), xcd.getBlue()));
    }
  }
    
    
    
    
/*********************************************
 * grava ficheiro com descricao de cores
 */
  public void GravarFicheiroCores() {
    XmlColorLibrary colors=new XmlColorLibrary();
    XmlColor[] cores=new XmlColor[Mesg.CompComCor.length];

    /* recolher informacao */
    for(int i=0;i<Mesg.CompComCor.length;i++) {
      XmlColor cor=new XmlColor();
      cor.setItem(Mesg.CompComCor[i]);
      pt.iknow.XmlColor.Rgb clr=new pt.iknow.XmlColor.Rgb();
      clr.setRed(Cor.getInstance().getCor(Mesg.CompComCor[i]).getRed());
      clr.setGreen(Cor.getInstance().getCor(Mesg.CompComCor[i]).getGreen());
      clr.setBlue(Cor.getInstance().getCor(Mesg.CompComCor[i]).getBlue());
      cor.setRgb(clr);
      cores[i]=cor;
    }            
    colors.setXmlColor(cores);

    /* grava ficheiro */
    FlowEditorConfig.saveColors(colors);
  }
    
    
}
