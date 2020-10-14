package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 翻页类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class PageOption {

	@ZapcomApi(value = "起码页号",remark = "起始页码",demo = "0",require = 1,verify = "regex=^\\d+$")
	/*正整数+0*/
	private int offset;
	
	@ZapcomApi(value = "每页条数",remark = "每页10条",demo = "10",require = 1,verify = "minlength=1")
	private int limit;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
