package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.HighendTypeInfo;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * 获取高端商品分类下具体内容
 * @author GaoYang
 *
 */
public class ApiGetHighendTypeResult extends RootResult{

	private List<HighendTypeInfo> hightendTypeList = new ArrayList<HighendTypeInfo>();

	public List<HighendTypeInfo> getHightendTypeList() {
		return hightendTypeList;
	}

	public void setHightendTypeList(List<HighendTypeInfo> hightendTypeList) {
		this.hightendTypeList = hightendTypeList;
	}
	
}
