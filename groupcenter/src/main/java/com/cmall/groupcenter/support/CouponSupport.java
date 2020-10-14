package com.cmall.groupcenter.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.support.CouponSupport.CouponMessage.CouponSum;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 优惠券支持类
 * @author cc
 *
 */
public class CouponSupport extends BaseClass implements IBaseInstance  {
	
	public final static CouponSupport INSTANCE = new CouponSupport();
	
	public CouponMessage upSendListByNoticeFlag(){
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.inAllValues("status", "1", "notify_flag", "0", "notify_num","3");
		List<MDataMap> listMaps = DbUp.upTable("oc_order_ld_coupon_task").queryAll("zid,phone,cust_id,product_count,coupon_type","","status=:status and notify_flag=:notify_flag and notify_num < :notify_num",mWhereMap);
		CouponMessage couponMessage = new CouponMessage();		
		if(listMaps != null && listMaps.size() > 0) {
			for(MDataMap map : listMaps) {
				sumCoupon(couponMessage.getSendMessage(), map);
				/**
				 * 获取所有汇总数据的zid
				 */
				couponMessage.zids.add(map.get("zid"));
			}
		}
		
		return couponMessage;
	}
	
	public static void main(String[] args) {
		CouponSupport support = new CouponSupport();
		List<MDataMap> listMaps = new ArrayList<>();
		MDataMap map1 = new MDataMap();
		map1.put("zid", "1");
		map1.put("phone", "13811445203");
		map1.put("cust_id", "13");
		map1.put("product_count", "1");
		map1.put("coupon_type", "1");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "2");
		map1.put("phone", "13811445203");
		map1.put("cust_id", "13");
		map1.put("product_count", "1");
		map1.put("coupon_type", "2");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "3");
		map1.put("phone", "13811445203");
		map1.put("cust_id", "13");
		map1.put("product_count", "2");
		map1.put("coupon_type", "3");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "4");
		map1.put("phone", "18811445203");
		map1.put("cust_id", "18");
		map1.put("product_count", "2");
		map1.put("coupon_type", "1");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "5");
		map1.put("phone", "18811445203");
		map1.put("cust_id", "18");
		map1.put("product_count", "3");
		map1.put("coupon_type", "2");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "6");
		map1.put("phone", "17811445203");
		map1.put("cust_id", "17");
		map1.put("product_count", "2");
		map1.put("coupon_type", "2");
		
		listMaps.add(map1);
		
		map1 = new MDataMap();
		map1.put("zid", "7");
		map1.put("phone", "13811445203");
		map1.put("cust_id", "13");
		map1.put("product_count", "4");
		map1.put("coupon_type", "1");
		
		listMaps.add(map1);
		
		CouponMessage couponMessage = new CouponMessage();		
		if(listMaps != null && listMaps.size() > 0) {
			for(MDataMap map : listMaps) {
				support.sumCoupon(couponMessage.getSendMessage(), map);
				/**
				 * 获取所有汇总数据的zid
				 */
				couponMessage.zids.add(map.get("zid"));
			}
		}
		String zids = StringUtils.join(couponMessage.getZids(),	WebConst.CONST_SPLIT_COMMA);
		System.out.println("zids:" + zids);
		for(CouponSum sum : couponMessage.getSendMessage()) {
			System.out.println("手机：" + sum.getPhone() + "，家有客代号：" + sum.getCustId() + "，黄金五折券" + sum.getGoldCount() + "张，铂金五折券" + sum.getPlatCount() + "张，钻石五折券" + sum.getDiamondCount() + "张");
		}
		/**
		 * 整理短信内容
		 */
		List<MDataMap> contentList = new ArrayList<MDataMap>();
		for(CouponSum cm : couponMessage.getSendMessage()) {
			MDataMap contentMap = new MDataMap();
			contentMap.put("phone", cm.getPhone());
			contentMap.put("custId", cm.getCustId());
			StringBuffer content = new StringBuffer();
			if(cm.getGoldCount() > 0) {
				content.append("黄金五折券" + cm.getGoldCount() + "张，");
			}
			if(cm.getPlatCount() > 0) {
				content.append("铂金五折券" + cm.getPlatCount() + "张，");
			}
			if(cm.getDiamondCount() > 0) {
				content.append("钻石五折券" + cm.getDiamondCount() + "张，");
			}
			contentMap.put("content", content.toString().substring(0, content.toString().length() - 1));
			contentList.add(contentMap);
		}
		for(MDataMap map : contentList) {
			System.out.println("{phone:" + map.get("phone") + ",custId:" + map.get("custId") + ",content:" + map.get("content") + "}");
		}
	}
	
	/**
	 * 统计优惠券
	 * @param sendMessage
	 * @param map
	 */
	public void sumCoupon(List<CouponSum> couponSum, MDataMap map) {
		String phone = map.get("phone");
		String custId = map.get("cust_id");
		/**
		 * 判断是否存在，如果存在，则加折扣券，不存在，则新加一条CouponMessage
		 */
		boolean isExist = false;
		for(CouponSum cm : couponSum) {
			if(phone.equals(cm.phone) && custId.equals(cm.custId)) {
				isExist = true;
				if(map.get("coupon_type").equals("1")) {
					cm.goldCount += Integer.parseInt(map.get("product_count").toString());
				} else if(map.get("coupon_type").equals("2")) {
					cm.platCount += Integer.parseInt(map.get("product_count").toString());
				} else {
					cm.diamondCount += Integer.parseInt(map.get("product_count").toString());
				}
				break;
			}
		}
		if(!isExist) {
			CouponSum cm = new CouponSum();
			cm.phone = phone;
			cm.custId = custId;
			if(map.get("coupon_type").equals("1")) {
				cm.goldCount = Integer.parseInt(map.get("product_count").toString());
			} else if(map.get("coupon_type").equals("2")) {
				cm.platCount = Integer.parseInt(map.get("product_count").toString());
			} else {
				cm.diamondCount = Integer.parseInt(map.get("product_count").toString());
			}
			couponSum.add(cm);
		}
	}
	
	
	public static class CouponMessage {
		Set<String> zids=new HashSet<String>();
		List<CouponSum> sendMessage = new ArrayList<CouponSum>();
		
		public static class CouponSum {
			private String phone;
			private String custId;
			private int goldCount;
			private int platCount;
			private int diamondCount;
			public String getPhone() {
				return phone;
			}
			public void setPhone(String phone) {
				this.phone = phone;
			}
			public String getCustId() {
				return custId;
			}
			public void setCustId(String custId) {
				this.custId = custId;
			}
			public int getGoldCount() {
				return goldCount;
			}
			public void setGoldCount(int goldCount) {
				this.goldCount = goldCount;
			}
			public int getPlatCount() {
				return platCount;
			}
			public void setPlatCount(int platCount) {
				this.platCount = platCount;
			}
			public int getDiamondCount() {
				return diamondCount;
			}
			public void setDiamondCount(int diamondCount) {
				this.diamondCount = diamondCount;
			}
		}

		public Set<String> getZids() {
			return zids;
		}

		public void setZids(Set<String> zids) {
			this.zids = zids;
		}

		public List<CouponSum> getSendMessage() {
			return sendMessage;
		}

		public void setSendMessage(List<CouponSum> sendMessage) {
			this.sendMessage = sendMessage;
		}
		
		
	}
}