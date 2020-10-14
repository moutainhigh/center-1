package com.cmall.groupcenter.coupon.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.model.AccountCouponInput;
import com.cmall.groupcenter.model.AccountCouponListResult;
import com.cmall.groupcenter.service.CouponInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetUserCoupon  extends RootApiForToken<AccountCouponListResult,AccountCouponInput>{

	@Override
	public AccountCouponListResult Process(AccountCouponInput inputParam, MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		CouponInfoService couponInfoService = new CouponInfoService();
		String mobile = getOauthInfo().getLoginName();
		
		if(StringUtils.isNotBlank(inputParam.getCouponId())){//针对单一优惠卷的操作
			if("remove".equals(inputParam.getOperating())){//删除操作
				return couponInfoService.removeCoupon(inputParam.getCouponId(),accountCode);
			}else{//查询操作
				return couponInfoService.searchCoupon(inputParam.getCouponId());
			}
		}else{//优惠卷链表
			return couponInfoService.searchCoupons(accountCode,inputParam.getType(),inputParam.getPageOption());
		}
	}

}
