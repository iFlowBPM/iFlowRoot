package pt.iflow.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ibm.wsdl.util.StringUtils;

import bsh.StringUtil;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.connector.document.DMSDocument;
import pt.iknow.utils.StringUtilities;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author iKnow
 * 
 * @web.servlet name="AnnotationIconsServlet"
 * 
 * @web.servlet-mapping url-pattern="/AnnotationIconsServlet"
 */
public class AnnotationIconsServlet extends HttpServlet {
	private static final long serialVersionUID = -9101755201777404343L;
	private static final String REQUEST_PARAMETER_ICON_NAME = "icon_name";
	private static final String REQUEST_PARAMETER_LABEL_NAME = "label_name";

	public AnnotationIconsServlet() {
	}

	public void init() {
	}

	private static void copyTo(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[8092];
		int r = -1;
		while ((r = in.read(b)) != -1)
			out.write(b, 0, r);
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream input = null;

		try {
			Repository rep = BeanFactory.getRepBean();
			int size = 0;

			UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);

			//String iconName = ;
			
			String iconName = AnnotationIconsEnum.lookup(request.getParameter(REQUEST_PARAMETER_ICON_NAME)).getCode();

			if (StringUtilities.isEmpty(iconName)) {
				String labelName = request.getParameter(REQUEST_PARAMETER_LABEL_NAME);
				if (!StringUtilities.isEmpty(labelName)) {
					if ((labelName.charAt(0)) == '\'') {
						labelName = labelName.substring(1, labelName.length() - 1);
					}
					iconName = this.getIconFileName(userInfo, labelName);
				}
			} else {
				if ((iconName.charAt(0)) == '\'') {
					iconName = iconName.substring(1, iconName.length() - 1);
				}
			}

			if (null != userInfo && !pt.iflow.applet.StringUtils.isEmpty(iconName)) {
				RepositoryFile repFile = rep.getAnnotationIcon(userInfo, iconName);
				input = repFile.getResourceAsStream();
				size = repFile.getSize();
			}

			if (input == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
				return;
			}

			//response.setHeader("Content-Disposition", "inline;filename=" + iconName + ".png");
			OutputStream out = response.getOutputStream();
			response.setContentLength(size);
			copyTo(input, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private String getIconFileName(UserInfoInterface userInfo, String labelName) {
		Connection db = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		String iconFileName = null;
		String LABEL_ICON_COLLUM = "icon";
		try {
			db = DatabaseInterface.getConnection(userInfo);
			db.setAutoCommit(false);

			StringBuffer query = new StringBuffer();
			query.append("SELECT L.icon ");
			query.append(" FROM label L ");
			query.append(" WHERE L.name like ?");

			pst = db.prepareStatement(query.toString());
			pst.setString(1, labelName);

			rs = pst.executeQuery();

			if (rs.next()) {
				iconFileName = rs.getString(LABEL_ICON_COLLUM);
				Logger.debug(userInfo.getUtilizador(), this, "getIconFileName",
						"Found Icon name: Label [" + labelName + "], icon [" + iconFileName + "]");
			}

		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), this, "getIconFileName", "Unable to get label icon file name", e);
		} finally {
			DatabaseInterface.closeResources(db, pst, rs);
		}
		return iconFileName;
	}
	
	enum AnnotationIconsEnum {
		URGENT("label_urgent.png"), IMPORTANT("label_important.png"), NORMAL("label_normal.png"),CLOCK("label_clock.png"),COMMENT("label_comment_blue.png");

		private String code;

		private AnnotationIconsEnum(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	public static AnnotationIconsEnum lookup(String code){
		for(AnnotationIconsEnum element : AnnotationIconsEnum.values()){
			if(element.getCode().equals(code))
				return element;
		} 
		
		throw new IllegalArgumentException("Invalid Code AnnotationIconEnum");
		
	}

}
}

