/**
 * Sales_Internal_Req_subform.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class Sales_Internal_Req_subform  implements java.io.Serializable {
    private java.lang.String key;

    private microsoft_dynamics_schemas.Type type;

    private microsoft_dynamics_schemas.Sub_Type_Item sub_Type_Item;

    private java.lang.String no;

    private java.lang.String description;

    private java.lang.String description_2;

    private java.math.BigDecimal quantity;

    private java.lang.Boolean no_More_Quantities;

    private java.lang.String location_Code;

    private java.lang.Boolean not_in_Vat_Report;

    private java.lang.String unit_of_Measure_Code;

    private java.lang.String unit_of_Measure;

    private java.lang.Boolean use_Duplication_List;

    private java.lang.Integer appl_from_Item_Entry;

    private java.lang.Integer appl_to_Item_Entry;

    private java.lang.String shortcut_Dimension_1_Code;

    private java.lang.String shortcut_Dimension_2_Code;

    private java.lang.String shortcutDimCode_x005B_3_x005D_;

    private java.lang.String shortcutDimCode_x005B_4_x005D_;

    private java.lang.String DRF_Code;

    private java.lang.String budget_Dimension_1_Code;

    private java.lang.String budget_Dimension_2_Code;

    private java.lang.String budget_Dimension_3_Code;

    private java.lang.String budget_Dimension_4_Code;

    private java.lang.String treasury_Operation;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec;

    private java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec;

    public Sales_Internal_Req_subform() {
    }

    public Sales_Internal_Req_subform(
           java.lang.String key,
           microsoft_dynamics_schemas.Type type,
           microsoft_dynamics_schemas.Sub_Type_Item sub_Type_Item,
           java.lang.String no,
           java.lang.String description,
           java.lang.String description_2,
           java.math.BigDecimal quantity,
           java.lang.Boolean no_More_Quantities,
           java.lang.String location_Code,
           java.lang.Boolean not_in_Vat_Report,
           java.lang.String unit_of_Measure_Code,
           java.lang.String unit_of_Measure,
           java.lang.Boolean use_Duplication_List,
           java.lang.Integer appl_from_Item_Entry,
           java.lang.Integer appl_to_Item_Entry,
           java.lang.String shortcut_Dimension_1_Code,
           java.lang.String shortcut_Dimension_2_Code,
           java.lang.String shortcutDimCode_x005B_3_x005D_,
           java.lang.String shortcutDimCode_x005B_4_x005D_,
           java.lang.String DRF_Code,
           java.lang.String budget_Dimension_1_Code,
           java.lang.String budget_Dimension_2_Code,
           java.lang.String budget_Dimension_3_Code,
           java.lang.String budget_Dimension_4_Code,
           java.lang.String treasury_Operation,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec,
           java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec) {
           this.key = key;
           this.type = type;
           this.sub_Type_Item = sub_Type_Item;
           this.no = no;
           this.description = description;
           this.description_2 = description_2;
           this.quantity = quantity;
           this.no_More_Quantities = no_More_Quantities;
           this.location_Code = location_Code;
           this.not_in_Vat_Report = not_in_Vat_Report;
           this.unit_of_Measure_Code = unit_of_Measure_Code;
           this.unit_of_Measure = unit_of_Measure;
           this.use_Duplication_List = use_Duplication_List;
           this.appl_from_Item_Entry = appl_from_Item_Entry;
           this.appl_to_Item_Entry = appl_to_Item_Entry;
           this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
           this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
           this.shortcutDimCode_x005B_3_x005D_ = shortcutDimCode_x005B_3_x005D_;
           this.shortcutDimCode_x005B_4_x005D_ = shortcutDimCode_x005B_4_x005D_;
           this.DRF_Code = DRF_Code;
           this.budget_Dimension_1_Code = budget_Dimension_1_Code;
           this.budget_Dimension_2_Code = budget_Dimension_2_Code;
           this.budget_Dimension_3_Code = budget_Dimension_3_Code;
           this.budget_Dimension_4_Code = budget_Dimension_4_Code;
           this.treasury_Operation = treasury_Operation;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec;
           this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec;
    }


    /**
     * Gets the key value for this Sales_Internal_Req_subform.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this Sales_Internal_Req_subform.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the type value for this Sales_Internal_Req_subform.
     * 
     * @return type
     */
    public microsoft_dynamics_schemas.Type getType() {
        return type;
    }


    /**
     * Sets the type value for this Sales_Internal_Req_subform.
     * 
     * @param type
     */
    public void setType(microsoft_dynamics_schemas.Type type) {
        this.type = type;
    }


    /**
     * Gets the sub_Type_Item value for this Sales_Internal_Req_subform.
     * 
     * @return sub_Type_Item
     */
    public microsoft_dynamics_schemas.Sub_Type_Item getSub_Type_Item() {
        return sub_Type_Item;
    }


    /**
     * Sets the sub_Type_Item value for this Sales_Internal_Req_subform.
     * 
     * @param sub_Type_Item
     */
    public void setSub_Type_Item(microsoft_dynamics_schemas.Sub_Type_Item sub_Type_Item) {
        this.sub_Type_Item = sub_Type_Item;
    }


    /**
     * Gets the no value for this Sales_Internal_Req_subform.
     * 
     * @return no
     */
    public java.lang.String getNo() {
        return no;
    }


    /**
     * Sets the no value for this Sales_Internal_Req_subform.
     * 
     * @param no
     */
    public void setNo(java.lang.String no) {
        this.no = no;
    }


    /**
     * Gets the description value for this Sales_Internal_Req_subform.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Sales_Internal_Req_subform.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the description_2 value for this Sales_Internal_Req_subform.
     * 
     * @return description_2
     */
    public java.lang.String getDescription_2() {
        return description_2;
    }


    /**
     * Sets the description_2 value for this Sales_Internal_Req_subform.
     * 
     * @param description_2
     */
    public void setDescription_2(java.lang.String description_2) {
        this.description_2 = description_2;
    }


    /**
     * Gets the quantity value for this Sales_Internal_Req_subform.
     * 
     * @return quantity
     */
    public java.math.BigDecimal getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this Sales_Internal_Req_subform.
     * 
     * @param quantity
     */
    public void setQuantity(java.math.BigDecimal quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the no_More_Quantities value for this Sales_Internal_Req_subform.
     * 
     * @return no_More_Quantities
     */
    public java.lang.Boolean getNo_More_Quantities() {
        return no_More_Quantities;
    }


    /**
     * Sets the no_More_Quantities value for this Sales_Internal_Req_subform.
     * 
     * @param no_More_Quantities
     */
    public void setNo_More_Quantities(java.lang.Boolean no_More_Quantities) {
        this.no_More_Quantities = no_More_Quantities;
    }


    /**
     * Gets the location_Code value for this Sales_Internal_Req_subform.
     * 
     * @return location_Code
     */
    public java.lang.String getLocation_Code() {
        return location_Code;
    }


    /**
     * Sets the location_Code value for this Sales_Internal_Req_subform.
     * 
     * @param location_Code
     */
    public void setLocation_Code(java.lang.String location_Code) {
        this.location_Code = location_Code;
    }


    /**
     * Gets the not_in_Vat_Report value for this Sales_Internal_Req_subform.
     * 
     * @return not_in_Vat_Report
     */
    public java.lang.Boolean getNot_in_Vat_Report() {
        return not_in_Vat_Report;
    }


    /**
     * Sets the not_in_Vat_Report value for this Sales_Internal_Req_subform.
     * 
     * @param not_in_Vat_Report
     */
    public void setNot_in_Vat_Report(java.lang.Boolean not_in_Vat_Report) {
        this.not_in_Vat_Report = not_in_Vat_Report;
    }


    /**
     * Gets the unit_of_Measure_Code value for this Sales_Internal_Req_subform.
     * 
     * @return unit_of_Measure_Code
     */
    public java.lang.String getUnit_of_Measure_Code() {
        return unit_of_Measure_Code;
    }


    /**
     * Sets the unit_of_Measure_Code value for this Sales_Internal_Req_subform.
     * 
     * @param unit_of_Measure_Code
     */
    public void setUnit_of_Measure_Code(java.lang.String unit_of_Measure_Code) {
        this.unit_of_Measure_Code = unit_of_Measure_Code;
    }


    /**
     * Gets the unit_of_Measure value for this Sales_Internal_Req_subform.
     * 
     * @return unit_of_Measure
     */
    public java.lang.String getUnit_of_Measure() {
        return unit_of_Measure;
    }


    /**
     * Sets the unit_of_Measure value for this Sales_Internal_Req_subform.
     * 
     * @param unit_of_Measure
     */
    public void setUnit_of_Measure(java.lang.String unit_of_Measure) {
        this.unit_of_Measure = unit_of_Measure;
    }


    /**
     * Gets the use_Duplication_List value for this Sales_Internal_Req_subform.
     * 
     * @return use_Duplication_List
     */
    public java.lang.Boolean getUse_Duplication_List() {
        return use_Duplication_List;
    }


    /**
     * Sets the use_Duplication_List value for this Sales_Internal_Req_subform.
     * 
     * @param use_Duplication_List
     */
    public void setUse_Duplication_List(java.lang.Boolean use_Duplication_List) {
        this.use_Duplication_List = use_Duplication_List;
    }


    /**
     * Gets the appl_from_Item_Entry value for this Sales_Internal_Req_subform.
     * 
     * @return appl_from_Item_Entry
     */
    public java.lang.Integer getAppl_from_Item_Entry() {
        return appl_from_Item_Entry;
    }


    /**
     * Sets the appl_from_Item_Entry value for this Sales_Internal_Req_subform.
     * 
     * @param appl_from_Item_Entry
     */
    public void setAppl_from_Item_Entry(java.lang.Integer appl_from_Item_Entry) {
        this.appl_from_Item_Entry = appl_from_Item_Entry;
    }


    /**
     * Gets the appl_to_Item_Entry value for this Sales_Internal_Req_subform.
     * 
     * @return appl_to_Item_Entry
     */
    public java.lang.Integer getAppl_to_Item_Entry() {
        return appl_to_Item_Entry;
    }


    /**
     * Sets the appl_to_Item_Entry value for this Sales_Internal_Req_subform.
     * 
     * @param appl_to_Item_Entry
     */
    public void setAppl_to_Item_Entry(java.lang.Integer appl_to_Item_Entry) {
        this.appl_to_Item_Entry = appl_to_Item_Entry;
    }


    /**
     * Gets the shortcut_Dimension_1_Code value for this Sales_Internal_Req_subform.
     * 
     * @return shortcut_Dimension_1_Code
     */
    public java.lang.String getShortcut_Dimension_1_Code() {
        return shortcut_Dimension_1_Code;
    }


    /**
     * Sets the shortcut_Dimension_1_Code value for this Sales_Internal_Req_subform.
     * 
     * @param shortcut_Dimension_1_Code
     */
    public void setShortcut_Dimension_1_Code(java.lang.String shortcut_Dimension_1_Code) {
        this.shortcut_Dimension_1_Code = shortcut_Dimension_1_Code;
    }


    /**
     * Gets the shortcut_Dimension_2_Code value for this Sales_Internal_Req_subform.
     * 
     * @return shortcut_Dimension_2_Code
     */
    public java.lang.String getShortcut_Dimension_2_Code() {
        return shortcut_Dimension_2_Code;
    }


    /**
     * Sets the shortcut_Dimension_2_Code value for this Sales_Internal_Req_subform.
     * 
     * @param shortcut_Dimension_2_Code
     */
    public void setShortcut_Dimension_2_Code(java.lang.String shortcut_Dimension_2_Code) {
        this.shortcut_Dimension_2_Code = shortcut_Dimension_2_Code;
    }


    /**
     * Gets the shortcutDimCode_x005B_3_x005D_ value for this Sales_Internal_Req_subform.
     * 
     * @return shortcutDimCode_x005B_3_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_3_x005D_() {
        return shortcutDimCode_x005B_3_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_3_x005D_ value for this Sales_Internal_Req_subform.
     * 
     * @param shortcutDimCode_x005B_3_x005D_
     */
    public void setShortcutDimCode_x005B_3_x005D_(java.lang.String shortcutDimCode_x005B_3_x005D_) {
        this.shortcutDimCode_x005B_3_x005D_ = shortcutDimCode_x005B_3_x005D_;
    }


    /**
     * Gets the shortcutDimCode_x005B_4_x005D_ value for this Sales_Internal_Req_subform.
     * 
     * @return shortcutDimCode_x005B_4_x005D_
     */
    public java.lang.String getShortcutDimCode_x005B_4_x005D_() {
        return shortcutDimCode_x005B_4_x005D_;
    }


    /**
     * Sets the shortcutDimCode_x005B_4_x005D_ value for this Sales_Internal_Req_subform.
     * 
     * @param shortcutDimCode_x005B_4_x005D_
     */
    public void setShortcutDimCode_x005B_4_x005D_(java.lang.String shortcutDimCode_x005B_4_x005D_) {
        this.shortcutDimCode_x005B_4_x005D_ = shortcutDimCode_x005B_4_x005D_;
    }


    /**
     * Gets the DRF_Code value for this Sales_Internal_Req_subform.
     * 
     * @return DRF_Code
     */
    public java.lang.String getDRF_Code() {
        return DRF_Code;
    }


    /**
     * Sets the DRF_Code value for this Sales_Internal_Req_subform.
     * 
     * @param DRF_Code
     */
    public void setDRF_Code(java.lang.String DRF_Code) {
        this.DRF_Code = DRF_Code;
    }


    /**
     * Gets the budget_Dimension_1_Code value for this Sales_Internal_Req_subform.
     * 
     * @return budget_Dimension_1_Code
     */
    public java.lang.String getBudget_Dimension_1_Code() {
        return budget_Dimension_1_Code;
    }


    /**
     * Sets the budget_Dimension_1_Code value for this Sales_Internal_Req_subform.
     * 
     * @param budget_Dimension_1_Code
     */
    public void setBudget_Dimension_1_Code(java.lang.String budget_Dimension_1_Code) {
        this.budget_Dimension_1_Code = budget_Dimension_1_Code;
    }


    /**
     * Gets the budget_Dimension_2_Code value for this Sales_Internal_Req_subform.
     * 
     * @return budget_Dimension_2_Code
     */
    public java.lang.String getBudget_Dimension_2_Code() {
        return budget_Dimension_2_Code;
    }


    /**
     * Sets the budget_Dimension_2_Code value for this Sales_Internal_Req_subform.
     * 
     * @param budget_Dimension_2_Code
     */
    public void setBudget_Dimension_2_Code(java.lang.String budget_Dimension_2_Code) {
        this.budget_Dimension_2_Code = budget_Dimension_2_Code;
    }


    /**
     * Gets the budget_Dimension_3_Code value for this Sales_Internal_Req_subform.
     * 
     * @return budget_Dimension_3_Code
     */
    public java.lang.String getBudget_Dimension_3_Code() {
        return budget_Dimension_3_Code;
    }


    /**
     * Sets the budget_Dimension_3_Code value for this Sales_Internal_Req_subform.
     * 
     * @param budget_Dimension_3_Code
     */
    public void setBudget_Dimension_3_Code(java.lang.String budget_Dimension_3_Code) {
        this.budget_Dimension_3_Code = budget_Dimension_3_Code;
    }


    /**
     * Gets the budget_Dimension_4_Code value for this Sales_Internal_Req_subform.
     * 
     * @return budget_Dimension_4_Code
     */
    public java.lang.String getBudget_Dimension_4_Code() {
        return budget_Dimension_4_Code;
    }


    /**
     * Sets the budget_Dimension_4_Code value for this Sales_Internal_Req_subform.
     * 
     * @param budget_Dimension_4_Code
     */
    public void setBudget_Dimension_4_Code(java.lang.String budget_Dimension_4_Code) {
        this.budget_Dimension_4_Code = budget_Dimension_4_Code;
    }


    /**
     * Gets the treasury_Operation value for this Sales_Internal_Req_subform.
     * 
     * @return treasury_Operation
     */
    public java.lang.String getTreasury_Operation() {
        return treasury_Operation;
    }


    /**
     * Sets the treasury_Operation value for this Sales_Internal_Req_subform.
     * 
     * @param treasury_Operation
     */
    public void setTreasury_Operation(java.lang.String treasury_Operation) {
        this.treasury_Operation = treasury_Operation;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec value for this Sales_Internal_Req_subform.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec value for this Sales_Internal_Req_subform.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec value for this Sales_Internal_Req_subform.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec value for this Sales_Internal_Req_subform.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec value for this Sales_Internal_Req_subform.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec value for this Sales_Internal_Req_subform.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec;
    }


    /**
     * Gets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec value for this Sales_Internal_Req_subform.
     * 
     * @return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec
     */
    public java.lang.String getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec() {
        return STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec;
    }


    /**
     * Sets the STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec value for this Sales_Internal_Req_subform.
     * 
     * @param STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec
     */
    public void setSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec(java.lang.String STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec) {
        this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec = STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Sales_Internal_Req_subform)) return false;
        Sales_Internal_Req_subform other = (Sales_Internal_Req_subform) obj;
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
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.sub_Type_Item==null && other.getSub_Type_Item()==null) || 
             (this.sub_Type_Item!=null &&
              this.sub_Type_Item.equals(other.getSub_Type_Item()))) &&
            ((this.no==null && other.getNo()==null) || 
             (this.no!=null &&
              this.no.equals(other.getNo()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.description_2==null && other.getDescription_2()==null) || 
             (this.description_2!=null &&
              this.description_2.equals(other.getDescription_2()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.no_More_Quantities==null && other.getNo_More_Quantities()==null) || 
             (this.no_More_Quantities!=null &&
              this.no_More_Quantities.equals(other.getNo_More_Quantities()))) &&
            ((this.location_Code==null && other.getLocation_Code()==null) || 
             (this.location_Code!=null &&
              this.location_Code.equals(other.getLocation_Code()))) &&
            ((this.not_in_Vat_Report==null && other.getNot_in_Vat_Report()==null) || 
             (this.not_in_Vat_Report!=null &&
              this.not_in_Vat_Report.equals(other.getNot_in_Vat_Report()))) &&
            ((this.unit_of_Measure_Code==null && other.getUnit_of_Measure_Code()==null) || 
             (this.unit_of_Measure_Code!=null &&
              this.unit_of_Measure_Code.equals(other.getUnit_of_Measure_Code()))) &&
            ((this.unit_of_Measure==null && other.getUnit_of_Measure()==null) || 
             (this.unit_of_Measure!=null &&
              this.unit_of_Measure.equals(other.getUnit_of_Measure()))) &&
            ((this.use_Duplication_List==null && other.getUse_Duplication_List()==null) || 
             (this.use_Duplication_List!=null &&
              this.use_Duplication_List.equals(other.getUse_Duplication_List()))) &&
            ((this.appl_from_Item_Entry==null && other.getAppl_from_Item_Entry()==null) || 
             (this.appl_from_Item_Entry!=null &&
              this.appl_from_Item_Entry.equals(other.getAppl_from_Item_Entry()))) &&
            ((this.appl_to_Item_Entry==null && other.getAppl_to_Item_Entry()==null) || 
             (this.appl_to_Item_Entry!=null &&
              this.appl_to_Item_Entry.equals(other.getAppl_to_Item_Entry()))) &&
            ((this.shortcut_Dimension_1_Code==null && other.getShortcut_Dimension_1_Code()==null) || 
             (this.shortcut_Dimension_1_Code!=null &&
              this.shortcut_Dimension_1_Code.equals(other.getShortcut_Dimension_1_Code()))) &&
            ((this.shortcut_Dimension_2_Code==null && other.getShortcut_Dimension_2_Code()==null) || 
             (this.shortcut_Dimension_2_Code!=null &&
              this.shortcut_Dimension_2_Code.equals(other.getShortcut_Dimension_2_Code()))) &&
            ((this.shortcutDimCode_x005B_3_x005D_==null && other.getShortcutDimCode_x005B_3_x005D_()==null) || 
             (this.shortcutDimCode_x005B_3_x005D_!=null &&
              this.shortcutDimCode_x005B_3_x005D_.equals(other.getShortcutDimCode_x005B_3_x005D_()))) &&
            ((this.shortcutDimCode_x005B_4_x005D_==null && other.getShortcutDimCode_x005B_4_x005D_()==null) || 
             (this.shortcutDimCode_x005B_4_x005D_!=null &&
              this.shortcutDimCode_x005B_4_x005D_.equals(other.getShortcutDimCode_x005B_4_x005D_()))) &&
            ((this.DRF_Code==null && other.getDRF_Code()==null) || 
             (this.DRF_Code!=null &&
              this.DRF_Code.equals(other.getDRF_Code()))) &&
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
            ((this.treasury_Operation==null && other.getTreasury_Operation()==null) || 
             (this.treasury_Operation!=null &&
              this.treasury_Operation.equals(other.getTreasury_Operation()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec()))) &&
            ((this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec==null && other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec()==null) || 
             (this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec!=null &&
              this.STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec.equals(other.getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getSub_Type_Item() != null) {
            _hashCode += getSub_Type_Item().hashCode();
        }
        if (getNo() != null) {
            _hashCode += getNo().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getDescription_2() != null) {
            _hashCode += getDescription_2().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getNo_More_Quantities() != null) {
            _hashCode += getNo_More_Quantities().hashCode();
        }
        if (getLocation_Code() != null) {
            _hashCode += getLocation_Code().hashCode();
        }
        if (getNot_in_Vat_Report() != null) {
            _hashCode += getNot_in_Vat_Report().hashCode();
        }
        if (getUnit_of_Measure_Code() != null) {
            _hashCode += getUnit_of_Measure_Code().hashCode();
        }
        if (getUnit_of_Measure() != null) {
            _hashCode += getUnit_of_Measure().hashCode();
        }
        if (getUse_Duplication_List() != null) {
            _hashCode += getUse_Duplication_List().hashCode();
        }
        if (getAppl_from_Item_Entry() != null) {
            _hashCode += getAppl_from_Item_Entry().hashCode();
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
        if (getShortcutDimCode_x005B_3_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_3_x005D_().hashCode();
        }
        if (getShortcutDimCode_x005B_4_x005D_() != null) {
            _hashCode += getShortcutDimCode_x005B_4_x005D_().hashCode();
        }
        if (getDRF_Code() != null) {
            _hashCode += getDRF_Code().hashCode();
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
        if (getTreasury_Operation() != null) {
            _hashCode += getTreasury_Operation().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec().hashCode();
        }
        if (getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec() != null) {
            _hashCode += getSTRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Sales_Internal_Req_subform.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sales_Internal_Req_subform"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Type"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sub_Type_Item");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sub_Type_Item"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Sub_Type_Item"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "No"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description_2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Description_2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no_More_Quantities");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "No_More_Quantities"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Location_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("not_in_Vat_Report");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Not_in_Vat_Report"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_of_Measure_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Unit_of_Measure_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit_of_Measure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Unit_of_Measure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("use_Duplication_List");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Use_Duplication_List"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appl_from_Item_Entry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Appl_from_Item_Entry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appl_to_Item_Entry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Appl_to_Item_Entry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_1_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Shortcut_Dimension_1_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcut_Dimension_2_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Shortcut_Dimension_2_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_3_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "ShortcutDimCode_x005B_3_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortcutDimCode_x005B_4_x005D_");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "ShortcutDimCode_x005B_4_x005D_"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DRF_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "DRF_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_1_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Budget_Dimension_1_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_2_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Budget_Dimension_2_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_3_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Budget_Dimension_3_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budget_Dimension_4_Code");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Budget_Dimension_4_Code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("treasury_Operation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "Treasury_Operation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcAvailability_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSubstitutions_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesPrices_Rec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_reqinternamaterial", "STRSUBSTNO__x0027__Percent1__x0027__x002C_SalesInfoPaneMgt_CalcNoOfSalesLineDisc_Rec"));
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
