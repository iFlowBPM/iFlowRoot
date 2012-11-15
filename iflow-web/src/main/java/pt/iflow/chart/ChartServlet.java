package pt.iflow.chart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.Repository;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.chart.resources.RepositoryLoader;
import pt.iflow.chart.style.RepositoryStyleLoader;
import pt.iknow.chart.AbstractChart;
import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.IChart;
import pt.iknow.chart.resources.ResourceLoader;
import pt.iknow.chart.style.StyleLoader;

/**
 * 
 * <p>
 * Title:Chart Generation Servlet
 * </p>
 * <p>
 * Description: Servlet that generates charts using the JFreeChart library
 * </p>
 * <p>
 * Copyright (c) 2006 iKnow
 * </p>
 * 
 * @author agon
 * 
 * @web.servlet name="ChartServlet"
 * 
 * @web.servlet-mapping url-pattern="/Chart"
 * @web.servlet-mapping url-pattern="/Form/Chart"
 */
public class ChartServlet extends HttpServlet {

  private static final Properties chartNameMap = new Properties();

  // CHART TYPES
  private static final String sCHART_PIE = "PIE"; //$NON-NLS-1$
  private static final String sCHART_PIE3D = sCHART_PIE + "3D"; //$NON-NLS-1$
  private static final String sCHART_BAR = "BAR"; //$NON-NLS-1$
  private static final String sCHART_STACKED = "STACKED"; //$NON-NLS-1$
  private static final String sCHART_LAYERED_BAR = "LAYERED"; //$NON-NLS-1$

  static {
    chartNameMap.setProperty(sCHART_PIE, "pt.iflow.chart.IFPieChart"); //$NON-NLS-1$
    chartNameMap.setProperty(sCHART_PIE3D, "pt.iflow.chart.IFPie3DChart"); //$NON-NLS-1$
    chartNameMap.setProperty(sCHART_BAR, "pt.iflow.chart.IFBarChart"); //$NON-NLS-1$
    chartNameMap.setProperty(sCHART_STACKED, "pt.iflow.chart.IFStackedChart"); //$NON-NLS-1$
    chartNameMap.setProperty(sCHART_LAYERED_BAR, "pt.iflow.chart.IFLayeredBarChart"); //$NON-NLS-1$

    // Loaders especificos do UniFlow (usam o repositorio)
    ResourceLoader.registerResourceLoader("repos", RepositoryLoader.class); //$NON-NLS-1$
    StyleLoader.registerStyleLoader("repos", RepositoryStyleLoader.class); //$NON-NLS-1$
  }

  private static final String REP_CHART_SUFFIX = ".chart"; //$NON-NLS-1$
  private static final String CHART_TYPE_PROPERTY = "chart.type"; //$NON-NLS-1$

  private static IChart instantiate(ChartCtx ctx, UserInfoInterface userInfo, String template, Properties paramProps) {
    IChart result = null;
    InputStream input = null;
    IMessages msg = userInfo.getMessages();

    Repository rep = BeanFactory.getRepBean();
    String file = template + REP_CHART_SUFFIX;

    input = rep.getChartFile(userInfo, file).getResourceAsStream();

    if (null == input) {
      // Try to read template as a whole file name
      file = template;
      input = rep.getChartFile(userInfo, file).getResourceAsStream();
      if (null == input) return null;
    }
    Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", msg.getString("ChartServlet.60")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    // data cannot be null!! weeeeee!!

    Properties props = new Properties();
    try {
      props.load(input);
    } catch (IOException e) {
    } finally {
      try {
        input.close();
      } catch (IOException e) {
      }
    }
    Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", "Data loaded"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    // Override template properties
    if (null != paramProps) {
      Enumeration<Object> pprops = paramProps.keys();
      while (pprops.hasMoreElements()) {
        String key = (String) pprops.nextElement();
        props.setProperty(key, paramProps.getProperty(key));
      }
    }
    props.setProperty(AbstractChart.IMAGE_FORMAT_PROP, "png");
    Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", "Data overritten"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    String chartType = props.getProperty(CHART_TYPE_PROPERTY);
    if (null == chartType)
      return null;

    Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", "DChart type: "+chartType); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    try {
      Class<? extends Object> chartClass = rep.loadClass(userInfo, chartNameMap.getProperty(chartType.toUpperCase()));
      Object instance = chartClass.newInstance();
      result = (IChart) instance;
      result.init(ctx, props);
      Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", "Init complete"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } catch (Exception e) {
      result = null;
      Logger.debug(userInfo.getUtilizador(), "ChartServlet", "instantiate", "Init failed: "+e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    return result;
  }

  // OPTIONS
  private static final String sOPT_TEMPLATE = "template"; //$NON-NLS-1$
  private static final String sOPT_WIDTH = "width"; //$NON-NLS-1$
  private static final String sOPT_HEIGHT = "height"; //$NON-NLS-1$
  //private static final String sOPT_DATASOURCE = "datasource";
  private static final String sOPT_FLOWID = "flowid"; //$NON-NLS-1$
  private static final String sOPT_PID = "pid"; //$NON-NLS-1$
  private static final String sOPT_SUBPID = "subpid"; //$NON-NLS-1$

  // DEFAULTS/CONSTs
  //private static final String sPROCESS_DATASOURCE = "process";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
    generateChart(req, resp);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
    generateChart(req, resp);
  }

  private void generateChart(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    Properties props = getRequestAsProperties(request);
    HttpSession session = request.getSession();
    UserInfoInterface userInfo = null;
    String username = null;
    ProcessData procData = null;
    IMessages msg = null;

    if (session != null) {
      userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    }
    if (userInfo == null) {
      // create a dummy user info
      userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
    }
    
    msg = userInfo.getMessages();
    
    ProcessManager pm = (ProcessManager) session.getAttribute("pm"); //$NON-NLS-1$

    if (userInfo != null)
      username = userInfo.getUtilizador();

    // first get control parameters
    String sTemplate = props.getProperty(sOPT_TEMPLATE);
    String sWidth = props.getProperty(sOPT_WIDTH);
    String sHeight = props.getProperty(sOPT_HEIGHT);
    //String sDataSource = request.getParameter(sOPT_DATASOURCE);
    String sFlowId = props.getProperty(sOPT_FLOWID);
    String sPid = props.getProperty(sOPT_PID);
    String sSubPid = props.getProperty(sOPT_SUBPID);
    String flowExecType = props.getProperty(Const.FLOWEXECTYPE);
    if (flowExecType==null) flowExecType = "";

    int nWidth = -1;
    int nHeight = -1;
    int flowid = -1;
    int pid = -1;
    int subpid = -1;

    try {
      flowid = Integer.parseInt(sFlowId);
      pid = Integer.parseInt(sPid);

      if (sSubPid == null || sSubPid.equals("")) { //$NON-NLS-1$
        // process not yet "migrated".. assume default subpid
        subpid = 1;
      } else {
        subpid = Integer.parseInt(sSubPid);
      }

      if (pid == Const.nSESSION_PID) {
        subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
        procData = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
      } else {
        procData = pm.getProcessData(userInfo, flowid, pid, subpid, session);
        // clean session dataset just in case...
        session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
      }

      // validations
      if (sWidth != null)
        nWidth = Integer.parseInt(sWidth);

      if (sHeight != null)
        nHeight = Integer.parseInt(sHeight);

    } catch (Exception e) {
      Logger.error(username, this, "service", "Excepcao no processamento dos argumentos"); //$NON-NLS-1$ //$NON-NLS-2$
      e.printStackTrace();
      
    }
    Logger.debug(username, this, "service", "Grafico com tamanho: "+nWidth+"x"+nHeight+" titulo: '"+props.getProperty("chart.title")+"' Dados: "+props.getProperty("dsl")+" - "+props.getProperty("dsv")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
    
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    if (sTemplate == null || "".equals(sTemplate)) { //$NON-NLS-1$
      Logger.error(username, this, "service", "Invalid template"); //$NON-NLS-1$ //$NON-NLS-2$
      AbstractChart.generateErrorMessage(msg.getString("ChartServlet.54"), 400, 300, bout); //$NON-NLS-1$
    } else if (nWidth < 1 || nHeight < 1) {
      Logger.error(username, this, "service", "Invalid size"); //$NON-NLS-1$ //$NON-NLS-2$
      AbstractChart.generateErrorMessage(msg.getString("ChartServlet.56"), 400, 300, bout); //$NON-NLS-1$
    } else if (procData == null) {
      Logger.error(username, this, "service", msg.getString("ChartServlet.55")); //$NON-NLS-1$ //$NON-NLS-2$
      AbstractChart.generateErrorMessage(msg.getString("ChartServlet.59"), 400, 300, bout); //$NON-NLS-1$
    } else {
      try {

        ChartCtx ctx = new ChartCtx();
        ctx.put(Const.USER_INFO, userInfo);
        ctx.put(Const.SESSION_PROCESS, procData);
        ctx.put(DataConverter.PROPERTIES, props);

        IChart chart = instantiate(ctx, userInfo, sTemplate, props);

        chart.draw(ctx, procData, nWidth, nHeight, bout);//response.getOutputStream());
      } catch (Throwable t) {
        Logger.error(userInfo.getUtilizador(), this, "generateChart", "Error generating chart image", t);
        bout = new ByteArrayOutputStream(); // clear eventual data generated
        AbstractChart.generateErrorMessage(msg.getString("ChartServlet.59"), 400, 300, bout); //$NON-NLS-1$
      }
    }
    response.setDateHeader("Last-Modified", System.currentTimeMillis());
    response.setContentType("image/png");
    response.setContentLength(bout.size());
    bout.writeTo(response.getOutputStream());
    
  }

  private static Properties getRequestAsProperties(HttpServletRequest request) {
    Properties props = new Properties();
    Map<?,?> map = request.getParameterMap();

    Iterator<?> keys = map.keySet().iterator();
    while (keys.hasNext()) {
      String key = (String) keys.next();

      String[] values = (String[]) map.get(key);
      if (null == values)
        continue;
      String value = values[values.length - 1]; // last value survive
      props.setProperty(key, value);
    }
    return props;
  }
  
  public void init() { }
}
