package com.cmall.groupcenter.job.hjycoin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.srnpr.xmassystem.invoke.ref.model.UpdateCustAmtInput;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
 * 取消订单扣除预估收益定时
* @author Angel Joy
* @Time 2020-7-10 16:56:11 
* @Version 1.0
* <p>Description:</p>
*/
public class JobSubtractPredictCoinsForCancelOrderToTgz extends RootJobForExec {

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
			mWebResult.setResultMessage("LD订单不通过惠家有系统操作惠币");
			return mWebResult;
		} 
		
		// 家有客代号
		RootResult rootResult = new RootResult();
		//下单赋予推广人惠币
		String sqlTgz = "SELECT SUM(tgz_money) money,tgz_member_code FROM familyhas.fh_tgz_order_detail WHERE order_code = :order_code GROUP BY order_code,tgz_member_code";
		List<Map<String,Object>> listDetails = DbUp.upTable("fh_tgz_order_detail").dataSqlList(sqlTgz,new MDataMap("order_code",sInfo));
		for(Map<String,Object> map : listDetails) {
			String memberCode = MapUtils.getString(map, "tgz_member_code","");
			if(StringUtils.isEmpty(memberCode)) {
				continue;
			}
			String custId = new HjycoinService().getCustId(memberCode);
			BigDecimal giveMoney = new BigDecimal(MapUtils.getString(map, "money","0"));
			if(giveMoney.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			rootResult = new HjycoinService().changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZEHB, giveMoney, custId, orderInfo.get("big_order_code"), sInfo,"20");
		}
		mWebResult.inOtherResult(rootResult);
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990042");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
