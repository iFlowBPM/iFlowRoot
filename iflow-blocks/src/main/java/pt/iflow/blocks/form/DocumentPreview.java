package pt.iflow.blocks.form;

import java.io.PrintStream;
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

public class DocumentPreview implements FieldInterface {

  public String getDescription() {
    return "Document Preview";
  }

  public void generateHTML(PrintStream out, Properties prop) {
//    try {
//      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
//      out.println("  <tr>");
//      out
//          .println("    <td height=\"20\" bgcolor=\"#4b6e98\" bordercolorlight=\"#4b6e98\" bordercolordark=\"#ffffff\"><div align=\"center\" class=\"v10bBRAnd\">"
//              + prop.getProperty(FormProps.FORM_TEMPLATE) + "</div>");
//      out.println("    </td>");
//      out.println("  </tr>");
//      out.println("</table>");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }

  public void generateXSL(PrintStream out) {
//    try {
//      out.println("<!-- HEADER -->");
//      out.println("    <xsl:value-of select=\"text/text()\"/>");
//      out.println("<!-- HEADER -->");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
//      out.print("xmlStr = xmlStr + \"<field><type>header</type>\"");
//      out.print("+ \"<document_preview>" + prop.getProperty(FormProps.DOCUMENT_PREVIEW) + "</document_preview>\"");
//      out.println("+ \"</field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
    	Integer filesAvailable = Integer.parseInt(prop.getProperty("filesAvailable"));;
    	StringBuffer sb = new StringBuffer();
    	sb.append("<field><type>" + FormProps.DOCUMENT_PREVIEW + "</type>");
    	sb.append("<files_available>" + filesAvailable + "</files_available>");
    	for(int i=0; i< filesAvailable; i++)
		  	//sb.append("<link_url_" + i +">").append(prop.getProperty("link_url_"+i)).append("</link_url_" + i +">");
    		sb.append("<file><link_url>").append(prop.getProperty("link_url_"+i)).append("</link_url></file>");
    	sb.append("</field>");
      
      return sb.toString();
    } catch (Exception e) {
    	Logger.error("ADMIN", this, "getXML", null, e);
    }
    return null;
  }

  public boolean isOutputField() {
    return true;
  }

  public boolean isArrayTable() {
    return false;
  }

	public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
		String login = userInfo.getUtilizador();
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
	                if (sLinkUrl != null) 
	                	props.setProperty("link_url_"+filesAvailable,sLinkUrl);	               
	                filesAvailable++;
	              }
	            }
	            catch (Exception edocs) {
	              Logger.warning(login, this, "setup", 
	                  procData.getSignature() + "Error processing document " + sDocId, edocs);
	            }
	          }
	        }
	        props.setProperty("filesAvailable",String.valueOf(filesAvailable));
	      }
	    }
	}
  
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
