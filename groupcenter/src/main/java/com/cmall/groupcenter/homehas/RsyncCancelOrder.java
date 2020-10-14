package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/***
 * 定时取消订单
 * @author jlin
 *
 */
public class RsyncCancelOrder extends RsyncHomeHas<RsyncConfigCancelOrder, RsyncRequestCancelOrder, RsyncResponseCancelOrder> {

	private final static RsyncConfigCancelOrder CONFIG_ORDER_CANCEL = new RsyncConfigCancelOrder();
	
	public RsyncConfigCancelOrder upConfig() {
		return CONFIG_ORDER_CANCEL;
	}

	private RsyncRequestCancelOrder requestCancelOrder = new RsyncRequestCancelOrder();

	public RsyncRequestCancelOrder upRsyncRequest() {
		
		return requestCancelOrder;
	}

	private RsyncResponseCancelOrder responseCancelOrder = new RsyncResponseCancelOrder();
	
	public RsyncResult doProcess(RsyncRequestCancelOrder tRequest, RsyncResponseCancelOrder tResponse) {
		
		this.responseCancelOrder = tResponse;
		
		RsyncResult mWebResult = new RsyncResult();
		
		String out_order_code = tRequest.getOrd_id();
		
//		//取消订单成功后，需要更新取消订单的日志表
//		if(tResponse.isSuccess()){
//			isSuccess=true;
//			DbUp.upTable("oc_order_cancel_h").dataUpdate(new MDataMap("out_order_code",out_order_code,"call_flag","0","update_time",DateUtil.getSysDateTimeString()), "call_flag,update_time", "out_order_code");
//		}else{
//			mWebResult.setResultCode(918501012);
//			mWebResult.setResultMessage(bInfo(918501012,out_order_code));
//			return mWebResult;
//		}
		
		
		//定时任务会每隔10分钟执行
		DbUp.upTable("oc_order_cancel_h").dataExec("update oc_order_cancel_h set call_flag=:call_flag , update_time=:update_time , cancel_count=cancel_count+1 where out_order_code=:out_order_code ", new MDataMap("out_order_code",out_order_code,"call_flag",(tResponse.isSuccess()?"0":"1"),"update_time",DateUtil.getSysDateTimeString()));
		
		return mWebResult;
	}

	public RsyncResponseCancelOrder upResponseObject() {

		return new RsyncResponseCancelOrder();
	}

	public RsyncResponseCancelOrder getResponseObject() {
		return responseCancelOrder;
	}
}
