package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;

public class DMSTable implements FieldInterface {

	protected static final String TITLE = "{0}_title";
    protected static final String ALIGN = "{0}_align";
	protected static final String VALUE = "{0}_{1}_value"; // [row]_[col]_value

	public String getDescription() {
		return "Tabela DMS (BETA)";
	}

	public String getXML(Properties prop) {
		try {
			int nRows = 0;
			int nCols = 0;
			StringBuffer sb = new StringBuffer();
			sb.append("<field><type>arraytable</type>");
			sb.append("<fieldid>").append(prop.getProperty("fieldid")).append("</fieldid>");
			sb.append("<print>false</print>");
			sb.append("<export>false</export>");

			// START -: HEADER
			sb.append("<row>");
			for (int i = 0; true; i++) {
				String titleKey = MessageFormat.format(TITLE, new Object[] { i });
				if (!prop.containsKey(titleKey)) {
					break;
				}
				nCols++;
				sb.append("<col>");
				sb.append("<header>true</header>");
				sb.append("<value>");
				sb.append((String) prop.getProperty(titleKey));
				sb.append("</value>");
				sb.append("<align>");
                String alignKey = MessageFormat.format(ALIGN, new Object[] { i });
                String align = (String)prop.getProperty(alignKey);
                if (StringUtils.isEmpty(align))
                    align = "center";
				sb.append(align);
				sb.append("</align>");
				sb.append("</col>");
			}
			sb.append("</row>");
			// END -: HEADER

			// START -: BODY
			Map<Integer, Map<Integer, String>> rowMap = new HashMap<Integer, Map<Integer, String>>();
			for (int col = 0; col < nCols; col++) {
				for (int row = 0; true; row++) {
					String valueKey = MessageFormat.format(VALUE, new Object[] { row, col });
					if (!prop.containsKey(valueKey)) {
						break;
					}
					Map<Integer, String> colMap;
					if (rowMap.containsKey(row)) {
						colMap = rowMap.get(row);
					} else {
						colMap = new HashMap<Integer, String>();
					}
					colMap.put(col, prop.getProperty(valueKey));
					rowMap.put(row, colMap);
				}
			}
			nRows = rowMap.size();
			for (int row = 0; row < nRows; row++) {
				Map<Integer, String> colMap = rowMap.get(row);
				if (colMap != null) {
					sb.append("<row>");
					for (int col = 0; col < nCols; col++) {
						String value = colMap.get(col);
						if (StringUtils.isEmpty(value)) {
							value = " ";
						}
						sb.append("<col>");
						sb.append("<header>false</header>");
		                sb.append("<align>");
		                String alignKey = MessageFormat.format(ALIGN, new Object[] { col });
		                String align = (String)prop.getProperty(alignKey);
		                if (StringUtils.isEmpty(align)) {
		                  align = "center";
		                }
		                sb.append(align);
		                sb.append("</align>");
						
						sb.append("<value>").append(value).append("</value>");
						sb.append("</col>");
					}
					sb.append("</row>");
				}
			}
			// END -: BODY

			// add properties with count of data rows and number of columns
			sb.append("<datarow_count>");
			sb.append(nRows);
			sb.append("</datarow_count>");
			sb.append("<column_count>");
			sb.append(nCols);
			sb.append("</column_count>");

			sb.append("<even_field>");
			if (prop.containsKey("even_field")) {
				sb.append(prop.getProperty("even_field"));
			}
			sb.append("</even_field>");
			sb.append("</field>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
		try {
			// XXX BETA version has no macro-title
			String titlesProp = props.getProperty(FormProps.DMS_TITLES);
			String varsProp = props.getProperty(FormProps.DMS_VARS);
            String alignProp = props.getProperty(FormProps.DMS_ALIGN);
			String[] titles = titlesProp.split(",");
			String[] vars = varsProp.split(",");
            String[] align = StringUtils.isEmpty(alignProp) ? null : alignProp.split(",");
			for (int i = 0; i < titles.length; i++) {
				props.put(MessageFormat.format(TITLE, new Object[] { i }), StringUtils.isEmpty(titles[i]) ? "" : titles[i]);
                String colAlign = "";
                if (align != null && align.length > i) {
                  colAlign = align[i]; 
                }
				props.put(MessageFormat.format(ALIGN, new Object[] { i }), colAlign);
			}

			// XXX BETA version uses default table formatting

			String folder = getParsedProperty(userInfo, procData, props.getProperty(FormProps.DMS_FOLDER));
			DMSUtils dms = DMSUtils.getInstance();
			ContentResult parent = dms.getDirectDescendents(DMSConnectorUtils.createCredential(userInfo, procData), null,
			    folder);
			if (parent != null) {
				List<ContentResult> children = parent.getChildren();
				// XXX BETA version has crap link input
				for (int row = 0; children != null && row < children.size(); row++) {
					ContentResult child = children.get(row);
					for (int col = 0; col < vars.length; col++) {
						String var = vars[col];
						if (StringUtils.isEmpty(var)) {
							props.put(MessageFormat.format(VALUE, new Object[] { row, col }), var);
						} else if (StringUtils.equals("description", var)) {
							props.put(MessageFormat.format(VALUE, new Object[] { row, col }), child.getDescription());
						} else if (StringUtils.equals("name", var)) {
							props.put(MessageFormat.format(VALUE, new Object[] { row, col }), child.getName());
						} else if (StringUtils.equals("link", var)) {
						  DocumentIdentifier did = DocumentIdentifier.getInstance(child.getId());					      
						  String docUrl = FormUtils.generateDocumentURL(userInfo, response, procData, did);
						  String docName = StringEscapeUtils.escapeXml(child.getName());
						  String link = FormUtils.genLinkXml(docUrl, docName, false);
						  props.put(MessageFormat.format(VALUE, new Object[] { row, col }),link); 
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), this, "getXML", "Exception caught: ", e);
		}
	}

	public void generateHTML(PrintStream out, Properties prop) {
	}

	public void generateXML(PrintStream out, Properties prop) {
	}

	public void generateXSL(PrintStream out) {
	}

	public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
	}

	public boolean isArrayTable() {
		return false;
	}

	public boolean isOutputField() {
		return true;
	}
	
	private static String getParsedProperty(UserInfoInterface userInfo, ProcessData procData, String value)
	throws EvalException {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		String retObj = value;
		retObj = procData.transform(userInfo, retObj);
		if (StringUtils.isBlank(retObj)) {
			retObj = value;
		}
		return retObj;
	}
}
