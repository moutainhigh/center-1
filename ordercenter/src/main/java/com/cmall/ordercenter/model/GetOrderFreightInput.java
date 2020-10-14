package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 订单运费： 输入：{区域码{商品代码：数量} } 
 * 返回：[{店铺id:运费}]
 * @author huoqiangshou
 *
 */
public class GetOrderFreightInput extends RootInput {

	/**
	 * 目的地区域代码
	 */
	@ZapcomApi(value="目的地区域代码")
	private String areaCode;
	
	/**
	 * 商品信息
	 */
	@ZapcomApi(value="商品信息")
	private List<PtInfo> ptInfos = new ArrayList<PtInfo>();
	
	
	
	
	public String getAreaCode() {
		return areaCode;
	}




	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}




	public List<PtInfo> getPtInfos() {
		return ptInfos;
	}




	public void setPtInfos(List<PtInfo> ptInfos) {
		this.ptInfos = ptInfos;
	}

	
}
