package pt.iflow.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;

/**
 * Servlet implementation class for Servlet: RoundedCornerServlet
 *
 */
 public class RoundedCornerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;

   public static final String SERVICE_NAME = "rounded";

   public static final String PARM_COLOR = "c";
   public static final String PARM_BACKGROUND_COLOR = "bc";
   public static final String PARM_WIDTH = "w";
   public static final String PARM_HEIGHT = "h";
   public static final String PARM_ANGLE = "a";

   public static final String PARM_SHADOW_WIDTH ="sw";
   public static final String PARM_SHADOW_OPACITY ="o";
   public static final String PARM_SHADOW_SIDE = "s";

   public static final String PARM_WHOLE_SHADOW = "shadow";
   public static final String PARM_ARC_HEIGHT = "ah";
   public static final String PARM_ARC_WIDTH = "aw";

   private static final long MONTH_SECONDS = 60 * 60 * 24 * 30;

   private static final long EXPIRES = System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L;

   private RoundedCornerGenerator _generator = new RoundedCornerGenerator();

   // holds pre-built binaries for previously generated colors
   private static Map<String,byte[]> _imageCache = new HashMap<String, byte[]>();

   public void initialize()
   {
       String[] names = ImageIO.getWriterFormatNames();

       boolean supportsGif = false;
       
       for (int i=0; i < names.length; i++)
       {
           if (names[i].toLowerCase().equals("gif"))
           {
               supportsGif = true;
               break;
           }
       }
   }

   
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RoundedCornerServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  process(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}   
	
  void writeImageResponse(HttpServletResponse response, byte[] data, String type)
  throws Exception
  {
      OutputStream os = null;

      try {
          response.setDateHeader("Expires", EXPIRES);
          response.setHeader("Cache-Control", "public, max-age=" + (MONTH_SECONDS * 3));
          response.setContentLength(data.length);
          response.setContentType("image/" + type);
          
          os = response.getOutputStream();

          os.write(data);
          
          os.flush();

      }  finally {
          try {
              if (os != null) {
                  os.close();
              }
          } catch (Throwable t) {
              // ignore
          }
      }
  }

	private float getFloatParameter (HttpServletRequest request, String param, float defaultValue) {
	  float value = defaultValue;
	  
	  try {
	    String paramValue = request.getParameter(param);
	    if (StringUtils.isNotEmpty(paramValue)) {
	      value = Float.parseFloat(paramValue);
	    }
	  }
	  catch (Exception e) {
	  }
	  
	  return value;
	}
	
  private int getIntParameter (HttpServletRequest request, String param, int defaultValue) {
    int value = defaultValue;
    
    try {
      value = Integer.parseInt(request.getParameter(param));
    }
    catch (Exception e) {
    }
    
    return value;
  }
  
  private boolean getBooleanParameter (HttpServletRequest request, String param, boolean defaultValue) {
    boolean value = defaultValue;
    
    try {
      value = Boolean.parseBoolean(request.getParameter(param));
    }
    catch (Exception e) {
    }
    
    return value;
  }
  
  private String getStringParameter (HttpServletRequest request, String param, String defaultValue) {
    
    String value = request.getParameter(param);
    if (value == null) value = defaultValue;
        
    return value;
  }
  
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	  if (request.getHeader("If-Modified-Since") != null) {
	    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	    return;
	  }

	  String color = getStringParameter(request, PARM_COLOR, "000000");
	  String bgColor = getStringParameter(request, PARM_BACKGROUND_COLOR, "ffffff");
	  int width = getIntParameter(request, PARM_WIDTH, 10);
	  int height = getIntParameter(request, PARM_HEIGHT, 10);
	  String angle = getStringParameter(request, PARM_ANGLE, "tr");

	  int shadowWidth = getIntParameter(request, PARM_SHADOW_WIDTH, 0);
	  float shadowOpacity = getFloatParameter(request, PARM_SHADOW_OPACITY, 1.0f);
	  String side = getStringParameter(request, PARM_SHADOW_SIDE, null);

	  boolean wholeShadow = getBooleanParameter(request, PARM_WHOLE_SHADOW, false);
	  float arcWidth = getFloatParameter(request, PARM_ARC_WIDTH, 3.0f);
	  float arcHeight = getFloatParameter(request, PARM_ARC_HEIGHT, 3.0f);

	  String hashKey = color + bgColor + width + height + angle + shadowWidth + shadowOpacity + side + wholeShadow;

	  ByteArrayOutputStream bo = null;

	  try {

	    String type = "png";

	    byte[] data = (byte[])_imageCache.get(hashKey);
	    if (data != null)
	    {
	      writeImageResponse(response, data, type);
	      return;
	    }

	    BufferedImage image = null;

	    if (wholeShadow)
	    {
	      image = _generator.buildShadow(color, bgColor, width, height, arcWidth, arcHeight, shadowWidth, shadowOpacity);
	    } else if (side != null)
	    {
	      image = _generator.buildSideShadow(side, shadowWidth, shadowOpacity);
	    } else
	    {
	      image = _generator.buildCorner(color, bgColor, width, height, angle, shadowWidth, shadowOpacity);
	    }

	    bo = new ByteArrayOutputStream();

	    boolean success = ImageIO.write(image, type, bo);

	    data = bo.toByteArray();

	    if (!success || data == null || data.length < 1)
	    {
	      Logger.error("admin", this, "process",
	         "Image generated had zero length byte array or failed to convert from parameters of:\n"
	          + "[color:" + color + ", bgColor:" + bgColor
	          + ", width:" + width + ", height:" + height
	          + ", angle:" + angle + ", shadowWidth:" + shadowWidth
	          + ", shadowOpacity:" + shadowOpacity + ", side:" + side
	          + ", wholeShadow: " + wholeShadow + ", arcWidth: " + arcWidth
	          + ", arcHeight:" + arcHeight + "\n image: " + image);

	      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	      return;
	    }

	    _imageCache.put(hashKey, data);

	    writeImageResponse(response, data, type);

	  } catch (IOException eof)
	  {
	    // ignored / expected exceptions happen when browser prematurely abandons connections - IE does this a lot
	  } catch (Throwable ex) {

	    ex.printStackTrace();
	   
	  } finally {
	    try {
	      if (bo != null) {
	        bo.close();
	      }
	    } catch (Throwable t) {
	      // ignore
	    }

	  }
	}   


 }