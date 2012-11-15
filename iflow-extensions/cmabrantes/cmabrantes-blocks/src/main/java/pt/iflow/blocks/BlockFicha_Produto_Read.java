package pt.iflow.blocks;

import microsoft_dynamics_schemas.WS_Ficha_Produto;
import microsoft_dynamics_schemas.WS_Ficha_Produto_Port;
import microsoft_dynamics_schemas.WS_Ficha_Produto_ServiceLocator;

import pt.common.properties.WebServiceProperties;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockFicha_Produto_Read extends Block {

  public Port portIn, portOk, portError;

  private static final String IN_NO = "In_No";
  private static final String OUT_NO = "Out_No";
  private static final String OUT_DESCRIPTION = "Out_Description";
  private static final String OUT_BASE_UNIT_OF_MEASURE = "Out_Base_Unit_of_Measure";
  private static final String OUT_BILL_OF_MATERIALS = "Out_Bill_of_Materials";
  private static final String OUT_SHELF_NO = "Out_Shelf_No";
  private static final String OUT_AUTOMATIC_EXT_TEXTS = "Out_Automatic_Ext_Texts";
  private static final String OUT_CREATED_FROM_NONSTOCK_ITEM = "Out_Created_From_Nonstock_Item";
  private static final String OUT_ITEM_CATEGORY_CODE = "Out_Item_Category_Code";
  private static final String OUT_PRODUCT_GROUP_CODE = "Out_Product_Group_Code";
  private static final String OUT_SEARCH_DESCRIPTION = "Out_Search_Description";
  private static final String OUT_INVENTORY = "Out_Inventory";
  private static final String OUT_QTY_ON_PURCH_ORDER = "Out_Qty_on_Purch_Order";
  private static final String OUT_QTY_ON_PROD_ORDER = "Out_Qty_on_Prod_Order";
  private static final String OUT_QTY_ON_COMPONENT_LINES = "Out_Qty_on_Component_Lines";
  private static final String OUT_QTY_ON_SALES_ORDER = "Out_Qty_on_Sales_Order";
  private static final String OUT_QTY_ON_SERVICE_ORDER = "Out_Qty_on_Service_Order";
  private static final String OUT_SERVICE_ITEM_GROUP = "Out_Service_Item_Group";
  private static final String OUT_BLOCKED = "Out_Blocked";
  private static final String OUT_LAST_DATE_MODIFIED = "Out_Last_Date_Modified";
  private static final String OUT_COSTING_METHOD = "Out_Costing_Method";
  private static final String OUT_COST_IS_ADJUSTED = "Out_Cost_is_Adjusted";
  private static final String OUT_COST_IS_POSTED_TO_G_L = "Out_Cost_is_Posted_to_G_L";
  private static final String OUT_STANDARD_COST = "Out_Standard_Cost";
  private static final String OUT_UNIT_COST = "Out_Unit_Cost";
  private static final String OUT_OVERHEAD_RATE = "Out_Overhead_Rate";
  private static final String OUT_INDIRECT_COST_PERCENT = "Out_Indirect_Cost_Percent";
  private static final String OUT_LAST_DIRECT_COST = "Out_Last_Direct_Cost";
  private static final String OUT_PRICE_PROFIT_CALCULATION = "Out_Price_Profit_Calculation";
  private static final String OUT_PROFIT_PERCENT = "Out_Profit_Percent";
  private static final String OUT_UNIT_PRICE = "Out_Unit_Price";
  private static final String OUT_GEN_PROD_POSTING_GROUP = "Out_Gen_Prod_Posting_Group";
  private static final String OUT_VAT_PROD_POSTING_GROUP = "Out_VAT_Prod_Posting_Group";
  private static final String OUT_INVENTORY_POSTING_GROUP = "Out_Inventory_Posting_Group";
  private static final String OUT_NET_INVOICED_QTY = "Out_Net_Invoiced_Qty";
  private static final String OUT_ALLOW_INVOICE_DISC = "Out_Allow_Invoice_Disc";
  private static final String OUT_ITEM_DISC_GROUP = "Out_Item_Disc_Group";
  private static final String OUT_SALES_UNIT_OF_MEASURE = "Out_Sales_Unit_of_Measure";
  private static final String OUT_REPLENISHMENT_SYSTEM = "Out_Replenishment_System";
  private static final String OUT_VENDOR_NO = "Out_Vendor_No";
  private static final String OUT_VENDOR_ITEM_NO = "Out_Vendor_Item_No";
  private static final String OUT_PURCH_UNIT_OF_MEASURE = "Out_Purch_Unit_of_Measure";
  private static final String OUT_LEAD_TIME_CALCULATION = "Out_Lead_Time_Calculation";
  private static final String OUT_MANUFACTURING_POLICY = "Out_Manufacturing_Policy";
  private static final String OUT_ROUTING_NO = "Out_Routing_No";
  private static final String OUT_PRODUCTION_BOM_NO = "Out_Production_BOM_No";
  private static final String OUT_ROUNDING_PRECISION = "Out_Rounding_Precision";
  private static final String OUT_FLUSHING_METHOD = "Out_Flushing_Method";
  private static final String OUT_SCRAP_PERCENT = "Out_Scrap_Percent";
  private static final String OUT_LOT_SIZE = "Out_Lot_Size";
  private static final String OUT_REORDERING_POLICY = "Out_Reordering_Policy";
  private static final String OUT_INCLUDE_INVENTORY = "Out_Include_Inventory";
  private static final String OUT_RESERVE = "Out_Reserve";
  private static final String OUT_ORDER_TRACKING_POLICY = "Out_Order_Tracking_Policy";
  private static final String OUT_STOCKKEEPING_UNIT_EXISTS = "Out_Stockkeeping_Unit_Exists";
  private static final String OUT_CRITICAL = "Out_Critical";
  private static final String OUT_REORDER_CYCLE = "Out_Reorder_Cycle";
  private static final String OUT_SAFETY_LEAD_TIME = "Out_Safety_Lead_Time";
  private static final String OUT_SAFETY_STOCK_QUANTITY = "Out_Safety_Stock_Quantity";
  private static final String OUT_REORDER_POINT = "Out_Reorder_Point";
  private static final String OUT_REORDER_QUANTITY = "Out_Reorder_Quantity";
  private static final String OUT_MAXIMUM_INVENTORY = "Out_Maximum_Inventory";
  private static final String OUT_MINIMUM_ORDER_QUANTITY = "Out_Minimum_Order_Quantity";
  private static final String OUT_MAXIMUM_ORDER_QUANTITY = "Out_Maximum_Order_Quantity";
  private static final String OUT_ORDER_MULTIPLE = "Out_Order_Multiple";
  private static final String OUT_TARIFF_NO = "Out_Tariff_No";
  private static final String OUT_COUNTRY_REGION_OF_ORIGIN_CODE = "Out_Country_Region_of_Origin_Code";
  private static final String OUT_NET_WEIGHT = "Out_Net_Weight";
  private static final String OUT_GROSS_WEIGHT = "Out_Gross_Weight";
  private static final String OUT_ITEM_TRACKING_CODE = "Out_Item_Tracking_Code";
  private static final String OUT_SERIAL_NOS = "Out_Serial_Nos";
  private static final String OUT_LOT_NOS = "Out_Lot_Nos";
  private static final String OUT_EXPIRATION_CALCULATION = "Out_Expiration_Calculation";
  private static final String OUT_COMMON_ITEM_NO = "Out_Common_Item_No";
  private static final String OUT_SPECIAL_EQUIPMENT_CODE = "Out_Special_Equipment_Code";
  private static final String OUT_PUT_AWAY_TEMPLATE_CODE = "Out_Put_away_Template_Code";
  private static final String OUT_PUT_AWAY_UNIT_OF_MEASURE_CODE = "Out_Put_away_Unit_of_Measure_Code";
  private static final String OUT_PHYS_INVT_COUNTING_PERIOD_CODE = "Out_Phys_Invt_Counting_Period_Code";
  private static final String OUT_LAST_PHYS_INVT_DATE = "Out_Last_Phys_Invt_Date";
  private static final String OUT_LAST_COUNTING_PERIOD_UPDATE = "Out_Last_Counting_Period_Update";
  private static final String OUT_NEXT_COUNTING_PERIOD = "Out_Next_Counting_Period";
  private static final String OUT_IDENTIFIER_CODE = "Out_Identifier_Code";
  private static final String OUT_USE_CROSS_DOCKING = "Out_Use_Cross_Docking";

  public BlockFicha_Produto_Read(int anFlowId, int id, int subflowblockid, String filename) {
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
      WS_Ficha_Produto_ServiceLocator locator = new WS_Ficha_Produto_ServiceLocator();
      WS_Ficha_Produto_Port service = locator.getWS_Ficha_Produto_Port(WebServiceProperties.getCidadelaProxyURL());

      String in_No = procData.transform(userInfo, this.getAttribute(IN_NO));

      WS_Ficha_Produto result = service.read(in_No);

      if (result != null)
      {
        procData.parseAndSet(getAttribute(OUT_NO),result.getNo());
        procData.parseAndSet(getAttribute(OUT_DESCRIPTION),result.getDescription());
        procData.parseAndSet(getAttribute(OUT_BASE_UNIT_OF_MEASURE),result.getBase_Unit_of_Measure());
        procData.set(getAttribute(OUT_BILL_OF_MATERIALS), result.getBill_of_Materials());
        procData.parseAndSet(getAttribute(OUT_SHELF_NO),result.getShelf_No());
        procData.set(getAttribute(OUT_AUTOMATIC_EXT_TEXTS), result.getAutomatic_Ext_Texts());
        procData.set(getAttribute(OUT_CREATED_FROM_NONSTOCK_ITEM), result.getCreated_From_Nonstock_Item());
        procData.parseAndSet(getAttribute(OUT_ITEM_CATEGORY_CODE),result.getItem_Category_Code());
        procData.parseAndSet(getAttribute(OUT_PRODUCT_GROUP_CODE),result.getProduct_Group_Code());
        procData.parseAndSet(getAttribute(OUT_SEARCH_DESCRIPTION),result.getSearch_Description());
        procData.set(getAttribute(OUT_INVENTORY), result.getInventory().doubleValue());
        procData.set(getAttribute(OUT_QTY_ON_PURCH_ORDER), result.getQty_on_Purch_Order());
        procData.set(getAttribute(OUT_QTY_ON_PROD_ORDER), result.getQty_on_Prod_Order());
        procData.set(getAttribute(OUT_QTY_ON_COMPONENT_LINES), result.getQty_on_Component_Lines());
        procData.set(getAttribute(OUT_QTY_ON_SALES_ORDER), result.getQty_on_Sales_Order());
        procData.set(getAttribute(OUT_QTY_ON_SERVICE_ORDER), result.getQty_on_Service_Order());
        procData.parseAndSet(getAttribute(OUT_SERVICE_ITEM_GROUP),result.getService_Item_Group());
        procData.set(getAttribute(OUT_BLOCKED), result.getBlocked());
        procData.set(getAttribute(OUT_LAST_DATE_MODIFIED), result.getLast_Date_Modified());
        procData.parseAndSet(getAttribute(OUT_COSTING_METHOD), result.getCosting_Method().getValue());
        procData.set(getAttribute(OUT_COST_IS_ADJUSTED), result.getCost_is_Adjusted());
        procData.set(getAttribute(OUT_COST_IS_POSTED_TO_G_L), result.getCost_is_Posted_to_G_L());
        procData.set(getAttribute(OUT_STANDARD_COST), result.getStandard_Cost());
        procData.set(getAttribute(OUT_UNIT_COST), result.getUnit_Cost());
        procData.set(getAttribute(OUT_OVERHEAD_RATE), result.getOverhead_Rate());
        procData.set(getAttribute(OUT_INDIRECT_COST_PERCENT), result.getIndirect_Cost_Percent());
        procData.set(getAttribute(OUT_LAST_DIRECT_COST), result.getLast_Direct_Cost());
        procData.parseAndSet(getAttribute(OUT_PRICE_PROFIT_CALCULATION), result.getPrice_Profit_Calculation().getValue());
        procData.set(getAttribute(OUT_PROFIT_PERCENT), result.getProfit_Percent());
        procData.set(getAttribute(OUT_UNIT_PRICE), result.getUnit_Price());
        procData.parseAndSet(getAttribute(OUT_GEN_PROD_POSTING_GROUP),result.getGen_Prod_Posting_Group());
        procData.parseAndSet(getAttribute(OUT_VAT_PROD_POSTING_GROUP),result.getVAT_Prod_Posting_Group());
        procData.parseAndSet(getAttribute(OUT_INVENTORY_POSTING_GROUP),result.getInventory_Posting_Group());
        procData.set(getAttribute(OUT_NET_INVOICED_QTY), result.getNet_Invoiced_Qty());
        procData.set(getAttribute(OUT_ALLOW_INVOICE_DISC), result.getAllow_Invoice_Disc());
        procData.parseAndSet(getAttribute(OUT_ITEM_DISC_GROUP),result.getItem_Disc_Group());
        procData.parseAndSet(getAttribute(OUT_SALES_UNIT_OF_MEASURE),result.getSales_Unit_of_Measure());
        procData.parseAndSet(getAttribute(OUT_REPLENISHMENT_SYSTEM), result.getReplenishment_System().getValue());
        procData.parseAndSet(getAttribute(OUT_VENDOR_NO),result.getVendor_No());
        procData.parseAndSet(getAttribute(OUT_VENDOR_ITEM_NO),result.getVendor_Item_No());
        procData.parseAndSet(getAttribute(OUT_PURCH_UNIT_OF_MEASURE),result.getPurch_Unit_of_Measure());
        procData.parseAndSet(getAttribute(OUT_LEAD_TIME_CALCULATION),result.getLead_Time_Calculation());
        procData.parseAndSet(getAttribute(OUT_MANUFACTURING_POLICY), result.getManufacturing_Policy().getValue());
        procData.parseAndSet(getAttribute(OUT_ROUTING_NO),result.getRouting_No());
        procData.parseAndSet(getAttribute(OUT_PRODUCTION_BOM_NO),result.getProduction_BOM_No());
        procData.set(getAttribute(OUT_ROUNDING_PRECISION), result.getRounding_Precision());
        procData.parseAndSet(getAttribute(OUT_FLUSHING_METHOD), result.getFlushing_Method().getValue());
        procData.set(getAttribute(OUT_SCRAP_PERCENT), result.getScrap_Percent());
        procData.set(getAttribute(OUT_LOT_SIZE), result.getLot_Size());
        procData.parseAndSet(getAttribute(OUT_REORDERING_POLICY), result.getReordering_Policy().getValue());
        procData.set(getAttribute(OUT_INCLUDE_INVENTORY), result.getInclude_Inventory());
        procData.parseAndSet(getAttribute(OUT_RESERVE), result.getReserve().getValue());
        procData.parseAndSet(getAttribute(OUT_ORDER_TRACKING_POLICY), result.getOrder_Tracking_Policy().getValue());
        procData.set(getAttribute(OUT_STOCKKEEPING_UNIT_EXISTS), result.getStockkeeping_Unit_Exists());
        procData.set(getAttribute(OUT_CRITICAL), result.getCritical());
        procData.parseAndSet(getAttribute(OUT_REORDER_CYCLE),result.getReorder_Cycle());
        procData.parseAndSet(getAttribute(OUT_SAFETY_LEAD_TIME),result.getSafety_Lead_Time());
        procData.set(getAttribute(OUT_SAFETY_STOCK_QUANTITY), result.getSafety_Stock_Quantity());
        procData.set(getAttribute(OUT_REORDER_POINT), result.getReorder_Point());
        procData.set(getAttribute(OUT_REORDER_QUANTITY), result.getReorder_Quantity());
        procData.set(getAttribute(OUT_MAXIMUM_INVENTORY), result.getMaximum_Inventory());
        procData.set(getAttribute(OUT_MINIMUM_ORDER_QUANTITY), result.getMinimum_Order_Quantity());
        procData.set(getAttribute(OUT_MAXIMUM_ORDER_QUANTITY), result.getMaximum_Order_Quantity());
        procData.set(getAttribute(OUT_ORDER_MULTIPLE), result.getOrder_Multiple());
        procData.parseAndSet(getAttribute(OUT_TARIFF_NO),result.getTariff_No());
        procData.parseAndSet(getAttribute(OUT_COUNTRY_REGION_OF_ORIGIN_CODE),result.getCountry_Region_of_Origin_Code());
        procData.set(getAttribute(OUT_NET_WEIGHT), result.getNet_Weight());
        procData.set(getAttribute(OUT_GROSS_WEIGHT), result.getGross_Weight());
        procData.parseAndSet(getAttribute(OUT_ITEM_TRACKING_CODE),result.getItem_Tracking_Code());
        procData.parseAndSet(getAttribute(OUT_SERIAL_NOS),result.getSerial_Nos());
        procData.parseAndSet(getAttribute(OUT_LOT_NOS),result.getLot_Nos());
        procData.parseAndSet(getAttribute(OUT_EXPIRATION_CALCULATION),result.getExpiration_Calculation());
        procData.parseAndSet(getAttribute(OUT_COMMON_ITEM_NO),result.getCommon_Item_No());
        procData.parseAndSet(getAttribute(OUT_SPECIAL_EQUIPMENT_CODE),result.getSpecial_Equipment_Code());
        procData.parseAndSet(getAttribute(OUT_PUT_AWAY_TEMPLATE_CODE),result.getPut_away_Template_Code());
        procData.parseAndSet(getAttribute(OUT_PUT_AWAY_UNIT_OF_MEASURE_CODE),result.getPut_away_Unit_of_Measure_Code());
        procData.parseAndSet(getAttribute(OUT_PHYS_INVT_COUNTING_PERIOD_CODE),result.getPhys_Invt_Counting_Period_Code());
        procData.set(getAttribute(OUT_LAST_PHYS_INVT_DATE), result.getLast_Phys_Invt_Date());
        procData.set(getAttribute(OUT_LAST_COUNTING_PERIOD_UPDATE), result.getLast_Counting_Period_Update());
        procData.parseAndSet(getAttribute(OUT_NEXT_COUNTING_PERIOD),result.getNext_Counting_Period());
        procData.parseAndSet(getAttribute(OUT_IDENTIFIER_CODE),result.getIdentifier_Code());
        procData.set(getAttribute(OUT_USE_CROSS_DOCKING), result.getUse_Cross_Docking());
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