package com.cmall.groupcenter.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.RsyncAlipayMoveInformation;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
/**
 *同步支付宝成功支付数据
 * @author wz
 *
 */
public class AlipayMoveInformationService extends BaseClass{
	
	public void synchronizationAlipayMove(String orderCode){
		
//		JobExecHelper.createExecInfo("449746990001", orderCode, "");
		
		//update by jlin 2015-11-25 14:50:03
		//定时表中支付信息换做小订单号
		if(StringUtils.startsWith(orderCode, "OS")){
			for (MDataMap orderInfo : DbUp.upTable("oc_orderinfo").queryAll("order_code", "", "big_order_code=:big_order_code and small_seller_code in (:homehas,:homepool) ", new MDataMap("big_order_code",orderCode,"homehas",MemberConst.MANAGE_CODE_HOMEHAS,"homepool",MemberConst.MANAGE_CODE_HPOOL))) {
				JobExecHelper.createExecInfo("449746990001", orderInfo.get("order_code"), "");
			}
		}else{
			JobExecHelper.createExecInfo("449746990001", orderCode, "");
		}
		
		
		/*
		RsyncAlipayMoveInformation rsyncAlipayMoveInformation=new RsyncAlipayMoveInformation();
		
		MDataMap mDataMap = DbUp.upTable("oc_payment").one("out_trade_no", orderCode,"mark", "001");
		if(!mDataMap.isEmpty()){
				Map<String,Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code =:order_code", new MDataMap("order_code",mDataMap.get("out_trade_no")));
				if(bConfig("familyhas.app_code").equals(map.get("seller_code"))){
					rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(mDataMap.get("seller_email"));
					rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
					rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("out_order_code").toString());
					rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(mDataMap.get("total_fee"));
					rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no"));
					rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(FormatHelper.upDateTime());   //gmt_payment
					rsyncAlipayMoveInformation.doRsync();
				}
		}
		*/
//		MDataMap queryMap = new MDataMap();
//		queryMap.put("mark", "001");
//		queryMap.put("out_trade_no", orderCode);
//		List<MDataMap> paymentList = DbUp.upTable("oc_payment").queryAll("",
//		"", "", queryMap); // 查询此订单是否调用过支付宝接口
//		if(paymentList !=null && !"".equals(paymentList)){
//			for(MDataMap mDataMap : paymentList){
//				
//				Map<String,Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code =:order_code", new MDataMap("order_code",mDataMap.get("out_trade_no")));
//				if(bConfig("familyhas.app_code").equals(map.get("seller_code"))){
//					rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(mDataMap.get("seller_email"));
//					rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
//					rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("out_order_code").toString());
//					rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(mDataMap.get("total_fee"));
//					rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no"));
//					rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(mDataMap.get("gmt_payment"));   //gmt_payment
//					rsyncAlipayMoveInformation.doRsync();
//					
//				}
//				
//			}
//			
//		}
	}

}
