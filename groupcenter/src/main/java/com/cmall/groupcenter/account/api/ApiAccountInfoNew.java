package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountInfoResultNew;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 微公社账户信息
 * @author chenbin
 *
 */
public class ApiAccountInfoNew extends
		RootApiForToken<AccountInfoResultNew, RootInput> {

	public AccountInfoResultNew Process(RootInput inputParam, MDataMap mRequestMap) {
		AccountInfoResultNew accountInfoResultNew=new AccountInfoResultNew();
		accountInfoResultNew.setLoginName(getOauthInfo().getLoginName());
		qryAccountInfo(accountInfoResultNew,getUserCode());
		return accountInfoResultNew;
	}

	public void qryAccountInfo(AccountInfoResultNew accountInfoResultNew,String memberCode){

        //本月消费
        double currentMonthConsumeMoney = 0d;

		//查询个人头像
		 String headIconSql = "select head_icon_url,nickname from mc_extend_info_groupcenter where  member_code= '"+this.getUserCode()+"'";
		 Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
	     if(headIconMap!=null && headIconMap.get("head_icon_url")!=null){
	    	 accountInfoResultNew.setHeadIconUrl(headIconMap.get("head_icon_url")==null ? "" : String.valueOf(headIconMap.get("head_icon_url")));
	     } 
	     accountInfoResultNew.setNickName(headIconMap==null || StringUtils.isBlank(String.valueOf(headIconMap.get("nickname"))) ? "" :  String.valueOf(headIconMap.get("nickname")) );

		String sAccountCode = DbUp.upTable("mc_member_info")
                .oneWhere("account_code", "", "", "member_code", memberCode)
				.get("account_code");
		MDataMap mGroupAccountMap = DbUp.upTable("gc_group_account").oneWhere(
				"","", "", "account_code", sAccountCode);
		
		String sCurrentMonth = DateHelper
				.upMonth(FormatHelper.upDateTime());
		accountInfoResultNew.setCurrentMonth(sCurrentMonth.substring(5));
		
		String sLevelCode = "";
		String scaleReckon="";




		// 如果有微公社账户信息
		if (mGroupAccountMap != null) {

			accountInfoResultNew.setExpectRebateMoney(mGroupAccountMap
					.get("account_rebate_money"));
			accountInfoResultNew.setWithdrawMoney(mGroupAccountMap
					.get("account_withdraw_money"));
			accountInfoResultNew.setTotalRebateMoney(mGroupAccountMap.get("total_withdraw_money"));
			scaleReckon=mGroupAccountMap.get("scale_reckon");
			sLevelCode = mGroupAccountMap.get("account_level");

			// 开始获取用户的所有下线统计
			List<AccountRelation> listRelations = new GroupAccountSupport()
					.upAccountFlower(sAccountCode);
			accountInfoResultNew.setAllFriend(listRelations.size() - 1);

			// 判断是否可绑定上线 如果关系表中有则不可绑定
			accountInfoResultNew
					.setFlagRelation(Integer
							.parseInt(DbUp
									.upTable("gc_member_relation")
									.dataGet(
											" count(1) ",
											"account_code=:account_code or parent_code=:account_code",
											new MDataMap("account_code",
													sAccountCode)).toString()) > 0 ? 0
							: 1);

			
			MDataMap mActiveMap = DbUp.upTable("gc_active_month").oneWhere(
					"sum_consume,sum_member", "", "", "account_code",
					sAccountCode, "active_month", sCurrentMonth);
			// 开始取出用户的活跃信息
			if (mActiveMap != null) {
                currentMonthConsumeMoney =  Double.parseDouble(mActiveMap.get("sum_consume"));
				accountInfoResultNew.setCurrentConsume(mActiveMap.get("sum_consume"));
				accountInfoResultNew.setActiveFriend(Integer.valueOf(mActiveMap
						.get("sum_member")));
			}

		} else {
			sLevelCode = GroupConst.DEFAULT_LEVEL_CODE;
			scaleReckon=GroupConst.DEFAULT_SCALE_RECKON;
		}
        accountInfoResultNew.setScaleReckon(scaleReckon);
		MDataMap mLevelMap = WebTemp.upTempDataMap("gc_group_level", "",
				"level_code", sLevelCode);
		accountInfoResultNew.setLevelName(mLevelMap.get("level_name"));
		int iMembers=0;
		BigDecimal bConsume=BigDecimal.ZERO;
		//土豪级别时，特殊处理
		MDataMap topLevelMap=WebTemp.upTempDataMap("gc_group_level", "", "level_code","4497465200010004");
		if(sLevelCode.equals(GroupConst.TOP_LEVEL_CODE)){
			iMembers = Integer.valueOf(topLevelMap.get("upgrade_members"))
					- accountInfoResultNew.getActiveFriend();
			accountInfoResultNew.setNextLevelFriend(topLevelMap.get("upgrade_members"));
			bConsume = new BigDecimal(
					topLevelMap.get("upgrade_consume")).subtract(new BigDecimal(
					accountInfoResultNew.getCurrentConsume()));
			accountInfoResultNew.setNextLevelConsume(topLevelMap.get("upgrade_consume"));
		}
		else{
			iMembers = Integer.valueOf(mLevelMap.get("upgrade_members"))
					- accountInfoResultNew.getActiveFriend();
			accountInfoResultNew.setNextLevelFriend(mLevelMap.get("upgrade_members"));
			bConsume = new BigDecimal(
					mLevelMap.get("upgrade_consume")).subtract(new BigDecimal(
					accountInfoResultNew.getCurrentConsume()));
			accountInfoResultNew.setNextLevelConsume(mLevelMap.get("upgrade_consume"));
		}
		
		if (iMembers < 0) {
			iMembers = 0;
		}
        accountInfoResultNew.setNextLevelGapFriend(iMembers);
		if (bConsume.compareTo(BigDecimal.ZERO) < 0) {
			bConsume = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		
//		accountInfoResultNew.setNextLevelGapConsume(bConsume.toString());
		BigDecimal fullPercent=new BigDecimal("100");
		BigDecimal consumePercent=new BigDecimal(accountInfoResultNew.getCurrentConsume()).divide(new BigDecimal(accountInfoResultNew.getNextLevelConsume()), 2, BigDecimal.ROUND_HALF_UP).multiply(fullPercent);
		BigDecimal friendPercent=new BigDecimal(accountInfoResultNew.getActiveFriend()).divide(new BigDecimal(accountInfoResultNew.getNextLevelFriend()), 2, BigDecimal.ROUND_HALF_UP).multiply(fullPercent);
		
		if(consumePercent.compareTo(fullPercent)==1){
			consumePercent=fullPercent;
		}
		if(friendPercent.compareTo(fullPercent)==1){
			friendPercent=fullPercent;
		}
		accountInfoResultNew.setNextLevelConsumePercent(consumePercent.toString());
		accountInfoResultNew.setNextLevelFriendPercent(friendPercent.toString());
		// -------------------- 本月升级还需消费--------start-----------

            //本月升级还需消费
            BigDecimal gapConsume=BigDecimal.ZERO;

        //土豪级别时，特殊处理
            BigDecimal.valueOf(currentMonthConsumeMoney);
        if(sLevelCode.equals(GroupConst.TOP_LEVEL_CODE)){
            gapConsume=new BigDecimal(topLevelMap.get("upgrade_consume")).subtract(BigDecimal.valueOf(currentMonthConsumeMoney));
        }
        else{
            gapConsume = new BigDecimal(
                    mLevelMap.get("upgrade_consume")).subtract(BigDecimal.valueOf(currentMonthConsumeMoney));
        }

        if (gapConsume.compareTo(BigDecimal.ZERO) < 0) {
            gapConsume = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        MDataMap mNextLevel = WebTemp.upTempDataMap("gc_group_level", "",
                "level_code", mLevelMap.get("next_level"));
        if (mNextLevel != null) {
            accountInfoResultNew.setNextLevelName(mNextLevel.get("level_name"));


            accountInfoResultNew.setUpdateRemark(bInfo(918501201,
                    mNextLevel.get("level_name"), String.valueOf(iMembers),
                    bConsume.toPlainString()));
        }
        accountInfoResultNew.setNextLevelConsume(String.valueOf(gapConsume));
        // -------------------- 本月升级还需消费--------end-----------
		//未读消息数
		accountInfoResultNew.setUnreadCount(DbUp.upTable("sc_comment_push_single").count("is_read","4497465200180001","user_code",this.getUserCode(),"app_code","SI2011"));
		
		//获取最新登陆时间
		String lastLoginTimeSql = "select last_time from mc_login_info where  member_code= '"+this.getUserCode()+"' and manage_code='"+ this.getManageCode()+"'";
		 Map<String, Object> lMap = DbUp.upTable("mc_login_info").dataSqlOne(lastLoginTimeSql,null);

		//String lastLoginTime = DbUp.upTable("mc_login_info").oneWhere("account_code", "", "", "member_code", memberCode)
				//.get("last_time");
		//DbUp.upTable("mc_login_info").oneWhere("", "", "", "member_code", memberCode).get("last_time");


		//获取member_code，方便取得登录数据
		String member_code = this.getUserCode();

		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("member_code",member_code);
		List<Map<String, Object>> loginLog = DbUp.upTable("mc_login_log").dataSqlList(" SELECT * FROM mc_login_log mcLog " +
				" WHERE 1=1 " +
				" AND mcLog.member_code=:member_code " +
				" ORDER BY mcLog.login_time DESC " +
				" LIMIT 0,2 ",mWhereMap);

		//因为最新的一条数据是本次登录的。而此处要获取上一次登录的数据
		if(loginLog!=null && loginLog.size()==2){
				accountInfoResultNew.setLastLoginTime(String.valueOf(loginLog.get(1).get("login_time")));
		 }
		
		
	}
	
}
