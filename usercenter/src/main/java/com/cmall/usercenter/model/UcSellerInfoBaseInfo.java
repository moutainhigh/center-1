package com.cmall.usercenter.model;
import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * sellerinfo基本信息
 * @author ligj
 * 
 */
public class UcSellerInfoBaseInfo extends BaseClass
{
	/**
	 * 卖家编号
	 */
	private String sellerCode ="" ;
	/**
	 * 卖家名称
	 */
	private String sellerName = "";
	/**
	 * 卖家编号
	 */
	private String smallSellerCode = "";
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSmallSellerCode() {
		return smallSellerCode;
	}
	public void setSmallSellerCode(String smallSellerCode) {
		this.smallSellerCode = smallSellerCode;
	}
	
	
}
