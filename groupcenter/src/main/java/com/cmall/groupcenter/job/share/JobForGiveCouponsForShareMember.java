package com.cmall.groupcenter.job.share;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.util.HttpUtil;
import com.cmall.ordercenter.util.CouponUtil;
import com.srnpr.xmassystem.support.PlusSupportLD;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.MoneyHelper;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
* @author Angel Joy
* @Time 2020年6月12日 下午5:04:03 
* @Version 1.0
* <p>Description:</p>
*/
public class JobForGiveCouponsForShareMember extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();
		String sql = "SELECT * FROM familyhas.fh_share_profit_setting limit 1";
		Map<String,Object> settings = DbUp.upTable("fh_share_profit_setting").dataSqlOne(sql, new MDataMap());
		if(settings == null) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("活动已过期！");
			return mWebResult;
		}
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",sInfo);
		if(orderInfo == null || orderInfo.isEmpty()) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("订单异常！");
			return mWebResult;
		}
		String small_seller_code = orderInfo.get("small_seller_code");
		String activity_code = MapUtils.getString(settings, "share_activity_code","");
		if(StringUtils.isEmpty(activity_code)) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("暂无活动！");
			return mWebResult;
		}
		String sqlCouponType = "SELECT * FROM ordercenter.oc_coupon_type WHERE activity_code = :activity_code limit 1";
		Map<String,Object> couponType = DbUp.upTable("oc_coupon_type").dataSqlOne(sqlCouponType, new MDataMap("activity_code",activity_code));
		if(couponType == null || couponType.isEmpty()) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("暂无优惠券类型！");
			return mWebResult;
		}
		String couponTypeCode = MapUtils.getString(couponType, "coupon_type_code","");
		if(StringUtils.isEmpty(couponTypeCode)) {
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("优惠券类型错误！");
			return mWebResult;
		}
		boolean asaleCount = true;
		String sSql = "SELECT * FROM familyhas.fh_share_order_detail where order_code = :orderCode";
		List<Map<String,Object>> orderDetails  = DbUp.upTable("fh_share_order_detail").dataSqlList(sSql, new MDataMap("orderCode",sInfo));
		if("SI2003".equals(small_seller_code)) {//LD 商品
			for(Map<String,Object> map : orderDetails) {
				Integer count = this.getAllowCount(sInfo, MapUtils.getString(map, "sku_code",""));
				if(MapUtils.getInteger(map, "sku_num", 0) != count) {
					asaleCount  = false;
				}
			}
		}else {
			String sqlCount = "SELECT COUNT(1) num FROM ordercenter.oc_order_after_sale WHERE order_code = '"+sInfo+"' AND asale_type = '4497477800030001' AND asale_status != '4497477800050004' AND asale_status != '4497477800050007' AND asale_status != '4497477800050011'";
			Map<String,Object> map = DbUp.upTable("oc_order_after_sale").dataSqlOne(sqlCount, new MDataMap());
			Integer count = MapUtils.getInteger(map, "num");
			if(count > 0) {
				asaleCount = false;
			}
		}
		if(asaleCount) {//没有申请过退货的才会赋予优惠券
			for(Map<String,Object> map : orderDetails) {
				BigDecimal profitMoney = new BigDecimal(MapUtils.getString(map, "profit_money", "0")); 
				BigDecimal rate = new BigDecimal(MapUtils.getString(settings, "share_rate", "0"));
				BigDecimal money = MoneyHelper.round(0, BigDecimal.ROUND_FLOOR, profitMoney.divide(new BigDecimal("100")).multiply(rate));
				RootResult rootResult = new CouponUtil().provideCouponForShare(MapUtils.getString(map, "share_member_code",""), couponTypeCode, "0", "","",MapUtils.getInteger(map, "sku_num", 0),money.toString());
				mWebResult.setResultCode(rootResult.getResultCode());
				mWebResult.setResultMessage(rootResult.getResultMessage());
			}
		}
		return mWebResult;
	}

	@Override
	public ConfigJobExec getConfig() {
		return config;
	}
	
	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990033");
	}
	
	/**
	 * 获取颜色款式获取LD允许售后数量
	 * @param order_code
	 * @param sku_code
	 * @return
	 */
	private Integer getAllowCount(String order_code, String sku_code) {
		PlusSupportLD ld = new PlusSupportLD();
		String isSyncLd = ld.upSyncLdOrder();
		if("N".equals(isSyncLd)){//关闭状态
			return 0;
		}
		MDataMap mapOrder = DbUp.upTable("oc_orderinfo").one("order_code",order_code);
		MDataMap goodOrder = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
		Integer ordId = 0,goodId=0,colorId=0,styleId=0;
		if(mapOrder != null && !mapOrder.isEmpty()){
			String out_order_code = mapOrder.get("out_order_code");
			if(!StringUtils.isEmpty(out_order_code)){
				ordId = Integer.parseInt(out_order_code);
			}
		}
		if(goodOrder != null && !goodOrder.isEmpty()){
			String product_code = goodOrder.get("product_code");
			if(!StringUtils.isEmpty(product_code)){
				goodId = Integer.parseInt(product_code);
			}
			String sku_key = goodOrder.get("sku_key");
			colorId = Integer.parseInt(sku_key.split("&")[0].replace("color_id=", ""));
			styleId = Integer.parseInt(sku_key.split("&")[1].replace("style_id=", ""));
		}
		if(ordId == 0){
			return 0;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ordId", ordId);
		params.put("goodId", goodId);
		params.put("colorId", colorId);
		params.put("styleId", styleId);
		String result = HttpUtil.post(bConfig("groupcenter.rsync_homehas_url")+"getCancelGoodCnt", JSONObject.toJSONString(params), "UTF-8");
		JSONObject jo = JSONObject.parseObject(result);
		Integer skuCount = 0;
		if(jo != null && jo.getInteger("code") == 0){
			skuCount = jo.getInteger("result");
		}
		return skuCount;
	}

}
