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
package pt.iflow.documents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.PassImage;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.applet.IDEntry;
import pt.iflow.applet.ImageIconRep;

import com.infosistema.crypto.CryptoUtils;
import com.lowagie.text.pdf.OcspClientBouncyCastle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSigGenericPKCS;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.TSAClient;


public class RubricarDocumento {
	
	
public void insertImage(int docid, UserInfoInterface userInfo){
		
		  try{		  
			  PdfReader reader = new PdfReader(getDocument(docid));	 	
			  String temprub = rubricarTodas(reader, userInfo);
	
	          InputStream input = new FileInputStream(temprub);
	          byte[] buf = IOUtils.toByteArray(input); 
	          saveDocument(docid, buf);
	          input.close();
	
	          File temp = new File(temprub);
		      temp.delete();
		      
		  } catch (Exception e) {}
	  }
	
	
public String rubricarTodas(PdfReader reader, UserInfoInterface userInfo){
		int pageCount = 0;
		try {
			pageCount = reader.getNumberOfPages();
			reader.close();
		} catch (Exception e) {e.printStackTrace();} 
		String fileAux = Const.fUPLOAD_TEMP_DIR+"\\"+getGuidName()+".pdf";
		insertImageRubrica(reader, pageCount, fileAux, userInfo);
		return fileAux;
}
	  
public void insertImageRubrica(PdfReader reader, int pageCount, String fileWrite, UserInfoInterface userInfo){	
	
		try {
			//Criar Modelo para as Rubricas
		  	PassImage pi = BeanFactory.getPassImageManagerBean();
	        byte[] im = pi.getImage(userInfo);	        
	        InputStream is = new ByteArrayInputStream(im);	      
	        ObjectInputStream ois = new ObjectInputStream(is);  
	        ImageIconRep image = (ImageIconRep)(ois.readObject());
	        ois.close();
	        com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(image.getImage(), null);
	        
	        int iniX = 70;
			int iniY = 110;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        reader = new PdfReader(reader);   
	        PdfStamper stp1 = new PdfStamper(reader, out, '\3', true);
	        PdfFormField sig = PdfFormField.createSignature(stp1.getWriter());
	        sig.setWidget(new com.lowagie.text.Rectangle(iniX,iniY, iniX + img.getWidth(), iniY + img.getHeight()), null);
	        sig.setFlags(PdfAnnotation.FLAGS_PRINT);
	        sig.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g"));
	        sig.setFieldName("Rubricas");
	        sig.setPage(1);
	
	        //Inserir em todas as paginas o Modelo
	        for(int i = 1; i<pageCount; i++)
	        	stp1.addAnnotation(sig, i);
	        
	        stp1.close();
	        out.close();          
	     
	        //Guardar/Ler PDF com modelos inseridos
	        FileOutputStream fout = new FileOutputStream (fileWrite);
	        reader = new PdfReader(out.toByteArray());
	        
	        //Preencher Modelo com Dados
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
			PdfSignatureAppearance sap = stp.getSignatureAppearance();  
		  	sap.setAcro6Layers(true);
		  	reader.close();	  	
			sap.setVisibleSignature("Rubricas");
	    	sap.setLayer2Text("\n\n(Doc. assinado digitalmente)");
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
		    //---------------------------------------------------------------
		  
		    	String alias = Const.sALIAS;
		    	String keystorePath = Const.sKEYSTORE_PATH;
		    	String keystorePass = Const.sKEYSTORE_PASS;
		    	String aliasPass = Const.sALIAS_PASS;
		    	boolean sign = false;
Security.getProviders();
	    if(alias != null && keystorePath != null && keystorePass != null && aliasPass!=null){

	    	try{
	    		FileInputStream fileIn = new FileInputStream(keystorePath);
		        //KeyStore keyStore2 = KeyStore.getInstance("Windows-ROOT");
		        
		        KeyStore keyStore = KeyStore.getInstance("SHA1withDSA","RSA");
		        keyStore.load(fileIn, keystorePass.toCharArray());
		        java.security.cert.Certificate[] chain = keyStore.getCertificateChain(alias);
		        X509Certificate certChain[] = new X509Certificate[chain.length];

		        CertificateFactory cf = CertificateFactory.getInstance("X.509");
		        for (int count = 0; count < chain.length; count++) {
		          ByteArrayInputStream certIn = new ByteArrayInputStream(chain[0].getEncoded());
		          X509Certificate cert = (X509Certificate) cf.generateCertificate(certIn);
		          certChain[count] = cert;
		        }

		        Key key = keyStore.getKey(alias, aliasPass.toCharArray());
		        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		        KeySpec keySpec = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class);
		        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

			    Provider prov = keyStore.getProvider();
			    
//			    PdfSigGenericPKCS sg = sap.getSigStandard();
//			    PdfPKCS7 sgn = sg.getSigner();
			    
				PdfPKCS7 sgn = new PdfPKCS7(privateKey, chain, null, "RSA", prov.getName(), true);
			    InputStream data = sap.getRangeStream();
			    MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			    byte buf[] = new byte[8192];
			    int n;
			    while ((n = data.read(buf)) > 0) {
			        messageDigest.update(buf, 0, n);
			    }
			    byte hash[] = messageDigest.digest();
			    Calendar cal = Calendar.getInstance();
			    byte[] ocsp = null;
			    
			    if ( chain.length >= 2) {
			        String url = PdfPKCS7.getOCSPURL((X509Certificate)chain[0]);
			        if (url != null && url.length() > 0)
			            ocsp = new OcspClientBouncyCastle((X509Certificate)chain[0], (X509Certificate)chain[1], url).getEncoded();
			    }
			    byte sh[] = sgn.getAuthenticatedAttributeBytes(hash, cal, ocsp);
			    sgn.update(sh, 0, sh.length);
		
			    TSAClient tsc = null;
			    byte[] encodedSig = sgn.getEncodedPKCS7(hash, cal, tsc, ocsp);
		
			    if (contentEstimated + 2 < encodedSig.length)
			        throw new Exception("Not enough space");
			
			
			    byte[] paddedSig = new byte[contentEstimated];
			    PdfDictionary dic2 = new PdfDictionary();
		    	System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
			    dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
			    sap.close(dic2);
			    sign = true;
	    	}catch(Exception e){
	    	      Logger.warning("", "RubricarDocumento", "insertImageRubrica", "Não foi possivel obter o certificado do servidor, verifique os dados no ficheiro \"certificates.properties\".");
	    	}
    	}
	    
	    if(sign != true){
			    byte[] paddedSig = new byte[contentEstimated];
			    PdfDictionary dic2 = new PdfDictionary();
			    dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
			    sap.close(dic2);	
    	}
		    
		} catch (Exception e) {e.printStackTrace();} 
}
	  

private void loadCertificateStore() {

    KeyStore[] stores = new KeyStore[0];
    try {
      stores = CryptoUtils.getSystemKeyStores();
    } catch (GeneralSecurityException e1) {

    for(KeyStore store : stores)
      loadCertificateStore(store);
    }

  }


  private void loadCertificateStore(KeyStore store) {
    try {
      Enumeration<String> aliases = store.aliases();
      while(aliases.hasMoreElements()) {
        String alias = (String) aliases.nextElement();
        X509Certificate cert = (X509Certificate) store.getCertificate(alias);
        IDEntry idEntry = new IDEntry(alias, store);

        // ignore invalid certificates and filter by signature
        if(!(idEntry.isValid() && idEntry.isSignature())) continue;

      }
    } catch (KeyStoreException e1) {
    }
  }



	  public String getGuidName(){
	      UUID uuid = UUID.randomUUID();
	      return uuid.toString();    
	  } 

	  public byte[] getImageAssinatura(UserInfoInterface userInfo) {
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
		        catch (SQLException e) {    
		          Logger.debug(userInfo.getUtilizador(), this, "getImageAssinatura", "Erro ao ler imagem da bd", e);
		          return null; } 
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
	            } catch (IOException e) { Logger.error("", "", "", "PROBLEMA BD"); }
	        
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
	      } catch (Exception e) { 
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
	            } catch (IOException e) {   }
	        

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
	        
	      }  finally {  DatabaseInterface.closeResources(pst, rs, db);}
	    }
	  
}
