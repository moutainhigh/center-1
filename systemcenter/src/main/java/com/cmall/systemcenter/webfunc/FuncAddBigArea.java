package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.BigAreaService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 大区管理--》大区包含的省份
 * @author zhaoshuli
 *
 */
public class FuncAddBigArea extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		BigAreaService service = new BigAreaService();
		MWebResult result = service.saveBigArea(mDataMap);
		
		return result;
	}

}
