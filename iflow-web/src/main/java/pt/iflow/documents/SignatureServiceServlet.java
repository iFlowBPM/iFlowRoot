package pt.iflow.documents;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.PassImage;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

/**
 * Document Upload and Download
 * 
 * @author ombl
 * 
 * @web.servlet name="DocumentService"
 * 
 * @web.servlet-mapping url-pattern="/DocumentService"
 * 
 */
public class SignatureServiceServlet extends HttpServlet {

  private static final long serialVersionUID = -3535202556875137862L;
  
  public static final String IMAGE_PARAM = "image"; //$NON-NLS-1$
  public static final String MAXSIZE_PARAM = "maxsize"; //$NON-NLS-1$
  
  public SignatureServiceServlet() {
  }

  public void init() throws ServletException {
  }
  
  protected void setHeaders(byte[] image, HttpServletResponse response) throws ServletException, IOException {
	    response.setHeader("Content-Disposition", "attachment;filename=\"image\";");
	    response.setContentLength(image.length);
	  }
	  
  
  protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();

    // User must be authenticated
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if (null == userInfo) {
      Logger.error("<unknown>", this, "doHead", "Invalid user/user not authenticated.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

  }
  
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();

	    // User must be authenticated
	    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	    if (null == userInfo) {
	      Logger.error("<unknown>", this, "doGet", "Invalid user/user not authenticated.");
	      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
	      return;
	    }

	    byte[] ba = null;
        //Ler imagem da BD
	    PassImage pi = BeanFactory.getPassImageManagerBean();
	    ba = pi.getImage(userInfo);
	        
	    if (ba == null) {
	      Logger.debug(userInfo.getUtilizador(), this, "doGet", "file not retrieved from db");
	      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
	      return;
	    }
	    Logger.debug(userInfo.getUtilizador(), this, "doGet", "sending file to client...");
	    setHeaders(ba, response);

	    //ENVIAR PARAMETROS DA ASSINATURA
	    response.setHeader("SIGNATURE_POSITION_STYLE", Const.sSIGNATURE_POSITION_STYLE);
	    response.setHeader("RUBRIC_IMAGE", Const.sRUBRIC_IMAGE);
	    response.setHeader("RUBRIC_POSITION_STYLE", Const.sRUBRIC_POSITION_STYLE);
   
	    OutputStream out = response.getOutputStream();
	    out.write(ba);
	    out.flush();
	    out.close();
	  }
  
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if (null == userInfo) {
      Logger.error("<unknown>", this, "doPost", "Invalid user/user not authenticated.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }
    
    Logger.debug(userInfo.getUtilizador(), this, "doPost", "Post called");
    byte[] ba = new byte[0];
    ServletOutputStream outStream = null;
    try {
      FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);

      FormFile img = fdFormData.getFileParameter(IMAGE_PARAM);
      
      //Guardar imagem na BD
      PassImage pi = BeanFactory.getPassImageManagerBean();
      pi.saveImage(userInfo, img.getData());    
		
      outStream = response.getOutputStream();
      outStream.write(ba);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "doPost", "Error uploading files.", e);
      throw new ServletException(e);
    } finally {
      if(null != outStream)outStream.close();
    }
  }
}
