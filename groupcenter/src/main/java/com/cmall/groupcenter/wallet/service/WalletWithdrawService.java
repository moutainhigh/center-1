package com.cmall.groupcenter.wallet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcWalletWithdrawInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcWalletWithdrawLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawInfo;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawLog;
import com.cmall.groupcenter.txservice.TxTraderWalletService;
import com.cmall.groupcenter.wallet.model.TraderWalletMoneyOptionModel;
import com.cmall.groupcenter.wallet.model.WithdrawApplyInput;
import com.cmall.groupcenter.wallet.model.WithdrawApplyResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;

/**
 * 微公社钱包提现相关
 * @author panwei
 *
 */
public class WalletWithdrawService extends BaseClass{
	
	//获取钱包账户（不存在则创建）
	public MDataMap findWallet(String traderCode, String accountCode) {
		MDataMap walletMap=DbUp.upTable("gc_trader_wallet").one("trader_code",traderCode,"account_code",accountCode);
		//不存在则判断后创建
		if(walletMap==null){
			DbUp.upTable("gc_trader_wallet").insert("trader_code",traderCode,"account_code",accountCode,
					"create_time",FormatHelper.upDateTime());
			walletMap=DbUp.upTable("gc_trader_wallet").one("trader_code",traderCode,"account_code",accountCode);
		}
		
		return walletMap;
	}
	
	//获取account_code(不存在返回null)
	public String getAccountCode(String memberCode,String manageCode){
		MDataMap memberInfo=DbUp.upTable("mc_member_info").one("manage_code",manageCode,"member_code",memberCode);
		if(memberInfo!=null){
			return memberInfo.get("account_code");
		}else{
			return null;
		}
	}


	//获取商户编号
	public String getTraderCode(String manageCode){
		MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
		if(appMap!=null){
			return appMap.get("trade_code");
		}else{
			return null;
		}
		
	}

}
