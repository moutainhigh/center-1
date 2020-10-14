package com.cmall.newscenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.service.ScoreFlowBussinessService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncFlowAgencyChange extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			ScoreFlowBussinessService fs = new ScoreFlowBussinessService();
			
			String flowBussinessUid=mSubMap.get("flow_bussinessid");
			String fromStatus= mSubMap.get("from_status");
			String toStatus=mSubMap.get("to_status");
			String flowType = mSubMap.get("flow_type");
			
			String level_number = mSubMap.get("level_number"); 
			
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			
			String userCode=userInfo.getUserCode();
			String remark = StringUtils.isEmpty(mSubMap.get("remark")) ?"remark" : mSubMap.get("remark");
			RootResult ret =
					fs.ChangeAgencyFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, mSubMap,level_number);
			
			mResult.setResultCode(ret.getResultCode());
			if(ret.getResultCode() == 1)
				mResult.setResultMessage(bInfo(949701000));
			else
				mResult.setResultMessage(ret.getResultMessage());

		}
		return mResult;
	}

}
