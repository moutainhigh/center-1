package com.cmall.systemcenter.webfunc;

import java.util.List;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.model.UserStatus;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.service.ScFlowBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncFlowGetButtonHtml extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		if (mResult.upFlagTrue()) {
			
			FlowService fs = new FlowService();
			
			String flowCode=mDataMap.get("flow_code");
			String fromStatus= mDataMap.get("current_status");
			String userCode=UserFactory.INSTANCE.create().getUserCode();
			
			
			ScFlowMain sc = fs.getApprovalFlowByFL(flowCode);
			String buttonHtml = "";
			if(sc == null)
			{
				
			}
			else
			{
				List<RoleStatus> roles = fs.getRoleToStatusList(sc.getFlowType(), fromStatus);
				List<UserStatus> usList = fs.getUserToStatusList(roles,userCode);
				String buttonValue = "";
				for(UserStatus us : usList)
				{
					if(us.getUserCode().equals(userCode)){
						
						String toStatus = us.getToStatus();
						buttonValue=ScFlowBase.getZWDefineNote(AppConst.ZW_DEFINE_FLOW_BUTTON, toStatus);
						buttonHtml+="<input type='button' style='margin-right:25px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b7' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
					}
				}
				
				if(!buttonHtml.equals(""))
				{
					//buttonValue=bInfo(949701014, ScDefineService.getDefineNameByCode("44971724"));
					//buttonHtml+="<input type='button'  style='margin-right:5px;'  class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b7'  zap_tostatus_attr='44971724'  value='"+buttonValue+"'>";
				}
			}
			
			mResult.setResultMessage(buttonHtml);
		}
		
		return mResult;
	}
}