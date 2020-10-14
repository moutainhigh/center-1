package com.cmall.systemcenter.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.style.ToStringStyler;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.ChangeFlowCallableStatementCreator;
import com.cmall.systemcenter.common.CreateFlowCallableStatementCreator;
import com.cmall.systemcenter.model.FlowNextOperator;
import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.model.UserStatus;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MStringMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webface.IWebFunc;
import com.srnpr.zapweb.webfactory.UserFactory;

import scala.collection.immutable.HashMap;

public class FlowService extends BaseClass {

	private static String FlowHead = "SF";

	/**
	 * 保存审批状态
	 * 
	 * @param fromType
	 * @param fromStatus
	 * @param toStatus
	 * @param webFunc
	 * @param roleCode
	 * @return
	 */
	public RootResult AddChangeStatus(String flowType, String fromStatus,
			String toStatus, String webFunc, String roleCode) {
		RootResult ret = new RootResult();
		if (flowType.equals("")) {
			ret.setResultCode(949701016);
			ret.setResultMessage(bInfo(949701016));
			return ret;
		} else if (fromStatus.equals("")) {
			ret.setResultCode(949701017);
			ret.setResultMessage(bInfo(949701017));
			return ret;
		} else if (toStatus.equals("")) {
			ret.setResultCode(949701018);
			ret.setResultMessage(bInfo(949701018));
			return ret;
		} else if (roleCode.equals("")) {
			ret.setResultCode(949701021);
			ret.setResultMessage(bInfo(949701021));
			return ret;
		} else if (toStatus.equals(fromStatus)) {
			ret.setResultCode(949701022);
			ret.setResultMessage(bInfo(949701022));
			return ret;
		} else {
			if (!webFunc.equals("")) {
				try {
					Class<?> cClass = ClassUtils.getClass(webFunc);
					if (cClass != null && cClass.getDeclaredMethods() != null) {
						IFlowFunc flowFunc = (IFlowFunc) cClass.newInstance();
					}

				} catch (Exception e) {
					ret.setResultCode(949701019);
					ret.setResultMessage(bInfo(949701019));
					return ret;
				}
			}

			try {

				MDataMap currentData = DbUp.upTable("sc_flow_statuschange")
						.one("flow_type", flowType, "from_status", fromStatus,
								"to_status", toStatus, "role_id", roleCode);

				if (currentData != null) {
					ret.setResultCode(949701023);
					ret.setResultMessage(bInfo(949701023));
					return ret;
				}

				UUID uuid = UUID.randomUUID();

				MDataMap insertDatamap = new MDataMap();

				insertDatamap.put("uid", uuid.toString().replace("-", ""));
				insertDatamap.put("flow_type", flowType);
				insertDatamap.put("from_status", fromStatus);
				insertDatamap.put("to_status", toStatus);
				insertDatamap.put("chang_status_func", webFunc);
				insertDatamap.put("role_id", roleCode);

				DbUp.upTable("sc_flow_statuschange").dataInsert(insertDatamap);

			} catch (Exception ex) {
				ret.setResultCode(949701012);
				ret.setResultMessage(bInfo(949701012, ex.getMessage()));
				return ret;
			}
		}

		return ret;
	}

	/**
	 * 创建流程审批单
	 * 
	 * @param flow
	 *            流程审批单主数据
	 * @return 正常返回1 ，错误返回错误编号
	 */
	public RootResult CreateFlow(ScFlowMain flow) {

		RootResult ret = new RootResult();

		if (flow == null) {
			ret.setResultCode(949701010);
			ret.setResultMessage(bInfo(949701010));
			return ret;
		} else {
			
			//加类型必填
			
			flow.setFlowCode(WebHelper.upCode(FlowHead));
			FlowNextOperator fq = this.getNextAll(flow.getFlowType(),
					flow.getCurrentStatus());

			if (fq.getNextOperator().equals(""))
				flow.setFlowIsend(1);

			flow.setNextOperators(fq.getNextOperator());
			flow.setNextOperatorStatus(fq.getNextOperatorStatus());

			// 调用 添加订单的存储过程
			List<SqlParameter> params = new ArrayList<SqlParameter>();
			params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
			params.add(new SqlOutParameter("error", Types.VARCHAR));
			CreateFlowCallableStatementCreator cscc = new CreateFlowCallableStatementCreator(
					flow);

			DbTemplate dt = DbUp.upTable("sc_flow_main").upTemplate();
			Map<String, Object> outValues = dt.getJdbcOperations().call(cscc,
					params);

			String returnCode = outValues.get("outFlag").toString();
			if (Integer.parseInt(returnCode) != 1) {
				ret.setResultCode(Integer.parseInt(returnCode));
				ret.setResultMessage(bInfo(Integer.parseInt(returnCode)));
				return ret;
			} else {
				ret.setResultMessage(flow.getFlowCode());
				return ret;
			}
		}
	}

	/**
	 * 审批流程审批单
	 * 
	 * @param flowCode
	 *            流程审批单编号
	 * @param fromStatus
	 *            从什么状态
	 * @param toStatus
	 *            到什么状态
	 * @param userCode
	 *            操作人编号
	 * @param roleCode
	 *            角色编号字符串
	 * @param remark
	 *            审批备注
	 * @return
	 */
	public RootResult ChangeFlow(String lockCode,String flowCode, String fromStatus,
			String toStatus, String userCode,String roleCode, String remark,MDataMap mSubMap) {
 
		RootResult ret = new RootResult();
		String uuid = "";
		String Func = "";

		// 调用之前的处理逻辑
		if (flowCode == null || flowCode.equals("")) {
			ret.setResultCode(949701002);
			ret.setResultMessage(bInfo(949701002));
			return ret;
		}

		WebHelper wh = new WebHelper();

		try {
//			MDataMap flowMain = DbUp.upTable("sc_flow_main").one("flow_code",
//					flowCode);
			Map<String, Object> map = DbUp.upTable("sc_flow_main").upTemplate().queryForMap("select * from systemcenter.sc_flow_main where flow_code=:flowCode",new MDataMap("flowCode", flowCode));
			if (map == null) {
				ret.setResultCode(949701003);
				ret.setResultMessage(bInfo(949701003));
				return ret;
			} else {
				MDataMap flowMain = new MDataMap(map);
				if (Integer.valueOf(flowMain.get("flow_isend").toString()) == 1) {
					ret.setResultCode(949701004);
					ret.setResultMessage(bInfo(949701004));
					return ret;
				} else if (!flowMain.get("current_status").equals(fromStatus)) {
					ret.setResultCode(949701006);
					ret.setResultMessage(bInfo(949701006));
					return ret;
				} else if (roleCode.equals("")) {
					ret.setResultCode(949701015);
					ret.setResultMessage(bInfo(949701015));
					return ret;
				} else {

					// 通过user 取得当前人的角色 配置。
					
					String[] roleAry = StringUtils.split(roleCode,WebConst.CONST_SPLIT_LINE);
					// 判断是否有权限
					boolean isExist = false;
					
					for (String role: roleAry) {
						if (flowMain.get("next_operators").indexOf(role) >= 0)
							isExist = true;
					}

					if (!isExist) {
						ret.setResultCode(949701015);
						ret.setResultMessage(bInfo(949701015));
						return ret;
					} else {
						SerializeSupport ss = new SerializeSupport<ScFlowMain>();
						ScFlowMain sfm = new ScFlowMain();
						ss.serialize(flowMain, sfm);

						MDataMap flowStatuschange = DbUp.upTable(
								"sc_flow_statuschange").one("flow_type",
								sfm.getFlowType(), "from_status", fromStatus, "to_status", toStatus);

						if (flowStatuschange == null) {
							
							if(toStatus.equals("44971724"))
							{
								// 调用 添加订单的存储过程
								List<SqlParameter> params = new ArrayList<SqlParameter>();
								params.add(new SqlOutParameter("outFlag",
										Types.VARCHAR));
								params.add(new SqlOutParameter("error",
										Types.VARCHAR));
								ChangeFlowCallableStatementCreator cscc = new ChangeFlowCallableStatementCreator(
										sfm, fromStatus, toStatus);

								DbTemplate dt = DbUp.upTable("sc_flow_main")
										.upTemplate();
								Map<String, Object> outValues = dt
										.getJdbcOperations().call(cscc, params);

								String returnCode = outValues.get("outFlag")
										.toString();
								
								if (Integer.parseInt(returnCode) != 1) {
									ret.setResultCode(Integer.parseInt(returnCode));
									ret.setResultMessage(bInfo(Integer
											.parseInt(returnCode)));
								}
								
								return ret;
							}
							else{
								ret.setResultCode(949701013);
								ret.setResultMessage(bInfo(949701013, flowCode));
								return ret;
							}
							
						} else {
							Func = flowStatuschange.get("chang_status_func");
							sfm.setUpdator(userCode);
							sfm.setFlowRemark(remark);
							FlowNextOperator fq = this.getNextAll(
									flowMain.get("flow_type"), toStatus);
							if (fq.getNextOperator().equals(""))
								sfm.setFlowIsend(1);
							sfm.setNextOperators(fq.getNextOperator());
							sfm.setNextOperatorStatus(fq
									.getNextOperatorStatus());

							/**
							 * 如果传入参数锁lockCode不等于空
							 */
							if(StringUtils.isNotBlank(lockCode)){
								uuid = wh.addLock(lockCode, 300);
							}else{
								uuid = wh.addLock(flowCode, 300);
							}
							if (uuid.equals("")) {
								ret.setResultCode(949701005);
								ret.setResultMessage(bInfo(949701005));
								return ret;
							}

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
														flowCode,
														flowMain.get(
																"outer_code")
																.toString(),
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

							// 调用 添加订单的存储过程
							List<SqlParameter> params = new ArrayList<SqlParameter>();
							params.add(new SqlOutParameter("outFlag",
									Types.VARCHAR));
							params.add(new SqlOutParameter("error",
									Types.VARCHAR));
							ChangeFlowCallableStatementCreator cscc = new ChangeFlowCallableStatementCreator(
									sfm, fromStatus, toStatus);

							DbTemplate dt = DbUp.upTable("sc_flow_main")
									.upTemplate();
							Map<String, Object> outValues = dt
									.getJdbcOperations().call(cscc, params);

							String returnCode = outValues.get("outFlag")
									.toString();
							if (Integer.parseInt(returnCode) != 1) {
								ret.setResultCode(Integer.parseInt(returnCode));
								ret.setResultMessage(bInfo(Integer
										.parseInt(returnCode)));

								// 解锁
								WebHelper.unLock(uuid);
								return ret;
							} else {

								try {
									if (flowFunc != null) {
										RootResult afterCode = flowFunc
												.afterFlowChange(
														flowCode,
														flowMain.get(
																"outer_code")
																.toString(),
														fromStatus, toStatus,mSubMap);
									}

								} catch (Exception e) {
									bLogError(949701008, Func);
									e.printStackTrace();
								}
								ret.setResultCode(1);

								// 解锁
								WebHelper.unLock(uuid);
								return ret;
							}
						}
					}
				}
			}
			// 调用之后的处理逻辑
		} catch (Exception ex) {
			ret.setResultCode(949701012);
			ret.setResultMessage(bInfo(949701012, ex.getMessage()));
			WebHelper.unLock(uuid);
			return ret;
		}
	}
	public RootResult ChangeFlow(String flowCode, String fromStatus,
			String toStatus, String userCode,String roleCode, String remark,MDataMap mSubMap){
		return  ChangeFlow(null,flowCode, fromStatus, toStatus, userCode, roleCode, remark, mSubMap);
	}
	/**
	 * 获取某个审批人的可以审批的记录
	 * 
	 * @param userCode
	 * @return
	 */
	public List<ScFlowMain> getApprovalFlowList(String userCode) {
		List<ScFlowMain> list = new ArrayList<ScFlowMain>();

		MDataMap afMapParam = new MDataMap();
		afMapParam.put("user_code", "%" + userCode + "%");
		List<MDataMap> scFlowMainListMap = DbUp.upTable("sc_flow_main").query(
				"", "", "next_operators like :user_code and flow_isend=0 ",
				afMapParam, -1, -1);

		if (scFlowMainListMap == null || scFlowMainListMap.size() == 0) {
			return list;
		} else {
			int size = scFlowMainListMap.size();

			SerializeSupport ss = new SerializeSupport<ScFlowMain>();

			for (int i = 0; i < size; i++) {
				ScFlowMain pic = new ScFlowMain();
				ss.serialize(scFlowMainListMap.get(i), pic);
				list.add(pic);
			}
		}

		return list;
	}

	/**
	 * 获取某个审批人的可以审批的记录
	 * 
	 * @param userCode
	 * @param flowCode
	 * @return
	 */
	public ScFlowMain getApprovalFlowByFL(String flowCode) {
		List<ScFlowMain> list = new ArrayList<ScFlowMain>();

		MDataMap afMapParam = new MDataMap();
		afMapParam.put("flow_code", flowCode);

		List<MDataMap> scFlowMainListMap = DbUp.upTable("sc_flow_main").query(
				"", "", "flow_isend=0 and flow_code=:flow_code", afMapParam,
				-1, -1);

		if (scFlowMainListMap == null || scFlowMainListMap.size() == 0) {
			return null;
		} else {

			int size = scFlowMainListMap.size();
			SerializeSupport ss = new SerializeSupport<ScFlowMain>();

			for (int i = 0; i < size; i++) {
				ScFlowMain pic = new ScFlowMain();
				ss.serialize(scFlowMainListMap.get(i), pic);
				list.add(pic);
			}

			return list.get(0);
		}
	}
	/**
	 * 
	 * 获取审批流程-通过外部编号
	 * 
	 * @param outerCode
	 * @return
	 */
	public ScFlowMain getApprovalFlowByOurterCode(String outerCode){
		MDataMap afMapParam = new MDataMap();
		afMapParam.put("outer_code", outerCode);
		List<MDataMap> scFlowMainListMap = DbUp.upTable("sc_flow_main").query("", "", "flow_isend=0 and outer_code=:outer_code", afMapParam,-1, -1);
		return getScFlowMain(scFlowMainListMap);
	}
	
	/**
	 * 
	 * 获取审批流程-通过外部编号;<br/>流程类型如下:
	 * 449717230014(商户审批(新));
	 * 449717230013(商品价格审批);
	 * 449717230012(商品库存审批);
	 * 449717230011(商品修改状态);
	 * 449717230010(商品状态);
	 * 449717230005(店铺模板审核);
	 * 449717230004(商家审核)
	 * @param outerCode
	 * @param flowType 流程类型
	 * @return
	 */
	public ScFlowMain getApprovalFlowByOurterCode(String outerCode, String flowType){
		MDataMap afMapParam = new MDataMap();
		afMapParam.put("outer_code", outerCode);
		afMapParam.put("flow_type", flowType);
		List<MDataMap> scFlowMainListMap = DbUp.upTable("sc_flow_main").query("", "", "flow_isend=0 and outer_code=:outer_code and flow_type=:flow_type", afMapParam, -1, -1);
		return getScFlowMain(scFlowMainListMap);
	}
	
	private ScFlowMain getScFlowMain(List<MDataMap> scFlowMainListMap) {
		ScFlowMain ret = null;
		if (scFlowMainListMap == null || scFlowMainListMap.size() == 0) {
			return null;
		} else {
			int size = scFlowMainListMap.size();
			SerializeSupport ss = new SerializeSupport<ScFlowMain>();
			for (int i = 0; i < size; i++) {
				ScFlowMain pic = new ScFlowMain();
				ss.serialize(scFlowMainListMap.get(i), pic);
				ret = pic;
				break;
			}
		}
		return ret;
	}

	/**
	 * 获取已经审批的审批单
	 * 
	 * @param operatorId
	 * @return
	 */
	public List<ScFlowMain> getAlreadyApprovalFlowList(String operatorId) {
		List<ScFlowMain> list = new ArrayList<ScFlowMain>();

		return list;
	}

	public FlowNextOperator getNextAll(String flowType, String toStatus) {
		FlowNextOperator ret = new FlowNextOperator();

		List<RoleStatus> roles = this.getRoleToStatusList(flowType, toStatus);

		if (roles == null || roles.size() == 0)
			return ret;
		else {
			ret.setNextOperator(this.getNextOperator(roles));
			ret.setNextOperatorStatus(this.getNextOpoeratorStatus(roles));
		}

		return ret;
	}

	/**
	 * 获取下一审批人状态
	 * 
	 * @param list
	 * @return
	 */
	private String getNextOpoeratorStatus(List<RoleStatus> list) {
		if (list == null || list.size() == 0)
			return "";
		else {
			MDataMap dataMap = new MDataMap();

			String ret = "";

			for (RoleStatus us : list) {
				ret += us.getRoleCode() + ":" + us.getToStatus() + ";";
			}

			if (ret.length() > 0)
				ret = ret.substring(0, ret.length() - 1);

			return ret;
		}
	}

	/**
	 * 获取下一级审批人
	 * 
	 * @param list
	 * @return
	 */
	private String getNextOperator(List<RoleStatus> list) {
		if (list == null || list.size() == 0)
			return "";
		else {
			MDataMap dataMap = new MDataMap();

			for (RoleStatus us : list) {
				if (!dataMap.contains(us.getRoleCode())) {
					dataMap.put(us.getRoleCode(), us.getRoleCode());
				}
			}
			String ret = "";
			for (String sKey : dataMap.upKeys()) {
				ret += dataMap.get(sKey) + ",";
			}
			if (ret.length() > 0)
				ret = ret.substring(0, ret.length() - 1);

			return ret;
		}
	}

	/**
	 * 获取角色 状态 对应关系
	 * 
	 * @param flowType
	 * @param toStatus
	 * @return
	 */
	public List<RoleStatus> getRoleToStatusList(String flowType, String toStatus) {
		List<RoleStatus> ret = new ArrayList<RoleStatus>();
		StringBuffer roleCodeStr = new StringBuffer();

		MDataMap sfsMapParam = new MDataMap();
		sfsMapParam.put("flow_type", flowType);
		sfsMapParam.put("from_status", toStatus);
		List<MDataMap> sfsListMap = DbUp.upTable("sc_flow_statuschange").query(
				"", "", " flow_type=:flow_type and from_status=:from_status",
				sfsMapParam, -1, -1);

		if (sfsListMap == null || sfsListMap.size() == 0)
			return ret;
		else {
			for (MDataMap dm : sfsListMap) {
				RoleStatus rs = new RoleStatus();

				rs.setRoleCode(dm.get("role_id"));
				rs.setToStatus(dm.get("to_status"));

				ret.add(rs);
			}
			return ret;
		}

	}

	/**
	 * 获取传入角色对应的用户 关联信息
	 * 
	 * @param roles
	 * @return
	 */
	public List<UserStatus> getUserToStatusList(List<RoleStatus> roles,String userCode) {
		List<UserStatus> ret = new ArrayList<UserStatus>();
		StringBuffer roleCodeStr = new StringBuffer();

		if (roles == null || roles.size() == 0)
			return ret;
		else {
			MDataMap urMapParam = new MDataMap();

			int i = 0;
			String whereStr = "";
			for (RoleStatus rs : roles) {
				urMapParam.put("role_code" + i, rs.getRoleCode());
				whereStr += " role_code=:role_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2)
				whereStr = whereStr.substring(0, whereStr.length() - 2);
			
			if(!whereStr.equals(""))
				whereStr="("+whereStr + ") and user_code=:user_code ";
			else
				whereStr=" user_code=:user_code ";
				
			
			urMapParam.put("user_code", userCode);

			List<MDataMap> UserStatusListMap = DbUp.upTable("za_userrole")
					.query("", "", whereStr, urMapParam, -1, -1);

			if (UserStatusListMap == null || UserStatusListMap.size() == 0)
				return ret;
			else {

				Map mapKey = new MStringMap();

				for (MDataMap dm : UserStatusListMap) {
					
					for (RoleStatus rs : roles) {
						if (rs.getRoleCode().equals(dm.get("role_code")))
						{
							UserStatus us = new UserStatus();
							us.setRoleCode(dm.get("role_code"));
							us.setUserCode(dm.get("user_code"));
							us.setToStatus(rs.getToStatus());
							
							if (!mapKey
									.containsKey(us.getUserCode() + us.getToStatus())) {
								ret.add(us);
								mapKey.put(us.getUserCode() + us.getToStatus(),
										us.getUserCode() + us.getToStatus());
							}
						}
					}
				}
				
				
				Collections.sort(ret, new Comparator() {  
			          public int compare(Object a, Object b) {
			            String one = ((UserStatus)a).getToStatus();  
			            String two = ((UserStatus)b).getToStatus();
			            String oneVal = ScFlowBase.getZWDefineNote(AppConst.ZW_DEFINE_FLOW_BUTTON, one);
			            String twoVal = ScFlowBase.getZWDefineNote(AppConst.ZW_DEFINE_FLOW_BUTTON, two);
			            if(oneVal.compareTo(twoVal)>0){
			            	return 1;
			            }else if(oneVal.compareTo(twoVal)<0){
			            	return -1;
			            }else{
			            	return one.compareTo(two);	
			            }
			          }  
			       }); 
				
				return ret;
			}
		}
	}
	
	public boolean isExistSP(String outerCode){
		int count = DbUp.upTable("sc_flow_main").count("outer_code",outerCode,"flow_isend","0");
		
		return count>0;
	}
	/**
	 * 
	 * 获取最新的审批流程-通过外部编号
	 * 
	 * @param outerCode
	 * @return
	 */
	public ScFlowMain getNewApprovalFlowByOurterCode(String outerCode){
		ScFlowMain ret = null;
		MDataMap afMapParam = new MDataMap();
		afMapParam.put("outer_code", outerCode);

		List<MDataMap> scFlowMainListMap = DbUp.upTable("sc_flow_main").query(
				"", "create_time desc", "outer_code=:outer_code", afMapParam,
				0, 1);

		if (scFlowMainListMap == null || scFlowMainListMap.size() == 0) {
			return null;
		} else {

			int size = scFlowMainListMap.size();
			SerializeSupport ss = new SerializeSupport<ScFlowMain>();
			for (int i = 0; i < size; i++) {
				ScFlowMain pic = new ScFlowMain();
				ss.serialize(scFlowMainListMap.get(i), pic);
				ret = pic;
				break;
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		//-18023
		System.out.println("同意".compareTo("驳回"));
		System.out.println("驳回".compareTo("同意"));
	}
}
