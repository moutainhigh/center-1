package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.GovdistrictService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 初始化行政区划
 * @author huoqiangshou
 *
 */
public class FunInitGovdistrict  extends RootFunc{

	/* (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		
		MWebResult result = new MWebResult();
		result.setResultCode(1);
		GovdistrictService gs = new GovdistrictService();
		result.setResultObject(gs.initData());
		return result;
	}

}
