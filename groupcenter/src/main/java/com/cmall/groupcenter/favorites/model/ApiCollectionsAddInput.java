package com.cmall.groupcenter.favorites.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 添加帖子收藏接口输入类
 * @author guz
 *
 */
public class ApiCollectionsAddInput extends RootInput{
	@ZapcomApi(value = "帖子编号",require=1, remark = "JL123456")
	private String post_id = "";
	
	@ZapcomApi(value = "状态", require=1,remark = "4497472000020001可用4497472000020002移除")
	private String flag = "";
	
	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
