package com.cmall.bbcenter.service;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Created by zhaoshuli on 14-4-16.
 */
public class SupplierBalanceService extends BaseClass {

	private static String TABLE_BC_SUPPLIER_BALANCE = "bc_supplier_balance";

	/*
	 * 根据uuid获得供应商信息
	 */
	public MDataMap getSupplierInfoByUid(String uid) {

		MDataMap map = new MDataMap();
		map.put("uid", uid);
		List<Map<String, Object>> reList = DbUp
				.upTable("bc_supplier_info")
				.dataQuery(
						" supplier_code,linkman,acount_name,account_bank,account_no,mobile,email,company_name ",
						"", "", map, 0, 0);
		map.clear();
		if (!reList.isEmpty()) {
			for (Map<String, Object> sup : reList) {
				map.put("supplier_code", (String) sup.get("supplier_code"));
				map.put("linkman", (String) sup.get("linkman"));
				map.put("acount_name", (String) sup.get("acount_name"));
				map.put("account_bank", (String) sup.get("account_bank"));
				map.put("account_no", (String) sup.get("account_no"));
				map.put("mobile", (String) sup.get("mobile"));
				map.put("email", (String) sup.get("email"));
				map.put("company_name", (String) sup.get("company_name"));
			}
		}
		return map;

	}

	/**
	 * 保存数据
	 * 
	 * @param dataMap
	 * @return
	 */
	public MWebResult doSaveSupperlierBalance(MDataMap dataMap) {
		MWebResult mResult = new MWebResult();

		try {
			mResult.setResultCode(1);
			mResult.setResultMessage("ok");
			mResult.setResultObject("aaa('hello')");
			mResult.setResultType("116018010");
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("balance_name", dataMap.get("zw_f_balance_name"));
			mDataMap.put("balance_account", dataMap.get("zw_f_balance_account"));
			mDataMap.put("flag_enables", dataMap.get("zw_f_flag_enables"));
			mDataMap.put("supplier_code", dataMap.get("zw_f_supplier_code"));

			if (StringUtils.isNotBlank(dataMap.get("zw_f_uid"))) {
				mDataMap.put("uid", dataMap.get("zw_f_uid"));
				DbUp.upTable(TABLE_BC_SUPPLIER_BALANCE)
						.dataUpdate(
								mDataMap,
								"balance_name,balance_account,flag_enables,supplier_code",
								"uid");
			} else {
				DbUp.upTable(TABLE_BC_SUPPLIER_BALANCE).dataInsert(mDataMap);
			}
		} catch (Exception e) {
			mResult.setResultMessage("" + e.getMessage());
			return mResult;
		}
		return mResult;

	}

	/**
	 * 
	 * 保存供应商登陆信息
	 * 
	 * @param dataMap
	 * @return
	 */
	public MWebResult doAddSupperlierUserInfo(MDataMap dataMap) {
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(0);
		mResult.setResultMessage("保存成功");

		mResult.setResultObject("refushPage("+mResult.getResultCode()+",'"+mResult.getResultMessage()+"')");
		mResult.setResultType("116018010");
		String passwd = dataMap.get("zw_f_password"), repeatePasswd = dataMap
				.get("zw_f_repeate_password"),haveSave= dataMap
						.get("haveSaved");
		if (!passwd.equals(repeatePasswd)) {
			mResult.setResultCode(0);
			mResult.setResultMessage("密码不一致");
			return mResult;
		}

		String userCOde = WebHelper.upCode("UI"), uid = dataMap.get("uid");
		try {
			MDataMap insertDatamap = new MDataMap();
			String nowTime = DateUtil.getNowTime();
			insertDatamap.put("user_code", userCOde);
			insertDatamap.put("uid", uid);
			insertDatamap.put("manage_code", dataMap.get("zw_f_manage_code"));
			insertDatamap.put("create_time", nowTime);
			insertDatamap.put("user_name", dataMap.get("zw_f_login_name"));
			insertDatamap.put("user_password",
					SecrurityHelper.MD5Customer(dataMap.get("zw_f_password")));
			insertDatamap.put("user_type_did", "467721200005");
			if ("no".equals(haveSave)) {  //没有保存
				DbUp.upTable("za_userinfo").dataInsert(insertDatamap);
			} else {
				DbUp.upTable("za_userinfo").dataUpdate(insertDatamap,
						"user_password", "uid"); // 更新密码
			}

		} catch (Exception e) {
			mResult.setResultCode(909401004);
			mResult.setResultMessage(bInfo(909401004));
			mResult.setResultObject("refushPage("+mResult.getResultCode()+",'"+mResult.getResultMessage()+"')");
			return mResult;
		}
		if ("no".equals(haveSave)) { // 不为空的时候添加用户角色表
			MDataMap insertDatamap = new MDataMap();
			insertDatamap.put("user_code", userCOde);
			insertDatamap.put("role_code", "4677031800030001"); // 供应商角色
			try {
				DbUp.upTable("za_userrole").dataInsert(insertDatamap);
			} catch (Exception e) {
				e.printStackTrace();
				mResult.setResultCode(959701008);
				mResult.setResultMessage(bInfo(959701008));
				mResult.setResultObject("refushPage("+mResult.getResultCode()+",'"+mResult.getResultMessage()+"')");

				return mResult;
			}
		}

		return mResult;
	}
	
	/**
	 * 根据供应商查询 登陆信息
	 * @param supperlierCode
	 * @return
	 */
	public String getUserInfoBySupperlierCode(String supperlierCode) {
		String  userName = "";
		MDataMap map = new MDataMap();
		map.put("manage_code", supperlierCode);
		List<Map<String, Object>> reList = DbUp
				.upTable("za_userinfo")
				.dataQuery(
						"  user_name  ",
						"", "", map, 0, 0);
		map.clear();
		 for(Map<String,Object> m :reList){
			  userName = (String)m.get("user_name");
		 }
		return userName;
	}
	
}
