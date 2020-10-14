package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.homehas.RsyncAlipayMoveInformation;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

public class JobSendPayInfoToHomehas extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String orderCode) {
		
		MWebResult mWebResult = new MWebResult();
		
		if(StringUtils.startsWith(orderCode, "DD")){
			mWebResult.inErrorMessage(918507002);
			return mWebResult;
		}
		

		boolean bFlagSuccess = false;

		//定义子订是否出现错误  以解决大单号下有一个成功的就返回成功的问题   每失败一次加一  表示有失败
		int iSubError=0;
		
		RsyncAlipayMoveInformation rsyncAlipayMoveInformation = new RsyncAlipayMoveInformation();
		
		int i=0;
		
		
		String[] flagValues = new String[]{"#0","#1","#2","#3","#4","#5","#6","#7","#8","#9","#a","#b"
				,"#c","#d","#e","#f","#g","#h","#i","#j","#k","#l","#m","#n","#o","#p","#q","#r","#s","#t"
				,"#u","#v","#w","#x","#y","#z"};
		
		MDataMap mDataMap = new MDataMap();
		mDataMap = DbUp.upTable("oc_payment").one("out_trade_no",orderCode,"flag_success","1");   //支付宝支付
		
		MDataMap mDataMapWechat = new MDataMap();
		mDataMapWechat = DbUp.upTable("oc_payment_wechatNew").one("out_trade_no",orderCode,"flag_success","1");  //微信支付
		
		//"PP"为支付单号
		if("PP".equals(orderCode.substring(0, 2))){
			Map<String,Object> payInfoMap = new HashMap<String,Object>();
			List<Map<String, Object>> payDetailList =  new ArrayList<Map<String, Object>>();
			
			payInfoMap = DbUp.upTable("oc_pay_info").dataSqlOne("select * from oc_pay_info where pay_code=:pay_code and state = '1' ",
					new MDataMap("pay_code",orderCode));
			
			if(payInfoMap!=null && !"".equals(payInfoMap) && payInfoMap.size()>0){
				payDetailList = DbUp.upTable("oc_paydetail").dataSqlList(
						"select * from oc_paydetail where pay_code = '"+payInfoMap.get("pay_code")+"' order by create_time desc", new MDataMap());
				
				for(Map<String, Object> map : payDetailList){
					
					rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(String.valueOf(payInfoMap.get("seller_email")));
					rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
					rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("order_code").toString());
					rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(String.valueOf(map.get("due_money")));
					//目前惠家有 和 家有惠    流水号需要加#0     这个待定   因为现在所有的支付  都是走支付单号的   
					if(MemberConst.MANAGE_CODE_HOMEHAS.equals(payInfoMap.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(payInfoMap.get("seller_code"))){
						rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(String.valueOf(payInfoMap.get("trade_no"))+flagValues[i]);
						i++;
					}else{
						rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(String.valueOf(payInfoMap.get("trade_no")));
					}
					
					rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(FormatHelper.upDateTime()); // gmt_payment
					if (rsyncAlipayMoveInformation.doRsync()) {
						bFlagSuccess = rsyncAlipayMoveInformation.upProcessResult().isSend_result();
					}
				}
			}
			
			
		}else if(mDataMap!=null && !"".equals(mDataMap) && mDataMap.size()>0){    //支付宝支付同步
				//小订单
				if("DD".equals(orderCode.substring(0, 2))){
					Map<String, Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne(
							"select * from oc_orderinfo where order_code =:order_code",
							new MDataMap("order_code", mDataMap.get("out_trade_no")));
					// if (bConfig("familyhas.app_code").equals(map.get("seller_code")))
					// {
					//由于外部订单号不能为空，  因为创建订单时把生成外部订单号改成了异步，所以需要此判断
					if(!"".equals(map.get("out_order_code")) && map.get("out_order_code")!=null){
						// 如果是惠家友    或者刘嘉玲     或者    家有惠的订单 开始调用 
						if (MemberConst.MANAGE_CODE_APP.equals(map.get("seller_code"))
								|| MemberConst.MANAGE_CODE_HOMEHAS.equals(map
										.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))|| MemberConst.MANAGE_CODE_SPDOG.equals(map.get("seller_code"))) {
							
							
							rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(
									mDataMap.get("seller_email"));
							rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
							rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(
									map.get("out_order_code").toString());
							rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(
									mDataMap.get("total_fee"));
							//目前支持惠家有  和  家有惠    流水号需要加#0
							if(MemberConst.MANAGE_CODE_HOMEHAS.equals(map.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))){
								rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no")+"#0");
							}else{
								rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no"));
							}
							
							rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(
									FormatHelper.upDateTime()); // gmt_payment
							if (rsyncAlipayMoveInformation.doRsync()) {

								bFlagSuccess = rsyncAlipayMoveInformation.upProcessResult()
										.isSend_result();;

								
								
								
							}
						}
					}
					
					// }
				}else if("OS".equals(orderCode.substring(0, 2))){
					//查询大订单下包含多少小订单
					List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code", mDataMap.get("out_trade_no")));
					for(MDataMap map : list){
						//由于外部订单号不能为空，  因为创建订单时把生成外部订单号改成了异步，所以需要此判断
						if(!"".equals(map.get("out_order_code")) && map.get("out_order_code")!=null){
							// 如果是惠家友    或者刘嘉玲     或者    家有惠的订单 开始调用 
							if (MemberConst.MANAGE_CODE_APP.equals(map.get("seller_code"))
									|| MemberConst.MANAGE_CODE_HOMEHAS.equals(map
											.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))|| MemberConst.MANAGE_CODE_SPDOG.equals(map.get("seller_code"))) {
								
								rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(mDataMap.get("seller_email"));
								rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("54");
								rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("out_order_code").toString());
								rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(map.get("due_money"));
								//目前惠家有 和 家有惠    流水号需要加#0
								if(MemberConst.MANAGE_CODE_HOMEHAS.equals(map.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))){
									rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no")+flagValues[i]);
									i++;
								}else{
									rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMap.get("trade_no"));
								}
								rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(FormatHelper.upDateTime()); // gmt_payment
								if (rsyncAlipayMoveInformation.doRsync()) {
									bFlagSuccess = rsyncAlipayMoveInformation.upProcessResult().isSend_result();
									
									
									if(!bFlagSuccess)
									{
										iSubError++;
									}
								}
							}
						}
					}
					
//					Map<String, Object> map = DbUp.upTable("oc_orderinfo_upper").dataSqlOne(
//							"select * from oc_orderinfo_upper where big_order_code =:big_order_code",
//							new MDataMap("big_order_code", mDataMap.get("out_trade_no")));
					
					
				}
				
		}else if(mDataMapWechat!=null && !"".equals(mDataMapWechat) && mDataMapWechat.size()>0){    //微信支付同步 
			
			//小订单
			if("DD".equals(orderCode.substring(0, 2))){
				Map<String, Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne(
						"select * from oc_orderinfo where order_code =:order_code",
						new MDataMap("order_code", mDataMapWechat.get("out_trade_no")));
				// if (bConfig("familyhas.app_code").equals(map.get("seller_code")))
				// {
				//由于外部订单号不能为空，  因为创建订单时把生成外部订单号改成了异步，所以需要此判断
				if(!"".equals(map.get("out_order_code")) && map.get("out_order_code")!=null){
					// 如果是惠家友    或者刘嘉玲     或者    家有惠的订单 开始调用 
					if (MemberConst.MANAGE_CODE_APP.equals(map.get("seller_code"))
							|| MemberConst.MANAGE_CODE_HOMEHAS.equals(map
									.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))|| MemberConst.MANAGE_CODE_SPDOG.equals(map.get("seller_code"))) {
						
						
						rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(
								mDataMapWechat.get("mch_id"));
						rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("WEC");
						rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(
								map.get("out_order_code").toString());
						rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(
								mDataMapWechat.get("total_fee"));
						//目前支持惠家有  和  家有惠    流水号需要加#0
						if(MemberConst.MANAGE_CODE_HOMEHAS.equals(map.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))){
							rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMapWechat.get("transaction_id")+"#0");
						}else{
							rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMapWechat.get("transaction_id"));
						}
						
						rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(
								FormatHelper.upDateTime()); // gmt_payment
						if (rsyncAlipayMoveInformation.doRsync()) {

							bFlagSuccess = rsyncAlipayMoveInformation.upProcessResult().isSend_result();

						}
					}
				}
				
				// }
			}else if("OS".equals(orderCode.substring(0, 2))){
				//查询大订单下包含多少小订单
				List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code", mDataMapWechat.get("out_trade_no")));
				for(MDataMap map : list){
					//由于外部订单号不能为空，  因为创建订单时把生成外部订单号改成了异步，所以需要此判断
					if(!"".equals(map.get("out_order_code")) && map.get("out_order_code")!=null){
						// 如果是惠家友    或者刘嘉玲     或者    家有惠的订单 开始调用 
						if (MemberConst.MANAGE_CODE_APP.equals(map.get("seller_code"))
								|| MemberConst.MANAGE_CODE_HOMEHAS.equals(map
										.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))|| MemberConst.MANAGE_CODE_SPDOG.equals(map.get("seller_code"))) {
							
							rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(mDataMapWechat.get("mch_id"));
							rsyncAlipayMoveInformation.upRsyncRequest().setBankCd("WEC");
							rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(map.get("out_order_code").toString());
							rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(map.get("due_money"));
							//目前惠家有 和 家有惠    流水号需要加#0
							if(MemberConst.MANAGE_CODE_HOMEHAS.equals(map.get("seller_code")) || MemberConst.MANAGE_CODE_HPOOL.equals(map.get("seller_code"))){
								rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMapWechat.get("transaction_id")+flagValues[i]);
								i++;
							}else{
								rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(mDataMapWechat.get("transaction_id"));
							}
							rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(FormatHelper.upDateTime()); // gmt_payment
							if (rsyncAlipayMoveInformation.doRsync()) {
								bFlagSuccess = rsyncAlipayMoveInformation.upProcessResult().isSend_result();
								
								
								if(!bFlagSuccess)
								{
									iSubError++;
								}
							}
						}
					}
				}
				
//				Map<String, Object> map = DbUp.upTable("oc_orderinfo_upper").dataSqlOne(
//						"select * from oc_orderinfo_upper where big_order_code =:big_order_code",
//						new MDataMap("big_order_code", mDataMap.get("out_trade_no")));
				
				
			}
			
		}


		
		
		
		if(iSubError>0)
		{
			bFlagSuccess=false;
		}

		if (!bFlagSuccess) {

			mWebResult.inErrorMessage(918507002);

		}

		return (IBaseResult) mWebResult;

	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990001");
	}

	@Override
	public ConfigJobExec getConfig() {

		return config;
	}
	
	
	
	
	public static void main(String[] args) {
		JobSendPayInfoToHomehas homehas = new JobSendPayInfoToHomehas();
		//测试上述代码
		homehas.execByInfo("OS150728100343");
	}
}
