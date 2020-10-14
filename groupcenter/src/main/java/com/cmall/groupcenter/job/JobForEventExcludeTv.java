package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadSkuInfo;
import com.srnpr.xmassystem.load.LoadSkuItem;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每天下午7点定时排查直播品
 */
public class JobForEventExcludeTv extends RootJob {
	
	LoadSkuInfo loadSkuInfo = new LoadSkuInfo();
	LoadSkuItem loadSkuItem = new LoadSkuItem();
	
	public void doExecute(JobExecutionContext context) {
		disable();
	}
	
	/**
	 * 作废当天活动的直播品
	 */
	private void disable() {
		// 查询第二天的直播品，只查询1台
		List<String> tvProductList = getTvProductList();
		if(tvProductList.isEmpty()) {
			return;
		}
		
		// 查询已经发布的活动中包含了直播品的数据
		String codes = "'" + StringUtils.join(tvProductList,"','") + "'";
		String tomorrow = FormatHelper.upDateTime(DateUtils.addDays(new Date(), 1),"yyyy-MM-dd");
		// 查询明天参与活动的商品
		String sql = "SELECT p.event_code, p.item_code, p.product_code, p.sku_code FROM systemcenter.sc_event_info e,systemcenter.sc_event_item_product p"
				+ " WHERE e.event_status = '4497472700020002' AND p.event_code = e.event_code AND p.flag_enable = 1 AND e.exclude_tv_flag = '449748600002'"
				+ " AND e.event_type_code IN('4497472600010001','4497472600010002','4497472600010005','4497472600010024','4497472600010030')"
				+ " AND ((e.begin_time <= :tomorrow AND e.end_time > :tomorrow) OR e.begin_time LIKE '"+tomorrow+"%')"
				+ " AND p.product_code IN(" + codes + ")";
		
		List<Map<String, Object>> itemMapList = DbUp.upTable("sc_event_info").dataSqlList(sql, new MDataMap("tomorrow", tomorrow));
		
		String skuCode,productCode,eventCode,itemCode,time;
		for(Map<String, Object> itemMap : itemMapList) {
			eventCode = itemMap.get("event_code").toString();
			itemCode = itemMap.get("item_code").toString();
			productCode = itemMap.get("product_code").toString();
			skuCode = itemMap.get("sku_code").toString();
			
			// 作废商品
			DbUp.upTable("sc_event_item_product").dataUpdate(new MDataMap("flag_enable","0","item_code",itemCode), "flag_enable", "item_code");
			// 清理缓存
			loadSkuInfo.deleteInfoByCode(skuCode);
			loadSkuItem.deleteInfoByCode(itemCode);
			
			// 兼容折扣没有SKU的情况
			if(StringUtils.isBlank(skuCode)) {
				PlusHelperNotice.onChangeProductInfo(productCode);
			}
			
			// 记录作废的商品明细
			time = FormatHelper.upDateTime();
			DbUp.upTable("sc_event_tv_disabled").dataInsert(new MDataMap(
					"event_code", eventCode,
					"item_code", itemCode,
					"product_code", productCode,
					"sku_code", skuCode,
					"flag", "0",
					"create_time", time,
					"update_time", time
					));
		}
	}
	
	private List<String> getTvProductList() {
		// 查询第二天的节目品
		String today = FormatHelper.upDateTime(DateUtils.addDays(new Date(), 1),"yyyy-MM-dd");
		String sql = "SELECT DISTINCT good_id FROM productcenter.pc_tv WHERE form_fr_date LIKE '"+today+"%' and so_id = '1000001'";
		List<Map<String, Object>> tvList = DbUp.upTable("pc_tv").dataSqlList(sql, new MDataMap());
		List<String> tvProductList = new ArrayList<String>();
		for(Map<String, Object> m : tvList) {
			tvProductList.add((String)m.get("good_id"));
		}
		return tvProductList;
	}
}
