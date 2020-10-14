package com.cmall.systemcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowBussinesstype;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.model.UserStatus;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.service.ScFlowBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncFlowBussinessGetButtonHtml extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			FlowBussinessService fs = new FlowBussinessService();
			String flowType = mDataMap.get("flow_type");
			String flowbussinessid = mDataMap.get("flowbussinessid");
			String toStatus = mDataMap.get("to_status");
			
			MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",
					flowType);
			
			ScFlowBussinesstype sfbt = new ScFlowBussinesstype();
			if(flowTypeData == null)
			{
				mResult.setResultCode(949701024);
				mResult.setResultMessage(bInfo(949701024));
				return mResult;
			}
			else
			{
				SerializeSupport ss = new SerializeSupport<ScFlowBussinesstype>();
				ss.serialize(flowTypeData, sfbt);
			}
			
			
			MDataMap flowMain = DbUp.upTable(sfbt.getTableName()).one("uid",
					flowbussinessid);
			
			String fromStatus= "" ;
			if(flowMain == null)
			{
				mResult.setResultCode(949701025);
				mResult.setResultMessage(bInfo(949701025));
				return mResult;
			} else {
				fromStatus = flowMain.get(sfbt.getColumnName());
			}
			String fromStatusName = ScFlowBase.getDefineNameByCode(fromStatus);
			String buttonHtml = "";
			String userCode=UserFactory.INSTANCE.create().getUserCode();
			
			/*
			FlowService fss = new FlowService();
			List<RoleStatus> roles = fss.getRoleToStatusList(flowType, fromStatus);
			List<UserStatus> usList = fss.getUserToStatusList(roles,userCode);
			String buttonValue = "";
			for(UserStatus us : usList)
			{
				if(us.getUserCode().equals(userCode)){
					
					String toStatus = us.getToStatus();
					buttonValue=bInfo(949701014, ScFlowBase.getDefineNameByCode(toStatus));
					buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b9' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
				}
			}
			
			if(!buttonHtml.equals(""))
			{
				if(!buttonHtml.equals(""))
					buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			}
			
			*/
			
			/*
			
			List<RoleStatus> roles = fs.getToStatusList(flowType, fromStatus);
			String buttonValue = "";
			
			for(RoleStatus us : roles)
			{
				String toStatus = us.getToStatus();
				buttonValue=bInfo(949701014, ScDefineService.getDefineNameByCode(toStatus));
				buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b9' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
			}
			if(!buttonHtml.equals(""))
				buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			*/
			
			
			List<RoleStatus> roles = fs.getToStatusList(flowType, fromStatus,userCode);
			String buttonValue = "";
			
			for(RoleStatus us : roles)
			{
				// 如果指定了变更状态，则只显示目标状态按钮 
				if(StringUtils.isNotBlank(toStatus) && !toStatus.equalsIgnoreCase(us.getToStatus())){
					continue;
				}
				if("4497153900050007".equals(us.getToStatus())) {//4497153900050007 状态为用户取消售后单状态，不在商户页面中展示
					continue;
				}
				buttonValue=bInfo(949701014, ScFlowBase.getDefineNameByCode(us.getToStatus()));
				buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b9' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
			}
			
			if(!buttonHtml.equals(""))
				buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			
			mResult.setResultMessage(buttonHtml);
		}
		
		return mResult;
		
	}

}
