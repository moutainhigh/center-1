package com.cmall.groupcenter.func.wonderfuldiscovery;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncEdit;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 模块:精彩发现
 * 功能:修改数据
 * @author LHY
 * 2015年1月13日 下午6:38:41
 */
public class FuncEditWonderfulDiscovery extends RootFunc{

	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		mDataMap.put("zw_f_update_time", DateUtil.getSysDateTimeString());
		mDataMap.put("zw_f_updator", UserFactory.INSTANCE.create().getLoginName());
		FuncEdit funcEdit = new FuncEdit();
		mWebResult = funcEdit.funcDo(sOperateUid, mDataMap);
		return mWebResult;
	}

}
