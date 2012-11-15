package pt.iflow.blocks;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.axis.types.Time;

import microsoft_dynamics_schemas.Sales_Internal_Req_subform;
import microsoft_dynamics_schemas.Sub_Type_Item;
import microsoft_dynamics_schemas.Type;
import microsoft_dynamics_schemas.WS_ReqInternaMaterial;
import microsoft_dynamics_schemas.WS_ReqInternaMaterial_Fields;
import microsoft_dynamics_schemas.WS_ReqInternaMaterial_Filter;
import microsoft_dynamics_schemas.WS_ReqInternaMaterial_Port;
import microsoft_dynamics_schemas.WS_ReqInternaMaterial_ServiceLocator;

import pt.common.properties.WebServiceProperties;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockReqInternaMaterial_Create extends Block {

  public Port portIn, portOk, portError;

  private static final String IN_KEY = "in_key";
  private static final String IN_NO = "in_no";
  private static final String IN_SERVICE = "in_service";
  private static final String IN_SHIP_TO_NAME = "in_ship_to_Name";
  private static final String IN_SHIP_TO_ADDRESS = "in_ship_to_Address";
  private static final String IN_SHIP_TO_ADDRESS_2 = "in_ship_to_Address_2";
  private static final String IN_SHIP_TO_POST_CODE = "in_ship_to_Post_Code";
  private static final String IN_SHIP_TO_CITY = "in_ship_to_City";
  private static final String IN_SHIP_TO_COUNTY = "in_ship_to_County";
  private static final String IN_SHIP_TO_CONTACT = "in_ship_to_Contact";
  private static final String IN_POSTING_DATE = "in_posting_Date";
  private static final String IN_DOCUMENT_DATE = "in_document_Date";
  private static final String IN_USER = "in_user";
  private static final String IN_LOCATION_CODE = "in_location_Code";
  private static final String IN_SHIPMENT_METHOD_CODE = "in_shipment_Method_Code";
  private static final String IN_SHIPMENT_DATE = "in_shipment_Date";
  private static final String IN_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSHIPTOADDR_SELL_TO_CUSTOMER_NO = "in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No";
  private static final String IN_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFCONTACTS_REC = "in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec";
  private static final String IN_SALESLINES_KEY = "in_salesLines_key";
  private static final String IN_SALESLINES_TYPE = "in_salesLines_type";
  private static final String IN_SALESLINES_SUB_TYPE_ITEM = "in_salesLines_sub_Type_Item";
  private static final String IN_SALESLINES_NO = "in_salesLines_no";
  private static final String IN_SALESLINES_DESCRIPTION = "in_salesLines_description";
  private static final String IN_SALESLINES_DESCRIPTION_2 = "in_salesLines_description_2";
  private static final String IN_SALESLINES_QUANTITY = "in_salesLines_quantity";
  private static final String IN_SALESLINES_NO_MORE_QUANTITIES = "in_salesLines_no_More_Quantities";
  private static final String IN_SALESLINES_LOCATION_CODE = "in_salesLines_location_Code";
  private static final String IN_SALESLINES_NOT_IN_VAT_REPORT = "in_salesLines_not_in_Vat_Report";
  private static final String IN_SALESLINES_UNIT_OF_MEASURE_CODE = "in_salesLines_unit_of_Measure_Code";
  private static final String IN_SALESLINES_UNIT_OF_MEASURE = "in_salesLines_unit_of_Measure";
  private static final String IN_SALESLINES_USE_DUPLICATION_LIST = "in_salesLines_use_Duplication_List";
  private static final String IN_SALESLINES_APPL_FROM_ITEM_ENTRY = "in_salesLines_appl_from_Item_Entry";
  private static final String IN_SALESLINES_APPL_TO_ITEM_ENTRY = "in_salesLines_appl_to_Item_Entry";
  private static final String IN_SALESLINES_SHORTCUT_DIMENSION_1_CODE = "in_salesLines_shortcut_Dimension_1_Code";
  private static final String IN_SALESLINES_SHORTCUT_DIMENSION_2_CODE = "in_salesLines_shortcut_Dimension_2_Code";
  private static final String IN_SALESLINES_SHORTCUTDIMCODE_X005B_3_X005D_ = "in_salesLines_shortcutDimCode_x005B_3_x005D_";
  private static final String IN_SALESLINES_SHORTCUTDIMCODE_X005B_4_X005D_ = "in_salesLines_shortcutDimCode_x005B_4_x005D_";
  private static final String IN_SALESLINES_DRF_CODE = "in_salesLines_DRF_Code";
  private static final String IN_SALESLINES_BUDGET_DIMENSION_1_CODE = "in_salesLines_budget_Dimension_1_Code";
  private static final String IN_SALESLINES_BUDGET_DIMENSION_2_CODE = "in_salesLines_budget_Dimension_2_Code";
  private static final String IN_SALESLINES_BUDGET_DIMENSION_3_CODE = "in_salesLines_budget_Dimension_3_Code";
  private static final String IN_SALESLINES_BUDGET_DIMENSION_4_CODE = "in_salesLines_budget_Dimension_4_Code";
  private static final String IN_SALESLINES_TREASURY_OPERATION = "in_salesLines_treasury_Operation";
  private static final String IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCAVAILABILITY_REC = "in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec";
  private static final String IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSUBSTITUTIONS_REC = "in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec";
  private static final String IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESPRICES_REC = "in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec";
  private static final String IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESLINEDISC_REC = "in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec";
  private static final String OUT_KEY = "out_key";
  private static final String OUT_NO = "out_no";
  private static final String OUT_SERVICE = "out_service";
  private static final String OUT_SHIP_TO_NAME = "out_ship_to_Name";
  private static final String OUT_SHIP_TO_ADDRESS = "out_ship_to_Address";
  private static final String OUT_SHIP_TO_ADDRESS_2 = "out_ship_to_Address_2";
  private static final String OUT_SHIP_TO_POST_CODE = "out_ship_to_Post_Code";
  private static final String OUT_SHIP_TO_CITY = "out_ship_to_City";
  private static final String OUT_SHIP_TO_COUNTY = "out_ship_to_County";
  private static final String OUT_SHIP_TO_CONTACT = "out_ship_to_Contact";
  private static final String OUT_POSTING_DATE = "out_posting_Date";
  private static final String OUT_DOCUMENT_DATE = "out_document_Date";
  private static final String OUT_USER = "out_user";
  private static final String OUT_LOCATION_CODE = "out_location_Code";
  private static final String OUT_SHIPMENT_METHOD_CODE = "out_shipment_Method_Code";
  private static final String OUT_SHIPMENT_DATE = "out_shipment_Date";
  private static final String OUT_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSHIPTOADDR_SELL_TO_CUSTOMER_NO = "out_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No";
  private static final String OUT_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFCONTACTS_REC = "out_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec";
  private static final String OUT_SALESLINES_KEY = "out_salesLines_key";
  private static final String OUT_SALESLINES_TYPE = "out_salesLines_type";
  private static final String OUT_SALESLINES_SUB_TYPE_ITEM = "out_salesLines_sub_Type_Item";
  private static final String OUT_SALESLINES_NO = "out_salesLines_no";
  private static final String OUT_SALESLINES_DESCRIPTION = "out_salesLines_description";
  private static final String OUT_SALESLINES_DESCRIPTION_2 = "out_salesLines_description_2";
  private static final String OUT_SALESLINES_QUANTITY = "out_salesLines_quantity";
  private static final String OUT_SALESLINES_NO_MORE_QUANTITIES = "out_salesLines_no_More_Quantities";
  private static final String OUT_SALESLINES_LOCATION_CODE = "out_salesLines_location_Code";
  private static final String OUT_SALESLINES_NOT_IN_VAT_REPORT = "out_salesLines_not_in_Vat_Report";
  private static final String OUT_SALESLINES_UNIT_OF_MEASURE_CODE = "out_salesLines_unit_of_Measure_Code";
  private static final String OUT_SALESLINES_UNIT_OF_MEASURE = "out_salesLines_unit_of_Measure";
  private static final String OUT_SALESLINES_USE_DUPLICATION_LIST = "out_salesLines_use_Duplication_List";
  private static final String OUT_SALESLINES_APPL_FROM_ITEM_ENTRY = "out_salesLines_appl_from_Item_Entry";
  private static final String OUT_SALESLINES_APPL_TO_ITEM_ENTRY = "out_salesLines_appl_to_Item_Entry";
  private static final String OUT_SALESLINES_SHORTCUT_DIMENSION_1_CODE = "out_salesLines_shortcut_Dimension_1_Code";
  private static final String OUT_SALESLINES_SHORTCUT_DIMENSION_2_CODE = "out_salesLines_shortcut_Dimension_2_Code";
  private static final String OUT_SALESLINES_SHORTCUTDIMCODE_X005B_3_X005D_ = "out_salesLines_shortcutDimCode_x005B_3_x005D_";
  private static final String OUT_SALESLINES_SHORTCUTDIMCODE_X005B_4_X005D_ = "out_salesLines_shortcutDimCode_x005B_4_x005D_";
  private static final String OUT_SALESLINES_DRF_CODE = "out_salesLines_DRF_Code";
  private static final String OUT_SALESLINES_BUDGET_DIMENSION_1_CODE = "out_salesLines_budget_Dimension_1_Code";
  private static final String OUT_SALESLINES_BUDGET_DIMENSION_2_CODE = "out_salesLines_budget_Dimension_2_Code";
  private static final String OUT_SALESLINES_BUDGET_DIMENSION_3_CODE = "out_salesLines_budget_Dimension_3_Code";
  private static final String OUT_SALESLINES_BUDGET_DIMENSION_4_CODE = "out_salesLines_budget_Dimension_4_Code";
  private static final String OUT_SALESLINES_TREASURY_OPERATION = "out_salesLines_treasury_Operation";
  private static final String OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCAVAILABILITY_REC = "out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec";
  private static final String OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSUBSTITUTIONS_REC = "out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec";
  private static final String OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESPRICES_REC = "out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec";
  private static final String OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESLINEDISC_REC = "out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec";

  public BlockReqInternaMaterial_Create(int anFlowId, int id, int subflowblockid, String filename) {
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
      WS_ReqInternaMaterial_ServiceLocator locator = new WS_ReqInternaMaterial_ServiceLocator();
      WS_ReqInternaMaterial_Port service = locator.getWS_ReqInternaMaterial_Port(WebServiceProperties.getCidadelaProxyURL());

      String in_key = procData.transform(userInfo, this.getAttribute(IN_KEY));
      String in_no = procData.transform(userInfo, this.getAttribute(IN_NO));
      String in_service = procData.transform(userInfo, this.getAttribute(IN_SERVICE));
      String in_ship_to_Name = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_NAME));
      String in_ship_to_Address = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_ADDRESS));
      String in_ship_to_Address_2 = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_ADDRESS_2));
      String in_ship_to_Post_Code = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_POST_CODE));
      String in_ship_to_City = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_CITY));
      String in_ship_to_County = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_COUNTY));
      String in_ship_to_Contact = procData.transform(userInfo, this.getAttribute(IN_SHIP_TO_CONTACT));
      Date in_posting_Date = (Date)(procData.eval(userInfo, this.getAttribute(IN_POSTING_DATE)));
      Date in_document_Date = (Date)(procData.eval(userInfo, this.getAttribute(IN_DOCUMENT_DATE)));
      String in_user = procData.transform(userInfo, this.getAttribute(IN_USER));
      String in_location_Code = procData.transform(userInfo, this.getAttribute(IN_LOCATION_CODE));
      String in_shipment_Method_Code = procData.transform(userInfo, this.getAttribute(IN_SHIPMENT_METHOD_CODE));
      Date in_shipment_Date = (Date)(procData.eval(userInfo, this.getAttribute(IN_SHIPMENT_DATE)));
      String in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No = procData.transform(userInfo, this.getAttribute(IN_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSHIPTOADDR_SELL_TO_CUSTOMER_NO));
      String in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec = procData.transform(userInfo, this.getAttribute(IN_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFCONTACTS_REC));
      String[] in_salesLines_key = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_KEY)));
      String[] in_salesLines_type = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_TYPE)));
      String[] in_salesLines_sub_Type_Item = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_SUB_TYPE_ITEM)));
      String[] in_salesLines_no = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_NO)));
      String[] in_salesLines_description = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_DESCRIPTION)));
      String[] in_salesLines_description_2 = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_DESCRIPTION_2)));
      Long[] in_salesLines_quantity = (Long[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_QUANTITY)));
      Boolean[] in_salesLines_no_More_Quantities = (Boolean[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_NO_MORE_QUANTITIES)));
      String[] in_salesLines_location_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_LOCATION_CODE)));
      Boolean[] in_salesLines_not_in_Vat_Report = (Boolean[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_NOT_IN_VAT_REPORT)));
      String[] in_salesLines_unit_of_Measure_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_UNIT_OF_MEASURE_CODE)));
      String[] in_salesLines_unit_of_Measure = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_UNIT_OF_MEASURE)));
      Boolean[] in_salesLines_use_Duplication_List = (Boolean[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_USE_DUPLICATION_LIST)));
      Integer[] in_salesLines_appl_from_Item_Entry = (Integer[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_APPL_FROM_ITEM_ENTRY)));
      Integer[] in_salesLines_appl_to_Item_Entry = (Integer[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_APPL_TO_ITEM_ENTRY)));
      String[] in_salesLines_shortcut_Dimension_1_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_SHORTCUT_DIMENSION_1_CODE)));
      String[] in_salesLines_shortcut_Dimension_2_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_SHORTCUT_DIMENSION_2_CODE)));
      String[] in_salesLines_shortcutDimCode_x005B_3_x005D_ = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_SHORTCUTDIMCODE_X005B_3_X005D_)));
      String[] in_salesLines_shortcutDimCode_x005B_4_x005D_ = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_SHORTCUTDIMCODE_X005B_4_X005D_)));
      String[] in_salesLines_DRF_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_DRF_CODE)));
      String[] in_salesLines_budget_Dimension_1_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_BUDGET_DIMENSION_1_CODE)));
      String[] in_salesLines_budget_Dimension_2_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_BUDGET_DIMENSION_2_CODE)));
      String[] in_salesLines_budget_Dimension_3_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_BUDGET_DIMENSION_3_CODE)));
      String[] in_salesLines_budget_Dimension_4_Code = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_BUDGET_DIMENSION_4_CODE)));
      String[] in_salesLines_treasury_Operation = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_TREASURY_OPERATION)));
      String[] in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCAVAILABILITY_REC)));
      String[] in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSUBSTITUTIONS_REC)));
      String[] in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESPRICES_REC)));
      String[] in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec = (String[])(procData.eval(userInfo, this.getAttribute(IN_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESLINEDISC_REC)));

      WS_ReqInternaMaterial reqInternaMaterial = new WS_ReqInternaMaterial();

      reqInternaMaterial.setKey(in_key);
      reqInternaMaterial.setNo(in_no);
      reqInternaMaterial.setService(in_service);
      reqInternaMaterial.setShip_to_Name(in_ship_to_Name);
      reqInternaMaterial.setShip_to_Address(in_ship_to_Address);
      reqInternaMaterial.setShip_to_Address_2(in_ship_to_Address_2);
      reqInternaMaterial.setShip_to_Post_Code(in_ship_to_Post_Code);
      reqInternaMaterial.setShip_to_City(in_ship_to_City);
      reqInternaMaterial.setShip_to_County(in_ship_to_County);
      reqInternaMaterial.setShip_to_Contact(in_ship_to_Contact);
      reqInternaMaterial.setPosting_Date(in_posting_Date);
      reqInternaMaterial.setDocument_Date(in_document_Date);
      reqInternaMaterial.setUser(in_user);
      reqInternaMaterial.setLocation_Code(in_location_Code);
      reqInternaMaterial.setShipment_Method_Code(in_shipment_Method_Code);
      reqInternaMaterial.setShipment_Date(in_shipment_Date);
      reqInternaMaterial.setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No(in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No);
      reqInternaMaterial.setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec(in_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec);
      
      Sales_Internal_Req_subform[] salesLines = new Sales_Internal_Req_subform[in_salesLines_key.length];
      
      for (int i = 0; i < in_salesLines_key.length; i++) {
        if (i < in_salesLines_key.length) {
          salesLines[i].setKey(in_salesLines_key[i]);
        }
        if (i < in_salesLines_type.length) {
          salesLines[i].setType(Type.fromValue(in_salesLines_type[i]));
        }
        if (i < in_salesLines_sub_Type_Item.length) {
          salesLines[i].setSub_Type_Item(Sub_Type_Item.fromValue(in_salesLines_sub_Type_Item[i]));
        }
        if (i < in_salesLines_no.length) {
          salesLines[i].setNo(in_salesLines_no[i]);
        }
        if (i < in_salesLines_description.length) {
          salesLines[i].setDescription(in_salesLines_description[i]);
        }
        if (i < in_salesLines_description_2.length) {
          salesLines[i].setDescription_2(in_salesLines_description_2[i]);
        }
        if (i < in_salesLines_quantity.length) {
          salesLines[i].setQuantity(BigDecimal.valueOf(in_salesLines_quantity[i]));
        }
        if (i < in_salesLines_no_More_Quantities.length) {
          salesLines[i].setNo_More_Quantities(in_salesLines_no_More_Quantities[i]);
        }
        if (i < in_salesLines_location_Code.length) {
          salesLines[i].setLocation_Code(in_salesLines_location_Code[i]);
        }
        if (i < in_salesLines_not_in_Vat_Report.length) {
          salesLines[i].setNot_in_Vat_Report(in_salesLines_not_in_Vat_Report[i]);
        }
        if (i < in_salesLines_unit_of_Measure_Code.length) {
          salesLines[i].setUnit_of_Measure_Code(in_salesLines_unit_of_Measure_Code[i]);
        }
        if (i < in_salesLines_unit_of_Measure.length) {
          salesLines[i].setUnit_of_Measure(in_salesLines_unit_of_Measure[i]);
        }
        if (i < in_salesLines_use_Duplication_List.length) {
          salesLines[i].setUse_Duplication_List(in_salesLines_use_Duplication_List[i]);
        }
        if (i < in_salesLines_appl_from_Item_Entry.length) {
          salesLines[i].setAppl_from_Item_Entry(in_salesLines_appl_from_Item_Entry[i]);
        }
        if (i < in_salesLines_appl_to_Item_Entry.length) {
          salesLines[i].setAppl_to_Item_Entry(in_salesLines_appl_to_Item_Entry[i]);
        }
        if (i < in_salesLines_shortcut_Dimension_1_Code.length) {
          salesLines[i].setShortcut_Dimension_1_Code(in_salesLines_shortcut_Dimension_1_Code[i]);
        }
        if (i < in_salesLines_shortcut_Dimension_2_Code.length) {
          salesLines[i].setShortcut_Dimension_2_Code(in_salesLines_shortcut_Dimension_2_Code[i]);
        }
        if (i < in_salesLines_shortcutDimCode_x005B_3_x005D_.length) {
          salesLines[i].setShortcutDimCode_x005B_3_x005D_(in_salesLines_shortcutDimCode_x005B_3_x005D_[i]);
        }
        if (i < in_salesLines_shortcutDimCode_x005B_4_x005D_.length) {
          salesLines[i].setShortcutDimCode_x005B_4_x005D_(in_salesLines_shortcutDimCode_x005B_4_x005D_[i]);
        }
        if (i < in_salesLines_DRF_Code.length) {
          salesLines[i].setDRF_Code(in_salesLines_DRF_Code[i]);
        }
        if (i < in_salesLines_budget_Dimension_1_Code.length) {
          salesLines[i].setBudget_Dimension_1_Code(in_salesLines_budget_Dimension_1_Code[i]);
        }
        if (i < in_salesLines_budget_Dimension_2_Code.length) {
          salesLines[i].setBudget_Dimension_2_Code(in_salesLines_budget_Dimension_2_Code[i]);
        }
        if (i < in_salesLines_budget_Dimension_3_Code.length) {
          salesLines[i].setBudget_Dimension_3_Code(in_salesLines_budget_Dimension_3_Code[i]);
        }
        if (i < in_salesLines_budget_Dimension_4_Code.length) {
          salesLines[i].setBudget_Dimension_4_Code(in_salesLines_budget_Dimension_4_Code[i]);
        }
        if (i < in_salesLines_treasury_Operation.length) {
          salesLines[i].setTreasury_Operation(in_salesLines_treasury_Operation[i]);
        }
        if (i < in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec.length) {
          salesLines[i].setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec(in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec[i]);
        }
        if (i < in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec.length) {
          salesLines[i].setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec(in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec[i]);
        }
        if (i < in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec.length) {
          salesLines[i].setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec(in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec[i]);
        }
        if (i < in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec.length) {
          salesLines[i].setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec(in_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec[i]);
        }
      }
      
      reqInternaMaterial.setSalesLines(salesLines);

      WS_ReqInternaMaterial result = service.create(reqInternaMaterial);

      if (result != null)
      {
        procData.parseAndSet(getAttribute(OUT_KEY), result.getKey());
        procData.parseAndSet(getAttribute(OUT_NO), result.getNo());
        procData.parseAndSet(getAttribute(OUT_SERVICE), result.getService());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_NAME), result.getShip_to_Name());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_ADDRESS), result.getShip_to_Address());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_ADDRESS_2), result.getShip_to_Address_2());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_POST_CODE), result.getShip_to_Post_Code());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_CITY), result.getShip_to_City());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_COUNTY), result.getShip_to_County());
        procData.parseAndSet(getAttribute(OUT_SHIP_TO_CONTACT), result.getShip_to_Contact());
        procData.set(getAttribute(OUT_POSTING_DATE), result.getPosting_Date());
        procData.set(getAttribute(OUT_DOCUMENT_DATE), result.getDocument_Date());
        procData.parseAndSet(getAttribute(OUT_USER), result.getUser());
        procData.parseAndSet(getAttribute(OUT_LOCATION_CODE), result.getLocation_Code());
        procData.parseAndSet(getAttribute(OUT_SHIPMENT_METHOD_CODE), result.getShipment_Method_Code());
        procData.set(getAttribute(OUT_SHIPMENT_DATE), result.getShipment_Date());
        procData.parseAndSet(getAttribute(OUT_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSHIPTOADDR_SELL_TO_CUSTOMER_NO), result.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfShipToAddr_Sell_to_Customer_No());
        procData.parseAndSet(getAttribute(OUT_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFCONTACTS_REC), result.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfContacts_Rec());
      
        String[] out_salesLines_key = new String[result.getSalesLines().length];
        String[] out_salesLines_type = new String[result.getSalesLines().length];
        String[] out_salesLines_sub_Type_Item = new String[result.getSalesLines().length];
        String[] out_salesLines_no = new String[result.getSalesLines().length];
        String[] out_salesLines_description = new String[result.getSalesLines().length];
        String[] out_salesLines_description_2 = new String[result.getSalesLines().length];
        Long[] out_salesLines_quantity = new Long[result.getSalesLines().length];
        Boolean[] out_salesLines_no_More_Quantities = new Boolean[result.getSalesLines().length];
        String[] out_salesLines_location_Code = new String[result.getSalesLines().length];
        Boolean[] out_salesLines_not_in_Vat_Report = new Boolean[result.getSalesLines().length];
        String[] out_salesLines_unit_of_Measure_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_unit_of_Measure = new String[result.getSalesLines().length];
        Boolean[] out_salesLines_use_Duplication_List = new Boolean[result.getSalesLines().length];
        Integer[] out_salesLines_appl_from_Item_Entry = new Integer[result.getSalesLines().length];
        Integer[] out_salesLines_appl_to_Item_Entry = new Integer[result.getSalesLines().length];
        String[] out_salesLines_shortcut_Dimension_1_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_shortcut_Dimension_2_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_shortcutDimCode_x005B_3_x005D_ = new String[result.getSalesLines().length];
        String[] out_salesLines_shortcutDimCode_x005B_4_x005D_ = new String[result.getSalesLines().length];
        String[] out_salesLines_DRF_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_budget_Dimension_1_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_budget_Dimension_2_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_budget_Dimension_3_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_budget_Dimension_4_Code = new String[result.getSalesLines().length];
        String[] out_salesLines_treasury_Operation = new String[result.getSalesLines().length];
        String[] out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec = new String[result.getSalesLines().length];
        String[] out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec = new String[result.getSalesLines().length];
        String[] out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec = new String[result.getSalesLines().length];
        String[] out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec = new String[result.getSalesLines().length];
        
        for (int i =0; i < result.getSalesLines().length; i++) {
          out_salesLines_key[i] = result.getSalesLines()[i].getKey();
          out_salesLines_type[i] = result.getSalesLines()[i].getType().getValue();
          out_salesLines_sub_Type_Item[i] = result.getSalesLines()[i].getSub_Type_Item().getValue();
          out_salesLines_no[i] = result.getSalesLines()[i].getNo();
          out_salesLines_description[i] = result.getSalesLines()[i].getDescription();
          out_salesLines_description_2[i] = result.getSalesLines()[i].getDescription_2();
          out_salesLines_quantity[i] = result.getSalesLines()[i].getQuantity().longValue();
          out_salesLines_no_More_Quantities[i] = result.getSalesLines()[i].getNo_More_Quantities();
          out_salesLines_location_Code[i] = result.getSalesLines()[i].getLocation_Code();
          out_salesLines_not_in_Vat_Report[i] = result.getSalesLines()[i].getNot_in_Vat_Report();
          out_salesLines_unit_of_Measure_Code[i] = result.getSalesLines()[i].getUnit_of_Measure_Code();
          out_salesLines_unit_of_Measure[i] = result.getSalesLines()[i].getUnit_of_Measure();
          out_salesLines_use_Duplication_List[i] = result.getSalesLines()[i].getUse_Duplication_List();
          out_salesLines_appl_from_Item_Entry[i] = result.getSalesLines()[i].getAppl_from_Item_Entry();
          out_salesLines_appl_to_Item_Entry[i] = result.getSalesLines()[i].getAppl_to_Item_Entry();
          out_salesLines_shortcut_Dimension_1_Code[i] = result.getSalesLines()[i].getShortcut_Dimension_1_Code();
          out_salesLines_shortcut_Dimension_2_Code[i] = result.getSalesLines()[i].getShortcut_Dimension_2_Code();
          out_salesLines_shortcutDimCode_x005B_3_x005D_[i] = result.getSalesLines()[i].getShortcutDimCode_x005B_3_x005D_();
          out_salesLines_shortcutDimCode_x005B_4_x005D_[i] = result.getSalesLines()[i].getShortcutDimCode_x005B_4_x005D_();
          out_salesLines_DRF_Code[i] = result.getSalesLines()[i].getDRF_Code();
          out_salesLines_budget_Dimension_1_Code[i] = result.getSalesLines()[i].getBudget_Dimension_1_Code();
          out_salesLines_budget_Dimension_2_Code[i] = result.getSalesLines()[i].getBudget_Dimension_2_Code();
          out_salesLines_budget_Dimension_3_Code[i] = result.getSalesLines()[i].getBudget_Dimension_3_Code();
          out_salesLines_budget_Dimension_4_Code[i] = result.getSalesLines()[i].getBudget_Dimension_4_Code();
          out_salesLines_treasury_Operation[i] = result.getSalesLines()[i].getTreasury_Operation();
          out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec[i] = result.getSalesLines()[i].getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec();
          out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec[i] = result.getSalesLines()[i].getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec();
          out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec[i] = result.getSalesLines()[i].getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec();
          out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec[i] = result.getSalesLines()[i].getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec();
        }
        
        procData.set(getAttribute(OUT_SALESLINES_KEY), out_salesLines_key);
        procData.set(getAttribute(OUT_SALESLINES_TYPE), out_salesLines_type);
        procData.set(getAttribute(OUT_SALESLINES_SUB_TYPE_ITEM), out_salesLines_sub_Type_Item);
        procData.set(getAttribute(OUT_SALESLINES_NO), out_salesLines_no);
        procData.set(getAttribute(OUT_SALESLINES_DESCRIPTION), out_salesLines_description);
        procData.set(getAttribute(OUT_SALESLINES_DESCRIPTION_2), out_salesLines_description_2);
        procData.set(getAttribute(OUT_SALESLINES_QUANTITY), out_salesLines_quantity);
        procData.set(getAttribute(OUT_SALESLINES_NO_MORE_QUANTITIES), out_salesLines_no_More_Quantities);
        procData.set(getAttribute(OUT_SALESLINES_LOCATION_CODE), out_salesLines_location_Code);
        procData.set(getAttribute(OUT_SALESLINES_NOT_IN_VAT_REPORT), out_salesLines_not_in_Vat_Report);
        procData.set(getAttribute(OUT_SALESLINES_UNIT_OF_MEASURE_CODE), out_salesLines_unit_of_Measure_Code);
        procData.set(getAttribute(OUT_SALESLINES_UNIT_OF_MEASURE), out_salesLines_unit_of_Measure);
        procData.set(getAttribute(OUT_SALESLINES_USE_DUPLICATION_LIST), out_salesLines_use_Duplication_List);
        procData.set(getAttribute(OUT_SALESLINES_APPL_FROM_ITEM_ENTRY), out_salesLines_appl_from_Item_Entry);
        procData.set(getAttribute(OUT_SALESLINES_APPL_TO_ITEM_ENTRY), out_salesLines_appl_to_Item_Entry);
        procData.set(getAttribute(OUT_SALESLINES_SHORTCUT_DIMENSION_1_CODE), out_salesLines_shortcut_Dimension_1_Code);
        procData.set(getAttribute(OUT_SALESLINES_SHORTCUT_DIMENSION_2_CODE), out_salesLines_shortcut_Dimension_2_Code);
        procData.set(getAttribute(OUT_SALESLINES_SHORTCUTDIMCODE_X005B_3_X005D_), out_salesLines_shortcutDimCode_x005B_3_x005D_);
        procData.set(getAttribute(OUT_SALESLINES_SHORTCUTDIMCODE_X005B_4_X005D_), out_salesLines_shortcutDimCode_x005B_4_x005D_);
        procData.set(getAttribute(OUT_SALESLINES_DRF_CODE), out_salesLines_DRF_Code);
        procData.set(getAttribute(OUT_SALESLINES_BUDGET_DIMENSION_1_CODE), out_salesLines_budget_Dimension_1_Code);
        procData.set(getAttribute(OUT_SALESLINES_BUDGET_DIMENSION_2_CODE), out_salesLines_budget_Dimension_2_Code);
        procData.set(getAttribute(OUT_SALESLINES_BUDGET_DIMENSION_3_CODE), out_salesLines_budget_Dimension_3_Code);
        procData.set(getAttribute(OUT_SALESLINES_BUDGET_DIMENSION_4_CODE), out_salesLines_budget_Dimension_4_Code);
        procData.set(getAttribute(OUT_SALESLINES_TREASURY_OPERATION), out_salesLines_treasury_Operation);
        procData.set(getAttribute(OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCAVAILABILITY_REC), out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec);
        procData.set(getAttribute(OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSUBSTITUTIONS_REC), out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec);
        procData.set(getAttribute(OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESPRICES_REC), out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec);
        procData.set(getAttribute(OUT_SALESLINES_STRSUBSTNO__X0027__PERCENT1__X0027__X002C_SALESINFOPANEMGT_CALCNOOFSALESLINEDISC_REC), out_salesLines_STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec);
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