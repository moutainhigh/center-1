package com.cmall.groupcenter.func;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 自动转换账户
 * 
 * @author srnpr
 *
 */
public class FuncAutoAccount extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		return new GroupAccountSupport().aotoConvertAccount();
	}

}
