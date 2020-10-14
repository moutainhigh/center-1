package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 商家行业单选控件
 * @author GaoYang
 *
 */
public class AgentTradeSingleSelect extends ComponentWindowSingle{
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=agent_trade|trade_code|trade_name&zw_s_source_page=page_chart_v_agent_trade");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
}
