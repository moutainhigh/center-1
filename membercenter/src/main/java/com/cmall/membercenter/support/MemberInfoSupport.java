package com.cmall.membercenter.support;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.AgentMemberInfo;
import com.cmall.membercenter.model.ChangePasswordForWxInput;
import com.cmall.membercenter.model.ChangePasswordInput;
import com.cmall.membercenter.model.CheckUserResult;
import com.cmall.membercenter.model.HomePoolMemberInfo;
import com.cmall.membercenter.model.MemCodeResult;
import com.cmall.membercenter.model.MemberChangeInput;
import com.cmall.membercenter.model.MemberConfig;
import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.ValiOriginalPasswordInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebTemp;

public class MemberInfoSupport extends BaseClass {

	/**
	 * 获取用户基本信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public MemberInfo upMemberInfo(String sMemberCode) {

		MemberInfo memberInfo = new MemberInfo();

		MDataMap mMemberMap = DbUp.upTable("mc_extend_info_star").one(
				"member_code", sMemberCode);
		if (mMemberMap != null) {
			memberInfo.setCreate_time(mMemberMap.get("create_time"));
			try {
				String memberSex = StringUtils.isEmpty(mMemberMap.get("member_sex")) ? "0" : mMemberMap.get("member_sex");
				memberInfo.setGender(BigInteger.valueOf(Long.valueOf(memberSex)));
			} catch(Exception e) {
				memberInfo.setGender(BigInteger.valueOf(0L));
			}
			
			try {
				String memberGroup = StringUtils.isEmpty(mMemberMap.get("member_group")) ? "0" : mMemberMap.get("member_group");
				memberInfo.setGroup(BigInteger.valueOf(Long.valueOf(memberGroup)));
			} catch(Exception e) {
				memberInfo.setGroup(BigInteger.valueOf(0L));
			}
			
			if(StringUtils.isNotEmpty(mMemberMap.get("member_level"))){
				try {
					String memberLevel = StringUtils.isEmpty(mMemberMap.get("member_level")) ? "0" : mMemberMap.get("member_level");
					if(memberLevel.length() > 4) {
						//搞清业务规则 > N???
						memberLevel = memberLevel.substring(memberLevel.length() - 4, memberLevel.length());
						memberInfo.setLevel(Integer.valueOf(memberLevel));
						memberInfo
						.setLevel_name(WebTemp.upTempDataOne("mc_member_level",
								"level_name", "level_code",
								mMemberMap.get("member_level")));
					}
				} catch(Exception e) {
					memberInfo.setLevel(0);
				}
			}
			else{
				memberInfo.setLevel(Integer.valueOf("0001"));
				memberInfo.setLevel_name(WebTemp.upTempDataOne("mc_member_level",
						"level_name", "level_code","4497465000030001"));
			}
			
			
			if(StringUtils.isNotEmpty(mMemberMap.get("vip_level"))){
				//会员合并  look here
//				memberInfo.setVip_level(Integer.valueOf(mMemberMap.get("vip_level")
//						.substring(mMemberMap.get("vip_level").length() - 4,
//								mMemberMap.get("vip_level").length())));

				//会员合并
				memberInfo
						.setVip_level_name(WebTemp.upTempDataOne("sc_define",
						"define_name", "define_code",
						mMemberMap.get("vip_level")));
			}
			else{
				memberInfo.setVip_level(Integer.valueOf("0001"));
				memberInfo.setVip_level_name(WebTemp.upTempDataOne("sc_define",
						"define_name", "define_code","4497469400060001"));
			}
			

			memberInfo.setMember_code(mMemberMap.get("member_code"));
			memberInfo.setNickname(mMemberMap.get("nickname"));
			memberInfo.setEmail(mMemberMap.get("email"));
			memberInfo.setBirthday(mMemberMap.get("birthday"));
			try {
				String memberScore = StringUtils.isEmpty(mMemberMap.get("member_score")) ? "0" : mMemberMap.get("member_score");
				memberInfo.setScore(Integer.valueOf(memberScore));
			} catch(Exception e) {
				memberInfo.setScore(0);
			}
			//会员合并
			try {
				String memberPoints = StringUtils.isEmpty(mMemberMap.get("points")) ? "0" : mMemberMap.get("points");
				memberInfo.setPoints(Integer.valueOf(memberPoints));
			} catch(Exception e) {
				memberInfo.setPoints(0);
			}

			memberInfo.setMobile(mMemberMap.get("mobile_phone"));

			memberInfo
					.setScore_unit(bConfig("membercenter.member_default_scopeunit"));

			memberInfo.getAvatar().setLarge(mMemberMap.get("member_avatar"));

			memberInfo.getAvatar().setThumb(mMemberMap.get("member_avatar"));
		}

		return memberInfo;
	}
	
	/**
	 * 获取代理商基本信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public AgentMemberInfo upAgentMemberInfo(String sMemberCode) {

		AgentMemberInfo memberInfo = new AgentMemberInfo();

		MDataMap mMemberMap = DbUp.upTable("nc_agency").one(
				"member_code", sMemberCode);
		
		if (mMemberMap != null) {
			memberInfo.setAgent_level(mMemberMap.get("level_number"));
			memberInfo.setAgent_name(mMemberMap.get("agent_name"));
			memberInfo.setAgent_wechat(mMemberMap.get("agent_wechat"));
            memberInfo.setAgent_password(mMemberMap.get("agent_mobilephone"));
			memberInfo.setMember_code(mMemberMap.get("member_code"));
			memberInfo.setIsnot_proving(mMemberMap.get("isnot_proving"));
			
			MDataMap lemMap = new MDataMap(); 
			
			lemMap.put("agency_level", mMemberMap.get("agent_level"));
			
			Map<String, Object> levelMap = DbUp
					.upTable("nc_agency_level")
					.dataSqlOne(
							"select agency_level from nc_agency_level  where parent_id=''and agency_level=:agency_level",
							lemMap);
			if(levelMap != null && !levelMap.isEmpty()){
				
				memberInfo.setHighest_level("1");	
				
			}
			
		}

		return memberInfo;

	}

	/**
	 * @Description: 获取家有汇用户基本信息
	 * @param sMemberCode
	 *            用户code
	 * @return
	 * @author 张海生
	 * @return HomePoolMemberInfo 返回类型
	 * @date 2014-11-3 下午4:31:23
	 * @throws
	 */
	public HomePoolMemberInfo HomePoolUpMemberInfo(String sMemberCode) {

		HomePoolMemberInfo memberInfo = new HomePoolMemberInfo();

		MDataMap mMemberMap = DbUp.upTable("mc_extend_info_homepool").one(
				"member_code", sMemberCode);
		if (mMemberMap != null) {
			try {
				String gender = StringUtils.isEmpty(mMemberMap.get("gender")) ? "0" : mMemberMap.get("gender");
				memberInfo.setGender(BigInteger.valueOf(Long.valueOf(gender)));
			} catch(Exception e) {
				memberInfo.setGender(BigInteger.valueOf(0L));
			}
			memberInfo.setMember_code(mMemberMap.get("member_code"));
			memberInfo.setNickname(mMemberMap.get("nickname"));
			memberInfo.setMobile(mMemberMap.get("mobile"));
			memberInfo.setHeadPic(mMemberMap.get("head_pic"));
			memberInfo.setMemberName(mMemberMap.get("member_name"));
			memberInfo.setBirthday(mMemberMap.get("birthday"));
			memberInfo.setEmail(mMemberMap.get("email"));
			memberInfo.setOldCode(mMemberMap.get("old_code"));
			memberInfo.setMemberSign(mMemberMap.get("member_sign"));
			memberInfo.setRealName(mMemberMap.get("realName"));
			memberInfo.setEmaiStatus(mMemberMap.get("emai_status"));
			memberInfo.setMobileStatus(mMemberMap.get("mobile_status"));
			memberInfo.setVipType(mMemberMap.get("vip_type"));
			memberInfo.setVipLevel(mMemberMap.get("vip_level"));
			memberInfo.setPoints(mMemberMap.get("points"));
			memberInfo.setMemberType(mMemberMap.get("member_type"));
		}

		return memberInfo;

	}

	/**
	 * 获取用户配置信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public MemberConfig upMemberConfig(String sMemberCode) {

		MDataMap mMemberMap = DbUp.upTable("mc_extend_info_star").one(
				"member_code", sMemberCode);
		MemberConfig memberConfig = new MemberConfig();
		if (mMemberMap != null) {
			try {
				String flagPush = StringUtils.isEmpty(mMemberMap.get("flag_push")) ? "0" : mMemberMap.get("flag_push");
				memberConfig.setPush(Integer.valueOf(flagPush));
			} catch(Exception e) {
				memberConfig.setPush(0);
			}
		}

		return memberConfig;
	}

	/**
	 * 修改用户信息
	 * 
	 * @param sMemberCode
	 * @param changeMemberInfo
	 * @return
	 */
	public boolean changeMemberInfo(String sMemberCode,
			MemberChangeInput memberChangeInput) {

		return true;
	}

	/**
	 * 修改密码 本操作根据用户编号和原密码来校验是否能修改 <br/>
	 * 如果用户有多个登陆信息时 会将所有登陆信息的密码都重置为同一个密码
	 * 
	 * @param sMemberCode
	 * @param changePasswordInput
	 * @return
	 */
	public RootResultWeb changePassword(String sMemberCode,
			ChangePasswordInput changePasswordInput) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			String sOldPassword = SecrurityHelper
					.MD5Secruity(changePasswordInput.getOld_password());

			// 获取该用户的该操作信息
			int iCount = DbUp.upTable("mc_login_info").count("member_code",
					sMemberCode, "login_pass", sOldPassword);

			// 判断如果该账户能用该密码登陆 则修改密码
			if (iCount > 0) {

				updatePassword(changePasswordInput.getNew_password(),
						sMemberCode);

			} else {

				rootResultWeb.inErrorMessage(934105141);

			}
		}

		return rootResultWeb;
	}
	
	/**
	 * 代理商修改密码 本操作根据用户编号和原密码来校验是否能修改 <br/>
	 * 如果用户有多个登陆信息时 会将所有登陆信息的密码都重置为同一个密码
	 * 
	 * @param sMemberCode
	 * @param changePasswordInput
	 * @return
	 */
	public RootResultWeb changeAgentPassword(String sMemberCode,
			ChangePasswordInput changePasswordInput) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			String sOldPassword = SecrurityHelper
					.MD5Secruity(changePasswordInput.getOld_password());

			// 获取该用户的该操作信息
			int iCount = DbUp.upTable("nc_agency").count("member_code",
					sMemberCode, "login_pass", sOldPassword);

			// 判断如果该账户能用该密码登陆 则修改密码
			if (iCount > 0) {

				updateAgentPassword(changePasswordInput.getNew_password(),
						sMemberCode);

			} else {

				rootResultWeb.inErrorMessage(934105141);

			}
		}

		return rootResultWeb;
	}
	
	/**
	 * 修改密码 本操作根据用户编号和原密码来校验是否能修改 <br/>
	 * 如果用户有多个登陆信息时 会将所有登陆信息的密码都重置为同一个密码
	 * 
	 * @param sMemberCode
	 * @param changePasswordInput
	 * @return
	 */
	public RootResultWeb valiOriginalPassword(String sMemberCode,
			ValiOriginalPasswordInput changePasswordInput) {
		RootResultWeb rootResultWeb = new RootResultWeb();
			String sOldPassword = SecrurityHelper
					.MD5Secruity(changePasswordInput.getOld_password());

			// 获取该用户的该操作信息
			int iCount = DbUp.upTable("mc_login_info").count("member_code",
					sMemberCode, "login_pass", sOldPassword);
			if(iCount==0){
				rootResultWeb.inErrorMessage(934105141);
			}
		return rootResultWeb;
	}

	/**
	 * 更新用户密码 更新用户所属账户的所有app的密码
	 * 
	 * @param sPassword
	 * @param sMemebrCode
	 */
	private void updatePassword(String sPassword, String sMemebrCode) {
		String sNewPassword = SecrurityHelper.MD5Secruity(sPassword);

		String sAccountCode = DbUp
				.upTable("mc_member_info")
				.dataGet("account_code", "",
						new MDataMap("member_code", sMemebrCode)).toString();

		for (MDataMap mMemberMap : DbUp.upTable("mc_member_info").queryByWhere(
				"account_code", sAccountCode)) {

			MDataMap mUpdateMap = new MDataMap();

			mUpdateMap.put("login_pass", sNewPassword);
			mUpdateMap.put("member_code", mMemberMap.get("member_code"));

			// 更新该用户名下所有登陆信息的密码
			DbUp.upTable("mc_login_info").dataUpdate(mUpdateMap, "login_pass",
					"member_code");

		}

	}
	
	
	/**
	 * 代理商更新用户密码 更新用户所属账户的所有密码
	 * 
	 * @param sPassword
	 * @param sMemebrCode
	 */
	private void updateAgentPassword(String sPassword, String sMemebrCode) {
		String sNewPassword = SecrurityHelper.MD5Secruity(sPassword);

			MDataMap mUpdateMap = new MDataMap();

			mUpdateMap.put("agent_password", sNewPassword);
			mUpdateMap.put("member_code", sMemebrCode);
			mUpdateMap.put("isnot_proving", "1");
			

			// 更新该用户名下所有登陆信息的密码
			DbUp.upTable("nc_agency").dataUpdate(mUpdateMap, "agent_password,isnot_proving",
					"member_code");
			
			

		}

	/** 
	* @Description:修改密码 (供微信商城调用)
	* @param manageCode 应用编号
	* @param changePasswordInput
	* @author 张海生
	* @date 2015-4-2 下午4:23:15
	* @return RootResultWeb 
	* @throws 
	*/
	public RootResultWeb changePasswordForWx(String sMemberCode,
			ChangePasswordForWxInput changePasswordInput) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {
				updatePassword(changePasswordInput.getNew_password(),sMemberCode);
		}
		return rootResultWeb;
	}

	/**
	 * 忘记密码
	 * 
	 * @param sLoginName
	 * @param sPassword
	 * @return
	 */
	public RootResultWeb forgetPassword(String sLoginName, String sManageCode,
			String sPassword) {
		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			MDataMap mLoginMap = DbUp.upTable("mc_login_info").one(
					"login_name", sLoginName);

			if (mLoginMap != null && mLoginMap.size() > 0) {

				updatePassword(sPassword, mLoginMap.get("member_code"));
			} else {
				rootResultWeb.inErrorMessage(934105101);
			}

		}
		return rootResultWeb;

	}
	
	/**
	 *代理商忘记密码
	 * 
	 * @param sLoginName
	 * @param sPassword
	 * @return
	 */
	public RootResultWeb forgetAgentPassword(String sLoginName, String sManageCode,
			String sPassword) {
		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			MDataMap mLoginMap = DbUp.upTable("nc_agency").one(
					"agent_mobilephone", sLoginName);

			if (mLoginMap != null && mLoginMap.size() > 0) {

				updateAgentPassword(sPassword, mLoginMap.get("member_code"));
			} else {
				rootResultWeb.inErrorMessage(934105101);
			}

		}
		return rootResultWeb;

	}

	/**
	 * 家有汇验证手机或者邮箱是否存在
	 * 
	 * @param bindNum
	 *            邮箱号或者手机号
	 * @param type
	 *            2：邮箱，3：手机号
	 * @author 张海生
	 * @date 2014-11-7 下午11:20:22
	 * @return
	 */
	public RootResultWeb HomePoolCheckInfor(String bindNum, String type) {
		RootResultWeb result = new RootResultWeb();
		MDataMap mp = new MDataMap();
		if ("1".equals(type)) {// 验证邮箱是否存在
			mp.put("email", bindNum);
			if (result.upFlagTrue()) {
				int count = DbUp.upTable("mc_extend_info_homepool").dataCount(
						"email=:email", mp);
				if (count > 0) {
					result.inErrorMessage(934105109);
				}
			}
		} else if ("2".equals(type)) {// 验证手机号是否存在
			mp.put("mobile", bindNum);
			if (result.upFlagTrue()) {
				int count = DbUp.upTable("mc_extend_info_homepool").dataCount(
						"mobile=:mobile", mp);
				if (count > 0) {
					result.inErrorMessage(934105110);
				}
			}
		}
		return result;
	}

	/**
	 * @Description:验证用户名是否存在，并返回邮箱和手机号
	 * @param userName
	 *            用户名
	 * @author 张海生
	 * @date 2014-11-20 上午11:00:48
	 * @return CheckUserResult
	 * @throws
	 */
	public CheckUserResult checkUser(String userName) {
		CheckUserResult userResult = new CheckUserResult();
		String sSql = "select mobile, email from mc_extend_info_homepool where member_name=:userName or mobile=:userName or email=:userName";
		List<Map<String, Object>> ldata = DbUp.upTable(
				"mc_extend_info_homepool").dataSqlList(sSql,
				new MDataMap("userName", userName));
		if (ldata != null && ldata.size() > 0) {
			Map<String, Object> mdata = ldata.get(0);
			userResult.setEmail((String) mdata.get("email"));
			userResult.setMobile((String) mdata.get("mobile"));
		} else {
			userResult.inErrorMessage(934105101);
		}
		return userResult;
	}

	/**
	 * @Description: 家有汇用户昵称
	 * @param nickName
	 *            昵称
	 * @param memCode
	 * @author 张海生
	 * @date 2014-12-17 上午10:36:45
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb checkNickName(String nickName, String memCode) {
		RootResultWeb result = new RootResultWeb();
		String sSql = "select zid, nickname from mc_extend_info_homepool where nickname=:nickName and member_code !=:memCode";
		MDataMap dMap = new MDataMap();
		dMap.put("nickName", nickName);
		dMap.put("memCode", memCode);
		List<Map<String, Object>> ldata = DbUp.upTable(
				"mc_extend_info_homepool").dataSqlList(sSql, dMap);
		if (ldata != null && ldata.size() > 0) {
			result.inErrorMessage(934105114);// 用户昵称已存在
		}
		return result;
	}

	/**
	 * @Description:根据家有汇用户Code获取惠家友用户Code
	 * @param memberCode
	 *            家有汇用户Code
	 * @author 张海生
	 * @date 2014-12-24 下午2:18:01
	 * @return memCodeResult
	 * @throws
	 */
	public MemCodeResult getHjyMemCode(String memberCode) {
		MemCodeResult result = new MemCodeResult();
		List<MDataMap> dataList = DbUp.upTable("mc_login_info").queryAll(
				"login_name", null, null,
				new MDataMap("member_code", memberCode));
		if (dataList != null && dataList.size() > 0) {
			String sql = "manage_code=:manageCode";
			int size = dataList.size();
			String loginNames = "";
			for (int i = 0; i < size; i++) {
				if ("".equals(loginNames)) {
					loginNames += "	AND login_name in ('"
							+ dataList.get(i).get("login_name") + "'";
				} else {
					loginNames += ",'" + dataList.get(i).get("login_name")
							+ "'";
				}
			}
			sql = sql + loginNames + ")";
			List<MDataMap> dataList1 = DbUp.upTable("mc_login_info")
					.queryAll(
							"manage_code,member_code",
							null,
							sql,
							new MDataMap("manageCode",
									MemberConst.MANAGE_CODE_HOMEHAS));
			if (dataList1 != null && dataList1.size() > 0) {
				MDataMap mData1 = dataList1.get(0);
				result.setManageCode(mData1.get("manage_code"));
				result.setMemberCode(mData1.get("member_code"));
			} else {
				result.inErrorMessage(934105101);// 用户不存在
			}
		} else {
			result.inErrorMessage(934105101);// 用户不存在
		}
		return result;
	}

	/**
	 * @Description:更新用户的等级，积分，会员类型
	 * @param lever
	 *            会员等级
	 * @param score
	 *            积分
	 * @param sMemebrCode
	 *            用户code
	 * @author 张海生
	 * @date 2014-12-27 下午5:39:18
	 * @throws
	 */
	public void updateUserLever(String lever, String score, String sMemebrCode) {
		MDataMap mUpdateMap = new MDataMap();
		mUpdateMap.put("points", score);
		mUpdateMap.put("member_code", sMemebrCode);
		// 80:警惕会员,90:黑名单会员,100:睡眠会员
		if ("80".equals(lever) || "90".equals(lever) || "100".equals(lever)) {
			mUpdateMap.put("status", "449746600002");// 禁用
			// 更新用户的积分和可用状态
			DbUp.upTable("mc_extend_info_homepool").dataUpdate(mUpdateMap,
					"points,status", "member_code");
		} else if ("70".equals(lever)) {
			mUpdateMap.put("vip_type", "4497469400050001");// 家有员工
			// 更新用户的积分和会员类型
			DbUp.upTable("mc_extend_info_homepool").dataUpdate(mUpdateMap,
					"points,vip_type", "member_code");
		} else {
			if ("10".equals(lever)) {// 顾客
				mUpdateMap.put("vip_level", "4497469400060001");
			} else if ("20".equals(lever)) {// 一星会员
				mUpdateMap.put("vip_level", "4497469400060002");
			} else if ("25".equals(lever)) {// 二星会员
				mUpdateMap.put("vip_level", "4497469400060003");
			} else if ("30".equals(lever)) {// 三星会员
				mUpdateMap.put("vip_level", "4497469400060004");
			} else if ("40".equals(lever)) {// 四星会员
				mUpdateMap.put("vip_level", "4497469400060005");
			} else if ("50".equals(lever)) {// 五星会员
				mUpdateMap.put("vip_level", "4497469400060006");
			}
			// 更新用户的积分和等级
			DbUp.upTable("mc_extend_info_homepool").dataUpdate(mUpdateMap,
					"points,vip_level", "member_code");
		}
	}

	public RootResultWeb checkLoginNameIsExist(String loginName) {
		RootResultWeb result = new RootResultWeb();
		MDataMap mLoginMap = DbUp.upTable("mc_login_info").one("login_name",loginName);
		if (null != mLoginMap) {
			result.inErrorMessage(934105146);
			if(StringUtils.isBlank(mLoginMap.get("login_pass").trim())){
				result.inErrorMessage(934105145);
			}
		}else{
			result.setResultMessage("该号码没有注册，请先注册");
		}
		return result;
	}
	/**
	 * @Description:验证登录名是否存在
	 * @param loginName
	 *            登录名
	 * @author 张海生
	 * @date 2015-1-10 下午5:14:16
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb checkLoginName(String loginName) {
		RootResultWeb result = new RootResultWeb();
		int count = DbUp.upTable("mc_login_info").dataCount(
				null,
				new MDataMap("login_name", loginName, "manage_code",
						MemberConst.MANAGE_CODE_HPOOL));
		if (count > 0) {
			result.inErrorMessage(934105118);// 登录名已经在家有汇注册
		} else {
			int count1 = DbUp.upTable("mc_login_info").dataCount(
					"login_name=:login_name and manage_code<>:manage_code",
					new MDataMap("login_name", loginName, "manage_code",
							MemberConst.MANAGE_CODE_HPOOL));
			if(count1 > 0){
				result.inErrorMessage(934105119);//登录名已在家有汇以外的系统注册
			}else{
				result.setResultMessage("该号码没有注册，请先注册");
			}
		}
		return result;
	}
	/**
	 * 密码为空时更新密码（仅限惠家有网页购买初始化密码时使用） <br/>
	 * 如果用户有多个登陆信息时 会将所有登陆信息的密码都重置为同一个密码
	 * 
	 * @param sMemberCode
	 * @param changePasswordInput
	 * @return
	 */
	public RootResultWeb changePasswordForHtml(String sMemberCode,
			ChangePasswordInput changePasswordInput) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()&&"".equals(changePasswordInput.getOld_password())) {

			String sOldPassword = "";

			// 获取该用户的该操作信息
			int iCount = DbUp.upTable("mc_login_info").count("member_code",
					sMemberCode, "login_pass", sOldPassword);

			// 判断如果该账户能用该密码登陆 则修改密码
			if (iCount > 0) {

				updatePassword(changePasswordInput.getNew_password(),
						sMemberCode);

			} else {

				rootResultWeb.inErrorMessage(934105143);

			}
		}

		return rootResultWeb;
	}
}
