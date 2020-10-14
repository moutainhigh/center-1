package com.cmall.bbcenter.webfunc;

import com.cmall.bbcenter.service.SupplierBalanceService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加供应商登陆信息
 * @author zhaoshuli
 *
 */
public class AddSupperlierUserInfo extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		  MWebResult mResult = null;
		  SupplierBalanceService service = new SupplierBalanceService();
	      mResult = service.doAddSupperlierUserInfo(mDataMap);
	      
		return mResult;
	}

}
