package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 预定时间段信息
 * @author 
 *
 */
public class ScheduledSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=hc_scheduled_time|uid|time_name&zw_s_source_page=page_chart_f_hc_scheduled_time");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
