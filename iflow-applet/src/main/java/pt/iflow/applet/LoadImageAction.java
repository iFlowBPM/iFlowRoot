package pt.iflow.applet;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JCheckBox;
import javax.swing.JLabel;


public class LoadImageAction implements ActionListener {

  public static String sALLOW = "ALLOW";
  public static String sYES = "YES"; 
  public static String sNO = "NO";
  public static String sPREVIEW = "PREVIEW";
  public static String sMATRIX = "MATRIX";  
  public static String sSIGNATURE = "SIGNATURE";
  public static String sRUBRIC = "RUBRIC";
	
  private final FileDialog janelafich;
  private final Container cont;
  
  private static com.lowagie.text.Image imagePDF;       //imagem utilizada para assinar PDF 
  private static ImageIconRep imageBD;					//imagem guardada na BD 
  private static ImageIconRep imageApplet;				//sample da imagem mostrada na applet
  private static ImageIconRep imagePosicao;
  private static com.lowagie.text.Image rubricPDF;
  
  private static JLabel label;
  private static JCheckBox jcbSave;
  private static JCheckBox jcbUtil;
  private static JCheckBox jcbRub;
  private static Boolean flagModificada = false;

  private static int assActual = 1;
  private static int pagToSign = -1;
  
  //Parametros de configuração da applet (CMA)
  public static boolean posMatriz = true; 				// if (true) "MATRIZ" else "COORDENADAS"
  private static int flagJava = -1;
  
  //Posição por MATRIZ
  private static int numColunas = 3;
  private static int iniX = 70;
  private static int iniY = 110;
  
  //Posição da Rubrica
  public static boolean posRubSame = true;

  //Imagem da Rubrica igual à da Matriz
  public static boolean rubimgSameass = true;
  
  //Outros
  private static int tamW = 150;
  private static int tamH = 40;
  private static int tamPag = 841;
  private static int acertoY = 25;

  
  public LoadImageAction(FileDialog fd, Container aux, JCheckBox jcbUtil, JCheckBox jcbSave, JCheckBox jcbRub) {
	  this.janelafich = fd;
	  this.cont = aux;
	  LoadImageAction.label = new JLabel();
	  label.setPreferredSize(new Dimension(tamW, tamH));
	  this.cont.add(label, BorderLayout.CENTER);
	  LoadImageAction.jcbUtil = jcbUtil;
	  LoadImageAction.jcbSave = jcbSave;
	  LoadImageAction.jcbRub = jcbRub;
  }
  public static void setSignature_position_style(String signature_position_style){
    if(StringUtils.isEmpty(signature_position_style)){
      flagJava = -1;
      iniX = 70;
      iniY = 110;
    }else{
        if(signature_position_style.equals(sMATRIX))
            posMatriz = true;
        else
            posMatriz = false;
        flagJava = 1;
    }
  }
  public static void setSignatureParameters(String signature_position_style, String rubric_image, String rubric_position_style){
	  
	  //Verificar se a posição da assinatura é dada por matriz ou por coordenadas
    if(flagJava == -1){
	  if(signature_position_style.equals(sMATRIX))
		  posMatriz = true;
	  else
		  posMatriz = false;
    }
	  //Verificar se a imagem da rubrica é igual a da assinatura
	  if(rubric_image.equals(sSIGNATURE))
		  rubimgSameass = true;
	  else
		  rubimgSameass = false;
	  
	  //Verificar se a posição da rubrica é a mesma da assinatura
	  if(rubric_position_style.equals(sMATRIX))
		  posRubSame = true;
	  else
		  posRubSame = false;
  }

  	public void actionPerformed(ActionEvent e) {
	  janelafich.setDirectory(".\\");
	  janelafich.setVisible(true);
	  
	  if(janelafich.getFile() != null){
		  String path = janelafich.getDirectory()+janelafich.getFile(); 
		  readImageFromFile(path);
		  }
  	}

  	//Ler imagens
	private void readImageFromFile(String imagePath) {
		File file = new File(imagePath); 
		Image original = null;
		ImageIconRep o = null;
		try {
			InputStream is = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int)length];

		    int offset = 0;
		    int numRead = 0;
		    while (offset < bytes.length
		           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		        offset += numRead;
		    }
		    is.close();

		    o = new ImageIconRep(bytes);
		    original = o.getImage();
			} 
		catch (IOException e) {e.printStackTrace();} 

		//criar imagens
		createImageApplet(original);
		createImageBD(original);
		createAssImagePDF(o.getImage()); 

		//Actualizar Componentes 
		actualizarComponentes(true);
		flagModificada = true;
		
	    //Reload jFrame
		cont.getParent().setVisible(false);
		cont.getParent().setVisible(true);	
	}

	public static void setImageFromBD(ImageIconRep img){	
		imageBD = null; 					//meter imagemBD = null porque nao vamos gravar outra x a mesma
		createAssImagePDF (img.getImage()); //converte para image o icon recebido para o PDf
 		createImageApplet(img.getImage());  //resize para a imagem do icon recebido
 		
 		//Actualizar Componentes		
 		actualizarComponentes(false);
		flagModificada = false;	
	}
	
	public static void setRubricImageFromBD(ImageIconRep rubImg){	
		Image img = rubImg.getImage();
		ImageIconRep aux =  new ImageIconRep(img.getScaledInstance(tamW, -1, Image.SCALE_SMOOTH));

		if(aux.getIconHeight() > tamH)
 			aux = new ImageIconRep(img.getScaledInstance(-1, tamH, Image.SCALE_SMOOTH));

		try {
			rubricPDF =  com.lowagie.text.Image.getInstance(aux.getImage(), null);
		} catch (Exception e) {	
			rubricPDF = null; 
			e.printStackTrace();
		}
	}
	
	private static void actualizarComponentes(boolean tosave){
		label.setIcon(imageApplet);
		jcbUtil.setEnabled(true);
		jcbUtil.setSelected(true);
		jcbSave.setEnabled(tosave);
		jcbSave.setSelected(tosave);
		jcbRub.setEnabled(true);
	}
	
	//Metodos para criacao das imagens necessarias
	public static void createImageApplet(Image img){ 
		imageApplet = new ImageIconRep(img.getScaledInstance(tamW, -1, Image.SCALE_SMOOTH));
 		if(imageApplet.getIconHeight() > tamH){ 
 			imageApplet = new ImageIconRep(img.getScaledInstance(-1, tamH, Image.SCALE_SMOOTH));
 		}
	}	
	
	public static void createImageBD(Image img){ 
		imageBD = new ImageIconRep(img.getScaledInstance(tamW, -1, Image.SCALE_SMOOTH));
 		if(imageBD.getIconHeight() > tamH){ 
 			imageBD = new ImageIconRep(img.getScaledInstance(-1, tamH, Image.SCALE_SMOOTH));
 		}
	}
	
	public static void createAssImagePDF(Image img){
		
		ImageIconRep aux =  new ImageIconRep(img.getScaledInstance(tamW, -1, Image.SCALE_SMOOTH));

		if(aux.getIconHeight() > tamH)
 			aux = new ImageIconRep(img.getScaledInstance(-1, tamH, Image.SCALE_SMOOTH));

		try {
			imagePosicao = new ImageIconRep(img.getScaledInstance(aux.getIconWidth()/2, -1, Image.SCALE_SMOOTH));
			imagePDF =  com.lowagie.text.Image.getInstance(aux.getImage(), null);
		} catch (Exception e) {	
			imagePDF = null; 
			e.printStackTrace();
		}
	}		
	

	//Metodos de consulta externa
	public static Boolean getFlagGravar(){   
		if(flagModificada && jcbSave.isSelected() && imageBD != null)
			return true;
		else
			return false;
	}
	
	public static ImageIconRep getImageBD(){   
		return imageBD;
	}

	public static ImageIconRep getImagePosicao(){   
		return imagePosicao;
	}
	
	public static Boolean getFlagPDF(){   
		if(jcbUtil.isSelected() && imagePDF != null)
			return true;
		else
			return false;
	}
	
	public static Boolean getUseImageForSignature(){   
		return jcbUtil.isSelected();
	}
	
	public static com.lowagie.text.Image getAssImagePDF(){   
		return imagePDF;
	}
 	
	public static com.lowagie.text.Image getRubImagePDF(){   
		return rubricPDF;
	}
	
	public static int[] getImageXY(){
		 
		 //calcular qual a linha e coluna onde vai ficar (comeca em zero)
		 int linhaActual = ((int) Math.ceil((double)assActual/ (double)numColunas) ) - 1;   
		 int colunaActual = ( assActual  - (linhaActual * numColunas)) - 1;    				
		 
		 //calcular X e Y
		 int [] novas = {iniX + (tamW * colunaActual), iniY + (tamH * linhaActual)};
		 
		 return novas;
	}
	
	public static void setNumAss(int existentes)
	{
		if(existentes>=-1)
			assActual = existentes + 1;
		else
			assActual = 1;
	}
	
	public static int getNumAss(){
		return assActual;
	}
	
	public static boolean getFlagRub(){
		if(jcbRub != null && jcbRub.isSelected())
			return true;
		else
			return false;
	}
	
	public static void setAssPos(int x, int y){
		iniX = x*2;
		iniY = tamPag-(y*2)-(acertoY);
	}
	 
	public static int getAssX(){
		return iniX;
	}
	public static int getAssY(){
		return iniY;
	}
	
	public static void setPagToSign(int pag){
		pagToSign = pag;
	}
	public static int getPagToSign(){
		return pagToSign;
	}
	
}










