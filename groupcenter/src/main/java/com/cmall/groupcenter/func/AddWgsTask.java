package com.cmall.groupcenter.func;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社新增任务
 * @author dyc
 * @version 1.0
 **/
public class AddWgsTask extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String startTime = mAddMaps.get("start_time");
				String endTime = mAddMaps.get("end_time");
				Date date1 = format.parse(startTime);
				Date date2 = format.parse(endTime);
				if(date1.after(date2)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("开始时间不能小于结束时间");
					return mResult;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			/*系统当前时间*/
			String create_time = DateUtil.getNowTime();
			String publisher = UserFactory.INSTANCE.create().getUserCode();
			String tid = WebHelper.upCode("TASK");
			String contentStr = mAddMaps.get("content");
			TaskModel task = new JsonHelper<TaskModel>().StringToObj(contentStr,new TaskModel());
			
			//查询当前等级的任务是否存在，一个等级的只能有一条
			int count = DbUp.upTable("nc_wgs_task").count("t_level",mAddMaps.get("t_level"),"is_delete","449747110001","is_available","449747110002","is_reckon",mAddMaps.get("is_reckon"));
			if(count<=0){
				//任务表插入参数
				mAddMaps.put("tid", tid);
				mAddMaps.put("create_time",create_time);
				mAddMaps.put("update_time",create_time);
				mAddMaps.put("creator",publisher);/*获取当前登录人*/
				mAddMaps.put("is_delete","449747110001");//默认未删除
				mAddMaps.put("reward_msg",saveItemsAndRewards(tid, task));//拼装奖励信息
				mAddMaps.remove("content");
				DbUp.upTable("nc_wgs_task").dataInsert(mAddMaps);
			}else{
				mResult.inErrorMessage(918519033);
			}
			
		}
		return mResult;
	}
	
	/**
	 * 保存任务步骤及奖励信息
	 * */
	public static String saveItemsAndRewards(String tid,TaskModel task){
		StringBuffer re = new StringBuffer();
		int num = 1;
		//任务步骤信息
		for(TaskItemModel item : task.getItems()){
						
			MDataMap iMap = new MDataMap();
			iMap.put("tid", tid);
			iMap.put("content", item.getContent());
			iMap.put("params", item.getParam());
			iMap.put("type", item.getType());
			iMap.put("btn_name", item.getBtnName());
			iMap.put("is_node", item.getIsNode());
			iMap.put("step_num", (num++)+"");
			iMap.put("create_time", DateUtil.getNowTime());

			DbUp.upTable("nc_wgs_task_detail").dataInsert(iMap);
		}
		
		//任务步骤信息
		for(TaskRewardModel reward : task.getRewards()){
			if(re.toString().length()==0){
				re.append(reward.getTip());
			}else{
				re.append(",");
			}
			re.append(reward.getName());
			re.append(reward.getValue());
			
			MDataMap iMap = new MDataMap();
			iMap.put("tid", tid);
			iMap.put("reward_name", reward.getCode());
			iMap.put("reward_value", reward.getValue());
			iMap.put("reward_tips", reward.getTip());
			iMap.put("create_time", DateUtil.getNowTime());

			DbUp.upTable("nc_wgs_task_reward").dataInsert(iMap);
		}
				
		return re.toString();
	}
}
