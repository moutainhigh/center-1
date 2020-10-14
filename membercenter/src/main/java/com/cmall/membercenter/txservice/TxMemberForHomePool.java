package com.cmall.membercenter.txservice;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.membercenter.McExtendInfoHomepoolMapper;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomepool;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomepoolExample;
import com.cmall.dborm.txmodel.membercenter.McLoginInfo;
import com.cmall.membercenter.group.model.HomePoolLoginInput;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.cmall.membercenter.model.TVUserRegHomePoolInput;
import com.cmall.membercenter.model.UserRegHomePoolInput;
import com.cmall.membercenter.model.UserRegHomePoolResult;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * @author ligj
 * 
 */
public class TxMemberForHomePool extends TxMemberBase {

	/**
	 * 用户注册
	 * 
	 * @param userRegInput
	 * @return
	 */
	public UserRegHomePoolResult insertUserReg(
			UserRegHomePoolInput userRegInput, String sManageCode) {

		UserRegHomePoolResult userRegResult = new UserRegHomePoolResult();
		MReginsterResult result = null;
		String memcode = "";
		String loginName = "";
		String mobile = userRegInput.getMobile();
		if (userRegResult.upFlagTrue()) {
			loginName = userRegInput.getLogin_name();
			String sSql = "select zid from mc_login_info where login_name=:loginName or login_name=:mobile";
			List<Map<String, Object>> ldata = DbUp.upTable("mc_login_info")
					.dataSqlList(
							sSql,
							new MDataMap("loginName", loginName, "mobile",
									mobile));
			if (ldata != null && ldata.size() > 0) {
				userRegResult.inErrorMessage(922401028);// 登录名已经存在
				return userRegResult;
			}
			MLoginInput input = new MLoginInput();
			String pwd = userRegInput.getPassword();
			input.setLoginName(loginName);
			input.setLoginPassword(pwd);
			input.setManageCode(sManageCode);
			// 通行证账号
			input.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);

			result = doUserReginster(input);
			memcode = result.getMemberInfo().getMemberCode();
			// 开始插入登陆信息
			if (result.upFlagTrue()) {
				if (!userRegInput.getLogin_name().equals(mobile)) {// 插入登录名为手机号的登录信息
					McLoginInfo mInfor = new McLoginInfo();
					mInfor.setLoginCode(sManageCode + WebConst.CONST_SPLIT_DOWN
							+ mobile);
					mInfor.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
					mInfor.setLoginName(mobile);
					mInfor.setLoginPass(SecrurityHelper.MD5Secruity(pwd));
					mInfor.setManageCode(sManageCode);
					mInfor.setMemberCode(memcode);
					TxMemberBase tb = new TxMemberBase();
					tb.insertLoginInfo(mInfor);
				}
			} else {
				userRegResult.setResultCode(result.getResultCode());
				userRegResult.setResultMessage(result.getResultMessage());
			}
		}
		// 开始插入扩展信息表
		if (userRegResult.upFlagTrue()) {
			saveUserExtendInfor(memcode, loginName, mobile,
					userRegInput.getRealName(), userRegInput.getCusId());// 插入扩展信息
		}
		// 开始返回用户的登录信息
		if (userRegResult.upFlagTrue()) {
			userRegResult = getUserLoginInfor(memcode, sManageCode, loginName,
					userRegResult);
		}
		return userRegResult;
	}

	/**
	 * @Description:电视用户注册
	 * @param userRegInput
	 *            用户输入参数
	 * @author 张海生
	 * @date 2014-11-14 上午10:09:03
	 * @return UserRegHomePoolResult
	 * @throws
	 */
	public UserRegHomePoolResult insertTVUserReg(
			TVUserRegHomePoolInput userRegInput) {
		UserRegHomePoolResult userResult = new UserRegHomePoolResult();
		String loginName = userRegInput.getLogin_name();
		MDataMap mdata = DbUp.upTable("mc_login_info").one("login_name",
				loginName, "manage_code", MemberConst.MANAGE_CODE_HOMEHAS,
				"login_pass", "", "last_time", "");// 登录表里查询是否符合电视用户注册条件
		String memCode = "";
		if (mdata != null) {
			memCode = mdata.get("member_code");
			if (!StringUtils.isEmpty(memCode)) {
				int count = DbUp.upTable("mc_extend_info_homehas").count(
						"member_code", memCode);// 扩展表里查询是否符合电视用户注册条件
				if (count == 0) {
					userResult.inErrorMessage(934105111);
				} else {
					userResult.inOtherResult(new MemberInfoSupport()
							.forgetPassword(loginName,
									MemberConst.MANAGE_CODE_HOMEHAS,
									userRegInput.getPassword()));// 添加密码
				}
			} else {
				userResult.inErrorMessage(934105111);
			}
		} else {
			userResult.inErrorMessage(934105111);
		}
		if (userResult.upFlagTrue()) {// 插入扩展信息
			saveUserExtendInfor(memCode, loginName, userRegInput.getMobile(),
					userRegInput.getRealName(), "");
		}
		if (userResult.upFlagTrue()) {// 返回登录信息
			getUserLoginInfor(memCode, MemberConst.MANAGE_CODE_HOMEHAS,
					loginName, userResult);
		}
		return userResult;
	}

	/**
	 * @Description:插入用户扩展信息
	 * @param memCode
	 *            用户code
	 * @param loginName
	 *            登录名
	 * @param mobile
	 *            手机号
	 * @param realName
	 *            真实姓名
	 * @author 张海生
	 * @date 2014-11-14 上午11:05:08
	 * @return void
	 * @throws
	 */
	public void saveUserExtendInfor(String memCode, String loginName,
			String mobile, String realName, String cusId) {
		McExtendInfoHomepoolMapper mcExtendInfoHomepoolMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomepoolMapper");
		McExtendInfoHomepool mcExtendInfoHomepool = new McExtendInfoHomepool();
		mcExtendInfoHomepool.setMemberCode(memCode);
		mcExtendInfoHomepool.setUid(WebHelper.upUuid());
		mcExtendInfoHomepool.setMemberName(loginName);
		mcExtendInfoHomepool.setMobile(mobile);
		mcExtendInfoHomepool.setRealname(realName);
		mcExtendInfoHomepool.setOldCode(cusId);
		mcExtendInfoHomepool.setMobileStatus(new Integer(1).byteValue());// 手机为已验证
		mcExtendInfoHomepool.setCreateTime(FormatHelper.upDateTime());
		mcExtendInfoHomepoolMapper.insertSelective(mcExtendInfoHomepool);
	}

	/**
	 * @Description: 获取用户的登录信息
	 * @param memCode
	 *            用户code
	 * @param manageCode
	 *            应用编号
	 * @param loginName
	 *            登录名
	 * @param userRegResult
	 * @author 张海生
	 * @date 2014-11-14 上午11:19:52
	 * @return UserRegHomePoolResult
	 * @throws
	 */
	public UserRegHomePoolResult getUserLoginInfor(String memCode,
			String manageCode, String loginName,
			UserRegHomePoolResult userRegResult) {
		String sAuthCode = new MemberLoginSupport().memberLogin(memCode,
				manageCode, loginName);
		userRegResult.getUser().setMember_code(memCode);
		if (StringUtils.isNotEmpty(sAuthCode)) {
			userRegResult.setUser_token(sAuthCode);
		} else {
			userRegResult.inErrorMessage(934105102);
		}
		return userRegResult;
	}

	/**
	 * @Description: 修改用户基本资料
	 * @param mcExtendInfoHomepool
	 *            用户基本信息实体
	 * @param sMemberCode
	 *            用户code
	 * @author 张海生
	 * @date 2014-11-10 下午5:42:34
	 * @return MWebResult
	 * @throws
	 */
	public MWebResult updateMemberInfo(
			McExtendInfoHomepool mcExtendInfoHomepool, String sMemberCode,
			String manageCode) {
		MWebResult result = new MWebResult();
		String mobilePhone = mcExtendInfoHomepool.getMobile();
		Map<String, Object> dmap = getLoginInforByMem(sMemberCode, manageCode);
		if (dmap == null || dmap.size() < 1) {
			result.inErrorMessage(934105101);
			return result;
		}
		if (!StringUtils.isEmpty(mobilePhone)) {
			int count = DbUp.upTable("mc_login_info").count("login_name",
					mobilePhone, "manage_code", manageCode);
			if (count == 0) {
				McLoginInfo mInfor = new McLoginInfo();
				setMinfor(mInfor, dmap, mobilePhone);
				TxMemberBase tb = new TxMemberBase();
				tb.insertLoginInfo(mInfor);
			}
		}
		McExtendInfoHomepoolMapper mcExtendInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomepoolMapper");

		McExtendInfoHomepoolExample example = new McExtendInfoHomepoolExample();

		example.createCriteria().andMemberCodeEqualTo(sMemberCode);

		mcExtendInfoMapper.updateByExampleSelective(mcExtendInfoHomepool,
				example);

		return new MWebResult();
	}

	/**
	 * @Description:绑定邮箱
	 * @param email
	 *            邮箱号
	 * @param sMemberCode
	 *            用户code
	 * @param manageCode
	 *            应用编号
	 * @author 张海生
	 * @date 2014-11-26 下午1:31:27
	 * @return MWebResult
	 * @throws
	 */
	public MWebResult bindEmail(String email, String sMemberCode,
			String manageCode) {
		MWebResult result = new MWebResult();
		int count = DbUp.upTable("mc_extend_info_homepool").count("email",
				email, "emai_status", "1");
		if (count > 0) {// 如果邮箱已经被绑定
			result.inErrorMessage(934105112);
		} else {
			MDataMap dMap = new MDataMap();
			dMap.put("email", email);
			dMap.put("emai_status", "1");
			dMap.put("member_code", sMemberCode);
			DbUp.upTable("mc_extend_info_homepool").dataUpdate(dMap,
					"email,emai_status", "member_code");// 更新邮箱为已验证
			List<Map<String, Object>> mList = getOtherLoginInfor(email,
					sMemberCode, manageCode);// 查询是否存在同登录名的别的用户
			if (mList != null && mList.size() > 0) {
				result.inErrorMessage(934105112);
			} else {
				List<Map<String, Object>> cList = getLonginInfor(email,
						sMemberCode, manageCode);// 查询登录记录是否存在
				if (cList == null || cList.size() == 0) {
					Map<String, Object> dmap = getLoginInforByMem(sMemberCode,
							manageCode);
					if (dmap == null || dmap.size() < 1) {
						result.inErrorMessage(934105101);
						return result;
					} else {// 插入邮箱登录记录
						McLoginInfo mInfor = new McLoginInfo();
						setMinfor(mInfor, dmap, email);
						TxMemberBase tb = new TxMemberBase();
						tb.insertLoginInfo(mInfor);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @Description: 绑定手机
	 * @param mobile
	 *            手机号
	 * @param sMemberCode
	 *            用户code
	 * @param manageCode
	 *            应用编号
	 * @return
	 * @author 张海生
	 * @date 2014-11-26 下午3:52:27
	 * @return MWebResult
	 * @throws
	 */
	public MWebResult bindMobile(String mobile, String sMemberCode,
			String manageCode) {
		MWebResult result = new MWebResult();
		int count = DbUp.upTable("mc_extend_info_homepool").count("mobile",
				mobile, "mobile_status", "1");
		if (count > 0) {// 如果手机已经被绑定
			result.inErrorMessage(934105113);
		} else {
			MDataMap dMap = new MDataMap();
			dMap.put("mobile", mobile);
			dMap.put("mobile_status", "1");
			dMap.put("member_code", sMemberCode);
			DbUp.upTable("mc_extend_info_homepool").dataUpdate(dMap,
					"mobile,mobile_status", "member_code");// 更新手机为已验证
			List<Map<String, Object>> mList = getOtherLoginInfor(mobile,
					sMemberCode, manageCode);// 查询是否存在同登录名的别的用户
			if (mList != null && mList.size() > 0) {
				result.inErrorMessage(934105113);
			} else {
				List<Map<String, Object>> cList = getLonginInfor(mobile,
						sMemberCode, manageCode);// 查询登录记录是否存在
				if (cList == null || cList.size() == 0) {// 如果登录表里不存在用户的手机登录记录则插入
					Map<String, Object> dmap = getLoginInforByMem(sMemberCode,
							manageCode);
					if (dmap == null || dmap.size() < 1) {
						result.inErrorMessage(934105101);
						return result;
					} else {// 插入邮箱登录记录
						McLoginInfo mInfor = new McLoginInfo();
						setMinfor(mInfor, dmap, mobile);
						TxMemberBase tb = new TxMemberBase();
						tb.insertLoginInfo(mInfor);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @Description: 查询是否存在同登录名的别的用户
	 * @param longinName
	 * @param sMemberCode
	 * @param manageCode
	 * @return
	 * @author 张海生
	 * @date 2014-11-26 下午5:59:56
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getOtherLoginInfor(String longinName,
			String sMemberCode, String manageCode) {
		String sql = "SELECT zid FROM mc_login_info where  member_code <> '"
				+ sMemberCode + "' and login_name='" + longinName
				+ "' and manage_code='" + manageCode + "'";
		List<Map<String, Object>> mList = DbUp.upTable("mc_login_info")
				.dataSqlList(sql, null);
		return mList;
	}

	/**
	 * @Description: 查询用户登录记录是否存在
	 * @param longinName
	 * @param sMemberCode
	 * @param manageCode
	 * @return
	 * @author 张海生
	 * @date 2014-11-26 下午6:02:54
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getLonginInfor(String longinName,
			String sMemberCode, String manageCode) {
		String csql = "SELECT zid FROM mc_login_info where  member_code = '"
				+ sMemberCode + "' and login_name='" + longinName
				+ "' and manage_code='" + manageCode + "'";
		List<Map<String, Object>> cList = DbUp.upTable("mc_login_info")
				.dataSqlList(csql, null);
		return cList;
	}

	/**
	 * @Description:根据用户code获取登录信息
	 * @param memberCode
	 *            用户code
	 * @param manageCode
	 *            应用编号
	 * @author 张海生
	 * @date 2014-11-12 下午5:15:31
	 * @return Map<String,Object>
	 * @throws
	 */
	public Map<String, Object> getLoginInforByMem(String memberCode,
			String manageCode) {
		String sql = "select zid,uid,login_code, manage_code,login_name,login_pass,member_code,create_time,failed_time,"
				+ "failed_count,flag_enable,last_time,login_type,login_group from mc_login_info where member_code=:memberCode and manage_code=:manageCode";
		Map<String, Object> dmap = DbUp.upTable("mc_login_info")
				.dataSqlOne(
						sql,
						new MDataMap("memberCode", memberCode, "manageCode",
								manageCode));
		return dmap;
	}

	/**
	 * @Description:
	 * @param mInfor
	 *            登录实体
	 * @param lm
	 *            已有登录信息
	 * @param longinName
	 *            登录名
	 * @author 张海生
	 * @date 2014-11-10 下午5:35:57
	 * @return McLoginInfo
	 * @throws
	 */
	public McLoginInfo setMinfor(McLoginInfo mInfor, Map<String, Object> lm,
			String longinName) {
		mInfor.setLoginGroup((String) lm.get("login_group"));
		mInfor.setLoginName(longinName);
		mInfor.setLoginPass((String) lm.get("login_pass"));
		mInfor.setLoginType((String) lm.get("login_type"));
		mInfor.setManageCode((String) lm.get("manage_code"));
		mInfor.setLoginCode(mInfor.getManageCode() + WebConst.CONST_SPLIT_DOWN
				+ longinName);
		mInfor.setMemberCode((String) lm.get("member_code"));
		return mInfor;
	}

	/**
	 * @Description:从家有倒过来的内购会员，密码默认为手机后六位
	 * @author 张海生
	 * @date 2015-2-2 下午4:36:56
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb setInnerMemer() {
		bLogInfo(0, "*****************家有汇内购会员导入更新开始*******************");
		RootResultWeb reuslt = new RootResultWeb();
		List<MDataMap> mdataList = DbUp.upTable("inner_member_temp")
				.queryAll(null, "zid desc", null, null);
		MemberLoginSupport mSupport = new MemberLoginSupport();
		HomePoolLoginInput LoginInput = new HomePoolLoginInput();
		String remark = "";
		for (int i = 0; i < mdataList.size(); i++) {
			//System.out.println(i);
			if (mdataList.get(i) == null)
				continue;
			String mobile = mdataList.get(i).get("mobile");
			bLogInfo(0, "*****" + i + ":开始执行手机号为" + mobile + "的用户*****");
			String realName = mdataList.get(i).get("cusName");
			String cusId = mdataList.get(i).get("cusId");
			if (StringUtils.isNotEmpty(mobile)) {
				String pwd1 = mobile.substring(mobile.length() - 6);
				String pwd2 = SecrurityHelper.MD5Secruity(pwd1);
				List<MDataMap> mList = DbUp.upTable("mc_login_info").queryAll(
						"login_pass,manage_code", null, null,
						new MDataMap("login_name", mobile));
				if (mList == null || mList.size() == 0) {// 如果系统中不存在此手机号的登录记录，则在家有汇系统注册
					this.insertUserReg1("site", mobile, mobile, pwd1, realName,
							cusId);
					remark = "1";
					remarkMemberTemp(remark, mobile);
					continue;
				}
				MDataMap md1 = null;
				MDataMap md2 = null;
				for (MDataMap mDataMap : mList) {
					if(MemberConst.MANAGE_CODE_HPOOL.equals(mDataMap.get("manage_code"))){
						md1 = mDataMap;
					}else if(MemberConst.MANAGE_CODE_HOMEHAS.equals(mDataMap.get("manage_code"))){
						md2 = mDataMap;
					}
				}
				if (md1 != null
						&& StringUtils.isNotEmpty(md1.get("login_pass"))) {// 更新家有汇内购会员相关信息
					updateCusInfor(realName, cusId, "4497469400050001", mobile);
					remark = "2";
					remarkMemberTemp(remark, mobile);
					continue;
				}
				if (md2 != null) {// 惠家友登录记录存在
					String pwd = md2.get("login_pass");
					if (StringUtils.isNotEmpty(pwd)) {
						// 家有汇用户信息存在，但密码为空，则密码跟惠家友一致
						if(md1 != null){
							this.updateHomePoolPwd(pwd, mobile,
									MemberConst.MANAGE_CODE_HPOOL);
							updateCusInfor(realName, cusId, "4497469400050001",
									mobile);// 更新家有汇用户扩展表
							remark = "3";
							remarkMemberTemp(remark, mobile);
						}else{//插入家有汇的登录记录及相关信息
							LoginInput.setLoginName(mobile);
							LoginInput.setLoginPass("");
							mSupport.homePoolLogin(LoginInput, MemberConst.MANAGE_CODE_HPOOL);
							MDataMap md3 = DbUp.upTable("mc_login_info").oneWhere(
									"member_code", null, null, "manage_code",
									MemberConst.MANAGE_CODE_HPOOL, "login_name", mobile);
							if(md3 != null){
								saveUserExtendInfor(md3.get("member_code"), mobile, mobile, realName, cusId);
							}
							updateCusInfor(realName, cusId, "4497469400050001",
									mobile);// 更新家有汇用户扩展表
							remark = "4";
							remarkMemberTemp(remark, mobile);
						}
					} else {// 如果惠家友登录密码为空
						this.updateHomePoolPwd(pwd2, mobile,
								MemberConst.MANAGE_CODE_HOMEHAS);
						// 家有汇用户信息存在，但密码为空，则密码跟惠家友一致
						if(md1 != null){
							this.updateHomePoolPwd(pwd2, mobile,
									MemberConst.MANAGE_CODE_HPOOL);
							updateCusInfor(realName, cusId, "4497469400050001",
									mobile);// 更新家有汇用户扩展表
							remark = "5";
							remarkMemberTemp(remark, mobile);
						}else{//插入家有汇的登录记录及相关信息
							LoginInput.setLoginName(mobile);
							LoginInput.setLoginPass(pwd1);
							mSupport.homePoolLogin(LoginInput, MemberConst.MANAGE_CODE_HPOOL);
							updateCusInfor(realName, cusId, "4497469400050001",
									mobile);// 更新家有汇用户扩展表
							remark = "6";
							remarkMemberTemp(remark, mobile);
						}
					}
				} else {// 惠家友登录不存在
					if (md1 == null) {// 家有汇记录不存在,则直接注册家有汇用户
						this.insertUserReg1("site", mobile, mobile, pwd1,
								realName, cusId);
						remark = "7";
						remarkMemberTemp(remark, mobile);
					} else {
						this.updateHomePoolPwd(pwd2, mobile,
								MemberConst.MANAGE_CODE_HPOOL);
						updateCusInfor(realName, cusId, "4497469400050001",
								mobile);
						remark = "8";
						remarkMemberTemp(remark, mobile);
					}
				}
			}
		}
		bLogInfo(0, "**************家有汇内购会员导入更新结束************");
		return reuslt;
	}
	
	/** 
	* @Description:对内购会员临时表添加备注
	* @param ramark 备注
	* @param mobile 手机号
	* @author 张海生
	* @date 2015-2-5 下午4:41:23
	* @return void 
	* @throws 
	*/
	public void remarkMemberTemp(String remark,String mobile){
		MDataMap myMap = new MDataMap();
		myMap.put("dealStatus", "1");
		myMap.put("dealTime", FormatHelper.upDateTime());
		myMap.put("mobile", mobile);
		myMap.put("remark", remark);
		DbUp.upTable("inner_member_temp").dataUpdate(myMap, "dealStatus,dealTime,remark", "mobile");
	}

	/**
	 * @Description:注册家有汇用户
	 * @param site
	 *            注册来源
	 * @param mobile
	 *            手机号
	 * @param pwd
	 *            密码
	 * @param realName
	 *            真实姓名
	 * @param cusId
	 *            家有LD系统用户code
	 * @author 张海生
	 * @date 2015-2-3 下午3:13:28
	 * @return void
	 * @throws
	 */
	public void insertUserReg1(String site, String loginName, String mobile,
			String pwd, String realName, String cusId) {
		UserRegHomePoolInput userInput = new UserRegHomePoolInput();
		userInput.setClient_source(site);
		userInput.setLogin_name(loginName);
		userInput.setMobile(mobile);
		userInput.setPassword(pwd);
		userInput.setRealName(realName);
		userInput.setCusId(cusId);
		this.insertUserReg(userInput, MemberConst.MANAGE_CODE_HPOOL);
	}

	/**
	 * @Description:更新用户某个应用的密码
	 * @param pwd
	 *            密码
	 * @param loginName
	 *            登录名
	 * @param manageCode
	 *            应用编号
	 * @author 张海生
	 * @date 2015-2-3 下午3:21:39
	 * @return void
	 * @throws
	 */
	public void updateHomePoolPwd(String pwd, String loginName,
			String manageCode) {
		MDataMap dMap = new MDataMap();
		dMap.put("login_pass", pwd);
		dMap.put("login_name", loginName);
		dMap.put("manage_code", manageCode);
		DbUp.upTable("mc_login_info").dataUpdate(dMap, "login_pass",
				"login_name,manage_code");
	}

	/**
	 * @Description:更新家有汇内购会员相关信息
	 * @param realName
	 *            真实姓名
	 * @param cusId
	 *            家有LD系统用户code
	 * @param vipType
	 *            类型（4497469400050001：内购会员，4497469400050002：普通会员）
	 * @param mobile
	 * @author 张海生
	 * @date 2015-2-3 下午3:29:22
	 * @return void
	 * @throws
	 */
	public void updateCusInfor(String realName, String cusId, String vipType,
			String mobile) {
		MDataMap dMap1 = new MDataMap();
		dMap1.put("realName", realName);
		dMap1.put("old_code", cusId);
		dMap1.put("vip_type", vipType);
		dMap1.put("mobile", mobile);
		DbUp.upTable("mc_extend_info_homepool").dataUpdate(dMap1,
				"realName,old_code,vip_type", "mobile");
	}
}
