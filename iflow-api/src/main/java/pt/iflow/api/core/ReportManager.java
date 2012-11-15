package pt.iflow.api.core;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections15.OrderedMap;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.UserInfoInterface;

public interface ReportManager {

  /**
   * Stores given report in DB.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param report
   *          Report to store.
   */
  void storeReport(UserInfoInterface userInfo, ProcessData procData, ReportTO report);

  /**
   * Retrieves all reports from given process.
   * 
   * @param userInfo
   *          User Information.
   * @param procData
   *          Process Data.
   * @return List of all reports for given process.
   */
  List<ReportTO> getProcessReports(UserInfoInterface userInfo, ProcessData procData);

  /**
   * Retrieves flow SLA reports.
   * 
   * @param userInfo
   *          User Information.
   * @param flowid
   *          Flow id.
   * @param includeOpen
   *          Flag indicating if the report should include currently open
   *          processes.
   * @return Parsed reports for graphic display.
   */
  AuditData[] getFlowReports(UserInfoInterface userInfo, int flowid, boolean includeOpen ,Date[] interval);

  /**
   * Retrieves reports indicating how many processes in this flow have surpassed
   * their TTL and how many haven't.
   * 
   * @param userInfo
   *          User Information.
   * @param flowid
   *          Flow id.
   * @param includeOpen
   *          Flag indicating if the report should include currently open
   *          processes.
   * @return Map containing TTL reports for graphic display.
   */
  OrderedMap<String, AuditData[]> getFlowTTLReports(UserInfoInterface userInfo, int flowid, boolean includeOpen,Date[] interval);
}
