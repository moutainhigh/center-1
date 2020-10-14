package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 欄目-行程列表輸入類
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class ScheduleListInput extends RootInput {

	@ZapcomApi(value="行程ID",remark="行程",require=1)
	private String column = "";
	
	@ZapcomApi(value="翻页类")
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	
}
