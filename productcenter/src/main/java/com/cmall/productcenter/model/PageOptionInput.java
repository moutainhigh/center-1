package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/***
 * 翻页信息
 * @author chew
 * date 2015-08-11
 * @version 1.0
 */
public class PageOptionInput extends RootInput{

	@ZapcomApi(value = "起码页号",remark = "起始页码",demo = "0",require = 1,verify = "regex=^[1-9]\\d+$")
	/*正整数+1*/
	private int page_no;
	
	@ZapcomApi(value = "每页条数",remark = "每页条数",demo = "10",require = 1,verify = "minlength=1")
	private int page_size;
	
	public int getPage_no() {
		return page_no;
	}

	public void setPage_no(int page_no) {
		this.page_no = page_no;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

}
