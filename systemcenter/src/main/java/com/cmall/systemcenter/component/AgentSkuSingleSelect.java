package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 推荐商品单选控件
 * @author GaoYang
 *
 */
public class AgentSkuSingleSelect extends ComponentWindowSingle{
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=pc_skuinfo|sku_code|sku_name&zw_s_source_page=page_chart_v_agent_pc_skuinfo");
		
		return upShowText(mWebField, mDataMap, iType);
	}
}
