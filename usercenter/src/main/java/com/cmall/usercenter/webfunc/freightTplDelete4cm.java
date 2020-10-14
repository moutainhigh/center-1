package com.cmall.usercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除模板和明细
 * @author huoqiangshou
 *
 */
public class freightTplDelete4cm extends RootFunc {
	private static String TABLE_TPL="uc_freight_tpl"; //运费模板
	
	private static String TABLE_TPL_DETAL="uc_freight_tpl_detail"; //运费模板明细
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		String tplId = mDataMap.get("zw_f_uid");
		//模板表
		DbUp.upTable(TABLE_TPL).dataDelete(" uid='"+tplId+"'", null, "");
		//明细
		DbUp.upTable(TABLE_TPL_DETAL).dataDelete(" uid='"+tplId+"'", null, "");
		
		return mResult;
	}

}
