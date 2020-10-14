package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 活动类型多选控件
 * @author jack
 *
 */
public class ActivitySingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=gc_activity_type|type_code|type_name&zw_s_source_page=page_chart_v_select_gc_activity_type");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
