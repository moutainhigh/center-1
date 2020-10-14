package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cmall.groupcenter.util.WgsMailSupport;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社提现信息汇总 每一天执行一次，若两天银行没有回调，就提示信息
 * 
 * @author huangs
 * 
 */
public class JobWithdrawInfoSummarize extends RootJob {
	
	public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd 00:00:00"; // 年/月/日
	public static final SimpleDateFormat sdfDateTime = new SimpleDateFormat(DATE_FORMAT_DATETIME);
	
	@Override
	public void doExecute(JobExecutionContext context) {
		//当天凌晨时间(yyyy-MM-dd 00:00:00)
		 String currentZeroTime=sdfDateTime.format(new Date());
		//昨天凌晨时间(yyyy-MM-dd-1 00:00:00)
		 String yesterdayZeroTime=DateUtil.toString(DateUtil.addDays(new Date(), -1), sdfDateTime);
		//前天凌晨时间(yyyy-MM-dd-2 00:00:00)
		 String beforeYesterdayZeroTime=DateUtil.toString(DateUtil.addDays(new Date(), -2), sdfDateTime);
		//大前天凌晨时间(yyyy-MM-dd-3 00:00:00)
		 String beforebeforeYesterdayZeroTime=DateUtil.toString(DateUtil.addDays(new Date(), -3), sdfDateTime);
		 
		 Map infodata = new LinkedHashMap();
		 MDataMap timeMap = new MDataMap("yesterdayZeroTime", yesterdayZeroTime,"currentZeroTime",currentZeroTime,
				 "beforeYesterdayZeroTime",beforeYesterdayZeroTime,"beforebeforeYesterdayZeroTime",beforebeforeYesterdayZeroTime);
		// 昨天申请提现总数 和昨天申请提现总实际金额
		 String applyWidrowSql="select count(1) as count,ifnull(sum(withdraw_money),0) as money from gc_pay_order_info where create_time between :yesterdayZeroTime and :currentZeroTime "; 
		 Map<String, Object> applyWidrow=DbUp.upTable("gc_pay_order_info").dataSqlOne(applyWidrowSql, timeMap);
		 String applyWidrowCount=applyWidrow.get("count").toString();
		 infodata.put("applyWidrowCount", "申请提现总数为" + applyWidrowCount);
		 String applyWidrowAmount=applyWidrow.get("money").toString();
		 infodata.put("applyWidrowAmount", "申请提现总金额" + applyWidrowAmount);
		
		
		
		// 昨日提现审核通过的总数 和 昨日提现审核通过的总实际付款金额
		 String auditPassSql="select count(1) as count,ifnull(sum(withdraw_money),0) as money from gc_pay_order_info where audit_time between :yesterdayZeroTime and :currentZeroTime  and order_status='4497153900120002' ";
		 Map<String, Object> auditPass=DbUp.upTable("gc_pay_order_info").dataSqlOne(auditPassSql,timeMap);
		 String auditPassCount=auditPass.get("count").toString();
		 infodata.put("auditPassCount", "提现审核通过的总数为" + auditPassCount);
		 String auditPassAmount=auditPass.get("money").toString();
		 infodata.put("auditPassAmount", "提现审核通过的总金额" + auditPassAmount);
		 
		 
		// 昨日提现审核未通过的总数 和 昨日提现审核未通过的总实际付款金额
		 String auditNOPassSql="select count(1) as count,ifnull(sum(withdraw_money),0) as money from gc_pay_order_info where audit_time between :yesterdayZeroTime and :currentZeroTime  and order_status='4497153900120003' ";
		 Map<String, Object> auditNoPass=DbUp.upTable("gc_pay_order_info").dataSqlOne(auditNOPassSql,timeMap);
		 String auditNoPassCount=auditNoPass.get("count").toString();
		 infodata.put("auditNoPassCount", "提现审核未通过的总数为" + auditNoPassCount);
		 String auditNoPassAmount=auditNoPass.get("money").toString();
		 infodata.put("auditNoPassAmount", "提现审核未通过的总金额" + auditNoPassAmount);

		 
		 //昨日向银行提交提现成功的总数和昨日向银行提交提现成功的总金额
		 String summitBankSuccessSql="select count(DISTINCT(pay_order_code)) as count,ifnull(sum(amount),0) as money from gc_pay_money_log where  order_code='00' and request_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> summitBankSuccess=DbUp.upTable("gc_pay_money_log").dataSqlOne(summitBankSuccessSql,timeMap);
		 String summitBankSuccessCount=summitBankSuccess.get("count").toString();
		 infodata.put("summitBankSuccessCount", "向银行提交成功的总数为" + summitBankSuccessCount);
		 String summitBankSuccessAmount=summitBankSuccess.get("money").toString();
		 infodata.put("summitBankSuccessAmount", "向银行提交成功的总金额" + summitBankSuccessAmount);
		 
		 //昨日向银行提交提现申请失败的总数和昨日向银行提交提现申请失败的总金额
		 String summitBankFailedSql="select count(DISTINCT(pay_order_code)) as count,ifnull(sum(amount),0) as money from gc_pay_money_log where  order_code not in('00','') and request_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> summitBankFailed=DbUp.upTable("gc_pay_money_log").dataSqlOne(summitBankFailedSql,timeMap);
		 String summitBankFailedCount=summitBankFailed.get("count").toString();
		 infodata.put("summitBankFailedCount", "向银行提交失败的总数为" + summitBankFailedCount);
		 String summitBankFailedAmount=summitBankFailed.get("money").toString();
		 infodata.put("summitBankFailedAmount", "向银行提交失败的总金额" + summitBankFailedAmount);
		 
		//昨日银行回传转账交易成功的总数和昨日转账交易成功的总金额
		 String withdrawSuccessSql="select count(DISTINCT(pay_order_code)) as count,ifnull(sum(amount),0) as money from gc_pay_money_log where  notify_code='00' and notify_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> withdrawSuccess=DbUp.upTable("gc_pay_money_log").dataSqlOne(withdrawSuccessSql,timeMap);
		 String withdrawSuccessCount=withdrawSuccess.get("count").toString();
		 infodata.put("withdrawSuccessCount", "提现支付成功的总数" + withdrawSuccessCount);
		 String withdrawSuccessAmount=withdrawSuccess.get("money").toString();
		 infodata.put("withdrawSuccessAmount", "提现支付成功的总金额" + withdrawSuccessAmount);
		
		//昨日银行回传打款失败的总数和昨日打款失败的总金额
		 String withdrawFailedSql="select count(DISTINCT(pay_order_code)) as count,ifnull(sum(amount),0) as money from gc_pay_money_log where  notify_code not in('00','') and notify_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> withdrawFailed=DbUp.upTable("gc_pay_money_log").dataSqlOne(withdrawFailedSql,timeMap);
		 String withdrawFailedCount=withdrawFailed.get("count").toString();
		 infodata.put("withdrawFailedCount", "提现支付失败的总数" + withdrawFailedCount);
		 String withdrawFailedAmount=withdrawFailed.get("money").toString();
		 infodata.put("withdrawFailedAmount", "提现支付失败的总金额" + withdrawFailedAmount);
		
		
		
		// 在两天内银行没有回传打款状态的订单
		List<Map<String, Object>> backRecordList = DbUp.upTable("gc_pay_money_log").dataQuery("DISTINCT (pay_order_code) , amount ", "", 
				"order_code='00' and notify_code='' and request_time  between :beforebeforeYesterdayZeroTime and :beforeYesterdayZeroTime", timeMap, 0, 0);
		
		if (backRecordList.size() > 0) {

			infodata.put("backRecordList", "两天银行没有回传提现订单返回状态的订单" + backRecordList);
		}
		else {
			infodata.put("backRecordList", "两天银行没有回传提现订单的返回状态数量为0");
		}
		
		//昨日查询支付的总数和昨日查询支付的总金额
		 String paySql="select count(1) as count,ifnull(sum(trade_money),0) as money from gc_vpay_order where  create_time between :yesterdayZeroTime and :currentZeroTime and trade_type='4497465200200001' ";
		 Map<String, Object> pay=DbUp.upTable("gc_vpay_order").dataSqlOne(paySql,timeMap);
		 String paySqlCount=pay.get("count").toString();
		 infodata.put("paySqlCount", "昨日查询支付成功的总数" + paySqlCount);
		 String paySqlAmount=pay.get("money").toString();
		 infodata.put("paySqlAmount", "昨日查询支付成功的总金额" + paySqlAmount);
		
		 
		//查询退款的总数和查询退款的总金额
		 String refundSql="select count(1) as count,ifnull(sum(trade_money),0) as money from gc_vpay_order where  create_time between :yesterdayZeroTime and :currentZeroTime and trade_type='4497465200200002' ";
		 Map<String, Object> refund=DbUp.upTable("gc_vpay_order").dataSqlOne(refundSql,timeMap);
		 String refundCount=refund.get("count").toString();
		 infodata.put("refundCount", "查询退款成功的总数" + refundCount);
		 String refundAmount=refund.get("money").toString();
		 infodata.put("refundAmount", "查询退款成功的总金额" + refundAmount);
		 
		 //钱包的相关操作
		//查询钱包提现中的总数和查询钱包提现中的总金额
		 String applyWalletwithdrawSql="select count(1) as count,ifnull(sum(withdraw_money),0) as money from gc_wallet_withdraw_info where create_time between :yesterdayZeroTime and :currentZeroTime "; 
		 Map<String, Object> applyWalletWithdraw=DbUp.upTable("gc_wallet_withdraw_info").dataSqlOne(applyWalletwithdrawSql, timeMap);
		 String applyWalletWithdrawCount=applyWalletWithdraw.get("count").toString();
		 infodata.put("applyWalletWithdrawCount", "钱包申请提现总数为" + applyWalletWithdrawCount);
		 String applyWalletWithdrawAmount=applyWalletWithdraw.get("money").toString();
		 infodata.put("applyWalletWithdrawAmount", "钱包申请提现总金额" + applyWalletWithdrawAmount);
		 
		// 昨日钱包提现系统审核失败的总数 和 昨日钱包提现系统审核失败的总实际付款金额
		 String auditWalletNoPassSql="select count(1) as count,ifnull(sum(withdraw_money),0) as money from gc_wallet_withdraw_info where update_time between :yesterdayZeroTime and :currentZeroTime  and withdraw_status='4497476000010003' ";
		 Map<String, Object> auditWalletNoPass=DbUp.upTable("gc_wallet_withdraw_info").dataSqlOne(auditWalletNoPassSql,timeMap);
		 String auditWalletNoPassCount=auditWalletNoPass.get("count").toString();
		 infodata.put("auditWalletNoPassCount", "钱包提现系统审核失败的总数为" + auditWalletNoPassCount);
		 String auditWalletNoPassAmount=auditWalletNoPass.get("money").toString();
		 infodata.put("auditWalletNoPassAmount", "钱包提现系统审核失败的总金额" + auditWalletNoPassAmount);
		 
		 
		//昨日钱包提现向银行提交提现成功的总数和昨日向银行提交提现成功的总金额
		 String walletSummitBankSuccessSql="select count(DISTINCT(withdraw_code)) as count,ifnull(sum(amount),0) as money from gc_wallet_pay_log where  order_code='00' and request_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> walletSummitBankSuccess=DbUp.upTable("gc_wallet_pay_log").dataSqlOne(walletSummitBankSuccessSql,timeMap);
		 String walletSummitBankSuccessCount=walletSummitBankSuccess.get("count").toString();
		 infodata.put("walletSummitBankSuccessCount", "钱包提现向银行提交成功的总数为" + walletSummitBankSuccessCount);
		 String walletSummitBankSuccessAmount=walletSummitBankSuccess.get("money").toString();
		 infodata.put("walletSummitBankSuccessAmount", "钱包提现向银行提交成功的总金额" + walletSummitBankSuccessAmount);
		 
		//昨日钱包提现向银行提交提现失败的总数和昨日向银行提交提现成功的总金额
		 String walletSummitBankFailedSql="select count(DISTINCT(withdraw_code)) as count,ifnull(sum(amount),0) as money from gc_wallet_pay_log where  order_code not in('00','') and request_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> walletSummitBankFailed=DbUp.upTable("gc_wallet_pay_log").dataSqlOne(walletSummitBankFailedSql,timeMap);
		 String walletSummitBankFailedCount=walletSummitBankFailed.get("count").toString();
		 infodata.put("walletSummitBankFailedCount", "钱包提现向银行提交失败的总数为" + walletSummitBankFailedCount);
		 String walletSummitBankFailedAmount=walletSummitBankFailed.get("money").toString();
		 infodata.put("walletSummitBankSuccessAmount", "钱包提现向银行提交失败的总金额" + walletSummitBankFailedAmount);
		 
		//昨日钱包提现银行回传转账交易成功的总数和昨日转账交易成功的总金额
		 String walletWithdrawSuccessSql="select count(DISTINCT(withdraw_code)) as count,ifnull(sum(amount),0) as money from gc_wallet_pay_log where  notify_code='00' and notify_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> walletWithdrawSuccess=DbUp.upTable("gc_wallet_pay_log").dataSqlOne(walletWithdrawSuccessSql,timeMap);
		 String walletWithdrawSuccessCount=walletWithdrawSuccess.get("count").toString();
		 infodata.put("walletWithdrawSuccessCount", "钱包提现支付成功的总数" + walletWithdrawSuccessCount);
		 String walletWithdrawSuccessAmount=walletWithdrawSuccess.get("money").toString();
		 infodata.put("walletWithdrawSuccessAmount", "钱包提现支付成功的总金额" + walletWithdrawSuccessAmount);
		 
		//昨日银行回传打款失败的总数和昨日打款失败的总金额
		 String walletWithdrawFailedSql="select count(DISTINCT(withdraw_code)) as count,ifnull(sum(amount),0) as money from gc_wallet_pay_log where  notify_code not in('00','') and notify_time between :yesterdayZeroTime and :currentZeroTime ";
		 Map<String, Object> walletWithdrawFailed=DbUp.upTable("gc_wallet_pay_log").dataSqlOne(walletWithdrawFailedSql,timeMap);
		 String walletWithdrawFailedCount=walletWithdrawFailed.get("count").toString();
		 infodata.put("withdrawFailedCount", "提现支付失败的总数" + walletWithdrawFailedCount);
		 String walletWithdrawFailedAmount=walletWithdrawFailed.get("money").toString();
		 infodata.put("withdrawFailedAmount", "提现支付失败的总金额" + walletWithdrawFailedAmount);
		 
		// 在两天内银行没有回传打款状态的订单
		List<Map<String, Object>> backWalletRecordList = DbUp.upTable("gc_wallet_pay_log").dataQuery("DISTINCT (withdraw_code) , amount ", "", 
				"order_code='00' and notify_code='' and request_time  between :beforebeforeYesterdayZeroTime and :beforeYesterdayZeroTime", timeMap, 0, 0);
		
		if (backWalletRecordList.size() > 0) {

			infodata.put("backWalletRecordList", "两天银行没有回传钱包提现订单返回状态的订单" + backWalletRecordList);
		}
		else {
			infodata.put("backWalletRecordList", "两天银行没有回传钱包提现订单的返回状态数量为0");
		}
		 
		 
		
		sendMail(infodata);

	}
    
	private void sendMail(Map info) {
		String yesterdayTime = DateUtil.toString(
				DateUtil.addDays(new Date(), -1), DateUtil.sdfDateOnly);
		String title ="微公社" + yesterdayTime + "提现支付情况统计";
		String content = "";
		Iterator iterator=info.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next(); 
			Object val = info.get(key); 
			content += (String)val + "<br/>";
		}

		 WgsMailSupport.INSTANCE.sendMail("统计当日提现信息",title,content);

	}

	/*public static void main(String[] args) throws JobExecutionException {

		JobWithdrawInfoSummarize summarize = new JobWithdrawInfoSummarize();
		summarize.doExecute(null);
	}*/

}
