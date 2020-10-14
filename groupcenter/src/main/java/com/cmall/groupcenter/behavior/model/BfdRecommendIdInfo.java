package com.cmall.groupcenter.behavior.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 百分点推荐结果唯一标识id封装类
 * 
 * @author wangqingxia
 * @date 2016年4月5日 上午10:13:04
 *
 */
public class BfdRecommendIdInfo {
	/*推荐结果唯一标识id*/
	private String recommendId = "";
//	private List<BfdRecResultInfo> listRecProduct = new ArrayList<BfdRecResultInfo>();
	private List<BfdRecProductInfo> listRecProduct = new ArrayList<BfdRecProductInfo>();
	
	
	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public List<BfdRecProductInfo> getListRecProduct() {
		return listRecProduct;
	}

	public void setListRecProduct(List<BfdRecProductInfo> listRecProduct) {
		this.listRecProduct = listRecProduct;
	}

	
    	
    

}
