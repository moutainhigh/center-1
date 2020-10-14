package com.cmall.bbcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 
 * 供应商单选控件
 * @author yang
 */
public class SupplierSingleSelect  extends ComponentWindowSingle{
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		mWebField.setSourceParam("zw_s_max_select=1&zw_s_source_tableinfo=bc_supplier_info|supplier_code|company_name&zw_s_source_page=page_chart_vv_bc_supplier_info");
		
		return upShowText(mWebField, mDataMap, iType);
	}
}
