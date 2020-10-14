package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * app二级联动栏目列表
 * @author 李国杰
 *
 */
public class AppSelectResult extends RootResult {

	
	/**
	 * 属性列表
	 */
	private List<MDataMap> listColumn=new ArrayList<MDataMap>();

	public List<MDataMap> getListColumn() {
		return listColumn;
	}

	public void setListColumn(List<MDataMap> listColumn) {
		this.listColumn = listColumn;
	}
	
	
}
