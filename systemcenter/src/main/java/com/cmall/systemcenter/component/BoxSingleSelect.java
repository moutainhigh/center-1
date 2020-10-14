package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 包厢信息
 * @author 
 *
 */
public class BoxSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=hc_box_information|uid|box_name&zw_s_source_page=page_chart_f_hc_box_information");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
