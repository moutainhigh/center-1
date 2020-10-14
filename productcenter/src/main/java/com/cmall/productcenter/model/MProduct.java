package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

public class MProduct {
    /**
     * 商品编码
     */
    private String productCode  = ""  ;
    
    private String bigSortName="";
    
    private String propertyKeycode = "";
    
    /**
     * 商品关联属性信息
     */
    private List<MProductProperty> mProductPropertyList = new ArrayList<MProductProperty>();
    
    
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public List<MProductProperty> getmProductPropertyList() {
		return mProductPropertyList;
	}
	public void setmProductPropertyList(List<MProductProperty> mProductPropertyList) {
		this.mProductPropertyList = mProductPropertyList;
	}
	public String getBigSortName() {
		return bigSortName;
	}
	public void setBigSortName(String bigSortName) {
		this.bigSortName = bigSortName;
	}
	public String getPropertyKeycode() {
		return propertyKeycode;
	}
	public void setPropertyKeycode(String propertyKeycode) {
		this.propertyKeycode = propertyKeycode;
	}
	
    
    
    
	
}
