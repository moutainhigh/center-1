package com.cmall.groupcenter.comment.util;

import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiPostsCommentUtil {
	public static Map<String, Object> getPostsComment(String uuid) {
		Map<String, Object> oneMap = DbUp.upTable("nc_posts_comment").dataSqlOne("select * from nc_posts_comment where uid= '"+uuid+"'", new MDataMap());
		return oneMap;
	}
	
	public static String getPostsCommentCount(String postCode) {
		MDataMap mDataMap = new MDataMap();
		Map<String, Object> oneMap = DbUp.upTable("nc_posts_comment").dataSqlOne("SELECT COUNT(DISTINCT publisher_code) as num FROM nc_posts_comment where post_code='"+postCode+"'", mDataMap);
		String num = String.valueOf(oneMap.get("num"));
		return num;
	}
}
