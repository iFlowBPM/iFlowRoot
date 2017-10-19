package pt.iknow.floweditor;

import java.awt.Color;
import java.math.BigInteger;
import java.util.List;

import pt.iflow.editor.xml.codegen.color.XmlColorDescription;
import pt.iflow.editor.xml.codegen.color.XmlColorLibrary;
import pt.iflow.editor.xml.codegen.color.XmlColorType;

/*****************************************************
 *
 * Project FLOW EDITOR
 *
 * class: AcessoFicheiroCores
 *
 * desc: acesso de leitura e escrita ao ficheiro com informação de cores
 *
 ****************************************************/

// import pt.iknow.XmlColor.XmlColor;
// import pt.iknow.XmlColor.XmlColorLibrary;

class AcessoFicheiroCores {

	/**************************************************
	 * Ler descricao de cores a serem utilizados
	 */
	public void LerFicheiroCores() {
		XmlColorLibrary colors = FlowEditorConfig.getColors();
		List<XmlColorType> items = colors.getXmlColor();

		for (int i = 0; i < items.size(); i++) {
			XmlColorType cor = items.get(i);
			XmlColorDescription xcd = cor.getRgb();

			/* colocar cor na class de cores */
			Cor.getInstance().setCor(cor.getItem().toLowerCase(),
					new Color(xcd.getRed().intValue(), xcd.getGreen().intValue(), xcd.getBlue().intValue()));
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
			clr.setRed( BigInteger.valueOf(Cor.getInstance().getCor(Mesg.CompComCor[i]).getRed()) );
			clr.setGreen( BigInteger.valueOf(Cor.getInstance().getCor(Mesg.CompComCor[i]).getGreen()) );
			clr.setBlue( BigInteger.valueOf(Cor.getInstance().getCor(Mesg.CompComCor[i]).getBlue()) );
			cor.setRgb(clr);
			cores[i] = cor;
		}
		colors.withXmlColor(cores);

		/* grava ficheiro */
		FlowEditorConfig.saveColors(colors);
	}

}
