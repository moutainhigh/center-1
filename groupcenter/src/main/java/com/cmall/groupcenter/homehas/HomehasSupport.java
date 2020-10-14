package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCustInfo.CustInfo;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.systemcenter.bill.HexUtil;
import com.cmall.systemcenter.bill.MD5Util;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户中心支持类<br>
 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
 */
public class HomehasSupport extends BaseClass {

	/**
	 * 调用用户中心的注册接口<br>
	 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
	 * @param phone 手机号
	 * @param pass 密码
	 * @return token 注册成功的AccessToken，失败则返回空字符串
	 */
	public String register(String phone, String pass){
		MWebResult result = registerWithResult(phone, pass);
		if(result.getResultCode() != 1) return "";
		return ((MDataMap)result.getResultObject()).get("accessToken");
	}
	
	/**
	 * 调用用户中心的注册接口<br>
	 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
	 * @param phone 手机号
	 * @param pass 密码
	 * @return  注册成功时ResultObject对象是一个MDataMap的类型值，包含accessToken，memberCode
	 */
	public MWebResult registerWithResult(String phone, String pass){
		JSONObject obj = new JSONObject();
		obj.put("loginName", phone);
		obj.put("loginPass", pass);
		obj.put("version", "1");
		
		MDataMap dataMap = new MDataMap();
		dataMap.put("api_target", bConfig("groupcenter.checkedUserInfo_api_target"));
		dataMap.put("api_key", bConfig("groupcenter.checkedUserInfo_api_key"));
		dataMap.put("api_input", obj.toJSONString());
		dataMap.put("api_timespan", FormatHelper.upDateTime());
		dataMap.put("api_project", bConfig("groupcenter.checkedUserInfo_api_project"));
		
		StringBuffer str = new StringBuffer();
		str.append(dataMap.get("api_target"));
		str.append(dataMap.get("api_key"));
		str.append(dataMap.get("api_input"));
		str.append(dataMap.get("api_timespan"));
		str.append(bConfig("groupcenter.checkedUserInfo_api_pass"));
		dataMap.put("api_secret", HexUtil.toHexString(MD5Util.md5(str.toString())));
		
		MWebResult result = new MWebResult();
		
		String sResponseString = null;
		try {
			sResponseString = WebClientSupport.upPost(bConfig("groupcenter.chcekedUserInfo_api_url"), dataMap);
			LogFactory.getLog(getClass()).info("[用户中心注册结果]["+phone+"]"+sResponseString);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(0);
			result.setResultMessage(e+"");
			return result;
		}
		
		try {
			JSONObject resultJson = JSON.parseObject(sResponseString);
			result.setResultObject(new MDataMap("accessToken",resultJson.getString("accessToken"),"memberCode",resultJson.getString("memberCode")));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(0);
			result.setResultMessage(e+"");
			
			LogFactory.getLog(getClass()).warn("调用用户中心的注册接口失败："+sResponseString);
		}
		
		return result;
	}
	
	/**
	 * 根据家有客代号调用用户中心接口注册用户，如果用户已存在则无操作<br>
	 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
	 * @param mResult
	 * @param homehasCode
	 * @return 
	 */
	public MWebResult registerByHomeHasCode(String homehasCode){
		MWebResult mResult = new MWebResult();
		MDataMap homehas = null;
		MDataMap mMemberMap = null;
		CustInfo custInfo = null;
		// 根据客代号查询对应的手机号
		RsyncCustInfo rsyncCustInfo=new RsyncCustInfo();
		rsyncCustInfo.upRsyncRequest().setCust_id(homehasCode);
		rsyncCustInfo.doRsync();
		if(rsyncCustInfo.getResponseObject()!=null&&rsyncCustInfo.getResponseObject().getResult()!=null&&rsyncCustInfo.getResponseObject().getResult().size()>0){
			// 默认取第一个
			custInfo = rsyncCustInfo.getResponseObject().getResult().get(0);
		}
		
		if(custInfo == null){
			mResult.inErrorMessage(918505131, homehasCode);
			return mResult;
		}
		
		// 取手机号
		String sMobilePhone = StringUtils.trimToEmpty(custInfo.getHp_teld())+StringUtils.trimToEmpty(custInfo.getHp_telh())+StringUtils.trimToEmpty(custInfo.getHp_teln());
		if (StringUtils.isEmpty(sMobilePhone)){
			sMobilePhone = StringUtils.trimToEmpty(custInfo.getTeld())+StringUtils.trimToEmpty(custInfo.getTelh())+StringUtils.trimToEmpty(custInfo.getTeln());
		}
		if (StringUtils.isEmpty(sMobilePhone)){
			mResult.inErrorMessage(918505101, custInfo.getCust_id());
			return mResult;
		}
		if (!RegexHelper.checkRegexField(sMobilePhone, RegexConst.MOBILE_PHONE)) {
			mResult.inErrorMessage(918505104, custInfo.getCust_id(), sMobilePhone);
			return mResult;
		}
		
		// 用户是否已注册
		mMemberMap = DbUp.upTable("mc_login_info").one("login_name", sMobilePhone, "manage_code", MemberConst.MANAGE_CODE_HOMEHAS);
		
		// 如果用户不存在则走用户中心进行注册
		if(mMemberMap == null){
			register(sMobilePhone, RandomStringUtils.randomNumeric(8));
			mMemberMap = DbUp.upTable("mc_login_info").one("login_name", sMobilePhone, "manage_code", MemberConst.MANAGE_CODE_HOMEHAS);
		}
		
		// 用户不存在且用户中心未注册成功
		if(mMemberMap == null){
			mResult.inErrorMessage(918505131, homehasCode);
			return mResult;
		}
		
		//homehas = DbUp.upTable("mc_extend_info_homehas").oneWhere("member_code,homehas_code", "", "", "homehas_code", homehasCode,"member_code",mMemberMap.get("member_code"));
		homehas = DbUp.upTable("mc_extend_info_homehas").oneWhere("member_code,homehas_code", "", "member_code = :member_code and homehas_code != ''", "member_code",mMemberMap.get("member_code"));
		
		// 如果对应的homehas不存在则保存一份
		if(homehas == null){
			homehas = new MDataMap();
			homehas.put("member_code", mMemberMap.get("member_code"));
			homehas.put("homehas_code", homehasCode);
			homehas.put("member_name", custInfo.getCust_nm());
			homehas.put("old_code", "");
			homehas.put("member_sign", "4497467900030001");
			// 70表示是家有员工
			homehas.put("vip_type", "70".equals(custInfo.getCust_lvl_cd()) ? "4497469400050001" : "4497469400050002");
			homehas.put("vip_level", custInfo.getCust_lvl_cd());
			DbUp.upTable("mc_extend_info_homehas").dataInsert(homehas);
		}
		
		mResult.setResultObject(mMemberMap.get("member_code"));
		return mResult;
	}
	
	/**
	 * 根据客代号查询手机号
	 * @param custId
	 * @return 返回  [手机号 , 座机号]
	 */
	public List<String> getPhones(String custId) {
		List<String> resultList = new ArrayList<String>();
		CustInfo custInfo = null;
		// 根据客代号查询对应的手机号
		RsyncCustInfo rsyncCustInfo=new RsyncCustInfo();
		rsyncCustInfo.upRsyncRequest().setCust_id(custId);
		rsyncCustInfo.doRsync();
		if(rsyncCustInfo.getResponseObject()!=null&&rsyncCustInfo.getResponseObject().getResult()!=null&&rsyncCustInfo.getResponseObject().getResult().size()>0){
			// 默认取第一个
			custInfo = rsyncCustInfo.getResponseObject().getResult().get(0);
		}
		
		if(custInfo == null){
			return resultList;
		}
		
		// 取手机号
		String sMobilePhone = StringUtils.trimToEmpty(custInfo.getHp_teld())+StringUtils.trimToEmpty(custInfo.getHp_telh())+StringUtils.trimToEmpty(custInfo.getHp_teln());
		if (StringUtils.isNotEmpty(sMobilePhone)){
			resultList.add(sMobilePhone);
		}
		
		// 取手机号
		sMobilePhone = StringUtils.trimToEmpty(custInfo.getTeld())+StringUtils.trimToEmpty(custInfo.getTelh())+StringUtils.trimToEmpty(custInfo.getTeln());
		if (StringUtils.isNotEmpty(sMobilePhone)){
			resultList.add(sMobilePhone);
		}
		
		return resultList;
	}
	
	/**
	 * 微信解绑接口
	 * @param phone
	 * zb
	 */
	public MWebResult unBindWX(String phone) {
		
		MWebResult result = new MWebResult();
		JSONObject obj = new JSONObject();
		obj.put("type", 13);
		obj.put("phone", phone);
		
		MDataMap dataMap = new MDataMap();
		dataMap.put("api_target", bConfig("groupcenter.unBindWx_Auto_api_target"));
		dataMap.put("api_key", bConfig("groupcenter.checkedUserInfo_api_key"));
		dataMap.put("api_input", obj.toJSONString());
		dataMap.put("api_timespan", FormatHelper.upDateTime());
		dataMap.put("api_project", bConfig("groupcenter.checkedUserInfo_api_project"));
		
		StringBuffer str = new StringBuffer();
		str.append(dataMap.get("api_target"));
		str.append(dataMap.get("api_key"));
		str.append(dataMap.get("api_input"));
		str.append(dataMap.get("api_timespan"));
		str.append(bConfig("groupcenter.checkedUserInfo_api_pass"));
		dataMap.put("api_secret", HexUtil.toHexString(MD5Util.md5(str.toString())));
		
		String sResponseString = null;
		try {
			sResponseString = WebClientSupport.upPost(bConfig("groupcenter.chcekedUserInfo_api_url"), dataMap);
			
			LogFactory.getLog(getClass()).info("[微信解绑]["+phone+"]"+sResponseString);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(0);
			result.setResultMessage(e+"");
			return result;
		}
		return result;
	}
	
	
	
	
}
