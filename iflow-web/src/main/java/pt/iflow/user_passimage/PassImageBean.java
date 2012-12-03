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
package pt.iflow.user_passimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import pt.iflow.api.core.PassImage;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.applet.ImageIconRep;

public class PassImageBean implements PassImage {

  private static PassImageBean instance = null;

  public static PassImageBean getInstance() {
    if (null == instance)
      instance = new PassImageBean();
    return instance;
  }

  public byte[] getImage(UserInfoInterface userInfo) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    byte[] img = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT passimage FROM user_passimage where userid=?");
      pst.setString(1, userInfo.getUserId());
      rs = pst.executeQuery();   

      rs.next(); 
      img = rs.getBytes(1);
    } 
    catch (SQLException e) {	return null; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return img;
  }
  
  public byte[] getRubricImage(UserInfoInterface userInfo) {
	    Connection db = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    byte[] img = null;

	    try {
	      db = DatabaseInterface.getConnection(userInfo);
	      pst = db.prepareStatement("SELECT rubimage FROM user_passimage where userid=?");
	      pst.setString(1, userInfo.getUserId());
	      rs = pst.executeQuery();   

	      rs.next(); 
	      img = rs.getBytes(1);
	    } 
	    catch (SQLException e) {	return null; } 
	    finally {  DatabaseInterface.closeResources(pst, rs, db);}

	    return img;
	  }

  public void saveImage(UserInfoInterface userInfo, byte[] img) {
    
    if (img==null) return;

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int n = -1;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("UPDATE user_passimage SET passimage=? WHERE userid=?");
      pst.setBytes(1, img);
      pst.setString(2, userInfo.getUserId());


      n = pst.executeUpdate();   
    } 
    catch (Exception e) {e.printStackTrace();} 

    try {
      if(n <= 0){ //caso nao tenha feito o update
        db = DatabaseInterface.getConnection(userInfo);
        pst = db.prepareStatement("INSERT into user_passimage (userid, passimage) values (?,?)");
        pst.setString(1, userInfo.getUserId());
        pst.setBytes(2, img);

        pst.executeUpdate(); 
      }
    } 
    catch (SQLException ex) {	ex.printStackTrace(); }	
    finally {  DatabaseInterface.closeResources(pst, rs, db);}
  }

  public void saveRubrica(UserInfoInterface userInfo, byte[] img) {
    
    if (img==null) return;

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int n = -1;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("UPDATE user_passimage SET rubimage=? WHERE userid=?");
      pst.setBytes(1, img);
      pst.setString(2, userInfo.getUserId());

      n = pst.executeUpdate();   
    } 
    catch (Exception e) {e.printStackTrace();} 

    try {
      if(n <= 0){ //caso nao tenha feito o update
        db = DatabaseInterface.getConnection(userInfo);
        pst = db.prepareStatement("INSERT into user_passimage (userid, rubimage) values (?,?)");
        pst.setString(1, userInfo.getUserId());
        pst.setBytes(2, img);

        pst.executeUpdate(); 
      }
    } 
    catch (SQLException ex) {   ex.printStackTrace(); } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}
  }

  public int getNumAss(UserInfoInterface userInfo, int docid) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int num = -1;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT numass FROM documents where docid=?");
      pst.setInt(1, docid);
      rs = pst.executeQuery();   

      rs.next(); 
      num = rs.getInt(1);
    } 
    catch (SQLException e) {	return -1; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return num;
  }

  public void updateNumAss(UserInfoInterface userInfo, int docid, int numass) {
    Connection db = null;
    PreparedStatement pst = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("UPDATE documents set numass = ? where docid=?");
      pst.setInt(1, numass);
      pst.setInt(2, docid);
      pst.executeUpdate();   
    } 
    catch (SQLException e) {	return; } 
    finally {  DatabaseInterface.closeResources(pst, db);}

  }

  public void teste(UserInfoInterface userInfo){
    //OBTER TODAS AS IMAGES
    java.sql.Connection db = null;
    java.sql.PreparedStatement pst = null;
    java.sql.ResultSet rs = null;
    byte[] imgBytes = null;
    int passid = -1;

    try {
      db = pt.iflow.api.db.DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT passimage,passid FROM user_passimage");
      rs = pst.executeQuery();   

      while(rs.next()){

        imgBytes = rs.getBytes(1);
        passid = rs.getInt(2);

        try{
          //CONVERTER PARA VERSAO ANTIGA
          InputStream is = new ByteArrayInputStream(imgBytes);	      
          ObjectInputStream ois = new ObjectInputStream(is);  

          Object ob = ois.readObject();
          javax.swing.ImageIcon imageVersion = (javax.swing.ImageIcon)(ob);

          //javax.swing.ImageIcon imageVersion = (javax.swing.ImageIcon)(ois.readObject());
          ois.close();

          //CONVERTER PARA NEUTRO
          java.awt.Image im = imageVersion.getImage();

          //CONVERTER PARA NOVO FORMATO
          pt.iflow.applet.ImageIconRep imageRep = new pt.iflow.applet.ImageIconRep(im.getScaledInstance(-1, -1, java.awt.Image.SCALE_SMOOTH));        
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutputStream out2 = new ObjectOutputStream(bos);
          out2.writeObject(imageRep);
          out2.close();



          //ACTUALIZAR IMAGEM NA BD
          java.sql.Connection db2 = null;
          java.sql.PreparedStatement pst2 = null;
          java.sql.ResultSet rs2 = null;
          @SuppressWarnings("unused")
          int n = -1;

          try {
            db2 = pt.iflow.api.db.DatabaseInterface.getConnection(userInfo);
            pst2 = db2.prepareStatement("UPDATE user_passimage SET passimage=? WHERE passid=?");
            pst2.setBytes(1, bos.toByteArray());
            pst2.setString(2, ""+passid);
            n = pst2.executeUpdate();   
          } 
          catch (java.sql.SQLException e) {
            Logger.warning(userInfo.getUtilizador(), this, "Version Control", "Erro no Update da imagem! ID:"+passid, e);
          } finally {  pt.iflow.api.db.DatabaseInterface.closeResources(pst2, rs2, db2);}



          Logger.warning(userInfo.getUtilizador(), this, "Version Control", "Era desta versão! ID:"+passid);

        }catch(Exception e){
          Logger.warning(userInfo.getUtilizador(), this, "Version Control", "Nao era desta versão! ID:"+passid);
        }
      }

    } 
    catch (java.sql.SQLException e) {	
      Logger.warning(userInfo.getUtilizador(), this, "Version Control", "Erro a ler da BD!", e);
    } 
    finally {  pt.iflow.api.db.DatabaseInterface.closeResources(pst, rs, db);}
  }

  public byte[] getImageUser(String userid){
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    byte[] img = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      pst = db.prepareStatement("SELECT passimage FROM user_passimage where userid='"+userid+"'");
      rs = pst.executeQuery();   

      rs.next(); 
      img = rs.getBytes(1);
    } 
    catch (SQLException e) {	return null; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return img;
  }

  public byte[] getImageIconRepFromImage(byte[] bytes) {
    try {
      //ByteArrayInputStream bis = new ByteArrayInputStream(byteImage);
      //ObjectInputStream ois = new ObjectInputStream(bis);
      //Image image = (Image)(ois.readObject());
      ImageIconRep imageIconRep = new ImageIconRep(bytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(imageIconRep);
      return baos.toByteArray();
    } catch (Exception e) {
      return null;
    }
  }
}
