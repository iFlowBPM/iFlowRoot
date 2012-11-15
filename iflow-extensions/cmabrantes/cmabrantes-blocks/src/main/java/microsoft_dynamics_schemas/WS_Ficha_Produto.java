/**
 * WS_Ficha_Produto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Ficha_Produto  implements java.io.Serializable {
    private java.lang.String key;

    private java.lang.String no;

    private java.lang.String description;

    private java.lang.String base_Unit_of_Measure;

    private java.lang.Boolean bill_of_Materials;

    private java.lang.String shelf_No;

    private java.lang.Boolean automatic_Ext_Texts;

    private java.lang.Boolean created_From_Nonstock_Item;

    private java.lang.String item_Category_Code;

    private java.lang.String product_Group_Code;

    private java.lang.String search_Description;

    private java.math.BigDecimal inventory;

    private java.math.BigDecimal qty_on_Purch_Order;

    private java.math.BigDecimal qty_on_Prod_Order;

    private java.math.BigDecimal qty_on_Component_Lines;

    private java.math.BigDecimal qty_on_Sales_Order;

    private java.math.BigDecimal qty_on_Service_Order;

    private java.lang.String service_Item_Group;

    private java.lang.Boolean blocked;

    private java.util.Date last_Date_Modified;

    private microsoft_dynamics_schemas.Costing_Method costing_Method;

    private java.lang.Boolean cost_is_Adjusted;

    private java.lang.Boolean cost_is_Posted_to_G_L;

    private java.math.BigDecimal standard_Cost;

    private java.math.BigDecimal unit_Cost;

    private java.math.BigDecimal overhead_Rate;

    private java.math.BigDecimal indirect_Cost_Percent;

    private java.math.BigDecimal last_Direct_Cost;

    private microsoft_dynamics_schemas.Price_Profit_Calculation price_Profit_Calculation;

    private java.math.BigDecimal profit_Percent;

    private java.math.BigDecimal unit_Price;

    private java.lang.String gen_Prod_Posting_Group;

    private java.lang.String VAT_Prod_Posting_Group;

    private java.lang.String inventory_Posting_Group;

    private java.math.BigDecimal net_Invoiced_Qty;

    private java.lang.Boolean allow_Invoice_Disc;

    private java.lang.String item_Disc_Group;

    private java.lang.String sales_Unit_of_Measure;

    private microsoft_dynamics_schemas.Replenishment_System replenishment_System;

    private java.lang.String vendor_No;

    private java.lang.String vendor_Item_No;

    private java.lang.String purch_Unit_of_Measure;

    private java.lang.String lead_Time_Calculation;

    private microsoft_dynamics_schemas.Manufacturing_Policy manufacturing_Policy;

    private java.lang.String routing_No;

    private java.lang.String production_BOM_No;

    private java.math.BigDecimal rounding_Precision;

    private microsoft_dynamics_schemas.Flushing_Method flushing_Method;

    private java.math.BigDecimal scrap_Percent;

    private java.math.BigDecimal lot_Size;

    private microsoft_dynamics_schemas.Reordering_Policy reordering_Policy;

    private java.lang.Boolean include_Inventory;

    private microsoft_dynamics_schemas.Reserve reserve;

    private microsoft_dynamics_schemas.Order_Tracking_Policy order_Tracking_Policy;

    private java.lang.Boolean stockkeeping_Unit_Exists;

    private java.lang.Boolean critical;

    private java.lang.String reorder_Cycle;

    private java.lang.String safety_Lead_Time;

    private java.math.BigDecimal safety_Stock_Quantity;

    private java.math.BigDecimal reorder_Point;

    private java.math.BigDecimal reorder_Quantity;

    private java.math.BigDecimal maximum_Inventory;

    private java.math.BigDecimal minimum_Order_Quantity;

    private java.math.BigDecimal maximum_Order_Quantity;

    private java.math.BigDecimal order_Multiple;

    private java.lang.String tariff_No;

    private java.lang.String country_Region_of_Origin_Code;

    private java.math.BigDecimal net_Weight;

    private java.math.BigDecimal gross_Weight;

    private java.lang.String item_Tracking_Code;

    private java.lang.String serial_Nos;

    private java.lang.String lot_Nos;

    private java.lang.String expiration_Calculation;

    private java.lang.String common_Item_No;

    private java.lang.String special_Equipment_Code;

    private java.lang.String put_away_Template_Code;

    private java.lang.String put_away_Unit_of_Measure_Code;

    private java.lang.String phys_Invt_Counting_Period_Code;

    private java.util.Date last_Phys_Invt_Date;

    private java.util.Date last_Counting_Period_Update;

    private java.lang.String next_Counting_Period;

    private java.lang.String identifier_Code;

    private java.lang.Boolean use_Cross_Docking;

    public WS_Ficha_Produto() {
    }

    public WS_Ficha_Produto(
           java.lang.String key,
           java.lang.String no,
           java.lang.String description,
           java.lang.String base_Unit_of_Measure,
           java.lang.Boolean bill_of_Materials,
           java.lang.String shelf_No,
           java.lang.Boolean automatic_Ext_Texts,
           java.lang.Boolean created_From_Nonstock_Item,
           java.lang.String item_Category_Code,
           java.lang.String product_Group_Code,
           java.lang.String search_Description,
           java.math.BigDecimal inventory,
           java.math.BigDecimal qty_on_Purch_Order,
           java.math.BigDecimal qty_on_Prod_Order,
           java.math.BigDecimal qty_on_Component_Lines,
           java.math.BigDecimal qty_on_Sales_Order,
           java.math.BigDecimal qty_on_Service_Order,
           java.lang.String service_Item_Group,
           java.lang.Boolean blocked,
           java.util.Date last_Date_Modified,
           microsoft_dynamics_schemas.Costing_Method costing_Method,
           java.lang.Boolean cost_is_Adjusted,
           java.lang.Boolean cost_is_Posted_to_G_L,
           java.math.BigDecimal standard_Cost,
           java.math.BigDecimal unit_Cost,
           java.math.BigDecimal overhead_Rate,
           java.math.BigDecimal indirect_Cost_Percent,
           java.math.BigDecimal last_Direct_Cost,
           microsoft_dynamics_schemas.Price_Profit_Calculation price_Profit_Calculation,
           java.math.BigDecimal profit_Percent,
           java.math.BigDecimal unit_Price,
           java.lang.String gen_Prod_Posting_Group,
           java.lang.String VAT_Prod_Posting_Group,
           java.lang.String inventory_Posting_Group,
           java.math.BigDecimal net_Invoiced_Qty,
           java.lang.Boolean allow_Invoice_Disc,
           java.lang.String item_Disc_Group,
           java.lang.String sales_Unit_of_Measure,
           microsoft_dynamics_schemas.Replenishment_System replenishment_System,
           java.lang.String vendor_No,
           java.lang.String vendor_Item_No,
           java.lang.String purch_Unit_of_Measure,
           java.lang.String lead_Time_Calculation,
           microsoft_dynamics_schemas.Manufacturing_Policy manufacturing_Policy,
           java.lang.String routing_No,
           java.lang.String production_BOM_No,
           java.math.BigDecimal rounding_Precision,
           microsoft_dynamics_schemas.Flushing_Method flushing_Method,
           java.math.BigDecimal scrap_Percent,
           java.math.BigDecimal lot_Size,
           microsoft_dynamics_schemas.Reordering_Policy reordering_Policy,
           java.lang.Boolean include_Inventory,
           microsoft_dynamics_schemas.Reserve reserve,
           microsoft_dynamics_schemas.Order_Tracking_Policy order_Tracking_Policy,
           java.lang.Boolean stockkeeping_Unit_Exists,
           java.lang.Boolean critical,
           java.lang.String reorder_Cycle,
           java.lang.String safety_Lead_Time,
           java.math.BigDecimal safety_Stock_Quantity,
           java.math.BigDecimal reorder_Point,
           java.math.BigDecimal reorder_Quantity,
           java.math.BigDecimal maximum_Inventory,
           java.math.BigDecimal minimum_Order_Quantity,
           java.math.BigDecimal maximum_Order_Quantity,
           java.math.BigDecimal order_Multiple,
           java.lang.String tariff_No,
           java.lang.String country_Region_of_Origin_Code,
           java.math.BigDecimal net_Weight,
           java.math.BigDecimal gross_Weight,
           java.lang.String item_Tracking_Code,
           java.lang.String serial_Nos,
           java.lang.String lot_Nos,
           java.lang.String expiration_Calculation,
           java.lang.String common_Item_No,
           java.lang.String special_Equipment_Code,
           java.lang.String put_away_Template_Code,
           java.lang.String put_away_Unit_of_Measure_Code,
           java.lang.String phys_Invt_Counting_Period_Code,
           java.util.Date last_Phys_Invt_Date,
           java.util.Date last_Counting_Period_Update,
           java.lang.String next_Counting_Period,
           java.lang.String identifier_Code,
           java.lang.Boolean use_Cross_Docking) {
           this.key = key;
           this.no = no;
           this.description = description;
           this.base_Unit_of_Measure = base_Unit_of_Measure;
           this.bill_of_Materials = bill_of_Materials;
           this.shelf_No = shelf_No;
           this.automatic_Ext_Texts = automatic_Ext_Texts;
           this.created_From_Nonstock_Item = created_From_Nonstock_Item;
           this.item_Category_Code = item_Category_Code;
           this.product_Group_Code = product_Group_Code;
           this.search_Description = search_Description;
           this.inventory = inventory;
           this.qty_on_Purch_Order = qty_on_Purch_Order;
           this.qty_on_Prod_Order = qty_on_Prod_Order;
           this.qty_on_Component_Lines = qty_on_Component_Lines;
           this.qty_on_Sales_Order = qty_on_Sales_Order;
           this.qty_on_Service_Order = qty_on_Service_Order;
           this.service_Item_Group = service_Item_Group;
           this.blocked = blocked;
           this.last_Date_Modified = last_Date_Modified;
           this.costing_Method = costing_Method;
           this.cost_is_Adjusted = cost_is_Adjusted;
           this.cost_is_Posted_to_G_L = cost_is_Posted_to_G_L;
           this.standard_Cost = standard_Cost;
           this.unit_Cost = unit_Cost;
           this.overhead_Rate = overhead_Rate;
           this.indirect_Cost_Percent = indirect_Cost_Percent;
           this.last_Direct_Cost = last_Direct_Cost;
           this.price_Profit_Calculation = price_Profit_Calculation;
           this.profit_Percent = profit_Percent;
           this.unit_Price = unit_Price;
           this.gen_Prod_Posting_Group = gen_Prod_Posting_Group;
           this.VAT_Prod_Posting_Group = VAT_Prod_Posting_Group;
           this.inventory_Posting_Group = inventory_Posting_Group;
           this.net_Invoiced_Qty = net_Invoiced_Qty;
           this.allow_Invoice_Disc = allow_Invoice_Disc;
           this.item_Disc_Group = item_Disc_Group;
           this.sales_Unit_of_Measure = sales_Unit_of_Measure;
           this.replenishment_System = replenishment_System;
           this.vendor_No = vendor_No;
           this.vendor_Item_No = vendor_Item_No;
           this.purch_Unit_of_Measure = purch_Unit_of_Measure;
           this.lead_Time_Calculation = lead_Time_Calculation;
           this.manufacturing_Policy = manufacturing_Policy;
           this.routing_No = routing_No;
           this.production_BOM_No = production_BOM_No;
           this.rounding_Precision = rounding_Precision;
           this.flushing_Method = flushing_Method;
           this.scrap_Percent = scrap_Percent;
           this.lot_Size = lot_Size;
           this.reordering_Policy = reordering_Policy;
           this.include_Inventory = include_Inventory;
           this.reserve = reserve;
           this.order_Tracking_Policy = order_Tracking_Policy;
           this.stockkeeping_Unit_Exists = stockkeeping_Unit_Exists;
           this.critical = critical;
           this.reorder_Cycle = reorder_Cycle;
           this.safety_Lead_Time = safety_Lead_Time;
           this.safety_Stock_Quantity = safety_Stock_Quantity;
           this.reorder_Point = reorder_Point;
           this.reorder_Quantity = reorder_Quantity;
           this.maximum_Inventory = maximum_Inventory;
           this.minimum_Order_Quantity = minimum_Order_Quantity;
           this.maximum_Order_Quantity = maximum_Order_Quantity;
           this.order_Multiple = order_Multiple;
           this.tariff_No = tariff_No;
           this.country_Region_of_Origin_Code = country_Region_of_Origin_Code;
           this.net_Weight = net_Weight;
           this.gross_Weight = gross_Weight;
           this.item_Tracking_Code = item_Tracking_Code;
           this.serial_Nos = serial_Nos;
           this.lot_Nos = lot_Nos;
           this.expiration_Calculation = expiration_Calculation;
           this.common_Item_No = common_Item_No;
           this.special_Equipment_Code = special_Equipment_Code;
           this.put_away_Template_Code = put_away_Template_Code;
           this.put_away_Unit_of_Measure_Code = put_away_Unit_of_Measure_Code;
           this.phys_Invt_Counting_Period_Code = phys_Invt_Counting_Period_Code;
           this.last_Phys_Invt_Date = last_Phys_Invt_Date;
           this.last_Counting_Period_Update = last_Counting_Period_Update;
           this.next_Counting_Period = next_Counting_Period;
           this.identifier_Code = identifier_Code;
           this.use_Cross_Docking = use_Cross_Docking;
    }


    /**
     * Gets the key value for this WS_Ficha_Produto.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this WS_Ficha_Produto.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this WS_Ficha_Produto.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this WS_Ficha_Produto.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the description value for this WS_Ficha_Produto.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this WS_Ficha_Produto.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the base_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @return base_Unit_of_Measure
     */
    public java.lang.String getBase_Unit_of_Measure() {
        return base_Unit_of_Measure;
    }


    /**
     * Sets the base_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @param base_Unit_of_Measure
     */
    public void setBase_Unit_of_Measure(java.lang.String base_Unit_of_Measure) {
        this.base_Unit_of_Measure = base_Unit_of_Measure;
    }


    /**
     * Gets the bill_of_Materials value for this WS_Ficha_Produto.
     * 
     * @return bill_of_Materials
     */
    public java.lang.Boolean getBill_of_Materials() {
        return bill_of_Materials;
    }


    /**
     * Sets the bill_of_Materials value for this WS_Ficha_Produto.
     * 
     * @param bill_of_Materials
     */
    public void setBill_of_Materials(java.lang.Boolean bill_of_Materials) {
        this.bill_of_Materials = bill_of_Materials;
    }


    /**
     * Gets the shelf_No value for this WS_Ficha_Produto.
     * 
     * @return shelf_No
     */
    public java.lang.String getShelf_No() {
        return shelf_No;
    }


    /**
     * Sets the shelf_No value for this WS_Ficha_Produto.
     * 
     * @param shelf_No
     */
    public void setShelf_No(java.lang.String shelf_No) {
        this.shelf_No = shelf_No;
    }


    /**
     * Gets the automatic_Ext_Texts value for this WS_Ficha_Produto.
     * 
     * @return automatic_Ext_Texts
     */
    public java.lang.Boolean getAutomatic_Ext_Texts() {
        return automatic_Ext_Texts;
    }


    /**
     * Sets the automatic_Ext_Texts value for this WS_Ficha_Produto.
     * 
     * @param automatic_Ext_Texts
     */
    public void setAutomatic_Ext_Texts(java.lang.Boolean automatic_Ext_Texts) {
        this.automatic_Ext_Texts = automatic_Ext_Texts;
    }


    /**
     * Gets the created_From_Nonstock_Item value for this WS_Ficha_Produto.
     * 
     * @return created_From_Nonstock_Item
     */
    public java.lang.Boolean getCreated_From_Nonstock_Item() {
        return created_From_Nonstock_Item;
    }


    /**
     * Sets the created_From_Nonstock_Item value for this WS_Ficha_Produto.
     * 
     * @param created_From_Nonstock_Item
     */
    public void setCreated_From_Nonstock_Item(java.lang.Boolean created_From_Nonstock_Item) {
        this.created_From_Nonstock_Item = created_From_Nonstock_Item;
    }


    /**
     * Gets the item_Category_Code value for this WS_Ficha_Produto.
     * 
     * @return item_Category_Code
     */
    public java.lang.String getItem_Category_Code() {
        return item_Category_Code;
    }


    /**
     * Sets the item_Category_Code value for this WS_Ficha_Produto.
     * 
     * @param item_Category_Code
     */
    public void setItem_Category_Code(java.lang.String item_Category_Code) {
        this.item_Category_Code = item_Category_Code;
    }


    /**
     * Gets the product_Group_Code value for this WS_Ficha_Produto.
     * 
     * @return product_Group_Code
     */
    public java.lang.String getProduct_Group_Code() {
        return product_Group_Code;
    }


    /**
     * Sets the product_Group_Code value for this WS_Ficha_Produto.
     * 
     * @param product_Group_Code
     */
    public void setProduct_Group_Code(java.lang.String product_Group_Code) {
        this.product_Group_Code = product_Group_Code;
    }


    /**
     * Gets the search_Description value for this WS_Ficha_Produto.
     * 
     * @return search_Description
     */
    public java.lang.String getSearch_Description() {
        return search_Description;
    }


    /**
     * Sets the search_Description value for this WS_Ficha_Produto.
     * 
     * @param search_Description
     */
    public void setSearch_Description(java.lang.String search_Description) {
        this.search_Description = search_Description;
    }


    /**
     * Gets the inventory value for this WS_Ficha_Produto.
     * 
     * @return inventory
     */
    public java.math.BigDecimal getInventory() {
        return inventory;
    }


    /**
     * Sets the inventory value for this WS_Ficha_Produto.
     * 
     * @param inventory
     */
    public void setInventory(java.math.BigDecimal inventory) {
        this.inventory = inventory;
    }


    /**
     * Gets the qty_on_Purch_Order value for this WS_Ficha_Produto.
     * 
     * @return qty_on_Purch_Order
     */
    public java.math.BigDecimal getQty_on_Purch_Order() {
        return qty_on_Purch_Order;
    }


    /**
     * Sets the qty_on_Purch_Order value for this WS_Ficha_Produto.
     * 
     * @param qty_on_Purch_Order
     */
    public void setQty_on_Purch_Order(java.math.BigDecimal qty_on_Purch_Order) {
        this.qty_on_Purch_Order = qty_on_Purch_Order;
    }


    /**
     * Gets the qty_on_Prod_Order value for this WS_Ficha_Produto.
     * 
     * @return qty_on_Prod_Order
     */
    public java.math.BigDecimal getQty_on_Prod_Order() {
        return qty_on_Prod_Order;
    }


    /**
     * Sets the qty_on_Prod_Order value for this WS_Ficha_Produto.
     * 
     * @param qty_on_Prod_Order
     */
    public void setQty_on_Prod_Order(java.math.BigDecimal qty_on_Prod_Order) {
        this.qty_on_Prod_Order = qty_on_Prod_Order;
    }


    /**
     * Gets the qty_on_Component_Lines value for this WS_Ficha_Produto.
     * 
     * @return qty_on_Component_Lines
     */
    public java.math.BigDecimal getQty_on_Component_Lines() {
        return qty_on_Component_Lines;
    }


    /**
     * Sets the qty_on_Component_Lines value for this WS_Ficha_Produto.
     * 
     * @param qty_on_Component_Lines
     */
    public void setQty_on_Component_Lines(java.math.BigDecimal qty_on_Component_Lines) {
        this.qty_on_Component_Lines = qty_on_Component_Lines;
    }


    /**
     * Gets the qty_on_Sales_Order value for this WS_Ficha_Produto.
     * 
     * @return qty_on_Sales_Order
     */
    public java.math.BigDecimal getQty_on_Sales_Order() {
        return qty_on_Sales_Order;
    }


    /**
     * Sets the qty_on_Sales_Order value for this WS_Ficha_Produto.
     * 
     * @param qty_on_Sales_Order
     */
    public void setQty_on_Sales_Order(java.math.BigDecimal qty_on_Sales_Order) {
        this.qty_on_Sales_Order = qty_on_Sales_Order;
    }


    /**
     * Gets the qty_on_Service_Order value for this WS_Ficha_Produto.
     * 
     * @return qty_on_Service_Order
     */
    public java.math.BigDecimal getQty_on_Service_Order() {
        return qty_on_Service_Order;
    }


    /**
     * Sets the qty_on_Service_Order value for this WS_Ficha_Produto.
     * 
     * @param qty_on_Service_Order
     */
    public void setQty_on_Service_Order(java.math.BigDecimal qty_on_Service_Order) {
        this.qty_on_Service_Order = qty_on_Service_Order;
    }


    /**
     * Gets the service_Item_Group value for this WS_Ficha_Produto.
     * 
     * @return service_Item_Group
     */
    public java.lang.String getService_Item_Group() {
        return service_Item_Group;
    }


    /**
     * Sets the service_Item_Group value for this WS_Ficha_Produto.
     * 
     * @param service_Item_Group
     */
    public void setService_Item_Group(java.lang.String service_Item_Group) {
        this.service_Item_Group = service_Item_Group;
    }


    /**
     * Gets the blocked value for this WS_Ficha_Produto.
     * 
     * @return blocked
     */
    public java.lang.Boolean getBlocked() {
        return blocked;
    }


    /**
     * Sets the blocked value for this WS_Ficha_Produto.
     * 
     * @param blocked
     */
    public void setBlocked(java.lang.Boolean blocked) {
        this.blocked = blocked;
    }


    /**
     * Gets the last_Date_Modified value for this WS_Ficha_Produto.
     * 
     * @return last_Date_Modified
     */
    public java.util.Date getLast_Date_Modified() {
        return last_Date_Modified;
    }


    /**
     * Sets the last_Date_Modified value for this WS_Ficha_Produto.
     * 
     * @param last_Date_Modified
     */
    public void setLast_Date_Modified(java.util.Date last_Date_Modified) {
        this.last_Date_Modified = last_Date_Modified;
    }


    /**
     * Gets the costing_Method value for this WS_Ficha_Produto.
     * 
     * @return costing_Method
     */
    public microsoft_dynamics_schemas.Costing_Method getCosting_Method() {
        return costing_Method;
    }


    /**
     * Sets the costing_Method value for this WS_Ficha_Produto.
     * 
     * @param costing_Method
     */
    public void setCosting_Method(microsoft_dynamics_schemas.Costing_Method costing_Method) {
        this.costing_Method = costing_Method;
    }


    /**
     * Gets the cost_is_Adjusted value for this WS_Ficha_Produto.
     * 
     * @return cost_is_Adjusted
     */
    public java.lang.Boolean getCost_is_Adjusted() {
        return cost_is_Adjusted;
    }


    /**
     * Sets the cost_is_Adjusted value for this WS_Ficha_Produto.
     * 
     * @param cost_is_Adjusted
     */
    public void setCost_is_Adjusted(java.lang.Boolean cost_is_Adjusted) {
        this.cost_is_Adjusted = cost_is_Adjusted;
    }


    /**
     * Gets the cost_is_Posted_to_G_L value for this WS_Ficha_Produto.
     * 
     * @return cost_is_Posted_to_G_L
     */
    public java.lang.Boolean getCost_is_Posted_to_G_L() {
        return cost_is_Posted_to_G_L;
    }


    /**
     * Sets the cost_is_Posted_to_G_L value for this WS_Ficha_Produto.
     * 
     * @param cost_is_Posted_to_G_L
     */
    public void setCost_is_Posted_to_G_L(java.lang.Boolean cost_is_Posted_to_G_L) {
        this.cost_is_Posted_to_G_L = cost_is_Posted_to_G_L;
    }


    /**
     * Gets the standard_Cost value for this WS_Ficha_Produto.
     * 
     * @return standard_Cost
     */
    public java.math.BigDecimal getStandard_Cost() {
        return standard_Cost;
    }


    /**
     * Sets the standard_Cost value for this WS_Ficha_Produto.
     * 
     * @param standard_Cost
     */
    public void setStandard_Cost(java.math.BigDecimal standard_Cost) {
        this.standard_Cost = standard_Cost;
    }


    /**
     * Gets the unit_Cost value for this WS_Ficha_Produto.
     * 
     * @return unit_Cost
     */
    public java.math.BigDecimal getUnit_Cost() {
        return unit_Cost;
    }


    /**
     * Sets the unit_Cost value for this WS_Ficha_Produto.
     * 
     * @param unit_Cost
     */
    public void setUnit_Cost(java.math.BigDecimal unit_Cost) {
        this.unit_Cost = unit_Cost;
    }


    /**
     * Gets the overhead_Rate value for this WS_Ficha_Produto.
     * 
     * @return overhead_Rate
     */
    public java.math.BigDecimal getOverhead_Rate() {
        return overhead_Rate;
    }


    /**
     * Sets the overhead_Rate value for this WS_Ficha_Produto.
     * 
     * @param overhead_Rate
     */
    public void setOverhead_Rate(java.math.BigDecimal overhead_Rate) {
        this.overhead_Rate = overhead_Rate;
    }


    /**
     * Gets the indirect_Cost_Percent value for this WS_Ficha_Produto.
     * 
     * @return indirect_Cost_Percent
     */
    public java.math.BigDecimal getIndirect_Cost_Percent() {
        return indirect_Cost_Percent;
    }


    /**
     * Sets the indirect_Cost_Percent value for this WS_Ficha_Produto.
     * 
     * @param indirect_Cost_Percent
     */
    public void setIndirect_Cost_Percent(java.math.BigDecimal indirect_Cost_Percent) {
        this.indirect_Cost_Percent = indirect_Cost_Percent;
    }


    /**
     * Gets the last_Direct_Cost value for this WS_Ficha_Produto.
     * 
     * @return last_Direct_Cost
     */
    public java.math.BigDecimal getLast_Direct_Cost() {
        return last_Direct_Cost;
    }


    /**
     * Sets the last_Direct_Cost value for this WS_Ficha_Produto.
     * 
     * @param last_Direct_Cost
     */
    public void setLast_Direct_Cost(java.math.BigDecimal last_Direct_Cost) {
        this.last_Direct_Cost = last_Direct_Cost;
    }


    /**
     * Gets the price_Profit_Calculation value for this WS_Ficha_Produto.
     * 
     * @return price_Profit_Calculation
     */
    public microsoft_dynamics_schemas.Price_Profit_Calculation getPrice_Profit_Calculation() {
        return price_Profit_Calculation;
    }


    /**
     * Sets the price_Profit_Calculation value for this WS_Ficha_Produto.
     * 
     * @param price_Profit_Calculation
     */
    public void setPrice_Profit_Calculation(microsoft_dynamics_schemas.Price_Profit_Calculation price_Profit_Calculation) {
        this.price_Profit_Calculation = price_Profit_Calculation;
    }


    /**
     * Gets the profit_Percent value for this WS_Ficha_Produto.
     * 
     * @return profit_Percent
     */
    public java.math.BigDecimal getProfit_Percent() {
        return profit_Percent;
    }


    /**
     * Sets the profit_Percent value for this WS_Ficha_Produto.
     * 
     * @param profit_Percent
     */
    public void setProfit_Percent(java.math.BigDecimal profit_Percent) {
        this.profit_Percent = profit_Percent;
    }


    /**
     * Gets the unit_Price value for this WS_Ficha_Produto.
     * 
     * @return unit_Price
     */
    public java.math.BigDecimal getUnit_Price() {
        return unit_Price;
    }


    /**
     * Sets the unit_Price value for this WS_Ficha_Produto.
     * 
     * @param unit_Price
     */
    public void setUnit_Price(java.math.BigDecimal unit_Price) {
        this.unit_Price = unit_Price;
    }


    /**
     * Gets the gen_Prod_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @return gen_Prod_Posting_Group
     */
    public java.lang.String getGen_Prod_Posting_Group() {
        return gen_Prod_Posting_Group;
    }


    /**
     * Sets the gen_Prod_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @param gen_Prod_Posting_Group
     */
    public void setGen_Prod_Posting_Group(java.lang.String gen_Prod_Posting_Group) {
        this.gen_Prod_Posting_Group = gen_Prod_Posting_Group;
    }


    /**
     * Gets the VAT_Prod_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @return VAT_Prod_Posting_Group
     */
    public java.lang.String getVAT_Prod_Posting_Group() {
        return VAT_Prod_Posting_Group;
    }


    /**
     * Sets the VAT_Prod_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @param VAT_Prod_Posting_Group
     */
    public void setVAT_Prod_Posting_Group(java.lang.String VAT_Prod_Posting_Group) {
        this.VAT_Prod_Posting_Group = VAT_Prod_Posting_Group;
    }


    /**
     * Gets the inventory_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @return inventory_Posting_Group
     */
    public java.lang.String getInventory_Posting_Group() {
        return inventory_Posting_Group;
    }


    /**
     * Sets the inventory_Posting_Group value for this WS_Ficha_Produto.
     * 
     * @param inventory_Posting_Group
     */
    public void setInventory_Posting_Group(java.lang.String inventory_Posting_Group) {
        this.inventory_Posting_Group = inventory_Posting_Group;
    }


    /**
     * Gets the net_Invoiced_Qty value for this WS_Ficha_Produto.
     * 
     * @return net_Invoiced_Qty
     */
    public java.math.BigDecimal getNet_Invoiced_Qty() {
        return net_Invoiced_Qty;
    }


    /**
     * Sets the net_Invoiced_Qty value for this WS_Ficha_Produto.
     * 
     * @param net_Invoiced_Qty
     */
    public void setNet_Invoiced_Qty(java.math.BigDecimal net_Invoiced_Qty) {
        this.net_Invoiced_Qty = net_Invoiced_Qty;
    }


    /**
     * Gets the allow_Invoice_Disc value for this WS_Ficha_Produto.
     * 
     * @return allow_Invoice_Disc
     */
    public java.lang.Boolean getAllow_Invoice_Disc() {
        return allow_Invoice_Disc;
    }


    /**
     * Sets the allow_Invoice_Disc value for this WS_Ficha_Produto.
     * 
     * @param allow_Invoice_Disc
     */
    public void setAllow_Invoice_Disc(java.lang.Boolean allow_Invoice_Disc) {
        this.allow_Invoice_Disc = allow_Invoice_Disc;
    }


    /**
     * Gets the item_Disc_Group value for this WS_Ficha_Produto.
     * 
     * @return item_Disc_Group
     */
    public java.lang.String getItem_Disc_Group() {
        return item_Disc_Group;
    }


    /**
     * Sets the item_Disc_Group value for this WS_Ficha_Produto.
     * 
     * @param item_Disc_Group
     */
    public void setItem_Disc_Group(java.lang.String item_Disc_Group) {
        this.item_Disc_Group = item_Disc_Group;
    }


    /**
     * Gets the sales_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @return sales_Unit_of_Measure
     */
    public java.lang.String getSales_Unit_of_Measure() {
        return sales_Unit_of_Measure;
    }


    /**
     * Sets the sales_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @param sales_Unit_of_Measure
     */
    public void setSales_Unit_of_Measure(java.lang.String sales_Unit_of_Measure) {
        this.sales_Unit_of_Measure = sales_Unit_of_Measure;
    }


    /**
     * Gets the replenishment_System value for this WS_Ficha_Produto.
     * 
     * @return replenishment_System
     */
    public microsoft_dynamics_schemas.Replenishment_System getReplenishment_System() {
        return replenishment_System;
    }


    /**
     * Sets the replenishment_System value for this WS_Ficha_Produto.
     * 
     * @param replenishment_System
     */
    public void setReplenishment_System(microsoft_dynamics_schemas.Replenishment_System replenishment_System) {
        this.replenishment_System = replenishment_System;
    }


    /**
     * Gets the vendor_No value for this WS_Ficha_Produto.
     * 
     * @return vendor_No
     */
    public java.lang.String getVendor_No() {
        return vendor_No;
    }


    /**
     * Sets the vendor_No value for this WS_Ficha_Produto.
     * 
     * @param vendor_No
     */
    public void setVendor_No(java.lang.String vendor_No) {
        this.vendor_No = vendor_No;
    }


    /**
     * Gets the vendor_Item_No value for this WS_Ficha_Produto.
     * 
     * @return vendor_Item_No
     */
    public java.lang.String getVendor_Item_No() {
        return vendor_Item_No;
    }


    /**
     * Sets the vendor_Item_No value for this WS_Ficha_Produto.
     * 
     * @param vendor_Item_No
     */
    public void setVendor_Item_No(java.lang.String vendor_Item_No) {
        this.vendor_Item_No = vendor_Item_No;
    }


    /**
     * Gets the purch_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @return purch_Unit_of_Measure
     */
    public java.lang.String getPurch_Unit_of_Measure() {
        return purch_Unit_of_Measure;
    }


    /**
     * Sets the purch_Unit_of_Measure value for this WS_Ficha_Produto.
     * 
     * @param purch_Unit_of_Measure
     */
    public void setPurch_Unit_of_Measure(java.lang.String purch_Unit_of_Measure) {
        this.purch_Unit_of_Measure = purch_Unit_of_Measure;
    }


    /**
     * Gets the lead_Time_Calculation value for this WS_Ficha_Produto.
     * 
     * @return lead_Time_Calculation
     */
    public java.lang.String getLead_Time_Calculation() {
        return lead_Time_Calculation;
    }


    /**
     * Sets the lead_Time_Calculation value for this WS_Ficha_Produto.
     * 
     * @param lead_Time_Calculation
     */
    public void setLead_Time_Calculation(java.lang.String lead_Time_Calculation) {
        this.lead_Time_Calculation = lead_Time_Calculation;
    }


    /**
     * Gets the manufacturing_Policy value for this WS_Ficha_Produto.
     * 
     * @return manufacturing_Policy
     */
    public microsoft_dynamics_schemas.Manufacturing_Policy getManufacturing_Policy() {
        return manufacturing_Policy;
    }


    /**
     * Sets the manufacturing_Policy value for this WS_Ficha_Produto.
     * 
     * @param manufacturing_Policy
     */
    public void setManufacturing_Policy(microsoft_dynamics_schemas.Manufacturing_Policy manufacturing_Policy) {
        this.manufacturing_Policy = manufacturing_Policy;
    }


    /**
     * Gets the routing_No value for this WS_Ficha_Produto.
     * 
     * @return routing_No
     */
    public java.lang.String getRouting_No() {
        return routing_No;
    }


    /**
     * Sets the routing_No value for this WS_Ficha_Produto.
     * 
     * @param routing_No
     */
    public void setRouting_No(java.lang.String routing_No) {
        this.routing_No = routing_No;
    }


    /**
     * Gets the production_BOM_No value for this WS_Ficha_Produto.
     * 
     * @return production_BOM_No
     */
    public java.lang.String getProduction_BOM_No() {
        return production_BOM_No;
    }


    /**
     * Sets the production_BOM_No value for this WS_Ficha_Produto.
     * 
     * @param production_BOM_No
     */
    public void setProduction_BOM_No(java.lang.String production_BOM_No) {
        this.production_BOM_No = production_BOM_No;
    }


    /**
     * Gets the rounding_Precision value for this WS_Ficha_Produto.
     * 
     * @return rounding_Precision
     */
    public java.math.BigDecimal getRounding_Precision() {
        return rounding_Precision;
    }


    /**
     * Sets the rounding_Precision value for this WS_Ficha_Produto.
     * 
     * @param rounding_Precision
     */
    public void setRounding_Precision(java.math.BigDecimal rounding_Precision) {
        this.rounding_Precision = rounding_Precision;
    }


    /**
     * Gets the flushing_Method value for this WS_Ficha_Produto.
     * 
     * @return flushing_Method
     */
    public microsoft_dynamics_schemas.Flushing_Method getFlushing_Method() {
        return flushing_Method;
    }


    /**
     * Sets the flushing_Method value for this WS_Ficha_Produto.
     * 
     * @param flushing_Method
     */
    public void setFlushing_Method(microsoft_dynamics_schemas.Flushing_Method flushing_Method) {
        this.flushing_Method = flushing_Method;
    }


    /**
     * Gets the scrap_Percent value for this WS_Ficha_Produto.
     * 
     * @return scrap_Percent
     */
    public java.math.BigDecimal getScrap_Percent() {
        return scrap_Percent;
    }


    /**
     * Sets the scrap_Percent value for this WS_Ficha_Produto.
     * 
     * @param scrap_Percent
     */
    public void setScrap_Percent(java.math.BigDecimal scrap_Percent) {
        this.scrap_Percent = scrap_Percent;
    }


    /**
     * Gets the lot_Size value for this WS_Ficha_Produto.
     * 
     * @return lot_Size
     */
    public java.math.BigDecimal getLot_Size() {
        return lot_Size;
    }


    /**
     * Sets the lot_Size value for this WS_Ficha_Produto.
     * 
     * @param lot_Size
     */
    public void setLot_Size(java.math.BigDecimal lot_Size) {
        this.lot_Size = lot_Size;
    }


    /**
     * Gets the reordering_Policy value for this WS_Ficha_Produto.
     * 
     * @return reordering_Policy
     */
    public microsoft_dynamics_schemas.Reordering_Policy getReordering_Policy() {
        return reordering_Policy;
    }


    /**
     * Sets the reordering_Policy value for this WS_Ficha_Produto.
     * 
     * @param reordering_Policy
     */
    public void setReordering_Policy(microsoft_dynamics_schemas.Reordering_Policy reordering_Policy) {
        this.reordering_Policy = reordering_Policy;
    }


    /**
     * Gets the include_Inventory value for this WS_Ficha_Produto.
     * 
     * @return include_Inventory
     */
    public java.lang.Boolean getInclude_Inventory() {
        return include_Inventory;
    }


    /**
     * Sets the include_Inventory value for this WS_Ficha_Produto.
     * 
     * @param include_Inventory
     */
    public void setInclude_Inventory(java.lang.Boolean include_Inventory) {
        this.include_Inventory = include_Inventory;
    }


    /**
     * Gets the reserve value for this WS_Ficha_Produto.
     * 
     * @return reserve
     */
    public microsoft_dynamics_schemas.Reserve getReserve() {
        return reserve;
    }


    /**
     * Sets the reserve value for this WS_Ficha_Produto.
     * 
     * @param reserve
     */
    public void setReserve(microsoft_dynamics_schemas.Reserve reserve) {
        this.reserve = reserve;
    }


    /**
     * Gets the order_Tracking_Policy value for this WS_Ficha_Produto.
     * 
     * @return order_Tracking_Policy
     */
    public microsoft_dynamics_schemas.Order_Tracking_Policy getOrder_Tracking_Policy() {
        return order_Tracking_Policy;
    }


    /**
     * Sets the order_Tracking_Policy value for this WS_Ficha_Produto.
     * 
     * @param order_Tracking_Policy
     */
    public void setOrder_Tracking_Policy(microsoft_dynamics_schemas.Order_Tracking_Policy order_Tracking_Policy) {
        this.order_Tracking_Policy = order_Tracking_Policy;
    }


    /**
     * Gets the stockkeeping_Unit_Exists value for this WS_Ficha_Produto.
     * 
     * @return stockkeeping_Unit_Exists
     */
    public java.lang.Boolean getStockkeeping_Unit_Exists() {
        return stockkeeping_Unit_Exists;
    }


    /**
     * Sets the stockkeeping_Unit_Exists value for this WS_Ficha_Produto.
     * 
     * @param stockkeeping_Unit_Exists
     */
    public void setStockkeeping_Unit_Exists(java.lang.Boolean stockkeeping_Unit_Exists) {
        this.stockkeeping_Unit_Exists = stockkeeping_Unit_Exists;
    }


    /**
     * Gets the critical value for this WS_Ficha_Produto.
     * 
     * @return critical
     */
    public java.lang.Boolean getCritical() {
        return critical;
    }


    /**
     * Sets the critical value for this WS_Ficha_Produto.
     * 
     * @param critical
     */
    public void setCritical(java.lang.Boolean critical) {
        this.critical = critical;
    }


    /**
     * Gets the reorder_Cycle value for this WS_Ficha_Produto.
     * 
     * @return reorder_Cycle
     */
    public java.lang.String getReorder_Cycle() {
        return reorder_Cycle;
    }


    /**
     * Sets the reorder_Cycle value for this WS_Ficha_Produto.
     * 
     * @param reorder_Cycle
     */
    public void setReorder_Cycle(java.lang.String reorder_Cycle) {
        this.reorder_Cycle = reorder_Cycle;
    }


    /**
     * Gets the safety_Lead_Time value for this WS_Ficha_Produto.
     * 
     * @return safety_Lead_Time
     */
    public java.lang.String getSafety_Lead_Time() {
        return safety_Lead_Time;
    }


    /**
     * Sets the safety_Lead_Time value for this WS_Ficha_Produto.
     * 
     * @param safety_Lead_Time
     */
    public void setSafety_Lead_Time(java.lang.String safety_Lead_Time) {
        this.safety_Lead_Time = safety_Lead_Time;
    }


    /**
     * Gets the safety_Stock_Quantity value for this WS_Ficha_Produto.
     * 
     * @return safety_Stock_Quantity
     */
    public java.math.BigDecimal getSafety_Stock_Quantity() {
        return safety_Stock_Quantity;
    }


    /**
     * Sets the safety_Stock_Quantity value for this WS_Ficha_Produto.
     * 
     * @param safety_Stock_Quantity
     */
    public void setSafety_Stock_Quantity(java.math.BigDecimal safety_Stock_Quantity) {
        this.safety_Stock_Quantity = safety_Stock_Quantity;
    }


    /**
     * Gets the reorder_Point value for this WS_Ficha_Produto.
     * 
     * @return reorder_Point
     */
    public java.math.BigDecimal getReorder_Point() {
        return reorder_Point;
    }


    /**
     * Sets the reorder_Point value for this WS_Ficha_Produto.
     * 
     * @param reorder_Point
     */
    public void setReorder_Point(java.math.BigDecimal reorder_Point) {
        this.reorder_Point = reorder_Point;
    }


    /**
     * Gets the reorder_Quantity value for this WS_Ficha_Produto.
     * 
     * @return reorder_Quantity
     */
    public java.math.BigDecimal getReorder_Quantity() {
        return reorder_Quantity;
    }


    /**
     * Sets the reorder_Quantity value for this WS_Ficha_Produto.
     * 
     * @param reorder_Quantity
     */
    public void setReorder_Quantity(java.math.BigDecimal reorder_Quantity) {
        this.reorder_Quantity = reorder_Quantity;
    }


    /**
     * Gets the maximum_Inventory value for this WS_Ficha_Produto.
     * 
     * @return maximum_Inventory
     */
    public java.math.BigDecimal getMaximum_Inventory() {
        return maximum_Inventory;
    }


    /**
     * Sets the maximum_Inventory value for this WS_Ficha_Produto.
     * 
     * @param maximum_Inventory
     */
    public void setMaximum_Inventory(java.math.BigDecimal maximum_Inventory) {
        this.maximum_Inventory = maximum_Inventory;
    }


    /**
     * Gets the minimum_Order_Quantity value for this WS_Ficha_Produto.
     * 
     * @return minimum_Order_Quantity
     */
    public java.math.BigDecimal getMinimum_Order_Quantity() {
        return minimum_Order_Quantity;
    }


    /**
     * Sets the minimum_Order_Quantity value for this WS_Ficha_Produto.
     * 
     * @param minimum_Order_Quantity
     */
    public void setMinimum_Order_Quantity(java.math.BigDecimal minimum_Order_Quantity) {
        this.minimum_Order_Quantity = minimum_Order_Quantity;
    }


    /**
     * Gets the maximum_Order_Quantity value for this WS_Ficha_Produto.
     * 
     * @return maximum_Order_Quantity
     */
    public java.math.BigDecimal getMaximum_Order_Quantity() {
        return maximum_Order_Quantity;
    }


    /**
     * Sets the maximum_Order_Quantity value for this WS_Ficha_Produto.
     * 
     * @param maximum_Order_Quantity
     */
    public void setMaximum_Order_Quantity(java.math.BigDecimal maximum_Order_Quantity) {
        this.maximum_Order_Quantity = maximum_Order_Quantity;
    }


    /**
     * Gets the order_Multiple value for this WS_Ficha_Produto.
     * 
     * @return order_Multiple
     */
    public java.math.BigDecimal getOrder_Multiple() {
        return order_Multiple;
    }


    /**
     * Sets the order_Multiple value for this WS_Ficha_Produto.
     * 
     * @param order_Multiple
     */
    public void setOrder_Multiple(java.math.BigDecimal order_Multiple) {
        this.order_Multiple = order_Multiple;
    }


    /**
     * Gets the tariff_No value for this WS_Ficha_Produto.
     * 
     * @return tariff_No
     */
    public java.lang.String getTariff_No() {
        return tariff_No;
    }


    /**
     * Sets the tariff_No value for this WS_Ficha_Produto.
     * 
     * @param tariff_No
     */
    public void setTariff_No(java.lang.String tariff_No) {
        this.tariff_No = tariff_No;
    }


    /**
     * Gets the country_Region_of_Origin_Code value for this WS_Ficha_Produto.
     * 
     * @return country_Region_of_Origin_Code
     */
    public java.lang.String getCountry_Region_of_Origin_Code() {
        return country_Region_of_Origin_Code;
    }


    /**
     * Sets the country_Region_of_Origin_Code value for this WS_Ficha_Produto.
     * 
     * @param country_Region_of_Origin_Code
     */
    public void setCountry_Region_of_Origin_Code(java.lang.String country_Region_of_Origin_Code) {
        this.country_Region_of_Origin_Code = country_Region_of_Origin_Code;
    }


    /**
     * Gets the net_Weight value for this WS_Ficha_Produto.
     * 
     * @return net_Weight
     */
    public java.math.BigDecimal getNet_Weight() {
        return net_Weight;
    }


    /**
     * Sets the net_Weight value for this WS_Ficha_Produto.
     * 
     * @param net_Weight
     */
    public void setNet_Weight(java.math.BigDecimal net_Weight) {
        this.net_Weight = net_Weight;
    }


    /**
     * Gets the gross_Weight value for this WS_Ficha_Produto.
     * 
     * @return gross_Weight
     */
    public java.math.BigDecimal getGross_Weight() {
        return gross_Weight;
    }


    /**
     * Sets the gross_Weight value for this WS_Ficha_Produto.
     * 
     * @param gross_Weight
     */
    public void setGross_Weight(java.math.BigDecimal gross_Weight) {
        this.gross_Weight = gross_Weight;
    }


    /**
     * Gets the item_Tracking_Code value for this WS_Ficha_Produto.
     * 
     * @return item_Tracking_Code
     */
    public java.lang.String getItem_Tracking_Code() {
        return item_Tracking_Code;
    }


    /**
     * Sets the item_Tracking_Code value for this WS_Ficha_Produto.
     * 
     * @param item_Tracking_Code
     */
    public void setItem_Tracking_Code(java.lang.String item_Tracking_Code) {
        this.item_Tracking_Code = item_Tracking_Code;
    }


    /**
     * Gets the serial_Nos value for this WS_Ficha_Produto.
     * 
     * @return serial_Nos
     */
    public java.lang.String getSerial_Nos() {
        return serial_Nos;
    }


    /**
     * Sets the serial_Nos value for this WS_Ficha_Produto.
     * 
     * @param serial_Nos
     */
    public void setSerial_Nos(java.lang.String serial_Nos) {
        this.serial_Nos = serial_Nos;
    }


    /**
     * Gets the lot_Nos value for this WS_Ficha_Produto.
     * 
     * @return lot_Nos
     */
    public java.lang.String getLot_Nos() {
        return lot_Nos;
    }


    /**
     * Sets the lot_Nos value for this WS_Ficha_Produto.
     * 
     * @param lot_Nos
     */
    public void setLot_Nos(java.lang.String lot_Nos) {
        this.lot_Nos = lot_Nos;
    }


    /**
     * Gets the expiration_Calculation value for this WS_Ficha_Produto.
     * 
     * @return expiration_Calculation
     */
    public java.lang.String getExpiration_Calculation() {
        return expiration_Calculation;
    }


    /**
     * Sets the expiration_Calculation value for this WS_Ficha_Produto.
     * 
     * @param expiration_Calculation
     */
    public void setExpiration_Calculation(java.lang.String expiration_Calculation) {
        this.expiration_Calculation = expiration_Calculation;
    }


    /**
     * Gets the common_Item_No value for this WS_Ficha_Produto.
     * 
     * @return common_Item_No
     */
    public java.lang.String getCommon_Item_No() {
        return common_Item_No;
    }


    /**
     * Sets the common_Item_No value for this WS_Ficha_Produto.
     * 
     * @param common_Item_No
     */
    public void setCommon_Item_No(java.lang.String common_Item_No) {
        this.common_Item_No = common_Item_No;
    }


    /**
     * Gets the special_Equipment_Code value for this WS_Ficha_Produto.
     * 
     * @return special_Equipment_Code
     */
    public java.lang.String getSpecial_Equipment_Code() {
        return special_Equipment_Code;
    }


    /**
     * Sets the special_Equipment_Code value for this WS_Ficha_Produto.
     * 
     * @param special_Equipment_Code
     */
    public void setSpecial_Equipment_Code(java.lang.String special_Equipment_Code) {
        this.special_Equipment_Code = special_Equipment_Code;
    }


    /**
     * Gets the put_away_Template_Code value for this WS_Ficha_Produto.
     * 
     * @return put_away_Template_Code
     */
    public java.lang.String getPut_away_Template_Code() {
        return put_away_Template_Code;
    }


    /**
     * Sets the put_away_Template_Code value for this WS_Ficha_Produto.
     * 
     * @param put_away_Template_Code
     */
    public void setPut_away_Template_Code(java.lang.String put_away_Template_Code) {
        this.put_away_Template_Code = put_away_Template_Code;
    }


    /**
     * Gets the put_away_Unit_of_Measure_Code value for this WS_Ficha_Produto.
     * 
     * @return put_away_Unit_of_Measure_Code
     */
    public java.lang.String getPut_away_Unit_of_Measure_Code() {
        return put_away_Unit_of_Measure_Code;
    }


    /**
     * Sets the put_away_Unit_of_Measure_Code value for this WS_Ficha_Produto.
     * 
     * @param put_away_Unit_of_Measure_Code
     */
    public void setPut_away_Unit_of_Measure_Code(java.lang.String put_away_Unit_of_Measure_Code) {
        this.put_away_Unit_of_Measure_Code = put_away_Unit_of_Measure_Code;
    }


    /**
     * Gets the phys_Invt_Counting_Period_Code value for this WS_Ficha_Produto.
     * 
     * @return phys_Invt_Counting_Period_Code
     */
    public java.lang.String getPhys_Invt_Counting_Period_Code() {
        return phys_Invt_Counting_Period_Code;
    }


    /**
     * Sets the phys_Invt_Counting_Period_Code value for this WS_Ficha_Produto.
     * 
     * @param phys_Invt_Counting_Period_Code
     */
    public void setPhys_Invt_Counting_Period_Code(java.lang.String phys_Invt_Counting_Period_Code) {
        this.phys_Invt_Counting_Period_Code = phys_Invt_Counting_Period_Code;
    }


    /**
     * Gets the last_Phys_Invt_Date value for this WS_Ficha_Produto.
     * 
     * @return last_Phys_Invt_Date
     */
    public java.util.Date getLast_Phys_Invt_Date() {
        return last_Phys_Invt_Date;
    }


    /**
     * Sets the last_Phys_Invt_Date value for this WS_Ficha_Produto.
     * 
     * @param last_Phys_Invt_Date
     */
    public void setLast_Phys_Invt_Date(java.util.Date last_Phys_Invt_Date) {
        this.last_Phys_Invt_Date = last_Phys_Invt_Date;
    }


    /**
     * Gets the last_Counting_Period_Update value for this WS_Ficha_Produto.
     * 
     * @return last_Counting_Period_Update
     */
    public java.util.Date getLast_Counting_Period_Update() {
        return last_Counting_Period_Update;
    }


    /**
     * Sets the last_Counting_Period_Update value for this WS_Ficha_Produto.
     * 
     * @param last_Counting_Period_Update
     */
    public void setLast_Counting_Period_Update(java.util.Date last_Counting_Period_Update) {
        this.last_Counting_Period_Update = last_Counting_Period_Update;
    }


    /**
     * Gets the next_Counting_Period value for this WS_Ficha_Produto.
     * 
     * @return next_Counting_Period
     */
    public java.lang.String getNext_Counting_Period() {
        return next_Counting_Period;
    }


    /**
     * Sets the next_Counting_Period value for this WS_Ficha_Produto.
     * 
     * @param next_Counting_Period
     */
    public void setNext_Counting_Period(java.lang.String next_Counting_Period) {
        this.next_Counting_Period = next_Counting_Period;
    }


    /**
     * Gets the identifier_Code value for this WS_Ficha_Produto.
     * 
     * @return identifier_Code
     */
    public java.lang.String getIdentifier_Code() {
        return identifier_Code;
    }


    /**
     * Sets the identifier_Code value for this WS_Ficha_Produto.
     * 
     * @param identifier_Code
     */
    public void setIdentifier_Code(java.lang.String identifier_Code) {
        this.identifier_Code = identifier_Code;
    }


    /**
     * Gets the use_Cross_Docking value for this WS_Ficha_Produto.
     * 
     * @return use_Cross_Docking
     */
    public java.lang.Boolean getUse_Cross_Docking() {
        return use_Cross_Docking;
    }


    /**
     * Sets the use_Cross_Docking value for this WS_Ficha_Produto.
     * 
     * @param use_Cross_Docking
     */
    public void setUse_Cross_Docking(java.lang.Boolean use_Cross_Docking) {
        this.use_Cross_Docking = use_Cross_Docking;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WS_Ficha_Produto)) return false;
        WS_Ficha_Produto other = (WS_Ficha_Produto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.key==null && other.getKey()==null) || 
             (this.key!=null &&
              this.key.equals(other.getKey()))) &&
            ((this.no==null && other.getNo()==null) || 
             (this.no!=null &&
              this.no.equals(other.getNo()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.base_Unit_of_Measure==null && other.getBase_Unit_of_Measure()==null) || 
             (this.base_Unit_of_Measure!=null &&
              this.base_Unit_of_Measure.equals(other.getBase_Unit_of_Measure()))) &&
            ((this.bill_of_Materials==null && other.getBill_of_Materials()==null) || 
             (this.bill_of_Materials!=null &&
              this.bill_of_Materials.equals(other.getBill_of_Materials()))) &&
            ((this.shelf_No==null && other.getShelf_No()==null) || 
             (this.shelf_No!=null &&
              this.shelf_No.equals(other.getShelf_No()))) &&
            ((this.automatic_Ext_Texts==null && other.getAutomatic_Ext_Texts()==null) || 
             (this.automatic_Ext_Texts!=null &&
              this.automatic_Ext_Texts.equals(other.getAutomatic_Ext_Texts()))) &&
            ((this.created_From_Nonstock_Item==null && other.getCreated_From_Nonstock_Item()==null) || 
             (this.created_From_Nonstock_Item!=null &&
              this.created_From_Nonstock_Item.equals(other.getCreated_From_Nonstock_Item()))) &&
            ((this.item_Category_Code==null && other.getItem_Category_Code()==null) || 
             (this.item_Category_Code!=null &&
              this.item_Category_Code.equals(other.getItem_Category_Code()))) &&
            ((this.product_Group_Code==null && other.getProduct_Group_Code()==null) || 
             (this.product_Group_Code!=null &&
              this.product_Group_Code.equals(other.getProduct_Group_Code()))) &&
            ((this.search_Description==null && other.getSearch_Description()==null) || 
             (this.search_Description!=null &&
              this.search_Description.equals(other.getSearch_Description()))) &&
            ((this.inventory==null && other.getInventory()==null) || 
             (this.inventory!=null &&
              this.inventory.equals(other.getInventory()))) &&
            ((this.qty_on_Purch_Order==null && other.getQty_on_Purch_Order()==null) || 
             (this.qty_on_Purch_Order!=null &&
              this.qty_on_Purch_Order.equals(other.getQty_on_Purch_Order()))) &&
            ((this.qty_on_Prod_Order==null && other.getQty_on_Prod_Order()==null) || 
             (this.qty_on_Prod_Order!=null &&
              this.qty_on_Prod_Order.equals(other.getQty_on_Prod_Order()))) &&
            ((this.qty_on_Component_Lines==null && other.getQty_on_Component_Lines()==null) || 
             (this.qty_on_Component_Lines!=null &&
              this.qty_on_Component_Lines.equals(other.getQty_on_Component_Lines()))) &&
            ((this.qty_on_Sales_Order==null && other.getQty_on_Sales_Order()==null) || 
             (this.qty_on_Sales_Order!=null &&
              this.qty_on_Sales_Order.equals(other.getQty_on_Sales_Order()))) &&
            ((this.qty_on_Service_Order==null && other.getQty_on_Service_Order()==null) || 
             (this.qty_on_Service_Order!=null &&
              this.qty_on_Service_Order.equals(other.getQty_on_Service_Order()))) &&
            ((this.service_Item_Group==null && other.getService_Item_Group()==null) || 
             (this.service_Item_Group!=null &&
              this.service_Item_Group.equals(other.getService_Item_Group()))) &&
            ((this.blocked==null && other.getBlocked()==null) || 
             (this.blocked!=null &&
              this.blocked.equals(other.getBlocked()))) &&
            ((this.last_Date_Modified==null && other.getLast_Date_Modified()==null) || 
             (this.last_Date_Modified!=null &&
              this.last_Date_Modified.equals(other.getLast_Date_Modified()))) &&
            ((this.costing_Method==null && other.getCosting_Method()==null) || 
             (this.costing_Method!=null &&
              this.costing_Method.equals(other.getCosting_Method()))) &&
            ((this.cost_is_Adjusted==null && other.getCost_is_Adjusted()==null) || 
             (this.cost_is_Adjusted!=null &&
              this.cost_is_Adjusted.equals(other.getCost_is_Adjusted()))) &&
            ((this.cost_is_Posted_to_G_L==null && other.getCost_is_Posted_to_G_L()==null) || 
             (this.cost_is_Posted_to_G_L!=null &&
              this.cost_is_Posted_to_G_L.equals(other.getCost_is_Posted_to_G_L()))) &&
            ((this.standard_Cost==null && other.getStandard_Cost()==null) || 
             (this.standard_Cost!=null &&
              this.standard_Cost.equals(other.getStandard_Cost()))) &&
            ((this.unit_Cost==null && other.getUnit_Cost()==null) || 
             (this.unit_Cost!=null &&
              this.unit_Cost.equals(other.getUnit_Cost()))) &&
            ((this.overhead_Rate==null && other.getOverhead_Rate()==null) || 
             (this.overhead_Rate!=null &&
              this.overhead_Rate.equals(other.getOverhead_Rate()))) &&
            ((this.indirect_Cost_Percent==null && other.getIndirect_Cost_Percent()==null) || 
             (this.indirect_Cost_Percent!=null &&
              this.indirect_Cost_Percent.equals(other.getIndirect_Cost_Percent()))) &&
            ((this.last_Direct_Cost==null && other.getLast_Direct_Cost()==null) || 
             (this.last_Direct_Cost!=null &&
              this.last_Direct_Cost.equals(other.getLast_Direct_Cost()))) &&
            ((this.price_Profit_Calculation==null && other.getPrice_Profit_Calculation()==null) || 
             (this.price_Profit_Calculation!=null &&
              this.price_Profit_Calculation.equals(other.getPrice_Profit_Calculation()))) &&
            ((this.profit_Percent==null && other.getProfit_Percent()==null) || 
             (this.profit_Percent!=null &&
              this.profit_Percent.equals(other.getProfit_Percent()))) &&
            ((this.unit_Price==null && other.getUnit_Price()==null) || 
             (this.unit_Price!=null &&
              this.unit_Price.equals(other.getUnit_Price()))) &&
            ((this.gen_Prod_Posting_Group==null && other.getGen_Prod_Posting_Group()==null) || 
             (this.gen_Prod_Posting_Group!=null &&
              this.gen_Prod_Posting_Group.equals(other.getGen_Prod_Posting_Group()))) &&
            ((this.VAT_Prod_Posting_Group==null && other.getVAT_Prod_Posting_Group()==null) || 
             (this.VAT_Prod_Posting_Group!=null &&
              this.VAT_Prod_Posting_Group.equals(other.getVAT_Prod_Posting_Group()))) &&
            ((this.inventory_Posting_Group==null && other.getInventory_Posting_Group()==null) || 
             (this.inventory_Posting_Group!=null &&
              this.inventory_Posting_Group.equals(other.getInventory_Posting_Group()))) &&
            ((this.net_Invoiced_Qty==null && other.getNet_Invoiced_Qty()==null) || 
             (this.net_Invoiced_Qty!=null &&
              this.net_Invoiced_Qty.equals(other.getNet_Invoiced_Qty()))) &&
            ((this.allow_Invoice_Disc==null && other.getAllow_Invoice_Disc()==null) || 
             (this.allow_Invoice_Disc!=null &&
              this.allow_Invoice_Disc.equals(other.getAllow_Invoice_Disc()))) &&
            ((this.item_Disc_Group==null && other.getItem_Disc_Group()==null) || 
             (this.item_Disc_Group!=null &&
              this.item_Disc_Group.equals(other.getItem_Disc_Group()))) &&
            ((this.sales_Unit_of_Measure==null && other.getSales_Unit_of_Measure()==null) || 
             (this.sales_Unit_of_Measure!=null &&
              this.sales_Unit_of_Measure.equals(other.getSales_Unit_of_Measure()))) &&
            ((this.replenishment_System==null && other.getReplenishment_System()==null) || 
             (this.replenishment_System!=null &&
              this.replenishment_System.equals(other.getReplenishment_System()))) &&
            ((this.vendor_No==null && other.getVendor_No()==null) || 
             (this.vendor_No!=null &&
              this.vendor_No.equals(other.getVendor_No()))) &&
            ((this.vendor_Item_No==null && other.getVendor_Item_No()==null) || 
             (this.vendor_Item_No!=null &&
              this.vendor_Item_No.equals(other.getVendor_Item_No()))) &&
            ((this.purch_Unit_of_Measure==null && other.getPurch_Unit_of_Measure()==null) || 
             (this.purch_Unit_of_Measure!=null &&
              this.purch_Unit_of_Measure.equals(other.getPurch_Unit_of_Measure()))) &&
            ((this.lead_Time_Calculation==null && other.getLead_Time_Calculation()==null) || 
             (this.lead_Time_Calculation!=null &&
              this.lead_Time_Calculation.equals(other.getLead_Time_Calculation()))) &&
            ((this.manufacturing_Policy==null && other.getManufacturing_Policy()==null) || 
             (this.manufacturing_Policy!=null &&
              this.manufacturing_Policy.equals(other.getManufacturing_Policy()))) &&
            ((this.routing_No==null && other.getRouting_No()==null) || 
             (this.routing_No!=null &&
              this.routing_No.equals(other.getRouting_No()))) &&
            ((this.production_BOM_No==null && other.getProduction_BOM_No()==null) || 
             (this.production_BOM_No!=null &&
              this.production_BOM_No.equals(other.getProduction_BOM_No()))) &&
            ((this.rounding_Precision==null && other.getRounding_Precision()==null) || 
             (this.rounding_Precision!=null &&
              this.rounding_Precision.equals(other.getRounding_Precision()))) &&
            ((this.flushing_Method==null && other.getFlushing_Method()==null) || 
             (this.flushing_Method!=null &&
              this.flushing_Method.equals(other.getFlushing_Method()))) &&
            ((this.scrap_Percent==null && other.getScrap_Percent()==null) || 
             (this.scrap_Percent!=null &&
              this.scrap_Percent.equals(other.getScrap_Percent()))) &&
            ((this.lot_Size==null && other.getLot_Size()==null) || 
             (this.lot_Size!=null &&
              this.lot_Size.equals(other.getLot_Size()))) &&
            ((this.reordering_Policy==null && other.getReordering_Policy()==null) || 
             (this.reordering_Policy!=null &&
              this.reordering_Policy.equals(other.getReordering_Policy()))) &&
            ((this.include_Inventory==null && other.getInclude_Inventory()==null) || 
             (this.include_Inventory!=null &&
              this.include_Inventory.equals(other.getInclude_Inventory()))) &&
            ((this.reserve==null && other.getReserve()==null) || 
             (this.reserve!=null &&
              this.reserve.equals(other.getReserve()))) &&
            ((this.order_Tracking_Policy==null && other.getOrder_Tracking_Policy()==null) || 
             (this.order_Tracking_Policy!=null &&
              this.order_Tracking_Policy.equals(other.getOrder_Tracking_Policy()))) &&
            ((this.stockkeeping_Unit_Exists==null && other.getStockkeeping_Unit_Exists()==null) || 
             (this.stockkeeping_Unit_Exists!=null &&
              this.stockkeeping_Unit_Exists.equals(other.getStockkeeping_Unit_Exists()))) &&
            ((this.critical==null && other.getCritical()==null) || 
             (this.critical!=null &&
              this.critical.equals(other.getCritical()))) &&
            ((this.reorder_Cycle==null && other.getReorder_Cycle()==null) || 
             (this.reorder_Cycle!=null &&
              this.reorder_Cycle.equals(other.getReorder_Cycle()))) &&
            ((this.safety_Lead_Time==null && other.getSafety_Lead_Time()==null) || 
             (this.safety_Lead_Time!=null &&
              this.safety_Lead_Time.equals(other.getSafety_Lead_Time()))) &&
            ((this.safety_Stock_Quantity==null && other.getSafety_Stock_Quantity()==null) || 
             (this.safety_Stock_Quantity!=null &&
              this.safety_Stock_Quantity.equals(other.getSafety_Stock_Quantity()))) &&
            ((this.reorder_Point==null && other.getReorder_Point()==null) || 
             (this.reorder_Point!=null &&
              this.reorder_Point.equals(other.getReorder_Point()))) &&
            ((this.reorder_Quantity==null && other.getReorder_Quantity()==null) || 
             (this.reorder_Quantity!=null &&
              this.reorder_Quantity.equals(other.getReorder_Quantity()))) &&
            ((this.maximum_Inventory==null && other.getMaximum_Inventory()==null) || 
             (this.maximum_Inventory!=null &&
              this.maximum_Inventory.equals(other.getMaximum_Inventory()))) &&
            ((this.minimum_Order_Quantity==null && other.getMinimum_Order_Quantity()==null) || 
             (this.minimum_Order_Quantity!=null &&
              this.minimum_Order_Quantity.equals(other.getMinimum_Order_Quantity()))) &&
            ((this.maximum_Order_Quantity==null && other.getMaximum_Order_Quantity()==null) || 
             (this.maximum_Order_Quantity!=null &&
              this.maximum_Order_Quantity.equals(other.getMaximum_Order_Quantity()))) &&
            ((this.order_Multiple==null && other.getOrder_Multiple()==null) || 
             (this.order_Multiple!=null &&
              this.order_Multiple.equals(other.getOrder_Multiple()))) &&
            ((this.tariff_No==null && other.getTariff_No()==null) || 
             (this.tariff_No!=null &&
              this.tariff_No.equals(other.getTariff_No()))) &&
            ((this.country_Region_of_Origin_Code==null && other.getCountry_Region_of_Origin_Code()==null) || 
             (this.country_Region_of_Origin_Code!=null &&
              this.country_Region_of_Origin_Code.equals(other.getCountry_Region_of_Origin_Code()))) &&
            ((this.net_Weight==null && other.getNet_Weight()==null) || 
             (this.net_Weight!=null &&
              this.net_Weight.equals(other.getNet_Weight()))) &&
            ((this.gross_Weight==null && other.getGross_Weight()==null) || 
             (this.gross_Weight!=null &&
              this.gross_Weight.equals(other.getGross_Weight()))) &&
            ((this.item_Tracking_Code==null && other.getItem_Tracking_Code()==null) || 
             (this.item_Tracking_Code!=null &&
              this.item_Tracking_Code.equals(other.getItem_Tracking_Code()))) &&
            ((this.serial_Nos==null && other.getSerial_Nos()==null) || 
             (this.serial_Nos!=null &&
              this.serial_Nos.equals(other.getSerial_Nos()))) &&
            ((this.lot_Nos==null && other.getLot_Nos()==null) || 
             (this.lot_Nos!=null &&
              this.lot_Nos.equals(other.getLot_Nos()))) &&
            ((this.expiration_Calculation==null && other.getExpiration_Calculation()==null) || 
             (this.expiration_Calculation!=null &&
              this.expiration_Calculation.equals(other.getExpiration_Calculation()))) &&
            ((this.common_Item_No==null && other.getCommon_Item_No()==null) || 
             (this.common_Item_No!=null &&
              this.common_Item_No.equals(other.getCommon_Item_No()))) &&
            ((this.special_Equipment_Code==null && other.getSpecial_Equipment_Code()==null) || 
             (this.special_Equipment_Code!=null &&
              this.special_Equipment_Code.equals(other.getSpecial_Equipment_Code()))) &&
            ((this.put_away_Template_Code==null && other.getPut_away_Template_Code()==null) || 
             (this.put_away_Template_Code!=null &&
              this.put_away_Template_Code.equals(other.getPut_away_Template_Code()))) &&
            ((this.put_away_Unit_of_Measure_Code==null && other.getPut_away_Unit_of_Measure_Code()==null) || 
             (this.put_away_Unit_of_Measure_Code!=null &&
              this.put_away_Unit_of_Measure_Code.equals(other.getPut_away_Unit_of_Measure_Code()))) &&
            ((this.phys_Invt_Counting_Period_Code==null && other.getPhys_Invt_Counting_Period_Code()==null) || 
             (this.phys_Invt_Counting_Period_Code!=null &&
              this.phys_Invt_Counting_Period_Code.equals(other.getPhys_Invt_Counting_Period_Code()))) &&
            ((this.last_Phys_Invt_Date==null && other.getLast_Phys_Invt_Date()==null) || 
             (this.last_Phys_Invt_Date!=null &&
              this.last_Phys_Invt_Date.equals(other.getLast_Phys_Invt_Date()))) &&
            ((this.last_Counting_Period_Update==null && other.getLast_Counting_Period_Update()==null) || 
             (this.last_Counting_Period_Update!=null &&
              this.last_Counting_Period_Update.equals(other.getLast_Counting_Period_Update()))) &&
            ((this.next_Counting_Period==null && other.getNext_Counting_Period()==null) || 
             (this.next_Counting_Period!=null &&
              this.next_Counting_Period.equals(other.getNext_Counting_Period()))) &&
            ((this.identifier_Code==null && other.getIdentifier_Code()==null) || 
             (this.identifier_Code!=null &&
              this.identifier_Code.equals(other.getIdentifier_Code()))) &&
            ((this.use_Cross_Docking==null && other.getUse_Cross_Docking()==null) || 
             (this.use_Cross_Docking!=null &&
              this.use_Cross_Docking.equals(other.getUse_Cross_Docking())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        if (getNo() != null) {
            _hashCode += getNo().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getBase_Unit_of_Measure() != null) {
            _hashCode += getBase_Unit_of_Measure().hashCode();
        }
        if (getBill_of_Materials() != null) {
            _hashCode += getBill_of_Materials().hashCode();
        }
        if (getShelf_No() != null) {
            _hashCode += getShelf_No().hashCode();
        }
        if (getAutomatic_Ext_Texts() != null) {
            _hashCode += getAutomatic_Ext_Texts().hashCode();
        }
        if (getCreated_From_Nonstock_Item() != null) {
            _hashCode += getCreated_From_Nonstock_Item().hashCode();
        }
        if (getItem_Category_Code() != null) {
            _hashCode += getItem_Category_Code().hashCode();
        }
        if (getProduct_Group_Code() != null) {
            _hashCode += getProduct_Group_Code().hashCode();
        }
        if (getSearch_Description() != null) {
            _hashCode += getSearch_Description().hashCode();
        }
        if (getInventory() != null) {
            _hashCode += getInventory().hashCode();
        }
        if (getQty_on_Purch_Order() != null) {
            _hashCode += getQty_on_Purch_Order().hashCode();
        }
        if (getQty_on_Prod_Order() != null) {
            _hashCode += getQty_on_Prod_Order().hashCode();
        }
        if (getQty_on_Component_Lines() != null) {
            _hashCode += getQty_on_Component_Lines().hashCode();
        }
        if (getQty_on_Sales_Order() != null) {
            _hashCode += getQty_on_Sales_Order().hashCode();
        }
        if (getQty_on_Service_Order() != null) {
            _hashCode += getQty_on_Service_Order().hashCode();
        }
        if (getService_Item_Group() != null) {
            _hashCode += getService_Item_Group().hashCode();
        }
        if (getBlocked() != null) {
            _hashCode += getBlocked().hashCode();
        }
        if (getLast_Date_Modified() != null) {
            _hashCode += getLast_Date_Modified().hashCode();
        }
        if (getCosting_Method() != null) {
            _hashCode += getCosting_Method().hashCode();
        }
        if (getCost_is_Adjusted() != null) {
            _hashCode += getCost_is_Adjusted().hashCode();
        }
        if (getCost_is_Posted_to_G_L() != null) {
            _hashCode += getCost_is_Posted_to_G_L().hashCode();
        }
        if (getStandard_Cost() != null) {
            _hashCode += getStandard_Cost().hashCode();
        }
        if (getUnit_Cost() != null) {
            _hashCode += getUnit_Cost().hashCode();
        }
        if (getOverhead_Rate() != null) {
            _hashCode += getOverhead_Rate().hashCode();
        }
        if (getIndirect_Cost_Percent() != null) {
            _hashCode += getIndirect_Cost_Percent().hashCode();
        }
        if (getLast_Direct_Cost() != null) {
            _hashCode += getLast_Direct_Cost().hashCode();
        }
        if (getPrice_Profit_Calculation() != null) {
            _hashCode += getPrice_Profit_Calculation().hashCode();
        }
        if (getProfit_Percent() != null) {
            _hashCode += getProfit_Percent().hashCode();
        }
        if (getUnit_Price() != null) {
            _hashCode += getUnit_Price().hashCode();
        }
        if (getGen_Prod_Posting_Group() != null) {
            _hashCode += getGen_Prod_Posting_Group().hashCode();
        }
        if (getVAT_Prod_Posting_Group() != null) {
            _hashCode += getVAT_Prod_Posting_Group().hashCode();
        }
        if (getInventory_Posting_Group() != null) {
            _hashCode += getInventory_Posting_Group().hashCode();
        }
        if (getNet_Invoiced_Qty() != null) {
            _hashCode += getNet_Invoiced_Qty().hashCode();
        }
        if (getAllow_Invoice_Disc() != null) {
            _hashCode += getAllow_Invoice_Disc().hashCode();
        }
        if (getItem_Disc_Group() != null) {
            _hashCode += getItem_Disc_Group().hashCode();
        }
        if (getSales_Unit_of_Measure() != null) {
            _hashCode += getSales_Unit_of_Measure().hashCode();
        }
        if (getReplenishment_System() != null) {
            _hashCode += getReplenishment_System().hashCode();
        }
        if (getVendor_No() != null) {
            _hashCode += getVendor_No().hashCode();
        }
        if (getVendor_Item_No() != null) {
            _hashCode += getVendor_Item_No().hashCode();
        }
        if (getPurch_Unit_of_Measure() != null) {
            _hashCode += getPurch_Unit_of_Measure().hashCode();
        }
        if (getLead_Time_Calculation() != null) {
            _hashCode += getLead_Time_Calculation().hashCode();
        }
        if (getManufacturing_Policy() != null) {
            _hashCode += getManufacturing_Policy().hashCode();
        }
        if (getRouting_No() != null) {
            _hashCode += getRouting_No().hashCode();
        }
        if (getProduction_BOM_No() != null) {
            _hashCode += getProduction_BOM_No().hashCode();
        }
        if (getRounding_Precision() != null) {
            _hashCode += getRounding_Precision().hashCode();
        }
        if (getFlushing_Method() != null) {
            _hashCode += getFlushing_Method().hashCode();
        }
        if (getScrap_Percent() != null) {
            _hashCode += getScrap_Percent().hashCode();
        }
        if (getLot_Size() != null) {
            _hashCode += getLot_Size().hashCode();
        }
        if (getReordering_Policy() != null) {
            _hashCode += getReordering_Policy().hashCode();
        }
        if (getInclude_Inventory() != null) {
            _hashCode += getInclude_Inventory().hashCode();
        }
        if (getReserve() != null) {
            _hashCode += getReserve().hashCode();
        }
        if (getOrder_Tracking_Policy() != null) {
            _hashCode += getOrder_Tracking_Policy().hashCode();
        }
        if (getStockkeeping_Unit_Exists() != null) {
            _hashCode += getStockkeeping_Unit_Exists().hashCode();
        }
        if (getCritical() != null) {
            _hashCode += getCritical().hashCode();
        }
        if (getReorder_Cycle() != null) {
            _hashCode += getReorder_Cycle().hashCode();
        }
        if (getSafety_Lead_Time() != null) {
            _hashCode += getSafety_Lead_Time().hashCode();
        }
        if (getSafety_Stock_Quantity() != null) {
            _hashCode += getSafety_Stock_Quantity().hashCode();
        }
        if (getReorder_Point() != null) {
            _hashCode += getReorder_Point().hashCode();
        }
        if (getReorder_Quantity() != null) {
            _hashCode += getReorder_Quantity().hashCode();
        }
        if (getMaximum_Inventory() != null) {
            _hashCode += getMaximum_Inventory().hashCode();
        }
        if (getMinimum_Order_Quantity() != null) {
            _hashCode += getMinimum_Order_Quantity().hashCode();
        }
        if (getMaximum_Order_Quantity() != null) {
            _hashCode += getMaximum_Order_Quantity().hashCode();
        }
        if (getOrder_Multiple() != null) {
            _hashCode += getOrder_Multiple().hashCode();
        }
        if (getTariff_No() != null) {
            _hashCode += getTariff_No().hashCode();
        }
        if (getCountry_Region_of_Origin_Code() != null) {
            _hashCode += getCountry_Region_of_Origin_Code().hashCode();
        }
        if (getNet_Weight() != null) {
            _hashCode += getNet_Weight().hashCode();
        }
        if (getGross_Weight() != null) {
            _hashCode += getGross_Weight().hashCode();
        }
        if (getItem_Tracking_Code() != null) {
            _hashCode += getItem_Tracking_Code().hashCode();
        }
        if (getSerial_Nos() != null) {
            _hashCode += getSerial_Nos().hashCode();
        }
        if (getLot_Nos() != null) {
            _hashCode += getLot_Nos().hashCode();
        }
        if (getExpiration_Calculation() != null) {
            _hashCode += getExpiration_Calculation().hashCode();
        }
        if (getCommon_Item_No() != null) {
            _hashCode += getCommon_Item_No().hashCode();
        }
        if (getSpecial_Equipment_Code() != null) {
            _hashCode += getSpecial_Equipment_Code().hashCode();
        }
        if (getPut_away_Template_Code() != null) {
            _hashCode += getPut_away_Template_Code().hashCode();
        }
        if (getPut_away_Unit_of_Measure_Code() != null) {
            _hashCode += getPut_away_Unit_of_Measure_Code().hashCode();
        }
        if (getPhys_Invt_Counting_Period_Code() != null) {
            _hashCode += getPhys_Invt_Counting_Period_Code().hashCode();
        }
        if (getLast_Phys_Invt_Date() != null) {
            _hashCode += getLast_Phys_Invt_Date().hashCode();
        }
        if (getLast_Counting_Period_Update() != null) {
            _hashCode += getLast_Counting_Period_Update().hashCode();
        }
        if (getNext_Counting_Period() != null) {
            _hashCode += getNext_Counting_Period().hashCode();
        }
        if (getIdentifier_Code() != null) {
            _hashCode += getIdentifier_Code().hashCode();
        }
        if (getUse_Cross_Docking() != null) {
            _hashCode += getUse_Cross_Docking().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WS_Ficha_Produto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("base_Unit_of_Measure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Base_Unit_of_Measure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bill_of_Materials");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Bill_of_Materials"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shelf_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Shelf_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automatic_Ext_Texts");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Automatic_Ext_Texts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("created_From_Nonstock_Item");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Created_From_Nonstock_Item"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item_Category_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Item_Category_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("product_Group_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Product_Group_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("search_Description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Search_Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inventory");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Inventory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_on_Purch_Order");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Qty_on_Purch_Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_on_Prod_Order");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Qty_on_Prod_Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_on_Component_Lines");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Qty_on_Component_Lines"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_on_Sales_Order");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Qty_on_Sales_Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qty_on_Service_Order");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Qty_on_Service_Order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("service_Item_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Service_Item_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blocked");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Blocked"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last_Date_Modified");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Last_Date_Modified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("costing_Method");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Costing_Method"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Costing_Method"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cost_is_Adjusted");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Cost_is_Adjusted"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cost_is_Posted_to_G_L");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Cost_is_Posted_to_G_L"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standard_Cost");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Standard_Cost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_Cost");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Unit_Cost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("overhead_Rate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Overhead_Rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("indirect_Cost_Percent");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Indirect_Cost_Percent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last_Direct_Cost");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Last_Direct_Cost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("price_Profit_Calculation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Price_Profit_Calculation"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Price_Profit_Calculation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("profit_Percent");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Profit_Percent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_Price");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Unit_Price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gen_Prod_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Gen_Prod_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VAT_Prod_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "VAT_Prod_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inventory_Posting_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Inventory_Posting_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("net_Invoiced_Qty");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Net_Invoiced_Qty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allow_Invoice_Disc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Allow_Invoice_Disc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item_Disc_Group");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Item_Disc_Group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sales_Unit_of_Measure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Sales_Unit_of_Measure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replenishment_System");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Replenishment_System"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Replenishment_System"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vendor_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Vendor_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vendor_Item_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Vendor_Item_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purch_Unit_of_Measure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Purch_Unit_of_Measure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lead_Time_Calculation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Lead_Time_Calculation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturing_Policy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Manufacturing_Policy"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Manufacturing_Policy"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routing_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Routing_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("production_BOM_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Production_BOM_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rounding_Precision");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Rounding_Precision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flushing_Method");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Flushing_Method"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Flushing_Method"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scrap_Percent");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Scrap_Percent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lot_Size");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Lot_Size"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reordering_Policy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reordering_Policy"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reordering_Policy"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("include_Inventory");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Include_Inventory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reserve"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reserve"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order_Tracking_Policy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Order_Tracking_Policy"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Order_Tracking_Policy"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stockkeeping_Unit_Exists");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Stockkeeping_Unit_Exists"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("critical");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Critical"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reorder_Cycle");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reorder_Cycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("safety_Lead_Time");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Safety_Lead_Time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("safety_Stock_Quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Safety_Stock_Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reorder_Point");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reorder_Point"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reorder_Quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reorder_Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximum_Inventory");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Maximum_Inventory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minimum_Order_Quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Minimum_Order_Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximum_Order_Quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Maximum_Order_Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order_Multiple");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Order_Multiple"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tariff_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Tariff_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country_Region_of_Origin_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Country_Region_of_Origin_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("net_Weight");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Net_Weight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gross_Weight");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Gross_Weight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item_Tracking_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Item_Tracking_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serial_Nos");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Serial_Nos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lot_Nos");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Lot_Nos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiration_Calculation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Expiration_Calculation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("common_Item_No");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Common_Item_No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("special_Equipment_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Special_Equipment_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("put_away_Template_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Put_away_Template_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("put_away_Unit_of_Measure_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Put_away_Unit_of_Measure_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phys_Invt_Counting_Period_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Phys_Invt_Counting_Period_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last_Phys_Invt_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Last_Phys_Invt_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last_Counting_Period_Update");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Last_Counting_Period_Update"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("next_Counting_Period");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Next_Counting_Period"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identifier_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Identifier_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("use_Cross_Docking");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Use_Cross_Docking"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
