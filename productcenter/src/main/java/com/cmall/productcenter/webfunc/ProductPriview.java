package com.cmall.productcenter.webfunc;

import com.cmall.systemcenter.common.TagHtml;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapweb.helper.WebSessionHelper;

public class ProductPriview extends BaseClass {

	public String upHtml() {
		
		String sReturnString="";
		
		
		String sProductCode = WebSessionHelper.create().upRequest("productcode");
		
		
		try {
			sReturnString = WebClientSupport.upPost(
					FormatHelper.formatString(bConfig("productcenter.remote_preview"),sProductCode), new MDataMap());
			
			sReturnString=TagHtml.AddWarnHtml(sReturnString,bInfo(949705200));
		} catch (Exception e) {
			
			e.printStackTrace();
			
			sReturnString="";
			
		}
		
		
		return sReturnString;
		
		
	}
}
