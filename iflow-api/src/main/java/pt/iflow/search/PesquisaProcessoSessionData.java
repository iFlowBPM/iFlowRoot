package pt.iflow.search;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.blocks.FormService;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.Repository;
import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class PesquisaProcessoSessionData implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3215450981404848170L;
  
  private int curPage;
  private int pageCount;
  private int pageSize;
  
  private UserInfoInterface userInfo;
  
  private int len;
  private ProcessData procData;
  
  private ArrayList[] pages;
  private String[] fields;
  private String[] passLinks;
  
  private ProcessManager pm;
  
  private String arrays, tagprefix, linkLabel;
  private boolean showLinks;
  private int fieldId;
  
  private String identifier;
  
  private int preload;
  
  private DataTypeInterface [] dataTypes;
  
  public static final String sROW_TAG="row";
  public static final String sCOL_TAG="col";
  public static final String sVALUE_TAG="v";
  
  
  
  public PesquisaProcessoSessionData(ProcessManager pm, UserInfoInterface uInfo, ProcessData procData, String arrays, String[] fields, 
      String [] dataTypes, int pageSize, String tagprefix, String links, int fieldId, int preload, String link_label, String [] passLinks) {
    this.pm = pm;
    this.fields = fields;
    this.userInfo = uInfo;
    this.procData = procData;
    this.arrays = arrays;
    this.tagprefix = tagprefix;
    this.showLinks = "yes".equals(links);
    this.fieldId = fieldId;
    this.preload = preload;
    this.linkLabel = link_label;
    this.passLinks = passLinks;

    this.identifier = "&amp;flowid="+procData.getFlowId()+"&amp;pid="+procData.getPid()+"&amp;subpid="+procData.getSubPid();
    
    init(dataTypes, pageSize);
  }

  /**
   * Load data from dataset and prepare internal state
   * @param procData
   * @param arrays
   * @param pageSize
   */
  private void init(String [] dataTypes, int pageSize) {
    
    this.dataTypes = new DataTypeInterface[fields.length];
    Locale locale = userInfo.getUserSettings().getLocale();

    try {
      Repository rep = BeanFactory.getRepBean();
      for(int i = 0; i < fields.length; i++) {
        try {
          Class<?> c = rep.loadClass(Const.SYSTEM_ORGANIZATION, dataTypes[i]);
          this.dataTypes[i] = (DataTypeInterface) c.newInstance();
          this.dataTypes[i].setLocale(locale);
        } catch(Throwable t) {
          this.dataTypes[i] = new DatatypeImpl();
        }
      }
    } catch(Throwable t) {
      // Reset datatypes...
      for(int i = 0; i < fields.length; i++)
        this.dataTypes[i] = new DatatypeImpl();
    }
    
    
    len = procData.getList(arrays).size();
    
    pageCount = len / pageSize;
    int pageMod = len % pageSize;
    if(pageMod != 0) pageCount++;
    curPage = 0;
    this.pageSize = pageSize;
    
    pages = new ArrayList[pageCount];
    Arrays.fill(pages, null);
    
    
    preLoad(curPage);
    fetch();
  }
  
  
  private void fetch() {
    for(int i = 0; i < preload; i++) {
      int p = i+curPage+1;
      if(p < pageCount && pages[p] == null) preLoad(p);
    }
  }
  
  
  private static final int MODE = Const.nALL_PROCS_READONLY;
  
  // TODO Seria interessante ter uma thread que fosse carregando os resultados em background. Caso a pagina i ainda nao estivesse pronta, aguardava aqui...
  
  
  
  private void preLoad(int page) {

    int baseP = page * pageSize;

    int toLoad = Math.min(len-baseP, pageSize);

    ProcessHeader[] pha = new ProcessHeader[toLoad];

    ProcessListVariable var = procData.getList(arrays);
    for(int i = 0; i < toLoad; i++) {
      int fid, pid, sid;
      String line = var.getFormattedItem(i+baseP);
      String [] l = line.split("/");
      fid = Integer.parseInt(l[0]);
      pid = Integer.parseInt(l[1]);
      sid = Integer.parseInt(l[2]);
      pha[i] = new ProcessHeader(fid, pid, sid);
    }

    // The return value can have less results than expected...
    //ProcessData[] pd = pm.getProcessesData(userInfo, pha, fields, MODE);
    ProcessData[] pd = pm.getProcessesData(userInfo, pha, MODE);

    ArrayList<PesquisaProcessoRow> pageData = new ArrayList<PesquisaProcessoRow>();
    // get process data like pid, fid, sid, gid, uid
    for(int i = 0; i < pd.length; i++) {
      // pages[page][i] = new Hashtable();

      String link = null;

      if(showLinks) {
        StringBuffer sb = new StringBuffer();
        sb.append(identifier).append("&amp;").append(linkLabel).append("_").append(fieldId).append("=1");
        sb.append("&amp;").append(linkLabel).append("_").append(fieldId).append("_").append("flowid=").append(pd[i].getFlowId());
        sb.append("&amp;").append(linkLabel).append("_").append(fieldId).append("_").append("pid=").append(pd[i].getPid());
        sb.append("&amp;").append(linkLabel).append("_").append(fieldId).append("_").append("subpid=").append(pd[i].getSubPid());
        for(int j = 0; j < fields.length; j++) {
          String field = fields[j];
          DataTypeInterface dti = dataTypes[j];
          String value = dti.formatToHtml(pd[i].getFormatted(field));
          if(null == value) value = "";

          try {
            value = URLEncoder.encode(value, "UTF-8");
          } catch(Throwable t) {
          }
          if("true".equals(passLinks[j]))
            sb.append("&amp;").append(linkLabel).append("_").append(fieldId).append("_").append(field).append("=").append(value);

        }
        link = sb.toString();
      }

      String [] pageValues = new String [fields.length];
      for(int j = 0; j < fields.length; j++) {
        String field = fields[j];
        DataTypeInterface dti = dataTypes[j];

        String value = dti.formatToHtml(pd[i].getFormatted(field));
        if(null == value) value = "";
        pageValues[j] = value;
      }
      pageData.add(new PesquisaProcessoRow(link, pageValues));
    }

    pages[page] = pageData;

  }
  
  public ArrayList getPage() {
    if(pages[curPage] == null) preLoad(curPage);
    
    fetch();
    
    return pages[curPage];
  }
  
  public boolean hasNext() {
    return curPage+1 < pages.length;
  }
  
  public boolean hasPrevious() {
    return curPage > 0;
  }
  
  public void nextPage() {
    if(curPage+1 < pages.length) curPage++;
  }
  
  public void previousPage() {
    if(curPage > 0) curPage--;
  }
  
  public int getCurrent() {
    return curPage+1;
  }
  
  public ArrayList exportAllData() {
    ArrayList result = new ArrayList();
    ArrayList allPages = getAllPages();
    
    for(int j = 0; allPages != null && j < allPages.size(); j++) {
      PesquisaProcessoRow row = (PesquisaProcessoRow) allPages.get(j);
      String [] values = row.getValues();
      ArrayList line = new ArrayList();
      for(int k = 0; k < values.length; k++)
        line.add(values[k]);
      result.add(line);
    }
    
    return result;
  }
  
  public ArrayList exportPrintData() {
    ArrayList result = new ArrayList();
    ArrayList allPages = getAllPages();
    
    for(int j = 0; allPages != null && j < allPages.size(); j++) {
      PesquisaProcessoRow row = (PesquisaProcessoRow) allPages.get(j);
      String [] values = row.getValues();
      HashMap line = new HashMap();
      
      for(int k = 0; k < values.length; k++)
        line.put(fields[k], values[k]);
      result.add(line);
    }
    
    return result;
  }
  
  public ArrayList getAllPages() {
    ArrayList result = new ArrayList();
    int actPage = curPage;
    curPage = 0;
    for(int i = 0; i < pageCount; i++) {
      ArrayList page = getPage();
      if(null != page)
        result.addAll(page);
      nextPage();
    }
    curPage = actPage;
    
    return result;
  }
  
  public String getTagPrefix() {
    return tagprefix;
  }
  
  public int getPageSize() {
    return pageSize;
  }
  
  public int getNumCols() {
    return fields.length;
  }
  
  // failsafe datatype...
  private class DatatypeImpl implements DataTypeInterface {

    private String var;
    private int forIndex = -1;
    
    public String formatToForm(Object arg0) {
      return formatToHtml(arg0);
    }

    public String formatToForm(Object arg0, Object[] arg1) {
      return formatToHtml(arg0);
    }

    public String formatToHtml(Object arg0) {
      return String.valueOf(arg0);
    }

    public String formatToHtml(Object arg0, Object[] arg1) {
      return formatToHtml(arg0);
    }

    public String getDescription() {
      return null;
    }

    public String getFormPrefix() {
      return null;
    }

    public String getFormPrefix(Object[] arg0) {
      return null;
    }

    public String getFormSuffix() {
      return null;
    }

    public String getFormSuffix(Object[] arg0) {
      return null;
    }

    public int getID() {
      return 0;
    }

    public String getPrimitiveType() {
      return null;
    }

    public String getPrimitiveTypeMethod() {
      return null;
    }

    public String getShortDescription() {
      return null;
    }

    public String getText(Object arg0) {
      return null;
    }

    public double getValue(Object arg0) {
      return 0;
    }

    public String validateFormData(Object arg0) {
      return null;
    }

    public String validateFormData(Object arg0, Object[] arg1) {
      return null;
    }
    
    public void setLocale(Locale locale) {
    }

    public String format(UserInfoInterface userInfo, ProcessData procData, FormService service, 
        int fieldNumber, boolean isOutput, String name,
        ProcessVariableValue value, Properties props, ServletUtils response) {
      return formatRow(userInfo, procData, service, fieldNumber, isOutput, -1, name, -1, value, props, response);
    }

    public String formatRow(UserInfoInterface userInfo, ProcessData procData, FormService service, 
        int fieldNumber, boolean isOutput, int varIndex, String name,
        int row, ProcessVariableValue value, Properties props, ServletUtils response) {
      return value != null ? value.format() : "";
    }

    public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    }

    public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props,
        StringBuilder logBuffer) {
      // TODO Auto-generated method stub
      
    }

    public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props,
        boolean ignoreValidation, StringBuilder logBuffer) {
      // TODO Auto-generated method stub
      return null;
    }

    public String parseAndSetList(UserInfoInterface userInfo, ProcessData procData, int varIndex, String name, int count, FormData formData,
        Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
      // TODO Auto-generated method stub
      return null;
    }

    public void setVariable(String varName) {
      setVariable(varName, -1);
    }

    public void setVariable(String varName, int forIndex) {
      this.var = varName;
      this.forIndex = forIndex;
    }
    
  }
  
  
}
