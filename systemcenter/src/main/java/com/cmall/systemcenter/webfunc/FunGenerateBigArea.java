package com.cmall.systemcenter.webfunc;

import com.cmall.systemcenter.service.BigAreaService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 生产大区域json
 * @author zhaoshuli
 *
 */
public class FunGenerateBigArea extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		BigAreaService service = new BigAreaService();
		MWebResult result = service.generateBigArea(mDataMap); 
		
		return result;
	}

}
