package com.cmall.groupcenter.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class GroupCommonService extends BaseClass{

	/**
	 * 获取好友关系度
	 * custAccountCode为accountCode的n度好友
	 * 0：自己，1：一度，2：二度，-1：上线，-2：上上线,5:没有关系
	 * @param memberCode
	 * @param custMemberCode
	 * @return
	 */
	public int getRelationLevelByAccountCode(String accountCode,String custAccountCode){
		//默认为5
		int relationLevel=5;
		MWebResult mWebResult=new MWebResult();
		if(!accountCode.equals(custAccountCode)){
			//一度好友,二度好友
			if(mWebResult.upFlagTrue()){
				MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",custAccountCode,"flag_enable","1");
				if(oneMap!=null){
					//一度好友
					if(oneMap.get("parent_code").equals(accountCode)){
						relationLevel=1;
						mWebResult.setResultCode(0);
					}
					else{
						//二度好友
						if(DbUp.upTable("gc_member_relation").count("account_code",oneMap.get("parent_code"),"parent_code",accountCode,"flag_enable","1")>0){
							relationLevel=2;
							mWebResult.setResultCode(0);
						}
					}
				}	
			}
			//上线，上上线
			if(mWebResult.upFlagTrue()){
				MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",accountCode,"flag_enable","1");
				if(oneMap!=null){
					//上线
					if(oneMap.get("parent_code").equals(custAccountCode)){
						relationLevel=-1;
						mWebResult.setResultCode(0);
					}
					else{
						//上上线
						if(DbUp.upTable("gc_member_relation").count("account_code",oneMap.get("parent_code"),"parent_code",custAccountCode,"flag_enable","1")>0){
							relationLevel=-2;
							mWebResult.setResultCode(0);
						}
					}
				}	

			}
		}
		else{
			relationLevel=0;
		}
		
		return relationLevel;
	}
	
	/**
	 * 通过账户编号获取手机号
	 * @param accountCode
	 * @return
	 */
	public String getMobileByAccountCode(String accountCode){
		String mobile="";
		List<MDataMap> memberList=DbUp.upTable("mc_member_info").queryByWhere("account_code",accountCode);
		if(memberList!=null&&memberList.size()>0){
			MDataMap memberMap=memberList.get(0);
			if(StringUtils.isNotEmpty(memberMap.get("member_code"))){
				List<MDataMap> mobileList=DbUp.upTable("mc_login_info").queryByWhere("member_code",memberMap.get("member_code"));
				if(mobileList!=null&&mobileList.size()>0){
					MDataMap mobileMap=mobileList.get(0);
					if(StringUtils.isNotEmpty(mobileMap.get("login_name"))){
						mobile=mobileMap.get("login_name");
					}
				}
			}
			
		}
		return mobile;
	}
	
	/**
	 * 通过账户编号获取用户编号
	 * manageCode为空时，随便取一条
	 * @param accountCode
	 * @param manageCode
	 * @return
	 */
	public String getMemberCodeByAccountCode(String accountCode,String manageCode){
		String memberCode="";
		if(StringUtils.isNotBlank(manageCode)){
			MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountCode,"manage_code",manageCode,"flag_enable","1");
			if(memberMap!=null){
				memberCode=memberMap.get("member_code");
			}
		}
		else{
			MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",accountCode,"flag_enable","1");
			if(memberMap!=null){
				memberCode=memberMap.get("member_code");
			}
		}
		
		return memberCode;
	}
	
	public String getAccountCodeByMemberCode(String memberCode){
		String accountCode="";
		MDataMap accountMap=DbUp.upTable("mc_member_info").one("member_code",memberCode,"flag_enable","1");
		if(accountMap!=null){
			accountCode=accountMap.get("account_code");
		}
		return accountCode;
	}
}
