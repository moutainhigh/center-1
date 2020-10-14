package com.cmall.membercenter.txservice;


import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.membercenter.McAccountInfo;
import com.cmall.dborm.txmodel.membercenter.McLoginInfo;
import com.cmall.dborm.txmodel.membercenter.McMemberInfo;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.GsonHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;

public class TxMemberBase extends BaseClass {

	/**
	 * 用户注册操作 同一种注册类型不允许重复
	 * 
	 * @return
	 */
	public MReginsterResult doUserReginster(MLoginInput input) {
		MReginsterResult result = new MReginsterResult();

		McAccountInfo mcAccountInfo = new McAccountInfo();
		McMemberInfo mcMemberInfo = new McMemberInfo();
		McLoginInfo mcLoginInfo = new McLoginInfo();

		// 定义账户编号
		String sAccountCode = "";
		// 定义登陆密码
		String sPassword = "";

		// 判断用户名是否重复
		if (result.upFlagTrue()) {

			if (DbUp.upTable("mc_login_info").count("login_name",
					input.getLoginName(), "manage_code", input.getManageCode()) > 0) {
				result.inErrorMessage(934105104);
			}

		}

		// 用户注册时判断是否有组信息 如果有组信息 则将账号写成一致
		if (result.upFlagTrue()) {

			MDataMap mGroupMap = DbUp.upTable("mc_login_info").one(
					"login_name", input.getLoginName(), "login_group",
					input.getLoginGroup());

			if (mGroupMap != null) {
				MDataMap mMemberMap = DbUp.upTable("mc_member_info").one(
						"member_code", mGroupMap.get("member_code"));

				sAccountCode = mMemberMap.get("account_code");
				sPassword = mGroupMap.get("login_pass");
				
				
			}
				

		}

		// 开始插入数据库
		if (result.upFlagTrue()) {

			/* 账户信息表 */

			// 判断如果存在账户 则设置为账户编号
			if (StringUtils.isEmpty(sAccountCode)) {

				mcAccountInfo.setAccountCode(WebHelper.upCode("AI"));
				insertAccountInfo(mcAccountInfo);
			} else {
				mcAccountInfo.setAccountCode(sAccountCode);
			}

			/* 用户信息表 */
			mcMemberInfo.setAccountCode(mcAccountInfo.getAccountCode());

			mcMemberInfo.setManageCode(input.getManageCode());
			mcMemberInfo.setMemberCode(WebHelper.upCode("MI"));
			// mcMemberInfo.setNickname(userRegInput.getNickname());

			insertMemberInfo(mcMemberInfo);

			if(input.getManageCode().equals("SI2003")){
				/* 用户扩展表*/
				DbUp.upTable("mc_extend_info_star").dataInsert(new MDataMap("nickname",input.getLoginName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")));
			}
			
			/* 登陆信息表 */
			mcLoginInfo.setMemberCode(mcMemberInfo.getMemberCode());
			mcLoginInfo.setManageCode(mcMemberInfo.getManageCode());

			mcLoginInfo.setLoginName(input.getLoginName());

			// 如果有组信息的密码 则设置为组信息的密码
			if (StringUtils.isNotEmpty(sPassword)) {
				mcLoginInfo.setLoginPass(sPassword);
			}
			// 否则设置为注册信息传入的密码
			else if (StringUtils.isNotEmpty(input.getLoginPassword())) {
				mcLoginInfo.setLoginPass(SecrurityHelper.MD5Secruity(input
						.getLoginPassword()));
			}

			mcLoginInfo.setLoginCode(mcLoginInfo.getManageCode()
					+ WebConst.CONST_SPLIT_DOWN + mcLoginInfo.getLoginName());
			mcLoginInfo.setLoginGroup(input.getLoginGroup());

			insertLoginInfo(mcLoginInfo);

		}

		if (result.upFlagTrue()) {
			result.setMemberInfo(mcMemberInfo);
		}

		return result;
	}

	/**
	 * 添加账户信息
	 * 
	 * @param mcAccountInfo
	 */
	public void insertAccountInfo(McAccountInfo mcAccountInfo) {
		/*McAccountInfoMapper mcAccountInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McAccountInfoMapper");*/

		mcAccountInfo.setUid(WebHelper.upUuid());
		
		MDataMap mMemberMap= new MDataMap();
		//mMemberMap.put("zid", String.valueOf(mcAccountInfo.getZid()));
		mMemberMap.put("uid", String.valueOf(mcAccountInfo.getUid()));
		mMemberMap.put("account_code", String.valueOf(mcAccountInfo.getAccountCode()));
		if(mcAccountInfo.getAccountType()!=null){
			mMemberMap.put("account_type", String.valueOf(mcAccountInfo.getAccountType()));
		}
		DbUp.upTable("mc_account_info").dataInsert(mMemberMap);
		
		// 记录日志
		DbUp.upTable("lc_membercenter_log").dataInsert(new MDataMap("member_code",String.valueOf(String.valueOf(mcAccountInfo.getAccountCode())),"table_name","mc_account_info","optype","I","content",GsonHelper.toJson(mMemberMap),"stack",WebHelper.getThreadStackTrace(),"create_time",FormatHelper.upDateTime()));
	}

	/**
	 * 添加用户信息
	 * 
	 * @param mcMemberInfo
	 */
	public void insertMemberInfo(McMemberInfo mcMemberInfo) {
		
		mcMemberInfo.setCreateTime(FormatHelper.upDateTime());
		mcMemberInfo.setFlagEnable(1);
		mcMemberInfo.setUid(WebHelper.upUuid());
		
		/*McMemberInfoMapper mcMemberInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McMemberInfoMapper");
		mcMemberInfoMapper.insertSelective(mcMemberInfo);*/
		
		
		
		
		
		MDataMap mMemberMap= new MDataMap();
		//mMemberMap.put("zid", String.valueOf(mcAccountInfo.getZid()));
		mMemberMap.put("uid", String.valueOf(mcMemberInfo.getUid()));
		if(mcMemberInfo.getMemberCode()!=null){
			mMemberMap.put("member_code", String.valueOf(mcMemberInfo.getMemberCode()));
		}
		if(mcMemberInfo.getAccountCode()!=null){
			mMemberMap.put("account_code", String.valueOf(mcMemberInfo.getAccountCode()));
		}
		if(mcMemberInfo.getManageCode()!=null){
			mMemberMap.put("manage_code", String.valueOf(mcMemberInfo.getManageCode()));
		}
		if(mcMemberInfo.getFlagEnable()!=null){
			mMemberMap.put("flag_enable", String.valueOf(mcMemberInfo.getFlagEnable()));
		}
		if(mcMemberInfo.getCreateTime()!=null){
			mMemberMap.put("create_time", String.valueOf(mcMemberInfo.getCreateTime()));
		}
		
		DbUp.upTable("mc_member_info").dataInsert(mMemberMap);
		
		// 记录日志
		DbUp.upTable("lc_membercenter_log").dataInsert(new MDataMap("member_code",String.valueOf(mcMemberInfo.getMemberCode()),"table_name","mc_member_info","optype","I","content",GsonHelper.toJson(mMemberMap),"stack",WebHelper.getThreadStackTrace(),"create_time",FormatHelper.upDateTime()));
	}

	/**
	 * 添加登陆信息
	 * 
	 * @param mcLoginInfo
	 */
	public void insertLoginInfo(McLoginInfo mcLoginInfo) {
		/*McLoginInfoMapper mcLoginInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McLoginInfoMapper");

		mcLoginInfo.setUid(WebHelper.upUuid());
		mcLoginInfo.setCreateTime(FormatHelper.upDateTime());
		
		mcLoginInfoMapper.insertSelective(mcLoginInfo);*/
		
		MDataMap mMemberMap= new MDataMap();
		mMemberMap.put("uid", WebHelper.upUuid());
		mMemberMap.put("create_time",FormatHelper.upDateTime() );
		
		if(mcLoginInfo.getMemberCode()!=null){
			mMemberMap.put("member_code", String.valueOf(mcLoginInfo.getMemberCode()));
		}
		if(mcLoginInfo.getManageCode()!=null){
			mMemberMap.put("manage_code", String.valueOf(mcLoginInfo.getManageCode()));
		}
		if(mcLoginInfo.getLoginName()!=null){
			mMemberMap.put("login_name", String.valueOf(mcLoginInfo.getLoginName()));
		}
		if(mcLoginInfo.getLoginPass()!=null){
			mMemberMap.put("login_pass", String.valueOf(mcLoginInfo.getLoginPass()));
		}
		if(mcLoginInfo.getLoginCode()!=null){
			mMemberMap.put("login_code", String.valueOf(mcLoginInfo.getLoginCode()));
		}
		if(mcLoginInfo.getLoginGroup()!=null){
			mMemberMap.put("login_group", String.valueOf(mcLoginInfo.getLoginGroup()));
		}
		DbUp.upTable("mc_login_info").dataInsert(mMemberMap);
		
		// 记录日志
		DbUp.upTable("lc_membercenter_log").dataInsert(new MDataMap("member_code",String.valueOf(mcLoginInfo.getMemberCode()),"table_name","mc_login_info","optype","I","content",GsonHelper.toJson(mMemberMap),"stack",WebHelper.getThreadStackTrace(),"create_time",FormatHelper.upDateTime()));
	}

}
