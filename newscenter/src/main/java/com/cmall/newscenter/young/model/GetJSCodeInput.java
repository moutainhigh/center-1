package com.cmall.newscenter.young.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * @date 2015-04-16
 * @author huangs
 * 获取js代码接口
 */
public class GetJSCodeInput extends RootInput{
	@ZapcomApi(value = "频道编号",require = 1)
    private String recreation_code="";
 
	public String getRecreation_code() {
		return recreation_code;
	}

	public void setRecreation_code(String recreation_code) {
		this.recreation_code = recreation_code;
	}
	
	
}
