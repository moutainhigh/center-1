package com.cmall.groupcenter.groupapp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupapp.model.AccountModel;
import com.cmall.groupcenter.groupapp.model.GetHomePageInfoResult;
import com.cmall.groupcenter.groupapp.model.HomeContent;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class HomePageInfoService extends BaseClass{

	//获取首页数据
	public GetHomePageInfoResult getHomePageInfoResult(String memberCode,String manageCode) {
		GetHomePageInfoResult result=new GetHomePageInfoResult();
		//获取用户账户信息
		MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",memberCode,"manage_code",manageCode);
		if(memberMap!=null){
			String accountCode=memberMap.get("account_code");
			MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
					"","", "", "account_code", accountCode);
			
			AccountModel accountModel=new AccountModel();
			if(mGroupAccountMap!=null){
				accountModel.setAccountMoney(mGroupAccountMap.get("account_withdraw_money"));
				accountModel.setAlreadyRebateMoney(mGroupAccountMap.get("total_withdraw_money")); 
				accountModel.setExpectedRebateMoney(mGroupAccountMap.get("account_rebate_money"));
			}else{
				accountModel.setAccountMoney("0.00");
				accountModel.setAlreadyRebateMoney("0.00"); 
				accountModel.setExpectedRebateMoney("0.00");
			}
			
			
			
			result.setAccountModel(accountModel);
			//广告信息
			List<HomeContent> homeContentList=new ArrayList<HomeContent>();
			MDataMap advertPlace=DbUp.upTable("nc_advertise_place").one("place_name","APP首页");
			if(advertPlace!=null){
				MDataMap mWhereMap=new MDataMap();
				mWhereMap.put("place_code", advertPlace.get("place_code"));
				mWhereMap.put("now_time", FormatHelper.upDateTime());
				List<MDataMap> advertList=DbUp.upTable("nc_advertise").queryAll("", "sort_num ASC", "place_code=:place_code and status='4497472000110001' "
						+ "and start_time<=:now_time and end_time>=:now_time", mWhereMap);
				
				for(MDataMap advert:advertList){
					HomeContent homeContent=new HomeContent();
					String[] url=advert.get("adImg_url").split("@@");
					String adImg_url="";
					if(url[0].equals("code")){
						String shopUrl=bConfig("groupcenter.wei_shop_url");

						adImg_url=shopUrl+"group.aspx?act=p&source=jyh&pid="+url[1];
					}else{
						
						adImg_url=url[1];
						homeContent.setTitle(advert.get("ad_name"));
					}
					homeContent.setImageUrl(advert.get("adImg"));
					homeContent.setJumpType("0");
					homeContent.setParams(adImg_url);
					homeContentList.add(homeContent);
				}
				result.setHomeContentList(homeContentList);
			}
		}else{
			result.inErrorMessage(915805334);
		}
		return result;
	}
	
}
