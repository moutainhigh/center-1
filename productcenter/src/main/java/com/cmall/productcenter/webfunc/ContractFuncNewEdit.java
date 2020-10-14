package com.cmall.productcenter.webfunc;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncEdit;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
* @ClassName: contractTypeFuncAdd 
* @Description: 编辑合同类型（新）
* @author 李泽帆
* @date 2017年11月17日11:01:05
*  
*/
public class ContractFuncNewEdit extends FuncEdit {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap dataMap = new MDataMap();
		String create_time = DateUtil.getSysDateTimeString();// 系统当前时间
		/* 获取当前登录人 */
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		dataMap.put("zw_f_update_time", create_time);
		dataMap.put("zw_f_update_user", create_user);
		String startTime = mDataMap.get("zw_f_start_time");
		String endTime = mDataMap.get("zw_f_end_time");
		dataMap.put("zw_f_uid", mDataMap.get("zw_f_uid"));
		dataMap.put("zw_f_contract_code", mDataMap.get("zw_f_contract_code"));
		dataMap.put("zw_f_contract_type", mDataMap.get("zw_f_contract_type"));
		dataMap.put("zw_f_taxpayer_type", mDataMap.get("zw_f_taxpayer_type"));
		dataMap.put("zw_f_sale_deduction", mDataMap.get("zw_f_sale_deduction"));
		dataMap.put("zw_f_special_terms", mDataMap.get("zw_f_special_terms"));
		dataMap.put("zw_f_small_seller_code", mDataMap.get("zw_f_small_seller_code"));
		dataMap.put("zw_f_start_time", mDataMap.get("zw_f_start_time"));
		dataMap.put("zw_f_end_time", mDataMap.get("zw_f_end_time"));
		if (DateUtil.compareTime(endTime, startTime,DateUtil.DATE_FORMAT_DATEONLY) <= 0) {
			mResult.setResultCode(-1);
			mResult.setResultMessage("合同到期日要大于合同开始日");
			return mResult;
		} else if (DateUtil.compareTime(endTime,DateUtil.getSysDateTimeString(),DateUtil.DATE_FORMAT_DATEONLY) <= 0) {
			mResult.setResultCode(-2);
			mResult.setResultMessage("合同到期日要大于当前时间");
			return mResult;
		}
		String contractType = mDataMap.get("zw_f_contract_type");
		MDataMap cp = DbUp.upTable("fh_contract_type").oneWhere(
				"contract_type_name", "", "", "contract_type_code",
				contractType);
		if (cp != null && !"解除协议".equals(cp.get("contract_type_name"))) {
			dataMap.put("zw_f_dissolution_time", "");
			dataMap.put("zw_f_dissolution_instructions", "");
		} else {
			String dissolutionTime = mDataMap.get("zw_f_dissolution_time");
			if (DateUtil.compareTime(dissolutionTime, startTime ,DateUtil.DATE_FORMAT_DATEONLY) <= 0) {
				mResult.setResultCode(-3);
				mResult.setResultMessage("解除协议日期要大于开始日期");
				return mResult;
			}
		}
		dataMap.put("zw_f_dissolution_time", mDataMap.get("zw_f_dissolution_time"));
		dataMap.put("zw_f_dissolution_instructions", mDataMap.get("zw_f_dissolution_instructions"));
		MDataMap maddMap = dataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String contractCode = mDataMap.get("zw_f_contract_code");
		MDataMap whereMap = new MDataMap();
		whereMap.put("contract_code", contractCode);
		whereMap.put("uid", mDataMap.get("zw_f_uid"));
		int count = DbUp.upTable("fh_contract_new").dataCount("contract_code=:contract_code and uid<>:uid", whereMap);
		if(count > 0){
			mResult.setResultCode(-4);
			mResult.setResultMessage("合同编号不能重复");
			return mResult;
		}
		try {
			DbUp.upTable("fh_contract_new").dataUpdate(maddMap, "", "uid");
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(959701033);
		}
		return mResult;
	}

}
