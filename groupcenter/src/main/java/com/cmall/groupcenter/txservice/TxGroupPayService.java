package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.cmall.dborm.txmapper.groupcenter.GcVpayOrderMapper;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcTraderAccountChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcVpayOrder;
import com.cmall.dborm.txmodel.groupcenter.GcVpayOrderExample;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.third.model.GroupPayInput;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class TxGroupPayService {

	/**
	 * 微公社支付退款
	 * 
	 * @param gcVpayOrder
	 * @param groupRefundInput
	 * @param mapTrans
	 * @return
	 */
	public void doGroupRefundSome(GcVpayOrder gcVpayOrder,GroupRefundInput groupRefundInput,MDataMap mapTrans) {
		//支付退款记录		
		GcVpayOrderMapper gcVpayOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcVpayOrderMapper");
		
		gcVpayOrderMapper.insertSelective(gcVpayOrder);
		//退款，账户增加退款

		TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
        List<GcWithdrawLog> listWithdrawLogs=new ArrayList<GcWithdrawLog>();
		GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
        gcWithdrawLog.setAccountCode(gcVpayOrder.getAccountCode());
        gcWithdrawLog.setMemberCode(gcVpayOrder.getMemberCode());
        gcWithdrawLog.setWithdrawMoney(gcVpayOrder.getTradeMoney());
        gcWithdrawLog.setWithdrawChangeType("4497465200040009");//支付退款类型
        gcWithdrawLog.setChangeCodes(gcVpayOrder.getTradeCode());
        listWithdrawLogs.add(gcWithdrawLog);
		txGroupAccountService.updateAccount(null, listWithdrawLogs);
	
		
		//更新对应支付单相应信息
		String newRefundCode="";
		if(StringUtil.isBlank(mapTrans.get("refundCode"))){
			newRefundCode=gcVpayOrder.getTradeCode();
		}else{
			newRefundCode=mapTrans.get("refundCode")+","+gcVpayOrder.getTradeCode();
		}
		
		
		GcVpayOrderExample gcVpayOrderExample=new GcVpayOrderExample();
		gcVpayOrderExample.createCriteria().andTradeCodeEqualTo(groupRefundInput.getTradeCode());
		List<GcVpayOrder> gcVpayOrderList=gcVpayOrderMapper.selectByExample(gcVpayOrderExample);
		if(gcVpayOrderList != null && gcVpayOrderList.size() > 0){
			GcVpayOrder gcVpayOrderUpdate = gcVpayOrderList.get(0);
			gcVpayOrderUpdate.setRefundMoney(new BigDecimal(mapTrans.get("newtradeMoney")));
			gcVpayOrderUpdate.setRefundCode(newRefundCode);
			gcVpayOrderUpdate.setUpdateTime(FormatHelper.upDateTime());
			gcVpayOrderUpdate.setRefundTime(groupRefundInput.getRefundTime());
			gcVpayOrderMapper.updateByExampleSelective(gcVpayOrderUpdate, gcVpayOrderExample);
		}

	//都进行成功，返回退款成功,在商户账户明细表中插入数据   fengl 2016-1-19		
		String changeType="4497465200270002";

		MDataMap  mDataMap =DbUp.upTable("gc_wopen_appmanage").one("app_code",mapTrans.get("manageCode"));
		if(mDataMap!=null&&StringUtils.isNotBlank(mDataMap.get("trade_code"))){
		  	String traderCode=mDataMap.get("trade_code");
		  	BigDecimal aTradeMoney=gcVpayOrder.getTradeMoney();
		  	boolean Istrue=true;
		  	if(changeType.equals("4497465200270002")){
		  		aTradeMoney=aTradeMoney.negate();
		  		if(DbUp.upTable("gc_trader_account_change_log").count("change_type","4497465200270001","change_codes",groupRefundInput.getTradeCode())<1){
		  			Istrue=false;
		  		}
		  		
		  	}
		  	if(Istrue){

		        GcTraderAccountChangeLog gcTraderAccountChangeLog = new GcTraderAccountChangeLog();
		        gcTraderAccountChangeLog.setTraderCode(traderCode);
		        gcTraderAccountChangeLog.setChangeMoney(aTradeMoney);//支付单 应该为正值，退款单应该为负值
		        gcTraderAccountChangeLog.setChangeType(changeType);//4497465200270001--支付单 4497465200270002--退款单  
		        gcTraderAccountChangeLog.setChangeCodes(gcVpayOrder.getTradeCode()); 
		        txGroupAccountService.updateTraderBalanceAndAddLog(gcTraderAccountChangeLog,null);				        
				            
		  	}
	   }
	}


	/**
	 * 微公社支付
	 * @param gcVpayOrder
	 * @param groupPayInput
	 * @param tradeCode 付款单流水号
	 * @param accountCode
	 * @param tradeMoney
	 * @param manageCode 应用编号
	 */
	public void doGroupPay(GcVpayOrder gcVpayOrder,
			GroupPayInput groupPayInput,String tradeCode, String accountCode,
			BigDecimal tradeMoney,String manageCode) {
		//支付，账户扣减交易金额
		TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
        List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
		GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
        gcWithdrawLog.setAccountCode(accountCode);
        gcWithdrawLog.setMemberCode(groupPayInput.getMemberCode());
        gcWithdrawLog.setWithdrawMoney(tradeMoney.negate());
        gcWithdrawLog.setWithdrawChangeType("4497465200040008");//支付类型
        gcWithdrawLog.setChangeCodes(tradeCode);
        withdrawList.add(gcWithdrawLog);
		txGroupAccountService.updateAccount(null, withdrawList);
		
		//支付记录
		GcVpayOrderMapper gcVpayOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcVpayOrderMapper");
		
		gcVpayOrderMapper.insertSelective(gcVpayOrder);
		
		//都进行成功，支付成功，在商户账户明细表中插入支付数据 
		String changeType="4497465200270001";
		MDataMap  mDataMap =DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
		if(mDataMap!=null&&StringUtils.isNotBlank(mDataMap.get("trade_code"))){
		  	String traderCode=mDataMap.get("trade_code");
		  	BigDecimal aTradeMoney=gcVpayOrder.getTradeMoney();
	        GcTraderAccountChangeLog gcTraderAccountChangeLog = new GcTraderAccountChangeLog();
	        gcTraderAccountChangeLog.setTraderCode(traderCode);
	        gcTraderAccountChangeLog.setChangeMoney(aTradeMoney);//支付单 应该为正值，退款单应该为负值
	        gcTraderAccountChangeLog.setChangeType(changeType);//4497465200270001--支付单 4497465200270002--退款单  
	        gcTraderAccountChangeLog.setChangeCodes(tradeCode); 
	        txGroupAccountService.updateTraderBalanceAndAddLog(gcTraderAccountChangeLog,null);				        
		}
			
	}

}
