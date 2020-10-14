package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 外呼衍生单逻辑处理
 */
public class JobForWaihuOrderOther extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();
		// 下单人
		String buyerCode = (String)DbUp.upTable("oc_orderinfo_upper").dataGet("buyer_code", "", new MDataMap("big_order_code", sInfo));
		// 绑定的外呼人编号
		MDataMap bindMap = DbUp.upTable("fh_waihu_bind").oneWhere("waihu_code", "", "member_code = :member_code AND expired_time > now()", "member_code", buyerCode);
		// 忽略没有绑定关系的
		if(bindMap == null) {
			return mWebResult;
		}
		
		String waihuCode = bindMap.get("waihu_code");
		String sSql = "SELECT d.order_code,d.product_code,d.sku_code,o.create_time FROM oc_orderinfo o,oc_orderdetail d WHERE o.big_order_code = :big_order_code AND o.order_code = d.order_code AND d.gift_flag = '1'";
		List<Map<String, Object>> detailList = DbUp.upTable("oc_orderinfo").dataSqlList(sSql, new MDataMap("big_order_code", sInfo));
		
		String orderCode,productCode,skuCode,createTime;
		for(Map<String, Object> detail : detailList) {
			orderCode = detail.get("order_code").toString();
			productCode = detail.get("product_code").toString();
			skuCode = detail.get("sku_code").toString();
			createTime = detail.get("create_time").toString();
			
			// 忽略外呼订单和直播商品
			if(isWaihuOrder(orderCode, skuCode)
					||isTv(productCode,createTime)) {
				continue;
			}
			
			// 忽略衍生订单表是否已经存在的数据
			if(DbUp.upTable("fh_waihu_order_detail_other").count("order_code",orderCode,"sku_code",skuCode) > 0) {
				continue;
			}
			
			DbUp.upTable("fh_waihu_order_detail_other").insert(
					"member_code", waihuCode,
					"order_code", orderCode,
					"sku_code", skuCode,
					"create_time", FormatHelper.upDateTime()
					);
		}
		
		return mWebResult;
	}
	
	/**
	 * 查询是否已经被记录为外呼推广订单
	 */
	private boolean isWaihuOrder(String orderCode, String skuCode) {
		return DbUp.upTable("fh_waihu_order_detail").count("order_code", orderCode, "sku_code", skuCode) > 0;
		
	}
	
	/**
	 * 查询商品是否是直播商品
	 */
	private boolean isTv(String productCode, String day) {
		// 商品编号大于6位的都默认为商户品
		if(productCode.length() > 6) {
			return false;
		}
		
		return DbUp.upTable("pc_tv").oneWhere("zid", "", "good_id = :good_id AND so_id = '1000001' AND form_fr_date like :day", "good_id", productCode,"day",day+"%") != null;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990045");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}
	
	
}