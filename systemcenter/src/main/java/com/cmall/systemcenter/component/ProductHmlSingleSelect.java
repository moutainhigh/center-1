package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 商品单选控件
 * @author houwen
 *
 */
public class ProductHmlSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=pc_productinfo|product_code|product_name&zw_s_source_page=page_chart_v_hml_pc_productinfo");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
