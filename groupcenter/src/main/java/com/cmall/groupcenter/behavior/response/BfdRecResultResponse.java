package com.cmall.groupcenter.behavior.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.behavior.model.BfdRecResultInfo;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 百分点推荐结果响应信息
 * @author pang_jhui
 *
 */
public class BfdRecResultResponse extends RootResultWeb {
	
	
	/*推荐结果列表*/
	private List<BfdRecResultInfo> recResultInfos = new ArrayList<BfdRecResultInfo>();
//	/*推荐结果唯一标识id,wangqx 4月5日添加字段*/
//	private String recommendId = "";

	public List<BfdRecResultInfo> getRecResultInfos() {
		return recResultInfos;
	}

	public void setRecResultInfos(List<BfdRecResultInfo> recResultInfos) {
		this.recResultInfos = recResultInfos;
	}

//	public String getRecommendId() {
//		return recommendId;
//	}
//
//	public void setRecommendId(String recommendId) {
//		this.recommendId = recommendId;
//	}
	

}
