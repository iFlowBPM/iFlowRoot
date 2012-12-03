/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iknow.floweditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;
import org.mortbay.util.IO;

import pt.iknow.utils.StringUtilities;

public class MVCWebServer {
  private static boolean debug = false;

  private Server actionServer = null;
  private String actionURL = null;

  private Hashtable<String,WebFormHandler> hsWebFormHandlers = new Hashtable<String,WebFormHandler>();
  private static final MimetypesFileTypeMap mimeMapping;// = new MimetypesFileTypeMap();
  private static MVCWebServer instance = null;
  
  static {
    Log.setLog(new FlowEditorLogger());
    mimeMapping = new MimetypesFileTypeMap(MVCWebServer.class.getResourceAsStream("mime.types"));
  }
  
  private MVCWebServer() {
    
  }
  
  public static MVCWebServer getInstance() {
    if(null == instance) instance = new MVCWebServer();
    return instance;
  }

  public Server getActionServer() {
    return actionServer;
  }

  public String getActionURL() {
    return actionURL;
  }

  public void start() {
    if(null != actionServer) {
      FlowEditor.log("Server is running. Ignore....");
      return;
    }

    actionServer = new Server();
    Connector connector=new SelectChannelConnector(); // NIO sockets
    connector.setPort(0);
    connector.setHost("127.0.0.1"); // bind to local address
    actionServer.setConnectors(new Connector[]{connector});

    Handler editorHandler=new FlowEditorHandler();
    HandlerCollection handlers = new HandlerCollection();
    handlers.setHandlers(new Handler[]{editorHandler});

    actionServer.setHandler(handlers);

    try {
      actionServer.start();
      actionURL = "http://127.0.0.1:" + connector.getLocalPort();
    } catch (Exception e) {
      FlowEditor.log("Could not start jetty engine", e);
    }
  }


  public void stop() {
    if(null == actionServer) return;
    try {
      actionServer.stop();
      actionServer = null;
    } catch (Exception e) {
      FlowEditor.log("Error stopping jetty engine", e);
    }
  }

  public static void setDebug(boolean enabled) {
    debug = enabled;
  }


  public WebFormHandler createWebFormHandler(Object block) {
    WebFormHandler handler = new WebFormHandler(block);
    String handlerCode = handler.getHandlerCode();
    if(handlerCode == null) return null;
    hsWebFormHandlers.put(handlerCode, handler);
    return handler;
  }

  public void releaseWebFormHandler(WebFormHandler handler) {
    if(handler == null) return;
    hsWebFormHandlers.remove(handler.getHandlerCode());
  }
  
  public WebFormHandler getWebFormHandler(String handlerCode) {
    if(null == handlerCode) return null;
    return hsWebFormHandlers.get(handlerCode);
  }

  private final class FlowEditorHandler extends AbstractHandler {

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException,
    ServletException {
      Request base_request = (request instanceof Request)?(Request)request:HttpConnection.getCurrentConnection().getRequest();
      Response base_response = (response instanceof Response)?(Response)response:HttpConnection.getCurrentConnection().getResponse();

      FlowEditor.log("Requesting resource: "+target);
      
      // Set default types
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      request.setCharacterEncoding("UTF-8");

      String blockHandlerCode = request.getParameter(WebFormHandler.HANDLER_CODE_PARAM);
      WebFormHandler handler = getWebFormHandler(blockHandlerCode);
      
      // is it a block?
      if(handler!=null) {
        int status = WebFormHandler.REQUEST_NOT_HANDLED;
        try {
          status = handler.handleRequest(target, request, response);
        } catch (Throwable t) {
        	FlowEditor.log("Error invokinghandler", t);
          base_response.setContentType("text/html");  // get mime type
          base_response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          PrintWriter pw = base_response.getWriter();
          pw.println("<h2>Error executing block handler "+handler.getClass().getName()+"</h2>");
          pw.println("<pre>");
          t.printStackTrace(pw);
          pw.println("</pre>");
          pw.close();
          status = WebFormHandler.REQUEST_HANDLED;
        }
        if(status == WebFormHandler.REQUEST_HANDLED) {
          base_request.setHandled(true);
          return;
        }
      }

      InputStream result = MVCWebServer.class.getResourceAsStream("/web"+target);
      base_request.setHandled(true);
      if(null == result) {
        base_response.setContentType("text/html");
        base_response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        base_response.getWriter().println("<h1>Not Found</h1>");
      } else {
        // base_response.setContentType("text/html");  // get mime type
        String mimetype = getContentType(target);
        FlowEditor.log("  ==> "+mimetype);
        if(!StringUtilities.isEmpty(mimetype))
          base_response.setContentType(mimetype);
        
        base_response.setStatus(HttpServletResponse.SC_OK);
        OutputStream out = base_response.getOutputStream();
        IO.copy(result, out);
      }
    }
  }

  private static String getContentType(String fileName) {
    if(null == fileName) return null;
    return mimeMapping.getContentType(fileName.toLowerCase());
  }



  /**
   * Wrapper class to log jetty messages to FlowEditor
   * @author oscar
   *
   */
  private final static class FlowEditorLogger implements Logger {
    public void debug(String msg, Throwable th) {
      if(debug)
        FlowEditor.log(msg, th);
    }

    public void debug(String msg, Object arg0, Object arg1) {
      if(debug)
        FlowEditor.log(format(msg,arg0,arg1));
    }

    public Logger getLogger(String name) {
      return this;
    }

    public void info(String msg, Object arg0, Object arg1) {
      FlowEditor.log(format(msg,arg0,arg1));
    }

    public boolean isDebugEnabled() {
      return debug;
    }

    public void setDebugEnabled(boolean enabled) {
      debug = enabled;
    }

    public void warn(String msg, Throwable th) {
      FlowEditor.log(msg, th);
    }

    public void warn(String msg, Object arg0, Object arg1) {
      FlowEditor.log(format(msg,arg0,arg1));
    }

    // copiado de StdErrLog
    private String format(String msg, Object arg0, Object arg1)
    {
      int i0=msg.indexOf("{}");
      int i1=i0<0?-1:msg.indexOf("{}",i0+2);

      if (arg1!=null && i1>=0)
        msg=msg.substring(0,i1)+arg1+msg.substring(i1+2);
      if (arg0!=null && i0>=0)
        msg=msg.substring(0,i0)+arg0+msg.substring(i0+2);
      return msg;
    }
  }
}
