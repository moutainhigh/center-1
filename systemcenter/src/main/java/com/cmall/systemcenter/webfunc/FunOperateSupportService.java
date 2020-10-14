package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.BigAreaService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FunOperateSupportService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		BigAreaService service = new BigAreaService();
		MWebResult result = service.saveOperateSupport(mDataMap);
		
		return result;
	}

}
