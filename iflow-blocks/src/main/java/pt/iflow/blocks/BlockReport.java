package pt.iflow.blocks;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * @author Luis Cabral
 * @version 18.02.2009
 */
public class BlockReport extends Block {

  public Port portOut, portIn;

  public final static String REPORT = "Report";
  public final static String TTL = "TTL";
  private final static String DEADLINE = "Deadline";

  private final static String DAYS = "days";
  private final static String HOURS = "hours";
  private final static String MINS = "mins";
  private final static String SECS = "secs";


  /**
   * C'tor.
   * 
   * @param anFlowId
   *          Flow ID to which this block belongs to.
   * @param id
   *          The block's ID.
   * @param subflowblockid
   *          The block's sub-flow Id.
   * @param filename
   *          The block's sub-flow filename.
   */
  public BlockReport(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  /**
   * This Block does not have an event port.
   * 
   * @see pt.iflow.api.blocks.Block#getEventPort()
   * @return null
   */
  public Port getEventPort() {
    return null;
  }

  /**
   * This Block only has one "In" Port.
   * 
   * @see pt.iflow.api.blocks.Block#getInPorts(pt.iflow.api.utils.UserInfoInterface)
   * @return The single "In" Port present in this block.
   */
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] ports = new Port[] { portIn };
    return ports;
  }

  /**
   * @see pt.iflow.api.blocks.Block#getOutPorts(pt.iflow.api.utils.UserInfoInterface)
   * @return The single "Out" Port present in this block.
   */
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] ports = new Port[] { portOut };
    return ports;
  }

  /**
   * @see pt.iflow.api.blocks.Block#before(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return ""
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String codReporting = getAttribute(REPORT);
    Calendar capturedMoment = Calendar.getInstance();
    Timestamp current = new Timestamp(capturedMoment.getTimeInMillis());
    ReportTO report = new ReportTO(flowid, pid, subpid, codReporting, current);

    // when starting a new report, close all previous ones for this process...
    for (ReportTO rep : procData.getCachedReports().values()) {
      if (rep.isActive()) {
        rep.setActive(false);
        rep.setStopReporting(current);
      }
    }
    
    // handle time-to-live before storing the report
    if (StringUtils.isNotBlank(getAttribute(TTL))) {
      Calendar ttl = incrementTimeFrom((Calendar) capturedMoment.clone());
      if (ttl.getTime().after(capturedMoment.getTime())) {
        report.setTtl(new Timestamp(ttl.getTime().getTime()));
      }
    }
    else if (StringUtils.isNotEmpty(getAttribute(DEADLINE))) {
      String dateVar = getAttribute(DEADLINE);
      if (procData.getVariableDataType(dateVar) instanceof DateDataType) {
        ProcessSimpleVariable var = procData.get(dateVar);         
        if (var != null) {
          Date dt = (Date)(var.getValue());
          if (dt != null && dt.after(capturedMoment.getTime())) {
            report.setTtl(new Timestamp(dt.getTime()));
          }
        }
      }
    }
    procData.storeReport(report);

    return this.getUrl(userInfo, procData);
  }

  /**
   * @see pt.iflow.api.blocks.Block#after(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return Exit Port.
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    return portOut;
  }

  /**
   * @see pt.iflow.api.blocks.Block#canProceed(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return true
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * @see pt.iflow.api.blocks.Block#getDescription(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return Block's description.
   */
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Relatório");
  }

  /**
   * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return Block's result.
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Relatório Processado");
  }

  /**
   * @see pt.iflow.api.blocks.Block#getUrl(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   * @return ""
   */
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private Map<String, Integer> getAttributeAsMap(String attribute) {
    String props = getAttribute(attribute);
    Map<String, Integer> map = new HashMap<String, Integer>();
    if (props != null) {
      for (String prop : props.split(";")) {
        String key = prop.split("=")[0];
        Integer value = Integer.parseInt(prop.split("=")[1]);
        map.put(key.toLowerCase(), value);
      }
    }
    return map;
  }

  private Calendar incrementTimeFrom(Calendar time) {
    Map<String, Integer> ttlMap = getAttributeAsMap(TTL);
    time = incrementDays((Calendar) time.clone(), ttlMap.get(DAYS));
    time = incrementHours((Calendar) time.clone(), ttlMap.get(HOURS));
    time = incrementMins((Calendar) time.clone(), ttlMap.get(MINS));
    time = incrementSecs((Calendar) time.clone(), ttlMap.get(SECS));
    return time;
  }

  private Calendar incrementSecs(Calendar time, Integer secs) {
    while (secs != null && secs > 0) {
      if (time.get(Calendar.SECOND) == time.getActualMaximum(Calendar.SECOND)) {
        time = incrementMins((Calendar) time.clone(), 1);
        time.set(Calendar.SECOND, time.getActualMinimum(Calendar.SECOND));
      } else {
        time.set(Calendar.SECOND, (time.get(Calendar.SECOND) + 1));
      }
      secs--;
    }
    return time;
  }

  private Calendar incrementMins(Calendar time, Integer mins) {
    while (mins != null && mins > 0) {
      if (time.get(Calendar.MINUTE) == time.getActualMaximum(Calendar.MINUTE)) {
        time = incrementHours((Calendar) time.clone(), 1);
        time.set(Calendar.MINUTE, time.getActualMinimum(Calendar.MINUTE));
      } else {
        time.set(Calendar.MINUTE, (time.get(Calendar.MINUTE) + 1));
      }
      mins--;
    }
    return time;
  }

  private Calendar incrementHours(Calendar time, Integer hours) {
    while (hours != null && hours > 0) {
      if (time.get(Calendar.HOUR) == time.getActualMaximum(Calendar.HOUR)) {
        time = incrementDays((Calendar) time.clone(), 1);
        time.set(Calendar.HOUR, time.getActualMinimum(Calendar.HOUR));
      } else {
        time.set(Calendar.HOUR, (time.get(Calendar.HOUR) + 1));
      }
      hours--;
    }
    return time;
  }

  private Calendar incrementDays(Calendar time, Integer days) {
    while (days != null && days > 0) {
      if (time.get(Calendar.DATE) == time.getActualMaximum(Calendar.DATE)) {
        if (time.get(Calendar.MONTH) == time.getActualMaximum(Calendar.MONTH)) {
          time.set(Calendar.YEAR, (time.get(Calendar.YEAR) + 1));
          time.set(Calendar.MONTH, time.getActualMinimum(Calendar.MONTH));
          time.set(Calendar.DATE, time.getActualMinimum(Calendar.DATE));
        } else {
          time.set(Calendar.MONTH, (time.get(Calendar.MONTH) + 1));
          time.set(Calendar.DATE, time.getActualMinimum(Calendar.DATE));
        }
      } else {
        time.set(Calendar.DATE, (time.get(Calendar.DATE) + 1));
      }
      days--;
    }
    return time;
  }
}
