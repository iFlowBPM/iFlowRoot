//
// BuildXmlLinks.java
//
// Copyright (C) 2004, iKnow - Consultoria em Tecnologias de Informacao, Lda
//

/**
 * <p>Title: BuildXmlLinks</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */
package pt.iflow.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.XslTransformerFactory;
import pt.iflow.flows.FlowData;

public class BuildXmlLinks
{
	     Connection _db = null;
		  Flow _flow = null;
        IFlowData[] _fda = null;
		  Hashtable<Integer,IFlowData> _htFlowData = new Hashtable<Integer,IFlowData>();
        int _userflowid = -1;
        String _userflowlink = null;
		  String _userflowtitle = null;
		  UserInfoInterface _userInfo = null;
	
	/**
	 * Constructor
	 */
	public BuildXmlLinks(UserInfoInterface userInfo, IFlowData[] fda)
	{
            try {
					_userInfo = userInfo;
              _fda = fda;
				  for (int i=0; fda!=null && i<fda.length; i++) {
					  _htFlowData.put(new Integer(fda[i].getId()), fda[i]);
				  }
            } catch (Exception e) {
              e.printStackTrace();
            }
	}

	public BuildXmlLinks(int userflowid, String userflowlink, String userflowtitle)
	{
            _userflowid    = userflowid;
            _userflowlink  = userflowlink;
				_userflowtitle = userflowtitle;
	}

//     public String getMainPageSonsXML(Integer parentid, long ts, boolean bA, boolean bC, boolean bW, boolean bR) {

//       String stmp = null;
//       Statement st = null;
//       ResultSet rs = null;
// 		StringBuffer sbSons = new StringBuffer();
//       Hashtable htname = new Hashtable();
//       Hashtable hturl  = new Hashtable();
//       Hashtable htpriv = new Hashtable();

//       try {
//         st = _db.createStatement();
//         rs = st.executeQuery("select * from links_uniflow where parentid = " + parentid.intValue());

//         while (rs.next()) {
//           stmp = rs.getString("name");
//           if (stmp == null) stmp = new String("");
//           htname.put(new Integer(rs.getInt("linkid")), stmp);

//           stmp = rs.getString("url");
//           if (stmp == null) stmp = new String("null");
//           hturl.put(new Integer(rs.getInt("linkid")), stmp);

//           stmp = rs.getString("privilege");
//           if (stmp == null) stmp = new String("");
//           htpriv.put(new Integer(rs.getInt("linkid")), stmp);
//         }

//       } catch (SQLException sqle) {
//         sqle.printStackTrace();
//       } finally {
//         try { rs.close(); rs = null; } catch (Exception e) {} 
//         try { st.close(); st = null; } catch (Exception e) {} 
//       }
		
		
//       for (Enumeration e = htname.keys(); e.hasMoreElements(); ) {
// 			boolean flag = false;
// 			StringBuffer sbtmp = new StringBuffer();
//         Integer linkid = (Integer)e.nextElement();
//         stmp = (String)htpriv.get(linkid);
//         if ((stmp == null) || stmp.equals(""))
//           continue;

//         if (stmp.equals("M") || /* Menu, user_hist, user_procs - everyone */
//             (bA && stmp.equals("A")) || /* Admin */
//             (bC && stmp.equals("C")) || /* Create */
//             (bW && stmp.equals("W")) || /* Write */
//             (bR && stmp.equals("R"))) { /* Read */

//            StringBuffer sbtmp2 = new StringBuffer();
//            sbtmp2.append(this.getMainPageSonsXML(linkid, ts, bA,bC,bW,bR));

//            /* Don't show root menus without sons */
//            if (stmp.equals("M") && 
//                (parentid.intValue() == 0) &&
//                (sbtmp2.length() == 0)) continue;

//            /* Create flows substituted when 'ufid' is passed in url */
//            if (stmp.equals("C") && (_userflowid != -1)) {
// 				  flag = true;
//               hturl.put(linkid, "inicio_flow.jsp?flowid=" + _userflowid);
//               htname.put(linkid, _userflowlink);
//            }

// 			  String nametmp = (String)htname.get(linkid);
// 			  if (this._userflowtitle != null && nametmp.equals("Flows")) {
// 				  sbtmp.append("<link>\n  <name url=\"null\">").append(this._userflowtitle);
// 			  } else {
// 	           sbtmp.append("<link>\n  <name url=\"").append((String)hturl.get(linkid));
// 				  if ( ((String)hturl.get(linkid)).indexOf("?") == -1) {
// 			        sbtmp.append("?ts=").append(ts);
// 				  }
// 					sbtmp.append("\">").append((String)htname.get(linkid));

// 			  }	  
//            sbtmp.append("</name>\n");
//            sbtmp.append(sbtmp2.toString());
//            sbtmp.append("</link>\n");
// 			  if (flag) sbSons.insert(0, sbtmp.toString());
// 			  else sbSons.append(sbtmp.toString());
//         }
//       }
//       return sbSons.toString();
//     }

//    public String getMainPageXML(Connection db, long ts, boolean bA, boolean bC, boolean bW, boolean bR) {
//      _db = db;
//      StringBuffer xmllinks = new StringBuffer();
//
//      xmllinks.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//      xmllinks.append("<XMLlinks>\n");
//      xmllinks.append(this.getMainPageSonsXML(new Integer(0), ts, bA, bC, bW, bR));
//      xmllinks.append("</XMLlinks>");
//      _db = null;
//      return xmllinks.toString();
//    }

    /* O XML gerado e' mt parecido com o da pagina principal*/
    public String getFlowsPageXML(Connection db, String flowsPrivs, long ts) {
      _db = db;
      StringBuffer xmllinks = new StringBuffer();

      String thisFlows = new String(" and flowid in (" + flowsPrivs + ")");
      xmllinks.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      xmllinks.append("<XMLlinks>\n");
      xmllinks.append(this.getFlowsPageSonsXML(thisFlows, new Integer(0), ts));
      xmllinks.append("</XMLlinks>");
      _db = null;

      return xmllinks.toString();
    }

	 private String getFlowsPageSonsXML(String thisFlows, Integer parentid, long ts) {
		 Statement st = null;
		 ResultSet rs = null;
		 StringBuffer sbtmp = new StringBuffer();
		 ArrayList<String> alname = new ArrayList<String>();
		 ArrayList<String> alurl  = new ArrayList<String>();
		 ArrayList<Integer> alflowid = new ArrayList<Integer>();
		 ArrayList<Integer> alid   = new ArrayList<Integer>();

		 try {
			 st = _db.createStatement();
			 rs = st.executeQuery("select * from links_flows where parentid = " + parentid.intValue() + thisFlows);

			 while (rs.next()) {
				 alname.add(rs.getString("name"));
				 alurl.add(rs.getString("url"));
				 alid.add(new Integer(rs.getInt("linkid")));
				 alflowid.add(new Integer(rs.getInt("flowid")));
			 }
			 if (alid.size() == 0) throw new Exception("No more childs");
			 rs.close();
			 rs = null;

			 Iterator<String> iname = alname.iterator();
			 Iterator<String> iurl = alurl.iterator();
			 Iterator<Integer> iid = alid.iterator();
			 Iterator<Integer> iflowid = alflowid.iterator();

			 while ((alname.size() > 0) && iid.hasNext()) {
				 Integer flowid = iflowid.next();
				 String name = iname.next();
				 String url = iurl.next();
				 Integer id = iid.next();

				 if (flowid.intValue() == 0) {
					 if (url != null && !url.equals("")) {
						 sbtmp.append("<link>\n  <name url=\"").append(url);
						 sbtmp.append("?ts=").append(ts).append("\">").append(name);
						 sbtmp.append("</name>\n");
					 } else {
						 rs = st.executeQuery("select * from links_flows where parentid = " + id + thisFlows);
						 boolean hasSomeDeployed = false;

						 while (rs.next()) {
							 Integer iSonFlowId = new Integer(rs.getInt("flowid"));
							 if (_htFlowData.containsKey(iSonFlowId)) {
								sbtmp.append("<link>\n  <name url=\"").append(url);
								 sbtmp.append("?ts=").append(ts).append("\">").append(name);
								 sbtmp.append("</name>\n");
								 hasSomeDeployed = true;
								 break;
							 }
						 }
						 rs.close();
						 
						 if (!hasSomeDeployed) continue;
					 }
				 } else {

					 IFlowData fdtmp = _htFlowData.get(flowid);
					 name = fdtmp.getName();

					 sbtmp.append("<link>\n  <name url=\"").append("inicio_flow.jsp?flowid=");
					 sbtmp.append(flowid).append("&amp;ts=").append(ts).append("\">");
					 sbtmp.append(name).append("</name>\n");
				 }
				 sbtmp.append(this.getFlowsPageSonsXML(thisFlows, id, ts));
				 sbtmp.append("</link>\n");
			 }
		 } catch (SQLException sqle) {
			 sqle.printStackTrace();
		 } catch (Exception e) { }
		 finally {
			 try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return sbtmp.toString();
	 }

	 // FIXME Verificar que este nao causa transtorno a /WebContent/flows.jsp
	 public String getHTMLTransformation(UserInfoInterface userInfo, String sXsl, String sXml) {
	   String sHtml = null;

	   try {
	     Transformer transformer = XslTransformerFactory.getTransformer(userInfo, sXsl);
	     InputStream isInStream = new ByteArrayInputStream(sXml.getBytes("UTF-8"));
	     OutputStream osOutStream = new ByteArrayOutputStream();
	     transformer.transform(new StreamSource(isInStream), new StreamResult(osOutStream));
	     sHtml = osOutStream.toString();

	   } catch (Exception ei) {
	     sHtml = "StyleSheet inv&aacute;lida.";
	     ei.printStackTrace();
	   }

	   return sHtml;
	 }

}
