package com.cmall.groupcenter.func;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.groupcenter.GcTraderFoundsChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 调整微公社账户余额(可提现账户金额)
 * @author GaoYang
 * @CreateDate 2015年6月8日下午5:24:17
 *
 */
public class FuncChangeAccountBlance extends RootFunc{

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String updateTime = FormatHelper.upDateTime();
		
		String updateUser = UserFactory.INSTANCE.create().getLoginName();
		
		//账户编号
		String accountCode = mAddMaps.get("account_code");
		//类型
		String changeType = mAddMaps.get("change_type");
		//商家编号
		String traderCode = mAddMaps.get("trader_code");
		//订单编号
		String orderCode = mAddMaps.get("order_code");
		//金额
		String accountWithdrawMoney = mAddMaps.get("account_withdraw_money");
		//原因
		String reason = mAddMaps.get("change_reason");
		
		//账户校验：不能为空
		if(StringUtils.isBlank(accountCode)){
			mResult.inErrorMessage(918519025);
			return mResult;
		}
		
		//类型校验：不能为空
		if(StringUtils.isBlank(changeType)){
			mResult.inErrorMessage(918519035);
			return mResult;
		}
		
		//商户校验：不能为空
		if("449747580001".equals(changeType)){
			if(StringUtils.isBlank(traderCode)){
				mResult.inErrorMessage(918519036);
				return mResult;
			}
		}

		
		//校验金额:不能是非数字、0、空,不能超过1000
		if(StringUtils.isBlank(accountWithdrawMoney)){
			mResult.inErrorMessage(918519028);
			return mResult;
		}
		
		BigDecimal withdrawMoney=new BigDecimal(accountWithdrawMoney);
		BigDecimal zero=BigDecimal.ZERO;
		BigDecimal thousand = new BigDecimal(1000);
		
		if(checkIsNumeric(accountWithdrawMoney)){
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
		
		//原因校验：不能为空
		if(StringUtils.isBlank(reason)){
			mResult.inErrorMessage(918519027);
			return mResult;
		}
		
		if (mResult.upFlagTrue()) {
			
			//账户操作类
			TxGroupAccountService txGroupAccountService=BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			//生成账户余额变动的关联编号
			String changeCodes = WebHelper.upCode("MAS");
			//关联商户的情况
			String changeMemo = "";
			
			//20150817修改
			//检查并自动创建微公社账户
			GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
			mResult = groupAccountSupport.checkAndCreateGroupAccount(accountCode);
			if(mResult.upFlagTrue()){
				if("449747580001".equals(changeType)){
					changeMemo = accountCode + "账户在"+ updateTime + "时间关联商户"+traderCode +"人工返利" + accountWithdrawMoney +"元";
					MDataMap traderMap=DbUp.upTable("gc_trader_info").one("trader_code",traderCode);
					if(withdrawMoney.compareTo(zero) == -1){
						//给账户减钱，要把钱返回商户的保证金
						if(traderMap != null){
							GcTraderFoundsChangeLog gcTraderFoundsChangeLog = new GcTraderFoundsChangeLog();
							gcTraderFoundsChangeLog.setTraderCode(traderCode);
							gcTraderFoundsChangeLog.setAccountCode(traderMap.get("account_code"));
							gcTraderFoundsChangeLog.setGurranteeChangeAmount(withdrawMoney.abs());
							gcTraderFoundsChangeLog.setChangeType("4497472500030005");//人工加钱
							gcTraderFoundsChangeLog.setCreateUser(updateUser);
							gcTraderFoundsChangeLog.setRemark("后台人工操作："+changeMemo+",账户余额变动关联编号："+changeCodes);
							if(StringUtils.isNotBlank(orderCode)){
								gcTraderFoundsChangeLog.setOrderCode(orderCode);
							}
							txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
						}
					}else{
						//给账户加钱，要扣除商户的保证金
						if(traderMap != null){
							BigDecimal gurranteeBalance=new BigDecimal(traderMap.get("gurrantee_balance"));
							if(withdrawMoney.compareTo(gurranteeBalance) == 1){
								mResult.inErrorMessage(918519037);
								return mResult;
							}else{
								GcTraderFoundsChangeLog gcTraderFoundsChangeLog = new GcTraderFoundsChangeLog();
								gcTraderFoundsChangeLog.setTraderCode(traderCode);
								gcTraderFoundsChangeLog.setAccountCode(traderMap.get("account_code"));
								gcTraderFoundsChangeLog.setGurranteeChangeAmount(withdrawMoney.negate());
								gcTraderFoundsChangeLog.setChangeType("4497472500030006");//人工减钱
								gcTraderFoundsChangeLog.setCreateUser(updateUser);
								gcTraderFoundsChangeLog.setRemark("后台人工操作："+changeMemo+",账户余额变动关联编号："+changeCodes);
								if(StringUtils.isNotBlank(orderCode)){
									gcTraderFoundsChangeLog.setOrderCode(orderCode);
								}
								txGroupAccountService.updateTraderDeposit(gcTraderFoundsChangeLog);
							}
						}
					}
				}else{
					//直接给账户充值的情况
					changeMemo = accountCode + "账户在"+ updateTime + "时间人工充值" + accountWithdrawMoney +"元";
				}
			}else{
				//系统中不存在此账户
				return mResult;
			}
			
			if(mResult.upFlagTrue()){
				//提现日志类型 4497465200040010 人工加钱 4497465200040011人工减钱
				String withdrawChangeType = "";
				if(withdrawMoney.compareTo(zero) == -1){
					withdrawChangeType = "4497465200040011";
				}else{
					withdrawChangeType = "4497465200040010";
				}
				
		        List<GcWithdrawLog> withdrawList=new ArrayList<GcWithdrawLog>();
				GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
				
		        gcWithdrawLog.setAccountCode(accountCode);
		        gcWithdrawLog.setWithdrawMoney(withdrawMoney);
		        gcWithdrawLog.setWithdrawChangeType(withdrawChangeType);
		        gcWithdrawLog.setChangeCodes(changeCodes);
		        withdrawList.add(gcWithdrawLog);
				txGroupAccountService.updateAccount(null, withdrawList);
				
				//记录日志
				MDataMap inMap = new MDataMap();
				inMap.put("account_code", accountCode);
				inMap.put("account_withdraw_money", accountWithdrawMoney);
				inMap.put("change_reason", reason);
				inMap.put("update_time", updateTime);
				inMap.put("update_user", updateUser);
				inMap.put("change_codes", changeCodes);
				inMap.put("change_type", changeType);
				inMap.put("trader_code", traderCode);
				inMap.put("order_code", orderCode);
				inMap.put("change_memo", changeMemo);
				
				DbUp.upTable("gc_account_balance_change_log").dataInsert(inMap);
			}

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
