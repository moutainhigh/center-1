package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * Sku单选控件
 * @author houwen
 *
 */
public class MemberSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=mc_extend_info_star|member_code|nickname&zw_s_source_page=page_chart_v_hml_mc_extend_info_star");
		

		
		return upShowTextPost(mWebField, mDataMap, iType);
	}
	
	
}
