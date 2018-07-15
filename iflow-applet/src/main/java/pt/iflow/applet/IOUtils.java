package pt.iflow.applet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IOUtils {
  private static Log log = LogFactory.getLog(IOUtils.class);

  public static byte[] getResourceAsBytes(String resource) {
    return getResourceAsBytes(resource, null);
  }

  public static byte[] getResourceAsBytes(String resource, ClassLoader cl) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in = null;
    try {
      if (null == cl)
        copyStreams(in = IOUtils.class.getResourceAsStream(resource), out);
      else
        copyStreams(in = cl.getResourceAsStream(resource), out);
    } catch (Exception e) {
      log.error("Error reading resource " + resource, e); //$NON-NLS-1$
      return null;
    } finally {
    	if( in != null) IOUtils.safeClose(in);
    }
    return out.toByteArray();
  }

  public static void copyStreams(InputStream in, OutputStream out) throws IOException {
    byte[] b = new byte[8192];
    int r;
    while ((r = in.read(b)) >= 0)
      out.write(b, 0, r);
  }
  
  public static void safeClose(InputStream is) {
	  if (is != null) {
		  try {
			  is.close();
		  } catch (IOException e) {
			 
		  }
	  }
  }

  public static void safeClose(OutputStream os) {
	  if (os != null) {
		  try {
			  os.close();
		  } catch (IOException e) {
			  
		  }
	  }
  }

}
