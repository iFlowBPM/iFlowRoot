package pt.iflow.documents;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.utils.Logger;

/**
 * Servlet implementation class for Servlet: RoundedCornerServlet
 *
 */
 public class PdfSampleServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;

   //public static final String SERVICE_NAME = "pdfsample";

   public static final String PARM_FILE = "f";
   public static final String PARM_PAGE_NUMBER = "p";

   //private static final long MONTH_SECONDS = 60 * 60 * 24 * 30;
  // private static final long EXPIRES = System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L;




   
   
	public PdfSampleServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  process(request, response);
	}  	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}   
	
  void writeImageResponse(HttpServletResponse response, byte[] data, String type)
  throws Exception
  {
	  	  
      OutputStream os = null;

      try {
      //    response.setDateHeader("Expires", EXPIRES);
      //    response.setHeader("Cache-Control", "public, max-age=" + (MONTH_SECONDS * 3));
          response.setContentLength(data.length);
          response.setContentType("image/" + type);
          
          os = response.getOutputStream();
          os.write(data);  
          os.flush();

      }  finally {
          try {
              if (os != null) {os.close();}
          } catch (Throwable t) { }
      }
  }


	

  
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	  if (request.getHeader("If-Modified-Since") != null) {
	    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	    return;
	  }

	  int docid = Integer.parseInt(request.getParameter(PARM_FILE));
	  int page = Integer.parseInt(request.getParameter(PARM_PAGE_NUMBER));

	  ByteArrayOutputStream bo = null;

	  try {

	       String type = "png";
	       PdfSampleImages pdf = new PdfSampleImages();
		   BufferedImage image = pdf.getImageFromPdf(docid,page);
		   bo = new ByteArrayOutputStream();
		   ImageIO.write(image, "PNG", bo);  
		   byte[] data = bo.toByteArray();
   
	    
	    if (data != null)
	    {
	      writeImageResponse(response, data, type);
	      return;
	    }

	
	    bo = new ByteArrayOutputStream();
	    boolean success = ImageIO.write(image, type, bo);
	    data = bo.toByteArray();

	    if (!success || data == null || data.length < 1)
	    {
	      Logger.error("admin", this, "process","Erro ao gerar imagem da pagina do pdf\n");
	      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	      return;
	    }

	    writeImageResponse(response, data, type);

	  } catch (IOException eof){}
	    catch (Throwable ex) { ex.printStackTrace();
	  } finally {
	  try {
	      if (bo != null) {bo.close();} 
	      } catch (Throwable t) {}
	  }
	}   

	  


 }