package pt.iflow.documents;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.DataSet;

public class AppendDocuments {

  private static final int STREAM_SIZE = 8096;

  public static File postProcessPDF(UserInfoInterface userInfo, ProcessData procData, File pdf, String docsVar) {
    Logger.trace("postProcessPDF");
    if(null == userInfo || null == procData || null == pdf) return pdf;
    String login = userInfo.getUtilizador();
    List<Integer> fileIds = new ArrayList<Integer>();
    try {
      String[] ids = null;
      Object o = procData.eval(userInfo, docsVar);

      if(o != null) {
        Document[] docs = (Document[])o;
        ids = new String[docs.length];
        for (int i=0; i < docs.length; i++)
          ids[i] = String.valueOf(docs[i].getDocId());
      }

      if(ids != null) {
        for(String s : ids) {
          if(StringUtils.isBlank(s)) continue;
          try {
            fileIds.add(new Integer(s));
          } catch(NumberFormatException e) {
          }
        }
      }
    } catch (Throwable t) {
      Logger.warning(login, "AppenDocuments", "postProcessPDF", "Error retrieving document IDs", t);
    }

    if(fileIds.isEmpty()) return pdf;

    
    File tmpPdf = null;
    PdfReader reader = null;
    int numPages = 0;
    com.lowagie.text.Document document = null;
    PdfCopy writer = null;

    try (InputStream in = new BufferedInputStream(new FileInputStream(pdf));){
      
      // read the original PDF from memory
      reader = new PdfReader(in);
      reader.consolidateNamedDestinations();
      numPages = reader.getNumberOfPages();

      // output to temp file
      tmpPdf = File.createTempFile("print_", ".pdf");
      try (FileOutputStream fout = new FileOutputStream(tmpPdf);) {
    	  Logger.debug(login, "AppenDocuments", "postProcessPDF", "Vai duplicar");
    	  // step 1: creation of a document-object
    	  document = new com.lowagie.text.Document(reader.getPageSizeWithRotation(1));
    	  // step 2: we create a writer that listens to the document
    	  writer = new PdfCopy(document, fout);
    	  // step 3: we open the document
    	  document.open();
      }
      // step 4: we add content
      PdfImportedPage page = null;
      for (int i = 1; i <= numPages;i++) {
    	  if (writer != null) {
    		  page = writer.getImportedPage(reader, i);
    		  writer.addPage(page);
    	  }
      }
      // copy eventual acroform
      PRAcroForm form = reader.getAcroForm();  
      if (form != null) {  
    	  if (writer != null) {
    		  writer.copyAcroForm(reader);
    	  }
      }  

      Logger.debug(login, "AppenDocuments", "postProcessPDF", "Copiou paginas impressao");
      reader.close();
      reader = null;

      // Retrieve and append documents
      Documents docBean = BeanFactory.getDocumentsBean();
      Rectangle pSize = document.getPageSize();
      for(Integer docid : fileIds) {
        PdfReader file = null;
        try {
          Logger.debug(login, "AppenDocuments", "postProcessPDF", "Obter o documento "+docid);

          Document doc = docBean.getDocument(userInfo, procData, docid);
          if(doc == null) {
            Logger.warning(login, "AppenDocuments", "postProcessPDF", "Documento "+docid+" nao foi encontrado.");
            continue;
          }
          if(doc.getContent() == null) {
            Logger.warning(login, "AppenDocuments", "postProcessPDF", "Documento "+docid+" nao tem conteudo.");
            continue;
          }
          String fname = doc.getFileName();

          Logger.debug(login, "AppenDocuments", "postProcessPDF", "Documento "+docid+" => "+fname);
          byte [] pdfContents = "".getBytes();
          if(fname.toLowerCase(Locale.ENGLISH).endsWith(".pdf")) { 
            Logger.debug(login, "AppenDocuments", "postProcessPDF", "Assumindo PDF...");
            pdfContents = doc.getContent();
          } else if(isTiffImageExt(fname)) { // try multipage tiff
            Logger.debug(login, "AppenDocuments", "postProcessPDF", "Assumindo TIFF...");
            pdfContents = convertImageTiff(pSize, doc.getContent());
          } else if(isImageExt(fname)) { // convert single page image
            Logger.debug(login, "AppenDocuments", "postProcessPDF", "Assumindo imagem...");
            pdfContents = convertImageSingle(pSize, doc.getContent());
          }

          if("".getBytes() != pdfContents) {
            file = new PdfReader(pdfContents);
            file.consolidateNamedDestinations();
            int nPag = file.getNumberOfPages();
            for(int i = 1; i <= nPag;i++) {
              if (writer != null) {
            	  page = writer.getImportedPage(file, i);
            	  writer.addPage(page);
              }
            }
            Logger.debug(login, "AppenDocuments", "postProcessPDF", "Documento "+fname+" ("+docid+") copiado");
          } else {
            Logger.error(login, "AppenDocuments", "postProcessPDF", "Unsupported file: "+fname);
          }

        } catch(Throwable t) {
          Logger.error(login, "AppenDocuments", "postProcessPDF", "Page ignored", t);
        } finally {
        	try { if(file != null) file.close();} catch (Exception e) {}
        	try { if(reader != null) reader.close();} catch (Exception e) {}
        }
      }
      // step 5: we close the document
      try {
    	  if(null != document)
    	      document.close();
	} catch (Exception e) {
		// TODO: handle exception
	}
      	
      Logger.debug(login, "AppenDocuments", "postProcessPDF", "fim");


      // if everithing is fine, swap images
      File f = tmpPdf;
      tmpPdf = pdf;
      pdf = f;

    } catch (Exception e) {
      Logger.error(login, "AppenDocuments", "postProcessPDF", "Error creating temp file", e);
    } finally {
      if(document!=null) document.close();
      // remove the "unused" file
      if(tmpPdf != null) tmpPdf.delete();
    }

    return pdf;
  }

  private static boolean isImageExt(String fname) {
    return ((fname.endsWith(".jpg") || fname.endsWith(".jpeg"))
        ||(fname.endsWith(".png"))
        ||(fname.endsWith(".gif"))
        ||(fname.endsWith(".bmp"))
        ||(fname.endsWith(".tiff") || fname.endsWith(".tif")));
  }

  private static boolean isTiffImageExt(String fname) {
    return (fname.endsWith(".tiff") || fname.endsWith(".tif"));
  }

  private static byte [] convertImageTiff(final Rectangle pSize, byte[] imgData) throws Exception {
    try {
      // Convert image to PDF
      ByteArrayOutputStream outfile = new ByteArrayOutputStream();  
      com.lowagie.text.Document imgDoc = new com.lowagie.text.Document(pSize);
      PdfWriter writer = PdfWriter.getInstance(imgDoc, outfile);  
      writer.setStrictImageSequence(true);  
      imgDoc.open();  
      RandomAccessFileOrArray data = new RandomAccessFileOrArray(imgData);
      int nPages = TiffImage.getNumberOfPages(data);
      for(int i = 1; i <= nPages; i++) {
        Image img = TiffImage.getTiffImage(data, i);
        // check if image fits page
        float pw = pSize.getWidth();
        float ph = pSize.getHeight();
        float iw = img.getPlainWidth();
        float ih = img.getPlainHeight();

        if(pw<iw || ph < ih) {
          img.scaleToFit(pw, ph);
        }
        img.setAlignment(Image.MIDDLE);
        imgDoc.add(img);
        if(i!=nPages) imgDoc.newPage();
      }
      imgDoc.close();  
      outfile.flush();  
      return outfile.toByteArray();
    } catch (Exception e) {
      Logger.warning("<tiff2pdf>", "BlockFormulario", "convertImageTiff", "Error converting TIFF using multiple page method", e);
    }
    return convertImageSingle(pSize, imgData);
  }

  private static byte [] convertImageSingle(Rectangle pSize, byte[] imgData) throws Exception {
    // Convert image to PDF
    ByteArrayOutputStream outfile = new ByteArrayOutputStream();  
    com.lowagie.text.Document imgDoc = new com.lowagie.text.Document(pSize);
    PdfWriter writer = PdfWriter.getInstance(imgDoc, outfile);  
    writer.setStrictImageSequence(true);  
    imgDoc.open();  
    Image img = Image.getInstance(imgData);  

    // check if image fits page
    float pw = pSize.getWidth();
    float ph = pSize.getHeight();
    float iw = img.getPlainWidth();
    float ih = img.getPlainHeight();

    if(pw<iw || ph < ih) {
      img.scaleToFit(pw, ph);
    }
    img.setAlignment(Image.MIDDLE);
    imgDoc.add(img);  
    imgDoc.close();  
    outfile.flush();  
    return outfile.toByteArray();
  }

  public static byte[] mergePDFs(UserInfoInterface userInfo, ProcessData procData, String[] docsVars) {
    String login = userInfo.getUtilizador();
    Documents docBean = BeanFactory.getDocumentsBean();
    File pdf = null; 
    List<Integer> fileIds = new ArrayList<Integer>();
    for (int n = 0; n < docsVars.length; n++) {
    	
      try {
        Object o = procData.eval(userInfo, docsVars[n]);
        if(o != null) {
          Document[] docs = (Document[])o;
          for (int i=0; i < docs.length; i++) {
            int docid = docs[i].getDocId(); 
            if (docid <= 0) continue;
            if (pdf == null) {
              Document doc = docBean.getDocument(userInfo, procData, docid);
              pdf = File.createTempFile("merge_", ".pdf");
              try (FileOutputStream fos = new FileOutputStream(pdf);) {
            	  fos.write(doc.getContent());
              }
            } else {
              try {
                fileIds.add(docid);
              } catch(NumberFormatException e) { }
            }
          }
        }
      } catch (Throwable t) {
        Logger.warning(login, "AppenDocuments", "mergePDFs", "Error retrieving document IDs", t);
	  	}    
    }

    if(fileIds.isEmpty()) return null;

    
    File tmpPdf = null;
    PdfReader reader = null;
    int numPages = 0;
    com.lowagie.text.Document document = null;
    PdfCopy writer = null;
    

    try (InputStream  in = new BufferedInputStream(new FileInputStream(pdf));){
     
      // read the original PDF from memory
      reader = new PdfReader(in);
      reader.consolidateNamedDestinations();
      numPages = reader.getNumberOfPages();

      // output to temp file
      tmpPdf = File.createTempFile("print_", ".pdf");
      try (OutputStream fout = new FileOutputStream(tmpPdf);) {
    	  Logger.debug(login, "AppenDocuments", "mergePDFs", "Vai duplicar");
    	  // step 1: creation of a document-object
    	  document = new com.lowagie.text.Document(reader.getPageSizeWithRotation(1));
    	  // step 2: we create a writer that listens to the document
    	  writer = new PdfCopy(document, fout);
      }
      // step 3: we open the document
      document.open();

      // step 4: we add content
      PdfImportedPage page = null;
      for (int i = 1; i <= numPages;i++) {
    	if (writer != null) {
    		page = writer.getImportedPage(reader, i);
    		writer.addPage(page);
    	}
      }
      // copy eventual acroform
      PRAcroForm form = reader.getAcroForm();  
      if (form != null) {  
    	  if (writer != null) {
    		  writer.copyAcroForm(reader);
    	  }
      }

      Logger.debug(login, "AppenDocuments", "mergePDFs", "Copiou paginas impressao");
      reader.close();
      reader = null;


      // Retrieve and append documents
      Rectangle pSize = document.getPageSize();
      for(Integer docid : fileIds) {
        PdfReader file = null;
        try {
          Logger.debug(login, "AppenDocuments", "mergePDFs", "Obter o documento "+docid);

          Document doc = docBean.getDocument(userInfo, procData, docid);
          if(doc == null) {
            Logger.warning(login, "AppenDocuments", "mergePDFs", "Documento "+docid+" nao foi encontrado.");
            continue;
          }
          if(doc.getContent() == null) {
            Logger.warning(login, "AppenDocuments", "mergePDFs", "Documento "+docid+" nao tem conteudo.");
            continue;
          }
          String fname = doc.getFileName().toLowerCase(Locale.ENGLISH);

          Logger.debug(login, "AppenDocuments", "mergePDFs", "Documento "+docid+" => "+fname);
          byte [] pdfContents = "".getBytes();
          if(fname.endsWith(".pdf")) { 
            Logger.debug(login, "AppenDocuments", "mergePDFs", "Assumindo PDF...");
            pdfContents = doc.getContent();
          } else if(isTiffImageExt(fname)) { // try multipage tiff
            Logger.debug(login, "AppenDocuments", "mergePDFs", "Assumindo TIFF...");
            pdfContents = convertImageTiff(pSize, doc.getContent());
          } else if(isImageExt(fname)) { // convert single page image
            Logger.debug(login, "AppenDocuments", "mergePDFs", "Assumindo imagem...");
            pdfContents = convertImageSingle(pSize, doc.getContent());
          }

          if("".getBytes() != pdfContents) {
            file = new PdfReader(pdfContents);
            file.consolidateNamedDestinations();
            int nPag = file.getNumberOfPages();
            for(int i = 1; i <= nPag;i++) {
            	if (writer != null) {
            		page = writer.getImportedPage(file, i);
            		writer.addPage(page);
            	}
            }
            Logger.debug(login, "AppenDocuments", "mergePDFs", "Documento "+fname+" ("+docid+") copiado");
          } else {
            Logger.error(login, "AppenDocuments", "mergePDFs", "Unsupported file: "+fname);
          }

        } catch(Throwable t) {
          Logger.error(login, "AppenDocuments", "mergePDFs", "Page ignored", t);
        } finally {
          try {
        	  if (file != null)
                  file.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
        	
        }
      }
      // step 5: we close the document
      document.close();
      document = null;

      Logger.debug(login, "AppenDocuments", "mergePDFs", "fim");


      // if everithing is fine, swap images
      File f = tmpPdf;
      tmpPdf = pdf;
      pdf = f;

    } catch (Exception e) {
      Logger.error(login, "AppenDocuments", "mergePDFs", "Error creating temp file", e);
    } finally {
      if(document!=null) document.close();

      // remove the "unused" file
      if(tmpPdf != null) tmpPdf.delete();
    }

    byte[] retObj = null;
    try (InputStream pdfIn = new FileInputStream(pdf);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();){
  
      byte[] r = new byte[STREAM_SIZE];
      int j = 0;
      while ((j = pdfIn.read(r, 0, STREAM_SIZE)) != -1)
        baos.write(r, 0, j);
      pdfIn.close();
      baos.flush();
      baos.close();
      retObj = baos.toByteArray();
    } catch (Exception e) {
      Logger.error(login, "AppenDocuments", "mergePDFs", "Error reading temp file", e);
    }
    
    return retObj;
  }
}
