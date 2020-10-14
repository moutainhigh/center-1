package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncAlipayMoveInformation;
import com.cmall.groupcenter.homehas.RsyncGetStock;
import com.cmall.groupcenter.service.AlipayMoveInformationService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

public class AlipayMoveInformation extends RootJob{

	public void doExecute(JobExecutionContext context) {
		
		MDataMap queryMap = new MDataMap();
		queryMap.put("mark", "001");
		
		RsyncAlipayMoveInformation rsyncAlipayMoveInformation=new RsyncAlipayMoveInformation();
		
		List<MDataMap> paymentList = DbUp.upTable("oc_payment").queryAll("",
				"", "", queryMap); // 查询此订单是否调用过支付宝接口
		
		if(paymentList !=null && !"".equals(paymentList)){
			for(MDataMap mDataMap : paymentList){
				
				Map<String,Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code =:order_code", new MDataMap("order_code",mDataMap.get("order_code")));
				if(bConfig("familyhas.app_code").equals(map.get("seller_code"))){
					rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(mDataMap.get("seller_email"));
					rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
					rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("out_order_code").toString());
					rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(mDataMap.get("total_fee"));
					rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no"));
					rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(mDataMap.get("gmt_payment"));   //gmt_payment
					rsyncAlipayMoveInformation.doRsync();
					
				}
				
			}
			
		}
		
	}
	
}
