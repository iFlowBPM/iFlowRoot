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
package pt.iflow.blocks;

import java.io.OutputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormOperations;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.blocks.form.FormXMLReader;
import pt.iflow.api.blocks.form.IWidget;
import pt.iflow.api.blocks.form.IWidgetFactory;
import pt.iflow.api.blocks.form.Tab;
import pt.iflow.api.blocks.form.TransformUtility;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogueImpl;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.documents.DocumentSessionHelper;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.webform.WebWidgetFactory;
import pt.iflow.search.PesquisaProcessoSessionData;
import pt.iknow.utils.html.FormData;

import com.twolattes.json.Marshaller;

public class BlockWebForm extends Block implements FormOperations {
  //Ports
  public Port portIn, portFront;

  private static final String sJSP_FORM = "webform.jsp";
  private static final Marshaller<Form> jsonMarshaller = Marshaller.create(Form.class);
  private final static String sFORM_NAME = "dados";
  private static final String PROPERTY_NAME_PREFIX="FORM_";

  private Form form = null;
  private boolean initialized = false;

  public BlockWebForm(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = true;
  }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portFront;

    // remove error.
    procData.clearError();

    return outPort;
  }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // Variables
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    String description = this.getDescription(userInfo, procData);
    String url         = this.getUrl(userInfo, procData);

    Logger.trace(this,"before",login + " call with subpid="+subpid+",pid="+pid+",flowid="+flowid);

    String nextPage = url;

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    try {
      Activity activity = new Activity(login,flowid,pid,subpid,0,0,description,url,1);
      activity.mid = procData.getMid();
      activity.setRead();
      pm.updateActivity(userInfo,activity);
    }
    catch (Exception e) {
      Logger.error(login, this, "before",
          procData.getSignature() + "Caught an unexpected exception scheduling activities: " + e.getMessage(), e);
    }


    String sCreator = procData.getAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR);
    if (StringUtils.isEmpty(sCreator)) {
      sCreator = procData.getCreator();
      if (!login.equals(sCreator)) {
        procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
      }
      else {
        if (pm != null) {
          Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
          while(it != null && it.hasNext()) {
            Activity activity = (Activity)it.next();
            if (!login.equals(activity.userid)) {
              // Another user has activity scheduled
              procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
              break;
            }
          }
        }
      }
    }

    this.init();

    return nextPage;
  }

  private synchronized void init() {
    if(!this.initialized) {
      try {
        Set<FormChunk> newData = new TreeSet<FormChunk>();
        Map<String,String> attributes = getAttributeMap();
        if (attributes != null && !attributes.isEmpty()) {
          for (String attr : attributes.keySet()) {
            newData.add(new FormChunk(attr, attributes.get(attr)));
          }
        }
        String data = StringUtils.join(newData, null);
        Logger.debug(null, this, "init", "Data is:\n"+data);
        this.form = jsonMarshaller.unmarshall(new JSONObject(data));

      } catch (Throwable t) {
        Logger.error(null, this, "init", "Error loading form data", t);
      }
      this.initialized = true;
    }
  }


  @Override
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Formulário");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Formulário Processado");
  }


  @Override
  public Port getEventPort() {
    return portEvent;
  }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    return new Port[]{portIn};
  }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    return new Port[]{portFront};
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    int flowid  = procData.getFlowId();
    int pid     = procData.getPid();
    int subpid  = procData.getSubPid();

    return "WebForm/" + sJSP_FORM + "?flowid="+ flowid+"&pid="+ pid + "&subpid="+subpid;
  }


  public boolean hasError(ProcessData procData) {
    boolean retObj = false;
    retObj = procData.hasError();
    return retObj;
  }

  public boolean setError(ProcessData procData, String asError) {
    procData.setError(asError);
    return true;
  }

  public String getError(ProcessData procData) {
    return procData.getError();
  }

  @SuppressWarnings("unchecked")
  public Object execute (int op, Object[] aoa) {
    Object retObj = null;

    switch (op) {
    case OP_REFRESH_CACHE:
      // void refreshCache()
      this.refreshCache(null); // dont really need userinfo
      break;
    case OP_GENERATE_FORM:
      // String generateForm(UserInfo,ProcessData,HashMap)
      retObj = generateForm((UserInfoInterface)aoa[0],
          (ProcessData)aoa[1],
          (Map<String,String>) aoa[2],
          (ServletUtils) aoa[3]);
      break;
    case OP_PROCESS_FORM:
      // DataSet processForm(UserInfo,ProcessData,HashMap)
      retObj = processForm((UserInfoInterface)aoa[0],
          (ProcessData)aoa[1],
          (FormData)aoa[2],
          (ServletUtils)aoa[3],
          (Boolean)aoa[4]);
      break;
    case OP_HAS_ERROR:
      // boolean hasError(ProcessData)
      retObj = new Boolean(this.hasError((ProcessData)aoa[0]));
      break;
    case OP_SET_ERROR:
      // boolean setError(ProcessData,String)
      retObj = new Boolean(this.setError((ProcessData)aoa[0], (String)aoa[1]));
      break;
    case OP_GET_ERROR:
      // String getError(ProcessData)
      retObj = this.getError((ProcessData)aoa[0]);
      break;
    case OP_GET_FORM_NAME:
      // String: var sFORM_NAME
      retObj = sFORM_NAME;
      break;
    case OP_EXPORT_SPREADSHEET:
      // String exportFieldToExcel (service)
      retObj = null;
      break;
    case OP_PRINT:
      // String print (service)
      retObj = null;
      break;
    case OP_GET_PRINT_STYLESHEET:
      // String attribute sPRINT_STYLESHEET
      retObj = this.form.getProperties().getPrintStylesheet();
      break;
    case OP_EXPORT_PROCTABLE_STYLESHEET:
      // String exportProcTableToExcel (service)
      retObj = null;
      break;
    case OP_GET_AUTOPRINT:
      // String auto print
      retObj = null;
      break;
    case OP_GET_PROCESS_SEARCH:
      // String get pesquisa processo
      retObj = null;
      break;
    case OP_AUTO_SUBMIT:
      // Boolean test if can move forward
      retObj = this.form.getProperties().getAutosubmit();
      break;
    default:
    }

    return retObj;
  }

  public String getFormName() {
    return sFORM_NAME;
  }

  public String getPrintStylesheet() {
    init();
    return this.form.getProperties().getPrintStylesheet();
  }

  public Boolean isForwardOnSubmit() {
    init();
    // TODO fixme - usar o parser
    return new Boolean(this.form.getProperties().getAutosubmit());
  }

  public String generateForm(final UserInfoInterface userInfo, final ProcessData procData, final Map<String, String> ahmHiddenFields, final ServletUtils servletContext) {
    init();
    return generate(userInfo, procData, form, ahmHiddenFields, servletContext);
  }

  static String generate(final UserInfoInterface userInfo, final ProcessData procData, final Form form, final Map<String, String> ahmHiddenFields, final ServletUtils servletContext) {

    // Cleanup pending documents
    if(servletContext != null) {
      HttpSession session = servletContext.getSession();
      if(null != session) {
        session.removeAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
      }
    }
    
    String sLogin = userInfo.getUtilizador();
    String retObj = null;

    long start = Runtime.getRuntime().freeMemory();
    Logger.debug(sLogin, "BlockWebForm","generateForm", "MEMORIA ANTES: " +  start);
    boolean debugForm = (StringUtils.isNotEmpty(Const.DEBUG_FORM) && ArrayUtils.contains(new String[]{ "true", "yes"}, Const.DEBUG_FORM.toLowerCase()));
    StringWriter outputFormXml = null;

    FormXMLReader readr = new FormXMLReader(userInfo, procData, form, new WebWidgetFactory(), servletContext);
    readr.setJsp(sJSP_FORM);
    readr.setFormName(sFORM_NAME);
    TransformUtility utility = TransformUtility.newInstance(readr);

    // usar o valor recebido no URL
    // String sUrl = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + Const.APP_URL_PREFIX ;
    String sUrl = servletContext.getRequestURL();
    Logger.debug(sLogin, "BlockWebForm", "generateForm", "Requested base URL: "+sUrl);
    Locale userLocale = userInfo.getUserSettings().getLocale();
    utility.setParameter("lang_string", userLocale.getLanguage());
    utility.setParameter("country_string", userLocale.getCountry());
    utility.setParameter("locale_string", userInfo.getUserSettings().getLangString());
    utility.setParameter("url_prefix", Const.APP_URL_PREFIX);
    utility.setParameter("full_url_prefix", sUrl);
    utility.setParameter("use_scanner","false");
    utility.setParameter("response", servletContext);
    utility.setParameter("theme", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());

    if(debugForm) {
      // debug form enabled
      outputFormXml = new StringWriter();
    }
    StringWriter outputStream = new StringWriter();

    try {
      // transformer.transform(inputSource, new StreamResult(outputStream));
      utility.transform(outputStream, outputFormXml);
    } catch (Throwable t) {
      Logger.error(sLogin, "BlockWebForm", "generateForm", "Error processing form", t);
      outputStream = new StringWriter();
      outputStream.write("Stylesheet inv&aacute;lida");

    }
    Logger.debug(sLogin, "BlockWebForm","generateForm", "MEMORIA DEPOIS: " +  (start - Runtime.getRuntime().freeMemory()));
    retObj = outputStream.toString();
    if(debugForm) {
      Logger.debug(sLogin, "BlockWebForm", "generateForm", "XML:\n"+outputFormXml);
      Logger.debug(sLogin, "BlockWebForm", "generateForm", "HTML:\n"+retObj);
    }

    return retObj;
  }

  public ProcessData processForm(final UserInfoInterface userInfo, final ProcessData procData, final FormData formData, final ServletUtils servletContext, final boolean ignoreValidation) {
    init();
    
    // Save pending documents
    if(servletContext != null) {
      HttpSession session = servletContext.getSession();
      if(null != session) {
        DocumentSessionHelper helper = (DocumentSessionHelper) session.getAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
        if(null != helper)
          helper.updateProcessData(userInfo, procData);
      }
    }

    IWidgetFactory factory = new WebWidgetFactory();

    List<Tab> tabs = form.getTabs();
    for(Tab tab : tabs) {
      List<Field> fields = tab.getFields();
      for(Field field : fields) {
        IWidget widget = factory.newWidget(field.getType());
        if(null != widget)
          widget.process(userInfo, procData, field, formData);
      }
    }

    return procData;
  }

  public String getPesquisaProcessoFIDs() {
    return null;
  }

  public String print(UserInfoInterface userInfo, ProcessData procData, String asField, ServletUtils response) {
    return null;
  }
  public String autoPrint() {
    return null;
  }

  public String exportFieldToSpreadSheet(UserInfoInterface userInfo, ProcessData procData, String asField, OutputStream apsOut,
      ServletUtils response) {
    return null;
  }

  public String exportProcTableToSpreadSheet(UserInfoInterface userInfo, ProcessData procData, PesquisaProcessoSessionData ppsd,
      OutputStream apsOut) {
    return null;
  }

  private static class FormChunk implements Comparable<FormChunk> {
    private int pos;
    private String chunk;

    public FormChunk(String name, String value) {
      pos = new Integer(name.substring(PROPERTY_NAME_PREFIX.length()));
      chunk = value;
    }

    public String toString() {
      return chunk;
    }

    public int compareTo(FormChunk o) {
      return pos-o.pos;
    }
  }


  public static String generatePreview(final UserInfoInterface userInfo, final String jsonForm, final String jsonCatalog, final ServletUtils servletContext) throws Throwable {
    String retObj = "";
    try {
      // deserialize form
      Form form = jsonMarshaller.unmarshall(new JSONObject(jsonForm));

      // deserialize catalog
      JSONArray catalogArray = new JSONArray(jsonCatalog);

      // Load catalogue and process data
      ProcessCatalogueImpl catalogue = new ProcessCatalogueImpl();
      ProcessData procData = new ProcessData(catalogue);
      for(int i = 0; i < catalogArray.length(); i++) {
        try {
          JSONObject catalogueEntry = catalogArray.getJSONObject(i);
          String name = catalogueEntry.getString("name");
          String desc = catalogueEntry.getString("desc");
          String sType = catalogueEntry.getString("type");
          String value = catalogueEntry.getString("value");

          DataTypeEnum type = DataTypeEnum.getDataType(sType);

          catalogue.setDataType(name, type.newDataTypeInstance(), desc, value);
          if(type.isList()) {
            ProcessListVariable list = new ProcessListVariable(catalogue.getDataType(name), name);
            if(StringUtils.isNotEmpty(value)) {
              JSONArray vArr = new JSONArray(value);
              for(int j = 0; j < vArr.length(); j++) {
                try {
                  list.parseAndAddNewItem(vArr.getString(j));
                } catch(ParseException e){
                }
              }
            }
            procData.setList(list, false);
          } else {
            ProcessSimpleVariable variable = new ProcessSimpleVariable(catalogue.getDataType(name), name);
            if(StringUtils.isNotEmpty(value)) {
              try {
                variable.parse(value);
              } catch(ParseException e) {
              }
            }
            procData.set(variable, false);
          }
        } catch(JSONException e) {
        }
      }

      retObj = generate(userInfo, procData, form, new HashMap<String, String>(), servletContext);

    } catch (Throwable t) {
      Logger.error(userInfo.getUtilizador(), "BlockWebForm", "generatePreview", "Erro ao gerar o formulário de exemplo", t);
      throw t;
    }

    return retObj;
  }

}
