package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetCustByDate;
import com.cmall.groupcenter.homehas.model.RsyncModelCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetCustByDate;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetCustByDate;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步一定时间范围内的会员信息
 * 
 * @author srnpr
 * 
 */
public class RsyncGetCustByDate
		extends
		RsyncHomeHas<RsyncConfigGetCustByDate, RsyncRequestGetCustByDate, RsyncResponseGetCustByDate> {

	final static RsyncConfigGetCustByDate CONFIG_GET_CUST_BY_DATE = new RsyncConfigGetCustByDate();

	public RsyncConfigGetCustByDate upConfig() {
		return CONFIG_GET_CUST_BY_DATE;
	}

	public RsyncRequestGetCustByDate upRsyncRequest() {

		RsyncRequestGetCustByDate requestGetCustByDate = new RsyncRequestGetCustByDate();

		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		requestGetCustByDate.setStart_day(rsyncDateCheck.getStartDate());
		requestGetCustByDate.setEnd_day(rsyncDateCheck.getEndDate());

		return requestGetCustByDate;

	}
	
	/**
	 * 注册用户
	 * 
	 * @param custInfo
	 * @return
	 */
	private MWebResult reginUser(RsyncModelCustInfo custInfo) {

		MWebResult mWebResult = new MWebResult();

		// 定义手机号码
		String sMobilePhone = "";

		// 取出家有的用户手机号
		if (StringUtils.isNotEmpty(custInfo.getHp_teld())) {
			sMobilePhone = custInfo.getHp_teld() + custInfo.getHp_telh()
					+ custInfo.getHp_teln();
		}
		// 判断如果手机号为空 则尝试以电话号码作为手机号码的标记 以兼容家有信息中的错误
		if (StringUtils.isEmpty(sMobilePhone)) {
			if (StringUtils.isNotEmpty(custInfo.getTeld()))
				sMobilePhone = custInfo.getTeld() + custInfo.getTelh()
						+ custInfo.getTeln();

		}

		// 判断手机号非空
		if (mWebResult.upFlagTrue()) {
			if (StringUtils.isEmpty(sMobilePhone)) {
				mWebResult.inErrorMessage(918505101, custInfo.getCust_id());
			}
		}

		// 判断手机号格式是否正确
		if (mWebResult.upFlagTrue()) {
			if (!RegexHelper.checkRegexField(sMobilePhone,
					RegexConst.MOBILE_PHONE)) {
				mWebResult.inErrorMessage(918505104, custInfo.getCust_id(),
						sMobilePhone);
			}
		}

		if (mWebResult.upFlagTrue()) {
			String memberCode = custInfo.getWeb_id();
			if(StringUtils.isBlank(memberCode)){
				MDataMap mMemberMap = DbUp.upTable("mc_login_info").one("login_name", sMobilePhone, "manage_code",MemberConst.MANAGE_CODE_HOMEHAS);
				if(mMemberMap != null){
					memberCode = mMemberMap.get("member_code");
				}
			}
			
			if(StringUtils.isNotBlank(memberCode)){
				MDataMap mExtendMap = DbUp.upTable("mc_extend_info_homehas").oneWhere("","","","member_code",memberCode,"homehas_code",custInfo.getCust_id());
				if(mExtendMap == null){
					String sql = "select count(*) from mc_extend_info_homehas where member_code = '"+memberCode+"' and homehas_code != ''";
					int num = DbUp.upTable("mc_extend_info_homehas").upTemplate().queryForObject(sql, new HashMap<String, String>(), Integer.class);
					
					// 不存在任何可用客代号的情况下才保存一下
					if(num == 0){
						// 保存客代号到数据库
						mExtendMap = DbUp.upTable("mc_extend_info_homehas").one("member_code",memberCode,"homehas_code","");
						
						if(mExtendMap != null){
							// 如果用户编号对应的记录存在但是客代号是空则重用此条记录，更新为正确的客代号
							mExtendMap.put("homehas_code", custInfo.getCust_id());
							mExtendMap.put("vip_type", "70".equals(custInfo.getCust_lvl_cd()) ? "4497469400050001" : "4497469400050002");
							mExtendMap.put("vip_level", custInfo.getCust_lvl_cd());
							DbUp.upTable("mc_extend_info_homehas").dataUpdate(mExtendMap, "homehas_code,vip_type,vip_level", "zid");
							XmasKv.upFactory(EKvSchema.UserMemberCode).del(memberCode);
						}else{
							// 否则新增一条新的客代号记录
							mExtendMap = new MDataMap();
							mExtendMap.put("member_code", memberCode);
							mExtendMap.put("homehas_code", custInfo.getCust_id());
							mExtendMap.put("member_name", StringUtils.trimToEmpty(custInfo.getCust_nm()));
							mExtendMap.put("vip_level", custInfo.getCust_lvl_cd());
							DbUp.upTable("mc_extend_info_homehas").dataInsert(mExtendMap);
						}
					}
				}else{ 
					String updateFields = "";
					if (!mExtendMap.get("vip_level").equals(custInfo.getCust_lvl_cd())){
						mExtendMap.put("vip_level", custInfo.getCust_lvl_cd());
						updateFields = "vip_level";
					}
					
					if (StringUtils.isNotBlank(custInfo.getCust_nm()) && !custInfo.getCust_nm().equals(mExtendMap.get("member_name"))){
						mExtendMap.put("member_name", custInfo.getCust_nm());
						if(!updateFields.isEmpty()){
							updateFields += ",";
						}
						updateFields += "member_name";
					}
					
					if(!updateFields.isEmpty()){
						DbUp.upTable("mc_extend_info_homehas").dataUpdate(mExtendMap, updateFields, "zid");
					}
				}
			}
			
			DbUp.upTable("mc_extend_info_homepool").dataExec("update mc_extend_info_homepool set old_code=:old_code where mobile=:mobile and old_code=''", new MDataMap("mobile",sMobilePhone,"old_code",custInfo.getCust_id()));
			//此处处理家有汇的扩展信息 规则：LD同步过来的信息的手机号码与我们相匹配
		//	DbUp.upTable("mc_extend_info_star").dataExec("update mc_extend_info_star set old_code=:old_code where mobile_phone=:mobile and old_code=''", new MDataMap("mobile",sMobilePhone,"old_code",custInfo.getCust_id()));
		}

		/*
		 * MLoginInputHomehas mLoginInput = new MLoginInputHomehas();
		 * 
		 * return txMemberForHomeHas.createMemberInfo(mLoginInput);
		 */

		return mWebResult;

	}

	public RsyncResult doProcess(RsyncRequestGetCustByDate tRequest,
			RsyncResponseGetCustByDate tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				// 设置预期处理数量
				result.setProcessNum(tResponse.getResult().size());

				for (RsyncModelCustInfo custInfo : tResponse.getResult()) {
					MWebResult mResult = reginUser(custInfo);

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

				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));

			}

		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			result.setStatusData(tRequest.getEnd_day());
		}

		return result;

	}

	public RsyncResponseGetCustByDate upResponseObject() {

		return new RsyncResponseGetCustByDate();
	}
}
