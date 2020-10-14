package com.cmall.groupcenter.account.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.GroupConstant.PapersEnum;
import com.cmall.groupcenter.account.model.AccountModel;
import com.cmall.groupcenter.account.model.PersonalResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;
/**
 * 
 * @author fengls
 *
 */
public class ApiPersonalCenter extends 
			RootApiForToken<PersonalResult, RootInput>  {


	public PersonalResult Process(RootInput inputParam,MDataMap mRequestMap) {

		PersonalResult personalResult = new PersonalResult();
		//根据用户编号查账户编号（oneWhere取一条记录）
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		//	得到电话号码。
		 personalResult.setMobile(getOauthInfo().getLoginName());
		 
		 //查询个人头像
		 String headIconSql = "select head_icon_url,nickname from mc_extend_info_groupcenter where app_code='"+ this.getManageCode() +"' and member_code= '"+this.getUserCode()+"'";
		 Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
	     if(headIconMap!=null && headIconMap.get("head_icon_url")!=null){
	    	 personalResult.setHeadIconUrl(headIconMap.get("head_icon_url")==null ? "" : String.valueOf(headIconMap.get("head_icon_url")));
	     } 
	     //personalResult.setNickName(headIconMap==null || StringUtils.isBlank(String.valueOf(headIconMap.get("nickname"))) ? "" :  String.valueOf(headIconMap.get("nickname")) );
	     Map<String, String> map = new HashMap<String, String>();
	     map.put("member_code", getUserCode());
	     personalResult.setNickName(NickNameHelper.getNickName(map));
//		得到加入时间和级别
		 String sql = "select a.create_time as create_time,b.level_name as level_name from gc_group_account a,gc_group_level b where a.account_level = b.level_code and a.account_code = '"+sAccountCode+"'";
		 Map<String, Object> userInfoMap = DbUp.upTable("gc_group_account").dataSqlOne(sql,null);
		 MDataMap dataMap = DbUp.upTable("mc_login_info").oneWhere("create_time", "create_time", " login_name=:login_name and flag_enable=1 ", "login_name",personalResult.getMobile());
		 String sLevelCode = GroupConst.DEFAULT_LEVEL_CODE;
			MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
					"level_code", sLevelCode);
		// System.out.println("创建时间为：====================="+dataMap.get("create_time"));
		personalResult.setCreateTime(dataMap==null||StringUtils.isBlank(dataMap.get("create_time"))?"":dataMap.get("create_time"));
		if(userInfoMap!=null){
			personalResult.setLevelName(userInfoMap.get("level_name")!=null&&!userInfoMap.get("level_name").equals("")?userInfoMap.get("level_name").toString():mLevelMap.get("level_name"));
		}else {
			personalResult.setLevelName(mLevelMap.get("level_name"));
		}
//		得到邀请人电话号码
		String sql2 = "SELECT b.login_name AS login_name,b.manage_code,meig.nickname nick_name,a.parent_code parent_code,b.member_code member_code FROM groupcenter.gc_member_relation a INNER JOIN mc_member_info c ON a.parent_code = c.account_code INNER JOIN mc_login_info b ON b.member_code = c.member_code LEFT JOIN mc_extend_info_groupcenter meig ON meig.member_code = b.member_code AND b.manage_code = meig.app_code WHERE a.account_code = '" +sAccountCode+"' ";
		List<Map<String, Object>> membeInfoMap = DbUp.upTable("mc_login_info").dataSqlList(sql2, null);
		if(membeInfoMap != null && membeInfoMap.size()>0){
			 personalResult.setInviter(membeInfoMap.get(0).get("login_name")!=null?membeInfoMap.get(0).get("login_name").toString():"") ;
			 for(Map<String,Object> managecodeMap : membeInfoMap) {
				 if(this.getManageCode().toString().equals(String.valueOf(managecodeMap.get("manage_code")))){
//					 personalResult.setInviterNickName(StringUtils.isBlank((String)managecodeMap.get("nick_name")) ? "" :  (String)managecodeMap.get("nick_name") );
					 map.put("member_code", String.valueOf(managecodeMap.get("member_code")));
					 map.put("account_code_wo", sAccountCode);
					 map.put("account_code_ta", String.valueOf(managecodeMap.get("parent_code")));
					 personalResult.setInviterNickName(NickNameHelper.getNickName(map));
				 }
			 }
		 }
		
		//MDataMap membeInfoMap =  DbUp.upTable("gc_member_relation").one("account_code",sAccountCode);
	
//		我的消息数  后台更改了字段
		 personalResult.setMessageSize(DbUp.upTable("sc_comment_push_single").count("is_read","4497465200180001","account_code",sAccountCode,"app_code","SI2011"));
//		 消息推送，自己建一张表s,0为关，查出为关的都关了，默认推送,1为开
		 MDataMap userPushMap =  DbUp.upTable("sc_user_push_info").one("app_code","","user_code",getUserCode(),"app_code","SI2011");
		 if(userPushMap!=null){
			 personalResult.setIsPush(userPushMap.get("is_send")!=null?userPushMap.get("is_send"):"449747100002");
		 }else{
			 personalResult.setIsPush("449747100001");
		 }
		 
		 
//		 激活产品
		 String sSql  ="select b.manage_code as manage_code from mc_member_info a ,mc_member_info b where a.account_code = b.account_code and a.member_code ='"+getUserCode()+"';";
		 List<Map<String, Object>> list =  DbUp.upTable("mc_member_info").dataSqlList(sSql, null);
		 List<String> activationlist = new ArrayList<String>(); 
		 for(Map<String,Object> data : list){
			 activationlist.add(data.get("manage_code").toString());
		 }
		 personalResult.setActivationList(activationlist);
		 
		 //我的收藏数量
		 int total = DbUp.upTable("nc_collections").dataCount("app_code =:app_code and flag =:flag and member_code =:member_code", new MDataMap("app_code", getManageCode(),"flag","4497472000020001","member_code",getUserCode()));
		 personalResult.setFavorites(total);
		 
		 Map<String, Object>  mDate = DbUp.upTable("gc_member_papers_info").dataSqlOne("select * from gc_member_papers_info where account_code='"+sAccountCode+"'", new MDataMap());
		 if(mDate != null){
			 personalResult.setPapersName(PapersEnum.getCardAliasByCardType((String)mDate.get("papers_type")));
		 }
		 
		 //获取我的银行卡数量
		 List<MDataMap> mdate = DbUp.upTable("gc_member_bank").query("bank_code",
					"-create_time","flag_enable =1 and account_code=:account_code", new MDataMap("account_code",sAccountCode), -1, 0);
		 
		 int cardNum = mdate.size();
			 
		 personalResult.setBankCardsCount(String.valueOf(cardNum));
		 
		//获取账户的资金信息
		 MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
					"","", "", "account_code", sAccountCode);
		 
		 if(mGroupAccountMap != null){
			 
			//预计返利
			 personalResult.getAccountProperty().setExpectedRebateMoney(mGroupAccountMap.get("account_rebate_money"));
			 	
			 //账户金额
			 personalResult.getAccountProperty().setAccountMoney(mGroupAccountMap.get("account_withdraw_money"));
			 
			 //已返利
			 personalResult.getAccountProperty().setAlreadyRebateMoney(mGroupAccountMap.get("total_withdraw_money"));	
			 
		 }
		 
		 //获取优惠券数量
		 List<MDataMap> mdata = DbUp.upTable("gc_mem_coupon").query("coupon_uid",
					"-create_time,-zid"," account_code = :accountCode ", new MDataMap("accountCode",sAccountCode),-1,0);//查询用户相关的优惠卷唯一标识
		 int num = mdata.size();
		 personalResult.setCouponCount(String.valueOf(num));
		 
			 
		return personalResult;
	}


	
}
