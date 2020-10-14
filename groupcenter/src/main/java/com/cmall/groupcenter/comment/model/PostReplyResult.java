package com.cmall.groupcenter.comment.model;

import java.util.HashMap;
import java.util.Map;

import com.cmall.groupcenter.util.DateTimeUtil;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 回复帖子输出类
 * @author LHY
 * 2015年4月27日 上午10:30:15
 */
public class PostReplyResult extends RootResultWeb {
	
	@ZapcomApi(value = "返回当前评论信息")
	private Map<String, Object> map = new HashMap<String, Object>();
	@ZapcomApi(value = "返回评论总人数")
	private String num;
	
	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		if(map!=null) {
			String time = String.valueOf(map.get("publish_time"));
			time = DateTimeUtil.getDateDiff(time);
			map.put("publish_time", time);
		}
		this.map = map;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
}
