package com.cmall.newscenter.young.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * @date 2015-04-15
 * @author huangs
 * 获取js代码接口
 */
public class GetJSCodeResult extends RootResultWeb{
@ZapcomApi(value="jscode")
private String js_code="";
 
public String getJs_code() {
	return js_code;
}

public void setJs_code(String js_code) {
	this.js_code = js_code;
}

}
