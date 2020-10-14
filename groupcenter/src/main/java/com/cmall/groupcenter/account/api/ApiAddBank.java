package com.cmall.groupcenter.account.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AddBankInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 添加银行卡信息
 * 
 * @author srnpr
 * 
 */
public class ApiAddBank extends RootApiForToken<RootResultWeb, AddBankInput> {

	public RootResultWeb Process(AddBankInput inputParam, MDataMap mRequestMap) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		
		MDataMap memberInfo = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode());
		if(memberInfo == null){
			rootResultWeb.inErrorMessage(915805334);
		}
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, memberInfo.get("account_code"));
		
		if (StringUtils.isNotEmpty(sLockCode)) {
			MDataMap papersInfo = null;
			if(rootResultWeb.upFlagTrue()){
				
				papersInfo = DbUp.upTable("gc_member_papers_info").one("account_code", memberInfo.get("account_code"),"flag_enable","1");
				
				if(null == papersInfo){
					rootResultWeb.inErrorMessage(915805333);
				}
			}
			if(rootResultWeb.upFlagTrue()){
				
				GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
				groupAccountSupport.checkAndCreateGroupAccount(memberInfo.get("account_code"));
				
				
				//判断银行卡号是否存在 
				String sWhere = "member_code='" + getUserCode() + "' and account_code ='" + memberInfo.get("account_code") + "' and flag_enable=1 and card_code ='" +inputParam.getCardCode() +"'";
				 if(DbUp.upTable("gc_member_bank").dataCount(sWhere, new MDataMap()) > 0){
					 rootResultWeb.inErrorMessage(915805226);
				 }else{
					 List<MDataMap> existBankList = DbUp.upTable("gc_member_bank").query("bank_code,bank_name,card_code,card_kind",
								"-create_time","flag_enable =0 and member_code=:member_code and account_code=:account_code and card_code=:card_code", new MDataMap("member_code",getUserCode(),"account_code",memberInfo.get("account_code"),"card_code",inputParam.getCardCode()), -1, 0);
					 
					 if(existBankList != null && existBankList.size() > 0){
						 DbUp.upTable("gc_member_bank").dataUpdate(new MDataMap("bank_code",existBankList.get(0).get("bank_code"),
								 "user_name",papersInfo.get("user_name"),"card_kind",inputParam.getCardKind(),"bank_name",inputParam.getBankName(),"bank_phone",inputParam.getBankPhone(),"flag_enable","1"), "user_name,card_kind,bank_name,bank_phone,flag_enable", "bank_code");
					 
					 }else{
						 MDataMap mWhereMap = new MDataMap();
							mWhereMap.inAllValues("account_code", memberInfo.get("account_code"));

							int iMaxNumber = Integer
									.valueOf(DbUp.upTable("gc_member_bank")
											.dataGet(" ifnull(max(sort_num),0) ", "", mWhereMap)
											.toString()) + 100;
							
							Object iconUidObj = DbUp.upTable("gc_bank_icon").dataGet("uid", " bank_name like '%"+inputParam.getBankName()+"%'",null);
							
							String iconUid = iconUidObj!=null ? iconUidObj.toString() : "";
							// 开始插入数据
							DbUp.upTable("gc_member_bank").insert("bank_code",
									WebHelper.upCode("GCMB"), "member_code", getUserCode(),
									"user_name",papersInfo.get("user_name"),
									"account_code", memberInfo.get("account_code"), "card_code",
									inputParam.getCardCode(), "card_kind",
									inputParam.getCardKind(),"bank_name",
									inputParam.getBankName(),"bank_phone",
									inputParam.getBankPhone(), "create_time",
									FormatHelper.upDateTime(), "sort_num",
									String.valueOf(iMaxNumber),"bank_icon_uid",iconUid );
					 }
				 }
				 
			}
			// 解鎖
			WebHelper.unLock(sLockCode);
		}
		return rootResultWeb;
	}
}
