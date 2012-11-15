package pt.iflow.blocks;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.axis.types.Time;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import microsoft_dynamics_schemas.Assiduity_Doc_Line_Line;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade_ServiceLocator;

import pt.common.properties.WebServiceProperties;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockCreateRegistoAssiduidade extends Block {

  public Port portIn, portOk, portError;

  private static final String IN_USERID = "In_UserID";
  private static final String IN_DATAREGISTO = "In_DataRegisto";
  private static final String IN_DATADOCUMENTO = "In_DataDocumento";
  private static final String IN_NUMS = "In_Nums";
  private static final String IN_CODUNIDADEMEDIDA = "In_CodUnidadeMedida";
  private static final String IN_NUMSEMPREGADO = "In_NumsEmpregado";
  private static final String IN_BEGINDATES = "In_BeginDates";
  private static final String IN_BEGINTIMES = "In_BeginTimes";
  private static final String IN_ENDDATES = "In_EndDates";
  private static final String IN_ENDTIMES = "In_EndTimes";
  private static final String OUT_NO = "Out_No";
  private static final String OUT_DESCRIPTION = "Out_Description";
  private static final String OUT_DATAREGISTO = "Out_DataRegisto";
  private static final String OUT_DATADOCUMENTO = "Out_DataDocumento";
  private static final String OUT_USERID = "Out_UserID";

  public BlockCreateRegistoAssiduidade(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portOk;
    retObj[1] = portError;
    return retObj;
  }

  public String before(UserInfoInterface userInfo, ProcessData procData) {
    this.init(userInfo);
    return "";
  }

  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port retObj = portOk;
    StringBuffer logMsg = new StringBuffer();

    try {
      WS_Registo_Assiduidade_ServiceLocator locator = new WS_Registo_Assiduidade_ServiceLocator();
      WS_Registo_Assiduidade_Port service = locator.getWS_Registo_Assiduidade_Port(WebServiceProperties.getCidadelaProxyURL());

      String in_UserId = procData.transform(userInfo, this.getAttribute(IN_USERID));
      Date in_DataRegisto = (Date)(procData.eval(userInfo, this.getAttribute(IN_DATAREGISTO)));
      Date in_DataDocumento = (Date)(procData.eval(userInfo, this.getAttribute(IN_DATADOCUMENTO)));
      String[] in_Nums = (String[])(procData.eval(userInfo, this.getAttribute(IN_NUMS)));
      String[] in_CodUnidadeMedida = (String[])(procData.eval(userInfo, this.getAttribute(IN_CODUNIDADEMEDIDA)));
      String[] in_NumsEmpregado = (String[])(procData.eval(userInfo, this.getAttribute(IN_NUMSEMPREGADO)));
      Date[] in_BeginDates = (Date[])(procData.eval(userInfo, this.getAttribute(IN_BEGINDATES)));
      Time[] in_BeginTimes = (Time[])(procData.eval(userInfo, this.getAttribute(IN_BEGINTIMES)));
      Date[] in_EndDates = (Date[])(procData.eval(userInfo, this.getAttribute(IN_ENDDATES)));
      Time[] in_EndTimes = (Time[])(procData.eval(userInfo, this.getAttribute(IN_ENDTIMES)));

      Logger.debug(userInfo.getUserId(), this, "after","CALLING: service: " 
          + "; user: " + in_UserId
          + "; dataRegisto: " + in_DataRegisto
          + "; dataDocumento: " + in_DataDocumento
          + "; nums: " + in_Nums
          + "; codsUnidadeMedida: " + in_CodUnidadeMedida
          + "; numsEmpregado: " + in_NumsEmpregado
          + "; beginDates: " + in_BeginDates
          + "; beginTimes: " + in_BeginTimes
          + "; endDates: " + in_EndDates
          + "; endTimes: " + in_EndTimes
      );

      WS_Registo_Assiduidade ws_Registo_Assiduidade = new WS_Registo_Assiduidade();
      ws_Registo_Assiduidade.setUser_ID(in_UserId);
      ws_Registo_Assiduidade.setPosting_Date(in_DataRegisto);
      ws_Registo_Assiduidade.setDocument_Date(in_DataDocumento);
      Assiduity_Doc_Line_Line[] lines = new Assiduity_Doc_Line_Line[in_Nums.length];
      for (int i = 0; in_Nums!=null && i < in_Nums.length; i++) {
        Assiduity_Doc_Line_Line assiduity_Doc_Line_Line = new Assiduity_Doc_Line_Line();

        assiduity_Doc_Line_Line.setNo(in_Nums[i]);
        if (in_NumsEmpregado!= null && in_NumsEmpregado.length < i)
          assiduity_Doc_Line_Line.setEmployee_No(in_NumsEmpregado[i]);
        if (in_BeginDates!= null && in_BeginDates.length < i)
          assiduity_Doc_Line_Line.setBegin_Date(in_BeginDates[i]);
        if (in_BeginTimes!= null && in_BeginTimes.length < i)
          assiduity_Doc_Line_Line.setBegin_Hour(in_BeginTimes[i]);
        if (in_EndDates!= null && in_EndDates.length < i)
          assiduity_Doc_Line_Line.setEnd_Date(in_EndDates[i]);
        if (in_EndTimes!= null && in_EndTimes.length < i)
          assiduity_Doc_Line_Line.setEnd_Hour(in_EndTimes[i]);
        lines[i] = assiduity_Doc_Line_Line;
      }
      ws_Registo_Assiduidade.setSubformLinhas(lines);

      WS_Registo_Assiduidade result = service.create(ws_Registo_Assiduidade);

      if (result != null)
      {
        procData.parseAndSet(getAttribute(OUT_NO), result.getNo());
        procData.parseAndSet(getAttribute(OUT_DESCRIPTION), result.getDescription());
        procData.set(getAttribute(OUT_DATAREGISTO), result.getPosting_Date());
        procData.set(getAttribute(OUT_DATADOCUMENTO), result.getDocument_Date());
        procData.parseAndSet(getAttribute(OUT_USERID), result.getUser_ID());
      } else {
        retObj = portError;
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUserId(), this, "after", "cauth exception: " + e.getMessage());
      retObj = portError;
    }

    logMsg.append("Using '" + retObj.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return retObj;
  }

  protected void init(UserInfoInterface userInfo) {
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Invocação de WebService Cidadela (CreateRegistoAssiduidade)");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Invocação de WebService Cidadela (CreateRegistoAssiduidade)  efectuada");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}