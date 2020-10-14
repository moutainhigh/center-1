package com.cmall.groupcenter.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.cmall.groupcenter.model.AccountTaskInfo;
import com.cmall.groupcenter.model.AccountTaskInput;
import com.cmall.groupcenter.model.AccountTaskListResult;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


public class TaskDetailService extends BaseClass{

	//任务是否完成(否:449747110001,是:449747110002)
	private static String isAvailable = "449747110002";
	
	private static String isDelete = "449747110001";
	//奖励等级
	private static String rewardNameByLevel = "449747320001";
	//返利增加
	private static String rewardNameByRebate = "449747320002";
	//任务类型(发送短信条数:449747290001,返利笔数:449747290002)
	private static String taskTypeMsg = "449747290001"; 
		
	private static String rebateCount = "449747290002"; 
	//判断是否返利
	private static String isRebate = "4497465200040001";
	//社友增加
	private static String addFriend = "449747290003";
	//昵称头像
	private static String nickNameAndImg = "449747290004";
	//app下载
	private static String appDownload = "449747290005";
	//昵称
	private static String nickName = "昵称";
	//头像
	private static String headImg = "头像";
	//昵称和头像
	private static String nickAndHead = "昵称和头像";
	/**
	 * @title 根据用户登记查询用户任务列表
	 * @author shenghr
	 *
	 */
	public AccountTaskListResult showAccountTaskDetail(String accountCode, AccountTaskInput inputParam,String memberCode,String mobile) {
		AccountTaskListResult result = new AccountTaskListResult();
		List<AccountTaskInfo> accountTaskList = new ArrayList<AccountTaskInfo>();
		String currentstepNum = "";
		MDataMap taskLevelMap = DbUp.upTable("gc_mem_task_level").oneWhere(
				"task_level",
				"", "", "account_code", accountCode);
		MDataMap taskMap = DbUp.upTable("gc_mem_task").oneWhere(
				"step_num,create_time",
				"", "", "account_code", accountCode);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select tid,t_level,is_reckon from nc_wgs_task  where is_available =:isAvailable and is_delete =:isDelete";
		if(taskLevelMap == null){
			sql += " order by t_level";
			list = DbUp.upTable("nc_wgs_task").dataSqlList(sql, new MDataMap("isAvailable",isAvailable,"isDelete",isDelete));
		}else{
			sql += " and t_level =:level";
			list = DbUp.upTable("nc_wgs_task").dataSqlList(sql, new MDataMap("isAvailable",isAvailable,"isDelete",isDelete,"level",taskLevelMap.get("task_level").toString()));
		}
		if(list.size() != 0){
			int size = list.size();
			String tLevel = "";
			String tId = "";
			String reckon = "";
			String isReckon = "";
			if(size >= 2 ){
				int account=DbUp.upTable("gc_withdraw_log").dataCount("withdraw_change_type=:isRebate and account_code=:accountCode", new MDataMap("isRebate",isRebate,"accountCode",accountCode));
					if(account == 0){
						isReckon = isDelete;//无返利
					}else
						isReckon = isAvailable;//有返利
					for (Map<String, Object> map : list) {
						reckon = map.get("is_reckon").toString();
						if(isReckon.equals(reckon)){
							tId = map.get("tid").toString();
							tLevel = map.get("t_level").toString();
							break;
						}
					}
			} 
			else{
				tId = list.get(0).get("tid").toString();
				tLevel = list.get(0).get("t_level").toString();	
				reckon = list.get(0).get("is_reckon").toString();
			}	
			result.setTid(tId);
			result.setTaskMark(tLevel+reckon);
			String pWhere = "tid=:tId";
			MDataMap dataMap = new MDataMap("tId",tId);
			if(taskMap!=null && taskMap.get("step_num")!=null){
				pWhere = "tid=:tId and step_num >=:stepNum";
				dataMap = new MDataMap("tId",tId,"stepNum",taskMap.get("step_num").toString());
				currentstepNum = taskMap.get("step_num").toString();
			}
		List<MDataMap> listMap = DbUp.upTable("nc_wgs_task_detail").queryAll("content,btn_name,params,type,step_num,is_node,is_play", "step_num", pWhere, dataMap);
		//判断是否有节点
		if(taskLevelMap == null && taskMap == null){
			DbUp.upTable("gc_mem_task").insert("account_code",accountCode,"tid",tId,"is_finish",isDelete,"create_time",DateUtil.getNowTime(),"step_num",inputParam.getStepNum().equals("")?"0":inputParam.getStepNum());
			DbUp.upTable("gc_mem_task_level").insert("account_code",accountCode,"task_level",tLevel,"create_time",DateUtil.getNowTime());
		}
		else if(!inputParam.getStepNum().equals("")){
			DbUp.upTable("gc_mem_task").dataUpdate(new MDataMap("step_num",inputParam.getStepNum(),"account_code",accountCode), "step_num", "account_code");
		}	
		//判断任务的类型
		int sum = 0;
		int stepNum = -1;
		int length = listMap.size();
		String taskType = "";
		String manageCode = "";
		String currbtnName = "";
		if(length != 0){
			String maxStepNum = listMap.get(length-1).get("step_num");
			result.setMaxStepNum(Integer.parseInt(maxStepNum));
			result.setLevel(tLevel);
			AccountTaskInfo taskInfoLast = new AccountTaskInfo();
			for (MDataMap map : listMap) {
				AccountTaskInfo taskInfo = new AccountTaskInfo();
				String type = map.get("type")==null?"":map.get("type").toString();
				//判断用户是否完成一笔返利
				if(type.equals(rebateCount)){
					int account=DbUp.upTable("gc_withdraw_log").dataCount("withdraw_change_type=:isRebate and account_code=:accountCode and create_time>=:createTime", new MDataMap("isRebate",isRebate,"accountCode",accountCode,"createTime",taskMap.get("create_time")));
					Integer rebateNum = map.get("params")==null?0:Integer.parseInt(map.get("params").toString());
					if(account >= rebateNum){
						continue;
					}
				}
				String content = map.get("content")==null?"":map.get("content").toString();
				String btnName = map.get("btn_name")==null?"":map.get("btn_name").toString();
				//用来标识这个任务属于什么类型
				if(type.equals(addFriend) || type.equals(nickNameAndImg) || type.equals(appDownload) || type.equals(taskTypeMsg)){
					taskType = type;
					taskInfoLast.setTaskDescription(content);
					stepNum = Integer.parseInt(map.get("step_num").toString());
					manageCode = map.get("params");
					currbtnName = btnName;
				}
				if(content.contains("{") && content.contains("}")){
					taskInfoLast.setTaskDescription(content);
					String params = map.get("params").toString();
					if(params.matches("[0-9]*")){
					    sum = map.get("params")==null?0:Integer.parseInt(params);
						content = judgeParams(content,sum+"");
					}else{
						String name = judegeIsAlterNickAndHead(memberCode);
						name = (name.equals("") || name.equals("no"))?params:name;
						content = judgeParams(content,name);
						
					}
					stepNum = Integer.parseInt(map.get("step_num").toString());
				}
				String isNode = map.get("is_node");
				String isPlay = map.get("is_play") == null?"":map.get("is_play");
				String taskStepNum = map.get("step_num") == null?"":map.get("step_num");
				taskInfo.setTaskStepNum(taskStepNum);
				taskInfo.setIsPlay(isPlay);
				taskInfo.setIsNode(isNode);
				taskInfo.setTaskBtn(btnName);
				taskInfo.setTaskDescription(content);	
				accountTaskList.add(taskInfo);
			}	
			result.setAccountTaskList(accountTaskList);
			if(currentstepNum.equals(stepNum+"") && stepNum != -1){
				result = new AccountTaskListResult();
				String sqlData = "";
				String nameAndHead = "";
				List<Map<String, Object>> listData = new ArrayList<Map<String,Object>>();
				int counts = 0;
				//任务类型匹配
				if(taskType.equals(addFriend)){//社友增加
					MDataMap in1Map=new MDataMap("code",accountCode,"flag_enable", "1","recommendTime",taskMap.get("create_time"));
					//一度,二度好友的数量
					sqlData = "select count(1) from gc_member_relation where (parent_code =:code"+
							  " or parent_code in (select account_code from gc_member_relation where parent_code =:code and flag_enable =:flag_enable))"+
 							  " and flag_enable =:flag_enable and create_time>=:recommendTime";		
					listData=DbUp.upTable("gc_member_relation").dataSqlList(sqlData, in1Map);
					counts = Integer.parseInt(listData.get(0).get("count(1)").toString());
				}else if(taskType.equals(nickNameAndImg)){//头像和昵称的修改
					nameAndHead = judegeIsAlterNickAndHead(memberCode);
					nameAndHead = nameAndHead.equals("")?nickAndHead:nameAndHead;
					nameAndHead = nameAndHead.equals("no")?"":nameAndHead;
					sum = 0;
				}else if(taskType.equals(appDownload)){//下载app
					if(!judegeIsActvateApp(mobile,manageCode)){
						nameAndHead = appDownload;
					}
				}else if(taskType.equals(taskTypeMsg)){//发送短信
				    sqlData = "select count(1) from gc_recommend_info where mobile=:mobile and recommend_time>=:recommendTime  group by recommended_mobile";
					listData = DbUp.upTable("gc_recommend_info").dataSqlList(sqlData, new MDataMap("mobile",mobile,"recommendTime",taskMap.get("create_time")));					
					if(listData.size() != 0)
						counts = listData.size();
				}
				if(counts < sum || nameAndHead.length() != 0){
					List<AccountTaskInfo> task = new ArrayList<AccountTaskInfo>();
					String tasks = (sum-counts)+"";
					if(nameAndHead.length() != 0)
						tasks = nameAndHead;
					taskInfoLast.setTaskDescription(judgeParams(taskInfoLast.getTaskDescription(),tasks));
					taskInfoLast.setTaskBtn(currbtnName);
					task.add(taskInfoLast);
					result.setAccountTaskList(task);
				}else{
					//任务完成
					AccountTaskInfo info = accountTaskList.get(accountTaskList.size()-1);
					List<AccountTaskInfo> task = new ArrayList<AccountTaskInfo>();
					task.add(info);
					result.setAccountTaskList(task);
				}
				result.setMaxStepNum(Integer.parseInt(maxStepNum));
				result.setLevel(tLevel);
				result.setTid(tId);
				result.setTaskMark(tLevel+reckon);
			}
		  }
		}
		return result;
	}
	/**
	 * @title 判断手机133****1234
	 * @param nickName
	 * @return
	 */
	private boolean judegeNickName(String nickName){
		if(StringUtility.isNull(nickName))
			return true;
		boolean matches = nickName.matches("1\\d{2}\\*{4}\\d{4}");
		return matches;
	}
	
	/**
	 * @title 截取{0}字符串
	 * @param str
	 * @param sum
	 * @return
	 */
	private String judgeParams(String str,String sum){
	      if(str.contains("{") && str.contains("}")){
			int begin = str.indexOf("{");
			int end = str.indexOf("}");
			String t = str.substring(begin, end+1);
			str = str.replace(t, sum);
		}
	      return str;
	}
	/**
	 * @title  任务完成升级用户等级
	 * @author shenghr
	 * @param tid 
	 *
	 */
	public void updateAccountTask(String accountCode,String mobile,String memberCode) {
		MDataMap taskLevelMap = DbUp.upTable("gc_mem_task_level").oneWhere(
				"task_level",
				"", "", "account_code", accountCode);
		MDataMap taskIdMap = DbUp.upTable("gc_mem_task").oneWhere("tid", "", "", "account_code",accountCode);
		String tid = taskIdMap.get("tid");
		List<MDataMap> dataMap = DbUp.upTable("nc_wgs_task_reward").queryAll("reward_value,reward_name", "", "tid=:tid", new MDataMap("tid",tid));
		List<MDataMap> taskDataMap = DbUp.upTable("nc_wgs_task").queryAll("is_coupon", "", "tid=:tid", new MDataMap("tid",tid));
		int  rewardByLevel = 0;
		int  rewardByRebate = 0;
		for (MDataMap mDataMap : dataMap) {
			if(rewardNameByLevel.equals(mDataMap.get("reward_name").toString()))
				rewardByLevel = mDataMap.get("reward_value") == null?0:Integer.parseInt(mDataMap.get("reward_value").toString());
			if(rewardNameByRebate.equals(mDataMap.get("reward_name").toString()))
				rewardByRebate = mDataMap.get("reward_value") == null?0:Integer.parseInt(mDataMap.get("reward_value").toString());
		}
		int task_level = Integer.parseInt(taskLevelMap.get("task_level").toString());
		DbUp.upTable("gc_mem_task_level").dataUpdate(new MDataMap("task_level",(rewardByLevel+task_level)+"","account_code",accountCode,"create_time",DateUtil.getNowTime()), "task_level,create_time", "account_code");
		DbUp.upTable("gc_mem_task").dataUpdate(new MDataMap("is_finish",isAvailable,"step_num","0","account_code",accountCode,"tid",tid,"create_time",DateUtil.getNowTime()), "is_finish,step_num,tid,create_time", "account_code");
		//增加返利	
		ChangeAccountBlanceService accountBlanceService = new ChangeAccountBlanceService();
		if(rewardByRebate != 0){
			accountBlanceService.changeBlanceByAccount(accountCode, rewardByRebate+"", "返利任务完成，增加相应的返利", mobile);
		}
		String coupon = taskDataMap.get(0).get("is_coupon");
		//奖励优惠券
		if(StringUtility.isNotNull(coupon) && coupon.equals(isAvailable)){
			List<MDataMap> mdate = DbUp.upTable("gc_coupon_import").query("uid,zid",
					"-import_time","used <> 1  and end_time >= :today and coupon_amount =:couponAmount", new MDataMap("today",DateUtil.toString(new Date(),"yyyy-MM-dd"),"couponAmount","10"), -1, 0);//查询出当前可用的优惠卷，按照导入时间排序
			if(mdate != null && mdate.size() >0){
				MDataMap mDataMap = mdate.get(0); //取出第一张
				String couponUid = mDataMap.get("uid");
				//将优惠卷与当前用户进行关联
				DbUp.upTable("gc_mem_coupon").insert("account_code",accountCode,"coupon_uid",couponUid,"create_time",DateUtil.getNowTime());
				//标识该优惠卷已被使用
				MDataMap paramDataMap = new MDataMap();
				paramDataMap.put("used", "1");
				paramDataMap.put("uid", couponUid);
				paramDataMap.put("zid", mDataMap.get("zid"));
				paramDataMap.put("update_time",DateUtil.getNowTime());
				DbUp.upTable("gc_coupon_import").dataUpdate(paramDataMap,"used,update_time","uid,zid");
			}
		}
		//判断下一个任务 是否为 修改昵称头像任务 是否为激活app任务
		List<MDataMap> taskList = DbUp.upTable("nc_wgs_task").queryAll("tid,is_reckon", "", "t_level=:level", new MDataMap("level",(rewardByLevel+task_level)+""));
		if(taskList.size() != 0){
			String tId = "";
			String reckon = "";
			String isReckon = "";
			if(taskList.size() == 2){
				int account=DbUp.upTable("gc_withdraw_log").dataCount("withdraw_change_type=:isRebate and account_code=:accountCode", new MDataMap("isRebate",isRebate,"accountCode",accountCode));
				if(account == 0){
					isReckon = isDelete;//无返利
				}else
					isReckon = isAvailable;//有返利
				for (MDataMap map : taskList) {
					reckon = map.get("is_reckon").toString();
					if(isReckon.equals(reckon)){
						tId = map.get("tid").toString();
						break;
					}
				}
			}else
			 tId = taskList.get(0).get("tid");
			DbUp.upTable("gc_mem_task").dataUpdate(new MDataMap("is_finish",isAvailable,"step_num","0","account_code",accountCode,"tid",tId,"create_time",DateUtil.getNowTime()), "tid", "account_code");
			String type = "";
			String manageCode = "";
			List<MDataMap> taskDetailList = DbUp.upTable("nc_wgs_task_detail").queryAll("type,params", "", "tid=:tid", new MDataMap("tid",tId));
			for (MDataMap mDataMap : taskDetailList) {
				type = mDataMap.get("type");
				if(type.equals(nickNameAndImg)){
					break;
				}else if(type.equals(appDownload)){
					manageCode = mDataMap.get("params");
					break;
				}
			}
			if(type.equals(nickNameAndImg)){
				//判断用户是否修改过昵称和头像
				String head = judegeIsAlterNickAndHead(memberCode);
				 if(head.equals("no")){
					  updateAccountTask(accountCode,mobile,memberCode);
				    
				 }
			}else if(type.equals(appDownload)){
				//判断用户是否激活过app
				if(judegeIsActvateApp(mobile,manageCode)){
					updateAccountTask(accountCode,mobile,memberCode);
				}
			}
		}
	}
	
	/**
	 * 
	 * @title 判断用户是否修改过昵称和头像
	 * @param accountCode
	 * @return
	 */
	
	public String judegeIsAlterNickAndHead(String memberCode){
		MDataMap in1Map=new MDataMap("code",memberCode);
		String nameAndHead = "";
		List<MDataMap> queryList = DbUp.upTable("mc_extend_info_groupcenter").queryAll("head_icon_url,nickName", "", "member_code =:code", in1Map);
		if(queryList.size() != 0){
			String iconUrl = queryList.get(0).get("head_icon_url");
			String nick = queryList.get(0).get("nickName");
			if(StringUtility.isNull(iconUrl) && !judegeNickName(nick)){
				nameAndHead = headImg;
			}else if(!StringUtility.isNull(iconUrl) && judegeNickName(nick)){
				nameAndHead = nickName;
			}else if(StringUtility.isNull(iconUrl) && judegeNickName(nick)){
				nameAndHead = nickAndHead;
			}
			//账号已经修改过昵称和头像
			if(nameAndHead.length() == 0)
				   return "no";
		}
		
		return nameAndHead;
	}
	/**
	 * @title 判断用户是否激活app 
	 * @param memberCode
	 * @param mobile
	 * @return
	 */
	public boolean judegeIsActvateApp(String mobile,String manageCode){
		MDataMap in1Map=new MDataMap("mobile", mobile,"manageCode",manageCode);
		String sqlData = "select count(1) from mc_login_info where login_name =:mobile and manage_code =:manageCode ";
		List<Map<String,Object>> sqlList = DbUp.upTable("mc_login_info").dataSqlList(sqlData, in1Map);
		Integer re = Integer.parseInt(sqlList.get(0).get("count(1)").toString());
		return (re > 0)?true:false;
	}
	
	
	
	/**	
	 * @title 判断用户是否存在有任务
	 * @param accountCode
	 * @return
	 */
	public AccountTaskListResult judgeIsTask(String accountCode) {
		AccountTaskListResult taskReslut = new AccountTaskListResult();
		int result = 0;
		MDataMap oneWhere = DbUp.upTable("nc_wgs_task").oneWhere("max(t_level)", "", "is_available =:isAvailable and is_delete =:isDelete","isAvailable", isAvailable,"isDelete",isDelete);
		String maxLevel = oneWhere.get("max(t_level)");
		int dataCount = DbUp.upTable("gc_mem_task_level").dataCount("account_code =:accountCode and task_level<=:maxLevel", new MDataMap("accountCode",accountCode,"maxLevel",maxLevel));
		//当前用户的level高于任务列表中最大的任务等级   说明所有任务完成
		if(dataCount == 0){
			result = 0;
		}
		else {
			int courrentCounts = DbUp.upTable("gc_mem_task_level").dataCount("account_code =:accountCode and task_level=:maxLevel", new MDataMap("accountCode",accountCode,"maxLevel",maxLevel));
			if(courrentCounts != 0)
				result = 0;
			else
				result = 1;
		}
		taskReslut.setIsTask(result);
		MDataMap taskLevelMap = DbUp.upTable("gc_mem_task_level").oneWhere(
				"task_level",
				"", "", "account_code", accountCode);
		if(taskLevelMap == null)
			taskReslut.setLevel("0");
		else
			taskReslut.setLevel(taskLevelMap.get("task_level")==null?"0":taskLevelMap.get("task_level").toString());
		 return taskReslut;
	}
	

}
