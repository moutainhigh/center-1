package com.cmall.bbcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

public class SupplierBalanceSingleSelect extends ComponentWindowSingle{
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=bc_supplier_balance|supplier_code|balance_name&zw_s_source_page=page_chart_vv_bc_supplier_balance");
		
		return upShowText(mWebField, mDataMap, iType);
	}
}
