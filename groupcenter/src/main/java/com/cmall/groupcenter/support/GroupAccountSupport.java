package com.cmall.groupcenter.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.cmall.groupcenter.groupapp.model.RongYunSingleChatBean;
import com.cmall.groupcenter.groupapp.service.RongYunService;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.service.GroupCommonService;
import com.cmall.groupcenter.service.GroupService;
import com.cmall.groupcenter.txservice.TxGroupAccountService;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 微公社账户支持类
 * 
 * @author srnpr
 * 
 */
public class GroupAccountSupport extends BaseClass {

	/**
	 * 创建微公社账户
	 * 
	 * @param sAccountCode
	 * @param sManageCode
	 * @return
	 */
	public MWebResult createGroupAccount(String sAccountCode, String sManageCode) {

		TxGroupAccountService txGroupAccountService = BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

		return txGroupAccountService.doCreateAccount(sAccountCode, sManageCode);

	}

	/**
	 * 检查并自动创建微公社账户
	 * 
	 * @param sAccountCodes
	 * @return
	 */
	public MWebResult checkAndCreateGroupAccount(String... sAccountCodes) {
		MWebResult mWebResult = new MWebResult();

		// -------------------- 判断用户基本信息表中是否存在账户信息
		if (mWebResult.upFlagTrue()) {
			List<MDataMap> listAccount = DbUp.upTable("mc_account_info")
					.queryIn("account_code", "", "", new MDataMap(), 0, 0,
							"account_code", FormatHelper.join(sAccountCodes));

			// 如果这两个账号不存在 则报错
			if (!(listAccount != null && listAccount.size() == sAccountCodes.length)) {
				mWebResult.inErrorMessage(918505170,
						FormatHelper.join(sAccountCodes));
			}

		}

		// -------------------- 判断微公社账户表是否存在账户信息
		if (mWebResult.upFlagTrue()) {
			MDataMap mExistDataMap = new MDataMap();
			for (MDataMap mChildMap : DbUp.upTable("gc_group_account")
					.queryIn(
							"account_code",
							"",
							"",
							new MDataMap(),
							0,
							0,
							"account_code",
							StringUtils.join(sAccountCodes,
									WebConst.CONST_SPLIT_COMMA))) {
				mExistDataMap.put(mChildMap.get("account_code"),
						mChildMap.get("account_code"));

			}

			// 如果已有的微公社账户信息和传入的参数的微公社账户信息不等
			if (mExistDataMap.size() != sAccountCodes.length) {
				TxGroupAccountService txGroupAccountService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

				for (String sKey : sAccountCodes) {
					// 如果不在已有账户信息中
					if (!mExistDataMap.containsKey(sKey)) {
						if (mWebResult.upFlagTrue()) {
							mWebResult.inOtherResult(txGroupAccountService
									.doCreateAccount(sKey, ""));
						}
					}

				}

			}
		}

		return mWebResult;

	}

	/**
	 * 获取用户的关联关系表 该方法返回的是账户的所有上级链
	 * 
	 * @param sAccountCode
	 *            账户编号
	 * @param sMaxCreateTime
	 *            最小创建时间 如果传入为空则不判断 如果非空则返回在这时间之前的上级关系链
	 * @return
	 */
	public List<AccountRelation> upAccountRelations(String sAccountCode,
			String sMaxCreateTime) {

		List<AccountRelation> listResult = new ArrayList<AccountRelation>();

		// 先将自己初始化加入关联关系列表中
		AccountRelation accountRelation = new AccountRelation();
		accountRelation.setAccountCode(sAccountCode);
		accountRelation.setDeep(0);
		listResult.add(accountRelation);

		String sWhere = "";
		MDataMap mWhereMap = new MDataMap();

		// 判断如果传入的最大开始时间不为空 则添加查询条件 取该时间之前的关联关系
		if (StringUtils.isNotEmpty(sMaxCreateTime)) {
			sWhere = "create_time<=:max_create_time";
			mWhereMap.put("max_create_time", sMaxCreateTime);
		}

		// 开始循环所有会员关联关系 开始按照这个关系队列清分
		for (int i = 0; i < GroupConst.RECKON_LEVEL_MAX; i++) {

			List<String> listParentStrings = new ArrayList<String>();

			// 循环将父级加入数组中
			for (AccountRelation item : listResult) {
				if (item.getDeep() == i) {
					listParentStrings.add(item.getAccountCode());
				}
			}

			// 如果有父级 才开始循环取出所有父级
			if (listParentStrings.size() > 0) {
				for (MDataMap mChildMap : DbUp.upTable("gc_member_relation")
						.queryInSafe(
								"account_code,parent_code,flag_enable",
								"",
								sWhere,
								mWhereMap,
								0,
								0,
								"account_code",
								StringUtils.join(listParentStrings,
										WebConst.CONST_SPLIT_COMMA))) {

					// 如果关联关系可用
					if (mChildMap.get("flag_enable").equals("1")) {
						AccountRelation levelAccountRelation = new AccountRelation();
						levelAccountRelation.setAccountCode(mChildMap
								.get("parent_code"));
						levelAccountRelation.setDeep(i + 1);
						listResult.add(levelAccountRelation);
					}

				}

			}

		}

		//

		return listResult;

	}

	/**
	 * 获取用户的所有下线及关系
	 * 
	 * @param sAccountCode
	 * @return
	 */
	public List<AccountRelation> upAccountFlower(String sAccountCode) {

		List<AccountRelation> listResult = new ArrayList<AccountRelation>();

		// 先将自己初始化加入关联关系列表中
		AccountRelation accountRelation = new AccountRelation();
		accountRelation.setAccountCode(sAccountCode);
		accountRelation.setDeep(0);
		listResult.add(accountRelation);

		// 开始循环所有会员关联关系 开始按照这个关系队列清分
		for (int i = 0; i < GroupConst.RECKON_LEVEL_MAX; i++) {

			List<String> listParentStrings = new ArrayList<String>();

			// 循环将父级加入数组中
			for (AccountRelation item : listResult) {
				if (item.getDeep() == i) {
					listParentStrings.add(item.getAccountCode());
				}
			}

			// 如果有父级 才开始循环取出所有父级
			if (listParentStrings.size() > 0) {
				for (MDataMap mChildMap : DbUp.upTable("gc_member_relation")
						.queryInSafe(
								"account_code,parent_code,flag_enable",
								"",
								"",
								new MDataMap(),
								0,
								0,
								"parent_code",
								StringUtils.join(listParentStrings,
										WebConst.CONST_SPLIT_COMMA))) {

					// 如果关联关系可用
					if (mChildMap.get("flag_enable").equals("1")) {
						AccountRelation levelAccountRelation = new AccountRelation();
						levelAccountRelation.setAccountCode(mChildMap
								.get("account_code"));
						levelAccountRelation.setDeep(i + 1);
						listResult.add(levelAccountRelation);
					}

				}

			}

		}

		//

		return listResult;

	}

	/**
	 * 创建账户的上下级关系
	 * 
	 * @param sAccountCode
	 * @param sParentCode
	 * @return
	 */
	public MWebResult createRelation(String sAccountCode, String sParentCode,
			String sRemark, String sCreateTime) {

		MWebResult mWebResult = new MWebResult();
//-------------------------start  -------------------------
		//判断账户是否是商户，是商户就不能绑定上下级关系 fengl
		if(mWebResult.upFlagTrue()){
			mWebResult.inOtherResult(checkAccountIsTrader(sAccountCode,
					sParentCode));
		}
//-------------------------end-------------------------		
		// 判断账户基本信息是否存在
		if (mWebResult.upFlagTrue()) {

			mWebResult.inOtherResult(checkAndCreateGroupAccount(sAccountCode,
					sParentCode));

		}

		// 判断不能将自己添加为自己的上级
		if (mWebResult.upFlagTrue()) {

			if (sAccountCode.equals(sParentCode)) {
				mWebResult.inErrorMessage(918505171, sAccountCode);
			}

		}

		// 判断一个账户只能有一个上级
		if (mWebResult.upFlagTrue()) {
			if (upAccountRelations(sAccountCode, "").size() != 1) {
				mWebResult.inErrorMessage(918505172, sAccountCode);
			}
		}

		if (mWebResult.upFlagTrue()) {

			// 循环所有上级的关系链
			for (AccountRelation accountRelation : upAccountRelations(
					sParentCode, "")) {

				// 如果上级的关系链中有账号
				if (accountRelation.getAccountCode().equals(sAccountCode)) {

					mWebResult.inErrorMessage(918505173, sAccountCode,
							sParentCode);

				}

			}

		}
		
		if(mWebResult.upFlagTrue()){
			//下线存在下线，不能再绑定上线
			if(upAccountFlower(sAccountCode).size()>1){
				mWebResult.inErrorMessage(918505174,sAccountCode);
			}
		}

		// 开始执行实际的插入过程
		if (mWebResult.upFlagTrue()) {

			DbUp.upTable("gc_member_relation").insert(
					"account_code",
					sAccountCode,
					"parent_code",
					sParentCode,
					"create_time",
					StringUtils.isEmpty(sCreateTime) ? FormatHelper
							.upDateTime() : sCreateTime, "remark", sRemark);

		}
		
		//推送消息
		if(mWebResult.upFlagTrue()){
			
			
			
				try {
					
	//-------------------------------start--------------------------------------
					RongYunService rongYunService=new RongYunService();
					
					RongYunSingleChatBean bean=new RongYunSingleChatBean();
					MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",sAccountCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
					MDataMap pMemberMap=DbUp.upTable("mc_member_info").one("account_code",sParentCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
					
					boolean messagePush=true;
					if(null!=memberMap&&null!=pMemberMap){
						   if(null!=memberMap.get("member_code")&&null!=pMemberMap.get("member_code")){
							bean.setFromUserId(memberMap.get("member_code"));
							bean.setToUserId(pMemberMap.get("member_code"));
							bean.setObjectName("RC:TxtMsg");
							bean.setContent("{\"content\":\"我们已经成为好友啦！\"}");//给上级发消息
							//给上级发消息(荣云)
							RootResultWeb rootResultWeb=rongYunService.singleChatMessageSend(bean);
							if(rootResultWeb.getResultCode()!=1){
								messagePush=true;
							}else{
								//给自己发消息
								RongYunSingleChatBean beanMe=new RongYunSingleChatBean();
								beanMe.setFromUserId(pMemberMap.get("member_code"));
								beanMe.setToUserId(memberMap.get("member_code"));
								beanMe.setObjectName("RC:TxtMsg");
								beanMe.setContent("{\"content\":\"我们已经成为好友啦！\"}");//给自己发消息
								rongYunService.singleChatMessageSend(beanMe);
								messagePush=false;
							}


							
						  }
					}
                    if(messagePush){
//                      推送到系统消息 ：我们已经成为好友啦！
						AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
						addSinglePushCommentInput.setAccountCode(sParentCode);
						addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
						addSinglePushCommentInput.setType("44974720000400010003");
						
						addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
						addSinglePushCommentInput.setProperties("systemMessageType=3&dateTime="+System.currentTimeMillis());
						addSinglePushCommentInput.setTitle("新好友加入");
//						addSinglePushCommentInput.setTitle("我们已经成为好友啦！");
						MDataMap memberMapMessage=DbUp.upTable("mc_member_info").one("account_code",sParentCode,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
						if(memberMapMessage!=null){
							addSinglePushCommentInput.setUserCode(memberMapMessage.get("member_code"));
						}
						GroupCommonService groupCommonService=new GroupCommonService();
						String content="【"+groupCommonService.getMobileByAccountCode(sAccountCode)+"】已经成为您的好友啦";
//					    String content="【"+groupCommonService.getMobileByAccountCode(sAccountCode)+"】已成功接受邀请成为你的微公社一度好友";
//						String content="我们已经成为好友啦！";
					    addSinglePushCommentInput.setContent(content);
					    String relationCode=groupCommonService.getMemberCodeByAccountCode(sAccountCode, GroupConst.GROUP_APP_MANAGE_CODE);
					    if(StringUtils.isBlank(relationCode)){
					    	relationCode=groupCommonService.getMemberCodeByAccountCode(sAccountCode,"");
					    }
					    addSinglePushCommentInput.setRelationCode(relationCode);
						
						if(DbUp.upTable("gc_account_push_set").count("account_code",sParentCode,"push_type_id","5c1ef8fdd2fd4320a0139ead976fe0ac","push_type_onoff","449747100002")<1){
							addSinglePushCommentInput.setSendStatus("4497465000070001");
						}
						else{
							addSinglePushCommentInput.setSendStatus("4497465000070002");
						}
						
						SinglePushComment.addPushComment(addSinglePushCommentInput);
					}
					
//-------------------------------end--------------------------------------	
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}

		return mWebResult;

	}

	/**
	 * 自动转换所有的清分账户到可提现账户
	 * 
	 * @return
	 */
	public MWebResult aotoConvertAccount() {

		MWebResult mWebResult = new MWebResult();

		String sTimer = DateHelper
				.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);
		
		TxGroupAccountService txGroupAccountService = BeansHelper.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
		// 取出预定义规则的订单记录
		List<MDataMap> listMaps = DbUp
				.upTable("gc_reckon_log")
				.queryAll(
						"distinct order_code",
						"",
						"reckon_change_type='4497465200030001' and flag_withdraw=1 and reckon_money>0 and order_reckon_time!='' and order_reckon_time<'"
								+ sTimer + "'", new MDataMap());

		if (listMaps != null && listMaps.size() > 0) {
			//根据订单查询 此订单下的用户,分别进行事务处理
			for (MDataMap mDataMap : listMaps) {
				String orderCode = mDataMap.get("order_code");
				MDataMap sWhereMap = new MDataMap();
				sWhereMap.put("order_code", orderCode);
				List<MDataMap> listAcMaps = DbUp
						.upTable("gc_reckon_log")
						.queryAll(
								"distinct account_code",
								"",
								"order_code=:order_code and reckon_change_type='4497465200030001' and flag_withdraw=1 and reckon_money>0 and order_reckon_time!='' "
								+ "and order_reckon_time<'" + sTimer + "'", sWhereMap);
				if(listAcMaps != null && listAcMaps.size() > 0){
					for(MDataMap acMap : listAcMaps){
						String accountCode = acMap.get("account_code");
						txGroupAccountService.convertAccountForFourth(orderCode,accountCode);
					}
				}
			}
		}

		return mWebResult;

	}

	/**
	 * 自动降级
	 * 
	 * @return
	 */
	public MWebResult autoFallAccountLevel() {

		MWebResult mWebResult = new MWebResult();

		// 取出所有可降级的级别编号
		List<MDataMap> listFallLevels = DbUp.upTable("gc_group_level")
				.queryByWhere("flag_fall", "1");

		if (listFallLevels.size() > 0) {
			List<String> listLevels = new ArrayList<String>();

			for (MDataMap mDataMap : listFallLevels) {
				listLevels.add(mDataMap.get("level_code"));
			}

			// 获取本月的月初第一天
			String sLastMonth = DateHelper.upDate(new Date(),
					DateHelper.CONST_PARSE_MONTH_FIRST_DAY);

			// 取出所有当月没有检查过降级
			List<MDataMap> listMaps = DbUp.upTable("gc_group_account").queryIn(
					"account_code",
					"",
					" (fall_check_time='' or  fall_check_time<'" + sLastMonth
							+ "') ", new MDataMap(), 0, 0, "account_level",
					StringUtils.join(listLevels, WebConst.CONST_SPLIT_COMMA));

			if (listMaps != null && listMaps.size() > 0) {

				TxGroupAccountService txGroupAccountService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
				for (MDataMap mItemMap : listMaps) {

					txGroupAccountService.fallAccountLevel(mItemMap
							.get("account_code"));
				}

			}

		}

		return mWebResult;

	}
	
	/**
	 * 自动转换订单清分账户到可提现账户
	 * 
	 * @return
	 */
	public MWebResult autoConvertAccountByOrderCode(String orderCode) {

		MWebResult mWebResult = new MWebResult();

		String sTimer = DateHelper
				.upDateTimeAdd(GroupConst.RECKON_AUTO_CONVERT_DAY);

		MDataMap mQueryMap=new MDataMap();
		mQueryMap.put("order_code", orderCode);

		// 取出预定义规则的记录
		List<MDataMap> listMaps = DbUp
				.upTable("gc_reckon_log")
				.queryAll(
						"distinct account_code",
						"",
						"reckon_change_type=4497465200030001 and flag_withdraw=1 and reckon_money>0 and order_reckon_time!='' and order_reckon_time<'"
								+ sTimer + "' and order_code=:order_code ", mQueryMap);

		if (listMaps != null && listMaps.size() > 0) {
			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

			for (MDataMap mDataMap : listMaps) {
				txGroupAccountService.convertAccountForFourth(orderCode,mDataMap.get("account_code"));

			}

		}

		return mWebResult;

	}
	/**
	 * 判断账户是否是商户
	 * @param accountCode
	 * @param parentCode
	 * @return
	 */
	
	public static MWebResult checkAccountIsTrader(String accountCode,String parentCode){
		MWebResult mWebResult = new MWebResult();
		if (mWebResult.upFlagTrue()) {
			if(DbUp.upTable("gc_trader_info").count("account_code",accountCode)>0){
				mWebResult.inErrorMessage(918570012);
			}
		}
		if (mWebResult.upFlagTrue()) {
			if(DbUp.upTable("gc_trader_info").count("account_code",parentCode)>0){
				mWebResult.inErrorMessage(918570012);
			}
		}
		
		return mWebResult;
	}
	
	public static void main(String[] args) {
//		MWebResult mWebResult=checkAccountIsTrader("AI150630100116","AI140821100111");
//		System.out.println(mWebResult.getResultCode());
//		RongYunService rongYunService=new RongYunService();
//		RongYunSingleChatBean beanMe=new RongYunSingleChatBean();
//		beanMe.setFromUserId("MI151230100008");
//		beanMe.setToUserId("MI151230100007");
//		beanMe.setObjectName("RC:TxtMsg");
//		beanMe.setContent("{\"content\":\"我们已经成为好友啦111！11111111111 14:15\"}");//给上级发消息
//		rongYunService.singleChatMessageSend(beanMe);
//		
//
//		RongYunSingleChatBean beanMe1=new RongYunSingleChatBean();
//		beanMe1.setFromUserId("MI151230100007");
//		beanMe1.setToUserId("MI151230100008");
//		beanMe1.setObjectName("RC:TxtMsg");
//		beanMe1.setContent("{\"content\":\"我们已经成为好友啦2222222222222 14:15\"}");//给自己发消息
//		rongYunService.singleChatMessageSend(beanMe1);
	}

}
