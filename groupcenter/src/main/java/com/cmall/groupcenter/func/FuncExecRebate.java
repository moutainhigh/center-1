package com.cmall.groupcenter.func;

import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.groupcenter.support.RebateOrderSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncExecRebate extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult=new MWebResult();
	    RebateOrderSupport rebateOrderSupport=new RebateOrderSupport();
	    mWebResult.inOtherResult(rebateOrderSupport.rebateAllOrders());
		return mWebResult;
	}

	
}
