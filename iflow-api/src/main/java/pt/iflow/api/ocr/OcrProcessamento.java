package pt.iflow.api.ocr;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

import pt.iflow.api.utils.Logger;


public class OcrProcessamento {
	
private static RenderedImage shift(RenderedImage img, float x, float y) {  
	
	if(x==0 && y==0) return img;
	
	ParameterBlock pb = new ParameterBlock();
	pb.addSource(img); 
	pb.add((int)java.lang.Math.abs(x));  
	pb.add((int)java.lang.Math.abs(x));
	pb.add((int)java.lang.Math.abs(y));  
	pb.add((int)java.lang.Math.abs(y));
	double[] cons = {0.0};
    pb.add(new BorderExtenderConstant(cons));
	RenderedImage temp = JAI.create("Border", pb); 
	
	return crop(temp, -x, -y, img.getWidth(), img.getHeight());
}

private static RenderedImage crop(RenderedImage img, float topLeftX, float topLeftY, float roiWidth, float roiHeight) {  
    ParameterBlock pb = new ParameterBlock();
	pb.addSource(img); 
	pb.add(topLeftX);
	pb.add(topLeftY);
	pb.add(roiWidth);
	pb.add(roiHeight);
	return JAI.create("crop", pb);
}

private static RenderedImage rotate(RenderedImage img, float angle) {  
	float centerX = (float)img.getWidth() / 2;
	float centerY = (float)img.getHeight() / 2;	
	float degree = (float)(angle * (Math.PI/180.0F));
	ParameterBlock pb = new ParameterBlock();
	pb.addSource(img);
	pb.add(centerX);
	pb.add(centerY);
	pb.add(degree);
	RenderedImage tmp = JAI.create("rotate", pb);
	return crop(tmp,0, 0, img.getWidth(), img.getHeight());
}


private static RenderedImage erode(RenderedImage img, int nkernel) {  

	KernelJAI kernel3 = new KernelJAI(3, 3, new float[]
	                                                  { 0, 1, 0,
														1, 1, 1, 
														0, 1, 0 });
	KernelJAI kernel5 = new KernelJAI(5, 5, new float[]
	                                                  { 0, 0, 1, 0, 0,
														0, 0, 1, 0, 0,
														1, 1, 1, 1, 1,
														0, 0, 1, 0, 0,
														0, 0, 1, 0, 0});
	KernelJAI kernel7 = new KernelJAI(7, 7, new float[]
	                                                  { 0, 0, 0, 1, 0, 0, 0,
														0, 0, 0, 1, 0, 0, 0,
														0, 0, 0, 1, 0, 0, 0,
														1, 1, 1, 1, 1, 1, 1,
														0, 0, 0, 1, 0, 0, 0,
														0, 0, 0, 1, 0, 0, 0,
														0, 0, 0, 1, 0, 0, 0 });
	
	ParameterBlock p = new ParameterBlock();
	p.addSource(img);
	if(nkernel == 5) {
		p.add(kernel5);
	}
	else {
		p.add(kernel3);
	}
	return JAI.create("Erode",p);
}

	

private static PlanarImage binarize(RenderedImage imageToBinarize, double threshold) {

	int numBands = imageToBinarize.getSampleModel().getNumBands();
    
	//convert to grayscale
	if (numBands >= 3) {
		ParameterBlock bandCombineParams = new ParameterBlock();
		bandCombineParams.addSource(imageToBinarize);
		if (numBands == 3) {
			bandCombineParams.add( new double[][]{ {0.2,0.3,0.4,0.5} } );
		}
		else {
			bandCombineParams.add( new double[][]{ {0.1,0.2,0.3,0.4,0.5} } );
		}
		imageToBinarize = JAI.create("bandcombine", bandCombineParams, null);
    

		//binarize
		ParameterBlock binarizeParams = new ParameterBlock();
		binarizeParams.addSource( imageToBinarize  );
		binarizeParams.add( threshold );
		return JAI.create("binarize", binarizeParams);
	}
	return null;
}

private static RenderedImage scale(RenderedImage img){
	float factor = 0.1F;
	ParameterBlock pb = new ParameterBlock();
	pb.addSource(img); 
	pb.add(factor);          
	pb.add(factor);  
	pb.add(0.0F);          
	pb.add(0.0F);  
	return JAI.create("scale", pb, null);
}


private static RenderedImage readImage(String filename){
	return JAI.create("fileload", filename);
}


private static void saveImage(RenderedImage img, String path) {
	File file = new File(path + File.separator);
	try {
		ImageIO.write(img,"PNG", file);
	} catch (IOException e) {e.printStackTrace();}
}


private static int subtract(RenderedImage modelo, RenderedImage recebida){
	Raster rst = recebida.getData();
	Raster rst2 = modelo.getData();

	int sum = 0;

	for(int i = 0; i<rst.getHeight(); i++){
		for(int j = 0; j<rst.getWidth(); j++){
			if(rst.getSample(j+rst.getSampleModelTranslateX(), i+rst.getSampleModelTranslateY(), 0) != rst2.getSample(j+rst2.getSampleModelTranslateX(), i+rst2.getSampleModelTranslateY(), 0))
				sum = sum + 1;
		}
	}
	return sum;
}


public static String[] cropImage(RenderedImage img, float[][] pontos, String pasta, boolean bBinarize, boolean bErode){
	RenderedImage temp = null;
	String[] cortes = new String[pontos.length];
	
	Raster rt = img.getData();
	
	for (int i = 0 ; i < pontos.length; i++){
		temp = crop(img,pontos[i][0]+rt.getSampleModelTranslateX(),pontos[i][1]+rt.getSampleModelTranslateY(),pontos[i][2],pontos[i][3]);
		
		if (bBinarize) {
			temp = binarize(temp, 127);
		}
		if (bErode) {
			temp = erode(temp,3);
			temp = erode(temp,3);
		}
		
		cortes[i] = pasta+"corte_"+i+".png";
		saveImage(temp,cortes[i]);
	}
return cortes;	
}

public static void main(String[] args) {
	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	String modPath = "C:\\ocr\\modelo.bmp";
	String recPath = "C:\\ocr\\recebida.tif";
	String newModPath = "C:\\ocr\\zmodelo.jpg";

	float[][] pontos = new float[][] {{2085, 226, 267, 314},
									{182, 1749, 528, 184}};
	
	gerarModelo(modPath, newModPath);
	String[] cortes = alinharCortar(newModPath, recPath, pontos, "C:\\ocr\\", true, true);
}

public static void gerarModelo(String modPath, String newPath){
	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	RenderedImage modelo = readImage(modPath);
	modelo = scale(modelo);
	modelo = binarize(modelo, 200);
	modelo = erode(modelo,3);
    saveImage(modelo, newPath);
}

public static String[] alinharCortar(String modPath, String recPath, float[][] pontos, String pasta, boolean bBinarize, boolean bErode){
	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Read Images...");
	RenderedImage modelo = readImage(modPath);
	RenderedImage original = readImage(recPath);
	
	RenderedImage recebida = original;

	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Scale...");
	recebida = scale(recebida);
	
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Binarize...");
	recebida = binarize(recebida, 200);
	
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Erode...");
	recebida = erode(recebida, 3);
	
	int difMin = recebida.getHeight()*recebida.getWidth();
	float bestX = 0;
	float bestY = 0;
	float bestAng = 0;
	
	for(float ang = -2; ang <= 2; ang=ang+1){
		Logger.debug("", "OcrProcessamento", "alinhaCortar", "Rotate Image... | "+ang);
		RenderedImage tempRotate = rotate(recebida, ang);
		
			for(float shf = -5; shf <= 5; shf=shf+5){	
					RenderedImage tempShift = shift(tempRotate, shf, 0.0F);

					int sum = subtract(modelo, tempShift);
					
					if(sum < difMin){
						difMin = sum;
						bestX = shf;
						bestAng = ang;
					}
			}
			for(float shf = -5; shf <= 5; shf=shf+5){	
					RenderedImage tempShift = shift(tempRotate, bestX, shf);

					int sum = subtract(modelo, tempShift);
					
					if(sum < difMin){
						difMin = sum;
						bestY = shf;
						bestAng = ang;
					}
			}
	}
	
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Dif: "+difMin+" bestAng "+bestAng+" bestX "+bestX+" bestY "+bestY);

    recebida = rotate(original,bestAng);	
    recebida = shift(recebida,bestX*10,bestY*10);
   
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Crop Image...");
	String[] cortes = null;
	cortes = cropImage(recebida, pontos, pasta, bBinarize, bErode);
	
   //saveImage(recebida, "C:\\ocr\\zrecebida.jpg");
    
	Logger.debug("", "OcrProcessamento", "alinhaCortar", "Processing Done.");
   return cortes;
}

}
