package com.cmall.groupcenter.message.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 模块:个人中心->消息
 * 功能:查询当前用户的推送消息
 * @author LHY
 * 2015年1月15日 下午4:33:02
 */
public class CommentPushSingleList extends RootResultWeb {
	@ZapcomApi(value="信息列表",remark="信息列表")
	private List<CommentPushSingleListResult> list = new ArrayList<CommentPushSingleListResult>();
	
	public List<CommentPushSingleListResult> getList() {
		return list;
	}

	public void setList(List<CommentPushSingleListResult> list) {
		this.list = list;
	} 
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
}