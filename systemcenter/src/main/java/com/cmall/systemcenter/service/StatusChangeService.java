package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowBussinesstype;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MStringMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class StatusChangeService extends BaseClass {
	
	/**
	 * @param flowBussinessUid 业务id
	 * @param flowType 流程类型id
	 * @param fromStatus 从什么状态
	 * @param toStatus 到什么状态
	 * @param userCode 操作人编号
	 * @param remark 审批备注
	 * @return
	 */
	public RootResult ChangeFlow(String fieldName,String fieldValue,String flowType, String fromStatus,String toStatus, 
			String userCode, String remark,MDataMap mSubMap) {

		mSubMap.put("userCodex", userCode);
		
		RootResult ret = new RootResult();
		String uuid = "";
		String Func = "";

		MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",
				flowType);
		
		ScFlowBussinesstype sfbt = new ScFlowBussinesstype();
		if(flowTypeData == null)
		{
			ret.setResultCode(949701024);
			ret.setResultMessage(bInfo(949701024));
			return ret;
		}
		else
		{
			SerializeSupport ss = new SerializeSupport<ScFlowBussinesstype>();
			ss.serialize(flowTypeData, sfbt);
		}

		WebHelper wh = new WebHelper();

		try {
			
			
			
			MDataMap flowMain = DbUp.upTable(sfbt.getTableName()).one(fieldName,
					fieldValue);

			if(flowMain == null)
			{
				ret.setResultCode(949701025);
				ret.setResultMessage(bInfo(949701025));
				return ret;
			} else {				
				
				String currentStatus = flowMain.get(sfbt.getColumnName());
				if (!currentStatus.equals(fromStatus)) {
					ret.setResultCode(949701006);
					ret.setResultMessage(bInfo(949701006));
					return ret;
				}  else {
					
					//取得当前的流转
					MDataMap flowStatuschange = DbUp.upTable(
							"sc_flow_statuschange").one("flow_type",
									flowType, "from_status", currentStatus, "to_status", toStatus);
					
					if (flowStatuschange == null) {
						ret.setResultCode(949701013);
						ret.setResultMessage(bInfo(949701013, fieldValue));
						return ret;
					} else {
						
						//新增权限判断
						List<RoleStatus> toStatusList = this.getToStatusList(flowType, fromStatus, userCode);
						boolean isAuthoerity = false;
						for (RoleStatus roleStatus : toStatusList) {
							if(roleStatus.getToStatus().equals(toStatus)){
								isAuthoerity = true;
							}
						}
						
						if(!isAuthoerity){
							ret.setResultCode(949701015);
							ret.setResultMessage(bInfo(949701015, fieldValue));
							return ret;
						}
						
						uuid = wh.addLock(fieldValue, 300);
						
						if (uuid.equals("")) {
							ret.setResultCode(949701005);
							ret.setResultMessage(bInfo(949701005));
							return ret;
						}
						
						Func = flowStatuschange.get("chang_status_func");
						
						IFlowFunc flowFunc = null;
						if (StringUtils.isNotEmpty(Func)) {
							try {
								Class<?> cClass = ClassUtils.getClass(Func);
								if (cClass != null
										&& cClass.getDeclaredMethods() != null) {
									flowFunc = (IFlowFunc) cClass
											.newInstance();
									RootResult beforeCode = flowFunc
											.BeforeFlowChange(
													fieldValue,
													fieldValue,
													fromStatus, toStatus,mSubMap);

									if(beforeCode!=null)
									{
										if (beforeCode.getResultCode() != 1) {
											wh.unLock(uuid);
											return beforeCode;

										}
									}
									

								}

							} catch (Exception e) {
								wh.unLock(uuid);
								bLogError(949701007, Func);
								e.printStackTrace();
								ret.setResultCode(949701007);
								ret.setResultMessage(bInfo(949701007, Func));
								return ret;
							}
						}

						//更新状态
						MDataMap updateDataMap = new MDataMap();
						
						updateDataMap.put(fieldName, fieldValue);
						updateDataMap.put("to_Status", toStatus);
						updateDataMap.put("user_code", userCode);
						updateDataMap.put("create_time", DateUtil.getSysDateTimeString());
						updateDataMap.put("from_status", fromStatus);
						
						String updateSql = " update " + sfbt.getTableName() + " set " 
								+(sfbt.getCreatorColumnname().equals("") ? "":(sfbt.getCreatorColumnname()  + " =:user_code, "))
								+(sfbt.getCreatetimeColumnname().equals("") ? "":(sfbt.getCreatetimeColumnname()  + " =:create_time,"))
								+sfbt.getColumnName() + " =:to_Status "
								+" Where "+fieldName+"=:"+fieldName+" and "+ sfbt.getColumnName() +" =:from_status";
						
						int retcode = DbUp.upTable(sfbt.getTableName()).dataExec(updateSql, updateDataMap);
								
						//DbUp.upTable("").dataUpdate(mDataMap, sUpdateFields, sWhereFields)
						
						if (retcode != 1) {
							ret.setResultCode(949701026);
							ret.setResultMessage(bInfo(949701026));
							wh.unLock(uuid);
							return ret;
							
						} else {

							try {
								if (flowFunc != null) {
									RootResult afterCode = flowFunc
											.afterFlowChange(
													fieldValue,
													fieldValue,
													fromStatus, toStatus,mSubMap);
								}

							} catch (Exception e) {
								bLogError(949701008, Func);
								e.printStackTrace();
							}
							
							//如果是1 则记录到日志
							if(sfbt.getIsCommonlog().equals("1"))
							{
								try
								{
									UUID uuid2 = UUID.randomUUID();
									
									MDataMap insertDatamap = new MDataMap();
									
									insertDatamap.put("uid", uuid2.toString().replace("-", ""));
									insertDatamap.put("flow_code", fieldValue);
									insertDatamap.put("flow_type", flowType);
									insertDatamap.put("creator", userCode);
									insertDatamap.put("create_time", DateUtil.getSysDateTimeString());
									insertDatamap.put("flow_remark", remark);
									insertDatamap.put("current_status", toStatus);
									DbUp.upTable("sc_flow_bussiness_history").dataInsert(insertDatamap);
								}
								catch(Exception ex)
								{
									bLogError(949701027, fieldValue,flowType);
								}
							}
							
							
							ret.setResultCode(1);
							// 解锁
							WebHelper.unLock(uuid);
							return ret;
						}
					}
					
				}
			}
			// 调用之后的处理逻辑
		} catch (Exception ex) {
			
			if(!uuid.equals(""))
			{
				wh.unLock(uuid);
			}
			
			ret.setResultCode(949701012);
			ret.setResultMessage(bInfo(949701012, ex.getMessage()));
			WebHelper.unLock(uuid);
			return ret;
		}
	}
	
	
	/**
	 * 获取当前类型的状态，根据当期的状态
	 * 
	 * @param flowType
	 * @param fromStatus
	 * @return
	 */
	public List<RoleStatus> getToStatusList(String flowType, String fromStatus,String userCode) {
		List<RoleStatus> ret = new ArrayList<RoleStatus>();
		
		MDataMap sfsMapParam = new MDataMap();
		sfsMapParam.put("flow_type", flowType);
		sfsMapParam.put("from_status", fromStatus);
		List<MDataMap> sfsListMap = DbUp.upTable("sc_flow_statuschange").query(
				"", "", " flow_type=:flow_type and from_status=:from_status",
				sfsMapParam, -1, -1);

		if (sfsListMap == null || sfsListMap.size() == 0)
			return ret;
		else {
			Map mapKey = new MStringMap();
			for (MDataMap dm : sfsListMap) {
				RoleStatus rs = new RoleStatus();

				//新增权限判断 -修改
				if(!mapKey.containsKey(dm.get("to_status"))){
					rs.setToStatus(dm.get("to_status"));
					if(dm.get("role_id").equals("all")){
						mapKey.put(rs.getToStatus(), rs.getToStatus());
						ret.add(rs);
					}else{
						
						MDataMap urMapParam = new MDataMap();
						urMapParam.put("user_code", userCode);
						urMapParam.put("role_code", dm.get("role_id"));
						List<MDataMap> UserStatusListMap = DbUp.upTable("za_userrole")
								.query("", "", "user_code=:user_code and role_code=:role_code", urMapParam, -1, -1);
						
						if(UserStatusListMap!=null && UserStatusListMap.size() > 0){
							mapKey.put(rs.getToStatus(), rs.getToStatus());
							ret.add(rs);
						}
					}
				}
				
				
				
			}
			return ret;
		}
	}
}
