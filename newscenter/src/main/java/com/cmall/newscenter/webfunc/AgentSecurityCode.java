package com.cmall.newscenter.webfunc;

import java.util.UUID;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 生成二维码
 * 
 * @author shiyz date 2016-03-17
 * @version 1.0
 */
public class AgentSecurityCode extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap batchMap = new MDataMap();
		// 生成箱的数量
		int security_num = Integer.valueOf(mAddMaps.get("security_xnum"));
		// 生成盒的数量
		int security_hnum = Integer.valueOf(mAddMaps.get("security_hnum"));

		/* 防伪批次 */
		String security_batch = FormatHelper.upDateTime().replace("-", "")
				.replace(":", "").trim().replace(" ", "")
				+ Math.round(Math.random() * 9000 + 1000);

		/* 系统当前时间 */
		String create_time = com.cmall.newscenter.util.DateUtil.getNowTime();

		batchMap.put("security_generationtime", create_time);

		batchMap.put("security_batch", security_batch);

		batchMap.put("security_productname",
				mAddMaps.get("security_productname"));

		batchMap.put("security_productcode",
				mAddMaps.get("security_productcode"));
		// 生成箱的数量
		batchMap.put("security_xnum", mAddMaps.get("security_xnum"));
		// 生成盒的数量
		batchMap.put("security_hnum", mAddMaps.get("security_hnum"));
		/** 将箱防伪码信息插入nc_agent_dimensional表中 */
		DbUp.upTable("nc_agent_dimensional").dataInsert(batchMap);

		MDataMap mdDataMap = new MDataMap();

		if (security_num != 0) {

			for (int i = 0; i < security_num; i++) {

				String security_code = "" + bConfig("newscenter.wxchat_url")
						+ "?code=" + batchMap.get("security_productcode") + "-"
						+ UUID.randomUUID() + "&app=liujialing&type=xiang";

				mdDataMap.put("securityx_code", security_code);

				mdDataMap.put("security_batch", security_batch);

				mdDataMap.put("security_productname",
						mAddMaps.get("security_productname"));

				mdDataMap.put("security_productcode",
						mAddMaps.get("security_productcode"));

				/** 将盒防伪码信息插入nc_agent_details表中 */
				DbUp.upTable("nc_agent_details").dataInsert(mdDataMap);

				MDataMap hDataMap = new MDataMap();
				
				// 关联箱的二维码生成盒的二维码
				if (security_hnum != 0) {

					for (int j = 0; j < security_hnum; j++) {

						String agent_code = ""
								+ bConfig("newscenter.wxchat_url") + "?code="
								+ batchMap.get("security_productcode") + "-"
								+ UUID.randomUUID() + "&app=liujialing&type=he";
						

						hDataMap.put("security_batch", security_batch);

						hDataMap.put("security_productname",
								mAddMaps.get("security_productname"));

						hDataMap.put("security_productcode",
								mAddMaps.get("security_productcode"));

						hDataMap.put("securityx_code", security_code);

						hDataMap.put("securityh_code", agent_code);

						/** 将盒防伪码信息插入nc_agent_details表中 */
						DbUp.upTable("nc_agent_details").dataInsert(hDataMap);
					}
				}

			}
		}

		return mResult;
	}
}
