package com.cmall.dborm.txmodel;

public class PcProductproperty {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.zid
     *
     * @mbggenerated
     */
    private Integer zid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.uid
     *
     * @mbggenerated
     */
    private String uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.product_code
     *
     * @mbggenerated
     */
    private String productCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.property_keycode
     *
     * @mbggenerated
     */
    private String propertyKeycode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.property_code
     *
     * @mbggenerated
     */
    private String propertyCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.property_key
     *
     * @mbggenerated
     */
    private String propertyKey;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.property_value
     *
     * @mbggenerated
     */
    private String propertyValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.property_type
     *
     * @mbggenerated
     */
    private String propertyType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.big_sort
     *
     * @mbggenerated
     */
    private Integer bigSort;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productproperty.small_sort
     *
     * @mbggenerated
     */
    private Integer smallSort;
    
    private int type = 2;  // 1内联赠品 2外联
    
    private String startDate = "";   // 内联赠品 展示的开始时间
    
    private String endDate = "";   // 内联赠品 展示的结束时间
    

    
    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.zid
     *
     * @return the value of pc_productproperty.zid
     *
     * @mbggenerated
     */
    public Integer getZid() {
        return zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.zid
     *
     * @param zid the value for pc_productproperty.zid
     *
     * @mbggenerated
     */
    public void setZid(Integer zid) {
        this.zid = zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.uid
     *
     * @return the value of pc_productproperty.uid
     *
     * @mbggenerated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.uid
     *
     * @param uid the value for pc_productproperty.uid
     *
     * @mbggenerated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.product_code
     *
     * @return the value of pc_productproperty.product_code
     *
     * @mbggenerated
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.product_code
     *
     * @param productCode the value for pc_productproperty.product_code
     *
     * @mbggenerated
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.property_keycode
     *
     * @return the value of pc_productproperty.property_keycode
     *
     * @mbggenerated
     */
    public String getPropertyKeycode() {
        return propertyKeycode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.property_keycode
     *
     * @param propertyKeycode the value for pc_productproperty.property_keycode
     *
     * @mbggenerated
     */
    public void setPropertyKeycode(String propertyKeycode) {
        this.propertyKeycode = propertyKeycode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.property_code
     *
     * @return the value of pc_productproperty.property_code
     *
     * @mbggenerated
     */
    public String getPropertyCode() {
        return propertyCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.property_code
     *
     * @param propertyCode the value for pc_productproperty.property_code
     *
     * @mbggenerated
     */
    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.property_key
     *
     * @return the value of pc_productproperty.property_key
     *
     * @mbggenerated
     */
    public String getPropertyKey() {
        return propertyKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.property_key
     *
     * @param propertyKey the value for pc_productproperty.property_key
     *
     * @mbggenerated
     */
    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.property_value
     *
     * @return the value of pc_productproperty.property_value
     *
     * @mbggenerated
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.property_value
     *
     * @param propertyValue the value for pc_productproperty.property_value
     *
     * @mbggenerated
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.property_type
     *
     * @return the value of pc_productproperty.property_type
     *
     * @mbggenerated
     */
    public String getPropertyType() {
        return propertyType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.property_type
     *
     * @param propertyType the value for pc_productproperty.property_type
     *
     * @mbggenerated
     */
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.big_sort
     *
     * @return the value of pc_productproperty.big_sort
     *
     * @mbggenerated
     */
    public Integer getBigSort() {
        return bigSort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.big_sort
     *
     * @param bigSort the value for pc_productproperty.big_sort
     *
     * @mbggenerated
     */
    public void setBigSort(Integer bigSort) {
        this.bigSort = bigSort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productproperty.small_sort
     *
     * @return the value of pc_productproperty.small_sort
     *
     * @mbggenerated
     */
    public Integer getSmallSort() {
        return smallSort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productproperty.small_sort
     *
     * @param smallSort the value for pc_productproperty.small_sort
     *
     * @mbggenerated
     */
    public void setSmallSort(Integer smallSort) {
        this.smallSort = smallSort;
    }
}