package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 热门排行
 * @author zhouguohui
 * DATE 2014-1121
 * @version 1.0
 */
public class ApiSearchHotwordInput extends RootInput {
	
	@ZapcomApi(value="返回数量集", remark="如果num<=0默认为10条，否者按传递参数返回")
	private int  num=0;

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
