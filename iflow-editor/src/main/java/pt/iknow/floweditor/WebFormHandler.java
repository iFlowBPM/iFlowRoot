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
/**
 * 
 */
package pt.iknow.floweditor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iknow.utils.StringUtilities;

public class WebFormHandler {
  /**
   * The request was handled by this Handler
   */
  public static final int REQUEST_HANDLED = 0;

  /**
   * The request was *NOT* handled by this Handler
   */
  public static final int REQUEST_NOT_HANDLED = 1;

  public static final String HANDLER_CODE_PARAM="bhCode";
  public static final String BASE_FORM="webForm";

  private static int instanceId = 1000;
  private final String handlerCode;
  private final String context;
  private final Object handler;
  private final Map<String,Method> actionMapper;

  WebFormHandler(Object handler) {
    if(null == handler) throw new NullPointerException("null handler");
    this.handler = handler;
    WebFormActionHandler actionHandler = handler.getClass().getAnnotation(WebFormActionHandler.class);
    if(null == actionHandler) throw new IllegalArgumentException("Not a WebFormActionHandler");

    String ctx = actionHandler.ContextPath();
    if(StringUtilities.isEmpty(ctx)) ctx = handler.getClass().getSimpleName();

    if(!ctx.startsWith("/")) ctx = "/"+ctx;
    if(!ctx.endsWith("/")) ctx = ctx+"/";
    context = ctx;
    handlerCode = (instanceId++) + "-" + System.currentTimeMillis();
    actionMapper = new HashMap<String, Method>();

    Method[] methods = handler.getClass().getMethods();
    Class<?> requestClass = HttpServletRequest.class;
    Class<?> responseClass = HttpServletResponse.class;
    for(Method m : methods) {
      WebFormAction annotation = m.getAnnotation(WebFormAction.class);
      if(null == annotation) continue;
      String evtName = annotation.EventName();
      if(StringUtilities.isEmpty(evtName)) evtName=m.getName(); // defaults to method name

      // verify method
      Class<?>[] paramTypes = m.getParameterTypes();
      if(paramTypes.length == 2 && paramTypes[0].isAssignableFrom(requestClass) && paramTypes[1].isAssignableFrom(responseClass))
        actionMapper.put(evtName, m);
      else FlowEditor.log("Ignoring method with invalid signature: "+m);
    }
  }

  public String getContext() {
    return context;
  }

  public int handleRequest(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
    if(target == null || !target.startsWith(getContext())) {
      FlowEditor.log("Invalid request. Target="+target);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return REQUEST_HANDLED;
    }
    String resource = target.substring(getContext().length()); // remove context part
    FlowEditor.log("The resource is: "+resource);
    if(actionMapper.containsKey(resource)) {
      response.setContentType(actionMapper.get(resource).getAnnotation(WebFormAction.class).ActionType().getMimeType());
      FlowEditor.log("Handler found for resource "+resource);
      actionMapper.get(resource).invoke(this.handler, request, response);
      return REQUEST_HANDLED;
    }

    return REQUEST_NOT_HANDLED;
  }

  public String getHandlerCode() {
    return handlerCode;
  }

  public String getURL(String resource, String query) {
    if(StringUtilities.isEmpty(resource)) return null;
    if(resource.charAt(0)=='/') resource = resource.substring(1);
    if(StringUtilities.isEmpty(resource)) return null;
    if(StringUtilities.isEmpty(query)) query = "";
    else if(query.charAt(0)!='&') query = "&"+query;
    return FlowEditor.getActionURL()+getContext()+resource+"?"+HANDLER_CODE_PARAM+"="+getHandlerCode()+query;
  }
}
