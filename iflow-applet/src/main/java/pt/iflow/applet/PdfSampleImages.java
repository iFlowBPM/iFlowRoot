package pt.iflow.applet;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.pdf.PdfReader;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

  public class PdfSampleImages {
	  private static Log log = LogFactory.getLog(PdfSampleImages.class);
 
	  
      private static int alturaPag = 841;
      private static int larguraPag = 595;
//      private static int alturaPag = 420;
//      private static int larguraPag = 297;
	  
  public static BufferedImage getImageFromPdf(String filepath,int pag){
 
	  File file = new File(filepath);
      RandomAccessFile raf = null;
      PDFFile pdffile = null;

	try {
		  raf = new RandomAccessFile(file, "r");
	      FileChannel channel = raf.getChannel();
	      ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	      pdffile = new PDFFile(buf);
	      raf.close();
	      channel.close();
	} catch (Exception e) { 
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e1) { e1.printStackTrace(); }
		log.error("Erro a adquirir imagens do pdf", e); 
		} 

	PDFPage page = null;
	if(pag == -1) //Caso n receba  para tirar da ultima
	    page = pdffile.getPage(getNumPagesByPath(filepath));	
	else
		page = pdffile.getPage(pag);
	
	      Rectangle rect = new Rectangle(0,0,larguraPag,alturaPag);
	      Image img = page.getImage( larguraPag/2, alturaPag/2, rect, null, true, true);	
	      BufferedImage bImg = toBufferedImage( img );

	return bImg;         
  }

  public static BufferedImage getImageFromPdfByFile(File file,int pag){
	  
	  
      RandomAccessFile raf;
      PDFFile pdffile = null;

	try {
		  raf = new RandomAccessFile(file, "r");
	      FileChannel channel = raf.getChannel();
	      ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	      pdffile = new PDFFile(buf);
	      raf.close();
	      channel.close();
	} catch (Exception e) { log.error("Erro a adquirir imagens do pdf", e); } 

	PDFPage page = null;
	if(pag == -1) //Caso n receba  para tirar da ultima
	    page = pdffile.getPage(getNumPagesByPath(file.getAbsolutePath()));	
	else
		page = pdffile.getPage(pag);
	
	      Rectangle rect = new Rectangle(0,0,larguraPag,alturaPag);
	      Image img = page.getImage( larguraPag/2, alturaPag/2, rect, null, true, true);	
	      BufferedImage bImg = toBufferedImage( img );

	return bImg;         
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
