package com.cmall.groupcenter.homehas;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseInnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseInnerCustInfo.InnerCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInputHomehas;
import com.cmall.membercenter.txservice.TxMemberForHomeHas;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步内部会员基本信息[惠家有版]
 * @author jlin
 *
 */
public class RsyncInnerCustInfo_h extends RsyncHomeHas<RsyncConfigInnerCustInfo, RsyncRequestInnerCustInfo, RsyncResponseInnerCustInfo> {

	final static RsyncConfigInnerCustInfo CONFIG_INNER_CUST_INFO = new RsyncConfigInnerCustInfo();
	
	public RsyncConfigInnerCustInfo upConfig() {
		return CONFIG_INNER_CUST_INFO;
	}

	public RsyncRequestInnerCustInfo upRsyncRequest() {
		RsyncRequestInnerCustInfo requestInnerCustInfo = new RsyncRequestInnerCustInfo();
		return requestInnerCustInfo;
	}

	private TxMemberForHomeHas txMemberForHomeHas = null;
	
	
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
		
		if (mWebResult.upFlagTrue()) {
			
			MDataMap mMemberMap = DbUp.upTable("mc_login_info").one("login_name", sMobilePhone, "manage_code",MemberConst.MANAGE_CODE_HOMEHAS);
			if (mMemberMap == null) {
				
				MLoginInputHomehas mLoginInput = new MLoginInputHomehas();

				mLoginInput.setHomeHasCode(custInfo.getCust_id());
				mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
				mLoginInput.setLoginName(sMobilePhone);
				mLoginInput.setLoginPassword(sMobilePhone.substring(5));
				mLoginInput.setManageCode(MemberConst.MANAGE_CODE_HOMEHAS);
				mLoginInput.setMemberName(custInfo.getCust_nm());
				
				// 创建会员
				txMemberForHomeHas.createMemberInfo(mLoginInput);
				
			}
			
			DbUp.upTable("mc_extend_info_homehas").dataUpdate(new MDataMap("homehas_code",custInfo.getCust_id(),"vip_type","4497469400050001"), "vip_type", "homehas_code");
			
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

			txMemberForHomeHas = BeansHelper.upBean("bean_com_cmall_membercenter_txservice_TxMemberForHomeHas");

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
