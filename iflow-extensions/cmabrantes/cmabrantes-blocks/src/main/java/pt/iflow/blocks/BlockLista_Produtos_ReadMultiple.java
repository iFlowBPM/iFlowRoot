package pt.iflow.blocks;

import java.util.Date;

import microsoft_dynamics_schemas.WS_Lista_Produtos;
import microsoft_dynamics_schemas.WS_Lista_Produtos_Fields;
import microsoft_dynamics_schemas.WS_Lista_Produtos_Filter;
import microsoft_dynamics_schemas.WS_Lista_Produtos_Port;
import microsoft_dynamics_schemas.WS_Lista_Produtos_ServiceLocator;

import pt.common.properties.WebServiceProperties;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockLista_Produtos_ReadMultiple extends Block {

  public Port portIn, portOk, portError;

  private static final String IN_FILTER_FIELD = "in_filter_field";
  private static final String IN_FILTER_CRITERIA = "in_filter_criteria";
  private static final String IN_SETSIZE = "in_setSize";
  private static final String OUT_KEY = "out_key";
  private static final String OUT_NO = "out_no";
  private static final String OUT_NAME = "out_name";
  private static final String OUT_RESPONSIBILITY_CENTER = "out_responsibility_Center";
  private static final String OUT_LOCATION_CODE = "out_location_Code";
  private static final String OUT_POST_CODE = "out_post_Code";
  private static final String OUT_COUNTRY_REGION_CODE = "out_country_Region_Code";
  private static final String OUT_PHONE_NO = "out_phone_No";
  private static final String OUT_FAX_NO = "out_fax_No";
  private static final String OUT_IC_PARTNER_CODE = "out_IC_Partner_Code";
  private static final String OUT_CONTACT = "out_contact";
  private static final String OUT_SALESPERSON_CODE = "out_salesperson_Code";
  private static final String OUT_CUSTOMER_POSTING_GROUP = "out_customer_Posting_Group";
  private static final String OUT_GEN_BUS_POSTING_GROUP = "out_gen_Bus_Posting_Group";
  private static final String OUT_VAT_BUS_POSTING_GROUP = "out_VAT_Bus_Posting_Group";
  private static final String OUT_CUSTOMER_PRICE_GROUP = "out_customer_Price_Group";
  private static final String OUT_CUSTOMER_DISC_GROUP = "out_customer_Disc_Group";
  private static final String OUT_PAYMENT_TERMS_CODE = "out_payment_Terms_Code";
  private static final String OUT_REMINDER_TERMS_CODE = "out_reminder_Terms_Code";
  private static final String OUT_FIN_CHARGE_TERMS_CODE = "out_fin_Charge_Terms_Code";
  private static final String OUT_CURRENCY_CODE = "out_currency_Code";
  private static final String OUT_LANGUAGE_CODE = "out_language_Code";
  private static final String OUT_SEARCH_NAME = "out_search_Name";
  private static final String OUT_CREDIT_LIMIT_LCY = "out_credit_Limit_LCY";
  private static final String OUT_BLOCKED = "out_blocked";
  private static final String OUT_LAST_DATE_MODIFIED = "out_last_Date_Modified";
  private static final String OUT_APPLICATION_METHOD = "out_application_Method";
  private static final String OUT_COMBINE_SHIPMENTS = "out_combine_Shipments";
  private static final String OUT_RESERVE = "out_reserve";
  private static final String OUT_SHIPPING_ADVICE = "out_shipping_Advice";
  private static final String OUT_SHIPPING_AGENT_CODE = "out_shipping_Agent_Code";
  private static final String OUT_BASE_CALENDAR_CODE = "out_base_Calendar_Code";

  public BlockLista_Produtos_ReadMultiple(int anFlowId, int id, int subflowblockid, String filename) {
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
      WS_Lista_Produtos_ServiceLocator locator = new WS_Lista_Produtos_ServiceLocator();
      WS_Lista_Produtos_Port service = locator.getWS_Lista_Produtos_Port(WebServiceProperties.getCidadelaProxyURL());

      String[] in_filter_field = (String[])(procData.eval(userInfo, this.getAttribute(IN_FILTER_FIELD)));
      String[] in_filter_criteria = (String[])(procData.eval(userInfo, this.getAttribute(IN_FILTER_CRITERIA)));
      int in_setSize = (Integer)(procData.eval(userInfo, this.getAttribute(IN_SETSIZE)));

      WS_Lista_Produtos_Filter[] filters = new WS_Lista_Produtos_Filter[in_filter_field.length];
      for (int i = 0; i < in_filter_field.length; i++) {
        if (i < in_filter_criteria.length) {
          filters[i] = new WS_Lista_Produtos_Filter(WS_Lista_Produtos_Fields.fromValue(in_filter_field[i]), in_filter_criteria[i]);
        }
      }

      WS_Lista_Produtos[] result = service.readMultiple(filters, null, in_setSize);

      if (result != null)
      {
        String[] out_key = new String[result.length];
        String[] out_no = new String[result.length];
        String[] out_name = new String[result.length];
        String[] out_responsibility_Center = new String[result.length];
        String[] out_location_Code = new String[result.length];
        String[] out_post_Code = new String[result.length];
        String[] out_country_Region_Code = new String[result.length];
        String[] out_phone_No = new String[result.length];
        String[] out_fax_No = new String[result.length];
        String[] out_IC_Partner_Code = new String[result.length];
        String[] out_contact = new String[result.length];
        String[] out_salesperson_Code = new String[result.length];
        String[] out_customer_Posting_Group = new String[result.length];
        String[] out_gen_Bus_Posting_Group = new String[result.length];
        String[] out_VAT_Bus_Posting_Group = new String[result.length];
        String[] out_customer_Price_Group = new String[result.length];
        String[] out_customer_Disc_Group = new String[result.length];
        String[] out_payment_Terms_Code = new String[result.length];
        String[] out_reminder_Terms_Code = new String[result.length];
        String[] out_fin_Charge_Terms_Code = new String[result.length];
        String[] out_currency_Code = new String[result.length];
        String[] out_language_Code = new String[result.length];
        String[] out_search_Name = new String[result.length];
        double[] out_credit_Limit_LCY = new double[result.length];
        String[] out_blocked = new String[result.length];
        Date[] out_last_Date_Modified = new Date[result.length];
        String[] out_application_Method = new String[result.length];
        boolean[] out_combine_Shipments = new boolean[result.length];
        String[] out_reserve = new String[result.length];
        String[] out_shipping_Advice = new String[result.length];
        String[] out_shipping_Agent_Code = new String[result.length];
        String[] out_base_Calendar_Code = new String[result.length];
        for (int i =0; i < result.length; i++) {
          out_key[i] = result[i].getKey();
          out_no[i] = result[i].getNo();
          out_name[i] = result[i].getName();
          out_responsibility_Center[i] = result[i].getResponsibility_Center();
          out_location_Code[i] = result[i].getLocation_Code();
          out_post_Code[i] = result[i].getPost_Code();
          out_country_Region_Code[i] = result[i].getCountry_Region_Code();
          out_phone_No[i] = result[i].getPhone_No();
          out_fax_No[i] = result[i].getFax_No();
          out_IC_Partner_Code[i] = result[i].getIC_Partner_Code();
          out_contact[i] = result[i].getContact();
          out_salesperson_Code[i] = result[i].getSalesperson_Code();
          out_customer_Posting_Group[i] = result[i].getCustomer_Posting_Group();
          out_gen_Bus_Posting_Group[i] = result[i].getGen_Bus_Posting_Group();
          out_VAT_Bus_Posting_Group[i] = result[i].getVAT_Bus_Posting_Group();
          out_customer_Price_Group[i] = result[i].getCustomer_Price_Group();
          out_customer_Disc_Group[i] = result[i].getCustomer_Disc_Group();
          out_payment_Terms_Code[i] = result[i].getPayment_Terms_Code();
          out_reminder_Terms_Code[i] = result[i].getReminder_Terms_Code();
          out_fin_Charge_Terms_Code[i] = result[i].getFin_Charge_Terms_Code();
          out_currency_Code[i] = result[i].getCurrency_Code();
          out_language_Code[i] = result[i].getLanguage_Code();
          out_search_Name[i] = result[i].getSearch_Name();
          out_credit_Limit_LCY[i] = result[i].getCredit_Limit_LCY().doubleValue();
          out_blocked[i] = result[i].getBlocked().getValue();
          out_last_Date_Modified[i] = result[i].getLast_Date_Modified();
          out_application_Method[i] = result[i].getApplication_Method().getValue();
          out_combine_Shipments[i] = result[i].getCombine_Shipments();
          out_reserve[i] = result[i].getReserve().getValue();
          out_shipping_Advice[i] = result[i].getShipping_Advice().getValue();
          out_shipping_Agent_Code[i] = result[i].getShipping_Agent_Code();
          out_base_Calendar_Code[i] = result[i].getBase_Calendar_Code();
        }
        
        procData.set(getAttribute(OUT_KEY), out_key);
        procData.set(getAttribute(OUT_NO), out_no);
        procData.set(getAttribute(OUT_NAME), out_name);
        procData.set(getAttribute(OUT_RESPONSIBILITY_CENTER), out_responsibility_Center);
        procData.set(getAttribute(OUT_LOCATION_CODE), out_location_Code);
        procData.set(getAttribute(OUT_POST_CODE), out_post_Code);
        procData.set(getAttribute(OUT_COUNTRY_REGION_CODE), out_country_Region_Code);
        procData.set(getAttribute(OUT_PHONE_NO), out_phone_No);
        procData.set(getAttribute(OUT_FAX_NO), out_fax_No);
        procData.set(getAttribute(OUT_IC_PARTNER_CODE), out_IC_Partner_Code);
        procData.set(getAttribute(OUT_CONTACT), out_contact);
        procData.set(getAttribute(OUT_SALESPERSON_CODE), out_salesperson_Code);
        procData.set(getAttribute(OUT_CUSTOMER_POSTING_GROUP), out_customer_Posting_Group);
        procData.set(getAttribute(OUT_GEN_BUS_POSTING_GROUP), out_gen_Bus_Posting_Group);
        procData.set(getAttribute(OUT_VAT_BUS_POSTING_GROUP), out_VAT_Bus_Posting_Group);
        procData.set(getAttribute(OUT_CUSTOMER_PRICE_GROUP), out_customer_Price_Group);
        procData.set(getAttribute(OUT_CUSTOMER_DISC_GROUP), out_customer_Disc_Group);
        procData.set(getAttribute(OUT_PAYMENT_TERMS_CODE), out_payment_Terms_Code);
        procData.set(getAttribute(OUT_REMINDER_TERMS_CODE), out_reminder_Terms_Code);
        procData.set(getAttribute(OUT_FIN_CHARGE_TERMS_CODE), out_fin_Charge_Terms_Code);
        procData.set(getAttribute(OUT_CURRENCY_CODE), out_currency_Code);
        procData.set(getAttribute(OUT_LANGUAGE_CODE), out_language_Code);
        procData.set(getAttribute(OUT_SEARCH_NAME), out_search_Name);
        procData.set(getAttribute(OUT_CREDIT_LIMIT_LCY), out_credit_Limit_LCY);
        procData.set(getAttribute(OUT_BLOCKED), out_blocked);
        procData.set(getAttribute(OUT_LAST_DATE_MODIFIED), out_last_Date_Modified);
        procData.set(getAttribute(OUT_APPLICATION_METHOD), out_application_Method);
        procData.set(getAttribute(OUT_COMBINE_SHIPMENTS), out_combine_Shipments);
        procData.set(getAttribute(OUT_RESERVE), out_reserve);
        procData.set(getAttribute(OUT_SHIPPING_ADVICE), out_shipping_Advice);
        procData.set(getAttribute(OUT_SHIPPING_AGENT_CODE), out_shipping_Agent_Code);
        procData.set(getAttribute(OUT_BASE_CALENDAR_CODE), out_base_Calendar_Code);
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