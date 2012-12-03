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
/*
 *
 * Created on Jul 29, 2005 by mach
 *
 */

package pt.iflow.delegations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJBException;
import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.delegations.DelegationInfo;
import pt.iflow.api.delegations.DelegationInfoData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * XDoclet-based session bean.  The class must be declared
 * public according to the EJB specification.
 *
 * To generate the EJB related files to this EJB:
 *		- Add Standard EJB module to XDoclet project properties
 *		- Customize XDoclet configuration for your appserver
 *		- Run XDoclet
 *
 * Below are the xdoclet-related tags needed for this EJB.
 *
 */
public class DelegationInfoBean implements DelegationInfo {

  private static DelegationInfoBean instance = null;
  
  private DelegationInfoBean() {}
  
  public static DelegationInfoBean getInstance() {
    if(null == instance)
      instance = new DelegationInfoBean();
    return instance;
  }
  
  
  
  
  
  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright (c) 2005 mach</p>
   *
   * @author mach
   */

  private static final int nALL_FLOWS = 0;
  private static final int nDEPLOYED_FLOWS = 1;
  private static final int nUNDEPLOYED_FLOWS = 2;

  /**
	 *
	 */
  private static final long serialVersionUID = 1L;

  private DelegationInfoData buildInfo(ResultSet rs) throws SQLException {

    DelegationInfoData data = new DelegationInfoDataImpl();

    data.setHierarchyID(rs.getInt("hierarchyid"));
    data.setFlowID(rs.getInt("flowid"));
    data.setFlowName(rs.getString("flowname"));
    data.setAcceptKey(rs.getString("acceptkey"));
    data.setRejectKey(rs.getString("rejectkey"));
    data.setOwnerID(rs.getString("ownerid"));
    data.setPending(rs.getInt("pending"));
    data.setExpires(rs.getTimestamp("expires"));
    data.setUserID(rs.getString("userid"));
    return data;

  }

  /**
   * Returns all received delegations, for both deployed and undeployed flows
   *
   * @param userInfo the requesting user
   * @return a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getReceivedDelegations(UserInfoInterface userInfo) {
    return this.getReceivedDelegations(userInfo, nALL_FLOWS);
  }


  /**
   * Returns deployed flows received delegations
   *
   * @param userInfo the requesting user
   * @return  a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getDeployedReceivedDelegations(UserInfoInterface userInfo) {
    return this.getReceivedDelegations(userInfo, nDEPLOYED_FLOWS);
  }

  /**
   * Returns undeployed flows received delegations
   *
   * @param userInfo the requesting user
   * @return  a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getUnDeployedReceivedDelegations(UserInfoInterface userInfo) {
    return this.getReceivedDelegations(userInfo, nUNDEPLOYED_FLOWS);
  }

  private Collection<DelegationInfoData> getReceivedDelegations(UserInfoInterface userInfo, int anMode) {
	  return getDelegations("DelegationInfoBean.RECEIVED_DELEGATIONS", userInfo.getUtilizador(), anMode);
  }

  /**
   * Returns all sent delegations, for both deployed and undeployed flows
   *
   * @param userInfo the requesting user
   * @return a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getSentDelegations(UserInfoInterface userInfo) {
    return this.getSentDelegations(userInfo, nALL_FLOWS);
  }

  /**
   * Returns deployed flows sent delegations
   *
   * @param userInfo the requesting user
   * @return  a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getDeployedSentDelegations(UserInfoInterface userInfo) {
    return this.getSentDelegations(userInfo, nDEPLOYED_FLOWS);
  }

  /**
   * Returns undeployed flows sent delegations
   *
   * @param userInfo the requesting user
   * @return  a collection of DelegationInfoData objects
   * @throws EJBException
   *
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<DelegationInfoData> getUnDeployedSentDelegations(UserInfoInterface userInfo) {
    return this.getSentDelegations(userInfo, nUNDEPLOYED_FLOWS);
  }

  private Collection<DelegationInfoData> getSentDelegations(UserInfoInterface userInfo, int anMode) {
	  return getDelegations("DelegationInfoBean.SENT_DELEGATIONS", userInfo.getUtilizador(), anMode);
  }

  private Collection<DelegationInfoData> getDelegations(String queryName, String username, int anMode) {
	    List<DelegationInfoData> retObj = new ArrayList<DelegationInfoData>();

	    DataSource ds = null;
	    Connection db = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;


	    try {
	        ds = Utils.getDataSource();
	        db = ds.getConnection();

	        String flowMode = "";
	        switch (anMode) {
	        case nDEPLOYED_FLOWS:
	      	  flowMode = " and b.enabled=1 ";
	      	  break;
	        case nUNDEPLOYED_FLOWS:
	      	  flowMode = " and b.enabled=0 ";
	      	  break;
	        case nALL_FLOWS:
	        default:
	      	  ;
	        }
	        
	        String query = DBQueryManager.processQuery(queryName, new Object[]{flowMode});
	        st = db.prepareStatement(query);
	        st.setString(1, username);
	        
	        rs = st.executeQuery();

	        while (rs.next()) {

	          DelegationInfoData data = this.buildInfo(rs);

	          retObj.add(data);

	        }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally {
          DatabaseInterface.closeResources(db, st, rs);	      
	    }

	    return retObj;
	  }

//  /**
//   *
//   * TODO Add comment for method getSentDelegations on DelegationInfoBean
//   *
//   * @param userInfo
//   * @return
//   * @throws EJBException
//   *
//   * @ejb.interface-method view-type = "remote"
//   */
//  public DelegationInfoData getDelegation(String delegationID) throws EJBException {
//    DelegationInfoData retObj = null;
//
//    return retObj;
//  }
//
//  /**
//   *
//   * TODO DUMMY DATA METHOD !!! Should be deleted after tests
//   *
//   * @return
//   * @throws EJBException
//   *
//   * @ejb.interface-method view-type = "remote"
//   */
//  public DelegationInfoData getDummyInfo() throws EJBException {
//    DelegationInfoData retObj = new DelegationInfoData();
//
//    retObj.setHierarchyID(1);
//    retObj.setFlowID(2);
//    retObj.setAcceptKey("key1");
//    retObj.setRejectKey("key2");
//    retObj.setOwnerID("owner");
//    retObj.setPending(1);
//    retObj.setExpires(new Date());
//    retObj.setUserID("user");
//
//    return retObj;
//  }

}
