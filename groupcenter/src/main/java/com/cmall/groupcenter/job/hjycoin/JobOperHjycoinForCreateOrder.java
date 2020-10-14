package com.cmall.groupcenter.job.hjycoin;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.xmassystem.invoke.ref.model.UpdateCustAmtInput;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
 * 下单赋予预估收益定时
* @author Angel Joy
* @Time 2020-7-10 16:26:39 
* @Version 1.0
* <p>Description:</p>
*/
public class JobOperHjycoinForCreateOrder extends RootJobForExec {
	
	PlusServiceAccm plusServiceAccm = new PlusServiceAccm();

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();

		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code", sInfo);

		if (orderInfo == null) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("未查询到订单信息");
			return mWebResult;
		}
		
		// LD订单不通过惠家有系统赋予积分
		if ("SI2003".equals(orderInfo.get("small_seller_code"))) {
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("LD订单不通过惠家有系统赋予惠币");
			return mWebResult;
		} 
		
		// 家有客代号
		String custId = plusServiceAccm.getCustId(orderInfo.get("buyer_code"));
		if(StringUtils.isBlank(custId)){
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("未查询到家有客代号，无法赋予惠币");
			return mWebResult;
		}
		
		// 需要赋予的惠币金额
		BigDecimal giveMoney = (BigDecimal)DbUp.upTable("oc_orderdetail").dataGet("sum(give_hjycoin)", "order_code = :order_code and give_hjycoin > 0", new MDataMap("order_code", sInfo));
		if(giveMoney == null || giveMoney.compareTo(BigDecimal.ZERO) <= 0){
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("没有惠币需要赋予");
			return mWebResult;
		}
		
		
		// 赋予惠币
		RootResult rootResult = new RootResult();
		rootResult = new HjycoinService().changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZAHB, giveMoney, custId, orderInfo.get("big_order_code"), sInfo,"20");
		
		mWebResult.inOtherResult(rootResult);
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990035");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}


}
