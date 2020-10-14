package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 限购地区单选控件
 * @author liqt
 *
 */
public class SelectAreaTemplate extends ComponentWindowSingle {

	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {

		mWebField
				.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=sc_area_template|template_code|template_name&zw_s_source_page=page_chart_v_sc_area_template_select");

		return upShowText(mWebField, mDataMap, iType);
	}
}
