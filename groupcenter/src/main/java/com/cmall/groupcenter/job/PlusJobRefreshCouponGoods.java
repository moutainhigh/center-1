package com.cmall.groupcenter.job;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.load.LoadCouponType;
import com.srnpr.xmassystem.modelevent.PlusModelCouponType;
import com.srnpr.xmassystem.modelevent.PlusModelCouponType.CouponTypeLimit;
import com.srnpr.xmassystem.plusquery.PlusModelQuery;
import com.srnpr.xmassystem.top.PlusConfigScheduler;
import com.srnpr.xmassystem.top.PlusTopScheduler;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.GsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webface.IKvSchedulerConfig;

/**
 * 刷新优惠券限定的商品的solr索引
 * @remark
 * @author 任宏斌
 * @date 2019年12月30日
 */
public class PlusJobRefreshCouponGoods extends PlusTopScheduler {

	public IBaseResult execByInfo(String sInfo) {

		String couponTypeCode = new GsonHelper().fromJson(sInfo, new String());
		
		PlusModelCouponType plusModelCouponType = new LoadCouponType().upInfoByCode(new PlusModelQuery(couponTypeCode));
		if("4497471600070002".equals(plusModelCouponType.getLimitCondition())) {
			CouponTypeLimit couponTypeLimit = plusModelCouponType.getCouponTypeLimit();
			
			if("4497471600070002".equals(couponTypeLimit.getProductLimit())
					&& couponTypeLimit.getExceptProduct() == 0) {
				String productCodes = couponTypeLimit.getProductCodes();
				
				if(StringUtils.isNotEmpty(productCodes)) {
					MDataMap dataMap = new MDataMap();
					dataMap.put("productCode", productCodes);
					try {
						
						WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), dataMap);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		RootResultWeb result = new RootResultWeb();

		return result;
	}

	private final static PlusConfigScheduler plusConfigScheduler = new PlusConfigScheduler(
			EPlusScheduler.UpdateCouponGoods);

	public IKvSchedulerConfig getConfig() {
		return plusConfigScheduler;
	}
}
