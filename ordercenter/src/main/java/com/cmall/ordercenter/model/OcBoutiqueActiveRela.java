package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName: OcBoutiqueActiveRela <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013年10月28日 下午6:43:55 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */
public class OcBoutiqueActiveRela extends BaseClass{
	/**
	 * 活动编码
	 */
	private  String activity_code =  "";
	/**
	 * 精品汇编码
	 */
	private String  boutique_code = "";
	
	
	public OcBoutiqueActiveRela() {
		super();
	}
	
	public String getActivity_code() {
		return activity_code;
	}
	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}
	public String getBoutique_code() {
		return boutique_code;
	}
	public void setBoutique_code(String boutique_code) {
		this.boutique_code = boutique_code;
	}
	
	

}
