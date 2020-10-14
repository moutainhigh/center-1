package com.cmall.systemcenter.webfunc;

import java.util.List;

import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.model.UserStatus;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncFlowStatusAdd  extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			
			FlowService fs = new FlowService();
			
			String flowType=mSubMap.get("flow_type");
			String fromStatus= mSubMap.get("from_status");
			String toStatus= mSubMap.get("to_status");
			String roleCode = mSubMap.get("role_id");
			String webFunc = mSubMap.get("chang_status_func");
			
			//如果是 FlowBussiness 一定是null,这个事公用的方法 FlowService 和 FlowBussinessService
			if(roleCode == null)
				roleCode = "all";
		
			RootResult rr =fs.AddChangeStatus(flowType, fromStatus, toStatus, webFunc, roleCode);
			
			mResult.setResultCode(rr.getResultCode());
			mResult.setResultMessage(rr.getResultMessage());
			
			if(rr.getResultCode() == 1)
				mResult.setResultMessage(bInfo(949701000));
			
		}
		
		return mResult;
	}

}
