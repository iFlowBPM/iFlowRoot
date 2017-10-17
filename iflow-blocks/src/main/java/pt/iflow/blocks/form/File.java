package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ListIterator;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processtype.DocumentDataType;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

public class File implements FieldInterface {

  public String getDescription() {
    return "File";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    // TODO
  }

  public void generateXSL(PrintStream out) {
    // TODO
  }

  public void generateXML(PrintStream out, Properties prop) {
    // TODO
  }

  public String getXML(Properties prop) {
    try {
      StringBuilder sb = new StringBuilder();

      String stmp = null;

      sb.append("<field><type>file</type>");
      sb.append("<obligatory>").append(prop.getProperty(FormProps.sOBLIGATORY_PROP)).append("</obligatory>");

      sb.append("<flowid>").append(prop.getProperty(FormProps.FLOWID)).append("</flowid>");
      sb.append("<pid>").append(prop.getProperty(FormProps.PID)).append("</pid>");
      sb.append("<subpid>").append(prop.getProperty(FormProps.SUBPID)).append("</subpid>");
      
      String sFileLabel= prop.getProperty("file_label");
      if (sFileLabel == null) sFileLabel = "";
      sb.append("<file_label>").append(sFileLabel).append("</file_label>");

      String text = prop.getProperty("text");
      if (text == null) text = "";
      sb.append("<text>").append(text).append("</text>");

      stmp = prop.getProperty("variable");
      if (stmp == null) stmp = "";
      sb.append("<variable>").append(stmp).append("</variable>");
      
      stmp = prop.getProperty("file_signature_type");
      if (stmp == null) stmp = "";
      sb.append("<signatureType>").append(stmp).append("</signatureType>");
      
      stmp = prop.getProperty("file_encrypt_users");
      stmp = StringUtils.isEmpty(stmp) ? "false" : "true";
      sb.append("<encryptType>").append(stmp).append("</encryptType>");

      stmp = prop.getProperty("show_edition");
      sb.append("<show_edition>").append(stmp).append("</show_edition>");

      stmp = prop.getProperty("file_sign_new");
      sb.append("<file_sign_new>").append(stmp).append("</file_sign_new>");

      stmp = prop.getProperty("file_sign_method");
      sb.append("<file_sign_method>").append(stmp).append("</file_sign_method>");

      stmp = prop.getProperty("file_sign_existing");
      sb.append("<file_sign_existing>").append(stmp).append("</file_sign_existing>");
      
      stmp = prop.getProperty("show_remove");
      sb.append("<show_remove>").append(stmp).append("</show_remove>");

      String showLink = prop.getProperty("show_link");
      sb.append("<show_link>").append(showLink).append("</show_link>");

      String sLinkLabel= prop.getProperty("link_label");
      if (sLinkLabel == null) sLinkLabel = "";
      sb.append("<link_label>").append(sLinkLabel).append("</link_label>");

      String linkTextTpl = prop.getProperty("link_text");
      if (linkTextTpl == null) linkTextTpl = "";
//      sb.append("<link_text>").append(stmp).append("</link_text>");

//      stmp = prop.getProperty("link_url");
//      if (stmp == null) stmp = "";
//      sb.append("<link_url>").append(stmp).append("</link_url>");

      String sEditionLabel = prop.getProperty("edition_label");
      if (sEditionLabel == null) sEditionLabel = "";
      sb.append("<edition_label>").append(sEditionLabel).append("</edition_label>");

      String sRemoveLabel = prop.getProperty("remove_label");
      if (sRemoveLabel == null) sRemoveLabel = "";
      sb.append("<remove_label>").append(sRemoveLabel).append("</remove_label>");

      sb.append("<has_label_row>");
      if (StringUtils.isNotEmpty(sFileLabel) || StringUtils.isNotEmpty(sLinkLabel) || StringUtils.isNotEmpty(sEditionLabel) || StringUtils.isNotEmpty(sRemoveLabel)) {
        sb.append("true");
      }
      else {
        sb.append("false");
      }
      sb.append("</has_label_row>");

      sb.append("<scanner_enabled>").append(prop.getProperty("scanner_enabled")).append("</scanner_enabled>");
      sb.append("<upload_enabled>").append(prop.getProperty("upload_enabled")).append("</upload_enabled>");
      sb.append("<upload_label>").append(prop.getProperty("upload_label")).append("</upload_label>");
      sb.append("<upload_limit>").append(prop.getProperty("upload_limit")).append("</upload_limit>");
      sb.append("<file_valid_extensions>").append(prop.getProperty("file_valid_extensions")).append("</file_valid_extensions>");
      sb.append("<onclick>").append(prop.getProperty(FormProps.ONCLICK)).append("</onclick>");
      // FIXME a maioria dos browsers não suportam o atributo accept, recomenda-se fazer a validação server-side
      sb.append("<accept>").append(prop.getProperty(FormProps.FILE_UPLOAD_EXTENSIONS)).append("</accept>");

      stmp = prop.getProperty("file_is_image");
      sb.append("<file_is_image>").append(stmp).append("</file_is_image>");
      String imgWidth = "";
      String imgHeight = "";
      if ("true".equals(stmp)){
        if (prop.getProperty("file_is_image_width") != null){
          imgWidth = prop.getProperty("file_is_image_width");
        }
        if (prop.getProperty("file_is_image_height") != null){
          imgHeight = prop.getProperty("file_is_image_height");
        }
      }
      sb.append("<file_is_image_size_width>").append(imgWidth).append("</file_is_image_size_width>");
      sb.append("<file_is_image_size_height>").append(imgHeight).append("</file_is_image_size_height>");

      // New properties
      String sFilesAvailable = prop.getProperty("filesAvailable");
      if(StringUtils.isNotEmpty(sFilesAvailable)) {
        int filesAvailable = Integer.parseInt(sFilesAvailable);
        
        for(int i = 0; i < filesAvailable; i++) {
          sb.append("<file>");
          
          String fid = prop.getProperty("fileid_"+i);
          if (fid == null) fid = "";
          String fname = prop.getProperty("filename_"+i);
          if (fname == null) fname = "";
          
          String asSignatures = prop.getProperty("asSignatures_"+i);
          if (asSignatures == null) asSignatures = "false";
          
          String tosign = prop.getProperty("tosign_"+i);
          if (tosign == null) tosign = "true";
          
          sb.append("<id>").append(fid).append("</id>");
          sb.append("<name>").append(fname).append("</name>");
          sb.append("<asSignatures>").append(asSignatures).append("</asSignatures>");
          sb.append("<tosign>").append(tosign).append("</tosign>");
          
          sb.append("<text>").append(MessageFormat.format(text, new Object[]{fid,fname})).append("</text>");
          
          String linkLabel = prop.getProperty("link_label_"+i);
          if (linkLabel == null) linkLabel = "";
          sb.append("<link_label>").append(linkLabel).append("</link_label>");
          
          String linkText = MessageFormat.format(linkTextTpl, new Object[]{fid,fname});
          if (StringUtils.isEmpty(linkText)) linkText = fname;
          sb.append("<link_text>").append(linkText).append("</link_text>");
          
          String linkUrl = prop.getProperty("link_url_"+i);
          if (linkUrl == null) linkUrl = "";
          sb.append("<link_url>").append(linkUrl).append("</link_url>");

          sb.append("</file>");
        }
      }

      sb.append("</field>");

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isOutputField() {
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
  
    String login = userInfo.getUtilizador();
    
    boolean bShowLink= !checkDisabledCond(userInfo, procData, props, "link_cond");
    boolean bShowEdition= !checkDisabledCond(userInfo, procData, props, "edition_cond");
    boolean bShowRemove= !checkDisabledCond(userInfo, procData, props, "remove_cond");
    boolean bScannerEnabled = !checkDisabledCond(userInfo, procData, props, "file_scanner_cond");
    boolean bUploadEnabled = !checkDisabledCond(userInfo, procData, props, "file_upload_cond");

    String sFileLabel = props.getProperty("file_label");
    String sLinkLabel = props.getProperty("link_label");
    String sLinkText = props.getProperty("link_text");
    String sEditionLabel = props.getProperty("edition_label");
    String sRemoveLabel = props.getProperty("remove_label");
    String sUploadLimit = props.getProperty("file_upload_limit");

    try  {
      Integer.parseInt(sUploadLimit);
    } catch(Throwable t) {
      sUploadLimit = "1";
    }

    String sUploadLabel = props.getProperty("file_upload_label");
    if (StringUtils.isEmpty(sUploadLabel)) {
      sUploadLabel = "";
    }

    String sVar = props.getProperty(FormProps.sVARIABLE);
    ProcessListVariable listvar = null;

    try {
      listvar = procData.getList(sVar);
      if (listvar == null) {
        listvar = new ProcessListVariable(new DocumentDataType(), "");
        Integer docId = Integer.parseInt(procData.transform(userInfo, sVar));
        Document doc = BeanFactory.getDocumentsBean().getDocument(userInfo, procData, docId);
        if (StringUtils.isNotBlank(doc.getFileName()))
          listvar.setItemValue(0, docId);
        else
          listvar = null;
      }
    } catch (Exception e) {
      listvar = null;
    }

    int filesAvailable = 0;
    if (listvar == null) {
      Logger.warning(login,this,"setup",
          procData.getSignature() + "DOCLIST VAR " + sVar + " IS NULL (perhaps not defined in catalogue)!");
    }
    else {
      int nFiles = listvar.size();
      String sLinkUrl = "";
      if(nFiles > 0) {

        Documents docBean = BeanFactory.getDocumentsBean();
        
        ListIterator<ProcessListItem> itemIterator = listvar.getItemIterator();
        while (itemIterator.hasNext()) {
          ProcessListItem item = itemIterator.next();
          String sDocId = null;
          if (item != null)
            sDocId = item.format();

          if(StringUtils.isNotEmpty(sDocId)) {
            try {
              DocumentIdentifier did = DocumentIdentifier.getInstance(sDocId);
              Document docData = docBean.getDocumentInfo(userInfo, procData, did);
              if (docData != null) {
                sLinkUrl = FormUtils.generateDocumentURL(userInfo, response, procData, did);
                props.setProperty("fileid_"+filesAvailable,did.getId());
                props.setProperty("filename_"+filesAvailable,StringEscapeUtils.escapeXml(docData.getFileName()));
                if (sLinkLabel != null) props.setProperty("link_label_"+filesAvailable,StringEscapeUtils.escapeXml(sLinkLabel));
                //  if (sLinkText != null) props.setProperty("link_text_"+filesAvailable,sLinkText);
                if (sLinkUrl != null) props.setProperty("link_url_"+filesAvailable,sLinkUrl);
                
                Documents docbean = BeanFactory.getDocumentsBean();
                boolean asSignatures = docbean.asSignatures(userInfo, Integer.parseInt(did.getId()));
                props.setProperty("asSignatures_"+filesAvailable,""+asSignatures);
                
                boolean tosign = docbean.isToSign(userInfo, Integer.parseInt(did.getId()));
                props.setProperty("tosign_"+filesAvailable,""+tosign);
                
                filesAvailable++;
              }
            }
            catch (Exception edocs) {
              Logger.warning(login, this, "setup", 
                  procData.getSignature() + "Error processing document " + sDocId, edocs);
            }
          }
        }
      }
    }

    String sFileAllowedExtensions = props.getProperty("file_upload_valid_extensions");
    if (sFileAllowedExtensions == null) {
      sFileAllowedExtensions = "";
    }

    //Set variables to use in xls
    props.setProperty("show_link",bShowLink ? "true" : "false");
    props.setProperty("filesAvailable",String.valueOf(filesAvailable));

    if (sFileLabel != null) props.setProperty("file_label",sFileLabel);
    if (sLinkLabel != null) props.setProperty("link_label",sLinkLabel);
    if (sLinkText != null) props.setProperty("link_text",sLinkText);
    // if (sLinkUrl != null) props.setProperty("link_url",sLinkUrl);

    props.setProperty("show_edition",bShowEdition ? "true" : "false");
    if (sEditionLabel != null) props.setProperty("edition_label",sEditionLabel);

    props.setProperty("show_remove",bShowRemove ? "true" : "false");
    if (sRemoveLabel != null) props.setProperty("remove_label",sRemoveLabel);

    props.setProperty("scanner_enabled",bScannerEnabled ? "true" : "false");
    props.setProperty("upload_enabled",bUploadEnabled ? "true" : "false");
    props.setProperty("upload_label", sUploadLabel);
    props.setProperty("upload_limit", sUploadLimit);
    props.setProperty("file_valid_extensions", sFileAllowedExtensions);
  }

  private boolean checkDisabledCond(UserInfoInterface userInfo, ProcessData procData, Properties props, String prop) {
    boolean cond = false;
    String condProp = props.getProperty(prop);
    if (StringUtils.isNotEmpty(condProp)) {
      try {
        cond = procData.query(userInfo, condProp);
        Logger.debug(userInfo.getUtilizador(), this, "checkDisabledCond", 
            procData.getSignature() + "evaluated " + prop + " to " + cond);
      }
      catch (Exception eeval) {
        Logger.warning(userInfo.getUtilizador(), this, "checkDisabledCond", 
            procData.getSignature() + "exception evaluating " + prop + ": " + condProp, eeval);
      }
    }
    return cond;
  }

  public boolean isArrayTable() {
    return false;
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
