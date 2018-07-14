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
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class FlowMenusServlet
  extends HttpServlet
  implements Servlet
{
  static final long serialVersionUID = 1L;
  
  public void init()
    throws ServletException
  {}
  
  private void forward(HttpServletRequest request, HttpServletResponse response, String page)
    throws ServletException, IOException
  {
    request.getRequestDispatcher(page).forward(request, response);
  }
  
  private void editMenusAction(HttpServletRequest request, UserInfoInterface userInfo)
  {
    boolean bAdminUser = userInfo.isOrgAdmin();
    HashSet<Integer> hsPrivs = new HashSet();
    Flow flow = BeanFactory.getFlowBean();
    if (!bAdminUser)
    {
      FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
      for (int i = 0; i < fra.length; i++) {
        if (fra[i].hasPrivilege('C')) {
          hsPrivs.add(new Integer(fra[i].getFlowid()));
        }
      }
    }
    IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
    HashMap<Integer, String> flows = new HashMap();
    for (IFlowData flowData : fda) {
      flows.put(Integer.valueOf(flowData.getId()), flowData.getName());
    }
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    PreparedStatement st2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    request.setAttribute("menuItems", null);
    try
    {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.prepareStatement("select linkid,name,flowid from links_flows where parentid = 0 and organizationid='?'");
      st.setString(1, userInfo.getCompanyID());
      ArrayList<FlowMenusServlet.FlowMenuItem> menuItems = new ArrayList();
      
      rs = st.executeQuery();
      while ((null != rs) && (rs.next()))
      {
        int linkid = rs.getInt("linkid");
        String name = rs.getString("name");
        int flowId = rs.getInt("flowid");
        if (flowId != 0) {
          name = (String)flows.get(Integer.valueOf(flowId));
        }
        FlowMenusServlet.FlowMenuItem item = new FlowMenusServlet.FlowMenuItem(linkid, name);
        menuItems.add(item);
        
        st2 = db.prepareStatement("select linkid,name,flowid from links_flows where parentid = ? and organizationid='?'");
        st2.setInt(1, linkid);
        st2.setString(2, userInfo.getCompanyID());
        rs2 = st2.executeQuery();
        while ((null != rs2) && (rs2.next()))
        {
          String childName = rs2.getString("name");
          int childId = rs2.getInt("linkid");
          int childFlowId = rs2.getInt("flowid");
          if (childFlowId != 0) {
            childName = (String)flows.get(Integer.valueOf(childFlowId));
          }
          if ((childName != null) && (!childName.equals(""))) {
            item.addChildren(new FlowMenusServlet.FlowMenuItem(childId, childName));
          }
        }
        rs2.close();
      }
      request.setAttribute("menuItems", menuItems);
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    finally
    {
      Utils.closeDB(new Object[] { null, st2, rs2 });
      Utils.closeDB(new Object[] { db, st, rs });
    }
  }
  
  private void removeMenusAction(HttpServletRequest request, UserInfoInterface userInfo)
  {
    if (!userInfo.isOrgAdmin()) {
      return;
    }
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    
    String stmp = request.getParameter("toDelete");
    if ((stmp != null) && (!stmp.equals("")))
    {
      int id = Integer.parseInt(stmp);
      try
      {
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
      }
      catch (SQLException sqle)
      {
        sqle.printStackTrace();
      }
      finally
      {
//        Utils.closeDB(new Object[] {db, st, rs });
        	try {if (db != null) db.close(); } catch (SQLException e) {}
          	try {if (st != null) st.close(); } catch (SQLException e) {}
          	try {if (rs != null) rs.close(); } catch (SQLException e) {}  
      }
      userInfo.setFlowPageHTML("");
    }
  }
  
  private boolean addMenuItemAction(HttpServletRequest request, UserInfoInterface userInfo)
  {
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
    if ((StringUtils.isEmpty(sflowid)) || (StringUtils.isEmpty(sparentid)))
    {
      Logger.info(userInfo.getUtilizador(), this, "addMenuItemAction", "Nothing to add...");
      return false;
    }
    if (StringUtils.isNotEmpty(sflowid))
    {
      try
      {
        flowid = Integer.parseInt(sflowid);
      }
      catch (NumberFormatException e)
      {
        Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Invalid flowid. Menu item not added.");
        return true;
      }
      if (flowid == 0)
      {
        texto = request.getParameter("texto");
        url = request.getParameter("url");
      }
      else
      {
        IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
        boolean flowFound = false;
        for (IFlowData flowData : fda) {
          if (flowid == flowData.getId())
          {
            flowFound = true;
            break;
          }
        }
        if (!flowFound)
        {
          Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Flow id was not found");
          return true;
        }
      }
    }
    if (StringUtils.isNotEmpty(sparentid)) {
      try
      {
        parentid = Integer.parseInt(sparentid);
      }
      catch (NumberFormatException e)
      {
        Logger.warning(userInfo.getUtilizador(), this, "addMenuItemAction", "Invalid parentid. Menu item not added.");
        return true;
      }
    }
    if ((StringUtils.isEmpty(texto)) && (flowid == 0))
    {
      request.setAttribute("err_msg", msg.getString("flow_menu_add.error.notext"));
      return false;
    }
    if ((StringUtils.isEmpty(url)) && (flowid == 0) && (parentid != 0))
    {
      request.setAttribute("err_msg", msg.getString("flow_menu_add.error.nourl"));
      return false;
    }
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    boolean itemAdded = false;
    try
    {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      
      pst = db.prepareStatement(DBQueryManager.getQuery("FlowMenus.INSERT"));
      pst.setInt(1, parentid);
      pst.setInt(2, flowid);
      if (flowid == 0)
      {
        pst.setString(3, texto);
        pst.setString(4, url);
      }
      else
      {
        pst.setNull(3, 12);
        pst.setNull(4, 12);
      }
      pst.setString(5, userInfo.getCompanyID());
      pst.executeUpdate();
      
      userInfo.setFlowPageHTML("");
      
      itemAdded = true;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    finally
    {
      // Utils.closeDB(new Object[] {db, st, rs });
      	try {if (db != null) db.close(); } catch (SQLException e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (pst != null) pst.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return itemAdded;
  }
  
  private void addMenuFormAction(HttpServletRequest request, UserInfoInterface userInfo)
  {
    boolean bAdminUser = userInfo.isOrgAdmin();
    HashSet<Integer> hsPrivs = new HashSet();
    Flow flow = BeanFactory.getFlowBean();
    if (!bAdminUser)
    {
      FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
      for (int i = 0; i < fra.length; i++) {
        if (fra[i].hasPrivilege('C')) {
          hsPrivs.add(new Integer(fra[i].getFlowid()));
        }
      }
    }
    IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
    
    HashMap<Integer, String> flows = new HashMap();
    TreeSet<Integer> availableFlows = new TreeSet();
    for (IFlowData flowData : fda)
    {
      flows.put(Integer.valueOf(flowData.getId()), flowData.getName());
      availableFlows.add(Integer.valueOf(flowData.getId()));
    }
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    
    request.setAttribute("rootItems", null);
    String name;
    try
    {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      

      st = db.prepareStatement("select distinct flowid from links_flows where flowid <> 0 and organizationid=?");
      st.setString(1, userInfo.getCompanyID());
      rs = st.executeQuery();
      while (rs.next())
      {
        int flowid = rs.getInt("flowid");
        if (availableFlows.contains(Integer.valueOf(flowid))) {
          availableFlows.remove(Integer.valueOf(flowid));
        }
      }
      rs.close();
      st.close();
      
      ArrayList<FlowMenuItem> items = new ArrayList();
      
      st = db.prepareStatement("select distinct linkid, flowid, name from links_flows where parentid = 0 and organizationid=?");
      st.setString(1, userInfo.getCompanyID());
      rs = st.executeQuery();
      while (rs.next())
      {
        int linkid = rs.getInt("linkid");
        name = rs.getString("name");
        int flowid = rs.getInt("flowid");
        if (flowid != 0) {
          name = (String)flows.get(Integer.valueOf(flowid));
        }
        items.add(new FlowMenuItem(linkid, name));
      }
      request.setAttribute("rootItems", items);
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    finally
    {
      Utils.closeDB(new Object[] {db, st, rs });
    }
    ArrayList<FlowMenuItem> flowItems = new ArrayList();
    for (Integer flowid : availableFlows) {
      flowItems.add(new FlowMenuItem(flowid.intValue(), (String)flows.get(flowid)));
    }
    request.setAttribute("flowItems", flowItems);
  }
  
  protected void service(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException
		  {
		    String name = request.getServletPath();
		    String gotoPage = "/inc/defs.jsp";
		    
		    UserInfoInterface userInfo = (UserInfoInterface)request.getSession().getAttribute("UserInfo");
		    if (null != userInfo) {
		      if (name.endsWith("flow_menu_add"))
		      {
		        boolean gotoEdditPage = addMenuItemAction(request, userInfo);
		        if (gotoEdditPage)
		        {
		          editMenusAction(request, userInfo);
		          gotoPage = "/Admin/flow_menu_edit.jsp";
		        }
		        else
		        {
		          addMenuFormAction(request, userInfo);
		          gotoPage = "/Admin/flow_menu_add.jsp";
		        }
		      }
		      else
		      {
		        removeMenusAction(request, userInfo);
		        editMenusAction(request, userInfo);
		        gotoPage = "/Admin/flow_menu_edit.jsp";
		      }
		    }
		    forward(request, response, gotoPage);
		  }
  
  public static class FlowMenuItem
  {
    private String name;
    private int linkid;
    private ArrayList<FlowMenuItem> children;
    
    public FlowMenuItem(int linkid, String name)
    {
      this.linkid = linkid;
      this.name = name;
      this.children = null;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public void setName(String name)
    {
      this.name = name;
    }
    
    public int getLinkid()
    {
      return this.linkid;
    }
    
    public void setLinkid(int linkid)
    {
      this.linkid = linkid;
    }
    
    public ArrayList<FlowMenuItem> getChildren()
    {
      return this.children;
    }
    
    public void addChildren(FlowMenuItem item)
    {
      if (this.children == null) {
        this.children = new ArrayList();
      }
      this.children.add(item);
    }
  }
}
