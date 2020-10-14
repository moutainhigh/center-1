package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * ClassName: OcBoutiqueSellerRela <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013年10月28日 下午6:43:19 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */
public class OcBoutiqueSellerRela extends BaseClass{
	/**
	 * 精品汇编码
	 */
	private String boutique_code = "";
	/**
	 * 商家编码
	 */
	private String seller_code = "";
	
	
	
	public String getBoutique_code() {
		return boutique_code;
	}
	public void setBoutique_code(String boutique_code) {
		this.boutique_code = boutique_code;
	}
	public String getSeller_code() {
		return seller_code;
	}
	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}
	public OcBoutiqueSellerRela(String boutique_code, String seller_code) {
		super();
		this.boutique_code = boutique_code;
		this.seller_code = seller_code;
	}
	public OcBoutiqueSellerRela() {
		
		super();
	}
}
