package pt.iflow.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class PersistSession {

	public void getSession(UserInfoInterface userInfo, HttpSession session) {
		String userid = userInfo.getUtilizador();
		Connection db = null;
		Statement st = null;
		ResultSet rs = null;
		Object[][] valores = new Object[0][2]; 
		byte[] buf = null;

		try {
			db = DatabaseInterface.getConnection(userInfo);
			st = db.createStatement();
			rs = st.executeQuery("select session from user_session where userid = '" + userid + "'");

			if (rs.next()) {
				buf = rs.getBytes("session");
			}
			/* TODO - Validar com a nova interface
			if (buf != null) {
				ValidatingObjectInputStream validator = new ValidatingObjectInputStream(new ByteArrayInputStream(buf));
				validator.accept(SessionObject.class);
				//ByteArrayInputStream bais = new ByteArrayInputStream(buf);
				//ObjectInputStream objectIn = new ObjectInputStream(bais);
				valores = ((SessionObject) validator.readObject()).getValores();
				validator.close();
			}
			 */
			rs.close();
			rs = null;
		} catch (SQLException sqle) {
			Logger.error(userid, "PersistSession", "getSession", "caught sql exception: " + sqle.getMessage(), sqle);
		} catch (Exception e) {
			Logger.error(userid, "PersistSession", "getSession", "caught exception: " + e.getMessage(), e);
		} finally {
			DatabaseInterface.closeResources(db, st, rs);
		}

		for (int i = 0; i < valores.length; i++) {
			session.setAttribute((String) valores[i][0], valores[i][1]);
		}
	}

	public void setSession(UserInfoInterface userInfo, HttpSession session) {
		SessionObject valores = new SessionObject(session);
		int rows = 0;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			oout.writeObject(valores);
			oout.close();
		} catch (IOException e) {
			return;
		} catch (Exception ex) {
			Logger.error(userInfo.getUtilizador(), "PersistSession", "setSession",
					"caught exception: " + ex.getMessage(), ex);
			return;
		}

		Connection db = null;
		PreparedStatement pst = null;
		try {
			db = DatabaseInterface.getConnection(userInfo);
			pst = db.prepareStatement(
					"Update user_session set session=? where userid='" + userInfo.getUtilizador() + "'");
			pst.setBytes(1, baos.toByteArray());
			rows = pst.executeUpdate();

			if (rows <= 0) {
				db = DatabaseInterface.getConnection(userInfo);
				pst = db.prepareStatement(
						"insert into user_session (userid, session) values ('" + userInfo.getUtilizador() + "',?)");
				pst.setBytes(1, baos.toByteArray());
				pst.execute();
			}
		} catch (SQLException sqle) {
			Logger.error(userInfo.getUtilizador(), "PersistSession", "setSession",
					"caught sql exception: " + sqle.getMessage(), sqle);
		} finally {
			DatabaseInterface.closeResources(db, pst);
		}
	}

}
