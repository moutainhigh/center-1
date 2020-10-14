package com.cmall.groupcenter.job;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.service.OrderService;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmassystem.Constants;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.helper.KvHelper;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 异步向LD下订单
 * @author jlin
 *
 */
public class JobForAddOrder extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String orderCode) {
		
		MWebResult mWebResult = new MWebResult();
		//根据订单号查询是否是拼团单。
		MDataMap groupOrderMap = DbUp.upTable("sc_event_collage_item").one("collage_ord_code",orderCode);
		if(groupOrderMap != null && !groupOrderMap.isEmpty()){//不为空时，证明是拼团单，然后检查是否已经拼团成功。
			String collageCode = groupOrderMap.get("collage_code");
			MDataMap collageInfo = DbUp.upTable("sc_event_collage").one("collage_code",collageCode);
			//判断此团是否拼团成功
			String collageStatus = collageInfo.get("collage_status");
			if(!"449748300002".equals(collageStatus)){//非拼团成功的订单做以下操作
				//操作失败标识
				mWebResult.setResultCode(99);
				return mWebResult;
			}
		}
		MDataMap orderMap = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
		
		// 0元单时再判断一下订单状态，如果是未支付状态则延迟一下再同步
		if("4497153900010001".equals(orderMap.get("order_status")) 
				&& BigDecimal.ZERO.compareTo(new BigDecimal(orderMap.get("due_money"))) == 0) {
			//操作失败标识
			mWebResult.setResultCode(99);
			return mWebResult;
		}
		
		// 异步下单时锁定一下订单，防止订单在下单成功的同时订单又被用户取消的情况
		String lockKey = KvHelper.lockCodes(20, Constants.LOCK_ORDER_UPDATE + orderCode);
		if(StringUtils.isBlank(lockKey)) {
			// 订单正常操作中，请稍候重试！
			mWebResult.inErrorMessage(918590001);
			return mWebResult;
		}
		
		if(orderMap==null||"4497153900010006".equals(orderMap.get("order_status"))){
			DbUp.upTable("lc_change_channel").dataUpdate(new MDataMap("is_success","0", "is_send","1", "order_code",orderCode,
					"comment","order_status:4497153900010006"), "is_success,is_send,comment", "order_code");
			return mWebResult;
		}
		
		String buyer_code=orderMap.get("buyer_code");
		String out_code=orderMap.get("out_order_code");
		if(StringUtils.isNotBlank(out_code)){
			return mWebResult;
		}
		
		MemberLoginSupport memberLoginSupport = new MemberLoginSupport();
		String mobileid = memberLoginSupport.getMoblie(buyer_code);
		
		//查询订单相关信息
		OrderService service = new OrderService();
		
		mWebResult = service.rsyncOrder2(orderCode, mobileid);
		//检查一下订单是否已经被取消了
		if(!mWebResult.upFlagTrue()){
			if(DbUp.upTable("oc_orderinfo").count("order_code",orderCode,"order_status","4497153900010006")>0) {
				MDataMap mDataMap = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				String out_order_code=mDataMap.get("out_order_code");
				String now=DateUtil.getSysDateTimeString();
				if(StringUtils.isNotBlank(out_order_code)){
					DbUp.upTable("oc_order_cancel_h").insert("order_code",orderCode,"buyer_code",buyer_code,"out_order_code",out_order_code,"call_flag","1","create_time",now,"update_time",now,"canceler","system");
				}
			}
		}
		
		// 操作执行完成解除锁定
		KvHelper.unLockCodes(lockKey, Constants.LOCK_ORDER_UPDATE + orderCode);
		
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990002");
		setSendTime(config);
	}
	
	public static void setSendTime(ConfigJobExec config) {
		String st = TopUp.upConfig("groupcenter.repeat_send_time");
		int sendTime = 70;
		try {
			sendTime = Integer.parseInt(st);
		} catch(Exception e) {}
		config.setMaxExecNumber(sendTime);
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

	
}
