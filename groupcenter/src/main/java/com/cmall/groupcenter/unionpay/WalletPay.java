package com.cmall.groupcenter.unionpay;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.paymoney.util.Base64Util;
import com.cmall.groupcenter.paymoney.util.HiiposmUtil;
import com.cmall.groupcenter.txservice.TxTraderWalletService;
import com.cmall.groupcenter.wallet.model.TraderWalletMoneyOptionModel;
import com.cmall.systemcenter.ali.sign.MD5;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 微公社商户给用户提现
 * @author GaoYang
 *
 */
public class WalletPay extends BaseClass{

	public void PayMoney(MDataMap mDataMap) {
		
		MDataMap map=new MDataMap();
		String orderNum=WebHelper.upCode("9999");
		String withdrawCode=mDataMap.get("withdraw_code");
		String traderCode = mDataMap.get("trader_code");
		String accountCode = mDataMap.get("account_code");
		String withdrawMoney = mDataMap.get("withdraw_money");
		String cardCode = mDataMap.get("card_code");
		String memberName = mDataMap.get("member_name");
		String memberCode=mDataMap.get("member_code");
		map.put("requestId",orderNum);
		
		try{
			String remark="walletWithdraw";
			String characterSet="UTF-8";
			JSONObject requestJsonObject = new JSONObject();
			requestJsonObject.put("command", "01003");
			requestJsonObject.put("logisticsid", bConfig("groupcenter.unionpay_logisticsid"));
			requestJsonObject.put("companyid", bConfig("groupcenter.unionpay_companyid"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			requestJsonObject.put("txntime",sdf.format(new Date()));
			requestJsonObject.put("bankaccount",cardCode);
			requestJsonObject.put("accountname",memberName);
			requestJsonObject.put("ordernum", orderNum);
			requestJsonObject.put("bankcode", "");
			requestJsonObject.put("bankname", "");
			requestJsonObject.put("certificatetype","01");
			requestJsonObject.put("certificateno","");
			requestJsonObject.put("phone", "");
			String amount=mDataMap.get("pay_money");//实际付款金额
			requestJsonObject.put("txntaccount", amount);
			requestJsonObject.put("reserved", "");
			String data  = Base64Util.encode(requestJsonObject.toString().getBytes());
			MD5 md5=new MD5();
			data = data.replace('_','/').replace('+', '-');
			//System.out.println(data);
			String sign =md5.sign(data, "C6FB78ADC5C58A72B1CCBC953F3", characterSet);
			String url="http://www.masget.cn:8098/api.aspx";
//			String url=bConfig("groupcenter.rbPayRequestUrl");

			String reqeustJson = "command=01003&data="+data+"&sign="+sign;
			HiiposmUtil hiiposmUtil=new HiiposmUtil();
			
			//增加支付接口日志
			map.inAllValues("withdraw_code",withdrawCode,"amount",amount,"receiveCardNo",mDataMap.get("card_code"),
					"receiveCardName",mDataMap.get("member_name"),"lbnkName",mDataMap.get("bank_name"),"capCorg",mDataMap.get("bank_code"),
					"remark",remark,"request_data",url+reqeustJson,"request_time",FormatHelper.upDateTime(),"pay_type","4497465200100001");
			DbUp.upTable("gc_wallet_pay_log").dataInsert(map);
			
			try{
			    //提交支付订单
				String wResult= hiiposmUtil.sendAndRecv(url, reqeustJson,characterSet);
				
				//更新返回信息
				map.put("response_time", FormatHelper.upDateTime());
				map.put("response_data", wResult);
				DbUp.upTable("gc_wallet_pay_log").dataUpdate(map, "response_time,response_data", "requestId");
				
				//更新提交状态
				JSONObject jsonObject=new JSONObject();
				jsonObject=jsonObject.parseObject(wResult);
				String code=jsonObject.getString("response");
				String message=jsonObject.getString("message");
				map.put("order_code", code);
			    map.put("order_code_message", message);
			    
			    if(code.equals("00")){//成功时默认为空，赋给提交成功
			    	map.put("order_code_message", bConfig("groupcenter.pay_order_success"));
			    }
			    DbUp.upTable("gc_wallet_pay_log").dataUpdate(map, "order_code,order_code_message", "requestId");
				
			    //提交失败
			    if(!code.equals("00")){
			    	//提交失败，提现金额返回用户账户
			    	TxTraderWalletService txTraderWalletService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxTraderWalletService");
	                TraderWalletMoneyOptionModel traderWalletMoneyOptionModel = new TraderWalletMoneyOptionModel();
	                traderWalletMoneyOptionModel.setTraderCode(traderCode);
	                traderWalletMoneyOptionModel.setAccountCode(accountCode);
	                traderWalletMoneyOptionModel.setMoneyOccured(withdrawMoney);
	                traderWalletMoneyOptionModel.setChangeLogType("4497476000020004");//提现失败
	                traderWalletMoneyOptionModel.setRelationCode(withdrawCode);
	                traderWalletMoneyOptionModel.setMemberCode(memberCode);
			    	txTraderWalletService.doDeposit(traderWalletMoneyOptionModel);
			    	
					//更新支付状态为提现失败
				    MDataMap payMap2=new MDataMap();
				    payMap2.inAllValues("withdraw_code",withdrawCode,"withdraw_status","4497476000010006","update_time",FormatHelper.upDateTime());
				    DbUp.upTable("gc_wallet_withdraw_info").dataUpdate(payMap2, "withdraw_status,update_time", "withdraw_code");
				    
				    //增加支付失败日志
				    MDataMap payLog2=new MDataMap();
				    payLog2.inAllValues("withdraw_code",withdrawCode,"withdraw_status","4497476000010006","update_time",FormatHelper.upDateTime());
				    DbUp.upTable("gc_wallet_withdraw_log").dataInsert(payLog2);
			    }else{
					//更新付款单支付状态为打款中
				    MDataMap payMap=new MDataMap();
				    payMap.inAllValues("withdraw_code",withdrawCode,"withdraw_status","4497476000010004","update_time",FormatHelper.upDateTime());
				    DbUp.upTable("gc_wallet_withdraw_info").dataUpdate(payMap, "withdraw_status,update_time", "withdraw_code");
				    
				    //增加付款单日志表
				    MDataMap wLog=new MDataMap();
				    wLog.inAllValues("withdraw_code",withdrawCode,"withdraw_status","4497476000010004","update_time",FormatHelper.upDateTime(),"remark",bConfig("groupcenter.pay_order"));
				    DbUp.upTable("gc_wallet_withdraw_log").dataInsert(wLog);
			    }
			}catch(Exception e){
				map.put("error_exception", "order:"+e.getMessage());
				DbUp.upTable("gc_wallet_pay_log").dataUpdate(map, "error_exception", "requestId");
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
