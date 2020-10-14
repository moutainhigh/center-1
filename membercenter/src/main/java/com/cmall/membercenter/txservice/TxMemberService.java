package com.cmall.membercenter.txservice;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.membercenter.McAccountInfoMapper;
import com.cmall.dborm.txmapper.membercenter.McLoginInfoMapper;
import com.cmall.dborm.txmapper.membercenter.McMemberInfoMapper;
import com.cmall.dborm.txmodel.membercenter.McAccountInfo;
import com.cmall.dborm.txmodel.membercenter.McLoginInfo;
import com.cmall.dborm.txmodel.membercenter.McLoginInfoExample;
import com.cmall.dborm.txmodel.membercenter.McMemberInfo;
import com.cmall.dborm.txmodel.membercenter.McMemberInfoExample;
import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.MemberResult;
import com.cmall.membercenter.model.UserLoginInput;
import com.cmall.membercenter.model.UserLoginResult;
import com.cmall.membercenter.model.UserRegInput;
import com.cmall.membercenter.model.UserRegResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MOauthInfo;
import com.srnpr.zapweb.websupport.OauthSupport;

public class TxMemberService extends BaseClass {

	/**
	 * 用户注册
	 * 
	 * @param userRegInput
	 * @return
	 */
	public UserRegResult insertUserReg(UserRegInput userRegInput,
			String sManageCode) {

		UserRegResult userRegResult = new UserRegResult();
		/*
		String sRegexMobile = "regex=^[1]\\d{10}";

		// 判断各种情况下的用户名规则
		if (userRegResult.upFlagTrue()) {

			// 限定APP注册时只能用手机号注册
			if (userRegInput.getClient_source().equals("app")) {

				if (!RegexHelper.checkRegexField(userRegInput.getLogin_name(),
						sRegexMobile)) {
					userRegResult.inErrorMessage(934105103);
				}

			}

		}

		// 判断用户名是否重复
		if (userRegResult.upFlagTrue()) {

			if (DbUp.upTable("mc_login_info").count("login_name",
					userRegInput.getLogin_name(), "manage_code", sManageCode) > 0) {
				userRegResult.inErrorMessage(934105104);
			}

		}

		McAccountInfo mcAccountInfo = new McAccountInfo();
		McMemberInfo mcMemberInfo = new McMemberInfo();
		McLoginInfo mcLoginInfo = new McLoginInfo();

		// 初始化一些基本信息
		if (userRegResult.upFlagTrue()) {

			mcMemberInfo
					.setMemberGroup(bConfig("membercenter.member_default_group"));
			mcMemberInfo
					.setMemberLevel(bConfig("membercenter.member_default_level"));

			mcMemberInfo
					.setMemberSex(bConfig("membercenter.member_default_sex"));

			// 如果是app注册 则将用户名设置为手机号
			if (userRegInput.getClient_source().equals("app")) {
				if (RegexHelper.checkRegexField(userRegInput.getLogin_name(),
						sRegexMobile)) {
					mcMemberInfo.setMobilePhone(userRegInput.getLogin_name());
				}
			}

		}

		// 开始插入数据库
		if (userRegResult.upFlagTrue()) {
			McMemberInfoMapper mcMemberInfoMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_McMemberInfoMapper");

			McLoginInfoMapper mcLoginInfoMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_McLoginInfoMapper");

			McAccountInfoMapper mcAccountInfoMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_McAccountInfoMapper");

			// 账户信息表 
			mcAccountInfo.setUid(WebHelper.upUuid());

			mcAccountInfo.setAccountCode(WebHelper.upCode("AI"));
			mcAccountInfoMapper.insertSelective(mcAccountInfo);

			// 用户信息表 
			mcMemberInfo.setAccountCode(mcAccountInfo.getAccountCode());
			mcMemberInfo.setCreateTime(FormatHelper.upDateTime());
			mcMemberInfo.setFlagEnable(1);
			mcMemberInfo.setManageCode(sManageCode);
			mcMemberInfo.setMemberCode(WebHelper.upCode("MI"));
			mcMemberInfo.setUid(WebHelper.upUuid());
			mcMemberInfo.setNickname(userRegInput.getNickname());

			mcMemberInfoMapper.insertSelective(mcMemberInfo);

			// 登陆信息表 
			mcLoginInfo.setMemberCode(mcMemberInfo.getMemberCode());
			mcLoginInfo.setManageCode(mcMemberInfo.getManageCode());
			mcLoginInfo.setUid(WebHelper.upUuid());
			mcLoginInfo.setCreateTime(FormatHelper.upDateTime());
			mcLoginInfo.setLoginName(userRegInput.getLogin_name());
			mcLoginInfo.setLoginPass(SecrurityHelper.MD5Secruity(userRegInput
					.getPassword()));
			mcLoginInfo.setLoginCode(mcLoginInfo.getManageCode()
					+ WebConst.CONST_SPLIT_DOWN + mcLoginInfo.getLoginName());
			mcLoginInfoMapper.insertSelective(mcLoginInfo);
		}

		// 开始返回用户的登录信息
		if (userRegResult.upFlagTrue()) {

			String sAuthCode = new MemberLoginSupport().memberLogin(
					mcMemberInfo.getMemberCode(), mcMemberInfo.getManageCode());
			
			userRegResult.setMember_code(mcMemberInfo.getMemberCode());
			
			if (StringUtils.isNotEmpty(sAuthCode)) {
				userRegResult.setUser_token(sAuthCode);
			} else {

				userRegResult.inErrorMessage(934105102);
			}

		}
		
		*/

		return userRegResult;
	}

}
