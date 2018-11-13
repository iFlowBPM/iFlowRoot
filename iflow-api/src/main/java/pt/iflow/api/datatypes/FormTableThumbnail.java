package pt.iflow.api.datatypes;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.html.FormData;

public class FormTableThumbnail implements DataTypeInterface, ArrayTableProcessingCapable {

	  public FormTableThumbnail() {
	  }

	  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps, Map<String,Object> deps) {
	  }

	  public int getID() {
	    return 16;
	  }

	  public String getDescription() {
	    return Messages.getString("thumbnail"); //$NON-NLS-1$
	  }

	  public String getShortDescription() {
	    return Messages.getString("thumbnail"); //$NON-NLS-1$
	  }

	  public String getPrimitiveType() {
	    return getPrimitiveTypeMethod();
	  }

	  public String getPrimitiveTypeMethod() {
	    return "String"; //$NON-NLS-1$
	  }

	  public int getFormSize() {
	    return 1;
	  }

	  public String getFormPrefix() {
	    return getFormPrefix(null);
	  }
	  public String getFormPrefix(Object[] aoaArgs) {
	    return ""; //$NON-NLS-1$
	  }

	  public String getFormSuffix() {
	    return getFormSuffix(null);
	  }
	  public String getFormSuffix(Object[] aoaArgs) {
	    return ""; //$NON-NLS-1$
	  }

	  public String validateFormData (Object input) {
	    return validateFormData(input,null);
	  }

	  public String validateFormData (Object input, Object[] aoaArgs) {
	    return null;
	  }

	  public String format(UserInfoInterface userInfo, 
	      ProcessData procData,
	      FormService service,
	      int fieldNumber, 
	      String name,
	      ProcessVariableValue value, 
	      Properties props,
	      ServletUtils response) {
	    return formatRow(userInfo, procData, service, fieldNumber, -1, name, -1, value, props, response);
	  }
	  
	  public String formatRow(UserInfoInterface userInfo, 
	      ProcessData procData,
	      FormService service,
	      int fieldNumber,
	      int varIndex,
	      String name,
	      int row,
	      ProcessVariableValue value, 
	      Properties props,
	      ServletUtils response) {

	    String propPrefix = varIndex < 0 || row < 0 ? "" : varIndex + "_" + row + "_";
	    props.setProperty(propPrefix + "prefix", getFormPrefix());
	    props.setProperty(propPrefix + "suffix", getFormSuffix());
	    
	    String val = value != null ? value.format() : "";

	    String docUrl = "#";
	    String docFile = "-";
	    try {
	      DocumentIdentifier did = DocumentIdentifier.getInstance(val);
	      docUrl = FormUtils.generateDocumentURL(userInfo, response, procData, did);
	      Document docData = BeanFactory.getDocumentsBean().getDocumentInfo(userInfo, procData, did);
	      docFile = null == docData? "" : StringEscapeUtils.escapeXml(docData.getFileName());
	    } catch (Throwable t) {
	      Logger.warning(userInfo.getUtilizador(), this, "format", 
	          procData.getSignature() + "exception processing formtabledocument with id: " + val, t);                
	    }
	    String linkXml = genLinkXml(val, docUrl, docFile);
	    linkXml = StringUtils.replace(linkXml, "<a>", "<thumbnail>");
	    linkXml = StringUtils.replace(linkXml, "</a>", "</thumbnail>");
	    linkXml = StringUtils.replace(linkXml, "<href>", "<src>");
	    linkXml = StringUtils.replace(linkXml, "</href>", "</src>");
	    return linkXml;
	  }
	  
	  public String formatToHtml (Object input) {
	    return genLinkXml(value(input),null,null);
	  }

	  public String formatToHtml (Object input, Object[] aoaArgs) {
	    return genLinkXml(value(input),(String)aoaArgs[0],(String)aoaArgs[1]);
	  }

	  public String formatToForm (Object input) {
	    return genLinkXml(value(input),null,null);
	  }

	  public String formatToForm (Object input, Object[] aoaArgs) {
	    return genLinkXml(value(input),(String)aoaArgs[0],(String)aoaArgs[1]);
	  }


	  public String getText(Object input) {
	    return value(input);
	  }

	  public double getValue(Object input) {
	    return java.lang.Double.NaN;
	  }


	  public static String value (Object s) {
	    return (String)s;
	  }

	  private static String genLinkXml(String sFileId, String URL, String fileName) {
	    return FormUtils.genLinkXml(URL, fileName, false);
	  }

	  public void setLocale(Locale locale) {
	  }

	  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props,
	      StringBuilder logBuffer) {
	  }

	  public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props,
	      boolean ignoreValidation, StringBuilder logBuffer) {
	    return null;
	  }

	  public String parseAndSetList(UserInfoInterface userInfo, ProcessData procData, int varIndex, String name, int count, FormData formData,
	      Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
	    return null;
	  }

}
