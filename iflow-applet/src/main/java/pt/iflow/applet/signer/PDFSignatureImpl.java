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
package pt.iflow.applet.signer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.applet.DynamicField;
import pt.iflow.applet.DynamicForm;
import pt.iflow.applet.ExtensionFileFilter;
import pt.iflow.applet.IDEntry;
import pt.iflow.applet.IVFile;
import pt.iflow.applet.LoadImageAction;
import pt.iflow.applet.Messages;
import pt.iflow.applet.TempVFile;
import pt.iflow.applet.WebClient;
import pt.iflow.applet.DynamicField.Type;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.OcspClientBouncyCastle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.TSAClient;
import com.lowagie.text.pdf.TSAClientBouncyCastle;

/**
 * No signature
 * @author ombl
 *
 */
public class PDFSignatureImpl implements FileSigner {
  private static Log log = LogFactory.getLog(PDFSignatureImpl.class);

  private final boolean active;

  private static String S_LOCATION = null;
  private static String S_REASON = null;
  private static String S_CONTACT = null;
  
  
  private String location;
  private String reason;
  private String contact;
  private String tsaLocation = null;

  private IDEntry entry;


  private String customSignatureText;
  private boolean changesAllowed = false;
  private boolean signatureVisible = true;
  private boolean useTSA = false;
  private boolean useOCSP = false;
  
  
  public PDFSignatureImpl() {
    boolean active = true;
    this.active = active;

    // set some defaults
    setLocation(S_LOCATION);
    setReason(S_REASON);
    setContact(S_CONTACT);
    setSignatureVisible(true);

  }
  
  public void init(final WebClient webClient) {
    if(null == webClient) {
      tsaLocation = null;
      return;
    }
    // tsa service url is: <URLBase>/tsa
    try {
      URL tsaURL = new URL(webClient.getBaseURL(), "tsa"); //$NON-NLS-1$
      tsaLocation = tsaURL.toString();
      log.debug("tsa location: "+tsaLocation); //$NON-NLS-1$
    } catch (MalformedURLException e) {
      tsaLocation = null;
      log.warn("Could not compute TSA service URL"); //$NON-NLS-1$
      if(log.isDebugEnabled())
        log.debug("URL error: ", e); //$NON-NLS-1$
    }
    
  }


  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return this.location;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getReason() {
    return this.reason;
  }

  public void setSignatureVisible(boolean signatureVisible) {
    this.signatureVisible = signatureVisible;
  }

  public boolean isSignatureVisible() {
    return signatureVisible;
  }
  
  public boolean isChangesAllowed() {
    return this.changesAllowed;
  }

  public String getCustomSignatureText() {
    return this.customSignatureText;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }
  
  public boolean isUseTSA() {
    return this.useTSA;
  }
  
  public void setUseTSA(boolean useTSA) {
    this.useTSA = useTSA;
  }
  
  public boolean isUseOCSP() {
    return this.useOCSP;
  }
  
  public void setUseOCSP(boolean useOCSP) {
    this.useOCSP = useOCSP;
  }
  
  private String getSignatureText(X509Certificate cert, Calendar timestamp) {
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //$NON-NLS-1$
    StringBuilder sb = new StringBuilder();
    sb.append(Messages.getString("PDFSignatureImpl.5")).append(PdfPKCS7.getSubjectFields(cert).getField("CN")); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append(Messages.getString("PDFSignatureImpl.7")).append(df.format(timestamp.getTime())); //$NON-NLS-1$
    return sb.toString();
  }
  
  private String saveToFile(IVFile pdfFile){
    
    File f = new File(System.getProperty("java.io.tmpdir"), "iFlow");  
      if(!f.exists()) 
          f.mkdir();  
    String fileAux = f.getAbsolutePath()+"\\"+getGuidName()+".pdf";
    

    int r;
    byte[] buffer = new byte[8192];

    try {
      OutputStream out= new FileOutputStream(fileAux);
      InputStream in = pdfFile.getInputStream();
      
      while ((r = in.read(buffer)) > 0)
        out.write(buffer, 0, r);
    } catch (IOException e) {   
      e.printStackTrace();  
      }      
    
    return fileAux;
  }
  
  private void deleteFile(String name){
    try {
    File filedelete = new File(name);
    filedelete.delete();
    } catch (Exception e) {   
      e.printStackTrace();  
      }   
  }
  
  public String hashSignExternalTimestamp(IVFile pdffile){
    String read = saveToFile(pdffile);
    String write = saveToFile(pdffile);
    String result = "";
    
    try {
      result = hashSignExternalTimestamp(read, write);
    } catch (Exception e) { e.printStackTrace();  }
    
    return result;
  }
  
  public String hashSignExternalTimestamp(String read, String write) throws Exception {
    Provider prov = entry.getProvider();
    PrivateKey key = entry.getPrivateKey();
    Certificate [] chain = entry.getCertificateChain();
    
 
    PdfReader reader = new PdfReader(read);
    int pageCount = reader.getNumberOfPages();

    File outputFile = new File(write);
    PdfStamper stp = PdfStamper.createSignature(reader, null, '\0', outputFile, true);


    PdfSignatureAppearance sap = stp.getSignatureAppearance();
    sap.setProvider(prov.getName());
    sap.setReason(getReason());
    sap.setLocation(getLocation());
    sap.setContact(getContact());

    sap.setCrypto(null, chain, null, PdfSignatureAppearance.SELF_SIGNED);

    int [] coord = LoadImageAction.getImageXY();
    
    if(!LoadImageAction.posMatriz){ //Se for por coordenadas do sample
        coord[0] = LoadImageAction.getAssX();
        coord[1] = LoadImageAction.getAssY();
    }

     

    //Adicionar imagem ao PDF se for para utilizar
    if(LoadImageAction.getFlagPDF()){
	    	sap.setAcro6Layers(true);
	    	Image img = LoadImageAction.getAssImagePDF();
	    	
	    	if(LoadImageAction.getPagToSign() == -1)
	    	    sap.setVisibleSignature(new Rectangle(coord[0], coord[1], coord[0]+img.getWidth(), coord[1]+img.getHeight()), pageCount, null);
	    	else
	    		sap.setVisibleSignature(new Rectangle(coord[0], coord[1], coord[0]+img.getWidth(), coord[1]+img.getHeight()), LoadImageAction.getPagToSign(), null);	
	    	
	    	sap.setLayer2Text("\n\n(Doc. assinado digitalmente)");
		    sap.setImage(img);
    }else{
	    	if(LoadImageAction.getPagToSign() == -1)
		        sap.setVisibleSignature(new Rectangle(coord[0], coord[1], coord[0]+150, coord[1]+40), pageCount, null);
	    	else
	    		sap.setVisibleSignature(new Rectangle(coord[0], coord[1], coord[0]+150, coord[1]+40), LoadImageAction.getPagToSign(), null);
		    
	    	sap.setLayer2Text(getSignatureText((X509Certificate) chain[0], sap.getSignDate()));
    }

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

    PdfPKCS7 sgn = new PdfPKCS7(key, chain, null, "SHA1", prov.getName(), false);
    InputStream data = sap.getRangeStream();
    MessageDigest messageDigest = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
    byte buf[] = new byte[8192];
    int n;
    while ((n = data.read(buf)) > 0) {
        messageDigest.update(buf, 0, n);
    }
    byte hash[] = messageDigest.digest();
    Calendar cal = Calendar.getInstance();
    byte[] ocsp = null;
    if (isUseOCSP() && chain.length >= 2) {
        String url = PdfPKCS7.getOCSPURL((X509Certificate)chain[0]);
        if (url != null && url.length() > 0)
            ocsp = new OcspClientBouncyCastle((X509Certificate)chain[0], (X509Certificate)chain[1], url).getEncoded();
    }
    byte sh[] = sgn.getAuthenticatedAttributeBytes(hash, cal, ocsp);
    sgn.update(sh, 0, sh.length); 
    TSAClient tsc = null;
    if(isUseTSA() && tsaLocation != null)
      tsc = new TSAClientBouncyCastle(tsaLocation);

    //o PIN/PASS dos certificados é pedido aqui
    byte[] encodedSig = sgn.getEncodedPKCS7(hash, cal, tsc, ocsp);
    
    if (contentEstimated + 2 < encodedSig.length)
        throw new Exception("Not enough space"); //$NON-NLS-1$

    byte[] paddedSig = new byte[contentEstimated];
    System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);     
    PdfDictionary dic2 = new PdfDictionary();
    dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
    sap.close(dic2);
    
    deleteFile(read);
    return write;
  }

 private String copy(String fileSource) throws IOException {
   File source = new File(fileSource);
   
   File f = new File(System.getProperty("java.io.tmpdir"), "iFlow");  
   if(!f.exists()) 
       f.mkdir();  
   String fileAux = f.getAbsolutePath()+"\\"+getGuidName()+".pdf";
   
   File dest = new File(fileAux);
   
    InputStream in = new FileInputStream(source);
    OutputStream out = new FileOutputStream(dest);
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
    }
    in.close();
    out.close();
    return fileAux;
}
  
  public IVFile sign(final IVFile pdf) {
    IVFile outFile = null;
    OutputStream fout = null;
    try {
      outFile = new TempVFile(pdf.getName(), pdf.getVarName());
      fout = outFile.getOutputStream();
   
      //Verificar se é para rubricar todas as paginas
      if(LoadImageAction.getFlagRub()){
    	  //Verificar se é para utilizar a mesma imagem na rubrica e na assinatura
    	  if(LoadImageAction.rubimgSameass){  	    
		      String n = rubricarTodas(pdf);
		      InputStream input = new FileInputStream(n);
		      byte[] buffer = new byte[8192];
		      int r;
		      while ((r = input.read(buffer)) > 0)
		          fout.write(buffer, 0, r);      
		      input.close();
		      deleteFile(n);
    	  }else{
              String read = rubricarTodas(pdf);                                    
              String write = copy(read);             
              String signFile = hashSignExternalTimestamp(read, write); 
              
              InputStream input = new FileInputStream(signFile);
              byte[] buffer = new byte[8192];
              int r;
              while ((r = input.read(buffer)) > 0)
                  fout.write(buffer, 0, r);      
              input.close();
              deleteFile(signFile);
    	  }
      }else{
    	  String signFile = hashSignExternalTimestamp(pdf);
          InputStream input = new FileInputStream(signFile);
          byte[] buffer = new byte[8192];
          int r;
          while ((r = input.read(buffer)) > 0)
              fout.write(buffer, 0, r);      
          input.close();
          deleteFile(signFile);
      }
              
 
    } catch (Exception e) {
      log.error("Error signing data", e);
      outFile = null;
    } finally {
      try {
        if (fout != null)
          fout.close();
      } catch (IOException e) {
      }
    }

    return outFile;
  }

  public boolean isActive() {
    return this.active;
  }

  // TODO concluir e validar a verificação de PDFs
  public String verify(final IVFile pdf) {
    // Certificados adicionados localmente
    KeyStore kall = PdfPKCS7.loadCacertsKeyStore();

    StringBuffer sb = new StringBuffer();
    final String nl = System.getProperty("line.separator"); //$NON-NLS-1$
    InputStream fin = null;
    try {
      boolean second = false;
      PdfReader reader = new PdfReader(fin = pdf.getInputStream());
      AcroFields af = reader.getAcroFields();
      ArrayList<?> names = af.getSignatureNames();
      if (names.isEmpty())
        return Messages.getString("PDFSignatureImpl.9"); //$NON-NLS-1$
      for (Iterator<?> iter = names.iterator(); iter.hasNext();) {
        if (second) {
          sb.append(nl).append("  ======  ").append(nl).append(nl); //$NON-NLS-1$
        }
        String name = (String) iter.next();
        sb.append(Messages.getString("PDFSignatureImpl.11")).append(name).append(nl); //$NON-NLS-1$
        sb.append(Messages.getString("PDFSignatureImpl.12")).append(af.signatureCoversWholeDocument(name) ? Messages.getString("PDFSignatureImpl.13") : Messages.getString("PDFSignatureImpl.14")).append(nl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append(Messages.getString("PDFSignatureImpl.15")).append(af.getRevision(name)).append(Messages.getString("PDFSignatureImpl.16")).append(af.getTotalRevisions()).append(nl); //$NON-NLS-1$ //$NON-NLS-2$

        PdfPKCS7 pk = af.verifySignature(name);
        Calendar cal = pk.getSignDate();
        Certificate pkc[] = pk.getCertificates();
        sb.append(Messages.getString("PDFSignatureImpl.17")).append(PdfPKCS7.getSubjectFields(pk.getSigningCertificate())).append(nl); //$NON-NLS-1$
        sb.append(Messages.getString("PDFSignatureImpl.18")).append(!pk.verify() ? Messages.getString("PDFSignatureImpl.19") : Messages.getString("PDFSignatureImpl.20")).append(nl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
        if (fails == null)
          sb.append(Messages.getString("PDFSignatureImpl.21")).append(nl); //$NON-NLS-1$
        else
          sb.append(Messages.getString("PDFSignatureImpl.22")).append(nl);// : " + fails[1]); //$NON-NLS-1$
      }
    } catch (Exception e) {
      log.error("error verifying file", e); //$NON-NLS-1$
      return Messages.getString("PDFSignatureImpl.24"); //$NON-NLS-1$
    } finally {
      try {
        if(fin != null) {
          fin.close();
        }
      } catch(IOException e) {}
    }
    return sb.toString();
  }

  public DynamicForm getForm() {
    DynamicForm form = new DynamicForm(Messages.getString("PDFSignatureImpl.0")); //$NON-NLS-1$
    DynamicField fld = null;

    form.addField(fld = new DynamicField(Type.TEXTFIELD, Messages.getString("PDFSignatureImpl.28")) { //$NON-NLS-1$
      public void setValue(Object value) {
        super.setValue(value);
        setReason((String)value);
      }
    });
    fld.setValue(getReason());
    
    form.addField(fld = new DynamicField(Type.TEXTFIELD, Messages.getString("PDFSignatureImpl.29")) { //$NON-NLS-1$
      public void setValue(Object value) {
        super.setValue(value);
        setLocation((String)value);
      }
    });
    fld.setValue(getLocation());
    
    form.addField(fld = new DynamicField(Type.TEXTFIELD, Messages.getString("PDFSignatureImpl.30")) { //$NON-NLS-1$
      public void setValue(Object value) {
        super.setValue(value);
        setContact((String)value);
      }
    });
    fld.setValue(getContact());
    
    form.addField(fld = new DynamicField(Type.CHECKBOX, Messages.getString("PDFSignatureImpl.31")) { //$NON-NLS-1$
      public void setValue(Object value) {
        super.setValue(value);
        setSignatureVisible(value==null?false:(Boolean)value);
      }
    });
    fld.setValue(isSignatureVisible());
    
    if(tsaLocation != null) {
      form.addField(fld = new DynamicField(Type.CHECKBOX, Messages.getString("PDFSignatureImpl.32")) { //$NON-NLS-1$
        public void setValue(Object value) {
          super.setValue(value);
          setUseTSA(value==null?false:(Boolean)value);
        }
      });
      fld.setValue(isUseTSA());
    }
    
    form.addField(new DynamicField(Type.SIGNATURE_CERTIFICATE, Messages.getString("PDFSignatureImpl.33")) { //$NON-NLS-1$
      public void setValue(Object value) {
        super.setValue(value);
        entry = (IDEntry) value;
      }  
    });
    
    form.addField(new DynamicField(Type.IMAGE, Messages.getString("SignatureImage.4")) { //$NON-NLS-1$
        public void setValue(Object value) {
          super.setValue(value);
        }  
      }); 
    
    return form;
  }
  
  public String validateForm() {
    if(null == entry)
      return Messages.getString("PDFSignatureImpl.34"); //$NON-NLS-1$
    else if(!entry.isValid())
      return Messages.getString("PDFSignatureImpl.35"); //$NON-NLS-1$
    return null;
  }
  
  public ExtensionFileFilter getFilter() {
    return new ExtensionFileFilter(Messages.getString("PDFSignatureImpl.36"), new String[] { "PDF" }); //$NON-NLS-1$ //$NON-NLS-2$
  }


public String rubricarTodas(IVFile pdffile){
	int pageCount = 0;
	try{
	  
        String read = saveToFile(pdffile);    
        PdfReader reader = new PdfReader(read);
        pageCount = reader.getNumberOfPages();
        String write = saveToFile(pdffile);
        insertImageRubrica(reader, pageCount, write);
        deleteFile(read);
        return write;
    
	}catch(IOException e){ e.printStackTrace(); return "";}
  }
  
  public void insertImageRubrica(PdfReader reader, int pageCount, String fileWrite){	
      int iniY = 841 - 10;
      int iniX = 595 - 10;
      
	try {

		//Verificar qual é a imagem para utilizar na rubrica
		Image img = null;
		if(LoadImageAction.rubimgSameass)
			img = LoadImageAction.getAssImagePDF();
		else
			img = LoadImageAction.getRubImagePDF();
		
		//Criar Modelo para as Rubricas
		ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfStamper stp1 = new PdfStamper(reader, out, '\3', true);
        PdfFormField sig = PdfFormField.createSignature(stp1.getWriter());

        if(LoadImageAction.posRubSame){
        	
            int [] coord = LoadImageAction.getImageXY();
            
            if(!LoadImageAction.posMatriz){ //Se for por coordenadas do sample
                coord[0] = LoadImageAction.getAssX();
                coord[1] = LoadImageAction.getAssY();
            }

            sig.setWidget(new Rectangle(coord[0], coord[1], coord[0]+img.getWidth(), coord[1]+img.getHeight()), null);
        }else{
        	sig.setWidget(new Rectangle( iniX-img.getWidth(), iniY-img.getHeight(), iniX, iniY), null);
        }
        
              
        sig.setFlags(PdfAnnotation.FLAGS_PRINT);
        sig.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g"));
        sig.setFieldName("Assinaturas");
        sig.setPage(1);

        //Se a imagem da rubrica n for a mesma da assinatura não mete na ultima pag
        if(!LoadImageAction.rubimgSameass) 
        	pageCount = pageCount-1;
        
        //Inserir em todas as paginas o Modelo   
        for(int i = 1; i<=pageCount; i++)
        	stp1.addAnnotation(sig, i);
        
        stp1.close();

        //Guardar/Ler PDF com modelos inseridos       
        reader = new PdfReader(out.toByteArray());
        File outputFile = new File(fileWrite);
        
        
        //Preencher Modelo com Dados    
        PdfStamper stp = PdfStamper.createSignature(reader, null, '\0', outputFile, true);
	
		PdfSignatureAppearance sap = stp.getSignatureAppearance();  
	  	sap.setAcro6Layers(true);
	  	reader.close();	  	
		sap.setVisibleSignature("Assinaturas");
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
	     
	    Provider prov = entry.getProvider();
	    PrivateKey key = entry.getPrivateKey();
	    Certificate [] chain = entry.getCertificateChain();
		PdfPKCS7 sgn = new PdfPKCS7(key, chain, null, "SHA1", prov.getName(), false); //$NON-NLS-1$
	    InputStream data = sap.getRangeStream();
	    MessageDigest messageDigest = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
	    byte buf[] = new byte[8192];
	    int n;
	    while ((n = data.read(buf)) > 0) {
	        messageDigest.update(buf, 0, n);
	    }
	    byte hash[] = messageDigest.digest();
	    Calendar cal = Calendar.getInstance();
	    byte[] ocsp = null;
	    
	    if (isUseOCSP() && chain.length >= 2) {
	        String url = PdfPKCS7.getOCSPURL((X509Certificate)chain[0]);
	        if (url != null && url.length() > 0)
	            ocsp = new OcspClientBouncyCastle((X509Certificate)chain[0], (X509Certificate)chain[1], url).getEncoded();
	    }
	    byte sh[] = sgn.getAuthenticatedAttributeBytes(hash, cal, ocsp);
	    sgn.update(sh, 0, sh.length);

	    TSAClient tsc = null;
	    if(isUseTSA() && tsaLocation != null)
	      tsc = new TSAClientBouncyCastle(tsaLocation);
	    byte[] encodedSig = sgn.getEncodedPKCS7(hash, cal, tsc, ocsp);

	    if (contentEstimated + 2 < encodedSig.length)
	        throw new Exception("Not enough space"); //$NON-NLS-1$

	    byte[] paddedSig = new byte[contentEstimated];
	    PdfDictionary dic2 = new PdfDictionary(); 
	    System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
	    dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
	    sap.close(dic2);
			   
	} catch (Exception e) {e.printStackTrace();} 
  }
  
  public String getGuidName(){
      UUID uuid = UUID.randomUUID();
      return uuid.toString();    
  }
  
  
  }
