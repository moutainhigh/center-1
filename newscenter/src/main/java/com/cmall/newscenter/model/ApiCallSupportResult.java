package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 获取相册信息
 * @author shiyz
 * @version 1.0
 */
public class ApiCallSupportResult extends RootResultWeb {
	
	@ZapcomApi(value="")
	private String xmlValues;

	public String getXmlValues() {
		return xmlValues;
	}

	public void setXmlValues(String xmlValues) {
		this.xmlValues = xmlValues;
	}	
	

}
