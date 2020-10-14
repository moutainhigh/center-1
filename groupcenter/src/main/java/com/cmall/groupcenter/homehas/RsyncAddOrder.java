package com.cmall.groupcenter.homehas;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 同步订单信息
 * @author jl
 *
 */
public class RsyncAddOrder extends RsyncHomeHas<RsyncConfigAddOrder, RsyncRequestAddOrder, RsyncResponseAddOrder> {

	private final static RsyncConfigAddOrder rsyncConfigAddOrder = new RsyncConfigAddOrder();
	private String statusCode="";//状态码
	
	public RsyncConfigAddOrder upConfig() {
		return rsyncConfigAddOrder;
	}

	private RsyncRequestAddOrder rsyncRequestAddOrder = new RsyncRequestAddOrder();

	public RsyncRequestAddOrder upRsyncRequest() {
		
		return rsyncRequestAddOrder;
	}

	public RsyncResult doProcess(RsyncRequestAddOrder tRequest, RsyncResponseAddOrder tResponse) {
		
		statusCode=tResponse.getStatus();//状态码
		
		RsyncResult mWebResult = new RsyncResult();
		String Web_ord_id=tRequest.getWeb_ord_id();
		
		if(!tResponse.isSuccess()){
			mWebResult.setResultCode(918501004);
			mWebResult.setResultMessage(bInfo(918501004,Web_ord_id));
			return mWebResult;
		}
		
		//把家有返回的数据保存一份
		String order_code = tRequest.getWeb_ord_id();
		String ord_id = tResponse.getOrd_id();
		String dlv_add_seq = tResponse.getDlv_add_seq();
		String cust_id = tResponse.getCust_id();
		String create_time = DateUtil.getSysDateTimeString();
		
		// 如果有返回订单号则更新到表中
		if(StringUtils.isNotBlank(ord_id)){
			//更新order表的out_order_code
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"out_order_code",ord_id), "out_order_code", "order_code");
		}
		
		// 状态码非空且不是S00则表示失败，解决黑名单用户下单success返回true的问题
		if(StringUtils.isNotBlank(tResponse.getStatus()) && !"S00".equalsIgnoreCase(tResponse.getStatus())){
			mWebResult.setResultCode(918501004);
			mWebResult.setResultMessage(bInfo(918501004,Web_ord_id));
			return mWebResult;
		}
		
		MDataMap dataMap=new MDataMap();
		dataMap.put("order_code", order_code);
		if(StringUtils.isEmpty(ord_id)) {
			ord_id = "BLANK-" + System.currentTimeMillis();
		}
		dataMap.put("ord_id", ord_id);
		if(cust_id!=null){
			dataMap.put("cust_id", cust_id);
		}
		if(dlv_add_seq!=null){
			dataMap.put("dlv_add_seq", dlv_add_seq);
		}
		
		dataMap.put("create_time", create_time);
		
		DbUp.upTable("oc_order_homehas").dataInsert(dataMap);
		
		MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
		String buyer_code = orderInfo.get("buyer_code");
		
		//DbUp.upTable("mc_extend_info_homehas").dataExec("update mc_extend_info_homehas set homehas_code=:homehas_code where member_code=:buyer_code and homehas_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		//DbUp.upTable("mc_extend_info_homepool").dataExec("update mc_extend_info_homepool set old_code=:homehas_code where member_code=:buyer_code and old_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		//DbUp.upTable("mc_extend_info_star").dataExec("update mc_extend_info_star set old_code=:homehas_code where member_code=:buyer_code and old_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		
		//DbUp.upTable("mc_extend_info_homehas").oneWhere("", "", sWhere, sParams);
		
		// 保存返回的用户编号
		if(StringUtils.isNotBlank(buyer_code) && StringUtils.isNotBlank(cust_id)){
			if(DbUp.upTable("mc_extend_info_homehas").count("member_code", buyer_code, "homehas_code", cust_id) == 0){
				MDataMap homehas = new MDataMap("member_code", buyer_code, "homehas_code", cust_id);
				DbUp.upTable("mc_extend_info_homehas").dataInsert(homehas);
			}
		}
		
		return new RsyncResult();
	}

	public RsyncResponseAddOrder upResponseObject() {

		return new RsyncResponseAddOrder();
	}

	/**
	 * 返回状态码
	 * @return
	 */
	public String getStatusCode(){
		return statusCode;
	}
}
