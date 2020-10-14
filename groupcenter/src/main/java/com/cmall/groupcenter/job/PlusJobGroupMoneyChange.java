package com.cmall.groupcenter.job;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.third.model.GroupPayInput;
import com.cmall.groupcenter.third.model.GroupPayResult;
import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.modelproduct.PlusModelGroupMoneyChange;
import com.srnpr.xmassystem.top.PlusConfigScheduler;
import com.srnpr.xmassystem.top.PlusTopScheduler;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.GsonHelper;
import com.srnpr.zapcom.basehelper.LogHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webface.IKvSchedulerConfig;

/**
 * 定时将缓存中的微公社金额变更调用微公社支付
 * @author xiegj
 *
 */
public class PlusJobGroupMoneyChange extends PlusTopScheduler {

	public IBaseResult execByInfo(String sInfo) {

		PlusModelGroupMoneyChange plusChange = new GsonHelper().fromJson(sInfo,
				new PlusModelGroupMoneyChange());

		RootResultWeb result = new RootResultWeb();

		if (plusChange != null) {

			GroupPayInput gi = new GroupPayInput();
			gi.setMemberCode(plusChange.getMemberCode());
			gi.setOrderCode(plusChange.getChangeOrderCode());
			gi.setOrderCreateTime(plusChange.getCreateTime());
			gi.setTradeMoney(plusChange.getChangeMoney().toString());
			GroupPayResult gr = new GroupPayService().GroupPay(gi, plusChange.getManageCode());
			if(gr.upFlagTrue()){
				MDataMap dataMap = new MDataMap();
				dataMap.put("order_code", plusChange.getChangeOrderCode());
				dataMap.put("pay_type", "449746280009");
				dataMap.put("pay_sequenceid", gr.getTradeCode());
				DbUp.upTable("oc_order_pay").dataUpdate(dataMap, "pay_sequenceid", "order_code,pay_type");
			}
			LogHelper.addLog("groupMoney_change", plusChange);
		}
		return result;
	}

	private final static PlusConfigScheduler plusConfigScheduler = new PlusConfigScheduler(
			EPlusScheduler.GroupMoneyChangeLog);

	public IKvSchedulerConfig getConfig() {

		return plusConfigScheduler;
	}

}
