package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/***
 * 翻页结果
 * @author chew
 * date 2015-08-11
 * @version 1.0
 */
public class PageResults extends RootResultWeb {

	@ZapcomApi(value = "符合条件的结果总数")
	private int total_results = 0;
	
	@ZapcomApi(value = "是否存在下一页")
	private boolean has_next = false;

	public int getTotal_results() {
		return total_results;
	}

	public void setTotal_results(int total_results) {
		this.total_results = total_results;
	}

	public boolean isHas_next() {
		return has_next;
	}

	public void setHas_next(boolean has_next) {
		this.has_next = has_next;
	}

}
