package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 类目单选控件
 * @author jack
 *
 */
public class CategorySingleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=pc_categoryinfo|category_code|category_name&zw_s_source_page=page_chart_v_seller_pc_categoryinfo");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
