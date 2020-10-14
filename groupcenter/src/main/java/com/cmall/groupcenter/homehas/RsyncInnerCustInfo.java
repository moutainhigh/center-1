package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseInnerCustInfo.InnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 按时间同步内部会员基本信息
 * @author jlin
 *
 */
public class RsyncInnerCustInfo extends RsyncHomeHas<RsyncConfigInnerCustInfo, RsyncRequestInnerCustInfo, RsyncResponseInnerCustInfo> {

	final static RsyncConfigInnerCustInfo CONFIG_INNER_CUST_INFO = new RsyncConfigInnerCustInfo();
	
	public RsyncConfigInnerCustInfo upConfig() {
		return CONFIG_INNER_CUST_INFO;
	}

	public RsyncRequestInnerCustInfo upRsyncRequest() {
		RsyncRequestInnerCustInfo requestInnerCustInfo = new RsyncRequestInnerCustInfo();
		return requestInnerCustInfo;
	}

	private MWebResult reginInnerCustInfo(InnerCustInfo custInfo) {
		
		MWebResult mWebResult = new MWebResult();
		
		
		// 定义手机号码
		String sMobilePhone = "";

		// 取出家有的用户手机号
		if (StringUtils.isNotEmpty(custInfo.getHp_teld())) {
			sMobilePhone = custInfo.getHp_teld() + custInfo.getHp_telh()+ custInfo.getHp_teln();
		}
		// 判断如果手机号为空 则尝试以电话号码作为手机号码的标记 以兼容家有信息中的错误
		if (StringUtils.isEmpty(sMobilePhone)) {
			if (StringUtils.isNotEmpty(custInfo.getTeld())){
				sMobilePhone = custInfo.getTeld() + custInfo.getTelh()+ custInfo.getTeln();
			}
		}
		
		// 判断手机号非空
		if (mWebResult.upFlagTrue()) {
			if (StringUtils.isEmpty(sMobilePhone)) {
				mWebResult.inErrorMessage(918505101, custInfo.getCust_id());
			}
		}
		
		// 判断手机号格式是否正确
		if (mWebResult.upFlagTrue()) {
			if (!RegexHelper.checkRegexField(sMobilePhone,RegexConst.MOBILE_PHONE)) {
				mWebResult.inErrorMessage(918505104, custInfo.getCust_id(),sMobilePhone);
			}
		}
		
		if (!mWebResult.upFlagTrue()){
			return mWebResult;
		}
		
		// 不再取接口返回的用户编号，避免LD系统手机号绑定的用户编号和惠家有系统里面手机号对应的用户编号不一致时出现的客代号绑定错误问题
		// String memberCode = custInfo.getWeb_id();
		String memberCode = null; 
		
		// 用户编号以惠家有系统里面最新的为准
		if(StringUtils.isBlank(memberCode)){
			MDataMap loginInfoMap = DbUp.upTable("mc_login_info").oneWhere("member_code", "", "", "login_name",sMobilePhone, "manage_code", "SI2003");
			if(loginInfoMap != null){
				memberCode = loginInfoMap.get("member_code");
			}
		}
		
		// 用户不存在则注册一个
		if(StringUtils.isBlank(memberCode)){
			new HomehasSupport().register(sMobilePhone, RandomStringUtils.randomNumeric(8));
			MDataMap loginInfoMap = DbUp.upTable("mc_login_info").oneWhere("member_code", "", "", "login_name",sMobilePhone, "manage_code", "SI2003");
			if(loginInfoMap != null){
				memberCode = loginInfoMap.get("member_code");
			}
		}
		
		if(StringUtils.isNotBlank(memberCode)){
			// 更新SI2003的用户信息
			MDataMap homehas = DbUp.upTable("mc_extend_info_homehas").oneWhere("", "", "", "homehas_code", custInfo.getCust_id(), "member_code", memberCode);
			
			// 如果对应的homehas不存在则保存一份
			if(homehas == null){
				String sql = "select count(*) from mc_extend_info_homehas where member_code = '"+memberCode+"' and homehas_code != ''";
				int num = DbUp.upTable("mc_extend_info_homehas").upTemplate().queryForObject(sql, new HashMap<String, String>(), Integer.class);
				
				// 如果存在绑定的有其他客代号则先删除旧数据
				if(num > 0){
					DbUp.upTable("mc_extend_info_homehas").delete("member_code", memberCode);
				}
				
				// 插入最新的客户代号绑定信息
				homehas = new MDataMap();
				homehas.put("member_code", memberCode);
				homehas.put("homehas_code", custInfo.getCust_id());
				homehas.put("member_name", StringUtils.trimToEmpty(custInfo.getCust_nm()));
				homehas.put("old_code", "");
				homehas.put("member_sign", "4497467900030001");
				homehas.put("vip_type", "4497469400050001");
				homehas.put("vip_level", custInfo.getCust_lvl_cd());
				DbUp.upTable("mc_extend_info_homehas").dataInsert(homehas);
				
				XmasKv.upFactory(EKvSchema.UserMemberCode).del(memberCode);
			}else if(!custInfo.getCust_lvl_cd().equals(homehas.get("vip_level"))
					|| !"4497469400050001".equals(homehas.get("vip_type"))
					|| !StringUtils.trimToEmpty(custInfo.getCust_nm()).equals(homehas.get("member_name"))){
				//用户等级与原有用户等级不相同则进行修改
				homehas.put("vip_type", "4497469400050001");
				homehas.put("vip_level", custInfo.getCust_lvl_cd());
				homehas.put("member_name", StringUtils.trimToEmpty(custInfo.getCust_nm()));
				DbUp.upTable("mc_extend_info_homehas").update(homehas);
				
				XmasKv.upFactory(EKvSchema.UserMemberCode).del(memberCode);
			}
		}
		
		// 更新SI2009的用户信息，如果没有则忽略（兼容旧网站）
		MDataMap loginInfoMap = DbUp.upTable("mc_login_info").oneWhere("member_code", "", "", "login_name",sMobilePhone, "manage_code", "SI2009");
		if(loginInfoMap != null){
			MDataMap homePool = DbUp.upTable("mc_extend_info_homepool").one("member_code", loginInfoMap.get("member_code"));
			if(homePool != null && !"4497469400050001".equals(homePool.get("vip_type"))){
				homePool.put("vip_type", "4497469400050001");
				DbUp.upTable("mc_extend_info_homepool").update(homePool);
				
				XmasKv.upFactory(EKvSchema.UserMemberCode).del(loginInfoMap.get("member_code"));
			}
		}
		
		return mWebResult;
	}
	
	public RsyncResult doProcess(RsyncRequestInnerCustInfo tRequest,RsyncResponseInnerCustInfo tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;
		
		if (tResponse != null && tResponse.getResult() != null) {
			result.setProcessNum(tResponse.getResult().size());
		} else {
			result.setProcessNum(0);
		}
		
		// 判断有需要处理的数据才开始处理
		if (result.getProcessNum() > 0) {

			// 设置预期处理数量
			result.setProcessNum(tResponse.getResult().size());

			for (InnerCustInfo custInfo : tResponse.getResult()) {
				
				String lock_uid=WebHelper.addLock(1000*60, custInfo.getCust_id());
				MWebResult mResult = reginInnerCustInfo(custInfo);
				WebHelper.unLock(lock_uid);
				
				// 如果成功则将成功计数加1
				if (mResult.upFlagTrue()) {
					iSuccessSum++;

				} else {
					if (result.getResultList() == null) {
						result.setResultList(new ArrayList<Object>());
					}
					result.getResultList().add(mResult.getResultMessage());
				}
			}

			result.setProcessData(bInfo(918501102, result.getProcessNum(),iSuccessSum, result.getProcessNum() - iSuccessSum));

		}
		
		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
		}

		return result;

	}

	public RsyncResponseInnerCustInfo upResponseObject() {
		return new RsyncResponseInnerCustInfo();
	}
	
}
