package pt.iflow.documents;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.sql.DataSource;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.applet.ImageIconRep;

import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfSampleImages {
  private static Log log = LogFactory.getLog(PdfSampleImages.class);

  private static int alturaPag = 841;
  private static int larguraPag = 595;
  private static Rectangle rect = null;
  /*********************************************************************************************************/  
  /********                      METODOS PARA MANIPULAR FICHEIROS PDF                              *********/
  /********                                                                                        *********/
  /*********************************************************************************************************/ 	  


  public BufferedImage getImageFromPdf(int docid,int pag){
    PDFFile pdffile = null;


    try {
      ByteBuffer buf = ByteBuffer.wrap(getDocument(docid));
      pdffile = new PDFFile(buf);
    } catch (Exception e) { log.error("Erro a adquirir imagens do pdf", e); } 

    PDFPage page = pdffile.getPage(pag);	
    rect = new Rectangle(0,0,(int)page.getBBox().getWidth(),(int)page.getBBox().getHeight());

    //	alturaPag  = rect.height;
    //	larguraPag = rect.width;
    Image img = page.getImage( larguraPag/2, alturaPag/2, rect, null, true, true);	
    BufferedImage bImg = toBufferedImage( img );
    return bImg;         
  }

  public BufferedImage toBufferedImage(Image image) {

    if (image instanceof BufferedImage) { return (BufferedImage)image;}


    image = new ImageIcon(image).getImage();
    boolean hasAlpha = true;
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      int transparency = Transparency.OPAQUE;
      if (hasAlpha) {
        transparency = Transparency.BITMASK;
      }
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
    } catch (HeadlessException e) { log.error("Erro a transformar Image em bufferedImage", e); }

    if (bimage == null) {
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha) {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }

    Graphics g = bimage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return bimage;
  }


  public void insertImage(int x, int y,int pagina, int docid, boolean rubricar, UserInfoInterface userInfo){

    File file2 = new File(Const.fUPLOAD_TEMP_DIR + File.separator + getGuidName()+".pdf");

     if(x == 0 && y == 0){
      x = larguraPag;          //caso nao seja selecionada nenhuma posição
      y = 0;                   //meter no canto inferior direito
    }else{
      y = alturaPag - y + (int)(alturaPag/50);  //inverter eixo e afinar posição (+18)
      x = x - (int)(larguraPag/40);              //afinar posição eliminado erro sistematico (-15)
    }

    try {
      FileOutputStream fileOutput = new FileOutputStream(file2);
      PdfReader reader = new PdfReader(getDocument(docid));
      PdfStamper stp = PdfStamper.createSignature(reader, fileOutput, '\0', null, true);  
      PdfSignatureAppearance sap = stp.getSignatureAppearance();  
      sap.setAcro6Layers(true);	
      reader.close();

      byte[] im = getImageAssinatura(userInfo);
      ByteArrayInputStream bis = new ByteArrayInputStream (im);
      ObjectInputStream ois = new ObjectInputStream(bis);

      ImageIconRep j = (ImageIconRep)(ois.readObject());    	
      com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(j.getImage(), null);

      if(y-j.getIconHeight() < 0)            y = (int)j.getIconHeight();
      if(x+j.getIconWidth() > larguraPag)    x = larguraPag - (int) j.getIconWidth();
      if(x < 0)                              x= 0;
      if(y > alturaPag)                      y= alturaPag;

      x= (x * rect.width)/larguraPag;
      y= (y * rect.height)/alturaPag;

      sap.setVisibleSignature(new com.lowagie.text.Rectangle(x, y-j.getIconHeight(), x+j.getIconWidth(), y), pagina, getGuidName());	
      sap.setLayer2Text("");
      sap.setImage(img);

      PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached")); //$NON-NLS-1$
      dic.setName(userInfo.getUserFullName());
      dic.setReason(sap.getReason());
      dic.setLocation(sap.getLocation());
      dic.setContact(sap.getContact());
      dic.setDate(new PdfDate(sap.getSignDate()));
      sap.setCryptoDictionary(dic);
      int contentEstimated = 15000;
      HashMap<Object,Object> exc = new HashMap<Object, Object>();
      exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));
      sap.preClose(exc);

      byte[] paddedSig = new byte[contentEstimated];
      PdfDictionary dic2 = new PdfDictionary();
      dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
      sap.close(dic2);

    } catch (Exception e) {log.error("Erro na assinatura do pdf", e);} 
    
    String tempfile=  "";
    
    if(rubricar) {                                                         //rubricar, gravar na BD e apagar temp             
      String temprub = rubricarTodas(tempfile, userInfo);

      try{
        InputStream input = new FileInputStream(temprub);
        byte[] buf = IOUtils.toByteArray(input); 
        saveDocument(docid, buf);
        input.close();
      } catch (Exception e) {log.error("Erro a gravar documento na BD", e);}      	  
      File temp = new File(temprub);
      temp.delete();

    }else{                                                                //caso não seja, copiar para BD e apagar temp
      try{
        InputStream input = new FileInputStream(tempfile);
        byte[] buf = IOUtils.toByteArray(input); 
        saveDocument(docid, buf);
        input.close();
      } catch (Exception e) {log.error("Erro a gravar documento na BD", e);}
      File filedelete = new File(tempfile);
      filedelete.delete();
    }
  }

  public String rubricarTodas(String filePath, UserInfoInterface userInfo){
    File file = new File(filePath + File.separator);
    int pageCount = 0;
    try {
      FileInputStream fileInput = new FileInputStream(file);	
      PdfReader reader = new PdfReader(fileInput);
      pageCount = reader.getNumberOfPages();
      fileInput.close();
      reader.close();
    } catch (Exception e) {e.printStackTrace();} 


    String fileOriginal = filePath;
    String fileAux = Const.fUPLOAD_TEMP_DIR + File.separator + getGuidName()+".pdf";;
    String fileAux2 = Const.fUPLOAD_TEMP_DIR + File.separator +getGuidName()+".pdf";;
    String lixoaux = "";

    //Primeira vez, Rubrica na 1ª pagina ou se é só uma pagina rubricar a unica
    insertImageRubrica(fileOriginal, 1, fileAux, userInfo);

    //Rubricar resto das paginas menos a ultima
    for(int i = 2; i < pageCount;i++){
      insertImageRubrica(fileAux,i, fileAux2, userInfo);
      lixoaux = fileAux;
      fileAux = fileAux2;
      fileAux2 = lixoaux;		
    }

    //Apagar os ficheiros Auxiliares e deixar só o final
    File filedelete = new File(fileAux2);
    filedelete.delete();
    File filedelete2 = new File(fileOriginal);
    filedelete2.delete();

    return fileAux;
  }

  public void insertImageRubrica(String fileRead, int pag, String fileWrite, UserInfoInterface userInfo){	 

    File file = new File("");
    File file2 = new File("");

    try {
      FileInputStream fileInput = new FileInputStream(file);
      FileOutputStream fileOutput = new FileOutputStream(file2);

      PdfReader reader = new PdfReader(fileInput);		
      PdfStamper stp = PdfStamper.createSignature(reader, fileOutput, '\0', null, true);  
      PdfSignatureAppearance sap = stp.getSignatureAppearance();  
      sap.setAcro6Layers(true);	

      reader.close();
      fileInput.close();

      byte[] im = getImageRubrica(userInfo);
      if (im == null) im = getImageAssinatura(userInfo);
      if (im == null) return;
      ByteArrayInputStream bis = new ByteArrayInputStream (im);
      ObjectInputStream ois = new ObjectInputStream(bis);
      ImageIconRep j = (ImageIconRep)(ois.readObject());     
      com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(j.getImage(), null);
      log.debug("Rubrica com tamanho "+j.getIconWidth()+"|"+j.getIconHeight());
      sap.setVisibleSignature(new com.lowagie.text.Rectangle(((int)rect.getWidth()-10)-(int)j.getIconWidth(), ((int)rect.getHeight()-10)-(int)j.getIconHeight(), (int)rect.getWidth()-10, (int)rect.getHeight()-10), pag, getGuidName());	
      log.debug("Tamanho da pagina "+(int)rect.getWidth()+"|"+(int)rect.getHeight());
      sap.setLayer2Text("");
      sap.setImage(img);

      PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached")); //$NON-NLS-1$
      dic.setReason(sap.getReason());
      dic.setLocation(sap.getLocation());
      dic.setContact(sap.getContact());
      dic.setDate(new PdfDate(sap.getSignDate()));
      sap.setCryptoDictionary(dic);
      int contentEstimated = 15000;
      HashMap<Object,Object> exc = new HashMap<Object, Object>();
      exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));
      sap.preClose(exc);
      byte[] paddedSig = new byte[contentEstimated];
      PdfDictionary dic2 = new PdfDictionary();
      dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
      sap.close(dic2);

    } catch (Exception e) {log.error("Erro a rubricar pdf", e);} 
  }


  public String getGuidName(){
    UUID uuid = UUID.randomUUID();
    return uuid.toString();    
  } 

  public int getNumPages(int docid){
    int pageCount = 0;
    try {
      PdfReader reader = new PdfReader(getDocument(docid));
      pageCount = reader.getNumberOfPages();
      reader.close();
    } catch (Exception e) {log.debug("O Documento é inválido ou não permite assinatura.");} 
    return pageCount;
  }

  public int[] getTamPage(){
    int[] tam = new int[2];
    tam[0] = (int)larguraPag/2;
    tam[1] = (int)alturaPag/2;
    return tam;
  }


  /*********************************************************************************************************/  
  /********                      METODOS PARA LER IMAGENS DA ASSINATURA                            *********/
  /********                                                                                        *********/
  /*********************************************************************************************************/ 

  public int[] getTamAss(UserInfoInterface userInfo){
    int [] tamanho = new int[2];
    try {
      byte[] img = getImageAssinatura(userInfo);
      ByteArrayInputStream bis = new ByteArrayInputStream (img);
      ObjectInputStream ois = new ObjectInputStream(bis);
      ImageIconRep j = (ImageIconRep)(ois.readObject());   
      //com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(j.getImage(), null);
      tamanho[0] = (int) j.getIconWidth()/2;
      tamanho[1] = (int) j.getIconHeight()/2;
      Logger.debug(userInfo.getUtilizador(),this,"getTamAss","Assinatura Obtida: larg="+tamanho[0]+" Alt="+tamanho[1]);
    } catch (Exception e) { Logger.debug(userInfo.getUtilizador(),this,"getTamAss","Não existe Assinatura na Base de dados para o utilizador!"); }
    return tamanho;
  }


  public byte[] getImageAssinatura(UserInfoInterface userInfo) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    byte[] img = null;

    log.debug("GetImageAssinatura, Execute query: SELECT passimage FROM user_passimage WHERE userid="+userInfo.getUserId()); 

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT passimage FROM user_passimage where userid=?");
      pst.setString(1, userInfo.getUserId());
      rs = pst.executeQuery();   

      rs.next(); 
      img = rs.getBytes(1);
    } 
    catch (SQLException e) {    
      log.debug(e);
      return null; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return img;
  }


  public byte[] getImageRubrica(UserInfoInterface userInfo) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    byte[] img = null;

    log.debug("GetImageAssinatura, Execute query: SELECT passimage FROM user_passimage WHERE userid="+userInfo.getUserId()); 

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT rubimage FROM user_passimage where userid=?");
      pst.setString(1, userInfo.getUserId());
      rs = pst.executeQuery();   

      rs.next(); 
      img = rs.getBytes(1);
    } 
    catch (SQLException e) {    return null; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return img;
  }  



  /*********************************************************************************************************/  
  /********                        METODOS PARA LER FICHEIRO A ASSINAR                             *********/
  /********                         permite ler da BD e de filesystem                              *********/
  /*********************************************************************************************************/ 


  private static byte[] getDocument(int docid){  
    String caminho = "";
    caminho = checkDocumentBD(docid);

    if(caminho == null || caminho.equals("")){         //Caso esteja na Base de dados

      return getDocumentFromDB(docid).getContent();

    }else{                                              //Caso esteja em Filesystem
      File fich = new File(caminho);    
      byte[] data = new byte[(int)fich.length()];
      try {

        FileInputStream fileInput = new FileInputStream(fich);
        fileInput.read(data);
        fileInput.close();
      } catch (IOException e) {  log.error("Erro a ler ficheiro de filesystem", e); }

      return data;
    }
  }


  private static String checkDocumentBD(int docid){
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String filepath = ""; 
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      pst = db.prepareStatement("SELECT docurl FROM documents WHERE docid=?");
      pst.setInt(1, docid);
      rs = pst.executeQuery();
      if(rs.next()) {
        filepath = rs.getString("docurl");
      } else {
        throw new Exception("File not found.");
      }
      rs.close();
      rs = null;
      pst.close();
      pst = null;
    } catch (Exception e) { log.error("Erro a ler caminho do ficheiro da BD", e);
    } finally {  DatabaseInterface.closeResources(pst, rs, db);}
    return filepath;
  }


  private static DocumentData getDocumentFromDB(int docid) {
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    DocumentData dbDoc = new DocumentData();
    ResultSet rs = null;
    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      pst = db.prepareStatement("SELECT docid,flowid,pid,subpid,filename,updated,datadoc FROM documents WHERE docid=?");
      pst.setInt(1, docid);
      rs = pst.executeQuery();
      if(rs.next()) {
        dbDoc.setDocId(rs.getInt("docid"));
        dbDoc.setFlowid(rs.getInt("flowid"));
        dbDoc.setPid(rs.getInt("pid"));
        dbDoc.setSubpid(rs.getInt("subpid"));
        dbDoc.setContent(rs.getBytes("datadoc"));
        dbDoc.setFileName(rs.getString("filename"));
      } else {
        throw new Exception("File not found.");
      }
      rs.close();
      rs = null;
      pst.close();
      pst = null;
    } catch (Exception e) {
      log.error("Erro a adquirir ficheiro da BD", e);
      dbDoc = null;
    }  finally {  DatabaseInterface.closeResources(pst, rs, db);}
    return dbDoc;
  }



  /*********************************************************************************************************/  
  /********                   METODOS PARA ESCRITA DO FICHEIRO ASSINADO                            *********/
  /********                     permite gravar na BD e em filesystem                               *********/
  /*********************************************************************************************************/   


  private static void saveDocument(int docid, byte[] doc){  
    String caminho = "";
    caminho = checkDocumentBD(docid);

    if(caminho == null || caminho.equals("")){         //Caso esteja na Base de dados

      updateDocument(docid, doc);

    }else{                                              //Caso esteja em Filesystem
      File fich = new File(caminho);    
      try {
        FileOutputStream fileOutput = new FileOutputStream(fich);
        fileOutput.write(doc);
        fileOutput.close();
      } catch (IOException e) {  log.error("Erro a ler ficheiro de filesystem", e); }


    }
  }


  public static void updateDocument(int docid, byte[] doc) {
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);  
      Date dateNow = new Date();

      String query = "UPDATE documents set datadoc=?,updated=? WHERE docid=?";
      pst = db.prepareStatement(query);
      pst.setBytes(1, doc);
      pst.setTimestamp(2, new java.sql.Timestamp(dateNow.getTime()));
      pst.setInt(3, docid);
      pst.executeUpdate();
      db.commit();

    } catch (Exception e) {
      log.error("Erro a gravar ficheiro na BD", e);
    }  finally {  DatabaseInterface.closeResources(pst, rs, db);}
  }



  public byte[] getImageAssinatura(String userId) {
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    byte[] img = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false); 
      pst = db.prepareStatement("SELECT passimage FROM user_passimage where userid="+userId);
      rs = pst.executeQuery();   

      rs.next(); 
      img = rs.getBytes(1);
    } 
    catch (SQLException e) {    
      log.debug(e);
      return null; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    return img;
  }
}
