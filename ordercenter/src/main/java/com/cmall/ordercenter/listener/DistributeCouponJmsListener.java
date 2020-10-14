package com.cmall.ordercenter.listener;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.MacData;

import com.cmall.ordercenter.service.CouponsService;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapzero.root.RootJmsListenser;

public class DistributeCouponJmsListener extends RootJmsListenser {

	/**
	 * @Description:发放优惠券 
	 * @param sMessage
	 * 发放来源(1:注册送券，2:在线支付下单送，3:在线支付支付送4:在线支付收获送5:货到付款下单送6:货到付款收获送7:推荐人送券) 
	 * @param mDataMap 手机号
	 * @author 张海生 
	 * @date 2015-6-15 下午3:13:08
	 */
	public boolean onReceiveText(String sMessage, MDataMap mDataMap) {
		// boolean ret = true;
		if (StringUtils.isEmpty(sMessage) || mDataMap == null)
			return true;
		try {
			String mobile = mDataMap.get("mobile");
			String manageCode = mDataMap.get("manage_code");
			String bigOrderCode = mDataMap.get("big_order_code");
			String memberCode = mDataMap.get("member_code");
			
			String activityCode = "";
			//兼容用户行为触发发券
			MDataMap activeMap = new MDataMap();
			if(CouponConst.start_up_coupon.equals(sMessage)||CouponConst.add_shop_car_coupon.equals(sMessage)) {
				 activeMap = DbUp.upTable("oc_coupon_relative_behavior").oneWhere("activity_code, manage_code", "", "",
						"relative_type", sMessage, "manage_code", manageCode);
			}else {
				 activeMap = DbUp.upTable("oc_coupon_relative").oneWhere("activity_code, manage_code", "", "",
						"relative_type", sMessage, "manage_code", manageCode);
			}
			
			if (activeMap != null) {
				activityCode = activeMap.get("activity_code");
			}
			
			CouponsService cService = new CouponsService();
			RootResultWeb result = new RootResultWeb();
			
			if (CouponConst.referees__coupon.equals(sMessage)) {
				//推荐送券 为邀请人专门设定的添加方法
				result = cService.distributeCouponsForYQ(activityCode, mobile, "2");
			} else if (CouponConst.pay_coupon.equals(sMessage))  {
				//在线支付支付送
				result = cService.distributeCoupons(activityCode, mobile, bigOrderCode, memberCode, "2");
			} else if (CouponConst.start_up_coupon.equals(sMessage)||CouponConst.add_shop_car_coupon.equals(sMessage)){
				//连续启动|连续加入购物车送
				result = cService.distributeCoupons(activityCode, mobile, "2");
			}else {
				//其他送券
				result = cService.distributeCoupons(activityCode, mobile, "1");
			}
			
			if (result.upFlagTrue() && "7".equals(sMessage)) {
				// 查询领券的用户MemberCode
				//4497469400030002/已发布
				MDataMap memMap = DbUp.upTable("mc_login_info").oneWhere("member_code", "", "", "login_name", mobile, "manage_code", manageCode);
				String sSql = "select coupon_type_code,money from oc_coupon_type where activity_code=:activity_code"
						+ " and status=:status  and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time))";
				List<Map<String, Object>> couponTypeList = DbUp.upTable("oc_coupon_type").dataSqlList(sSql,
						new MDataMap("activity_code", activityCode, "status", "4497469400030002"));
				int total = 0;// 返券金额
				int sum = 0;//返卷数目
				if (couponTypeList != null && couponTypeList.size() > 0) {
					for (Map<String, Object> cmap : couponTypeList) {
						total += Integer.parseInt(cmap.get("money").toString());
					}
					sum =couponTypeList.size();
				}
				if (total > 0) {
					XmasKv.upFactory(EKvSchema.SuperiorTotalCoupon).incrBy(memMap.get("member_code"), total);// 用于记录送券返回的总金额
					XmasKv.upFactory(EKvSchema.SuperiorSumCoupon).incrBy(memMap.get("member_code"), sum);// 用于记录送券返回的总数目
				}
			}
		} catch (Exception e) {
			bLogError(0, "类型" + sMessage + "发券异常");
			WebHelper.errorMessage("OnDistributeCoupon", "distributeCouponError", 1,
					"com.cmall.homepool.listener.DistributeCouponJmsListener",
					JmsNameEnumer.OnDistributeCoupon + WebConst.CONST_SPLIT_LINE + sMessage, e);
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
