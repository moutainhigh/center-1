package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 店铺单选控件
 * @author jack
 *
 */
public class ShopSingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=uc_sellerinfo|seller_code|seller_name&zw_s_source_page=page_chart_v_category_uc_sellerinfo");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
