package com.cmall.groupcenter.func;

import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 执行清分
 * 
 * @author srnpr
 * 
 */
public class FuncExecReckon extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
		groupReckonSupport.reckonAllOrders();

		return new MWebResult();

	}

}
