package com.cmall.groupcenter.job;

import java.math.BigDecimal;

import com.cmall.systemcenter.webfunc.FuncFlowBussinessChange;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 渠道商退货售后完成后，返还预付款定时
 * @author jlin
 *
 */
public class JobForReturnChannelBalance extends RootJobForExec {
	
	@Override
	public IBaseResult execByInfo(String returnCode) {

		MWebResult mWebResult = new MWebResult();
		//根据订单号查询是否是渠道商售后。
		MDataMap asaleOrder = DbUp.upTable("oc_order_after_sale").one("asale_code",returnCode);
		if(asaleOrder == null || asaleOrder.isEmpty()) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("售后单不存在");
			return mWebResult;
		}
		String asale_source = asaleOrder.get("asale_source");
		if(!"4497477800060003".equals(asale_source)) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("售后单不是渠道商售后");
			return mWebResult;
		}
		String orderCode = asaleOrder.get("order_code");
		//订单信息
		MDataMap channelOrder = DbUp.upTable("oc_order_channel").oneWhere("channel_seller_code", "","order_code=:order_code", "order_code", orderCode);
		if(channelOrder == null || channelOrder.isEmpty()) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("渠道商订单不存在");
			return mWebResult;
		}
		String channel_seller_code = channelOrder.get("channel_seller_code");
		MDataMap returnOrder = DbUp.upTable("oc_return_goods").one("return_code",returnCode);
		if(returnOrder == null || returnOrder.isEmpty()) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("渠道商退货单不存在");
			return mWebResult;
		}
		String returnMoney = returnOrder.get("expected_return_money");
		FuncFlowBussinessChange change = new FuncFlowBussinessChange();
		Integer count = change.rebackBalance(channel_seller_code, new BigDecimal(returnMoney),returnCode);
		if(count <= 0) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("返还预付款失败");
			return mWebResult;
		}
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990023");
	}

	@Override
	public ConfigJobExec getConfig() {

		return config;
	}

	
	public static void main(String[] args) {
		JobForReturnChannelBalance homehas = new JobForReturnChannelBalance();
		// 测试上述代码
		homehas.execByInfo("DD130311104");
	}
}
