package com.cmall.systemcenter.component;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webcomponent.ComponentWindowSingle;
import com.srnpr.zapweb.webmodel.MWebField;

/**
 * 评论标签多选控件
 * @author ligj
 *
 */
public class CommentLabelMultipleSelect extends ComponentWindowSingle {

	
	
	@Override
	public String upText(MWebField mWebField, MDataMap mDataMap, int iType) {
		
		
		mWebField.setSourceParam("zw_s_max_select=0&zw_s_source_tableinfo=pc_comment_labelmanage|label_code|label_name&zw_s_source_page=page_chart_vv_pc_comment_labelmanage");
		
		
		
		return upShowText(mWebField, mDataMap, iType);
	}
	
	
}
