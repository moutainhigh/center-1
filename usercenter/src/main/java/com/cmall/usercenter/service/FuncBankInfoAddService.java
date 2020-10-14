package com.cmall.usercenter.service;

import org.apache.commons.lang3.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncBankInfoAddService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String bankName = mSubMap.get("bank_name");
		String ldCode = mSubMap.get("ld_code");
		String ncCode = mSubMap.get("nc_code");
		String bankStatus = mSubMap.get("bank_status");
		int count = DbUp.upTable("uc_bankinfo").count("bank_name", bankName);
		if(count>0) {
			result.setResultMessage("该银行名称已经存在请重新填写");
			return result;
		}
		String createTime = DateUtil.getSysDateTimeString();
		String loginName = UserFactory.INSTANCE.create().getLoginName();
		MDataMap map = new MDataMap();
		map.put("bank_name", bankName);
		map.put("bank_code", WebHelper.upCode("1010"));
		map.put("bank_status", bankStatus);
		map.put("ld_code", StringUtils.trimToEmpty(ldCode));
		map.put("nc_code", StringUtils.trimToEmpty(ncCode));
		map.put("create_time", createTime);
		map.put("creator", loginName);
		map.put("update_time", createTime);
		map.put("updator", loginName);
		DbUp.upTable("uc_bankinfo").dataInsert(map);
		
		map.remove("update_time");
		map.remove("updator");
		
		map.put("old_bank_name", bankName);
		DbUp.upTable("uc_bankinfo_history").dataInsert(map);
		//
		return result;
	}

}
