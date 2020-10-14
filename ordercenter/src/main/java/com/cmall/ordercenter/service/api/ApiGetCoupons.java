package com.cmall.ordercenter.service.api;

import java.math.BigDecimal;

import com.cmall.ordercenter.model.ApiGetCouponsResult;
import com.cmall.ordercenter.model.api.ApiGetCouponsInput;
import com.cmall.ordercenter.util.CouponUtil;
import com.srnpr.xmassystem.load.LoadCouponGetUser;
import com.srnpr.xmassystem.modelbean.CouponGetUserInfo;
import com.srnpr.xmassystem.modelbean.CouponGetUserQuery;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MOauthInfo;

/**
 * 领取优惠券
 *@author zhouenzhi 
 * 
 */
public class ApiGetCoupons extends RootApiForToken<ApiGetCouponsResult, ApiGetCouponsInput> {

	public ApiGetCouponsResult Process(ApiGetCouponsInput input,
			MDataMap mRequestMap) {
		ApiGetCouponsResult result = new ApiGetCouponsResult();
		MOauthInfo oauth = getOauthInfo();
		String memberCode = oauth.getUserCode();
		String couponTypeCode = input.getCouponTypeCode();
		String uid = input.getUid();//防止恶意刷优惠券
		//先校验优惠券是否存在，通过uid跟couponTypeCode查询优惠券是否存在。
		CouponUtil couponUtil = new CouponUtil();
		boolean flagCoupon = couponUtil.checkCouponExist(uid,couponTypeCode);//校验入参优惠券是否合法，防止恶意刷单
		if(!flagCoupon) {//入参优惠券不合法，有可能是恶意刷单
			result.setResultCode(0);
			result.setResultMessage("优惠券不存在！！！，请联系客服");
			return result;
		}
		
		RootResult re = couponUtil.provideCoupon(memberCode, couponTypeCode, "0", "","",1);
		result.setResultCode(re.getResultCode());
		result.setResultMessage(re.getResultMessage());
		if(result.getResultCode() == 1) {
			CouponGetUserQuery userQuery = new CouponGetUserQuery();
			userQuery.setCode(memberCode+"-"+couponTypeCode);
			CouponGetUserInfo userInfo = new LoadCouponGetUser().upInfoByCode(userQuery);
			result.setEndTime(userInfo.getEndTime());
			result.setResultMessage("领取成功");
		}
		//返回添加时间，小程序分销券倒计时使用
		result.setSystemTime(com.cmall.systemcenter.common.DateUtil.getSysDateTimeString());
		return result;
	}
}