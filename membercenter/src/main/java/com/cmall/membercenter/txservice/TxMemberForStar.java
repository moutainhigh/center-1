package com.cmall.membercenter.txservice;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.membercenter.McExtendInfoStarMapper;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoStar;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoStarExample;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.cmall.membercenter.model.UserRegInput;
import com.cmall.membercenter.model.UserRegResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class TxMemberForStar extends TxMemberBase {

	/**
	 * 用户注册
	 * 
	 * @param userRegInput
	 * @return
	 */
	public UserRegResult insertUserReg(UserRegInput userRegInput,
			String sManageCode) {

		UserRegResult userRegResult = new UserRegResult();

		String sRegexMobile = RegexConst.MOBILE_PHONE;

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

		MReginsterResult result = null;

		if (userRegResult.upFlagTrue()) {

			MLoginInput input = new MLoginInput();

			input.setLoginName(userRegInput.getLogin_name());
			input.setLoginPassword(userRegInput.getPassword());
			input.setManageCode(sManageCode);
			// 通行证账号
			input.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			result = doUserReginster(input);

			// 开始登陆信息
			if (result.upFlagTrue()) {

			} else {
				userRegResult.setResultCode(result.getResultCode());
				userRegResult.setResultMessage(result.getResultMessage());
			}

		}

		// 开始插入扩展信息表
		if (userRegResult.upFlagTrue()) {

			initExtendInfo(new MDataMap("login_name",
					userRegInput.getLogin_name(), "manage_code", sManageCode,
					"member_code", result.getMemberInfo().getMemberCode(),
					"nick_name", userRegInput.getNickname()));

			/*
			 * McExtendInfoStarMapper mcExtendInfoStarMapper = BeansHelper
			 * .upBean("bean_com_cmall_dborm_txmapper_McExtendInfoStarMapper");
			 * 
			 * McExtendInfoStar mcExtendInfoStar = new McExtendInfoStar();
			 * 
			 * mcExtendInfoStar.setMemberCode(result.getMemberInfo()
			 * .getMemberCode()); mcExtendInfoStar.setUid(WebHelper.upUuid());
			 * mcExtendInfoStar.setFlagPush(1);
			 * mcExtendInfoStar.setMemberGroup("4497465000020001");
			 * mcExtendInfoStar.setMemberLevel("4497465000030001");
			 * mcExtendInfoStar.setMemberSex("4497465100010001");
			 * mcExtendInfoStar.setAppCode(sManageCode);
			 * 
			 * // 如果是app注册 则将用户名设置为手机号 if
			 * (userRegInput.getClient_source().equals("app")) { if
			 * (RegexHelper.checkRegexField(userRegInput.getLogin_name(),
			 * sRegexMobile)) {
			 * 
			 * mcExtendInfoStar.setMobilePhone(userRegInput .getLogin_name()); }
			 * }
			 * 
			 * mcExtendInfoStar.setNickname(userRegInput.getNickname());
			 * 
			 * mcExtendInfoStar.setCreateTime(FormatHelper.upDateTime());
			 * 
			 * mcExtendInfoStarMapper.insertSelective(mcExtendInfoStar);
			 */
		}

		// 开始返回用户的登录信息
		if (userRegResult.upFlagTrue()) {
			String sAuthCode = new MemberLoginSupport().memberLogin(result
					.getMemberInfo().getMemberCode(), result.getMemberInfo()
					.getManageCode(), userRegInput.getLogin_name());

			userRegResult.getUser().setMember_code(
					result.getMemberInfo().getMemberCode());

			if (StringUtils.isNotEmpty(sAuthCode)) {
				userRegResult.setUser_token(sAuthCode);
			} else {

				userRegResult.inErrorMessage(934105102);
			}
		}

		return userRegResult;
	}

	public void initExtendInfo(MDataMap mDataMap) {

		// 如果不存在与扩展信息表中
		if (DbUp.upTable("mc_extend_info_star").count("member_code",
				mDataMap.get("member_code")) == 0) {

			McExtendInfoStarMapper mcExtendInfoStarMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoStarMapper");

			McExtendInfoStar mcExtendInfoStar = new McExtendInfoStar();

			mcExtendInfoStar.setMemberCode(mDataMap.get("member_code"));
			mcExtendInfoStar.setUid(WebHelper.upUuid());
			mcExtendInfoStar.setFlagPush(1);
			mcExtendInfoStar.setMemberGroup("4497465000020001");
			mcExtendInfoStar.setMemberLevel("4497465000030001");
			if("SI2007".equals(mDataMap.get("manage_code"))||"SI2013".equals(mDataMap.get("manage_code"))){
				mcExtendInfoStar.setMemberSex("4497465100010003");
			}else {
				mcExtendInfoStar.setMemberSex("4497465100010001");
			}
			
			mcExtendInfoStar.setAppCode(mDataMap.get("manage_code"));

			if (RegexHelper.checkRegexField(mDataMap.get("login_name"),
					RegexConst.MOBILE_PHONE)) {

				mcExtendInfoStar.setMobilePhone(mDataMap.get("login_name"));
			}

			if (mDataMap.containsKey("nick_name")) {
				mcExtendInfoStar.setNickname(mDataMap.get("nick_name"));
			}

			mcExtendInfoStar.setCreateTime(FormatHelper.upDateTime());

			mcExtendInfoStarMapper.insertSelective(mcExtendInfoStar);
		}
	}

	public MWebResult updateMemberInfo(McExtendInfoStar mcExtendInfoStar,
			String sMemberCode) {
		McExtendInfoStarMapper mcExtendInfoStarMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoStarMapper");

		McExtendInfoStarExample example = new McExtendInfoStarExample();

		example.createCriteria().andMemberCodeEqualTo(sMemberCode);

		mcExtendInfoStarMapper.updateByExampleSelective(mcExtendInfoStar,
				example);

		return new MWebResult();
	}

}
