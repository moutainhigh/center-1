package com.cmall.groupcenter.job.hjycoin;

import java.math.BigDecimal;
import java.util.UUID;

import com.cmall.ordercenter.service.goods.ReturnGoodsService;
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
* @author Angel Joy
* @Time 2020-7-13 10:32:51 
* @Version 1.0
* <p>Description:</p>
*/
public class JobSubtractCoinsForReturnGoods extends RootJobForExec{
	
	ReturnGoodsService goodsService = new ReturnGoodsService();
	PlusServiceAccm plusServiceAccm = new PlusServiceAccm();

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();

		MDataMap returnGoods = DbUp.upTable("oc_return_goods").one("return_code", sInfo);
		if (returnGoods == null) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("退货单不存在");
			return mWebResult;
		}
		
//		if ("4497153900050006".equals(returnGoods.get("status"))
//				|| "4497153900050007".equals(returnGoods.get("status"))) {
//			mWebResult.setResultCode(0);
//			mWebResult.setResultMessage("退货单已经被客服否决或者已经取消");
//			return mWebResult;
//		}
		
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code", returnGoods.get("order_code"));

		// LD订单不通过惠家有系统扣除积分
		if ("SI2003".equals(returnGoods.get("small_seller_code"))) {
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("LD订单不通过惠家有系统扣除积分");
			return mWebResult;
		}
		
		MDataMap changeMap = DbUp.upTable("fh_hjycoin_oper_details").one("code",orderInfo.get("order_code"),"oper_type","449748650001");
		/**
		 * ddflag = false 订单惠币未转正，发生退货时，需要扣除预估惠币
		 * ddflag = true 订单惠币已未转正，发生退货时，需要扣除正式惠币
		 */
		Boolean ddflag = false;
		if(changeMap != null) {//该订单惠币已经转正，需要扣除转正惠币
			ddflag = true;
		}
		
		// 退货商品部分赋予的惠币
		BigDecimal returnHjycoinMoney = new BigDecimal(returnGoods.get("expected_return_give_hjycoin_money"));
		if(returnHjycoinMoney.compareTo(BigDecimal.ZERO) <= 0){
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("退货商品赋予惠币为0");
			return mWebResult;
		}
		String memberCode = returnGoods.get("buyer_code");
		String cust_id = new HjycoinService().getCustId(memberCode);
		RootResult rootResult = new RootResult();
		String typeString = "449748650002";
		if(ddflag) {
			typeString = "449748650003";
			rootResult = new HjycoinService().changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZFHB, returnHjycoinMoney, cust_id, orderInfo.get("big_order_code"), returnGoods.get("order_code"),"10");
		}else {
			typeString = "449748650002";
			rootResult = new HjycoinService().changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZFHB, returnHjycoinMoney, cust_id, orderInfo.get("big_order_code"), returnGoods.get("order_code"),"20");
		}
		//添加到记录表
		if(rootResult.getResultCode() == 1) {
			MDataMap fh_hjycoin_oper_details = new MDataMap();
			fh_hjycoin_oper_details.put("uid",UUID.randomUUID().toString().replace("-", "").trim());
			fh_hjycoin_oper_details.put("code", sInfo);
			fh_hjycoin_oper_details.put("hjycoin", returnHjycoinMoney+"");
			fh_hjycoin_oper_details.put("oper_type", typeString);
			DbUp.upTable("fh_hjycoin_oper_details").dataInsert(fh_hjycoin_oper_details);
		}
		mWebResult.inOtherResult(rootResult);
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990037");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
