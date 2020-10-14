package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 广告位单选控件  家有汇
 * @author 李国杰
 *
 */
public class AdPlaceSingleSelectHomePool extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=nc_advertise_place|place_code|place_name&zw_s_source_page=page_chart_v_jyh_single_nc_advertise_place");
		

		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
