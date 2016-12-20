package pt.iflow.applet;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.lowagie.text.pdf.PdfReader;

  public class PdfSampleImages {
	  private static Log log = LogFactory.getLog(PdfSampleImages.class);
 
	  
      private static int alturaPag = 841;
      private static int larguraPag = 595;
//      private static int alturaPag = 420;
//      private static int larguraPag = 297;
      
      public static void main(String[] args) throws IOException{
    	  PDDocument document = PDDocument.load(new File("C:\\Users\\pussman\\Downloads\\inf_05.pdf"));
    	  PDFRenderer pdfRenderer = new PDFRenderer(document);
    	  int p =document.getNumberOfPages();
    	  PDPageTree pdpt = document.getPages();
    	  int c = pdpt.getCount();
    	  BufferedImage bim = pdfRenderer.renderImageWithDPI(2, 150, ImageType.RGB);
    	  ImageIO.write(bim, "PNG", new File( "C:\\Users\\pussman\\Downloads\\inf_05.png" ));
      }
	  
  public static BufferedImage getImageFromPdf(String filepath,int pag){
		BufferedImage bim = null;
		try {
			PDDocument document = PDDocument.load(new File(filepath));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			
			if (pag <0 || pag>(document.getNumberOfPages()-1)) // Caso n receba para tirar da ultima
				bim = pdfRenderer.renderImageWithDPI(document.getNumberOfPages()-1, 150, ImageType.RGB);
			else
				bim = pdfRenderer.renderImageWithDPI(pag-1, 150, ImageType.RGB);
		} catch (Exception e) {
			log.error("Erro a adquirir imagens do pdf", e);
		}

		return bim;         
  }

  public static BufferedImage getImageFromPdfByFile(File file,int pag){
	  BufferedImage bim = null;
		try {
			PDDocument document = PDDocument.load(file);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			
			if (pag <0 || pag>(document.getNumberOfPages()-1)) // Caso n receba para tirar da ultima
				bim = pdfRenderer.renderImageWithDPI(document.getNumberOfPages()-1, 150, ImageType.RGB);
			else
				bim = pdfRenderer.renderImageWithDPI(pag-1, 150, ImageType.RGB);
		} catch (Exception e) {
			log.error("Erro a adquirir imagens do pdf", e);
		}

		return bim;  
  }
  
  
  public static BufferedImage toBufferedImage(Image image) {
      if (image instanceof BufferedImage) {
          return (BufferedImage)image;
      }

      image = new ImageIcon(image).getImage();
      boolean hasAlpha = true;
      BufferedImage bimage = null;
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
          int transparency = Transparency.OPAQUE;
          if (hasAlpha) {
              transparency = Transparency.BITMASK;
          }
          GraphicsDevice gs = ge.getDefaultScreenDevice();
          GraphicsConfiguration gc = gs.getDefaultConfiguration();
          bimage = gc.createCompatibleImage(
              image.getWidth(null), image.getHeight(null), transparency);
      } catch (HeadlessException e) { log.error("Erro a transformar Image em bufferedImage", e); }

      if (bimage == null) {
          int type = BufferedImage.TYPE_INT_RGB;
          if (hasAlpha) {
              type = BufferedImage.TYPE_INT_ARGB;
          }
          bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
      }

      Graphics g = bimage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();
      return bimage;
  }
  
//  public String getGuidName(){
//      UUID uuid = UUID.randomUUID();
//      return uuid.toString();    
//  } 
  
  public static int getNumPagesByPath(String path){
    PdfReader reader = null;
    int pages = 1;
    try {
      reader = new PdfReader(path);
      pages = reader.getNumberOfPages();
      reader.close();
    } catch (IOException e) {
      log.error("Erro ao adquirir numero de paginas do pdf");
      e.printStackTrace();
    }
    log.debug("File "+ path+" with "+pages+" pages.");
    return pages;
  }

}
