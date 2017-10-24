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

import pt.iknow.XmlColor2.XmlColorType;

import java.awt.Color;
import java.util.List;

import pt.iknow.XmlColor2.XmlColorDescription;
import pt.iknow.XmlColor2.XmlColorLibrary;

class AcessoFicheiroCores {

	/**************************************************
	 * Ler descricao de cores a serem utilizados
	 */
	public void LerFicheiroCores() {
		XmlColorLibrary colors = FlowEditorConfig.getColors();
		List<XmlColorType> items = colors.getXmlColor();

		for( XmlColorType cor : items ) 
		{
			XmlColorDescription xcd = cor.getRgb();

			/* colocar cor na class de cores */
			Cor.getInstance().setCor(cor.getItem().toLowerCase(),new Color(xcd.getRed(), xcd.getGreen(), xcd.getBlue()));
		}
	}

	/*********************************************
	 * grava ficheiro com descricao de cores
	 */
	public void GravarFicheiroCores() {
		XmlColorLibrary colors = new XmlColorLibrary();
		XmlColorType[] cores = new XmlColorType[Mesg.CompComCor.length];

		/* recolher informacao */
		for (int i = 0; i < Mesg.CompComCor.length; i++) {
			XmlColorType cor = new XmlColorType();
			cor.setItem(Mesg.CompComCor[i]);
			XmlColorDescription clr = new XmlColorDescription();
			clr.setRed(Cor.getInstance().getCor(Mesg.CompComCor[i]).getRed());
			clr.setGreen(Cor.getInstance().getCor(Mesg.CompComCor[i]).getGreen());
			clr.setBlue(Cor.getInstance().getCor(Mesg.CompComCor[i]).getBlue());
			cor.setRgb(clr);
			cores[i] = cor;
		}
		colors.withXmlColor(cores);

		/* grava ficheiro */
		FlowEditorConfig.saveColors(colors);
	}

}
