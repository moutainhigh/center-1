package com.cmall.groupcenter.job.hjycoin;

import java.math.BigDecimal;

import com.srnpr.xmassystem.invoke.ref.model.UpdateCustAmtInput;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
 * 取消申请退货，需要返还扣除的下单赋予惠币。
* @author Angel Joy
* @Time 2020-7-13 10:44:19 
* @Version 1.0
* <p>Description:</p>
*/
public class JobGiveCoinsForCancelReturn extends RootJobForExec {
	
	HjycoinService service = new HjycoinService();
	PlusServiceAccm plusServiceAccm = new PlusServiceAccm();

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();
		/**
		 * ddflag == true 时，是返回的正式收益，证明已经推送过预估转正了，隐含意思是，原订单签收超过15天。默认情况下，按照预估收益处理
		 */
		boolean ddflag = false;
		MDataMap returnInfo = DbUp.upTable("oc_return_goods").one("return_code",sInfo);
		if(returnInfo == null || returnInfo.isEmpty()) {
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("售后单不存在！");
			return mWebResult;  
		}
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",returnInfo.get("order_code"));
		if(orderInfo == null || orderInfo.isEmpty()) {
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("订单不存在！");
			return mWebResult;  
		}
		int count = DbUp.upTable("fh_hjycoin_oper_details").count("code",returnInfo.get("order_code"),"oper_type","449748650001");
		if(count > 0) {//已经推送过数据
			ddflag = true;
		}
		/**
		 * reflag == 1 时，申请退货是扣除的是预估，reflag == 2 时 申请退货扣的是正式。
		 */
		Integer  reflag = 1;
		if(DbUp.upTable("fh_hjycoin_oper_details").count("code",sInfo,"oper_type","449748650002")>0) {
			reflag = 1;
			//需要返还预估惠币。//需要判断是否推过转正
		}
		if(DbUp.upTable("fh_hjycoin_oper_details").count("code",sInfo,"oper_type","449748650003")>0) {
			reflag = 2;
			//需要返还正式惠币，//肯定是已经推过转正了。
		}
		BigDecimal returnMoney = new BigDecimal(returnInfo.get("expected_return_give_hjycoin_money"));
		String custId = plusServiceAccm.getCustId(returnInfo.get("buyer_code"));
		if(reflag==1) {//需要返还预估收益
			service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZHHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"20");
			if(ddflag) {//已经推过订单转正的售后单，返还预估之后还需要再掉一次转正
				service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZZHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"20");
			}
		}else if(reflag == 2) {
			service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZHHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"10");
		}
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990038");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
