package com.cmall.membercenter.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.cmall.membercenter.agent.AgentLoginInput;
import com.cmall.membercenter.agent.AgentLoginResult;
import com.cmall.membercenter.enumer.ELoginType;
import com.cmall.membercenter.group.model.GroupLoginInput;
import com.cmall.membercenter.group.model.GroupLoginResult;
import com.cmall.membercenter.group.model.HomePoolLoginInput;
import com.cmall.membercenter.group.model.HomePoolLoginResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.AgentMemberInfo;
import com.cmall.membercenter.model.AgentMemberResult;
import com.cmall.membercenter.model.GetMemberResult;
import com.cmall.membercenter.model.HXUserLoginInfo;
import com.cmall.membercenter.model.HXUserLoginInfoExtendInfo;
import com.cmall.membercenter.model.HXUserLoginService;
import com.cmall.membercenter.model.HomePoolGetMemberResult;
import com.cmall.membercenter.model.HomePoolMLoginResult;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MLoginResult;
import com.cmall.membercenter.model.MReginsterResult;
import com.cmall.membercenter.model.MemberResult;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.membercenter.model.UserLoginInput;
import com.cmall.membercenter.model.UserLoginResult;
import com.cmall.membercenter.oauth.model.CheckUserInfoInput;
import com.cmall.membercenter.oauth.model.CheckUserInfoResult;
import com.cmall.membercenter.txservice.TxMemberBase;
import com.cmall.membercenter.txservice.TxMemberForHomePool;
import com.cmall.membercenter.txservice.TxMemberForStar;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.cmall.systemcenter.service.StartPageService;
import com.cmall.systemcenter.support.VerifySupport;
import com.google.gson.JsonObject;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MOauthScope;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.OauthSupport;

public class MemberLoginSupport extends BaseClass {

	/**
	 * 获取用户信息和配置信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public MemberResult upMemberInfo(String sMemberCode) {
		MemberResult memberResult = new MemberResult();

		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();

		memberResult.setUser(memberInfoSupport.upMemberInfo(sMemberCode));
		memberResult.setConfig(memberInfoSupport.upMemberConfig(sMemberCode));

		// memberInfo.setLevel_name(level_name);

		return memberResult;

	}

	/**
	 * 获取代理商信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public AgentMemberResult upAgentMemberInfo(String sMemberCode) {

		AgentMemberResult memberResult = new AgentMemberResult();

		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();

		memberResult.setUser(memberInfoSupport.upAgentMemberInfo(sMemberCode));

		return memberResult;

	}

	/**
	 * 获取用户信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public GetMemberResult getMemberInfo(String sMemberCode) {

		GetMemberResult getMemberResult = new GetMemberResult();

		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();

		getMemberResult.setUser(memberInfoSupport.upMemberInfo(sMemberCode));

		// memberInfo.setLevel_name(level_name);

		return getMemberResult;

	}

	/**
	 * @Description:获取用户信息
	 * @param sMemberCode
	 *            用户code
	 * @return
	 * @author 张海生
	 * @return GetMemberResult 返回类型
	 * @date 2014-11-3 下午4:26:35
	 * @throws
	 */
	public HomePoolGetMemberResult HomePoolGetMemberInfo(String sMemberCode) {

		HomePoolGetMemberResult getMemberResult = new HomePoolGetMemberResult();

		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();

		getMemberResult.setUser(memberInfoSupport
				.HomePoolUpMemberInfo(sMemberCode));

		// memberInfo.setLevel_name(level_name);

		return getMemberResult;

	}

	/**
	 * 微公社用户登陆
	 * 
	 * @param groupLoginInput
	 * @param sManageCode
	 * @return
	 */
	public GroupLoginResult doGroupLogin(GroupLoginInput groupLoginInput,
			String sManageCode) {

		GroupLoginResult groupLoginResult = new GroupLoginResult();

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginName(groupLoginInput.getLoginName());
		mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
		mLoginInput.setLoginPassword(groupLoginInput.getLoginPass());
		mLoginInput.setManageCode(sManageCode);
		/*// 微公社短信登陆
		if (groupLoginInput.getSerialNumber() != null
				&& groupLoginInput.getSerialNumber().equals(
						ELoginType.verifyCodeLogin.name())) {
			mLoginInput.setLoginType(ELoginType.verifyCodeLogin);
			groupLoginInput.setSerialNumber("");
		}*/

		MLoginResult mLoginResult = doLogin(mLoginInput);

		groupLoginResult.inOtherResult(mLoginResult);
		groupLoginResult.setIsNoPassword(mLoginResult.getIsNoPassword());

		if (groupLoginResult.upFlagTrue()) {
			groupLoginResult.setUserToken(mLoginResult.getUserToken());
			groupLoginResult.setMemberCode(mLoginResult.getMemberCode());
			// 判断是否第一次登录，如果是第一次登录微公社，push消息
			if (mLoginResult.isFirstLogin() && "SI2011".equals(sManageCode)) {
				insertWeblcomeMsg(mLoginResult.getMemberCode(), sManageCode);
			}
		}

		// 绑定用户流水号
		if (groupLoginResult.upFlagTrue()) {
			new StartPageService().updateLsh(groupLoginInput.getSerialNumber(),
					mLoginResult.getMemberCode());
		}
		// 如果用户登陆成功 并且app中不存在用户信息 则插入扩展信息
		if (groupLoginResult.upFlagTrue()) {
			// 定义是否用户存在于此app中
			if (DbUp.upTable("mc_extend_info_star").count("member_code",
					mLoginResult.getMemberCode()) == 0) {
				TxMemberForStar memberService = BeansHelper
						.upBean("bean_com_cmall_membercenter_txservice_TxMemberForStar");
				memberService
						.initExtendInfo(new MDataMap("login_name",
								groupLoginInput.getLoginName(), "manage_code",
								sManageCode, "member_code", mLoginResult
										.getMemberCode(), "nick_name",
								StringUtils.substring(
										groupLoginInput.getLoginName(), 0, 3)
										+ "*****"
										+ StringUtils.substring(
												groupLoginInput.getLoginName(),
												8, 11)));

			}
		}
		if ("SI3003".equals(sManageCode)) {// 客服：沙皮狗
			String memberCode = mLoginResult.getMemberCode();
			if (!"".equals(memberCode)) {
				HXUserLoginService service = new HXUserLoginService();
				MDataMap mDataMap = service.loginInfo(memberCode);
				HXUserLoginInfo hxUserLoginInfo = new HXUserLoginInfo();
				hxUserLoginInfo.setHxUserName(mDataMap.get("hx_user_name"));
				hxUserLoginInfo.setHxPassWord(mDataMap.get("hx_pass_word"));
				hxUserLoginInfo.setHxWorkerId(mDataMap.get("hx_worker_id"));
				hxUserLoginInfo.setHxStatus(mDataMap.get("hx_status"));
				HXUserLoginInfoExtendInfo extendInfo = getExtendInfo(
						memberCode, sManageCode);
				hxUserLoginInfo.setExtendInfo(extendInfo);
				groupLoginResult.setHxUserLoginInfo(hxUserLoginInfo);
			}
		}
		return groupLoginResult;

	}

	public HXUserLoginInfoExtendInfo getExtendInfo(String memberCode,
			String appCode) {
		HXUserLoginInfoExtendInfo extendInfo = new HXUserLoginInfoExtendInfo();
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("memberCode", memberCode);
		mWhereMap.put("appCode", appCode);
		Map<String, Object> map = DbUp
				.upTable("mc_extend_info_star")
				.dataSqlOne(
						"select member_avatar, nickname from mc_extend_info_star where member_code=:memberCode and app_code=:appCode",
						mWhereMap);
		if (map != null && map.size() > 0) {
			extendInfo
					.setMember_avatar(String.valueOf(map.get("member_avatar")));
			extendInfo.setNickname(String.valueOf(map.get("nickname")));
		}
		return extendInfo;
	}

	/**
	 * @Description: 家有汇用户登录
	 * @param LoginInput
	 *            输入参数实体
	 * @param sManageCode
	 *            平台编号
	 * @param @return
	 * @return GroupLoginResult 返回类型
	 * @date 2014-10-31 下午2:09:00
	 * @throws
	 */
	public HomePoolLoginResult homePoolLogin(HomePoolLoginInput LoginInput,
			String sManageCode) {

		HomePoolLoginResult groupLoginResult = new HomePoolLoginResult();
		MLoginInput mLoginInput = new MLoginInput();
		// String loginName = LoginInput.getLoginName();
		String sSql = "select status from mc_extend_info_homepool"
				+ " where member_name=:loginName or mobile=:loginName"
				+ " or email=:loginName";
		List<Map<String, Object>> ldata = DbUp.upTable(
				"mc_extend_info_homepool").dataSqlList(sSql,
				new MDataMap("loginName", LoginInput.getLoginName()));// 查询用户是否被禁用
		if (ldata != null && ldata.size() > 0) {
			Map<String, Object> mdata = ldata.get(0);
			String memStatus = (String) mdata.get("status");
			if ("449746600002".equals(memStatus)) {
				groupLoginResult.inErrorMessage(934105115);// 用户被禁用
			}
		}
		// MDataMap dMap = DbUp.upTable("mc_login_info").oneWhere("member_code",
		// null, null, "login_name", loginName, "login_pass", "",
		// "manage_code", MemberConst.MANAGE_CODE_HPOOL);
		// if (dMap != null) {
		// List<MDataMap> mapList = DbUp
		// .upTable("mc_extend_info_homepool")
		// .queryAll(
		// "zid,mobile,email",
		// null,
		// "old_code<>'' and (mobile<>'' or email<>'') and member_code=:memberCode",
		// new MDataMap("memberCode", dMap.get("member_code")));
		// if (mapList != null && mapList.size() > 0) {
		// groupLoginResult.setMobile(mapList.get(0).get("mobile"));
		// groupLoginResult.setEmail(mapList.get(0).get("email"));
		// groupLoginResult.inErrorMessage(934105116);// 是家有汇老用户，需要重置密码
		// }
		// }
		if (groupLoginResult.upFlagTrue()) {
			HomePoolMLoginResult mLoginResult = new HomePoolMLoginResult();
			if (groupLoginResult.upFlagTrue()) {
				mLoginInput.setLoginName(LoginInput.getLoginName());
				mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
				mLoginInput.setLoginPassword(LoginInput.getLoginPass());
				mLoginInput.setManageCode(sManageCode);
				mLoginResult = HomePooldoLogin(mLoginInput);// 用户登录
				groupLoginResult.inOtherResult(mLoginResult);// 设置登录返回信息
			}
			if (groupLoginResult.upFlagTrue()) {// 若登录成功设置返回的token信息
				MDataMap tmap = DbUp.upTable("mc_extend_info_homepool")
						.oneWhere("member_code,old_code", null, null,
								"member_code", mLoginResult.getMemberCode());
				groupLoginResult.setMemberCode(tmap.get("member_code"));
				groupLoginResult.setOldCode(tmap.get("old_code"));
				groupLoginResult.setUserToken(mLoginResult.getUserToken());
				groupLoginResult.setMemberName(mLoginResult.getMemberName());
				groupLoginResult.setNickname(mLoginResult.getNickname());
			}
		}
		return groupLoginResult;

	}

	/**
	 * 约她用户登陆 该类型登陆会自动创建用户信息
	 * 
	 * @param groupLoginInput
	 * @param sManageCode
	 * @return
	 */
	public GroupLoginResult doTrystItLogin(GroupLoginInput groupLoginInput,
			String sManageCode) {

		// 判断如果登陆信息表中没有数据 则自动创建一条登陆信息
		if (DbUp.upTable("mc_login_info").count("login_name",
				groupLoginInput.getLoginName(), "manage_code", sManageCode) == 0) {

			TxMemberBase txMemberBase = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");
			MLoginInput mLoginInput = new MLoginInput();
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
			mLoginInput.setLoginName(groupLoginInput.getLoginName());
			mLoginInput.setManageCode(sManageCode);
			txMemberBase.doUserReginster(mLoginInput);

		}

		// 开始执行用户登陆
		GroupLoginResult groupLoginResult = new GroupLoginResult();

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginName(groupLoginInput.getLoginName());
		mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

		mLoginInput.setLoginPassword(groupLoginInput.getLoginPass());
		mLoginInput.setManageCode(sManageCode);
		// 设置登陆类型为短信验证码登陆
		mLoginInput.setLoginType(ELoginType.Message);
		MLoginResult mLoginResult = doLogin(mLoginInput);

		groupLoginResult.inOtherResult(mLoginResult);

		if (groupLoginResult.upFlagTrue()) {
			groupLoginResult.setUserToken(mLoginResult.getUserToken());
		}

		return groupLoginResult;
	}

	/**
	 * 明星类app登陆
	 * 
	 * @param userLoginInput
	 * @param sManageCode
	 * @return
	 */
	public UserLoginResult doStarLogin(UserLoginInput userLoginInput,
			String sManageCode) {

		UserLoginResult userLoginResult = new UserLoginResult();

		ScoredChange scordChange = new ScoredChange();

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginName(userLoginInput.getLogin_name());
		mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
		mLoginInput.setLoginPassword(userLoginInput.getPassword());
		mLoginInput.setManageCode(sManageCode);

		// 判断登陆类型
		if (userLoginInput.getVerify_type().equals("msg_code")) {
			mLoginInput.setLoginType(ELoginType.Message);
		}

		MLoginResult mLoginResult = doLogin(mLoginInput);

		// 将登陆结果添加到返回结果中
		userLoginResult.inOtherResult(mLoginResult);

		// 如果用户登陆成功 并且app中不存在用户信息 则插入扩展信息
		if (userLoginResult.upFlagTrue()) {

			// 定义是否用户存在于此app中
			if (DbUp.upTable("mc_extend_info_star").count("member_code",
					mLoginResult.getMemberCode()) == 0) {
				TxMemberForStar memberService = BeansHelper
						.upBean("bean_com_cmall_membercenter_txservice_TxMemberForStar");
				memberService
						.initExtendInfo(new MDataMap("login_name",
								userLoginInput.getLogin_name(), "manage_code",
								sManageCode, "member_code", mLoginResult
										.getMemberCode(), "nick_name",
								StringUtils.substring(
										userLoginInput.getLogin_name(), 0, 3)
										+ "*****"
										+ StringUtils.substring(
												userLoginInput.getLogin_name(),
												8, 11)));

			}

		}

		// 设置相关信息
		if (userLoginResult.upFlagTrue()) {

			MemberResult memberResult = upMemberInfo(mLoginResult
					.getMemberCode());

			/* 变动积分 */
			scordChange = new ScoredSupport().FirstLandingScored(memberResult
					.getUser().getMember_code());

			userLoginResult.setUser(memberResult.getUser());

			userLoginResult.setConfig(memberResult.getConfig());

			userLoginResult.setUser_token(mLoginResult.getUserToken());
			/* 返回积分 */
			userLoginResult.setsChange(scordChange);

		}

		return userLoginResult;
	}

	/**
	 * 代理商登录
	 * 
	 * @param userLoginInput
	 * @param sManageCode
	 * @return
	 */
	public AgentLoginResult agentStarLogin(AgentLoginInput userLoginInput,
			String manage_code) {

		AgentLoginResult userLoginResult = new AgentLoginResult();

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginName(userLoginInput.getLoginName());
		mLoginInput.setLoginPassword(userLoginInput.getLoginPass());
		mLoginInput.setManageCode(manage_code);

		MLoginResult mLoginResult = doAgentLogin(mLoginInput);

		// 将登陆结果添加到返回结果中
		userLoginResult.inOtherResult(mLoginResult);

		// 设置相关信息
		if (userLoginResult.upFlagTrue()) {

			AgentMemberResult memberResult = upAgentMemberInfo(mLoginResult
					.getMemberCode());

			userLoginResult.setUser(memberResult.getUser());

			userLoginResult.setUser_token(mLoginResult.getUserToken());

		}

		return userLoginResult;
	}

	/**
	 * 第三方登录
	 * 
	 * @param checkUserInfo
	 * @param manageCode
	 * @return
	 */
	public CheckUserInfoResult doUserLoginByThirdParty(
			CheckUserInfoInput checkUserInfo, String manageCode) {

		CheckUserInfoResult checkUserInfoResult = new CheckUserInfoResult();

		// 判断如果登陆信息表中没有数据 则自动创建一条登陆信息
		if (DbUp.upTable("mc_login_info").count("login_name",
				checkUserInfo.getLoginName(), "manage_code", manageCode) == 0) {

			MLoginInput mLoginInput = new MLoginInput();

			mLoginInput.setLoginName(checkUserInfo.getLoginName());
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
			mLoginInput.setLoginPassword(checkUserInfo.getLoginPass());
			mLoginInput.setManageCode(manageCode);

			new TxMemberBase().doUserReginster(mLoginInput);
			
			//注册送券 by zht
			JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnDistributeCoupon,
					CouponConst.register_coupon,new MDataMap("mobile", checkUserInfo.getLoginName(),"manage_code", manageCode));
		}

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginName(checkUserInfo.getLoginName());
		mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
		mLoginInput.setLoginPassword(checkUserInfo.getLoginPass());
		mLoginInput.setManageCode(manageCode);
		// 免密码登陆
		mLoginInput.setLoginType(ELoginType.LoginName);
		// 设置外部插入登录信息
		mLoginInput.setIsInsideLogin(0);

		MLoginResult mLoginResult = doLogin(mLoginInput);

		checkUserInfoResult.inOtherResult(mLoginResult);

		if (checkUserInfoResult.upFlagTrue()) {
			// 如果有membercode 否则返回
			if (StringUtils.isNotBlank(mLoginResult.getMemberCode())) {

				OauthSupport oauthSupport = new OauthSupport();

				// 设置授权类型
				MOauthScope mOauthScope = new MOauthScope();

				mOauthScope.setManageCode(manageCode);
				mOauthScope.setScopeType("oauth");

				checkUserInfoResult.setAccessToken(oauthSupport.insertOauth(
						mLoginResult.getMemberCode(), manageCode,
						checkUserInfo.getLoginName(),
						MemberConst.OAUTH_EXPIRESS_TIME,
						oauthSupport.scopeToJson(mOauthScope)));

			} else {
				checkUserInfoResult.inErrorMessage(969905923);
			}
		}
		return checkUserInfoResult;
	}

	/**
	 * 根据组信息创建用户
	 * 
	 * @param mGroupMap
	 * @param sManageCode
	 * @return
	 */
	private MReginsterResult doCreateUserByGroup(MDataMap mGroupMap,
			String sManageCode) {

		TxMemberBase txMemberBase = BeansHelper
				.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");

		MLoginInput mLoginInput = new MLoginInput();

		mLoginInput.setLoginGroup(mGroupMap.get("login_group"));
		mLoginInput.setLoginName(mGroupMap.get("login_name"));

		mLoginInput.setManageCode(sManageCode);

		return txMemberBase.doUserReginster(mLoginInput);
	}

	/**
	 * 代理商登陆
	 * 
	 * @param mLoginInput
	 * @param sManageCode
	 * @return
	 */
	private MLoginResult doAgentLogin(MLoginInput mLoginInput) {

		MLoginResult mLoginResult = new MLoginResult();
		int lFaield = 0;

		MDataMap mLoginMap = DbUp
				.upTable("nc_agency")
				.oneWhere(
						"",
						"",
						"agent_mobilephone=:agent_mobilephone or agent_wechat=:agent_wechat",
						"agent_mobilephone", mLoginInput.getLoginName(),
						"agent_wechat", mLoginInput.getLoginName());

		if (mLoginResult.upFlagTrue()) {
			// 判断是用户是否存在 如果用户不存在 则自动判断
			if (mLoginMap == null) {

				mLoginResult.inErrorMessage(934105101);
				
				return mLoginResult;
			}
			List<MDataMap> list = DbUp.upTable("nc_agency").queryByWhere(
					"member_code", mLoginMap.get("member_code"));

			// 把状态组装
			List<Object> listValue = new ArrayList<Object>();
			for (MDataMap dmap : list) {
				listValue.add(dmap.get("agent_stauts"));
			}
			// 判断用户是否被冻结
						if (!listValue.contains("4497464900070003")) {
							mLoginResult.inErrorMessage(934105106);
							return mLoginResult;
						}
			
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date dt1 = df.parse(FormatHelper.upDateTime());
				int num = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("auth_end_time") == null
							|| list.get(i).get("auth_end_time").isEmpty()) {

						num++;
					} else {
						Date dt2 = df.parse(list.get(i).get("auth_end_time"));
						if (dt1.getTime() > dt2.getTime()) {
							num++;
						}
					}

				}
				if (list.size() == num) {

					mLoginResult.inErrorMessage(934105147);
					return mLoginResult;
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		// 强制再次校验是否有登陆信息 防止插入没有成功
		if (mLoginResult.upFlagTrue()) {
			if (mLoginMap == null) {
				mLoginResult.inErrorMessage(934105101);
			}
		}

		if (mLoginResult.upFlagTrue()) {

			lFaield = Integer.parseInt(mLoginMap.get("failed_count"));

			// 判断最大失败次数
			if (lFaield >= WebConst.CONST_USER_FAIL_TIME) {

				// 开始判断最大失败次数
				try {

					Date dFailDate = DateUtils.addMinutes(FormatHelper
							.parseDate(mLoginMap.get("failed_time")),
							WebConst.CONST_USER_FAIL_LOCK_MINUTE);

					if (dFailDate.after(new Date())) {

						// 判断当前时间差
						int iDiffMinute = (int) (Math
								.ceil((dFailDate.getTime() - (new Date())
										.getTime())) / (1000 * 60));

						mLoginResult.inErrorMessage(934105105, String
								.valueOf((iDiffMinute > 0 ? iDiffMinute : 1)));

						// userLoginResult.inErrorMessage(934105102);

					} else {
						lFaield = 0;
					}

				} catch (Exception e) {
					e.printStackTrace();
					mLoginResult.inErrorMessage(934105105,
							WebConst.CONST_USER_FAIL_LOCK_MINUTE);
				}
			}

			if (mLoginResult.upFlagTrue()) {
				switch (mLoginInput.getLoginType()) {
				case Password:
					// 判断如果密码不对则
					if (!SecrurityHelper.MD5Secruity(
							mLoginInput.getLoginPassword()).equalsIgnoreCase(
							mLoginMap.get("agent_password").trim())) {
						// 如果密码不对时 增加冻结次数
						mLoginMap.put("failed_count",
								String.valueOf(lFaield + 1));

						mLoginMap.put("failed_time", FormatHelper.upDateTime());

						DbUp.upTable("nc_agency").dataUpdate(mLoginMap,
								"failed_count,failed_time", "uid");
						mLoginResult.inErrorMessage(934105102);
						return mLoginResult;
						// 如果密码为空，设置有无密码状态
					}
					break;
				case verifyCodeLogin:
					VerifySupport verifySupport = new VerifySupport();
					// 验证验证码是否正确
					mLoginResult.inOtherResult(verifySupport
							.checkVerifyCodeByType(
									EVerifyCodeTypeEnumer.verifyCodeLogin,
									mLoginInput.getLoginName(),
									mLoginInput.getLoginPassword()));
					break;
				case Message:
					// 特殊情况，随便输入验证码就能登录
					if (!"13811206150"
							.equals(mLoginInput.getLoginName().trim())) {
						VerifySupport verifySupportT = new VerifySupport();
						// 验证验证码是否正确
						mLoginResult.inOtherResult(verifySupportT
								.checkVerifyCodeByType(
										EVerifyCodeTypeEnumer.MemberLogin,
										mLoginInput.getLoginName(),
										mLoginInput.getLoginPassword()));
					}
					break;
				case LoginName:
					// 免密码，直接break;
					break;
				default:
					mLoginResult.inErrorMessage(934105108);
					break;
				}

			}
		}

		// 判断如果登陆成功 并且曾经有登陆失败信息 则清空历史登陆失败信息
		if (mLoginResult.upFlagTrue()) {

			// 登录成功增加登录日志----start------
			String logUid = WebHelper.upUuid();

			MDataMap loginLog = new MDataMap();
			loginLog.put("uid", logUid);
			loginLog.put("login_time", FormatHelper.upDateTime());
			loginLog.put("member_code", mLoginMap.get("member_code"));
			loginLog.put("manage_code", mLoginInput.getManageCode());

			DbUp.upTable("mc_login_log").dataInsert(loginLog);
			// 登录成功增加登录日志----end------

			// 登录成功后修改最后登录时间
			if (lFaield > 0) {
				mLoginMap.put("last_time", FormatHelper.upDateTime());
				mLoginMap.put("failed_count", "0");
				mLoginMap.put("failed_time", "");

				DbUp.upTable("nc_agency").dataUpdate(mLoginMap,
						"failed_count,failed_time,last_time", "uid");
			}

		}

		// 开始执行登陆操作 返回auth认证编码
		if (mLoginResult.upFlagTrue() && mLoginInput.isInsideLogin()) {

			String sAuthCode = memberLogin(mLoginMap.get("member_code"),
					mLoginInput.getManageCode(),
					mLoginMap.get("agent_mobilephone"));

			if (StringUtils.isNotEmpty(sAuthCode)) {
				mLoginResult.setUserToken(sAuthCode);

				mLoginResult.setMemberCode(mLoginMap.get("member_code"));

			} else {

				mLoginResult.inErrorMessage(934105102);
			}

		}
		if (mLoginResult.upFlagTrue() && mLoginResult.getMemberCode() == "") {
			mLoginResult.setMemberCode(mLoginMap.get("member_code"));
		}

		return mLoginResult;

	}

	public  MLoginResult doLogin(MLoginInput mLoginInput) {

		MLoginResult mLoginResult = new MLoginResult();
		int lFaield = 0;

		MDataMap mLoginMap = DbUp.upTable("mc_login_info").one("login_name",
				mLoginInput.getLoginName(), "manage_code",
				mLoginInput.getManageCode());

		if (mLoginResult.upFlagTrue()) {
			// 判断是用户是否存在 如果用户不存在 则自动判断
			if (mLoginMap == null) {

				MDataMap mGroupMap = DbUp.upTable("mc_login_info").one(
						"login_name", mLoginInput.getLoginName(),
						"login_group", mLoginInput.getLoginGroup());

				// 如果没有同组登陆信息 则该用户不存在
				if (mGroupMap == null) {
					mLoginResult.inErrorMessage(934105101);
				}
				// 否则初始化登陆信息
				else {
					mLoginResult.setFirstLogin(true);
					doCreateUserByGroup(mGroupMap, mLoginInput.getManageCode());

					mLoginMap = DbUp.upTable("mc_login_info").one("login_name",
							mLoginInput.getLoginName(), "manage_code",
							mLoginInput.getManageCode());

				}

			}
			// 判断用户是否被冻结
			else if (!StringUtils.equals(mLoginMap.get("flag_enable"), "1")) {
				mLoginResult.inErrorMessage(934105106);
			}
		}

		// 强制再次校验是否有登陆信息 防止插入没有成功
		if (mLoginResult.upFlagTrue()) {
			if (mLoginMap == null) {
				mLoginResult.inErrorMessage(934105101);
			}
		}

		if (mLoginResult.upFlagTrue()) {

			lFaield = Integer.parseInt(mLoginMap.get("failed_count"));

			// 判断最大失败次数
			if (lFaield >= WebConst.CONST_USER_FAIL_TIME) {

				// 开始判断最大失败次数
				try {

					Date dFailDate = DateUtils.addMinutes(FormatHelper
							.parseDate(mLoginMap.get("failed_time")),
							WebConst.CONST_USER_FAIL_LOCK_MINUTE);

					if (dFailDate.after(new Date())) {

						// 判断当前时间差
						int iDiffMinute = (int) (Math
								.ceil((dFailDate.getTime() - (new Date())
										.getTime())) / (1000 * 60));

						mLoginResult.inErrorMessage(934105105, String
								.valueOf((iDiffMinute > 0 ? iDiffMinute : 1)));

						// userLoginResult.inErrorMessage(934105102);

					} else {
						lFaield = 0;
					}

				} catch (Exception e) {
					e.printStackTrace();
					mLoginResult.inErrorMessage(934105105,
							WebConst.CONST_USER_FAIL_LOCK_MINUTE);
				}
			}

			if (mLoginResult.upFlagTrue()) {
				switch (mLoginInput.getLoginType()) {
				case Password:
					// 判断如果密码不对则
					if (!SecrurityHelper.MD5Secruity(
							mLoginInput.getLoginPassword()).equalsIgnoreCase(
							mLoginMap.get("login_pass").trim())) {
						// 如果密码不对时 增加冻结次数
						mLoginMap.put("failed_count",
								String.valueOf(lFaield + 1));

						mLoginMap.put("failed_time", FormatHelper.upDateTime());

						DbUp.upTable("mc_login_info").dataUpdate(mLoginMap,
								"failed_count,failed_time", "uid");
						mLoginResult.inErrorMessage(934105102);
						// 如果密码为空，设置有无密码状态
						mLoginResult
								.setIsNoPassword(StringUtils.isBlank(mLoginMap
										.get("login_pass").trim()) ? "1" : "0");
					}
					break;
				case verifyCodeLogin:
					/*VerifySupport verifySupport = new VerifySupport();
					// 验证验证码是否正确
					mLoginResult.inOtherResult(verifySupport
							.checkVerifyCodeByType(
									EVerifyCodeTypeEnumer.verifyCodeLogin,
									mLoginInput.getLoginName(),
									mLoginInput.getLoginPassword()));*/
					break;
				case Message:
					// 特殊情况，随便输入验证码就能登录
					if (!"13811206150"
							.equals(mLoginInput.getLoginName().trim())) {
						VerifySupport verifySupportT = new VerifySupport();
						// 验证验证码是否正确
						mLoginResult.inOtherResult(verifySupportT
								.checkVerifyCodeByType(
										EVerifyCodeTypeEnumer.MemberLogin,
										mLoginInput.getLoginName(),
										mLoginInput.getLoginPassword()));
					}
					break;
				case LoginName:
					// 免密码，直接break;
					break;
				default:
					mLoginResult.inErrorMessage(934105108);
					break;
				}

			}
		}

		// 判断如果登陆成功 并且曾经有登陆失败信息 则清空历史登陆失败信息
		if (mLoginResult.upFlagTrue()) {

			// 登录成功增加登录日志----start------
			String logUid = WebHelper.upUuid();

			MDataMap loginLog = new MDataMap();
			loginLog.put("uid", logUid);
			loginLog.put("login_time", FormatHelper.upDateTime());
			loginLog.put("member_code", mLoginMap.get("member_code"));
			loginLog.put("manage_code", mLoginInput.getManageCode());

			DbUp.upTable("mc_login_log").dataInsert(loginLog);
			// 登录成功增加登录日志----end------

			// 登录成功后修改最后登录时间
			if (lFaield > 0) {
				mLoginMap.put("last_time", FormatHelper.upDateTime());
				mLoginMap.put("failed_count", "0");
				mLoginMap.put("failed_time", "");

				DbUp.upTable("mc_login_info").dataUpdate(mLoginMap,
						"failed_count,failed_time,last_time", "uid");
			}

		}

		// 开始执行登陆操作 返回auth认证编码
		if (mLoginResult.upFlagTrue() && mLoginInput.isInsideLogin()) {

			String sAuthCode = memberLogin(mLoginMap.get("member_code"),
					mLoginInput.getManageCode(), mLoginMap.get("login_name"));

			if (StringUtils.isNotEmpty(sAuthCode)) {
				mLoginResult.setUserToken(sAuthCode);

				mLoginResult.setMemberCode(mLoginMap.get("member_code"));

			} else {

				mLoginResult.inErrorMessage(934105102);
			}

		}
		if (mLoginResult.upFlagTrue() && mLoginResult.getMemberCode() == "") {
			mLoginResult.setMemberCode(mLoginMap.get("member_code"));
		}

		return mLoginResult;

	}

	/**
	 * @Description:
	 * @param mLoginInput
	 *            输入参数
	 * @return
	 * @return HomePoolMLoginResult 返回类型
	 * @date 2014-10-31 下午4:27:30
	 * @throws
	 */
	private HomePoolMLoginResult HomePooldoLogin(MLoginInput mLoginInput) {
		HomePoolMLoginResult homeLoginResult = new HomePoolMLoginResult();
		MLoginResult mLoginResult = doLogin(mLoginInput);
		homeLoginResult.setResultCode(mLoginResult.getResultCode());
		homeLoginResult.setResultMessage(mLoginResult.getResultMessage());
		if (mLoginResult.upFlagTrue()) {// 登录成功

			// 定义是否用户存在于此app中
			if (DbUp.upTable("mc_extend_info_homepool").count("member_code",
					mLoginResult.getMemberCode()) == 0) {
				TxMemberForHomePool memberService = BeansHelper
						.upBean("bean_com_cmall_membercenter_txservice_TxMemberForHomePool");
				String mobile = (new MemberLoginSupport())
						.getMoblie(mLoginResult.getMemberCode());
				memberService.saveUserExtendInfor(mLoginResult.getMemberCode(),
						mLoginInput.getLoginName(), mobile, mobile, "");
				homeLoginResult.setNickname(mobile);
			} else {
				MDataMap mGroupMap = DbUp.upTable("mc_extend_info_homepool")
						.one("member_code", mLoginResult.getMemberCode());
				homeLoginResult.setNickname(mGroupMap.get("nickname"));
			}
			homeLoginResult.setUserToken(mLoginResult.getUserToken());
			homeLoginResult.setMemberCode(mLoginResult.getMemberCode());
			homeLoginResult.setMemberName(mLoginInput.getLoginName());
		}
		return homeLoginResult;
	}

	/**
	 * 用户登陆信息写入token表
	 * 
	 * @param sMemberCode
	 * @param sManageCode
	 * @return
	 */
	public String memberLogin(String sMemberCode, String sManageCode,
			String sLoginName) {
		OauthSupport oauthSupport = new OauthSupport();
		return oauthSupport.insertOauth(sMemberCode, sManageCode, sLoginName,
				"9000d", "");
	}

	/**
	 * 删除所有用的登陆token信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public MWebResult deleteAllTokenByMemberCode(String sMemberCode) {
		MDataMap mUpDataMap = new MDataMap();
		mUpDataMap.inAllValues("user_code", sMemberCode, "flag_enable", "0");
		DbUp.upTable("za_oauth").dataUpdate(mUpDataMap, "flag_enable",
				"user_code");

		return new MWebResult();
	}

	/**
	 * 用户注销
	 * 
	 * @param sAccessToken
	 * @return
	 */
	public MWebResult memberLogout(String sAccessToken, String serialNumber) {
		MDataMap mUpDataMap = new MDataMap();
		mUpDataMap
				.inAllValues("access_token", sAccessToken, "flag_enable", "0");
		DbUp.upTable("za_oauth").dataUpdate(mUpDataMap, "flag_enable",
				"access_token");

		// 解除流水号的绑定
		if (!StringUtils.isEmpty(serialNumber))
			new StartPageService().updateLsh(serialNumber, null);
		return new MWebResult();
	}

	public MWebResult checkOrCreateUserByMobile(String sMobile,
			String sManageCode) {

		MWebResult mResult = new MWebResult();

		if (DbUp.upTable("mc_login_info").count("login_name", sMobile) == 0) {

			MLoginInput mLoginInput = new MLoginInput();
			mLoginInput.setLoginName(sMobile);
			// mLoginInput.setLoginPassword(userRegInput.getPassword());
			mLoginInput.setManageCode(sManageCode);
			// 通行证账号
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			TxMemberBase txMemberBase = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");

			mResult.inOtherResult(txMemberBase.doUserReginster(mLoginInput));

		}

		return mResult;

	}
	
	/**
	 * 微公社用户验证，没有则创建
	 * @param sMobile
	 * @param sManageCode
	 * @return
	 */
	public MWebResult checkOrCreateUserByWGS(String sMobile,
			String sManageCode) {

		MWebResult mResult = new MWebResult();

		if (DbUp.upTable("mc_login_info").count("login_name", sMobile,"manage_code",sManageCode) == 0) {

			MLoginInput mLoginInput = new MLoginInput();
			mLoginInput.setLoginName(sMobile);
			// mLoginInput.setLoginPassword(userRegInput.getPassword());
			mLoginInput.setManageCode(sManageCode);
			// 通行证账号
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			TxMemberBase txMemberBase = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");

			mResult.inOtherResult(txMemberBase.doUserReginster(mLoginInput));

		}

		return mResult;

	}

	/**
	 * 为了不影响其他页面的逻辑，此处重载上述方法，添加一个密码的保存
	 * 
	 * @author lipengfei
	 * @date 2015-6-30
	 * @param sMobile
	 * @param password
	 * @param sManageCode
	 * @return
	 */
	public MWebResult checkOrCreateUserByMobile(String sMobile,
			String password, String sManageCode) {

		MWebResult mResult = new MWebResult();

		if (DbUp.upTable("mc_login_info").count("login_name", sMobile) == 0) {

			MLoginInput mLoginInput = new MLoginInput();
			mLoginInput.setLoginName(sMobile);

			if (StringUtils.isNotEmpty(password)) {
				mLoginInput.setLoginPassword(password);
			}

			mLoginInput.setManageCode(sManageCode);
			// 通行证账号
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			TxMemberBase txMemberBase = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");

			mResult.inOtherResult(txMemberBase.doUserReginster(mLoginInput));

		} else {
			mResult.setResultCode(0);
			mResult.setResultMessage("登录名已存在");
		}

		return mResult;

	}

	/**
	 * 获取登录用户的手机号
	 * 
	 */
	public String getMoblie(String memberCode) {
		String mobile = "";
		try {
			List<MDataMap> list = new ArrayList<MDataMap>();
			list = DbUp.upTable("mc_extend_info_homepool").queryByWhere(
					"member_code", memberCode);
			for (int i = 0; i < list.size(); i++) {
				if (RegexHelper.checkRegexField(list.get(i).get("mobile"),
						"base=mobile")) {
					mobile = list.get(i).get("mobile");
					break;
				}
			}
			if ("".equals(mobile)) {
				list.clear();
				list = DbUp.upTable("mc_login_info").queryByWhere(
						"member_code", memberCode);
				for (int j = 0; j < list.size(); j++) {
					if (RegexHelper.checkRegexField(
							list.get(j).get("login_name"), "base=mobile")) {
						mobile = list.get(j).get("login_name");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mobile;
	}

	/**
	 * 查看登录名
	 * 
	 * @param member_code
	 * @return
	 */
	public String getMemberLoginName(String member_code) {
		String mobile = "";
		if (StringUtils.isBlank(member_code)) {
			return mobile;
		}
		MDataMap dataMap = DbUp.upTable("mc_login_info").one("member_code",
				member_code);
		if (dataMap != null) {
			mobile = dataMap.get("login_name");
		}
		return mobile;
	}

	public void insertWeblcomeMsg(String memberCode, String manageCode) {
		AddSinglePushCommentInput addSinglePushCommentInput = new AddSinglePushCommentInput();
		MDataMap memberInfo = DbUp.upTable("mc_member_info").oneWhere(
				"account_code", "", "", "member_code", memberCode);
		if (memberInfo != null) {
			addSinglePushCommentInput.setAccountCode(memberInfo
					.get("account_code"));
		}
		addSinglePushCommentInput.setAppCode(manageCode);
		addSinglePushCommentInput.setType("44974720000400010001");

		addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
		addSinglePushCommentInput.setProperties("systemMessageType=1&dateTime="
				+ System.currentTimeMillis());
		addSinglePushCommentInput.setTitle("欢迎加入微公社");
		addSinglePushCommentInput.setUserCode(memberCode);

		addSinglePushCommentInput.setContent("欢迎您加入微公社，可以直接跟好友聊天啦~");

		addSinglePushCommentInput.setSendStatus("4497465000070001");

		SinglePushComment.addPushComment(addSinglePushCommentInput);
	}
}
