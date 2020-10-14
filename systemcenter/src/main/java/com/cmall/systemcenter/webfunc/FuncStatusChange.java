package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.StatusChangeService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 更改以某列的值为查询条件查出数据的另一列的状态
 * */
public class FuncStatusChange extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			StatusChangeService fs = new StatusChangeService();
			
			String fieldName=mSubMap.get("field_name");
			String fieldValue=mSubMap.get("field_value");
			String fromStatus= mSubMap.get("from_status");
			String toStatus=mSubMap.get("to_status");
			String flowType = mSubMap.get("flow_type");
			
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			
			String userCode=userInfo.getUserCode();
			String remark=mSubMap.get("remark");
				
			RootResult ret =
					fs.ChangeFlow(fieldName, fieldValue, flowType, fromStatus, toStatus, userCode, remark, mSubMap);
			
			mResult.setResultCode(ret.getResultCode());
			if(ret.getResultCode() == 1)
				mResult.setResultMessage(bInfo(949701000));
			else
				mResult.setResultMessage(ret.getResultMessage());

		}

		return mResult;
	}

}
