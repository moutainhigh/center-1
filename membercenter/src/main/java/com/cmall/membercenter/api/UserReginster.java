package com.cmall.membercenter.api;

import com.cmall.membercenter.model.MemberResult;
import com.cmall.membercenter.model.UserRegInput;
import com.cmall.membercenter.model.UserRegResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.membercenter.txservice.TxMemberForStar;
import com.cmall.membercenter.txservice.TxMemberService;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 用户注册接口
 * 
 * @author srnpr
 * 
 */
public class UserReginster extends
		RootApiForManage<UserRegResult, UserRegInput> {

	public UserRegResult Process(UserRegInput inputParam, MDataMap mRequestMap) {

		UserRegResult userRegResult = new UserRegResult();

		/* 判断昵称是否存在 */
		if (userRegResult.upFlagTrue()) {

			ScoredSupport scoredSupport = new ScoredSupport();

			userRegResult.inOtherResult(scoredSupport.existsNickName(inputParam
					.getNickname()));

		}

		/* 判断昵称是否包含敏感词 （惠美丽） */
		if (userRegResult.upFlagTrue()) {

			if (getManageCode().equals("SI2007")
					|| getManageCode().equals("SI2013")) {

				ScoredSupport scoredSupport = new ScoredSupport();

				userRegResult.inOtherResult(scoredSupport
						.existsSensitiveWord(inputParam.getNickname()));
			}
		}

		// 判断验证码对不对
		if (userRegResult.upFlagTrue()) {

			VerifySupport verifySupport = new VerifySupport();
			userRegResult.inOtherResult(verifySupport.checkVerifyCodeByType(
					EVerifyCodeTypeEnumer.MemberReginster,
					inputParam.getLogin_name(), inputParam.getVerify_code())

			);

		}

		if (userRegResult.upFlagTrue()) {

			MDataMap mGroupMap = DbUp.upTable("mc_login_info").one(
					"login_name", inputParam.getLogin_name());

			if (mGroupMap != null) {

				userRegResult.inErrorMessage(934105143);
			}

		}

		// 开始调用注册流程
		if (userRegResult.upFlagTrue()) {
			TxMemberForStar memberService = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberForStar");

			userRegResult = memberService.insertUserReg(inputParam,
					getManageCode());
			// 设置相关信息
			if (userRegResult.upFlagTrue()) {

				MemberResult memberResult = new MemberLoginSupport()
						.upMemberInfo(userRegResult.getUser().getMember_code());

				userRegResult.setUser(memberResult.getUser());

				userRegResult.setConfig(memberResult.getConfig());

			}
		}
		return userRegResult;

	}
}
