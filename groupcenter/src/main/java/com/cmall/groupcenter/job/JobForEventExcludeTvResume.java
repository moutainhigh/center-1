package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadSkuInfo;
import com.srnpr.xmassystem.load.LoadSkuItem;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每天定时恢复直播品活动
 */
public class JobForEventExcludeTvResume extends RootJob {
	
	LoadSkuInfo loadSkuInfo = new LoadSkuInfo();
	LoadSkuItem loadSkuItem = new LoadSkuItem();
	
	public void doExecute(JobExecutionContext context) {
		resume();
	}

	/**
	 * 还原以前作废的活动商品
	 */
	private void resume() {
		// 查询2周内作废的商品，不包含今天
		List<MDataMap> mapList = DbUp.upTable("sc_event_tv_disabled").queryAll("", "", "flag = 0 AND create_time > DATE_SUB(NOW(),INTERVAL 14 DAY) AND create_time < DATE(NOW())", new MDataMap());
		if(mapList.isEmpty()) {
			return;
		}
		
		// 查询当天的直播品，只查询1台
		List<String> tvProductList = getTvProductList();
		
		String sql,skuCode,productCode,eventCode,itemCode;
		MDataMap eventInfo;
		String now = FormatHelper.upDateTime();
		for(MDataMap map : mapList) {
			eventCode = map.get("event_code").toString();
			itemCode = map.get("item_code").toString();
			productCode = map.get("product_code").toString();
			skuCode = map.get("sku_code").toString();
	
			eventInfo = DbUp.upTable("sc_event_info").one("event_code", eventCode);
			// 如果活动已经结束则不还原
			if(now.compareTo(eventInfo.get("end_time")) > 0) {
				map.put("flag", "2");
				map.put("update_time", FormatHelper.upDateTime());
				DbUp.upTable("sc_event_tv_disabled").dataUpdate(map, "flag,update_time", "zid");
				continue;
			}
			
			// 当天有直播品的时候也不还原
			if(tvProductList.contains(productCode)) {
				continue;
			}
			
			// 活动未结束则查询是否跟其他活动冲突
			sql = "SELECT p.event_code FROM systemcenter.sc_event_info e,systemcenter.sc_event_item_product p"
					+ " WHERE e.event_status = '4497472700020002' AND p.event_code = e.event_code AND p.flag_enable = 1"
					+ " AND e.event_type_code IN('4497472600010001','4497472600010002','4497472600010005','4497472600010024','4497472600010030')"
					+ " AND e.event_code != '" + eventCode + "'"
					+ " AND p.product_code IN(" + productCode + ") "
					+ " AND (e.begin_time BETWEEN :begin_time AND :end_time || e.end_time BETWEEN :begin_time AND :end_time || (e.begin_time < :begin_time AND e.end_time > :end_time))";
			
			List<Map<String, Object>> eventList = DbUp.upTable("sc_event_item_product").dataSqlList(sql, eventInfo);
			if(!eventList.isEmpty()) {
				// 如果存在则说明有冲突的活动，暂时不进行还原
				continue;
			}
			
			// 还原商品
			DbUp.upTable("sc_event_item_product").dataUpdate(new MDataMap("flag_enable","1","item_code",itemCode), "flag_enable", "item_code");

			// 清理缓存
			loadSkuInfo.deleteInfoByCode(skuCode);
			loadSkuItem.deleteInfoByCode(itemCode);
			
			// 兼容折扣没有SKU的情况
			if(StringUtils.isBlank(skuCode)) {
				PlusHelperNotice.onChangeProductInfo(productCode);
			}
			
			map.put("flag", "1"); // 已还原
			map.put("update_time", FormatHelper.upDateTime());
			DbUp.upTable("sc_event_tv_disabled").dataUpdate(map, "flag,update_time", "zid");
		}
	}
	
	private List<String> getTvProductList() {
		String today = FormatHelper.upDateTime("yyyy-MM-dd");
		String sql = "SELECT DISTINCT good_id FROM productcenter.pc_tv WHERE form_fr_date LIKE '"+today+"%' and so_id = '1000001'";
		List<Map<String, Object>> tvList = DbUp.upTable("pc_tv").dataSqlList(sql, new MDataMap());
		List<String> tvProductList = new ArrayList<String>();
		for(Map<String, Object> m : tvList) {
			tvProductList.add((String)m.get("good_id"));
		}
		return tvProductList;
	}
}
