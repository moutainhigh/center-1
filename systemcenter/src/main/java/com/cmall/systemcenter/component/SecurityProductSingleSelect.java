package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 商品多选控件
 * @author jack
 *
 */
public class SecurityProductSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=pc_skuinfo|sku_code|sku_name&zw_s_source_page=page_chart_v_security_pc_skuinfo");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
