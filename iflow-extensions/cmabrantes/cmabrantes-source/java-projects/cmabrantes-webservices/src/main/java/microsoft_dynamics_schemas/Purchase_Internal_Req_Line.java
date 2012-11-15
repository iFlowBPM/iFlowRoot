/**
 * Purchase_Internal_Req_Line.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Purchase_Internal_Req_Line  implements java.io.Serializable {
    private java.lang.String key;

    private java.lang.String no;

    private java.lang.String description;

    private java.lang.String location_Code;

    private java.math.BigDecimal quantity;

    private java.math.BigDecimal expense_Proposal_Qty;

    private java.math.BigDecimal quantity_Received;

    private java.lang.String unit_of_Measure_Code;

    private java.lang.String unit_of_Measure;

    private java.lang.String budget_Dimension_1_Code;

    private java.lang.String budget_Dimension_2_Code;

    private java.lang.String budget_Dimension_3_Code;

    private java.lang.String budget_Dimension_4_Code;

    private java.util.Date promised_Receipt_Date;

    private java.util.Date planned_Receipt_Date;

    private java.util.Date expected_Receipt_Date;

    private java.util.Date order_Date;

    private java.lang.String lead_Time_Calculation;

    private java.lang.Boolean finished;

    private java.lang.Integer appl_to_Item_Entry;

    private java.lang.String shortcut_Dimension_1_Code;

    private java.lang.String shortcut_Dimension_2_Code;

    private java.lang.String DRF_Code;

    private java.lang.String shortcutDimCode_x005B_3_x005D_;

    private java.lang.String shortcutDimCode_x005B_4_x005D_;

    private java.lang.String shortcutDimCode_x005B_5_x005D_;

    private java.lang.String shortcutDimCode_x005B_6_x005D_;

    private java.lang.String shortcutDimCode_x005B_7_x005D_;

    private java.lang.String shortcutDimCode_x005B_8_x005D_;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec;

    public Purchase_Internal_Req_Line() {
    }

    public Purchase_Internal_Req_Line(
           java.lang.String key,
           java.lang.String no,
           java.lang.String description,
           java.lang.String location_Code,
           java.math.BigDecimal quantity,
           java.math.BigDecimal expense_Proposal_Qty,
           java.math.BigDecimal quantity_Received,
           java.lang.String unit_of_Measure_Code,
           java.lang.String unit_of_Measure,
           java.lang.String budget_Dimension_1_Code,
           java.lang.String budget_Dimension_2_Code,
           java.lang.String budget_Dimension_3_Code,
           java.lang.String budget_Dimension_4_Code,
           java.util.Date promised_Receipt_Date,
           java.util.Date planned_Receipt_Date,
           java.util.Date expected_Receipt_Date,
           java.util.Date order_Date,
           java.lang.String lead_Time_Calculation,
           java.lang.Boolean finished,
           java.lang.Integer appl_to_Item_Entry,
           java.lang.String shortcut_Dimension_1_Code,
           java.lang.String shortcut_Dimension_2_Code,
           java.lang.String DRF_Code,
           java.lang.String shortcutDimCode_x005B_3_x005D_,
           java.lang.String shortcutDimCode_x005B_4_x005D_,
           java.lang.String shortcutDimCode_x005B_5_x005D_,
           java.lang.String shortcutDimCode_x005B_6_x005D_,
           java.lang.String shortcutDimCode_x005B_7_x005D_,
           java.lang.String shortcutDimCode_x005B_8_x005D_,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec) {
           this.key = key;
           this.no = no;
           this.description = description;
           this.location_Code = location_Code;
           this.quantity = quantity;
           this.expense_Proposal_Qty = expense_Proposal_Qty;
           this.quantity_Received = quantity_Received;
           this.unit_of_Measure_Code = unit_of_Measure_Code;
           this.unit_of_Measure = unit_of_Measure;
           this.budget_Dimension_1_Code = budget_Dimension_1_Code;
           this.budget_Dimension_2_Code = budget_Dimension_2_Code;
           this.budget_Dimension_3_Code = budget_Dimension_3_Code;
           this.budget_Dimension_4_Code = budget_Dimension_4_Code;
           this.promised_Receipt_Date = promised_Receipt_Date;
           this.planned_Receipt_Date = planned_Receipt_Date;
           this.expected_Receipt_Date = expected_Receipt_Date;
           this.order_Date = order_Date;
           this.lead_Time_Calculation = lead_Time_Calculation;
           this.finished = finished;
           this.appl_to_Item_Entry = appl_to_Item_Entry;
           this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
           this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
           this.DRF_Code = DRF_Code;
           this.shortcutDimCode_x005B_3_x005D_ = shortcutDimCode_x005B_3_x005D_;
           this.shortcutDimCode_x005B_4_x005D_ = shortcutDimCode_x005B_4_x005D_;
           this.shortcutDimCode_x005B_5_x005D_ = shortcutDimCode_x005B_5_x005D_;
           this.shortcutDimCode_x005B_6_x005D_ = shortcutDimCode_x005B_6_x005D_;
           this.shortcutDimCode_x005B_7_x005D_ = shortcutDimCode_x005B_7_x005D_;
           this.shortcutDimCode_x005B_8_x005D_ = shortcutDimCode_x005B_8_x005D_;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec;
    }


    /**
     * Gets the key value for this Purchase_Internal_Req_Line.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this Purchase_Internal_Req_Line.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the no value for this Purchase_Internal_Req_Line.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this Purchase_Internal_Req_Line.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the description value for this Purchase_Internal_Req_Line.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Purchase_Internal_Req_Line.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the location_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return location_Code
     */
    public java.lang.String getLocation_Code() {
        return location_Code;
    }


    /**
     * Sets the location_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param location_Code
     */
    public void setLocation_Code(java.lang.String location_Code) {
        this.location_Code = location_Code;
    }


    /**
     * Gets the quantity value for this Purchase_Internal_Req_Line.
     * 
     * @return quantity
     */
    public java.math.BigDecimal getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this Purchase_Internal_Req_Line.
     * 
     * @param quantity
     */
    public void setQuantity(java.math.BigDecimal quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the expense_Proposal_Qty value for this Purchase_Internal_Req_Line.
     * 
     * @return expense_Proposal_Qty
     */
    public java.math.BigDecimal getExpense_Proposal_Qty() {
        return expense_Proposal_Qty;
    }


    /**
     * Sets the expense_Proposal_Qty value for this Purchase_Internal_Req_Line.
     * 
     * @param expense_Proposal_Qty
     */
    public void setExpense_Proposal_Qty(java.math.BigDecimal expense_Proposal_Qty) {
        this.expense_Proposal_Qty = expense_Proposal_Qty;
    }


    /**
     * Gets the quantity_Received value for this Purchase_Internal_Req_Line.
     * 
     * @return quantity_Received
     */
    public java.math.BigDecimal getQuantity_Received() {
        return quantity_Received;
    }


    /**
     * Sets the quantity_Received value for this Purchase_Internal_Req_Line.
     * 
     * @param quantity_Received
     */
    public void setQuantity_Received(java.math.BigDecimal quantity_Received) {
        this.quantity_Received = quantity_Received;
    }


    /**
     * Gets the unit_of_Measure_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return unit_of_Measure_Code
     */
    public java.lang.String getUnit_of_Measure_Code() {
        return unit_of_Measure_Code;
    }


    /**
     * Sets the unit_of_Measure_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param unit_of_Measure_Code
     */
    public void setUnit_of_Measure_Code(java.lang.String unit_of_Measure_Code) {
        this.unit_of_Measure_Code = unit_of_Measure_Code;
    }


    /**
     * Gets the unit_of_Measure value for this Purchase_Internal_Req_Line.
     * 
     * @return unit_of_Measure
     */
    public java.lang.String getUnit_of_Measure() {
        return unit_of_Measure;
    }


    /**
     * Sets the unit_of_Measure value for this Purchase_Internal_Req_Line.
     * 
     * @param unit_of_Measure
     */
    public void setUnit_of_Measure(java.lang.String unit_of_Measure) {
        this.unit_of_Measure = unit_of_Measure;
    }


    /**
     * Gets the budget_Dimension_1_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return budget_Dimension_1_Code
     */
    public java.lang.String getBudget_Dimension_1_Code() {
        return budget_Dimension_1_Code;
    }


    /**
     * Sets the budget_Dimension_1_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param budget_Dimension_1_Code
     */
    public void setBudget_Dimension_1_Code(java.lang.String budget_Dimension_1_Code) {
        this.budget_Dimension_1_Code = budget_Dimension_1_Code;
    }


    /**
     * Gets the budget_Dimension_2_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return budget_Dimension_2_Code
     */
    public java.lang.String getBudget_Dimension_2_Code() {
        return budget_Dimension_2_Code;
    }


    /**
     * Sets the budget_Dimension_2_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param budget_Dimension_2_Code
     */
    public void setBudget_Dimension_2_Code(java.lang.String budget_Dimension_2_Code) {
        this.budget_Dimension_2_Code = budget_Dimension_2_Code;
    }


    /**
     * Gets the budget_Dimension_3_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return budget_Dimension_3_Code
     */
    public java.lang.String getBudget_Dimension_3_Code() {
        return budget_Dimension_3_Code;
    }


    /**
     * Sets the budget_Dimension_3_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param budget_Dimension_3_Code
     */
    public void setBudget_Dimension_3_Code(java.lang.String budget_Dimension_3_Code) {
        this.budget_Dimension_3_Code = budget_Dimension_3_Code;
    }


    /**
     * Gets the budget_Dimension_4_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return budget_Dimension_4_Code
     */
    public java.lang.String getBudget_Dimension_4_Code() {
        return budget_Dimension_4_Code;
    }


    /**
     * Sets the budget_Dimension_4_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param budget_Dimension_4_Code
     */
    public void setBudget_Dimension_4_Code(java.lang.String budget_Dimension_4_Code) {
        this.budget_Dimension_4_Code = budget_Dimension_4_Code;
    }


    /**
     * Gets the promised_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @return promised_Receipt_Date
     */
    public java.util.Date getPromised_Receipt_Date() {
        return promised_Receipt_Date;
    }


    /**
     * Sets the promised_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @param promised_Receipt_Date
     */
    public void setPromised_Receipt_Date(java.util.Date promised_Receipt_Date) {
        this.promised_Receipt_Date = promised_Receipt_Date;
    }


    /**
     * Gets the planned_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @return planned_Receipt_Date
     */
    public java.util.Date getPlanned_Receipt_Date() {
        return planned_Receipt_Date;
    }


    /**
     * Sets the planned_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @param planned_Receipt_Date
     */
    public void setPlanned_Receipt_Date(java.util.Date planned_Receipt_Date) {
        this.planned_Receipt_Date = planned_Receipt_Date;
    }


    /**
     * Gets the expected_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @return expected_Receipt_Date
     */
    public java.util.Date getExpected_Receipt_Date() {
        return expected_Receipt_Date;
    }


    /**
     * Sets the expected_Receipt_Date value for this Purchase_Internal_Req_Line.
     * 
     * @param expected_Receipt_Date
     */
    public void setExpected_Receipt_Date(java.util.Date expected_Receipt_Date) {
        this.expected_Receipt_Date = expected_Receipt_Date;
    }


    /**
     * Gets the order_Date value for this Purchase_Internal_Req_Line.
     * 
     * @return order_Date
     */
    public java.util.Date getOrder_Date() {
        return order_Date;
    }


    /**
     * Sets the order_Date value for this Purchase_Internal_Req_Line.
     * 
     * @param order_Date
     */
    public void setOrder_Date(java.util.Date order_Date) {
        this.order_Date = order_Date;
    }


    /**
     * Gets the lead_Time_Calculation value for this Purchase_Internal_Req_Line.
     * 
     * @return lead_Time_Calculation
     */
    public java.lang.String getLead_Time_Calculation() {
        return lead_Time_Calculation;
    }


    /**
     * Sets the lead_Time_Calculation value for this Purchase_Internal_Req_Line.
     * 
     * @param lead_Time_Calculation
     */
    public void setLead_Time_Calculation(java.lang.String lead_Time_Calculation) {
        this.lead_Time_Calculation = lead_Time_Calculation;
    }


    /**
     * Gets the finished value for this Purchase_Internal_Req_Line.
     * 
     * @return finished
     */
    public java.lang.Boolean getFinished() {
        return finished;
    }


    /**
     * Sets the finished value for this Purchase_Internal_Req_Line.
     * 
     * @param finished
     */
    public void setFinished(java.lang.Boolean finished) {
        this.finished = finished;
    }


    /**
     * Gets the appl_to_Item_Entry value for this Purchase_Internal_Req_Line.
     * 
     * @return appl_to_Item_Entry
     */
    public java.lang.Integer getAppl_to_Item_Entry() {
        return appl_to_Item_Entry;
    }


    /**
     * Sets the appl_to_Item_Entry value for this Purchase_Internal_Req_Line.
     * 
     * @param appl_to_Item_Entry
     */
    public void setAppl_to_Item_Entry(java.lang.Integer appl_to_Item_Entry) {
        this.appl_to_Item_Entry = appl_to_Item_Entry;
    }


    /**
     * Gets the shortcut_Dimension_1_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcut_Dimension_1_Code
     */
    public java.lang.String getShortcut_Dimension_1_Code() {
        return shortcut_Dimension_1_Code;
    }


    /**
     * Sets the shortcut_Dimension_1_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcut_Dimension_1_Code
     */
    public void setShortcut_Dimension_1_Code(java.lang.String shortcut_Dimension_1_Code) {
        this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
    }


    /**
     * Gets the shortcut_Dimension_2_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcut_Dimension_2_Code
     */
    public java.lang.String getShortcut_Dimension_2_Code() {
        return shortcut_Dimension_2_Code;
    }


    /**
     * Sets the shortcut_Dimension_2_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcut_Dimension_2_Code
     */
    public void setShortcut_Dimension_2_Code(java.lang.String shortcut_Dimension_2_Code) {
        this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
    }


    /**
     * Gets the DRF_Code value for this Purchase_Internal_Req_Line.
     * 
     * @return DRF_Code
     */
    public java.lang.String getDRF_Code() {
        return DRF_Code;
    }


    /**
     * Sets the DRF_Code value for this Purchase_Internal_Req_Line.
     * 
     * @param DRF_Code
     */
    public void setDRF_Code(java.lang.String DRF_Code) {
        this.DRF_Code = DRF_Code;
    }


    /**
     * Gets the shortcutDimCode_x005B_3_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_3_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_3_x005D_() {
        return shortcutDimCode_x005B_3_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_3_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_3_x005D_
     */
    public void setShortcutDimCode_x005B_3_x005D_(java.lang.String shortcutDimCode_x005B_3_x005D_) {
        this.shortcutDimCode_x005B_3_x005D_ = shortcutDimCode_x005B_3_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_4_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_4_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_4_x005D_() {
        return shortcutDimCode_x005B_4_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_4_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_4_x005D_
     */
    public void setShortcutDimCode_x005B_4_x005D_(java.lang.String shortcutDimCode_x005B_4_x005D_) {
        this.shortcutDimCode_x005B_4_x005D_ = shortcutDimCode_x005B_4_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_5_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_5_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_5_x005D_() {
        return shortcutDimCode_x005B_5_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_5_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_5_x005D_
     */
    public void setShortcutDimCode_x005B_5_x005D_(java.lang.String shortcutDimCode_x005B_5_x005D_) {
        this.shortcutDimCode_x005B_5_x005D_ = shortcutDimCode_x005B_5_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_6_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_6_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_6_x005D_() {
        return shortcutDimCode_x005B_6_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_6_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_6_x005D_
     */
    public void setShortcutDimCode_x005B_6_x005D_(java.lang.String shortcutDimCode_x005B_6_x005D_) {
        this.shortcutDimCode_x005B_6_x005D_ = shortcutDimCode_x005B_6_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_7_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_7_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_7_x005D_() {
        return shortcutDimCode_x005B_7_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_7_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_7_x005D_
     */
    public void setShortcutDimCode_x005B_7_x005D_(java.lang.String shortcutDimCode_x005B_7_x005D_) {
        this.shortcutDimCode_x005B_7_x005D_ = shortcutDimCode_x005B_7_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_8_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @return shortcutDimCode_x005B_8_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_8_x005D_() {
        return shortcutDimCode_x005B_8_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_8_x005D_ value for this Purchase_Internal_Req_Line.
     * 
     * @param shortcutDimCode_x005B_8_x005D_
     */
    public void setShortcutDimCode_x005B_8_x005D_(java.lang.String shortcutDimCode_x005B_8_x005D_) {
        this.shortcutDimCode_x005B_8_x005D_ = shortcutDimCode_x005B_8_x005D_;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec value for this Purchase_Internal_Req_Line.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Purchase_Internal_Req_Line)) return false;
        Purchase_Internal_Req_Line other = (Purchase_Internal_Req_Line) obj;
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
            ((this.location_Code==null && other.getLocation_Code()==null) || 
             (this.location_Code!=null &&
              this.location_Code.equals(other.getLocation_Code()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.expense_Proposal_Qty==null && other.getExpense_Proposal_Qty()==null) || 
             (this.expense_Proposal_Qty!=null &&
              this.expense_Proposal_Qty.equals(other.getExpense_Proposal_Qty()))) &&
            ((this.quantity_Received==null && other.getQuantity_Received()==null) || 
             (this.quantity_Received!=null &&
              this.quantity_Received.equals(other.getQuantity_Received()))) &&
            ((this.unit_of_Measure_Code==null && other.getUnit_of_Measure_Code()==null) || 
             (this.unit_of_Measure_Code!=null &&
              this.unit_of_Measure_Code.equals(other.getUnit_of_Measure_Code()))) &&
            ((this.unit_of_Measure==null && other.getUnit_of_Measure()==null) || 
             (this.unit_of_Measure!=null &&
              this.unit_of_Measure.equals(other.getUnit_of_Measure()))) &&
            ((this.budget_Dimension_1_Code==null && other.getBudget_Dimension_1_Code()==null) || 
             (this.budget_Dimension_1_Code!=null &&
              this.budget_Dimension_1_Code.equals(other.getBudget_Dimension_1_Code()))) &&
            ((this.budget_Dimension_2_Code==null && other.getBudget_Dimension_2_Code()==null) || 
             (this.budget_Dimension_2_Code!=null &&
              this.budget_Dimension_2_Code.equals(other.getBudget_Dimension_2_Code()))) &&
            ((this.budget_Dimension_3_Code==null && other.getBudget_Dimension_3_Code()==null) || 
             (this.budget_Dimension_3_Code!=null &&
              this.budget_Dimension_3_Code.equals(other.getBudget_Dimension_3_Code()))) &&
            ((this.budget_Dimension_4_Code==null && other.getBudget_Dimension_4_Code()==null) || 
             (this.budget_Dimension_4_Code!=null &&
              this.budget_Dimension_4_Code.equals(other.getBudget_Dimension_4_Code()))) &&
            ((this.promised_Receipt_Date==null && other.getPromised_Receipt_Date()==null) || 
             (this.promised_Receipt_Date!=null &&
              this.promised_Receipt_Date.equals(other.getPromised_Receipt_Date()))) &&
            ((this.planned_Receipt_Date==null && other.getPlanned_Receipt_Date()==null) || 
             (this.planned_Receipt_Date!=null &&
              this.planned_Receipt_Date.equals(other.getPlanned_Receipt_Date()))) &&
            ((this.expected_Receipt_Date==null && other.getExpected_Receipt_Date()==null) || 
             (this.expected_Receipt_Date!=null &&
              this.expected_Receipt_Date.equals(other.getExpected_Receipt_Date()))) &&
            ((this.order_Date==null && other.getOrder_Date()==null) || 
             (this.order_Date!=null &&
              this.order_Date.equals(other.getOrder_Date()))) &&
            ((this.lead_Time_Calculation==null && other.getLead_Time_Calculation()==null) || 
             (this.lead_Time_Calculation!=null &&
              this.lead_Time_Calculation.equals(other.getLead_Time_Calculation()))) &&
            ((this.finished==null && other.getFinished()==null) || 
             (this.finished!=null &&
              this.finished.equals(other.getFinished()))) &&
            ((this.appl_to_Item_Entry==null && other.getAppl_to_Item_Entry()==null) || 
             (this.appl_to_Item_Entry!=null &&
              this.appl_to_Item_Entry.equals(other.getAppl_to_Item_Entry()))) &&
            ((this.shortcut_Dimension_1_Code==null && other.getShortcut_Dimension_1_Code()==null) || 
             (this.shortcut_Dimension_1_Code!=null &&
              this.shortcut_Dimension_1_Code.equals(other.getShortcut_Dimension_1_Code()))) &&
            ((this.shortcut_Dimension_2_Code==null && other.getShortcut_Dimension_2_Code()==null) || 
             (this.shortcut_Dimension_2_Code!=null &&
              this.shortcut_Dimension_2_Code.equals(other.getShortcut_Dimension_2_Code()))) &&
            ((this.DRF_Code==null && other.getDRF_Code()==null) || 
             (this.DRF_Code!=null &&
              this.DRF_Code.equals(other.getDRF_Code()))) &&
            ((this.shortcutDimCode_x005B_3_x005D_==null && other.getShortcutDimCode_x005B_3_x005D_()==null) || 
             (this.shortcutDimCode_x005B_3_x005D_!=null &&
              this.shortcutDimCode_x005B_3_x005D_.equals(other.getShortcutDimCode_x005B_3_x005D_()))) &&
            ((this.shortcutDimCode_x005B_4_x005D_==null && other.getShortcutDimCode_x005B_4_x005D_()==null) || 
             (this.shortcutDimCode_x005B_4_x005D_!=null &&
              this.shortcutDimCode_x005B_4_x005D_.equals(other.getShortcutDimCode_x005B_4_x005D_()))) &&
            ((this.shortcutDimCode_x005B_5_x005D_==null && other.getShortcutDimCode_x005B_5_x005D_()==null) || 
             (this.shortcutDimCode_x005B_5_x005D_!=null &&
              this.shortcutDimCode_x005B_5_x005D_.equals(other.getShortcutDimCode_x005B_5_x005D_()))) &&
            ((this.shortcutDimCode_x005B_6_x005D_==null && other.getShortcutDimCode_x005B_6_x005D_()==null) || 
             (this.shortcutDimCode_x005B_6_x005D_!=null &&
              this.shortcutDimCode_x005B_6_x005D_.equals(other.getShortcutDimCode_x005B_6_x005D_()))) &&
            ((this.shortcutDimCode_x005B_7_x005D_==null && other.getShortcutDimCode_x005B_7_x005D_()==null) || 
             (this.shortcutDimCode_x005B_7_x005D_!=null &&
              this.shortcutDimCode_x005B_7_x005D_.equals(other.getShortcutDimCode_x005B_7_x005D_()))) &&
            ((this.shortcutDimCode_x005B_8_x005D_==null && other.getShortcutDimCode_x005B_8_x005D_()==null) || 
             (this.shortcutDimCode_x005B_8_x005D_!=null &&
              this.shortcutDimCode_x005B_8_x005D_.equals(other.getShortcutDimCode_x005B_8_x005D_()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec())));
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
        if (getLocation_Code() != null) {
            _hashCode += getLocation_Code().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getExpense_Proposal_Qty() != null) {
            _hashCode += getExpense_Proposal_Qty().hashCode();
        }
        if (getQuantity_Received() != null) {
            _hashCode += getQuantity_Received().hashCode();
        }
        if (getUnit_of_Measure_Code() != null) {
            _hashCode += getUnit_of_Measure_Code().hashCode();
        }
        if (getUnit_of_Measure() != null) {
            _hashCode += getUnit_of_Measure().hashCode();
        }
        if (getBudget_Dimension_1_Code() != null) {
            _hashCode += getBudget_Dimension_1_Code().hashCode();
        }
        if (getBudget_Dimension_2_Code() != null) {
            _hashCode += getBudget_Dimension_2_Code().hashCode();
        }
        if (getBudget_Dimension_3_Code() != null) {
            _hashCode += getBudget_Dimension_3_Code().hashCode();
        }
        if (getBudget_Dimension_4_Code() != null) {
            _hashCode += getBudget_Dimension_4_Code().hashCode();
        }
        if (getPromised_Receipt_Date() != null) {
            _hashCode += getPromised_Receipt_Date().hashCode();
        }
        if (getPlanned_Receipt_Date() != null) {
            _hashCode += getPlanned_Receipt_Date().hashCode();
        }
        if (getExpected_Receipt_Date() != null) {
            _hashCode += getExpected_Receipt_Date().hashCode();
        }
        if (getOrder_Date() != null) {
            _hashCode += getOrder_Date().hashCode();
        }
        if (getLead_Time_Calculation() != null) {
            _hashCode += getLead_Time_Calculation().hashCode();
        }
        if (getFinished() != null) {
            _hashCode += getFinished().hashCode();
        }
        if (getAppl_to_Item_Entry() != null) {
            _hashCode += getAppl_to_Item_Entry().hashCode();
        }
        if (getShortcut_Dimension_1_Code() != null) {
            _hashCode += getShortcut_Dimension_1_Code().hashCode();
        }
        if (getShortcut_Dimension_2_Code() != null) {
            _hashCode += getShortcut_Dimension_2_Code().hashCode();
        }
        if (getDRF_Code() != null) {
            _hashCode += getDRF_Code().hashCode();
        }
        if (getShortcutDimCode_x005B_3_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_3_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_4_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_4_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_5_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_5_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_6_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_6_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_7_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_7_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_8_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_8_x005D_().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Purchase_Internal_Req_Line.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Purchase_Internal_Req_Line"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Location_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expense_Proposal_Qty");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Expense_Proposal_Qty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity_Received");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Quantity_Received"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_of_Measure_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Unit_of_Measure_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_of_Measure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Unit_of_Measure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_1_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Budget_Dimension_1_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_2_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Budget_Dimension_2_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_3_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Budget_Dimension_3_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_4_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Budget_Dimension_4_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("promised_Receipt_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Promised_Receipt_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("planned_Receipt_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Planned_Receipt_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expected_Receipt_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Expected_Receipt_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order_Date");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Order_Date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lead_Time_Calculation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Lead_Time_Calculation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finished");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Finished"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appl_to_Item_Entry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Appl_to_Item_Entry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_1_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Shortcut_Dimension_1_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_2_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "Shortcut_Dimension_2_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DRF_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "DRF_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_3_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_3_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_4_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_4_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_5_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_5_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_6_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_6_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_7_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_7_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_8_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "ShortcutDimCode_x005B_8_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcAvailability_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchasePrices_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternacompra", "STRSUBSTNO__x0027__Percent1__x0027__x002C_PurchInfoPaneMgt_CalcNoOfPurchLineDisc_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
