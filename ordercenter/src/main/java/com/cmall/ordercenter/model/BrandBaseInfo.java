package com.cmall.ordercenter.model;


/**
 *品牌对象 
 * 
 */
public class BrandBaseInfo {
	
	private String brandCode="";
	//品牌中文名称
	private String brandZNName="";
	//品牌英文名称
	private String brandUNName="";

	private String brandPic="";

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandZNName() {
		return brandZNName;
	}

	public void setBrandZNName(String brandZNName) {
		this.brandZNName = brandZNName;
	}

	public String getBrandUNName() {
		return brandUNName;
	}

	public void setBrandUNName(String brandUNName) {
		this.brandUNName = brandUNName;
	}

	public String getBrandPic() {
		return brandPic;
	}

	public void setBrandPic(String brandPic) {
		this.brandPic = brandPic;
	}
	
}
