package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 刘嘉玲APP资讯-喜欢输入参数
 * @author shiyz
 * date: 2014-07-04
 * @version1.0
 */
public class InforMationLikeInput extends RootInput {
	@ZapcomApi(value="资讯id",remark="资讯id",demo="123456",require=1,verify="minlength=6")
	/*资讯id*/
	private String feed = "";

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}
	

}
