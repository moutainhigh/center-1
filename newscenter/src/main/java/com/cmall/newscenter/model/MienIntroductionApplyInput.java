package com.cmall.newscenter.model;

import java.math.BigInteger;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 获取栏目详情（url）输入类
 * @author guz
 * date 2014-9-15
 * @version 1.0
 */
public class MienIntroductionApplyInput extends RootInput{
	
	@ZapcomApi(value = "分类编号" ,demo= "44974650000100020004",require = 1)
	private String column = "0";

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
}
