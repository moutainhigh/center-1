package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 * 缓存成功提示信息
 * @author zhouguohui
 *
 */
public class ApiForSearchSolrDataResult extends RootResultWeb {

	@ZapcomApi(value = "缓存提示" ,remark="0代表缓存失败   1代表缓存成功")
	private int num =1;

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	
	
	
}
