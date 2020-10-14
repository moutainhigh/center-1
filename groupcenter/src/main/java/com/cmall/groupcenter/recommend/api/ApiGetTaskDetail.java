package com.cmall.groupcenter.recommend.api;

import java.util.List;

import com.cmall.groupcenter.func.TaskItemModel;
import com.cmall.groupcenter.func.TaskRewardModel;
import com.cmall.groupcenter.recommend.model.ApiGetTaskDetailInput;
import com.cmall.groupcenter.recommend.model.ApiGetTaskDetailResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取微公社任务步骤详情
 * 
 * @author dyc
 *
 */
public class ApiGetTaskDetail
		extends
		RootApiForManage<ApiGetTaskDetailResult, ApiGetTaskDetailInput> {

	public ApiGetTaskDetailResult Process(
			ApiGetTaskDetailInput inputParam, MDataMap mRequestMap) {
		ApiGetTaskDetailResult detailResult = new ApiGetTaskDetailResult();
		
		//查询步骤信息
		List<MDataMap> items = DbUp.upTable("nc_wgs_task_detail").queryAll("", "step_num", "", new MDataMap("tid",inputParam.getTid()));
		
		if(items!=null){
			for(MDataMap mdata : items){
				TaskItemModel item = new TaskItemModel();					
				item.setContent(mdata.get("content"));
				item.setType(mdata.get("type"));
				item.setParam(mdata.get("params"));
				item.setBtnName(mdata.get("btn_name"));
				item.setIsNode(mdata.get("is_node"));
				
				detailResult.getItems().add(item);
			}
		}
		
		//查询奖励信息
		List<MDataMap> rewards = DbUp.upTable("nc_wgs_task_reward").queryByWhere("tid",inputParam.getTid());
		if(items!=null){
			for(MDataMap mdata : rewards){
				TaskRewardModel reward = new TaskRewardModel();	
				reward.setName(mdata.get("reward_name"));
				reward.setValue(mdata.get("reward_value"));
				reward.setTip(mdata.get("reward_tips"));
				
				detailResult.getRewards().add(reward);
			}
		}
		return detailResult;
	}

}
