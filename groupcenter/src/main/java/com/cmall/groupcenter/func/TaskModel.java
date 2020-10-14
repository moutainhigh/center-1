package com.cmall.groupcenter.func;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串转对象的类
 * @author dyc
 * */
public class TaskModel {
	
	private List<TaskItemModel> items = new ArrayList<TaskItemModel>();
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
