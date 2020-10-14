package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 
 * 增加定时任务，每天凌晨1点执行，捞取当天活动的所有商品，
 * 与用户收藏的价格和last_push_sku_price字段进行对比，
 * 活动价低于收藏价格和last_push_sku_price价时，数据插入推送任务表
 *
 */
public class JobPushPriceTable extends RootJob {
	public void doExecute(JobExecutionContext context) {
		MDataMap mWhereMap = new MDataMap();
		String nowString = FormatHelper.upDateTime(DateUtil.DATE_FORMAT_DATETIME);
		mWhereMap.put("now", nowString);
		String sSql = "SELECT DISTINCT product_code FROM sc_event_info s INNER JOIN sc_event_item_product sp WHERE s.flag_enable = 1 AND :now > s.begin_time AND :now < s.end_time AND s.event_status = '4497472700020002' AND s.event_code = sp.event_code AND s.event_type_code in ('4497472600010001','4497472600010002','4497472600010005','4497472600010019','4497472600010024')";
		List<Map<String,Object>> dataSqlList = DbUp.upTable("sc_event_info").dataSqlList(sSql, mWhereMap);
		HashSet<String> set = new HashSet<String>();
		for(Map<String,Object> map : dataSqlList) {
			String product_code = MapUtils.getString(map, "product_code");
			ProductPriceService productPriceService = new ProductPriceService();
			PlusModelSkuQuery plusModelSkuQuery = new PlusModelSkuQuery();
			plusModelSkuQuery.setCode(product_code);
			Map<String, BigDecimal> productMinPrice = productPriceService.getProductMinPrice(plusModelSkuQuery);
			BigDecimal bigDecimal = productMinPrice.get(product_code);
			
			float sku_price = bigDecimal.floatValue();
			mWhereMap.clear();
			mWhereMap.put("product_code", product_code);
			mWhereMap.put("sku_price", String.valueOf(sku_price));
			String sql = "SELECT DISTINCT member_code FROM fh_product_collection fhp WHERE fhp.product_code =:product_code AND LEAST(IF(fhp.last_push_sku_price = 0,999999,fhp.last_push_sku_price),IF(fhp.sku_price = 0,999999,fhp.sku_price)) > :sku_price";
			List<Map<String,Object>> dataSqlList1 = DbUp.upTable("fh_product_collection").dataSqlList(sql, mWhereMap);
			for(Map<String,Object> map2 : dataSqlList1) {
				String member_code = MapUtils.getString(map2, "member_code");
				mWhereMap.clear();
				mWhereMap.put("last_push_sku_price", String.valueOf(sku_price));
				mWhereMap.put("product_code", product_code);
				mWhereMap.put("member_code", member_code);
				DbUp.upTable("fh_product_collection").dataUpdate(mWhereMap, "last_push_sku_price", "product_code,member_code");
				if(!set.contains(member_code)) {
					set.add(member_code);
					mWhereMap.clear();
					mWhereMap.put("member_code", member_code);
					mWhereMap.put("push_content", "你关注的商品已降价了请查看");
					mWhereMap.put("create_time", nowString);
					mWhereMap.put("push_status", "0");
					DbUp.upTable("fh_collection_push").dataInsert(mWhereMap);
				}
			}
		}			
	}
}
