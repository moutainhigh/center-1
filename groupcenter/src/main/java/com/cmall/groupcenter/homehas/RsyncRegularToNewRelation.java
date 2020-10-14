package com.cmall.groupcenter.homehas;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncSellerInfoToLD.Dlr;
import com.cmall.groupcenter.homehas.RsyncSellerInfoToLD.TRequest;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncMemberRelation;
import com.cmall.groupcenter.homehas.model.RsyncModelRegularToNewRelation;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncRegularToNewRelation;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncRegularToNewRelation;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * LD老推新绑定活动
 * @author AngelJoy
 * @date 2020-03-19
 * @time 10:43
 *
 */
public class RsyncRegularToNewRelation extends
        RsyncHomeHas<RsyncConfigRsyncMemberRelation, RsyncRequestSyncRegularToNewRelation, RsyncResponseSyncRegularToNewRelation>{

	RsyncConfigRsyncMemberRelation RSYNC_REGULAR_TO_NEW_RELATION = new RsyncConfigRsyncMemberRelation();

	RsyncRequestSyncRegularToNewRelation paramList = new RsyncRequestSyncRegularToNewRelation();
	RsyncResponseSyncRegularToNewRelation  rsyncResponse = new RsyncResponseSyncRegularToNewRelation();
    public RsyncConfigRsyncMemberRelation upConfig() {
        return RSYNC_REGULAR_TO_NEW_RELATION;
    }

    public RsyncRequestSyncRegularToNewRelation upRsyncRequest() {
        return paramList;
    }

    public RsyncResponseSyncRegularToNewRelation upResponseObject() {
        return new RsyncResponseSyncRegularToNewRelation();
    }

    public static void main(String[] args) {


        RsyncRegularToNewRelation rsyncRegularToNewRelation = new RsyncRegularToNewRelation();
        rsyncRegularToNewRelation.doRsync();
    }



	@Override
	public RsyncResult doProcess(RsyncRequestSyncRegularToNewRelation tRequest, RsyncResponseSyncRegularToNewRelation tResponse) {
		rsyncResponse = tResponse;
		RsyncResult result = new RsyncResult();
		String flag = rsyncResponse.getSuccess();
		if("true".equals(flag)) {
			result.setResultCode(1);
		}else {
			result.setResultCode(0);
		}
		return result;
	}

	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseSyncRegularToNewRelation getResponseObject() {
		return rsyncResponse;
	}
	
	/**
	 * 传入商户编号，设置同步所需的请求参数
	 * @param smallSellerCode
	 * @return
	 */
	public void buildRequest(String member_code_regular,String member_code_new){
		RsyncModelRegularToNewRelation relation = new RsyncModelRegularToNewRelation();
		relation.setWeb_id(member_code_regular);
		relation.setScust_web_id(member_code_new);
		MDataMap regularInfo = DbUp.upTable("mc_login_info").one("member_code",member_code_regular);
		MDataMap newInfo = DbUp.upTable("mc_login_info").one("member_code",member_code_new);
		if(regularInfo !=null && !regularInfo.isEmpty()) {
			relation.setFcust_id(regularInfo.get("login_name"));
		}
		if(newInfo !=null && !newInfo.isEmpty()) {
			relation.setScust_id(newInfo.get("login_name"));
		}
		relation.setEtr_id("APP");
		relation.setEtr_date(DateUtil.getSysDateTimeString());
		paramList.getParamList().add(relation);
	}

}
