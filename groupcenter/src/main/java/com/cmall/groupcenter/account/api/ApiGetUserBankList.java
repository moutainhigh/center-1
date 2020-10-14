package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.GetBankInfoResult;
import com.cmall.groupcenter.account.model.GetBankInfoResult.BankInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加银行卡信息
 * 
 * @author chenxk
 * 
 */
public class ApiGetUserBankList extends RootApiForToken<GetBankInfoResult, RootInput> {

	public GetBankInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {

		GetBankInfoResult getBankInfoResult = new GetBankInfoResult();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		
		if(getBankInfoResult.upFlagTrue()){
			List<MDataMap> mdate = DbUp.upTable("gc_member_bank").query("bank_code,bank_name,card_code,card_kind,bank_phone,bank_icon_uid",
					"-create_time","flag_enable =1 and account_code=:account_code", new MDataMap("account_code",sAccountCode), -1, 0);
			
			List<BankInfo> bankList = new ArrayList<BankInfo>();
			if(mdate != null && mdate.size() >0){
				//查询提现失败的银行卡信息
				List<MDataMap>  payOrderInfos = DbUp.upTable("gc_pay_order_info").query("bank_code,pay_status", "", "account_code=:account_code and pay_status='4497465200070003' group by bank_code", new MDataMap("account_code",sAccountCode), -1, 0);
				//查询有提现记录银行卡
				List<MDataMap>  withdrawBankCodeInfos = DbUp.upTable("gc_pay_order_info").query("bank_code", "-create_time", "account_code=:account_code and pay_status !='4497465200070003' and order_status != '4497153900120003' group by bank_code",new MDataMap("account_code",sAccountCode), -1, 0);
				for(MDataMap mDate : mdate){
					BankInfo bankInfo = getBankInfoResult.new BankInfo();
					Object iconObject = DbUp.upTable("gc_bank_icon").dataGet("icon_url", " uid = '"+mDate.get("bank_icon_uid")+"'",null);
					String icon_urlString = iconObject!=null ? iconObject.toString() : "";
					String cardCode = mDate.get("card_code");
					bankInfo.setTailNumber((StringUtils.isNotEmpty(cardCode) && cardCode.length() >4) ? cardCode.substring(cardCode.length()-4) : cardCode);;
					bankInfo.setBankName(mDate.get("bank_name"));
					bankInfo.setIconUrl(icon_urlString);
					bankInfo.setCardKind(mDate.get("card_kind"));
					bankInfo.setBankCode(mDate.get("bank_code"));
					bankInfo.setBankPhone(mDate.get("bank_phone"));
					//前两位后四位
					if((StringUtils.isNotEmpty(cardCode) && cardCode.length() >5)){
						bankInfo.setCardNumber(cardCode.substring(0, 2)+(cardCode.substring(2, cardCode.length()-4).replaceAll("\\w", "*"))+cardCode.substring(cardCode.length()-4));
					} else {
						bankInfo.setCardNumber(cardCode);
					} 
					for(MDataMap payOrderInfo : payOrderInfos){
						if(payOrderInfo.get("bank_code").equals(mDate.get("bank_code"))){
							bankInfo.setIsEnable("0");
							break;
						}
					}
					for(MDataMap withdrawBankCodeInfo : withdrawBankCodeInfos){
						if(bankInfo.getIsEnable().equals("1")){
							bankInfo.setIsDefault(withdrawBankCodeInfo.get("bank_code").equals(mDate.get("bank_code")) ? "1" : "0");
							break;
						}
					}
					
					bankList.add(bankInfo);
				}
			}
			getBankInfoResult.setBankList(bankList);
		}
		
		return getBankInfoResult;
	}
}
