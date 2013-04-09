package pt.iflow.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * Servlet implementation class for Servlet: FlowMenusServlet
 *
 *@web.servlet name="FlowMenus"
 *@web.servlet-mapping
 *  url-pattern="/Admin/flow_menu_edit"
 *@web.servlet-mapping
 *  url-pattern="/Admin/flow_menu_add"
 *@web.servlet-mapping
 *  url-pattern="/Admin/flow_menu_del"
 */
public class FlowMenusServlet  extends HttpServlet implements Servlet {
  static final long serialVersionUID = 1L;

  public FlowMenusServlet() {
    super();
  }
  
  public void init() throws ServletException {  }

  private void forward(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
    request.getRequestDispatcher(page).forward(request, response);
  }
  
  
  private void editMenusAction(HttpServletRequest request, UserInfoInterface userInfo) {
    boolean bAdminUser = userInfo.isOrgAdmin();
    HashSet<Integer> hsPrivs = new HashSet<Integer>();
    Flow flow = BeanFactory.getFlowBean();

    if (!bAdminUser) {// If not admin, list only flows that the user can access
      FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
      for (int i = 0; i < fra.length; i++) {

        if (fra[i].hasPrivilege(FlowRolesTO.CREATE_PRIV)) {
          hsPrivs.add(new Integer(fra[i].getFlowid()));
        }
      }
    }

    IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
    HashMap<Integer,String> flows = new HashMap<Integer, String>();
    
    for (IFlowData flowData : fda) {
      flows.put(flowData.getId(), flowData.getName());
    }


    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    Statement st2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    request.setAttribute("menuItems", null);
    
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      st2 = db.createStatement();

      ArrayList<FlowMenuItem> menuItems = new ArrayList<FlowMenuItem>();
      
      rs = st.executeQuery("select linkid,name,flowid from links_flows where parentid = 0 and organizationid='"+userInfo.getCompanyID()+"'");
      while (null != rs && rs.next()) {
        int linkid = rs.getInt("linkid");
        String name = rs.getString("name");
        int flowId = rs.getInt("flowid");
        if (flowId != 0) {
          name = flows.get(flowId);
        }
        
        FlowMenuItem item = new FlowMenuItem(linkid, name);
        menuItems.add(item);

        rs2 = st2.executeQuery("select linkid,name,flowid from links_flows where parentid = " + linkid+" and organizationid='"+userInfo.getCompanyID()+"'");
        while (null != rs2 && rs2.next()) {
          String childName = rs2.getString("name");
          int childId = rs2.getInt("linkid");
          int childFlowId = rs2.getInt("flowid");
          if (childFlowId != 0) {
            childName = flows.get(childFlowId);
          }
          // To hide the flows that have been undeployed
          if (childName == null || childName.equals("")) continue;
          
          item.addChildren(new FlowMenuItem(childId, childName));

        }
        rs2.close();
      }


      request.setAttribute("menuItems", menuItems);

    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      Utils.closeDB(null,st2,rs2);
      Utils.closeDB(db,st,rs);
    }


  }
  
  
  private void removeMenusAction(HttpServletRequest request, UserInfoInterface userInfo) {
    if (!userInfo.isOrgAdmin()) {
      return;
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    String stmp = request.getParameter("toDelete");
    if (stmp != null && !stmp.equals("")) {
      int id = Integer.parseInt(stmp);
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();

        st = db.prepareStatement("delete from links_flows where parentid = ? and organizationid=?");
        st.setInt(1, id);
        st.setString(2, userInfo.getCompanyID());
        st.executeUpdate();

        st = db.prepareStatement("delete from links_flows where linkid = ? and organizationid=?");
        st.setInt(1, id);
        st.setString(2, userInfo.getCompanyID());
        st.executeUpdate();

      } catch (SQLException sqle) {
        sqle.printStackTrace();
      } finally {
        Utils.closeDB(db,st,rs);
      }

      // limpa o HTML do unserInfo
      userInfo.setFlowPageHTML("");
    }

  }
  
  
  private boolean addMenuItemAction(HttpServletRequest request, UserInfoInterface userInfo) {
    boolean bAdminUser = userInfo.isOrgAdmin();
    if (!bAdminUser) {
      return true;
    }

    IMessages msg = userInfo.getMessages();

    int flowid = 0;
    int parentid = 0;

    String url = null;
    String texto = null;
    String sflowid = request.getParameter("addedflow");
    String sparentid = request.getParameter("parentid");

    if (StringUtils.isEmpty(sflowid) || StringUtils.isEmpty(sparentid)) {
      Logger.info(userInfo.getUtilizador(), this, "addMenuItemAction", "Nothing to add...");
      return false;
    }

    if (StringUtils.isNotEmpty(sflowid)) {
      try {
        flowid = Integer.parseInt(sflowid);
      } catch (NumberFormatException e) {
        Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Invalid flowid. Menu item not added.");
        return true;
      }
      if (flowid == 0) {
        texto = request.getParameter("texto");
        url = request.getParameter("url");
      } else {
        IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
        boolean flowFound = false;
        for (IFlowData flowData : fda) {
          if (flowid == flowData.getId()) {
            flowFound = true;
            break;
          }
        }
        if (!flowFound) {
          Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Flow id was not found");
          return true;
        }
      }
    }
    
    if (StringUtils.isNotEmpty(sparentid)) {
      try {
        parentid = Integer.parseInt(sparentid);
      } catch (NumberFormatException e) {
        Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Invalid parentid. Menu item not added.");
        return true;
      }
    }


    if(StringUtils.isEmpty(texto) && flowid==0) {
      request.setAttribute("err_msg", msg.getString("flow_menu_add.error.notext"));
      return false;
    }
    if(StringUtils.isEmpty(url) && flowid==0 && parentid != 0) {
      request.setAttribute("err_msg", msg.getString("flow_menu_add.error.nourl"));
      return false;
    }

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    boolean itemAdded = false;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      pst = db.prepareStatement(DBQueryManager.getQuery("FlowMenus.INSERT"));
      pst.setInt(1, parentid);
      pst.setInt(2, flowid);
      if (flowid == 0) {
        pst.setString(3, texto);
        pst.setString(4, url);
      } else {
        pst.setNull(3, java.sql.Types.VARCHAR);
        pst.setNull(4, java.sql.Types.VARCHAR);
      }
      pst.setString(5, userInfo.getCompanyID());
      pst.executeUpdate();
      // limpa o HTML do unserInfo
      userInfo.setFlowPageHTML("");

      itemAdded = true;
    } catch (SQLException sqle) {
        sqle.printStackTrace();
      } finally {
        Utils.closeDB(db,st,rs);
      }

    return itemAdded;
  }
  
  private void addMenuFormAction(HttpServletRequest request, UserInfoInterface userInfo) {
    boolean bAdminUser = userInfo.isOrgAdmin();
    HashSet<Integer> hsPrivs = new HashSet<Integer>();
    Flow flow = BeanFactory.getFlowBean();
    if (!bAdminUser) {
      FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
      for (int i = 0; i < fra.length; i++) {

        if (fra[i].hasPrivilege(FlowRolesTO.CREATE_PRIV)) {
          hsPrivs.add(new Integer(fra[i].getFlowid()));
        }
      }
    }

    IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
    
    HashMap<Integer,String> flows = new HashMap<Integer, String>();
    TreeSet<Integer> availableFlows = new TreeSet<Integer>();
    for (IFlowData flowData : fda) {
      flows.put(flowData.getId(), flowData.getName());
      availableFlows.add(flowData.getId());
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    
    request.setAttribute("rootItems", null);
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      // get all "used" flows
      st = db.prepareStatement("select distinct flowid from links_flows where flowid <> 0 and organizationid=?");
      st.setString(1, userInfo.getCompanyID());
      rs = st.executeQuery();
      
      while(rs.next()) {
        int flowid = rs.getInt("flowid");
        if(availableFlows.contains(flowid)) availableFlows.remove(flowid);
      }
      rs.close();
      st.close();
      
      ArrayList<FlowMenuItem> items = new ArrayList<FlowMenuItem>();
      
      st = db.prepareStatement("select distinct linkid, flowid, name from links_flows where parentid = 0 and organizationid=?");
      st.setString(1, userInfo.getCompanyID());
      rs = st.executeQuery();
      
      while (rs.next()) {
        int linkid = rs.getInt("linkid");
        String name = rs.getString("name");
        int flowid = rs.getInt("flowid");
        if (flowid != 0) {
          name = flows.get(flowid);
        }
        items.add(new FlowMenuItem(linkid, name));
      }

      request.setAttribute("rootItems", items);
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      Utils.closeDB(db,st,rs);
    }
    
    ArrayList<FlowMenuItem> flowItems = new ArrayList<FlowMenuItem>();
    for (Integer flowid : availableFlows) {
      flowItems.add(new FlowMenuItem(flowid, flows.get(flowid)));
    }
    request.setAttribute("flowItems", flowItems);

  }
  
  
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String name = request.getServletPath();
    String gotoPage = "/inc/defs.jsp";
    
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if(null != userInfo) {
      if(name.endsWith("flow_menu_add")) {
        boolean gotoEdditPage = addMenuItemAction(request, userInfo);
        if(gotoEdditPage) {
          editMenusAction(request, userInfo);
          gotoPage = "/Admin/flow_menu_edit.jsp";
        } else {
          addMenuFormAction(request, userInfo);
          gotoPage = "/Admin/flow_menu_add.jsp";
        }
      } else {
        removeMenusAction(request, userInfo);
        editMenusAction(request, userInfo);
        gotoPage = "/Admin/flow_menu_edit.jsp";
      }
    }
    forward(request, response, gotoPage);
  }

  
  public static class FlowMenuItem {
    private String name;
    private int linkid;
    private ArrayList<FlowMenuItem> children;
    
    public FlowMenuItem(int linkid, String name) {
      this.linkid = linkid;
      this.name = name;
      this.children = null;
    }
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public int getLinkid() {
      return linkid;
    }
    public void setLinkid(int linkid) {
      this.linkid = linkid;
    }
    public ArrayList<FlowMenuItem> getChildren() {
      return children;
    }
    public void addChildren(FlowMenuItem item) {
      if(null == children) children = new ArrayList<FlowMenuItem>();
      children.add(item);
    }
    
  }
}
