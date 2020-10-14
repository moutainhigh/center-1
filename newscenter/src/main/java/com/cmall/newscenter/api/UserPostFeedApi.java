package com.cmall.newscenter.api;

import com.cmall.newscenter.model.InforMationFeed;
import com.cmall.newscenter.model.UserPostFeedInput;
import com.cmall.newscenter.model.UserPostFeedResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 用户 - 发布资讯
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserPostFeedApi extends RootApiForManage<UserPostFeedResult, UserPostFeedInput> {

	public UserPostFeedResult Process(UserPostFeedInput inputParam,
			MDataMap mRequestMap) {
		UserPostFeedResult result = new UserPostFeedResult();
		if(result.upFlagTrue()){
			InforMationFeed feeds = new InforMationFeed();
			
			feeds.setText("文字描述");
			feeds.setTitle("主题活动");
			feeds.setFaved(1);
			feeds.setFav_count(1000);
			feeds.setCreated_at("2009/07/07 21:51:22");
		}
		return result;
	}

}
