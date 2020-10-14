package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.xmasorder.model.ShoppingCartCache;
import com.srnpr.xmasorder.model.ShoppingCartCacheInfo;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.GsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;
import com.srnpr.zapweb.websupport.PushSupport;

/**
 * 购物车用户提醒
 */
public class JobForShopcartAddNotify extends RootJobForExclusiveLock {

	public void doExecute(JobExecutionContext context) {
		String staticKey = getClass().getName();
		String lastZid = getStaticValue(staticKey, "0");
		Calendar now = Calendar.getInstance();;
		// 2小时内
		String timeHour2 = DateFormatUtils.format(DateUtils.addHours(now.getTime(), -2), "yyyy-MM-dd HH:mm:ss");
		// 1小时内
		String timeHour1 = DateFormatUtils.format(DateUtils.addHours(now.getTime(), -1), "yyyy-MM-dd HH:mm:ss");
		
		// 查询添加购物车数据在1小时后2小时内的数据
		List<MDataMap> list = DbUp.upTable("lc_shoptcart_add_log").queryAll("zid,member_code,product_code", "zid", "create_time >= :timeHour2 AND create_time <= :timeHour1 AND zid > :lastZid", 
				new MDataMap("timeHour2", timeHour2, "timeHour1", timeHour1, "lastZid", lastZid));
		
		// 更新执行的起始zid
		if(!list.isEmpty()) {
			lastZid = list.get(0).get("zid");
			updateStaticValue(staticKey, lastZid);
		}

		// 11点——第二天7点不发送短信和push
		int hour = now.get(Calendar.HOUR_OF_DAY);
		if(hour < 7 || hour >= 23) {
			return;
		}
		
		// 根据用户编号分组商品
		Map<String,List<String>> memberGroupMap = new HashMap<String, List<String>>();
		List<String> productList;
		String productCode;
		for(MDataMap map : list) {
			productList = memberGroupMap.get(map.get("member_code"));
			if(productList == null) {
				productList = new ArrayList<String>();
				memberGroupMap.put(map.get("member_code"), productList);
			}
			
			productCode = map.get("product_code");
			if(!productList.contains(productCode)) {
				productList.add(productCode);
			}
		}
		
		// 发送通知邮件
		Set<Entry<String, List<String>>> entrySet = memberGroupMap.entrySet();;
		for(Entry<String, List<String>> entry : entrySet) {
			notify(entry.getKey(), entry.getValue());
		}
	}
	
	private void notify(String memberCode, List<String> productList) {
		// 查询是否在12小时内有发送过通知
		int v = DbUp.upTable("lc_shoptcart_add_notify").dataCount("member_code = :member_code AND update_time > DATE_SUB(NOW(),INTERVAL 12 HOUR)", new MDataMap("member_code", memberCode));
		if(v > 0) {
			return;
		}
		
		// 检查是否有商品需要发送通知
		if(!checkShopcartProduct(memberCode, productList)) {
			return;
		}
		
		MDataMap loginInfo = DbUp.upTable("mc_login_info").one("member_code", memberCode);
		if(loginInfo == null) {
			return;
		}
		
		// 发送短信
		SmsUtil sms = new SmsUtil();
		boolean smsRes = sms.sendSmsForYX(loginInfo.get("login_name"), "我躺在购物车里1个小时了，快点把我带走吧！打开微信，搜索“惠家有特卖”，进入购物车，快去找到我吧！");
		
		// 发送push
		PushSupport.PushInfo pushInfo = new PushSupport.PushInfo();
		pushInfo.setPhone(loginInfo.get("login_name"));
		pushInfo.setMsgContent("我躺在购物车里1个小时了，快点把我带走吧！");
		pushInfo.setToPage("10");
		
		PushSupport.Xattrs attr = new PushSupport.Xattrs();
		attr.setSource("449748720001");
		pushInfo.setXattrs(attr);
		
		RootResult pushRes = new PushSupport().push(pushInfo);
		
		// 任意一个发送成功则认为给用户发送过通知
		if(smsRes || pushRes.getResultCode() == 1) {
			updateNotifyTime(memberCode);
		}
	}
	
	/**
	 * 检查购物车商品是否已经被购买或者下架
	 * @param memberCode
	 * @param productList
	 * @return
	 */
	private boolean checkShopcartProduct(String memberCode, List<String> productList) {
		String sSql = "SELECT 1 FROM oc_orderinfo o, oc_orderdetail d WHERE o.order_code = d.order_code AND o.create_time > DATE_SUB(NOW(),INTERVAL 2 HOUR) AND d.product_code = :productCode AND o.buyer_code = :memberCode";
		String json = XmasKv.upFactory(EKvSchema.ShopCart).get(memberCode);
		
		ShoppingCartCacheInfo info = new GsonHelper().fromJson(json, new ShoppingCartCacheInfo());
		List<ShoppingCartCache> cacheList = info.getCaches();
		List<String> cList = new ArrayList<String>();
		
		for(ShoppingCartCache cache : cacheList) {
			if(cache.getSku_num() > 0) {
				cList.add(cache.getProduct_code());
			}
		}
		
		// 是否应该发送通知
		// 用户将商品加入购物车1小时以后，2小时内，未购买该商品，则发送push提醒，短信提醒
		boolean notifyFlag = false;
		for(String productCode : productList) {
			// 如果购物车已经不存在这个商品了则忽略
			if(!cList.contains(productCode)) {
				continue;
			}
			
			// 如果2小时内购买了这个商品则忽略
			if(DbUp.upTable("oc_orderinfo").dataSqlOne(sSql, new MDataMap("memberCode", memberCode, "productCode", productCode)) != null) {
				continue;
			}
			
			notifyFlag = true;
			break;
		}
		
		return notifyFlag;
	}
	
	private void updateNotifyTime(String memberCode) {
		MDataMap map = new MDataMap();
		map.put("member_code", memberCode);
		map.put("update_time", FormatHelper.upDateTime());
		if(DbUp.upTable("lc_shoptcart_add_notify").count("member_code", memberCode) == 0) {
			DbUp.upTable("lc_shoptcart_add_notify").dataInsert(map);
		} else {
			DbUp.upTable("lc_shoptcart_add_notify").dataUpdate(map, "update_time", "member_code");
		}
	}
	
}
