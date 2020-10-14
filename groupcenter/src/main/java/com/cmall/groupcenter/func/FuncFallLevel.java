package com.cmall.groupcenter.func;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 降级操作
 * 
 * @author srnpr
 * 
 */
public class FuncFallLevel extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		return new GroupAccountSupport().autoFallAccountLevel();
	}

}
