package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 品牌故事-欄目列表輸出類
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class BrandStoryColumnResult extends RootResultWeb {
	
	@ZapcomApi(value="欄目列表")
	private List<Column> columns = new ArrayList<Column>();

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	

}
