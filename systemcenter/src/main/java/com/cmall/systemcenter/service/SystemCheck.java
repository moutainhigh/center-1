package com.cmall.systemcenter.service;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapweb.webclass.WebCheck;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SystemCheck extends BaseClass {

	private static WebCheck webCheck = new WebCheck();

	/**
	 * 检查是否存在允许链接外的内容
	 * 
	 * @param sSource
	 * @return
	 */
	public MWebResult checkLink(String sSource) {
		String sAllow = bConfig("systemcenter.allowhost");
		String sDangerHtml=bConfig("systemcenter.dangerhtml");
		return webCheck.checkLinks(sSource, sAllow,sDangerHtml);

		// return

	}

}
