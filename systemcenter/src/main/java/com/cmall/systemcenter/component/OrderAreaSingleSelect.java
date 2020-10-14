package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 订单地区单选控件
 * @author 黄思
 *
 */
public class OrderAreaSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=nc_order_area|area_code|area_name&zw_s_source_page=page_chart_v_nc_order_area");
		

		
		return upShowTextPost(mWebField, mDataMap, iType);
	}
	
	
}
