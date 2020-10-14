package com.cmall.groupcenter.recommend.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.cmall.groupcenter.comment.model.PostCommentList;
import com.cmall.groupcenter.func.TaskItemModel;
import com.cmall.groupcenter.func.TaskRewardModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetTaskDetailResult extends RootResultWeb{
	@ZapcomApi(value = "任务list")
	private List<TaskItemModel> items = new ArrayList<TaskItemModel>();
	
	@ZapcomApi(value = "奖励list")
	private List<TaskRewardModel> rewards = new ArrayList<TaskRewardModel>();

	/**
	 * 获取  items
	 */
	public List<TaskItemModel> getItems() {
		return items;
	}

	/**
	 * 设置 
	 * @param items 
	 */
	public void setItems(List<TaskItemModel> items) {
		this.items = items;
	}

	/**
	 * 获取  rewards
	 */
	public List<TaskRewardModel> getRewards() {
		return rewards;
	}

	/**
	 * 设置 
	 * @param rewards 
	 */
	public void setRewards(List<TaskRewardModel> rewards) {
		this.rewards = rewards;
	}
	
	
}
