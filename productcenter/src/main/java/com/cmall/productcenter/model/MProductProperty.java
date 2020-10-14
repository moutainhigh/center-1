package com.cmall.productcenter.model;

public class MProductProperty {
	/**
     * 商品编号
     */
    private String productCode  = ""  ;
    /**
     * 属性名称编号
     */
    private String propertyKeycode  = ""  ;
    /**
     * 属性编号
     */
    private String propertyCode  = ""  ;
    /**
     * 属性名称
     */
    private String propertyKey  = ""  ;
    /**
     * 属性值
     */
    private String propertyValue  = ""  ;
    
    /**
     * 大项排序
     */
    private int bigSort = 0;
    
    /**
     * 大项的某个小项排序
     */
    private int smallSort = 0;
    
    
    /**
     * 属性类型
     *  449736200001	颜色属性
	 * 	449736200002	关键属性
	 * 	449736200003	销售属性
	 * 	449736200004	自定义属性
	 * 
     */
    private String propertyType  = ""  ;
    
    
	public int getBigSort() {
		return bigSort;
	}
	public void setBigSort(int bigSort) {
		this.bigSort = bigSort;
	}
	public int getSmallSort() {
		return smallSort;
	}
	public void setSmallSort(int smallSort) {
		this.smallSort = smallSort;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getPropertyKeycode() {
		return propertyKeycode;
	}
	public void setPropertyKeycode(String propertyKeycode) {
		this.propertyKeycode = propertyKeycode;
	}
	public String getPropertyCode() {
		return propertyCode;
	}
	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}
	public String getPropertyKey() {
		return propertyKey;
	}
	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
    
    
    
    
    
    
}
