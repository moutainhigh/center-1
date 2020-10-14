package com.cmall.productcenter.model;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 根据广告位获取App与栏目
 * @author 李国杰
 *
 */
public class GetAppAndColumnResult extends RootResult {

	
	/**
	 * 属性列表
	 */
	private MDataMap resultMap=new MDataMap();

	public MDataMap getResultMap() {
		return resultMap;
	}

	public void setResultMap(MDataMap resultMap) {
		this.resultMap = resultMap;
	}
	
}
