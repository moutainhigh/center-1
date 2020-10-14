package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * app单选控件
 * @author jack
 *
 */
public class AppSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=uc_appinfo|app_code|app_name&zw_s_source_page=page_chart_v_single_uc_appinfo");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
