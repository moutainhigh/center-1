package com.cmall.groupcenter.mq.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.mq.model.CustLvlListenModel;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class MqCustService {

	public MWebResult reginRsyncCustLvl(CustLvlListenModel custLvl) {
		MWebResult mWebResult = new MWebResult();
		String cust_id = null == custLvl.getCust_id() ? "" : custLvl.getCust_id();
		String vip_level = null == custLvl.getCust_lvl_cd() ? "" : custLvl.getCust_lvl_cd();
		if(StringUtils.isNotEmpty(cust_id) && StringUtils.isNotEmpty(vip_level)) {
			DbUp.upTable("mc_extend_info_homehas").dataUpdate(new MDataMap("homehas_code", cust_id, "vip_level", vip_level), "vip_level", "homehas_code");
		}
		return mWebResult;
	}

}
