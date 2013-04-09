package pt.iflow.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.ReportManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>
 * Title:Audit Chart Servlet
 * </p>
 * <p>
 * Description: Servlet that generates audit charts using the JFreeChart library
 * </p>
 * <p>
 * Copyright (c) 2007 iKnow
 * </p>
 * 
 * @author agon
 * 
 * @web.servlet name="AuditChartServlet"
 * 
 * @web.servlet-mapping url-pattern="/AuditChart"
 */
public class AuditChartServlet extends HttpServlet {

  /**
   * 
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -8668531566277664798L;
  public static final String AUDIT_TYPE = "audit_type"; //$NON-NLS-1$
  public static final String AUDIT_USER_PERFORMANCE = "USER_PERFORMANCE"; //$NON-NLS-1$
  public static final String AUDIT_PROC_STATISTICS = "PROC_STATISTICS"; //$NON-NLS-1$
  public static final String AUDIT_PROC_SLA = "PROC_SLA"; //$NON-NLS-1$

  public static final String PARAM_FLOWID = "flowid"; //$NON-NLS-1$
  public static final String PARAM_WIDTH = "width"; //$NON-NLS-1$
  public static final String PARAM_HEIGHT = "height"; //$NON-NLS-1$

  public static final String PARAM_DISPLAY_TIME = "display_time"; //$NON-NLS-1$
  public static final String PARAM_DISPLAY_UNITS = "display_units";
  public static final String PARAM_DISPLAY_DATE = "display_date"; //$NON-NLS-1$
  public static final String PARAM_INCLUDE_OPEN = "include_open";
  public static final String PARAM_SECOND_GRAPH = "second_graph";
  public static final String DAYS = "D"; //$NON-NLS-1$
  public static final String HOURS = "H"; //$NON-NLS-1$
  public static final String MINS = "M"; //$NON-NLS-1$
  public static final String SECS = "S"; //$NON-NLS-1$

  public static final String LAST_YEAR = "LY";
  public static final String THIS_YEAR = "TY";
  public static final String LAST_MONTH = "LM";
  public static final String THIS_MONTH = "TM";
  public static final String BEHIND_MONTH = "BM";
  public static final String BEHIND_YEAR = "BY";
  
  protected static final Paint CHART_BLACK = new Color(0, 0, 0);
  protected static final Paint CHART_WHITE = new Color(255, 255, 255);

  protected static final Paint CHART_COLOR_1 = new Color(0xFF, 0x5B, 0x00);
  protected static final Paint CHART_COLOR_2 = new Color(0xFF, 0x9B, 0x00);
  protected static final Paint CHART_COLOR_3 = new Color(0xFF, 0xDB, 0x00);
  protected static final Paint CHART_COLOR_4 = new Color(0xE3, 0xFF, 0x00);
  protected static final Paint CHART_COLOR_5 = new Color(0xA3, 0xFF, 0x00);

  protected static final Paint CHART_COLOR_RED = new Color(0xF0, 0X30, 0X10);
  protected static final Paint CHART_COLOR_GREEN = new Color(0xA0, 0XF0, 0X00);

  protected static final String SHOW_OFFLINE_PARAM = "show_offline";
  
  public static Font fFONT = null;
  public static Font titleFONT = null;
  public static Font subtitleFONT = null;
  public static String[] HTML_COLORS = new String[0];

  public final static int DEFAULT_WIDTH = 500;
  public final static int DEFAULT_HEIGHT = 350;
  protected static Paint[] CHART_COLORS = new Paint[] {};

  static {
    fFONT = new Font("SansSerif", Font.PLAIN, 10);
    titleFONT = new Font("SansSerif", Font.BOLD, 14);
    subtitleFONT = new Font("SansSerif", Font.PLAIN, 12);
    CHART_COLORS = new Paint[] { CHART_COLOR_1, CHART_COLOR_2, CHART_COLOR_3, CHART_COLOR_4, CHART_COLOR_5 };
  }

  /**
   * The doGet method of the servlet. <br>
   * 
   * This method is called when a form has its tag value method equals to get.
   * 
   * @param request
   *          the request send by the client to the server
   * @param response
   *          the response send by the server to the client
   * @throws ServletException
   *           if an error occurred
   * @throws IOException
   *           if an error occurred
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    this.service(request, response);
  }

  /**
   * The doPost method of the servlet. <br>
   * 
   * This method is called when a form has its tag value method equals to post.
   * 
   * @param request
   *          the request send by the client to the server
   * @param response
   *          the response send by the server to the client
   * @throws ServletException
   *           if an error occurred
   * @throws IOException
   *           if an error occurred
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    this.service(request, response);
  }

  public boolean checkAccess(UserInfoInterface userInfo, int flowId) {
    if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(flowId)) {
      return true;
    }
    return false;
  }

  public static OrderedMap<String, String> getDisplayParams(UserInfoInterface userInfo) {
    IMessages msg = userInfo.getMessages();
    OrderedMap<String, String> params = new ListOrderedMap<String, String>();
    params.put("" + DAYS, msg.getString("AuditChartServlet.6")); //$NON-NLS-1$
    params.put("" + HOURS, msg.getString("AuditChartServlet.7")); //$NON-NLS-1$
    params.put("" + MINS, msg.getString("AuditChartServlet.8")); //$NON-NLS-1$
    params.put("" + SECS, msg.getString("AuditChartServlet.9")); //$NON-NLS-1$
    params.put(LAST_MONTH, msg.getString("AuditChartServlet.44")); //$NON-NLS-1$
    params.put(THIS_MONTH, msg.getString("AuditChartServlet.45")); //$NON-NLS-1$
    params.put(LAST_YEAR, msg.getString("AuditChartServlet.46")); //$NON-NLS-1$
    params.put(THIS_YEAR, msg.getString("AuditChartServlet.47")); //$NON-NLS-1$
    params.put(BEHIND_MONTH, msg.getString("AuditChartServlet.60")); //$NON-NLS-1$
    params.put(BEHIND_YEAR, msg.getString("AuditChartServlet.61")); //$NON-NLS-1$
    return params;
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    IMessages msg = userInfo.getMessages();
    ServletOutputStream out=null;

    try {
      Flow flow = BeanFactory.getFlowBean();
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      FlowHolder fh = BeanFactory.getFlowHolderBean();
      ReportManager rm = BeanFactory.getReportManagerBean();

      int flowid = Integer.parseInt(request.getParameter(PARAM_FLOWID));

      if (!checkAccess(userInfo, flowid)) {
        // TODO return warning icon/image ?
        return;
      }



      
      String auditType = request.getParameter(AUDIT_TYPE);
      OrderedMap<String, AuditData[]> auditGroup = new ListOrderedMap<String, AuditData[]>();
      AuditData[] ada = new AuditData[] {};

      boolean bDays = false;
      boolean bHours = false;
      boolean bMinutes = false;
      boolean bSeconds = false;
      String sLabel = "";
      String sChartName = "";
      String subtitle = "";
      
      String intervalParam = request.getParameter(PARAM_DISPLAY_TIME);
      Date startDate = parseStartDate(intervalParam.split(",")[0]);
      Date endDate = parseEndDate(intervalParam.split(",")[1]);
      Date[] interval = new Date[] { startDate, endDate };
      
      if (StringUtils.equals(auditType, AUDIT_USER_PERFORMANCE)) {
        if (flowid < 0) {
          sChartName = msg.getString("AuditChartServlet.28");
          subtitle = msg.getString("AuditChartServlet.29");
          IFlowData[] fda;
          if(StringUtils.equals("true", request.getParameter(SHOW_OFFLINE_PARAM)))
            fda = fh.listFlows(userInfo, FlowType.WORKFLOW);
          else
            fda = fh.listFlowsOnline(userInfo, FlowType.WORKFLOW);
          
          auditGroup = new ListOrderedMap<String, AuditData[]>();
          for (IFlowData item : fda) {
            if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(item.getId())) {
              AuditData[] auditData = pm.getProcessActivityPerformance(userInfo, item.getId(),interval);
              auditGroup.put("" + item.getId(), auditData);
            }
          }
        } else {
          sChartName = (flow.getFlow(userInfo, flowid)).getName();
          subtitle = msg.getString("AuditChartServlet.23");
          ada = pm.getProcessActivityPerformance(userInfo, flowid,interval);
        }
      } else if (StringUtils.equals(auditType, AUDIT_PROC_STATISTICS)) {

        if (flowid < 0) {
          sChartName = msg.getString("AuditChartServlet.48");
          subtitle = intervalParam.split(",")[0] + "  -  " + intervalParam.split(",")[1];
          IFlowData[] fda;
          if(StringUtils.equals("true", request.getParameter(SHOW_OFFLINE_PARAM)))
            fda = fh.listFlows(userInfo, FlowType.WORKFLOW);
          else
            fda = fh.listFlowsOnline(userInfo, FlowType.WORKFLOW);
          auditGroup = new ListOrderedMap<String, AuditData[]>();
          for (IFlowData item : fda) {
            if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(item.getId())) {
              AuditData[] auditData = pm.getProcessActivityStatistics(userInfo, item.getId(), interval);
              for (int i = 0, j = auditData.length; i < j; i++) {
                auditData[i].setName(msg.getString(auditData[i].getName()));
              }
              auditGroup.put("" + item.getId(), auditData);
            }
          }
        } else {
          sChartName = msg.getString("AuditChartServlet.48") + " : " + (flow.getFlow(userInfo, flowid)).getName();
          subtitle = intervalParam.split(",")[0] + "  -  " + intervalParam.split(",")[1];
          ada = pm.getProcessActivityStatistics(userInfo, flowid, interval);
          for (int i = 0, j = ada.length; i < j; i++) {
            ada[i].setName(msg.getString(ada[i].getName()));
          }
        }
      } else if (StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
        boolean includeOpen = StringUtils.isNotBlank(request.getParameter(PARAM_INCLUDE_OPEN))
            && StringUtils.equalsIgnoreCase("true", request.getParameter(PARAM_INCLUDE_OPEN));
        if (flowid < 0) {
          sChartName = msg.getString("AuditChartServlet.58");
          subtitle = msg.getString("AuditChartServlet.59");
          IFlowData[] fda;
          if(StringUtils.equals("true", request.getParameter(SHOW_OFFLINE_PARAM)))
            fda = fh.listFlows(userInfo, FlowType.WORKFLOW);
          else
            fda = fh.listFlowsOnline(userInfo, FlowType.WORKFLOW);
          auditGroup = new ListOrderedMap<String, AuditData[]>();
          for (IFlowData item : fda) {
            if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(item.getId())) {
              AuditData[] auditData = rm.getFlowReports(userInfo, item.getId(), includeOpen , interval);
              auditGroup.put("" + item.getId(), fixNames(userInfo, auditData));
            }
          }
        } else {
          sChartName = (flow.getFlow(userInfo, flowid)).getName();
          subtitle = msg.getString("AuditChartServlet.59");
          ada = fixNames(userInfo, rm.getFlowReports(userInfo, flowid, includeOpen, interval));
          auditGroup = rm.getFlowTTLReports(userInfo, flowid, includeOpen, interval);
        }
      }

      if (StringUtils.equals(auditType, AUDIT_USER_PERFORMANCE) || StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
        String sDisplayTime = request.getParameter(PARAM_DISPLAY_UNITS);
        if (sDisplayTime != null && !sDisplayTime.equals("")) { //$NON-NLS-1$
          if (sDisplayTime.equals(DAYS)) {
            bDays = true;
          } else if (sDisplayTime.equals(HOURS)) {
            bHours = true;
          } else if (sDisplayTime.equals(MINS)) {
            bMinutes = true;
          } else if (sDisplayTime.equals(SECS)) {
            bSeconds = true;
          } else {
            bHours = true;
          }
        } else {
          bHours = true;
        }
        if (bDays) {
          sLabel = msg.getString("AuditChartServlet.24"); //$NON-NLS-1$
        } else if (bHours) {
          sLabel = msg.getString("AuditChartServlet.25"); //$NON-NLS-1$
        } else if (bMinutes) {
          sLabel = msg.getString("AuditChartServlet.26"); //$NON-NLS-1$
        } else if (bSeconds) {
          sLabel = msg.getString("AuditChartServlet.27"); //$NON-NLS-1$
        }
      }
      String time = (bDays ? DAYS : (bHours ? HOURS : (bMinutes ? MINS : (bSeconds ? SECS : null))));
      String[] chartInfo = new String[] { sChartName, subtitle, sLabel };
      BufferedImage bi;
      if (flowid < 0) {
        chartInfo[2] = "";
        bi = createStackedBarChart(request, chartInfo, auditGroup, time);
      } else {
        if (StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
          String secondGraph = request.getParameter(PARAM_SECOND_GRAPH);
          if (secondGraph != null && Boolean.parseBoolean(secondGraph)) {
            chartInfo[0] = "";
            chartInfo[2] = "";
            bi = createMixedChart(request, chartInfo, auditGroup, time);
          } else {
            chartInfo[1] = msg.getString("AuditChartServlet.53");
            bi = createPieChart(request, chartInfo, ada, time);
          }
        } else {
          bi = createPieChart(request, chartInfo, ada, time);
        }
      }
      ImageEncoder ie = ImageEncoderFactory.newInstance(ImageFormat.PNG);
      // preallocate 20KB for image output
      ByteArrayOutputStream bout = new ByteArrayOutputStream(20*1024);
      ie.encode(bi, bout);
      response.setContentLength(bout.size());
      response.setContentType("image/png");
      out = response.getOutputStream();
      bout.writeTo(out);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "service", msg.getString("AuditChartServlet.4"), e);
    } finally {
      if(out != null) out.close();
    }
  }

  private BufferedImage createPieChart(HttpServletRequest request, String[] chartInfo, AuditData[] ada, String time)
      throws IOException {
    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    IMessages msg = userInfo.getMessages();
    String auditType = request.getParameter(AUDIT_TYPE);
    String sChartName = chartInfo[0];
    String subtitle = chartInfo[1];
    String label = chartInfo[2];
    DefaultPieDataset dataset = new DefaultPieDataset();
    for (int i = 0; i < ada.length; i++) {
      String sValue = ada[i].getValue();
      Double nValue = 0D;
      if (StringUtils.equals(auditType, AUDIT_USER_PERFORMANCE)) {
        nValue = getAuditValue(sValue, time);
      } else if (StringUtils.equals(auditType, AUDIT_PROC_STATISTICS)) {
        nValue = Double.parseDouble(sValue);
      } else if (StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
        nValue = getSLAAuditValue(sValue, time);
      }

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "createPieChart", "INT VALUE FOR " + ada[i].getName() + " IS " + nValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }

      dataset.setValue(ada[i].getName(), nValue);
    }

    JFreeChart chart = ChartFactory.createPieChart3D(sChartName, dataset, true, true, false);
    chart.setTitle(new TextTitle(sChartName, titleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP, HorizontalAlignment.CENTER,
        VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    if (StringUtils.isNotBlank(subtitle)) {
      if (StringUtils.isNotBlank(label)) {
        subtitle += " (" + label.trim() + ")";
      }
      chart.addSubtitle(new TextTitle(subtitle, subtitleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP,
          HorizontalAlignment.CENTER, VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    }
    chart.setBorderVisible(false);

    PiePlot3D plot = (PiePlot3D) chart.getPlot();
    plot.setLabelFont(fFONT);
    plot.setNoDataMessage("no data");
    plot.setNoDataMessageFont(fFONT);
    plot.setIgnoreZeroValues(false);
    plot.setIgnoreNullValues(true);
    plot.setLabelGap(0.02);
    plot.setDepthFactor(0.05);
    plot.setDarkerSides(true);
    plot.setOutlineVisible(false);
    plot.setSectionOutlinesVisible(false);
    plot.setLabelBackgroundPaint(new Color(0xFF, 0xF0, 0x73));

    NumberFormat nfValues = NumberFormat.getInstance(new Locale(msg.getString("AuditChartServlet.5"), msg
        .getString("AuditChartServlet.34")));
    nfValues.setMinimumFractionDigits(0);
    nfValues.setMaximumFractionDigits(0);
    NumberFormat pcValues = NumberFormat.getPercentInstance(new Locale(msg.getString("AuditChartServlet.35"), msg
        .getString("AuditChartServlet.36")));
    pcValues.setMinimumFractionDigits(2);
    pcValues.setMaximumFractionDigits(2);

    StandardPieSectionLabelGenerator lg = new StandardPieSectionLabelGenerator("{0} - {1} " + label + "({2})", nfValues, pcValues);
    plot.setLabelGenerator(lg);
    plot.setLabelOutlinePaint(CHART_WHITE);

    plot.setBackgroundPaint(CHART_WHITE);
    for (int i = 0; i < ada.length; i++) {
      plot.setSectionPaint((Comparable<String>) ada[i].getName(), CHART_COLORS[i % CHART_COLORS.length]);
    }

    return chart.createBufferedImage(getWidth(request), getHeight(request));
  }

  private BufferedImage createStackedBarChart(HttpServletRequest request, String[] chartInfo,
      OrderedMap<String, AuditData[]> auditGroup, String time) throws IOException {
    HttpSession session = request.getSession();
    Flow flow = BeanFactory.getFlowBean();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    IMessages msg = userInfo.getMessages();
    String auditType = request.getParameter(AUDIT_TYPE);
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    int columnCounter = 0;
    for (String key : auditGroup.keySet()) {
      AuditData[] ada = auditGroup.get(key);
      String flowName = flow.getFlow(userInfo, Integer.parseInt(key)).getName();

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "createStackedBarChart", "Flow '" + flowName + "' (id=" + key + ") out of "
            + auditGroup.keySet().size() + " flows for this group: " + ada.length + " audits present.");
      }

      if (columnCounter < ada.length) {
        columnCounter = ada.length;
      }

      Double total = 0D;
      for (int i = 0; i < ada.length; i++) {
        String sValue = ada[i].getValue();
        Double nValue = 0D;
        if (StringUtils.equals(auditType, AUDIT_USER_PERFORMANCE)) {
          nValue = getAuditValue(sValue, time);
        } else if (StringUtils.equals(auditType, AUDIT_PROC_STATISTICS)) {
          nValue = Double.parseDouble(sValue);
        } else if (StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
          nValue = getSLAAuditValue(sValue, time);
        }
        total += nValue;
      }
      for (int i = 0; i < ada.length; i++) {
        String sValue = ada[i].getValue();
        double nValue = 0.00D;
        if (StringUtils.equals(auditType, AUDIT_USER_PERFORMANCE)) {
          nValue = getAuditValue(sValue, time);
        } else if (StringUtils.equals(auditType, AUDIT_PROC_STATISTICS)) {
          nValue = Integer.parseInt(sValue);
        } else if (StringUtils.equals(auditType, AUDIT_PROC_SLA)) {
          nValue = getSLAAuditValue(sValue, time);
        }
        Double value = ((nValue * 100) / total);

        if (Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "createStackedBarChart", auditType + " - " + ada[i].getName() + ": " + value
              + "% ([" + sValue + "=" + nValue + "] out of " + total + ")");
        }

        dataset.setValue(value, (Comparable<String>) ada[i].getName(), (Comparable<String>) flowName);
      }
    }

    String chartName = chartInfo[0];
    String subtitle = chartInfo[1];
    String label = chartInfo[2];

    JFreeChart chart = ChartFactory.createStackedBarChart(chartName, msg.getString("AuditChartServlet.14"), msg
        .getString("AuditChartServlet.15"), dataset, PlotOrientation.VERTICAL, true, true, false);
    chart.setTitle(new TextTitle(chartName, titleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP, HorizontalAlignment.CENTER,
        VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    if (StringUtils.isNotBlank(subtitle)) {
      if (StringUtils.isNotBlank(label)) {
        subtitle += " (" + label.trim() + ")";
      }
      chart.addSubtitle(new TextTitle(subtitle, subtitleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP,
          HorizontalAlignment.CENTER, VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    }
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setNoDataMessage("no data");
    plot.setNoDataMessageFont(fFONT);

    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
    StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
    renderer.setRenderAsPercentages(true);
    renderer.setDrawBarOutline(true);
    renderer.setBaseItemLabelsVisible(true);
    renderer.setMaximumBarWidth(0.10);

    NumberFormat nfValues = NumberFormat.getInstance(new Locale(msg.getString("AuditChartServlet.5"), msg
        .getString("AuditChartServlet.34")));
    nfValues.setMinimumFractionDigits(0);
    nfValues.setMaximumFractionDigits(0);
    NumberFormat pcValues = NumberFormat.getPercentInstance(new Locale(msg.getString("AuditChartServlet.35"), msg
        .getString("AuditChartServlet.36")));
    pcValues.setMinimumFractionDigits(2);
    pcValues.setMaximumFractionDigits(2);
    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}%", nfValues, pcValues));

    plot.setBackgroundPaint(CHART_WHITE);
    plot.setDomainGridlinePaint(CHART_BLACK);
    for (int i = 0; i < columnCounter; i++) {
      renderer.setSeriesPaint(i, CHART_COLORS[i % CHART_COLORS.length]);
    }

    if (dataset.getColumnCount() >= 5) {
      if(dataset.getColumnCount() <= 10) {
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
      } else {
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      }
    }
    
    return chart.createBufferedImage(getWidth(request), getHeight(request));
  }
  
  private BufferedImage createMixedChart(HttpServletRequest request, String[] chartInfo,
      OrderedMap<String, AuditData[]> auditGroup, String time) {
    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    IMessages msg = userInfo.getMessages();
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (AuditData[] audits : auditGroup.values()) {
      fixNames(userInfo, audits);
      int total = 0;
      for (int i = 0; i < audits.length; i++) {
        total += Integer.parseInt(audits[i].getValue());
      }
      for (int i = 0; i < audits.length; i++) {
        double nValue = Double.parseDouble(audits[i].getValue());
        Double value = ((nValue * 100) / total);
        dataset.setValue(value, (Comparable<String>) (i == 0 ? msg.getString("AuditChartServlet.51") : (i == 1 ? msg
            .getString("AuditChartServlet.52") : null)), (Comparable<String>) audits[i].getName());
      }
    }

    String chartName = chartInfo[0];
    String subtitle = chartInfo[1];
    String label = chartInfo[2];

    JFreeChart chart = ChartFactory.createStackedBarChart(chartName, msg.getString("AuditChartServlet.54"), msg
        .getString("AuditChartServlet.15"), dataset, PlotOrientation.VERTICAL, true, true, false);
    chart.setTitle(new TextTitle(chartName, titleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP, HorizontalAlignment.CENTER,
        VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    if (StringUtils.isNotBlank(subtitle)) {
      if (StringUtils.isNotBlank(label)) {
        subtitle += " (" + label.trim() + ")";
      }
      chart.addSubtitle(new TextTitle(subtitle, subtitleFONT, new Color(0x44, 0x44, 0x44), RectangleEdge.TOP,
          HorizontalAlignment.CENTER, VerticalAlignment.TOP, RectangleInsets.ZERO_INSETS));
    }
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setNoDataMessage("no data");
    plot.setNoDataMessageFont(fFONT);

    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
    StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
    renderer.setRenderAsPercentages(true);
    renderer.setDrawBarOutline(true);
    renderer.setBaseItemLabelsVisible(true);
    renderer.setMaximumBarWidth(0.10);

    NumberFormat nfValues = NumberFormat.getInstance(new Locale(msg.getString("AuditChartServlet.5"), msg
        .getString("AuditChartServlet.34")));
    nfValues.setMinimumFractionDigits(0);
    nfValues.setMaximumFractionDigits(0);
    NumberFormat pcValues = NumberFormat.getPercentInstance(new Locale(msg.getString("AuditChartServlet.35"), msg
        .getString("AuditChartServlet.36")));
    pcValues.setMinimumFractionDigits(2);
    pcValues.setMaximumFractionDigits(2);
    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}%", nfValues, pcValues));

    plot.setBackgroundPaint(CHART_WHITE);
    plot.setDomainGridlinePaint(CHART_BLACK);
    renderer.setSeriesPaint(0, CHART_COLOR_GREEN);
    renderer.setSeriesPaint(1, CHART_COLOR_RED);

    if (dataset.getColumnCount() >= 5) {
      if(dataset.getColumnCount() <= 10) {
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
      } else {
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      }
    }
    
    return chart.createBufferedImage(getWidth(request), getHeight(request));
  }

  private int getWidth(HttpServletRequest request) {
    int width = DEFAULT_WIDTH;
    try {
      String widthParam = request.getParameter(PARAM_WIDTH);
      if (widthParam != null && !widthParam.equals("")) {
        width = Integer.parseInt(widthParam);
      }
    } catch (Exception ei) {
    }
    return width;
  }

  private int getHeight(HttpServletRequest request) {
    int height = DEFAULT_HEIGHT;
    try {
      String heightParam = request.getParameter(PARAM_HEIGHT);
      if (heightParam != null && !heightParam.equals("")) {
        height = Integer.parseInt(heightParam);
      }
    } catch (Exception ei) {
    }
    return height;
  }

  private double getAuditValue(String asValue, String time) {
    double fVal = Float.parseFloat(asValue);

    double fS = fVal;
    double fM = fS / 60;
    double fH = fM / 60;
    double fD = fH / 24;

 
    if (StringUtils.equalsIgnoreCase(time, DAYS)) {
      return Math.round(fD);
    } else if (StringUtils.equalsIgnoreCase(time, HOURS)) {
      return Math.round(fH);
    } else if (StringUtils.equalsIgnoreCase(time, MINS)) {
      return Math.round(fM);
    } else if (StringUtils.equalsIgnoreCase(time, SECS)) {
      return Math.round(fS);
    } else {
      return 0;
    }
  }

  private double getSLAAuditValue(String asValue, String time) {
    double fVal = Double.parseDouble(asValue);

    double fS = fVal / 1000;
    double fM = fS / 60;
    double fH = fM / 60;
    double fD = fH / 24;

    if (StringUtils.equalsIgnoreCase(time, DAYS)) {
      return fD;
    } else if (StringUtils.equalsIgnoreCase(time, HOURS)) {
      return fH;
    } else if (StringUtils.equalsIgnoreCase(time, MINS)) {
      return fM;
    } else if (StringUtils.equalsIgnoreCase(time, SECS)) {
      return fS;
    } else {
      return 0;
    }
  }

  public static String lastYear() {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    year--;
    return parseDate(1, Calendar.JANUARY, year) + "," + parseDate(31, Calendar.DECEMBER, year);
  }

  public static String thisYear() {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    return parseDate(1, Calendar.JANUARY, year) + "," + parseDate(31, Calendar.DECEMBER, year);
  }

  public static String lastMonth() {
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    if (month == Calendar.JANUARY) {
      month = Calendar.DECEMBER;
      year--;
    } else {
      month--;
    }
    return parseDate(1, month, year) + "," + parseDate(lastDayOfMonth(month, year), month, year);
  }

  public static String behindMonth() {
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month2;
    int year2;
    if (month == Calendar.JANUARY) {
      month2 = Calendar.DECEMBER;
      year2 = year-1;
    } else {
      year2 = year;
      month2 = month-1;
    }
//    int day2 = day - 1;
//    day = day + 1;
    return parseDate(day, month2, year2) + "," + parseDate(day, month, year);
  }
  
  public static String behindYear() {
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    return parseDate(day, month, year-1) + "," + parseDate(day, month, year);
  }
  
  public static String thisMonth() {
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    return parseDate(1, month, year) + "," + parseDate(lastDayOfMonth(month, year), month, year);
  }

  public static String getBehindDate() {  
   String mes = behindMonth();
   return ( mes.split(",")[0]);
  }
  public static String getTodayDate() {  
    String mes = behindMonth();
    return ( mes.split(",")[1]);
   }
  
  public static int lastDayOfMonth(int month, int year) {
    Calendar calCurr = Calendar.getInstance();
    calCurr.set(year, month, 1);
    return calCurr.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  private static String parseDate(int day, int month, int year) {
    return day + "/" + (month + 1) + "/" + year;
  }

  /**
   * Parses a String into a Date object.
   * 
   * @param date
   *          Date variable which must be set in "dd/mm/yyyy" format only and in
   *          that order.
   * @return Parsed Date object.
   */
  private static Date parseStartDate(String date) {
    return parseDate(date, false);
  }
  private static Date parseEndDate(String date) {
    return parseDate(date, true);    
  }
  private static Date parseDate(String date, boolean allDay) {
    Calendar calCurr = Calendar.getInstance();
    String[] dt = date.split("/");
    int day = Integer.parseInt(dt[0]);
    int month = (Integer.parseInt(dt[1]) - 1);
    int year = Integer.parseInt(dt[2]);
    int hour = allDay ? 23 : 0;
    int minute = allDay ? 59 : 0;
    int second = allDay ? 59 : 0;    
    calCurr.set(year, month, day, hour, minute, second);
    return calCurr.getTime();
  }

  private AuditData[] fixNames(UserInfoInterface userInfo, AuditData[] audits) {
    String startMsg = "const.start";
    for (int i = 0, e = audits.length; i < e; i++) {
      AuditData data = audits[i];
      if (StringUtils.equals(data.getName(), startMsg)) {
        data.setName(userInfo.getMessages().getString(startMsg));
      }
      audits[i] = data;
    }
    return audits;
  }
}
