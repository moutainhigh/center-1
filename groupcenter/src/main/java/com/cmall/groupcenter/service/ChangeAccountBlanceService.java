package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 调整账户可提现余额
 * @author GaoYang
 *
 */
public class ChangeAccountBlanceService extends BaseClass{

	/**
	 * 调整账余额
	 * @param accountCode 账户
	 * @param money 金额(正数字)
	 * @param reason 调整原因
	 * @param operator 操作人
	 * @return
	 */
	public MWebResult changeBlanceByAccount(String accountCode,String money,String reason,String operator){
		MWebResult mResult = new MWebResult();
		
		//账户校验：不能为空
		if(StringUtils.isBlank(accountCode)){
			mResult.inErrorMessage(918519025);
			return mResult;
		}
		
		//原因校验：不能为空
		if(StringUtils.isBlank(reason)){
			mResult.inErrorMessage(918519027);
			return mResult;
		}
		
		//操作人校验：不能为空
		if(StringUtils.isBlank(operator)){
			mResult.inErrorMessage(918519031);
			return mResult;
		}
		
		//校验金额:不能是非数字、0、空,不能超过1000
		if(StringUtils.isBlank(money)){
			mResult.inErrorMessage(918519028);
			return mResult;
		}
		
		BigDecimal withdrawMoney=new BigDecimal(money);
		if(checkIsNumeric(money)){
			BigDecimal zero=BigDecimal.ZERO;
			BigDecimal thousand = new BigDecimal(1000);
			
			if(withdrawMoney.compareTo(zero) == 0){
				mResult.inErrorMessage(918519028);
				return mResult;
			}
			
			if(withdrawMoney.compareTo(thousand) == 1){
				mResult.inErrorMessage(918519029);
				return mResult;
			}
			
		}else{
			mResult.inErrorMessage(918519030);
			return mResult;
		}
		
		//检查并自动创建微公社账户
		GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
		mResult = groupAccountSupport.checkAndCreateGroupAccount(accountCode);
		
		if (mResult.upFlagTrue()) {
			//提现日志类型 
			String withdrawChangeType = "4497465200040012";//任务奖励
			//更新账户信息
			String updateTime = FormatHelper.upDateTime();
			TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
	        List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
			GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
			String changeCodes = WebHelper.upCode("MAS");
	        gcWithdrawLog.setAccountCode(accountCode);
	        gcWithdrawLog.setWithdrawMoney(withdrawMoney);
	        gcWithdrawLog.setWithdrawChangeType(withdrawChangeType);
	        gcWithdrawLog.setChangeCodes(changeCodes);
	        withdrawList.add(gcWithdrawLog);
			txGroupAccountService.updateAccount(null, withdrawList);
			
			DbUp.upTable("gc_account_balance_change_log").insert("account_code",accountCode,"account_withdraw_money",money,
					"change_reason",reason,"update_time",updateTime,"update_user",operator,"change_codes",changeCodes);
		}
		
		return mResult;
	}

	/**
	 * 校验金额是否是正负数字 并保留2位小数
	 * @param money
	 * @return true是正确格式
	 */
	private boolean checkIsNumeric(String money) {
		Pattern pattern = Pattern.compile("^[+-]?[0-9]+(.[0-9]{1,2})?$");
		Matcher isNum = pattern.matcher(money);
		if(!isNum.matches()){
			return false;
		}
		return true;
	}
	
}
