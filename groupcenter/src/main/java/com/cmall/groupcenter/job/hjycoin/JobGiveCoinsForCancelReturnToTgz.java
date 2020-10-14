package com.cmall.groupcenter.job.hjycoin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

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
* @author Angel Joy
* @Time 2020-7-13 10:44:19 
* @Version 1.0
* <p>Description:</p>
*/
public class JobGiveCoinsForCancelReturnToTgz extends RootJobForExec {
	
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
		String orderCode = returnInfo.get("order_code");
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
		if(orderInfo == null || orderInfo.isEmpty()) {
			mWebResult.setResultCode(1);
			mWebResult.setResultMessage("订单不存在！");
			return mWebResult;  
		}
		int count = DbUp.upTable("fh_hjycoin_oper_details").count("code",orderCode,"oper_type","449748650001");
		if(count > 0) {//已经推送过数据
			ddflag = true;
		}
		/**
		 * reflag == 1 时，申请退货是扣除的是预估，reflag == 2 时 申请退货扣的是正式。
		 */
		Integer  reflag = 1;
		if(DbUp.upTable("fh_hjycoin_oper_details").count("code",sInfo,"oper_type","449748650002")>0) {
			reflag = 1;//申请时扣除的是预估收益
		}
		if(DbUp.upTable("fh_hjycoin_oper_details").count("code",sInfo,"oper_type","449748650003")>0) {
			reflag = 2;//申请时扣除的是转正收益
		}
		String sqlString = "SELECT * FROM ordercenter.oc_return_goods_detail WHERE return_code = :return_code";
		List<Map<String,Object>> returnDetailsList = DbUp.upTable("oc_return_goods_detail").dataSqlList(sqlString, new MDataMap("return_code",sInfo));
		for (Map<String, Object> map : returnDetailsList) {
			String sku_code = MapUtils.getString(map, "sku_code","");
			Integer skuNum = MapUtils.getInteger(map, "count",0);
			//根据订单号，sku单号，查询推广人编号
			MDataMap tgzDetail = DbUp.upTable("fh_tgz_order_detail").one("order_code",orderCode,"sku_code",sku_code);
			if(tgzDetail == null || tgzDetail.isEmpty()) {
				continue;
			}
			String custId = plusServiceAccm.getCustId(tgzDetail.get("tgz_member_code"));
			BigDecimal returnMoney = new BigDecimal(tgzDetail.get("tgz_money")).multiply(new BigDecimal(skuNum)).divide(new BigDecimal(tgzDetail.get("sku_num")));
			if(reflag==1) {//需要返还预估收益
				service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZIHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"20");
				if(ddflag) {//已经推过订单转正的售后单，返还预估之后还需要再掉一次转正
					service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZZHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"20");
				}
			}else if(reflag == 2) {
				service.changeForHjycoin(UpdateCustAmtInput.CurdFlag.ZIHB, returnMoney, custId, orderInfo.get("big_order_code"), returnInfo.get("order_code"),"10");
			}
		}
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990044");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
