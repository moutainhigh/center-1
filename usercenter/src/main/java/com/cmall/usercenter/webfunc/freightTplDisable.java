package com.cmall.usercenter.webfunc;

import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 快递禁用
 * @author huoqiangshou
 *
 */
public class freightTplDisable extends RootFunc {

	private static String TABLE_TPL="uc_freight_tpl"; //运费模板
	private static String TABLE_PRODUCT="pc_productinfo";//商品表
	private static String TEMPLATE_FIELD="transport_template";//运费模板字段
	
	/**
	 * 运费模板启用/禁用，关联了商品的模板不能禁用
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		//模板状态 449746250002 :否
		String isDisable = mDataMap.get("zw_f_isDisable");
		if("449746250002".equals(isDisable)){  //禁用的话 记录时间
			//查询模板关联的商品
			int ct = DbUp.upTable(TABLE_PRODUCT).dataCount(TEMPLATE_FIELD +"=:uid", dataMap);
			if(ct>0){ //不能禁用
				mResult.setResultCode(959701028);
				mResult.setResultMessage("模板已经关联商品不能禁用！");
			}else{
				dataMap.put("isDisable", "449746250001");
				dataMap.put("disableDate", FormatHelper.upDateTime());
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "isDisable,disableDate", "uid");
			}
		}else{
			dataMap.put("isDisable", "449746250002");
			dataMap.put("disableDate", FormatHelper.upDateTime());
			DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "isDisable,disableDate", "uid");
		}
		
		PlusHelperNotice.onChangeFreight(tplUid);
		
		return mResult;
	}

}
