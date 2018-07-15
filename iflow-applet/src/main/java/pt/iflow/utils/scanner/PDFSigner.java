package pt.iflow.utils.scanner;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;

import pt.iflow.applet.IOUtils;
import pt.iflow.applet.Messages;


public class PDFSigner implements FileSigner {

  private final boolean active;

  private String location;
  private String reason;
  private String contact;
  private boolean signatureVisible;

  private boolean signatureLoaded = false;
  private PrivateKey key;
  private Certificate[] certificateChain;

  private boolean useSHA1 = false;
  private String customSignatureText;
  private boolean changesAllowed = false;
  
  

  public PDFSigner() {
    boolean active = false;
    this.active = active;

    // set some defaults
    setLocation(null);
    setReason(null);
    setContact(null);
    setSignatureVisible(false);

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
    return this.signatureVisible;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }


  public synchronized void loadSignature(Component frame) {
    if(signatureLoaded) return;
    PDFSignInfo info = new PDFSignInfo();

    if(info.showDialog(frame) == PDFSignInfo.CANCEL) return;

    key = info.getSignKey();
    certificateChain = info.getCertificateChain();
    setLocation(info.getSignLocation());
    setReason(info.getSignReason());
    setContact(info.getSignContact());

    signatureLoaded = true;

  }


  private Calendar getTime() {
    // TODO NTP time ??
    return Calendar.getInstance();
  }

  // SHA1
  public void sha1SignEncapsulated(InputStream pdfFile, OutputStream outputSigned, PrivateKey key, Certificate[] chain) throws Exception {
    hashSignEncapsulated(pdfFile, outputSigned, key, chain, "SHA1", true); //$NON-NLS-1$
  }

  // MD5
  public void md5SignEncapsulated(InputStream pdfFile, OutputStream outputSigned, PrivateKey key, Certificate[] chain) throws Exception {
    hashSignEncapsulated(pdfFile, outputSigned, key, chain, "MD5", true); //$NON-NLS-1$
  }


  public void hashSignEncapsulated(InputStream pdfFile, OutputStream outputSigned, PrivateKey key, Certificate[] chain, String hashAlgo, boolean usePDFCode) throws Exception {
    final int TAM_ASSINATURA = 256; // especulacao...
    final int TAM_HASH = 160; // SHA1 -> 160 bytes
    final int TAM_THRESHOLD = 1024; // para o just in case...
    // Este tamanho pode variar consoante os certificados usados. Como eh que poderemos estimar isto?
    int csize = TAM_ASSINATURA+TAM_HASH+TAM_THRESHOLD;
    for(int i = 0; i < chain.length; i++)
      csize += chain[i].getEncoded().length;

    System.out.println("Tamanho estimado: "+csize); //$NON-NLS-1$

    PdfReader reader = new PdfReader(pdfFile);
    PdfStamper stp = PdfStamper.createSignature(reader, outputSigned, '\0');

    // Cria um campo "assinatura" sem informação criptográfica.
    PdfSignatureAppearance sap = stp.getSignatureAppearance();

    // Campos opcionais
    sap.setVisibleSignature(new Rectangle(100, 100, 300, 200), 1, null);
    sap.setSignDate(getTime()); // NTP time
    sap.setReason("I like to sign"); //$NON-NLS-1$
    sap.setLocation("Universe"); //$NON-NLS-1$
    sap.setContact("912345678"); //$NON-NLS-1$
    sap.setAcro6Layers(true);
    sap.setRender(PdfSignatureAppearance.SignatureRenderDescription);

    if(!changesAllowed)
      sap.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);

    // make signature visible
    if(isSignatureVisible())
      sap.setVisibleSignature(new Rectangle(100, 100, 300, 200), reader.getNumberOfPages(), null);


    if(customSignatureText != null) {

      // Descrição personalizada
      Font font = new Font(Font.HELVETICA);
      font.setSize(10);
      font.setStyle(Font.NORMAL);
      sap.setLayer2Font(font);
      sap.setLayer2Text(customSignatureText);
      sap.setCrypto(null, chain, null, null);
    }

    // Porque chain[0]? Porque versões do AcrobatReader anteriores à 6 exigem que 
    // o certificado assinante seja o primeiro da lista.
    X509Certificate certificate = (X509Certificate) chain[0];


    // Criar dicionario com informacao da assinatura
    PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
    dic.setDate(new PdfDate(sap.getSignDate()));
    dic.setName(PdfPKCS7.getSubjectFields(certificate).getField("CN")); //$NON-NLS-1$
    if (sap.getReason() != null)
      dic.setReason(sap.getReason());
    if (sap.getLocation() != null)
      dic.setLocation(sap.getLocation());
    if (sap.getContact() != null)
      dic.setContact(sap.getContact());
    sap.setCryptoDictionary(dic); // associa informação criptográfica à assinatura.


    // reserva o tamanho para o object PKCS#7.
    // O objecto é guardado numa string codificada em hexadecimal e delimitada por '<' e '>'
    // Como cada byte em formato hexadecimal tem tamanho 2, o espaço a reservar é:
    // 2*<num bytes do objecto> + delimitadores
    HashMap<Object, Object> exc = new HashMap<Object, Object>();
    exc.put(PdfName.CONTENTS, new Integer(csize * 2 + 2));
    sap.preClose(exc);

    // Calcula o Hash do documento com SHA1
    MessageDigest md = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
    InputStream s = null;
    
    byte[] hash;
	try {
		s = sap.getRangeStream(); // obtem dados do documento excepto conteudo da assinatura
		int read = 0;
		byte[] buff = new byte[8192];
		while ((read = s.read(buff)) > 0) {
		  md.update(buff, 0, read);
		}
		hash = md.digest();
	} finally {
		if( s != null) IOUtils.safeClose(s);
	} 

    byte [] outc = new byte[csize];
    Arrays.fill(outc, 0, outc.length, (byte)0); // init with 0 just in case

    if(usePDFCode) {
      // Construir objecto PKCS#7 usando api do iText
      PdfPKCS7 pkcs7 = new PdfPKCS7(
          key,        // chave para assinatura 
          chain,      // certificate chain
          null,       // revocation list
          hashAlgo,   // algoritmo hash (só para a assinatura?)
          null,       // provider name
          true        // true se PdfName.ADBE_PKCS7_SHA1
      );

      pkcs7.setExternalDigest(null, hash, null);
      byte[] pk = pkcs7.getEncodedPKCS7();
      if(pk.length > csize) System.err.println("Eh pah, os tamanhos não batem muito certo..."); //$NON-NLS-1$
      System.arraycopy(pk, 0, outc, 0, pk.length);
    } else {
      byte pk[] = SecurityHelper.bcSign(hash, key, chain, hashAlgo);
      if(pk.length > csize) System.err.println("Eh pah, os tamanhos não batem muito certo..."); //$NON-NLS-1$
      System.arraycopy(pk, 0, outc, 0, pk.length);
    }
    // Nao se devia copiar para o outro dictionary?
    PdfDictionary dic2 = new PdfDictionary();
    dic2.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));
    sap.close(dic2);
  }


  public File sign(final File pdf) {
    if(!signatureLoaded) return null;
    
    File outFile = null;
    FileInputStream fin = null;
    FileOutputStream fout = null;
    try {
      outFile = File.createTempFile("signed_", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
      fin = new FileInputStream(pdf);
      fout = new FileOutputStream(outFile);
      if(useSHA1) {
        sha1SignEncapsulated(fin, fout, key, certificateChain);
      } else {
        md5SignEncapsulated(fin, fout, key, certificateChain);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    	if( fin != null) IOUtils.safeClose(fin);
    	if( fout != null) IOUtils.safeClose(fout);
    }

    return outFile;

  }

  public boolean isActive() {
    return this.active;
  }

  
  public String verify(final File pdf) {
    // Certificados adicionados localmente
    KeyStore kall = PdfPKCS7.loadCacertsKeyStore();

    StringBuffer sb = new StringBuffer();
    final String nl = System.getProperty("line.separator"); //$NON-NLS-1$
    FileInputStream fin = null;
    try {
      boolean second = false;
      PdfReader reader = new PdfReader(fin = new FileInputStream(pdf));
      AcroFields af = reader.getAcroFields();
      ArrayList names = af.getSignatureNames();
      if(names.isEmpty()) return Messages.getString("PDFSigner.13"); //$NON-NLS-1$
      for(Iterator iter = names.iterator(); iter.hasNext();) {
        if(second) {
          sb.append(nl).append("  ======  ").append(nl).append(nl); //$NON-NLS-1$
        }
        String name = (String)iter.next();
        sb.append(Messages.getString("PDFSigner.15")).append(name).append(nl); //$NON-NLS-1$
        sb.append(Messages.getString("PDFSigner.16")).append(af.signatureCoversWholeDocument(name)?Messages.getString("PDFSigner.17"):Messages.getString("PDFSigner.18")).append(nl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append(Messages.getString("PDFSigner.19")).append(af.getRevision(name)).append(Messages.getString("PDFSigner.20")).append(af.getTotalRevisions()).append(nl); //$NON-NLS-1$ //$NON-NLS-2$
//        // Start revision extraction
//        File tempFile = File.createTempFile("rev_"+af.getRevision(name), ".pdf");
//        FileOutputStream out = new FileOutputStream(tempFile);
//        byte bb[] = new byte[8192];
//        InputStream ip = af.extractRevision(name);
//        int n = 0;
//        while ((n = ip.read(bb)) > 0)
//           out.write(bb, 0, n);
//        out.close();
//        ip.close();
//        // End revision extraction
        PdfPKCS7 pk = af.verifySignature(name);
        Calendar cal = pk.getSignDate();
        Certificate pkc[] = pk.getCertificates();
        sb.append(Messages.getString("PDFSigner.21")).append(PdfPKCS7.getSubjectFields(pk.getSigningCertificate())).append(nl); //$NON-NLS-1$
        sb.append(Messages.getString("PDFSigner.22")).append(!pk.verify()?Messages.getString("PDFSigner.23"):Messages.getString("PDFSigner.24")).append(nl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
        if (fails == null)
          sb.append(Messages.getString("PDFSigner.25")).append(nl); //$NON-NLS-1$
        else
          sb.append(Messages.getString("PDFSigner.26")).append(nl);//: " + fails[1]); //$NON-NLS-1$
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Messages.getString("PDFSigner.27"); //$NON-NLS-1$
	} finally {
		if( fin != null) IOUtils.safeClose(fin);
	}   
    return sb.toString();
  }

}
