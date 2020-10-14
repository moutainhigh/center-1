package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 风采 - 栏目列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class ShareColumnResult extends RootResultWeb{
	
	@ZapcomApi(value = "栏目")
	private List<Column> columns = new ArrayList<Column>();

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	
}
