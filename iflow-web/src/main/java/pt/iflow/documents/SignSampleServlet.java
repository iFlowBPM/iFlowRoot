package pt.iflow.documents;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.PassImage;
import pt.iflow.api.utils.Logger;
import pt.iflow.applet.ImageIconRep;

/**
 * Servlet implementation class for Servlet:
 *
 */
 public class SignSampleServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;

   public static final String PARM_USER = "u";

   
	public SignSampleServlet() {
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

	  String userid = request.getParameter(PARM_USER);

	  ByteArrayOutputStream bo = null;

	  try {

	    String type = "png";
	    PdfSampleImages pdf = new PdfSampleImages();
	    PassImage pi = BeanFactory.getPassImageManagerBean();
	    
	    byte[] img = pi.getImageUser(userid);
	    ByteArrayInputStream bis = new ByteArrayInputStream (img);
	    ObjectInputStream ois = new ObjectInputStream(bis);
	    ImageIconRep j = (ImageIconRep)(ois.readObject());
	    
	    Image o = j.getImage();
        BufferedImage image = pdf.toBufferedImage(o);
		   bo = new ByteArrayOutputStream();
		   ImageIO.write(image, "PNG", bo);  
		   byte[] data = bo.toByteArray();
   
	    img = data;
		   
	    if (img != null)
	    {
	      writeImageResponse(response, img, type);
	      return;
	    }

	    if (img == null || img.length < 1)
	    {
	      Logger.error("admin", this, "process","Erro ao gerar imagem da pagina do pdf\n");
	      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	      return;
	    }

	    writeImageResponse(response, img, type);

	  } catch (IOException eof){}
	    catch (Throwable ex) { ex.printStackTrace();
	  } finally {
	  try {
	      if (bo != null) {bo.close();} 
	      } catch (Throwable t) {}
	  }
	}   


 }