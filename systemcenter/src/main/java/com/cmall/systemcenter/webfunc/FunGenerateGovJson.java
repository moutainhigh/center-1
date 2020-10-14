package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.GovdistrictService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 生成行政区划json
 * @author huoqiangshou
 *
 */
public class FunGenerateGovJson extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		
		MWebResult result = new MWebResult();
		result.setResultCode(1);
		GovdistrictService gs = new GovdistrictService();
		result.setResultObject(gs.generateGovJson());
		return result;
	}

}
